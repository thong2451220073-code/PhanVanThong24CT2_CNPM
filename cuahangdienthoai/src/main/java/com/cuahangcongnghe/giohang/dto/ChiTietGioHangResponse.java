package com.cuahangcongnghe.giohang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHangResponse {

    private Long id;
    private Long sanPhamId;
    private String tenSanPham;
    private String hinhAnhSanPham;
    private BigDecimal donGia;
    private Integer soLuong;
    private BigDecimal thanhTien;
    private boolean daChon;
    private boolean conHang;
}
