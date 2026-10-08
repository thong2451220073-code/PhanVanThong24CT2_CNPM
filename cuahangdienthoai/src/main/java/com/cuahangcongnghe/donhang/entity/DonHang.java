package com.cuahangcongnghe.donhang.entity;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_hang")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_don_hang", nullable = false, unique = true, length = 30)
    private String maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

    // Thông tin giao hàng (chụp lại tại thời điểm đặt hàng)
    @Column(name = "ho_ten_nguoi_nhan", nullable = false, length = 100)
    private String hoTenNguoiNhan;

    @Column(name = "so_dien_thoai_nguoi_nhan", nullable = false, length = 15)
    private String soDienThoaiNguoiNhan;

    @Column(name = "dia_chi_giao_hang", nullable = false, columnDefinition = "TEXT")
    private String diaChiGiaoHang;

    @Column(name = "tong_tien_hang", nullable = false, precision = 15, scale = 2)
    private BigDecimal tongTienHang;

    @Column(name = "phi_van_chuyen", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan", nullable = false, precision = 15, scale = 2)
    private BigDecimal tongThanhToan;

    @Column(name = "phuong_thuc_thanh_toan", nullable = false, length = 30)
    private String phuongThucThanhToan;

    @Column(name = "da_thanh_toan")
    @Builder.Default
    private boolean daThanhToan = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private TrangThaiDonHang trangThai;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ly_do_huy")
    private String lyDoHuy;

    // Neu don hang duoc mot nhan vien tao ho khach (mua truc tiep/qua dien thoai) thi luu id nhan vien.
    // Null nghia la khach tu dat hang qua website.
    @Column(name = "nhan_vien_tao_id")
    private Long nhanVienTaoId;

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void khiTao() {
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = TrangThaiDonHang.CHO_XAC_NHAN;
        }
    }

    @PreUpdate
    protected void khiCapNhat() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    public void themChiTiet(ChiTietDonHang chiTiet) {
        danhSachChiTiet.add(chiTiet);
        chiTiet.setDonHang(this);
    }

}
