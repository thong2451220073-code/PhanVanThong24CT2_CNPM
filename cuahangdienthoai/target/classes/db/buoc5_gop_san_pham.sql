-- BUOC 5 - GOP HINH ANH + BIEN THE VAO MO HINH SAN PHAM
-- BACKUP DATABASE truoc khi chay.

USE cuahangcongnghe;

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Them cot anh dai dien neu chua co.
ALTER TABLE san_pham
    ADD COLUMN IF NOT EXISTS duong_dan_anh VARCHAR(2000) NULL;

-- 2. Giữ anh chinh dau tien cho moi san pham.
UPDATE san_pham sp
LEFT JOIN (
    SELECT h1.san_pham_id, h1.duong_dan_anh
    FROM hinh_anh_san_pham h1
    INNER JOIN (
        SELECT san_pham_id, MIN(id) AS id
        FROM hinh_anh_san_pham
        WHERE la_anh_chinh = TRUE
        GROUP BY san_pham_id
    ) x ON x.id = h1.id
) anh ON anh.san_pham_id = sp.id
SET sp.duong_dan_anh = anh.duong_dan_anh
WHERE anh.duong_dan_anh IS NOT NULL;

-- 3. Xoa quan he bien the khoi cac bang dang dung.
ALTER TABLE ton_kho DROP COLUMN IF EXISTS bien_the_id;
ALTER TABLE chi_tiet_phieu_kho DROP COLUMN IF EXISTS bien_the_id;

-- 4. Xoa hai bang phu.
DROP TABLE IF EXISTS hinh_anh_san_pham;
DROP TABLE IF EXISTS bien_the_san_pham;

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- 5. Kiem tra.
SELECT COUNT(*) AS so_san_pham FROM san_pham;
SELECT COUNT(*) AS so_ton_kho FROM ton_kho;
