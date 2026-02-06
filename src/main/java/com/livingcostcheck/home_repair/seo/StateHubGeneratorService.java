package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates State Hub Pages for improved SEO structure
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StateHubGeneratorService {

    private final VerdictEngineService verdictEngineService;
    private final TemplateEngine templateEngine;

    // State metadata
    private static final Map<String, String> STATE_NAMES = Map.ofEntries(
            Map.entry("TX", "Texas"),
            Map.entry("CA", "California"),
            Map.entry("FL", "Florida"),
            Map.entry("NY", "New York"),
            Map.entry("PA", "Pennsylvania"),
            Map.entry("IL", "Illinois"),
            Map.entry("OH", "Ohio"),
            Map.entry("GA", "Georgia"),
            Map.entry("NC", "North Carolina"),
            Map.entry("MI", "Michigan"),
            Map.entry("NJ", "New Jersey"),
            Map.entry("VA", "Virginia"),
            Map.entry("WA", "Washington"),
            Map.entry("AZ", "Arizona"),
            Map.entry("MA", "Massachusetts"),
            Map.entry("TN", "Tennessee"),
            Map.entry("IN", "Indiana"),
            Map.entry("MO", "Missouri"),
            Map.entry("MD", "Maryland"),
            Map.entry("WI", "Wisconsin"),
            Map.entry("CO", "Colorado"),
            Map.entry("MN", "Minnesota"),
            Map.entry("SC", "South Carolina"),
            Map.entry("AL", "Alabama"),
            Map.entry("LA", "Louisiana"),
            Map.entry("KY", "Kentucky"),
            Map.entry("OR", "Oregon"),
            Map.entry("OK", "Oklahoma"),
            Map.entry("CT", "Connecticut"),
            Map.entry("UT", "Utah"),
            Map.entry("IA", "Iowa"),
            Map.entry("NV", "Nevada"),
            Map.entry("AR", "Arkansas"),
            Map.entry("MS", "Mississippi"),
            Map.entry("KS", "Kansas"),
            Map.entry("NM", "New Mexico"),
            Map.entry("NE", "Nebraska"),
            Map.entry("WV", "West Virginia"),
            Map.entry("ID", "Idaho"),
            Map.entry("HI", "Hawaii"),
            Map.entry("NH", "New Hampshire"),
            Map.entry("ME", "Maine"),
            Map.entry("RI", "Rhode Island"),
            Map.entry("MT", "Montana"),
            Map.entry("DE", "Delaware"),
            Map.entry("SD", "South Dakota"),
            Map.entry("ND", "North Dakota"),
            Map.entry("AK", "Alaska"),
            Map.entry("VT", "Vermont"),
            Map.entry("WY", "Wyoming"));

    private static final Map<String, String> CLIMATE_DESCRIPTIONS = Map.ofEntries(
            Map.entry("TX",
                    "Hot-humid climate with high UV exposure and moisture management challenges. Freeze-thaw cycles in northern regions."),
            Map.entry("CA",
                    "Diverse climate zones from coastal Mediterranean to inland desert. Seismic considerations for structural work."),
            Map.entry("FL",
                    "Tropical and subtropical zones with extreme humidity, hurricane exposure, and saltwater corrosion risks."),
            Map.entry("NY", "Cold winters with freeze-thaw cycles. Coastal areas face moisture and salt exposure."),
            Map.entry("IL", "Continental climate with severe freeze-thaw cycles and temperature extremes."),
            Map.entry("AZ", "Arid desert climate with intense UV degradation and minimal moisture risks."),
            Map.entry("WA",
                    "Marine climate with high precipitation and moderate temperatures. Moisture control critical."));

    public int generateAllStateHubs(String outputBasePath) {
        log.info("Starting State Hub page generation...");

        Map<String, ?> allMetros = verdictEngineService.getMetroMasterData().getData();

        // Group metros by state
        Map<String, List<String>> metrosByState = new HashMap<>();
        for (String metroCode : allMetros.keySet()) {
            String stateCode = extractStateCode(metroCode);
            if (stateCode != null) {
                metrosByState.computeIfAbsent(stateCode, k -> new ArrayList<>()).add(metroCode);
            }
        }

        int successCount = 0;
        for (Map.Entry<String, List<String>> entry : metrosByState.entrySet()) {
            String stateCode = entry.getKey();
            List<String> metroCodes = entry.getValue();

            try {
                generateStateHub(stateCode, metroCodes, outputBasePath);
                successCount++;
                log.info("Generated State Hub: {}", STATE_NAMES.getOrDefault(stateCode, stateCode));
            } catch (Exception e) {
                log.error("Failed to generate State Hub for {}: {}", stateCode, e.getMessage());
            }
        }

        log.info("State Hub Generation Complete! {} hubs created", successCount);
        return successCount;
    }

    private void generateStateHub(String stateCode, List<String> metroCodes, String outputBasePath) throws IOException {
        String stateName = STATE_NAMES.getOrDefault(stateCode, stateCode);
        String climateDesc = CLIMATE_DESCRIPTIONS.getOrDefault(stateCode,
                "Regional climate conditions require standard maintenance protocols for building envelope integrity.");

        // Create CityInfo objects (Name + Slug)
        List<CityInfo> cities = metroCodes.stream()
                .map(code -> new CityInfo(formatMetroName(code), code.toLowerCase().replace("_", "-")))
                .sorted(Comparator.comparing(CityInfo::name))
                .collect(Collectors.toList());

        // Calculate average costs by era (simplified - using placeholder data)
        Map<String, Double> avgCostByEra = Map.of(
                "Pre-1950", 18000.0,
                "1950-1970", 15000.0,
                "1970-1980", 14000.0,
                "1980-1995", 12000.0,
                "1995-2010", 10000.0,
                "2010-Present", 8000.0);

        // Prepare template data
        Map<String, Object> templateData = new HashMap<>();

        // State Hub SEO Strategy (Navigational / High-Level Consideration)
        String seoTitle = String.format("Is it safe to buy a fixer-upper in %s?", stateName);
        String seoH1 = String.format("What to consider before fixing a home in %s", stateName);

        templateData.put("title", seoTitle);
        templateData.put("h1", seoH1);
        templateData.put("stateName", stateName);
        templateData.put("stateCode", stateCode);
        templateData.put("cities", cities);
        templateData.put("avgCostByEra", avgCostByEra);
        templateData.put("climateDescription", climateDesc);
        templateData.put("baseUrl", "https://lifeverdict.com");
        templateData.put("canonicalUrl", "https://lifeverdict.com/home-repair/states/" + stateCode.toLowerCase());

        // Render HTML
        StringOutput output = new StringOutput();
        templateEngine.render("seo/state-hub.jte", templateData, output);
        String html = output.toString();

        // Write to file
        Path filePath = Paths.get(outputBasePath, "states", stateCode.toLowerCase() + ".html");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, html);

        log.debug("Generated State Hub: {}", filePath);
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

    private String formatMetroName(String metroCode) {
        String[] parts = metroCode.split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == parts.length - 1 && part.length() == 2) {
                // Skip state code for display name
                continue;
            }
            result.append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1).toLowerCase());
            if (i < parts.length - 2) {
                result.append(" ");
            }
        }

        return result.toString().trim();
    }

    // Inner class for template data
    public static record CityInfo(String name, String slug) {
    }
}
