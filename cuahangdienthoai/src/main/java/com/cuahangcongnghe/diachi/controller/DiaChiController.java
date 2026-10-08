package com.cuahangcongnghe.diachi.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.diachi.entity.DiaChi;
import com.cuahangcongnghe.diachi.repository.DiaChiRepository;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dia-chi")
@RequiredArgsConstructor
public class DiaChiController {
    private final DiaChiRepository diaChiRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping
    public ResponseEntity<List<DiaChi>> layCuaToi(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung) {
        return ResponseEntity.ok(diaChiRepository.findByNguoiDungId(nguoiDung.getId()));
    }

    @PostMapping
    public ResponseEntity<DiaChi> taoMoi(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                         @RequestBody DiaChi request) {
        NguoiDung chuDiaChi = nguoiDungRepository.findById(nguoiDung.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        DiaChi diaChi = new DiaChi();
        diaChi.setNguoiDung(chuDiaChi);
        ganDuLieu(diaChi, request);
        List<DiaChi> hienCo = diaChiRepository.findByNguoiDungId(nguoiDung.getId());
        diaChi.setLaMacDinh(request.isLaMacDinh() || hienCo.isEmpty());
        if (diaChi.isLaMacDinh()) boMacDinhCu(nguoiDung.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(diaChiRepository.save(diaChi));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaChi> capNhat(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                           @PathVariable Long id, @RequestBody DiaChi request) {
        DiaChi diaChi = layVaKiemTra(id, nguoiDung.getId());
        ganDuLieu(diaChi, request);
        if (request.isLaMacDinh()) {
            boMacDinhCu(nguoiDung.getId());
            diaChi.setLaMacDinh(true);
        }
        return ResponseEntity.ok(diaChiRepository.save(diaChi));
    }

    @PatchMapping("/{id}/mac-dinh")
    public ResponseEntity<DiaChi> datMacDinh(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                              @PathVariable Long id) {
        DiaChi diaChi = layVaKiemTra(id, nguoiDung.getId());
        boMacDinhCu(nguoiDung.getId());
        diaChi.setLaMacDinh(true);
        return ResponseEntity.ok(diaChiRepository.save(diaChi));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                    @PathVariable Long id) {
        DiaChi diaChi = layVaKiemTra(id, nguoiDung.getId());
        boolean laMacDinh = diaChi.isLaMacDinh();
        diaChiRepository.delete(diaChi);
        if (laMacDinh) {
            List<DiaChi> conLai = diaChiRepository.findByNguoiDungId(nguoiDung.getId());
            if (!conLai.isEmpty()) {
                conLai.get(0).setLaMacDinh(true);
                diaChiRepository.save(conLai.get(0));
            }
        }
        return ResponseEntity.noContent().build();
    }

    private DiaChi layVaKiemTra(Long id, Long nguoiDungId) {
        DiaChi diaChi = diaChiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));
        if (!diaChi.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new AccessDeniedException("Bạn không có quyền với địa chỉ này");
        }
        return diaChi;
    }

    private void boMacDinhCu(Long nguoiDungId) {
        diaChiRepository.findByNguoiDungId(nguoiDungId).forEach(a -> {
            if (a.isLaMacDinh()) {
                a.setLaMacDinh(false);
                diaChiRepository.save(a);
            }
        });
    }

    private void ganDuLieu(DiaChi target, DiaChi source) {
        target.setHoTenNguoiNhan(kiemTra(source.getHoTenNguoiNhan(), "Họ tên người nhận"));
        target.setSoDienThoai(kiemTra(source.getSoDienThoai(), "Số điện thoại"));
        target.setDiaChiChiTiet(kiemTra(source.getDiaChiChiTiet(), "Địa chỉ chi tiết"));
        target.setPhuongXa(kiemTra(source.getPhuongXa(), "Phường/Xã"));
        target.setQuanHuyen(kiemTra(source.getQuanHuyen(), "Quận/Huyện"));
        target.setTinhThanh(kiemTra(source.getTinhThanh(), "Tỉnh/Thành phố"));
        target.setLoaiDiaChi(source.getLoaiDiaChi() == null || source.getLoaiDiaChi().isBlank() ? "NHA" : source.getLoaiDiaChi());
    }

    private String kiemTra(String value, String tenTruong) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(tenTruong + " không được để trống");
        return value.trim();
    }
}
