package com.livingcostcheck.home_repair.service.dto.verdict;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifespanData {
    private Map<String, ItemLifespan> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemLifespan {
        private int standard_lifespan;
        private int warning_threshold;
        private int critical_threshold;
        private String pretty_name;
        private String impact_description;
    }
}
