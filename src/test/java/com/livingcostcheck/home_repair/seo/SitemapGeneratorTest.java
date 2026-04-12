package com.livingcostcheck.home_repair.seo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SitemapGeneratorTest {

    private final SitemapGenerator sitemapGenerator = new SitemapGenerator();

    @Test
    void generateSitemapKeepsOnlyWinnerUrls(@TempDir Path tempDir) throws IOException {
        Path sitemapPath = tempDir.resolve("static").resolve("sitemap.xml");

        sitemapGenerator.generateSitemap(
                sitemapPath.toString(),
                List.of(
                        "https://lifeverdict.com/home-repair/verdicts/states/tx.html",
                        "https://lifeverdict.com/home-repair/verdicts/pittsburgh-pa/pre-1950.html",
                        "https://lifeverdict.com/home-repair/verdicts/abilene-tx/pre-1950.html",
                        "https://lifeverdict.com/home-repair/verdicts/states/ca.html"));

        String xml = Files.readString(sitemapPath);
        assertThat(xml).contains("https://lifeverdict.com/home-repair");
        assertThat(xml).contains("https://lifeverdict.com/home-repair/verdicts/states/tx.html");
        assertThat(xml).contains("https://lifeverdict.com/home-repair/verdicts/pittsburgh-pa/pre-1950.html");
        assertThat(xml).doesNotContain("https://lifeverdict.com/home-repair/verdicts/abilene-tx/pre-1950.html");
        assertThat(xml).doesNotContain("https://lifeverdict.com/home-repair/verdicts/states/ca.html");
    }
}
