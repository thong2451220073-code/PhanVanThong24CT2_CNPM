package com.cuahangcongnghe.thanhtoan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dong hien thi trong tab "Thanh toan" cua trang quan tri (moi don hang = 1 dong). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanQuanTriResponse {
    private Long donHangId;
    private String maDonHang;
    private String trangThaiDonHang;
    private String hoTenNguoiNhan;
    private String soDienThoaiNguoiNhan;
    private BigDecimal soTien;
    private String phuongThuc;
    /** CHO_THANH_TOAN | DA_THANH_TOAN | THAT_BAI | DA_HOAN_TIEN */
    private String trangThai;
    private String maGiaoDich;
    private String lyDoThatBai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayThanhToan;
}
