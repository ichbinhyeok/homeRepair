package com.livingcostcheck.home_repair.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeRepairJourneySmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void buyingJourneyResultIsNoindexAndSanitized() throws Exception {
        MvcResult verdictSubmit = mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING")
                        .param("loanType", "FHA")
                        .param("budget", "65000")
                        .param("sqft", "1900")
                        .param("condition", "SEVERE")
                        .param("isFpePanel", "true")
                        .param("history", "ROOFING")
                        .param("history", "HVAC"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home-repair/result/*"))
                .andReturn();

        String resultUrl = verdictSubmit.getResponse().getRedirectedUrl();

        mockMvc.perform(get(resultUrl))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Robots-Tag", containsString("noindex")))
                .andExpect(content().string(containsString("Ready-To-Send Seller Credit Packet")))
                .andExpect(content().string(containsString("FHA financing")))
                .andExpect(content().string(not(containsString("12-Month Security Calendar"))))
                .andExpect(content().string(not(containsString("[FINANCIAL RISK PROMOTION]"))))
                .andExpect(content().string(not(containsString("ERA_RISK:"))))
                .andExpect(content().string(not(containsString("ERA_LABOR_ADJUSTMENT:"))));
    }

    @Test
    void riskDetailRouteRendersCanonicalAndNoInternalFlags() throws Exception {
        mockMvc.perform(get("/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<link rel=\"canonical\"")))
                .andExpect(content().string(containsString("Technical Breakdown")))
                .andExpect(content().string(not(containsString("[FINANCIAL RISK PROMOTION]"))))
                .andExpect(content().string(not(containsString("ERA_RISK:"))))
                .andExpect(content().string(not(containsString("ERA_LABOR_ADJUSTMENT:"))));
    }
}
