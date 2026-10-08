package com.cuahangcongnghe.doitra.service;

import com.cuahangcongnghe.doitra.dto.XuLyDoiTraRequest;
import com.cuahangcongnghe.doitra.dto.YeuCauDoiTraRequest;
import com.cuahangcongnghe.doitra.dto.YeuCauDoiTraResponse;
import com.cuahangcongnghe.doitra.entity.TrangThaiDoiTra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoiTraService {

    YeuCauDoiTraResponse taoYeuCau(Long nguoiDungId, YeuCauDoiTraRequest request);

    YeuCauDoiTraResponse layTheoId(Long id);

    YeuCauDoiTraResponse layTheoMaYeuCau(String maYeuCau);

    /**
     * Lay theo id, kiem tra nguoiDungId truyen vao phai la chu yeu cau
     * (truyen null de bo qua kiem tra, danh cho quan tri vien/nhan vien).
     */
    YeuCauDoiTraResponse layTheoIdChoNguoiDung(Long id, Long nguoiDungId);

    /**
     * Lay theo ma yeu cau, kiem tra nguoiDungId truyen vao phai la chu yeu cau
     * (truyen null de bo qua kiem tra, danh cho quan tri vien/nhan vien).
     */
    YeuCauDoiTraResponse layTheoMaYeuCauChoNguoiDung(String maYeuCau, Long nguoiDungId);

    Page<YeuCauDoiTraResponse> layTheoNguoiDung(Long nguoiDungId, Pageable pageable);

    Page<YeuCauDoiTraResponse> layTheoTrangThai(TrangThaiDoiTra trangThai, Pageable pageable);

    YeuCauDoiTraResponse xuLyYeuCau(Long id, XuLyDoiTraRequest request);

    void huyYeuCau(Long id, Long nguoiDungId);
}
