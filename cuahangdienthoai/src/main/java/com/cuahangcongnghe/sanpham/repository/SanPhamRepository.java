package com.cuahangcongnghe.sanpham.repository;

import com.cuahangcongnghe.sanpham.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SanPhamRepository extends JpaRepository<SanPham, Long>, JpaSpecificationExecutor<SanPham> {

    Optional<SanPham> findByMaSku(String maSku);

    Page<SanPham> findByTenSanPhamContainingIgnoreCase(String tuKhoa, Pageable pageable);

    Page<SanPham> findByGiaBetween(
            java.math.BigDecimal giaTu, java.math.BigDecimal giaDen, Pageable pageable);

    boolean existsByMaSku(String maSku);
}
