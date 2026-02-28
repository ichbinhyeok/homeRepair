package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JteTemplateTest {

        @Test
        public void testStaticVerdictTemplate() {
                TemplateEngine templateEngine = TemplateEngine.create(
                                new DirectoryCodeResolver(Paths.get("src/main/jte")),
                                ContentType.Html);

                // Mock Data
                VerdictDTOs.Verdict verdict = new VerdictDTOs.Verdict();
                verdict.setTier("LOW_RISK");
                verdict.setHeadline("Test Headline");
                verdict.setCostRangeLabel("$100 - $200");

                VerdictDTOs.SortedPlan plan = new VerdictDTOs.SortedPlan();
                VerdictDTOs.RiskAdjustedItem sampleRisk = VerdictDTOs.RiskAdjustedItem.builder()
                                .itemCode("ELECTRICAL_PANEL_UPGRADE")
                                .prettyName("Electrical Panel Upgrade")
                                .category("SAFETY")
                                .adjustedCost(9200.0)
                                .riskFlags(List.of(
                                                "ERA_RISK: KNOB_AND_TUBE_WIRING",
                                                "ERA_LABOR_ADJUSTMENT: 2.5x",
                                                "CRITICAL_SEVERITY_SURCHARGE"))
                                .mandatory(true)
                                .explanation(
                                                "[FINANCIAL RISK PROMOTION] High liability detected ($9,200). <strong>Legacy wiring risk detected.</strong>")
                                .definition("Legacy wiring systems often require full panel upgrades.")
                                .build();
                plan.setMustDo(new ArrayList<>(List.of(sampleRisk)));
                plan.setShouldDo(new ArrayList<>());
                verdict.setPlan(plan);
                verdict.setContextBriefing(new VerdictDTOs.ContextBriefing());

                Map<String, Object> params = new HashMap<>();
                params.put("title", "Test Title");
                params.put("h1Content", "Test H1");
                params.put("metroCode", "AUSTIN_TX");
                params.put("metroName", "Austin");
                params.put("era", "PRE_1950");
                params.put("eraName", "Pre-1950");
                params.put("verdict", verdict);
                params.put("eraLinks", new ArrayList<InternalLink>());
                params.put("cityLinks", new ArrayList<InternalLink>());
                params.put("baseUrl", "http://localhost");
                params.put("canonicalUrl", "http://localhost/test");
                params.put("faqSchema", "");
                params.put("stateLinks", new ArrayList<InternalLink>());
                params.put("climateFragment", "Climate Info");
                params.put("eraFragment", "Era Info");
                params.put("costFragment", "Cost Info");
                params.put("faqItems", new ArrayList<Map<String, String>>());
                params.put("lowPrice", "100");
                params.put("highPrice", "200");
                params.put("breadcrumbSchema", "");
                params.put("metroRisk", "Aging Infrastructure");
                params.put("climateZone", "5A");
                params.put("foundation", "FULL_BASEMENT");
                params.put("avgHouseAge", "N/A");
                params.put("howToSchema", "");
                params.put("dateString", "February 2026");

                StringOutput output = new StringOutput();
                assertDoesNotThrow(() -> templateEngine.render("seo/static-verdict.jte", params, output),
                                "static-verdict.jte should render without errors");

                String html = output.toString();
                assertFalse(html.contains("ERA_RISK"), "Template should not leak internal risk flags");
                assertFalse(html.contains("CRITICAL_SEVERITY_SURCHARGE"),
                                "Template should not leak internal surcharge flags");
                assertFalse(html.contains("[FINANCIAL RISK PROMOTION]"),
                                "Template should not leak internal explanation tokens");
                assertTrue(html.contains("Era-specific hazard"),
                                "Template should render user-facing risk labels");

                System.out.println(
                                "Static Output snippet: " + output.toString().substring(0,
                                                Math.min(output.toString().length(), 200)));
        }

        @Test
        public void testResultTemplate() {
                TemplateEngine templateEngine = TemplateEngine.create(
                                new DirectoryCodeResolver(Paths.get("src/main/jte")),
                                ContentType.Html);

                // Mock Verdict
                VerdictDTOs.Verdict verdict = new VerdictDTOs.Verdict();
                verdict.setTier("DEAL_KILLER");
                verdict.setHeadline("Risk Headline");
                verdict.setCostRangeLabel("$10k - $20k");
                verdict.setItemsAnalyzed(10);
                verdict.setStrategyUsed("TEST_STRATEGY");
                verdict.setStrategyExplanation("Test explanation");
                verdict.setExactCostEstimate(15000.0);

                VerdictDTOs.SortedPlan plan = new VerdictDTOs.SortedPlan();
                plan.setMustDo(new ArrayList<>());
                plan.setShouldDo(new ArrayList<>());
                verdict.setPlan(plan);
                verdict.setContextBriefing(new VerdictDTOs.ContextBriefing());

                // Mock History
                VerdictHistory history = new VerdictHistory();
                history.setId(UUID.randomUUID());

                Map<String, Object> params = new HashMap<>();
                params.put("verdict", verdict);
                params.put("history", history);
                params.put("title", "Result Title");
                params.put("verdictH1", "Result H1");

                // Note: result.jte uses @template.layout which might require 'pages/result.jte'
                // or similar path depending on root.
                // Assuming 'src/main/jte' is root, pages/result.jte is at 'pages/result.jte'

                StringOutput output = new StringOutput();
                assertDoesNotThrow(() -> templateEngine.render("pages/result.jte", params, output),
                                "result.jte should render without errors");

                System.out.println(
                                "Result Output snippet: " + output.toString().substring(0,
                                                Math.min(output.toString().length(), 200)));
        }
}
