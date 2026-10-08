package com.cuahangcongnghe.xuatkho.service;

import com.cuahangcongnghe.kho.dto.PhieuKhoResponse;
import com.cuahangcongnghe.xuatkho.dto.TaoPhieuXuatKhoRequest;

import java.util.List;

public interface XuatKhoService {

    List<PhieuKhoResponse> layTatCa();

    PhieuKhoResponse layTheoMa(String maPhieu);

    /**
     * Tao phieu xuat kho o trang thai CHO_XU_LY, chua tru ton kho.
     */
    PhieuKhoResponse taoPhieu(TaoPhieuXuatKhoRequest yeuCau, Long nguoiTaoId);

    /**
     * Xac nhan phieu xuat kho: tru so luong khoi ton kho cho tung san pham.
     * Se bao loi neu bat ky san pham nao khong du hang.
     */
    PhieuKhoResponse xacNhanXuatKho(String maPhieu);

    /**
     * Huy phieu xuat kho dang cho xu ly.
     */
    PhieuKhoResponse huyPhieu(String maPhieu);
}
