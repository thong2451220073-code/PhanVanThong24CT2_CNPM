package com.cuahangcongnghe.kho.dto;

import com.cuahangcongnghe.kho.entity.PhieuKho;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mot phieu kho hoan chinh, duoc ghep tu cac dong phieu_kho cung ma_phieu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhieuKhoResponse {

    private String maPhieu;
    private PhieuKho.LoaiPhieu loaiPhieu;
    private String nhaCungCap;
    private PhieuKho.LyDoXuatKho lyDoXuat;
    private Long donHangId;
    private Long nguoiTaoId;
    private BigDecimal tongTien;
    private String ghiChu;
    private PhieuKho.TrangThaiPhieuKho trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayHoanThanh;
    private List<ChiTiet> danhSachChiTiet;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTiet {
        private Long id;
        private Long sanPhamId;
        private String tenSanPham;
        private Integer soLuong;
        private BigDecimal donGia;
        private BigDecimal thanhTien;
    }

    /** Ghep cac dong cung ma_phieu thanh 1 phieu (danh sach khong rong). */
    public static PhieuKhoResponse tuCacDong(List<PhieuKho> cacDong) {
        PhieuKho dau = cacDong.get(0);
        BigDecimal tong = BigDecimal.ZERO;
        List<ChiTiet> chiTiets = new ArrayList<>();
        for (PhieuKho d : cacDong) {
            if (d.getThanhTien() != null) tong = tong.add(d.getThanhTien());
            chiTiets.add(ChiTiet.builder()
                    .id(d.getId())
                    .sanPhamId(d.getSanPham().getId())
                    .tenSanPham(d.getSanPham().getTenSanPham())
                    .soLuong(d.getSoLuong())
                    .donGia(d.getDonGia())
                    .thanhTien(d.getThanhTien())
                    .build());
        }
        return PhieuKhoResponse.builder()
                .maPhieu(dau.getMaPhieu())
                .loaiPhieu(dau.getLoaiPhieu())
                .nhaCungCap(dau.getNhaCungCap())
                .lyDoXuat(dau.getLyDoXuat())
                .donHangId(dau.getDonHangId())
                .nguoiTaoId(dau.getNguoiTao() != null ? dau.getNguoiTao().getId() : null)
                .tongTien(tong)
                .ghiChu(dau.getGhiChu())
                .trangThai(dau.getTrangThai())
                .ngayTao(dau.getNgayTao())
                .ngayHoanThanh(dau.getNgayHoanThanh())
                .danhSachChiTiet(chiTiets)
                .build();
    }

    /** Nhom danh sach dong (da sap xep) thanh cac phieu, giu nguyen thu tu. */
    public static List<PhieuKhoResponse> nhomTheoMaPhieu(List<PhieuKho> cacDong) {
        Map<String, List<PhieuKho>> nhom = new LinkedHashMap<>();
        for (PhieuKho d : cacDong) {
            nhom.computeIfAbsent(d.getMaPhieu(), k -> new ArrayList<>()).add(d);
        }
        List<PhieuKhoResponse> ketQua = new ArrayList<>();
        for (List<PhieuKho> dong : nhom.values()) {
            ketQua.add(tuCacDong(dong));
        }
        return ketQua;
    }
}
