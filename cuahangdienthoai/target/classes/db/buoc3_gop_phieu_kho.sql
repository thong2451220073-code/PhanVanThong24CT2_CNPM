-- BUOC 3 - GOP PHIEU NHAP KHO + PHIEU XUAT KHO
-- Gop 4 bang:
--   phieu_nhap_kho + chi_tiet_phieu_nhap_kho
--   phieu_xuat_kho + chi_tiet_phieu_xuat_kho
-- thanh 2 bang:
--   phieu_kho + chi_tiet_phieu_kho
-- BACKUP DATABASE TRUOC KHI CHAY.
-- CHI CHAY KHI PROJECT BUOC 3 CHUA DUOC START.

USE cuahangcongnghe;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS phieu_kho (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ma_phieu VARCHAR(50) NOT NULL,
    loai_phieu VARCHAR(10) NOT NULL,
    nha_cung_cap VARCHAR(255) NULL,
    ly_do_xuat VARCHAR(30) NULL,
    don_hang_id BIGINT NULL,
    nguoi_tao_id BIGINT NULL,
    tong_tien DECIMAL(15,2) NULL,
    ghi_chu TEXT NULL,
    trang_thai VARCHAR(20) NULL,
    ngay_tao DATETIME NULL,
    ngay_hoan_thanh DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_phieu_kho_ma_phieu (ma_phieu),
    KEY idx_phieu_kho_nguoi_tao (nguoi_tao_id),
    CONSTRAINT fk_phieu_kho_nguoi_tao FOREIGN KEY (nguoi_tao_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE IF NOT EXISTS chi_tiet_phieu_kho (
    id BIGINT NOT NULL AUTO_INCREMENT,
    phieu_kho_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    bien_the_id BIGINT NULL,
    so_luong INT NOT NULL,
    don_gia DECIMAL(15,2) NULL,
    thanh_tien DECIMAL(15,2) NULL,
    PRIMARY KEY (id),
    KEY idx_ct_phieu_kho_phieu (phieu_kho_id),
    KEY idx_ct_phieu_kho_san_pham (san_pham_id),
    KEY idx_ct_phieu_kho_bien_the (bien_the_id),
    CONSTRAINT fk_ct_phieu_kho_phieu FOREIGN KEY (phieu_kho_id) REFERENCES phieu_kho(id),
    CONSTRAINT fk_ct_phieu_kho_san_pham FOREIGN KEY (san_pham_id) REFERENCES san_pham(id),
    CONSTRAINT fk_ct_phieu_kho_bien_the FOREIGN KEY (bien_the_id) REFERENCES bien_the_san_pham(id)
);

-- Bao toan ID cua phieu nhap.
INSERT INTO phieu_kho
(id, ma_phieu, loai_phieu, nha_cung_cap, ly_do_xuat, don_hang_id, nguoi_tao_id,
 tong_tien, ghi_chu, trang_thai, ngay_tao, ngay_hoan_thanh)
SELECT
    id, ma_phieu, 'NHAP', nha_cung_cap, NULL, NULL, nguoi_tao_id,
    tong_tien, ghi_chu, trang_thai, ngay_tao, ngay_hoan_thanh
FROM phieu_nhap_kho;

-- Phieu xuat dung ID offset de tranh trung ID voi phieu nhap.
INSERT INTO phieu_kho
(id, ma_phieu, loai_phieu, nha_cung_cap, ly_do_xuat, don_hang_id, nguoi_tao_id,
 tong_tien, ghi_chu, trang_thai, ngay_tao, ngay_hoan_thanh)
SELECT
    1000000000 + id, ma_phieu, 'XUAT', NULL, ly_do_xuat, don_hang_id, nguoi_tao_id,
    tong_gia_tri, ghi_chu, trang_thai, ngay_tao, ngay_hoan_thanh
FROM phieu_xuat_kho;

-- Chi tiet phieu nhap: giu ID cu.
INSERT INTO chi_tiet_phieu_kho
(id, phieu_kho_id, san_pham_id, bien_the_id, so_luong, don_gia, thanh_tien)
SELECT
    id, phieu_nhap_kho_id, san_pham_id, bien_the_id, so_luong, don_gia, thanh_tien
FROM chi_tiet_phieu_nhap_kho;

-- Chi tiet phieu xuat: dung ID va phieu_kho_id co offset.
INSERT INTO chi_tiet_phieu_kho
(id, phieu_kho_id, san_pham_id, bien_the_id, so_luong, don_gia, thanh_tien)
SELECT
    1000000000 + id, 1000000000 + phieu_xuat_kho_id,
    san_pham_id, bien_the_id, so_luong, don_gia, thanh_tien
FROM chi_tiet_phieu_xuat_kho;

-- Dat AUTO_INCREMENT cao hon ID da migrate.
SET @max_phieu = (SELECT COALESCE(MAX(id), 0) + 1 FROM phieu_kho);
SET @sql1 = CONCAT('ALTER TABLE phieu_kho AUTO_INCREMENT = ', @max_phieu);
PREPARE stmt1 FROM @sql1;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;

SET @max_ct = (SELECT COALESCE(MAX(id), 0) + 1 FROM chi_tiet_phieu_kho);
SET @sql2 = CONCAT('ALTER TABLE chi_tiet_phieu_kho AUTO_INCREMENT = ', @max_ct);
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- Xoa 4 bang cu sau khi da migrate.
DROP TABLE IF EXISTS chi_tiet_phieu_nhap_kho;
DROP TABLE IF EXISTS chi_tiet_phieu_xuat_kho;
DROP TABLE IF EXISTS phieu_nhap_kho;
DROP TABLE IF EXISTS phieu_xuat_kho;

SET FOREIGN_KEY_CHECKS = 1;

-- Kiem tra ket qua.
SELECT loai_phieu, COUNT(*) AS so_phieu
FROM phieu_kho
GROUP BY loai_phieu;

SELECT COUNT(*) AS so_chi_tiet
FROM chi_tiet_phieu_kho;
