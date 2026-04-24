package com.livingcostcheck.home_repair.service;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowSummary;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionDefenseSignal;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponseInput;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponsePacket;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionExclusionItem;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionReadinessGate;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.CostRange;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.LoanType;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.SortedPlan;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InspectionResponseServiceTest {

    private final InspectionResponseService service = new InspectionResponseService();

    @Test
    void extractFindingsReadsReportTextAndQuickAdds() {
        List<String> findings = service.extractFindings("""
                1. Active roof leak above rear bedroom
                2. Federal Pacific panel flagged by inspector
                3. Minor paint scuffs in hallway
                """, List.of("Main sewer line shows active backup risk"));

        assertEquals(4, findings.size());
        assertTrue(findings.contains("Active roof leak above rear bedroom"));
        assertTrue(findings.contains("Federal Pacific panel flagged by inspector"));
        assertTrue(findings.contains("Minor paint scuffs in hallway"));
        assertTrue(findings.contains("Main sewer line shows active backup risk"));
    }

    @Test
    void buildPacketSeparatesLeverageFromCosmeticNoise() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.MID_FIVE_FIGURES);
        verdict.setCostRangeLabel("Mid-five figures");
        verdict.setExactCostEstimate(30000.0);
        verdict.setPrimaryCostDriver("Primary Cost Driver: Roof Replacement");
        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>());
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of("ROOFING"),
                List.of(
                        "Active roof leak above rear bedroom",
                        "HVAC age needs verification",
                        "Minor paint scuffs in hallway"),
                List.of(new InspectionEvidenceRef(
                        "Active roof leak above rear bedroom",
                        List.of("Report p.12: Active roof leak above rear bedroom"))),
                "sample-inspection.pdf",
                "HAS_ONE",
                "SEVEN_TO_TWENTY_ONE_DAYS",
                "AUTO",
                "DRAFTING_FIRST_NOTICE",
                "",
                LoanType.FHA,
                "Maple Street response window",
                "123 Maple St, Atlanta, GA",
                "Mina Kim",
                "Alex Park",
                "Atlanta",
                "1980 - 1995",
                "credit");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Atlanta", input);

        assertTrue(packet.mustFixNow().contains("Active roof leak above rear bedroom"));
        assertTrue(packet.verifyNext().contains("HVAC age needs verification"));
        assertTrue(packet.doNotLead().contains("Minor paint scuffs in hallway"));
        assertTrue(packet.agentNegotiationScript().contains("FHA financing"));
        assertTrue(packet.fullPacketText().contains("Do not lead with"));
        assertTrue(packet.dataAnchor().contains("scoped exposure"));
        assertTrue(packet.pricingBreakdown().stream().anyMatch(item -> item.contains("opening scope")));
        assertEquals("sample-inspection.pdf", packet.evidenceSourceLabel());
        assertTrue(packet.matchedEvidence().stream().anyMatch(item -> item.findingLabel().contains("Active roof leak")));
        assertTrue(packet.lenderVisibleSignals().contains("Active roof leak or water intrusion"));
        assertTrue(packet.lenderVisibleNote().contains("lender-visible"));
        assertTrue(packet.targetAsk() < verdict.getExactCostEstimate());
        assertTrue(packet.excludedFindings().stream().anyMatch(item -> item.findingLabel().contains("Minor paint scuffs")));
        assertTrue(packet.workflowLabel().contains("Credit-first"));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("credit amendment")));
        assertTrue(packet.fullPacketText().contains("Excluded on purpose"));
        assertEquals(6, packet.readinessGates().size());
        assertTrue(packet.readinessGates().stream().anyMatch(gate -> gate.label().equals("Evidence pack sufficient") && gate.status().equals("PASS")));
        assertTrue(packet.defenseSignals().stream().anyMatch(signal -> signal.label().equals("Number basis")));
        assertTrue(packet.defenseSignals().stream().anyMatch(signal -> signal.label().equals("Seller pushback")));
        assertTrue(packet.verdictRationale().stream().anyMatch(item -> item.contains("Verdict is")));
        assertTrue(!packet.missingEvidence().isEmpty());
        assertTrue(packet.reviewCaveats().stream().anyMatch(item -> item.contains("Quote caveat")));
        assertTrue(packet.reviewCaveats().stream().anyMatch(item -> item.contains("Lender caveat")));
        assertTrue(packet.reviewCaveats().stream().anyMatch(item -> item.contains("Form caveat")));
        assertTrue(packet.fullPacketText().contains("Pre-send review"));
        assertTrue(packet.fullPacketText().contains("Why this verdict"));
        assertTrue(packet.fullPacketText().contains("Missing or weak evidence"));
        assertTrue(packet.fullPacketText().contains("Caveats before send"));
    }

    @Test
    void verdictLeadAnchorCanPromoteFindingOutOfVerifyBucket() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setExactCostEstimate(18000.0);

        RiskAdjustedItem hvacLead = RiskAdjustedItem.builder()
                .itemCode("HVAC_HEAT_PUMP_CENTRAL")
                .prettyName("HVAC Heat Pump Central")
                .category("MECHANICAL")
                .adjustedCost(9500.0)
                .mandatory(true)
                .riskFlags(List.of("CRITICAL"))
                .build();

        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>(List.of(hvacLead)));
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of(),
                List.of("HVAC heat pump failed during cooling cycle"),
                List.of(new InspectionEvidenceRef(
                        "HVAC heat pump failed during cooling cycle",
                        List.of("Report p.8: HVAC heat pump failed during cooling cycle"))),
                "sample-inspection.pdf",
                "NONE",
                "UNDER_7_DAYS",
                "AUTO",
                "DRAFTING_FIRST_NOTICE",
                "",
                LoanType.CONVENTIONAL,
                "",
                "",
                "",
                "",
                "",
                "",
                "direct");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Atlanta", input);

        assertTrue(packet.mustFixNow().contains("HVAC heat pump failed during cooling cycle"));
        assertTrue(packet.matchedEvidence().stream().anyMatch(item -> item.findingLabel().contains("HVAC heat pump")));
        assertTrue(packet.pricingBreakdown().stream().anyMatch(item -> item.contains("HVAC Heat Pump Central")));
        assertTrue(packet.lenderVisibleSignals().contains("HVAC failure affecting habitability"));
        assertTrue(packet.workflowTitle().contains("transaction file"));
    }

    @Test
    void coloradoObjectionFlowUsesStateAwareHandoff() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setExactCostEstimate(22000.0);

        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>());
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of("FOUNDATION"),
                List.of("Foundation crack with settlement concern", "Minor paint touch-up needed"),
                List.of(new InspectionEvidenceRef(
                        "Foundation crack with settlement concern",
                        List.of("Report p.18: Foundation crack with settlement concern"))),
                "sample-inspection.pdf",
                "NONE",
                "UNDER_7_DAYS",
                "AUTO",
                "DRAFTING_FIRST_NOTICE",
                "",
                LoanType.FHA,
                "Denver objection",
                "456 Cedar Ave, Denver, CO 80206",
                "Buyer",
                "Agent",
                "Denver",
                "pre-1950",
                "objection");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Denver", input);

        assertTrue(packet.workflowLabel().contains("Colorado"));
        assertTrue(packet.workflowTitle().contains("Colorado"));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("Inspection Objection Notice")));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("Inspection Resolution Deadline")));
        assertTrue(packet.workflowNote().contains("state-approved"));
    }

    @Test
    void californiaRepairRequestUsesInvestigationContingencyLanguage() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setExactCostEstimate(25000.0);
        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>());
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of("ROOFING"),
                List.of("Active roof leak above garage", "Minor paint touch-up needed"),
                List.of(),
                "",
                "NONE",
                "SEVEN_TO_TWENTY_ONE_DAYS",
                "CA_INVESTIGATION",
                "DRAFTING_FIRST_NOTICE",
                "",
                LoanType.CONVENTIONAL,
                "LA repair request",
                "789 Palm Dr, Los Angeles, CA 90049",
                "Buyer",
                "Agent",
                "Los Angeles",
                "1980-1995",
                "repair_request");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Los Angeles", input);

        assertTrue(packet.workflowLabel().contains("California"));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("Request for Repair (RR)")));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("RRRR") || item.contains("AEA")));
        assertTrue(packet.workflowNote().contains("17 days"));
    }

    @Test
    void texasCreditFlowUsesOptionPeriodAndAmendmentLanguage() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setExactCostEstimate(21000.0);
        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>());
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of("PLUMBING"),
                List.of("Main sewer backup risk at exterior cleanout"),
                List.of(),
                "",
                "HAS_ONE",
                "UNDER_7_DAYS",
                "TX_OPTION",
                "DRAFTING_FIRST_NOTICE",
                "",
                LoanType.CONVENTIONAL,
                "Austin credit ask",
                "1012 Oak Ln, Austin, TX 78704",
                "Buyer",
                "Agent",
                "Austin",
                "1970-1980",
                "credit");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Austin", input);

        assertTrue(packet.workflowLabel().contains("Texas"));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("option period")));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("TREC Amendment to Contract")));
        assertTrue(packet.workflowNote().contains("Amendment to Contract"));
    }

    @Test
    void floridaAsIsFlowUsesAsIsSpecificLanguage() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setExactCostEstimate(19000.0);
        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>());
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of("WATER"),
                List.of("Water intrusion under master bedroom window"),
                List.of(),
                "",
                "NONE",
                "SEVEN_TO_TWENTY_ONE_DAYS",
                "FL_AS_IS",
                "DRAFTING_FIRST_NOTICE",
                "",
                LoanType.FHA,
                "Miami packet",
                "22 Bay Ave, Miami, FL 33139",
                "Buyer",
                "Agent",
                "Miami",
                "1995-2010",
                "ask");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Miami", input);

        assertTrue(packet.workflowLabel().contains("Florida"));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("AS IS")));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("termination")));
        assertTrue(packet.workflowNote().contains("inspection-period"));
    }

    @Test
    void expiredContingencyBlocksSendReadiness() {
        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setExactCostEstimate(17000.0);
        SortedPlan plan = new SortedPlan();
        plan.setMustDo(new ArrayList<>());
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());
        verdict.setPlan(plan);

        InspectionResponseInput input = new InspectionResponseInput(
                List.of("ROOFING"),
                List.of("Active roof leak above garage"),
                List.of(),
                "",
                "NONE",
                "UNDER_7_DAYS",
                "CA_INVESTIGATION",
                "CONTINGENCY_REMOVED_OR_EXPIRED",
                "2026-04-26T17:00",
                LoanType.CONVENTIONAL,
                "Expired LA file",
                "789 Palm Dr, Los Angeles, CA 90049",
                "Buyer",
                "Agent",
                "Los Angeles",
                "1980-1995",
                "repair_request");

        InspectionResponsePacket packet = service.buildPacket(verdict, "Los Angeles", input);

        assertEquals("Not sendable", packet.readinessLabel());
        assertTrue(packet.readinessNote().contains("hard gate"));
        assertTrue(packet.workflowNote().contains("advisory framing"));
        assertTrue(packet.workflowSteps().stream().anyMatch(item -> item.contains("full inspection leverage remains")));
        assertTrue(packet.readinessGates().stream().anyMatch(gate -> gate.label().equals("Deadline alive") && gate.status().equals("FAIL")));
        assertTrue(packet.readinessGates().stream().anyMatch(gate -> gate.label().equals("Stage and rights preserved") && gate.status().equals("FAIL")));
    }

    @Test
    void storedContextPreservesWorkspaceMetadata() {
        String storedContext = service.buildStoredContext(
                List.of("ROOFING"),
                List.of("Active roof leak above rear bedroom"),
                List.of(),
                "",
                "HAS_ONE",
                "SEVEN_TO_TWENTY_ONE_DAYS",
                "AUTO",
                "COUNTER_RECEIVED",
                "2026-04-28T17:00",
                LoanType.FHA,
                "Maple Street response window",
                "123 Maple St, Atlanta, GA",
                "Mina Kim",
                "Alex Park",
                "Broad U.S. baseline",
                "typical mid-age housing stock",
                "ask");

        InspectionResponseInput parsed = service.parseStoredContext(storedContext);

        assertEquals("Maple Street response window", parsed.caseLabel());
        assertEquals("123 Maple St, Atlanta, GA", parsed.propertyAddress());
        assertEquals("Mina Kim", parsed.clientName());
        assertEquals("Alex Park", parsed.agentName());
        assertEquals("Broad U.S. baseline", parsed.marketContextLabel());
        assertEquals("typical mid-age housing stock", parsed.eraContextLabel());
        assertEquals("AUTO", parsed.contractWorkflow());
        assertEquals("COUNTER_RECEIVED", parsed.dealStage());
        assertEquals("2026-04-28T17:00", parsed.responseDeadlineAt());
        assertEquals("ask", parsed.acquisitionEntry());
    }

    @Test
    void buyerApprovalClearsOpenReviewState() {
        InspectionResponsePacket packet = readyPacket();
        UUID verdictId = UUID.randomUUID();

        InspectionCaseWorkflowSummary summary = service.buildCaseWorkflow(packet, List.of(
                new EventLog(verdictId, EventLog.EventType.REQUEST_REVIEW,
                        "state=REQUEST_REVIEW|actor=Alex%20Park|note=Need%20one%20more%20pass"),
                new EventLog(verdictId, EventLog.EventType.BUYER_APPROVED,
                        "state=BUYER_APPROVED|actor=Mina%20Kim|note=Approved%20for%20send")));

        assertEquals("READY", summary.currentState());
        assertEquals("Ready", summary.currentLabel());
        assertTrue(summary.currentNote().contains("send-ready"));
        assertTrue(summary.timeline().stream().anyMatch(event -> event.note().contains("Actor: Alex Park")));
        assertTrue(summary.timeline().stream().anyMatch(event -> event.note().contains("Approved for send")));
    }

    @Test
    void counterReceivedBeatsPlainSentState() {
        InspectionResponsePacket packet = readyPacket();
        UUID verdictId = UUID.randomUUID();

        InspectionCaseWorkflowSummary summary = service.buildCaseWorkflow(packet, List.of(
                new EventLog(verdictId, EventLog.EventType.BUYER_APPROVED,
                        "state=BUYER_APPROVED|actor=Buyer|note=Approved"),
                new EventLog(verdictId, EventLog.EventType.MARK_SENT,
                        "state=MARK_SENT|actor=Agent|note=Sent%20to%20listing%20side"),
                new EventLog(verdictId, EventLog.EventType.COUNTER_RECEIVED,
                        "state=COUNTER_RECEIVED|actor=Agent|note=Seller%20countered%20at%20%248k")));

        assertEquals("COUNTERED", summary.currentState());
        assertEquals("Seller counter received", summary.currentLabel());
        assertTrue(summary.currentNote().contains("outcome negotiation"));
        assertTrue(summary.recommendedNextAction().contains("fallback"));
    }

    private InspectionResponsePacket readyPacket() {
        List<InspectionReadinessGate> gates = List.of(
                new InspectionReadinessGate("PASS", "Deadline alive", "Exact deadline captured."),
                new InspectionReadinessGate("PASS", "Form path locked", "State form is locked."),
                new InspectionReadinessGate("PASS", "Evidence pack sufficient", "Evidence is attached."),
                new InspectionReadinessGate("PASS", "Financing risk cleared", "No financing blocker."),
                new InspectionReadinessGate("PASS", "Stage and rights preserved", "Stage is explicit."),
                new InspectionReadinessGate("PASS", "Send bundle owned", "Owner and file identity are present."));
        return new InspectionResponsePacket(
                "Send: ask cleared pre-send review",
                "Ready packet for workflow-state tests.",
                List.of("HVAC age needs verification"),
                List.of("HVAC age needs verification"),
                List.of(),
                List.of(),
                List.of(),
                "",
                List.of(),
                List.of(),
                List.of(new InspectionExclusionItem("Cosmetic touch-up", "Excluded on purpose.")),
                "One quote in reserve",
                "response window under 7 days",
                "conventional financing",
                "No financing trigger was detected.",
                "Deadline captured.",
                "Evidence attached.",
                "scoped exposure",
                "Pricing logic stays narrow.",
                List.of("HVAC line item kept in scope."),
                List.of(),
                "No lender-visible signal detected.",
                "Ready to send",
                "All hard gates are green.",
                gates,
                List.of(new InspectionDefenseSignal("PASS", "Send posture", "All gates are green.", "Send with evidence.")),
                List.of("Verdict is Ready to send because every hard gate passed."),
                List.of("Lead items have exhibit support."),
                List.of("Quote caveat: use attached quote as support."),
                6,
                0,
                0,
                92,
                List.of("Every hard gate is green."),
                "Credit-first workflow",
                "How to move this into the real credit request",
                "This packet is ready for send.",
                List.of("Move the ask into the signed amendment."),
                List.of("Send the packet."),
                8500,
                12000,
                14500,
                "8,500",
                "12,000",
                "14,500",
                "Start with a seller-credit request.",
                "We are requesting a seller credit before closing.",
                "If the seller pushes back, hold the fallback.",
                "Full packet text");
    }
}
