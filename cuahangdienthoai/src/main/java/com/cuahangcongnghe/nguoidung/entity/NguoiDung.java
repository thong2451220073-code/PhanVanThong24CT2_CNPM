package com.cuahangcongnghe.nguoidung.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "kich_hoat", nullable = false)
    @Builder.Default
    private boolean kichHoat = true;

    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "vai_tro", nullable = false, length = 50)
    @Builder.Default
    private String vaiTro = "ROLE_KHACH_HANG";

    @PrePersist
    public void truocKhiLuu() {
        this.ngayTao = LocalDateTime.now();
    }
}
