package com.cuahangcongnghe.thongke.repository;

import com.cuahangcongnghe.donhang.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository chuyen biet cho cac truy van thong ke, tach rieng khoi DonHangRepository
 * de khong lam phinh to repository CRUD chinh cua don hang.
 *
 * Cac truy van gia dinh DonHang co: trangThai (enum, so sanh voi 'HOAN_THANH'),
 * ngayTao (LocalDateTime), tongThanhToan (BigDecimal). Va ChiTietDonHang co: donHang,
 * sanPham, soLuong, thanhTien. Neu ten field thuc te khac, chi can sua lai JPQL ben duoi.
 */
public interface ThongKeRepository extends JpaRepository<DonHang, Long> {

    @Query("SELECT COALESCE(SUM(dh.tongThanhToan), 0) FROM DonHang dh " +
           "WHERE dh.trangThai = 'HOAN_THANH' AND dh.ngayTao BETWEEN :tuNgay AND :denNgay")
    BigDecimal tinhTongDoanhThu(@Param("tuNgay") LocalDateTime tuNgay,
                                @Param("denNgay") LocalDateTime denNgay);

    @Query("SELECT COUNT(dh) FROM DonHang dh " +
           "WHERE dh.trangThai = 'HOAN_THANH' AND dh.ngayTao BETWEEN :tuNgay AND :denNgay")
    Long demSoDonHang(@Param("tuNgay") LocalDateTime tuNgay,
                       @Param("denNgay") LocalDateTime denNgay);

    @Query("SELECT ct.sanPham.id, ct.sanPham.tenSanPham, ct.sanPham.maSku, " +
           "SUM(ct.soLuong), SUM(ct.thanhTien) " +
           "FROM ChiTietDonHang ct " +
           "WHERE ct.donHang.trangThai = 'HOAN_THANH' " +
           "AND ct.donHang.ngayTao BETWEEN :tuNgay AND :denNgay " +
           "GROUP BY ct.sanPham.id, ct.sanPham.tenSanPham, ct.sanPham.maSku " +
           "ORDER BY SUM(ct.soLuong) DESC")
    List<Object[]> timSanPhamBanChay(@Param("tuNgay") LocalDateTime tuNgay,
                                      @Param("denNgay") LocalDateTime denNgay);
}
