package com.livingcostcheck.home_repair.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.Collections;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.DefaultResourceLoader;

public class VerdictEngineTest {

    private VerdictEngineService engineService;

    @BeforeEach
    public void setup() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        engineService = new VerdictEngineService(resourceLoader, objectMapper);
        engineService.loadData();
    }

    @Test
    public void testMandatoryDryRun() {
        // Scenario:
        // - Metro: AUSTIN_ROUND_ROCK_TX
        // - Era: 1980_1995
        // - Budget: $10,000
        // - History: [ROOFING]
        // - Purpose: LIVING

        UserContext context = UserContext.builder()
                .metroCode("AUSTIN_ROUND_ROCK_TX")
                .era("1980_1995")
                .budget(10000.0)
                .history(Collections.singletonList("ROOFING"))
                .condition("NONE") // History condition NONE implies removal
                .purpose("LIVING")
                .build();

        Verdict verdict = engineService.generateVerdict(context);

        System.out.println("Verdict Tier: " + verdict.getTier());
        System.out.println("Headline: " + verdict.getHeadline());
        System.out.println("Must Do: " + verdict.getPlan().getMustDo());
        System.out.println("Should Do: " + verdict.getPlan().getShouldDo());

        // Assertions

        // 1. Verdict should NOT be DENIED (Budget $10k is healthy for plumbing repair)
        // Note: Logic says if Budget >= Total, APPROVED.
        // Polybutylene repipe is ~ 1500sqft * 10-18$/lf ... rough cost.
        Assertions.assertNotEquals("DENIED", verdict.getTier());

        // 2. Roofing should be missing from Must Do (Removed by History)
        boolean roofingPresent = verdict.getPlan().getMustDo().stream()
                .anyMatch(i -> i.getItemCode().contains("ROOFING"));
        Assertions.assertFalse(roofingPresent, "Roofing should be removed due to history 'NONE'");

        // 3. Plumbing should be present and flagged as Safety/Critical or at least Must
        // Do
        boolean plumbingPresent = verdict.getPlan().getMustDo().stream()
                .anyMatch(
                        i -> i.getItemCode().contains("PLUMBING") && i.getRiskFlags().toString().contains("ERA_RISK"));
        Assertions.assertTrue(plumbingPresent, "Polybutylene Plumbing check/repipe should be present and flagged");

        // 4. Verify Cost is reasonable (not 0, not billion)
        double totalCost = verdict.getPlan().getMustDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum();
        Assertions.assertTrue(totalCost > 1000 && totalCost < 20000,
                "Cost should be reasonable for plumbing job: " + totalCost);
    }
}
