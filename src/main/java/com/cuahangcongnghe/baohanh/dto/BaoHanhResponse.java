package com.cuahangcongnghe.baohanh.dto;

import com.cuahangcongnghe.baohanh.entity.TrangThaiBaoHanh;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaoHanhResponse {

    private Long id;
    private String soSeri;
    private Long sanPhamId;
    private String tenSanPham;
    private Long nguoiDungId;
    private String tenNguoiDung;
    private String maDonHang;
    private LocalDate ngayMua;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer thoiHanThang;
    private TrangThaiBaoHanh trangThai;
    private String moTa;
    private boolean conHanBaoHanh;
}
