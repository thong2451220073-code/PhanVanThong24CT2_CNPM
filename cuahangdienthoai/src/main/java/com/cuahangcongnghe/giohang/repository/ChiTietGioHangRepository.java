package com.cuahangcongnghe.giohang.repository;

import com.cuahangcongnghe.giohang.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {

    Optional<ChiTietGioHang> findByNguoiDungIdAndSanPhamId(Long nguoiDungId, Long sanPhamId);

    List<ChiTietGioHang> findByNguoiDungId(Long nguoiDungId);

    List<ChiTietGioHang> findByNguoiDungIdAndDaChonTrue(Long nguoiDungId);

    void deleteByNguoiDungIdAndDaChonTrue(Long nguoiDungId);

    void deleteBySanPhamId(Long sanPhamId);

    long countByNguoiDungId(Long nguoiDungId);
}
