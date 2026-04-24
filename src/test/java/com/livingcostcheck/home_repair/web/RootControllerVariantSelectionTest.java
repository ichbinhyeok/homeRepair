package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.service.AcquisitionTelemetryService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RootControllerVariantSelectionTest {

    @Test
    void configuredDefaultVariantOwnsCanonicalLanding() {
        RootController controller = new RootController("credit", mock(AcquisitionTelemetryService.class));

        RedirectView redirect = controller.index(null);
        assertEquals("/seller-credit-after-home-inspection", redirect.getUrl());

        ExtendedModelMap model = new ExtendedModelMap();
        assertEquals("pages/hub", controller.sellerCreditAfterHomeInspection(model));
        assertEquals(AcquisitionSurface.CREDIT, model.getAttribute("surface"));
    }

    @Test
    void nonDefaultVariantKeepsExplicitQueryParam() {
        RootController controller = new RootController("credit", mock(AcquisitionTelemetryService.class));

        RedirectView redirect = controller.index("ask");
        assertEquals("/what-to-ask-for-after-home-inspection", redirect.getUrl());
    }
}
