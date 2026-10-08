package com.cuahangcongnghe.donhang.service;

import com.cuahangcongnghe.donhang.dto.DonHangResponse;
import com.cuahangcongnghe.donhang.dto.TaoDonHangRequest;
import com.cuahangcongnghe.donhang.dto.TaoDonHangThuCongRequest;
import com.cuahangcongnghe.donhang.entity.TrangThaiDonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonHangService {

    DonHangResponse taoDonHangTuGioHang(Long nguoiDungId, TaoDonHangRequest request);

    /**
     * Nhan vien ban hang/quan tri tao don ho khach mua truc tiep hoac qua dien thoai.
     */
    DonHangResponse taoDonHangThuCong(Long nhanVienTaoId, TaoDonHangThuCongRequest request);

    DonHangResponse layTheoId(Long id, Long nguoiDungId);

    DonHangResponse layTheoMaDonHang(String maDonHang, Long nguoiDungId);

    Page<DonHangResponse> layTheoNguoiDung(Long nguoiDungId, TrangThaiDonHang trangThai, Pageable pageable);

    Page<DonHangResponse> layTatCa(TrangThaiDonHang trangThai, Pageable pageable);

    DonHangResponse capNhatTrangThai(Long id, TrangThaiDonHang trangThaiMoi, String ghiChu);

    DonHangResponse huyDonHang(Long id, Long nguoiDungId, String lyDo);
}
