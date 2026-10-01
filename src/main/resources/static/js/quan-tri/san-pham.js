/* Quan tri - san pham: bang san pham, form them/sua, luu, xoa. */
function adminProductTable(d,stock={}){(d.content||[]).forEach(p=>adminProdCache[p.id]=p.tenSanPham);return `<div class="panel"><div class="row"><h2>Quản lý sản phẩm</h2><button class="btn" onclick="productForm()">+ Thêm sản phẩm</button></div><div class="table-wrap"><table class="table"><tr><th>ID</th><th>Tên</th><th>Giá</th><th>SKU</th><th>Tồn kho</th><th>Trạng thái</th><th></th></tr>${(d.content||[]).map(p=>`<tr><td>${p.id}</td><td>${esc(p.tenSanPham)}</td><td>${money(p.giaKhuyenMai||p.gia)}</td><td>${esc(p.maSku||'-')}</td><td>${stockCell(stock[p.id])}</td><td>${esc(p.trangThai||'-')}</td><td><button class="link-btn" onclick="adminStockIn(${p.id})">+ Nhập kho</button> <button class="link-btn" onclick="productForm(${p.id})">Sửa</button> <button class="link-btn danger-text" onclick="deleteProduct(${p.id})">Xóa</button></td></tr>`).join('')}</table></div></div>`}
async function adminProducts(){adminTab='products';const [d,stock]=await Promise.all([api('/api/san-pham?size=100'),loadStockMap()]);document.getElementById('adminBody').innerHTML=adminProductTable(d,stock)}
function productForm(id){
  // Đóng modal cũ để tránh trùng ID khi mở Sửa nhiều lần.
  document.getElementById('productModal')?.remove();
 
  document.body.insertAdjacentHTML('beforeend',`<div class="modal open" id="productModal"><div class="modalbox"><h2>${id?'Sửa':'Thêm'} sản phẩm</h2><div class="form"><input type="hidden" id="pId" value="${id||''}"><label>Tên sản phẩm</label><input id="pName" required><label>Mô tả</label><textarea id="pDesc"></textarea><label>Giá</label><input id="pPrice" type="number" min="0" required><label>Giá khuyến mãi</label><input id="pSale" type="number" min="0"><label>SKU</label><input id="pSku"><label>Trạng thái</label><select id="pStatus"><option>DANG_BAN</option><option>NGUNG_BAN</option><option>HET_HANG</option></select>${!id?'<label>Số lượng tồn kho ban đầu</label><input id="pStockInit" type="number" min="0" value="0">':''}${'<label>Ảnh sản phẩm</label><input id="pImage" placeholder="Dán URL ảnh (https://...)">'}<div class="actions"><button type="button" class="btn" onclick="saveProduct()">Lưu</button><button type="button" class="btn secondary" onclick="closeModal('productModal')">Hủy</button></div></div></div></div>`);
  if(id){loadProductForm(id)}else{
  }
}
 
async function loadProductForm(id){try{const p=await api('/api/san-pham/'+id);document.getElementById('pName').value=p.tenSanPham||'';document.getElementById('pDesc').value=p.moTa||'';document.getElementById('pPrice').value=p.gia||'';document.getElementById('pSale').value=p.giaKhuyenMai||'';document.getElementById('pSku').value=p.maSku||'';document.getElementById('pStatus').value=p.trangThai||'DANG_BAN';document.getElementById('pImage').value=p.duongDanAnh||''}catch(e){notify(e.message,'error')}}
async function saveProduct(){try{const id=val('pId');const body={tenSanPham:val('pName'),moTa:val('pDesc'),gia:Number(val('pPrice')),giaKhuyenMai:val('pSale')?Number(val('pSale')):null,maSku:val('pSku')||null,trangThai:val('pStatus'),duongDanAnh:val('pImage')||null};const saved=await api(id?'/api/quan-tri/san-pham/'+id:'/api/quan-tri/san-pham',{method:id?'PUT':'POST',body:JSON.stringify(body)});
  if(!id&&saved?.id){
    const soLuongBanDau=Number(val('pStockInit')||0);
    try{await api(`/api/ton-kho/khoi-tao?sanPhamId=${saved.id}&soLuongBanDau=${soLuongBanDau}`,{method:'POST'})}
    catch(e){notify('Đã lưu sản phẩm nhưng chưa khởi tạo được tồn kho: '+e.message,'error')}
  }
  closeModal('productModal');notify('Đã lưu sản phẩm');adminProducts()}catch(e){notify(e.message,'error')}}
async function deleteProduct(id){if(!confirm('Xóa sản phẩm này?'))return;try{await api('/api/quan-tri/san-pham/'+id,{method:'DELETE'});notify('Đã xóa');adminProducts()}catch(e){notify(e.message,'error')}}
