package com.cuahangcongnghe.ngoaile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class XuLyNgoaiLe {

    @ExceptionHandler(KhongTimThayException.class)
    public ResponseEntity<Map<String, Object>> xuLyKhongTimThay(KhongTimThayException ex) {
        return taoPhanHoiLoi(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(YeuCauKhongHopLeException.class)
    public ResponseEntity<Map<String, Object>> xuLyYeuCauKhongHopLe(YeuCauKhongHopLeException ex) {
        return taoPhanHoiLoi(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(KhongCoQuyenException.class)
    public ResponseEntity<Map<String, Object>> xuLyKhongCoQuyen(KhongCoQuyenException ex) {
        return taoPhanHoiLoi(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> xuLyTuChoiTruyCap(AccessDeniedException ex) {
        return taoPhanHoiLoi(HttpStatus.FORBIDDEN, "Ban khong co quyen truy cap chuc nang nay");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> xuLySaiThongTinDangNhap(BadCredentialsException ex) {
        return taoPhanHoiLoi(HttpStatus.UNAUTHORIZED, "Email hoac mat khau khong dung");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> xuLyDuLieuKhongHopLe(MethodArgumentNotValidException ex) {
        Map<String, String> loiTrenTruong = new HashMap<>();
        for (FieldError loi : ex.getBindingResult().getFieldErrors()) {
            loiTrenTruong.put(loi.getField(), loi.getDefaultMessage());
        }

        Map<String, Object> phanHoi = new HashMap<>();
        phanHoi.put("thoiGian", LocalDateTime.now());
        phanHoi.put("trangThai", HttpStatus.BAD_REQUEST.value());
        phanHoi.put("thongBao", "Du lieu gui len khong hop le");
        phanHoi.put("chiTietLoi", loiTrenTruong);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(phanHoi);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiChung(Exception ex) {
        log.error("Loi khong xac dinh: ", ex);
        return taoPhanHoiLoi(HttpStatus.INTERNAL_SERVER_ERROR, "Da xay ra loi he thong, vui long thu lai sau");
    }

    private ResponseEntity<Map<String, Object>> taoPhanHoiLoi(HttpStatus trangThai, String thongBao) {
        Map<String, Object> phanHoi = new HashMap<>();
        phanHoi.put("thoiGian", LocalDateTime.now());
        phanHoi.put("trangThai", trangThai.value());
        phanHoi.put("thongBao", thongBao);
        return ResponseEntity.status(trangThai).body(phanHoi);
    }
}
