package com.livingcostcheck.home_repair.service.dto.verdict;

import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class StateHubPage {
    public String stateCode;
    public String stateName;
    public String canonicalUrl;
    public String breadcrumbSchema;
    public List<CityData> cities;

    @Data
    @AllArgsConstructor
    public static class CityData {
        public String name;
        public List<InternalLink> eras;
    }
}
