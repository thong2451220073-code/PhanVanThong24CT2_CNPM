package com.cuahangcongnghe.baohanh.entity;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bao_hanh")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaoHanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_seri", length = 100)
    private String soSeri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "ma_don_hang")
    private String maDonHang;

    @Column(name = "ngay_mua", nullable = false)
    private LocalDate ngayMua;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "thoi_han_thang", nullable = false)
    private Integer thoiHanThang;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private TrangThaiBaoHanh trangThai;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void khiTao() {
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = TrangThaiBaoHanh.CON_HAN;
        }
    }

    @PreUpdate
    protected void khiCapNhat() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}
