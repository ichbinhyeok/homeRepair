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

        // Verify Informational Intent (No "Verdict" or "Risk" in H1)
        assertThat(result.h1()).contains("What homeowners typically spend fixing");
        assertThat(result.h1()).contains(metro);
        assertThat(result.h1()).contains(era);

        // Verify Curiosity Gap in Title
        assertThat(result.title()).contains("Can I really afford");
        assertThat(result.title()).contains("2026 Costs");
    }

    @Test
    void getDynamicResultHeader_shouldReturnContextualOutlook() {
        String metro = "Austin";
        VerdictDTOs.Verdict verdict = new VerdictDTOs.Verdict();
        verdict.setTier("high_risk"); // Tier shouldn't be in H1 anymore

        VerdictSeoService.SeoVariant result = service.getDynamicResultHeader(verdict, metro);

        // Verify "Outlook" framing (No "High Risk" in H1)
        assertThat(result.h1()).contains("Your home repair outlook");
        assertThat(result.h1()).contains(metro);
        assertThat(result.h1()).doesNotContain("High Risk");
        assertThat(result.h1()).doesNotContain("Analysis");

        // Verify Title
        assertThat(result.title()).contains("LifeVerdict");
    }
}
