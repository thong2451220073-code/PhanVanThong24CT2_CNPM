package com.cuahangcongnghe.donhang.repository;

import com.cuahangcongnghe.donhang.entity.DonHang;
import com.cuahangcongnghe.donhang.entity.TrangThaiDonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    Page<DonHang> findByNguoiDungIdOrderByNgayTaoDesc(Long nguoiDungId, Pageable pageable);

    Page<DonHang> findByNguoiDungIdAndTrangThaiOrderByNgayTaoDesc(
            Long nguoiDungId, TrangThaiDonHang trangThai, Pageable pageable);

    Optional<DonHang> findByMaDonHang(String maDonHang);

    Page<DonHang> findByTrangThai(TrangThaiDonHang trangThai, Pageable pageable);

    boolean existsByMaDonHang(String maDonHang);

    long countByNguoiDungIdAndTrangThai(Long nguoiDungId, TrangThaiDonHang trangThai);
}
