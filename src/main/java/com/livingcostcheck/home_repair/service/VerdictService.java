package com.livingcostcheck.home_repair.service;

import com.livingcostcheck.home_repair.service.dto.VerdictResult;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VerdictService {

    private static final Set<String> OLD_DECADES = Set.of("1990s", "1980s", "Pre-1980");

    public VerdictResult determineVerdict(String budgetStr, String decade, String purpose) {
        // Parse Budget
        int budget = parseBudget(budgetStr);
        boolean isOldHouse = OLD_DECADES.contains(decade);
        boolean isSellPurpose = "Flip/Sell".equalsIgnoreCase(purpose);
        boolean isEmergency = "Emergency".equalsIgnoreCase(purpose);

        // -------------------------------------------------------------
        // PRIORITY HIERARCHY
        // 1. Structural (Old + High Budget) OR Emergency
        // 2. Visual (Low Budget OR Selling)
        // 3. Operational (Default)
        // -------------------------------------------------------------

        // 1. Structural Check
        if (isEmergency || (isOldHouse && budget >= 20000)) {
            return VerdictResult.builder()
                    .code("URGENT_STRUCTURAL_REPAIR")
                    .title("FOUNDATION FIRST")
                    .defenseText("Ignoring structural integrity in a " + decade + " property is financial suicide. " +
                            "A $20k cosmetic renovation on a failing substructure will result in a near-total loss of value within 3 years.")
                    .offenseText(
                            "Stabilizing the core systems (Roof, Foundation, Electrical) locks in the asset value. " +
                                    "Once secured, every subsequent dollar boosts equity by 1.5x.")
                    .recommendedAction("Allocated 100% to Core Systems")
                    .build();
        }

        // 2. Visual Check
        if (budget < 5000 || isSellPurpose) {
            return VerdictResult.builder()
                    .code("MAXIMIZE_VISUAL_IMPACT")
                    .title("SURFACE LEVEL ONLY")
                    .defenseText(
                            "Your budget/goal does not support deep renovation. Opening walls now will expose expensive problems you cannot afford to fix. "
                                    +
                                    "Do NOT touch plumbing or electrical.")
                    .offenseText(
                            "Focus entirely on Paint, Lighting, and Hardware. These items have the highest 'Showroom ROI' for a quick turnover. "
                                    +
                                    "Make it look new, do not make it new.")
                    .recommendedAction("Paint, Fixtures, Flooring")
                    .build();
        }

        // 3. Operational Check (Default)
        return VerdictResult.builder()
                .code("OPERATIONAL_EFFICIENCY")
                .title("FUNCTION OVER FORM")
                .defenseText("Inefficient windows and HVAC are bleeding your monthly cash flow. " +
                        "Cosmetic upgrades while bleeding energy costs is a vanity project, not an investment.")
                .offenseText(
                        "Upgrade Windows, Insulation, or HVAC. This reduces holding costs immediately and appeals to smart buyers who look at utility bills. "
                                +
                                "This is the safe, middle-ground equity play.")
                .recommendedAction("HVAC, Windows, Insulation")
                .build();
    }

    private int parseBudget(String budgetStr) {
        try {
            return Integer.parseInt(budgetStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public String getCalculationRedirectUrl(String currentZip, String currentBudget, String currentDecade,
            String currentPurpose, String scenario) {
        int budgetVal = parseBudget(currentBudget);
        String newBudget = currentBudget;
        String newPurpose = currentPurpose;

        if ("boost_budget".equals(scenario)) {
            newBudget = String.valueOf(budgetVal + 5000);
        } else if ("sell_mode".equals(scenario)) {
            newPurpose = "Flip/Sell";
        }

        // Simple manual standard form encoding for MVP
        return String.format("/home-repair/calculate?zipCode=%s&budget=%s&purpose=%s&decade=%s",
                currentZip, newBudget, newPurpose, currentDecade);
    }
}
