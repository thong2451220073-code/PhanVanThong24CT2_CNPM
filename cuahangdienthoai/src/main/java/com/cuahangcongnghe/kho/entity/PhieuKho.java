package com.cuahangcongnghe.kho.entity;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Phieu nhap/xuat kho (da gop chi tiet vao day).
 * Moi dong = 1 san pham trong phieu; cac dong cung maPhieu thuoc cung 1 phieu.
 */
@Entity
@Table(name = "phieu_kho", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ma_phieu", "san_pham_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhieuKho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_phieu", nullable = false, length = 50)
    private String maPhieu;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phieu", nullable = false, length = 10)
    private LoaiPhieu loaiPhieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", precision = 15, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", precision = 15, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "nha_cung_cap", length = 255)
    private String nhaCungCap;

    @Enumerated(EnumType.STRING)
    @Column(name = "ly_do_xuat", length = 30)
    private LyDoXuatKho lyDoXuat;

    @Column(name = "don_hang_id")
    private Long donHangId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_tao_id")
    private NguoiDung nguoiTao;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    @Builder.Default
    private TrangThaiPhieuKho trangThai = TrangThaiPhieuKho.CHO_XU_LY;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @PrePersist
    @PreUpdate
    protected void khiLuu() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
        if (soLuong != null && donGia != null) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public enum LoaiPhieu { NHAP, XUAT }

    public enum LyDoXuatKho {
        GIAO_DON_HANG, HANG_LOI_HONG, CHUYEN_KHO, KIEM_KE_DIEU_CHINH, KHAC
    }

    public enum TrangThaiPhieuKho {
        CHO_XU_LY, DA_NHAP_KHO, DA_XUAT_KHO, DA_HUY
    }
}
