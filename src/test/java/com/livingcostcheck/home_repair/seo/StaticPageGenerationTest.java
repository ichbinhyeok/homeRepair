package com.livingcostcheck.home_repair.seo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test to generate all 702 static pSEO pages
 */
@SpringBootTest
public class StaticPageGenerationTest {

    @Autowired
    private StaticPageGeneratorService staticPageGeneratorService;

    @Test
    void generateAllStaticPages() {
        System.out.println("Starting static page generation...");

        int count = staticPageGeneratorService.generateAllPages(
                "src/main/resources/static/home-repair/verdicts");

        System.out.println("✅ Successfully generated " + count + " static pages!");
        System.out.println("📁 Location: src/main/resources/static/home-repair/verdicts/");
        System.out.println("🎯 Each page includes:");
        System.out.println("   - Dynamic Contextual Injection (DCI)");
        System.out.println("   - FAQ Schema for Rich Snippets");
        System.out.println("   - Hub-and-Spoke internal links");
    }
}
