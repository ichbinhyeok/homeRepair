package com.livingcostcheck.home_repair.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RiskHubStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validRiskSlugReturnsOk() throws Exception {
        mockMvc.perform(get("/home-repair/risks/knob-and-tube-wiring"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidRiskSlugReturnsNotFound() throws Exception {
        mockMvc.perform(get("/home-repair/risks/not-a-real-risk"))
                .andExpect(status().isNotFound());
    }
}

