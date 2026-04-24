package com.livingcostcheck.home_repair.service.dto.inspection;

public record InspectionExclusionItem(
        String findingLabel,
        String reason) {

    public InspectionExclusionItem {
        findingLabel = defaultText(findingLabel, "Excluded item");
        reason = defaultText(reason, "This item stays out of the opening ask.");
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
