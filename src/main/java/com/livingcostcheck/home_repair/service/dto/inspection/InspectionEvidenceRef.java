package com.livingcostcheck.home_repair.service.dto.inspection;

import java.util.List;

public record InspectionEvidenceRef(
        String findingLabel,
        List<String> citations) {

    public InspectionEvidenceRef {
        findingLabel = findingLabel == null ? "" : findingLabel.trim();
        citations = citations == null ? List.of() : List.copyOf(citations);
    }
}
