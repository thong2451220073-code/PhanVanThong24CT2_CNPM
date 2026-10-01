package com.cuahangcongnghe.hoadon;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.repository.DonHangRepository;
import com.cuahangcongnghe.ngoaile.KhongCoQuyenException;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Xuat hoa don PDF cho don hang (FR-06).
 */
@RestController
@RequestMapping("/api/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final DonHangRepository donHangRepository;
    private final HoaDonPdfService hoaDonPdfService;

    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<ByteArrayResource> taiHoaDonPdf(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @PathVariable Long donHangId) {

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng với id: " + donHangId));

        boolean laNhanSu = nguoiDung.getAuthorities().stream()
                .anyMatch(quyen -> quyen.getAuthority().equals("ROLE_QUAN_TRI")
                        || quyen.getAuthority().equals("ROLE_NHAN_VIEN"));

        if (!laNhanSu && !donHang.getNguoiDung().getId().equals(nguoiDung.getId())) {
            throw new KhongCoQuyenException("Bạn không có quyền xem hóa đơn của đơn hàng này");
        }

        byte[] pdf = hoaDonPdfService.xuatHoaDonPdf(donHang);
        ByteArrayResource resource = new ByteArrayResource(pdf);

        String tenFile = "hoa-don-" + donHang.getMaDonHang() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(tenFile).build().toString())
                .contentLength(pdf.length)
                .body(resource);
    }
}
