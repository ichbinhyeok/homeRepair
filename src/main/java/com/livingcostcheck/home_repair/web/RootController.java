package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.service.AcquisitionTelemetryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootController {

    private static final String BASE_URL = "https://lifeverdict.com";

    private final AcquisitionSurface defaultSurface;
    private final AcquisitionTelemetryService acquisitionTelemetryService;

    public RootController(
            @Value("${app.acquisition.default-surface:letter}") String defaultSurfaceCode,
            AcquisitionTelemetryService acquisitionTelemetryService) {
        this.defaultSurface = AcquisitionSurface.defaultSurface(defaultSurfaceCode);
        this.acquisitionTelemetryService = acquisitionTelemetryService;
    }

    @GetMapping("/")
    public RedirectView index(@RequestParam(value = "v", required = false) String legacyVariantCode) {
        AcquisitionSurface surface = legacyVariantCode == null || legacyVariantCode.isBlank()
                ? defaultSurface
                : AcquisitionSurface.fromLegacyVariant(legacyVariantCode, defaultSurface.code());
        String location = surface.path();
        RedirectView rv = new RedirectView(location);
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return rv;
    }

    @GetMapping("/inspection-response-letter")
    public Object inspectionResponseLetter(@RequestParam(value = "v", required = false) String legacyVariantCode,
            Model model) {
        if (legacyVariantCode != null && !legacyVariantCode.isBlank()) {
            AcquisitionSurface redirectedSurface = AcquisitionSurface.fromLegacyVariant(legacyVariantCode,
                    defaultSurface.code());
            if (redirectedSurface != AcquisitionSurface.LETTER) {
                return permanentRedirect(redirectedSurface.path());
            }
        }
        return renderSurface(AcquisitionSurface.LETTER, model);
    }

    @GetMapping("/seller-credit-after-home-inspection")
    public String sellerCreditAfterHomeInspection(Model model) {
        return renderSurface(AcquisitionSurface.CREDIT, model);
    }

    @GetMapping("/repair-request-vs-seller-credit-after-inspection")
    public String repairRequestVsSellerCreditAfterInspection(Model model) {
        return renderSurface(AcquisitionSurface.CREDIT_VS_REPAIR, model);
    }

    @GetMapping("/what-to-ask-for-after-home-inspection")
    public String whatToAskForAfterHomeInspection(Model model) {
        return renderSurface(AcquisitionSurface.ASK, model);
    }

    @GetMapping("/repair-request-after-home-inspection")
    public String repairRequestAfterHomeInspection(Model model) {
        return renderSurface(AcquisitionSurface.REPAIR_REQUEST, model);
    }

    @GetMapping("/inspection-objection-after-home-inspection")
    public String inspectionObjectionAfterHomeInspection(Model model) {
        return renderSurface(AcquisitionSurface.OBJECTION, model);
    }

    @GetMapping("/inspection-contingency-deadline-after-home-inspection")
    public String inspectionContingencyDeadlineAfterHomeInspection(Model model) {
        return renderSurface(AcquisitionSurface.DEADLINE, model);
    }

    @GetMapping({
            "/seller-refused-repairs-after-inspection",
            "/seller-counter-offer-after-home-inspection",
            "/seller-wont-negotiate-after-inspection",
            "/inspection-negotiation-fallback-after-home-inspection",
            "/reduce-seller-credit-request-after-inspection",
            "/seller-rejected-repair-addendum-after-inspection",
            "/response-to-seller-inspection-counter",
            "/reasonable-requests-after-home-inspection",
            "/what-not-to-ask-for-after-home-inspection",
            "/how-much-seller-credit-to-ask-after-inspection",
            "/home-inspection-negotiation-checklist",
            "/buyer-repair-request-list-after-inspection",
            "/inspection-report-negotiation-tool",
            "/fha-inspection-repairs-seller-credit",
            "/va-inspection-repairs-seller-credit",
            "/lender-required-repairs-after-inspection",
            "/appraisal-required-repairs-after-inspection",
            "/seller-credit-limits-after-home-inspection",
            "/home-inspection-repair-addendum",
            "/home-inspection-repair-amendment",
            "/inspection-contingency-removal-after-repairs",
            "/inspection-resolution-deadline",
            "/inspection-objection-notice",
            "/roof-repair-credit-after-inspection",
            "/sewer-scope-seller-credit-after-inspection",
            "/electrical-repair-request-after-inspection",
            "/foundation-repair-credit-after-inspection",
            "/mold-found-during-home-inspection-seller-credit",
            "/hvac-repair-credit-after-inspection",
            "/plumbing-leak-seller-credit-after-inspection",
            "/water-intrusion-seller-credit-after-inspection",
            "/polybutylene-pipes-seller-credit-after-inspection",
            "/federal-pacific-panel-seller-credit-after-inspection"
    })
    public String expandedAcquisitionSurface(HttpServletRequest request, Model model) {
        return renderSurface(AcquisitionSurface.fromPath(request.getRequestURI(), defaultSurface.code()), model);
    }

    @GetMapping("/for-buyer-agents")
    public String forBuyerAgents(Model model) {
        acquisitionTelemetryService.recordSurfaceView("agent_team", "/for-buyer-agents");
        model.addAttribute("canonicalUrl", BASE_URL + "/for-buyer-agents");
        return "pages/for-buyer-agents";
    }

    @GetMapping("/sample-seller-credit-request-after-home-inspection")
    public String samplePacket(Model model) {
        acquisitionTelemetryService.recordSurfaceView("sample_packet",
                "/sample-seller-credit-request-after-home-inspection");
        model.addAttribute("canonicalUrl", BASE_URL + "/sample-seller-credit-request-after-home-inspection");
        return "pages/sample-packet";
    }

    @GetMapping("/fha-va-inspection-repairs-and-seller-credit")
    public String fhaVaInspectionRepairsAndSellerCredit(Model model) {
        acquisitionTelemetryService.recordSurfaceView("financing", "/fha-va-inspection-repairs-and-seller-credit");
        model.addAttribute("canonicalUrl", BASE_URL + "/fha-va-inspection-repairs-and-seller-credit");
        return "pages/fha-va-inspection-repairs";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "pages/privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "pages/terms-of-service";
    }

    @GetMapping("/disclaimer")
    public RedirectView disclaimer() {
        RedirectView rv = new RedirectView("/home-repair/disclaimer");
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return rv;
    }

    String renderSurface(AcquisitionSurface surface, Model model) {
        acquisitionTelemetryService.recordSurfaceView(surface.code(), surface.path());
        model.addAttribute("surface", surface);
        model.addAttribute("relatedSurfaces", AcquisitionSurface.relatedSurfaces(surface));
        model.addAttribute("canonicalUrl", BASE_URL + surface.path());
        return "pages/hub";
    }

    private RedirectView permanentRedirect(String location) {
        RedirectView rv = new RedirectView(location);
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return rv;
    }
}
