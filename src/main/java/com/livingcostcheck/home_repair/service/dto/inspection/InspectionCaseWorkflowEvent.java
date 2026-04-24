package com.livingcostcheck.home_repair.service.dto.inspection;

public record InspectionCaseWorkflowEvent(
        String label,
        String note,
        String timestampLabel) {

    public InspectionCaseWorkflowEvent {
        label = defaultText(label, "Workflow event");
        note = defaultText(note, "");
        timestampLabel = defaultText(timestampLabel, "");
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
