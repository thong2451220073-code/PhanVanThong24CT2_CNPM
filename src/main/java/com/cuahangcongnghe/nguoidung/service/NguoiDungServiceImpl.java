package com.cuahangcongnghe.nguoidung.service;

import com.cuahangcongnghe.ngoaile.KhongTimThayException;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.nguoidung.dto.CapNhatNguoiDungRequest;
import com.cuahangcongnghe.nguoidung.dto.NguoiDungResponse;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImpl implements NguoiDungService {

    // Vai tro duoc phep gan qua API quan tri; ROLE_QUAN_TRI CO Y khong dua vao day
    // de tranh mot quan tri vien (hoac tai khoan bi chiem quyen) tu leo thang len sieu quan tri khac.
    private static final Set<String> VAI_TRO_DUOC_PHEP_GAN =
            Set.of("ROLE_KHACH_HANG", "ROLE_NHAN_VIEN");

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public NguoiDungResponse layThongTinTheoEmail(String email) {
        return chuyenSangResponse(timNguoiDungTheoEmail(email));
    }

    @Override
    @Transactional
    public NguoiDungResponse capNhatThongTin(String email, CapNhatNguoiDungRequest request) {
        NguoiDung nguoiDung = timNguoiDungTheoEmail(email);
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setSoDienThoai(request.getSoDienThoai());
        return chuyenSangResponse(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    public List<NguoiDungResponse> layTatCaNguoiDung() {
        return nguoiDungRepository.findAll().stream()
                .map(this::chuyenSangResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void khoaTaiKhoan(Long id) {
        NguoiDung nguoiDung = timNguoiDungTheoId(id);
        nguoiDung.setKichHoat(false);
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public void moKhoaTaiKhoan(Long id) {
        NguoiDung nguoiDung = timNguoiDungTheoId(id);
        nguoiDung.setKichHoat(true);
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public NguoiDungResponse ganVaiTro(Long id, Set<String> tenCacVaiTro) {
        NguoiDung nguoiDung = timNguoiDungTheoId(id);

        Set<String> tenKhongHopLe = tenCacVaiTro.stream()
                .filter(ten -> !VAI_TRO_DUOC_PHEP_GAN.contains(ten))
                .collect(Collectors.toSet());
        if (!tenKhongHopLe.isEmpty()) {
            throw new YeuCauKhongHopLeException(
                    "Vai tro khong hop le hoac khong duoc phep gan qua API nay: " + tenKhongHopLe);
        }

        if (tenCacVaiTro.size() != 1) {
            throw new YeuCauKhongHopLeException("Moi tai khoan chi duoc gan mot vai tro");
        }

        nguoiDung.setVaiTro(tenCacVaiTro.iterator().next());
        return chuyenSangResponse(nguoiDungRepository.save(nguoiDung));
    }

    private NguoiDung timNguoiDungTheoEmail(String email) {
        return nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay nguoi dung: " + email));
    }

    private NguoiDung timNguoiDungTheoId(Long id) {
        return nguoiDungRepository.findById(id)
                .orElseThrow(() -> new KhongTimThayException("Khong tim thay nguoi dung co id: " + id));
    }

    private NguoiDungResponse chuyenSangResponse(NguoiDung nguoiDung) {
        return NguoiDungResponse.builder()
                .id(nguoiDung.getId())
                .hoTen(nguoiDung.getHoTen())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .kichHoat(nguoiDung.isKichHoat())
                .ngayTao(nguoiDung.getNgayTao())
                .danhSachVaiTro(Set.of(nguoiDung.getVaiTro()))
                .build();
    }
}
