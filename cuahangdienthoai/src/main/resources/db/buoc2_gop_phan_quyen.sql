-- BUOC 2 - GOP VAI TRO VAO BANG nguoi_dung
-- BACKUP DATABASE truoc khi chay.
-- CHI CHAY KHI PROJECT DANG O BAN BUOC 2.

USE cuahangcongnghe;

-- 1. Tao cot vai_tro tam thoi.
ALTER TABLE nguoi_dung
    ADD COLUMN vai_tro VARCHAR(50) NULL;

-- 2. Chuyen vai tro cu sang cot moi.
-- Neu mot tai khoan co nhieu vai tro, uu tien theo thu tu:
-- QUAN_TRI > NHAN_VIEN_KHO > NHAN_VIEN > KHACH_HANG.
UPDATE nguoi_dung nd
SET nd.vai_tro = CASE
    WHEN EXISTS (
        SELECT 1 FROM nguoi_dung_vai_tro nvt
        JOIN vai_tro vt ON vt.id = nvt.vai_tro_id
        WHERE nvt.nguoi_dung_id = nd.id
          AND vt.ten_vai_tro = 'ROLE_QUAN_TRI'
    ) THEN 'ROLE_QUAN_TRI'
    WHEN EXISTS (
        SELECT 1 FROM nguoi_dung_vai_tro nvt
        JOIN vai_tro vt ON vt.id = nvt.vai_tro_id
        WHERE nvt.nguoi_dung_id = nd.id
          AND vt.ten_vai_tro = 'ROLE_NHAN_VIEN_KHO'
    ) THEN 'ROLE_NHAN_VIEN_KHO'
    WHEN EXISTS (
        SELECT 1 FROM nguoi_dung_vai_tro nvt
        JOIN vai_tro vt ON vt.id = nvt.vai_tro_id
        WHERE nvt.nguoi_dung_id = nd.id
          AND vt.ten_vai_tro = 'ROLE_NHAN_VIEN'
    ) THEN 'ROLE_NHAN_VIEN'
    ELSE 'ROLE_KHACH_HANG'
END;

-- 3. Bat buoc moi nguoi dung phai co vai tro.
ALTER TABLE nguoi_dung
    MODIFY COLUMN vai_tro VARCHAR(50) NOT NULL;

-- 4. Xoa cac bang RBAC cu.
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS vai_tro_quyen;
DROP TABLE IF EXISTS nguoi_dung_vai_tro;
DROP TABLE IF EXISTS quyen;
DROP TABLE IF EXISTS vai_tro;
SET FOREIGN_KEY_CHECKS = 1;

-- Kiem tra:
SELECT id, email, vai_tro FROM nguoi_dung ORDER BY id;
