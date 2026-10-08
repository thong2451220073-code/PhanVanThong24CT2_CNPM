package com.cuahangcongnghe.doitra.entity;

import com.cuahangcongnghe.donhang.entity.ChiTietDonHang;
import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_doi_tra")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauDoiTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_yeu_cau", nullable = false, unique = true, length = 30)
    private String maYeuCau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id", nullable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chi_tiet_don_hang_id", nullable = false)
    private ChiTietDonHang chiTietDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_yeu_cau", nullable = false, length = 30)
    private LoaiYeuCau loaiYeuCau;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "ly_do", nullable = false, columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "so_tien_hoan_tra", precision = 15, scale = 2)
    private BigDecimal soTienHoanTra;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private TrangThaiDoiTra trangThai;

    @Column(name = "ghi_chu_xu_ly", columnDefinition = "TEXT")
    private String ghiChuXuLy;

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void khiTao() {
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = TrangThaiDoiTra.CHO_DUYET;
        }
    }

    @PreUpdate
    protected void khiCapNhat() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}
