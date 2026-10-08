package com.cuahangcongnghe.thanhtoan.vietqr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * Sinh ma VietQR de khach hang chuyen khoan thanh toan don hang.
 *
 * Dung dich vu anh QR mien phi cua VietQR.io (khong can dang ky API key):
 * https://img.vietqr.io/image/{bankBin}-{accountNo}-{template}.png?amount=...&addInfo=...&accountName=...
 *
 * Danh sach ma ngan hang (bankBin): https://api.vietqr.io/v2/banks
 * Cac template dep san co: compact2 (mac dinh, gon), compact, qr_only, print.
 */
@Service
public class VietQrService {

    @Value("${vietqr.bank-bin}")
    private String bankBin;

    @Value("${vietqr.account-no}")
    private String accountNo;

    @Value("${vietqr.account-name}")
    private String accountName;

    @Value("${vietqr.template}")
    private String template;

    /**
     * @param soTien       so tien can thanh toan (VND)
     * @param maDonHang    dung lam noi dung chuyen khoan, giup doi soat don hang tu dong
     */
    public String taoUrlAnhQr(BigDecimal soTien, String maDonHang) {
        String noiDungChuyenKhoan = taoNoiDung(maDonHang);
        String soTienNguyen = soTien.setScale(0, java.math.RoundingMode.HALF_UP).toBigInteger().toString();

        return String.format(
                "https://img.vietqr.io/image/%s-%s-%s.png?amount=%s&addInfo=%s&accountName=%s",
                encode(bankBin),
                encode(accountNo),
                encode(template),
                soTienNguyen,
                encode(noiDungChuyenKhoan),
                encode(accountName)
        );
    }

    /** Noi dung chuyen khoan ngan gon (ngan hang gioi han do dai), khong dau, chua ma don de doi soat. */
    public String taoNoiDung(String maDonHang) {
        return "TT " + maDonHang;
    }

    public String getAccountNo() { return accountNo; }

    public String getAccountName() { return accountName; }

    private String encode(String giaTri) {
        return java.net.URLEncoder.encode(giaTri, StandardCharsets.UTF_8);
    }
}
