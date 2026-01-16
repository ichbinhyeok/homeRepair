package com.livingcostcheck.home_repair.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping.*;
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
        // Generate THREE strategic options instead of one
        List<StrategyOption> strategyOptions = new ArrayList<>();

        // Common Steps (0-2)
        List<BaseCostItem> candidates = step0_candidateGenerator(context);
        EstimatedScale scale = step2_autoScale(context);

        // TIER 1: Safety/Flip Baseline
        StrategyOption safetyOption = generateStrategyOption(
                StrategyType.SAFETY_FLIP,
                candidates,
                scale,
                context);
        strategyOptions.add(safetyOption);

        // TIER 2: Standard Living (Default)
        StrategyOption standardOption = generateStrategyOption(
                StrategyType.STANDARD_LIVING,
                candidates,
                scale,
                context);
        strategyOptions.add(standardOption);

        // TIER 3: Forever Home (Premium)
        StrategyOption foreverOption = generateStrategyOption(
                StrategyType.FOREVER_HOME,
                candidates,
                scale,
                context);
        strategyOptions.add(foreverOption);

        // Determine overall verdict based on Tier 1 (minimum required)
        double minRequired = safetyOption.getTotalCost();
        double budget = context.getBudget();
        String tier = "DENIED";
        String headline = "";

        if (budget >= minRequired) {
            tier = "APPROVED";
            headline = String.format(
                    "You can fix this house for $%,.0f (minimum), $%,.0f (standard), or $%,.0f (premium). Choose your strategy.",
                    safetyOption.getTotalCost(), standardOption.getTotalCost(), foreverOption.getTotalCost());
        } else if (budget >= (minRequired * 0.9)) {
            tier = "WARNING";
            headline = "Budget is tight for even minimum safety repairs. Risk of incomplete work.";
        } else {
            tier = "DENIED";
            headline = "Budget insufficient for required safety repairs at local 2026 rates.";
        }

        // Build final verdict with backward compatibility
        return Verdict.builder()
                .tier(tier)
                .headline(headline)
                .strategyOptions(strategyOptions)
                .plan(standardOption.getPlan()) // Default to standard for backward compatibility
                .mustDoExplanation(Collections.emptyList()) // Will be in each strategy option
                .optionalActions(Collections.emptyList())
                .futureCostWarning(Collections.emptyList())
                .upgradeScenario(Collections.emptyList())
                .build();
    }

    /**
     * Generate a single strategic option based on strategy type
     */
    private StrategyOption generateStrategyOption(
            StrategyType strategyType,
            List<BaseCostItem> candidates,
            EstimatedScale scale,
            UserContext context) {
        // Step 3: Preliminary Costing (with strategy-specific material grades)
        List<BaseCostItem> costedItems = step3_preliminaryCosting(candidates, scale, strategyType);

        // Step 4: Risk & History Filter
        List<RiskAdjustedItem> riskAdjustedItems = step4_riskFilter(costedItems, context);

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
                name = "The Safety/Flip Baseline";
                description = "Minimum cost to pass inspection";
                goal = "Safe to sell or rent quickly";
                materialGrade = "Budget (Asphalt Shingles, Vinyl Siding)";
                includedCategories = Arrays.asList("SAFETY", "CRITICAL", "MANDATORY");
                keyHighlights = Arrays.asList(
                        "Only inspection-mandatory items",
                        "Lowest material grades",
                        "Minimum to avoid buyer walkaway");
                break;
            case STANDARD_LIVING:
                name = "The Standard Living";
                description = "Comfortable living for 5-7 years";
                goal = "Safe and functional home";
                materialGrade = "Standard (Architectural Shingles, Fiber Cement)";
                includedCategories = Arrays.asList("SAFETY", "STRUCTURAL", "MECHANICAL", "FUNCTIONAL");
                keyHighlights = Arrays.asList(
                        "All safety + essential systems",
                        "Standard contractor materials",
                        "Balanced cost-to-quality ratio");
                break;
            case FOREVER_HOME:
                name = "The Forever Home";
                description = "Maximum asset value appreciation";
                goal = "Premium durability and resale value";
                materialGrade = "Premium (Metal Roof, Hardie Board, High-SEER HVAC)";
                includedCategories = Arrays.asList("ALL");
                keyHighlights = Arrays.asList(
                        "Everything including cosmetic upgrades",
                        "Premium materials with longest warranty",
                        "Maximize home value and appeal");
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

        double avgHouse = city.getAvgHouse() != null ? city.getAvgHouse() : 2000.0;
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
    private List<BaseCostItem> step3_preliminaryCosting(List<BaseCostItem> candidates, EstimatedScale scale,
            StrategyType strategyType) {
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
                        quantity = scale.getInteriorSqft() * 2.5; // Interior wall approx
                    }
                    break;
                case "LF":
                    if (candidate.getItemCode().contains("CABINET")) {
                        quantity = 35.0; // Avg kitchen cabinet run
                    } else if (candidate.getItemCode().contains("GUTTER")) {
                        quantity = Math.sqrt(scale.getInteriorSqft()) * 4.0 * 1.15; // Perimeter + waste
                    } else if (candidate.getItemCode().contains("REPIPE")) {
                        quantity = scale.getInteriorSqft() * 0.8; // Rough pipe length estimate
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
                quantity = Math.ceil(scale.getHvacTons() / 2.0); // Num units
            if ("ROOFING_ASPHALT_ARCHITECTURAL".equals(candidate.getItemCode()))
                quantity = scale.getRoofingSquares();
            if ("PLUMBING_WHOLE_HOUSE_REPIPE".equals(candidate.getItemCode()))
                quantity = scale.getInteriorSqft() * 0.04; // Approx linear feet based on floor area

            // --- 1. Small Job Penalty Logic (JSON Compliance) ---
            double penaltyMult = 1.0;
            Double minSize = itemDef.getMinProjectSize();
            Double shortMult = itemDef.getShortOrderMultiplier();
            if (minSize != null && quantity < minSize && shortMult != null) {
                penaltyMult = shortMult;
            }

            // Material Cost - STRATEGY-DEPENDENT
            double matBase = 0.0;
            switch (strategyType) {
                case SAFETY_FLIP:
                    // Use LOW end of material cost range
                    matBase = itemDef.getMaterialCostRange().getLow();
                    break;
                case STANDARD_LIVING:
                    // Use AVERAGE (existing logic)
                    matBase = (itemDef.getMaterialCostRange().getLow() + itemDef.getMaterialCostRange().getHigh())
                            / 2.0;
                    break;
                case FOREVER_HOME:
                    // Use HIGH end of material cost range
                    matBase = itemDef.getMaterialCostRange().getHigh();
                    break;
            }
            double matCost = matBase * scale.getMatLogistics() * quantity;

            // Labor Cost (Apply Penalty Here to capture efficiency loss)
            double laborRate = itemDef.getBaseLaborRateNational() * scale.getLaborMult();
            double laborCost = itemDef.getLaborHoursPerUnit() * laborRate * quantity * penaltyMult;

            // Mobilization
            double itemMob = itemDef.getMobilizationBaseFee() != null ? itemDef.getMobilizationBaseFee() : 0.0;
            double mobilization = Math.max(itemMob, scale.getMobFee());

            // Disposal (Fix Formula: Removed erroneous * 100.0)
            double wasteTons = itemDef.getWasteTonsPerUnit() != null ? itemDef.getWasteTonsPerUnit() : 0.0;
            double disposal = quantity * wasteTons * scale.getDispTax();

            double subtotal = matCost + laborCost + mobilization + disposal;

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

        // Apply Mobilization Optimization (Discount shared trips)
        optimizeMobilizationCosts(results);

        return results;
    }

    private void optimizeMobilizationCosts(List<BaseCostItem> items) {
        // Group by Trade Prefix (e.g. ROOFING, HVAC, ELECTRICAL) to prevent invalid
        // cross-trade discounts
        Map<String, List<BaseCostItem>> byTrade = items.stream()
                .collect(Collectors.groupingBy(item -> {
                    String code = item.getItemCode();
                    int idx = code.indexOf('_');
                    return (idx > 0) ? code.substring(0, idx) : code;
                }));

        for (List<BaseCostItem> group : byTrade.values()) {
            if (group.isEmpty())
                continue;

            // Find max mob fee item (Primary), prioritizing "PRIMARY" tag from JSON
            BaseCostItem primary = group.stream()
                    .max(Comparator.comparingDouble((BaseCostItem i) -> {
                        double score = i.getMobilization();
                        // Huge boost for explicit PRIMARY items so they always become the anchor
                        if ("PRIMARY".equalsIgnoreCase(i.getMobilizationPriority())) {
                            score += 100000.0;
                        }
                        return score;
                    }))
                    .orElse(group.get(0));

            for (BaseCostItem item : group) {
                if (item != primary && item.getMobilization() > 0) { // Only discount if not primary
                    double originalMob = item.getMobilization();
                    double discountedMob = originalMob * 0.5; // 50% discount for secondary items
                    item.setMobilization(discountedMob);

                    // Re-sum subtotal
                    double newSub = item.getMaterialCost() + item.getLaborCost() + discountedMob + item.getDisposal();
                    item.setSubtotal(newSub);
                }
            }
        }
    }

    // --- STEP 4: Risk & History Filter ---
    private List<RiskAdjustedItem> step4_riskFilter(List<BaseCostItem> items, UserContext context) {
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

                    // Build evidence-based explanation: [Definition] → [Damage Scenario] → [Cost
                    // Comparison]
                    StringBuilder evidenceExplanation = new StringBuilder();

                    // Step 1: Definition (if available)
                    if (risk.getDefinition() != null && !risk.getDefinition().isEmpty()) {
                        evidenceExplanation.append(risk.getDefinition()).append(" ");
                    }

                    // Step 2: Damage Scenario (if available)
                    if (risk.getDamageScenario() != null && !risk.getDamageScenario().isEmpty()) {
                        evidenceExplanation.append(risk.getDamageScenario()).append(" ");
                    }

                    explanation = evidenceExplanation.toString();

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

            // 2. History Handling
            boolean inHistory = false;
            if (context.getHistory() != null) {
                for (String h : context.getHistory()) {
                    if (item.getItemCode().contains(h)) {
                        inHistory = true;
                        break;
                    }
                }
            }

            if (inHistory) {
                if ("NONE".equals(context.getCondition())) {
                    // Logic: REMOVE item entirely ONLY if not SAFETY
                    if ("SAFETY".equals(category)) {
                        explanation += " [SAFETY OVERRIDE]: History claim ignored due to critical safety risk.";
                    } else {
                        continue; // REMOVE
                    }
                } else {
                    // RECHECK MODE
                    riskFlags.add("HISTORY_RECHECK");
                    double labor = item.getLaborCost() * 0.25;
                    double material = 150.0; // Diagnostic Min Cost

                    // Mobilization stays full
                    finalCost = labor + material + item.getMobilization() + item.getDisposal();
                    explanation += " Recheck required due to history condition: " + context.getCondition();
                }
            }

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

    // --- STEP 5: Priority Ranking ---
    private SortedPlan step5_priorityRanking(List<RiskAdjustedItem> items, UserContext context) {
        List<RiskAdjustedItem> mustDo = new ArrayList<>();
        List<RiskAdjustedItem> shouldDo = new ArrayList<>();
        List<RiskAdjustedItem> skip = new ArrayList<>();

        for (RiskAdjustedItem item : items) {
            String cat = item.getCategory();
            boolean isSafety = "SAFETY".equals(cat);
            boolean isStructural = "STRUCTURAL".equals(cat);
            boolean isMechanical = "MECHANICAL".equals(cat);

            if (isSafety) {
                mustDo.add(item);
            } else if (isStructural) {
                mustDo.add(item);
            } else if (isMechanical) {
                if (item.getRiskFlags().size() > 0)
                    mustDo.add(item); // Risk detected -> Must Do
                else {
                    if ("LIVING".equals(context.getPurpose()))
                        shouldDo.add(item);
                    else
                        shouldDo.add(item);
                }
            } else {
                // Cosmetic
                if ("RESALE".equals(context.getPurpose()))
                    shouldDo.add(item);
                else if ("LIVING".equals(context.getPurpose()))
                    shouldDo.add(item);
                else
                    skip.add(item);
            }
        }

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

    // --- STEP 5 (NEW): Strategic Filtering ---
    private SortedPlan step5_strategicFiltering(List<RiskAdjustedItem> items, UserContext context,
            StrategyType strategyType) {
        List<RiskAdjustedItem> mustDo = new ArrayList<>();
        List<RiskAdjustedItem> shouldDo = new ArrayList<>();
        List<RiskAdjustedItem> skip = new ArrayList<>();

        for (RiskAdjustedItem item : items) {
            String cat = item.getCategory();
            boolean isSafety = "SAFETY".equals(cat);
            boolean isStructural = "STRUCTURAL".equals(cat);
            boolean isMechanical = "MECHANICAL".equals(cat);
            boolean isCritical = item.isMandatory()
                    || item.getRiskFlags().stream().anyMatch(f -> f.contains("CRITICAL"));

            switch (strategyType) {
                case SAFETY_FLIP:
                    // Only SAFETY, CRITICAL, and MANDATORY items
                    if (isSafety || isCritical) {
                        mustDo.add(item);
                    } else {
                        skip.add(item);
                    }
                    break;

                case STANDARD_LIVING:
                    // SAFETY + STRUCTURAL + MECHANICAL (functional systems)
                    if (isSafety || isCritical) {
                        mustDo.add(item);
                    } else if (isStructural || isMechanical) {
                        mustDo.add(item);
                    } else {
                        // Cosmetic - depends on purpose
                        if ("RESALE".equals(context.getPurpose()) || "LIVING".equals(context.getPurpose())) {
                            shouldDo.add(item);
                        } else {
                            skip.add(item);
                        }
                    }
                    break;

                case FOREVER_HOME:
                    // EVERYTHING - all items are included
                    if (isSafety || isCritical || isStructural) {
                        mustDo.add(item);
                    } else {
                        // Even cosmetic items are "should do" for premium tier
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

    // --- STEP 6: Verdict & Strategic Advice (STRING GENERATION ONLY - NO
    // CALCULATION CHANGES) ---
    private Verdict step6_verdictGeneration(SortedPlan plan, UserContext context) {
        // CALCULATIONS UNCHANGED - These are existing logic
        double totalRequired = plan.getMustDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();
        double totalOptional = plan.getShouldDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();
        double budget = context.getBudget();
        String tier = "DENIED";
        String headline = "";

        // VERDICT LOGIC UNCHANGED - Existing thresholds
        if (budget >= totalRequired) {
            tier = "APPROVED";
            headline = "Your budget safely covers required repairs at local 2026 rates.";
        } else if (budget >= (totalRequired * 0.9)) {
            tier = "WARNING";
            headline = "Budget is tight. Risk of downgraded materials or missing abatement.";
        } else {
            tier = "DENIED";
            headline = "Budget is insufficient for local standards. Focus on safety-critical items only.";
        }

        // NEW: Enhanced explanation generation with evidence-based approach
        List<String> mustDoExplanations = new ArrayList<>();
        EraData eraData = riskFactorsData.getEras().getOrDefault(context.getEra(), new EraData());
        List<RiskItem> eraRisks = eraData.getCriticalRisks() != null ? eraData.getCriticalRisks()
                : Collections.emptyList();
        String dataAuthority = metroMasterData.getDataAuthority();

        for (RiskAdjustedItem item : plan.getMustDo()) {
            StringBuilder fullExplanation = new StringBuilder();

            // Start with existing explanation (already contains definition + damage
            // scenario from step4)
            if (item.getExplanation() != null && !item.getExplanation().isEmpty()) {
                fullExplanation.append(item.getExplanation());
            }

            // Add cost range disclosure (±5%)
            double costLow = item.getAdjustedCost() * 0.95;
            double costHigh = item.getAdjustedCost() * 1.05;

            // Find matched risk for remedy_multiplier (if applicable)
            RiskItem matchedRisk = null;
            for (RiskItem risk : eraRisks) {
                boolean isMatch = false;
                if ("POLYBUTYLENE_PLUMBING".equals(risk.getItem()) && item.getItemCode().contains("PLUMBING"))
                    isMatch = true;
                if ("KNOB_AND_TUBE_WIRING".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL"))
                    isMatch = true;
                if ("ALUMINUM_WIRING".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL"))
                    isMatch = true;
                if ("LP_INNER_SEAL_SIDING".equals(risk.getItem()) && item.getItemCode().contains("SIDING"))
                    isMatch = true;
                if ("SYNTHETIC_STUCCO_EIFS".equals(risk.getItem()) && item.getItemCode().contains("STUCCO"))
                    isMatch = true;
                if ("FEDERAL_PACIFIC_PANELS".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL_PANEL"))
                    isMatch = true;

                if (isMatch) {
                    matchedRisk = risk;
                    break;
                }
            }

            // Add quantified opportunity cost comparison (if remedy_multiplier available)
            if (matchedRisk != null && matchedRisk.getRemedyMultiplier() != null
                    && matchedRisk.getRemedyMultiplier() > 0) {
                double incidentLow = item.getAdjustedCost() * matchedRisk.getRemedyMultiplier() * 0.95;
                double incidentHigh = item.getAdjustedCost() * matchedRisk.getRemedyMultiplier() * 1.05;

                fullExplanation.append(String.format(
                        "Planned repair typically costs $%,.0f–$%,.0f. " +
                                "A single failure often results in $%,.0f–$%,.0f in combined damage and interior repairs. ",
                        costLow, costHigh, incidentLow, incidentHigh));
            } else {
                // No multiplier available, just show cost range
                fullExplanation.append(String.format(
                        "Estimated cost: $%,.0f–$%,.0f. ",
                        costLow, costHigh));
            }

            // Add regional credibility (if era-based risk)
            if (item.getRiskFlags() != null && item.getRiskFlags().stream().anyMatch(f -> f.contains("ERA_RISK"))) {
                if (dataAuthority != null && !dataAuthority.isEmpty()) {
                    fullExplanation.append(String.format(
                            "In homes of this era, according to %s, these systems have typically reached the end of their service life window. ",
                            dataAuthority));
                }
            }

            // Add disclaimer
            fullExplanation.append("(Actual contractor quotes may vary based on layout and access.)");

            // Format: "ItemName: Explanation"
            mustDoExplanations.add(item.getPrettyName() + ": " + fullExplanation.toString());
        }

        // FORBIDDEN LANGUAGE REMOVED: Old vague "may increase by 30-50%" replaced with
        // evidence-based approach above
        // No longer creating generic futureCostWarning - specific costs are now
        // integrated into each item's explanation

        return Verdict.builder()
                .tier(tier)
                .headline(headline)
                .mustDoExplanation(mustDoExplanations)
                .optionalActions(
                        plan.getShouldDo().stream().map(RiskAdjustedItem::getPrettyName).collect(Collectors.toList()))
                .futureCostWarning(Collections.emptyList()) // Replaced with inline cost comparisons
                .upgradeScenario(
                        Collections.singletonList("If budget allows, upgrade HVAC to Heat Pump for tax credits."))
                .plan(plan)
                .build();
    }
}
