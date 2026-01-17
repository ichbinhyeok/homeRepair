package com.livingcostcheck.home_repair.service.dto.verdict;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

public class VerdictDTOs {

    // Strategy Type for Three-Tier Scenario Engine
    public enum StrategyType {
        SAFETY_FLIP, // Tier 1: Minimum cost to pass inspection
        STANDARD_LIVING, // Tier 2: Comfortable living for 5-7 years
        FOREVER_HOME // Tier 3: Maximum asset value appreciation
    }

    // Relationship context for Money-First Decision Engine
    public enum RelationshipToHouse {
        LIVING, // Owner-Occupied (Now/Next/Later)
        BUYING, // Under Contract (Deal Killer)
        INVESTING // Flip/Rental (ROI/Risk)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserContext {
        private Double budget;
        private String metroCode;
        private String era; // PRE_1950, 1950_1970, etc.
        private RelationshipToHouse relationship; // New Primary Context
        private String purpose; // DEPRECATED: Kept for backward compat until full refactor
        // Context-Aware History (Phases 5 & 6)
        private List<String> coreSystemHistory; // Roof, HVAC, Elec, Plumbing (Safety/Risk)
        private List<String> livingSpaceHistory; // Kitchen, Bath, Flooring (Comfort/Cosmetic)

        @Deprecated
        private List<String> history; // Deprecated but kept for backward compatibility

        private String condition; // NONE, MINOR, SEVERE

        // Phase 4: Forensic Clues (Brand/Era specific observations)
        private Boolean isFpePanel; // Federal Pacific Electric / Zinsco panel
        private Boolean isPolyB; // Polybutylene plumbing pipes
        private Boolean isAluminum; // Aluminum wiring
        private Boolean isChineseDrywall; // Sulfur-emitting defective drywall (2001-2009)
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
        private String mobilizationPriority; // PRIMARY, SECONDARY
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
        private String compoundingBadge; // e.g., "RISK COMPOUNDING APPLIED (1.3x)"
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

    // Strategic Option representing one of three tiers
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrategyOption {
        private StrategyType strategyType;
        private String name; // e.g., "The Safety/Flip Baseline"
        private String description; // e.g., "Minimum cost to pass inspection"
        private String goal; // e.g., "Safe to sell or rent quickly"
        private double totalCost;
        private SortedPlan plan;
        private List<String> includedCategories; // e.g., ["SAFETY", "CRITICAL"]
        private String materialGrade; // e.g., "Budget (Asphalt Shingles)"
        private List<String> keyHighlights; // e.g., ["Only mandatory repairs", "Budget materials"]
        private String negotiationCopy; // Copy-pasteable text for real estate agents
        private double negotiationLeverage; // 1.5x of critical cost
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Verdict {
        private String tier; // APPROVED, WARNING, DENIED (based on budget vs minimum cost)
        private String headline;
        private List<StrategyOption> strategyOptions; // The 3 tiers
        private List<String> exclusionNote; // Transparency: Why items are missing (e.g "Kitchen skipped")

        private List<String> mustDoExplanation; // Kept for backward compatibility
        private List<String> optionalActions;
        private List<String> futureCostWarning;
        private List<String> upgradeScenario;
        private SortedPlan plan; // Kept for backward compatibility (defaults to STANDARD_LIVING)
    }
}
