package com.livingcostcheck.home_repair.seo;

import java.util.*;

/**
 * Fragment Library for Dynamic Contextual Injection (DCI)
 * Provides multiple sentence variations for each content section to prevent
 * thin content
 */
public class FragmentLibrary {

    // Climate Context Fragments (10 variations)
    private static final Map<String, List<String>> CLIMATE_FRAGMENTS = Map.of(
            "1", Arrays.asList(
                    "As a humid subtropical location in zone 1, homes face persistent moisture infiltration challenges in building envelopes.",
                    "Zone 1's high humidity levels create elevated risks for mold growth and structural moisture damage.",
                    "The subtropical climate of zone 1 demands rigorous moisture barrier maintenance to prevent envelope degradation."),
            "2A", Arrays.asList(
                    "In zone 2A's hot-humid climate, exterior materials experience accelerated weathering from combined heat and moisture exposure.",
                    "Zone 2A conditions require enhanced ventilation systems to combat moisture accumulation in wall cavities.",
                    "The warm, humid environment of zone 2A accelerates biological growth on exterior surfaces."),
            "3", Arrays.asList(
                    "Zone 3's mixed climate presents seasonal challenges requiring balanced moisture and thermal management strategies.",
                    "The temperate conditions of zone 3 minimize extreme weathering but still demand regular maintenance cycles.",
                    "Zone 3 homes benefit from moderate climate conditions, though seasonal transitions stress building materials."),
            "5A", Arrays.asList(
                    "Severe freeze-thaw cycles in zone 5A necessitate rigorous inspection of foundation footings and exterior masonry.",
                    "Zone 5A's cold climate creates expansion-contraction stress on structural components throughout winter months.",
                    "The harsh winters of zone 5A demand premium-grade insulation and air sealing to prevent thermal bridging."),
            "6", Arrays.asList(
                    "Zone 6's extreme cold requires specialized foundation systems to prevent frost heave and structural movement.",
                    "In zone 6, ice dam formation on roofing systems poses significant water intrusion risks during spring thaw.",
                    "The severe climate of zone 6 accelerates material fatigue through repeated thermal cycling."),
            "B", Arrays.asList(
                    "Dry climate zones experience intense UV exposure that degrades exterior coatings and roofing materials rapidly.",
                    "Arid conditions minimize moisture risks but create challenges with material brittleness and thermal expansion.",
                    "The low-humidity environment reduces biological growth but accelerates UV-driven material breakdown."));

    // Era-Specific Hazard Fragments (8 variations per era)
    private static final Map<String, List<String>> ERA_FRAGMENTS = Map.of(
            "PRE_1950", Arrays.asList(
                    "Maintaining a pre-1950 structure requires careful management of legacy electrical systems and potential lead-based coatings.",
                    "Pre-war construction often features knob-and-tube wiring that fails to meet modern safety standards.",
                    "Homes from this era typically contain asbestos insulation and lead paint requiring specialized remediation.",
                    "The craftsmanship of pre-1950 homes is exceptional, but outdated systems demand comprehensive upgrades."),
            "1950_1970", Arrays.asList(
                    "Mid-century homes from 1950-1970 frequently contain aluminum wiring that poses fire hazards under modern electrical loads.",
                    "This era's construction standards predate energy efficiency codes, resulting in significant thermal losses.",
                    "Homes built during 1950-1970 often feature galvanized steel plumbing nearing end-of-life.",
                    "The post-war building boom of 1950-1970 prioritized speed over longevity in many construction details."),
            "1970_1980", Arrays.asList(
                    "1970s construction coincided with the asbestos era, requiring careful inspection of insulation and flooring materials.",
                    "Homes from this decade often feature polybutylene plumbing that has proven unreliable over time.",
                    "The energy crisis of the 1970s led to experimental building techniques that sometimes created moisture problems.",
                    "1970-1980 era homes frequently require complete HVAC system replacement due to obsolete refrigerants."),
            "1980_1995", Arrays.asList(
                    "Construction from 1980-1995 saw improved energy codes but still predates modern moisture management understanding.",
                    "This era's homes often feature original windows and insulation that no longer meet efficiency standards.",
                    "Federal Pacific electrical panels common in 1980s homes are now recognized as fire hazards.",
                    "The building boom of the 1980s sometimes sacrificed quality for quantity in tract developments."),
            "1995_2010", Arrays.asList(
                    "Homes built 1995-2010 benefit from modern codes but may contain defective Chinese drywall if built 2001-2009.",
                    "This era saw the transition to more efficient HVAC systems, though many are now reaching replacement age.",
                    "Construction from this period generally features reliable systems but aging exterior finishes.",
                    "The housing boom of the 2000s created quality variations depending on builder reputation."),
            "2010_PRESENT", Arrays.asList(
                    "While modern 2010+ eras benefit from updated energy codes, moisture management in tightly sealed envelopes remains a priority.",
                    "Recent construction features advanced building science but requires proper maintenance of complex systems.",
                    "Homes from this era incorporate smart technology that may require specialized service knowledge.",
                    "The latest building codes ensure structural integrity, though long-term material performance remains unproven."));

    // Cost Justification Fragments (7 variations)
    private static final List<String> LOW_COST_FRAGMENTS = Arrays.asList(
            "Stable local labor rates allow for more comprehensive restoration projects within a standard budget.",
            "The competitive contractor market in this region provides cost-effective options for major repairs.",
            "Below-average labor costs make this an opportune location for extensive renovation work.",
            "Regional labor efficiency enables higher-quality work at moderate price points.");

    private static final List<String> HIGH_COST_FRAGMENTS = Arrays.asList(
            "Due to elevated labor costs in this market, prioritizing high-efficiency materials can yield significant long-term ROI.",
            "Premium labor rates in this region necessitate careful project phasing to manage cash flow.",
            "The competitive labor market demands strategic timing of major projects to optimize costs.",
            "Above-average construction costs make material selection and contractor vetting especially critical.");

    /**
     * Select a climate-appropriate fragment based on climate zone
     */
    public static String selectClimateFragment(String climateZone, long seed) {
        if (climateZone == null) {
            return "The local climate conditions require standard maintenance protocols for building envelope integrity.";
        }

        // Find matching zone (handle zones like "2A", "5A", etc.)
        String zoneKey = climateZone.startsWith("1") ? "1"
                : climateZone.startsWith("2") ? "2A"
                        : climateZone.startsWith("3") ? "3"
                                : climateZone.startsWith("5") ? "5A"
                                        : climateZone.startsWith("6") ? "6" : climateZone.contains("B") ? "B" : "3";

        List<String> fragments = CLIMATE_FRAGMENTS.getOrDefault(zoneKey, CLIMATE_FRAGMENTS.get("3"));
        return selectRandomFragment(fragments, seed);
    }

    /**
     * Select an era-appropriate fragment
     */
    public static String selectEraFragment(String era, long seed) {
        List<String> fragments = ERA_FRAGMENTS.getOrDefault(era, ERA_FRAGMENTS.get("1995_2010"));
        return selectRandomFragment(fragments, seed);
    }

    /**
     * Select a cost-appropriate fragment based on labor multiplier
     */
    public static String selectCostFragment(double laborMult, long seed) {
        List<String> fragments = laborMult > 1.1 ? HIGH_COST_FRAGMENTS : LOW_COST_FRAGMENTS;
        return selectRandomFragment(fragments, seed);
    }

    /**
     * Deterministic random selection using seed
     */
    private static String selectRandomFragment(List<String> fragments, long seed) {
        Random random = new Random(seed);
        int index = random.nextInt(fragments.size());
        return fragments.get(index);
    }
}
