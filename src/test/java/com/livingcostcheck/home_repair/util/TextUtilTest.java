package com.livingcostcheck.home_repair.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextUtilTest {

    @Test
    void testFormatMetroName() {
        assertEquals("Austin Round Rock, TX", TextUtil.formatMetroName("AUSTIN_ROUND_ROCK_TX"));
        assertEquals("New York, NY", TextUtil.formatMetroName("NEW_YORK_NY"));
        assertEquals("Chicago, IL", TextUtil.formatMetroName("CHICAGO_IL"));
        assertEquals("", TextUtil.formatMetroName(""));
        assertEquals("", TextUtil.formatMetroName(null));
        assertEquals("Unknown", TextUtil.formatMetroName("Unknown")); // Fallback behavior if < 2 parts? Let's check
                                                                      // impl.
        // Impl says: if parts.length < 2 return capitalize(metroCode).
    }

    @Test
    void testFormatEraName() {
        assertEquals("Pre-1950 (Historic)", TextUtil.formatEraName("PRE_1950"));
        assertEquals("1950-1970 (Mid-Century)", TextUtil.formatEraName("1950_1970"));
        assertEquals("Unknown Era", TextUtil.formatEraName("Unknown Era"));
    }

    @Test
    void testFormatEraText() {
        assertEquals("Historic Era", TextUtil.formatEraText("PRE_1950"));
        assertEquals("Mid-Century Era", TextUtil.formatEraText("1950_1970"));
    }
}
