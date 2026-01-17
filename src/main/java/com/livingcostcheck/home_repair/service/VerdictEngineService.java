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

        // Optimization: Step 3 & 4 are strategy-agnostic
        // Run them ONCE
        List<BaseCostItem> costedItems = step3_preliminaryCosting(candidates, scale);

        // Pass exclusionNotes list to be populated during filtering
        List<String> exclusionNotes = new ArrayList<>();
        List<RiskAdjustedItem> baseRiskAdjustedItems = step4_riskFilter(costedItems, context, exclusionNotes);

        // Context-Aware Strategy Generation (Money-First Efficiency)
        StrategyOption safetyOption = generateStrategyOption(StrategyType.SAFETY_FLIP, baseRiskAdjustedItems, context);
        // strategyOptions.add(safetyOption); // Internal calculation only

        StrategyOption standardOption = null;
        if (context.getRelationship() == RelationshipToHouse.INVESTING
                || context.getRelationship() == RelationshipToHouse.LIVING) {
            standardOption = generateStrategyOption(StrategyType.STANDARD_LIVING, baseRiskAdjustedItems, context);
        }

        // Determine overall verdict based on Tier 1 (minimum required - Safety Option)
        double minRequired = safetyOption.getTotalCost();
        double budget = context.getBudget();
        String tier = "DENIED";
        String headline = "";

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
        // LIVING/INVESTING -> Standard (Comfort/Stability)
        // BUYING -> Safety (Baseline Risk)
        SortedPlan displayPlan;
        if (standardOption != null) {
            displayPlan = standardOption.getPlan();
        } else {
            displayPlan = safetyOption.getPlan();
        }

        // Build final verdict
        return Verdict.builder()
                .tier(tier)
                .headline(headline)
                .strategyOptions(Collections.emptyList()) // Hide strategies to enforce Single Verdict UI
                .exclusionNote(exclusionNotes)
                .plan(displayPlan)
                .mustDoExplanation(Collections.emptyList())
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
                            exclusionNotes.add("Recent Major System Update: " + item.getDescription());
                            continue;
                        } else {
                            // Forensic flag overrides history
                            riskFlags.add("SAFETY_OVERRIDE: FORENSIC_RISK_DETECTED");
                            explanation += " Despite recent updates, forensic evidence of safety risk (e.g. Poly-B) requires remediation.";
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
                            exclusionNotes.add("Cosmetic Excluded: " + item.getDescription() + " (Use Existing)");
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
