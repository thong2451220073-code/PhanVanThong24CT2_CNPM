package com.cuahangcongnghe.kho.repository;

import com.cuahangcongnghe.kho.entity.PhieuKho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhieuKhoRepository extends JpaRepository<PhieuKho, Long> {
    List<PhieuKho> findByMaPhieuOrderByIdAsc(String maPhieu);
    boolean existsByMaPhieu(String maPhieu);
    List<PhieuKho> findByLoaiPhieuOrderByNgayTaoDescIdAsc(PhieuKho.LoaiPhieu loaiPhieu);
    void deleteBySanPhamId(Long sanPhamId);
}
