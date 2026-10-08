package com.cuahangcongnghe.baomat;

import com.cuahangcongnghe.nguoidung.entity.NguoiDung;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class ChiTietNguoiDung implements UserDetails {

    private final Long id;
    private final String hoTen;
    private final String email;
    private final String matKhau;
    private final boolean kichHoat;
    private final Collection<? extends GrantedAuthority> quyenHan;

    public ChiTietNguoiDung(NguoiDung nguoiDung) {
        this.id = nguoiDung.getId();
        this.hoTen = nguoiDung.getHoTen();
        this.email = nguoiDung.getEmail();
        this.matKhau = nguoiDung.getMatKhau();
        this.kichHoat = nguoiDung.isKichHoat();
        this.quyenHan = java.util.List.of(
                new SimpleGrantedAuthority(nguoiDung.getVaiTro()));
    }

    public static ChiTietNguoiDung tao(NguoiDung nguoiDung) {
        return new ChiTietNguoiDung(nguoiDung);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return quyenHan;
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return kichHoat;
    }
}
