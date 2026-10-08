package com.cuahangcongnghe.hoadon;

import com.cuahangcongnghe.donhang.entity.ChiTietDonHang;
import com.cuahangcongnghe.donhang.entity.DonHang;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Xuat hoa don PDF cho mot don hang (FR-06: "tu dong xuat hoa don dien tu dang PDF").
 *
 * Dung thu vien OpenPDF (fork mien phi, khong ban quyen cua iText 4) va nhung sẵn font
 * DejaVuSans (src/main/resources/fonts/DejaVuSans.ttf) de hien thi dung tieng Viet co dau
 * tren moi he dieu hanh, khong phu thuoc font co san cua may chu.
 */
@Service
public class HoaDonPdfService {

    private static final String DUONG_DAN_FONT = "fonts/DejaVuSans.ttf";
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat DINH_DANG_TIEN = new DecimalFormat("#,###");

    public byte[] xuatHoaDonPdf(DonHang donHang) {
        try {
            BaseFont baseFont = taoBaseFont();
            Font fontTieuDe = new Font(baseFont, 18, Font.BOLD);
            Font fontDeMuc = new Font(baseFont, 12, Font.BOLD);
            Font fontThuong = new Font(baseFont, 10, Font.NORMAL);
            Font fontIn_dam = new Font(baseFont, 10, Font.BOLD);

            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            // ----- Tieu de -----
            Paragraph tieuDe = new Paragraph("HÓA ĐƠN BÁN HÀNG", fontTieuDe);
            tieuDe.setAlignment(Element.ALIGN_CENTER);
            document.add(tieuDe);

            Paragraph tenCuaHang = new Paragraph("Cửa Hàng Công Nghệ - https://tao247.vn", fontThuong);
            tenCuaHang.setAlignment(Element.ALIGN_CENTER);
            tenCuaHang.setSpacingAfter(16);
            document.add(tenCuaHang);

            // ----- Thong tin don hang -----
            PdfPTable bangThongTin = new PdfPTable(2);
            bangThongTin.setWidthPercentage(100);
            bangThongTin.setWidths(new float[]{1f, 1f});

            themDongThongTin(bangThongTin, "Mã đơn hàng:", donHang.getMaDonHang(), fontIn_dam, fontThuong);
            themDongThongTin(bangThongTin, "Ngày tạo:", donHang.getNgayTao() != null ? donHang.getNgayTao().format(DINH_DANG_NGAY) : "-", fontIn_dam, fontThuong);
            themDongThongTin(bangThongTin, "Khách hàng:", donHang.getHoTenNguoiNhan(), fontIn_dam, fontThuong);
            themDongThongTin(bangThongTin, "Số điện thoại:", donHang.getSoDienThoaiNguoiNhan(), fontIn_dam, fontThuong);
            themDongThongTin(bangThongTin, "Địa chỉ giao hàng:", donHang.getDiaChiGiaoHang(), fontIn_dam, fontThuong);
            themDongThongTin(bangThongTin, "Phương thức thanh toán:", donHang.getPhuongThucThanhToan(), fontIn_dam, fontThuong);
            themDongThongTin(bangThongTin, "Trạng thái:", String.valueOf(donHang.getTrangThai()), fontIn_dam, fontThuong);

            bangThongTin.setSpacingAfter(16);
            document.add(bangThongTin);

            // ----- Bang chi tiet san pham -----
            PdfPTable bangChiTiet = new PdfPTable(4);
            bangChiTiet.setWidthPercentage(100);
            bangChiTiet.setWidths(new float[]{4f, 1.2f, 1.5f, 1.8f});

            themOTieuDeBang(bangChiTiet, "Sản phẩm", fontDeMuc);
            themOTieuDeBang(bangChiTiet, "SL", fontDeMuc);
            themOTieuDeBang(bangChiTiet, "Đơn giá", fontDeMuc);
            themOTieuDeBang(bangChiTiet, "Thành tiền", fontDeMuc);

            for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
                bangChiTiet.addCell(taoOThuong(ct.getTenSanPham(), fontThuong, Element.ALIGN_LEFT));
                bangChiTiet.addCell(taoOThuong(String.valueOf(ct.getSoLuong()), fontThuong, Element.ALIGN_CENTER));
                bangChiTiet.addCell(taoOThuong(dinhDangTien(ct.getDonGia()), fontThuong, Element.ALIGN_RIGHT));
                bangChiTiet.addCell(taoOThuong(dinhDangTien(ct.getThanhTien()), fontThuong, Element.ALIGN_RIGHT));
            }

            bangChiTiet.setSpacingAfter(16);
            document.add(bangChiTiet);

            // ----- Tong ket -----
            PdfPTable bangTongKet = new PdfPTable(2);
            bangTongKet.setWidthPercentage(50);
            bangTongKet.setHorizontalAlignment(Element.ALIGN_RIGHT);
            bangTongKet.setWidths(new float[]{1.2f, 1f});

            themDongTongKet(bangTongKet, "Tổng tiền hàng:", dinhDangTien(donHang.getTongTienHang()), fontThuong);
            themDongTongKet(bangTongKet, "Phí vận chuyển:", dinhDangTien(donHang.getPhiVanChuyen()), fontThuong);
            themDongTongKet(bangTongKet, "TỔNG THANH TOÁN:", dinhDangTien(donHang.getTongThanhToan()), fontIn_dam);

            document.add(bangTongKet);

            Paragraph camOn = new Paragraph("\nCảm ơn quý khách đã mua hàng tại Cửa Hàng Công Nghệ!", fontThuong);
            camOn.setAlignment(Element.ALIGN_CENTER);
            camOn.setSpacingBefore(24);
            document.add(camOn);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Không thể tạo file PDF hóa đơn", e);
        }
    }

    private BaseFont taoBaseFont() throws IOException, DocumentException {
        // Doc font tu classpath (da dong goi san trong resources) de dam bao chay duoc
        // tren moi moi truong trien khai, khong phu thuoc font he thong.
        byte[] duLieuFont = new ClassPathResource(DUONG_DAN_FONT).getInputStream().readAllBytes();
        return BaseFont.createFont(DUONG_DAN_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED,
                BaseFont.NOT_CACHED, duLieuFont, null);
    }

    private void themDongThongTin(PdfPTable bang, String nhan, String giaTri, Font fontNhan, Font fontGiaTri) {
        PdfPCell oNhan = new PdfPCell(new Phrase(nhan, fontNhan));
        oNhan.setBorder(Rectangle.NO_BORDER);
        oNhan.setPaddingBottom(4);
        bang.addCell(oNhan);

        PdfPCell oGiaTri = new PdfPCell(new Phrase(giaTri == null ? "-" : giaTri, fontGiaTri));
        oGiaTri.setBorder(Rectangle.NO_BORDER);
        oGiaTri.setPaddingBottom(4);
        bang.addCell(oGiaTri);
    }

    private void themOTieuDeBang(PdfPTable bang, String noiDung, Font font) {
        PdfPCell o = new PdfPCell(new Phrase(noiDung, font));
        o.setBackgroundColor(new Color(230, 230, 230));
        o.setPadding(6);
        bang.addCell(o);
    }

    private PdfPCell taoOThuong(String noiDung, Font font, int canLe) {
        PdfPCell o = new PdfPCell(new Phrase(noiDung == null ? "-" : noiDung, font));
        o.setPadding(6);
        o.setHorizontalAlignment(canLe);
        return o;
    }

    private void themDongTongKet(PdfPTable bang, String nhan, String giaTri, Font font) {
        PdfPCell oNhan = new PdfPCell(new Phrase(nhan, font));
        oNhan.setBorder(Rectangle.NO_BORDER);
        oNhan.setHorizontalAlignment(Element.ALIGN_RIGHT);
        oNhan.setPaddingBottom(3);
        bang.addCell(oNhan);

        PdfPCell oGiaTri = new PdfPCell(new Phrase(giaTri, font));
        oGiaTri.setBorder(Rectangle.NO_BORDER);
        oGiaTri.setHorizontalAlignment(Element.ALIGN_RIGHT);
        oGiaTri.setPaddingBottom(3);
        bang.addCell(oGiaTri);
    }

    private String dinhDangTien(BigDecimal soTien) {
        if (soTien == null) {
            return "0 đ";
        }
        return DINH_DANG_TIEN.format(soTien) + " đ";
    }
}
