/* Khoi dong: dieu huong theo URL/hash (#cart, #admin, #staff...). PHAI nap cuoi cung. */
function route(){const p=location.pathname;if(p==='/login.html'){login();return}if(p==='/register.html'){register();return}const h=location.hash;try{if(h==='#cart')return cart();if(h==='#checkout')return checkout();if(h==='#orders')return orders();if(h==='#addresses')return addresses();if(h==='#admin')return admin();if(h==='#staff')return staff();home()}catch(e){console.error(e)}}
window.addEventListener('hashchange',route);window.addEventListener('DOMContentLoaded',route);
