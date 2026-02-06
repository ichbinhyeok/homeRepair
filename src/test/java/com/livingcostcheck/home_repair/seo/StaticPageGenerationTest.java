package com.livingcostcheck.home_repair.seo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class StaticPageGenerationTest {

    @Autowired
    private StaticPageGeneratorService generatorService;

    @TempDir
    Path tempDir;

    @Test
    void generateAllStaticPages() {
        System.out.println("Starting static page generation...");

        List<String> urls = generatorService.generateAllPages(tempDir.toString());
        assertTrue(urls.size() > 0);

        System.out.println("✅ Successfully generated " + urls.size() + " static pages!");
        System.out.println("📁 Location: " + tempDir.toAbsolutePath());
    }
}
