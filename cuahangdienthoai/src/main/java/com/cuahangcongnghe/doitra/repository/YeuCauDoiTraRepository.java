package com.cuahangcongnghe.doitra.repository;

import com.cuahangcongnghe.doitra.entity.TrangThaiDoiTra;
import com.cuahangcongnghe.doitra.entity.YeuCauDoiTra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YeuCauDoiTraRepository extends JpaRepository<YeuCauDoiTra, Long> {

    Page<YeuCauDoiTra> findByNguoiDungIdOrderByNgayTaoDesc(Long nguoiDungId, Pageable pageable);

    Page<YeuCauDoiTra> findByTrangThai(TrangThaiDoiTra trangThai, Pageable pageable);

    Optional<YeuCauDoiTra> findByMaYeuCau(String maYeuCau);

    boolean existsByChiTietDonHangId(Long chiTietDonHangId);

    boolean existsByMaYeuCau(String maYeuCau);
}
