package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SeoVerificationTest {

    private StaticPageGeneratorService pageGenerator;
    private VerdictEngineService verdictService;

    @BeforeEach
    void setUp() throws IOException {
        // Mock VerdictEngineService
        verdictService = Mockito.mock(VerdictEngineService.class);

        // JTE Template Engine (Real)
        Path templateDir = Paths.get("src/main/jte");
        gg.jte.CodeResolver codeResolver = new DirectoryCodeResolver(templateDir);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

        // InternalLinkBuilder (Mock)
        InternalLinkBuilder linkBuilder = Mockito.mock(InternalLinkBuilder.class);
        when(linkBuilder.getOtherErasInCity(any(), any())).thenReturn(Collections.emptyList());
        when(linkBuilder.getNearbyMetrosInEra(any(), any())).thenReturn(Collections.emptyList());
        when(linkBuilder.getRelatedCitiesInState(any(), any())).thenReturn(Collections.emptyList());

        // Create Service
        com.livingcostcheck.home_repair.seo.VerdictSeoService verdictSeoService = new com.livingcostcheck.home_repair.seo.VerdictSeoService();
        SitemapGenerator sitemapGenerator = Mockito.mock(SitemapGenerator.class);
        pageGenerator = new StaticPageGeneratorService(verdictService, linkBuilder, templateEngine, verdictSeoService,
                sitemapGenerator);
    }

    @Test
    void verifySeoEnhancements(@TempDir Path tempDir) throws IOException {
        // Given
        String metroCode = "chicago_il";
        String era = "1950s";
        String outputDir = tempDir.toString();

        // Construct Mock Verdict with data for SEO checks
        Verdict mockVerdict = new Verdict();
        mockVerdict.setHeadline("Chicago 1950s Repair Verdict");
        mockVerdict.setCostRangeLabel("$10k - $20k");
        mockVerdict.setDealKiller(false);
        mockVerdict.setTier("TIER_2_WARNING");

        // Plan & Items for Price Calc
        SortedPlan plan = new SortedPlan();
        List<RiskAdjustedItem> mustDo = new ArrayList<>();

        RiskAdjustedItem item1 = new RiskAdjustedItem();
        item1.setPrettyName("Roof Repair");
        item1.setAdjustedCost(5000.0);
        mustDo.add(item1);

        RiskAdjustedItem item2 = new RiskAdjustedItem();
        item2.setPrettyName("Electric Update");
        item2.setAdjustedCost(3000.0);
        mustDo.add(item2);

        plan.setMustDo(mustDo);
        plan.setShouldDo(Collections.emptyList());
        mockVerdict.setPlan(plan);

        // ContextBriefing is NULL to verify robust handling
        mockVerdict.setContextBriefing(null);

        when(verdictService.generateVerdict(any())).thenReturn(mockVerdict);

        // When
        try {
            java.lang.reflect.Method method = StaticPageGeneratorService.class.getDeclaredMethod("generateSinglePage",
                    String.class, String.class, String.class);
            method.setAccessible(true);
            method.invoke(pageGenerator, metroCode, era, outputDir);
        } catch (Exception e) {
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable target = ((java.lang.reflect.InvocationTargetException) e).getTargetException();
                if (target instanceof RuntimeException) {
                    throw (RuntimeException) target;
                }
                throw new RuntimeException(target);
            }
            throw new RuntimeException(e);
        }

        // Then
        Path generatedFile = tempDir.resolve("chicago-il/1950s.html");
        assertTrue(Files.exists(generatedFile), "HTML file check");

        String content = Files.readString(generatedFile);

        // 1. Check Title Optimization (Must NOT contain Calculator)
        assertFalse(content.contains("Calculator"), "Title check");

        // 2. Check Domain Update
        assertTrue(content.contains("https://lifeverdict.com"), "Domain check");

        // 3. Check JSON-LD Schema (with spaces handling)
        // Checks based on JTE output format
        assertTrue(content.contains("\"@type\": \"WebApplication\""), "Schema Type check");
        assertTrue(content.contains("\"lowPrice\": \"3,000\""), "Price Low check");
        assertTrue(content.contains("\"highPrice\": \"8,000\""), "Price High check");

        // 4. Check Visible FAQ Section
        assertTrue(content.contains("class=\"card faq-section\""), "FAQ Section check");

        // 5. Check FragmentLibrary (Null Safety)
        assertTrue(content.contains("Climate Impact"), "Fragment Header check");
        // Verify default fallbacks are working (e.g. "standard maintenance protocols")
        assertTrue(content.contains("protocol"), "Fragment Content check");

        System.out.println("SEO Verification Test Passed for " + generatedFile);
    }
}
