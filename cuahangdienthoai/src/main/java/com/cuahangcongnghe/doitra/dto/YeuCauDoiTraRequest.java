package com.cuahangcongnghe.doitra.dto;

import com.cuahangcongnghe.doitra.entity.LoaiYeuCau;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class YeuCauDoiTraRequest {

    @NotNull(message = "Đơn hàng không được để trống")
    private Long donHangId;

    @NotNull(message = "Sản phẩm trong đơn hàng không được để trống")
    private Long chiTietDonHangId;

    @NotNull(message = "Loại yêu cầu không được để trống")
    private LoaiYeuCau loaiYeuCau;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer soLuong;

    @NotBlank(message = "Lý do không được để trống")
    private String lyDo;

    private List<String> hinhAnhMinhChung;
}
