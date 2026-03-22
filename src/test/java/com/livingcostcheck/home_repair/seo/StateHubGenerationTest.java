package com.livingcostcheck.home_repair.seo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StateHubGenerationTest {

    @Autowired
    private StaticPageGeneratorService staticPageGeneratorService;

    @Test
    void generateStaticStateHubsIntoTempOutput(@TempDir Path tempDir) throws IOException {
        Path outputDir = tempDir.resolve("home-repair").resolve("verdicts");

        int count = staticPageGeneratorService.generateStateHubPages(outputDir.toString());

        assertThat(count).isGreaterThan(0);
        assertThat(Files.exists(outputDir.resolve("states"))).isTrue();
        try (var files = Files.list(outputDir.resolve("states"))) {
            assertThat(files.findAny()).isPresent();
        }
    }
}
