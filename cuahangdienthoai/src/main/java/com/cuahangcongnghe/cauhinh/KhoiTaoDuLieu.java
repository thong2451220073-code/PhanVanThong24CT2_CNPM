package com.cuahangcongnghe.cauhinh;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import com.cuahangcongnghe.sanpham.entity.SanPham;
import com.cuahangcongnghe.sanpham.repository.SanPhamRepository;
import com.cuahangcongnghe.nguoidung.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KhoiTaoDuLieu implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final SanPhamRepository sanPhamRepository;

    @Override
    public void run(String... args) {
        khoiTaoDuLieuDienThoai();

        if (nguoiDungRepository.findByEmail("admin@cuahangcongnghe.com").isEmpty()) {
            NguoiDung admin = NguoiDung.builder()
                    .hoTen("Quan Tri Vien")
                    .email("admin@cuahangcongnghe.com")
                    .matKhau(passwordEncoder.encode("Admin@123"))
                    .kichHoat(true)
                    .vaiTro("ROLE_QUAN_TRI")
                    .build();

            nguoiDungRepository.save(admin);
        }

        // Tai khoan nhan vien: dang nhap, quan ly don hang, xu ly doi tra/bao hanh,
        // thong ke doanh thu, quan ly ton kho, nhap kho, xuat kho
        if (nguoiDungRepository.findByEmail("nhanvien@cuahangcongnghe.com").isEmpty()) {
            NguoiDung nhanVien = NguoiDung.builder()
                    .hoTen("Nhan Vien")
                    .email("nhanvien@cuahangcongnghe.com")
                    .matKhau(passwordEncoder.encode("NhanVien@123"))
                    .kichHoat(true)
                    .vaiTro("ROLE_NHAN_VIEN")
                    .build();

            nguoiDungRepository.save(nhanVien);
        }
    }

    private void khoiTaoDuLieuDienThoai() {
        taoDienThoai("iPhone 17 Pro Max", "IP17PM-256", "iPhone 17 Pro Max 256GB chính hãng", "https://picsum.photos/seed/ip17pm/800/800", 34990000);
        taoDienThoai("iPhone 17 Pro", "IP17P-256", "iPhone 17 Pro 256GB chính hãng", "https://picsum.photos/seed/ip17p/800/800", 30990000);
        taoDienThoai("iPhone 17", "IP17-256", "iPhone 17 256GB chính hãng", "https://picsum.photos/seed/ip17/800/800", 24990000);
        taoDienThoai("iPhone 16 Pro Max", "IP16PM-256", "iPhone 16 Pro Max 256GB chính hãng", "https://picsum.photos/seed/ip16pm/800/800", 28990000);
        taoDienThoai("iPhone 16 Pro", "IP16P-128", "iPhone 16 Pro 128GB chính hãng", "https://picsum.photos/seed/ip16p/800/800", 25990000);
        taoDienThoai("iPhone 16", "IP16-128", "iPhone 16 128GB chính hãng", "https://picsum.photos/seed/ip16/800/800", 20990000);
        taoDienThoai("iPhone 15 Pro Max", "IP15PM-256", "iPhone 15 Pro Max 256GB chính hãng", "https://picsum.photos/seed/ip15pm/800/800", 25990000);
        taoDienThoai("iPhone 15", "IP15-128", "iPhone 15 128GB chính hãng", "https://picsum.photos/seed/ip15/800/800", 18990000);
    }

    private void taoDienThoai(String ten, String sku, String moTa, String anh, double gia) {
        if (sanPhamRepository.findByMaSku(sku).isPresent()) return;
        SanPham sp = SanPham.builder()
                .tenSanPham(ten).moTa(moTa).gia(java.math.BigDecimal.valueOf(gia))
                .maSku(sku).trangThai("DANG_BAN")
                .duongDanAnh(anh).build();
        sanPhamRepository.save(sp);
    }

}
