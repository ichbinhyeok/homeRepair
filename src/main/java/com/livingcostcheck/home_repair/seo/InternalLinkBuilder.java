package com.livingcostcheck.home_repair.seo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates internal links for pSEO pages with Anchor Text Variation
 */
@Slf4j
@Component
public class InternalLinkBuilder {

        private static final List<String> ALL_ERAS = Arrays.asList(
                        "PRE_1950", "1950_1970", "1970_1980", "1980_1995", "1995_2010", "2010_PRESENT");

        private static final List<String> ERA_LINK_PREFIXES = Arrays.asList(
                        "View repair costs for", "Analysis: ", "Renovation guide: ", "Estimated costs for",
                        "Market data for", "Expected upkeep for", "Valuation impact of", "Maintenance index: ");

        private static final List<String> RISK_LINK_PREFIXES = Arrays.asList(
                        "Check details for", "Audit: ", "Forensic view: ", "Inspection points for",
                        "Critical data: ", "Market benchmarks for");

        // L2 Cross-Linking (Mesh)
        public List<InternalLink> getOtherRisksInSameHome(String metroCode, String era, String currentRiskCode) {
                List<String> categories = Arrays.asList("ROOFING", "PLUMBING", "HVAC", "ELECTRICAL", "FOUNDATION");
                Random rand = new Random((metroCode + era + currentRiskCode).hashCode());

                return categories.stream()
                                .filter(cat -> !currentRiskCode.toUpperCase().contains(cat)) // 현재 리스크 제외
                                .map(cat -> {
                                        String prefix = RISK_LINK_PREFIXES.get(rand.nextInt(RISK_LINK_PREFIXES.size()));
                                        return new InternalLink(
                                                        prefix + " " + cat.toLowerCase() + " systems",
                                                        buildRiskUrl(metroCode, era, cat));
                                })
                                .collect(Collectors.toList());
        }

        public List<InternalLink> getOtherErasInCity(String currentMetro, String currentEra) {
                Random rand = new Random((currentMetro + currentEra).hashCode());
                return ALL_ERAS.stream()
                                .filter(era -> !era.equals(currentEra))
                                .map(era -> {
                                        String prefix = ERA_LINK_PREFIXES.get(rand.nextInt(ERA_LINK_PREFIXES.size()));
                                        return new InternalLink(
                                                        prefix + " " + formatEraText(era) + " homes in "
                                                                        + formatMetroName(currentMetro),
                                                        buildVerdictUrl(currentMetro, era));
                                })
                                .collect(Collectors.toList());
        }

        // Smart Regional Mapping: Use State-based matching instead of manual map
        public List<InternalLink> getNearbyMetrosInEra(String currentMetro, String currentEra,
                        Map<String, ?> allMetros) {
                String state = extractStateCode(currentMetro);
                if (state == null)
                        return getDefaultNearbyMetros(currentMetro, currentEra);

                return allMetros.keySet().stream()
                                .filter(m -> m.endsWith("_" + state) && !m.equals(currentMetro))
                                .limit(5)
                                .map(metro -> new InternalLink(
                                                "Local Comp: " + formatMetroName(metro) + " ("
                                                                + formatEraText(currentEra) + ")",
                                                buildVerdictUrl(metro, currentEra)))
                                .collect(Collectors.toList());
        }

        private List<InternalLink> getDefaultNearbyMetros(String currentMetro, String era) {
                List<String> majorMetros = Arrays.asList(
                                "ATLANTA_SANDY_SPRINGS_GA", "BOSTON_CAMBRIDGE_MA", "CHICAGO_NAPERVILLE_IL",
                                "DALLAS_FT_WORTH_ARLINGTON_TX", "HOUSTON_THE_WOODLANDS_TX", "LOS_ANGELES_LONG_BEACH_CA",
                                "NEW_YORK_NEWARK_NJ", "SAN_FRANCISCO_OAKLAND_CA");

                return majorMetros.stream()
                                .filter(m -> !m.equals(currentMetro))
                                .limit(5)
                                .map(metro -> new InternalLink(
                                                "Market Comparison: " + formatMetroName(metro),
                                                buildVerdictUrl(metro, era)))
                                .collect(Collectors.toList());
        }

        private String buildVerdictUrl(String metro, String era) {
                return "/home-repair/verdicts/" + metro.toLowerCase().replace("_", "-") + "/"
                                + era.toLowerCase().replace("_", "-") + ".html";
        }

        private String buildRiskUrl(String metro, String era, String riskCode) {
                return "/home-repair/verdicts/" + metro.toLowerCase().replace("_", "-") + "/"
                                + era.toLowerCase().replace("_", "-") + "/" + riskCode.toLowerCase().replace("_", "-")
                                + ".html";
        }

        public String formatMetroName(String metroCode) {
                String[] parts = metroCode.split("_");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                        String part = parts[i];
                        if (i == parts.length - 1 && part.length() == 2)
                                result.append(part);
                        else if (part.length() > 0)
                                result.append(part.substring(0, 1).toUpperCase())
                                                .append(part.substring(1).toLowerCase());
                        if (i < parts.length - 1)
                                result.append(" ");
                }
                return result.toString();
        }

        public String formatEraText(String era) {
                switch (era) {
                        case "PRE_1950":
                                return "Pre-1950";
                        case "1950_1970":
                                return "1950s-1970s";
                        case "1970_1980":
                                return "1970s";
                        case "1980_1995":
                                return "1980s-1990s";
                        case "1995_2010":
                                return "1995-2010";
                        case "2010_PRESENT":
                                return "2010-Present";
                        default:
                                return era;
                }
        }

        public List<InternalLink> getRelatedCitiesInState(String currentMetro, String era, Set<String> allMetroCodes) {
                String state = extractStateCode(currentMetro);
                if (state == null)
                        return Collections.emptyList();

                return allMetroCodes.stream()
                                .filter(metro -> metro.endsWith("_" + state))
                                .filter(metro -> !metro.equals(currentMetro))
                                .limit(8)
                                .map(metro -> new InternalLink(
                                                "Regional Data: " + formatMetroName(metro) + " (" + formatEraText(era)
                                                                + ")",
                                                buildVerdictUrl(metro, era)))
                                .collect(Collectors.toList());
        }

        private String extractStateCode(String metroCode) {
                String[] parts = metroCode.split("_");
                if (parts.length > 0) {
                        String lastPart = parts[parts.length - 1];
                        if (lastPart.length() == 2 && lastPart.matches("[A-Z]{2}"))
                                return lastPart;
                }
                return null;
        }

        public static class InternalLink {
                public final String text;
                public final String href;

                public InternalLink(String text, String href) {
                        this.text = text;
                        this.href = href;
                }

                public String getText() {
                        return text;
                }

                public String getHref() {
                        return href;
                }
        }
}
