package com.cuahangcongnghe.sanpham.service;

import com.cuahangcongnghe.sanpham.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface SanPhamService {

    Page<SanPham> layTatCa(Pageable pageable);

    SanPham layTheoId(Long id);

    Page<SanPham> timKiem(String tuKhoa, Pageable pageable);

    Page<SanPham> locTheoKhoangGia(BigDecimal giaTu, BigDecimal giaDen, Pageable pageable);

    SanPham taoMoi(SanPham sanPham);

    SanPham capNhat(Long id, SanPham sanPham);

    void xoa(Long id);

}
