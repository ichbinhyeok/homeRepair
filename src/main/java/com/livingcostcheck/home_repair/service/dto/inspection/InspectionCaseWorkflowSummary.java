package com.livingcostcheck.home_repair.service.dto.inspection;

import java.util.List;

public record InspectionCaseWorkflowSummary(
        String currentState,
        String currentLabel,
        String currentNote,
        String recommendedNextAction,
        List<InspectionCaseWorkflowEvent> timeline) {

    public InspectionCaseWorkflowSummary {
        currentState = defaultText(currentState, "DRAFT");
        currentLabel = defaultText(currentLabel, "Draft");
        currentNote = defaultText(currentNote, "");
        recommendedNextAction = defaultText(recommendedNextAction, "");
        timeline = timeline == null ? List.of() : List.copyOf(timeline);
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
