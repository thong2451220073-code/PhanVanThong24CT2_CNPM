package com.cuahangcongnghe.thongke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamBanChayResponse {

    private Long sanPhamId;

    private String tenSanPham;

    private String maSku;

    private Long tongSoLuongDaBan;

    private BigDecimal tongDoanhThu;
}
