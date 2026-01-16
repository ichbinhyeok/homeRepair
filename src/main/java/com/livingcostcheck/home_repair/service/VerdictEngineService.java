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
                quantity = scale.getInteriorSqft() * 0.04; // Approx linear feet based on floor area

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
            double disposal = quantity * wasteTons * (scale.getDispTax() * 100.0);

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
            String category = "COSMETIC"; // Default

            // 1. Risk Overlay (MUST BE DONE FIRST)
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

    // --- STEP 6: Verdict & Strategic Advice ---
    private Verdict step6_verdictGeneration(SortedPlan plan, UserContext context) {
        // New Metric: Total Required Cost (Must-Do Only)
        double totalRequired = plan.getMustDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();

        // Optional items are summed for info, but don't block verdict
        double totalOptional = plan.getShouldDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();

        double budget = context.getBudget();
        String tier = "DENIED";
        String headline = "";

        // Verdict Rule: Checked against REQUIRED cost only
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

        List<String> futureCostWarning = new ArrayList<>();
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
