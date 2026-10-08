-- =====================================================================
-- CUA HANG DIEN THOAI - DATABASE DEMO (11 BANG) THEO SO DO USE CASE
-- =====================================================================
-- Actor -> vai_tro:  Admin = ROLE_QUAN_TRI | NhanVien = ROLE_NHAN_VIEN
--                    KhachHang = ROLE_KHACH_HANG
--
-- Use case -> bang:
--   dang_ky, dang_nhap, quan_ly_nguoi_dung ........ nguoi_dung
--   quan_ly_san_pham, xem/tim kiem/chi tiet SP,
--   quan_ly_danh_gia (dinh gia: gia, gia_khuyen_mai) san_pham
--   quan_ly_dia_chi ............................... dia_chi
--   quan_ly_gio_hang .............................. chi_tiet_gio_hang
--   dat_hang, xem_don_hang, huy_don_hang,
--   quan_ly_don_hang, thong_ke_doanh_thu .......... don_hang, chi_tiet_don_hang
--   thanh_toan, quan_ly_thanh_toan ................ thanh_toan
--   xu_ly_doi_tra ................................. yeu_cau_doi_tra
--   xu_ly_bao_hanh ................................ bao_hanh
--   quan_ly_ton_kho ............................... ton_kho
--   nhap_kho, xuat_kho ............................ phieu_kho
--
-- Cach chay:  mysql -u root -p < database_schema_demo.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS cuahangdienthoai
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cuahangdienthoai;

SET FOREIGN_KEY_CHECKS = 0;

-- Xoa cac bang cu khong con dung (neu database cu con ton tai)
DROP TABLE IF EXISTS danh_muc;
DROP TABLE IF EXISTS gio_hang;
DROP TABLE IF EXISTS chi_tiet_phieu_kho;

-- ---------------------------------------------------------------------
-- 1. NGUOI_DUNG  (Entity: NguoiDung)
--    Vai tro duoc luu truc tiep tren nguoi_dung (khong con bang RBAC rieng)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS nguoi_dung;
CREATE TABLE nguoi_dung (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    ho_ten          VARCHAR(150)    NOT NULL,
    email           VARCHAR(150)    NOT NULL,
    mat_khau        VARCHAR(255)    NOT NULL,
    so_dien_thoai   VARCHAR(20)     NULL,
    kich_hoat       BOOLEAN         NOT NULL DEFAULT TRUE,
    ngay_tao        DATETIME        NOT NULL,
    vai_tro         VARCHAR(50)     NOT NULL DEFAULT 'ROLE_KHACH_HANG',
    PRIMARY KEY (id),
    UNIQUE KEY uk_nguoi_dung_email (email),
    CONSTRAINT ck_nguoi_dung_vai_tro CHECK (
        vai_tro IN ('ROLE_KHACH_HANG', 'ROLE_NHAN_VIEN', 'ROLE_QUAN_TRI')
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. SAN_PHAM  (Entity: SanPham)
--    - Da gop hinh anh (duong_dan_anh) va thong tin bien the vao san pham
--    - Khong su dung thuong_hieu_id
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS san_pham;
CREATE TABLE san_pham (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    ten_san_pham    VARCHAR(255)    NOT NULL,
    mo_ta           TEXT            NULL,
    gia             DECIMAL(15,2)   NOT NULL,
    gia_khuyen_mai  DECIMAL(15,2)   NULL,
    ma_sku          VARCHAR(100)    NULL,
    trang_thai      VARCHAR(30)     NOT NULL DEFAULT 'DANG_BAN',
    duong_dan_anh   VARCHAR(2000)   NULL,
    ngay_tao        DATETIME        NULL,
    ngay_cap_nhat   DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_san_pham_sku (ma_sku),
    KEY idx_san_pham_trang_thai (trang_thai)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. DIA_CHI  (Entity: DiaChi) - so dia chi giao hang cua nguoi dung
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS dia_chi;
CREATE TABLE dia_chi (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nguoi_dung_id       BIGINT UNSIGNED NOT NULL,
    ho_ten_nguoi_nhan   VARCHAR(100)    NOT NULL,
    so_dien_thoai       VARCHAR(15)     NOT NULL,
    dia_chi_chi_tiet    TEXT            NOT NULL,
    phuong_xa           VARCHAR(100)    NOT NULL,
    quan_huyen          VARCHAR(100)    NOT NULL,
    tinh_thanh          VARCHAR(100)    NOT NULL,
    loai_dia_chi        VARCHAR(20)     NOT NULL DEFAULT 'NHA',
    la_mac_dinh         BOOLEAN         NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    KEY idx_dia_chi_nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_dia_chi_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. CHI_TIET_GIO_HANG (da gop gio_hang) - moi dong = 1 san pham trong
--    gio cua 1 nguoi dung
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS gio_hang;
DROP TABLE IF EXISTS chi_tiet_gio_hang;
CREATE TABLE chi_tiet_gio_hang (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nguoi_dung_id   BIGINT UNSIGNED NOT NULL,
    san_pham_id     BIGINT UNSIGNED NOT NULL,
    so_luong        INT             NOT NULL,
    da_chon         BOOLEAN         NOT NULL DEFAULT TRUE,
    ngay_them       DATETIME        NULL,
    ngay_cap_nhat   DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ctgh_nguoi_dung_san_pham (nguoi_dung_id, san_pham_id),
    KEY idx_ctgh_san_pham (san_pham_id),
    CONSTRAINT fk_ctgh_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id),
    CONSTRAINT fk_ctgh_san_pham FOREIGN KEY (san_pham_id) REFERENCES san_pham (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 7. TON_KHO  (Entity: TonKho) - moi san pham co dung 1 dong ton kho
--    (khong su dung bien_the_id)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS ton_kho;
CREATE TABLE ton_kho (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    san_pham_id         BIGINT UNSIGNED NOT NULL,
    so_luong_ton        INT             NOT NULL DEFAULT 0,
    so_luong_dat_truoc  INT             NOT NULL DEFAULT 0,
    muc_ton_toi_thieu   INT             NULL DEFAULT 5,
    vi_tri_kho          VARCHAR(100)    NULL,
    ngay_cap_nhat       DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ton_kho_san_pham (san_pham_id),
    CONSTRAINT fk_ton_kho_san_pham FOREIGN KEY (san_pham_id) REFERENCES san_pham (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 8. DON_HANG  (Entity: DonHang)
--    (khong su dung ma_giam_gia_ap_dung / tien_giam_gia)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS don_hang;
CREATE TABLE don_hang (
    id                          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    ma_don_hang                 VARCHAR(30)     NOT NULL,
    nguoi_dung_id               BIGINT UNSIGNED NOT NULL,
    ho_ten_nguoi_nhan           VARCHAR(100)    NOT NULL,
    so_dien_thoai_nguoi_nhan    VARCHAR(15)     NOT NULL,
    dia_chi_giao_hang           TEXT            NOT NULL,
    tong_tien_hang              DECIMAL(15,2)   NOT NULL,
    phi_van_chuyen              DECIMAL(15,2)   NOT NULL DEFAULT 0,
    tong_thanh_toan             DECIMAL(15,2)   NOT NULL,
    phuong_thuc_thanh_toan      VARCHAR(30)     NOT NULL,
    da_thanh_toan                BOOLEAN         NOT NULL DEFAULT FALSE,
    trang_thai                  VARCHAR(30)     NOT NULL,
    ghi_chu                     TEXT            NULL,
    ly_do_huy                   VARCHAR(255)    NULL,
    nhan_vien_tao_id             BIGINT UNSIGNED NULL,
    ngay_tao                    DATETIME        NOT NULL,
    ngay_cap_nhat                DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_don_hang_ma (ma_don_hang),
    KEY idx_don_hang_nguoi_dung (nguoi_dung_id),
    KEY idx_don_hang_trang_thai (trang_thai),
    CONSTRAINT fk_don_hang_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id),
    CONSTRAINT ck_don_hang_trang_thai CHECK (
        trang_thai IN ('CHO_XAC_NHAN','DA_XAC_NHAN','DANG_CHUAN_BI','DANG_GIAO','DA_GIAO','DA_HUY','TRA_HANG')
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 9. CHI_TIET_DON_HANG  (Entity: ChiTietDonHang)
--    (khong su dung cot da_danh_gia)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS chi_tiet_don_hang;
CREATE TABLE chi_tiet_don_hang (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    don_hang_id         BIGINT UNSIGNED NOT NULL,
    san_pham_id         BIGINT UNSIGNED NOT NULL,
    ten_san_pham        VARCHAR(255)    NOT NULL,
    hinh_anh_san_pham   VARCHAR(2000)   NULL,
    don_gia             DECIMAL(15,2)   NOT NULL,
    so_luong            INT             NOT NULL,
    thanh_tien          DECIMAL(15,2)   NOT NULL,
    PRIMARY KEY (id),
    KEY idx_ctdh_don_hang (don_hang_id),
    KEY idx_ctdh_san_pham (san_pham_id),
    CONSTRAINT fk_ctdh_don_hang FOREIGN KEY (don_hang_id) REFERENCES don_hang (id),
    CONSTRAINT fk_ctdh_san_pham FOREIGN KEY (san_pham_id) REFERENCES san_pham (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 10. THANH_TOAN  (Entity: ThanhToan) - moi don hang co dung 1 giao dich
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS thanh_toan;
CREATE TABLE thanh_toan (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    don_hang_id         BIGINT UNSIGNED NOT NULL,
    phuong_thuc         VARCHAR(30)     NOT NULL,
    so_tien             DECIMAL(15,2)   NOT NULL,
    trang_thai          VARCHAR(30)     NOT NULL DEFAULT 'CHO_THANH_TOAN',
    ma_giao_dich        VARCHAR(100)    NULL,
    ly_do_that_bai      VARCHAR(500)    NULL,
    ngay_tao            DATETIME        NULL,
    ngay_thanh_toan     DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_thanh_toan_don_hang (don_hang_id),
    CONSTRAINT fk_thanh_toan_don_hang FOREIGN KEY (don_hang_id) REFERENCES don_hang (id),
    CONSTRAINT ck_thanh_toan_phuong_thuc CHECK (
        phuong_thuc IN ('COD','CHUYEN_KHOAN','VNPAY','VIETQR')
    ),
    CONSTRAINT ck_thanh_toan_trang_thai CHECK (
        trang_thai IN ('CHO_THANH_TOAN','DA_THANH_TOAN','THAT_BAI','DA_HOAN_TIEN')
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 11. PHIEU_KHO (da gop chi_tiet_phieu_kho) - phieu nhap/xuat kho
--     Moi dong = 1 san pham; cac dong cung ma_phieu thuoc cung 1 phieu.
--     Tong tien phieu = SUM(thanh_tien) GROUP BY ma_phieu.
--     don_hang_id khong co khoa ngoai (giu dung theo entity cu).
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS chi_tiet_phieu_kho;
DROP TABLE IF EXISTS phieu_kho;
CREATE TABLE phieu_kho (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    ma_phieu            VARCHAR(50)     NOT NULL,
    loai_phieu          VARCHAR(10)     NOT NULL,
    san_pham_id         BIGINT UNSIGNED NOT NULL,
    so_luong            INT             NOT NULL,
    don_gia             DECIMAL(15,2)   NULL,
    thanh_tien          DECIMAL(15,2)   NULL,
    nha_cung_cap        VARCHAR(255)    NULL,
    ly_do_xuat          VARCHAR(30)     NULL,
    don_hang_id         BIGINT UNSIGNED NULL,
    nguoi_tao_id        BIGINT UNSIGNED NULL,
    ghi_chu             TEXT            NULL,
    trang_thai          VARCHAR(20)     NOT NULL DEFAULT 'CHO_XU_LY',
    ngay_tao            DATETIME        NULL,
    ngay_hoan_thanh     DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_phieu_kho_phieu_san_pham (ma_phieu, san_pham_id),
    KEY idx_phieu_kho_ma_phieu (ma_phieu),
    KEY idx_phieu_kho_san_pham (san_pham_id),
    KEY idx_phieu_kho_nguoi_tao (nguoi_tao_id),
    KEY idx_phieu_kho_don_hang (don_hang_id),
    CONSTRAINT fk_phieu_kho_san_pham FOREIGN KEY (san_pham_id) REFERENCES san_pham (id),
    CONSTRAINT fk_phieu_kho_nguoi_tao FOREIGN KEY (nguoi_tao_id) REFERENCES nguoi_dung (id),
    CONSTRAINT ck_phieu_kho_loai CHECK (loai_phieu IN ('NHAP','XUAT')),
    CONSTRAINT ck_phieu_kho_ly_do_xuat CHECK (
        ly_do_xuat IS NULL OR ly_do_xuat IN ('GIAO_DON_HANG','HANG_LOI_HONG','CHUYEN_KHO','KIEM_KE_DIEU_CHINH','KHAC')
    ),
    CONSTRAINT ck_phieu_kho_trang_thai CHECK (
        trang_thai IN ('CHO_XU_LY','DA_NHAP_KHO','DA_XUAT_KHO','DA_HUY')
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 13. BAO_HANH  (Entity: BaoHanh)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS bao_hanh;
CREATE TABLE bao_hanh (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    so_seri         VARCHAR(100)    NULL,
    san_pham_id     BIGINT UNSIGNED NOT NULL,
    nguoi_dung_id   BIGINT UNSIGNED NOT NULL,
    ma_don_hang     VARCHAR(255)    NULL,
    ngay_mua        DATE            NOT NULL,
    ngay_bat_dau    DATE            NOT NULL,
    ngay_ket_thuc   DATE            NOT NULL,
    thoi_han_thang  INT             NOT NULL,
    trang_thai      VARCHAR(30)     NOT NULL DEFAULT 'CON_HAN',
    mo_ta           TEXT            NULL,
    ngay_tao        DATETIME        NULL,
    ngay_cap_nhat   DATETIME        NULL,
    PRIMARY KEY (id),
    KEY idx_bao_hanh_san_pham (san_pham_id),
    KEY idx_bao_hanh_nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_bao_hanh_san_pham FOREIGN KEY (san_pham_id) REFERENCES san_pham (id),
    CONSTRAINT fk_bao_hanh_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id),
    CONSTRAINT ck_bao_hanh_trang_thai CHECK (
        trang_thai IN ('CON_HAN','HET_HAN','DANG_XU_LY','DA_SUA_XONG','DA_HUY')
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 14. YEU_CAU_DOI_TRA  (Entity: YeuCauDoiTra)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS yeu_cau_doi_tra;
CREATE TABLE yeu_cau_doi_tra (
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    ma_yeu_cau              VARCHAR(30)     NOT NULL,
    don_hang_id             BIGINT UNSIGNED NOT NULL,
    chi_tiet_don_hang_id    BIGINT UNSIGNED NOT NULL,
    nguoi_dung_id           BIGINT UNSIGNED NOT NULL,
    loai_yeu_cau            VARCHAR(30)     NOT NULL,
    so_luong                INT             NOT NULL,
    ly_do                   TEXT            NOT NULL,
    so_tien_hoan_tra        DECIMAL(15,2)   NULL,
    trang_thai              VARCHAR(30)     NOT NULL DEFAULT 'CHO_DUYET',
    ghi_chu_xu_ly           TEXT            NULL,
    ngay_tao                DATETIME        NULL,
    ngay_cap_nhat           DATETIME        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ycdt_ma_yeu_cau (ma_yeu_cau),
    KEY idx_ycdt_don_hang (don_hang_id),
    KEY idx_ycdt_chi_tiet_don_hang (chi_tiet_don_hang_id),
    KEY idx_ycdt_nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_ycdt_don_hang FOREIGN KEY (don_hang_id) REFERENCES don_hang (id),
    CONSTRAINT fk_ycdt_chi_tiet_don_hang FOREIGN KEY (chi_tiet_don_hang_id) REFERENCES chi_tiet_don_hang (id),
    CONSTRAINT fk_ycdt_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id),
    CONSTRAINT ck_ycdt_loai_yeu_cau CHECK (loai_yeu_cau IN ('DOI_HANG','TRA_HANG_HOAN_TIEN')),
    CONSTRAINT ck_ycdt_trang_thai CHECK (
        trang_thai IN ('CHO_DUYET','DA_DUYET','TU_CHOI','DANG_HOAN_TRA','DA_HOAN_TIEN','DA_HOAN_THANH','DA_HUY')
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- TAI KHOAN QUAN TRI MAC DINH
-- Tai khoan nay se duoc KhoiTaoDuLieu.java tu tao khi chay app lan dau
-- (email: admin@cuahangcongnghe.com / mat khau: Admin@123), nen KHONG
-- insert san o day de tranh trung voi ma hash BCrypt do app sinh ra.
-- =====================================================================
