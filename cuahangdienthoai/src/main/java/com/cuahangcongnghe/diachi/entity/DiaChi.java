package com.cuahangcongnghe.diachi.entity;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "ho_ten_nguoi_nhan", nullable = false, length = 100)
    private String hoTenNguoiNhan;

    @Column(name = "so_dien_thoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "dia_chi_chi_tiet", nullable = false, columnDefinition = "TEXT")
    private String diaChiChiTiet;

    @Column(name = "phuong_xa", nullable = false, length = 100)
    private String phuongXa;

    @Column(name = "quan_huyen", nullable = false, length = 100)
    private String quanHuyen;

    @Column(name = "tinh_thanh", nullable = false, length = 100)
    private String tinhThanh;

    @Column(name = "loai_dia_chi", nullable = false, length = 20)
    @Builder.Default
    private String loaiDiaChi = "NHA";

    @Column(name = "la_mac_dinh", nullable = false)
    @Builder.Default
    private boolean laMacDinh = false;

    @Transient
    public String getDiaChiDayDu() {
        return String.join(", ",
                java.util.stream.Stream.of(diaChiChiTiet, phuongXa, quanHuyen, tinhThanh)
                        .filter(x -> x != null && !x.isBlank())
                        .toList());
    }
}
