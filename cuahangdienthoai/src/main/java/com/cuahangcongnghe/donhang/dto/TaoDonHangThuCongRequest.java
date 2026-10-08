package com.cuahangcongnghe.donhang.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Dung khi Nhan vien ban hang tao don ho khach mua truc tiep tai cua hang hoac qua dien thoai.
 * Neu khach da co tai khoan va da co san dia chi, truyen diaChiId.
 * Neu khong (khach vang lai, chua dang ky), truyen day du 3 truong dia chi thu cong ben duoi.
 */
@Data
public class TaoDonHangThuCongRequest {

    @NotNull(message = "Phai chon khach hang cho don hang")
    private Long nguoiDungId;

    private Long diaChiId;

    // Dung khi khong truyen diaChiId (nhap tay dia chi giao hang luc goi dien/tai quay)
    private String hoTenNguoiNhanThuCong;
    private String soDienThoaiNguoiNhanThuCong;
    private String diaChiGiaoHangThuCong;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan;

    private String ghiChu;

    @NotEmpty(message = "Don hang phai co it nhat mot san pham")
    @Valid
    private List<ChiTietThuCongRequest> danhSachSanPham;
}
