package com.cuahangcongnghe.giohang.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThemVaoGioHangRequest {

    @NotNull(message = "Sản phẩm không được để trống")
    private Long sanPhamId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer soLuong;
}
