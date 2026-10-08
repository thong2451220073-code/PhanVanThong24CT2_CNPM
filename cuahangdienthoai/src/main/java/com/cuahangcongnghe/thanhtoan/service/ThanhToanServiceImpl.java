package com.cuahangcongnghe.thanhtoan.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.repository.DonHangRepository;
import com.cuahangcongnghe.ngoaile.KhongCoQuyenException;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.thanhtoan.entity.ThanhToan;
import com.cuahangcongnghe.thanhtoan.repository.ThanhToanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ThanhToanServiceImpl implements ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;

    @Override
    @Transactional(readOnly = true)
    public ThanhToan layTheoId(Long id) {
        return thanhToanRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay thanh toan voi id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ThanhToan layTheoDonHang(Long donHangId) {
        return thanhToanRepository.findByDonHangId(donHangId)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay thanh toan cho don hang: " + donHangId));
    }

    @Override
    @Transactional(readOnly = true)
    public ThanhToan layTheoIdChoNguoiDung(Long id, Long nguoiDungId) {
        ThanhToan thanhToan = layTheoId(id);
        kiemTraChuSoHuu(thanhToan.getDonHang(), nguoiDungId);
        return thanhToan;
    }

    @Override
    @Transactional(readOnly = true)
    public ThanhToan layTheoDonHangChoNguoiDung(Long donHangId, Long nguoiDungId) {
        ThanhToan thanhToan = layTheoDonHang(donHangId);
        kiemTraChuSoHuu(thanhToan.getDonHang(), nguoiDungId);
        return thanhToan;
    }

    private void kiemTraChuSoHuu(DonHang donHang, Long nguoiDungId) {
        if (nguoiDungId != null && !donHang.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Ban khong co quyen truy cap thong tin thanh toan nay");
        }
    }

    @Override
    public ThanhToan taoThanhToan(Long donHangId, ThanhToan.PhuongThucThanhToan phuongThuc, Long nguoiDungId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay don hang voi id: " + donHangId));
        kiemTraChuSoHuu(donHang, nguoiDungId);

        if (thanhToanRepository.findByDonHangId(donHangId).isPresent()) {
            throw new YeuCauKhongHopLeException("Don hang nay da co ban ghi thanh toan");
        }

        ThanhToan thanhToan = ThanhToan.builder()
                .donHang(donHang)
                .phuongThuc(phuongThuc)
                .soTien(donHang.getTongThanhToan())
                .trangThai(ThanhToan.TrangThaiThanhToan.CHO_THANH_TOAN)
                .build();

        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public ThanhToan xacNhanThanhToanThanhCong(Long thanhToanId, String maGiaoDich) {
        ThanhToan thanhToan = layTheoId(thanhToanId);

        if (thanhToan.getTrangThai() == ThanhToan.TrangThaiThanhToan.DA_THANH_TOAN) {
            throw new YeuCauKhongHopLeException("Thanh toan nay da duoc xac nhan truoc do");
        }

        thanhToan.setTrangThai(ThanhToan.TrangThaiThanhToan.DA_THANH_TOAN);
        thanhToan.setMaGiaoDich(maGiaoDich);
        thanhToan.setNgayThanhToan(LocalDateTime.now());

        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public ThanhToan danhDauThatBai(Long thanhToanId, String lyDo) {
        ThanhToan thanhToan = layTheoId(thanhToanId);
        thanhToan.setTrangThai(ThanhToan.TrangThaiThanhToan.THAT_BAI);
        thanhToan.setLyDoThatBai(lyDo);
        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public ThanhToan hoanTien(Long thanhToanId) {
        ThanhToan thanhToan = layTheoId(thanhToanId);

        if (thanhToan.getTrangThai() != ThanhToan.TrangThaiThanhToan.DA_THANH_TOAN) {
            throw new YeuCauKhongHopLeException("Chi co the hoan tien cho thanh toan da hoan thanh");
        }

        thanhToan.setTrangThai(ThanhToan.TrangThaiThanhToan.DA_HOAN_TIEN);
        return thanhToanRepository.save(thanhToan);
    }
}
