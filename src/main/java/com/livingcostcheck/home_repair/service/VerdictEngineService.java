package com.livingcostcheck.home_repair.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping.*;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerdictEngineService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private MetroMasterData metroMasterData;
    private RiskFactorsData riskFactorsData;
    private CostLibraryData costLibraryData;

    public MetroMasterData getMetroMasterData() {
        return metroMasterData;
    }

    @PostConstruct
    public void loadData() {
        try {
            metroMasterData = loadJson("classpath:data/2026_US_Metro_Master_Data.json", MetroMasterData.class);
            riskFactorsData = loadJson("classpath:data/risk_factors_by_year.json", RiskFactorsData.class);
            costLibraryData = loadJson("classpath:data/2026_Integrated_Construction_Cost_Library.json",
                    CostLibraryData.class);
            log.info("VerdictEngine Data Loaded Successfully.");
        } catch (Exception e) {
            log.error("Failed to load VerdictEngine Data", e);
            throw new RuntimeException("Engine Data Load Failure", e);
        }
    }

    private <T> T loadJson(String path, Class<T> clazz) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        return objectMapper.readValue(resource.getInputStream(), clazz);
    }

    public Verdict generateVerdict(UserContext context) {
        // === PHASE 1: STRATEGY ELIGIBILITY CHECK (NEW) ===
        // Check eligibility BEFORE cost calculation to prevent $0 verdicts

        List<StrategyEligibility> allEligibilities = Arrays.asList(
                evaluateEligibility(StrategyType.SAFETY_FLIP, context),
                evaluateEligibility(StrategyType.STANDARD_LIVING, context),
                evaluateEligibility(StrategyType.FOREVER_HOME, context));

        // Select best eligible strategy
        StrategyEligibility chosenEligibility = selectBestEligibleStrategy(allEligibilities, context);

        // If NO strategy is eligible, return INSUFFICIENT_DATA verdict
        if (chosenEligibility == null) {
            return buildInsufficientDataVerdict(allEligibilities, context);
        }

        log.info("Strategy Selected | chosen={} era={} metro={} relationship={}",
                chosenEligibility.getStrategyType(), context.getEra(),
                context.getMetroCode(), context.getRelationship());

        // === PHASE 2: COST CALCULATION (Only for eligible strategy) ===
        // Common Steps (0-2)
        List<BaseCostItem> candidates = step0_candidateGenerator(context);
        EstimatedScale scale = step2_autoScale(context);

        // Optimization: Step 3 & 4 are strategy-agnostic - run them ONCE
        List<BaseCostItem> costedItems = step3_preliminaryCosting(candidates, scale);

        // Pass exclusionNotes list to be populated during filtering
        List<String> exclusionNotes = new ArrayList<>();
        List<RiskAdjustedItem> baseRiskAdjustedItems = step4_riskFilter(costedItems, context, exclusionNotes);

        // Generate ONLY the chosen eligible strategy
        StrategyOption chosenOption = generateStrategyOption(
                chosenEligibility.getStrategyType(),
                baseRiskAdjustedItems,
                context);

        // Also generate SAFETY_FLIP for minimum cost calculation (if different from
        // chosen)
        StrategyOption safetyOption = chosenOption;
        if (chosenEligibility.getStrategyType() != StrategyType.SAFETY_FLIP) {
            // Check if SAFETY_FLIP is eligible - if not, use chosen strategy as minimum
            Optional<StrategyEligibility> safetyEligibility = allEligibilities.stream()
                    .filter(e -> e.getStrategyType() == StrategyType.SAFETY_FLIP && e.isEligible())
                    .findFirst();

            if (safetyEligibility.isPresent()) {
                safetyOption = generateStrategyOption(StrategyType.SAFETY_FLIP, baseRiskAdjustedItems, context);
            }
        }

        // === PHASE 3: VERDICT DETERMINATION ===
        double minRequired = safetyOption.getTotalCost();
        double budget = context.getBudget();
        String tier = "DENIED";
        String headline = "";

        // CRITICAL: minRequired should NEVER be 0 if we reached this point
        // If it is 0, that's a BUG - eligibility check should have caught it
        if (minRequired == 0.0) {
            log.error("BUG: minRequired = 0 despite eligible strategy | strategy={} era={} metro={}",
                    chosenEligibility.getStrategyType(), context.getEra(), context.getMetroCode());
            // Defensive: Return INSUFFICIENT_DATA rather than mislead with LOW_RISK
            return buildInsufficientDataVerdict(allEligibilities, context);
        }

        if (budget >= minRequired) {
            tier = "LOW_RISK";
            headline = String.format(
                    "Sufficient budget to cover critical code-mandatory repairs ($%,.0f). Financial risk is manageable.",
                    safetyOption.getTotalCost());
        } else if (budget >= (minRequired * 0.9)) {
            tier = "CONDITIONAL";
            headline = "Budget is tight for minimum safety repairs. High risk of incomplete remediation.";
        } else {
            tier = "HIGH_FINANCIAL_RISK";
            headline = "Budget insufficient for required safety repairs at 2026 rates. Significant financial exposure.";
        }

        // Select Plan for Display
        SortedPlan displayPlan = chosenOption.getPlan();

        // Build strategy explanation for transparency
        String strategyExplanation = buildStrategyExplanation(allEligibilities, chosenEligibility);

        List<String> skippedStrategies = allEligibilities.stream()
                .filter(e -> !e.isEligible())
                .map(e -> e.getStrategyType().name() + ": " + e.getExplanation())
                .collect(Collectors.toList());

        // Build final verdict
        return Verdict.builder()
                .tier(tier)
                .headline(headline)
                .strategyUsed(chosenEligibility.getStrategyType().name())
                .strategyExplanation(strategyExplanation)
                .skippedStrategies(skippedStrategies)
                .strategyOptions(Collections.emptyList()) // Hide strategies to enforce Single Verdict UI
                .exclusionNote(exclusionNotes)
                .plan(displayPlan)
                // YMYL-Safe Cost Presentation
                .costRange(VerdictDTOs.CostRange.fromCost(minRequired))
                .costRangeLabel(VerdictDTOs.CostRange.fromCost(minRequired).getLabel() +
                        " (" + VerdictDTOs.CostRange.fromCost(minRequired).getFormattedRange() + " typical range)")
                .itemsAnalyzed(candidates.size())
                .exactCostEstimate(minRequired)
                .mustDoExplanation(Collections.emptyList())
                .optionalActions(Collections.emptyList())
                .futureCostWarning(Collections.emptyList())
                .upgradeScenario(Collections.emptyList())
                // Deal Killer Logic
                .isDealKiller(isDealKiller(context))
                .dealKillerMessage(getDealKillerMessage(context))
                // Context Briefing
                .contextBriefing(buildContextBriefing(context))
                .build();
    }

    private boolean isDealKiller(UserContext context) {
        return Boolean.TRUE.equals(context.getIsChineseDrywall()) ||
                Boolean.TRUE.equals(context.getIsFpePanel()) ||
                Boolean.TRUE.equals(context.getIsPolyB());
    }

    private String getDealKillerMessage(UserContext context) {
        if (Boolean.TRUE.equals(context.getIsChineseDrywall()))
            return "CRITICAL ALERT: Defective Chinese Drywall Detected. This is a Transaction-Ending Defect.";
        if (Boolean.TRUE.equals(context.getIsFpePanel()))
            return "CRITICAL ALERT: Federal Pacific/Zinsco Panel. High Fire Risk - Uninsurable.";
        if (Boolean.TRUE.equals(context.getIsPolyB()))
            return "CRITICAL ALERT: Polybutylene Plumbing. Structural Flood Risk.";
        return null;
    }

    public VerdictDTOs.ContextBriefing getPrecalcBriefing(String metro, String era) {
        UserContext minimalContext = UserContext.builder()
                .metroCode(metro)
                .era(era)
                .build();
        return buildContextBriefing(minimalContext);
    }

    private VerdictDTOs.ContextBriefing buildContextBriefing(UserContext context) {
        MetroCityData city = metroMasterData.getData().get(context.getMetroCode());
        String laborRateDesc = String.format("Local Labor: %.0f%% of National Avg", city.getLaborMult() * 100);

        // Era Feature Logic
        String eraFeature = "Standard Construction Era";
        if (context.getEra().contains("1920") || context.getEra().contains("PRE_1950"))
            eraFeature = "Era Risk: Knob & Tube Wiring / Lead Paint";
        else if (context.getEra().contains("1970"))
            eraFeature = "Era Risk: Aluminum Wiring / Asbestos";
        else if (context.getEra().contains("2000"))
            eraFeature = "Era Risk: Synthetic Stucco / Chinese Drywall";

        return VerdictDTOs.ContextBriefing.builder()
                .regionalRisk(city.getRisk())
                .foundationType(city.getFoundation())
                .laborMarketRate(laborRateDesc)
                .eraFeature(eraFeature)
                .disclaimer("This is a contextual signal, not a full inspection.")
                .build();
    }

    // === PHASE 1: STRATEGY ELIGIBILITY LAYER ===
    // NEW: Check eligibility BEFORE cost calculation to prevent $0 verdicts

    /**
     * Evaluate eligibility for a given strategy based on available data.
     * This prevents strategies from executing cost calculations when data is
     * insufficient.
     */
    private StrategyEligibility evaluateEligibility(StrategyType strategyType, UserContext context) {
        switch (strategyType) {
            case SAFETY_FLIP:
                return evaluateSafetyEligibility(context);
            case STANDARD_LIVING:
                return evaluateStandardEligibility(context);
            case FOREVER_HOME:
                return evaluateForeverHomeEligibility(context);
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategyType);
        }
    }

    /**
     * SAFETY_FLIP requires era-specific critical risk data.
     * This is the most strict strategy as it focuses on code-mandatory repairs.
     */
    private StrategyEligibility evaluateSafetyEligibility(UserContext context) {
        List<String> missing = new ArrayList<>();

        // Check 1: Era risk data exists and has critical risks defined
        EraData eraData = riskFactorsData.getEras().get(context.getEra());
        if (eraData == null || eraData.getCriticalRisks() == null || eraData.getCriticalRisks().isEmpty()) {
            missing.add("criticalRisks for era " + context.getEra());
        }

        // Check 2: Metro data exists for localization
        if (!metroMasterData.getData().containsKey(context.getMetroCode())) {
            missing.add("metro localization data for " + context.getMetroCode());
        }

        boolean eligible = missing.isEmpty();
        double coverage = eligible ? 1.0 : 0.0;

        String explanation = eligible
                ? "Sufficient data for safety-only analysis"
                : "Safety-only analysis requires " + String.join(", ", missing);

        log.info("SAFETY_FLIP Eligibility | era={} metro={} eligible={} missing={}",
                context.getEra(), context.getMetroCode(), eligible, missing);

        return StrategyEligibility.builder()
                .strategyType(StrategyType.SAFETY_FLIP)
                .eligible(eligible)
                .coverageScore(coverage)
                .missingFactors(missing)
                .explanation(explanation)
                .build();
    }

    /**
     * STANDARD_LIVING is more lenient - only needs cost library and metro data.
     * It can work without era-specific risk data by using general assumptions.
     */
    private StrategyEligibility evaluateStandardEligibility(UserContext context) {
        List<String> missing = new ArrayList<>();

        // Check 1: Cost library exists
        if (costLibraryData == null || costLibraryData.getConstructionItemLibrary() == null
                || costLibraryData.getConstructionItemLibrary().isEmpty()) {
            missing.add("construction cost library");
        }

        // Check 2: Metro data exists
        if (!metroMasterData.getData().containsKey(context.getMetroCode())) {
            missing.add("metro localization data for " + context.getMetroCode());
        }

        boolean eligible = missing.isEmpty();
        double coverage = eligible ? 1.0 : 0.0;

        String explanation = eligible
                ? "Standard analysis available with general cost estimates"
                : "Standard analysis requires " + String.join(", ", missing);

        return StrategyEligibility.builder()
                .strategyType(StrategyType.STANDARD_LIVING)
                .eligible(eligible)
                .coverageScore(coverage)
                .missingFactors(missing)
                .explanation(explanation)
                .build();
    }

    /**
     * FOREVER_HOME has same requirements as STANDARD_LIVING for now.
     * Future: May require additional data for premium recommendations.
     */
    private StrategyEligibility evaluateForeverHomeEligibility(UserContext context) {
        // For now, FOREVER_HOME has the same requirements as STANDARD_LIVING
        StrategyEligibility standardEligibility = evaluateStandardEligibility(context);

        return StrategyEligibility.builder()
                .strategyType(StrategyType.FOREVER_HOME)
                .eligible(standardEligibility.isEligible())
                .coverageScore(standardEligibility.getCoverageScore())
                .missingFactors(standardEligibility.getMissingFactors())
                .explanation(standardEligibility.getExplanation().replace("Standard", "Forever Home"))
                .build();
    }

    /**
     * Select the best eligible strategy based on user context.
     * Preference order: SAFETY_FLIP (if BUYING) > STANDARD_LIVING > FOREVER_HOME
     */
    private StrategyEligibility selectBestEligibleStrategy(
            List<StrategyEligibility> eligibilities,
            UserContext context) {

        // Filter to only eligible strategies
        List<StrategyEligibility> eligible = eligibilities.stream()
                .filter(StrategyEligibility::isEligible)
                .collect(Collectors.toList());

        if (eligible.isEmpty()) {
            return null; // No eligible strategy
        }

        // For BUYING users, prefer SAFETY_FLIP if available
        if (context.getRelationship() == RelationshipToHouse.BUYING) {
            Optional<StrategyEligibility> safety = eligible.stream()
                    .filter(e -> e.getStrategyType() == StrategyType.SAFETY_FLIP)
                    .findFirst();
            if (safety.isPresent()) {
                return safety.get();
            }
        }

        // For LIVING/INVESTING users, prefer STANDARD_LIVING
        Optional<StrategyEligibility> standard = eligible.stream()
                .filter(e -> e.getStrategyType() == StrategyType.STANDARD_LIVING)
                .findFirst();
        if (standard.isPresent()) {
            return standard.get();
        }

        // Fallback to any eligible strategy
        return eligible.get(0);
    }

    /**
     * Build explanation for strategy selection to show in verdict.
     */
    private String buildStrategyExplanation(
            List<StrategyEligibility> allEligibilities,
            StrategyEligibility chosen) {

        StringBuilder explanation = new StringBuilder();
        explanation.append("Analysis Strategy: ")
                .append(chosen.getStrategyType().name())
                .append(". ");

        // Explain why others were skipped
        List<StrategyEligibility> skipped = allEligibilities.stream()
                .filter(e -> !e.isEligible())
                .collect(Collectors.toList());

        if (!skipped.isEmpty()) {
            explanation.append("Skipped strategies: ");
            for (StrategyEligibility skip : skipped) {
                explanation.append(skip.getStrategyType().name())
                        .append(" (")
                        .append(skip.getExplanation())
                        .append("); ");
            }
        }

        return explanation.toString();
    }

    /**
     * Build verdict when no strategy is eligible due to insufficient data.
     * This is an HONEST response - we don't guess when we don't have data.
     */
    private Verdict buildInsufficientDataVerdict(
            List<StrategyEligibility> eligibilities,
            UserContext context) {

        // Collect all missing factors
        Set<String> allMissing = eligibilities.stream()
                .flatMap(e -> e.getMissingFactors().stream())
                .collect(Collectors.toSet());

        String headline = String.format(
                "Cannot provide reliable cost assessment for %s-era homes in %s at this time.",
                context.getEra(),
                context.getMetroCode());

        List<String> explanations = Arrays.asList(
                "Our assessment requires: " + String.join(", ", allMissing),
                "This does not mean there are no costs - we simply lack sufficient data to make an accurate estimate.",
                "We recommend consulting a local licensed contractor for a professional inspection.");

        List<String> skipped = eligibilities.stream()
                .map(e -> e.getStrategyType().name() + ": " + e.getExplanation())
                .collect(Collectors.toList());

        log.warn("INSUFFICIENT_DATA verdict | era={} metro={} allMissing={}",
                context.getEra(), context.getMetroCode(), allMissing);

        return Verdict.builder()
                .tier("INSUFFICIENT_DATA")
                .headline(headline)
                .strategyUsed("NONE")
                .strategyExplanation("No analysis strategy could be executed with available data")
                .skippedStrategies(skipped)
                .plan(SortedPlan.builder()
                        .mustDo(Collections.emptyList())
                        .shouldDo(Collections.emptyList())
                        .skipForNow(Collections.emptyList())
                        .build())
                .costRange(null)
                .costRangeLabel("Unable to estimate")
                .itemsAnalyzed(0)
                .exactCostEstimate(0.0)
                .mustDoExplanation(explanations)
                .exclusionNote(Collections.emptyList())
                .optionalActions(Collections.emptyList())
                .futureCostWarning(Collections.emptyList())
                .upgradeScenario(Collections.emptyList())
                .strategyOptions(Collections.emptyList())
                .build();
    }

    /**
     * Generate a single strategic option based on strategy type
     */
    private StrategyOption generateStrategyOption(
            StrategyType strategyType,
            List<RiskAdjustedItem> riskAdjustedItems,
            UserContext context) {
        // Step 5: Strategic Filtering (replaces priority ranking)
        SortedPlan plan = step5_strategicFiltering(riskAdjustedItems, context, strategyType);

        // Calculate total cost
        double totalCost = plan.getMustDo().stream()
                .mapToDouble(RiskAdjustedItem::getAdjustedCost)
                .sum();

        // For FOREVER_HOME (and others if applicable), 'Should Do' items are part of
        // the investment plan
        if (!plan.getShouldDo().isEmpty()) {
            totalCost += plan.getShouldDo().stream()
                    .mapToDouble(RiskAdjustedItem::getAdjustedCost)
                    .sum();
        }

        // Build strategy-specific metadata
        String name = "";
        String description = "";
        String goal = "";
        String materialGrade = "";
        List<String> includedCategories = new ArrayList<>();
        List<String> keyHighlights = new ArrayList<>();

        switch (strategyType) {
            case SAFETY_FLIP:
                name = "Code & Safety Baseline";
                description = "Minimum cost to pass inspection and remove liability";
                goal = "Risk Remediation Only";
                materialGrade = "Code-Minimum (Safety & Legal)";
                includedCategories = Arrays.asList("SAFETY", "CRITICAL", "MANDATORY");
                keyHighlights = Arrays.asList(
                        "Only inspection-mandatory items",
                        "Standard materials, code-compliant",
                        "Minimum to avoid buyer walkaway");
                break;
            case STANDARD_LIVING:
                name = "Functional Living Standards";
                description = "Stable living conditions for 5-7 years";
                goal = "Habitability & System Stability";
                materialGrade = "All Essential Systems (Safety + Mechanical + Structural)";
                includedCategories = Arrays.asList("SAFETY", "STRUCTURAL", "MECHANICAL", "FUNCTIONAL");
                keyHighlights = Arrays.asList(
                        "All safety + essential systems",
                        "Standard contractor materials",
                        "Balanced functionality");
                break;
            case FOREVER_HOME:
                name = "Asset Protection Plan";
                description = "Long-term structural preservation";
                goal = "Minimize Future CapEx & Depreciation";
                materialGrade = "Asset-Grade (Durability Focused)";
                includedCategories = Arrays.asList("ALL");
                keyHighlights = Arrays.asList(
                        "Cosmetic & Asset preservation",
                        "Durability-focused materials",
                        "Minimize resale friction");
                break;
        }

        // Generate negotiation copy for CRITICAL items
        String negotiationCopy = generateNegotiationCopy(plan, context); // Context passes leverage implicitly via logic
                                                                         // inside or we calc here

        // Calculate Negotiation Leverage (1.5x of Critical/Risk Items)
        double leverageBase = plan.getMustDo().stream()
                .filter(i -> i.getRiskFlags().stream().anyMatch(f -> f.contains("CRITICAL") || f.contains("ERA_RISK")))
                .mapToDouble(RiskAdjustedItem::getAdjustedCost)
                .sum();
        double leverage = leverageBase * 1.5;

        return StrategyOption.builder()
                .strategyType(strategyType)
                .name(name)
                .description(description)
                .goal(goal)
                .totalCost(totalCost)
                .plan(plan)
                .includedCategories(includedCategories)
                .materialGrade(materialGrade)
                .keyHighlights(keyHighlights)
                .negotiationCopy(negotiationCopy)
                .negotiationLeverage(leverage)
                .build();
    }

    // --- STEP 0: Candidate Generator ---
    private List<BaseCostItem> step0_candidateGenerator(UserContext context) {
        List<BaseCostItem> candidates = new ArrayList<>();

        // 1. Add All Standard Library Items
        if (costLibraryData.getConstructionItemLibrary() != null) {
            costLibraryData.getConstructionItemLibrary().forEach((category, items) -> {
                items.forEach((key, item) -> {
                    candidates.add(BaseCostItem.builder()
                            .itemCode(key)
                            .category(category)
                            .description(item.getDescription())
                            // Placeholders for calculation
                            .rawData(Map.of("itemDef", item))
                            .build());
                });
            });
        }

        return candidates;
    }

    // --- STEP 1 & 2: Automated Scale & Localization ---
    private EstimatedScale step2_autoScale(UserContext context) {
        if (!metroMasterData.getData().containsKey(context.getMetroCode())) {
            throw new IllegalArgumentException("Invalid Metro Code: " + context.getMetroCode());
        }
        MetroCityData city = metroMasterData.getData().get(context.getMetroCode());

        // Use User Input if available, else fallback to Metro Avg
        double avgHouse = (context.getSqft() != null && context.getSqft() > 0)
                ? context.getSqft()
                : (city.getAvgHouse() != null ? city.getAvgHouse() : 2000.0);

        double avgLot = city.getAvgLot() != null ? city.getAvgLot() : 8000.0;

        return EstimatedScale.builder()
                .roofingSquares((avgHouse * 1.15) / 100.0)
                .hvacTons(avgHouse / 500.0)
                .interiorSqft(avgHouse / 1.5)
                .exteriorSqft(Math.max(0, avgLot - avgHouse))
                .laborMult(city.getLaborMult())
                .matLogistics(city.getMatLogistics())
                .mobFee(city.getMobFee())
                .dispTax(city.getDispTax())
                .avgHouseSqft(avgHouse)
                .build();
    }

    // --- STEP 3: Preliminary Costing ---
    private List<BaseCostItem> step3_preliminaryCosting(List<BaseCostItem> candidates, EstimatedScale scale) {
        List<BaseCostItem> results = new ArrayList<>();

        for (BaseCostItem candidate : candidates) {
            ConstructionItem itemDef = (ConstructionItem) candidate.getRawData().get("itemDef");

            // Determine Quantity
            double quantity = 0.0;
            switch (itemDef.getMeasureUnit()) {
                case "SQUARE":
                    if (candidate.getItemCode().contains("ROOF")) {
                        quantity = scale.getRoofingSquares();
                    } else if (candidate.getItemCode().contains("SIDING")) {
                        // Wall Area Approx = Floor Sqft * 1.2 (assuming single story box) / 100
                        quantity = (scale.getInteriorSqft() * 1.2) / 100.0;
                    } else {
                        quantity = scale.getInteriorSqft() / 100.0;
                    }
                    break; // Roofing/Siding 100 sqft unit
                case "UNIT":
                    quantity = 1.0;
                    break; // Default unit
                case "SQFT":
                    // Guessing logic based on category
                    if (candidate.getCategory().contains("INTERIOR") && candidate.getItemCode().contains("FLOOR")) {
                        quantity = scale.getInteriorSqft() * 0.85; // 85% coverage
                    } else if (candidate.getItemCode().contains("DECK")) {
                        quantity = 400.0; // Reasonable fixed size for deck (not whole lot!)
                    } else if (candidate.getCategory().contains("LANDSCAPING")) {
                        quantity = scale.getExteriorSqft(); // Yard work uses lot size
                    } else {
                        quantity = scale.getInteriorSqft(); // Fallback
                    }
                    break;
                case "ACRE":
                    quantity = Math.max(0.1, scale.getExteriorSqft() / 43560.0); // Convert sqft to acres, min 0.1
                    break;
                case "SQFT_WALL":
                    if (candidate.getItemCode().contains("EXTERIOR")) {
                        quantity = scale.getInteriorSqft() * 1.2; // Exterior wall approx
                    } else {
                        quantity = scale.getInteriorSqft() * 3.5; // Interior wall + ceiling (more accurate)
                    }
                    break;
                case "LF":
                    if (candidate.getItemCode().contains("CABINET")) {
                        quantity = 35.0; // Avg kitchen cabinet run
                    } else if (candidate.getItemCode().contains("GUTTER")) {
                        quantity = Math.sqrt(scale.getInteriorSqft()) * 4.0 * 1.15; // Perimeter + waste
                    } else {
                        quantity = scale.getExteriorSqft() * 0.1;
                    }
                    break; // Very rough
                case "EACH":
                    if (candidate.getItemCode().contains("WINDOW")) {
                        quantity = 12.0;
                    } else if (candidate.getItemCode().contains("DOOR")) {
                        quantity = 8.0;
                    } else if (candidate.getItemCode().contains("BATH")) {
                        quantity = 2.0; // 2.0 bathrooms average
                    } else if (candidate.getItemCode().contains("PANEL")) {
                        quantity = 1.0;
                    } else {
                        quantity = 1.0; // Safety default
                    }
                    break;
                default:
                    quantity = 1.0;
            }

            // Special overrides for known items
            if ("HVAC_HEAT_PUMP_CENTRAL".equals(candidate.getItemCode()))
                quantity = 1.0; // SPEC Line 30: HVAC is ONE system (tonnage is capacity, not units)
            if ("ROOFING_ASPHALT_ARCHITECTURAL".equals(candidate.getItemCode()))
                quantity = scale.getRoofingSquares();
            if ("PLUMBING_WHOLE_HOUSE_REPIPE".equals(candidate.getItemCode()))
                quantity = Math.max(150, scale.getInteriorSqft() * 0.15); // Min 150 LF per JSON min_project_size

            // --- 1. Small Job Penalty Logic (JSON Compliance) ---
            double penaltyMult = 1.0;
            Double minSize = itemDef.getMinProjectSize();
            Double shortMult = itemDef.getShortOrderMultiplier();
            if (minSize != null && quantity < minSize && shortMult != null) {
                penaltyMult = shortMult;
            }

            // Material Cost - SPEC Line 36: ALWAYS avg(low, high)
            // Strategy = Scope (what work), NOT Grade (material quality)
            // Material quality differences = different items in JSON (Asphalt vs Metal)
            double matBase = (itemDef.getMaterialCostRange().getLow() +
                    itemDef.getMaterialCostRange().getHigh()) / 2.0;
            double matCost = matBase * scale.getMatLogistics() * quantity;

            // Labor Cost (penalty will be applied to total subtotal, not here)
            double laborRate = itemDef.getBaseLaborRateNational() * scale.getLaborMult();
            double laborCost = itemDef.getLaborHoursPerUnit() * laborRate * quantity;

            // Mobilization - JSON business_logic:
            // PRIMARY: Item_Fee * City_Labor_Mult
            // SECONDARY: City_Base_Fee + (Item_Fee * 0.5)
            double itemMob = itemDef.getMobilizationBaseFee() != null ? itemDef.getMobilizationBaseFee() : 0.0;
            double mobilization = 0.0;
            String mobPriority = itemDef.getMobilizationPriority();

            if ("PRIMARY".equalsIgnoreCase(mobPriority)) {
                // Large jobs (roofing, siding): scale with city labor rates
                mobilization = itemMob * scale.getLaborMult();
            } else {
                // Small jobs (windows, panels): city base + 50% item fee
                mobilization = scale.getMobFee() + (itemMob * 0.5);
            }

            // Disposal - JSON business_logic: "Units * waste_tons_per_unit *
            // (City_Disposal_Tax_Rate * 100)"
            // disp_tax = 0.04 means $4 per ton (needs × 100 to get dollar amount)
            double wasteTons = itemDef.getWasteTonsPerUnit() != null ? itemDef.getWasteTonsPerUnit() : 0.0;
            double disposal = quantity * wasteTons * (scale.getDispTax() * 100.0);

            // Calculate initial subtotal
            double subtotal = matCost + laborCost + mobilization + disposal;

            // --- Apply Small Job Penalty to ALL FIELDS (JSON business_logic) ---
            // JSON: "If Units < min_project_size, Total_Cost * short_order_multiplier"
            // Sync all component fields with the penalty so step4 stays accurate
            if (minSize != null && quantity < minSize && shortMult != null) {
                matCost *= penaltyMult;
                laborCost *= penaltyMult;
                mobilization *= penaltyMult;
                disposal *= penaltyMult;
                subtotal *= penaltyMult;
            }

            results.add(BaseCostItem.builder()
                    .itemCode(candidate.getItemCode())
                    .category(candidate.getCategory())
                    .description(candidate.getDescription())
                    .materialCost(matCost)
                    .laborCost(laborCost)
                    .mobilization(mobilization)
                    .disposal(disposal)
                    .subtotal(subtotal)
                    .wasteTons(wasteTons)
                    .mobilizationPriority(itemDef.getMobilizationPriority())
                    .rawData(candidate.getRawData())
                    .build());
        }

        // --- Mobilization Grouping (Audit: Charge City_Base_Fee only ONCE per trade)
        // ---
        applyTradeMobilizationDiscounts(results, scale);

        return results;
    }

    private void applyTradeMobilizationDiscounts(List<BaseCostItem> items, EstimatedScale scale) {
        // Group by trade (ROOFING, ELECTRICAL, etc.)
        Map<String, List<BaseCostItem>> byTrade = items.stream()
                .collect(Collectors.groupingBy(i -> {
                    String code = i.getItemCode();
                    int idx = code.indexOf('_');
                    return (idx > 0) ? code.substring(0, idx) : code;
                }));

        for (List<BaseCostItem> group : byTrade.values()) {
            if (group.size() <= 1)
                continue;

            // Find the anchor (Primary item or highest mobilization)
            BaseCostItem anchor = group.stream()
                    .max(Comparator.comparingDouble(i -> {
                        double score = i.getMobilization();
                        if ("PRIMARY".equalsIgnoreCase(i.getMobilizationPriority()))
                            score += 1000000;
                        return score;
                    }))
                    .orElse(group.get(0));

            // For all OTHER items in this trade, remove the redundant scale.getMobFee()
            for (BaseCostItem item : group) {
                if (item == anchor)
                    continue;

                // Only remove if it's a secondary item that actually contains the fee
                if (item.getMobilization() >= scale.getMobFee()) {
                    double newMob = Math.max(0, item.getMobilization() - scale.getMobFee());
                    item.setMobilization(newMob);
                    // Recalculate subtotal
                    item.setSubtotal(
                            item.getMaterialCost() + item.getLaborCost() + item.getMobilization() + item.getDisposal());
                }
            }
        }
    }

    // --- STEP 4: Risk & History Filter ---
    private List<RiskAdjustedItem> step4_riskFilter(List<BaseCostItem> items, UserContext context,
            List<String> exclusionNotes) {
        List<RiskAdjustedItem> adjustedItems = new ArrayList<>();

        EraData eraData = riskFactorsData.getEras().getOrDefault(context.getEra(), new EraData());
        List<RiskItem> eraRisks = eraData.getCriticalRisks() != null ? eraData.getCriticalRisks()
                : Collections.emptyList();

        for (BaseCostItem item : items) {
            double finalCost = item.getSubtotal();
            List<String> riskFlags = new ArrayList<>();
            boolean mandatory = false;
            String explanation = "";
            String compoundingBadge = null;
            String category = "COSMETIC"; // Default

            // 0. FORENSIC CONFIRMATION (Phase 4 - User Visual Observations)
            // These override statistical guessing with explicit user confirmation
            boolean forensicMatch = false;

            if (Boolean.TRUE.equals(context.getIsFpePanel()) && item.getItemCode().contains("ELECTRICAL_PANEL")) {
                riskFlags.add("FORENSIC_CONFIRMATION: FEDERAL_PACIFIC_PANEL");
                finalCost *= 2.0; // High failure rate, insurance risk
                mandatory = true;
                explanation = "Federal Pacific Electric panels have a documented failure rate. Insurance companies often require replacement. ";
                compoundingBadge = "FORENSIC CONFIRMATION (2.0x)";
                forensicMatch = true;
            }

            if (Boolean.TRUE.equals(context.getIsPolyB()) && item.getItemCode().contains("PLUMBING")) {
                riskFlags.add("FORENSIC_CONFIRMATION: POLYBUTYLENE");
                finalCost *= 1.5; // Known for brittle failure
                mandatory = true;
                explanation = "Polybutylene pipes are banned in new construction due to brittle failure. ";
                compoundingBadge = "FORENSIC CONFIRMATION (1.5x)";
                forensicMatch = true;
            }

            if (Boolean.TRUE.equals(context.getIsAluminum()) && item.getItemCode().contains("ELECTRICAL")) {
                riskFlags.add("FORENSIC_CONFIRMATION: ALUMINUM_WIRING");
                finalCost *= 1.8; // Fire hazard
                mandatory = true;
                explanation = "Aluminum wiring requires specialized connectors and is a known fire hazard. ";
                compoundingBadge = "FORENSIC CONFIRMATION (1.8x)";
                forensicMatch = true;
            }

            if (Boolean.TRUE.equals(context.getIsChineseDrywall()) && item.getItemCode().contains("DRYWALL")) {
                riskFlags.add("FORENSIC_CONFIRMATION: CHINESE_DRYWALL");
                finalCost *= 4.0; // Entire home gut required
                mandatory = true;
                explanation = "Defective Chinese drywall (2001-2009) requires full home remediation including electrical and HVAC replacement. ";
                compoundingBadge = "FORENSIC CONFIRMATION (4.0x HAZMAT)";
                forensicMatch = true;
            }

            // 1. Risk Overlay (MUST BE DONE FIRST)
            RiskItem matchedRisk = null; // Store matched risk for explanation building
            for (RiskItem risk : eraRisks) {
                boolean isRiskMatch = false;

                // Hardcoded Mapping for MVP
                if ("POLYBUTYLENE_PLUMBING".equals(risk.getItem()) && item.getItemCode().contains("PLUMBING"))
                    isRiskMatch = true;
                if ("KNOB_AND_TUBE_WIRING".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL"))
                    isRiskMatch = true;
                if ("ALUMINUM_WIRING".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL"))
                    isRiskMatch = true;
                if ("LP_INNER_SEAL_SIDING".equals(risk.getItem()) && item.getItemCode().contains("SIDING"))
                    isRiskMatch = true;
                if ("SYNTHETIC_STUCCO_EIFS".equals(risk.getItem()) && item.getItemCode().contains("STUCCO"))
                    isRiskMatch = true;
                if ("FEDERAL_PACIFIC_PANELS".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL_PANEL"))
                    isRiskMatch = true;

                if (isRiskMatch) {
                    matchedRisk = risk; // Store for explanation building
                    riskFlags.add("ERA_RISK: " + risk.getItem());

                    // Build evidence-based explanation
                    StringBuilder evidenceExplanation = new StringBuilder();

                    if (risk.getDefinition() != null && !risk.getDefinition().isEmpty()) {
                        evidenceExplanation.append(risk.getDefinition()).append(" ");
                    }

                    if (risk.getDamageScenario() != null && !risk.getDamageScenario().isEmpty()) {
                        evidenceExplanation.append(risk.getDamageScenario()).append(" ");
                    }

                    explanation = evidenceExplanation.toString();

                    // SPEC Line 40: "Era Adjustment: Labor * RFY.remedy_cost_factor"
                    if (risk.getRemedyMultiplier() != null && risk.getRemedyMultiplier() > 0) {
                        double totalComponents = item.getMaterialCost() + item.getLaborCost()
                                + item.getMobilization() + item.getDisposal();
                        double laborProportion = totalComponents > 0
                                ? item.getLaborCost() / totalComponents
                                : 0.0;
                        double laborImpact = item.getSubtotal() * laborProportion * (risk.getRemedyMultiplier() - 1.0);
                        finalCost = item.getSubtotal() + laborImpact;

                        riskFlags.add("ERA_LABOR_ADJUSTMENT: " + risk.getRemedyMultiplier() + "x");
                    }

                    // THEN apply CRITICAL severity multiplier to TOTAL
                    if ("CRITICAL".equals(risk.getSeverity())) {
                        finalCost *= 1.3;
                        riskFlags.add("CRITICAL_SEVERITY_SURCHARGE");
                        compoundingBadge = "HISTORICAL RISK COMPOUNDING APPLIED (1.3x)";
                    }

                    if (Boolean.TRUE.equals(risk.getInspectionMandatory())) {
                        finalCost += 650.0;
                        riskFlags.add("MANDATORY_INSPECTION");
                    }
                    if ("HIGH".equals(risk.getRemovalCost())) {
                        finalCost += 2800.0;
                        riskFlags.add("HAZMAT_REMOVAL");
                    }
                    if ("CRITICAL".equals(risk.getSeverity()) || Boolean.TRUE.equals(risk.getInspectionMandatory())) {
                        mandatory = true;
                    }
                }
            }

            // Determine Category
            // STRUCTURAL: Strict Core Integrity (Roof, Foundation, Sewer ONLY)
            if (item.getItemCode().contains("ROOF") || item.getItemCode().contains("FOUNDATION")
                    || item.getItemCode().contains("SEWER")) {
                category = "STRUCTURAL";
            }
            // MECHANICAL: Essential Systems
            else if (item.getItemCode().contains("HVAC") || item.getItemCode().contains("PLUMBING")
                    || item.getItemCode().contains("ELECTRICAL")) {
                category = "MECHANICAL";
            }
            // COSMETIC is default (Includes SIDING, WINDOWS unless mapped otherwise or
            // Critical)

            // Safety Override (Dynamic Promotion)
            if (mandatory || riskFlags.stream().anyMatch(f -> f.contains("CRITICAL"))) {
                category = "SAFETY";
                mandatory = true;
            }

            // --- PHASE 6: HISTORY LOGIC (REFECTORED) ---
            // STRICT RULE: History is applied ONLY for LIVING users.
            // BUYING/INVESTING users see FULL RISK SCOPE.

            if (context.getRelationship() == RelationshipToHouse.LIVING) {

                // Logic A: Core Systems (Risk Layer)
                // Only downgrade if confirmed updated AND no conflicting forensic flags
                if (context.getCoreSystemHistory() != null) {
                    boolean isCoreUpdated = false;
                    if (item.getItemCode().contains("ROOF") && context.getCoreSystemHistory().contains("ROOFING"))
                        isCoreUpdated = true;
                    if (item.getItemCode().contains("HVAC") && context.getCoreSystemHistory().contains("HVAC"))
                        isCoreUpdated = true;
                    if (item.getItemCode().contains("PLUMBING") && context.getCoreSystemHistory().contains("PLUMBING"))
                        isCoreUpdated = true;
                    if (item.getItemCode().contains("PANEL") && context.getCoreSystemHistory().contains("ELEC_PANEL"))
                        isCoreUpdated = true;

                    if (isCoreUpdated) {
                        boolean forensicOverride = riskFlags.stream()
                                .anyMatch(f -> f.contains("FORENSIC_CONFIRMATION"));

                        if (!forensicOverride) {
                            // Valid update, no forensic risk -> Exclude
                            // Use generic category name instead of specific item description
                            String categoryName = item.getItemCode().contains("ROOF") ? "Roofing"
                                    : item.getItemCode().contains("HVAC") ? "HVAC System"
                                            : item.getItemCode().contains("PLUMB") ? "Plumbing"
                                                    : item.getItemCode().contains("PANEL") ? "Electrical Panel"
                                                            : "System";
                            exclusionNotes.add("Recent Major System Update: " + categoryName + " (user-confirmed)");
                            continue;
                        } else {
                            // Forensic flag overrides history
                            riskFlags.add("SAFETY_OVERRIDE: FORENSIC_RISK_DETECTED");

                            // Build specific forensic evidence explanation
                            String forensicEvidence = "";
                            if (Boolean.TRUE.equals(context.getIsFpePanel())) {
                                forensicEvidence = "Federal Pacific Electric panel branding";
                            } else if (Boolean.TRUE.equals(context.getIsPolyB())) {
                                forensicEvidence = "Polybutylene (Poly-B) pipe materials";
                            } else if (Boolean.TRUE.equals(context.getIsAluminum())) {
                                forensicEvidence = "aluminum wiring";
                            } else if (Boolean.TRUE.equals(context.getIsChineseDrywall())) {
                                forensicEvidence = "Chinese drywall sulfur signature";
                            } else {
                                forensicEvidence = "hazardous materials";
                            }

                            explanation += String.format(
                                    " **IMPORTANT**: You indicated this system was recently updated, but visual inspection confirmed %s. "
                                            +
                                            "The original hazardous component remains and must be replaced.",
                                    forensicEvidence);
                        }
                    }
                }

                // Logic B: Living Spaces (Comfort Layer)
                // STRICT CHECK: Category must be COSMETIC, Not Mandatory, No Risk Flags
                if (context.getLivingSpaceHistory() != null) {
                    boolean isLivingUpdated = false;

                    if (item.getCategory().contains("INTERIOR") || item.getItemCode().contains("CABINET")
                            || item.getItemCode().contains("FLOOR")) {
                        if (item.getItemCode().contains("KITCHEN")
                                && context.getLivingSpaceHistory().contains("KITCHEN_REMODEL"))
                            isLivingUpdated = true;
                        if (item.getItemCode().contains("BATH")
                                && context.getLivingSpaceHistory().contains("BATH_REMODEL"))
                            isLivingUpdated = true;
                        if (item.getItemCode().contains("FLOOR")
                                && context.getLivingSpaceHistory().contains("FLOORING"))
                            isLivingUpdated = true;
                        if (item.getItemCode().contains("WINDOW")
                                && context.getLivingSpaceHistory().contains("WINDOWS"))
                            isLivingUpdated = true;
                    }

                    if (isLivingUpdated) {
                        // STRICT GATING
                        boolean isCosmetic = "COSMETIC".equals(category);
                        boolean hasRisk = !riskFlags.isEmpty();

                        if (isCosmetic && !mandatory && !hasRisk) {
                            // Use generic space name instead of specific item description
                            String spaceName = item.getItemCode().contains("KITCHEN") ? "Kitchen"
                                    : item.getItemCode().contains("BATH") ? "Bathroom"
                                            : item.getItemCode().contains("FLOOR") ? "Interior Flooring"
                                                    : item.getItemCode().contains("WINDOW") ? "Windows"
                                                            : "Interior Space";
                            exclusionNotes.add("Cosmetic Excluded: " + spaceName + " (Recently Updated)");
                            continue;
                        }
                    }
                }
            }
            // IF NOT LIVING, History is ignored (Full Scope).

            adjustedItems.add(RiskAdjustedItem.builder()
                    .itemCode(item.getItemCode())
                    .prettyName(item.getDescription())
                    .category(category)
                    .adjustedCost(finalCost)
                    .riskFlags(riskFlags)
                    .mandatory(mandatory)
                    .explanation(explanation)
                    .compoundingBadge(compoundingBadge)
                    .build());
        }

        return adjustedItems;
    }

    // --- STEP 5 (NEW): Strategic Filtering ---
    private SortedPlan step5_strategicFiltering(List<RiskAdjustedItem> items, UserContext context,
            StrategyType strategyType) {
        List<RiskAdjustedItem> mustDo = new ArrayList<>();
        List<RiskAdjustedItem> shouldDo = new ArrayList<>();
        List<RiskAdjustedItem> skip = new ArrayList<>();

        // --- Item-Level Deduplication (Strategy = Scope, choose ONE item per category)
        List<RiskAdjustedItem> filteredItems = new ArrayList<>();

        for (RiskAdjustedItem item : items) {
            boolean shouldInclude = true;

            switch (strategyType) {
                case SAFETY_FLIP:
                    // Exclude premium items
                    if (item.getItemCode().contains("METAL") && item.getItemCode().contains("ROOF")) {
                        shouldInclude = false;
                    }
                    if (item.getItemCode().contains("DECK") || item.getItemCode().contains("CABINET")) {
                        shouldInclude = false;
                    }
                    break;

                case STANDARD_LIVING:
                    // Exclude only extreme premium items
                    if (item.getItemCode().contains("METAL") && item.getItemCode().contains("ROOF")) {
                        shouldInclude = false;
                    }
                    break;

                case FOREVER_HOME:
                    // Prefer premium items
                    break;
            }

            if (shouldInclude) {
                filteredItems.add(item);
            }
        }

        // --- Category-Based Filtering (cleaned up for Philosophy 1.0) ---
        for (RiskAdjustedItem item : filteredItems) {
            String cat = item.getCategory();
            boolean isSafety = "SAFETY".equals(cat);
            boolean isStructural = "STRUCTURAL".equals(cat);
            boolean isMechanical = "MECHANICAL".equals(cat);
            boolean isCritical = item.isMandatory()
                    || item.getRiskFlags().stream().anyMatch(f -> f.contains("CRITICAL"));

            switch (strategyType) {
                case SAFETY_FLIP:
                    // Pure Safety / Code Minimum
                    if (isSafety || isCritical) {
                        mustDo.add(item);
                    } else {
                        skip.add(item);
                    }
                    break;

                case STANDARD_LIVING:
                    // Safety + Functional + Standards
                    if (isSafety || isCritical) {
                        mustDo.add(item);
                    } else if (isStructural || isMechanical) {
                        mustDo.add(item);
                    } else {
                        // Cosmetic: Included unless filtered by Step 4 (History)
                        if ("COSMETIC".equals(cat)) {
                            shouldDo.add(item);
                        } else {
                            // E.g. other functional but not core
                            shouldDo.add(item);
                        }
                    }
                    break;

                case FOREVER_HOME:
                    // EVERYTHING
                    if (isSafety || isCritical || isStructural) {
                        mustDo.add(item);
                    } else {
                        shouldDo.add(item);
                    }
                    break;
            }
        }

        // Sort by cost descending
        Comparator<RiskAdjustedItem> costDesc = (a, b) -> Double.compare(b.getAdjustedCost(), a.getAdjustedCost());
        mustDo.sort(costDesc);
        shouldDo.sort(costDesc);
        skip.sort(costDesc);

        return SortedPlan.builder()
                .mustDo(mustDo)
                .shouldDo(shouldDo)
                .skipForNow(skip)
                .build();
    }

    // --- NEGOTIATION COPY GENERATOR (PHASE 3) ---
    private String generateNegotiationCopy(SortedPlan plan, UserContext context) {
        StringBuilder copy = new StringBuilder();
        List<RiskAdjustedItem> criticalItems = plan.getMustDo().stream()
                .filter(item -> "SAFETY".equals(item.getCategory()) || item.isMandatory())
                .collect(Collectors.toList());

        if (criticalItems.isEmpty()) {
            return "No critical mandatory repairs detected for this property age and location. Negotiation leverage based on condition is neutral.";
        }

        double totalCriticalCost = criticalItems.stream()
                .mapToDouble(RiskAdjustedItem::getAdjustedCost)
                .sum();

        // Apply 1.5x leverage multiplier (Cost + Hassle Factor)
        double leverageValue = totalCriticalCost * 1.5;

        copy.append("NEGOTIATION COPY (Copy & Paste for your agent):\n\n");
        copy.append(String.format(
                "Based on the 2026 RSMeans Cost Index for %s, this property requires $%,.0f in immediate Code-Mandatory repairs:\n\n",
                context.getMetroCode(), totalCriticalCost));

        for (RiskAdjustedItem item : criticalItems) {
            copy.append(String.format("• %s: $%,.0f\n", item.getPrettyName(), item.getAdjustedCost()));
        }

        copy.append(
                String.format("\nWe request a price reduction of $%,.0f to cover remediation costs and project risk.\n",
                        leverageValue));
        copy.append("This valuation is based on licensed contractor estimates and local material/labor indices.");

        return copy.toString();
    }

    // REMOVED: step6_verdictGeneration was never called in generateVerdict (dead
    // code)
    // All explanation logic is already handled in step4_riskFilter
}
