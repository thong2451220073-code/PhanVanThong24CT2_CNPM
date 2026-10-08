package com.cuahangcongnghe.xacthuc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class DangNhapResponse {
    private String token;
    private String loaiToken;
    private Long id;
    private String hoTen;
    private String email;
    private Set<String> danhSachVaiTro;
}
