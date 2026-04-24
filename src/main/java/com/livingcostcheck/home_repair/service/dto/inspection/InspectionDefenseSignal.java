package com.livingcostcheck.home_repair.service.dto.inspection;

public record InspectionDefenseSignal(
        String status,
        String label,
        String detail,
        String action) {

    public InspectionDefenseSignal {
        status = defaultText(status, "WARN").toUpperCase();
        label = defaultText(label, "Defense check");
        detail = defaultText(detail, "This part of the packet needs review.");
        action = defaultText(action, "Confirm before external send.");
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
