package com.cuahangcongnghe.donhang.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuahangcongnghe.diachi.entity.DiaChi;
import com.cuahangcongnghe.diachi.repository.DiaChiRepository;
import com.cuahangcongnghe.donhang.dto.ChiTietDonHangResponse;
import com.cuahangcongnghe.donhang.dto.DonHangResponse;
import com.cuahangcongnghe.donhang.dto.TaoDonHangRequest;
import com.cuahangcongnghe.donhang.entity.ChiTietDonHang;
import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.entity.TrangThaiDonHang;
import com.cuahangcongnghe.donhang.repository.DonHangRepository;
import com.cuahangcongnghe.giohang.entity.ChiTietGioHang;
import com.cuahangcongnghe.giohang.repository.ChiTietGioHangRepository;
import com.cuahangcongnghe.ngoaile.KhongCoQuyenException;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;
import com.cuahangcongnghe.kho.service.TonKhoService;
import com.cuahangcongnghe.donhang.dto.ChiTietThuCongRequest;
import com.cuahangcongnghe.donhang.dto.TaoDonHangThuCongRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DonHangServiceImpl implements DonHangService {

    private static final Set<TrangThaiDonHang> TRANG_THAI_CO_THE_HUY =
            Set.of(TrangThaiDonHang.CHO_XAC_NHAN, TrangThaiDonHang.DA_XAC_NHAN);

    private final DonHangRepository donHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final DiaChiRepository diaChiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SanPhamRepository sanPhamRepository;
    private final TonKhoService tonKhoService;

    @Override
    public DonHangResponse taoDonHangTuGioHang(Long nguoiDungId, TaoDonHangRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy người dùng"));

        if (!"VIETQR".equalsIgnoreCase(request.getPhuongThucThanhToan())) {
            throw new YeuCauKhongHopLeException("Hiện chỉ hỗ trợ thanh toán bằng VietQR");
        }
        request.setPhuongThucThanhToan("VIETQR");

        List<ChiTietGioHang> cacMucDaChon = chiTietGioHangRepository.findByNguoiDungIdAndDaChonTrue(nguoiDungId);
        if (cacMucDaChon.isEmpty()) {
            throw new YeuCauKhongHopLeException("Vui lòng chọn ít nhất một sản phẩm để đặt hàng");
        }

        // Kiem tra ton kho truoc khi tao don, tranh nhan don cho san pham da het hang
        for (ChiTietGioHang muc : cacMucDaChon) {
            if (!tonKhoService.kiemTraConHang(muc.getSanPham().getId(), muc.getSoLuong())) {
                throw new YeuCauKhongHopLeException(
                        "Sản phẩm '" + muc.getSanPham().getTenSanPham() + "' không đủ hàng trong kho");
            }
        }

        DiaChi diaChi = diaChiRepository.findById(request.getDiaChiId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy địa chỉ giao hàng"));
        if (!diaChi.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Địa chỉ giao hàng không thuộc về bạn");
        }

        DonHang donHang = DonHang.builder()
                .maDonHang(taoMaDonHang())
                .nguoiDung(nguoiDung)
                .hoTenNguoiNhan(diaChi.getHoTenNguoiNhan())
                .soDienThoaiNguoiNhan(diaChi.getSoDienThoai())
                .diaChiGiaoHang(gopDiaChi(diaChi))
                .phuongThucThanhToan(request.getPhuongThucThanhToan())
                .ghiChu(request.getGhiChu())
                .trangThai(TrangThaiDonHang.CHO_XAC_NHAN)
                .phiVanChuyen(BigDecimal.ZERO)
                .build();

        BigDecimal tongTienHang = BigDecimal.ZERO;
        for (ChiTietGioHang muc : cacMucDaChon) {
            SanPham sanPham = muc.getSanPham();
            BigDecimal donGia = sanPham.getGia();
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(muc.getSoLuong()));
            tongTienHang = tongTienHang.add(thanhTien);

            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .sanPham(sanPham)
                    .tenSanPham(sanPham.getTenSanPham())
                    .donGia(donGia)
                    .soLuong(muc.getSoLuong())
                    .thanhTien(thanhTien)
                    .build();
            donHang.themChiTiet(chiTiet);
        }

        donHang.setTongTienHang(tongTienHang);

        donHang.setTongThanhToan(tongTienHang.add(donHang.getPhiVanChuyen()));

        DonHang daLuu = donHangRepository.save(donHang);

        // Tu dong tru ton kho ngay khi dat hang thanh cong.
        // Neu tru kho that bai, transaction se rollback ca don hang.
        for (ChiTietGioHang muc : cacMucDaChon) {
            tonKhoService.xuatKho(muc.getSanPham().getId(), muc.getSoLuong());
        }

        chiTietGioHangRepository.deleteAll(cacMucDaChon);

        return chuyenSangResponse(daLuu);
    }

    @Override
    public DonHangResponse taoDonHangThuCong(Long nhanVienTaoId, TaoDonHangThuCongRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy khách hàng"));

        String hoTenNguoiNhan;
        String soDienThoaiNguoiNhan;
        String diaChiGiaoHang;
        if (request.getDiaChiId() != null) {
            DiaChi diaChi = diaChiRepository.findById(request.getDiaChiId())
                    .orElseThrow(() -> new KhongTimThayException("Không tìm thấy địa chỉ giao hàng"));
            if (!diaChi.getNguoiDung().getId().equals(nguoiDung.getId())) {
                throw new YeuCauKhongHopLeException("Địa chỉ giao hàng không thuộc về khách hàng này");
            }
            hoTenNguoiNhan = diaChi.getHoTenNguoiNhan();
            soDienThoaiNguoiNhan = diaChi.getSoDienThoai();
            diaChiGiaoHang = gopDiaChi(diaChi);
        } else {
            if (isBlank(request.getHoTenNguoiNhanThuCong()) || isBlank(request.getSoDienThoaiNguoiNhanThuCong())
                    || isBlank(request.getDiaChiGiaoHangThuCong())) {
                throw new YeuCauKhongHopLeException(
                        "Vui lòng chọn địa chỉ có sẵn hoặc nhập đầy đủ họ tên/số điện thoại/địa chỉ giao hàng");
            }
            hoTenNguoiNhan = request.getHoTenNguoiNhanThuCong();
            soDienThoaiNguoiNhan = request.getSoDienThoaiNguoiNhanThuCong();
            diaChiGiaoHang = request.getDiaChiGiaoHangThuCong();
        }

        if (request.getDanhSachSanPham() == null || request.getDanhSachSanPham().isEmpty()) {
            throw new YeuCauKhongHopLeException("Đơn hàng phải có ít nhất một sản phẩm");
        }

        for (ChiTietThuCongRequest muc : request.getDanhSachSanPham()) {
            if (!tonKhoService.kiemTraConHang(muc.getSanPhamId(), muc.getSoLuong())) {
                throw new YeuCauKhongHopLeException(
                        "Sản phẩm id " + muc.getSanPhamId() + " không đủ hàng trong kho");
            }
        }

        DonHang donHang = DonHang.builder()
                .maDonHang(taoMaDonHang())
                .nguoiDung(nguoiDung)
                .hoTenNguoiNhan(hoTenNguoiNhan)
                .soDienThoaiNguoiNhan(soDienThoaiNguoiNhan)
                .diaChiGiaoHang(diaChiGiaoHang)
                .phuongThucThanhToan(request.getPhuongThucThanhToan())
                .ghiChu(request.getGhiChu())
                .nhanVienTaoId(nhanVienTaoId)
                .trangThai(TrangThaiDonHang.CHO_XAC_NHAN)
                .phiVanChuyen(BigDecimal.ZERO)
                .build();

        BigDecimal tongTienHang = BigDecimal.ZERO;
        for (ChiTietThuCongRequest muc : request.getDanhSachSanPham()) {
            SanPham sanPham = sanPhamRepository.findById(muc.getSanPhamId())
                    .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm id: " + muc.getSanPhamId()));
            BigDecimal donGia = sanPham.getGia();
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(muc.getSoLuong()));
            tongTienHang = tongTienHang.add(thanhTien);

            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .sanPham(sanPham)
                    .tenSanPham(sanPham.getTenSanPham())
                    .donGia(donGia)
                    .soLuong(muc.getSoLuong())
                    .thanhTien(thanhTien)
                    .build();
            donHang.themChiTiet(chiTiet);
        }

        donHang.setTongTienHang(tongTienHang);

        donHang.setTongThanhToan(tongTienHang.add(donHang.getPhiVanChuyen()));

        DonHang daLuu = donHangRepository.save(donHang);

        // Don thu cong cung tu dong tru ton kho.
        for (ChiTietThuCongRequest muc : request.getDanhSachSanPham()) {
            tonKhoService.xuatKho(muc.getSanPhamId(), muc.getSoLuong());
        }

        return chuyenSangResponse(daLuu);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @Override
    @Transactional(readOnly = true)
    public DonHangResponse layTheoId(Long id, Long nguoiDungId) {
        DonHang donHang = timDonHang(id);
        if (nguoiDungId != null && !donHang.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền xem đơn hàng này");
        }
        return chuyenSangResponse(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public DonHangResponse layTheoMaDonHang(String maDonHang, Long nguoiDungId) {
        DonHang donHang = donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng: " + maDonHang));
        if (nguoiDungId != null && !donHang.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền xem đơn hàng này");
        }
        return chuyenSangResponse(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonHangResponse> layTheoNguoiDung(Long nguoiDungId, TrangThaiDonHang trangThai, Pageable pageable) {
        Page<DonHang> trang = (trangThai != null)
                ? donHangRepository.findByNguoiDungIdAndTrangThaiOrderByNgayTaoDesc(nguoiDungId, trangThai, pageable)
                : donHangRepository.findByNguoiDungIdOrderByNgayTaoDesc(nguoiDungId, pageable);
        return trang.map(this::chuyenSangResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonHangResponse> layTatCa(TrangThaiDonHang trangThai, Pageable pageable) {
        Page<DonHang> trang = (trangThai != null)
                ? donHangRepository.findByTrangThai(trangThai, pageable)
                : donHangRepository.findAll(pageable);
        return trang.map(this::chuyenSangResponse);
    }

    @Override
    public DonHangResponse capNhatTrangThai(Long id, TrangThaiDonHang trangThaiMoi, String ghiChu) {
        DonHang donHang = timDonHang(id);
        donHang.setTrangThai(trangThaiMoi);
        if (trangThaiMoi == TrangThaiDonHang.DA_GIAO) {
            donHang.setDaThanhToan(true);
        }

        return chuyenSangResponse(donHangRepository.save(donHang));
    }

    @Override
    public DonHangResponse huyDonHang(Long id, Long nguoiDungId, String lyDo) {
        DonHang donHang = timDonHang(id);
        if (!donHang.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền hủy đơn hàng này");
        }
        if (!TRANG_THAI_CO_THE_HUY.contains(donHang.getTrangThai())) {
            throw new YeuCauKhongHopLeException("Không thể hủy đơn hàng ở trạng thái hiện tại");
        }

        donHang.setTrangThai(TrangThaiDonHang.DA_HUY);
        donHang.setLyDoHuy(lyDo);

        // Don da tru kho khi dat hang, neu huy thi hoan lai so luong vao kho.
        for (ChiTietDonHang chiTiet : donHang.getDanhSachChiTiet()) {
            tonKhoService.nhapKho(chiTiet.getSanPham().getId(), chiTiet.getSoLuong());
        }

        return chuyenSangResponse(donHangRepository.save(donHang));
    }

    private DonHang timDonHang(Long id) {
        return donHangRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng với id: " + id));
    }

    private String taoMaDonHang() {
        String hauTo = DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now());
        String maDonHang = "DH" + hauTo;
        while (donHangRepository.existsByMaDonHang(maDonHang)) {
            maDonHang = "DH" + hauTo + (int) (Math.random() * 100);
        }
        return maDonHang;
    }

    private String gopDiaChi(DiaChi diaChi) {
        return java.util.stream.Stream.of(
                        diaChi.getDiaChiChiTiet(),
                        diaChi.getPhuongXa(),
                        diaChi.getQuanHuyen(),
                        diaChi.getTinhThanh())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
    }

    private DonHangResponse chuyenSangResponse(DonHang donHang) {
        List<ChiTietDonHangResponse> danhSachChiTiet = donHang.getDanhSachChiTiet().stream()
                .map(ct -> ChiTietDonHangResponse.builder()
                        .id(ct.getId())
                        .sanPhamId(ct.getSanPham().getId())
                        .tenSanPham(ct.getTenSanPham())
                        .hinhAnhSanPham(ct.getHinhAnhSanPham())
                        .donGia(ct.getDonGia())
                        .soLuong(ct.getSoLuong())
                        .thanhTien(ct.getThanhTien())
                        .build())
                .collect(Collectors.toList());

        return DonHangResponse.builder()
                .id(donHang.getId())
                .maDonHang(donHang.getMaDonHang())
                .nguoiDungId(donHang.getNguoiDung().getId())
                .hoTenNguoiNhan(donHang.getHoTenNguoiNhan())
                .soDienThoaiNguoiNhan(donHang.getSoDienThoaiNguoiNhan())
                .diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .danhSachChiTiet(danhSachChiTiet)
                .tongTienHang(donHang.getTongTienHang())
                .phiVanChuyen(donHang.getPhiVanChuyen())
                .tongThanhToan(donHang.getTongThanhToan())
                .phuongThucThanhToan(donHang.getPhuongThucThanhToan())
                .daThanhToan(donHang.isDaThanhToan())
                .trangThai(donHang.getTrangThai())
                .ghiChu(donHang.getGhiChu())
                .lyDoHuy(donHang.getLyDoHuy())
                .nhanVienTaoId(donHang.getNhanVienTaoId())
                .ngayTao(donHang.getNgayTao())
                .build();
    }
}
