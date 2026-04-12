package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerdictSeoService {

    public record SeoVariant(String title, String h1) {
    }

    public SeoVariant getStaticPageHeader(String metroName, String eraName) {
        String h1 = String.format("Seller credit after inspection for %s homes in %s", eraName, metroName);
        String title = String.format("%s %s seller credit after inspection | LifeVerdict",
                metroName, eraName);

        return new SeoVariant(title, h1);
    }

    public SeoVariant getDynamicResultHeader(VerdictDTOs.Verdict verdict, String metroName) {
        String title = "Your Seller Credit Plan | LifeVerdict";
        String h1 = String.format("Your seller credit plan in %s", metroName);

        return new SeoVariant(title, h1);
    }
}
