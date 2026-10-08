package com.cuahangcongnghe.xacthuc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DangNhapRequest {

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    private String email;

    @NotBlank(message = "Mat khau khong duoc de trong")
    private String matKhau;
}
