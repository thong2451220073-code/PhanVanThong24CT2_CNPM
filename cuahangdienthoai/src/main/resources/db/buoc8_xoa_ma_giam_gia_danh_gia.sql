-- BUOC 8: Xoa hoan toan chuc nang Ma giam gia (marketing) va Danh gia san pham
-- Chay sau khi da thay source code cua BUOC 8.

SET FOREIGN_KEY_CHECKS = 0;

-- ===== Danh gia san pham =====
-- Bang danh_gia tu tham chieu toi san_pham/nguoi_dung/don_hang, khong co bang nao
-- khac tham chieu NGUOC lai danh_gia nen co the drop thang bang.
DROP TABLE IF EXISTS danh_gia;

-- Cot da_danh_gia chi la cot boolean thuong, khong phai khoa ngoai.
ALTER TABLE chi_tiet_don_hang DROP COLUMN da_danh_gia;

-- ===== Ma giam gia (marketing) =====
DROP TABLE IF EXISTS ma_giam_gia;

-- 2 cot nay tren don_hang chi la du lieu thuong (String/BigDecimal),
-- khong phai khoa ngoai toi ma_giam_gia nen khong can go FK truoc khi xoa cot.
ALTER TABLE don_hang DROP COLUMN ma_giam_gia_ap_dung;
ALTER TABLE don_hang DROP COLUMN tien_giam_gia;

SET FOREIGN_KEY_CHECKS = 1;
