package com.cuahangcongnghe.donhang.dto;

import com.cuahangcongnghe.donhang.entity.TrangThaiDonHang;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHangResponse {

    private Long id;
    private String maDonHang;
    private Long nguoiDungId;
    private String hoTenNguoiNhan;
    private String soDienThoaiNguoiNhan;
    private String diaChiGiaoHang;
    private List<ChiTietDonHangResponse> danhSachChiTiet;
    private BigDecimal tongTienHang;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongThanhToan;
    private String phuongThucThanhToan;
    private boolean daThanhToan;
    private TrangThaiDonHang trangThai;
    private String ghiChu;
    private String lyDoHuy;
    private Long nhanVienTaoId;
    private LocalDateTime ngayTao;
}
