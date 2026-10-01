/* Ham tien ich dung chung: dinh dang tien, escape HTML, doc gia tri o nhap, thong bao, dong modal. */
const money=n=>new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(Number(n||0));
const esc=s=>String(s??'').replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
const val=id=>document.getElementById(id)?.value?.trim()||'';
function notify(msg,type='success'){let n=document.getElementById('toast');if(!n){document.body.insertAdjacentHTML('beforeend','<div id="toast" class="toast"></div>');n=document.getElementById('toast')}n.className='toast '+type;n.textContent=msg;n.classList.add('show');clearTimeout(window.__toast);window.__toast=setTimeout(()=>n.classList.remove('show'),2600)}
function loading(text='Đang tải...'){return `<div class="panel loading">${esc(text)}</div>`}
function closeModal(id){document.getElementById(id)?.remove()}
