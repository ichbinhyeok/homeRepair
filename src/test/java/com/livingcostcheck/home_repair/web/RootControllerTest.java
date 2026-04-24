package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.service.AcquisitionTelemetryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RootController.class)
public class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AcquisitionTelemetryService acquisitionTelemetryService;

    @Test
    void rootShouldRedirectToSingleAcquisitionLanding() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("/inspection-response-letter"));
    }

    @Test
    void rootShouldPreserveRequestedVariantWhenRedirecting() throws Exception {
        mockMvc.perform(get("/").param("v", "credit"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("/seller-credit-after-home-inspection"));
    }

    @Test
    void inspectionResponseLetterLandingShouldServeHubPage() throws Exception {
        mockMvc.perform(get("/inspection-response-letter"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/hub"))
                .andExpect(model().attribute("surface", AcquisitionSurface.LETTER));
    }

    @Test
    void inspectionResponseLetterLegacyVariantShouldRedirectToDedicatedSurface() throws Exception {
        mockMvc.perform(get("/inspection-response-letter").param("v", "credit"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("/seller-credit-after-home-inspection"));
    }

    @Test
    void sellerCreditSurfaceShouldServeHubPage() throws Exception {
        mockMvc.perform(get("/seller-credit-after-home-inspection"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/hub"))
                .andExpect(model().attribute("surface", AcquisitionSurface.CREDIT));
    }

    @Test
    void expandedAcquisitionSurfaceShouldServeHubPage() throws Exception {
        mockMvc.perform(get("/roof-repair-credit-after-inspection"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/hub"))
                .andExpect(model().attribute("surface", AcquisitionSurface.ROOF_CREDIT));
    }

    @Test
    void buyerAgentCommercialPageShouldServeDedicatedTemplate() throws Exception {
        mockMvc.perform(get("/for-buyer-agents"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/for-buyer-agents"));
    }

    @Test
    void samplePacketPageShouldServeDedicatedTemplate() throws Exception {
        mockMvc.perform(get("/sample-seller-credit-request-after-home-inspection"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/sample-packet"));
    }

    @Test
    void financingPageShouldServeDedicatedTemplate() throws Exception {
        mockMvc.perform(get("/fha-va-inspection-repairs-and-seller-credit"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/fha-va-inspection-repairs"));
    }
}
