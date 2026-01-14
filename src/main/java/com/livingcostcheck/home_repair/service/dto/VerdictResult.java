package com.livingcostcheck.home_repair.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerdictResult {
    private String code; // e.g. URGENT_STRUCTURAL
    private String title; // Display Title
    private String defenseText; // Loss Aversion copy
    private String offenseText; // ROI copy
    private String recommendedAction; // Short action text
}
