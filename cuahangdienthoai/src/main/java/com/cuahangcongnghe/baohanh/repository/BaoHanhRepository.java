package com.cuahangcongnghe.baohanh.repository;

import com.cuahangcongnghe.baohanh.entity.BaoHanh;
import com.cuahangcongnghe.baohanh.entity.TrangThaiBaoHanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaoHanhRepository extends JpaRepository<BaoHanh, Long> {

    Page<BaoHanh> findByNguoiDungId(Long nguoiDungId, Pageable pageable);

    List<BaoHanh> findBySanPhamId(Long sanPhamId);

    void deleteBySanPhamId(Long sanPhamId);

    Optional<BaoHanh> findBySoSeri(String soSeri);

    Page<BaoHanh> findByTrangThai(TrangThaiBaoHanh trangThai, Pageable pageable);

    boolean existsBySoSeri(String soSeri);
}
