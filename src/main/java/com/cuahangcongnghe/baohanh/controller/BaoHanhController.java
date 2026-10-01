package com.cuahangcongnghe.baohanh.controller;

import com.cuahangcongnghe.baohanh.dto.BaoHanhRequest;
import com.cuahangcongnghe.baohanh.dto.BaoHanhResponse;
import com.cuahangcongnghe.baohanh.entity.TrangThaiBaoHanh;
import com.cuahangcongnghe.baohanh.service.BaoHanhService;
import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
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
@RequestMapping("/api/bao-hanh")
@RequiredArgsConstructor
public class BaoHanhController {

    private final BaoHanhService baoHanhService;

    @PostMapping
    public ResponseEntity<BaoHanhResponse> taoBaoHanh(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @Valid @RequestBody BaoHanhRequest request) {
        BaoHanhResponse response = baoHanhService.taoBaoHanh(nguoiDung.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Quan tri vien / nhan vien duoc xem phieu bao hanh cua bat ky ai;
    // khach hang chi duoc xem phieu bao hanh cua chinh minh.
    private static boolean laNhanSu(ChiTietNguoiDung nguoiDung) {
        return nguoiDung.getAuthorities().stream().anyMatch(quyen ->
                quyen.getAuthority().equals("ROLE_QUAN_TRI") || quyen.getAuthority().equals("ROLE_NHAN_VIEN"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaoHanhResponse> layTheoId(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                      @PathVariable Long id) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        return ResponseEntity.ok(baoHanhService.layTheoIdChoNguoiDung(id, nguoiDungId));
    }

    @GetMapping("/so-seri/{soSeri}")
    public ResponseEntity<BaoHanhResponse> layTheoSoSeri(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                          @PathVariable String soSeri) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        return ResponseEntity.ok(baoHanhService.layTheoSoSeriChoNguoiDung(soSeri, nguoiDungId));
    }

    @GetMapping("/cua-toi")
    public ResponseEntity<Page<BaoHanhResponse>> layBaoHanhCuaToi(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            Pageable pageable) {
        return ResponseEntity.ok(baoHanhService.layTheoNguoiDung(nguoiDung.getId(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<Page<BaoHanhResponse>> layTheoTrangThai(
            @RequestParam(required = false) TrangThaiBaoHanh trangThai,
            Pageable pageable) {
        return ResponseEntity.ok(baoHanhService.layTheoTrangThai(trangThai, pageable));
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<BaoHanhResponse> capNhatTrangThai(
            @PathVariable Long id,
            @RequestParam TrangThaiBaoHanh trangThai) {
        return ResponseEntity.ok(baoHanhService.capNhatTrangThai(id, trangThai));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<Void> xoaBaoHanh(@PathVariable Long id) {
        baoHanhService.xoaBaoHanh(id);
        return ResponseEntity.noContent().build();
    }
}
