package com.cuahangcongnghe.cauhinh;

import com.cuahangcongnghe.sanpham.controller.AnhSanPhamController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Phuc vu anh da tai len tu thu muc "uploads" qua duong dan /uploads/**. */
@Configuration
public class CauHinhTaiNguyen implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(AnhSanPhamController.THU_MUC_ANH.toUri().toString());
    }
}
