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
        // Static pages now target the inspection-budget gap:
        // city + era repair budget + what a buyer should verify and negotiate.
        String h1 = String.format("%s home inspection repair budget in %s", eraName, metroName);
        String title = String.format("%s home inspection repair budget in %s: costs, risks, seller credit guide",
                eraName, metroName);

        return new SeoVariant(title, h1);
    }

    /**
     * Generates headers for Dynamic Result Pages (Transactional Intent).
     * Constraint: Must be "Outlook" or "Context Opener". NO "Verdict: High Risk".
     */
    public SeoVariant getDynamicResultHeader(VerdictDTOs.Verdict verdict, String metroName) {
        String title = "Your Inspection Repair Budget | LifeVerdict";
        String h1 = String.format("Your inspection repair budget in %s", metroName);

        return new SeoVariant(title, h1);
    }
}
