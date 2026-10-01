package com.cuahangcongnghe.cauhinh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CauHinhCors {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cauHinh = new CorsConfiguration();

        // Thay doi domain nay thanh domain that cua frontend khi trien khai
        cauHinh.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.vercel.app"));
        cauHinh.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cauHinh.setAllowedHeaders(List.of("*"));
        cauHinh.setExposedHeaders(List.of("Authorization"));
        cauHinh.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cauHinh);
        return source;
    }
}
