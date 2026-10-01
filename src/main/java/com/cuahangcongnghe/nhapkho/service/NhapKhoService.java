package com.cuahangcongnghe.nhapkho.service;

import com.cuahangcongnghe.kho.dto.PhieuKhoResponse;
import com.cuahangcongnghe.nhapkho.dto.TaoPhieuNhapKhoRequest;

import java.util.List;

public interface NhapKhoService {

    List<PhieuKhoResponse> layTatCa();

    PhieuKhoResponse layTheoMa(String maPhieu);

    /**
     * Tao phieu nhap kho o trang thai CHO_XU_LY, chua cong vao ton kho.
     */
    PhieuKhoResponse taoPhieu(TaoPhieuNhapKhoRequest yeuCau, Long nguoiTaoId);

    /**
     * Xac nhan phieu nhap kho: cong so luong vao ton kho cho tung san pham trong phieu.
     */
    PhieuKhoResponse xacNhanNhapKho(String maPhieu);

    /**
     * Huy phieu nhap kho dang cho xu ly.
     */
    PhieuKhoResponse huyPhieu(String maPhieu);
}
