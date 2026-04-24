package com.livingcostcheck.home_repair.service.dto.inspection;

import java.util.UUID;

public record InspectionWorkspaceSummary(
        UUID id,
        String caseLabel,
        String propertyAddress,
        String clientLabel,
        String agentLabel,
        String marketLabel,
        String timelineLabel,
        String loanTypeLabel,
        String createdAtLabel,
        String detailUrl) {

    public InspectionWorkspaceSummary {
        caseLabel = defaultText(caseLabel, "Untitled negotiation packet");
        propertyAddress = defaultText(propertyAddress, "Property address not captured");
        clientLabel = defaultText(clientLabel, "Buyer");
        agentLabel = defaultText(agentLabel, "Agent not assigned");
        marketLabel = defaultText(marketLabel, "");
        timelineLabel = defaultText(timelineLabel, "");
        loanTypeLabel = defaultText(loanTypeLabel, "");
        createdAtLabel = defaultText(createdAtLabel, "");
        detailUrl = defaultText(detailUrl, "/home-repair");
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
