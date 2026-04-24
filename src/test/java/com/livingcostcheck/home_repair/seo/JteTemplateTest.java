package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionDefenseSignal;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowEvent;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowSummary;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionExclusionItem;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionReadinessGate;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponsePacket;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionWorkspaceSummary;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JteTemplateTest {

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
                params.put("workspace", new InspectionWorkspaceSummary(
                                history.getId(),
                                "Maple Street response window",
                                "123 Maple St, Atlanta, GA",
                                "Mina Kim",
                                "Alex Park",
                                "Atlanta",
                                "response window 1-3 weeks",
                                "FHA financing",
                                "Apr 23, 7:30 PM",
                                "/home-repair/result/" + history.getId()));
                params.put("packet", new InspectionResponsePacket(
                                "Revise before send",
                                "Pre-send check fixture.",
                                List.of("Federal Pacific panel flagged by inspector"),
                                List.of("Federal Pacific panel flagged by inspector"),
                                List.of("Roof age needs verification"),
                                List.of("Minor paint scuffs"),
                                List.of(new InspectionEvidenceRef(
                                                "Federal Pacific panel flagged by inspector",
                                                List.of("Report p.14: Federal Pacific panel flagged by inspector"))),
                                "sample-inspection.pdf",
                                List.of("Inspection report page reference"),
                                List.of("Preference upgrades"),
                                List.of(new InspectionExclusionItem(
                                                "Preference upgrades",
                                                "Visible-before-offer preference ask.")),
                                "no quote attached; negotiation estimate only",
                                "closing in the next 1-3 weeks",
                                "FHA financing",
                                "Keep the request anchored to safety and habitability.",
                                "Use the short deadline to keep the ask clear.",
                                "Start with the inspection notes.",
                                "$10k - $20k",
                                "Cost data sizes leverage; findings decide scope.",
                                List.of("Electrical Panel Upgrade: $9,200 counted in the opening scope."),
                                List.of("Unsafe electrical panel or wiring"),
                                "These items can read as lender-visible habitability or appraisal issues.",
                                "Ready to send",
                                "The packet is narrow enough to send now.",
                                List.of(
                                                new InspectionReadinessGate("PASS", "Deadline alive", "Exact deadline captured."),
                                                new InspectionReadinessGate("WARN", "Form path locked", "Confirm the exact amendment form.")),
                                List.of(
                                                new InspectionDefenseSignal("WARN", "Number basis", "The ask is inspection-estimated.", "Confirm quote support."),
                                                new InspectionDefenseSignal("PASS", "Evidence support", "Lead item has report support.", "Attach the cited page.")),
                                List.of("Verdict is Draft only because one gate needs confirmation."),
                                List.of("Attach the exact report page for roof age before sending."),
                                List.of("Quote caveat: do not call this estimate a contractor bid."),
                                1,
                                1,
                                0,
                                88,
                                List.of("The ask has a safety anchor."),
                                "Credit-first workflow",
                                "How to move this into the real credit request",
                                "Use this as support language, not as a substitute for the signed amendment.",
                                List.of("Move the ask into the credit amendment."),
                                List.of("Send the agent-ready request."),
                                9000,
                                12500,
                                15000,
                                "9,000",
                                "12,500",
                                "15,000",
                                "Start with a seller-credit request.",
                                "We are requesting a seller credit before closing.",
                                "If the seller pushes back, fall back to safety items.",
                                "Full packet text"));
                params.put("title", "Result Title");
                params.put("verdictH1", "Result H1");
                params.put("caseWorkflow", new InspectionCaseWorkflowSummary(
                                "REVIEW",
                                "In review",
                                "Buyer approval is not recorded yet.",
                                "Record buyer approval once the packet is settled.",
                                List.of(new InspectionCaseWorkflowEvent(
                                                "Packet generated",
                                                "Snapshot: status=Ready to send",
                                                "Apr 24, 9:00 AM"))));

                // Note: result.jte uses @template.layout which might require 'pages/result.jte'
                // or similar path depending on root.
                // Assuming 'src/main/jte' is root, pages/result.jte is at 'pages/result.jte'

                StringOutput output = new StringOutput();
                assertDoesNotThrow(() -> templateEngine.render("pages/result.jte", params, output),
                                "result.jte should render without errors");
                assertTrue(output.toString().contains("Why this verdict was chosen"));
                assertTrue(output.toString().contains("Missing or weak evidence"));
                assertTrue(output.toString().contains("Boundaries the agent should not overstate"));

                System.out.println(
                                "Result Output snippet: " + output.toString().substring(0,
                                                Math.min(output.toString().length(), 200)));
        }
}
