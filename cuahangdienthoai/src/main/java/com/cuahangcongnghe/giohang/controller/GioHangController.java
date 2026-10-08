package com.cuahangcongnghe.giohang.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.giohang.dto.CapNhatGioHangRequest;
import com.cuahangcongnghe.giohang.dto.GioHangResponse;
import com.cuahangcongnghe.giohang.dto.ThemVaoGioHangRequest;
import com.cuahangcongnghe.giohang.service.GioHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gio-hang")
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;

    @GetMapping
    public ResponseEntity<GioHangResponse> layGioHang(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung) {
        return ResponseEntity.ok(gioHangService.layGioHang(nguoiDung.getId()));
    }

    @PostMapping
    public ResponseEntity<GioHangResponse> themVaoGioHang(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @Valid @RequestBody ThemVaoGioHangRequest request) {
        return ResponseEntity.ok(gioHangService.themVaoGioHang(nguoiDung.getId(), request));
    }

    @PutMapping("/{chiTietId}")
    public ResponseEntity<GioHangResponse> capNhatSoLuong(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long chiTietId,
            @Valid @RequestBody CapNhatGioHangRequest request) {
        return ResponseEntity.ok(gioHangService.capNhatSoLuong(nguoiDung.getId(), chiTietId, request));
    }

    @PatchMapping("/{chiTietId}/chon")
    public ResponseEntity<GioHangResponse> chonSanPham(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long chiTietId,
            @RequestParam boolean daChon) {
        return ResponseEntity.ok(gioHangService.chonSanPham(nguoiDung.getId(), chiTietId, daChon));
    }

    @DeleteMapping("/da-chon")
    public ResponseEntity<Void> xoaCacMucDaChon(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung) {
        gioHangService.xoaCacMucDaChon(nguoiDung.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chiTietId}")
    public ResponseEntity<GioHangResponse> xoaKhoiGioHang(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long chiTietId) {
        return ResponseEntity.ok(gioHangService.xoaKhoiGioHang(nguoiDung.getId(), chiTietId));
    }
}
