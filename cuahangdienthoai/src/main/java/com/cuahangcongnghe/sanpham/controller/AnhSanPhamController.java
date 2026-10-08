package com.cuahangcongnghe.sanpham.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Tai anh san pham tu may len server. Chi quan tri vien duoc goi
 * (duong dan /api/quan-tri/** da duoc CauHinhBaoMat chan theo ROLE_QUAN_TRI).
 * Anh luu o thu muc "uploads" canh file chay, truy cap qua /uploads/ten-file.
 */
@RestController
@RequestMapping("/api/quan-tri/anh")
public class AnhSanPhamController {

    public static final Path THU_MUC_ANH = Paths.get("uploads").toAbsolutePath().normalize();
    private static final Set<String> DUOI_HOP_LE = Set.of("jpg", "jpeg", "png", "webp", "gif");

    @PostMapping
    public ResponseEntity<?> taiLen(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("thongBao", "Chưa chọn file ảnh"));
        }
        String tenGoc = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int cham = tenGoc.lastIndexOf('.');
        String duoi = cham >= 0 ? tenGoc.substring(cham + 1).toLowerCase(Locale.ROOT) : "";
        String loai = file.getContentType() == null ? "" : file.getContentType();
        if (!DUOI_HOP_LE.contains(duoi) || !loai.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("thongBao", "Chỉ chấp nhận ảnh .jpg, .png, .webp, .gif"));
        }
        Files.createDirectories(THU_MUC_ANH);
        String tenMoi = UUID.randomUUID() + "." + duoi;
        Files.copy(file.getInputStream(), THU_MUC_ANH.resolve(tenMoi), StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.ok(Map.of("url", "/uploads/" + tenMoi));
    }
}
