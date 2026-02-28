package com.livingcostcheck.home_repair.seo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FragmentLibraryTest {

    @Test
    void selectClimateFragment_handles2BAsDryClimate() {
        String fragment = FragmentLibrary.selectClimateFragment("2B", 42L).toLowerCase();

        assertFalse(fragment.contains("hot-humid"));
        assertTrue(fragment.contains("dry")
                || fragment.contains("arid")
                || fragment.contains("uv")
                || fragment.contains("low-humidity"));
    }

    @Test
    void generateRegionalInsight_doesNotMislabel2BAs2A() {
        String insight = FragmentLibrary.generateRegionalInsight(
                "2B",
                "PRE_1950",
                0.88,
                "Phoenix, AZ",
                77L).toLowerCase();

        assertFalse(insight.contains("zone 2a"));
        assertFalse(insight.contains("hot-humid"));
        assertTrue(insight.contains("zone 2b") || insight.contains("dry"));
    }
}
