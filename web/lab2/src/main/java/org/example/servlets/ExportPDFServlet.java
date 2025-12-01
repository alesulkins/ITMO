package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.example.model.Result;
import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/export-pdf")
public class ExportPDFServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ExportPDFServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        List<Result> results = session != null ? (List<Result>) session.getAttribute("results") : null;
        if (results == null) {
            results = Collections.emptyList();
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");

        try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        writeContent(document, page, results);
        document.save(resp.getOutputStream());
    }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void writeContent(PDDocument document, PDPage page, List<Result> results) throws IOException {
        float margin = 50f;
        float yPosition = page.getMediaBox().getHeight() - margin;

        PDFont headerFont = resolveFont(document, true);
        PDFont bodyFont = resolveFont(document, false);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        try {
            contentStream.setLeading(16f);
            contentStream.beginText();
            contentStream.setFont(headerFont, 16);
            contentStream.newLineAtOffset(margin, yPosition);
            showTextSafe(contentStream, headerFont, "Results Report");
            contentStream.newLine();
            contentStream.setFont(bodyFont, 12);
            showTextSafe(contentStream, bodyFont, "Student: Yarulina Alesiya Ilgamovna");
            contentStream.newLine();
            showTextSafe(contentStream, bodyFont, "Group: P3220, variant 45674");
            contentStream.endText();
            yPosition -= 70;

            if (results.isEmpty()) {
                contentStream.beginText();
                contentStream.setFont(bodyFont, 12);
                contentStream.newLineAtOffset(margin, yPosition);
                showTextSafe(contentStream, bodyFont, "No data to export.");
                contentStream.endText();
                return;
            }

            float[] colWidths = new float[]{50f, 60f, 50f, 180f, 90f};
            float rowHeight = 22f;

            PDPage currentPage = page;
            yPosition = drawTableHeader(contentStream, margin, yPosition, colWidths, rowHeight, headerFont);

            for (Result result : results) {
                if (yPosition - rowHeight < margin) {
                    contentStream.close();
                    currentPage = new PDPage(PDRectangle.A4);
                    document.addPage(currentPage);
                    contentStream = new PDPageContentStream(document, currentPage);
                    contentStream.setLeading(16f);
                    yPosition = currentPage.getMediaBox().getHeight() - margin;
                    yPosition = drawTableHeader(contentStream, margin, yPosition, colWidths, rowHeight, headerFont);
                }
                String[] cells = new String[]{
                        formatNumber(result.getX()),
                        formatNumber(result.getY()),
                        formatNumber(result.getR()),
                        result.getFormattedTime(),
                        result.isHit() ? "Hit" : "Miss"
                };
                yPosition = drawTableRow(contentStream, margin, yPosition, colWidths, rowHeight, bodyFont, 11, cells);
            }
        } finally {
            contentStream.close();
        }
    }

    private float drawTableHeader(PDPageContentStream contentStream, float startX, float startY, float[] colWidths, float rowHeight, PDFont font) throws IOException {
        return drawTableRow(contentStream, startX, startY, colWidths, rowHeight, font, 12, new String[]{"X", "Y", "R", "Time", "Result"});
    }

    private float drawTableRow(PDPageContentStream contentStream,
                              float startX,
                              float startY,
                              float[] colWidths,
                              float rowHeight,
                              PDFont font,
                              float fontSize,
                              String[] values) throws IOException {
        float yTop = startY;
        float yBottom = yTop - rowHeight;
        contentStream.setLineWidth(0.5f);

        float x = startX;
        for (int i = 0; i < colWidths.length; i++) {
            float cellWidth = colWidths[i];
            contentStream.addRect(x, yBottom, cellWidth, rowHeight);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            float ascent = font.getFontDescriptor().getAscent() / 1000 * fontSize;
            float descent = font.getFontDescriptor().getDescent() / 1000 * fontSize;
            float textHeight = ascent - descent;
            float textY = yBottom + (rowHeight - textHeight) / 2 - descent;
            contentStream.newLineAtOffset(x + 6, textY);
            String value = values[i];
            showTextSafe(contentStream, font, value);
            contentStream.endText();

            x += cellWidth;
        }
        contentStream.moveTo(startX, yBottom);
        contentStream.lineTo(startX + sum(colWidths), yBottom);
        contentStream.stroke();

        return yBottom;
    }

    private float sum(float[] arr) {
        float s = 0f;
        for (float v : arr) s += v;
        return s;
    }

    private PDFont resolveFont(PDDocument document, boolean bold) throws IOException {
        String[] candidates = new String[] {
                "/System/Library/Fonts/Supplemental/Times New Roman.ttf",
                "/Library/Fonts/Times New Roman.ttf",

                "/System/Library/Fonts/Supplemental/Times New Roman Bold.ttf",
                "/Library/Fonts/Times New Roman Bold.ttf",
        };

        for (String path : candidates) {
            File f = new File(path);
            if (f.exists() && f.isFile() && f.canRead()) {
                try {
                    return PDType0Font.load(document, f);
                } catch (IOException | IllegalArgumentException ignored) {
                    LOG.log(Level.FINE, "Cannot load font", new Object[]{path, ignored.getMessage()});
                }
            }
        }
        return bold ? PDType1Font.TIMES_BOLD : PDType1Font.TIMES_ROMAN;
    }

    private void showTextSafe(PDPageContentStream stream, PDFont font, String text) throws IOException {
        try {
            stream.showText(text);
        } catch (IllegalArgumentException e) {
            String sanitized = text.replaceAll("[^\\p{ASCII}]", "?");
            stream.showText(sanitized);
        }
    }

    private String formatNumber(BigDecimal value) {
        if (value == null) return "";
        String s = value.stripTrailingZeros().toPlainString();
        int dot = s.indexOf('.');
        if (dot < 0) return s;
        String intPart = s.substring(0, dot);
        String frac = s.substring(dot + 1);
        if (frac.length() <= 8) {
            return s;
        }
        char first = frac.charAt(0);
        char last = frac.charAt(frac.length() - 1);
        return intPart + "." + first + "..." + last;
    }
}
