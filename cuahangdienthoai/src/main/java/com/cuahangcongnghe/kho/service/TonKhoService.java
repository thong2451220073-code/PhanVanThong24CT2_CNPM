package com.cuahangcongnghe.kho.service;

import com.cuahangcongnghe.kho.entity.TonKho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TonKhoService {

    Page<TonKho> layTatCa(Pageable pageable);

    TonKho layTheoId(Long id);

    void xoaTonKho(Long id);

    TonKho layTheoSanPham(Long sanPhamId);

    Page<TonKho> timSanPhamSapHetHang(Pageable pageable);

    TonKho khoiTaoTonKho(Long sanPhamId, Integer soLuongBanDau, String viTriKho);

    /**
     * Tang so luong ton (goi khi nhap kho).
     */
    TonKho nhapKho(Long sanPhamId, Integer soLuong);

    /**
     * Giam so luong ton (goi khi xuat kho / xac nhan don hang).
     */
    TonKho xuatKho(Long sanPhamId, Integer soLuong);

    /**
     * Giu truoc so luong hang khi khach dat hang, chua tru ton kho thuc te.
     */
    void datTruocHang(Long sanPhamId, Integer soLuong);

    /**
     * Huy giu cho khi don hang bi huy.
     */
    void huyDatTruocHang(Long sanPhamId, Integer soLuong);

    boolean kiemTraConHang(Long sanPhamId, Integer soLuongCan);
}
