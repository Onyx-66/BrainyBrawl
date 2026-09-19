// Enforce byte limits while streaming; Content-Length alone is not trustworthy.
export class BodyTooLarge extends Error {}
export async function readBoundedJson(request, limit=8192) {
  const declared=request.headers.get("content-length");
  if(declared!==null && Number(declared)>limit)throw new BodyTooLarge();
  if(!request.body)throw new SyntaxError("missing_body");
  const reader=request.body.getReader();
  const decoder=new TextDecoder("utf-8",{fatal:true});
  let bytes=0,text="";
  try {
    for(;;){
      const {done,value}=await reader.read();
      if(done)break;
      bytes+=value.byteLength;
      if(bytes>limit){await reader.cancel();throw new BodyTooLarge();}
      text+=decoder.decode(value,{stream:true});
    }
    text+=decoder.decode();
  } finally {reader.releaseLock();}
  const result=JSON.parse(text);
  if(result===null||typeof result!=="object"||Array.isArray(result))throw new SyntaxError("invalid_body");
  return result;
}
