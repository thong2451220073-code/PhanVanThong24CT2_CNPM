package com.cuahangcongnghe.giohang.service;

import com.cuahangcongnghe.giohang.dto.CapNhatGioHangRequest;
import com.cuahangcongnghe.giohang.dto.GioHangResponse;
import com.cuahangcongnghe.giohang.dto.ThemVaoGioHangRequest;

public interface GioHangService {

    GioHangResponse layGioHang(Long nguoiDungId);

    GioHangResponse themVaoGioHang(Long nguoiDungId, ThemVaoGioHangRequest request);

    GioHangResponse capNhatSoLuong(Long nguoiDungId, Long chiTietId, CapNhatGioHangRequest request);

    GioHangResponse chonSanPham(Long nguoiDungId, Long chiTietId, boolean daChon);

    GioHangResponse xoaKhoiGioHang(Long nguoiDungId, Long chiTietId);

    void xoaCacMucDaChon(Long nguoiDungId);
}
