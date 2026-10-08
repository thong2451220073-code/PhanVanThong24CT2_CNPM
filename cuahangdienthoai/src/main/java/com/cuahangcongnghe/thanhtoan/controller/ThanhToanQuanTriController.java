package com.cuahangcongnghe.thanhtoan.controller;

import com.cuahangcongnghe.thanhtoan.dto.ThanhToanQuanTriResponse;
import com.cuahangcongnghe.thanhtoan.service.ThanhToanQuanTriService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thanh-toan")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
public class ThanhToanQuanTriController {

    private final ThanhToanQuanTriService service;

    @GetMapping
    public ResponseEntity<Page<ThanhToanQuanTriResponse>> layDanhSach(Pageable pageable) {
        return ResponseEntity.ok(service.layDanhSach(pageable));
    }

    @PostMapping("/don-hang/{donHangId}/xac-nhan")
    public ResponseEntity<ThanhToanQuanTriResponse> xacNhan(@PathVariable Long donHangId,
                                                            @RequestParam(required = false) String maGiaoDich) {
        return ResponseEntity.ok(service.xacNhan(donHangId, maGiaoDich));
    }

    @PostMapping("/don-hang/{donHangId}/that-bai")
    public ResponseEntity<ThanhToanQuanTriResponse> thatBai(@PathVariable Long donHangId,
                                                            @RequestParam String lyDo) {
        return ResponseEntity.ok(service.danhDauThatBai(donHangId, lyDo));
    }

    @PostMapping("/don-hang/{donHangId}/hoan-tien")
    public ResponseEntity<ThanhToanQuanTriResponse> hoanTien(@PathVariable Long donHangId) {
        return ResponseEntity.ok(service.hoanTien(donHangId));
    }
}
