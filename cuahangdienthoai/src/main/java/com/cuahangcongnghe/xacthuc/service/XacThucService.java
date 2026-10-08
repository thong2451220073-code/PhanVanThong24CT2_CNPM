package com.cuahangcongnghe.xacthuc.service;

import com.cuahangcongnghe.xacthuc.dto.DangKyRequest;
import com.cuahangcongnghe.xacthuc.dto.DangNhapRequest;
import com.cuahangcongnghe.xacthuc.dto.DangNhapResponse;

public interface XacThucService {

    DangNhapResponse dangKy(DangKyRequest request);

    DangNhapResponse dangNhap(DangNhapRequest request);
}
