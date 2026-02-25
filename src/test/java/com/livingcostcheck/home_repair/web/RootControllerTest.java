package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping.MetroMasterData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RootController.class)
public class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerdictEngineService verdictEngineService;

    @Test
    void rootShouldServeHubPage() throws Exception {
        MetroMasterData mockData = new MetroMasterData();
        mockData.setData(
                Map.of("ATLANTA", new com.livingcostcheck.home_repair.service.dto.verdict.DataMapping.MetroCityData()));
        when(verdictEngineService.getMetroMasterData()).thenReturn(mockData);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/hub"));
    }
}
