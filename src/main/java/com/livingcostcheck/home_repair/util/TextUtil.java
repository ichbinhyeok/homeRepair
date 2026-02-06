package com.livingcostcheck.home_repair.util;

import org.springframework.util.StringUtils;

public class TextUtil {

    /**
     * Formats a metro code (e.g., "new_york_ny") into a displayable string (e.g.,
     * "New York, NY").
     */
    public static String formatMetroName(String metroCode) {
        if (metroCode == null || metroCode.isEmpty())
            return "";

        String[] parts = metroCode.split("_");
        // Fallback for single word or unexpected format
        if (parts.length < 2)
            return StringUtils.capitalize(metroCode.toLowerCase());

        // Separate state code (last part)
        String state = parts[parts.length - 1].toUpperCase();

        // Join the rest as city name
        StringBuilder city = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            // Capitalize each part properly (e.g., AUSTIN -> Austin)
            city.append(StringUtils.capitalize(parts[i].toLowerCase())).append(" ");
        }

        return city.toString().trim() + ", " + state;
    }

    /**
     * Formats an era code (e.g., "1950_1970") into a displayable string (e.g.,
     * "1950-1970 (Mid-Century)").
     */
    public static String formatEraName(String era) {
        if (era == null)
            return "";
        switch (era) {
            case "PRE_1950":
                return "Pre-1950 (Historic)";
            case "1950_1970":
                return "1950-1970 (Mid-Century)";
            case "1970_1980":
                return "1970-1980 (Industrial)";
            case "1980_1995":
                return "1980-1995 (Transitional)";
            case "1995_2010":
                return "1995-2010 (McMansion)";
            case "2010_PRESENT":
                return "2010-Present (Modern)";
            default:
                return era;
        }
    }

    /**
     * Formats an era code into a shorter text for filenames or internal usage.
     */
    public static String formatEraText(String era) {
        if (era == null)
            return "";
        switch (era) {
            case "PRE_1950":
                return "Historic Era";
            case "1950_1970":
                return "Mid-Century Era";
            case "1970_1980":
                return "1970s Era";
            case "1980_1995":
                return "1980s Era";
            case "1995_2010":
                return "Boom Era";
            case "2010_PRESENT":
                return "Modern Era";
            default:
                return "Unknown Era";
        }
    }
}
