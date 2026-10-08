package com.cuahangcongnghe.baohanh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BaoHanhRequest {

    @NotBlank(message = "Số seri không được để trống")
    private String soSeri;

    @NotNull(message = "Sản phẩm không được để trống")
    private Long sanPhamId;

    private String maDonHang;

    @NotNull(message = "Ngày mua không được để trống")
    private LocalDate ngayMua;

    @NotNull(message = "Thời hạn bảo hành (tháng) không được để trống")
    private Integer thoiHanThang;

    private String moTa;
}
