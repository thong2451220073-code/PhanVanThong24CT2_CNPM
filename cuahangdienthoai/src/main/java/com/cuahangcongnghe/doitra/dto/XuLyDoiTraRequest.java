package com.cuahangcongnghe.doitra.dto;

import com.cuahangcongnghe.doitra.entity.TrangThaiDoiTra;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class XuLyDoiTraRequest {

    @NotNull(message = "Trạng thái mới không được để trống")
    private TrangThaiDoiTra trangThai;

    private BigDecimal soTienHoanTra;

    private String ghiChuXuLy;
}
