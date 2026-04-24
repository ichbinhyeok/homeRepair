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

        int urlCount = sitemapGenerator.generateSitemap(
                sitemapPath.toString(),
                List.of(
                        "https://lifeverdict.com/home-repair/verdicts/states/tx.html",
                        "https://lifeverdict.com/home-repair/verdicts/pittsburgh-pa/pre-1950.html",
                        "https://lifeverdict.com/home-repair/verdicts/abilene-tx/pre-1950.html",
                        "https://lifeverdict.com/home-repair/verdicts/states/ca.html"));

        assertThat(urlCount).isEqualTo(44);
        String xml = Files.readString(sitemapPath);
        assertThat(xml).contains("https://lifeverdict.com/home-repair");
        assertThat(xml).contains("https://lifeverdict.com/inspection-response-letter");
        assertThat(xml).contains("https://lifeverdict.com/seller-credit-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/repair-request-vs-seller-credit-after-inspection");
        assertThat(xml).contains("https://lifeverdict.com/what-to-ask-for-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/repair-request-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/inspection-objection-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/inspection-contingency-deadline-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/seller-refused-repairs-after-inspection");
        assertThat(xml).contains("https://lifeverdict.com/reasonable-requests-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/fha-inspection-repairs-seller-credit");
        assertThat(xml).contains("https://lifeverdict.com/home-inspection-repair-addendum");
        assertThat(xml).contains("https://lifeverdict.com/roof-repair-credit-after-inspection");
        assertThat(xml).contains("https://lifeverdict.com/federal-pacific-panel-seller-credit-after-inspection");
        assertThat(xml).contains("https://lifeverdict.com/for-buyer-agents");
        assertThat(xml).contains("https://lifeverdict.com/sample-seller-credit-request-after-home-inspection");
        assertThat(xml).contains("https://lifeverdict.com/fha-va-inspection-repairs-and-seller-credit");
        assertThat(xml).doesNotContain("https://lifeverdict.com/</loc>");
        assertThat(xml).doesNotContain("https://lifeverdict.com/home-repair/verdicts/states/tx.html");
        assertThat(xml).doesNotContain("https://lifeverdict.com/home-repair/verdicts/pittsburgh-pa/pre-1950.html");
        assertThat(xml).doesNotContain("https://lifeverdict.com/home-repair/verdicts/abilene-tx/pre-1950.html");
        assertThat(xml).doesNotContain("https://lifeverdict.com/home-repair/verdicts/states/ca.html");
    }
}
