package com.livingcostcheck.home_repair.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowEvent;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowSummary;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionDefenseSignal;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionExclusionItem;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionReadinessGate;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponseInput;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponsePacket;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.ComparisonData;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.ContextBriefing;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.CostRange;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.LoanType;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InspectionResponseService {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern BULLET_PREFIX_PATTERN = Pattern
            .compile("^\\s*(?:[-*]+|\\d+[.)]|[A-Za-z][.)])\\s*");
    private static final Pattern NUMBERED_ITEM_PATTERN = Pattern.compile("(?<=\\S)\\s+(?=\\d+[.)]\\s+)");
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\r?\\n|[\\u2022;]+");
    private static final Pattern NON_ALNUM_PATTERN = Pattern.compile("[^a-z0-9]+");
    private static final Pattern STATE_CODE_PATTERN = Pattern.compile(",\\s*([A-Z]{2})(?:\\s+\\d{5}(?:-\\d{4})?)?\\s*$");
    private static final ObjectMapper MARKER_OBJECT_MAPPER = new ObjectMapper();

    private static final String INSPECTION_MARKER = "__INSPECTION:";
    private static final String EVIDENCE_MARKER = "__EVIDENCE:";
    private static final String EVIDENCE_SOURCE_MARKER = "__EVIDENCE_SOURCE:";
    private static final String QUOTE_SUPPORT_MARKER = "__QUOTE_SUPPORT:";
    private static final String CLOSING_WINDOW_MARKER = "__CLOSING_WINDOW:";
    private static final String CONTRACT_WORKFLOW_MARKER = "__CONTRACT_WORKFLOW:";
    private static final String DEAL_STAGE_MARKER = "__DEAL_STAGE:";
    private static final String RESPONSE_DEADLINE_MARKER = "__RESPONSE_DEADLINE:";
    private static final String LOAN_TYPE_MARKER = "__LOAN_TYPE:";
    private static final String CASE_LABEL_MARKER = "__CASE_LABEL:";
    private static final String PROPERTY_ADDRESS_MARKER = "__PROPERTY_ADDRESS:";
    private static final String CLIENT_NAME_MARKER = "__CLIENT_NAME:";
    private static final String AGENT_NAME_MARKER = "__AGENT_NAME:";
    private static final String MARKET_CONTEXT_MARKER = "__MARKET_CONTEXT:";
    private static final String ERA_CONTEXT_MARKER = "__ERA_CONTEXT:";
    private static final String ACQUISITION_ENTRY_MARKER = "__ENTRY:";

    private static final Set<String> MUST_REQUEST_TERMS = Set.of(
            "panel", "electric", "wiring", "aluminum", "fpe", "stab-lok", "zinsco",
            "roof leak", "active leak", "leak", "foundation", "struct", "mold",
            "asbestos", "polybutylene", "sewer", "crack", "water intrusion",
            "safety", "hazard", "fire", "carbon monoxide", "gas leak",
            "peeling paint", "chipping paint", "missing handrail", "handrail",
            "defective steps", "broken window", "failed septic");

    private static final Set<String> VERIFY_TERMS = Set.of(
            "roof", "hvac", "furnace", "air conditioner", "water heater", "plumbing",
            "window", "chimney", "drain", "grading", "gutter", "moisture",
            "insulation", "appliance", "crawlspace", "attic", "sump");

    private static final Set<String> DO_NOT_LEAD_TERMS = Set.of(
            "cosmetic", "paint", "cabinet", "flooring", "trim", "fixture",
            "door", "minor", "scuff", "caulk", "landscaping", "stain",
            "drawer", "handle", "light bulb", "decor", "dated", "old working",
            "old but working", "old but operational", "operational", "uneven old", "loose gutter", "gutter downspout",
            "loose downspout", "dirty gutter", "dirty gutters", "small drywall crack", "minor drywall crack",
            "backsplash", "countertop", "tile crack", "cracked tile", "cracked floor tile", "floor tile",
            "seller says", "never tripped",
            "fence panel", "fence panels", "aged", "dusty", "storage boxes", "stored boxes",
            "weeds", "vegetation", "fireplace screen", "window screen", "screen tear",
            "faded", "pool plaster", "furniture", "weathered", "plaster crack", "plaster cracks",
            "normal settlement", "driveway crack", "driveway cracks", "sidewalk crack", "patio crack",
            "hairline", "stucco crack", "stucco cracks", "dirty hvac filter", "hvac filter",
            "older but safe", "safe electrical panel", "minor settlement", "excluded in contract",
            "all new windows", "new windows", "old but operable", "old carpet", "garden hose", "hose leaks",
            "outlet cover", "outlet covers", "cover plate", "cover plates");

    private static final Set<String> HARD_DEFECT_OVERRIDES = Set.of(
            "foundation", "struct", "settlement", "major crack", "structural crack",
            "roof leak", "active leak", "water intrusion", "sewer", "polybutylene",
            "panel", "electrical", "wiring", "fpe", "zinsco", "stab-lok",
            "mold", "asbestos", "safety", "hazard", "fire", "gas leak",
            "peeling paint", "chipping paint", "handrail", "defective steps",
            "broken window", "failed septic");

    private static final Set<String> MATCH_STOPWORDS = Set.of(
            "active", "issue", "issues", "item", "items", "repair", "repairs", "replacement",
            "replace", "system", "systems", "needs", "need", "recommend", "recommended",
            "inspector", "inspection", "report", "noted", "noting", "observed", "further",
            "review", "licensed", "contractor", "minor", "major", "above", "below",
            "front", "rear", "left", "right", "side", "home", "house", "seller",
            "buyer", "credit", "request", "garage", "bedroom", "bathroom", "hallway");

    private static final Map<String, String> STATE_NAME_TO_CODE = Map.ofEntries(
            Map.entry("alabama", "AL"), Map.entry("alaska", "AK"), Map.entry("arizona", "AZ"),
            Map.entry("arkansas", "AR"), Map.entry("california", "CA"), Map.entry("colorado", "CO"),
            Map.entry("connecticut", "CT"), Map.entry("delaware", "DE"), Map.entry("florida", "FL"),
            Map.entry("georgia", "GA"), Map.entry("hawaii", "HI"), Map.entry("idaho", "ID"),
            Map.entry("illinois", "IL"), Map.entry("indiana", "IN"), Map.entry("iowa", "IA"),
            Map.entry("kansas", "KS"), Map.entry("kentucky", "KY"), Map.entry("louisiana", "LA"),
            Map.entry("maine", "ME"), Map.entry("maryland", "MD"), Map.entry("massachusetts", "MA"),
            Map.entry("michigan", "MI"), Map.entry("minnesota", "MN"), Map.entry("mississippi", "MS"),
            Map.entry("missouri", "MO"), Map.entry("montana", "MT"), Map.entry("nebraska", "NE"),
            Map.entry("nevada", "NV"), Map.entry("new hampshire", "NH"), Map.entry("new jersey", "NJ"),
            Map.entry("new mexico", "NM"), Map.entry("new york", "NY"), Map.entry("north carolina", "NC"),
            Map.entry("north dakota", "ND"), Map.entry("ohio", "OH"), Map.entry("oklahoma", "OK"),
            Map.entry("oregon", "OR"), Map.entry("pennsylvania", "PA"), Map.entry("rhode island", "RI"),
            Map.entry("south carolina", "SC"), Map.entry("south dakota", "SD"), Map.entry("tennessee", "TN"),
            Map.entry("texas", "TX"), Map.entry("utah", "UT"), Map.entry("vermont", "VT"),
            Map.entry("virginia", "VA"), Map.entry("washington", "WA"), Map.entry("west virginia", "WV"),
            Map.entry("wisconsin", "WI"), Map.entry("wyoming", "WY"), Map.entry("district of columbia", "DC"));

    private static final Map<String, Set<String>> COMPONENT_SIGNALS = Map.ofEntries(
            Map.entry("roof", Set.of("roof", "roofing", "shingle", "gutter", "flashing")),
            Map.entry("electrical", Set.of("electric", "electrical", "panel", "wiring", "fpe", "zinsco",
                    "stab-lok", "aluminum")),
            Map.entry("plumbing", Set.of("plumb", "plumbing", "pipe", "polybutylene", "sewer", "drain",
                    "water heater")),
            Map.entry("hvac", Set.of("hvac", "furnace", "heat pump", "air conditioner", "condenser", "duct")),
            Map.entry("foundation", Set.of("foundation", "struct", "settlement", "beam", "joist", "crack")),
            Map.entry("water", Set.of("leak", "moisture", "mold", "water intrusion", "crawlspace", "attic",
                    "chimney", "grading", "sump")),
            Map.entry("windows", Set.of("window", "windows", "door", "doors")),
            Map.entry("interior", Set.of("paint", "floor", "flooring", "cabinet", "trim", "caulk", "stain"))
    );

    private static final List<LenderVisibleRule> LENDER_VISIBLE_RULES = List.of(
            new LenderVisibleRule("Active roof leak or water intrusion",
                    Set.of("roof leak", "active leak", "water intrusion", "moisture intrusion", "leak", "mold"),
                    Set.of("roof", "water")),
            new LenderVisibleRule("Unsafe electrical panel or wiring",
                    Set.of("fpe", "zinsco", "stab-lok", "aluminum wiring", "unsafe panel", "panel"),
                    Set.of("electrical")),
            new LenderVisibleRule("Foundation movement or structural cracking",
                    Set.of("foundation", "structural", "settlement", "major crack", "crack"),
                    Set.of("foundation")),
            new LenderVisibleRule("Sewer, drain, or whole-house plumbing failure",
                    Set.of("sewer", "backup", "drain", "polybutylene", "repipe", "pipe failure"),
                    Set.of("plumbing")),
            new LenderVisibleRule("HVAC failure affecting habitability",
                    Set.of("no heat", "no cooling", "failed during cooling", "failed during heating", "hvac failure"),
                    Set.of("hvac")),
            new LenderVisibleRule("Hazardous material or environmental exposure",
                    Set.of("mold", "asbestos", "gas leak", "carbon monoxide", "hazard", "chinese drywall"),
                    Set.of("water", "interior")),
            new LenderVisibleRule("Unsafe window, door, or egress condition",
                    Set.of("broken window", "failed window", "egress", "unsafe door"),
                    Set.of("windows")),
            new LenderVisibleRule("FHA/VA safety or MPR-sensitive repair",
                    Set.of("peeling paint", "chipping paint", "missing handrail", "handrail",
                            "defective steps", "failed septic", "septic"),
                    Set.of("safety", "plumbing")));

    private static final DateTimeFormatter DEADLINE_INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DEADLINE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter EVENT_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH);

    public List<String> extractFindings(String rawReportText, List<String> manualFindings) {
        LinkedHashSet<String> findings = new LinkedHashSet<>();

        if (manualFindings != null) {
            manualFindings.stream()
                    .map(this::cleanFindingLabel)
                    .filter(s -> !s.isBlank())
                    .forEach(findings::add);
        }

        if (rawReportText != null && !rawReportText.isBlank()) {
            String prepared = NUMBERED_ITEM_PATTERN.matcher(rawReportText.replace('\u2022', '\n')).replaceAll("\n");
            for (String candidate : SPLIT_PATTERN.split(prepared)) {
                String cleaned = cleanFindingLabel(candidate);
                if (cleaned.isBlank()) {
                    continue;
                }
                if (cleaned.length() > 220) {
                    cleaned = cleaned.substring(0, 217).trim() + "...";
                }
                findings.add(cleaned);
            }
        }

        return findings.stream().limit(12).toList();
    }

    public String buildStoredContext(List<String> history,
            List<String> inspectionFindings,
            List<InspectionEvidenceRef> evidenceRefs,
            String evidenceSourceLabel,
            String quoteSupport,
            String closingWindow,
            String contractWorkflow,
            String dealStage,
            String responseDeadlineAt,
            LoanType loanType,
            String caseLabel,
            String propertyAddress,
            String clientName,
            String agentName,
            String marketContextLabel,
            String eraContextLabel,
            String acquisitionEntry) {
        List<String> values = new ArrayList<>();
        if (history != null) {
            history.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .forEach(values::add);
        }
        if (inspectionFindings != null) {
            inspectionFindings.stream()
                    .map(this::cleanFindingLabel)
                    .filter(s -> !s.isBlank())
                    .limit(12)
                    .map(this::encodeMarkerValue)
                    .map(encoded -> INSPECTION_MARKER + encoded)
                    .forEach(values::add);
        }
        if (evidenceRefs != null) {
            evidenceRefs.stream()
                    .filter(Objects::nonNull)
                    .map(this::serializeEvidenceMarker)
                    .filter(s -> !s.isBlank())
                    .map(this::encodeMarkerValue)
                    .map(encoded -> EVIDENCE_MARKER + encoded)
                    .forEach(values::add);
        }
        if (evidenceSourceLabel != null && !evidenceSourceLabel.isBlank()) {
            values.add(EVIDENCE_SOURCE_MARKER + encodeMarkerValue(evidenceSourceLabel));
        }
        if (quoteSupport != null && !quoteSupport.isBlank()) {
            values.add(QUOTE_SUPPORT_MARKER + encodeMarkerValue(quoteSupport));
        }
        if (closingWindow != null && !closingWindow.isBlank()) {
            values.add(CLOSING_WINDOW_MARKER + encodeMarkerValue(closingWindow));
        }
        if (contractWorkflow != null && !contractWorkflow.isBlank()) {
            values.add(CONTRACT_WORKFLOW_MARKER + encodeMarkerValue(contractWorkflow));
        }
        if (dealStage != null && !dealStage.isBlank()) {
            values.add(DEAL_STAGE_MARKER + encodeMarkerValue(dealStage));
        }
        if (responseDeadlineAt != null && !responseDeadlineAt.isBlank()) {
            values.add(RESPONSE_DEADLINE_MARKER + encodeMarkerValue(responseDeadlineAt));
        }
        if (loanType != null) {
            values.add(LOAN_TYPE_MARKER + encodeMarkerValue(loanType.name()));
        }
        if (caseLabel != null && !caseLabel.isBlank()) {
            values.add(CASE_LABEL_MARKER + encodeMarkerValue(caseLabel));
        }
        if (propertyAddress != null && !propertyAddress.isBlank()) {
            values.add(PROPERTY_ADDRESS_MARKER + encodeMarkerValue(propertyAddress));
        }
        if (clientName != null && !clientName.isBlank()) {
            values.add(CLIENT_NAME_MARKER + encodeMarkerValue(clientName));
        }
        if (agentName != null && !agentName.isBlank()) {
            values.add(AGENT_NAME_MARKER + encodeMarkerValue(agentName));
        }
        if (marketContextLabel != null && !marketContextLabel.isBlank()) {
            values.add(MARKET_CONTEXT_MARKER + encodeMarkerValue(marketContextLabel));
        }
        if (eraContextLabel != null && !eraContextLabel.isBlank()) {
            values.add(ERA_CONTEXT_MARKER + encodeMarkerValue(eraContextLabel));
        }
        if (acquisitionEntry != null && !acquisitionEntry.isBlank()) {
            values.add(ACQUISITION_ENTRY_MARKER + encodeMarkerValue(acquisitionEntry));
        }
        return String.join(",", values);
    }

    public InspectionResponseInput parseStoredContext(String storedRepairHistory) {
        if (storedRepairHistory == null || storedRepairHistory.isBlank()) {
            return new InspectionResponseInput(List.of(), List.of(), List.of(), "", "NONE", "FLEXIBLE", "AUTO", "DRAFTING_FIRST_NOTICE", "",
                    LoanType.CONVENTIONAL, "", "", "", "", "", "", "direct");
        }

        List<String> historyItems = new ArrayList<>();
        List<String> inspectionFindings = new ArrayList<>();
        List<InspectionEvidenceRef> evidenceRefs = new ArrayList<>();
        String evidenceSourceLabel = "";
        String quoteSupport = "NONE";
        String closingWindow = "FLEXIBLE";
        String contractWorkflow = "AUTO";
        String dealStage = "DRAFTING_FIRST_NOTICE";
        String responseDeadlineAt = "";
        LoanType loanType = LoanType.CONVENTIONAL;
        String caseLabel = "";
        String propertyAddress = "";
        String clientName = "";
        String agentName = "";
        String marketContextLabel = "";
        String eraContextLabel = "";
        String acquisitionEntry = "direct";

        for (String rawEntry : storedRepairHistory.split(",")) {
            String entry = rawEntry.trim();
            if (entry.isBlank()) {
                continue;
            }
            if (entry.startsWith(INSPECTION_MARKER)) {
                inspectionFindings.add(decodeMarkerValue(entry.substring(INSPECTION_MARKER.length())));
                continue;
            }
            if (entry.startsWith(EVIDENCE_MARKER)) {
                InspectionEvidenceRef evidenceRef = deserializeEvidenceMarker(
                        decodeMarkerValue(entry.substring(EVIDENCE_MARKER.length())));
                if (evidenceRef != null && !evidenceRef.findingLabel().isBlank() && !evidenceRef.citations().isEmpty()) {
                    evidenceRefs.add(evidenceRef);
                }
                continue;
            }
            if (entry.startsWith(EVIDENCE_SOURCE_MARKER)) {
                evidenceSourceLabel = decodeMarkerValue(entry.substring(EVIDENCE_SOURCE_MARKER.length()));
                continue;
            }
            if (entry.startsWith(QUOTE_SUPPORT_MARKER)) {
                quoteSupport = decodeMarkerValue(entry.substring(QUOTE_SUPPORT_MARKER.length()));
                continue;
            }
            if (entry.startsWith(CLOSING_WINDOW_MARKER)) {
                closingWindow = decodeMarkerValue(entry.substring(CLOSING_WINDOW_MARKER.length()));
                continue;
            }
            if (entry.startsWith(CONTRACT_WORKFLOW_MARKER)) {
                contractWorkflow = decodeMarkerValue(entry.substring(CONTRACT_WORKFLOW_MARKER.length()));
                continue;
            }
            if (entry.startsWith(DEAL_STAGE_MARKER)) {
                dealStage = decodeMarkerValue(entry.substring(DEAL_STAGE_MARKER.length()));
                continue;
            }
            if (entry.startsWith(RESPONSE_DEADLINE_MARKER)) {
                responseDeadlineAt = decodeMarkerValue(entry.substring(RESPONSE_DEADLINE_MARKER.length()));
                continue;
            }
            if (entry.startsWith(LOAN_TYPE_MARKER)) {
                try {
                    loanType = LoanType.valueOf(decodeMarkerValue(entry.substring(LOAN_TYPE_MARKER.length())));
                } catch (IllegalArgumentException ignored) {
                    loanType = LoanType.CONVENTIONAL;
                }
                continue;
            }
            if (entry.startsWith(CASE_LABEL_MARKER)) {
                caseLabel = decodeMarkerValue(entry.substring(CASE_LABEL_MARKER.length()));
                continue;
            }
            if (entry.startsWith(PROPERTY_ADDRESS_MARKER)) {
                propertyAddress = decodeMarkerValue(entry.substring(PROPERTY_ADDRESS_MARKER.length()));
                continue;
            }
            if (entry.startsWith(CLIENT_NAME_MARKER)) {
                clientName = decodeMarkerValue(entry.substring(CLIENT_NAME_MARKER.length()));
                continue;
            }
            if (entry.startsWith(AGENT_NAME_MARKER)) {
                agentName = decodeMarkerValue(entry.substring(AGENT_NAME_MARKER.length()));
                continue;
            }
            if (entry.startsWith(MARKET_CONTEXT_MARKER)) {
                marketContextLabel = decodeMarkerValue(entry.substring(MARKET_CONTEXT_MARKER.length()));
                continue;
            }
            if (entry.startsWith(ERA_CONTEXT_MARKER)) {
                eraContextLabel = decodeMarkerValue(entry.substring(ERA_CONTEXT_MARKER.length()));
                continue;
            }
            if (entry.startsWith(ACQUISITION_ENTRY_MARKER)) {
                acquisitionEntry = decodeMarkerValue(entry.substring(ACQUISITION_ENTRY_MARKER.length()));
                continue;
            }
            historyItems.add(entry);
        }

        return new InspectionResponseInput(historyItems, inspectionFindings, evidenceRefs, evidenceSourceLabel,
                quoteSupport, closingWindow, contractWorkflow, dealStage, responseDeadlineAt, loanType, caseLabel, propertyAddress, clientName, agentName,
                marketContextLabel, eraContextLabel, acquisitionEntry);
    }

    public InspectionResponsePacket buildPacket(Verdict verdict, String city, InspectionResponseInput input) {
        List<String> sourceFindings = new ArrayList<>(input.inspectionFindings());
        if (sourceFindings.isEmpty()) {
            sourceFindings.addAll(fallbackFindings(verdict));
        }

        List<NegotiationAnchor> leadAnchors = buildLeadAnchors(verdict, sourceFindings);
        List<NegotiationAnchor> verifyAnchors = buildVerifyAnchors(verdict, sourceFindings, leadAnchors);

        LinkedHashSet<String> mustFixNow = new LinkedHashSet<>();
        LinkedHashSet<String> verifyNext = new LinkedHashSet<>();
        LinkedHashSet<String> doNotLead = new LinkedHashSet<>();

        classifyFindings(sourceFindings, leadAnchors, verifyAnchors, mustFixNow, verifyNext, doNotLead);

        if (mustFixNow.isEmpty()) {
            leadAnchors.stream().map(NegotiationAnchor::label).limit(2).forEach(mustFixNow::add);
        }
        if (verifyNext.isEmpty()) {
            verifyAnchors.stream().map(NegotiationAnchor::label).limit(2).forEach(verifyNext::add);
        }
        if (verifyNext.isEmpty()) {
            verifyNext.add("one contractor quote only if the seller challenges scope or cost");
        }
        if (doNotLead.isEmpty()) {
            doNotLead.add("Cosmetic finishes unless they are tied to safety, water, or financing");
        }

        List<String> notWorthAsking = new ArrayList<>(doNotLead);
        notWorthAsking.add("Preference upgrades that were visible before the offer");
        notWorthAsking.add("Small maintenance items that weaken the main safety or system request");
        List<InspectionExclusionItem> excludedFindings = buildExclusionItems(doNotLead);

        NegotiationCostProfile costProfile = buildCostProfile(verdict, leadAnchors, verifyAnchors);
        List<InspectionEvidenceRef> matchedEvidence = matchEvidenceToLeadItems(input.evidenceRefs(), mustFixNow,
                leadAnchors);
        List<String> lenderVisibleSignals = detectLenderVisibleSignals(sourceFindings, leadAnchors);
        String lenderVisibleNote = buildLenderVisibleNote(input.loanType(), lenderVisibleSignals);
        AskRange askRange = buildAskRange(costProfile, input, lenderVisibleSignals, matchedEvidence);
        PacketWorkflow workflow = buildWorkflow(input, askRange, lenderVisibleSignals);
        List<InspectionReadinessGate> readinessGates = buildReadinessGates(input, mustFixNow, matchedEvidence,
                lenderVisibleSignals, workflow);
        int readinessPassCount = countReadinessGates(readinessGates, "PASS");
        int readinessWarnCount = countReadinessGates(readinessGates, "WARN");
        int readinessFailCount = countReadinessGates(readinessGates, "FAIL");
        PacketQuality quality = assessPacketQuality(input, sourceFindings, mustFixNow, verifyNext, doNotLead,
                costProfile, lenderVisibleSignals, matchedEvidence, readinessGates);

        String quoteSupportLabel = quoteSupportLabel(input.quoteSupport());
        String loanTypeLabel = loanTypeLabel(input.loanType());
        String closingWindowLabel = closingWindowLabel(input.closingWindow());
        String loanTypeNote = loanTypeNote(input.loanType());
        String responseDeadlineNote = responseDeadlineNote(input.closingWindow(), input.responseDeadlineAt());
        String evidenceNote = evidenceNote(input.quoteSupport());

        String focusSummary = joinForSentence(mustFixNow.stream().limit(2).toList());
        String verifySummary = joinForSentence(verifyNext.stream().limit(2).toList());
        String lenderVisibleSummary = lenderVisibleSignals.isEmpty()
                ? ""
                : " The strongest lender-visible items are "
                        + joinForSentence(lenderVisibleSignals.stream().limit(2).toList()) + ".";

        String numberBasisPhrase = numberBasisPhrase(input, matchedEvidence);
        String sellerCreditSummary = String.format(
                "Review a $%s seller-credit ask for %s. %s The number is sized from about $%s of scoped repair exposure, led by %s. Keep %s as verification and leave cosmetic noise out.%s",
                formatMoney(askRange.targetAsk()),
                city,
                numberBasisPhrase,
                formatMoney(costProfile.scopedExposure()),
                focusSummary,
                verifySummary,
                lenderVisibleSignals.isEmpty() ? "" : " Lender-visible items stay in the lead scope.");

        String agentNegotiationScript = String.format(
                "We are reviewing a seller-credit ask of $%s before closing. This is not a contractor bid and it is not based on the full repair wishlist; it is based on about $%s of scoped lead-item exposure anchored by %s. Because this deal is using %s, we are focusing on defects that affect safety, financing, water intrusion, or near-term system risk in %s.%s Secondary items to verify are %s. If the seller challenges the number, we can defend $%s and hold $%s as the opening target only after the red and yellow review gates are cleared.",
                formatMoney(askRange.targetAsk()),
                formatMoney(costProfile.scopedExposure()),
                focusSummary,
                loanTypeLabel,
                city,
                lenderVisibleSummary,
                verifySummary,
                formatMoney(askRange.defendableAsk()),
                formatMoney(askRange.targetAsk()));

        String fallbackScript = String.format(
                "If the seller rejects the full request, fall back to $%s tied only to %s. That keeps the response anchored to the strongest must-fix items instead of trading away leverage for cosmetic fixes or a vague promise to repair after closing.",
                formatMoney(askRange.defendableAsk()),
                focusSummary);

        List<String> evidenceChecklist = evidenceChecklist(input, costProfile, lenderVisibleSignals);
        List<String> nextActions = nextActions(input, askRange, costProfile, lenderVisibleSignals, matchedEvidence,
                workflow, readinessGates);
        List<InspectionDefenseSignal> defenseSignals = buildDefenseSignals(input, costProfile, matchedEvidence,
                lenderVisibleSignals, workflow, readinessGates, askRange);
        List<String> missingEvidence = buildMissingEvidence(mustFixNow, matchedEvidence, input, lenderVisibleSignals);
        List<String> reviewCaveats = buildReviewCaveats(input, lenderVisibleSignals, matchedEvidence, workflow);
        List<String> verdictRationale = buildVerdictRationale(quality, readinessGates, defenseSignals, askRange,
                costProfile);
        String fullPacketText = buildFullPacketText(city, input, mustFixNow, verifyNext, doNotLead,
                evidenceChecklist, costProfile.pricingBreakdown(), excludedFindings, defenseSignals,
                verdictRationale, missingEvidence, reviewCaveats,
                sellerCreditSummary, agentNegotiationScript, fallbackScript, workflow, askRange);

        return new InspectionResponsePacket(
                defenseTitle(quality),
                defenseSubtitle(quality, askRange, input),
                sourceFindings,
                new ArrayList<>(mustFixNow),
                new ArrayList<>(verifyNext),
                new ArrayList<>(doNotLead),
                matchedEvidence,
                input.evidenceSourceLabel(),
                evidenceChecklist,
                notWorthAsking,
                excludedFindings,
                quoteSupportLabel,
                closingWindowLabel,
                loanTypeLabel,
                loanTypeNote,
                responseDeadlineNote,
                evidenceNote,
                costProfile.dataAnchor(),
                costProfile.dataAnchorNote(),
                costProfile.pricingBreakdown(),
                lenderVisibleSignals,
                lenderVisibleNote,
                quality.readinessLabel(),
                quality.readinessNote(),
                readinessGates,
                defenseSignals,
                verdictRationale,
                missingEvidence,
                reviewCaveats,
                readinessPassCount,
                readinessWarnCount,
                readinessFailCount,
                quality.confidenceScore(),
                quality.confidenceReasons(),
                workflow.label(),
                workflow.title(),
                workflow.note(),
                workflow.steps(),
                nextActions,
                askRange.defendableAsk(),
                askRange.targetAsk(),
                askRange.stretchAsk(),
                formatMoney(askRange.defendableAsk()),
                formatMoney(askRange.targetAsk()),
                formatMoney(askRange.stretchAsk()),
                sellerCreditSummary,
                agentNegotiationScript,
                fallbackScript,
                fullPacketText);
    }

    private void classifyFindings(List<String> sourceFindings,
            List<NegotiationAnchor> leadAnchors,
            List<NegotiationAnchor> verifyAnchors,
            LinkedHashSet<String> mustFixNow,
            LinkedHashSet<String> verifyNext,
            LinkedHashSet<String> doNotLead) {
        for (String finding : sourceFindings) {
            String cleanFinding = cleanFindingLabel(finding);
            if (cleanFinding.isBlank()) {
                continue;
            }

            FindingBucket baseBucket = classifyByTerms(cleanFinding.toLowerCase(Locale.ENGLISH));
            NegotiationAnchor leadMatch = findBestMatchingAnchor(cleanFinding, leadAnchors);
            NegotiationAnchor verifyMatch = findBestMatchingAnchor(cleanFinding, verifyAnchors);

            if (baseBucket == FindingBucket.DO_NOT_LEAD) {
                doNotLead.add(cleanFinding);
                continue;
            }
            if (leadMatch != null && (leadMatch.hardLead() || leadMatch.coreLead())) {
                mustFixNow.add(cleanFinding);
                continue;
            }
            if (baseBucket == FindingBucket.MUST_REQUEST) {
                mustFixNow.add(cleanFinding);
                continue;
            }
            if (baseBucket == FindingBucket.DO_NOT_LEAD && leadMatch == null && verifyMatch == null) {
                doNotLead.add(cleanFinding);
                continue;
            }
            if (leadMatch != null) {
                mustFixNow.add(cleanFinding);
                continue;
            }
            if (verifyMatch != null || baseBucket == FindingBucket.VERIFY) {
                verifyNext.add(cleanFinding);
                continue;
            }
            verifyNext.add(cleanFinding);
        }
    }

    private List<NegotiationAnchor> buildLeadAnchors(Verdict verdict, List<String> sourceFindings) {
        List<NegotiationAnchor> anchors = planMustDoItems(verdict).stream()
                .map(item -> toAnchor(item, sourceFindings))
                .filter(anchor -> anchor.hardLead() || anchor.coreLead() || anchor.matchedFinding())
                .sorted(Comparator.comparingDouble(NegotiationAnchor::cost).reversed())
                .limit(4)
                .collect(Collectors.toCollection(ArrayList::new));

        if (anchors.isEmpty() && verdict.getPrimaryCostDriver() != null) {
            String label = cleanFindingLabel(verdict.getPrimaryCostDriver());
            double fallbackCost = Math.max(estimateRepairExposure(verdict) * 0.55, 4000.0);
            Set<String> tags = extractComponentSignals(label);
            Set<String> tokens = extractMeaningfulTokens(label);
            anchors.add(new NegotiationAnchor(label, fallbackCost, true, true, false, tags, tokens));
        }

        return anchors;
    }

    private List<NegotiationAnchor> buildVerifyAnchors(Verdict verdict,
            List<String> sourceFindings,
            List<NegotiationAnchor> leadAnchors) {
        Set<String> leadLabels = leadAnchors.stream()
                .map(NegotiationAnchor::label)
                .collect(Collectors.toSet());

        List<RiskAdjustedItem> candidates = new ArrayList<>();
        candidates.addAll(planMustDoItems(verdict));
        candidates.addAll(planShouldDoItems(verdict));

        return candidates.stream()
                .filter(Objects::nonNull)
                .filter(item -> !leadLabels.contains(cleanFindingLabel(item.getPrettyName())))
                .map(item -> toAnchor(item, sourceFindings))
                .filter(anchor -> anchor.matchedFinding() || anchor.coreLead() || anchor.cost() >= 4000.0)
                .sorted(Comparator.comparingDouble(NegotiationAnchor::cost).reversed())
                .limit(3)
                .toList();
    }

    private NegotiationAnchor toAnchor(RiskAdjustedItem item, List<String> sourceFindings) {
        String label = cleanFindingLabel(item.getPrettyName());
        boolean hardLead = isHardLeadItem(item);
        boolean coreLead = hardLead || isCoreLeadItem(item);
        boolean matchedFinding = sourceFindings.stream().anyMatch(finding -> itemMatchesFinding(item, finding));
        return new NegotiationAnchor(
                label,
                item.getAdjustedCost(),
                hardLead,
                coreLead,
                matchedFinding,
                extractComponentSignals(label + " " + item.getItemCode()),
                extractMeaningfulTokens(label + " " + item.getItemCode()));
    }

    private boolean isHardLeadItem(RiskAdjustedItem item) {
        if (item == null) {
            return false;
        }
        if (item.isMandatory()) {
            return true;
        }
        if (Boolean.TRUE.equals(item.getIsCodeMandated()) || Boolean.TRUE.equals(item.getIsForensicConfirmed())) {
            return true;
        }
        return item.getRiskFlags() != null && item.getRiskFlags().stream().anyMatch(flag -> {
            String normalized = flag.toUpperCase(Locale.ENGLISH);
            return normalized.contains("CRITICAL")
                    || normalized.contains("MANDATORY")
                    || normalized.contains("FORENSIC")
                    || normalized.contains("HAZMAT");
        });
    }

    private boolean isCoreLeadItem(RiskAdjustedItem item) {
        if (item == null) {
            return false;
        }
        if (item.getCategory() == null) {
            return item.getAdjustedCost() >= 7000.0;
        }
        return switch (item.getCategory()) {
            case "SAFETY", "STRUCTURAL", "MECHANICAL" -> true;
            default -> item.getAdjustedCost() >= 7000.0;
        };
    }

    private boolean itemMatchesFinding(RiskAdjustedItem item, String finding) {
        if (item == null || finding == null || finding.isBlank()) {
            return false;
        }
        Set<String> findingTags = extractComponentSignals(finding);
        Set<String> findingTokens = extractMeaningfulTokens(finding);
        Set<String> itemTags = extractComponentSignals(item.getPrettyName() + " " + item.getItemCode());
        Set<String> itemTokens = extractMeaningfulTokens(item.getPrettyName() + " " + item.getItemCode());
        return scoreMatch(findingTags, findingTokens, itemTags, itemTokens) > 0;
    }

    private List<InspectionEvidenceRef> matchEvidenceToLeadItems(List<InspectionEvidenceRef> evidenceRefs,
            LinkedHashSet<String> mustFixNow,
            List<NegotiationAnchor> leadAnchors) {
        if (evidenceRefs == null || evidenceRefs.isEmpty()) {
            return List.of();
        }

        List<String> targets = new ArrayList<>(mustFixNow);
        if (targets.isEmpty()) {
            leadAnchors.stream().map(NegotiationAnchor::label).forEach(targets::add);
        }

        LinkedHashSet<InspectionEvidenceRef> matches = new LinkedHashSet<>();
        for (String target : targets.stream().limit(4).toList()) {
            InspectionEvidenceRef bestMatch = findBestEvidenceMatch(target, evidenceRefs);
            if (bestMatch != null) {
                matches.add(bestMatch);
            }
        }
        return List.copyOf(matches);
    }

    private InspectionEvidenceRef findBestEvidenceMatch(String target, List<InspectionEvidenceRef> evidenceRefs) {
        if (target == null || target.isBlank()) {
            return null;
        }

        Set<String> targetTags = extractComponentSignals(target);
        Set<String> targetTokens = extractMeaningfulTokens(target);

        InspectionEvidenceRef bestMatch = null;
        int bestScore = 0;
        for (InspectionEvidenceRef evidenceRef : evidenceRefs) {
            int score = scoreMatch(targetTags, targetTokens,
                    extractComponentSignals(evidenceRef.findingLabel()),
                    extractMeaningfulTokens(evidenceRef.findingLabel()));
            if (score > bestScore) {
                bestScore = score;
                bestMatch = evidenceRef;
            }
        }
        return bestScore > 0 ? bestMatch : null;
    }

    private NegotiationAnchor findBestMatchingAnchor(String finding, List<NegotiationAnchor> anchors) {
        if (finding == null || finding.isBlank() || anchors.isEmpty()) {
            return null;
        }

        Set<String> findingTags = extractComponentSignals(finding);
        Set<String> findingTokens = extractMeaningfulTokens(finding);

        NegotiationAnchor bestAnchor = null;
        int bestScore = 0;
        for (NegotiationAnchor anchor : anchors) {
            int score = scoreMatch(findingTags, findingTokens, anchor.tags(), anchor.tokens());
            if (score > bestScore) {
                bestScore = score;
                bestAnchor = anchor;
            }
        }
        return bestScore > 0 ? bestAnchor : null;
    }

    private int scoreMatch(Set<String> leftTags, Set<String> leftTokens, Set<String> rightTags, Set<String> rightTokens) {
        int score = 0;
        for (String tag : leftTags) {
            if (rightTags.contains(tag)) {
                score += 3;
            }
        }
        for (String token : leftTokens) {
            if (rightTokens.contains(token)) {
                score += 1;
            }
        }
        return score;
    }

    private FindingBucket classifyByTerms(String normalizedFinding) {
        boolean cosmeticOrWeak = containsAny(normalizedFinding, DO_NOT_LEAD_TERMS);
        boolean hardDefect = containsAny(normalizedFinding, MUST_REQUEST_TERMS);
        if (cosmeticOrWeak && (normalizedFinding.contains("seller says") || normalizedFinding.contains("never tripped"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (containsAny(normalizedFinding, Set.of("no active leak", "no leak observed", "not leaking",
                "without leak", "no backup observed", "no failure observed"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (containsAny(normalizedFinding, Set.of("older but safe", "safe electrical panel"))
                && !containsAny(normalizedFinding, Set.of("fpe", "zinsco", "stab-lok", "double tap",
                        "double-tapped", "open splice", "overheat", "scorch", "hazard"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (normalizedFinding.contains("fireplace")
                && !containsAny(normalizedFinding, Set.of("chimney", "carbon monoxide", "gas leak", "unsafe",
                        "hazard", "cracked flue", "blocked flue", "active leak"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (normalizedFinding.contains("hose")
                && !containsAny(normalizedFinding, Set.of("supply line", "plumbing", "main", "water service"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (containsAny(normalizedFinding, Set.of("outlet cover", "outlet covers", "cover plate", "cover plates"))
                && !containsAny(normalizedFinding, Set.of("open splice", "exposed wiring", "missing cover at panel",
                        "junction box"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (containsAny(normalizedFinding, Set.of("all new windows", "buyer asks all new windows",
                "other windows old but operable"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && normalizedFinding.contains("normal settlement")) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if ((normalizedFinding.contains("driveway") || normalizedFinding.contains("sidewalk")
                || normalizedFinding.contains("patio"))
                && (normalizedFinding.contains("crack") || normalizedFinding.contains("cracked"))
                && !containsAny(normalizedFinding, Set.of("foundation", "struct", "settlement", "trip hazard",
                        "safety", "water intrusion", "active leak"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && (normalizedFinding.contains("fireplace screen")
                || normalizedFinding.contains("window screen") || normalizedFinding.contains("screen tear"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && (normalizedFinding.contains("gutter") || normalizedFinding.contains("downspout"))
                && !containsAny(normalizedFinding, Set.of("water intrusion", "active leak", "foundation", "grading"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && normalizedFinding.contains("drywall crack")
                && !containsAny(normalizedFinding, Set.of("foundation", "struct", "settlement", "stair-step"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && (normalizedFinding.contains("crack") || normalizedFinding.contains("cracked"))
                && !containsAny(normalizedFinding, Set.of("foundation", "struct", "settlement", "stair-step",
                        "roof", "water intrusion", "active leak", "deck safety"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && (normalizedFinding.contains("paneling") || normalizedFinding.contains("fence panel"))) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (cosmeticOrWeak && !containsAny(normalizedFinding, HARD_DEFECT_OVERRIDES)) {
            return FindingBucket.DO_NOT_LEAD;
        }
        if (hardDefect) {
            return FindingBucket.MUST_REQUEST;
        }
        if (cosmeticOrWeak) {
            return FindingBucket.DO_NOT_LEAD;
        }
        return FindingBucket.VERIFY;
    }

    private NegotiationCostProfile buildCostProfile(Verdict verdict,
            List<NegotiationAnchor> leadAnchors,
            List<NegotiationAnchor> verifyAnchors) {
        double broadExposure = estimateRepairExposure(verdict);

        double leadExposure = leadAnchors.stream()
                .mapToDouble(anchor -> anchor.cost() * leadWeight(anchor))
                .sum();
        double verifyReserve = verifyAnchors.stream()
                .limit(2)
                .mapToDouble(anchor -> anchor.cost() * verifyReserveWeight(anchor))
                .sum();

        int matchedLeadCount = (int) leadAnchors.stream().filter(NegotiationAnchor::matchedFinding).count();
        int hardLeadCount = (int) leadAnchors.stream().filter(NegotiationAnchor::hardLead).count();

        double scopedExposure = leadExposure + verifyReserve;
        if (broadExposure > 0) {
            double floor = broadExposure * (leadAnchors.isEmpty() ? 0.55 : 0.40);
            double ceiling = broadExposure * (matchedLeadCount >= 2 ? 0.95 : 0.88);
            if (hardLeadCount >= 2) {
                ceiling = Math.max(ceiling, broadExposure * 0.96);
            }
            scopedExposure = Math.max(scopedExposure, floor);
            scopedExposure = Math.min(scopedExposure, Math.max(ceiling, leadExposure));
        }
        if (scopedExposure <= 0) {
            scopedExposure = Math.max(estimateRepairExposure(verdict) * 0.55, 10000.0);
        }

        List<String> leadLabels = leadAnchors.stream()
                .map(NegotiationAnchor::label)
                .limit(3)
                .toList();
        List<String> verifyLabels = verifyAnchors.stream()
                .map(NegotiationAnchor::label)
                .limit(2)
                .toList();

        List<String> pricingBreakdown = new ArrayList<>();
        for (NegotiationAnchor anchor : leadAnchors.stream().limit(3).toList()) {
            pricingBreakdown.add(String.format(
                    "%s: $%s counted in the opening scope%s.",
                    anchor.label(),
                    formatMoney(anchor.cost()),
                    leadReasonSuffix(anchor)));
        }
        if (verifyReserve >= 500.0 && !verifyLabels.isEmpty()) {
            pricingBreakdown.add(String.format(
                    "$%s kept as a verification reserve for %s instead of inflating the first ask.",
                    formatMoney(verifyReserve),
                    joinForSentence(verifyLabels)));
        }
        double trimmedExposure = Math.max(0.0, broadExposure - scopedExposure);
        if (trimmedExposure >= 1500.0) {
            pricingBreakdown.add(String.format(
                    "$%s of broader repair exposure stays out of the opening ask because it is cosmetic, future-phase, or weakly documented.",
                    formatMoney(trimmedExposure)));
        }

        String dataAnchor;
        if (matchedLeadCount > 0) {
            dataAnchor = String.format(
                    "$%s scoped exposure across %d inspection-backed lead anchor%s",
                    formatMoney(scopedExposure),
                    matchedLeadCount,
                    matchedLeadCount == 1 ? "" : "s");
        } else if (!leadAnchors.isEmpty()) {
            dataAnchor = String.format(
                    "$%s scoped exposure across %d verdict-plan lead anchor%s",
                    formatMoney(scopedExposure),
                    leadAnchors.size(),
                    leadAnchors.size() == 1 ? "" : "s");
        } else {
            dataAnchor = "$" + formatMoney(scopedExposure) + " contextual repair exposure";
        }

        String dataAnchorNote = buildDataAnchorNote(verdict, broadExposure, scopedExposure, matchedLeadCount);

        return new NegotiationCostProfile(
                broadExposure,
                leadExposure,
                verifyReserve,
                scopedExposure,
                leadAnchors.size(),
                matchedLeadCount,
                hardLeadCount,
                leadLabels,
                verifyLabels,
                pricingBreakdown,
                dataAnchor,
                dataAnchorNote);
    }

    private double leadWeight(NegotiationAnchor anchor) {
        if (anchor.hardLead()) {
            return 1.00;
        }
        if (anchor.matchedFinding()) {
            return 0.90;
        }
        if (anchor.coreLead()) {
            return 0.82;
        }
        return 0.70;
    }

    private double verifyReserveWeight(NegotiationAnchor anchor) {
        return anchor.matchedFinding() ? 0.30 : 0.18;
    }

    private String leadReasonSuffix(NegotiationAnchor anchor) {
        if (anchor.hardLead() && anchor.matchedFinding()) {
            return " because it is inspection-backed and hard for the seller to ignore";
        }
        if (anchor.hardLead()) {
            return " because it is a safety, lender-visible, or code-sensitive anchor";
        }
        if (anchor.matchedFinding()) {
            return " because it lines up directly with the inspection text";
        }
        return " because it is a near-term system or structural exposure";
    }

    private String buildDataAnchorNote(Verdict verdict,
            double broadExposure,
            double scopedExposure,
            int matchedLeadCount) {
        List<String> notes = new ArrayList<>();
        if (broadExposure > 0 && Math.abs(broadExposure - scopedExposure) >= 1000.0) {
            notes.add(String.format(
                    "The opening ask is narrower than the full $%s repair estimate.",
                    formatMoney(broadExposure)));
        } else if (broadExposure > 0) {
            notes.add(String.format(
                    "Most of the estimated repair exposure is concentrated in the lead items, so the ask stays close to $%s.",
                    formatMoney(scopedExposure)));
        }

        String contextSignalNote = buildContextSignalNote(verdict.getContextBriefing(), verdict.getComparisonData());
        if (!contextSignalNote.isBlank()) {
            notes.add(contextSignalNote);
        }

        if (matchedLeadCount > 0) {
            notes.add("Only lead anchors and a small verify reserve were counted; cosmetic or later-phase work were cut.");
        } else {
            notes.add("No lead item was directly matched from the pasted findings, so keep one inspection page or quote ready if the seller pushes back.");
        }
        return String.join(" ", notes);
    }

    private String buildContextSignalNote(ContextBriefing briefing, ComparisonData comparisonData) {
        List<String> signals = new ArrayList<>();
        if (briefing != null) {
            if (briefing.getLaborMarketRate() != null && !briefing.getLaborMarketRate().isBlank()) {
                signals.add(briefing.getLaborMarketRate());
            }
            if (briefing.getEraFeature() != null && !briefing.getEraFeature().isBlank()) {
                signals.add(briefing.getEraFeature());
            }
        }
        if (comparisonData != null && Math.abs(comparisonData.getCostDelta()) >= 1000.0) {
            signals.add(String.format("%+.0f%% vs %s",
                    comparisonData.getDeltaPercentage(),
                    comparisonData.getModernEraLabel()));
        }
        if (signals.isEmpty()) {
            return "Metro and era inputs are still applied to the repair basis.";
        }
        return "Cost basis includes " + String.join(" and ", signals) + ".";
    }

    private PacketQuality assessPacketQuality(InspectionResponseInput input,
            List<String> sourceFindings,
            LinkedHashSet<String> mustFixNow,
            LinkedHashSet<String> verifyNext,
            LinkedHashSet<String> doNotLead,
            NegotiationCostProfile costProfile,
            List<String> lenderVisibleSignals,
            List<InspectionEvidenceRef> matchedEvidence,
            List<InspectionReadinessGate> readinessGates) {
        int score = 34;
        List<String> reasons = new ArrayList<>();
        int passCount = countReadinessGates(readinessGates, "PASS");
        int warnCount = countReadinessGates(readinessGates, "WARN");
        int failCount = countReadinessGates(readinessGates, "FAIL");

        reasons.add(passCount + " of " + readinessGates.size() + " hard readiness gates are green.");

        if (!sourceFindings.isEmpty()) {
            score += Math.min(16, 8 + (sourceFindings.size() * 2));
            reasons.add(sourceFindings.size() + " inspection findings were parsed into the packet.");
        }
        if (costProfile.leadAnchorCount() > 0) {
            score += 14;
            reasons.add("The opening scope is priced from " + costProfile.leadAnchorCount()
                    + " lead anchor" + (costProfile.leadAnchorCount() == 1 ? "" : "s")
                    + " instead of the full repair estimate.");
        }
        if (costProfile.matchedLeadCount() > 0) {
            score += 12;
            reasons.add(costProfile.matchedLeadCount() + " lead anchor"
                    + (costProfile.matchedLeadCount() == 1 ? "" : "s")
                    + " line up directly with the inspection text.");
        } else {
            reasons.add("The pricing scope currently leans on verdict-plan anchors more than direct inspection matches.");
        }
        if (costProfile.verifyReserve() >= 500.0 && !verifyNext.isEmpty()) {
            score += 6;
            reasons.add("Secondary items are parked in a verify reserve instead of bloating the first ask.");
        }
        if (!doNotLead.isEmpty()) {
            score += 7;
            reasons.add("Cosmetic or weak items are kept out of the lead ask.");
        }
        if (costProfile.broadExposure() - costProfile.scopedExposure() >= 1500.0) {
            score += 6;
            reasons.add("The ask is narrower than the full repair estimate, which makes it easier to defend.");
        }
        if (!matchedEvidence.isEmpty()) {
            score += 9;
            reasons.add(matchedEvidence.size() + " lead item" + (matchedEvidence.size() == 1 ? "" : "s")
                    + " are tied to report-page evidence.");
            if (hasOcrEvidence(matchedEvidence)) {
                reasons.add("Some report citations came from OCR, so the packet shows page support even for scans or photos.");
            }
        } else {
            reasons.add("No report-page evidence is attached yet, so the packet still leans on text findings alone.");
        }
        if (!lenderVisibleSignals.isEmpty()) {
            score += 5;
            reasons.add("Lender-visible or habitability-sensitive items are explicitly called out.");
        }

        if ("HAS_ONE".equals(input.quoteSupport())) {
            score += 7;
            reasons.add("One outside quote can defend the fallback without expanding the scope.");
        } else if ("MULTIPLE".equals(input.quoteSupport())) {
            score += 10;
            reasons.add("Multiple quotes make the target ask more defensible.");
        } else {
            reasons.add("No quote is attached yet; use inspection pages first and quote only if challenged.");
        }

        if (!"FLEXIBLE".equals(input.closingWindow())) {
            score += 5;
            reasons.add("The timeline is explicit, so the agent can frame urgency.");
        }
        if (!input.responseDeadlineAt().isBlank()) {
            score += 4;
            reasons.add("An exact response deadline is captured: " + formatResponseDeadline(input.responseDeadlineAt()) + ".");
        } else {
            reasons.add("No exact response deadline is captured yet, so timing still depends on the agent's transaction file.");
        }
        if ("NOTICE_SENT_WAITING".equals(input.dealStage())) {
            reasons.add("The first notice is already out, so the packet is being used to defend scope instead of drafting from scratch.");
        } else if ("COUNTER_RECEIVED".equals(input.dealStage())) {
            score += 2;
            reasons.add("A seller counter already exists, so the packet is narrowing the response instead of widening scope.");
        } else if ("TERMS_AGREED_NEED_AMENDMENT".equals(input.dealStage())) {
            reasons.add("The commercial terms may already be agreed, so the remaining risk is translating them into signed paperwork without drift.");
        } else if ("TERMINATION_CONSIDERED".equals(input.dealStage())) {
            score -= 4;
            reasons.add("Termination is a live option, so the packet should protect rights before it chases convenience.");
        } else if ("CONTINGENCY_REMOVED_OR_EXPIRED".equals(input.dealStage())) {
            score -= 22;
            reasons.add("The inspection contingency appears removed or expired, so the packet may be useful only as advisory framing, not as full leverage.");
        }

        if (costProfile.matchedLeadCount() == 0) {
            score -= 8;
        }
        if (costProfile.leadAnchorCount() <= 1 && "NONE".equals(input.quoteSupport())) {
            score -= 5;
            reasons.add("Only one lead anchor is carrying the ask, so keep one inspection page or quote ready.");
        }
        score += passCount * 2;
        score -= warnCount * 4;
        score -= failCount * 12;

        score = Math.max(48, Math.min(score, 97));

        String readinessLabel;
        String readinessNote;
        if (failCount > 0) {
            readinessLabel = "Not sendable";
            readinessNote = "At least one hard gate is red. Keep this in review mode until the blocking file gaps are closed.";
        } else if (warnCount > 0) {
            readinessLabel = "Draft only";
            readinessNote = "The packet can be reviewed internally, but at least one hard gate still needs human confirmation before external send.";
        } else {
            readinessLabel = "Ready to send";
            readinessNote = "All hard readiness gates are green, so this packet is aligned for a real send path instead of a drafting-only pass.";
        }

        return new PacketQuality(readinessLabel, readinessNote, score, reasons);
    }

    private List<InspectionReadinessGate> buildReadinessGates(InspectionResponseInput input,
            LinkedHashSet<String> mustFixNow,
            List<InspectionEvidenceRef> matchedEvidence,
            List<String> lenderVisibleSignals,
            PacketWorkflow workflow) {
        return List.of(
                buildDeadlineGate(input),
                buildFormPathGate(input, workflow),
                buildEvidenceGate(input, mustFixNow, matchedEvidence),
                buildFinancingGate(input, lenderVisibleSignals, matchedEvidence),
                buildStageGate(input),
                buildSendBundleGate(input, matchedEvidence));
    }

    private InspectionReadinessGate buildDeadlineGate(InspectionResponseInput input) {
        if ("CONTINGENCY_REMOVED_OR_EXPIRED".equals(input.dealStage())) {
            return new InspectionReadinessGate(
                    "FAIL",
                    "Deadline alive",
                    "The inspection leverage is marked removed or expired, so this file cannot be treated as a live send-ready negotiation.");
        }
        if (!input.responseDeadlineAt().isBlank()) {
            return new InspectionReadinessGate(
                    "PASS",
                    "Deadline alive",
                    "Exact response deadline captured for " + formatResponseDeadline(input.responseDeadlineAt())
                            + ". Human review should still confirm the local contract timezone.");
        }
        if (!"FLEXIBLE".equals(input.closingWindow())) {
            return new InspectionReadinessGate(
                    "WARN",
                    "Deadline alive",
                    "Only a relative timeline is captured. Add the exact contract cutoff before treating the packet as externally send-ready.");
        }
        return new InspectionReadinessGate(
                "FAIL",
                "Deadline alive",
                "No exact contract deadline is captured, so the file can lose leverage without the packet noticing.");
    }

    private InspectionReadinessGate buildFormPathGate(InspectionResponseInput input,
            PacketWorkflow workflow) {
        if (!"AUTO".equals(input.contractWorkflow()) && !"GENERAL_AMENDMENT".equals(input.contractWorkflow())) {
            return new InspectionReadinessGate(
                    "PASS",
                    "Form path locked",
                    "The workflow is explicitly tied to a known contract path: " + workflow.label() + ".");
        }
        if ("GENERAL_AMENDMENT".equals(input.contractWorkflow())) {
            return new InspectionReadinessGate(
                    "WARN",
                    "Form path locked",
                    "The packet is routed through a generic amendment path. Confirm the exact state form family before external send.");
        }
        if (workflow.label().contains("California")
                || workflow.label().contains("Texas")
                || workflow.label().contains("Florida")
                || workflow.label().contains("Colorado")) {
            return new InspectionReadinessGate(
                    "WARN",
                    "Form path locked",
                    "The state-specific flow was inferred from the address. Confirm the actual contract form and version before filing.");
        }
        return new InspectionReadinessGate(
                "FAIL",
                "Form path locked",
                "No state-form-native routing is locked yet, so the packet is still closer to prose than to a file-ready notice or amendment.");
    }

    private InspectionReadinessGate buildEvidenceGate(InspectionResponseInput input,
            LinkedHashSet<String> mustFixNow,
            List<InspectionEvidenceRef> matchedEvidence) {
        int requiredEvidenceCount = Math.max(1, Math.min(2, mustFixNow.size()));
        if (matchedEvidence.size() >= requiredEvidenceCount) {
            return new InspectionReadinessGate(
                    "PASS",
                    "Evidence pack sufficient",
                    "Core ask items have exhibit-level support from report pages or uploaded evidence.");
        }
        if (!matchedEvidence.isEmpty() || !input.evidenceSourceLabel().isBlank()) {
            return new InspectionReadinessGate(
                    "WARN",
                    "Evidence pack sufficient",
                    "Some evidence is attached, but not every lead item is pinned to a clean exhibit set yet.");
        }
        return new InspectionReadinessGate(
                "FAIL",
                "Evidence pack sufficient",
                "The packet is still text-only. Attach report pages, photos, or another exhibit before treating the lead ask as file-ready.");
    }

    private InspectionReadinessGate buildFinancingGate(InspectionResponseInput input,
            List<String> lenderVisibleSignals,
            List<InspectionEvidenceRef> matchedEvidence) {
        if (input.loanType() == LoanType.CASH && lenderVisibleSignals.isEmpty()) {
            return new InspectionReadinessGate(
                    "PASS",
                    "Financing risk cleared",
                    "This file reads as cash with no lender-visible trigger detected, so no extra underwriting trail is driving the packet.");
        }
        if (lenderVisibleSignals.isEmpty()) {
            return new InspectionReadinessGate(
                    "PASS",
                    "Financing risk cleared",
                    "Loan posture is known and no lender-visible trigger is currently leading the packet.");
        }
        if (matchedEvidence.isEmpty()) {
            return new InspectionReadinessGate(
                    "FAIL",
                    "Financing risk cleared",
                    "Lender-visible items are in play, but there is no exhibit-level support attached to defend them.");
        }
        return new InspectionReadinessGate(
                "WARN",
                "Financing risk cleared",
                "Lender-visible items are flagged, but written lender or loan-officer confirmation is not tracked in the packet yet.");
    }

    private InspectionReadinessGate buildStageGate(InspectionResponseInput input) {
        return switch (input.dealStage()) {
            case "COUNTER_RECEIVED" -> new InspectionReadinessGate(
                    "PASS",
                    "Stage and rights preserved",
                    "The file is in counter mode, and the packet can narrow the response instead of restarting the negotiation.");
            case "TERMS_AGREED_NEED_AMENDMENT" -> new InspectionReadinessGate(
                    "PASS",
                    "Stage and rights preserved",
                    "The file is in settlement mode, so the next risk is translation into signed paperwork rather than drafting a new first ask.");
            case "NOTICE_SENT_WAITING", "DRAFTING_FIRST_NOTICE" -> new InspectionReadinessGate(
                    "PASS",
                    "Stage and rights preserved",
                    "The current stage is explicit, so the packet can frame the next move without pretending the file is somewhere else.");
            case "TERMINATION_CONSIDERED" -> new InspectionReadinessGate(
                    "WARN",
                    "Stage and rights preserved",
                    "Termination is a live option, so the packet should protect rights before it widens the ask or softens the posture.");
            case "CONTINGENCY_REMOVED_OR_EXPIRED" -> new InspectionReadinessGate(
                    "FAIL",
                    "Stage and rights preserved",
                    "The contingency is marked removed or expired, so the packet cannot be treated as normal first-round leverage.");
            default -> new InspectionReadinessGate(
                    "WARN",
                    "Stage and rights preserved",
                    "The stage is not locked tightly enough yet, so the next action still needs human confirmation.");
        };
    }

    private InspectionReadinessGate buildSendBundleGate(InspectionResponseInput input,
            List<InspectionEvidenceRef> matchedEvidence) {
        boolean hasOwner = !input.agentName().isBlank();
        boolean hasFileIdentity = !input.caseLabel().isBlank() || !input.propertyAddress().isBlank();
        boolean hasAttachmentBase = !matchedEvidence.isEmpty() || !input.evidenceSourceLabel().isBlank();

        if (hasOwner && hasFileIdentity && hasAttachmentBase) {
            return new InspectionReadinessGate(
                    "PASS",
                    "Send bundle owned",
                    "The packet has a named owner, a file identity, and an attachment base for the outgoing bundle.");
        }
        if (hasOwner || hasFileIdentity) {
            return new InspectionReadinessGate(
                    "WARN",
                    "Send bundle owned",
                    "Some ownership metadata exists, but the final send bundle still lacks either a clear owner, a file identity, or a clean attachment manifest.");
        }
        return new InspectionReadinessGate(
                "FAIL",
                "Send bundle owned",
                "No clear file owner or deal identity is attached yet, so the packet is not safe to treat as a send bundle.");
    }

    private int countReadinessGates(List<InspectionReadinessGate> readinessGates, String status) {
        return (int) readinessGates.stream()
                .filter(gate -> status.equals(gate.status()))
                .count();
    }

    public InspectionCaseWorkflowSummary buildCaseWorkflow(InspectionResponsePacket packet,
            List<EventLog> eventLogs) {
        boolean hasApproval = hasWorkflowEvent(eventLogs, EventLog.EventType.BUYER_APPROVED);
        boolean hasSent = hasWorkflowEvent(eventLogs, EventLog.EventType.MARK_SENT);
        EventLog.EventType latestReviewGateEvent = latestWorkflowEventType(eventLogs,
                EventLog.EventType.REQUEST_REVIEW,
                EventLog.EventType.BUYER_APPROVED);
        boolean hasOpenReview = latestReviewGateEvent == EventLog.EventType.REQUEST_REVIEW;
        EventLog.EventType latestNegotiationOutcome = latestWorkflowEventType(eventLogs,
                EventLog.EventType.COUNTER_RECEIVED,
                EventLog.EventType.RESOLUTION_SIGNED,
                EventLog.EventType.MARK_TERMINATED);

        String currentState;
        String currentLabel;
        String currentNote;
        String recommendedNextAction;

        if (latestNegotiationOutcome == EventLog.EventType.MARK_TERMINATED) {
            currentState = "TERMINATED";
            currentLabel = "Terminated";
            currentNote = "Termination or withdrawal is recorded, so this file should now preserve the reason, deadline context, and supporting exhibits.";
            recommendedNextAction = "Keep the termination notice, inspection support, and key timeline notes together in the case record.";
        } else if (latestNegotiationOutcome == EventLog.EventType.RESOLUTION_SIGNED) {
            currentState = "RESOLVED";
            currentLabel = "Resolution signed";
            currentNote = "A signed resolution is recorded, so the live negotiation phase is over and the main risk is preserving the final agreement cleanly.";
            recommendedNextAction = "Keep the signed amendment or resolution with the cited exhibits and the final negotiation packet.";
        } else if (latestNegotiationOutcome == EventLog.EventType.COUNTER_RECEIVED) {
            currentState = "COUNTERED";
            currentLabel = "Seller counter received";
            currentNote = "A seller counter is recorded, so the file has moved from first send to outcome negotiation.";
            recommendedNextAction = "Compare the counter against the target and fallback, then either sign a resolution or terminate while rights are still alive.";
        } else if (hasSent) {
            currentState = "SENT";
            currentLabel = "Sent";
            currentNote = "A send event is recorded. The next real state should be a seller counter, a signed resolution, or a termination path.";
            recommendedNextAction = "When the other side responds, record the counter or the signed resolution instead of leaving the file in a vague sent state.";
        } else if ("Not sendable".equals(packet.readinessLabel())) {
            currentState = "DRAFT";
            currentLabel = "Draft";
            currentNote = "Hard readiness gates are still blocking this packet, so it should stay in draft mode.";
            recommendedNextAction = "Clear the red gates before asking anyone to approve or send the packet.";
        } else if ("Draft only".equals(packet.readinessLabel()) || hasOpenReview || !hasApproval) {
            currentState = "REVIEW";
            currentLabel = "In review";
            currentNote = hasOpenReview
                    ? "A review request was recorded after the last approval, so the file stays in review until it is re-approved."
                    : hasApproval
                            ? "The file is in review because some hard gates still need confirmation before send."
                    : "No buyer approval event is recorded yet, so this case should stay in review.";
            recommendedNextAction = hasOpenReview
                    ? "Resolve the requested review comments and then record a fresh buyer approval."
                    : hasApproval
                            ? "Resolve the remaining warning gates and then record a clean ready-to-send approval path."
                    : "Record buyer approval once the target ask and fallback are settled.";
        } else {
            currentState = "READY";
            currentLabel = "Ready";
            currentNote = "All hard gates are green and buyer approval is recorded, so this case has a send-ready status.";
            recommendedNextAction = "Send the packet through the real contract path and then record the sent artifact.";
        }

        List<InspectionCaseWorkflowEvent> timeline = new ArrayList<>();
        for (EventLog event : eventLogs) {
            String label = switch (event.getEventType()) {
                case PACKET_GENERATED -> "Packet generated";
                case REQUEST_REVIEW -> "Review requested";
                case BUYER_APPROVED -> "Buyer approval recorded";
                case MARK_SENT -> "Packet sent";
                case COUNTER_RECEIVED -> "Seller counter received";
                case RESOLUTION_SIGNED -> "Resolution signed";
                case MARK_TERMINATED -> "Termination or withdrawal recorded";
                case MARK_FILED -> "Legacy file retention recorded";
                case COPY_PACKET -> "Full packet copied";
                case COPY_AGENT_REQUEST -> "Agent request copied";
                case PRINT_PACKET -> "Packet printed";
                case SUBMIT_FEEDBACK -> "Marked useful";
                default -> null;
            };
            if (label == null) {
                continue;
            }
            String timestampLabel = event.getCreatedAt() == null
                    ? ""
                    : event.getCreatedAt().format(EVENT_DISPLAY_FORMATTER);
            String note = describeWorkflowEventTarget(event);
            timeline.add(new InspectionCaseWorkflowEvent(label, note, timestampLabel));
        }

        return new InspectionCaseWorkflowSummary(currentState, currentLabel, currentNote, recommendedNextAction, timeline);
    }

    private boolean hasWorkflowEvent(List<EventLog> eventLogs, EventLog.EventType eventType) {
        return eventLogs.stream().anyMatch(event -> event.getEventType() == eventType);
    }

    private EventLog.EventType latestWorkflowEventType(List<EventLog> eventLogs, EventLog.EventType... eventTypes) {
        Set<EventLog.EventType> allowed = Set.of(eventTypes);
        return eventLogs.stream()
                .filter(event -> allowed.contains(event.getEventType()))
                .reduce((first, second) -> second)
                .map(EventLog::getEventType)
                .orElse(null);
    }

    private String describeWorkflowEventTarget(EventLog event) {
        String actor = extractEventField(event.getTarget(), "actor");
        String note = extractEventField(event.getTarget(), "note");
        String snapshot = extractEventField(event.getTarget(), "snapshot");

        List<String> parts = new ArrayList<>();
        if (!actor.isBlank()) {
            parts.add("Actor: " + actor);
        }
        if (!note.isBlank()) {
            parts.add(note);
        }
        if (!snapshot.isBlank()) {
            parts.add("Snapshot: " + snapshot);
        }

        if (parts.isEmpty()) {
            return sanitizeEventTargetText(event.getTarget());
        }
        return String.join(" | ", parts);
    }

    private String extractEventField(String target, String key) {
        if (target == null || target.isBlank()) {
            return "";
        }
        String prefix = key + "=";
        for (String part : target.split("\\|")) {
            String trimmed = part.trim();
            if (trimmed.startsWith(prefix)) {
                String value = trimmed.substring(prefix.length()).trim();
                try {
                    return URLDecoder.decode(value, StandardCharsets.UTF_8);
                } catch (IllegalArgumentException ignored) {
                    return value;
                }
            }
        }
        return "";
    }

    private String sanitizeEventTargetText(String target) {
        if (target == null || target.isBlank()) {
            return "";
        }
        String sanitized = target.replace("entry=", "")
                .replace("action=", "")
                .replace("state=", "");
        return MULTI_SPACE_PATTERN.matcher(sanitized.replace('|', ' ')).replaceAll(" ").trim();
    }

    private List<String> buildVerdictRationale(PacketQuality quality,
            List<InspectionReadinessGate> readinessGates,
            List<InspectionDefenseSignal> defenseSignals,
            AskRange askRange,
            NegotiationCostProfile costProfile) {
        List<String> rationale = new ArrayList<>();
        int passCount = countReadinessGates(readinessGates, "PASS");
        int warnCount = countReadinessGates(readinessGates, "WARN");
        int failCount = countReadinessGates(readinessGates, "FAIL");

        rationale.add("Verdict is " + quality.readinessLabel() + " because " + passCount + " hard gate"
                + (passCount == 1 ? "" : "s") + " passed, " + warnCount + " need confirmation, and "
                + failCount + " are blocking.");

        List<String> blockingGates = readinessGates.stream()
                .filter(gate -> "FAIL".equals(gate.status()))
                .map(gate -> gate.label() + ": " + gate.note())
                .limit(3)
                .toList();
        if (!blockingGates.isEmpty()) {
            rationale.add("Do not send because " + joinForSentence(blockingGates) + ".");
        } else {
            List<String> warningGates = readinessGates.stream()
                    .filter(gate -> "WARN".equals(gate.status()))
                    .map(gate -> gate.label() + ": " + gate.note())
                    .limit(3)
                    .toList();
            if (warningGates.isEmpty()) {
                rationale.add("No hard readiness gate is red or yellow; timing, form path, evidence, financing, stage, and file owner are all cleared.");
            } else {
                rationale.add("Treat this as internal review until " + joinForSentence(warningGates) + ".");
            }
        }

        defenseSignals.stream()
                .filter(signal -> "Send posture".equals(signal.label()))
                .findFirst()
                .ifPresent(signal -> rationale.add(signal.detail() + " Next: " + signal.action()));
        rationale.add("The $" + formatMoney(askRange.targetAsk()) + " target is sized from about $"
                + formatMoney(costProfile.scopedExposure()) + " of scoped exposure; the fallback floor is $"
                + formatMoney(askRange.defendableAsk()) + ".");
        return rationale;
    }

    private List<String> buildMissingEvidence(LinkedHashSet<String> mustFixNow,
            List<InspectionEvidenceRef> matchedEvidence,
            InspectionResponseInput input,
            List<String> lenderVisibleSignals) {
        LinkedHashSet<String> missing = new LinkedHashSet<>();
        for (String leadItem : mustFixNow.stream().limit(4).toList()) {
            if (findBestEvidenceMatch(leadItem, matchedEvidence) == null) {
                missing.add("Attach a report page, photo, or quote for " + leadItem + ".");
            }
        }
        if (input.evidenceSourceLabel().isBlank()) {
            missing.add("Name the source report or photo set so the agent can find the exhibit quickly.");
        }
        if (hasOcrEvidence(matchedEvidence)) {
            missing.add("Check every OCR-marked citation against the original scan or photo before sending.");
        }
        if (!lenderVisibleSignals.isEmpty() && matchedEvidence.isEmpty()) {
            missing.add("Lender-visible items need exhibit-level support before the agent leans on financing pressure.");
        }
        if ("NONE".equals(input.quoteSupport())) {
            missing.add("No contractor quote is attached; keep the dollar figure labeled as a negotiation estimate.");
        }
        if (missing.isEmpty()) {
            missing.add("Lead items have exhibit support. Final check is matching the cited wording to the original report before send.");
        }
        return List.copyOf(missing);
    }

    private List<String> buildReviewCaveats(InspectionResponseInput input,
            List<String> lenderVisibleSignals,
            List<InspectionEvidenceRef> matchedEvidence,
            PacketWorkflow workflow) {
        List<String> caveats = new ArrayList<>();
        caveats.add("Evidence caveat: this checks pasted findings and attached citations; it does not verify the original inspection report.");
        caveats.add(switch (input.quoteSupport()) {
            case "MULTIPLE" -> "Quote caveat: multiple quotes support the ask, but use the lowest credible quote as the fallback floor.";
            case "HAS_ONE" -> "Quote caveat: one quote supports the fallback; do not call the full target ask a contractor bid.";
            default -> "Quote caveat: no quote is attached, so the number must stay framed as a negotiation estimate.";
        });
        if (lenderVisibleSignals.isEmpty()) {
            caveats.add("Lender caveat: no lender-visible trigger was detected, so do not oversell underwriting leverage.");
        } else {
            caveats.add("Lender caveat: " + joinForSentence(lenderVisibleSignals.stream().limit(2).toList())
                    + " may be financing-sensitive, but this packet is not a lender or appraisal ruling.");
        }
        if ("AUTO".equals(input.contractWorkflow())) {
            caveats.add("Form caveat: " + workflow.label()
                    + " was inferred from the file context; confirm the actual state or brokerage form before send.");
        } else if ("GENERAL_AMENDMENT".equals(input.contractWorkflow())) {
            caveats.add("Form caveat: generic amendment routing is not enough by itself; confirm the state form family and deadline.");
        } else {
            caveats.add("Form caveat: final language still belongs in the signed " + workflow.label()
                    + " paperwork, not only in this generated packet.");
        }
        if (matchedEvidence.isEmpty()) {
            caveats.add("Evidence caveat: no matched report-page evidence is attached yet, so the file should remain review-only.");
        }
        return caveats;
    }

    private List<String> nextActions(InspectionResponseInput input,
            AskRange askRange,
            NegotiationCostProfile costProfile,
            List<String> lenderVisibleSignals,
            List<InspectionEvidenceRef> matchedEvidence,
            PacketWorkflow workflow,
            List<InspectionReadinessGate> readinessGates) {
        List<String> actions = new ArrayList<>();
        boolean hasFailingGate = readinessGates.stream().anyMatch(gate -> "FAIL".equals(gate.status()));
        boolean hasWarningGate = readinessGates.stream().anyMatch(gate -> "WARN".equals(gate.status()));
        if (hasFailingGate) {
            actions.add("Do not send externally yet. Clear the red readiness gates before asking the buyer to approve or the agent to send.");
        } else if (hasWarningGate) {
            actions.add("Keep this in internal review until the yellow gates are confirmed by the agent, broker, lender, or file owner.");
        } else {
            actions.add("The pre-send check is green. Send the agent-ready request with a $"
                    + formatMoney(askRange.targetAsk()) + " opening ask.");
        }
        if (!input.responseDeadlineAt().isBlank()) {
            actions.add("Move before " + formatResponseDeadline(input.responseDeadlineAt())
                    + " so the packet lands while the current contract leverage is still alive.");
        }
        if (!costProfile.leadLabels().isEmpty()) {
            actions.add("Attach inspection pages or photos for " + joinForSentence(costProfile.leadLabels()) + " only.");
        } else {
            actions.add("Attach one inspection page or photo for the strongest lead item before treating the ask as firm.");
        }
        if (!matchedEvidence.isEmpty()) {
            actions.add("Quote the linked report pages directly in the agent email instead of paraphrasing from memory.");
            if (hasOcrEvidence(matchedEvidence)) {
                actions.add("Before sending, verify the OCR-marked excerpt against the scanned page or photo wording.");
            }
        }
        if (!lenderVisibleSignals.isEmpty()) {
            actions.add("Call out " + joinForSentence(lenderVisibleSignals.stream().limit(2).toList())
                    + " as lender-visible or habitability-sensitive in the agent note.");
        }
        if ("NONE".equals(input.quoteSupport())) {
            actions.add("Get one quote only if the seller disputes the scope or dollar amount.");
        } else {
            actions.add("Use the quote as evidence, not as a broad repair wishlist.");
        }
        if (!workflow.steps().isEmpty()) {
            actions.add(workflow.steps().get(0));
        }
        actions.add("If the seller pushes back, fall to $" + formatMoney(askRange.defendableAsk())
                + " instead of trading for cosmetic repairs.");
        return actions;
    }

    private List<InspectionExclusionItem> buildExclusionItems(LinkedHashSet<String> doNotLead) {
        List<InspectionExclusionItem> items = new ArrayList<>();
        for (String item : doNotLead) {
            String normalized = item.toLowerCase(Locale.ENGLISH);
            String reason;
            if (containsAny(normalized, DO_NOT_LEAD_TERMS)) {
                reason = "Cosmetic or preference-heavy item. Leading with it weakens the core safety or system ask.";
            } else if (normalized.contains("minor")) {
                reason = "Minor maintenance item. It belongs in a later punch list, not in the opening packet.";
            } else {
                reason = "Weak first-round leverage. Keep it out unless it later ties to safety, financing, or a hard quote.";
            }
            items.add(new InspectionExclusionItem(item, reason));
        }
        items.add(new InspectionExclusionItem(
                "Preference upgrades that were visible before the offer",
                "Visible-before-offer preference asks are easy to reject and rarely strengthen the first response."));
        items.add(new InspectionExclusionItem(
                "Small maintenance items that weaken the main safety or system request",
                "Low-value line items dilute focus and make the packet read like a laundry list."));
        return items;
    }

    private PacketWorkflow buildWorkflow(InspectionResponseInput input,
            AskRange askRange,
            List<String> lenderVisibleSignals) {
        List<String> steps = new ArrayList<>();
        String noteSuffix = lenderVisibleSignals.isEmpty()
                ? ""
                : " Keep lender-visible items explicit so the file shows why the request stays narrow.";
        String contractWorkflow = input.contractWorkflow();
        String stateCode = inferStateCode(input);

        if ("CA_INVESTIGATION".equals(contractWorkflow) || ("AUTO".equals(contractWorkflow) && "CA".equals(stateCode))) {
            return applyDealStage(buildCaliforniaWorkflow(input, askRange, noteSuffix), input, askRange);
        }
        if ("TX_OPTION".equals(contractWorkflow) || ("AUTO".equals(contractWorkflow) && "TX".equals(stateCode))) {
            return applyDealStage(buildTexasWorkflow(input, askRange, noteSuffix), input, askRange);
        }
        if ("FL_AS_IS".equals(contractWorkflow) || "FL_STANDARD".equals(contractWorkflow)
                || ("AUTO".equals(contractWorkflow) && "FL".equals(stateCode))) {
            return applyDealStage(buildFloridaWorkflow(input, askRange, noteSuffix), input, askRange);
        }
        if ("CO_OBJECTION".equals(contractWorkflow)
                || ("AUTO".equals(contractWorkflow) && "CO".equals(stateCode))) {
            return applyDealStage(buildColoradoWorkflow(input, askRange, noteSuffix), input, askRange);
        }
        if ("GENERAL_AMENDMENT".equals(contractWorkflow)) {
            return applyDealStage(buildGeneralAmendmentWorkflow(askRange, noteSuffix), input, askRange);
        }

        switch (input.acquisitionEntry()) {
            case "objection" -> {
                steps.add("Move the must-request items into the state objection notice or unsatisfactory-items form, not into a free-form laundry list.");
                steps.add("Attach or quote only the inspection pages supporting those unsatisfactory items.");
                steps.add("Carry the $" + formatMoney(askRange.targetAsk()) + " ask and $" + formatMoney(askRange.defendableAsk())
                        + " fallback into the resolution conversation after the objection is delivered.");
                if (input.loanType() == LoanType.FHA || input.loanType() == LoanType.VA) {
                    steps.add("Before final resolution, notify the lender in writing if the objection touches habitability, structural, or appraisal-sensitive items.");
                }
                return applyDealStage(new PacketWorkflow(
                        "Objection-first workflow",
                        "How to move this into the real objection file",
                        "This packet supports the objection. It is not a substitute for the state-approved objection notice or amendment."
                                + noteSuffix,
                        steps), input, askRange);
            }
            case "repair_request" -> {
                if ("CO".equals(stateCode)) {
                    steps.add("In Colorado-style files, do not treat this as a free-floating repair letter; translate the narrow scope into the objection / resolution paperwork that actually governs the deal.");
                    steps.add("Keep seller-managed repair items limited to the few defects that materially change leverage or financing posture.");
                    steps.add("If timing is weak, convert the same scope into a $" + formatMoney(askRange.targetAsk())
                            + " credit resolution instead of expanding the repair list.");
                    return applyDealStage(new PacketWorkflow(
                            "Colorado repair-resolution workflow",
                            "How to move this into Colorado's inspection paperwork",
                            "Colorado's transaction flow revolves around objection and resolution documents, not just a generic repair request. Use the packet to narrow scope before that paperwork is drafted."
                                    + noteSuffix,
                            steps), input, askRange);
                }
                steps.add("Use the must-request section to fill the repair addendum, but keep the request limited to the items that actually change leverage.");
                steps.add("If the seller cannot finish the work cleanly before closing, convert the same scope into a $" + formatMoney(askRange.targetAsk())
                        + " credit request instead of broadening the list.");
                steps.add("Document the fallback at $" + formatMoney(askRange.defendableAsk())
                        + " so the negotiation does not slide into vague promises to repair.");
                return applyDealStage(new PacketWorkflow(
                        "Repair-addendum workflow",
                        "How to move this into the real repair paperwork",
                        "Use the packet to decide what belongs in the repair addendum, then keep a credit fallback ready if timing or contractor control makes seller-managed repairs messy."
                                + noteSuffix,
                        steps), input, askRange);
            }
            case "credit" -> {
                if ("CO".equals(stateCode)) {
                    steps.add("Use the packet to support the credit number, then move that resolution into the Colorado objection / resolution paperwork instead of leaving it in email only.");
                    steps.add("Anchor the first written ask at $" + formatMoney(askRange.targetAsk())
                            + " and keep the fallback at $" + formatMoney(askRange.defendableAsk()) + ".");
                    steps.add("If the lender touches the file, keep the lender-visible items and written lender communication together with the resolution record.");
                    return applyDealStage(new PacketWorkflow(
                            "Colorado credit-resolution workflow",
                            "How to move this into Colorado's resolution file",
                            "In Colorado, the packet should support the objection-resolution flow. The negotiated credit still needs to land in the actual contract paperwork, not remain just a negotiation note."
                                    + noteSuffix,
                            steps), input, askRange);
                }
                steps.add("Use the agent note as the narrative support for a seller-credit amendment or concession request.");
                steps.add("Anchor the first written ask at $" + formatMoney(askRange.targetAsk())
                        + " and keep the fallback at $" + formatMoney(askRange.defendableAsk()) + ".");
                steps.add("If the lender requests backup, send the cited inspection pages and only the quotes tied to lead items.");
                return applyDealStage(new PacketWorkflow(
                        "Credit-first workflow",
                        "How to move this into the real credit request",
                        "The packet should support a narrow credit ask in the contract file, not act like a standalone memo that never gets translated into the amendment."
                                + noteSuffix,
                        steps), input, askRange);
            }
            default -> {
                steps.add("Send the agent note first, then move the narrow scope into the state-approved amendment, objection, or repair form your market actually uses.");
                steps.add("Keep the written ask at $" + formatMoney(askRange.targetAsk())
                        + " and avoid adding verify-later or cosmetic items just because they appeared in the report.");
                steps.add("If the seller resists, counter from the same lead items at $" + formatMoney(askRange.defendableAsk())
                        + " rather than restarting from scratch.");
                return applyDealStage(new PacketWorkflow(
                        "Agent packet workflow",
                        "How to move this into the real transaction file",
                        "This is a negotiation packet, not the signed state form. Use it to decide the scope and wording, then map it into the paperwork your market requires."
                                + noteSuffix,
                        steps), input, askRange);
            }
        }
    }

    private PacketWorkflow applyDealStage(PacketWorkflow workflow,
            InspectionResponseInput input,
            AskRange askRange) {
        String dealStage = input.dealStage();
        if (dealStage == null || dealStage.isBlank() || "DRAFTING_FIRST_NOTICE".equals(dealStage)) {
            return workflow;
        }

        List<String> steps = new ArrayList<>(workflow.steps());
        String note = workflow.note();

        switch (dealStage) {
            case "NOTICE_SENT_WAITING" -> {
                note += " The first notice is already out, so the next move is defending scope and deadline discipline, not rewriting a brand-new ask.";
                steps.add(0, "Do not reopen scope while waiting on the seller. Keep the lead items, page cites, and $" + formatMoney(askRange.defendableAsk())
                        + " fallback ready for the first pushback.");
            }
            case "COUNTER_RECEIVED" -> {
                note += " A seller counter is already on the table, so the packet should tighten the response instead of restarting the negotiation from zero.";
                steps.add(0, "Respond from the same lead items and send a documented counter at $" + formatMoney(askRange.defendableAsk())
                        + " to $" + formatMoney(askRange.targetAsk()) + " instead of rebuilding the whole request.");
            }
            case "TERMS_AGREED_NEED_AMENDMENT" -> {
                note += " The business terms sound agreed, so the main risk now is losing them in translation before they land in signed paperwork.";
                steps.add(0, "Stop negotiating by narrative email and move the settled dollar amount, repair responsibility, and deadline into the signed amendment or form now.");
            }
            case "TERMINATION_CONSIDERED" -> {
                note += " Termination is now a live path, so preserve the buyer's exit rights before broadening the request or sending soft language.";
                steps.add(0, "Check the inspection, objection, or option deadline before sending anything that weakens the buyer's ability to terminate cleanly.");
            }
            case "CONTINGENCY_REMOVED_OR_EXPIRED" -> {
                note += " The inspection contingency appears removed or expired, so treat this as advisory framing only unless the file still has another written path.";
                steps.add(0, "Do not present this as if full inspection leverage remains. Confirm any surviving amendment, lender, or termination path before promising a credit or repair outcome.");
            }
            default -> {
                return workflow;
            }
        }

        if (!input.responseDeadlineAt().isBlank()) {
            note += " Current file deadline: " + formatResponseDeadline(input.responseDeadlineAt()) + ".";
        }

        return new PacketWorkflow(workflow.label(), workflow.title(), note, steps);
    }

    private PacketWorkflow buildColoradoWorkflow(InspectionResponseInput input,
            AskRange askRange,
            String noteSuffix) {
        List<String> steps = new ArrayList<>();
        switch (input.acquisitionEntry()) {
            case "objection", "credit", "repair_request", "ask", "credit_vs_repair", "deadline", "financing",
                    "agent_team", "sample_packet" -> {
                steps.add("Move only the must-request items into Colorado's Inspection Objection Notice as the description of what is unsatisfactory.");
                steps.add("Keep the packet as drafting support, then map the final scope into the objection and inspection-resolution paperwork rather than sending a generic response letter alone.");
                steps.add("Watch the Inspection Resolution Deadline closely; if there is no written settlement by that deadline, the contract flow changes immediately.");
                if (input.loanType() == LoanType.FHA || input.loanType() == LoanType.VA) {
                    steps.add("Colorado's form warns that inspection resolution can affect the loan, so tell the lender in writing before finalizing any repair or credit resolution.");
                }
                return new PacketWorkflow(
                        "Colorado objection workflow",
                        "How to move this into Colorado's objection file",
                        "Colorado uses an Inspection Objection Notice with an Inspection Resolution Deadline. This packet should help draft the narrow objection, but the actual file still needs the state-approved notice and follow-up resolution flow."
                                + noteSuffix,
                        steps);
            }
            default -> {
                return buildGeneralAmendmentWorkflow(askRange, noteSuffix);
            }
        }
    }

    private PacketWorkflow buildGeneralAmendmentWorkflow(AskRange askRange, String noteSuffix) {
        return new PacketWorkflow(
                "General amendment workflow",
                "How to move this into the real amendment flow",
                "Use the packet to narrow the scope and wording, then move the final terms into the contract amendment, addendum, or state form your market actually requires."
                        + noteSuffix,
                List.of(
                        "Translate the narrow scope into the signed amendment or addendum rather than leaving it as email only.",
                        "Keep the written ask at $" + formatMoney(askRange.targetAsk()) + " and hold the fallback at $" + formatMoney(askRange.defendableAsk()) + ".",
                        "If the seller resists, negotiate from the same lead items instead of reopening cosmetic or verify-later issues."));
    }

    private PacketWorkflow buildCaliforniaWorkflow(InspectionResponseInput input,
            AskRange askRange,
            String noteSuffix) {
        List<String> steps = new ArrayList<>();
        switch (input.acquisitionEntry()) {
            case "repair_request", "credit", "ask", "credit_vs_repair", "deadline", "financing",
                    "agent_team", "sample_packet" -> {
                steps.add("Use the open investigation contingency window to send a C.A.R. Request for Repair (RR) rather than relying on a generic email alone.");
                steps.add("If the seller counters, move the final terms into C.A.R. Seller Response and Buyer Reply to Request for Repairs (RRRR) or an Amendment of Existing Agreement Terms (AEA).");
                steps.add("Do not remove the investigation contingency in writing until the repair, credit, or price-change terms are actually settled.");
                return new PacketWorkflow(
                        "California investigation-contingency workflow",
                        "How to move this into California's repair / credit flow",
                        "California files usually run through the investigation contingency and C.A.R. repair-request forms, not a standalone response letter. The default investigation window is often 17 days unless the contract changes it."
                                + noteSuffix,
                        steps);
            }
            default -> {
                steps.add("Treat this packet as investigation-contingency drafting support, then move the final scope into the C.A.R. forms your file is actually using.");
                steps.add("If the buyer wants repairs, credit, or a price adjustment, send the narrow scope through Request for Repair (RR) while the contingency is still open.");
                steps.add("Once the parties settle, document the agreed terms in RRRR or AEA and remove the contingency only in writing.");
                return new PacketWorkflow(
                        "California contingency workflow",
                        "How to move this into California's transaction file",
                        "California negotiation usually rides on the investigation contingency, follow-up repair request forms, and written contingency removal, not on an objection-style notice."
                                + noteSuffix,
                        steps);
            }
        }
    }

    private PacketWorkflow buildTexasWorkflow(InspectionResponseInput input,
            AskRange askRange,
            String noteSuffix) {
        List<String> steps = new ArrayList<>();
        switch (input.acquisitionEntry()) {
            case "repair_request", "credit", "ask", "credit_vs_repair", "deadline", "financing",
                    "agent_team", "sample_packet" -> {
                steps.add("Keep the inspection negotiation inside the Texas option period if one exists; do not wait until the deadline to narrow scope.");
                steps.add("Move repairs, credits, price changes, or lender-required repair allocations into the TREC Amendment to Contract instead of a free-floating repair letter.");
                steps.add("If the parties cannot settle inside the option window, be ready to use the buyer's termination path instead of drifting into undocumented side promises.");
                return new PacketWorkflow(
                        "Texas option-period workflow",
                        "How to move this into Texas contract paperwork",
                        "Texas files usually turn inspection findings into contract changes through the TREC contract and Amendment to Contract, with the option period doing much of the timing work."
                                + noteSuffix,
                        steps);
            }
            default -> {
                steps.add("Translate the packet into the TREC contract workflow your file is using, not into an objection-style notice.");
                steps.add("Use the narrow scope to decide whether to amend the contract or terminate during the option period.");
                steps.add("If lender-required repairs are part of the issue, itemize them in the amendment rather than burying them in narrative email.");
                return new PacketWorkflow(
                        "Texas contract-amendment workflow",
                        "How to move this into Texas transaction documents",
                        "Texas inspection negotiation usually flows through option-period timing, Amendment to Contract, and written termination or amendment forms rather than through a separate objection packet."
                                + noteSuffix,
                        steps);
            }
        }
    }

    private PacketWorkflow buildFloridaWorkflow(InspectionResponseInput input,
            AskRange askRange,
            String noteSuffix) {
        List<String> steps = new ArrayList<>();
        boolean asIs = "FL_AS_IS".equals(input.contractWorkflow());
        boolean standard = "FL_STANDARD".equals(input.contractWorkflow());
        switch (input.acquisitionEntry()) {
            case "repair_request", "credit", "ask", "credit_vs_repair", "deadline", "financing",
                    "agent_team", "sample_packet" -> {
                if (asIs) {
                    steps.add("Keep the packet inside the FR/Bar AS IS inspection period and treat cancellation leverage as the buyer's primary backstop.");
                    steps.add("Use the packet to narrow the issues before asking for a credit or concession, but do not assume the contract promises repairs the seller must cure.");
                    steps.add("If the property is not acceptable, preserve the buyer's written termination option before the inspection period expires.");
                } else if (standard) {
                    steps.add("Keep the packet inside the Florida standard Residential Contract inspection period and move any final ask into the contract notice/addendum flow tied to that form.");
                    steps.add("Check the repair obligations, repair limit, and notice timing before promising a seller-managed repair structure.");
                    steps.add("Use the packet to narrow scope first, then decide whether the clean answer is repair, credit, or cancellation.");
                } else {
                    steps.add("First confirm whether the file is using the Florida Realtors/Florida Bar AS IS contract or the standard Residential Contract, because the inspection remedy path changes with the form.");
                    steps.add("Keep the packet inside the inspection period and move the final request into the contract notice or addendum flow that matches that form.");
                    steps.add("If the file is AS IS, the buyer's strongest leverage may be cancellation during the inspection period; if it is the standard contract, repair obligations and limits need to be checked before promising a structure.");
                }
                return new PacketWorkflow(
                        "Florida inspection-period workflow",
                        "How to move this into Florida contract paperwork",
                        (asIs
                                ? "This file is being treated as Florida FR/Bar AS IS. The packet should help narrow the buyer's inspection-period leverage before the cancellation window closes."
                                : standard
                                        ? "This file is being treated as Florida's standard Residential Contract. The packet should narrow scope before the repair-limit and notice structure are applied."
                                        : "Florida inspection strategy depends heavily on whether the deal uses the FR/Bar AS IS contract or the standard Residential Contract. Use the packet to narrow scope, then map it into the correct inspection-period notice path.")
                                + noteSuffix,
                        steps);
            }
            default -> {
                if (asIs) {
                    steps.add("Treat the packet as support for the FR/Bar AS IS inspection-period decision, not as a substitute for the buyer's written cancellation right.");
                    steps.add("Keep all positioning inside the inspection deadline, because once that window closes the strongest leverage may be gone.");
                    steps.add("Use the narrow scope to decide whether to ask, amend, or terminate before the property becomes a sunk-cost argument.");
                } else {
                    steps.add("Do not assume Florida uses an objection notice. Confirm the contract form first and then move the packet into that contract's inspection-period path.");
                    steps.add("Keep all repair or credit positioning inside the written deadline, because inspection timing and cure structure depend on the contract form.");
                    steps.add("If the buyer's real leverage is cancellation, make that decision before turning the packet into a broad repair wishlist.");
                }
                return new PacketWorkflow(
                        "Florida contract-form workflow",
                        "How to move this into Florida's inspection flow",
                        (asIs
                                ? "Florida FR/Bar AS IS files are driven by the inspection-period acceptance decision more than by a repair-obligation promise, so the packet should support that leverage."
                                : "Florida files can diverge quickly between AS IS and repair-oriented contracts, so the packet should support the contract form rather than override it.")
                                + noteSuffix,
                        steps);
            }
        }
    }

    private List<String> fallbackFindings(Verdict verdict) {
        List<String> findings = new ArrayList<>();
        planMustDoItems(verdict).stream()
                .map(RiskAdjustedItem::getPrettyName)
                .filter(Objects::nonNull)
                .limit(4)
                .forEach(findings::add);
        if (findings.isEmpty() && verdict.getPrimaryCostDriver() != null) {
            findings.add(cleanFindingLabel(verdict.getPrimaryCostDriver()));
        }
        if (findings.isEmpty()) {
            findings.add("Highest-risk inspection items");
        }
        return findings;
    }

    private AskRange buildAskRange(NegotiationCostProfile costProfile,
            InspectionResponseInput input,
            List<String> lenderVisibleSignals,
            List<InspectionEvidenceRef> matchedEvidence) {
        double defendableMultiplier;
        double targetMultiplier;
        double stretchMultiplier;

        switch (input.loanType()) {
            case FHA, VA -> {
                defendableMultiplier = 0.80;
                targetMultiplier = 0.92;
                stretchMultiplier = 1.00;
            }
            case CASH -> {
                defendableMultiplier = 0.86;
                targetMultiplier = 1.00;
                stretchMultiplier = 1.12;
            }
            case INVESTOR -> {
                defendableMultiplier = 0.78;
                targetMultiplier = 0.90;
                stretchMultiplier = 0.98;
            }
            default -> {
                defendableMultiplier = 0.82;
                targetMultiplier = 0.96;
                stretchMultiplier = 1.06;
            }
        }

        if ("UNDER_7_DAYS".equals(input.closingWindow())) {
            targetMultiplier -= 0.03;
            stretchMultiplier -= 0.05;
        } else if ("TWENTY_ONE_TO_FORTY_FIVE_DAYS".equals(input.closingWindow())) {
            defendableMultiplier += 0.01;
            targetMultiplier += 0.01;
        }

        if ("HAS_ONE".equals(input.quoteSupport())) {
            defendableMultiplier += 0.01;
            targetMultiplier += 0.01;
            stretchMultiplier += 0.02;
        } else if ("MULTIPLE".equals(input.quoteSupport())) {
            defendableMultiplier += 0.03;
            targetMultiplier += 0.03;
            stretchMultiplier += 0.05;
        } else {
            stretchMultiplier -= 0.03;
        }

        if ("NONE".equals(input.quoteSupport()) && matchedEvidence.isEmpty()) {
            defendableMultiplier -= 0.03;
            targetMultiplier -= 0.06;
            stretchMultiplier -= 0.10;
        } else if ("NONE".equals(input.quoteSupport())) {
            targetMultiplier -= 0.03;
            stretchMultiplier -= 0.06;
        }

        if (costProfile.matchedLeadCount() == 0) {
            defendableMultiplier -= 0.04;
            targetMultiplier -= 0.08;
            stretchMultiplier -= 0.10;
        } else if (costProfile.matchedLeadCount() >= 2) {
            targetMultiplier += 0.02;
            stretchMultiplier += 0.03;
        }

        if (costProfile.hardLeadCount() >= 2) {
            defendableMultiplier += 0.02;
            targetMultiplier += 0.02;
        }
        if (!lenderVisibleSignals.isEmpty()) {
            defendableMultiplier += 0.02;
            targetMultiplier += 0.01;
        }
        if (!matchedEvidence.isEmpty()) {
            defendableMultiplier += 0.02;
            targetMultiplier += 0.02;
            stretchMultiplier += 0.01;
        }

        double defendableAsk = Math.max(1000.0, roundToNearest500(costProfile.scopedExposure() * defendableMultiplier));
        double targetAsk = Math.max(defendableAsk, roundToNearest500(costProfile.scopedExposure() * targetMultiplier));
        double stretchAsk = Math.max(targetAsk, roundToNearest500(costProfile.scopedExposure() * stretchMultiplier));

        if (costProfile.broadExposure() > 0) {
            double targetCap = switch (input.loanType()) {
                case CASH -> costProfile.broadExposure() * 1.02;
                default -> costProfile.broadExposure();
            };
            double stretchCap = switch (input.loanType()) {
                case CASH -> costProfile.broadExposure() * 1.08;
                default -> costProfile.broadExposure() * 1.02;
            };
            targetAsk = Math.min(targetAsk, roundToNearest500(targetCap));
            stretchAsk = Math.min(stretchAsk, roundToNearest500(stretchCap));
            targetAsk = Math.max(defendableAsk, targetAsk);
            stretchAsk = Math.max(targetAsk, stretchAsk);
        }

        return new AskRange(defendableAsk, targetAsk, stretchAsk);
    }

    private double estimateRepairExposure(Verdict verdict) {
        if (verdict.getExactCostEstimate() != null && verdict.getExactCostEstimate() > 0) {
            return verdict.getExactCostEstimate();
        }
        CostRange range = verdict.getCostRange();
        if (range == null) {
            return 10000.0;
        }
        if (range.getMax() == Double.MAX_VALUE) {
            return Math.max(100000.0, range.getMin() * 1.15);
        }
        return (range.getMin() + range.getMax()) / 2.0;
    }

    private List<String> evidenceChecklist(InspectionResponseInput input,
            NegotiationCostProfile costProfile,
            List<String> lenderVisibleSignals) {
        List<String> checklist = new ArrayList<>();
        if (input.evidenceSourceLabel() != null && !input.evidenceSourceLabel().isBlank()) {
            checklist.add("Quote report pages from " + input.evidenceSourceLabel() + " for the lead items.");
        }
        if (!costProfile.leadLabels().isEmpty()) {
            checklist.add("Inspection report page or photo for " + joinForSentence(costProfile.leadLabels()));
        } else {
            checklist.add("Inspection report page or photo for each must-request item");
        }
        checklist.add("Clear distinction between safety/system defects and cosmetic preferences");
        if (!lenderVisibleSignals.isEmpty()) {
            checklist.add("Explicitly call out " + joinForSentence(lenderVisibleSignals.stream().limit(2).toList())
                    + " as financing, appraisal, or habitability pressure");
        }
        if ("NONE".equals(input.quoteSupport())) {
            checklist.add("Do not describe the number as quote-backed. Add one contractor quote if the seller challenges scope or cost.");
        } else if ("HAS_ONE".equals(input.quoteSupport())) {
            checklist.add("Attach the existing quote as the anchor, not as a shopping list");
        } else {
            checklist.add("Use the lowest credible quote as the defensible floor and keep the target ask intact");
        }
        if (input.loanType() == LoanType.FHA || input.loanType() == LoanType.VA) {
            checklist.add("Call out lender-visible safety or habitability risk without overpromising underwriting outcomes");
        }
        checklist.add("Response deadline and preferred seller-credit wording");
        return checklist;
    }

    private boolean hasOcrEvidence(List<InspectionEvidenceRef> matchedEvidence) {
        return matchedEvidence.stream()
                .flatMap(evidence -> evidence.citations().stream())
                .anyMatch(citation -> citation.contains("(OCR)"));
    }

    private List<InspectionDefenseSignal> buildDefenseSignals(
            InspectionResponseInput input,
            NegotiationCostProfile costProfile,
            List<InspectionEvidenceRef> matchedEvidence,
            List<String> lenderVisibleSignals,
            PacketWorkflow workflow,
            List<InspectionReadinessGate> readinessGates,
            AskRange askRange) {
        List<InspectionDefenseSignal> signals = new ArrayList<>();

        if ("MULTIPLE".equals(input.quoteSupport())) {
            signals.add(new InspectionDefenseSignal(
                    "PASS",
                    "Number basis",
                    "The ask has multiple outside quotes behind it, so the target can be defended more directly.",
                    "Keep the quotes attached to the outgoing packet."));
        } else if ("HAS_ONE".equals(input.quoteSupport())) {
            signals.add(new InspectionDefenseSignal(
                    "WARN",
                    "Number basis",
                    "One quote can support the fallback, but the opening ask still depends on scoped exposure and inspection judgment.",
                    "Use the quoted item as the floor and avoid calling the full target a contractor bid."));
        } else if (matchedEvidence.isEmpty() && askRange.targetAsk() >= 10000) {
            signals.add(new InspectionDefenseSignal(
                    "FAIL",
                    "Number basis",
                    "The ask is data-estimated, above $10,000, and has neither a contractor quote nor exhibit-level evidence attached.",
                    "Lower the send posture to internal review until report pages, photos, or a quote support the lead items."));
        } else {
            signals.add(new InspectionDefenseSignal(
                    "WARN",
                    "Number basis",
                    "The dollar figure is data-estimated from scoped exposure, not contractor-backed pricing.",
                    "Label the number as a negotiation ask, not a bid, and get a quote if the seller challenges cost."));
        }

        if (!matchedEvidence.isEmpty()) {
            signals.add(new InspectionDefenseSignal(
                    "PASS",
                    "Evidence support",
                    matchedEvidence.size() + " lead item" + (matchedEvidence.size() == 1 ? "" : "s")
                            + " are tied to report pages, scans, or uploaded evidence.",
                    "Attach only the cited pages or photos so the request stays focused."));
        } else {
            signals.add(new InspectionDefenseSignal(
                    "FAIL",
                    "Evidence support",
                    "The packet is still relying on pasted text without a clean exhibit set.",
                    "Attach report pages, photos, or a quote before treating the lead ask as file-ready."));
        }

        if (costProfile.broadExposure() - costProfile.scopedExposure() >= 1500.0) {
            signals.add(new InspectionDefenseSignal(
                    "PASS",
                    "Seller pushback",
                    "$" + formatMoney(costProfile.broadExposure() - costProfile.scopedExposure())
                            + " of broader repair exposure was kept out of the first ask.",
                    "Use the excluded list if the seller argues the request is a repair wishlist."));
        } else {
            signals.add(new InspectionDefenseSignal(
                    "WARN",
                    "Seller pushback",
                    "Most of the repair exposure is still inside the ask, so the packet may need sharper boundaries.",
                    "Confirm that every lead item is safety, system, water, financing, or structural leverage."));
        }

        if (lenderVisibleSignals.isEmpty()) {
            signals.add(new InspectionDefenseSignal(
                    "PASS",
                    "Financing boundary",
                    "No lender-visible trigger is currently leading the packet.",
                    "Keep the loan posture visible but do not overcomplicate the ask."));
        } else if (matchedEvidence.isEmpty()) {
            signals.add(new InspectionDefenseSignal(
                    "FAIL",
                    "Financing boundary",
                    "Lender-visible issues are in play without exhibit-level support.",
                    "Attach report evidence and confirm the treatment with the lender or loan officer."));
        } else {
            signals.add(new InspectionDefenseSignal(
                    "WARN",
                    "Financing boundary",
                    "Lender-visible issues are flagged, but this packet is not a lender ruling.",
                    "Get written lender or loan-officer confirmation before finalizing repair-vs-credit structure."));
        }

        if (!"AUTO".equals(input.contractWorkflow()) && !"GENERAL_AMENDMENT".equals(input.contractWorkflow())) {
            signals.add(new InspectionDefenseSignal(
                    "PASS",
                    "Form boundary",
                    "The packet is tied to " + workflow.label() + ".",
                    "Move the final terms into the actual state or brokerage form before external filing."));
        } else if ("GENERAL_AMENDMENT".equals(input.contractWorkflow())) {
            signals.add(new InspectionDefenseSignal(
                    "WARN",
                    "Form boundary",
                    "The packet is routed through a generic amendment path.",
                    "Confirm the exact state form family before sending or filing."));
        } else {
            signals.add(new InspectionDefenseSignal(
                    "WARN",
                    "Form boundary",
                    "The form path was inferred from address and entry context.",
                    "Have the agent or broker verify the contract form and deadline before send."));
        }

        boolean hasFailingGate = readinessGates.stream().anyMatch(gate -> "FAIL".equals(gate.status()))
                || signals.stream().anyMatch(signal -> "FAIL".equals(signal.status()));
        boolean hasWarningGate = readinessGates.stream().anyMatch(gate -> "WARN".equals(gate.status()))
                || signals.stream().anyMatch(signal -> "WARN".equals(signal.status()));
        signals.add(new InspectionDefenseSignal(
                hasFailingGate ? "FAIL" : hasWarningGate ? "WARN" : "PASS",
                "Send posture",
                hasFailingGate
                        ? "At least one hard gate is red, so the packet should not be sent as-is."
                        : hasWarningGate
                                ? "The packet is a reviewable draft, but a human still needs to clear the yellow gates."
                                : "All hard gates are green, so the packet is aligned for a real send path.",
                hasFailingGate
                        ? "Clear the red gates before asking the buyer to approve or the agent to send."
                        : hasWarningGate
                                ? "Use the pre-send review to decide what must be confirmed before external send."
                                : "Keep the cited evidence and final form path with the outgoing request."));

        return signals;
    }

    private String defenseTitle(PacketQuality quality) {
        return switch (quality.readinessLabel()) {
            case "Ready to send" -> "Send: ask cleared pre-send review";
            case "Not sendable" -> "Do not send yet";
            default -> "Revise before send";
        };
    }

    private String defenseSubtitle(PacketQuality quality, AskRange askRange, InspectionResponseInput input) {
        String basis = "NONE".equals(input.quoteSupport())
                ? "The number is a negotiation estimate, not a contractor quote."
                : "Quote support is present, but the final ask still needs file review.";
        return "Pre-send verdict for a $" + formatMoney(askRange.targetAsk())
                + " inspection ask. " + quality.readinessNote() + " " + basis;
    }

    private String numberBasisPhrase(InspectionResponseInput input, List<InspectionEvidenceRef> matchedEvidence) {
        if ("MULTIPLE".equals(input.quoteSupport())) {
            return "Multiple outside quotes support the number.";
        }
        if ("HAS_ONE".equals(input.quoteSupport())) {
            return "One outside quote supports the fallback, but the opening ask still needs review.";
        }
        if (!matchedEvidence.isEmpty()) {
            return "The number is a negotiation estimate backed by inspection evidence, not a contractor quote.";
        }
        return "The number is a conservative negotiation estimate and should stay in review until evidence or quote support is attached.";
    }

    private String buildFullPacketText(String city,
            InspectionResponseInput input,
            LinkedHashSet<String> mustFixNow,
            LinkedHashSet<String> verifyNext,
            LinkedHashSet<String> doNotLead,
            List<String> evidenceChecklist,
            List<String> pricingBreakdown,
            List<InspectionExclusionItem> excludedFindings,
            List<InspectionDefenseSignal> defenseSignals,
            List<String> verdictRationale,
            List<String> missingEvidence,
            List<String> reviewCaveats,
            String sellerCreditSummary,
            String agentNegotiationScript,
            String fallbackScript,
            PacketWorkflow workflow,
            AskRange askRange) {
        return String.join("\n\n",
                "INSPECTION ASK PRE-SEND CHECK",
                "Market context: " + city,
                "Reviewed ask: $" + formatMoney(askRange.targetAsk()),
                "Defensible fallback: $" + formatMoney(askRange.defendableAsk()),
                "Stretch ask: $" + formatMoney(askRange.stretchAsk()),
                "Loan posture: " + loanTypeLabel(input.loanType()),
                "Timeline: " + closingWindowLabel(input.closingWindow()),
                "Response deadline: " + responseDeadlineNote(input.closingWindow(), input.responseDeadlineAt()),
                "Summary: " + sellerCreditSummary,
                "Must request now:\n- " + String.join("\n- ", mustFixNow),
                "Verify before expanding the ask:\n- " + String.join("\n- ", verifyNext),
                "Do not lead with:\n- " + String.join("\n- ", doNotLead),
                "Excluded on purpose:\n- " + excludedFindings.stream()
                        .map(item -> item.findingLabel() + " - " + item.reason())
                        .collect(Collectors.joining("\n- ")),
                "Why this verdict:\n- " + String.join("\n- ", verdictRationale),
                "Pricing basis:\n- " + String.join("\n- ", pricingBreakdown),
                "Pre-send review:\n- " + defenseSignals.stream()
                        .map(item -> item.label() + " [" + item.status() + "]: " + item.detail() + " Action: "
                                + item.action())
                        .collect(Collectors.joining("\n- ")),
                "Missing or weak evidence:\n- " + String.join("\n- ", missingEvidence),
                "Caveats before send:\n- " + String.join("\n- ", reviewCaveats),
                "Evidence checklist:\n- " + String.join("\n- ", evidenceChecklist),
                workflow.title() + ":\n- " + String.join("\n- ", workflow.steps()),
                "Agent message:\n" + agentNegotiationScript,
                "Fallback:\n" + fallbackScript);
    }

    private String quoteSupportLabel(String quoteSupport) {
        return switch (quoteSupport) {
            case "HAS_ONE" -> "one outside quote already supports the ask";
            case "MULTIPLE" -> "multiple contractor quotes support the ask";
            default -> "no quote attached; negotiation estimate only";
        };
    }

    private String loanTypeLabel(LoanType loanType) {
        return switch (loanType) {
            case FHA -> "FHA financing";
            case VA -> "VA financing";
            case CASH -> "cash offer";
            case INVESTOR -> "investor financing";
            default -> "conventional financing";
        };
    }

    private String closingWindowLabel(String closingWindow) {
        return switch (closingWindow) {
            case "UNDER_7_DAYS" -> "closing inside 7 days";
            case "SEVEN_TO_TWENTY_ONE_DAYS" -> "closing in the next 1-3 weeks";
            case "TWENTY_ONE_TO_FORTY_FIVE_DAYS" -> "closing in the next 3-6 weeks";
            default -> "a flexible closing timeline";
        };
    }

    private String loanTypeNote(LoanType loanType) {
        return switch (loanType) {
            case FHA -> "Keep the request anchored to safety, habitability, and lender-visible defects.";
            case VA -> "Lead with safety and system issues that could slow appraisal or underwriting review.";
            case CASH -> "Without lender overlays, the ask can be broader, but it still needs defect-based evidence.";
            case INVESTOR -> "Tie the request to rent-ready systems and near-term capex, not cosmetic polish.";
            default -> "The strongest conventional-loan leverage still comes from documented defects, not preferences.";
        };
    }

    private String responseDeadlineNote(String closingWindow, String responseDeadlineAt) {
        if (responseDeadlineAt != null && !responseDeadlineAt.isBlank()) {
            return "Contract response deadline: " + formatResponseDeadline(responseDeadlineAt)
                    + ". Treat this as the live cutoff before the file loses leverage or needs a different path.";
        }
        return switch (closingWindow) {
            case "UNDER_7_DAYS" -> "Use the short deadline to keep the request narrow and easy to accept.";
            case "SEVEN_TO_TWENTY_ONE_DAYS" -> "You have enough room to ask clearly, but not enough time for vague repair promises.";
            case "TWENTY_ONE_TO_FORTY_FIVE_DAYS" -> "Use the longer runway to gather one quote if the seller challenges the number.";
            default -> "Treat the packet as a decision filter before spending time on quotes.";
        };
    }

    private String formatResponseDeadline(String responseDeadlineAt) {
        try {
            return LocalDateTime.parse(responseDeadlineAt, DEADLINE_INPUT_FORMATTER).format(DEADLINE_DISPLAY_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return responseDeadlineAt;
        }
    }

    private String evidenceNote(String quoteSupport) {
        return switch (quoteSupport) {
            case "HAS_ONE" -> "One quote is enough to defend the fallback and keep the target ask credible.";
            case "MULTIPLE" -> "Multiple quotes strengthen the ask; do not let the seller reframe it as cosmetic negotiation.";
            default -> "Do not call this a contractor quote. Use it as a negotiation estimate until report pages, photos, or a quote support the lead items.";
        };
    }

    private List<String> detectLenderVisibleSignals(List<String> sourceFindings, List<NegotiationAnchor> leadAnchors) {
        List<String> combinedTexts = new ArrayList<>(sourceFindings);
        leadAnchors.stream().map(NegotiationAnchor::label).forEach(combinedTexts::add);

        List<String> signals = new ArrayList<>();
        for (LenderVisibleRule rule : LENDER_VISIBLE_RULES) {
            boolean matched = combinedTexts.stream().anyMatch(text -> matchesLenderVisibleRule(text, rule))
                    || leadAnchors.stream().anyMatch(anchor -> matchesLenderVisibleRule(anchor, rule));
            if (matched) {
                signals.add(rule.label());
            }
        }
        return signals;
    }

    private boolean matchesLenderVisibleRule(String text, LenderVisibleRule rule) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String normalized = text.toLowerCase(Locale.ENGLISH);
        for (String fragment : rule.fragments()) {
            if (normalized.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesLenderVisibleRule(NegotiationAnchor anchor, LenderVisibleRule rule) {
        for (String tag : rule.tags()) {
            if (anchor.tags().contains(tag)) {
                return true;
            }
        }
        return matchesLenderVisibleRule(anchor.label(), rule);
    }

    private String buildLenderVisibleNote(LoanType loanType, List<String> lenderVisibleSignals) {
        if (lenderVisibleSignals.isEmpty()) {
            return switch (loanType) {
                case FHA, VA -> "No explicit lender-visible trigger was detected from the pasted findings, so keep the request narrow and evidence-based.";
                case CASH -> "No financing trigger was detected, so the packet stays focused on negotiation leverage rather than underwriting pressure.";
                default -> "No explicit financing trigger was detected, so the packet leans on repair scope and inspection evidence.";
            };
        }

        return switch (loanType) {
            case FHA -> "These items can read as lender-visible habitability or appraisal issues, so they should stay in the lead request.";
            case VA -> "These items can slow VA appraisal or underwriting review, so they belong in the first response packet.";
            case CASH -> "There is no lender overlay here, but these are still objective deal-risk items the seller cannot dismiss as cosmetic.";
            case INVESTOR -> "These items read as rent-ready or near-term capex pressure, so they should stay ahead of cosmetic requests.";
            default -> "These items are strong objective defects, and they can affect lender, insurer, or inspection posture if ignored.";
        };
    }

    private boolean containsAny(String value, Set<String> fragments) {
        for (String fragment : fragments) {
            if (value.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    private String inferStateCode(InspectionResponseInput input) {
        String fromAddress = inferStateCodeFromText(input.propertyAddress());
        if (!fromAddress.isBlank()) {
            return fromAddress;
        }
        return inferStateCodeFromText(input.marketContextLabel());
    }

    private String inferStateCodeFromText(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return "";
        }
        java.util.regex.Matcher matcher = STATE_CODE_PATTERN.matcher(rawValue.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }

        String normalized = rawValue.toLowerCase(Locale.ENGLISH).trim();
        for (Map.Entry<String, String> entry : STATE_NAME_TO_CODE.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "";
    }

    private String cleanFindingLabel(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        String cleaned = HTML_TAG_PATTERN.matcher(rawValue).replaceAll(" ");
        cleaned = BULLET_PREFIX_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = cleaned.replace("Primary Cost Driver:", "");
        cleaned = cleaned.replace('\u00a0', ' ');
        cleaned = MULTI_SPACE_PATTERN.matcher(cleaned).replaceAll(" ").trim();
        return cleaned;
    }

    private Set<String> extractComponentSignals(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Set.of();
        }
        String normalized = normalizeForMatch(rawValue);
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        COMPONENT_SIGNALS.forEach((tag, fragments) -> {
            for (String fragment : fragments) {
                if (normalized.contains(fragment)) {
                    tags.add(tag);
                    break;
                }
            }
        });
        return tags;
    }

    private Set<String> extractMeaningfulTokens(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Set.of();
        }
        String normalized = normalizeForMatch(rawValue);
        LinkedHashSet<String> tokens = new LinkedHashSet<>();
        for (String token : NON_ALNUM_PATTERN.split(normalized)) {
            if (token.isBlank()) {
                continue;
            }
            if (token.length() <= 2 && !"hvac".equals(token) && !"fpe".equals(token)) {
                continue;
            }
            if (MATCH_STOPWORDS.contains(token)) {
                continue;
            }
            tokens.add(token);
        }
        return tokens;
    }

    private String normalizeForMatch(String rawValue) {
        return NON_ALNUM_PATTERN.matcher(rawValue.toLowerCase(Locale.ENGLISH)).replaceAll(" ").trim();
    }

    private String joinForSentence(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "the highest-risk inspection items";
        }
        if (values.size() == 1) {
            return values.get(0);
        }
        if (values.size() == 2) {
            return values.get(0) + " and " + values.get(1);
        }
        return String.join(", ", values.subList(0, values.size() - 1)) + ", and " + values.get(values.size() - 1);
    }

    private List<RiskAdjustedItem> planMustDoItems(Verdict verdict) {
        if (verdict.getPlan() == null || verdict.getPlan().getMustDo() == null) {
            return List.of();
        }
        return verdict.getPlan().getMustDo();
    }

    private List<RiskAdjustedItem> planShouldDoItems(Verdict verdict) {
        if (verdict.getPlan() == null || verdict.getPlan().getShouldDo() == null) {
            return List.of();
        }
        return verdict.getPlan().getShouldDo();
    }

    private double roundToNearest500(double value) {
        return Math.round(value / 500.0) * 500.0;
    }

    private String formatMoney(double value) {
        return String.format("%,.0f", value);
    }

    private String encodeMarkerValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String decodeMarkerValue(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String serializeEvidenceMarker(InspectionEvidenceRef evidenceRef) {
        try {
            return MARKER_OBJECT_MAPPER.writeValueAsString(new StoredEvidenceMarker(
                    cleanFindingLabel(evidenceRef.findingLabel()),
                    evidenceRef.citations()));
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private InspectionEvidenceRef deserializeEvidenceMarker(String rawValue) {
        try {
            StoredEvidenceMarker marker = MARKER_OBJECT_MAPPER.readValue(rawValue, StoredEvidenceMarker.class);
            return new InspectionEvidenceRef(marker.findingLabel(), marker.citations());
        } catch (Exception e) {
            return null;
        }
    }

    private enum FindingBucket {
        MUST_REQUEST,
        VERIFY,
        DO_NOT_LEAD
    }

    private record AskRange(double defendableAsk, double targetAsk, double stretchAsk) {
    }

    private record PacketQuality(String readinessLabel,
            String readinessNote,
            int confidenceScore,
            List<String> confidenceReasons) {
    }

    private record PacketWorkflow(String label,
            String title,
            String note,
            List<String> steps) {
    }

    private record NegotiationAnchor(String label,
            double cost,
            boolean hardLead,
            boolean coreLead,
            boolean matchedFinding,
            Set<String> tags,
            Set<String> tokens) {
    }

    private record NegotiationCostProfile(double broadExposure,
            double leadExposure,
            double verifyReserve,
            double scopedExposure,
            int leadAnchorCount,
            int matchedLeadCount,
            int hardLeadCount,
            List<String> leadLabels,
            List<String> verifyLabels,
            List<String> pricingBreakdown,
            String dataAnchor,
            String dataAnchorNote) {
    }

    private record LenderVisibleRule(String label, Set<String> fragments, Set<String> tags) {
    }

    private record StoredEvidenceMarker(String findingLabel, List<String> citations) {
    }
}
