package com.cuahangcongnghe.thongke.service;

import com.cuahangcongnghe.thongke.dto.DoanhThuResponse;
import com.cuahangcongnghe.thongke.dto.SanPhamBanChayResponse;

import java.time.LocalDate;
import java.util.List;

public interface ThongKeService {

    DoanhThuResponse thongKeDoanhThu(LocalDate tuNgay, LocalDate denNgay);

    /**
     * Thong ke doanh thu theo ngay trong khoang thoi gian, dung ve bieu do duong.
     */
    List<DoanhThuResponse> thongKeDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay);

    List<SanPhamBanChayResponse> topSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int soLuongTop);
}
