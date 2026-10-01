package com.cuahangcongnghe.sanpham.controller;

import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    // ===== Cong khai, khong can dang nhap (xem san pham) =====

    @GetMapping("/api/san-pham")
    public ResponseEntity<Page<SanPham>> layTatCa(Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.layTatCa(pageable));
    }

    @GetMapping("/api/san-pham/{id}")
    public ResponseEntity<SanPham> layTheoId(@PathVariable Long id) {
        return ResponseEntity.ok(sanPhamService.layTheoId(id));
    }

    @GetMapping("/api/san-pham/tim-kiem")
    public ResponseEntity<Page<SanPham>> timKiem(@RequestParam String tuKhoa, Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.timKiem(tuKhoa, pageable));
    }

    @GetMapping("/api/san-pham/khoang-gia")
    public ResponseEntity<Page<SanPham>> locTheoKhoangGia(@RequestParam BigDecimal giaTu,
                                                           @RequestParam BigDecimal giaDen,
                                                           Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.locTheoKhoangGia(giaTu, giaDen, pageable));
    }

    // ===== Chi quan tri vien (yeu cau ROLE_QUAN_TRI) =====
    // Luu y: nam ngoai "/api/san-pham/**" nen KHONG bi loach vao whitelist cong khai trong CauHinhBaoMat

    @PostMapping("/api/quan-tri/san-pham")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<SanPham> taoMoi(@Valid @RequestBody SanPham sanPham) {
        SanPham daTao = sanPhamService.taoMoi(sanPham);
        return ResponseEntity.status(HttpStatus.CREATED).body(daTao);
    }

    @PutMapping("/api/quan-tri/san-pham/{id}")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<SanPham> capNhat(@PathVariable Long id, @Valid @RequestBody SanPham sanPham) {
        return ResponseEntity.ok(sanPhamService.capNhat(id, sanPham));
    }

    @DeleteMapping("/api/quan-tri/san-pham/{id}")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<Void> xoa(@PathVariable Long id) {
        sanPhamService.xoa(id);
        return ResponseEntity.noContent().build();
    }

}
