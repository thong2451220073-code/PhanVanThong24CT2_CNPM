/* Trang nhan vien (#staff): khung trang + chuyen tab. Tab Don hang/Ton kho dung lai ham o quan-tri/. */
const staffUI={
  body:()=>document.getElementById('staffBody'),
  err(e){this.body().innerHTML=`<div class="panel error">${esc(e.message)}</div>`},
  mark(tab){document.querySelectorAll('#staffTabs button').forEach(b=>b.classList.toggle('active',b.dataset.tab===tab))}
};

async function staff(){
  if(!Phien.isStaff()){notify('Bạn không có quyền truy cập','error');location.href='/';return}
  document.getElementById('app').innerHTML=shell(`<main class="container"><h1>🧰 Quản lý cửa hàng</h1>
    <div class="admin-tabs" id="staffTabs">
      <button data-tab="orders" onclick="staffTab('orders')">Đơn hàng</button>
      <button data-tab="returns" onclick="staffTab('returns')">Đổi trả</button>
      <button data-tab="warranty" onclick="staffTab('warranty')">Bảo hành</button>
      <button data-tab="revenue" onclick="staffTab('revenue')">Doanh thu</button>
      <button data-tab="inventory" onclick="staffTab('inventory')">Tồn kho</button>
      <button data-tab="stockin" onclick="staffTab('stockin')">Nhập kho</button>
      <button data-tab="stockout" onclick="staffTab('stockout')">Xuất kho</button>
    </div><div id="staffBody"></div></main>`);
  staffTab('orders');
}

function staffTab(tab){
  staffUI.mark(tab);
  const b=staffUI.body();
  b.innerHTML=loading();
  // adminOrders/adminInventory (app.js) ghi vao #adminBody -> boc trong 1 div co id do
  const reuse=()=>{b.innerHTML='<div id="adminBody"></div>'};
  switch(tab){
    case 'orders':reuse();return adminOrders();
    case 'inventory':reuse();return adminInventory();
    case 'returns':return staffReturns();
    case 'warranty':return staffWarranty();
    case 'revenue':return staffRevenue();
    case 'stockin':return staffPhieu('nhap');
    case 'stockout':return staffPhieu('xuat');
  }
}
