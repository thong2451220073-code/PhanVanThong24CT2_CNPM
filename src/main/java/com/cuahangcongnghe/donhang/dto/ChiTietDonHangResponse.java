package com.cuahangcongnghe.donhang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangResponse {

    private Long id;
    private Long sanPhamId;
    private String tenSanPham;
    private String hinhAnhSanPham;
    private BigDecimal donGia;
    private Integer soLuong;
    private BigDecimal thanhTien;
}
