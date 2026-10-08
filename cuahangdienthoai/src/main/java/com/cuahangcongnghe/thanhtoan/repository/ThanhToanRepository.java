package com.cuahangcongnghe.thanhtoan.repository;

import com.cuahangcongnghe.thanhtoan.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {

    Optional<ThanhToan> findByDonHangId(Long donHangId);

    List<ThanhToan> findByDonHangIdIn(Collection<Long> donHangIds);

    Optional<ThanhToan> findByMaGiaoDich(String maGiaoDich);
}
