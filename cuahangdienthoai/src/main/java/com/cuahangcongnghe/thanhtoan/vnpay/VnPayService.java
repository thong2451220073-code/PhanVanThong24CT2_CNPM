package com.cuahangcongnghe.thanhtoan.vnpay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Tich hop cong thanh toan VNPAY (theo tai lieu chinh thuc cua VNPAY:
 * https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html).
 *
 * Luong hoat dong:
 * 1. Khach chon thanh toan VNPAY -> goi taoUrlThanhToan() de lay URL, redirect khach sang URL do.
 * 2. Khach thanh toan xong tren cong VNPAY -> VNPAY redirect trinh duyet khach ve vnpay.return-url
 *    kem theo cac tham so vnp_... va vnp_SecureHash -> can goi xacThucChuKy() de kiem tra tinh
 *    toan ven du lieu truoc khi cap nhat trang thai don hang.
 *
 * LUU Y BAO MAT: day la ban trien khai co ban, du de chay thu voi tai khoan sandbox.
 * Truoc khi dua vao san xuat that su, can:
 *  - Dang ky tai khoan doanh nghiep that voi VNPAY de lay TmnCode/HashSecret that.
 *  - Xu ly them webhook IPN (Instant Payment Notification) rieng, vi returnUrl chi chay
 *    o trinh duyet khach hang nen khong dang tin cay 100% (khach co the dong trang giua chung).
 */
@Service
public class VnPayService {

    private static final DateTimeFormatter DINH_DANG_THOI_GIAN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.version}")
    private String vnpVersion;

    /**
     * Tao URL de redirect khach hang sang cong thanh toan VNPAY.
     *
     * @param maThamChieuGiaoDich ma giao dich duy nhat (nen dung: maDonHang + "-" + thanhToanId
     *                            + so ngau nhien, de tranh trung neu khach thanh toan lai don cu)
     * @param soTien              so tien can thanh toan (VND)
     * @param thongTinDonHang     noi dung mo ta don hang hien thi tren cong thanh toan
     * @param diaChiIp            dia chi IP cua khach hang (lay tu HttpServletRequest)
     */
    public String taoUrlThanhToan(String maThamChieuGiaoDich, BigDecimal soTien, String thongTinDonHang, String diaChiIp) {
        String vnpCreateDate = LocalDateTime.now().format(DINH_DANG_THOI_GIAN);
        // VNPAY yeu cau so tien nhan len 100 (khong co phan thap phan)
        String vnpAmount = soTien.multiply(BigDecimal.valueOf(100)).toBigInteger().toString();

        Map<String, String> thamSo = new TreeMap<>();
        thamSo.put("vnp_Version", vnpVersion);
        thamSo.put("vnp_Command", "pay");
        thamSo.put("vnp_TmnCode", vnpTmnCode);
        thamSo.put("vnp_Amount", vnpAmount);
        thamSo.put("vnp_CurrCode", "VND");
        thamSo.put("vnp_TxnRef", maThamChieuGiaoDich);
        thamSo.put("vnp_OrderInfo", thongTinDonHang);
        thamSo.put("vnp_OrderType", "other");
        thamSo.put("vnp_Locale", "vn");
        thamSo.put("vnp_ReturnUrl", vnpReturnUrl);
        thamSo.put("vnp_IpAddr", (diaChiIp == null || diaChiIp.isBlank()) ? "127.0.0.1" : diaChiIp);
        thamSo.put("vnp_CreateDate", vnpCreateDate);

        String duLieuHash = xayDungChuoiHash(thamSo);
        String chuKy = hmacSha512(vnpHashSecret, duLieuHash);

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : thamSo.entrySet()) {
            queryString.append(urlEncode(entry.getKey())).append('=').append(urlEncode(entry.getValue())).append('&');
        }
        queryString.append("vnp_SecureHash=").append(chuKy);

        return vnpPayUrl + "?" + queryString;
    }

    /**
     * Xac thuc chu ky vnp_SecureHash tra ve tu VNPAY (dung cho ca return-url va IPN webhook).
     * Truyen vao TOAN BO tham so query nhan duoc tu VNPAY (bao gom vnp_SecureHash).
     */
    public boolean xacThucChuKy(Map<String, String> thamSoTraVe) {
        Map<String, String> thamSo = new TreeMap<>(thamSoTraVe);
        String chuKyNhanDuoc = thamSo.remove("vnp_SecureHash");
        thamSo.remove("vnp_SecureHashType");

        if (chuKyNhanDuoc == null) {
            return false;
        }

        String duLieuHash = xayDungChuoiHash(thamSo);
        String chuKyTinhLai = hmacSha512(vnpHashSecret, duLieuHash);
        return chuKyTinhLai.equalsIgnoreCase(chuKyNhanDuoc);
    }

    /** Ma giao dich VNPAY thanh cong la "00", ma khac la that bai/huy. */
    public boolean laGiaoDichThanhCong(Map<String, String> thamSoTraVe) {
        return "00".equals(thamSoTraVe.get("vnp_ResponseCode"));
    }

    public String taoMaThamChieuGiaoDich(String maDonHang) {
        int soNgauNhien = new SecureRandom().nextInt(9000) + 1000;
        return maDonHang + "-" + soNgauNhien;
    }

    private String xayDungChuoiHash(Map<String, String> thamSo) {
        StringBuilder chuoiHash = new StringBuilder();
        Iterator<Map.Entry<String, String>> it = thamSo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                chuoiHash.append(urlEncode(entry.getKey())).append('=').append(urlEncode(entry.getValue()));
                if (it.hasNext()) {
                    chuoiHash.append('&');
                }
            }
        }
        return chuoiHash.toString();
    }

    private String urlEncode(String giaTri) {
        return java.net.URLEncoder.encode(giaTri, StandardCharsets.UTF_8);
    }

    private String hmacSha512(String khoaBiMat, String duLieu) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec khoa = new SecretKeySpec(khoaBiMat.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(khoa);
            byte[] ketQua = hmac512.doFinal(duLieu.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : ketQua) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Khong the tao chu ky HMAC-SHA512 cho VNPAY", e);
        }
    }
}
