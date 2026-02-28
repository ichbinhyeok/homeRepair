package com.livingcostcheck.home_repair.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextUtilTest {

    @Test
    void formatMetroNameShouldHandleStandardAndFallbackValues() {
        assertEquals("Austin Round Rock, TX", TextUtil.formatMetroName("AUSTIN_ROUND_ROCK_TX"));
        assertEquals("New York, NY", TextUtil.formatMetroName("NEW_YORK_NY"));
        assertEquals("Chicago, IL", TextUtil.formatMetroName("CHICAGO_IL"));
        assertEquals("", TextUtil.formatMetroName(""));
        assertEquals("", TextUtil.formatMetroName(null));
        assertEquals("Unknown", TextUtil.formatMetroName("Unknown"));
    }

    @Test
    void formatEraNameAndTextShouldUseKnownMappings() {
        assertEquals("Pre-1950 (Historic)", TextUtil.formatEraName("PRE_1950"));
        assertEquals("1950-1970 (Mid-Century)", TextUtil.formatEraName("1950_1970"));
        assertEquals("Unknown Era", TextUtil.formatEraText("UNKNOWN"));
        assertEquals("Historic Era", TextUtil.formatEraText("PRE_1950"));
    }

    @Test
    void sanitizeUserFacingExplanationShouldRemoveInternalTokensAndHtml() {
        String raw = "[FINANCIAL RISK PROMOTION] High liability <strong>detected</strong>.";
        String sanitized = TextUtil.sanitizeUserFacingExplanation(raw, null);

        assertFalse(sanitized.contains("[FINANCIAL RISK PROMOTION]"));
        assertFalse(sanitized.contains("<strong>"));
        assertEquals("High liability detected.", sanitized);
    }

    @Test
    void toUserFacingRiskFlagsShouldMapInternalFlags() {
        List<String> mapped = TextUtil.toUserFacingRiskFlags(List.of(
                "ERA_RISK: KNOB_AND_TUBE_WIRING",
                "CRITICAL_SEVERITY_SURCHARGE",
                "MANDATORY_INSPECTION"));

        assertEquals(3, mapped.size());
        assertTrue(mapped.get(0).startsWith("Era-specific hazard:"));
        assertTrue(mapped.contains("High-severity risk"));
        assertTrue(mapped.contains("Professional inspection required"));
    }
}
