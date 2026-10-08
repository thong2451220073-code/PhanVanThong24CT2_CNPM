package com.cuahangcongnghe.xacthuc.service;

import com.cuahangcongnghe.baomat.ChiTietNguoiDung;
import com.cuahangcongnghe.baomat.DichVuJwt;
import com.cuahangcongnghe.ngoaile.YeuCauKhongHopLeException;
import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import com.cuahangcongnghe.xacthuc.dto.DangKyRequest;
import com.cuahangcongnghe.xacthuc.dto.DangNhapRequest;
import com.cuahangcongnghe.xacthuc.dto.DangNhapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class XacThucServiceImpl implements XacThucService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final DichVuJwt dichVuJwt;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public DangNhapResponse dangKy(DangKyRequest request) {
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new YeuCauKhongHopLeException("Email nay da duoc dang ky");
        }

        NguoiDung nguoiDungMoi = NguoiDung.builder()
                .hoTen(request.getHoTen())
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .soDienThoai(request.getSoDienThoai())
                .kichHoat(true)
                .vaiTro("ROLE_KHACH_HANG")
                .build();

        NguoiDung daLuu = nguoiDungRepository.save(nguoiDungMoi);
        return taoPhanHoiDangNhap(daLuu);
    }

    @Override
    public DangNhapResponse dangNhap(DangNhapRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMatKhau()));

        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new YeuCauKhongHopLeException("Email hoac mat khau khong dung"));

        return taoPhanHoiDangNhap(nguoiDung);
    }

    private DangNhapResponse taoPhanHoiDangNhap(NguoiDung nguoiDung) {
        String token = dichVuJwt.taoToken(ChiTietNguoiDung.tao(nguoiDung));

        Set<String> tenCacVaiTro = Set.of(nguoiDung.getVaiTro());

        return DangNhapResponse.builder()
                .token(token)
                .loaiToken("Bearer")
                .id(nguoiDung.getId())
                .hoTen(nguoiDung.getHoTen())
                .email(nguoiDung.getEmail())
                .danhSachVaiTro(tenCacVaiTro)
                .build();
    }
}
