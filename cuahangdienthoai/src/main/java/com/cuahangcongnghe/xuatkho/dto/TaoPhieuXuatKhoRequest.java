package com.cuahangcongnghe.xuatkho.dto;

import com.cuahangcongnghe.kho.entity.PhieuKho;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaoPhieuXuatKhoRequest {

    @NotNull(message = "Ly do xuat kho khong duoc de trong")
    private PhieuKho.LyDoXuatKho lyDoXuat;

    private Long donHangId;

    private String ghiChu;

    @NotEmpty(message = "Phieu xuat kho phai co it nhat mot chi tiet")
    @Valid
    private List<ChiTietXuatKhoRequest> danhSachChiTiet;
}
