package com.livingcostcheck.home_repair.service;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class EdgeCaseSimulations {

    @Autowired
    private VerdictEngineService verdictEngineService;

    @BeforeEach
    public void setup() {
        System.out.println("\n" + "=".repeat(80));
    }

    private void runSimulation(String scenarioName, UserContext context) {
        System.out.println("SCENARIO: " + scenarioName);
        System.out.println("INPUT: " + context);
        System.out.println("-".repeat(80));

        Verdict verdict = verdictEngineService.generateVerdict(context);

        System.out.println("VERDICT TIER: " + verdict.getTier());
        System.out.println("HEADLINE: " + verdict.getHeadline());
        System.out.println("STRATEGY USED: " + verdict.getStrategyUsed());
        System.out.println("COST RANGE: " + verdict.getCostRangeLabel());
        System.out.println("EXACT ESTIMATE: $" + String.format("%,.2f", verdict.getExactCostEstimate()));
        System.out.println("ITEMS ANALYZED: " + verdict.getItemsAnalyzed());

        if (verdict.isDealKiller()) {
            System.out.println("\n🔴 DEAL KILLER DETECTED: " + verdict.getDealKillerMessage());
        }

        System.out.println("\nCONTEXT BRIEFING:");
        if (verdict.getContextBriefing() != null) {
            System.out.println("  Regional Risk: " + verdict.getContextBriefing().getRegionalRisk());
            System.out.println("  Era Feature: " + verdict.getContextBriefing().getEraFeature());
            System.out.println("  Labor Market: " + verdict.getContextBriefing().getLaborMarketRate());
        }

        System.out.println("\nPLAN HIGHLIGHTS:");
        if (verdict.getPlan().getMustDo() != null && !verdict.getPlan().getMustDo().isEmpty()) {
            System.out.println("  [Must Do (" + verdict.getPlan().getMustDo().size() + " items)]");
            verdict.getPlan().getMustDo().stream().limit(5).forEach(item -> {
                System.out.println("    - " + item.getPrettyName() +
                        " ($" + String.format("%,.0f", item.getAdjustedCost()) + ") [" + item.getCategory() + "]");
            });
            if (verdict.getPlan().getMustDo().size() > 5) {
                System.out.println("    ... and " + (verdict.getPlan().getMustDo().size() - 5) + " more items.");
            }
        }

        if (verdict.getPlan().getShouldDo() != null && !verdict.getPlan().getShouldDo().isEmpty()) {
            System.out.println("  [Should Do (" + verdict.getPlan().getShouldDo().size() + " items)]");
            verdict.getPlan().getShouldDo().stream().limit(5).forEach(item -> {
                System.out.println("    - " + item.getPrettyName() +
                        " ($" + String.format("%,.0f", item.getAdjustedCost()) + ") [" + item.getCategory() + "]");
            });
            if (verdict.getPlan().getShouldDo().size() > 5) {
                System.out.println("    ... and " + (verdict.getPlan().getShouldDo().size() - 5) + " more items.");
            }
        }

        if (verdict.getExclusionNote() != null && !verdict.getExclusionNote().isEmpty()) {
            System.out.println("\nEXCLUSION NOTES:");
            verdict.getExclusionNote().forEach(note -> System.out.println("  - " + note));
        }

        System.out.println("=".repeat(80) + "\n");
    }

    @Test
    public void runEdgeCaseSimulations() {
        // 1. 초대형 예산 + 최신 집
        runSimulation("1. Ultra High Budget - Brand New House",
                UserContext.builder()
                        .budget(500000.0)
                        .sqft(4500.0)
                        .metroCode("SAN_FRANCISCO_OAKLAND_CA")
                        .era("2010_PRESENT")
                        .relationship(RelationshipToHouse.LIVING)
                        .condition("MINOR")
                        .build());

        // 2. 극소 예산 + 오래된 집
        runSimulation("2. Minimal Budget - Ancient House",
                UserContext.builder()
                        .budget(5000.0)
                        .sqft(1200.0)
                        .metroCode("JACKSON_MS")
                        .era("PRE_1950")
                        .relationship(RelationshipToHouse.BUYING)
                        .condition("SEVERE")
                        .build());

        // 3. 모든 Deal Killer 동시 발생
        runSimulation("3. Multiple Deal Killers",
                UserContext.builder()
                        .budget(200000.0)
                        .sqft(2000.0)
                        .metroCode("MIAMI_FT_LAUDERDALE_FL")
                        .era("1970_1980")
                        .relationship(RelationshipToHouse.BUYING)
                        .isFpePanel(true)
                        .isPolyB(true)
                        .isAluminum(true)
                        .isChineseDrywall(true)
                        .build());

        // 4. 투자자 + 완벽한 이력
        runSimulation("4. Investor - Fully Renovated",
                UserContext.builder()
                        .budget(50000.0)
                        .sqft(2500.0)
                        .metroCode("AUSTIN_ROUND_ROCK_TX")
                        .era("1980_1995")
                        .relationship(RelationshipToHouse.INVESTING)
                        .coreSystemHistory(Arrays.asList("ROOFING", "HVAC", "PLUMBING", "ELECTRICAL"))
                        .condition("MINOR")
                        .build());

        // 5. 초소형 집 + 고비용 도시
        runSimulation("5. Tiny House - Expensive City",
                UserContext.builder()
                        .budget(80000.0)
                        .sqft(800.0)
                        .metroCode("NYC_NEWARK_JERSEY_CITY_NY_NJ")
                        .era("1995_2010")
                        .relationship(RelationshipToHouse.LIVING)
                        .condition("MINOR")
                        .build());

        // 6. 대저택 + 저비용 도시
        runSimulation("6. Mansion - Cheap City",
                UserContext.builder()
                        .budget(150000.0)
                        .sqft(6000.0)
                        .metroCode("OKLAHOMA_CITY_OK")
                        .era("1990_2000")
                        .relationship(RelationshipToHouse.LIVING)
                        .condition("SEVERE")
                        .build());

        // 7. 중간 시대 + 부분 이력
        runSimulation("7. Mid-Era - Partial History",
                UserContext.builder()
                        .budget(100000.0)
                        .sqft(2200.0)
                        .metroCode("DENVER_AURORA_LAKEWOOD_CO")
                        .era("1980_1995")
                        .relationship(RelationshipToHouse.LIVING)
                        .coreSystemHistory(Arrays.asList("ROOFING"))
                        .livingSpaceHistory(Arrays.asList("KITCHEN", "BATHROOM"))
                        .condition("MINOR")
                        .build());

        // 8. 알루미늄 배선만 단독
        runSimulation("8. Aluminum Wiring Only",
                UserContext.builder()
                        .budget(60000.0)
                        .sqft(1800.0)
                        .metroCode("PHOENIX_MESA_CHANDLER_AZ")
                        .era("1970_1980")
                        .relationship(RelationshipToHouse.LIVING)
                        .isAluminum(true)
                        .condition("MINOR")
                        .build());

        // 9. 최신 집 + FPE 패널 (시대 불일치)
        runSimulation("9. Modern House - FPE Panel Anomaly",
                UserContext.builder()
                        .budget(100000.0)
                        .sqft(2500.0)
                        .metroCode("SEATTLE_TACOMA_BELLEVUE_WA")
                        .era("2010_PRESENT")
                        .relationship(RelationshipToHouse.BUYING)
                        .isFpePanel(true)
                        .build());

        // 10. 극단적 상태 + 중간 예산
        runSimulation("10. Extreme Condition - Medium Budget",
                UserContext.builder()
                        .budget(75000.0)
                        .sqft(2000.0)
                        .metroCode("BOSTON_CAMBRIDGE_MA")
                        .era("PRE_1950")
                        .relationship(RelationshipToHouse.LIVING)
                        .condition("SEVERE")
                        .build());
    }
}
