package com.livingcostcheck.home_repair.seo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates internal links for pSEO pages to create a mesh network
 * preventing orphan pages and improving crawlability
 */
@Slf4j
@Component
public class InternalLinkBuilder {

        // Era definitions
        private static final List<String> ALL_ERAS = Arrays.asList(
                        "PRE_1950",
                        "1950_1970",
                        "1970_1980",
                        "1980_1995",
                        "1995_2010",
                        "2010_PRESENT");

        // Metro geographic groupings for "nearby cities"
        private static final Map<String, List<String>> METRO_REGIONS = createMetroRegions();

        private static Map<String, List<String>> createMetroRegions() {
                Map<String, List<String>> regions = new HashMap<>();

                // Texas Triangle
                regions.put("AUSTIN_ROUND_ROCK_TX", Arrays.asList(
                                "SAN_ANTONIO_NEW_BRAUNFELS_TX", "HOUSTON_THE_WOODLANDS_TX",
                                "DALLAS_FT_WORTH_ARLINGTON_TX"));
                regions.put("DALLAS_FT_WORTH_ARLINGTON_TX", Arrays.asList(
                                "AUSTIN_ROUND_ROCK_TX", "SAN_ANTONIO_NEW_BRAUNFELS_TX", "HOUSTON_THE_WOODLANDS_TX"));
                regions.put("HOUSTON_THE_WOODLANDS_TX", Arrays.asList(
                                "AUSTIN_ROUND_ROCK_TX", "SAN_ANTONIO_NEW_BRAUNFELS_TX",
                                "DALLAS_FT_WORTH_ARLINGTON_TX"));

                // California Coast
                regions.put("LOS_ANGELES_LONG_BEACH_CA", Arrays.asList(
                                "SAN_DIEGO_CHULA_VISTA_CA", "RIVERSIDE_SAN_BERNARDINO_CA", "ANAHEIM_SANTA_ANA_CA"));
                regions.put("SAN_FRANCISCO_OAKLAND_CA", Arrays.asList(
                                "SAN_JOSE_SUNNYVALE_CA", "SACRAMENTO_ROSEVILLE_CA", "OAKLAND_BERKELEY_CA"));

                // Northeast Corridor
                regions.put("NEW_YORK_NEWARK_NJ", Arrays.asList(
                                "PHILADELPHIA_CAMDEN_PA", "BOSTON_CAMBRIDGE_MA", "BRIDGEPORT_STAMFORD_CT"));
                regions.put("BOSTON_CAMBRIDGE_MA", Arrays.asList(
                                "NEW_YORK_NEWARK_NJ", "PROVIDENCE_WARWICK_RI", "HARTFORD_WEST_HARTFORD_CT"));

                // Midwest
                regions.put("CHICAGO_NAPERVILLE_IL", Arrays.asList(
                                "DETROIT_WARREN_DEARBORN_MI", "MILWAUKEE_WAUKESHA_WI", "INDIANAPOLIS_CARMEL_IN"));

                // Pacific Northwest
                regions.put("SEATTLE_TACOMA_WA", Arrays.asList(
                                "PORTLAND_VANCOUVER_OR_WA", "SPOKANE_SPOKANE_VALLEY_WA", "EUGENE_OR"));

                // Southeast
                regions.put("ATLANTA_SANDY_SPRINGS_GA", Arrays.asList(
                                "CHARLOTTE_CONCORD_NC", "NASHVILLE_DAVIDSON_TN", "BIRMINGHAM_HOOVER_AL"));

                // Default: use major metros if no specific region defined
                return regions;
        }

        /**
         * Get internal links to other eras in the same city
         * 
         * @param currentMetro The current metro code
         * @param currentEra   The current era
         * @return List of link objects (text + href)
         */
        public List<InternalLink> getOtherErasInCity(String currentMetro, String currentEra) {
                return ALL_ERAS.stream()
                                .filter(era -> !era.equals(currentEra))
                                .map(era -> new InternalLink(
                                                formatEraText(era) + " homes in " + formatMetroName(currentMetro),
                                                buildVerdictUrl(currentMetro, era)))
                                .collect(Collectors.toList());
        }

        /**
         * Get internal links to nearby cities in the same era
         * 
         * @param currentMetro The current metro code
         * @param currentEra   The current era
         * @return List of link objects (text + href)
         */
        public List<InternalLink> getNearbyMetrosInEra(String currentMetro, String currentEra) {
                List<String> nearbyMetros = METRO_REGIONS.getOrDefault(currentMetro,
                                getDefaultNearbyMetros(currentMetro));

                return nearbyMetros.stream()
                                .limit(5) // Max 5 nearby cities
                                .map(metro -> new InternalLink(
                                                "Compare with " + formatMetroName(metro) + " ("
                                                                + formatEraText(currentEra) + ")",
                                                buildVerdictUrl(metro, currentEra)))
                                .collect(Collectors.toList());
        }

        private List<String> getDefaultNearbyMetros(String currentMetro) {
                // Fallback to major metros if no specific region defined
                List<String> majorMetros = Arrays.asList(
                                "ATLANTA_SANDY_SPRINGS_GA",
                                "BOSTON_CAMBRIDGE_MA",
                                "CHICAGO_NAPERVILLE_IL",
                                "DALLAS_FT_WORTH_ARLINGTON_TX",
                                "HOUSTON_THE_WOODLANDS_TX",
                                "LOS_ANGELES_LONG_BEACH_CA",
                                "MIAMI_FT_LAUDERDALE_FL",
                                "PHILADELPHIA_PA_NJ",
                                "PHOENIX_MESA_CHANDLER_AZ",
                                "SAN_ANTONIO_NEW_BRAUNFELS_TX",
                                "SAN_DIEGO_CHULA_VISTA_CA",
                                "SAN_FRANCISCO_OAKLAND_CA",
                                "SAN_JOSE_SUNNYVALE_CA",
                                "SEATTLE_TACOMA_BELLEVUE_WA",
                                "WASHINGTON_ARLINGTON_DC_VA");
                return majorMetros.stream()
                                .filter(m -> !m.equals(currentMetro))
                                .limit(5)
                                .collect(Collectors.toList());
        }

        private String buildVerdictUrl(String metro, String era) {
                return "/home-repair/verdicts/" + metro.toLowerCase().replace("_", "-") + "/"
                                + era.toLowerCase().replace("_", "-") + ".html";
        }

        private String formatMetroName(String metroCode) {
                // Convert AUSTIN_ROUND_ROCK_TX → Austin Round Rock TX
                String[] parts = metroCode.split("_");
                StringBuilder result = new StringBuilder();

                for (int i = 0; i < parts.length; i++) {
                        String part = parts[i];
                        // Last part is probably state code, keep it uppercase
                        if (i == parts.length - 1 && part.length() == 2) {
                                result.append(part);
                        } else {
                                // Capitalize first letter, lowercase rest
                                result.append(part.substring(0, 1).toUpperCase())
                                                .append(part.substring(1).toLowerCase());
                        }
                        if (i < parts.length - 1) {
                                result.append(" ");
                        }
                }

                return result.toString();
        }

        private String formatEraText(String era) {
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

        /**
         * Get related cities in the same state (Hub-and-Spoke structure)
         */
        public List<InternalLink> getRelatedCitiesInState(String currentMetro, String era) {
                String state = extractStateCode(currentMetro);
                if (state == null) {
                        return Collections.emptyList();
                }

                return METRO_REGIONS.keySet().stream()
                                .filter(metro -> metro.endsWith("_" + state))
                                .filter(metro -> !metro.equals(currentMetro))
                                .limit(8)
                                .map(metro -> new InternalLink(
                                                formatMetroName(metro) + " (" + formatEraText(era) + ")",
                                                buildVerdictUrl(metro, era)))
                                .collect(Collectors.toList());
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

        /**
         * Internal link data structure
         */
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
