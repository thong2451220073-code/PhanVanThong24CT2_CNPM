package com.cuahangcongnghe.kho.service;

import com.cuahangcongnghe.kho.entity.TonKho;
import com.cuahangcongnghe.kho.repository.TonKhoRepository;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TonKhoServiceImpl implements TonKhoService {

    private final TonKhoRepository tonKhoRepository;
    private final SanPhamRepository sanPhamRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TonKho> layTatCa(Pageable pageable) {
        return tonKhoRepository.findAll(pageable);
    }

    @Override
    public void xoaTonKho(Long id) {
        TonKho tonKho = tonKhoRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay ton kho voi id: " + id));
        if (tonKho.getSoLuongDatTruoc() != null && tonKho.getSoLuongDatTruoc() > 0) {
            throw new YeuCauKhongHopLeException("Khong the xoa ton kho dang co hang dat truoc");
        }
        tonKhoRepository.delete(tonKho);
    }

    @Override
    @Transactional(readOnly = true)
    public TonKho layTheoId(Long id) {
        return tonKhoRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay ton kho voi id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public TonKho layTheoSanPham(Long sanPhamId) {
        return tonKhoRepository.findBySanPhamId(sanPhamId)
                .orElseThrow(() -> new KhongTimThayException("Chua co ton kho cho san pham nay"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TonKho> timSanPhamSapHetHang(Pageable pageable) {
        return tonKhoRepository.timSanPhamSapHetHang(pageable);
    }

    @Override
    public TonKho khoiTaoTonKho(Long sanPhamId, Integer soLuongBanDau, String viTriKho) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay san pham voi id: " + sanPhamId));

        TonKho tonKho = TonKho.builder()
                .sanPham(sanPham)
                .soLuongTon(soLuongBanDau == null ? 0 : soLuongBanDau)
                .soLuongDatTruoc(0)
                .viTriKho(viTriKho)
                .build();

        return tonKhoRepository.save(tonKho);
    }

    @Override
    public TonKho nhapKho(Long sanPhamId, Integer soLuong) {
        if (soLuong == null || soLuong <= 0) {
            throw new YeuCauKhongHopLeException("So luong nhap kho phai lon hon 0");
        }
        TonKho tonKho = layTheoSanPham(sanPhamId);
        tonKho.setSoLuongTon(tonKho.getSoLuongTon() + soLuong);
        return tonKhoRepository.save(tonKho);
    }

    @Override
    public TonKho xuatKho(Long sanPhamId, Integer soLuong) {
        if (soLuong == null || soLuong <= 0) {
            throw new YeuCauKhongHopLeException("So luong xuat kho phai lon hon 0");
        }
        TonKho tonKho = layTheoSanPham(sanPhamId);
        if (tonKho.getSoLuongTon() < soLuong) {
            throw new YeuCauKhongHopLeException("So luong ton khong du de xuat kho");
        }
        tonKho.setSoLuongTon(tonKho.getSoLuongTon() - soLuong);
        return tonKhoRepository.save(tonKho);
    }

    @Override
    public void datTruocHang(Long sanPhamId, Integer soLuong) {
        TonKho tonKho = layTheoSanPham(sanPhamId);
        if (tonKho.soLuongCoTheBan() < soLuong) {
            throw new YeuCauKhongHopLeException("San pham khong du hang de dat truoc");
        }
        tonKho.setSoLuongDatTruoc(tonKho.getSoLuongDatTruoc() + soLuong);
        tonKhoRepository.save(tonKho);
    }

    @Override
    public void huyDatTruocHang(Long sanPhamId, Integer soLuong) {
        TonKho tonKho = layTheoSanPham(sanPhamId);
        int soLuongMoi = Math.max(0, tonKho.getSoLuongDatTruoc() - soLuong);
        tonKho.setSoLuongDatTruoc(soLuongMoi);
        tonKhoRepository.save(tonKho);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean kiemTraConHang(Long sanPhamId, Integer soLuongCan) {
        try {
            TonKho tonKho = layTheoSanPham(sanPhamId);
            return tonKho.soLuongCoTheBan() >= soLuongCan;
        } catch (KhongTimThayException ex) {
            return false;
        }
    }
}
