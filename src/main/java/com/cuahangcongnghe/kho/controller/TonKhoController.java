package com.cuahangcongnghe.kho.controller;

import com.cuahangcongnghe.kho.entity.TonKho;
import com.cuahangcongnghe.kho.service.TonKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ton-kho")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
public class TonKhoController {

    private final TonKhoService tonKhoService;

    @GetMapping
    public ResponseEntity<Page<TonKho>> layTatCa(Pageable pageable) {
        return ResponseEntity.ok(tonKhoService.layTatCa(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TonKho> layTheoId(@PathVariable Long id) {
        return ResponseEntity.ok(tonKhoService.layTheoId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Long id) {
        tonKhoService.xoaTonKho(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<TonKho> layTheoSanPham(@PathVariable Long sanPhamId) {
        return ResponseEntity.ok(tonKhoService.layTheoSanPham(sanPhamId));
    }

    @GetMapping("/sap-het-hang")
    public ResponseEntity<Page<TonKho>> timSapHetHang(Pageable pageable) {
        return ResponseEntity.ok(tonKhoService.timSanPhamSapHetHang(pageable));
    }

    @PostMapping("/khoi-tao")
    public ResponseEntity<TonKho> khoiTao(@RequestParam Long sanPhamId,
                                           @RequestParam(defaultValue = "0") Integer soLuongBanDau,
                                           @RequestParam(required = false) String viTriKho) {
        TonKho tonKho = tonKhoService.khoiTaoTonKho(sanPhamId, soLuongBanDau, viTriKho);
        return ResponseEntity.status(HttpStatus.CREATED).body(tonKho);
    }

    @PostMapping("/nhap")
    public ResponseEntity<TonKho> nhapKho(@RequestParam Long sanPhamId,
                                           @RequestParam Integer soLuong) {
        return ResponseEntity.ok(tonKhoService.nhapKho(sanPhamId, soLuong));
    }

    @PostMapping("/xuat")
    public ResponseEntity<TonKho> xuatKho(@RequestParam Long sanPhamId,
                                           @RequestParam Integer soLuong) {
        return ResponseEntity.ok(tonKhoService.xuatKho(sanPhamId, soLuong));
    }

    @GetMapping("/kiem-tra-con-hang")
    public ResponseEntity<Boolean> kiemTraConHang(@RequestParam Long sanPhamId,
                                                   @RequestParam Integer soLuong) {
        return ResponseEntity.ok(tonKhoService.kiemTraConHang(sanPhamId, soLuong));
    }
}
