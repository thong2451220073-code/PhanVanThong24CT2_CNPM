package com.cuahangcongnghe.nhapkho.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietNhapKhoRequest {

    @NotNull(message = "San pham khong duoc de trong")
    private Long sanPhamId;

    @NotNull(message = "So luong khong duoc de trong")
    @Positive(message = "So luong phai lon hon 0")
    private Integer soLuong;

    @NotNull(message = "Don gia khong duoc de trong")
    @Positive(message = "Don gia phai lon hon 0")
    private BigDecimal donGia;
}
