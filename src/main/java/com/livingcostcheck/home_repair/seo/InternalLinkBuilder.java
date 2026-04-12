package com.livingcostcheck.home_repair.seo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates internal links for pSEO pages with anchor text variation.
 */
@Slf4j
@Component
public class InternalLinkBuilder {

        private static final List<String> ALL_ERAS = Arrays.asList(
                        "PRE_1950", "1950_1970", "1970_1980", "1980_1995", "1995_2010", "2010_PRESENT");

        private static final List<String> ERA_LINK_PREFIXES = Arrays.asList(
                        "Seller-credit guide for", "Inspection plan for", "Negotiation view for",
                        "Repair request guide for", "What to ask after inspection for",
                        "Buyer checklist for", "Inspection leverage for", "Credit pressure in");

        private static final List<String> RISK_LINK_PREFIXES = Arrays.asList(
                        "Verify", "Inspection note:", "Quote check for", "Buyer flag for",
                        "Credit item:", "Ask support for");

        public List<InternalLink> getOtherRisksInSameHome(String metroCode, String era, String currentRiskCode) {
                List<String> categories = Arrays.asList("ROOFING", "PLUMBING", "HVAC", "ELECTRICAL", "FOUNDATION");
                Random rand = new Random((metroCode + era + currentRiskCode).hashCode());

                return categories.stream()
                                .filter(cat -> !currentRiskCode.toUpperCase(Locale.ENGLISH).contains(cat))
                                .map(cat -> {
                                        String prefix = RISK_LINK_PREFIXES.get(rand.nextInt(RISK_LINK_PREFIXES.size()));
                                        return new InternalLink(
                                                        prefix + " " + cat.toLowerCase(Locale.ENGLISH) + " systems",
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

        /**
         * Keep nearby links semantically close to the current metro.
         * Prefer same-state markets, then metros that share location tokens.
         */
        public List<InternalLink> getNearbyMetrosInEra(String currentMetro, String currentEra,
                        Map<String, ?> allMetros) {
                return allMetros.keySet().stream()
                                .filter(metro -> !metro.equals(currentMetro))
                                .map(metro -> Map.entry(metro, scoreRegionalSimilarity(currentMetro, metro)))
                                .filter(entry -> entry.getValue() > 0)
                                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                                                .reversed()
                                                .thenComparing(Map.Entry::getKey))
                                .limit(5)
                                .map(entry -> new InternalLink(
                                                "Related market: " + formatMetroName(entry.getKey()) + " ("
                                                                + formatEraText(currentEra) + ")",
                                                buildVerdictUrl(entry.getKey(), currentEra)))
                                .collect(Collectors.toList());
        }

        private String buildVerdictUrl(String metro, String era) {
                return "/home-repair/verdicts/" + metro.toLowerCase(Locale.ENGLISH).replace("_", "-") + "/"
                                + era.toLowerCase(Locale.ENGLISH).replace("_", "-") + ".html";
        }

        private String buildRiskUrl(String metro, String era, String riskCode) {
                return "/home-repair/verdicts/" + metro.toLowerCase(Locale.ENGLISH).replace("_", "-") + "/"
                                + era.toLowerCase(Locale.ENGLISH).replace("_", "-") + "/"
                                + riskCode.toLowerCase(Locale.ENGLISH).replace("_", "-");
        }

        public String formatMetroName(String metroCode) {
                String[] parts = metroCode.split("_");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                        String part = parts[i];
                        if (i == parts.length - 1 && part.length() == 2) {
                                result.append(part);
                        } else if (!part.isEmpty()) {
                                result.append(part.substring(0, 1).toUpperCase(Locale.ENGLISH))
                                                .append(part.substring(1).toLowerCase(Locale.ENGLISH));
                        }
                        if (i < parts.length - 1) {
                                result.append(" ");
                        }
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
                if (state == null) {
                        return Collections.emptyList();
                }

                return allMetroCodes.stream()
                                .filter(metro -> metro.endsWith("_" + state))
                                .filter(metro -> !metro.equals(currentMetro))
                                .sorted()
                                .limit(5)
                                .map(metro -> new InternalLink(
                                                "Related negotiation market: " + formatMetroName(metro) + " (" + formatEraText(era)
                                                                + ")",
                                                buildVerdictUrl(metro, era)))
                                .collect(Collectors.toList());
        }

        private int scoreRegionalSimilarity(String currentMetro, String candidateMetro) {
                String currentState = extractStateCode(currentMetro);
                String candidateState = extractStateCode(candidateMetro);
                Set<String> currentTokens = metroSimilarityTokens(currentMetro);
                Set<String> candidateTokens = metroSimilarityTokens(candidateMetro);

                int sharedTokens = 0;
                for (String token : currentTokens) {
                        if (candidateTokens.contains(token)) {
                                sharedTokens++;
                        }
                }

                int score = sharedTokens * 2;
                if (currentState != null && currentState.equals(candidateState)) {
                        score += 5;
                }
                return score;
        }

        private Set<String> metroSimilarityTokens(String metroCode) {
                String[] parts = metroCode.split("_");
                Set<String> tokens = new LinkedHashSet<>();
                for (int i = 0; i < parts.length; i++) {
                        String part = parts[i].toLowerCase(Locale.ENGLISH);
                        if (i == parts.length - 1 && part.length() == 2) {
                                continue;
                        }
                        if (part.length() >= 3) {
                                tokens.add(part);
                        }
                }
                return tokens;
        }

        private String extractStateCode(String metroCode) {
                String[] parts = metroCode.split("_");
                if (parts.length > 0) {
                        String lastPart = parts[parts.length - 1];
                        if (lastPart.length() == 2 && lastPart.matches("[A-Z]{2}")) {
                                return lastPart;
                        }
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
