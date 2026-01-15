package com.livingcostcheck.home_repair.service.dto.verdict;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

public class VerdictDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserContext {
        private Double budget;
        private String metroCode;
        private String era; // PRE_1950, 1950_1970, etc.
        private String purpose; // SAFETY, RESALE, LIVING
        private List<String> history; // Item codes
        private String condition; // NONE, MINOR, SEVERE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstimatedScale {
        private double roofingSquares;
        private double hvacTons;
        private double interiorSqft;
        private double exteriorSqft;

        // Local factors
        private double laborMult;
        private double matLogistics;
        private double mobFee;
        private double dispTax;
        private double avgHouseSqft;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCostItem {
        private String itemCode;
        private String category; // e.g., EXTERIOR_STRUCTURAL
        private String description;
        private double materialCost;
        private double laborCost;
        private double mobilization;
        private double disposal;
        private double subtotal;

        // Metadata passed through
        private double wasteTons;
        private Map<String, Object> rawData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAdjustedItem {
        private String itemCode;
        private String prettyName;
        private String category; // SAFETY, STRUCTURAL, MECHANICAL, COSMETIC
        private double adjustedCost;
        private List<String> riskFlags; // "CRITICAL_RISK", "HISTORY_RECHECK"
        private boolean mandatory;
        private String explanation; // Why is this here? (e.g. "Era Risk: Polybutylene")
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortedPlan {
        private List<RiskAdjustedItem> mustDo;
        private List<RiskAdjustedItem> shouldDo;
        private List<RiskAdjustedItem> skipForNow;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Verdict {
        private String tier; // APPROVED, WARNING, DENIED
        private String headline;
        private List<String> mustDoExplanation;
        private List<String> optionalActions;
        private List<String> futureCostWarning;
        private List<String> upgradeScenario;
        private SortedPlan plan;
    }
}
