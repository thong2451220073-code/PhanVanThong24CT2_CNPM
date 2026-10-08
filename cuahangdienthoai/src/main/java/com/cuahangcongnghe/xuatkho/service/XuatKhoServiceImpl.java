package com.cuahangcongnghe.xuatkho.service;

import com.cuahangcongnghe.kho.dto.PhieuKhoResponse;
import com.cuahangcongnghe.kho.entity.PhieuKho;
import com.cuahangcongnghe.kho.repository.PhieuKhoRepository;
import com.cuahangcongnghe.kho.service.TonKhoService;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;
import com.cuahangcongnghe.xuatkho.dto.ChiTietXuatKhoRequest;
import com.cuahangcongnghe.xuatkho.dto.TaoPhieuXuatKhoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class XuatKhoServiceImpl implements XuatKhoService {

    private final PhieuKhoRepository phieuKhoRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final TonKhoService tonKhoService;

    @Override
    @Transactional(readOnly = true)
    public List<PhieuKhoResponse> layTatCa() {
        return PhieuKhoResponse.nhomTheoMaPhieu(
                phieuKhoRepository.findByLoaiPhieuOrderByNgayTaoDescIdAsc(PhieuKho.LoaiPhieu.XUAT));
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuKhoResponse layTheoMa(String maPhieu) {
        return PhieuKhoResponse.tuCacDong(layCacDong(maPhieu));
    }

    @Override
    public PhieuKhoResponse taoPhieu(TaoPhieuXuatKhoRequest yeuCau, Long nguoiTaoId) {
        NguoiDung nguoiTao = nguoiDungRepository.findById(nguoiTaoId)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay nguoi dung voi id: " + nguoiTaoId));

        String maPhieu = sinhMaPhieu();
        Set<Long> daCo = new HashSet<>();
        List<PhieuKho> cacDong = new ArrayList<>();

        for (ChiTietXuatKhoRequest ct : yeuCau.getDanhSachChiTiet()) {
            if (!daCo.add(ct.getSanPhamId())) {
                throw new YeuCauKhongHopLeException("San pham id " + ct.getSanPhamId() + " bi lap trong phieu");
            }
            SanPham sanPham = sanPhamRepository.findById(ct.getSanPhamId())
                    .orElseThrow(() -> new KhongTimThayException("Khong tim thay san pham voi id: " + ct.getSanPhamId()));

            BigDecimal donGia = ct.getDonGia() != null ? ct.getDonGia() : sanPham.getGia();

            cacDong.add(PhieuKho.builder()
                    .maPhieu(maPhieu)
                    .loaiPhieu(PhieuKho.LoaiPhieu.XUAT)
                    .sanPham(sanPham)
                    .soLuong(ct.getSoLuong())
                    .donGia(donGia)
                    .lyDoXuat(yeuCau.getLyDoXuat())
                    .donHangId(yeuCau.getDonHangId())
                    .ghiChu(yeuCau.getGhiChu())
                    .nguoiTao(nguoiTao)
                    .trangThai(PhieuKho.TrangThaiPhieuKho.CHO_XU_LY)
                    .build());
        }

        return PhieuKhoResponse.tuCacDong(phieuKhoRepository.saveAll(cacDong));
    }

    @Override
    public PhieuKhoResponse xacNhanXuatKho(String maPhieu) {
        List<PhieuKho> cacDong = layCacDong(maPhieu);
        kiemTraDangCho(cacDong, "xac nhan");

        // Kiem tra du hang truoc khi tru, tranh tru mot phan roi moi phat hien thieu hang
        for (PhieuKho dong : cacDong) {
            if (!tonKhoService.kiemTraConHang(dong.getSanPham().getId(), dong.getSoLuong())) {
                throw new YeuCauKhongHopLeException(
                        "San pham '" + dong.getSanPham().getTenSanPham() + "' khong du ton kho de xuat");
            }
        }

        for (PhieuKho dong : cacDong) {
            tonKhoService.xuatKho(dong.getSanPham().getId(), dong.getSoLuong());
            dong.setTrangThai(PhieuKho.TrangThaiPhieuKho.DA_XUAT_KHO);
            dong.setNgayHoanThanh(LocalDateTime.now());
        }

        return PhieuKhoResponse.tuCacDong(phieuKhoRepository.saveAll(cacDong));
    }

    @Override
    public PhieuKhoResponse huyPhieu(String maPhieu) {
        List<PhieuKho> cacDong = layCacDong(maPhieu);
        kiemTraDangCho(cacDong, "huy");

        cacDong.forEach(d -> d.setTrangThai(PhieuKho.TrangThaiPhieuKho.DA_HUY));
        return PhieuKhoResponse.tuCacDong(phieuKhoRepository.saveAll(cacDong));
    }

    private List<PhieuKho> layCacDong(String maPhieu) {
        List<PhieuKho> cacDong = phieuKhoRepository.findByMaPhieuOrderByIdAsc(maPhieu);
        if (cacDong.isEmpty() || cacDong.get(0).getLoaiPhieu() != PhieuKho.LoaiPhieu.XUAT) {
            throw new KhongTimThayException("Khong tim thay phieu xuat kho voi ma: " + maPhieu);
        }
        return cacDong;
    }

    private void kiemTraDangCho(List<PhieuKho> cacDong, String hanhDong) {
        if (cacDong.get(0).getTrangThai() != PhieuKho.TrangThaiPhieuKho.CHO_XU_LY) {
            throw new YeuCauKhongHopLeException("Chi co the " + hanhDong + " phieu dang o trang thai CHO_XU_LY");
        }
    }

    private String sinhMaPhieu() {
        String hauTo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String maPhieu = "PXK" + hauTo;
        while (phieuKhoRepository.existsByMaPhieu(maPhieu)) {
            maPhieu = "PXK" + hauTo + (int) (Math.random() * 100);
        }
        return maPhieu;
    }
}
