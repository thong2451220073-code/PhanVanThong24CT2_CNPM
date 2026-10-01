package com.cuahangcongnghe.thanhtoan.controller;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.repository.DonHangRepository;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.thanhtoan.entity.ThanhToan;
import com.cuahangcongnghe.thanhtoan.repository.ThanhToanRepository;
import com.cuahangcongnghe.thanhtoan.service.ThanhToanService;
import com.cuahangcongnghe.thanhtoan.vietqr.VietQrService;
import com.cuahangcongnghe.thanhtoan.vnpay.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanController {

    private final ThanhToanService thanhToanService;
    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;
    private final VnPayService vnPayService;
    private final VietQrService vietQrService;

    // Quan tri vien / nhan vien duoc xem thanh toan cua bat ky don hang nao;
    // khach hang chi duoc xem thanh toan cua chinh minh.
    private static boolean laNhanSu(ChiTietNguoiDung nguoiDung) {
        return nguoiDung.getAuthorities().stream().anyMatch(quyen ->
                quyen.getAuthority().equals("ROLE_QUAN_TRI") || quyen.getAuthority().equals("ROLE_NHAN_VIEN"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThanhToan> layTheoId(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                @PathVariable Long id) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        return ResponseEntity.ok(thanhToanService.layTheoIdChoNguoiDung(id, nguoiDungId));
    }

    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<ThanhToan> layTheoDonHang(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                     @PathVariable Long donHangId) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        return ResponseEntity.ok(thanhToanService.layTheoDonHangChoNguoiDung(donHangId, nguoiDungId));
    }

    @PostMapping
    public ResponseEntity<ThanhToan> taoThanhToan(@AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
                                                   @RequestParam Long donHangId,
                                                   @RequestParam ThanhToan.PhuongThucThanhToan phuongThuc) {
        Long nguoiDungId = laNhanSu(nguoiDung) ? null : nguoiDung.getId();
        ThanhToan thanhToan = thanhToanService.taoThanhToan(donHangId, phuongThuc, nguoiDungId);
        return ResponseEntity.status(HttpStatus.CREATED).body(thanhToan);
    }

    /**
     * Endpoint danh cho webhook tu cong thanh toan (VNPay, Momo...) goi ve khi thanh toan thanh cong.
     * Tam thoi chi cho phep QUAN_TRI goi truc tiep (vi he thong chua tich hop xac thuc chu ky
     * cua cong thanh toan). Khi trien khai that, thay bang mot co che xac thuc rieng cho webhook
     * (vd. kiem tra chu ky HMAC cua VNPay/Momo) thay vi dua vao JWT nguoi dung.
     */
    @PostMapping("/{id}/xac-nhan")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<ThanhToan> xacNhanThanhCong(@PathVariable Long id, @RequestParam String maGiaoDich) {
        return ResponseEntity.ok(thanhToanService.xacNhanThanhToanThanhCong(id, maGiaoDich));
    }

    @PostMapping("/{id}/that-bai")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<ThanhToan> danhDauThatBai(@PathVariable Long id, @RequestParam String lyDo) {
        return ResponseEntity.ok(thanhToanService.danhDauThatBai(id, lyDo));
    }

    @PostMapping("/{id}/hoan-tien")
    @PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
    public ResponseEntity<ThanhToan> hoanTien(@PathVariable Long id) {
        return ResponseEntity.ok(thanhToanService.hoanTien(id));
    }

    // ===================== VNPAY (FR-06) =====================

    /**
     * Tao URL thanh toan VNPAY cho mot don hang. Frontend goi API nay roi redirect
     * trinh duyet cua khach sang URL tra ve.
     */
    @GetMapping("/vnpay/tao-url")
    public ResponseEntity<Map<String, String>> taoUrlVnPay(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @RequestParam Long donHangId,
            HttpServletRequest request) {

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng với id: " + donHangId));

        if (!donHang.getNguoiDung().getId().equals(nguoiDung.getId())) {
            throw new YeuCauKhongHopLeException("Đơn hàng không thuộc về bạn");
        }
        if (donHang.isDaThanhToan()) {
            throw new YeuCauKhongHopLeException("Đơn hàng này đã được thanh toán");
        }

        // Dam bao co ban ghi thanh toan tuong ung, tao moi neu chua co
        ThanhToan thanhToan = thanhToanRepository.findByDonHangId(donHangId)
                .orElseGet(() -> thanhToanService.taoThanhToan(donHangId, ThanhToan.PhuongThucThanhToan.VNPAY, nguoiDung.getId()));

        String maThamChieu = vnPayService.taoMaThamChieuGiaoDich(donHang.getMaDonHang());
        String diaChiIp = layDiaChiIp(request);
        String url = vnPayService.taoUrlThanhToan(
                maThamChieu, thanhToan.getSoTien(), "Thanh toan don hang " + donHang.getMaDonHang(), diaChiIp);

        Map<String, String> ketQua = new HashMap<>();
        ketQua.put("url", url);
        return ResponseEntity.ok(ketQua);
    }

    /**
     * VNPAY redirect trinh duyet cua khach ve day sau khi thanh toan xong.
     * Endpoint nay duoc phep truy cap cong khai (xem CauHinhBaoMat) vi trinh duyet
     * cua khach goi truc tiep, khong kem theo JWT cua he thong.
     */
    @GetMapping("/vnpay/xac-nhan")
    public ResponseEntity<Map<String, Object>> xuLyKetQuaVnPay(@RequestParam Map<String, String> thamSo) {
        Map<String, Object> phanHoi = new HashMap<>();

        if (!vnPayService.xacThucChuKy(thamSo)) {
            phanHoi.put("thanhCong", false);
            phanHoi.put("thongDiep", "Chữ ký không hợp lệ, dữ liệu có thể đã bị thay đổi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(phanHoi);
        }

        String vnpTxnRef = thamSo.get("vnp_TxnRef");
        String maDonHang = vnpTxnRef != null && vnpTxnRef.contains("-")
                ? vnpTxnRef.substring(0, vnpTxnRef.lastIndexOf('-'))
                : vnpTxnRef;

        DonHang donHang = donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng: " + maDonHang));

        ThanhToan thanhToan = thanhToanRepository.findByDonHangId(donHang.getId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy thanh toán cho đơn hàng: " + maDonHang));

        boolean thanhCong = vnPayService.laGiaoDichThanhCong(thamSo);
        if (thanhCong) {
            thanhToanService.xacNhanThanhToanThanhCong(thanhToan.getId(), thamSo.get("vnp_TransactionNo"));
            donHang.setDaThanhToan(true);
            donHangRepository.save(donHang);
        } else {
            thanhToanService.danhDauThatBai(thanhToan.getId(), "Mã lỗi VNPAY: " + thamSo.get("vnp_ResponseCode"));
        }

        phanHoi.put("thanhCong", thanhCong);
        phanHoi.put("maDonHang", maDonHang);
        phanHoi.put("maPhanHoiVnpay", thamSo.get("vnp_ResponseCode"));
        return ResponseEntity.ok(phanHoi);
    }

    private String layDiaChiIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // ===================== VietQR (FR-06) =====================

    /**
     * Sinh URL anh ma VietQR de khach chuyen khoan thanh toan don hang.
     */
    @GetMapping("/vietqr")
    public ResponseEntity<Map<String, String>> layMaVietQr(
            @AuthenticationPrincipal ChiTietNguoiDung nguoiDung,
            @RequestParam Long donHangId) {

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng với id: " + donHangId));

        if (!laNhanSu(nguoiDung) && !donHang.getNguoiDung().getId().equals(nguoiDung.getId())) {
            throw new YeuCauKhongHopLeException("Đơn hàng không thuộc về bạn");
        }

        String urlAnh = vietQrService.taoUrlAnhQr(donHang.getTongThanhToan(), donHang.getMaDonHang());

        Map<String, String> ketQua = new HashMap<>();
        ketQua.put("url", urlAnh);
        ketQua.put("soTien", donHang.getTongThanhToan().setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());
        ketQua.put("nganHang", "Vietcombank");
        ketQua.put("soTaiKhoan", vietQrService.getAccountNo());
        ketQua.put("tenTaiKhoan", vietQrService.getAccountName());
        ketQua.put("noiDung", vietQrService.taoNoiDung(donHang.getMaDonHang()));
        return ResponseEntity.ok(ketQua);
    }
}
