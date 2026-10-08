package com.cuahangcongnghe.nguoidung.service;

import com.cuahangcongnghe.nguoidung.dto.CapNhatNguoiDungRequest;
import com.cuahangcongnghe.nguoidung.dto.NguoiDungResponse;

import java.util.List;
import java.util.Set;

public interface NguoiDungService {

    NguoiDungResponse layThongTinTheoEmail(String email);

    NguoiDungResponse capNhatThongTin(String email, CapNhatNguoiDungRequest request);

    List<NguoiDungResponse> layTatCaNguoiDung();

    void khoaTaiKhoan(Long id);

    void moKhoaTaiKhoan(Long id);

    /**
     * Quan tri vien gan (thay the) danh sach vai tro cho mot tai khoan,
     * dung de chi dinh mot khach hang tro thanh Nhan vien ban hang / Nhan vien kho, v.v.
     */
    NguoiDungResponse ganVaiTro(Long id, Set<String> tenCacVaiTro);
}
