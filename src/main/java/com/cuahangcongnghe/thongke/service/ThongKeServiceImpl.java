package com.cuahangcongnghe.thongke.service;

import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.thongke.dto.DoanhThuResponse;
import com.cuahangcongnghe.thongke.dto.SanPhamBanChayResponse;
import com.cuahangcongnghe.thongke.repository.ThongKeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThongKeServiceImpl implements ThongKeService {

    private final ThongKeRepository thongKeRepository;

    @Override
    public DoanhThuResponse thongKeDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangThoiGian(tuNgay, denNgay);

        LocalDateTime batDau = tuNgay.atStartOfDay();
        LocalDateTime ketThuc = denNgay.atTime(23, 59, 59);

        BigDecimal tongDoanhThu = thongKeRepository.tinhTongDoanhThu(batDau, ketThuc);
        Long soDonHang = thongKeRepository.demSoDonHang(batDau, ketThuc);

        BigDecimal giaTriTrungBinh = BigDecimal.ZERO;
        if (soDonHang != null && soDonHang > 0) {
            giaTriTrungBinh = tongDoanhThu.divide(BigDecimal.valueOf(soDonHang), 2, RoundingMode.HALF_UP);
        }

        return new DoanhThuResponse(
                tuNgay + " - " + denNgay,
                tuNgay,
                denNgay,
                tongDoanhThu,
                soDonHang,
                giaTriTrungBinh
        );
    }

    @Override
    public List<DoanhThuResponse> thongKeDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangThoiGian(tuNgay, denNgay);

        List<DoanhThuResponse> ketQua = new ArrayList<>();
        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate ngayHienTai = tuNgay;
        while (!ngayHienTai.isAfter(denNgay)) {
            LocalDateTime batDauNgay = ngayHienTai.atStartOfDay();
            LocalDateTime ketThucNgay = ngayHienTai.atTime(23, 59, 59);

            BigDecimal doanhThuNgay = thongKeRepository.tinhTongDoanhThu(batDauNgay, ketThucNgay);
            Long soDonNgay = thongKeRepository.demSoDonHang(batDauNgay, ketThucNgay);

            BigDecimal giaTriTrungBinh = BigDecimal.ZERO;
            if (soDonNgay != null && soDonNgay > 0) {
                giaTriTrungBinh = doanhThuNgay.divide(BigDecimal.valueOf(soDonNgay), 2, RoundingMode.HALF_UP);
            }

            ketQua.add(new DoanhThuResponse(
                    ngayHienTai.format(dinhDangNgay),
                    ngayHienTai,
                    ngayHienTai,
                    doanhThuNgay,
                    soDonNgay,
                    giaTriTrungBinh
            ));

            ngayHienTai = ngayHienTai.plusDays(1);
        }

        return ketQua;
    }

    @Override
    public List<SanPhamBanChayResponse> topSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int soLuongTop) {
        kiemTraKhoangThoiGian(tuNgay, denNgay);

        if (soLuongTop <= 0) {
            throw new YeuCauKhongHopLeException("So luong top phai lon hon 0");
        }

        LocalDateTime batDau = tuNgay.atStartOfDay();
        LocalDateTime ketThuc = denNgay.atTime(23, 59, 59);

        List<Object[]> hangs = thongKeRepository.timSanPhamBanChay(batDau, ketThuc);

        List<SanPhamBanChayResponse> ketQua = new ArrayList<>();
        for (int i = 0; i < hangs.size() && i < soLuongTop; i++) {
            Object[] hang = hangs.get(i);
            ketQua.add(new SanPhamBanChayResponse(
                    (Long) hang[0],
                    (String) hang[1],
                    (String) hang[2],
                    (Long) hang[3],
                    (BigDecimal) hang[4]
            ));
        }

        return ketQua;
    }

    private void kiemTraKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay.isAfter(denNgay)) {
            throw new YeuCauKhongHopLeException("Tu ngay phai truoc hoac bang den ngay");
        }
    }
}
