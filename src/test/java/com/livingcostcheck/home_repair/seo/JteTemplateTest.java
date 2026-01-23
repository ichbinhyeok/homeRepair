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
                plan.setMustDo(new ArrayList<>());
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

                StringOutput output = new StringOutput();
                assertDoesNotThrow(() -> templateEngine.render("seo/static-verdict.jte", params, output),
                                "static-verdict.jte should render without errors");

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
