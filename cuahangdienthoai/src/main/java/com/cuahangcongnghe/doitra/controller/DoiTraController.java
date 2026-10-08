package com.cuahangcongnghe.doitra.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.doitra.dto.XuLyDoiTraRequest;
import com.cuahangcongnghe.doitra.dto.YeuCauDoiTraRequest;
import com.cuahangcongnghe.doitra.dto.YeuCauDoiTraResponse;
import com.cuahangcongnghe.doitra.entity.TrangThaiDoiTra;
import com.cuahangcongnghe.doitra.service.DoiTraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doi-tra")
@RequiredArgsConstructor
public class DoiTraController {

    private final DoiTraService doiTraService;

    @PostMapping
    public ResponseEntity<YeuCauDoiTraResponse> taoYeuCau(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @Valid @RequestBody YeuCauDoiTraRequest request) {
        YeuCauDoiTraResponse response = doiTraService.taoYeuCau(nguoiDung.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Quan tri vien / nhan vien duoc xem yeu cau doi tra cua bat ky ai;
    // khach hang chi duoc xem yeu cau cua chinh minh.
    private static boolean laNhanSu(ChiTietNguoiDung nguoiDung) {
        return nguoiDung.getAuthorities().stream().anyMatch(quyen ->
                quyen.getAuthority().equals("ROLE_QUAN_TRI") || quyen.getAuthority().equals("ROLE_NHAN_VIEN"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<YeuCauDoiTraResponse> layTheoId(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                           @PathVariable Long id) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        return ResponseEntity.ok(doiTraService.layTheoIdChoNguoiDung(id, nguoiDungId));
    }

    @GetMapping("/ma/{maYeuCau}")
    public ResponseEntity<YeuCauDoiTraResponse> layTheoMaYeuCau(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                                 @PathVariable String maYeuCau) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        return ResponseEntity.ok(doiTraService.layTheoMaYeuCauChoNguoiDung(maYeuCau, nguoiDungId));
    }

    @GetMapping("/cua-toi")
    public ResponseEntity<Page<YeuCauDoiTraResponse>> layYeuCauCuaToi(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            Pageable pageable) {
        return ResponseEntity.ok(doiTraService.layTheoNguoiDung(nguoiDung.getId(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<Page<YeuCauDoiTraResponse>> layTheoTrangThai(
            @RequestParam(required = false) TrangThaiDoiTra trangThai,
            Pageable pageable) {
        return ResponseEntity.ok(doiTraService.layTheoTrangThai(trangThai, pageable));
    }

    @PatchMapping("/{id}/xu-ly")
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<YeuCauDoiTraResponse> xuLyYeuCau(
            @PathVariable Long id,
            @Valid @RequestBody XuLyDoiTraRequest request) {
        return ResponseEntity.ok(doiTraService.xuLyYeuCau(id, request));
    }

    @PatchMapping("/{id}/huy")
    public ResponseEntity<Void> huyYeuCau(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long id) {
        doiTraService.huyYeuCau(id, nguoiDung.getId());
        return ResponseEntity.ok().build();
    }
}
