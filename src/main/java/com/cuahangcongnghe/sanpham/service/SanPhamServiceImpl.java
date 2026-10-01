package com.cuahangcongnghe.sanpham.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuahangcongnghe.baohanh.repository.BaoHanhRepository;
import com.cuahangcongnghe.donhang.repository.ChiTietDonHangRepository;
import com.cuahangcongnghe.giohang.repository.ChiTietGioHangRepository;
import com.cuahangcongnghe.kho.repository.PhieuKhoRepository;
import com.cuahangcongnghe.kho.repository.TonKhoRepository;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;

import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    // Repository lien quan den san pham
    private final BaoHanhRepository baoHanhRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final PhieuKhoRepository phieuKhoRepository;
    private final TonKhoRepository tonKhoRepository;


    // =========================================================
    // LAY TAT CA SAN PHAM
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<SanPham> layTatCa(Pageable pageable) {

        return sanPhamRepository.findAll(pageable);
    }


    // =========================================================
    // LAY SAN PHAM THEO ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SanPham layTheoId(Long id) {

        return sanPhamRepository.findById(id)
                .orElseThrow(() ->
                        new KhongTimThayException(
                                "Khong tim thay san pham voi id: " + id
                        )
                );
    }


    // =========================================================
    // TIM KIEM SAN PHAM
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<SanPham> timKiem(
            String tuKhoa,
            Pageable pageable
    ) {

        return sanPhamRepository
                .findByTenSanPhamContainingIgnoreCase(tuKhoa, pageable);
    }


    // =========================================================
    // LOC THEO KHOANG GIA
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<SanPham> locTheoKhoangGia(
            BigDecimal giaTu,
            BigDecimal giaDen,
            Pageable pageable
    ) {

        if (giaTu.compareTo(giaDen) > 0) {
            throw new YeuCauKhongHopLeException(
                    "Gia tu phai nho hon hoac bang gia den"
            );
        }

        return sanPhamRepository.findByGiaBetween(giaTu, giaDen, pageable);
    }


    // =========================================================
    // TAO SAN PHAM
    // =========================================================

    @Override
    public SanPham taoMoi(SanPham sanPham) {

        if (sanPham.getMaSku() != null
                && sanPhamRepository.existsByMaSku(
                        sanPham.getMaSku()
                )) {

            throw new YeuCauKhongHopLeException(
                    "Ma SKU da ton tai: " + sanPham.getMaSku()
            );
        }

        return sanPhamRepository.save(sanPham);
    }


    // =========================================================
    // CAP NHAT SAN PHAM
    // =========================================================

    @Override
    public SanPham capNhat(
            Long id,
            SanPham duLieuMoi
    ) {

        SanPham sanPhamHienTai = layTheoId(id);

        sanPhamHienTai.setTenSanPham(
                duLieuMoi.getTenSanPham()
        );

        sanPhamHienTai.setMoTa(
                duLieuMoi.getMoTa()
        );

        sanPhamHienTai.setGia(
                duLieuMoi.getGia()
        );

        sanPhamHienTai.setGiaKhuyenMai(
                duLieuMoi.getGiaKhuyenMai()
        );


        if (duLieuMoi.getTrangThai() != null) {
            sanPhamHienTai.setTrangThai(
                    duLieuMoi.getTrangThai()
            );
        }

        return sanPhamRepository.save(
                sanPhamHienTai
        );
    }


    // =========================================================
    // XOA SAN PHAM
    // =========================================================

    @Override
    public void xoa(Long id) {

        SanPham sanPham = layTheoId(id);

        // Xoa cac du lieu phu thuoc

        baoHanhRepository.deleteBySanPhamId(id);

        chiTietDonHangRepository.deleteBySanPhamId(id);

        chiTietGioHangRepository.deleteBySanPhamId(id);

        phieuKhoRepository.deleteBySanPhamId(id);

        tonKhoRepository.deleteBySanPhamId(id);

        // Xoa san pham

        sanPhamRepository.delete(sanPham);
    }



}