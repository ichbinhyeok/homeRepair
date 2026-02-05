package com.livingcostcheck.home_repair.seo;

import com.fasterxml.jackson.databind.JsonNode;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class GlossaryGeneratorService {

    private final VerdictEngineService verdictEngineService;
    private final TemplateEngine templateEngine;

    public int generateAllGlossaryPages(String outputBasePath) {
        log.info("Starting Glossary Page generation (Phase 4)...");
        
        // 1. Extract all unique risks from the JSON data
        Map<String, JsonNode> uniqueRisks = extractUniqueRisks();
        int count = 0;

        // 2. Generate a page for each risk
        for (Map.Entry<String, JsonNode> entry : uniqueRisks.entrySet()) {
            try {
                generateGlossaryPage(entry.getKey(), entry.getValue(), outputBasePath);
                count++;
            } catch (Exception e) {
                log.error("Failed to generate Glossary for {}: {}", entry.getKey(), e.getMessage());
            }
        }
        
        return count;
    }

    private Map<String, JsonNode> extractUniqueRisks() {
        Map<String, JsonNode> risks = new HashMap<>();
        JsonNode root = verdictEngineService.getRiskData();

        if (root == null || !root.has("eras")) return risks;

        root.get("eras").forEach(eraNode -> {
            if (eraNode.has("critical_risks")) {
                eraNode.get("critical_risks").forEach(risk -> {
                    String itemCode = risk.get("item").asText();
                    // Store the first occurrence (or merge if needed, but simple for now)
                    risks.putIfAbsent(itemCode, risk);
                });
            }
        });
        return risks;
    }

    private void generateGlossaryPage(String itemCode, JsonNode riskData, String outputBasePath) throws IOException {
        String slug = itemCode.toLowerCase().replace("_", "-");
        String title = prettify(itemCode);
        
        // Prepare template params
        Map<String, Object> params = new HashMap<>();
        params.put("title", title + " - Home Repair Glossary | LifeVerdict");
        params.put("h1", "What is " + title + "?");
        params.put("riskData", riskData);
        params.put("canonicalUrl", "https://lifeverdict.com/home-repair/guides/" + slug + ".html");

        // Render
        StringOutput output = new StringOutput();
        templateEngine.render("seo/glossary.jte", params, output);

        // Write to file
        Path path = Paths.get(outputBasePath + "/guides/" + slug + ".html");
        Files.createDirectories(path.getParent());
        Files.writeString(path, output.toString());
    }

    private String prettify(String code) {
        return Arrays.stream(code.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(code);
    }
}
