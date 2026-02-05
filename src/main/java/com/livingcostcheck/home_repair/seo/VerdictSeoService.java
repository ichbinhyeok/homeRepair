package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Service to centralize SEO Title & H1 logic.
 * Implements the "Two-Faced" strategy:
 * 1. Static (pSEO) -> Informational (Market Benchmark)
 * 2. Dynamic (Result) -> Contextual (Outlook/Verdict)
 */
@Service
public class VerdictSeoService {

    public record SeoVariant(String title, String h1) {
    }

    /**
     * Generates headers for Static pSEO Pages (Informational Intent).
     * Constraint: Must be neutral, purely descriptive. NO "Verdict" or "Analysis"
     * framing.
     */
    public SeoVariant getStaticPageHeader(String metroName, String eraName) {
        // Deterministic randomness based on metro/era combination
        int seed = (metroName + eraName).hashCode();
        int variant = Math.abs(seed % 3);

        String h1;
        String title;

        // Phase 3: CTR Optimization with 3 distinct hooks
        switch (variant) {
            case 0: // The "Hidden Cost" Angle
                title = String.format("%s House in %s? 5 Hidden Costs (2026)", eraName, metroName);
                h1 = String.format("The real cost of fixing up a %s home in %s", eraName, metroName);
                break;
            case 1: // The "Safety/Fear" Angle
                title = String.format("Is it safe to buy a %s home in %s? (Risk Report)", eraName, metroName);
                h1 = String.format("Critical safety risks in %s %s homes", metroName, eraName);
                break;
            default: // The "Question/Curiosity" Angle
                title = String.format("Buying a %s home in %s? Read this first.", eraName, metroName);
                h1 = String.format("What realtors won't tell you about %s homes in %s", eraName, metroName);
                break;
        }

        return new SeoVariant(title, h1);
    }

    /**
     * Generates headers for Dynamic Result Pages (Transactional Intent).
     * Constraint: Must be "Outlook" or "Context Opener". NO "Verdict: High Risk".
     */
    public SeoVariant getDynamicResultHeader(VerdictDTOs.Verdict verdict, String metroName) {
        // Title: Personal & Authoritative
        String title = "Your Home Repair Outlook | LifeVerdict";

        // H1: Context Opener
        // "Your home repair outlook in [City]"
        String h1 = String.format("Your home repair outlook in %s", metroName);

        return new SeoVariant(title, h1);
    }
}
