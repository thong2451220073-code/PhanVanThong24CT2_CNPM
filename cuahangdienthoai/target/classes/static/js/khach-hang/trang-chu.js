/* Trang chu: banner, san pham ban chay, theo dong iPhone, cau hoi thuong gap. */
const FAQ_DATA=[
 ['Mua iPhone tại đây có được bảo hành không?','Tất cả sản phẩm đều được bảo hành 12 tháng, hỗ trợ 1 đổi 1 nếu lỗi do nhà sản xuất trong thời gian bảo hành.'],
 ['Có hỗ trợ trả góp không?','Có. Hỗ trợ trả góp 0% lãi suất qua thẻ tín dụng hoặc công ty tài chính, hồ sơ duyệt nhanh trong ngày.'],
 ['Sản phẩm có phải hàng chính hãng không?','100% sản phẩm chính hãng, đầy đủ hoá đơn và tem nhãn, được kiểm tra kỹ trước khi giao cho khách.'],
 ['Có được đổi trả nếu không ưng ý?','Hỗ trợ đổi trả trong 30 ngày nếu máy còn nguyên vẹn, chưa kích hoạt bảo hành và đủ điều kiện đổi trả.'],
 ['Cửa hàng có thu cũ đổi mới không?','Có. Hỗ trợ định giá và thu lại máy cũ, trợ giá trực tiếp khi lên đời sang máy mới tại cửa hàng.']
];
function seriesOf(name){const m=String(name||'').match(/iPhone\s*(\d+)/i);return m?('iPhone '+m[1]):'Sản phẩm khác'}
function topCard(p,rank){const img=productImg(p);return `<article class="card top-card" onclick="detail(${p.id})">${rank?`<span class="top-rank">Top ${rank}</span>`:''}<div class="product-img">${img?`<img src="${esc(img)}" onerror="this.replaceWith(Object.assign(document.createElement('span'),{className:'placeholder',textContent:'📱'}))">`:'<span class="placeholder">📱</span>'}</div><div class="card-body"><h3>${esc(p.tenSanPham)}</h3><p class="price" style="font-size:15px">${money(p.giaKhuyenMai||p.gia)}</p></div></article>`}
async function loadTopSellers(){const box=document.getElementById('topSellers');if(!box)return;try{const d=await loadProducts();const list=(d.content||[]).slice(0,10);box.innerHTML=list.map((p,i)=>topCard(p,i+1)).join('')||'<div class="panel">Chưa có sản phẩm.</div>'}catch(e){box.innerHTML=`<div class="panel error">${esc(e.message)}</div>`}}
async function loadSeriesRows(){const box=document.getElementById('seriesRows');if(!box)return;try{const d=await api('/api/san-pham?size=200');const items=d.content||[];const groups={};items.forEach(p=>{const s=seriesOf(p.tenSanPham);(groups[s]=groups[s]||[]).push(p)});const order=Object.keys(groups).sort((a,b)=>(parseInt(String(b).replace(/\D/g,''))||0)-(parseInt(String(a).replace(/\D/g,''))||0));box.innerHTML=order.map(s=>`<div class="series-row"><div class="series-head"><h3>${esc(s)} Series</h3><a href="#" onclick="goSeries('${esc(s)}');return false">Xem tất cả →</a></div><div class="scroll-row">${groups[s].map(p=>topCard(p)).join('')}</div></div>`).join('')||'<div class="panel">Chưa có sản phẩm.</div>'}catch(e){box.innerHTML=`<div class="panel error">${esc(e.message)}</div>`}}
function initFaq(){document.querySelectorAll('.faq-q').forEach(q=>q.onclick=()=>q.parentElement.classList.toggle('open'))}
async function home(){
 document.getElementById('app').innerHTML=shell(`
 <div class="page">
  <main>
   <section class="hero-wrap"><div class="container">
    <div class="hero-banner">
     <div class="hero-copy"><span class="hero-tag">IPHONE STORE</span><h1>Sở hữu iPhone chính hãng<br><strong>Giá tốt nhất!</strong></h1><p>Công nghệ đỉnh cao &nbsp;•&nbsp; Bảo hành chính hãng &nbsp;•&nbsp; Hỗ trợ tận tâm</p><button class="btn hero-btn" onclick="document.getElementById('products-section').scrollIntoView({behavior:'smooth'})">Khám phá ngay →</button></div>
     <div class="hero-empty" aria-hidden="true"></div>
    </div>
    <div class="badge-strip home-services"><div class="b-item"><span>🛡️</span><div><b>Sản phẩm chính hãng</b><small>Cam kết 100% hàng chính hãng</small></div></div><div class="b-item"><span>🚚</span><div><b>Giao hàng nhanh</b><small>Toàn quốc, nhận hàng kiểm tra</small></div></div><div class="b-item"><span>🛡️</span><div><b>Bảo hành uy tín</b><small>Hỗ trợ bảo hành chính hãng</small></div></div><div class="b-item"><span>🎧</span><div><b>Hỗ trợ 24/7</b><small>Tư vấn miễn phí mọi lúc</small></div></div></div>
   </div></section>

 
   <section class="section"><div class="container"><div class="quick-title"><h2>SẢN PHẨM BÁN CHẠY</h2><span>Được khách hàng lựa chọn nhiều nhất</span></div><div id="topSellers" class="scroll-row">${loading('Đang tải...')}</div></div></section>
 
   <section class="section products-section" id="products-section"><div class="container"><div class="section-head"><div><h2>TÌM KIẾM SẢN PHẨM</h2><p>iPhone chính hãng — lựa chọn theo nhu cầu và ngân sách</p></div><button class="outline-btn" onclick="filterAll()">Xem tất cả →</button></div>
    <div class="toolbar"><input id="search" placeholder="Tìm iPhone, ví dụ: iPhone 16 Pro Max" onkeydown="if(event.key==='Enter')searchProducts()"><input id="minPrice" type="number" placeholder="Giá từ"><input id="maxPrice" type="number" placeholder="Giá đến"><button class="btn" onclick="searchProducts()">Tìm kiếm</button></div>
    <div id="products">${loading('Đang tải iPhone...')}</div>
   </div></section>

  </main>
  <footer class="footer"><div class="footer-grid"><div><div class="footer-brand"> iPhoneStore</div><p>Chuyên iPhone chính hãng. Giá tốt, bảo hành rõ ràng, hỗ trợ trả góp và thu cũ đổi mới.</p></div><div><h3>SẢN PHẨM</h3><p>iPhone 17 Series<br>iPhone 16 Series<br>iPhone 15 Series<br>iPhone 14 Series</p></div><div><h3>HỖ TRỢ KHÁCH HÀNG</h3><p>Chính sách bảo hành<br>Chính sách đổi trả<br>Hướng dẫn mua hàng<br>Thanh toán</p></div><div><h3>LIÊN HỆ</h3><p>Hotline: ${HOTLINE}<br>08:00 – 22:00 mỗi ngày<br>Zalo: <a href="${ZALO_LINK}" target="_blank" rel="noopener">Nhắn Zalo</a></p></div></div><div class="copyright">© 2026 iPhoneStore — Chuyên iPhone</div></footer>
 </div>`);
 await refreshProducts();
 loadTopSellers();
}
