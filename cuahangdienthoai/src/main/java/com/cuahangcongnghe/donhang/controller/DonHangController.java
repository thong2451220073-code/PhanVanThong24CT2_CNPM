package com.cuahangcongnghe.donhang.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.donhang.dto.DonHangResponse;
import com.cuahangcongnghe.donhang.dto.TaoDonHangRequest;
import com.cuahangcongnghe.donhang.dto.TaoDonHangThuCongRequest;
import com.cuahangcongnghe.donhang.entity.TrangThaiDonHang;
import com.cuahangcongnghe.donhang.service.DonHangService;
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
@RequestMapping("/api/don-hang")
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;

    @PostMapping
    public ResponseEntity<DonHangResponse> taoDonHang(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @Valid @RequestBody TaoDonHangRequest request) {
        DonHangResponse response = donHangService.taoDonHangTuGioHang(nguoiDung.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Nhan vien ban hang / quan tri tao don ho khach mua truc tiep tai cua hang hoac qua dien thoai.
     */
    @PostMapping("/thu-cong")
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<DonHangResponse> taoDonHangThuCong(
            @AuthenticationPrincipal ChiTietNguoiDung nhanVien,
            @Valid @RequestBody TaoDonHangThuCongRequest request) {
        DonHangResponse response = donHangService.taoDonHangThuCong(nhanVien.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonHangResponse> layTheoId(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long id) {
        return ResponseEntity.ok(donHangService.layTheoId(id, nguoiDung.getId()));
    }

    @GetMapping("/ma/{maDonHang}")
    public ResponseEntity<DonHangResponse> layTheoMaDonHang(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable String maDonHang) {
        return ResponseEntity.ok(donHangService.layTheoMaDonHang(maDonHang, nguoiDung.getId()));
    }

    @GetMapping("/cua-toi")
    public ResponseEntity<Page<DonHangResponse>> layDonHangCuaToi(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @RequestParam(required = false) TrangThaiDonHang trangThai,
            Pageable pageable) {
        return ResponseEntity.ok(donHangService.layTheoNguoiDung(nguoiDung.getId(), trangThai, pageable));
    }

    /**
     * Nhan vien/quan tri tra cuu lich su don hang cua mot khach hang cu the (ho tro CSKH qua dien thoai).
     */
    @GetMapping("/khach-hang/{nguoiDungId}")
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<Page<DonHangResponse>> layDonHangTheoKhachHang(
            @PathVariable Long nguoiDungId,
            @RequestParam(required = false) TrangThaiDonHang trangThai,
            Pageable pageable) {
        return ResponseEntity.ok(donHangService.layTheoNguoiDung(nguoiDungId, trangThai, pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<Page<DonHangResponse>> layTatCaDonHang(
            @RequestParam(required = false) TrangThaiDonHang trangThai,
            Pageable pageable) {
        return ResponseEntity.ok(donHangService.layTatCa(trangThai, pageable));
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI','ROLE_NHAN_VIEN')")
    public ResponseEntity<DonHangResponse> capNhatTrangThai(
            @PathVariable Long id,
            @RequestParam TrangThaiDonHang trangThai,
            @RequestParam(required = false) String ghiChu) {
        return ResponseEntity.ok(donHangService.capNhatTrangThai(id, trangThai, ghiChu));
    }

    @PatchMapping("/{id}/huy")
    public ResponseEntity<DonHangResponse> huyDonHang(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long id,
            @RequestParam(required = false) String lyDo) {
        return ResponseEntity.ok(donHangService.huyDonHang(id, nguoiDung.getId(), lyDo));
    }
}
