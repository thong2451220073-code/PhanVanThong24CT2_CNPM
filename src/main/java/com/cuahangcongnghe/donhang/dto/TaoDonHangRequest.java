package com.cuahangcongnghe.donhang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaoDonHangRequest {

    @NotNull(message = "Địa chỉ giao hàng không được để trống")
    private Long diaChiId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan;

    private String ghiChu;
}
