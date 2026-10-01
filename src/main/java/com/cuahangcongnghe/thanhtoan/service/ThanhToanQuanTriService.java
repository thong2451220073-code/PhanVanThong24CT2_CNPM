package com.cuahangcongnghe.thanhtoan.service;

import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.repository.DonHangRepository;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.thanhtoan.dto.ThanhToanQuanTriResponse;
import com.cuahangcongnghe.thanhtoan.entity.ThanhToan;
import com.cuahangcongnghe.thanhtoan.entity.ThanhToan.TrangThaiThanhToan;
import com.cuahangcongnghe.thanhtoan.repository.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Quan ly thanh toan cho quan tri/nhan vien. Lam viec theo don hang vi cac don COD/VIETQR
 * khong tu dong co ban ghi thanh_toan; ban ghi se duoc tao khi nhan vien xac nhan.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ThanhToanQuanTriService {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;

    @Transactional(readOnly = true)
    public Page<ThanhToanQuanTriResponse> layDanhSach(Pageable pageable) {
        Page<DonHang> trang = donHangRepository.findAll(pageable);
        List<Long> ids = trang.getContent().stream().map(DonHang::getId).toList();
        Map<Long, ThanhToan> theoDon = ids.isEmpty() ? Map.of()
                : thanhToanRepository.findByDonHangIdIn(ids).stream()
                    .collect(Collectors.toMap(t -> t.getDonHang().getId(), Function.identity()));
        return trang.map(dh -> chuyenSangResponse(dh, theoDon.get(dh.getId())));
    }

    public ThanhToanQuanTriResponse xacNhan(Long donHangId, String maGiaoDich) {
        DonHang donHang = timDonHang(donHangId);
        ThanhToan tt = timHoacTao(donHang);
        if (tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN) {
            throw new YeuCauKhongHopLeException("Đơn hàng này đã được xác nhận thanh toán");
        }
        tt.setTrangThai(TrangThaiThanhToan.DA_THANH_TOAN);
        tt.setMaGiaoDich(maGiaoDich == null || maGiaoDich.isBlank() ? null : maGiaoDich.trim());
        tt.setLyDoThatBai(null);
        tt.setNgayThanhToan(LocalDateTime.now());
        thanhToanRepository.save(tt);
        donHang.setDaThanhToan(true);
        donHangRepository.save(donHang);
        return chuyenSangResponse(donHang, tt);
    }

    public ThanhToanQuanTriResponse danhDauThatBai(Long donHangId, String lyDo) {
        DonHang donHang = timDonHang(donHangId);
        ThanhToan tt = timHoacTao(donHang);
        if (tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN) {
            throw new YeuCauKhongHopLeException("Thanh toán đã hoàn tất, hãy dùng chức năng hoàn tiền");
        }
        tt.setTrangThai(TrangThaiThanhToan.THAT_BAI);
        tt.setLyDoThatBai(lyDo);
        thanhToanRepository.save(tt);
        return chuyenSangResponse(donHang, tt);
    }

    public ThanhToanQuanTriResponse hoanTien(Long donHangId) {
        DonHang donHang = timDonHang(donHangId);
        ThanhToan tt = thanhToanRepository.findByDonHangId(donHangId).orElse(null);
        boolean daThu = (tt != null && tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                || (tt == null && donHang.isDaThanhToan());
        if (!daThu) {
            throw new YeuCauKhongHopLeException("Chỉ có thể hoàn tiền cho đơn đã thanh toán");
        }
        if (tt == null) {
            tt = timHoacTao(donHang);
            tt.setNgayThanhToan(LocalDateTime.now());
        }
        tt.setTrangThai(TrangThaiThanhToan.DA_HOAN_TIEN);
        thanhToanRepository.save(tt);
        donHang.setDaThanhToan(false);
        donHangRepository.save(donHang);
        return chuyenSangResponse(donHang, tt);
    }

    private DonHang timDonHang(Long id) {
        return donHangRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng với id: " + id));
    }

    private ThanhToan timHoacTao(DonHang donHang) {
        return thanhToanRepository.findByDonHangId(donHang.getId()).orElseGet(() ->
                thanhToanRepository.save(ThanhToan.builder()
                        .donHang(donHang)
                        .phuongThuc(doiPhuongThuc(donHang.getPhuongThucThanhToan()))
                        .soTien(donHang.getTongThanhToan())
                        .trangThai(TrangThaiThanhToan.CHO_THANH_TOAN)
                        .build()));
    }

    private static ThanhToan.PhuongThucThanhToan doiPhuongThuc(String s) {
        try {
            return ThanhToan.PhuongThucThanhToan.valueOf(s == null ? "COD" : s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ThanhToan.PhuongThucThanhToan.COD;
        }
    }

    private ThanhToanQuanTriResponse chuyenSangResponse(DonHang dh, ThanhToan tt) {
        String trangThai;
        if (tt != null) {
            trangThai = tt.getTrangThai().name();
            if (tt.getTrangThai() == TrangThaiThanhToan.CHO_THANH_TOAN && dh.isDaThanhToan()) {
                trangThai = TrangThaiThanhToan.DA_THANH_TOAN.name();
            }
        } else {
            trangThai = dh.isDaThanhToan() ? TrangThaiThanhToan.DA_THANH_TOAN.name()
                                           : TrangThaiThanhToan.CHO_THANH_TOAN.name();
        }
        return ThanhToanQuanTriResponse.builder()
                .donHangId(dh.getId())
                .maDonHang(dh.getMaDonHang())
                .trangThaiDonHang(dh.getTrangThai() == null ? null : dh.getTrangThai().name())
                .hoTenNguoiNhan(dh.getHoTenNguoiNhan())
                .soDienThoaiNguoiNhan(dh.getSoDienThoaiNguoiNhan())
                .soTien(tt != null ? tt.getSoTien() : dh.getTongThanhToan())
                .phuongThuc(tt != null ? tt.getPhuongThuc().name() : dh.getPhuongThucThanhToan())
                .trangThai(trangThai)
                .maGiaoDich(tt != null ? tt.getMaGiaoDich() : null)
                .lyDoThatBai(tt != null ? tt.getLyDoThatBai() : null)
                .ngayTao(dh.getNgayTao())
                .ngayThanhToan(tt != null ? tt.getNgayThanhToan() : null)
                .build();
    }
}
