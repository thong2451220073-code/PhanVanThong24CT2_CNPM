/* Nhan vien - phieu nhap kho / xuat kho. */
const PHIEU_LABEL={CHO_XU_LY:'Chờ xử lý',DA_NHAP_KHO:'Đã nhập kho',DA_XUAT_KHO:'Đã xuất kho',DA_HUY:'Đã hủy'};
const LYDO_XUAT_LABEL={GIAO_DON_HANG:'Giao đơn hàng',HANG_LOI_HONG:'Hàng lỗi hỏng',CHUYEN_KHO:'Chuyển kho',KIEM_KE_DIEU_CHINH:'Kiểm kê điều chỉnh',KHAC:'Khác'};
/* ---------- Nhap kho / Xuat kho (phieu kho) ---------- */
let staffPhieuKind='nhap',staffPhieuProducts=[];
async function staffPhieu(kind){
  staffPhieuKind=kind;const nhap=kind==='nhap';
  try{
    const [list,sp]=await Promise.all([api(nhap?'/api/nhap-kho':'/api/xuat-kho'),api('/api/san-pham?size=100')]);
    staffPhieuProducts=sp.content||[];
    const rows=(list||[]).slice().sort((a,b)=>String(b.ngayTao).localeCompare(String(a.ngayTao)));
    staffUI.body().innerHTML=`<div class="panel"><div class="row"><h2>${nhap?'Nhập kho':'Xuất kho'}</h2><div>
      <button class="btn" onclick="staffPhieuForm()">+ Tạo phiếu ${nhap?'nhập':'xuất'}</button> <button class="btn secondary" onclick="staffPhieu('${kind}')">↻ Làm mới</button></div></div>
      <div class="table-wrap"><table class="table"><tr><th>Mã phiếu</th><th>${nhap?'Nhà cung cấp':'Lý do xuất'}</th><th>Sản phẩm</th><th>Tổng tiền</th><th>Trạng thái</th><th>Ngày tạo</th><th>Thao tác</th></tr>
      ${rows.map(p=>`<tr><td><b>${esc(p.maPhieu)}</b></td><td>${nhap?esc(p.nhaCungCap||'-'):(LYDO_XUAT_LABEL[p.lyDoXuat]||esc(p.lyDoXuat||'-'))}</td>
      <td>${(p.danhSachChiTiet||[]).map(c=>`${esc(c.tenSanPham)} ×${c.soLuong}`).join('<br>')}</td><td>${money(p.tongTien)}</td><td><span class="status">${PHIEU_LABEL[p.trangThai]||esc(p.trangThai)}</span></td><td>${esc(p.ngayTao||'-')}</td>
      <td>${p.trangThai==='CHO_XU_LY'?`<button class="link-btn" onclick="staffPhieuAct('${esc(p.maPhieu)}','xac-nhan')">Xác nhận</button> <button class="link-btn danger-text" onclick="staffPhieuAct('${esc(p.maPhieu)}','huy')">Hủy</button>`:'-'}</td></tr>`).join('')||'<tr><td colspan="7" class="muted">Chưa có phiếu nào.</td></tr>'}
      </table></div></div>`;
  }catch(e){staffUI.err(e)}
}
async function staffPhieuAct(ma,act){
  const nhap=staffPhieuKind==='nhap';
  if(!confirm(`${act==='huy'?'Hủy':'Xác nhận'} phiếu ${ma}?`))return;
  try{await api(`/api/${nhap?'nhap-kho':'xuat-kho'}/${encodeURIComponent(ma)}/${act}`,{method:'POST'});notify(act==='huy'?'Đã hủy phiếu':'Đã xác nhận phiếu');staffPhieu(staffPhieuKind)}
  catch(e){notify(e.message,'error')}
}
function staffLineHtml(){
  const nhap=staffPhieuKind==='nhap';
  return `<div class="row staff-line" style="gap:8px;margin-bottom:6px"><select class="pl-sp">${staffPhieuProducts.map(p=>`<option value="${p.id}">${esc(p.tenSanPham)}</option>`).join('')}</select>
    <input class="pl-qty" type="number" min="1" value="1" style="width:80px" title="Số lượng"><input class="pl-price" type="number" min="0" placeholder="${nhap?'Đơn giá nhập':'Đơn giá (tuỳ chọn)'}" style="width:150px">
    <button type="button" class="link-btn danger-text" onclick="this.closest('.staff-line').remove()">✕</button></div>`;
}
function staffPhieuAddLine(){document.getElementById('plLines').insertAdjacentHTML('beforeend',staffLineHtml())}
function staffPhieuForm(){
  const nhap=staffPhieuKind==='nhap';
  document.getElementById('staffModal')?.remove();
  document.body.insertAdjacentHTML('beforeend',`<div class="modal open" id="staffModal"><div class="modalbox"><h2>Tạo phiếu ${nhap?'nhập':'xuất'} kho</h2><div class="form">
    ${nhap?`<label>Nhà cung cấp</label><input id="plSupplier" placeholder="VD: Apple Việt Nam">`
    :`<label>Lý do xuất</label><select id="plReason">${Object.keys(LYDO_XUAT_LABEL).map(k=>`<option value="${k}">${LYDO_XUAT_LABEL[k]}</option>`).join('')}</select><label>ID đơn hàng (nếu xuất để giao đơn)</label><input id="plOrder" type="number" min="1">`}
    <label>Ghi chú</label><input id="plNote">
    <label>Sản phẩm</label><div id="plLines">${staffLineHtml()}</div>
    <button type="button" class="link-btn" onclick="staffPhieuAddLine()">+ Thêm dòng</button>
    <div class="actions"><button type="button" class="btn" onclick="staffPhieuSave()">Tạo phiếu</button><button type="button" class="btn secondary" onclick="closeModal('staffModal')">Hủy</button></div></div></div></div>`);
}
async function staffPhieuSave(){
  const nhap=staffPhieuKind==='nhap';
  const lines=[...document.querySelectorAll('#plLines .staff-line')].map(l=>{
    const price=l.querySelector('.pl-price').value;
    return {sanPhamId:Number(l.querySelector('.pl-sp').value),soLuong:parseInt(l.querySelector('.pl-qty').value,10),donGia:price?Number(price):null}
  });
  if(!lines.length||lines.some(x=>!x.soLuong||x.soLuong<=0)){notify('Vui lòng nhập số lượng hợp lệ','error');return}
  if(nhap&&lines.some(x=>!x.donGia||x.donGia<=0)){notify('Phiếu nhập cần đơn giá nhập cho mọi dòng','error');return}
  const body={ghiChu:val('plNote')||null,danhSachChiTiet:lines};
  if(nhap)body.nhaCungCap=val('plSupplier')||null;
  else{body.lyDoXuat=val('plReason');if(val('plOrder'))body.donHangId=Number(val('plOrder'))}
  try{await api(nhap?'/api/nhap-kho':'/api/xuat-kho',{method:'POST',body:JSON.stringify(body)});closeModal('staffModal');notify('Đã tạo phiếu, hãy xác nhận để cập nhật tồn kho');staffPhieu(staffPhieuKind)}
  catch(e){notify(e.message,'error')}
}
