package com.cuahangcongnghe.cauhinh;

import com.cuahangcongnghe.baomat.BoLocJwt;
import com.cuahangcongnghe.baomat.DichVuNguoiDung;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Bat buoc phai co thi @PreAuthorize tren cac controller moi thuc su co hieu luc
@RequiredArgsConstructor
public class CauHinhBaoMat {

    private final DichVuNguoiDung dichVuNguoiDung;
    private final BoLocJwt boLocJwt;
    private final CorsConfigurationSource corsConfigurationSource;

    // Cac duong dan khong yeu cau dang nhap
    private static final String[] DUONG_DAN_CONG_KHAI = {
            "/",
            "/*.html",
            "/css/**",
            "/js/**",
            "/uploads/**",
            "/api/xac-thuc/**",
            "/api/san-pham/**",
            // Trang ket qua VNPAY va API xac nhan tuong ung duoc trinh duyet cua khach
            // goi truc tiep sau khi thanh toan, khong kem JWT cua he thong
            "/vnpay-ket-qua.html",
            "/api/thanh-toan/vnpay/xac-nhan",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain chuoiBoLocBaoMat(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(DUONG_DAN_CONG_KHAI).permitAll()
                        .requestMatchers("/api/quan-tri/**").hasAuthority("ROLE_QUAN_TRI")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(boLocJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(dichVuNguoiDung);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
