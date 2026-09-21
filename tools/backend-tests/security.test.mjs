import { PGlite } from '@electric-sql/pglite';
import { readFileSync, readdirSync, mkdirSync, writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import assert from 'node:assert/strict';

const root = fileURLToPath(new URL('../../', import.meta.url));
const db = new PGlite();
function saveFixture(name,snapshot){
  if(process.env.UPDATE_SERVER_FIXTURES!=='1')return;
  const path=root+'app/src/test/resources/server/';mkdirSync(path,{recursive:true});
  writeFileSync(path+name+'.json',JSON.stringify(snapshot,null,2)+'\n');
}

let checks = 0;
async function check(name, body) {
  await body(); checks++; console.log(`PASS ${name}`);
}
async function user(id) {
  await db.exec('reset role');
  await db.query("select set_config('request.jwt.claim.sub',$1,false)", [id]);
  await db.exec('set role authenticated');
}
async function admin() { await db.exec('reset role'); }
async function denied(sql, params = []) {
  await assert.rejects(db.query(sql, params), /permission denied|unauthorized|not_member|host_required|invite_required|unavailable|invalid_|insufficient|already_|idempotency|loadout|room_full|account_restricted/);
}
const ids = Array.from({length: 43}, (_, i) => `00000000-0000-0000-0000-${String(i+1).padStart(12,'0')}`);
try {
  await db.exec(`
    create role anon nologin;
    create role authenticated nologin;
    create role service_role nologin bypassrls;
    create schema storage;
    create table storage.buckets(id text primary key,name text,public boolean,file_size_limit bigint,allowed_mime_types text[]);
    create table storage.objects(id uuid primary key default gen_random_uuid(),bucket_id text references storage.buckets(id),name text,unique(bucket_id,name));
    alter table storage.objects enable row level security;
    grant usage on schema storage to authenticated;
    grant select,insert,update,delete on storage.objects to authenticated;
    create schema auth;
    create table auth.users(id uuid primary key, email text, raw_user_meta_data jsonb not null default '{}');
    create table auth.identities(user_id uuid not null references auth.users(id),provider text not null);
    create function auth.uid() returns uuid language sql stable as
      $$ select nullif(current_setting('request.jwt.claim.sub',true),'')::uuid $$;
    grant usage on schema auth to authenticated,anon;
    grant execute on function auth.uid() to authenticated,anon;
    insert into auth.users(id,raw_user_meta_data) values
      ('eeeeeeee-0000-0000-0000-000000000001','{"username":"Legacy"}'),
      ('eeeeeeee-0000-0000-0000-000000000002','{"username":"Legacy"}'),
      ('eeeeeeee-0000-0000-0000-000000000003','{"username":"!invalid"}');
  `);
  for (const name of readdirSync(`${root}/supabase/migrations`).filter(x => x.endsWith('.sql')).sort()) {
    await db.exec(readFileSync(`${root}/supabase/migrations/${name}`, 'utf8'));
    console.log(`APPLIED ${name}`);
  }
  for (let i=0;i<ids.length;i++) {
    await db.query("insert into auth.users(id,email,raw_user_meta_data) values($1,$2,$3)",
      [ids[i],`test${i}@example.invalid`,JSON.stringify({username:`Tester_${i}`})]);
  }
  await check('Existing Auth users are backfilled with unique valid profiles',async()=>{
    const rows=(await db.query("select username from public.profiles where id::text like 'eeeeeeee-%'")).rows;
    assert.equal(rows.length,3);assert.equal(new Set(rows.map(r=>r.username.toLowerCase())).size,3);
    assert(rows.every(r=>/^[A-Za-z0-9_.]{3,24}$/.test(r.username)));
  });
  await check('Empty launch loadouts are valid but arbitrary, singleton and null boosts are rejected',async()=>{
    await user(ids[42]);await db.query("select public.set_loadout('{}'::text[])");
    await denied("select public.set_loadout(array['fake'])");await denied("select public.set_loadout(array['fake','other'])");await denied('select public.set_loadout(null)');
    const room=(await db.query("select public.create_room('duel') as id")).rows[0].id;
    await db.query('select public.set_ready($1,true)',[room]);await db.query('select public.leave_room($1)',[room]);await admin();
  });
  await check('Every public table enables RLS', async () => {
    const result=await db.query("select relname from pg_class c join pg_namespace n on n.oid=c.relnamespace where n.nspname='public' and c.relkind='r' and not c.relrowsecurity");
    assert.equal(result.rows.length,0);
  });
  await check('Anonymous role has no client operations', async () => {
    await db.exec('set role anon');
    await denied('select * from public.profiles');
    await denied("select public.create_room('duel')");
  });
  await user(ids[0]);
  await check('Only own profile visible before friendship', async () => {
    const r=await db.query('select id,player_number from public.profiles');
    assert.equal(r.rows.length,1); assert.equal(r.rows[0].id,ids[0]);
    assert.match(String(r.rows[0].player_number),/^\d{8}$/);
  });
  await check('Clients cannot directly write any public table', async () => {
    await admin();
    const tables=await db.query("select tablename from pg_tables where schemaname='public'");
    await user(ids[0]);
    for(const {tablename} of tables.rows) {
      const r=await db.query("select has_table_privilege(current_user,$1,'INSERT,UPDATE,DELETE') as allowed",[`public.${tablename}`]);
      assert.equal(r.rows[0].allowed,false,tablename);
    }
  });
  await check('Answer keys and submissions are inaccessible', async () => {
    await denied('select * from private.content_answers');
    await denied('select * from private.submissions');
    await denied('select private.assign_member(gen_random_uuid(),gen_random_uuid())');
  });
  await check('Friend request requires recipient acceptance', async () => {
    await db.query("select public.friend_action($1,'request')",[ids[1]]);
    await db.query("select public.friend_action($1,'accept')",[ids[1]]);
    assert.equal((await db.query('select status from public.friendships')).rows[0].status,'pending');
    await user(ids[1]); await db.query("select public.friend_action($1,'accept')",[ids[0]]);
    assert.equal((await db.query('select * from public.profiles')).rows.length,2);
  });
  await check('Block removes friendship, hides target and exact searches', async () => {
    await user(ids[0]); await db.query('select public.set_block($1,true)',[ids[1]]);
    assert.equal((await db.query('select * from public.friendships')).rows.length,0);
    await user(ids[1]);
    assert.equal((await db.query("select * from public.search_players('Tester_0')")).rows.length,0);
    assert.equal((await db.query('select * from public.blocks')).rows.length,0);
    await denied("select public.friend_action($1,'request')",[ids[0]]);
    await user(ids[0]); await db.query('select public.set_block($1,false)',[ids[1]]);
  });
  let room;
  await check('Private default, invite enforcement and host-only matchmaking', async () => {
    await user(ids[0]); room=(await db.query("select public.create_room('duo') as id")).rows[0].id;
    assert.equal((await db.query('select matchmaking from public.rooms')).rows[0].matchmaking,false);
    await user(ids[1]); await denied('select public.join_room($1)',[room]);
    await denied('select public.set_matchmaking($1,true)',[room]);
    await user(ids[0]); await db.query('select public.set_matchmaking($1,true)',[room]);
  });
  await check('Forty-player Duo room has twenty teams and rejects overflow', async () => {
    for(let i=1;i<40;i++){ await user(ids[i]); await db.query('select public.join_room($1)',[room]); }
    assert.equal((await db.query('select * from public.room_members')).rows.length,40);
    assert.equal((await db.query('select * from public.teams')).rows.length,20);
    await user(ids[40]); await denied('select public.join_room($1)',[room]);
    assert.equal((await db.query('select * from public.rooms')).rows.length,0);
  });
  await check('Lobby host migrates deterministically', async () => {
    await user(ids[0]); await db.query('select public.leave_room($1)',[room]);
    await user(ids[1]); assert.equal((await db.query('select host_id from public.rooms where id=$1',[room])).rows[0].host_id,ids[1]);
  });
  await admin();
  await db.query("insert into public.cosmetics(id,kind,label_key,approved) values('test_avatar','avatar','test',true),('test_avatar2','avatar','test2',true)");
  await db.query("insert into public.store_items(id,cosmetic_id,currency,price,enabled) values('test_item','test_avatar','gold',80,true),('test_item2','test_avatar2','gold',80,true)");
  await db.query("insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key) values($1,'gold',100,'test_fixture','fixture','fixture')",[ids[0]]);
  await user(ids[0]);
  await check('Atomic purchase is idempotent and cannot overdraw', async () => {
    const key='11111111-1111-1111-1111-111111111111';
    const first=(await db.query("select public.purchase_item('test_item',$1) as id",[key])).rows[0].id;
    const second=(await db.query("select public.purchase_item('test_item',$1) as id",[key])).rows[0].id;
    assert.equal(first,second);
    assert.equal((await db.query("select balance from public.balances() where currency='gold'")).rows[0].balance,20);
    await denied("select public.purchase_item('test_item2',$1)",[key]);
    await denied("select public.purchase_item('test_item2',gen_random_uuid())");
    await denied("insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key) values($1,'flames',1,'match_win','fake','fake')",[ids[0]]);
  });
  await check('Enforcement prevents user RPC actions', async () => {
    await admin(); await db.query('insert into private.enforcement(user_id,disabled) values($1,true)',[ids[0]]);
    await user(ids[0]); await denied("select public.update_profile('ChangedName')");
  });

  await admin();
  await db.exec("delete from private.enforcement");
  await db.exec("insert into public.cosmetics(id,kind,label_key,approved) values('test_boost_a','boost','test',true),('test_boost_b','boost','test',true)");
  for(const id of [ids[40],ids[41]]) {
    await admin();
    await db.query("insert into public.inventory(user_id,cosmetic_id) values($1,'test_boost_a'),($1,'test_boost_b')",[id]);
    await user(id); await db.query("select public.set_loadout(array['test_boost_a','test_boost_b'])");
  }
  await admin();
  for(let i=0;i<20;i++) {
    const question=i<15; const id=`test_content_${i}`;
    const options=Array.from({length:question?5:10},(_,j)=>({id:`o${j}`,label:`Test option ${j}`}));
    await db.query("insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) values($1,$2,'en',1,1,'APPROVED',$3)",[id,question?'question_round':'image_guess',JSON.stringify({options})]);
    await db.query("insert into private.content_answers(content_id,answer) values($1,$2)",[id,JSON.stringify(question?{correct_option_id:'o0'}:{scoring_policy:'correct_only',choices:options.map((o,j)=>({...o,points:j===0?10:0,correct:j===0}))})]);
  }
  let duel,match,round;
  await check('Start creates server schedule and is idempotent', async()=>{
    await user(ids[40]); duel=(await db.query("select public.create_room('duel') as id")).rows[0].id;
    await db.query('select public.set_matchmaking($1,true)',[duel]);
    await db.query('select public.set_ready($1,true)',[duel]);
    await user(ids[41]); await db.query('select public.join_room($1)',[duel]);
    await db.query('select public.set_ready($1,true)',[duel]);
    await denied('select public.start_match($1)',[duel]);
    await user(ids[40]); match=(await db.query('select public.start_match($1) as id',[duel])).rows[0].id;
    assert.equal((await db.query('select public.start_match($1) as id',[duel])).rows[0].id,match);
    assert.equal((await db.query('select id from public.match_rounds where match_id=$1',[match])).rows.length,0);
    await admin(); const rounds=(await db.query('select * from public.match_rounds where match_id=$1 order by ordinal',[match])).rows;
    assert.equal(rounds.length,20); round=rounds[0].id;
    assert.equal(new Date(rounds[0].answer_opens_at)-new Date(rounds[0].starts_at),10000);
    assert.equal(new Date(rounds[0].deadline)-new Date(rounds[0].answer_opens_at),20000);
    assert.equal(new Date(rounds[19].deadline)-new Date(rounds[19].starts_at),30000);
  });
  const receipt='22222222-2222-2222-2222-222222222222';
  await check('Question-only window rejects answers; caller cannot submit as opponent', async()=>{
    await user(ids[40]);
    await assert.rejects(db.query("select public.submit_answer($1,$2,$3,'{\"option_id\":\"o0\"}')",[match,round,receipt]),/round_not_accepting/);
    await user(ids[42]); await denied('select public.match_snapshot($1)',[match]);
    await denied("select public.submit_answer($1,$2,$3,'{\"option_id\":\"o0\"}')",[match,round,receipt]);
  });
  await check('Timed snapshot conceals options, bank and first-correct fields',async()=>{
    await admin();
    await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '1 second',answer_opens_at=clock_timestamp()+interval '9 seconds',deadline=clock_timestamp()+interval '29 seconds' where id=$1",[round]);
    await user(ids[40]);
    assert.equal((await db.query('select id from public.content_items')).rows.length,0);
    await denied('select first_correct from public.match_rounds where id=$1',[round]);
    const snapshot=(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s;
    const current=snapshot.rounds.find(r=>r.id===round);
    assert(current);assert.equal(current.content.options,undefined);assert.equal(current.first_correct,undefined);
    assert.equal(current.submission,null);
    const before=(await db.query('select xmin::text as revision from public.matches where id=$1',[match])).rows[0].revision;
    await db.query('select public.match_snapshot($1)',[match]);
    const after=(await db.query('select xmin::text as revision from public.matches where id=$1',[match])).rows[0].revision;
    assert.equal(after,before,'unchanged snapshots must not write another match version');
  });
  await check('First valid correct receives one; replay does not duplicate score', async()=>{
    await admin(); await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '11 seconds',answer_opens_at=clock_timestamp()-interval '1 second',deadline=clock_timestamp()+interval '30 seconds' where id=$1",[round]);
    await user(ids[40]); const first=(await db.query("select public.submit_answer($1,$2,$3,'{\"option_id\":\"o0\"}') as r",[match,round,receipt])).rows[0].r;
    const replay=(await db.query("select public.submit_answer($1,$2,$3,'{\"option_id\":\"o0\"}') as r",[match,round,receipt])).rows[0].r;
    assert.deepEqual(first,replay);
    await denied("select public.submit_answer($1,$2,$3,'{\"option_id\":\"o1\"}')",[match,round,receipt]);
    await user(ids[41]); await db.query("select public.submit_answer($1,$2,gen_random_uuid(),'{\"option_id\":\"o0\"}')",[match,round]);
    assert.equal((await db.query('select * from public.score_events where match_id=$1',[match])).rows.length,0);
    await admin(); await db.query("update public.match_rounds set deadline=clock_timestamp()-interval '0.5 seconds' where id=$1",[round]);
    await user(ids[40]); await db.query('select public.match_snapshot($1)',[match]);
    const scores=(await db.query('select user_id,score from public.match_participants where match_id=$1',[match])).rows;
    assert.equal(scores.find(x=>x.user_id===ids[40]).score,1); assert.equal(scores.find(x=>x.user_id===ids[41]).score,0);
  });

  await check('Typed answers accept explicit translations without leaking correctness',async()=>{
    await admin();const next=(await db.query('select id,content_id from public.match_rounds where match_id=$1 and ordinal=1',[match])).rows[0];
    await db.query("update private.content_answers set answer=answer||$2::jsonb where content_id=$1",[next.content_id,JSON.stringify({accepted_answers:['heart','cœur','قلب']})]);
    await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '11 seconds',answer_opens_at=clock_timestamp()-interval '1 second',deadline=clock_timestamp()+interval '30 seconds' where id=$1",[next.id]);
    await user(ids[40]);await denied('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[match,next.id,JSON.stringify({answer:'قلب',points:99})]);
    const result=(await db.query('select public.submit_answer($1,$2,gen_random_uuid(),$3) as r',[match,next.id,JSON.stringify({answer:'قَلْب'})])).rows[0].r;
    assert.deepEqual(Object.keys(result).sort(),['accepted','receipt']);
    await user(ids[41]);await db.query('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[match,next.id,JSON.stringify({answer:'liver'})]);
    await admin();const scores=(await db.query('select user_id,awarded_points from private.submissions where round_id=$1',[next.id])).rows;
    assert.equal(scores.find(s=>s.user_id===ids[40]).awarded_points,1);assert.equal(scores.find(s=>s.user_id===ids[41]).awarded_points,0);
    assert.equal((await db.query("select private.normalize_answer('٩٦') as n")).rows[0].n,'96');
    assert.equal((await db.query("select private.normalize_answer('école') as n")).rows[0].n,'ecole');
    await db.query("update public.match_rounds set deadline=clock_timestamp()-interval '0.5 seconds' where id=$1",[next.id]);
    await user(ids[40]);await db.query('select public.match_snapshot($1)',[match]);
  });

  await check('Image Guess accepts exactly four distinct choices and conceals weights until deadline',async()=>{
    await admin();
    const imageRound=(await db.query('select id from public.match_rounds where match_id=$1 and ordinal=15',[match])).rows[0].id;
    await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '2 seconds',answer_opens_at=clock_timestamp()-interval '1 second',deadline=clock_timestamp()+interval '20 seconds' where id=$1",[imageRound]);
    await user(ids[40]);
    await denied('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[match,imageRound,JSON.stringify({choice_ids:['o0','o1','o2']})]);
    await denied('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[match,imageRound,JSON.stringify({choice_ids:['o0','o1','o2','o2']})]);
    await denied('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[match,imageRound,JSON.stringify({choice_ids:['o0','o1','o2','unknown']})]);
    const before=(await db.query('select score from public.match_participants where match_id=$1 and user_id=$2',[match,ids[40]])).rows[0].score;
    const result=(await db.query('select public.submit_answer($1,$2,gen_random_uuid(),$3) as r',[match,imageRound,JSON.stringify({choice_ids:['o0','o1','o2','o3']})])).rows[0].r;
    assert.deepEqual(Object.keys(result).sort(),['accepted','receipt']);
    assert.equal((await db.query('select score from public.match_participants where match_id=$1 and user_id=$2',[match,ids[40]])).rows[0].score,before);
    assert.equal((await db.query('select reveal from public.match_rounds where id=$1',[imageRound])).rows[0].reveal,null);
    await admin();await db.query("update public.match_rounds set deadline=clock_timestamp()-interval '0.5 seconds' where id=$1",[imageRound]);
    await user(ids[40]);await db.query('select public.match_snapshot($1)',[match]);
    assert.equal((await db.query('select score from public.match_participants where match_id=$1 and user_id=$2',[match,ids[40]])).rows[0].score,before+10);
  });

  await check('Deadline recovery finalizes once and awards exactly one Flame', async()=>{
    await admin();
    await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '31 seconds',answer_opens_at=clock_timestamp()-interval '21 seconds',deadline=clock_timestamp()-interval '1 second' where match_id=$1 and status<>'results'",[match]);
    await user(ids[40]); await db.query('select public.match_snapshot($1)',[match]); await db.query('select public.match_snapshot($1)',[match]);
    assert.equal((await db.query("select balance from public.balances() where currency='flames'")).rows[0].balance,1);
    assert.equal((await db.query('select * from public.match_results where match_id=$1',[match])).rows.length,2);
    await denied("update public.match_results set score=999 where match_id=$1",[match]);
    const replay=(await db.query("select public.submit_answer($1,$2,$3,'{\"option_id\":\"o0\"}') as r",[match,round,receipt])).rows[0].r;
    assert.equal(replay.accepted,true);
  });


  await check('Profile snapshot contains own ledger-derived balances and actual results',async()=>{
    await user(ids[40]);const snapshot=(await db.query('select public.profile_snapshot() as p')).rows[0].p;
    assert.equal(snapshot.profile.id,ids[40]);assert.equal(snapshot.played,1);assert.equal(snapshot.wins,1);
    assert.equal(snapshot.currencies.find(x=>x.currency==='flames').balance,1);
    await denied("select public.equip_cosmetic('test_avatar')");
  });
  await check('Leaderboards use finalized results and retain own rank outside the page',async()=>{
    await user(ids[41]);
    let board=(await db.query("select public.leaderboard('duel','win_rate','weekly',false,1) as b")).rows[0].b;
    assert.equal(board.rows.length,1);assert.equal(board.rows[0].user_id,ids[40]);assert.equal(board.rows[0].value,100);
    assert.equal(board.own.user_id,ids[41]);assert.equal(board.own.rank,2);assert.equal(board.own.value,0);
    board=(await db.query("select public.leaderboard('duel','win_rate','weekly',true,1) as b")).rows[0].b;
    assert.equal(board.rows.length,1);assert.equal(board.rows[0].user_id,ids[41]);
    await denied("select public.leaderboard('duel','highest_score')");
    await denied("select public.leaderboard('offline','highest_score')");
    await admin();await db.query("update public.matches set completed_at=clock_timestamp()-interval '8 days' where id=$1",[match]);
    await user(ids[41]);
    assert.equal((await db.query("select public.leaderboard('duel','win_rate','weekly') as b")).rows[0].b.own,null);
    assert.equal((await db.query("select public.leaderboard('duel','win_rate','all_time') as b")).rows[0].b.own.rank,2);
  });
  await check('Squad draft draws persist and choose only eligible teammates',async()=>{
    await admin();
    const roomId=(await db.query("insert into public.rooms(host_id,mode) values($1,'squad') returning id",[ids[0]])).rows[0].id;
    const matchId=(await db.query("insert into public.matches(room_id,mode) values($1,'squad') returning id",[roomId])).rows[0].id;
    const teamIds=['33333333-3333-3333-3333-333333333333','44444444-4444-4444-4444-444444444444'];
    for(let i=0;i<8;i++)await db.query('insert into public.match_participants(match_id,user_id,team_id,seat,eligible) values($1,$2,$3,$4,$5)',[matchId,ids[i],teamIds[Math.floor(i/4)],i%4,i!==0]);
    for(let question=1;question<=20;question++){
      const first=(await db.query('select private.squad_draft_draw($1,$2) as d',[matchId,question])).rows[0].d;
      assert.deepEqual((await db.query('select private.squad_draft_draw($1,$2) as d',[matchId,question])).rows[0].d,first);
      assert(teamIds.includes(first.choosing_team));assert.equal(Object.keys(first.answerers).length,2);
      for(const [team,player] of Object.entries(first.answerers)){
        const index=ids.indexOf(player);assert(index>=0&&index<8&&index!==0);assert.equal(team,teamIds[Math.floor(index/4)]);
      }
    }
    await user(ids[0]);await denied('select private.squad_draft_draw($1,1)',[matchId]);await denied('select * from private.squad_draft_draws');
  });
  let teamMatch,puzzleRound,scrambleRound;
  await check('Duo puzzle enforces side ownership and awards each placement once',async()=>{
    await admin();
    const roomId=(await db.query("insert into public.rooms(host_id,mode) values($1,'duo') returning id",[ids[10]])).rows[0].id;
    teamMatch=(await db.query("insert into public.matches(room_id,mode,status) values($1,'duo','active') returning id",[roomId])).rows[0].id;
    const teams=['55555555-5555-5555-5555-555555555555','66666666-6666-6666-6666-666666666666'];
    for(let i=0;i<4;i++)await db.query('insert into public.match_participants(match_id,user_id,team_id,seat) values($1,$2,$3,$4)',[teamMatch,ids[10+i],teams[Math.floor(i/2)],i%2]);
    await db.query("insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) values('test_puzzle','collaborative_puzzle','en',1,1,'APPROVED','{\"layout_type\":\"grid_12x8\"}'),('test_scramble','word_scramble','en',1,1,'APPROVED','{}')");
    const pieces=Array.from({length:96},(_,i)=>({id:`p${i}`,slot:`s${i}`,side:i<48?'LEFT':'RIGHT',rotation:0,polygon:`${(i%12)/12},${Math.floor(i/12)/8} ${(i%12+1)/12},${Math.floor(i/12)/8} ${(i%12+1)/12},${(Math.floor(i/12)+1)/8} ${(i%12)/12},${(Math.floor(i/12)+1)/8}`}));
    await db.query("insert into private.content_answers(content_id,answer) values('test_puzzle',$1),('test_scramble',$2)",[JSON.stringify({pieces}),JSON.stringify({accepted_answers:['CAFE','COFFEE'],full_points:10,reduced_points:6})]);
    const phase=(await db.query("insert into public.match_phases(match_id,ordinal,kind,status) values($1,0,'collaborative_puzzle','active') returning id",[teamMatch])).rows[0].id;
    puzzleRound=(await db.query("insert into public.match_rounds(match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline) values($1,$2,0,'test_puzzle','active',clock_timestamp()-interval '1 second',clock_timestamp()-interval '1 second',clock_timestamp()+interval '120 seconds') returning id",[teamMatch,phase])).rows[0].id;
    scrambleRound=(await db.query("insert into public.match_rounds(match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline) values($1,$2,1,'test_scramble','active',clock_timestamp()-interval '1 second',clock_timestamp()-interval '1 second',clock_timestamp()+interval '15 seconds') returning id",[teamMatch,phase])).rows[0].id;
    await user(ids[10]);
    await assert.rejects(db.query('select public.submit_team_action($1,$2,gen_random_uuid(),$3)',[teamMatch,puzzleRound,JSON.stringify({piece_id:'p48',slot_id:'s48',rotation:0})]),/not_eligible/);
    const wrong=(await db.query('select public.submit_team_action($1,$2,gen_random_uuid(),$3) as r',[teamMatch,puzzleRound,JSON.stringify({piece_id:'p0',slot_id:'s1',rotation:0})])).rows[0].r;
    assert.equal(wrong.points,0);assert.equal(wrong.correct,false);
    const key='77777777-7777-7777-7777-777777777777',action=JSON.stringify({piece_id:'p0',slot_id:'s0',rotation:0});
    const first=(await db.query('select public.submit_team_action($1,$2,$3,$4) as r',[teamMatch,puzzleRound,key,action])).rows[0].r;
    assert.equal(first.points,1);assert.deepEqual((await db.query('select public.submit_team_action($1,$2,$3,$4) as r',[teamMatch,puzzleRound,key,action])).rows[0].r,first);
    await denied('select public.submit_team_action($1,$2,gen_random_uuid(),$3)',[teamMatch,puzzleRound,action]);
    await denied('select public.submit_team_action($1,$2,$3,null)',[teamMatch,puzzleRound,key]);
    await denied('select public.submit_team_action($1,$2,gen_random_uuid(),null)',[teamMatch,puzzleRound]);
    assert.equal((await db.query('select score from public.match_participants where match_id=$1 and user_id=$2',[teamMatch,ids[10]])).rows[0].score,1);
    await denied('select * from private.puzzle_placements');
  });
  await check('Duo scramble normalizes answers, scores teams once, and rejects late answers',async()=>{
    await user(ids[10]);
    await denied('select public.submit_team_action($1,$2,gen_random_uuid(),$3)',[teamMatch,scrambleRound,JSON.stringify({score:999})]);
    let result=(await db.query('select public.submit_team_action($1,$2,gen_random_uuid(),$3) as r',[teamMatch,scrambleRound,JSON.stringify({answer:'wrong'})])).rows[0].r;assert.equal(result.points,0);
    result=(await db.query('select public.submit_team_action($1,$2,gen_random_uuid(),$3) as r',[teamMatch,scrambleRound,JSON.stringify({answer:'  coffee  '})])).rows[0].r;assert.equal(result.points,10);
    await user(ids[11]);await denied('select public.submit_team_action($1,$2,gen_random_uuid(),$3)',[teamMatch,scrambleRound,JSON.stringify({answer:'CAFE'})]);
    await user(ids[12]);result=(await db.query('select public.submit_team_action($1,$2,gen_random_uuid(),$3) as r',[teamMatch,scrambleRound,JSON.stringify({answer:'cafe'})])).rows[0].r;assert.equal(result.points,6);
    await user(ids[20]);await denied('select public.submit_team_action($1,$2,gen_random_uuid(),$3)',[teamMatch,scrambleRound,JSON.stringify({answer:'cafe'})]);
    await admin();await db.query("update public.match_rounds set deadline=clock_timestamp()-interval '0.1 second' where id=$1",[puzzleRound]);
    await user(ids[10]);await assert.rejects(db.query('select public.submit_team_action($1,$2,gen_random_uuid(),$3)',[teamMatch,puzzleRound,JSON.stringify({piece_id:'p1',slot_id:'s1',rotation:0})]),/round_not_accepting/);
  });
  await check('Preset reactions are localized, rate-limited, and hidden after reports or blocks',async()=>{
    await admin();
    const roomId=(await db.query("insert into public.rooms(host_id,mode) values($1,'duel') returning id",[ids[30]])).rows[0].id;
    await db.query('insert into public.room_members(room_id,user_id,seat) values($1,$2,0),($1,$3,0)',[roomId,ids[30],ids[31]]);
    await db.exec("insert into public.reactions(id,localization_key,context,approved) values('test_reaction','reaction.test','preset',true); insert into public.localization_entries(key,locale,value) values('reaction.test','en','Well played'),('reaction.test','fr','Bien joué')");
    await user(ids[30]);
    assert.equal((await db.query("select public.reaction_catalog('fr') as r")).rows[0].r[0].text,'Bien joué');
    assert.equal((await db.query("select public.reaction_catalog('ar') as r")).rows[0].r.length,0);
    await denied("select public.send_reaction($1,'arbitrary chat')",[roomId]);
    const event=(await db.query("select public.send_reaction($1,'test_reaction') as id",[roomId])).rows[0].id;
    await user(ids[31]);
    let snapshot=(await db.query('select public.room_snapshot($1) as s',[roomId])).rows[0].s;
    assert(snapshot.reactions.some(x=>x.id===event));
    await db.query("select public.report_player($1,'harassment','',$2)",[ids[30],event]);
    snapshot=(await db.query('select public.room_snapshot($1) as s',[roomId])).rows[0].s;
    assert(!snapshot.reactions.some(x=>x.id===event));
    await user(ids[30]);await db.query("select public.send_reaction($1,'test_reaction')",[roomId]);await db.query("select public.send_reaction($1,'test_reaction')",[roomId]);
    await assert.rejects(db.query("select public.send_reaction($1,'test_reaction')",[roomId]),/rate_limited/);
    await user(ids[31]);await db.query('select public.set_block($1,true)',[ids[30]]);
    assert.equal((await db.query('select public.room_snapshot($1) as s',[roomId])).rows[0].s.reactions.length,0);
  });
  let relayMatch,precisionRound,sortRound,relayTeam='88888888-8888-8888-8888-888888888888';
  await check('Precision relay uses server time, rejects inactive teammates and prevents repeat-crossing farming',async()=>{
    await admin();
    const room=(await db.query("insert into public.rooms(host_id,mode) values($1,'squad') returning id",[ids[22]])).rows[0].id;
    relayMatch=(await db.query("insert into public.matches(room_id,mode,status) values($1,'squad','active') returning id",[room])).rows[0].id;
    for(let i=0;i<4;i++)await db.query('insert into public.match_participants(match_id,user_id,team_id,seat) values($1,$2,$3,$4)',[relayMatch,ids[22+i],relayTeam,i]);
    await db.query("insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) values('test_precision','precision_tap','global',1,1,'APPROVED',$1),('test_sort_a','speed_sort','en',1,1,'APPROVED',$2),('test_sort_b','speed_sort','en',1,1,'APPROVED',$3)",[JSON.stringify({rotation_degrees_per_second:"1",hot_zone_degrees:"30",speed_increment:"1",width_increment:"1",max_hot_zone_degrees:"90"}),JSON.stringify({theme:'test',label:'a'}),JSON.stringify({theme:'test',label:'b'})]);
    await db.exec("insert into private.content_answers(content_id,answer) values('test_precision','{}'),('test_sort_a','{\"bucket\":\"A\"}'),('test_sort_b','{\"bucket\":\"B\"}')");
    const phase=(await db.query("insert into public.match_phases(match_id,ordinal,kind,status) values($1,0,'precision_tap','active') returning id",[relayMatch])).rows[0].id;
    precisionRound=(await db.query("insert into public.match_rounds(match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline) values($1,$2,0,'test_precision','active',clock_timestamp()-interval '1 second',clock_timestamp()-interval '1 second',clock_timestamp()+interval '79 seconds') returning id",[relayMatch,phase])).rows[0].id;
    sortRound=(await db.query("insert into public.match_rounds(match_id,phase_id,ordinal,content_id,status,starts_at,answer_opens_at,deadline) values($1,$2,1,'test_sort_a','active',clock_timestamp()-interval '1 second',clock_timestamp()-interval '1 second',clock_timestamp()+interval '89 seconds') returning id",[relayMatch,phase])).rows[0].id;
    await db.query('select private.prepare_relay_round($1)',[precisionRound]);
    await db.query('update private.precision_states set zone_start=0 where round_id=$1',[precisionRound]);
    await user(ids[23]);await assert.rejects(db.query("select public.submit_relay_action($1,$2,gen_random_uuid(),'{}')",[relayMatch,precisionRound]),/not_eligible/);
    await user(ids[22]);
    const key='99999999-9999-9999-9999-999999999999';
    const first=(await db.query("select public.submit_relay_action($1,$2,$3,'{}') as r",[relayMatch,precisionRound,key])).rows[0].r;
    assert.equal(first.points,1);assert.deepEqual((await db.query("select public.submit_relay_action($1,$2,$3,'{}') as r",[relayMatch,precisionRound,key])).rows[0].r,first);
    assert.equal((await db.query("select public.submit_relay_action($1,$2,gen_random_uuid(),'{}') as r",[relayMatch,precisionRound])).rows[0].r.points,0);
    await denied("select public.submit_relay_action($1,$2,gen_random_uuid(),'{\"score\":999}')",[relayMatch,precisionRound]);
    await admin();await db.query('update private.precision_states set zone_start=180 where round_id=$1',[precisionRound]);
    await user(ids[22]);assert.equal((await db.query("select public.submit_relay_action($1,$2,gen_random_uuid(),'{}') as r",[relayMatch,precisionRound])).rows[0].r.correct,false);
    await admin();assert.equal((await db.query('select streak from private.precision_states where round_id=$1 and user_id=$2',[precisionRound,ids[22]])).rows[0].streak,0);
  });
  await check('Speed Sort shares one stream, serializes relay turns, and scores only correct buckets',async()=>{
    await admin();await db.query('select private.prepare_relay_round($1)',[sortRound]);await db.query("update private.sort_streams set items=array['test_sort_a','test_sort_b'] where round_id=$1",[sortRound]);
    await user(ids[23]);await assert.rejects(db.query('select public.submit_relay_action($1,$2,gen_random_uuid(),$3)',[relayMatch,sortRound,JSON.stringify({index:0,bucket:'A'})]),/not_eligible/);
    await user(ids[22]);assert.equal((await db.query('select public.submit_relay_action($1,$2,gen_random_uuid(),$3) as r',[relayMatch,sortRound,JSON.stringify({index:0,bucket:'A'})])).rows[0].r.points,1);
    await user(ids[23]);await assert.rejects(db.query('select public.submit_relay_action($1,$2,gen_random_uuid(),$3)',[relayMatch,sortRound,JSON.stringify({index:0,bucket:'B'})]),/stale_item/);
    assert.equal((await db.query('select public.submit_relay_action($1,$2,gen_random_uuid(),$3) as r',[relayMatch,sortRound,JSON.stringify({index:1,bucket:'A'})])).rows[0].r.points,0);
    await admin();const state=(await db.query('select item_index,streak from private.sort_states where round_id=$1 and team_id=$2',[sortRound,relayTeam])).rows[0];assert.equal(state.item_index,2);assert.equal(state.streak,0);
  });
  let flowUserSequence=100;
  async function verifyTeamFlow(mode,count){
    await admin();
    await db.exec("update public.content_items set payload=payload||'{\"theme\":\"test\"}'::jsonb where kind='question_round'");
    for(let i=0;i<5;i++){
      const id=`flow_question_${i}`;
      await db.query("insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) select $1,kind,locale,schema_version,content_version,status,payload from public.content_items where id='test_content_0' on conflict do nothing",[id]);
      await db.query("insert into private.content_answers(content_id,answer) values($1,'{\"correct_option_id\":\"o0\"}') on conflict do nothing",[id]);
    }
    for(let i=0;i<4;i++){
      const id=`flow_scramble_${i}`;
      await db.query("insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) select $1,kind,locale,schema_version,content_version,status,payload from public.content_items where id='test_scramble' on conflict do nothing",[id]);
      await db.query("insert into private.content_answers(content_id,answer) select $1,answer from private.content_answers where content_id='test_scramble' on conflict do nothing",[id]);
    }
    const players=[];
    for(let i=0;i<count;i++){
      const number=flowUserSequence++,id=`aaaaaaaa-0000-4000-8000-${String(number).padStart(12,'0')}`;players.push(id);
      await admin();await db.query('insert into auth.users(id,email,raw_user_meta_data) values($1,$2,$3)',[id,`flow${number}@example.invalid`,JSON.stringify({username:`Flow_${number}`})]);
      await user(id);await db.query("select public.set_loadout('{}'::text[])");
    }
    await user(players[0]);const room=(await db.query('select public.create_room($1) as id',[mode])).rows[0].id;await db.query('select public.set_matchmaking($1,true)',[room]);await db.query('select public.set_ready($1,true)',[room]);
    for(const id of players.slice(1)){await user(id);await db.query('select public.join_room($1)',[room]);await db.query('select public.set_ready($1,true)',[room]);}
    await user(players[0]);const match=(await db.query('select public.start_match($1) as id',[room])).rows[0].id;
    assert.equal((await db.query('select public.start_match($1) as id',[room])).rows[0].id,match);
    await admin();
    const duration=(await db.query('select extract(epoch from deadline-starts_at)::int as seconds from public.match_rounds where match_id=$1 and ordinal=0',[match])).rows[0].seconds;
    assert.equal(duration,mode==='duo'?180:80);
    if(mode==='duo'){
      await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '121 seconds',answer_opens_at=clock_timestamp()-interval '121 seconds',deadline=clock_timestamp()+interval '59 seconds' where match_id=$1 and ordinal=0",[match]);
      await user(players[0]);const active=(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s;
      assert.equal(active.rounds.find(r=>r.ordinal===0).status,'active');
      await admin();
    }
    await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '1 second',answer_opens_at=clock_timestamp()-interval '1 second' where match_id=$1",[match]);
    await db.query("update public.match_phases set starts_at=clock_timestamp()-interval '1 second' where match_id=$1 and ordinal=0",[match]);
    await user(players[0]);saveFixture(mode+'_initial',(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s);

    await admin();await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '121 seconds',answer_opens_at=clock_timestamp()-interval '121 seconds',deadline=clock_timestamp()-interval '1 second' where match_id=$1",[match]);
    await db.query("update public.match_phases set starts_at=clock_timestamp()-interval '121 seconds',deadline=clock_timestamp()-interval '1 second' where match_id=$1 and ordinal=0",[match]);
    await user(players[0]);await db.query('select public.match_snapshot($1)',[match]);
    if(mode==='squad'){
      saveFixture('squad_sort',(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s);
      await admin();assert.equal((await db.query('select stage from private.team_match_state where match_id=$1',[match])).rows[0].stage,1);
      await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '91 seconds',answer_opens_at=clock_timestamp()-interval '91 seconds',deadline=clock_timestamp()-interval '1 second' where match_id=$1 and ordinal=1",[match]);
      await user(players[0]);await db.query('select public.match_snapshot($1)',[match]);
    }
    saveFixture(mode+'_draft',(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s);
    const total=mode==='duo'?15:20;
    for(let question=1;question<=total;question++){
      await admin();const draft=(await db.query('select * from private.team_drafts where match_id=$1 and question=$2',[match,question])).rows[0];assert(draft);
      const chooser=(await db.query('select user_id from public.match_participants where match_id=$1 and team_id=$2 order by seat limit 1',[match,draft.choosing_team])).rows[0].user_id;
      await user(chooser);const round=(await db.query("select public.choose_draft_theme($1,$2,'test') as id",[match,question])).rows[0].id;
      assert.equal((await db.query("select public.choose_draft_theme($1,$2,'test') as id",[match,question])).rows[0].id,round);
      await admin();const schedule=(await db.query('select starts_at,answer_opens_at,deadline from public.match_rounds where id=$1',[round])).rows[0];
      assert.equal(new Date(schedule.answer_opens_at)-new Date(schedule.starts_at),15000);assert.equal(new Date(schedule.deadline)-new Date(schedule.answer_opens_at),20000);
      await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '16 seconds',answer_opens_at=clock_timestamp()-interval '1 second',deadline=clock_timestamp()+interval '19 seconds' where id=$1",[round]);
      const designated=Object.values(draft.answerers);
      const outsider=players.find(id=>!designated.includes(id));
      await user(outsider);await assert.rejects(db.query("select public.submit_answer($1,$2,gen_random_uuid(),'{\"option_id\":\"o0\"}')",[match,round]),/not_eligible/);
      for(const id of designated){await user(id);await db.query("select public.submit_answer($1,$2,gen_random_uuid(),'{\"option_id\":\"o0\"}')",[match,round]);}
      await admin();await db.query("update public.match_rounds set deadline=clock_timestamp()-interval '0.01 seconds' where id=$1",[round]);
      await user(players[0]);await db.query('select public.match_snapshot($1)',[match]);
    }
    if(mode==='duo'){
      saveFixture('duo_scramble',(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s);
      await admin();assert.equal((await db.query('select count(*)::integer as n from public.match_participants where match_id=$1 and eligible',[match])).rows[0].n,count);
      assert.equal((await db.query('select count(*)::integer as n from public.match_rounds where match_id=$1 and ordinal>=16',[match])).rows[0].n,5);
      await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '16 seconds',answer_opens_at=clock_timestamp()-interval '16 seconds',deadline=clock_timestamp()-interval '1 second' where match_id=$1 and ordinal>=16",[match]);
    }
    await user(players[0]);await db.query('select public.match_snapshot($1)',[match]);await db.query('select public.match_snapshot($1)',[match]);
    saveFixture(mode+'_results',(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s);
    const results=(await db.query('select rank,score,winner from public.match_results where match_id=$1',[match])).rows;
    assert.equal(results.length,count);assert(results.every(r=>r.score===total));assert.equal(results.filter(r=>r.winner).length,mode==='duo'?2:4);
    await admin();const flames=(await db.query("select count(*)::integer as n,sum(delta)::integer as total from public.currency_ledger where source_event=$1 and currency='flames'",[match])).rows[0];assert.equal(flames.n,mode==='duo'?2:4);assert.equal(flames.total,flames.n);
  }
  await check('Small Duo rooms cycle all fifteen drafts, retain every team, and award exactly two winner Flames',()=>verifyTeamFlow('duo',4));
  await check('Forty-player Duo completes all phases without eliminating any of twenty teams',()=>verifyTeamFlow('duo',40));
  await check('Squad completes precision, sort, twenty random drafts and exactly four winner Flames',()=>verifyTeamFlow('squad',8));
  async function verifySoloFlow(count,tied){
    const players=[];await admin();
    for(let i=0;i<count+1;i++){
      const n=flowUserSequence++,id=`bbbbbbbb-0000-4000-8000-${String(n).padStart(12,'0')}`;players.push(id);
      await db.query('insert into auth.users(id,email,raw_user_meta_data) values($1,$2,$3)',[id,`solo${n}@example.invalid`,JSON.stringify({username:`Solo_${n}`})]);
    }
    const outsider=players.pop();await user(players[0]);
    const room=(await db.query("select public.create_room('solo') as id")).rows[0].id;
    await db.query('select public.set_ready($1,true)',[room]);
    await assert.rejects(db.query('select public.start_match($1)',[room]),/players_not_ready/);
    await db.query('select public.set_matchmaking($1,true)',[room]);
    for(const id of players.slice(1)){await user(id);await db.query('select public.join_room($1)',[room]);await db.query('select public.set_ready($1,true)',[room]);}
    if(count===20){await user(outsider);await denied('select public.join_room($1)',[room]);}
    await user(players[1]);await denied('select public.start_match($1)',[room]);
    await user(players[0]);const match=(await db.query('select public.start_match($1) as id',[room])).rows[0].id;
    assert.equal((await db.query('select public.start_match($1) as id',[room])).rows[0].id,match);
    await user(outsider);await denied('select public.match_snapshot($1)',[match]);
    await admin();const rounds=(await db.query('select * from public.match_rounds where match_id=$1 order by ordinal',[match])).rows;
    assert.equal(rounds.length,17);assert.equal(new Date(rounds[0].deadline)-new Date(rounds[0].starts_at),20_000);
    assert.equal(new Date(rounds[1].deadline)-new Date(rounds[1].starts_at),90_000);
    for(const r of rounds.slice(2)){assert.equal(new Date(r.answer_opens_at)-new Date(r.starts_at),10_000);assert.equal(new Date(r.deadline)-new Date(r.answer_opens_at),20_000);}
    async function activate(index){
      await admin();
      await db.query("update public.match_rounds set starts_at=clock_timestamp()-interval '12 seconds',answer_opens_at=clock_timestamp()-interval '2 seconds',deadline=clock_timestamp()+interval '8 seconds' where id=$1",[rounds[index].id]);
      await user(players[0]);return (await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s;
    }
    async function expire(index){await admin();await db.query("update public.match_rounds set deadline=clock_timestamp()-interval '1 second' where id=$1",[rounds[index].id]);await user(players[0]);await db.query('select public.match_snapshot($1)',[match]);}
    let snap=await activate(0);assert.equal(snap.board.players.length,1);saveFixture('solo_precision',snap);
    assert.equal((await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s.match.version,snap.match.version);
    await admin();await db.query('update private.precision_states set zone_start=0 where round_id=$1',[rounds[0].id]);
    for(const id of players){
      await user(id);const key=(await db.query('select gen_random_uuid() as id')).rows[0].id;
      const result=(await db.query("select public.submit_relay_action($1,$2,$3,'{}') as r",[match,rounds[0].id,key])).rows[0].r;
      assert.equal(result.points,1);assert.deepEqual((await db.query("select public.submit_relay_action($1,$2,$3,'{}') as r",[match,rounds[0].id,key])).rows[0].r,result);
    }
    await expire(0);await assert.rejects(db.query("select public.submit_relay_action($1,$2,gen_random_uuid(),'{}')",[match,rounds[0].id]),/round_not_accepting/);
    await activate(1);await admin();await db.query("update private.sort_streams set items=array['test_sort_a','test_sort_b'] where round_id=$1",[rounds[1].id]);
    for(const id of players){
      await user(id);snap=(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s;
      assert.equal(snap.board.active_user,id);assert.equal(snap.board.index,0);assert.equal(snap.board.label,'a');
      assert.equal((await db.query('select public.submit_relay_action($1,$2,gen_random_uuid(),$3) as r',[match,rounds[1].id,JSON.stringify({index:0,bucket:'A'})])).rows[0].r.points,1);
      await assert.rejects(db.query('select public.submit_relay_action($1,$2,gen_random_uuid(),$3)',[match,rounds[1].id,JSON.stringify({index:0,bucket:'A'})]),/stale_item/);
    }
    saveFixture('solo_sort',snap);await expire(1);
    for(let i=2;i<17;i++){
      await activate(i);await admin();const answer=(await db.query('select answer from private.content_answers where content_id=$1',[rounds[i].content_id])).rows[0].answer;
      for(const id of players){await user(id);await db.query('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[match,rounds[i].id,JSON.stringify({option_id:answer.correct_option_id})]);}
      if(i===2)saveFixture('solo_question',(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s);
      if(i===16&&!tied){await admin();await db.query('update public.match_participants set score=score+1 where match_id=$1 and user_id=$2',[match,players[0]]);}
      await expire(i);
    }
    snap=(await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s;
    saveFixture('solo_results',snap);assert.equal(snap.match.status,'results');assert.equal(snap.results.length,count);assert(snap.participants.every(p=>p.eligible&&p.team_id===null));
    assert.equal(snap.results.filter(r=>r.winner).length,1);assert(snap.results.every(r=>r.score===17||!tied&&r.user_id===players[0]&&r.score===18));
    if(tied){assert(snap.tie_rolls.length>=count);assert(snap.tie_rolls.every(r=>r.roll>=1&&r.roll<=20));}else{assert.equal(snap.tie_rolls.length,0);assert.equal(snap.results.find(r=>r.winner).user_id,players[0]);}
    assert.equal((await db.query('select public.match_snapshot($1) as s',[match])).rows[0].s.match.version,snap.match.version);
    await admin();const rewards=(await db.query('select count(*)::int as n,sum(delta)::int as points from public.currency_ledger where source_event=$1',[match])).rows[0];assert.deepEqual(rewards,{n:1,points:1});
  }
  await check('Twenty-player Solo runs all phases, isolates actions, accepts every correct answer and resolves tied winners once',()=>verifySoloFlow(20,true));
  await check('Two-player Solo keeps a unique winner without roulette or duplicate rewards',()=>verifySoloFlow(2,false));
  await check('Own account details never expose other users email or provider identities',async()=>{
    await admin();await db.query("insert into auth.identities(user_id,provider) values($1,'email'),($1,'google'),($2,'discord')",[ids[40],ids[41]]);
    await user(ids[40]);const own=(await db.query('select public.profile_snapshot() as s')).rows[0].s;
    assert.equal(own.email,'test40@example.invalid');assert.deepEqual(own.providers,['email','google']);
    assert(Array.isArray(own.mode_stats));
    await denied('select * from auth.identities');await denied('select * from auth.users');
    await user(ids[41]);const other=(await db.query('select public.profile_snapshot() as s')).rows[0].s;
    assert.equal(other.email,'test41@example.invalid');assert.deepEqual(other.providers,['discord']);
  });
  await check('Friend previews require acceptance and suppress blocked profiles without leaking account details',async()=>{
    await user(ids[38]);await denied('select public.player_preview($1)',[ids[39]]);
    await db.query("select public.friend_action($1,'request')",[ids[39]]);
    await denied('select public.player_preview($1)',[ids[39]]);
    await user(ids[39]);await db.query("select public.friend_action($1,'accept')",[ids[38]]);
    await user(ids[38]);const preview=(await db.query('select public.player_preview($1) as s',[ids[39]])).rows[0].s;
    assert.equal(preview.profile.id,ids[39]);assert(!('email' in preview));assert(!('providers' in preview));assert(!('currencies' in preview));
    await db.query('select public.set_block($1,true)',[ids[39]]);await denied('select public.player_preview($1)',[ids[39]]);
  });
  await check('Twenty-slot server random source stays within bounds and is client-inaccessible',async()=>{
    await user(ids[40]);await denied('select private.roll_twenty()');
    await admin();const rolls=(await db.query('select private.roll_twenty() as n from generate_series(1,1000)')).rows;
    assert(rolls.every(x=>x.n>=1&&x.n<=20));assert.equal(new Set(rolls.map(x=>x.n)).size,20);
  });

  await check('Public pool respects mode, privacy, blocks, presence, capacity and retry membership',async()=>{
    await admin();
    // Isolate the pool from earlier fixtures without changing their match outcomes.
    await db.exec('update public.rooms set matchmaking=false');
    const pool=Array.from({length:42},(_,i)=>`10000000-0000-0000-0000-${String(i+1).padStart(12,'0')}`);
    for(const [i,id] of pool.entries())await db.query('insert into auth.users(id,raw_user_meta_data) values($1,$2)',[id,JSON.stringify({username:`PoolTester_${i}`})]);
    await user(pool[0]);const room=(await db.query("select public.create_room('squad') as id")).rows[0].id;
    await user(pool[1]);assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,null);
    await user(pool[0]);await db.query('select public.set_matchmaking($1,true)',[room]);
    await user(pool[1]);assert.equal((await db.query("select public.join_public_room('duo') as id")).rows[0].id,null);
    await user(pool[0]);await db.query('select public.set_block($1,true)',[pool[1]]);
    await user(pool[1]);assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,null);
    await user(pool[0]);await db.query('select public.set_block($1,false)',[pool[1]]);
    await admin();await db.query("update public.room_members set last_seen=clock_timestamp()-interval '1 minute' where room_id=$1",[room]);
    await user(pool[1]);assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,null);
    await user(pool[0]);await db.query('select public.heartbeat($1)',[room]);
    for(let i=1;i<40;i++){
      await user(pool[i]);assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,room);
    }
    // Retry remains idempotent even when the room has become full.
    assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,room);
    await denied("select public.join_public_room('duo')");
    await user(pool[40]);assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,null);
    await admin();assert.equal((await db.query('select count(*)::integer as n from public.room_members where room_id=$1',[room])).rows[0].n,40);
    assert.equal((await db.query('select count(*)::integer as n from public.rooms where host_id=any($1::uuid[])',[pool])).rows[0].n,1);
    await db.query("update public.rooms set status='playing' where id=$1",[room]);
    await user(pool[41]);assert.equal((await db.query("select public.join_public_room('squad') as id")).rows[0].id,null);
    await denied("select public.join_public_room('duel')");await denied('select public.join_public_room(null)');
    await admin();await db.exec('set role anon');await denied("select public.join_public_room('squad')");
  });

  await check('Admin assignment is service-only and levels use earned Flames', async()=>{
    await user(ids[42]);await denied('select public.bootstrap_admin($1)',[ids[42]]);
    await denied('select * from private.administrators');
    await db.query("select public.update_profile('Mr.onyx')");
    await admin();await db.exec('set role service_role');await db.query('select public.bootstrap_admin($1)',[ids[42]]);
    await admin();
    await db.query("insert into public.currency_ledger(user_id,currency,delta,reason,source_event,idempotency_key) values($1,'flames',30,'match_win','test-level','test-level-positive'),($1,'flames',-5,'vault','test-level','test-level-spent')",[ids[42]]);
    await user(ids[42]);const r=await db.query('select public.profile_snapshot() as snapshot');
    assert.equal(r.rows[0].snapshot.level,4);assert.equal(r.rows[0].snapshot.lifetime_flames,30);assert.equal(r.rows[0].snapshot.is_admin,true);
    await user(ids[41]);const other=await db.query('select public.profile_snapshot() as snapshot');assert.equal(other.rows[0].snapshot.is_admin,false);
  });
  await check('Profile photo storage is private and cannot overwrite another user',async()=>{
    await user(ids[42]);await db.query("insert into storage.objects(bucket_id,name) values('profile-photos',$1)",[ids[42]+'/photo.jpg']);
    await db.query('select public.set_profile_photo()');
    await user(ids[41]);await assert.rejects(db.query("insert into storage.objects(bucket_id,name) values('profile-photos',$1)",[ids[42]+'/photo.jpg']),/row-level security/);
    const changed=await db.query("update storage.objects set name=$1 where name=$2 returning id",[ids[41]+'/photo.jpg',ids[42]+'/photo.jpg']);assert.equal(changed.rows.length,0);
    await admin();assert.equal((await db.query("select public from storage.buckets where id='profile-photos'")).rows[0].public,false);
  });
  await check('Deletion requests are self-only, idempotent and visible only to service administrators',async()=>{
    await user(ids[42]);await db.query('select public.request_account_deletion()');await db.query('select public.request_account_deletion()');
    await denied('select * from private.deletion_requests');await denied('select public.admin_deletion_requests()');
    await assert.rejects(db.query('select public.request_account_deletion($1)',[ids[41]]),/does not exist/);
    await admin();await db.exec('set role anon');await denied('select public.request_account_deletion()');
    await admin();await db.exec('set role service_role');const requests=(await db.query('select public.admin_deletion_requests() as r')).rows[0].r;
    assert.equal(requests.length,1);assert.equal(requests[0].user_id,ids[42]);assert.equal(requests[0].status,'pending');
  });
  await check('Learning answers accept multilingual aliases and typos but never approximate numeric values',async()=>{
    await admin();
    for(const [input,accepted,expected] of [
      ['westfalia',['Westphalia','Westfalia'],true],['mali',['The Mali Empire','Mali','مالي'],true],
      ['constantine',['Constantinople','Constantine'],true],['eiyt',['8','eight','eiyt','huit','ثمانية'],true],
      ['huit',['8','eight','huit','ثمانية'],true],['٨',['8','eight'],true],
      ['Gabriel Garcia Marqez',['Gabriel García Márquez','Marquez'],true],
      ['eighty',['8','eight'],false],['ثمانين',['8','ثمانية'],false],['1067',['1066','one thousand and sixty-six'],false],
      ['Bali',['Mali'],false],['V',['W'],false],['nothing',['Westphalia'],false]
    ])assert.equal((await db.query('select private.learning_answer($1,$2) result',[input,JSON.stringify(accepted)])).rows[0].result,expected,input);
  });
  await check('Missions use verified progress, cannot be client-granted, and reward each claim once',async()=>{
    await admin();
    const winner=(await db.query('select user_id from public.match_results where winner limit 1')).rows[0].user_id;
    await user(winner);
    const snapshot=(await db.query('select public.mission_snapshot() rows')).rows[0].rows;
    assert.equal(snapshot.length,6);assert(snapshot.find(x=>x.id==='win_1').progress>=1);
    await db.query("select public.claim_mission('win_1')");await db.query("select public.claim_mission('win_1')");
    assert((await db.query('select public.mission_snapshot() rows')).rows[0].rows.find(x=>x.id==='win_1').claimed);
    await admin();
    const ledger=(await db.query("select currency, sum(delta)::integer amount,count(*)::integer count from public.currency_ledger where user_id=$1 and source_event='win_1' group by currency",[winner])).rows;
    assert.equal(ledger.find(x=>x.currency==='gold').amount,150);assert.equal(ledger.find(x=>x.currency==='flames').amount,1);assert(ledger.every(x=>x.count===1));
    await user(ids[41]);await assert.rejects(db.query("select public.claim_mission('play_20')"),/mission_incomplete/);
    await denied("insert into private.mission_claims values($1,'play_20',now())",[ids[41]]);
  });
  await check('Image pools lock four correct and six wrong choices per round and reject hidden IDs',async()=>{
    await admin();
    const options=Array.from({length:24},(_,i)=>({id:`pool_${i}`,label:`Object ${i}`}));
    const answers=options.map((o,i)=>({id:o.id,correct:i<12,points:i<12?10:0}));
    await db.query("insert into public.content_items(id,kind,locale,schema_version,content_version,status,payload) values('pool_test','image_guess','en',1,1,'APPROVED',$1)",[JSON.stringify({options,answer_pool:true,selection_count:4})]);
    await db.query("insert into private.content_answers(content_id,answer) values('pool_test',$1)",[JSON.stringify({choices:answers,scoring_policy:'correct_only'})]);
    const m=(await db.query("select m.id,p.user_id from public.matches m join public.match_participants p on p.match_id=m.id where m.mode='duel' and p.eligible limit 1")).rows[0];
    const r=(await db.query("insert into public.match_rounds(match_id,phase_id,ordinal,content_id,starts_at,answer_opens_at,deadline,status) values($1,(select phase_id from public.match_rounds where match_id=$1 limit 1),999,'pool_test',now()-interval '1 second',now()-interval '1 second',now()+interval '60 seconds','active') returning id",[m.id])).rows[0].id;
    const idsChosen=(await db.query('select ids from private.image_round_choices where round_id=$1',[r])).rows[0].ids;
    assert.equal(idsChosen.length,10);assert.equal(idsChosen.filter(id=>answers.find(a=>a.id===id).correct).length,4);
    const payload=(await db.query("select private.round_display($1,payload,false) data from public.content_items where id='pool_test'",[r])).rows[0].data;
    assert.equal(payload.options.length,10);assert(!('answer_pool' in payload));
    const hidden=options.find(o=>!idsChosen.includes(o.id)).id;
    await user(m.user_id);
    await assert.rejects(db.query('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[m.id,r,JSON.stringify({choice_ids:[hidden,...idsChosen.slice(0,3)]})]),/invalid_selection/);
    await db.query('select public.submit_answer($1,$2,gen_random_uuid(),$3)',[m.id,r,JSON.stringify({choice_ids:idsChosen.filter(id=>answers.find(a=>a.id===id).correct)})]);
  });
  console.log(`Backend verification: ${checks} checks passed. Real PostgreSQL semantics via PGlite; network/Realtime and concurrent connections require separate integration tests.`);
} catch(error) {
  console.error('BACKEND TEST FAILURE:', error.message, error.detail ?? '', error.where ?? '');
  process.exitCode=1;
} finally { await db.close(); }
