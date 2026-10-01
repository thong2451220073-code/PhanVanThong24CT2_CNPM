package com.cuahangcongnghe.thanhtoan.entity;

import com.cuahangcongnghe.donhang.entity.DonHang;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id", nullable = false, unique = true)
    private DonHang donHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc", nullable = false, length = 30)
    private PhuongThucThanhToan phuongThuc;

    @Column(name = "so_tien", nullable = false, precision = 15, scale = 2)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiThanhToan trangThai = TrangThaiThanhToan.CHO_THANH_TOAN;

    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;

    @Column(name = "ly_do_that_bai", length = 500)
    private String lyDoThatBai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @PrePersist
    protected void khiTao() {
        ngayTao = LocalDateTime.now();
    }

    public enum PhuongThucThanhToan {
        COD,
        CHUYEN_KHOAN,
        VNPAY,
        VIETQR,
        MOMO,
        ZALOPAY
    }

    public enum TrangThaiThanhToan {
        CHO_THANH_TOAN,
        DA_THANH_TOAN,
        THAT_BAI,
        DA_HOAN_TIEN
    }
}
