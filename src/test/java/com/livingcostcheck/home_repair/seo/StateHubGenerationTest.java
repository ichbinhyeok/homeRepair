package com.livingcostcheck.home_repair.seo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test to generate State Hub pages
 */
@SpringBootTest
public class StateHubGenerationTest {

    @Autowired
    private StateHubGeneratorService stateHubGeneratorService;

    @Test
    void generateAllStateHubs() {
        System.out.println("Starting State Hub generation...");

        int count = stateHubGeneratorService.generateAllStateHubs(
                "src/main/resources/static/home-repair/verdicts");

        System.out.println("✅ Successfully generated " + count + " State Hub pages!");
        System.out.println("📁 Location: src/main/resources/static/home-repair/verdicts/states/");
        System.out.println("🎯 Each hub includes:");
        System.out.println("   - State-level cost statistics");
        System.out.println("   - Climate analysis");
        System.out.println("   - Links to all cities in the state");
    }
}
