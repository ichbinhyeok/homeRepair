package com.livingcostcheck.home_repair.service.dto.inspection;

import java.util.List;

public record InspectionResponsePacket(
        String title,
        String subtitle,
        List<String> originalFindings,
        List<String> mustFixNow,
        List<String> verifyNext,
        List<String> doNotLead,
        List<InspectionEvidenceRef> matchedEvidence,
        String evidenceSourceLabel,
        List<String> evidenceChecklist,
        List<String> notWorthAsking,
        List<InspectionExclusionItem> excludedFindings,
        String quoteSupportLabel,
        String closingWindowLabel,
        String loanTypeLabel,
        String loanTypeNote,
        String responseDeadlineNote,
        String evidenceNote,
        String dataAnchor,
        String dataAnchorNote,
        List<String> pricingBreakdown,
        List<String> lenderVisibleSignals,
        String lenderVisibleNote,
        String readinessLabel,
        String readinessNote,
        List<InspectionReadinessGate> readinessGates,
        List<InspectionDefenseSignal> defenseSignals,
        List<String> verdictRationale,
        List<String> missingEvidence,
        List<String> reviewCaveats,
        int readinessPassCount,
        int readinessWarnCount,
        int readinessFailCount,
        int confidenceScore,
        List<String> confidenceReasons,
        String workflowLabel,
        String workflowTitle,
        String workflowNote,
        List<String> workflowSteps,
        List<String> nextActions,
        double defendableAsk,
        double targetAsk,
        double stretchAsk,
        String defendableAskLabel,
        String targetAskLabel,
        String stretchAskLabel,
        String sellerCreditSummary,
        String agentNegotiationScript,
        String fallbackScript,
        String fullPacketText) {

    public InspectionResponsePacket {
        title = defaultText(title, "Response Packet Ready");
        subtitle = defaultText(subtitle, "A buyer-side response packet built from the inspection findings.");
        originalFindings = safeList(originalFindings);
        mustFixNow = safeList(mustFixNow);
        verifyNext = safeList(verifyNext);
        doNotLead = safeList(doNotLead);
        matchedEvidence = matchedEvidence == null ? List.of() : List.copyOf(matchedEvidence);
        evidenceSourceLabel = defaultText(evidenceSourceLabel, "");
        evidenceChecklist = safeList(evidenceChecklist);
        notWorthAsking = safeList(notWorthAsking);
        excludedFindings = excludedFindings == null ? List.of() : List.copyOf(excludedFindings);
        quoteSupportLabel = defaultText(quoteSupportLabel, "");
        closingWindowLabel = defaultText(closingWindowLabel, "");
        loanTypeLabel = defaultText(loanTypeLabel, "");
        loanTypeNote = defaultText(loanTypeNote, "");
        responseDeadlineNote = defaultText(responseDeadlineNote, "");
        evidenceNote = defaultText(evidenceNote, "");
        dataAnchor = defaultText(dataAnchor, "Contextual repair-cost range");
        dataAnchorNote = defaultText(dataAnchorNote, "");
        pricingBreakdown = safeList(pricingBreakdown);
        lenderVisibleSignals = safeList(lenderVisibleSignals);
        lenderVisibleNote = defaultText(lenderVisibleNote, "");
        readinessLabel = defaultText(readinessLabel, "Needs review");
        readinessNote = defaultText(readinessNote, "");
        readinessGates = readinessGates == null ? List.of() : List.copyOf(readinessGates);
        defenseSignals = defenseSignals == null ? List.of() : List.copyOf(defenseSignals);
        verdictRationale = safeList(verdictRationale);
        missingEvidence = safeList(missingEvidence);
        reviewCaveats = safeList(reviewCaveats);
        confidenceReasons = safeList(confidenceReasons);
        workflowLabel = defaultText(workflowLabel, "Negotiation packet workflow");
        workflowTitle = defaultText(workflowTitle, "How to move this into the real file");
        workflowNote = defaultText(workflowNote, "");
        workflowSteps = safeList(workflowSteps);
        nextActions = safeList(nextActions);
        defendableAskLabel = defaultText(defendableAskLabel, "0");
        targetAskLabel = defaultText(targetAskLabel, "0");
        stretchAskLabel = defaultText(stretchAskLabel, "0");
        sellerCreditSummary = defaultText(sellerCreditSummary, "");
        agentNegotiationScript = defaultText(agentNegotiationScript, "");
        fallbackScript = defaultText(fallbackScript, "");
        fullPacketText = defaultText(fullPacketText, "");
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
