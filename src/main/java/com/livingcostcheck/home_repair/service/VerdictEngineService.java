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
            metroMasterData = loadJson("classpath:data/2026 US Metro Master Data.json", MetroMasterData.class);
            riskFactorsData = loadJson("classpath:data/risk_factors_by_year.json", RiskFactorsData.class);
            costLibraryData = loadJson("classpath:data/2026 Integrated Construction & Renovation Cost Library.json",
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
        // Step 0: Candidate Generator
        List<BaseCostItem> candidates = step0_candidateGenerator(context);

        // Step 1 & 2: Scale & Localization
        EstimatedScale scale = step2_autoScale(context);

        // Step 3: Preliminary Costing
        List<BaseCostItem> costedItems = step3_preliminaryCosting(candidates, scale);

        // Step 4: Risk & History Filter
        List<RiskAdjustedItem> riskAdjustedItems = step4_riskFilter(costedItems, context);

        // Step 5: Priority Ranking
        SortedPlan sortedPlan = step5_priorityRanking(riskAdjustedItems, context);

        // Step 6: Verdict Generation
        return step6_verdictGeneration(sortedPlan, context);
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

        // 2. Add Era-Specific Risk Items (if they map to known items or are generic)
        if (context.getEra() != null && riskFactorsData.getEras().containsKey(context.getEra())) {
            EraData eraData = riskFactorsData.getEras().get(context.getEra());
            if (eraData.getCriticalRisks() != null) {
                // For now, these are used as flags in Step 4, but we ensure they are
                // considered.
                // If the risk item is synonymous with a library item (e.g. plumbing), it
                // enhances that item.
                // If it is standalone, it might need special handling.
                // Simpler approach: We attach Risk Data to context or lookup in Step 4.
                // However, "Candidate Generator" implies selecting WHAT to check.
                // We blindly check everything in the library + specific risks.
            }
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
                    quantity = scale.getRoofingSquares();
                    break; // Approximation
                case "UNIT":
                    quantity = 1.0;
                    break; // Default unit
                case "SQFT":
                    // Guessing logic based on category
                    if (candidate.getCategory().contains("INTERIOR"))
                        quantity = scale.getInteriorSqft();
                    else
                        quantity = scale.getExteriorSqft();
                    break;
                case "SQFT_WALL":
                    quantity = scale.getInteriorSqft() * 2.5;
                    break; // Rough wall estimate
                case "LF":
                    quantity = scale.getExteriorSqft() * 0.1;
                    break; // Very rough
                case "EACH":
                    quantity = 10.0;
                    break; // Windows etc default
                default:
                    quantity = 1.0;
            }

            // Special overrides for known items
            if ("HVAC_HEAT_PUMP_CENTRAL".equals(candidate.getItemCode()))
                quantity = Math.ceil(scale.getHvacTons() / 2.0); // Num units
            if ("ROOFING_ASPHALT_ARCHITECTURAL".equals(candidate.getItemCode()))
                quantity = scale.getRoofingSquares();
            if ("PLUMBING_WHOLE_HOUSE_REPIPE".equals(candidate.getItemCode()))
                quantity = scale.getInteriorSqft() * 0.05; // Approx linear feet based on floor area

            // Material Cost
            double matBase = (itemDef.getMaterialCostRange().getLow() + itemDef.getMaterialCostRange().getHigh()) / 2.0;
            double matCost = matBase * scale.getMatLogistics() * quantity;

            // Labor Cost
            double laborRate = itemDef.getBaseLaborRateNational() * scale.getLaborMult();
            double laborCost = itemDef.getLaborHoursPerUnit() * laborRate * quantity;

            // Mobilization
            double itemMob = itemDef.getMobilizationBaseFee() != null ? itemDef.getMobilizationBaseFee() : 0.0;
            double mobilization = Math.max(itemMob, scale.getMobFee());

            // Disposal
            double wasteTons = itemDef.getWasteTonsPerUnit() != null ? itemDef.getWasteTonsPerUnit() : 0.0;
            double disposal = quantity * wasteTons * (scale.getDispTax() * 100.0); // Assuming tax rate * $100 base dump
                                                                                   // fee? Or maybe logic in library:
                                                                                   // "City_Disposal_Tax_Rate * 100"
                                                                                   // implies rate * 100 is price per
                                                                                   // ton?
            // "Units * waste_tons_per_unit * (City_Disposal_Tax_Rate * 100)" -> seems to
            // imply tax rate is used directly as price factor?
            // Let's assume (Tax Rate * 100) is the cost per ton impact.
            // Actually, usually it's Base Tipping Fee * Tax. But sticking to prompt "Units
            // * waste_tons * (disp_tax * 100)"

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
                    .rawData(candidate.getRawData())
                    .build());
        }
        return results;
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

            // 1. History Handling
            boolean inHistory = context.getHistory() != null && context.getHistory().contains(item.getItemCode()); // Simple
                                                                                                                   // match?
                                                                                                                   // Or
                                                                                                                   // partial?
                                                                                                                   // Assuming
                                                                                                                   // exact
                                                                                                                   // code
                                                                                                                   // match
                                                                                                                   // or
                                                                                                                   // mapped.
            // Simplified: Assuming exact match for now. In reality, needs map.
            // User prompt example: "History: [ROOFING]" matches
            // "ROOFING_ASPHALT_ARCHITECTURAL" maybe?
            // Let's check for substring for robustness if exact fail
            boolean historyMatch = inHistory;
            if (!inHistory && context.getHistory() != null) {
                for (String h : context.getHistory()) {
                    if (item.getItemCode().contains(h)) {
                        historyMatch = true;
                        break;
                    }
                }
            }

            if (historyMatch) {
                if ("NONE".equals(context.getCondition())) {
                    continue; // REMOVE item entirely
                } else {
                    // RECHECK MODE
                    riskFlags.add("HISTORY_RECHECK");
                    double labor = item.getLaborCost() * 0.25;
                    double material = 150.0; // Diagnostic Min Cost (Hardcoded assumption or param?)
                    finalCost = labor + material + (item.getMobilization() * 0.5); // reduced mob?
                    explanation = "Recheck required due to history condition: " + context.getCondition();
                }
            }

            // 2. Risk Overlay
            // Check if this item relates to any Era Risk
            // Mapping Logic: Does RiskItem.item match ItemCode?
            // Example: "POLYBUTYLENE_PLUMBING" vs "PLUMBING_WHOLE_HOUSE_REPIPE"
            // We need a mapping or fuzzy match.
            for (RiskItem risk : eraRisks) { // RiskItem from JSON
                // Logic: If Risk Item matches cost item Category or Code
                boolean isRiskMatch = false;

                // Hardcoded Mapping for MVP (Critical for dry run)
                if ("POLYBUTYLENE_PLUMBING".equals(risk.getItem()) && item.getItemCode().contains("PLUMBING"))
                    isRiskMatch = true;
                if ("KNOB_AND_TUBE_WIRING".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL"))
                    isRiskMatch = true;
                if ("ALUMINUM_WIRING".equals(risk.getItem()) && item.getItemCode().contains("ELECTRICAL"))
                    isRiskMatch = true;

                if (isRiskMatch) {
                    riskFlags.add("ERA_RISK: " + risk.getItem());
                    explanation += " High risk detected for " + risk.getItem() + ".";

                    if ("CRITICAL".equals(risk.getSeverity())) {
                        finalCost *= 1.3;
                        riskFlags.add("CRITICAL_SEVERITY_SURCHARGE");
                    }
                    if (Boolean.TRUE.equals(risk.getInspectionMandatory())) {
                        finalCost += 650.0;
                        riskFlags.add("MANDATORY_INSPECTION");
                    }
                    if ("HIGH".equals(risk.getRemovalCost())) {
                        finalCost += 2800.0;
                        riskFlags.add("HAZMAT_REMOVAL");
                    }
                    mandatory = true; // Safety overrides ??
                }
            }

            // Assign Category mapping
            String finalCat = "COSMETIC";
            if (item.getItemCode().contains("ROOF") || item.getItemCode().contains("FOUNDATION")
                    || item.getCategory().contains("STRUCTURAL"))
                finalCat = "STRUCTURAL";
            if (item.getItemCode().contains("HVAC") || item.getItemCode().contains("PLUMBING")
                    || item.getItemCode().contains("ELECTRICAL"))
                finalCat = "MECHANICAL";
            if (mandatory || riskFlags.stream().anyMatch(f -> f.contains("CRITICAL")))
                finalCat = "SAFETY";

            adjustedItems.add(RiskAdjustedItem.builder()
                    .itemCode(item.getItemCode())
                    .prettyName(item.getDescription())
                    .category(finalCat)
                    .adjustedCost(finalCost)
                    .riskFlags(riskFlags)
                    .mandatory(mandatory)
                    .explanation(explanation)
                    .build());
        }
        return adjustedItems;
    }

    // --- STEP 5: Priority Ranking ---
    private SortedPlan step5_priorityRanking(List<RiskAdjustedItem> items, UserContext context) {
        List<RiskAdjustedItem> mustDo = new ArrayList<>();
        List<RiskAdjustedItem> shouldDo = new ArrayList<>();
        List<RiskAdjustedItem> skip = new ArrayList<>();

        // Base Sorting: SAFETY > STRUCTURAL > MECHANICAL > COSMETIC
        // Modifiers based on Purpose

        Comparator<RiskAdjustedItem> comparator = (a, b) -> {
            int scoreA = getCategoryScore(a.getCategory());
            int scoreB = getCategoryScore(b.getCategory());
            return Integer.compare(scoreB, scoreA); // Descending score
        };

        // Determine Cutoff based on "Purpose" and "Budget"?
        // Or just buckets? User prompt says "SortedPlan { must_do, should_do,
        // skip_for_now }"
        // Logic: SAFETY is ALWAYS Must Do.
        // STRUCTURAL is Must Do if Purpose != RESALE? Or always?
        // Let's use simple Logic:
        // SAFETY -> Must Do
        // STRUCTURAL -> Must Do
        // MECHANICAL -> Should Do (unless Critical)
        // COSMETIC -> Skip (unless Budget permits, or Purpose is RESALE)

        for (RiskAdjustedItem item : items) {
            String cat = item.getCategory();
            if ("SAFETY".equals(cat)) {
                mustDo.add(item);
            } else if ("STRUCTURAL".equals(cat)) {
                if ("LIVING".equals(context.getPurpose()))
                    shouldDo.add(item);
                else
                    mustDo.add(item); // Resale/Safety
            } else if ("MECHANICAL".equals(cat)) {
                if (item.getRiskFlags().size() > 0)
                    mustDo.add(item);
                else
                    shouldDo.add(item);
            } else {
                // Cosmetic
                if ("RESALE".equals(context.getPurpose()))
                    shouldDo.add(item);
                else
                    skip.add(item);
            }
        }

        // Apply sorting within buckets
        // Actually, Step 6 does the budget cutoff. Step 5 is just ranking?
        // Let's return them loosely bucketed but sorted by Cost descending?
        // No, user output is SortedPlan.

        return SortedPlan.builder()
                .mustDo(mustDo)
                .shouldDo(shouldDo)
                .skipForNow(skip)
                .build();
    }

    private int getCategoryScore(String cat) {
        if ("SAFETY".equals(cat))
            return 100;
        if ("STRUCTURAL".equals(cat))
            return 80;
        if ("MECHANICAL".equals(cat))
            return 60;
        return 20;
    }

    // --- STEP 6: Verdict & Strategic Advice ---
    private Verdict step6_verdictGeneration(SortedPlan plan, UserContext context) {
        double totalRequired = plan.getMustDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();
        double totalOptional = plan.getShouldDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();

        double budget = context.getBudget();
        String tier = "DENIED";
        String headline = "";

        if (budget >= (totalRequired + totalOptional)) {
            tier = "APPROVED";
            headline = "Your budget safely covers required repairs at local 2026 rates.";
        } else if (budget >= totalRequired * 0.9) {
            tier = "WARNING";
            headline = "Budget is tight. Risk of downgraded materials or missing abatement.";
        } else {
            tier = "DENIED";
            headline = "Budget is insufficient for local standards. Focus on safety-critical items only.";
        }

        List<String> futureCostWarning = new ArrayList<>();
        // Generate advice based on Era Risks
        if (context.getEra() != null && riskFactorsData.getEras().containsKey(context.getEra())) {
            EraData era = riskFactorsData.getEras().get(context.getEra());
            // Accessing plain risks to find remedy cost factor not easy with simple list.
            // But we can iterate the items in the plan.
        }

        plan.getMustDo().forEach(item -> {
            if (item.getRiskFlags().size() > 0) {
                futureCostWarning.add("Deferring " + item.getPrettyName() + " (" + item.getCategory()
                        + ") may increase cost by 30-50% due to collateral damage risk.");
            }
        });

        return Verdict.builder()
                .tier(tier)
                .headline(headline)
                .mustDoExplanation(plan.getMustDo().stream().map(i -> i.getPrettyName() + ": " + i.getExplanation())
                        .collect(Collectors.toList()))
                .optionalActions(
                        plan.getShouldDo().stream().map(RiskAdjustedItem::getPrettyName).collect(Collectors.toList()))
                .futureCostWarning(futureCostWarning)
                .upgradeScenario(
                        Collections.singletonList("If budget allows, upgrade HVAC to Heat Pump for tax credits."))
                .plan(plan)
                .build();
    }
}
