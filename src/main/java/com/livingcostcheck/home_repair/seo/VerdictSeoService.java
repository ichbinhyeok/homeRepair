package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class VerdictSeoService {

    public record SeoVariant(String title, String h1) {
    }

    public SeoVariant getDynamicResultHeader(VerdictDTOs.Verdict verdict, String metroName) {
        String title = "Your Ready-To-Send Seller Credit Packet | LifeVerdict";
        String h1 = metroName != null && metroName.toLowerCase(Locale.ENGLISH).contains("baseline")
                ? "Your ready-to-send seller credit packet"
                : String.format("Your ready-to-send seller credit packet in %s", metroName);

        return new SeoVariant(title, h1);
    }
}
