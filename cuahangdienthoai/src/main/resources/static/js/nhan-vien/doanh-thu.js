/* Nhan vien - thong ke doanh thu theo khoang ngay. */
/* ---------- Thong ke doanh thu ---------- */
const isoDay=d=>d.toISOString().slice(0,10);
async function staffRevenue(){
  const den=new Date(),tu=new Date();tu.setDate(den.getDate()-29);
  staffUI.body().innerHTML=`<div class="panel"><div class="row"><h2>Thống kê doanh thu</h2><div>
    Từ <input type="date" id="rvFrom" value="${isoDay(tu)}"> đến <input type="date" id="rvTo" value="${isoDay(den)}">
    <button class="btn" onclick="staffRevenueLoad()">Xem</button></div></div><div id="rvResult">${loading()}</div></div>`;
  staffRevenueLoad();
}
async function staffRevenueLoad(){
  const tu=val('rvFrom'),den=val('rvTo');
  if(!tu||!den||tu>den){notify('Khoảng ngày không hợp lệ','error');return}
  const box=document.getElementById('rvResult');box.innerHTML=loading();
  try{
    const q=`tuNgay=${tu}&denNgay=${den}`;
    const [t,ngay,top]=await Promise.all([api(`/api/thong-ke/doanh-thu?${q}`),api(`/api/thong-ke/doanh-thu/theo-ngay?${q}`),api(`/api/thong-ke/san-pham-ban-chay?${q}&soLuongTop=10`)]);
    box.innerHTML=`<div class="statgrid"><div class="stat">Tổng doanh thu<b>${money(t.tongDoanhThu)}</b></div><div class="stat">Số đơn<b>${t.soDonHang||0}</b></div><div class="stat">Giá trị TB/đơn<b>${money(t.giaTriDonHangTrungBinh)}</b></div></div>
    <div class="detail-grid"><div><h3>Doanh thu theo ngày</h3><div class="table-wrap"><table class="table"><tr><th>Ngày</th><th>Số đơn</th><th>Doanh thu</th></tr>${(ngay||[]).map(x=>`<tr><td>${esc(x.moc)}</td><td>${x.soDonHang||0}</td><td>${money(x.tongDoanhThu)}</td></tr>`).join('')||'<tr><td colspan="3" class="muted">Không có dữ liệu.</td></tr>'}</table></div></div>
    <div><h3>Sản phẩm bán chạy</h3><div class="table-wrap"><table class="table"><tr><th>Sản phẩm</th><th>SKU</th><th>Đã bán</th><th>Doanh thu</th></tr>${(top||[]).map(x=>`<tr><td>${esc(x.tenSanPham)}</td><td>${esc(x.maSku||'-')}</td><td>${x.tongSoLuongDaBan||0}</td><td>${money(x.tongDoanhThu)}</td></tr>`).join('')||'<tr><td colspan="4" class="muted">Không có dữ liệu.</td></tr>'}</table></div></div></div>`;
  }catch(e){box.innerHTML=`<div class="panel error">${esc(e.message)}</div>`}
}
