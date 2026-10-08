package com.cuahangcongnghe.nguoidung.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class GanVaiTroRequest {

    /**
     * Danh sach ten vai tro se GAN THAY THE cho toan bo vai tro hien tai cua nguoi dung.
     * Chi chap nhan: ROLE_KHACH_HANG, ROLE_NHAN_VIEN.
     * (Khong cho gan ROLE_QUAN_TRI qua API nay de tranh leo thang quyen ngoai y muon;
     * tai khoan quan tri chi duoc tao qua du lieu khoi tao ban dau.)
     */
    @NotEmpty(message = "Phai chon it nhat mot vai tro")
    private Set<String> danhSachVaiTro;
}
