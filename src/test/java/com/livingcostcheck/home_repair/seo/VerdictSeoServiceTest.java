package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class VerdictSeoServiceTest {

    private final VerdictSeoService service = new VerdictSeoService();

    @Test
    void getDynamicResultHeader_shouldReturnContextualOutlook() {
        String metro = "Austin";
        VerdictDTOs.Verdict verdict = new VerdictDTOs.Verdict();
        verdict.setTier("high_risk"); // Tier shouldn't be in H1 anymore

        VerdictSeoService.SeoVariant result = service.getDynamicResultHeader(verdict, metro);

        // Verify send-today packet framing (No "High Risk" in H1)
        assertThat(result.h1()).contains("Your ready-to-send seller credit packet");
        assertThat(result.h1()).contains(metro);
        assertThat(result.h1()).doesNotContain("High Risk");
        assertThat(result.h1()).doesNotContain("Analysis");

        // Verify Title
        assertThat(result.title()).contains("LifeVerdict");
    }
}
