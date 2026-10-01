/* Nhan vien - xu ly yeu cau doi tra. */
const DOITRA_LABEL={CHO_DUYET:'Chờ duyệt',DA_DUYET:'Đã duyệt',TU_CHOI:'Từ chối',DANG_HOAN_TRA:'Đang hoàn trả',DA_HOAN_TIEN:'Đã hoàn tiền',DA_HOAN_THANH:'Đã hoàn thành',DA_HUY:'Đã hủy'};
const LOAI_DOITRA_LABEL={DOI_HANG:'Đổi hàng',TRA_HANG_HOAN_TIEN:'Trả hàng hoàn tiền'};
/* ---------- Xu ly doi tra ---------- */
let staffReturnFilter='',staffReturnRows=[];
async function staffReturns(){
  try{
    const q=staffReturnFilter?`&trangThai=${staffReturnFilter}`:'';
    const d=await api(`/api/doi-tra?size=100&sort=ngayTao,desc${q}`);
    staffReturnRows=d.content||[];
    staffUI.body().innerHTML=`<div class="panel"><div class="row"><h2>Xử lý đổi trả</h2><div>
      <select onchange="staffReturnFilter=this.value;staffReturns()"><option value="">Tất cả trạng thái</option>${Object.keys(DOITRA_LABEL).map(k=>`<option value="${k}"${staffReturnFilter===k?' selected':''}>${DOITRA_LABEL[k]}</option>`).join('')}</select>
      <button class="btn secondary" onclick="staffReturns()">↻ Làm mới</button></div></div>
      <div class="table-wrap"><table class="table"><tr><th>Mã yêu cầu</th><th>Mã đơn</th><th>Sản phẩm</th><th>Loại</th><th>SL</th><th>Lý do</th><th>Trạng thái</th><th>Ngày tạo</th><th></th></tr>
      ${staffReturnRows.map((r,i)=>`<tr><td><b>${esc(r.maYeuCau)}</b></td><td>${esc(r.maDonHang||'-')}</td><td>${esc(r.tenSanPham||'-')}</td><td>${LOAI_DOITRA_LABEL[r.loaiYeuCau]||esc(r.loaiYeuCau)}</td><td>${r.soLuong||0}</td><td>${esc(r.lyDo||'-')}</td><td><span class="status" title="${esc(r.ghiChuXuLy||'')}">${DOITRA_LABEL[r.trangThai]||esc(r.trangThai)}</span></td><td>${esc(r.ngayTao||'-')}</td><td>${['DA_HOAN_THANH','DA_HOAN_TIEN','TU_CHOI','DA_HUY'].includes(r.trangThai)?'-':`<button class="link-btn" onclick="staffReturnForm(${i})">Xử lý</button>`}</td></tr>`).join('')||'<tr><td colspan="9" class="muted">Không có yêu cầu nào.</td></tr>'}
      </table></div></div>`;
  }catch(e){staffUI.err(e)}
}
function staffReturnForm(i){
  const r=staffReturnRows[i];if(!r)return;
  document.getElementById('staffModal')?.remove();
  const opts=['DA_DUYET','TU_CHOI','DANG_HOAN_TRA','DA_HOAN_TIEN','DA_HOAN_THANH'];
  document.body.insertAdjacentHTML('beforeend',`<div class="modal open" id="staffModal"><div class="modalbox"><h2>Xử lý ${esc(r.maYeuCau)}</h2>
    <p class="muted">${esc(r.tenSanPham||'')} · ${LOAI_DOITRA_LABEL[r.loaiYeuCau]||''} · SL ${r.soLuong||0}</p><p>Lý do: ${esc(r.lyDo||'-')}</p>
    <div class="form"><label>Trạng thái mới</label><select id="drStatus">${opts.map(o=>`<option value="${o}">${DOITRA_LABEL[o]}</option>`).join('')}</select>
    <label>Số tiền hoàn trả (nếu có)</label><input id="drMoney" type="number" min="0" placeholder="VD: 20000000">
    <label>Ghi chú xử lý</label><textarea id="drNote" rows="3"></textarea>
    <div class="actions"><button type="button" class="btn" onclick="staffReturnSave(${r.id})">Lưu</button><button type="button" class="btn secondary" onclick="closeModal('staffModal')">Hủy</button></div></div></div></div>`);
}
async function staffReturnSave(id){
  try{
    const body={trangThai:val('drStatus'),ghiChuXuLy:val('drNote')||null};
    if(val('drMoney'))body.soTienHoanTra=Number(val('drMoney'));
    await api(`/api/doi-tra/${id}/xu-ly`,{method:'PATCH',body:JSON.stringify(body)});
    closeModal('staffModal');notify('Đã cập nhật yêu cầu');staffReturns();
  }catch(e){notify(e.message,'error')}
}
