package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping;
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
        when(linkBuilder.getNearbyMetrosInEra(any(), any(), any())).thenReturn(Collections.emptyList());
        when(linkBuilder.getRelatedCitiesInState(any(), any(), any())).thenReturn(Collections.emptyList());
        when(linkBuilder.getOtherRisksInSameHome(any(), any(), any())).thenReturn(Collections.emptyList());

        // Mock VerdictEngineService Master Data
        DataMapping.MetroMasterData masterData = new DataMapping.MetroMasterData();
        DataMapping.MetroCityData chicagoData = new DataMapping.MetroCityData();
        chicagoData.setLaborMult(1.0);
        chicagoData.setClimateZone("4");
        chicagoData.setRisk("Flood");
        chicagoData.setFoundation("Slab");
        masterData.setData(Collections.singletonMap("chicago_il", chicagoData));
        when(verdictService.getMetroMasterData()).thenReturn(masterData);

        // Create Service
        com.livingcostcheck.home_repair.seo.VerdictSeoService verdictSeoService = new com.livingcostcheck.home_repair.seo.VerdictSeoService();
        pageGenerator = new StaticPageGeneratorService(verdictService, linkBuilder, templateEngine, verdictSeoService);
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
        item1.setItemCode("ROOFING");
        item1.setRiskFlags(Collections.singletonList("SAFETY_HAZARD"));
        item1.setExplanation("Old shingles need replacement.");
        mustDo.add(item1);

        RiskAdjustedItem item2 = new RiskAdjustedItem();
        item2.setPrettyName("HVAC Service");
        item2.setAdjustedCost(3000.0);
        item2.setItemCode("HVAC");
        item2.setRiskFlags(Collections.emptyList());
        item2.setExplanation("Regular maintenance needed.");
        mustDo.add(item2);

        RiskAdjustedItem item3 = new RiskAdjustedItem();
        item3.setPrettyName("Electric Update");
        item3.setAdjustedCost(3000.0);
        item3.setItemCode("ELECTR");
        item3.setRiskFlags(Collections.emptyList());
        item3.setExplanation("Old wiring check.");
        mustDo.add(item3);

        plan.setMustDo(mustDo);
        plan.setShouldDo(Collections.emptyList());
        mockVerdict.setPlan(plan);

        // ContextBriefing is NULL to verify robust handling
        mockVerdict.setContextBriefing(null);

        when(verdictService.generateVerdict(any())).thenReturn(mockVerdict);

        // When
        try {
            java.lang.reflect.Method method = StaticPageGeneratorService.class.getDeclaredMethod("generateSinglePage",
                    String.class, String.class, String.class, String.class);
            method.setAccessible(true);
            method.invoke(pageGenerator, metroCode, era, outputDir, "February 2026");
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
        assertTrue(content.contains("\"@type\":\"FAQPage\""), "FAQ Schema check");
        assertTrue(content.contains("\"@type\":\"HowTo\""), "HowTo Schema check");
        assertTrue(content.contains("3,000"), "Price Low check");
        assertTrue(content.contains("11,000"), "Price High check");

        // 4. Check Visible FAQ Section
        assertTrue(content.contains("Frequently Asked Questions"), "FAQ Section check");

        // 5. Check FragmentLibrary (Null Safety)
        // Verify regional insight contains part of the strategy
        assertTrue(content.contains("maintenance"), "Fragment Content check");

        System.out.println("SEO Verification Test Passed for " + generatedFile);
    }
}
