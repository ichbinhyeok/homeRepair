package com.livingcostcheck.home_repair.service.dto.inspection;

public record InspectionReadinessGate(
        String status,
        String label,
        String note) {

    public InspectionReadinessGate {
        status = defaultText(status, "WARN");
        label = defaultText(label, "Readiness gate");
        note = defaultText(note, "");
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
