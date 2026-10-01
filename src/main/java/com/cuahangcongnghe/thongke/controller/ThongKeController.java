package com.cuahangcongnghe.thongke.controller;

import com.cuahangcongnghe.thongke.dto.DoanhThuResponse;
import com.cuahangcongnghe.thongke.dto.SanPhamBanChayResponse;
import com.cuahangcongnghe.thongke.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
public class ThongKeController {

    private final ThongKeService thongKeService;

    @GetMapping("/doanh-thu")
    public ResponseEntity<DoanhThuResponse> thongKeDoanhThu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.thongKeDoanhThu(tuNgay, denNgay));
    }

    @GetMapping("/doanh-thu/theo-ngay")
    public ResponseEntity<List<DoanhThuResponse>> thongKeDoanhThuTheoNgay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.thongKeDoanhThuTheoNgay(tuNgay, denNgay));
    }

    @GetMapping("/san-pham-ban-chay")
    public ResponseEntity<List<SanPhamBanChayResponse>> topSanPhamBanChay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "10") int soLuongTop) {
        return ResponseEntity.ok(thongKeService.topSanPhamBanChay(tuNgay, denNgay, soLuongTop));
    }
}
