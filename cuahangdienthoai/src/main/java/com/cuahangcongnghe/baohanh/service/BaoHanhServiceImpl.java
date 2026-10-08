package com.cuahangcongnghe.baohanh.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuahangcongnghe.baohanh.dto.BaoHanhRequest;
import com.cuahangcongnghe.baohanh.dto.BaoHanhResponse;
import com.cuahangcongnghe.baohanh.entity.BaoHanh;
import com.cuahangcongnghe.baohanh.entity.TrangThaiBaoHanh;
import com.cuahangcongnghe.baohanh.repository.BaoHanhRepository;
import com.cuahangcongnghe.ngoaile.KhongCoQuyenException;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BaoHanhServiceImpl implements BaoHanhService {

    private final BaoHanhRepository baoHanhRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public BaoHanhResponse taoBaoHanh(Long nguoiDungId, BaoHanhRequest request) {
        if (baoHanhRepository.existsBySoSeri(request.getSoSeri())) {
            throw new YeuCauKhongHopLeException("Số seri này đã được đăng ký bảo hành");
        }

        SanPham sanPham = sanPhamRepository.findById(request.getSanPhamId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm"));

        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy người dùng"));

        LocalDate ngayKetThuc = request.getNgayMua().plusMonths(request.getThoiHanThang());

        BaoHanh baoHanh = BaoHanh.builder()
                .soSeri(request.getSoSeri())
                .sanPham(sanPham)
                .nguoiDung(nguoiDung)
                .maDonHang(request.getMaDonHang())
                .ngayMua(request.getNgayMua())
                .ngayBatDau(request.getNgayMua())
                .ngayKetThuc(ngayKetThuc)
                .thoiHanThang(request.getThoiHanThang())
                .trangThai(TrangThaiBaoHanh.CON_HAN)
                .moTa(request.getMoTa())
                .build();

        return chuyenSangResponse(baoHanhRepository.save(baoHanh));
    }

    @Override
    @Transactional(readOnly = true)
    public BaoHanhResponse layTheoId(Long id) {
        return chuyenSangResponse(timBaoHanh(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BaoHanhResponse layTheoSoSeri(String soSeri) {
        BaoHanh baoHanh = baoHanhRepository.findBySoSeri(soSeri)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy phiếu bảo hành với số seri: " + soSeri));
        return chuyenSangResponse(baoHanh);
    }

    @Override
    @Transactional(readOnly = true)
    public BaoHanhResponse layTheoIdChoNguoiDung(Long id, Long nguoiDungId) {
        BaoHanh baoHanh = timBaoHanh(id);
        kiemTraChuSoHuu(baoHanh, nguoiDungId);
        return chuyenSangResponse(baoHanh);
    }

    @Override
    @Transactional(readOnly = true)
    public BaoHanhResponse layTheoSoSeriChoNguoiDung(String soSeri, Long nguoiDungId) {
        BaoHanh baoHanh = baoHanhRepository.findBySoSeri(soSeri)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy phiếu bảo hành với số seri: " + soSeri));
        kiemTraChuSoHuu(baoHanh, nguoiDungId);
        return chuyenSangResponse(baoHanh);
    }

    private void kiemTraChuSoHuu(BaoHanh baoHanh, Long nguoiDungId) {
        if (nguoiDungId != null && !baoHanh.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền xem phiếu bảo hành này");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BaoHanhResponse> layTheoNguoiDung(Long nguoiDungId, Pageable pageable) {
        return baoHanhRepository.findByNguoiDungId(nguoiDungId, pageable)
                .map(this::chuyenSangResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BaoHanhResponse> layTheoTrangThai(TrangThaiBaoHanh trangThai, Pageable pageable) {
        return baoHanhRepository.findByTrangThai(trangThai, pageable)
                .map(this::chuyenSangResponse);
    }

    @Override
    public BaoHanhResponse capNhatTrangThai(Long id, TrangThaiBaoHanh trangThai) {
        BaoHanh baoHanh = timBaoHanh(id);
        baoHanh.setTrangThai(trangThai);
        return chuyenSangResponse(baoHanhRepository.save(baoHanh));
    }

    @Override
    public void xoaBaoHanh(Long id) {
        BaoHanh baoHanh = timBaoHanh(id);
        baoHanhRepository.delete(baoHanh);
    }

    private BaoHanh timBaoHanh(Long id) {
        return baoHanhRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy phiếu bảo hành với id: " + id));
    }

    private BaoHanhResponse chuyenSangResponse(BaoHanh baoHanh) {
        return BaoHanhResponse.builder()
                .id(baoHanh.getId())
                .soSeri(baoHanh.getSoSeri())
                .sanPhamId(baoHanh.getSanPham().getId())
                .tenSanPham(baoHanh.getSanPham().getTenSanPham())
                .nguoiDungId(baoHanh.getNguoiDung().getId())
                .tenNguoiDung(baoHanh.getNguoiDung().getHoTen())
                .maDonHang(baoHanh.getMaDonHang())
                .ngayMua(baoHanh.getNgayMua())
                .ngayBatDau(baoHanh.getNgayBatDau())
                .ngayKetThuc(baoHanh.getNgayKetThuc())
                .thoiHanThang(baoHanh.getThoiHanThang())
                .trangThai(baoHanh.getTrangThai())
                .moTa(baoHanh.getMoTa())
                .conHanBaoHanh(!LocalDate.now().isAfter(baoHanh.getNgayKetThuc()))
                .build();
    }
}
