package com.cuahangcongnghe.sanpham.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_san_pham", nullable = false, length = 255)
    private String tenSanPham;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "gia", nullable = false, precision = 15, scale = 2)
    private BigDecimal gia;

    @Column(name = "gia_khuyen_mai", precision = 15, scale = 2)
    private BigDecimal giaKhuyenMai;

    @Column(name = "ma_sku", unique = true, length = 100)
    private String maSku;

    @Column(name = "trang_thai", length = 30)
    @Builder.Default
    private String trangThai = "DANG_BAN";

    // Buoc 5: gop hinh anh vao san_pham, chi luu 1 anh dai dien.
    @Column(name = "duong_dan_anh", length = 2000)
    private String duongDanAnh;

    // Buoc 5: bien the khong con la bang rieng; san pham duoc quan ly theo SKU/gia/ton kho.
    @Transient
    public String getBienTheMoTa() { return null; }

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void khiTao() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void khiCapNhat() {
        ngayCapNhat = LocalDateTime.now();
    }
}
