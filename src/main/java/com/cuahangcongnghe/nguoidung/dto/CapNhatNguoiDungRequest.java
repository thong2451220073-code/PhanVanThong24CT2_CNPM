package com.cuahangcongnghe.nguoidung.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapNhatNguoiDungRequest {

    @NotBlank(message = "Ho ten khong duoc de trong")
    private String hoTen;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "So dien thoai khong hop le")
    private String soDienThoai;
}
