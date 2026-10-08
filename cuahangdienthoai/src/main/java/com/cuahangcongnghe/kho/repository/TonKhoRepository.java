package com.cuahangcongnghe.kho.repository;

import com.cuahangcongnghe.kho.entity.TonKho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TonKhoRepository extends JpaRepository<TonKho, Long> {

    Optional<TonKho> findBySanPhamId(Long sanPhamId);

    void deleteBySanPhamId(Long sanPhamId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TonKho t WHERE t.id = :id")
    Optional<TonKho> layDeCapNhat(@Param("id") Long id);

    @Query("SELECT t FROM TonKho t WHERE t.soLuongTon <= t.mucTonToiThieu")
    Page<TonKho> timSanPhamSapHetHang(Pageable pageable);

    @Modifying
    @Query("UPDATE TonKho t SET t.soLuongTon = t.soLuongTon + :soLuong WHERE t.id = :id")
    void congSoLuongTon(@Param("id") Long id, @Param("soLuong") Integer soLuong);

    @Modifying
    @Query("UPDATE TonKho t SET t.soLuongTon = t.soLuongTon - :soLuong WHERE t.id = :id")
    void truSoLuongTon(@Param("id") Long id, @Param("soLuong") Integer soLuong);
}
