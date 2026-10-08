package com.cuahangcongnghe.kho.entity;

import com.cuahangcongnghe.sanpham.entity.SanPham;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ton_kho", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"san_pham_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TonKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SanPham sanPham;


    @Column(name = "so_luong_ton", nullable = false)
    @Builder.Default
    private Integer soLuongTon = 0;

    // So luong da duoc giu cho don hang dang xu ly (chua tru hang that su)
    @Column(name = "so_luong_dat_truoc", nullable = false)
    @Builder.Default
    private Integer soLuongDatTruoc = 0;

    @Column(name = "muc_ton_toi_thieu")
    @Builder.Default
    private Integer mucTonToiThieu = 5;

    @Column(name = "vi_tri_kho", length = 100)
    private String viTriKho;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    @PreUpdate
    protected void capNhatThoiGian() {
        ngayCapNhat = LocalDateTime.now();
    }

    public Integer soLuongCoTheBan() {
        return soLuongTon - soLuongDatTruoc;
    }

    public boolean sapHetHang() {
        return soLuongTon <= mucTonToiThieu;
    }
}
