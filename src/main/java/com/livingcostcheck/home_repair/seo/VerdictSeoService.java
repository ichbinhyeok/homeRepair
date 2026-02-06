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
        // H1: Informational, descriptive (what this page is about)
        String h1 = String.format("What homeowners typically spend fixing %s homes in %s", eraName, metroName);

        // Title: CTR-Optimized (Curiosity Gap + Urgency + Year)
        // Pattern: "[Era] Home in [City]: $XX,XXX Hidden Costs? (2026)"
        String title = String.format("%s Home Repair Costs in %s: What to Expect (2026)", eraName, metroName);

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
