package com.cuahangcongnghe.giohang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GioHangResponse {

    private Long id;
    private List<ChiTietGioHangResponse> danhSachChiTiet;
    private Integer tongSoLuong;
    private BigDecimal tongTien;
}
