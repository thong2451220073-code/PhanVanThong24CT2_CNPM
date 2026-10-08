package com.cuahangcongnghe.nhapkho.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaoPhieuNhapKhoRequest {

    private String nhaCungCap;

    private String ghiChu;

    @NotEmpty(message = "Phieu nhap kho phai co it nhat mot chi tiet")
    @Valid
    private List<ChiTietNhapKhoRequest> danhSachChiTiet;
}
