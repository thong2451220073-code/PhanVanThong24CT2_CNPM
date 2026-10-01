package com.cuahangcongnghe.nguoidung.controller;

import com.cuahangcongnghe.nguoidung.dto.CapNhatNguoiDungRequest;
import com.cuahangcongnghe.nguoidung.dto.GanVaiTroRequest;
import com.cuahangcongnghe.nguoidung.dto.NguoiDungResponse;
import com.cuahangcongnghe.nguoidung.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    // ===== Nguoi dung tu quan ly ho so cua chinh minh =====

    @GetMapping("/api/nguoi-dung/toi")
    public ResponseEntity<NguoiDungResponse> layThongTinCuaToi(Authentication authentication) {
        return ResponseEntity.ok(nguoiDungService.layThongTinTheoEmail(authentication.getName()));
    }

    @PutMapping("/api/nguoi-dung/toi")
    public ResponseEntity<NguoiDungResponse> capNhatThongTinCuaToi(
            Authentication authentication,
            @Valid @RequestBody CapNhatNguoiDungRequest request) {
        return ResponseEntity.ok(nguoiDungService.capNhatThongTin(authentication.getName(), request));
    }

    // ===== Quan tri vien quan ly nguoi dung (yeu cau ROLE_QUAN_TRI) =====

    @GetMapping("/api/quan-tri/nguoi-dung")
    public ResponseEntity<List<NguoiDungResponse>> layTatCaNguoiDung() {
        return ResponseEntity.ok(nguoiDungService.layTatCaNguoiDung());
    }

    @PatchMapping("/api/quan-tri/nguoi-dung/{id}/khoa")
    public ResponseEntity<Void> khoaTaiKhoan(@PathVariable Long id) {
        nguoiDungService.khoaTaiKhoan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/quan-tri/nguoi-dung/{id}/mo-khoa")
    public ResponseEntity<Void> moKhoaTaiKhoan(@PathVariable Long id) {
        nguoiDungService.moKhoaTaiKhoan(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gan (thay the) danh sach vai tro cho mot tai khoan - dung de chi dinh
     * mot khach hang tro thanh Nhan vien ban hang / Nhan vien kho.
     * Vi du body: { "danhSachVaiTro": ["ROLE_NHAN_VIEN"] }
     */
    @PatchMapping("/api/quan-tri/nguoi-dung/{id}/vai-tro")
    public ResponseEntity<NguoiDungResponse> ganVaiTro(@PathVariable Long id,
                                                        @Valid @RequestBody GanVaiTroRequest request) {
        return ResponseEntity.ok(nguoiDungService.ganVaiTro(id, request.getDanhSachVaiTro()));
    }
}
