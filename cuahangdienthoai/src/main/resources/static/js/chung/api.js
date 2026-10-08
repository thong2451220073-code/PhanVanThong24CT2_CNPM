/* Ham api(): goi backend, tu dong gan token, xu ly loi 401 va thong bao loi. */
async function api(path,opt={}){
  const headers={...(opt.body instanceof FormData?{}:{'Content-Type':'application/json'}),...(opt.headers||{}),...Phien.auth()};
  const r=await fetch(API+path,{...opt,headers});
  const text=await r.text();
  let data=null; try{data=text?JSON.parse(text):null}catch{data=text}
  if(!r.ok){
    if(r.status===401){Phien.clear(); if(!location.pathname.includes('login')) location.href='/login.html'}
    const details=data?.chiTietLoi?Object.values(data.chiTietLoi).join(' · '):'';
    throw new Error(data?.thongBao||data?.message||details||`HTTP ${r.status}`)
  }
  return data;
}
