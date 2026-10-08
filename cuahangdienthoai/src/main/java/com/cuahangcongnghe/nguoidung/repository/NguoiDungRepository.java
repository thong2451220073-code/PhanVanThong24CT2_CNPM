package com.cuahangcongnghe.nguoidung.repository;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    Optional<NguoiDung> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT nd FROM NguoiDung nd WHERE " +
            "LOWER(nd.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
            "OR LOWER(nd.email) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
            "OR nd.soDienThoai LIKE CONCAT('%', :tuKhoa, '%')")
    List<NguoiDung> timKiemKhachHang(@Param("tuKhoa") String tuKhoa);
}
