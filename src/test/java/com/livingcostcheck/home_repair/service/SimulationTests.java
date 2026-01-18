package com.livingcostcheck.home_repair.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimulationTests {

    private VerdictEngineService engineService;

    @BeforeEach
    public void setup() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        engineService = new VerdictEngineService(resourceLoader, objectMapper);
        engineService.loadData();
    }

    private void runSimulation(String scenarioName, UserContext context) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCENARIO: " + scenarioName);
        System.out.println("INPUT: " + context);
        System.out.println("-".repeat(80));

        try {
            Verdict verdict = engineService.generateVerdict(context);

            System.out.println("VERDICT TIER: " + verdict.getTier());
            System.out.println("HEADLINE: " + verdict.getHeadline());
            System.out.println("STRATEGY USED: " + verdict.getStrategyUsed());
            System.out.println(
                    "COST RANGE: " + (verdict.getCostRange() != null ? verdict.getCostRange().getLabel() : "N/A"));
            System.out.println("EXACT ESTIMATE: $" + String.format("%,.2f", verdict.getExactCostEstimate()));
            System.out.println("ITEMS ANALYZED: " + verdict.getItemsAnalyzed());

            if (verdict.isDealKiller()) {
                System.out.println("🔴 DEAL KILLER DETECTED: " + verdict.getDealKillerMessage());
            }

            if (verdict.getContextBriefing() != null) {
                System.out.println("\nCONTEXT BRIEFING:");
                System.out.println("  Regional Risk: " + verdict.getContextBriefing().getRegionalRisk());
                System.out.println("  Era Feature: " + verdict.getContextBriefing().getEraFeature());
                System.out.println("  Labor Market: " + verdict.getContextBriefing().getLaborMarketRate());
            }

            if (verdict.getPlan() != null) {
                System.out.println("\nPLAN HIGHLIGHTS:");
                printPlanItems("Must Do", verdict.getPlan().getMustDo());
                printPlanItems("Should Do", verdict.getPlan().getShouldDo());
            }

            if (verdict.getExclusionNote() != null && !verdict.getExclusionNote().isEmpty()) {
                System.out.println("\nEXCLUSION NOTES:");
                verdict.getExclusionNote().forEach(note -> System.out.println("  - " + note));
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR RUNNING SCENARIO: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=".repeat(80));
    }

    private void printPlanItems(String title, List<RiskAdjustedItem> items) {
        if (items == null || items.isEmpty())
            return;
        System.out.println("  [" + title + " (" + items.size() + " items)]");
        items.stream().limit(5).forEach(i -> System.out.println("    - " + i.getPrettyName() + " ($"
                + String.format("%,.0f", i.getAdjustedCost()) + ") [" + i.getCategory() + "]"));
        if (items.size() > 5) {
            System.out.println("    ... and " + (items.size() - 5) + " more items.");
        }
    }

    @Test
    public void runAllSimulations() {
        // 🟢 Scenario 1: Happy Path - Sufficient Budget
        runSimulation("1. Sufficient Budget - LIVING User", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .budget(100000.0)
                .metroCode("ATLANTA_SANDY_SPRINGS_GA")
                .era("1990_2000")
                .sqft(2000.0)
                .coreSystemHistory(Collections.singletonList("ROOFING"))
                .condition("MINOR")
                .build());

        // 🟠 Scenario 2: Tight Budget - WARNING
        runSimulation("2. Tight Budget - BUYING User", UserContext.builder()
                .relationship(RelationshipToHouse.BUYING)
                .budget(35000.0)
                .metroCode("CHICAGO_NAPERVILLE_IL")
                .era("1970_1980")
                .sqft(1800.0)
                .condition("SEVERE")
                .build());

        // 🔴 Scenario 3: Deal Killer - Chinese Drywall
        runSimulation("3. Deal Killer - Chinese Drywall", UserContext.builder()
                .relationship(RelationshipToHouse.BUYING)
                .era("1995_2010")
                .isChineseDrywall(true)
                .metroCode("MIAMI_FT_LAUDERDALE_FL")
                .budget(50000.0)
                .build());

        // 🟡 Scenario 4: Era Risk - Old House
        runSimulation("4. Pre-1950 Era Risks", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .era("PRE_1950")
                .metroCode("CHICAGO_NAPERVILLE_IL")
                .budget(80000.0)
                .sqft(2500.0)
                .condition("MINOR")
                .build());

        // 🔵 Scenario 5: Forensic Clue - FPE Panel
        runSimulation("5. Forensic Clue - FPE Panel", UserContext.builder()
                .relationship(RelationshipToHouse.BUYING)
                .era("1970_1980")
                .isFpePanel(true)
                .metroCode("NYC_NEWARK_JERSEY_CITY_NY_NJ")
                .budget(40000.0)
                .build());

        // 🟣 Scenario 6: High-Cost Metro
        runSimulation("6. NYC High Labor Rate", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .metroCode("NYC_NEWARK_JERSEY_CITY_NY_NJ")
                .era("1980_1995")
                .budget(120000.0)
                .sqft(1500.0)
                .condition("SEVERE")
                .build());

        // 🟤 Scenario 7: Climate Risk - Miami
        runSimulation("7. Miami Hurricane/Flood Risk", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .metroCode("MIAMI_FT_LAUDERDALE_FL")
                .era("2010_PRESENT")
                .budget(60000.0)
                .sqft(2200.0)
                .condition("MINOR")
                .build());

        // ⚫ Scenario 8: Polybutylene Pipes
        runSimulation("8. Polybutylene Era Match", UserContext.builder()
                .relationship(RelationshipToHouse.BUYING)
                .era("1980_1995")
                .isPolyB(true)
                .metroCode("ATLANTA_SANDY_SPRINGS_GA")
                .budget(45000.0)
                .build());

        // 🟢 Scenario 9: Investor Strategy
        runSimulation("9. Investor Minimum Cost", UserContext.builder()
                .relationship(RelationshipToHouse.INVESTING)
                .era("1980_1995")
                .metroCode("ATLANTA_SANDY_SPRINGS_GA")
                .budget(30000.0)
                .sqft(1800.0)
                .condition("MINOR")
                .build());

        // 🟠 Scenario 10: Living User History Filter
        runSimulation("10. LIVING User - History Filter", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .era("1980_1995")
                .metroCode("ATLANTA_SANDY_SPRINGS_GA")
                .budget(70000.0)
                .coreSystemHistory(Arrays.asList("ROOFING", "HVAC"))
                .livingSpaceHistory(Collections.singletonList("KITCHEN"))
                .condition("MINOR")
                .build());

        // 🔵 Scenario 11: Aluminum Wiring
        runSimulation("11. Aluminum Wiring Detection", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .era("1970_1980")
                .isAluminum(true)
                .metroCode("CHICAGO_NAPERVILLE_IL")
                .budget(50000.0)
                .build());

        // 🟣 Scenario 12: Small Budget - DENIED
        runSimulation("12. Insufficient Budget", UserContext.builder()
                .relationship(RelationshipToHouse.BUYING)
                .era("PRE_1950")
                .metroCode("NYC_NEWARK_JERSEY_CITY_NY_NJ")
                .budget(15000.0)
                .sqft(2500.0)
                .condition("SEVERE")
                .build());

        // 🟤 Scenario 13: Modern House - Low Risk
        runSimulation("13. Modern House (2010+)", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .era("2010_PRESENT")
                .metroCode("ATLANTA_SANDY_SPRINGS_GA")
                .budget(50000.0)
                .sqft(2000.0)
                .condition("MINOR")
                .build());

        // ⚫ Scenario 14: Forever Home Strategy
        runSimulation("14. LIVING User - Forever Home", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .era("1980_1995")
                .metroCode("MIAMI_FT_LAUDERDALE_FL")
                .budget(150000.0)
                .sqft(3000.0)
                .condition("MINOR")
                .coreSystemHistory(Arrays.asList("ROOFING", "HVAC", "PLUMBING"))
                .build());

        // 🟢 Scenario 15: Missing Sqft - Auto Estimation
        runSimulation("15. Missing Sqft Context", UserContext.builder()
                .relationship(RelationshipToHouse.LIVING)
                .era("1980_1995")
                .metroCode("ATLANTA_SANDY_SPRINGS_GA")
                .budget(60000.0)
                .sqft(null)
                .condition("MINOR")
                .build());
    }
}
