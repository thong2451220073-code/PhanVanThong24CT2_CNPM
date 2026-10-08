package com.cuahangcongnghe.diachi.repository;

import com.cuahangcongnghe.diachi.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {
    List<DiaChi> findByNguoiDungId(Long nguoiDungId);
}
