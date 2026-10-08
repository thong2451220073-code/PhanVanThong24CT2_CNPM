package com.cuahangcongnghe.doitra.service;

import com.cuahangcongnghe.doitra.dto.XuLyDoiTraRequest;
import com.cuahangcongnghe.doitra.dto.YeuCauDoiTraRequest;
import com.cuahangcongnghe.doitra.dto.YeuCauDoiTraResponse;
import com.cuahangcongnghe.doitra.entity.TrangThaiDoiTra;
import com.cuahangcongnghe.doitra.entity.YeuCauDoiTra;
import com.cuahangcongnghe.doitra.repository.YeuCauDoiTraRepository;
import com.cuahangcongnghe.donhang.entity.ChiTietDonHang;
import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.entity.TrangThaiDonHang;
import com.cuahangcongnghe.donhang.repository.ChiTietDonHangRepository;
import com.cuahangcongnghe.donhang.repository.DonHangRepository;
import com.cuahangcongnghe.ngoaile.KhongCoQuyenException;
import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DoiTraServiceImpl implements DoiTraService {

    private static final Set<TrangThaiDonHang> TRANG_THAI_DON_HANG_DUOC_DOI_TRA =
            Set.of(TrangThaiDonHang.DA_GIAO);

    private final YeuCauDoiTraRepository yeuCauDoiTraRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public YeuCauDoiTraResponse taoYeuCau(Long nguoiDungId, YeuCauDoiTraRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy người dùng"));

        DonHang donHang = donHangRepository.findById(request.getDonHangId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng"));

        if (!donHang.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Đơn hàng này không thuộc về bạn");
        }

        if (!TRANG_THAI_DON_HANG_DUOC_DOI_TRA.contains(donHang.getTrangThai())) {
            throw new YeuCauKhongHopLeException("Chỉ có thể đổi trả đơn hàng đã giao thành công");
        }

        ChiTietDonHang chiTietDonHang = chiTietDonHangRepository.findById(request.getChiTietDonHangId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm trong đơn hàng"));

        if (!chiTietDonHang.getDonHang().getId().equals(donHang.getId())) {
            throw new YeuCauKhongHopLeException("Sản phẩm này không thuộc đơn hàng đã chọn");
        }

        if (request.getSoLuong() > chiTietDonHang.getSoLuong()) {
            throw new YeuCauKhongHopLeException("Số lượng đổi trả vượt quá số lượng đã mua");
        }

        if (yeuCauDoiTraRepository.existsByChiTietDonHangId(chiTietDonHang.getId())) {
            throw new YeuCauKhongHopLeException("Sản phẩm này đã có yêu cầu đổi trả trước đó");
        }

        YeuCauDoiTra yeuCau = YeuCauDoiTra.builder()
                .maYeuCau(taoMaYeuCau())
                .donHang(donHang)
                .chiTietDonHang(chiTietDonHang)
                .nguoiDung(nguoiDung)
                .loaiYeuCau(request.getLoaiYeuCau())
                .soLuong(request.getSoLuong())
                .lyDo(request.getLyDo())
                .trangThai(TrangThaiDoiTra.CHO_DUYET)
                .build();

        return chuyenSangResponse(yeuCauDoiTraRepository.save(yeuCau));
    }

    @Override
    @Transactional(readOnly = true)
    public YeuCauDoiTraResponse layTheoId(Long id) {
        return chuyenSangResponse(timYeuCau(id));
    }

    @Override
    @Transactional(readOnly = true)
    public YeuCauDoiTraResponse layTheoMaYeuCau(String maYeuCau) {
        YeuCauDoiTra yeuCau = yeuCauDoiTraRepository.findByMaYeuCau(maYeuCau)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy yêu cầu đổi trả: " + maYeuCau));
        return chuyenSangResponse(yeuCau);
    }

    @Override
    @Transactional(readOnly = true)
    public YeuCauDoiTraResponse layTheoIdChoNguoiDung(Long id, Long nguoiDungId) {
        YeuCauDoiTra yeuCau = timYeuCau(id);
        kiemTraChuSoHuu(yeuCau, nguoiDungId);
        return chuyenSangResponse(yeuCau);
    }

    @Override
    @Transactional(readOnly = true)
    public YeuCauDoiTraResponse layTheoMaYeuCauChoNguoiDung(String maYeuCau, Long nguoiDungId) {
        YeuCauDoiTra yeuCau = yeuCauDoiTraRepository.findByMaYeuCau(maYeuCau)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy yêu cầu đổi trả: " + maYeuCau));
        kiemTraChuSoHuu(yeuCau, nguoiDungId);
        return chuyenSangResponse(yeuCau);
    }

    private void kiemTraChuSoHuu(YeuCauDoiTra yeuCau, Long nguoiDungId) {
        if (nguoiDungId != null && !yeuCau.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền xem yêu cầu đổi trả này");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<YeuCauDoiTraResponse> layTheoNguoiDung(Long nguoiDungId, Pageable pageable) {
        return yeuCauDoiTraRepository.findByNguoiDungIdOrderByNgayTaoDesc(nguoiDungId, pageable)
                .map(this::chuyenSangResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<YeuCauDoiTraResponse> layTheoTrangThai(TrangThaiDoiTra trangThai, Pageable pageable) {
        Page<YeuCauDoiTra> trang = (trangThai != null)
                ? yeuCauDoiTraRepository.findByTrangThai(trangThai, pageable)
                : yeuCauDoiTraRepository.findAll(pageable);
        return trang.map(this::chuyenSangResponse);
    }

    @Override
    public YeuCauDoiTraResponse xuLyYeuCau(Long id, XuLyDoiTraRequest request) {
        YeuCauDoiTra yeuCau = timYeuCau(id);
        yeuCau.setTrangThai(request.getTrangThai());
        if (request.getSoTienHoanTra() != null) {
            yeuCau.setSoTienHoanTra(request.getSoTienHoanTra());
        }
        yeuCau.setGhiChuXuLy(request.getGhiChuXuLy());
        return chuyenSangResponse(yeuCauDoiTraRepository.save(yeuCau));
    }

    @Override
    public void huyYeuCau(Long id, Long nguoiDungId) {
        YeuCauDoiTra yeuCau = timYeuCau(id);
        if (!yeuCau.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new KhongCoQuyenException("Bạn không có quyền hủy yêu cầu này");
        }
        if (yeuCau.getTrangThai() != TrangThaiDoiTra.CHO_DUYET) {
            throw new YeuCauKhongHopLeException("Chỉ có thể hủy yêu cầu đang chờ duyệt");
        }
        yeuCau.setTrangThai(TrangThaiDoiTra.DA_HUY);
        yeuCauDoiTraRepository.save(yeuCau);
    }

    private YeuCauDoiTra timYeuCau(Long id) {
        return yeuCauDoiTraRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy yêu cầu đổi trả với id: " + id));
    }

    private String taoMaYeuCau() {
        String hauTo = DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now());
        String maYeuCau = "DT" + hauTo;
        while (yeuCauDoiTraRepository.existsByMaYeuCau(maYeuCau)) {
            maYeuCau = "DT" + hauTo + (int) (Math.random() * 100);
        }
        return maYeuCau;
    }

    private YeuCauDoiTraResponse chuyenSangResponse(YeuCauDoiTra yeuCau) {
        return YeuCauDoiTraResponse.builder()
                .id(yeuCau.getId())
                .maYeuCau(yeuCau.getMaYeuCau())
                .donHangId(yeuCau.getDonHang().getId())
                .maDonHang(yeuCau.getDonHang().getMaDonHang())
                .chiTietDonHangId(yeuCau.getChiTietDonHang().getId())
                .tenSanPham(yeuCau.getChiTietDonHang().getTenSanPham())
                .nguoiDungId(yeuCau.getNguoiDung().getId())
                .loaiYeuCau(yeuCau.getLoaiYeuCau())
                .soLuong(yeuCau.getSoLuong())
                .lyDo(yeuCau.getLyDo())
                .soTienHoanTra(yeuCau.getSoTienHoanTra())
                .trangThai(yeuCau.getTrangThai())
                .ghiChuXuLy(yeuCau.getGhiChuXuLy())
                .ngayTao(yeuCau.getNgayTao())
                .build();
    }
}
