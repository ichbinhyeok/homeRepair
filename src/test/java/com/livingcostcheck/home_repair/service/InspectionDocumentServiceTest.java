package com.livingcostcheck.home_repair.service;

import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InspectionDocumentServiceTest {

    private final InspectionDocumentService service = new InspectionDocumentService();

    @Test
    void extractEvidenceLinksFindingsToPdfPages() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "inspectionReportFile",
                "inspection.pdf",
                "application/pdf",
                samplePdf());

        InspectionDocumentService.DocumentEvidenceResult result = service.extractEvidence(file, List.of(
                "Active roof leak above rear bedroom",
                "Federal Pacific panel flagged by inspector"));

        assertEquals("inspection.pdf", result.sourceLabel());
        assertEquals(2, result.evidenceRefs().size());
        assertTrue(result.evidenceRefs().stream()
                .filter(ref -> ref.findingLabel().contains("roof leak"))
                .flatMap(ref -> ref.citations().stream())
                .anyMatch(citation -> citation.contains("p.1")));
        assertTrue(result.evidenceRefs().stream()
                .filter(ref -> ref.findingLabel().contains("Federal Pacific"))
                .flatMap(ref -> ref.citations().stream())
                .anyMatch(citation -> citation.contains("p.2")));
    }

    @Test
    void extractEvidenceAcceptsPlainTextFiles() {
        MockMultipartFile file = new MockMultipartFile(
                "inspectionReportFile",
                "inspection.txt",
                "text/plain",
                "Active roof leak above rear bedroom".getBytes());

        InspectionDocumentService.DocumentEvidenceResult result = service.extractEvidence(file,
                List.of("Active roof leak above rear bedroom"));

        InspectionEvidenceRef ref = result.evidenceRefs().getFirst();
        assertTrue(ref.citations().getFirst().contains("p.1"));
    }

    @Test
    void extractEvidenceLinksFindingsToImageUploadsViaOcr() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "inspectionReportFile",
                "inspection-scan.png",
                "image/png",
                sampleInspectionImage());

        InspectionDocumentService.DocumentEvidenceResult result = service.extractEvidence(file,
                List.of("Active roof leak above rear bedroom"));

        assertEquals("inspection-scan.png", result.sourceLabel());
        assertTrue(result.evidenceRefs().stream()
                .flatMap(ref -> ref.citations().stream())
                .anyMatch(citation -> citation.contains("(OCR)") && citation.contains("p.1")));
    }

    @Test
    void extractEvidenceLinksFindingsToScannedPdfViaOcr() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "inspectionReportFile",
                "inspection-scan.pdf",
                "application/pdf",
                sampleScannedPdf());

        InspectionDocumentService.DocumentEvidenceResult result = service.extractEvidence(file,
                List.of("Active roof leak above rear bedroom"));

        assertTrue(result.evidenceRefs().stream()
                .flatMap(ref -> ref.citations().stream())
                .anyMatch(citation -> citation.contains("(OCR)") && citation.contains("p.1")));
    }

    private byte[] samplePdf() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            writePage(document, "Page 1\nActive roof leak above rear bedroom. Recommend repair by licensed roofer.");
            writePage(document,
                    "Page 2\nFederal Pacific panel flagged by inspector. Panel replacement is recommended.");
            document.save(output);
            return output.toByteArray();
        }
    }

    private void writePage(PDDocument document, String text) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(50, 700);
            for (String line : text.split("\\n")) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -18);
            }
            contentStream.endText();
        }
    }

    private byte[] sampleInspectionImage() throws IOException {
        BufferedImage image = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 42));
        graphics.drawString("Inspection Summary", 80, 120);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 34));
        graphics.drawString("1. Active roof leak above rear bedroom.", 80, 220);
        graphics.drawString("2. Water staining noted near the ceiling line.", 80, 300);
        graphics.dispose();

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        }
    }

    private byte[] sampleScannedPdf() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(sampleInspectionImage()));
            PDImageXObject pageImage = LosslessFactory.createFromImage(document, image);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pageImage, 36, 140, 540, 405);
            }

            document.save(output);
            return output.toByteArray();
        }
    }
}
