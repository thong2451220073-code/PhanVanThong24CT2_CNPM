/* Nhan vien - xu ly bao hanh. */
const BAOHANH_LABEL={CON_HAN:'Còn hạn',HET_HAN:'Hết hạn',DANG_XU_LY:'Đang xử lý',DA_SUA_XONG:'Đã sửa xong',DA_HUY:'Đã hủy'};
/* ---------- Xu ly bao hanh ---------- */
let staffWarrantyFilter='';
async function staffWarranty(){
  try{
    const q=staffWarrantyFilter?`&trangThai=${staffWarrantyFilter}`:'';
    const d=await api(`/api/bao-hanh?size=100${q}`);
    const rows=d.content||[];
    staffUI.body().innerHTML=`<div class="panel"><div class="row"><h2>Xử lý bảo hành</h2><div>
      <select onchange="staffWarrantyFilter=this.value;staffWarranty()"><option value="">Tất cả trạng thái</option>${Object.keys(BAOHANH_LABEL).map(k=>`<option value="${k}"${staffWarrantyFilter===k?' selected':''}>${BAOHANH_LABEL[k]}</option>`).join('')}</select>
      <button class="btn secondary" onclick="staffWarranty()">↻ Làm mới</button></div></div>
      <div class="table-wrap"><table class="table"><tr><th>ID</th><th>Số seri</th><th>Sản phẩm</th><th>Khách hàng</th><th>Mã đơn</th><th>Hết hạn</th><th>Trạng thái</th><th>Cập nhật</th></tr>
      ${rows.map(r=>`<tr><td>${r.id}</td><td>${esc(r.soSeri||'-')}</td><td>${esc(r.tenSanPham||'-')}</td><td>${esc(r.tenNguoiDung||'-')}</td><td>${esc(r.maDonHang||'-')}</td><td>${esc(r.ngayKetThuc||'-')}</td><td><span class="status" title="${esc(r.moTa||'')}">${BAOHANH_LABEL[r.trangThai]||esc(r.trangThai)}</span></td>
      <td><select onchange="staffWarrantySet(${r.id},this.value)"><option value="">Chọn...</option>${['DANG_XU_LY','DA_SUA_XONG','CON_HAN','HET_HAN','DA_HUY'].filter(k=>k!==r.trangThai).map(k=>`<option value="${k}">${BAOHANH_LABEL[k]}</option>`).join('')}</select></td></tr>`).join('')||'<tr><td colspan="8" class="muted">Không có phiếu bảo hành.</td></tr>'}
      </table></div></div>`;
  }catch(e){staffUI.err(e)}
}
async function staffWarrantySet(id,tt){
  if(!tt)return;
  if(!confirm(`Chuyển trạng thái bảo hành sang "${BAOHANH_LABEL[tt]}"?`)){staffWarranty();return}
  try{await api(`/api/bao-hanh/${id}/trang-thai?trangThai=${tt}`,{method:'PATCH'});notify('Đã cập nhật bảo hành');staffWarranty()}
  catch(e){notify(e.message,'error');staffWarranty()}
}
