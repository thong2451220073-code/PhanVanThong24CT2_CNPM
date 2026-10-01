package com.cuahangcongnghe.giohang.entity;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_gio_hang", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nguoi_dung_id", "san_pham_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    @JsonIgnore
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "da_chon")
    @Builder.Default
    private boolean daChon = true;

    @Column(name = "ngay_them", updatable = false)
    private LocalDateTime ngayThem;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void khiTao() {
        this.ngayThem = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void khiCapNhat() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}
