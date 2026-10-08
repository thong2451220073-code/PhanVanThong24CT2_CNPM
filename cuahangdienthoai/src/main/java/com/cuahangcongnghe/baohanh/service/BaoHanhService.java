package com.cuahangcongnghe.baohanh.service;

import com.cuahangcongnghe.baohanh.dto.BaoHanhRequest;
import com.cuahangcongnghe.baohanh.dto.BaoHanhResponse;
import com.cuahangcongnghe.baohanh.entity.TrangThaiBaoHanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaoHanhService {

    BaoHanhResponse taoBaoHanh(Long nguoiDungId, BaoHanhRequest request);

    BaoHanhResponse layTheoId(Long id);

    BaoHanhResponse layTheoSoSeri(String soSeri);

    /**
     * Lay theo id, kiem tra nguoiDungId truyen vao phai la chu phieu bao hanh
     * (truyen null de bo qua kiem tra, danh cho quan tri vien/nhan vien).
     */
    BaoHanhResponse layTheoIdChoNguoiDung(Long id, Long nguoiDungId);

    /**
     * Lay theo so seri, kiem tra nguoiDungId truyen vao phai la chu phieu bao hanh
     * (truyen null de bo qua kiem tra, danh cho quan tri vien/nhan vien).
     */
    BaoHanhResponse layTheoSoSeriChoNguoiDung(String soSeri, Long nguoiDungId);

    Page<BaoHanhResponse> layTheoNguoiDung(Long nguoiDungId, Pageable pageable);

    Page<BaoHanhResponse> layTheoTrangThai(TrangThaiBaoHanh trangThai, Pageable pageable);

    BaoHanhResponse capNhatTrangThai(Long id, TrangThaiBaoHanh trangThai);

    void xoaBaoHanh(Long id);
}
