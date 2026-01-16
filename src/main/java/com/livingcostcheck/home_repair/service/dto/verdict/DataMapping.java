package com.livingcostcheck.home_repair.service.dto.verdict;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

public class DataMapping {

    // --- Metro Data ---
    @Data
    @NoArgsConstructor
    public static class MetroMasterData {
        private Map<String, Object> meta;
        private Map<String, MetroCityData> data;

        // Helper method to extract data_authority from meta
        public String getDataAuthority() {
            return meta != null ? (String) meta.get("data_authority") : null;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MetroCityData {
        @JsonProperty("labor_mult")
        private Double laborMult;
        @JsonProperty("mat_logistics")
        private Double matLogistics;
        @JsonProperty("mob_fee")
        private Double mobFee;
        @JsonProperty("disp_tax")
        private Double dispTax;
        @JsonProperty("avg_house")
        private Double avgHouse;
        @JsonProperty("avg_lot")
        private Double avgLot;
        private String foundation;
        private String risk;
        // other fields optional/ignored
    }

    // --- Risk Factors ---
    @Data
    @NoArgsConstructor
    public static class RiskFactorsData {
        private Map<String, Object> meta;
        private Map<String, EraData> eras;
    }

    @Data
    @NoArgsConstructor
    public static class EraData {
        @JsonProperty("era_name")
        private String eraName;
        @JsonProperty("critical_risks")
        private List<RiskItem> criticalRisks;
    }

    @Data
    @NoArgsConstructor
    public static class RiskItem {
        private String item;
        private String severity;
        private String issue;
        @JsonProperty("remedy_cost_factor")
        private Double remedyCostFactor;
        @JsonProperty("inspection_mandatory")
        private Boolean inspectionMandatory;
        @JsonProperty("removal_cost")
        private String removalCost; // "HIGH"

        // Enhanced fields for evidence-based explanations
        private String definition;
        @JsonProperty("damage_scenario")
        private String damageScenario;
        @JsonProperty("remedy_multiplier")
        private Double remedyMultiplier;
        // other fields like priority, probability
    }

    // --- Cost Library ---
    @Data
    @NoArgsConstructor
    public static class CostLibraryData {
        @JsonProperty("project_meta")
        private Map<String, Object> projectMeta;
        @JsonProperty("metro_index_data")
        private Map<String, MetroCityData> metroIndexData; // Seems redundant with Master Data, but kept for full
                                                           // mapping if used independently
        @JsonProperty("construction_item_library")
        private Map<String, Map<String, ConstructionItem>> constructionItemLibrary;
    }

    @Data
    @NoArgsConstructor
    public static class ConstructionItem {
        private String description;
        @JsonProperty("measure_unit")
        private String measureUnit;
        @JsonProperty("material_cost_range")
        private CostRange materialCostRange;
        @JsonProperty("labor_hours_per_unit")
        private Double laborHoursPerUnit;
        @JsonProperty("base_labor_rate_national")
        private Double baseLaborRateNational;
        @JsonProperty("mobilization_base_fee")
        private Double mobilizationBaseFee;
        @JsonProperty("waste_tons_per_unit")
        private Double wasteTonsPerUnit;
        @JsonProperty("mobilization_priority")
        private String mobilizationPriority;
        @JsonProperty("min_project_size")
        private Double minProjectSize;
        @JsonProperty("short_order_multiplier")
        private Double shortOrderMultiplier;
        @JsonProperty("tax_credit_eligible")
        private Boolean taxCreditEligible;
    }

    @Data
    @NoArgsConstructor
    public static class CostRange {
        private Double low;
        private Double high;
    }
}
