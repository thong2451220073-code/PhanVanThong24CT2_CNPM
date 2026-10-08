package com.cuahangcongnghe.giohang.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuahangcongnghe.giohang.dto.CapNhatGioHangRequest;
import com.cuahangcongnghe.giohang.dto.ChiTietGioHangResponse;
import com.cuahangcongnghe.giohang.dto.GioHangResponse;
import com.cuahangcongnghe.giohang.dto.ThemVaoGioHangRequest;
import com.cuahangcongnghe.giohang.entity.ChiTietGioHang;
import com.cuahangcongnghe.giohang.repository.ChiTietGioHangRepository;
import com.cuahangcongnghe.ngoaile.KhongCoQuyenException;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GioHangServiceImpl implements GioHangService {

    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional(readOnly = true)
    public GioHangResponse layGioHang(Long nguoiDungId) {
        return chuyenSangResponse(nguoiDungId);
    }

    @Override
    public GioHangResponse themVaoGioHang(Long nguoiDungId, ThemVaoGioHangRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy người dùng"));

        SanPham sanPham = sanPhamRepository.findById(request.getSanPhamId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm"));

        ChiTietGioHang chiTiet = chiTietGioHangRepository
                .findByNguoiDungIdAndSanPhamId(nguoiDungId, request.getSanPhamId())
                .orElse(null);

        if (chiTiet != null) {
            chiTiet.setSoLuong(chiTiet.getSoLuong() + request.getSoLuong());
        } else {
            chiTiet = ChiTietGioHang.builder()
                    .nguoiDung(nguoiDung)
                    .sanPham(sanPham)
                    .soLuong(request.getSoLuong())
                    .daChon(true)
                    .build();
        }

        chiTietGioHangRepository.save(chiTiet);
        return chuyenSangResponse(nguoiDungId);
    }

    @Override
    public GioHangResponse capNhatSoLuong(Long nguoiDungId, Long chiTietId, CapNhatGioHangRequest request) {
        ChiTietGioHang chiTiet = timChiTietCuaNguoiDung(chiTietId, nguoiDungId);
        chiTiet.setSoLuong(request.getSoLuong());
        chiTietGioHangRepository.save(chiTiet);
        return chuyenSangResponse(nguoiDungId);
    }

    @Override
    public GioHangResponse chonSanPham(Long nguoiDungId, Long chiTietId, boolean daChon) {
        ChiTietGioHang chiTiet = timChiTietCuaNguoiDung(chiTietId, nguoiDungId);
        chiTiet.setDaChon(daChon);
        chiTietGioHangRepository.save(chiTiet);
        return chuyenSangResponse(nguoiDungId);
    }

    @Override
    public GioHangResponse xoaKhoiGioHang(Long nguoiDungId, Long chiTietId) {
        ChiTietGioHang chiTiet = timChiTietCuaNguoiDung(chiTietId, nguoiDungId);
        chiTietGioHangRepository.delete(chiTiet);
        chiTietGioHangRepository.flush();
        return chuyenSangResponse(nguoiDungId);
    }

    @Override
    public void xoaCacMucDaChon(Long nguoiDungId) {
        chiTietGioHangRepository.deleteByNguoiDungIdAndDaChonTrue(nguoiDungId);
    }

    private ChiTietGioHang timChiTietCuaNguoiDung(Long chiTietId, Long nguoiDungId) {
        ChiTietGioHang chiTiet = chiTietGioHangRepository.findById(chiTietId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm trong giỏ hàng"));
        if (!chiTiet.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền thao tác trên giỏ hàng này");
        }
        return chiTiet;
    }

    private GioHangResponse chuyenSangResponse(Long nguoiDungId) {
        List<ChiTietGioHangResponse> danhSach = chiTietGioHangRepository.findByNguoiDungId(nguoiDungId).stream()
                .map(this::chuyenChiTietSangResponse)
                .collect(Collectors.toList());

        int tongSoLuong = danhSach.stream().mapToInt(ChiTietGioHangResponse::getSoLuong).sum();
        BigDecimal tongTien = danhSach.stream()
                .filter(ChiTietGioHangResponse::isDaChon)
                .map(ChiTietGioHangResponse::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return GioHangResponse.builder()
                .id(nguoiDungId)
                .danhSachChiTiet(danhSach)
                .tongSoLuong(tongSoLuong)
                .tongTien(tongTien)
                .build();
    }

    private ChiTietGioHangResponse chuyenChiTietSangResponse(ChiTietGioHang chiTiet) {
        BigDecimal donGia = chiTiet.getSanPham().getGia();
        BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));

        return ChiTietGioHangResponse.builder()
                .id(chiTiet.getId())
                .sanPhamId(chiTiet.getSanPham().getId())
                .tenSanPham(chiTiet.getSanPham().getTenSanPham())
                .donGia(donGia)
                .soLuong(chiTiet.getSoLuong())
                .thanhTien(thanhTien)
                .daChon(chiTiet.isDaChon())
                .conHang(true)
                .build();
    }
}
