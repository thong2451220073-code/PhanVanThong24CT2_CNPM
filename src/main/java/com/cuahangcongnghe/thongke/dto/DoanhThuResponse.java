package com.cuahangcongnghe.thongke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThuResponse {

    /**
     * Nhan cua moc thoi gian: vd "2026-09" cho theo thang, "2026-09-06" cho theo ngay.
     */
    private String moc;

    private LocalDate tuNgay;

    private LocalDate denNgay;

    private BigDecimal tongDoanhThu;

    private Long soDonHang;

    private BigDecimal giaTriDonHangTrungBinh;
}
