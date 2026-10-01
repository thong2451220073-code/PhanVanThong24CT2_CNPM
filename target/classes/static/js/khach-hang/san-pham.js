/* San pham: tai danh sach, the san pham, xem chi tiet san pham. */
async function loadProducts(q='',category='',min='',max=''){
  let url;
  if(q) url=`/api/san-pham/tim-kiem?tuKhoa=${encodeURIComponent(q)}&size=24`;
  else if(min||max) url=`/api/san-pham/khoang-gia?giaTu=${encodeURIComponent(min||0)}&giaDen=${encodeURIComponent(max||999999999999)}&size=24`;
  else url='/api/san-pham?size=24';
  return api(url);
}
function productImg(p){return p.duongDanAnh||''}
function productCard(p){const img=productImg(p);return `<article class="card product-card"><div class="product-img">${img?`<img src="${esc(img)}" onerror="this.remove()">`:'<span class="placeholder">📱</span>'}</div><div class="card-body"><div class="muted small">Điện thoại</div><h3>${esc(p.tenSanPham)}</h3><p><span class="price">${money(p.giaKhuyenMai||p.gia)}</span>${p.giaKhuyenMai?`<span class="old">${money(p.gia)}</span>`:''}</p><div class="actions"><button class="btn" onclick="detail(${p.id})">Chi tiết</button>${Phien.user?`<button class="btn secondary" onclick="addCart(${p.id})">🛒 Thêm</button>`:''}</div></div></article>`}
async function detail(id){try{const p=await api('/api/san-pham/'+id);const img=productImg(p);document.body.insertAdjacentHTML('beforeend',`<div class="modal open" id="detail"><div class="modalbox"><div class="row"><h2>${esc(p.tenSanPham)}</h2><button class="btn secondary" onclick="closeModal('detail')">Đóng</button></div><div class="detail-grid"><div class="detail-image">${img?`<img src="${esc(img)}">`:'📱'}</div><div><p class="price big">${money(p.giaKhuyenMai||p.gia)}</p><p>${esc(p.moTa||'Chưa có mô tả.')}</p><p>SKU: ${esc(p.maSku||'-')} · ${esc(p.trangThai||'-')}</p>${Phien.user?`<div class="actions"><input id="detailQty" type="number" min="1" value="1"><button class="btn" onclick="addCart(${p.id},Number(val('detailQty')));closeModal('detail')">Thêm vào giỏ</button></div>`:'<a class="btn" href="/login.html">Đăng nhập để mua</a>'}</div></div></div></div>`)}catch(e){notify(e.message,'error')}}
