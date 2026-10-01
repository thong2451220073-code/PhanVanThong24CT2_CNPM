package com.cuahangcongnghe.thanhtoan.service;

import com.cuahangcongnghe.thanhtoan.entity.ThanhToan;

public interface ThanhToanService {

    ThanhToan layTheoId(Long id);

    ThanhToan layTheoDonHang(Long donHangId);

    /**
     * Lay thanh toan theo id, kiem tra nguoiDungId truyen vao phai la chu don hang lien quan
     * (truyen null de bo qua kiem tra, danh cho quan tri vien).
     */
    ThanhToan layTheoIdChoNguoiDung(Long id, Long nguoiDungId);

    /**
     * Lay thanh toan theo don hang, kiem tra nguoiDungId truyen vao phai la chu don hang
     * (truyen null de bo qua kiem tra, danh cho quan tri vien).
     */
    ThanhToan layTheoDonHangChoNguoiDung(Long donHangId, Long nguoiDungId);

    /**
     * Tao ban ghi thanh toan moi cho mot don hang (trang thai CHO_THANH_TOAN).
     * nguoiDungId dung de kiem tra don hang co thuoc ve nguoi goi khong (null = bo qua, quan tri vien).
     */
    ThanhToan taoThanhToan(Long donHangId, ThanhToan.PhuongThucThanhToan phuongThuc, Long nguoiDungId);

    /**
     * Danh dau thanh toan thanh cong, thuong duoc goi tu webhook cong thanh toan.
     */
    ThanhToan xacNhanThanhToanThanhCong(Long thanhToanId, String maGiaoDich);

    /**
     * Danh dau thanh toan that bai.
     */
    ThanhToan danhDauThatBai(Long thanhToanId, String lyDo);

    /**
     * Hoan tien cho mot thanh toan da thanh cong (vd. khi doi/tra hang).
     */
    ThanhToan hoanTien(Long thanhToanId);
}
