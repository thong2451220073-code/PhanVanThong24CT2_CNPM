package com.cuahangcongnghe.doitra.dto;

import com.cuahangcongnghe.doitra.entity.LoaiYeuCau;
import com.cuahangcongnghe.doitra.entity.TrangThaiDoiTra;
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
public class YeuCauDoiTraResponse {

    private Long id;
    private String maYeuCau;
    private Long donHangId;
    private String maDonHang;
    private Long chiTietDonHangId;
    private String tenSanPham;
    private Long nguoiDungId;
    private LoaiYeuCau loaiYeuCau;
    private Integer soLuong;
    private String lyDo;
    private List<String> hinhAnhMinhChung;
    private BigDecimal soTienHoanTra;
    private TrangThaiDoiTra trangThai;
    private String ghiChuXuLy;
    private LocalDateTime ngayTao;
}
