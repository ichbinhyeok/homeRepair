package com.livingcostcheck.home_repair.service.dto.inspection;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.LoanType;

import java.util.List;

public record InspectionResponseInput(
        List<String> historyItems,
        List<String> inspectionFindings,
        List<InspectionEvidenceRef> evidenceRefs,
        String evidenceSourceLabel,
        String quoteSupport,
        String closingWindow,
        String contractWorkflow,
        String dealStage,
        String responseDeadlineAt,
        LoanType loanType,
        String caseLabel,
        String propertyAddress,
        String clientName,
        String agentName,
        String marketContextLabel,
        String eraContextLabel,
        String acquisitionEntry) {

    public InspectionResponseInput {
        historyItems = safeList(historyItems);
        inspectionFindings = safeList(inspectionFindings);
        evidenceRefs = evidenceRefs == null ? List.of() : List.copyOf(evidenceRefs);
        evidenceSourceLabel = defaultText(evidenceSourceLabel, "");
        quoteSupport = defaultText(quoteSupport, "NONE");
        closingWindow = defaultText(closingWindow, "FLEXIBLE");
        contractWorkflow = defaultText(contractWorkflow, "AUTO");
        dealStage = defaultText(dealStage, "DRAFTING_FIRST_NOTICE");
        responseDeadlineAt = defaultText(responseDeadlineAt, "");
        loanType = loanType == null ? LoanType.CONVENTIONAL : loanType;
        caseLabel = defaultText(caseLabel, "");
        propertyAddress = defaultText(propertyAddress, "");
        clientName = defaultText(clientName, "");
        agentName = defaultText(agentName, "");
        marketContextLabel = defaultText(marketContextLabel, "");
        eraContextLabel = defaultText(eraContextLabel, "");
        acquisitionEntry = defaultText(acquisitionEntry, "direct");
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
