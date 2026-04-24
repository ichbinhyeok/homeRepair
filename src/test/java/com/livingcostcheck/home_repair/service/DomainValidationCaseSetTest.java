package com.livingcostcheck.home_repair.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionReadinessGate;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponseInput;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponsePacket;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.CostRange;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.LoanType;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.SortedPlan;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainValidationCaseSetTest {

    private static final int EXPECTED_CASES_PER_RESOURCE = 100;

    private static final List<String> VALIDATION_CASE_RESOURCES = List.of(
            "inspection-validation-cases.json",
            "inspection-validation-cases-agent1-financing.json",
            "inspection-validation-cases-agent2-contracts.json",
            "inspection-validation-cases-agent3-systems.json",
            "inspection-validation-cases-agent4-personas.json",
            "inspection-validation-cases-agent5-acquisition.json",
            "inspection-validation-cases-agent6-insurance-hoa-local.json",
            "inspection-validation-cases-agent7-negotiation-failure.json",
            "inspection-validation-cases-agent8-trust-reporting.json",
            "inspection-validation-cases-agent9-acquisition-serp.json",
            "inspection-validation-cases-agent10-ops-persona.json");

    private static final int EXPECTED_TOTAL_CASES = EXPECTED_CASES_PER_RESOURCE * VALIDATION_CASE_RESOURCES.size();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final InspectionResponseService service = new InspectionResponseService();

    @Test
    void validationFixturesContainElevenHundredUsableDomainCases() throws Exception {
        ValidationCaseSet caseSet = loadCaseSet();

        for (String resourceName : VALIDATION_CASE_RESOURCES) {
            assertEquals(EXPECTED_CASES_PER_RESOURCE, loadCaseSet(resourceName).cases().size(),
                    resourceName + " must contain exactly " + EXPECTED_CASES_PER_RESOURCE + " validation cases");
        }
        assertEquals(EXPECTED_TOTAL_CASES, caseSet.cases().size());
        assertEquals(EXPECTED_TOTAL_CASES, caseSet.cases().stream().map(ValidationCase::id).collect(LinkedHashSet::new,
                Set::add, Set::addAll).size());
        assertFalse(caseSet.meta().sources().isEmpty(), "internet research sources must be attached");

        for (ValidationCase validationCase : caseSet.cases()) {
            assertNotBlank(validationCase.id(), validationCase.id() + " id");
            assertNotBlank(validationCase.sourceBasis(), validationCase.id() + " sourceBasis");
            assertNotBlank(validationCase.persona(), validationCase.id() + " persona");
            assertNotBlank(validationCase.state(), validationCase.id() + " state");
            assertNotBlank(validationCase.deal(), validationCase.id() + " deal");
            assertFalse(validationCase.findings().isEmpty(), validationCase.id() + " findings");
            assertTrue(validationCase.hasValidationRationale(), validationCase.id() + " validation rationale");
        }
    }

    @Test
    void everyValidationCaseProducesAPacketWithVisibleSafetyRails() throws Exception {
        for (ValidationCase validationCase : loadCaseSet().cases()) {
            InspectionResponsePacket packet = buildPacket(validationCase);

            assertNotBlank(packet.fullPacketText(), validationCase.id() + " full packet");
            assertNotBlank(packet.readinessLabel(), validationCase.id() + " readiness label");
            assertEquals(6, packet.readinessGates().size(), validationCase.id() + " readiness gates");
            assertTrue(packet.readinessGates().stream().anyMatch(gate -> gate.label().equals("Evidence pack sufficient")),
                    validationCase.id() + " evidence gate");
            assertTrue(packet.readinessGates().stream().anyMatch(gate -> gate.label().equals("Financing risk cleared")),
                    validationCase.id() + " financing gate");
            assertFalse(packet.defenseSignals().isEmpty(), validationCase.id() + " pre-send review");
            assertTrue(packet.defenseSignals().stream().anyMatch(signal -> signal.label().equals("Number basis")),
                    validationCase.id() + " number basis review");
            assertTrue(packet.defenseSignals().stream().anyMatch(signal -> signal.label().equals("Send posture")),
                    validationCase.id() + " send posture review");
            assertFalse(packet.verdictRationale().isEmpty(), validationCase.id() + " verdict rationale");
            assertFalse(packet.missingEvidence().isEmpty(), validationCase.id() + " missing evidence");
            assertTrue(packet.reviewCaveats().stream().anyMatch(caveat -> caveat.contains("Quote caveat")),
                    validationCase.id() + " quote caveat");
            assertTrue(packet.reviewCaveats().stream().anyMatch(caveat -> caveat.contains("Lender caveat")),
                    validationCase.id() + " lender caveat");
            assertTrue(packet.reviewCaveats().stream().anyMatch(caveat -> caveat.contains("Form caveat")),
                    validationCase.id() + " form caveat");
            assertNotBlank(packet.dataAnchorNote(), validationCase.id() + " data anchor note");
            assertTrue(packet.fullPacketText().contains("Excluded on purpose"),
                    validationCase.id() + " must show exclusions");
            assertTrue(packet.fullPacketText().contains("Why this verdict"),
                    validationCase.id() + " must explain verdict");
            assertTrue(packet.fullPacketText().contains("Missing or weak evidence"),
                    validationCase.id() + " must show evidence gaps");
            assertTrue(packet.fullPacketText().contains("Caveats before send"),
                    validationCase.id() + " must show caveats");
            assertTrue(packet.targetAsk() <= packet.stretchAsk(), validationCase.id() + " ask ordering");
            assertTrue(packet.defendableAsk() <= packet.targetAsk(), validationCase.id() + " fallback ordering");
        }
    }

    @Test
    void validationCasesCoverRevenueCriticalSegmentsAndFailureModes() throws Exception {
        ValidationCaseSet caseSet = loadCaseSet();
        String corpus = caseSet.cases().stream()
                .map(validationCase -> String.join(" ",
                        validationCase.persona(),
                        validationCase.state(),
                        validationCase.deal(),
                        validationCase.validationRationale(),
                        String.join(" ", validationCase.findings())))
                .collect(Collectors.joining(" "))
                .toLowerCase(Locale.ENGLISH);

        assertTrue(caseSet.cases().stream().map(ValidationCase::state).collect(Collectors.toSet()).size() >= 15,
                "case set should cover enough state/form variation to catch generic packet logic");
        assertTrue(corpus.contains("buyer agent"), "must cover repeat buyer-agent users");
        assertTrue(corpus.contains("transaction coordinator"), "must cover TC workflow pressure");
        assertTrue(corpus.contains("team lead"), "must cover team/broker review pressure");
        assertTrue(corpus.contains("loan officer"), "must cover lender-adjacent review pressure");
        assertTrue(corpus.contains("listing"), "must cover seller-side pushback");
        assertTrue(corpus.contains("inspector"), "must cover inspector-channel false positives");
        assertTrue(corpus.contains("fha"), "must cover FHA financing risk");
        assertTrue(corpus.contains("va"), "must cover VA financing risk");
        assertTrue(corpus.contains("counter"), "must cover post-counter workflow");
        assertTrue(corpus.contains("expired"), "must cover expired-rights failure mode");
        assertTrue(corpus.contains("as is"), "must cover Florida AS IS pressure");
    }

    @Test
    void literalExpectedExclusionsStayOutOfTheLeadAsk() throws Exception {
        int checkedExclusions = 0;
        for (ValidationCase validationCase : loadCaseSet().cases()) {
            InspectionResponsePacket packet = buildPacket(validationCase);
            for (String expectedExclusion : validationCase.expectedExclusions()) {
                if (isConditionalExclusion(expectedExclusion)) {
                    continue;
                }
                String matchedFinding = validationCase.findings().stream()
                        .filter(finding -> overlaps(finding, expectedExclusion))
                        .findFirst()
                        .orElse("");
                if (matchedFinding.isBlank()) {
                    continue;
                }
                checkedExclusions++;
                assertFalse(containsText(packet.mustFixNow(), matchedFinding),
                        validationCase.id() + " should not lead with excluded item: " + matchedFinding);
                assertTrue(containsText(packet.doNotLead(), matchedFinding)
                                || containsText(packet.notWorthAsking(), matchedFinding)
                                || packet.excludedFindings().stream()
                                        .anyMatch(item -> containsText(item.findingLabel(), matchedFinding))
                                || containsText(packet.fullPacketText(), matchedFinding),
                        validationCase.id() + " should visibly demote excluded item: " + matchedFinding);
            }
        }
        assertTrue(checkedExclusions >= 15, "fixture should contain enough literal exclusions to police scope drift");
    }

    @Test
    void expiredTexasOptionCaseIsNotSendable() throws Exception {
        InspectionResponsePacket packet = buildPacket(caseById("C21"));

        assertEquals("Not sendable", packet.readinessLabel());
        assertGate(packet, "Deadline alive", "FAIL");
        assertGate(packet, "Stage and rights preserved", "FAIL");
        assertTrue(packet.workflowNote().contains("advisory framing"));
    }

    @Test
    void fhaAndVaCasesDoNotPretendLenderVisibleItemsAreFullyCleared() throws Exception {
        for (String caseId : List.of("C02", "C12", "C25", "C26")) {
            InspectionResponsePacket packet = buildPacket(caseById(caseId));

            assertFalse(packet.lenderVisibleSignals().isEmpty(), caseId + " should detect lender-visible signals");
            assertTrue(financingGate(packet).status().equals("WARN") || financingGate(packet).status().equals("FAIL"),
                    caseId + " should require lender or exhibit review");
            assertTrue(packet.defenseSignals().stream()
                            .anyMatch(signal -> signal.label().equals("Financing boundary")
                                    && (signal.status().equals("WARN") || signal.status().equals("FAIL"))),
                    caseId + " should keep financing boundary out of PASS");
            assertTrue(packet.lenderVisibleNote().contains("lender-visible")
                            || packet.lenderVisibleNote().contains("loan")
                            || packet.lenderVisibleNote().contains("underwriting"),
                    caseId + " should explain financing pressure");
        }
    }

    @Test
    void cleanMinorBuyerCaseDoesNotLeadWithWorkingAppliances() throws Exception {
        InspectionResponsePacket packet = buildPacket(caseById("C14"));

        assertFalse(containsText(packet.mustFixNow(), "old working appliances"));
        assertTrue(containsText(packet.doNotLead(), "old working appliances")
                        || containsText(packet.notWorthAsking(), "old working appliances")
                        || containsText(packet.fullPacketText(), "old working appliances"),
                "C14 should visibly keep working appliances out of the lead ask");
    }

    @Test
    void stateSpecificCasesKeepTheCorrectContractHandoffLanguage() throws Exception {
        InspectionResponsePacket colorado = buildPacket(caseById("C22"));
        InspectionResponsePacket texas = buildPacket(caseById("C03"));
        InspectionResponsePacket california = buildPacket(caseById("C04"));
        InspectionResponsePacket florida = buildPacket(caseById("C05"));

        assertTrue(colorado.workflowLabel().contains("Colorado"));
        assertTrue(colorado.workflowSteps().stream().anyMatch(step -> step.contains("Inspection Objection")));
        assertTrue(texas.workflowLabel().contains("Texas"));
        assertTrue(texas.workflowSteps().stream().anyMatch(step -> step.contains("option period")));
        assertTrue(california.workflowLabel().contains("California"));
        assertTrue(california.workflowSteps().stream().anyMatch(step -> step.contains("Request for Repair")));
        assertTrue(florida.workflowLabel().contains("Florida"));
        assertTrue(florida.workflowSteps().stream().anyMatch(step -> step.contains("AS IS")));
    }

    private ValidationCase caseById(String caseId) throws IOException {
        return loadCaseSet().cases().stream()
                .filter(validationCase -> validationCase.id().equals(caseId))
                .findFirst()
                .orElseThrow();
    }

    private ValidationCaseSet loadCaseSet() throws IOException {
        List<ValidationCase> cases = new ArrayList<>();
        LinkedHashSet<String> sources = new LinkedHashSet<>();
        for (String resourceName : VALIDATION_CASE_RESOURCES) {
            ValidationCaseSet caseSet = loadCaseSet(resourceName);
            cases.addAll(caseSet.cases());
            if (caseSet.meta() != null) {
                sources.addAll(caseSet.meta().sources());
            }
        }
        return new ValidationCaseSet(
                new Meta("2026-04-24",
                        "Merged internet-researched validation corpus across eleven 100-case files.",
                        "Synthetic cases only; not legal, lending, or customer-interview proof.",
                        List.copyOf(sources)),
                cases);
    }

    private ValidationCaseSet loadCaseSet(String resourceName) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        assertNotNull(stream, resourceName + " must be on the test classpath");
        try (stream) {
            return OBJECT_MAPPER.readValue(stream, ValidationCaseSet.class);
        }
    }

    private InspectionResponsePacket buildPacket(ValidationCase validationCase) {
        Verdict verdict = buildVerdict(validationCase);
        InspectionResponseInput input = buildInput(validationCase);
        return service.buildPacket(verdict, cityFor(validationCase.state()), input);
    }

    private Verdict buildVerdict(ValidationCase validationCase) {
        List<String> repairLikeLeadScope = validationCase.expectedLeadScope().stream()
                .filter(this::isRepairLike)
                .toList();

        List<RiskAdjustedItem> mustDo = new ArrayList<>();
        for (int i = 0; i < repairLikeLeadScope.size(); i++) {
            String label = repairLikeLeadScope.get(i);
            mustDo.add(RiskAdjustedItem.builder()
                    .itemCode(toItemCode(label))
                    .prettyName(label)
                    .category(categoryFor(label))
                    .adjustedCost(7000 + (i * 2500))
                    .mandatory(isMandatoryLike(label))
                    .riskFlags(isMandatoryLike(label) ? List.of("CRITICAL") : List.of())
                    .build());
        }

        SortedPlan plan = new SortedPlan();
        plan.setMustDo(mustDo);
        plan.setShouldDo(new ArrayList<>());
        plan.setSkipForNow(new ArrayList<>());

        Verdict verdict = new Verdict();
        verdict.setCostRange(CostRange.LOW_FIVE_FIGURES);
        verdict.setCostRangeLabel("Low five figures");
        verdict.setExactCostEstimate(Math.max(12000.0, 14000.0 + mustDo.stream()
                .mapToDouble(RiskAdjustedItem::getAdjustedCost)
                .sum()));
        verdict.setPlan(plan);
        if (!mustDo.isEmpty()) {
            verdict.setPrimaryCostDriver("Primary Cost Driver: " + mustDo.getFirst().getPrettyName());
        }
        return verdict;
    }

    private InspectionResponseInput buildInput(ValidationCase validationCase) {
        String contractWorkflow = switch (validationCase.state()) {
            case "CA" -> "CA_INVESTIGATION";
            case "TX" -> "TX_OPTION";
            case "FL" -> validationCase.deal().toLowerCase(Locale.ENGLISH).contains("as is")
                    ? "FL_AS_IS"
                    : "FL_STANDARD";
            case "CO" -> "CO_OBJECTION";
            default -> "AUTO";
        };

        String dealStage = validationCase.deal().toLowerCase(Locale.ENGLISH).contains("expired")
                ? "CONTINGENCY_REMOVED_OR_EXPIRED"
                : validationCase.deal().toLowerCase(Locale.ENGLISH).contains("counter")
                        ? "COUNTER_RECEIVED"
                        : "DRAFTING_FIRST_NOTICE";

        String closingWindow = validationCase.deal().toLowerCase(Locale.ENGLISH).contains("tomorrow")
                || validationCase.deal().toLowerCase(Locale.ENGLISH).contains("tonight")
                || validationCase.deal().toLowerCase(Locale.ENGLISH).contains("deadline")
                        ? "UNDER_7_DAYS"
                        : "SEVEN_TO_TWENTY_ONE_DAYS";

        List<InspectionEvidenceRef> evidenceRefs = validationCase.expectedLeadScope().stream()
                .filter(this::isRepairLike)
                .limit(2)
                .map(label -> new InspectionEvidenceRef(label, List.of("Report p.1: " + label)))
                .toList();

        return new InspectionResponseInput(
                List.of(),
                validationCase.findings(),
                evidenceRefs,
                evidenceRefs.isEmpty() ? "" : validationCase.id() + "-synthetic-report.pdf",
                validationCase.validationRationale().toLowerCase(Locale.ENGLISH).contains("quote") ? "NONE" : "HAS_ONE",
                closingWindow,
                contractWorkflow,
                dealStage,
                dealStage.equals("CONTINGENCY_REMOVED_OR_EXPIRED") ? "" : "2026-04-28T17:00",
                loanTypeFor(validationCase),
                validationCase.id() + " validation case",
                "100 Main St, " + cityFor(validationCase.state()) + ", " + validationCase.state() + " 00000",
                "Synthetic Buyer",
                "Synthetic Agent",
                cityFor(validationCase.state()),
                "typical mid-age housing stock",
                acquisitionEntryFor(validationCase));
    }

    private LoanType loanTypeFor(ValidationCase validationCase) {
        String text = (validationCase.persona() + " " + validationCase.deal() + " "
                + validationCase.validationRationale())
                .toLowerCase(Locale.ENGLISH);
        if (text.contains("va")) {
            return LoanType.VA;
        }
        if (text.contains("fha")) {
            return LoanType.FHA;
        }
        if (text.contains("cash")) {
            return LoanType.CASH;
        }
        if (text.contains("investor")) {
            return LoanType.INVESTOR;
        }
        return LoanType.CONVENTIONAL;
    }

    private String acquisitionEntryFor(ValidationCase validationCase) {
        return switch (validationCase.state()) {
            case "CO" -> "objection";
            case "CA", "TX" -> "repair_request";
            case "FL" -> "ask";
            default -> "direct";
        };
    }

    private boolean isRepairLike(String label) {
        String normalized = label.toLowerCase(Locale.ENGLISH);
        return Stream.of("roof", "hvac", "foundation", "electrical", "electric", "wiring", "sewer", "plumbing",
                "leak", "water", "paint", "handrail", "steps", "window", "septic", "safety", "crawlspace",
                "mold", "structural", "deck").anyMatch(normalized::contains);
    }

    private boolean isMandatoryLike(String label) {
        String normalized = label.toLowerCase(Locale.ENGLISH);
        return Stream.of("safety", "roof", "leak", "foundation", "electrical", "wiring", "sewer", "septic",
                "handrail", "structural", "mold").anyMatch(normalized::contains);
    }

    private String categoryFor(String label) {
        String normalized = label.toLowerCase(Locale.ENGLISH);
        if (normalized.contains("foundation") || normalized.contains("structural")) {
            return "STRUCTURAL";
        }
        if (normalized.contains("hvac")) {
            return "MECHANICAL";
        }
        if (normalized.contains("roof") || normalized.contains("sewer") || normalized.contains("plumbing")
                || normalized.contains("water") || normalized.contains("leak")) {
            return "STRUCTURAL";
        }
        if (normalized.contains("paint") || normalized.contains("window")) {
            return "SAFETY";
        }
        return "SAFETY";
    }

    private String toItemCode(String label) {
        return label.toUpperCase(Locale.ENGLISH).replaceAll("[^A-Z0-9]+", "_").replaceAll("^_|_$", "");
    }

    private String cityFor(String state) {
        return switch (state) {
            case "AZ" -> "Phoenix";
            case "CA" -> "Sacramento";
            case "CO" -> "Denver";
            case "FL" -> "Tampa";
            case "GA" -> "Atlanta";
            case "IL" -> "Chicago";
            case "MA" -> "Boston";
            case "MO" -> "Saint Louis";
            case "NC" -> "Raleigh";
            case "NJ" -> "Newark";
            case "OH" -> "Columbus";
            case "OR" -> "Portland";
            case "PA" -> "Philadelphia";
            case "TX" -> "Austin";
            case "VA" -> "Richmond";
            case "WA" -> "Seattle";
            default -> "Metro";
        };
    }

    private InspectionReadinessGate financingGate(InspectionResponsePacket packet) {
        return packet.readinessGates().stream()
                .filter(gate -> gate.label().equals("Financing risk cleared"))
                .findFirst()
                .orElseThrow();
    }

    private void assertGate(InspectionResponsePacket packet, String label, String status) {
        assertTrue(packet.readinessGates().stream()
                .anyMatch(gate -> gate.label().equals(label) && gate.status().equals(status)),
                label + " should be " + status);
    }

    private boolean containsText(List<String> values, String expectedText) {
        String expected = expectedText.toLowerCase(Locale.ENGLISH);
        return values.stream().anyMatch(value -> value.toLowerCase(Locale.ENGLISH).contains(expected));
    }

    private boolean containsText(String value, String expectedText) {
        return value.toLowerCase(Locale.ENGLISH).contains(expectedText.toLowerCase(Locale.ENGLISH));
    }

    private boolean overlaps(String left, String right) {
        String normalizedLeft = left.toLowerCase(Locale.ENGLISH);
        String normalizedRight = right.toLowerCase(Locale.ENGLISH);
        return normalizedLeft.contains(normalizedRight) || normalizedRight.contains(normalizedLeft);
    }

    private boolean isConditionalExclusion(String expectedExclusion) {
        String normalized = expectedExclusion.toLowerCase(Locale.ENGLISH);
        return normalized.contains("only if")
                || normalized.contains("unless")
                || normalized.contains("if ");
    }

    private void assertNotBlank(String value, String label) {
        assertNotNull(value, label);
        assertFalse(value.isBlank(), label);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ValidationCaseSet(Meta meta, List<ValidationCase> cases) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Meta(String created, String basis, String warning, List<String> sources) {
        private Meta {
            sources = sources == null ? List.of() : List.copyOf(sources);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ValidationCase(
            String id,
            String sourceBasis,
            String persona,
            String state,
            String deal,
            List<String> findings,
            List<String> expectedLeadScope,
            List<String> expectedExclusions,
            String requiredGate,
            String riskIfWrong,
            List<String> expectedPacketSignals,
            String expectedRiskLevel,
            String expectedBuyerMessage) {

        private ValidationCase {
            findings = findings == null ? List.of() : List.copyOf(findings);
            expectedLeadScope = expectedLeadScope == null ? List.of() : List.copyOf(expectedLeadScope);
            expectedExclusions = expectedExclusions == null ? List.of() : List.copyOf(expectedExclusions);
            expectedPacketSignals = expectedPacketSignals == null ? List.of() : List.copyOf(expectedPacketSignals);
        }

        private boolean hasValidationRationale() {
            return hasText(requiredGate) && hasText(riskIfWrong)
                    || !expectedPacketSignals.isEmpty() && hasText(expectedRiskLevel) && hasText(expectedBuyerMessage);
        }

        private String validationRationale() {
            return Stream.of(
                    requiredGate,
                    riskIfWrong,
                    String.join(" ", expectedPacketSignals),
                    expectedRiskLevel,
                    expectedBuyerMessage)
                    .filter(ValidationCase::hasText)
                    .collect(Collectors.joining(" "));
        }

        private static boolean hasText(String value) {
            return value != null && !value.isBlank();
        }
    }
}
