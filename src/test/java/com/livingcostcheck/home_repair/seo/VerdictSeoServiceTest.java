package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class VerdictSeoServiceTest {

    private final VerdictSeoService service = new VerdictSeoService();

    @Test
    void getStaticPageHeader_shouldReturnInformationalContent() {
        String metro = "Austin";
        String era = "1950s";

        VerdictSeoService.SeoVariant result = service.getStaticPageHeader(metro, era);

        // Verify informational H1 aligned to city + era inspection-budget intent.
        assertThat(result.h1()).contains("home inspection repair budget in");
        assertThat(result.h1()).contains(metro);
        assertThat(result.h1()).contains(era);

        // Verify title emphasizes inspection budget + negotiation intent.
        assertThat(result.title()).contains("costs");
        assertThat(result.title()).contains("seller credit guide");
        assertThat(result.title()).doesNotContain("Avoid Hidden Costs:");
        assertThat(result.title()).doesNotContain("(2026 Audit)");
    }

    @Test
    void getDynamicResultHeader_shouldReturnContextualOutlook() {
        String metro = "Austin";
        VerdictDTOs.Verdict verdict = new VerdictDTOs.Verdict();
        verdict.setTier("high_risk"); // Tier shouldn't be in H1 anymore

        VerdictSeoService.SeoVariant result = service.getDynamicResultHeader(verdict, metro);

        // Verify inspection-budget framing (No "High Risk" in H1)
        assertThat(result.h1()).contains("Your inspection repair budget");
        assertThat(result.h1()).contains(metro);
        assertThat(result.h1()).doesNotContain("High Risk");
        assertThat(result.h1()).doesNotContain("Analysis");

        // Verify Title
        assertThat(result.title()).contains("LifeVerdict");
    }
}
