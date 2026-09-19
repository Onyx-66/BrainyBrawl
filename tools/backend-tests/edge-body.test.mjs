import assert from 'node:assert/strict';
import {readBoundedJson,BodyTooLarge} from '../../supabase/functions/game-action/body.mjs';
const request=body=>new Request('https://example.invalid',{method:'POST',body});
assert.deepEqual(await readBoundedJson(request('{"action":"match_snapshot","args":{}}')),{action:'match_snapshot',args:{}});
await assert.rejects(readBoundedJson(request('null')),SyntaxError);
await assert.rejects(readBoundedJson(request('[]')),SyntaxError);
await assert.rejects(readBoundedJson(request('{')),SyntaxError);
await assert.rejects(readBoundedJson(request(new Uint8Array([255]))),TypeError);
await assert.rejects(readBoundedJson(request(JSON.stringify({text:'x'.repeat(8192)}))),BodyTooLarge);
// Multi-byte Unicode counts as bytes, including when the caller omits Content-Length.
await assert.rejects(readBoundedJson(request(JSON.stringify({text:'\u0627'.repeat(5000)}))),BodyTooLarge);
let cancelled=false;
const stream=new ReadableStream({pull(c){c.enqueue(new Uint8Array(4096));},cancel(){cancelled=true;}});
await assert.rejects(readBoundedJson(new Request('https://example.invalid',{method:'POST',body:stream,duplex:'half'})),BodyTooLarge);
assert(cancelled);
console.log('PASS Edge request bounds, strict UTF-8, object shape and stream cancellation');
