package com.livingcostcheck.home_repair.service;

import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InspectionDocumentService {

    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern NON_ALNUM_PATTERN = Pattern.compile("[^a-z0-9]+");
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\r?\\n+");
    private static final int MAX_PAGE_COUNT = 40;
    private static final int MAX_SNIPPETS_PER_PAGE = 80;
    private static final int OCR_PAGE_LIMIT = 24;
    private static final int OCR_DPI = 220;
    private static final int MIN_TEXT_LENGTH_FOR_NATIVE_PARSE = 90;
    private static final Set<String> OCR_DATA_FILES = Set.of("eng.traineddata", "osd.traineddata");
    private static final Set<String> STOPWORDS = Set.of(
            "active", "item", "items", "issue", "issues", "repair", "repairs", "replace", "replacement",
            "recommend", "recommended", "inspector", "inspection", "report", "page", "see", "further",
            "review", "licensed", "contractor", "minor", "major", "front", "rear", "left", "right",
            "home", "house", "seller", "buyer", "noted", "observed", "system", "systems");
    private volatile Path tessdataDirectory;
    private volatile boolean ocrUnavailable;

    public DocumentEvidenceResult extractEvidence(MultipartFile file, List<String> findings) {
        if (file == null || file.isEmpty() || findings == null || findings.isEmpty()) {
            return DocumentEvidenceResult.empty();
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename().trim() : "";
        List<PageText> pages;
        try {
            if (looksLikePdf(file, originalFilename)) {
                pages = extractPdfPages(file.getBytes());
            } else if (looksLikePlainText(file, originalFilename)) {
                pages = List.of(new PageText(1, normalizeText(new String(file.getBytes(), StandardCharsets.UTF_8)),
                        false));
            } else if (looksLikeImage(file, originalFilename)) {
                pages = extractImageEvidence(file.getBytes());
            } else {
                throw new IllegalArgumentException("Upload a PDF, TXT, PNG, or JPG inspection file.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("We could not read that inspection file.");
        }

        List<PageSnippet> snippets = buildSnippets(pages);
        List<InspectionEvidenceRef> evidenceRefs = findings.stream()
                .map(finding -> buildEvidenceRef(finding, snippets))
                .filter(ref -> ref != null && !ref.citations().isEmpty())
                .toList();

        if (evidenceRefs.isEmpty()) {
            return new DocumentEvidenceResult(List.of(), sourceLabel(originalFilename));
        }
        return new DocumentEvidenceResult(evidenceRefs, sourceLabel(originalFilename));
    }

    private List<PageText> extractPdfPages(byte[] bytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            PDFRenderer renderer = new PDFRenderer(document);

            List<PageText> pages = new ArrayList<>();
            int totalPages = Math.min(document.getNumberOfPages(), MAX_PAGE_COUNT);
            for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) {
                stripper.setStartPage(pageNumber);
                stripper.setEndPage(pageNumber);
                String text = normalizeText(stripper.getText(document));
                boolean ocrBacked = false;

                if (shouldRunOcr(pageNumber, text)) {
                    String ocrText = extractOcrText(
                            renderer.renderImageWithDPI(pageNumber - 1, OCR_DPI, ImageType.GRAY));
                    if (!ocrText.isBlank()) {
                        text = text.isBlank()
                                ? ocrText
                                : normalizeText(text + "\n" + ocrText);
                        ocrBacked = true;
                    }
                }

                if (!text.isBlank()) {
                    pages.add(new PageText(pageNumber, text, ocrBacked));
                }
            }
            return pages;
        }
    }

    private List<PageText> extractImageEvidence(byte[] bytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IllegalArgumentException("We could not read that inspection image.");
            }
            String ocrText = extractOcrText(image);
            if (ocrText.isBlank()) {
                return List.of();
            }
            return List.of(new PageText(1, ocrText, true));
        }
    }

    private List<PageSnippet> buildSnippets(List<PageText> pages) {
        List<PageSnippet> snippets = new ArrayList<>();
        for (PageText page : pages) {
            List<String> candidates = SPLIT_PATTERN.splitAsStream(page.text())
                    .map(this::normalizeText)
                    .filter(text -> text.length() >= 25)
                    .limit(MAX_SNIPPETS_PER_PAGE)
                    .toList();

            for (String candidate : candidates) {
                snippets.add(new PageSnippet(page.pageNumber(), candidate, page.ocrBacked()));
            }

            if (candidates.isEmpty() && !page.text().isBlank()) {
                snippets.add(new PageSnippet(page.pageNumber(), shorten(page.text(), 220), page.ocrBacked()));
            }
        }
        return snippets;
    }

    private InspectionEvidenceRef buildEvidenceRef(String finding, List<PageSnippet> snippets) {
        Set<String> findingTokens = meaningfulTokens(finding);
        Set<String> findingTags = componentSignals(finding);
        if (findingTokens.isEmpty() && findingTags.isEmpty()) {
            return null;
        }

        List<ScoredSnippet> matches = snippets.stream()
                .map(snippet -> new ScoredSnippet(snippet, scoreSnippet(snippet, findingTokens, findingTags)))
                .filter(match -> match.score() > 0)
                .sorted(Comparator.comparingInt(ScoredSnippet::score).reversed()
                        .thenComparing(match -> match.snippet().pageNumber()))
                .limit(2)
                .toList();

        if (matches.isEmpty()) {
            return null;
        }

        List<String> citations = new ArrayList<>();
        for (ScoredSnippet match : matches) {
            String citationPrefix = "Report p." + match.snippet().pageNumber()
                    + (match.snippet().ocrBacked() ? " (OCR): " : ": ");
            citations.add(citationPrefix + shorten(match.snippet().text(), 180));
        }

        return new InspectionEvidenceRef(finding, citations);
    }

    private int scoreSnippet(PageSnippet snippet, Set<String> findingTokens, Set<String> findingTags) {
        String normalized = snippet.text().toLowerCase(Locale.ENGLISH);
        Set<String> snippetTokens = meaningfulTokens(normalized);
        Set<String> snippetTags = componentSignals(normalized);

        int score = 0;
        for (String tag : findingTags) {
            if (snippetTags.contains(tag)) {
                score += 4;
            }
        }
        for (String token : findingTokens) {
            if (snippetTokens.contains(token)) {
                score += 1;
            }
            if (normalized.contains(token)) {
                score += 1;
            }
        }
        return score;
    }

    private boolean looksLikePdf(MultipartFile file, String originalFilename) {
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ENGLISH) : "";
        return contentType.contains("pdf") || originalFilename.toLowerCase(Locale.ENGLISH).endsWith(".pdf");
    }

    private boolean looksLikePlainText(MultipartFile file, String originalFilename) {
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ENGLISH) : "";
        return contentType.startsWith("text/") || originalFilename.toLowerCase(Locale.ENGLISH).endsWith(".txt");
    }

    private boolean looksLikeImage(MultipartFile file, String originalFilename) {
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ENGLISH) : "";
        String filename = originalFilename.toLowerCase(Locale.ENGLISH);
        return contentType.startsWith("image/")
                || filename.endsWith(".png")
                || filename.endsWith(".jpg")
                || filename.endsWith(".jpeg");
    }

    private String normalizeText(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        return MULTI_SPACE_PATTERN.matcher(rawValue.replace('\u00a0', ' ')).replaceAll(" ").trim();
    }

    private boolean shouldRunOcr(int pageNumber, String text) {
        if (pageNumber > OCR_PAGE_LIMIT || ocrUnavailable) {
            return false;
        }
        if (text == null || text.isBlank()) {
            return true;
        }
        return text.length() < MIN_TEXT_LENGTH_FOR_NATIVE_PARSE || meaningfulTokens(text).size() < 8;
    }

    private String extractOcrText(BufferedImage image) {
        if (image == null || ocrUnavailable) {
            return "";
        }

        try {
            ITesseract tesseract = buildTesseract();
            String extracted = tesseract.doOCR(preprocessForOcr(image));
            return normalizeText(extracted);
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            ocrUnavailable = true;
            return "";
        } catch (TesseractException | IOException e) {
            return "";
        }
    }

    private ITesseract buildTesseract() throws IOException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(resolveTessdataDirectory().toString());
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        tesseract.setVariable("user_defined_dpi", String.valueOf(OCR_DPI));
        tesseract.setVariable("preserve_interword_spaces", "1");
        return tesseract;
    }

    private Path resolveTessdataDirectory() throws IOException {
        Path resolved = tessdataDirectory;
        if (resolved != null) {
            return resolved;
        }

        synchronized (this) {
            if (tessdataDirectory != null) {
                return tessdataDirectory;
            }

            Path directory = Path.of(System.getProperty("java.io.tmpdir"), "lifeverdict-tessdata");
            Files.createDirectories(directory);

            for (String dataFile : OCR_DATA_FILES) {
                Path target = directory.resolve(dataFile);
                if (Files.exists(target) && Files.size(target) > 0L) {
                    continue;
                }
                ClassPathResource resource = new ClassPathResource("tessdata/" + dataFile);
                try (InputStream inputStream = resource.getInputStream()) {
                    Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            tessdataDirectory = directory;
            return directory;
        }
    }

    private BufferedImage preprocessForOcr(BufferedImage source) {
        int scaledWidth = Math.max(1400, source.getWidth() * 2);
        int scaledHeight = Math.max(1800, source.getHeight() * 2);

        BufferedImage grayscale = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = grayscale.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(source, 0, 0, scaledWidth, scaledHeight, null);
        graphics.dispose();

        BufferedImage contrasted = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_BYTE_GRAY);
        new RescaleOp(1.30f, -18f, null).filter(grayscale, contrasted);
        return contrasted;
    }

    private String shorten(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3).trim() + "...";
    }

    private Set<String> componentSignals(String rawValue) {
        String normalized = rawValue == null ? "" : rawValue.toLowerCase(Locale.ENGLISH);
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        if (normalized.contains("roof") || normalized.contains("shingle") || normalized.contains("flashing")) {
            tags.add("roof");
        }
        if (normalized.contains("panel") || normalized.contains("wiring") || normalized.contains("electrical")
                || normalized.contains("fpe") || normalized.contains("zinsco")) {
            tags.add("electrical");
        }
        if (normalized.contains("pipe") || normalized.contains("plumb") || normalized.contains("sewer")
                || normalized.contains("drain") || normalized.contains("polybutylene")) {
            tags.add("plumbing");
        }
        if (normalized.contains("hvac") || normalized.contains("furnace") || normalized.contains("heat pump")
                || normalized.contains("air conditioner")) {
            tags.add("hvac");
        }
        if (normalized.contains("foundation") || normalized.contains("struct") || normalized.contains("settlement")
                || normalized.contains("crack")) {
            tags.add("foundation");
        }
        if (normalized.contains("water intrusion") || normalized.contains("leak") || normalized.contains("mold")
                || normalized.contains("moisture") || normalized.contains("crawlspace")) {
            tags.add("water");
        }
        if (normalized.contains("window") || normalized.contains("door") || normalized.contains("egress")) {
            tags.add("windows");
        }
        return tags;
    }

    private Set<String> meaningfulTokens(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Set.of();
        }
        return NON_ALNUM_PATTERN.splitAsStream(rawValue.toLowerCase(Locale.ENGLISH))
                .filter(token -> !token.isBlank())
                .filter(token -> token.length() > 2 || "fpe".equals(token) || "hvac".equals(token))
                .filter(token -> !STOPWORDS.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String sourceLabel(String originalFilename) {
        return originalFilename == null || originalFilename.isBlank()
                ? "uploaded inspection report"
                : originalFilename;
    }

    public record DocumentEvidenceResult(
            List<InspectionEvidenceRef> evidenceRefs,
            String sourceLabel) {

        public static DocumentEvidenceResult empty() {
            return new DocumentEvidenceResult(List.of(), "");
        }
    }

    private record PageText(int pageNumber, String text, boolean ocrBacked) {
    }

    private record PageSnippet(int pageNumber, String text, boolean ocrBacked) {
    }

    private record ScoredSnippet(PageSnippet snippet, int score) {
    }
}
