package com.cuahangcongnghe.xacthuc.controller;

import com.cuahangcongnghe.xacthuc.dto.DangKyRequest;
import com.cuahangcongnghe.xacthuc.dto.DangNhapRequest;
import com.cuahangcongnghe.xacthuc.dto.DangNhapResponse;
import com.cuahangcongnghe.xacthuc.service.XacThucService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xac-thuc")
@RequiredArgsConstructor
public class XacThucController {

    private final XacThucService xacThucService;

    @PostMapping("/dang-ky")
    public ResponseEntity<DangNhapResponse> dangKy(@Valid @RequestBody DangKyRequest request) {
        return ResponseEntity.ok(xacThucService.dangKy(request));
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<DangNhapResponse> dangNhap(@Valid @RequestBody DangNhapRequest request) {
        return ResponseEntity.ok(xacThucService.dangNhap(request));
    }
}
