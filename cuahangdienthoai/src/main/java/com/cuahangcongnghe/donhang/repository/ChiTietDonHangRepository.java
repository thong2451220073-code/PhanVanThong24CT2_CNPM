package com.cuahangcongnghe.donhang.repository;

import com.cuahangcongnghe.donhang.entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {

    List<ChiTietDonHang> findByDonHangId(Long donHangId);

    void deleteBySanPhamId(Long sanPhamId);

    Optional<ChiTietDonHang> findByIdAndDonHangNguoiDungId(Long id, Long nguoiDungId);

    List<ChiTietDonHang> findBySanPhamIdAndDonHangNguoiDungId(Long sanPhamId, Long nguoiDungId);
}
