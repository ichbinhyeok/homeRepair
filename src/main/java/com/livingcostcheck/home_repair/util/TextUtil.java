package com.livingcostcheck.home_repair.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class TextUtil {
    private static final Pattern INTERNAL_TOKEN_PATTERN = Pattern.compile("\\[[A-Z_ ]+\\]\\s*");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

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
     * Formats a metro code into an MSA-style label for user-facing copy.
     * Example: "austin_round_rock_tx" -> "Austin Round Rock, TX MSA"
     */
    public static String formatMsaName(String metroCode) {
        String metroName = formatMetroName(metroCode);
        if (metroName.isBlank()) {
            return "";
        }
        return metroName + " MSA";
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

    /**
     * Removes internal markers and HTML tags from explanatory copy before rendering.
     */
    public static String sanitizeUserFacingExplanation(String primary, String fallback) {
        String value = (primary != null && !primary.isBlank()) ? primary : fallback;
        if (value == null) {
            return "";
        }

        String cleaned = INTERNAL_TOKEN_PATTERN.matcher(value).replaceAll("");
        cleaned = HTML_TAG_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = cleaned.replaceAll("\\s{2,}", " ").trim();
        // Remove accidental spaces before punctuation after tag/token stripping.
        return cleaned.replaceAll("\\s+([,.;:!?])", "$1");
    }

    /**
     * Converts internal risk flags into user-facing, plain-language labels.
     */
    public static List<String> toUserFacingRiskFlags(List<String> rawFlags) {
        if (rawFlags == null || rawFlags.isEmpty()) {
            return List.of();
        }

        Set<String> deduped = new LinkedHashSet<>();
        for (String rawFlag : rawFlags) {
            if (rawFlag == null || rawFlag.isBlank()) {
                continue;
            }
            deduped.add(toUserFacingRiskFlag(rawFlag));
        }
        return new ArrayList<>(deduped);
    }

    private static String toUserFacingRiskFlag(String rawFlag) {
        String normalized = rawFlag.trim();
        String upper = normalized.toUpperCase(Locale.ENGLISH);

        if (upper.startsWith("ERA_RISK:")) {
            String value = valueAfterColon(normalized);
            return "Era-specific hazard: " + humanizeToken(value);
        }
        if (upper.startsWith("ERA_LABOR_ADJUSTMENT:")) {
            String value = valueAfterColon(normalized);
            return "Complexity adjustment: " + value;
        }
        if (upper.startsWith("FORENSIC_CONFIRMATION:")) {
            String value = valueAfterColon(normalized);
            return "Confirmed issue: " + humanizeToken(value);
        }
        if (upper.startsWith("STATISTICALLY_DEAD:")) {
            String value = valueAfterColon(normalized).replace("_LIFESPAN", "").replace("_", " ");
            return "Past expected lifespan: " + value;
        }
        if (upper.startsWith("WATCH:")) {
            return "Near end of expected lifespan";
        }
        if (upper.startsWith("VERIFIED_UPDATE:")) {
            return "System recently updated";
        }
        if (upper.startsWith("SAFETY_OVERRIDE:")) {
            return "Safety override required";
        }
        if ("CRITICAL_SEVERITY_SURCHARGE".equals(upper)) {
            return "High-severity risk";
        }
        if ("MANDATORY_INSPECTION".equals(upper)) {
            return "Professional inspection required";
        }
        if ("HAZMAT_REMOVAL".equals(upper)) {
            return "Hazardous material handling may be required";
        }

        return humanizeToken(normalized);
    }

    private static String valueAfterColon(String value) {
        int idx = value.indexOf(':');
        if (idx < 0 || idx == value.length() - 1) {
            return value.trim();
        }
        return value.substring(idx + 1).trim();
    }

    private static String humanizeToken(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }

        String[] parts = token.trim().replace("-", "_").split("[_\\s]+");
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(' ');
            }

            String upper = part.toUpperCase(Locale.ENGLISH);
            if (upper.length() <= 4 && upper.matches("[A-Z0-9]+")) {
                out.append(upper);
            } else {
                out.append(upper.substring(0, 1));
                out.append(upper.substring(1).toLowerCase(Locale.ENGLISH));
            }
        }

        String text = out.toString();
        text = text.replace("Hvac", "HVAC")
                .replace("Pvc", "PVC")
                .replace("Fpe", "FPE")
                .replace("PolyB", "Poly-B");
        return text;
    }
}
