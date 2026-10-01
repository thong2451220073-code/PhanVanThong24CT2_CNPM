package com.cuahangcongnghe.nhapkho.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.nhapkho.dto.TaoPhieuNhapKhoRequest;
import com.cuahangcongnghe.kho.dto.PhieuKhoResponse;
import com.cuahangcongnghe.nhapkho.service.NhapKhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nhap-kho")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
public class NhapKhoController {

    private final NhapKhoService nhapKhoService;

    @GetMapping
    public ResponseEntity<List<PhieuKhoResponse>> layTatCa() {
        return ResponseEntity.ok(nhapKhoService.layTatCa());
    }

    @GetMapping("/{maPhieu}")
    public ResponseEntity<PhieuKhoResponse> layTheoMa(@PathVariable String maPhieu) {
        return ResponseEntity.ok(nhapKhoService.layTheoMa(maPhieu));
    }

    @PostMapping
    public ResponseEntity<PhieuKhoResponse> taoPhieu(@Valid @RequestBody TaoPhieuNhapKhoRequest yeuCau,
                                                  @AuthenticationPrincipal ChiTietNguoiDung nguoiDungHienTai) {
        PhieuKhoResponse phieu = nhapKhoService.taoPhieu(yeuCau, nguoiDungHienTai.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(phieu);
    }

    @PostMapping("/{maPhieu}/xac-nhan")
    public ResponseEntity<PhieuKhoResponse> xacNhan(@PathVariable String maPhieu) {
        return ResponseEntity.ok(nhapKhoService.xacNhanNhapKho(maPhieu));
    }

    @PostMapping("/{maPhieu}/huy")
    public ResponseEntity<PhieuKhoResponse> huyPhieu(@PathVariable String maPhieu) {
        return ResponseEntity.ok(nhapKhoService.huyPhieu(maPhieu));
    }
}
