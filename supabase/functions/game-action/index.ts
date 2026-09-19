// User-scoped gateway: authorization and scoring remain in transactional SQL.
import { createClient } from "npm:@supabase/supabase-js@2.57.4";
import { readBoundedJson, BodyTooLarge } from "./body.mjs";
const allowed = new Set([
  "choose_draft_theme", "submit_team_action", "submit_relay_action", "move_puzzle_cursor",
  "room_snapshot", "current_room", "reaction_catalog", "leaderboard", "profile_snapshot", "player_preview", "social_snapshot", "store_snapshot", "equip_cosmetic",
  "start_match", "match_snapshot", "submit_answer", "create_room", "join_room",
  "quick_match", "join_public_room", "set_matchmaking", "set_ready", "heartbeat", "leave_room",
  "invite_friend", "rename_team", "purchase_item", "set_loadout", "balances",
  "send_reaction", "update_profile", "search_players", "friend_action", "set_block", "report_player",
]);
Deno.serve(async (request: Request) => {
  const headers = { "Content-Type": "application/json", "Cache-Control": "no-store" };
  if (request.method !== "POST") return new Response('{}', { status: 405, headers });
  const auth = request.headers.get("Authorization");
  if (!auth?.startsWith("Bearer ")) return new Response('{}', { status: 401, headers });
  const url = Deno.env.get("SUPABASE_URL");
  const key = Deno.env.get("SUPABASE_ANON_KEY");
  if (!url || !key) return new Response('{}', { status: 503, headers });
  const client = createClient(url, key, {
    global: { headers: { Authorization: auth } },
    auth: { persistSession: false, autoRefreshToken: false },
  });
  const { error: authError } = await client.auth.getUser();
  if (authError) return new Response('{}', { status: 401, headers });
  try {
    const body = await readBoundedJson(request);
    if (!allowed.has(body.action) || !body.args || typeof body.args !== "object" || Array.isArray(body.args)) {
      return new Response('{}', { status: 400, headers });
    }
    const { data, error } = await client.rpc(body.action, body.args);
    if (error) {
      // Never return DB diagnostics, query text or credentials.
      const safe = new Set(["players_not_ready", "loadout_required", "approved_content_required", "rules_not_approved",
        "round_not_accepting", "already_submitted", "insufficient_funds", "room_full", "invite_required", "rate_limited"]);
      return new Response(JSON.stringify({ error: safe.has(error.message) ? error.message : "request_rejected" }), { status: 409, headers });
    }
    return new Response(JSON.stringify({ data }), { status: 200, headers });
  } catch (error) {
    if(error instanceof BodyTooLarge)return new Response('{}', { status: 413, headers });
    return new Response(JSON.stringify({ error: "invalid_request" }), { status: 400, headers });
  }
});
