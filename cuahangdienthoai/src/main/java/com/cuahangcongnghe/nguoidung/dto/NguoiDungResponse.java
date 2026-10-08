package com.cuahangcongnghe.nguoidung.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDungResponse {
    private Long id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private boolean kichHoat;
    private LocalDateTime ngayTao;
    private Set<String> danhSachVaiTro;
}
