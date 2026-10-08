package com.cuahangcongnghe.xuatkho.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.xuatkho.dto.TaoPhieuXuatKhoRequest;
import com.cuahangcongnghe.kho.dto.PhieuKhoResponse;
import com.cuahangcongnghe.xuatkho.service.XuatKhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xuat-kho")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
public class XuatKhoController {

    private final XuatKhoService xuatKhoService;

    @GetMapping
    public ResponseEntity<List<PhieuKhoResponse>> layTatCa() {
        return ResponseEntity.ok(xuatKhoService.layTatCa());
    }

    @GetMapping("/{maPhieu}")
    public ResponseEntity<PhieuKhoResponse> layTheoMa(@PathVariable String maPhieu) {
        return ResponseEntity.ok(xuatKhoService.layTheoMa(maPhieu));
    }

    @PostMapping
    public ResponseEntity<PhieuKhoResponse> taoPhieu(@Valid @RequestBody TaoPhieuXuatKhoRequest yeuCau,
                                                  @AuthenticationPrincipal ChiTietNguoiDung nguoiDungHienTai) {
        PhieuKhoResponse phieu = xuatKhoService.taoPhieu(yeuCau, nguoiDungHienTai.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(phieu);
    }

    @PostMapping("/{maPhieu}/xac-nhan")
    public ResponseEntity<PhieuKhoResponse> xacNhan(@PathVariable String maPhieu) {
        return ResponseEntity.ok(xuatKhoService.xacNhanXuatKho(maPhieu));
    }

    @PostMapping("/{maPhieu}/huy")
    public ResponseEntity<PhieuKhoResponse> huyPhieu(@PathVariable String maPhieu) {
        return ResponseEntity.ok(xuatKhoService.huyPhieu(maPhieu));
    }
}
