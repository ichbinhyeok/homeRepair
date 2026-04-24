package com.livingcostcheck.home_repair.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
                        .param("caseLabel", "Maple Street response window")
                        .param("propertyAddress", "123 Maple St, Atlanta, GA")
                        .param("clientName", "Mina Kim")
                        .param("agentName", "Alex Park")
                        .param("isFpePanel", "true")
                        .param("inspectionReportText", "Active roof leak above the garage\nFederal Pacific panel flagged by inspector")
                        .param("history", "ROOFING")
                        .param("history", "HVAC"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home-repair/result/*"))
                .andReturn();

        String resultUrl = verdictSubmit.getResponse().getRedirectedUrl();

        mockMvc.perform(get(resultUrl))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Robots-Tag", containsString("noindex")))
                .andExpect(content().string(containsString("Inspection ask pre-send check")))
                .andExpect(content().string(containsString("FHA financing")))
                .andExpect(content().string(containsString("Cut before sending")))
                .andExpect(content().string(containsString("Financing pressure")))
                .andExpect(content().string(containsString("Maple Street response window")))
                .andExpect(content().string(containsString("123 Maple St, Atlanta, GA")))
                .andExpect(content().string(containsString("Mina Kim")))
                .andExpect(content().string(containsString("Alex Park")))
                .andExpect(content().string(containsString("Case workflow")))
                .andExpect(content().string(containsString("Audit timeline")))
                .andExpect(content().string(containsString("Validation mode: no payment or email required")))
                .andExpect(content().string(not(containsString("report-unlock-form"))))
                .andExpect(content().string(not(containsString("12-Month Security Calendar"))))
                .andExpect(content().string(not(containsString("[FINANCIAL RISK PROMOTION]"))))
                .andExpect(content().string(not(containsString("ERA_RISK:"))))
                .andExpect(content().string(not(containsString("ERA_LABOR_ADJUSTMENT:"))));
    }

    @Test
    void workflowStateEndpointRecordsReviewButBlocksCounterBeforeSend() throws Exception {
        MvcResult verdictSubmit = mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING")
                        .param("loanType", "CONVENTIONAL")
                        .param("budget", "65000")
                        .param("condition", "SEVERE")
                        .param("caseLabel", "Workflow check")
                        .param("propertyAddress", "77 Pine St, Atlanta, GA")
                        .param("clientName", "Taylor Kim")
                        .param("agentName", "Morgan Lee")
                        .param("inspectionFinding", "HVAC age needs verification"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String verdictId = verdictSubmit.getResponse().getRedirectedUrl()
                .substring(verdictSubmit.getResponse().getRedirectedUrl().lastIndexOf('/') + 1);

        mockMvc.perform(post("/home-repair/api/workflow-state")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", verdictId)
                        .param("workflowAction", "REQUEST_REVIEW")
                        .param("actor", "Morgan Lee")
                        .param("note", "Need one more pass before send"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Review state recorded.")));

        mockMvc.perform(post("/home-repair/api/workflow-state")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", verdictId)
                        .param("workflowAction", "COUNTER_RECEIVED"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Record a send event before logging a seller counter.")));

        mockMvc.perform(get("/home-repair/result/" + verdictId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Review requested")))
                .andExpect(content().string(containsString("Actor: Morgan Lee")))
                .andExpect(content().string(containsString("Need one more pass before send")));
    }

    @Test
    void workflowStateEndpointRejectsUnknownCaseIds() throws Exception {
        mockMvc.perform(post("/home-repair/api/workflow-state")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", "00000000-0000-0000-0000-000000000000")
                        .param("workflowAction", "REQUEST_REVIEW"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Unknown case.")));
    }

    @Test
    void agentDeskEndpointCapturesBuyerAgentLead() throws Exception {
        MvcResult verdictSubmit = mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("entry", "credit")
                        .param("relationship", "BUYING")
                        .param("inspectionFinding", "Main sewer line shows active backup risk"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String verdictId = verdictSubmit.getResponse().getRedirectedUrl()
                .substring(verdictSubmit.getResponse().getRedirectedUrl().lastIndexOf('/') + 1);

        mockMvc.perform(post("/home-repair/api/agent-desk")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", verdictId)
                        .param("email", "agent@example.com")
                        .param("entry", "credit")
                        .param("role", "buyer_agent")
                        .param("teamSize", "2_5")
                        .param("monthlyVolume", "5_10")
                        .param("note", "Need broker-ready export"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Agent desk request saved.")));
    }

    @Test
    void uploadedInspectionDocumentShowsMatchedEvidence() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "inspectionReportFile",
                "inspection.txt",
                "text/plain",
                "Active roof leak above the garage\nFederal Pacific panel flagged by inspector".getBytes());

        MvcResult verdictSubmit = mockMvc.perform(multipart("/home-repair/verdict")
                        .file(file)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING")
                        .param("loanType", "FHA")
                        .param("budget", "65000")
                        .param("sqft", "1900")
                        .param("condition", "SEVERE")
                        .param("inspectionReportText", "Active roof leak above the garage\nFederal Pacific panel flagged by inspector"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home-repair/result/*"))
                .andReturn();

        String resultUrl = verdictSubmit.getResponse().getRedirectedUrl();

        mockMvc.perform(get(resultUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Matched report evidence")))
                .andExpect(content().string(containsString("inspection.txt")))
                .andExpect(content().string(containsString("Report p.1")));
    }

    @Test
    void workspaceHidesPublicRecentCaseFilesUntilOwnershipExists() throws Exception {
        mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING")
                        .param("caseLabel", "Oak Street response window")
                        .param("propertyAddress", "88 Oak St, Atlanta, GA")
                        .param("clientName", "Jordan Lee")
                        .param("agentName", "Sam Park")
                        .param("inspectionFinding", "Main sewer line shows active backup risk"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/home-repair/workspace"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/workspace"))
                .andExpect(content().string(containsString("Public recent-case lists are disabled on purpose.")))
                .andExpect(content().string(not(containsString("Oak Street response window"))))
                .andExpect(content().string(not(containsString("88 Oak St, Atlanta, GA"))))
                .andExpect(content().string(not(containsString("Jordan Lee"))))
                .andExpect(content().string(not(containsString("Sam Park"))));
    }

    @Test
    void uploadedInspectionPhotoShowsOcrBackedEvidence() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "inspectionReportFile",
                "inspection-photo.png",
                "image/png",
                sampleInspectionPhoto());

        MvcResult verdictSubmit = mockMvc.perform(multipart("/home-repair/verdict")
                        .file(file)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING")
                        .param("loanType", "CONVENTIONAL")
                        .param("budget", "65000")
                        .param("sqft", "1900")
                        .param("condition", "SEVERE")
                        .param("inspectionReportText", "Active roof leak above rear bedroom"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home-repair/result/*"))
                .andReturn();

        String resultUrl = verdictSubmit.getResponse().getRedirectedUrl();

        mockMvc.perform(get(resultUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Matched report evidence")))
                .andExpect(content().string(containsString("inspection-photo.png")))
                .andExpect(content().string(containsString("(OCR)")));
    }

    @Test
    void engagementEventEndpointAcceptsValidationEventsOnly() throws Exception {
        MvcResult verdictSubmit = mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING")
                        .param("budget", "65000")
                        .param("condition", "SEVERE")
                        .param("inspectionFinding", "Main sewer line shows active backup risk"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String resultUrl = verdictSubmit.getResponse().getRedirectedUrl();
        String verdictId = resultUrl.substring(resultUrl.lastIndexOf('/') + 1);

        mockMvc.perform(post("/home-repair/api/event")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", verdictId)
                        .param("eventType", "COPY_PACKET")
                        .param("target", "full_packet"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/home-repair/api/event")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", verdictId)
                        .param("eventType", "CLICK_AFFILIATE")
                        .param("target", "blocked_in_validation_flow"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validationSignalsExposeEntryBreakdownForAcquisitionVariants() throws Exception {
        MvcResult verdictSubmit = mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("entry", "credit")
                        .param("relationship", "BUYING")
                        .param("inspectionFinding", "Main sewer line shows active backup risk"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String verdictId = verdictSubmit.getResponse().getRedirectedUrl()
                .substring(verdictSubmit.getResponse().getRedirectedUrl().lastIndexOf('/') + 1);

        mockMvc.perform(post("/home-repair/api/event")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("verdictId", verdictId)
                        .param("eventType", "COPY_PACKET")
                        .param("target", "entry=credit|action=full_packet"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admin/p-seo/validation-signals"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"entryBreakdown\"")))
                .andExpect(content().string(containsString("\"acquisitionReview\"")))
                .andExpect(content().string(containsString("\"credit\"")))
                .andExpect(content().string(containsString("\"generatedPackets\"")))
                .andExpect(content().string(containsString("\"copiedOrPrintedPackets\"")))
                .andExpect(content().string(containsString("\"activationFunnel\"")))
                .andExpect(content().string(containsString("\"moneySignalReady\"")))
                .andExpect(content().string(containsString("\"primaryBottleneck\"")))
                .andExpect(content().string(containsString("\"currentLeader\"")))
                .andExpect(content().string(containsString("\"winnerReason\"")));
    }

    @Test
    void fastFreePathWorksWithoutPropertyContext() throws Exception {
        MvcResult verdictSubmit = mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("relationship", "BUYING")
                        .param("inspectionFinding", "Main sewer line shows active backup risk"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get(verdictSubmit.getResponse().getRedirectedUrl()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Broad U.S. baseline")))
                .andExpect(content().string(containsString("typical mid-age housing stock")));
    }

    @Test
    void emptyInspectionInputReturnsLandingInsteadOfSyntheticPacket() throws Exception {
        mockMvc.perform(post("/home-repair/verdict")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/index"))
                .andExpect(content().string(containsString("Paste at least one proposed ask or inspection finding before running the pre-send check.")));
    }

    @Test
    void inspectionResponseLetterLandingOwnsSingleAcquisitionIntent() throws Exception {
        mockMvc.perform(get("/inspection-response-letter"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inspection response letter")))
                .andExpect(content().string(containsString("Check The Letter Free")))
                .andExpect(content().string(containsString("This is not an article page.")))
                .andExpect(content().string(containsString("Pre-send review preview")))
                .andExpect(content().string(containsString("what to ask, what to cut")))
                .andExpect(content().string(containsString("SoftwareApplication")))
                .andExpect(content().string(containsString("Sample tool run")))
                .andExpect(content().string(containsString("Response letter before the deadline")))
                .andExpect(content().string(containsString("Response Letter")))
                .andExpect(content().string(containsString("Seller Credit")))
                .andExpect(content().string(containsString("Inspection Objection")));
    }

    @Test
    void sellerCreditSurfaceOwnsCreditIntent() throws Exception {
        mockMvc.perform(get("/seller-credit-after-home-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("seller credit request")))
                .andExpect(content().string(containsString("Check Credit Ask Free")))
                .andExpect(content().string(containsString("repair request vs seller credit")))
                .andExpect(content().string(containsString("/home-repair?entry=credit#packet-builder")));
    }

    @Test
    void creditVsRepairSurfaceOwnsDecisionIntent() throws Exception {
        mockMvc.perform(get("/repair-request-vs-seller-credit-after-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("repair request vs seller credit")))
                .andExpect(content().string(containsString("Check Credit Vs Repair")))
                .andExpect(content().string(containsString("Start with the credit-vs-repair decision")))
                .andExpect(content().string(containsString("/home-repair?entry=credit_vs_repair#packet-builder")));
    }

    @Test
    void legacyLandingVariantRedirectsToDedicatedSurface() throws Exception {
        mockMvc.perform(get("/inspection-response-letter").param("v", "credit"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("/seller-credit-after-home-inspection"));
    }

    @Test
    void inspectionObjectionSurfaceOwnsObjectionIntent() throws Exception {
        mockMvc.perform(get("/inspection-objection-after-home-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inspection objection")))
                .andExpect(content().string(containsString("Check Objection Ask Free")))
                .andExpect(content().string(containsString("/home-repair?entry=objection#packet-builder")));
    }

    @Test
    void deadlineSurfaceOwnsUrgentInspectionWindowIntent() throws Exception {
        mockMvc.perform(get("/inspection-contingency-deadline-after-home-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inspection contingency deadline")))
                .andExpect(content().string(containsString("Check Deadline-Sensitive Ask")))
                .andExpect(content().string(containsString("Start from the live deadline")))
                .andExpect(content().string(containsString("/home-repair?entry=deadline#packet-builder")));
    }

    @Test
    void expandedSurfaceOwnsSpecificHighIntentDecisionWithoutBreakingToolIdentity() throws Exception {
        mockMvc.perform(get("/seller-refused-repairs-after-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("seller refused repairs")))
                .andExpect(content().string(containsString("Check Seller Refusal")))
                .andExpect(content().string(containsString("first ask already came back no")))
                .andExpect(content().string(containsString("/home-repair?entry=seller_refused#packet-builder")))
                .andExpect(content().string(containsString("Pre-send review preview")))
                .andExpect(content().string(containsString("Seller already said no")))
                .andExpect(content().string(containsString("Counter-move proof")))
                .andExpect(content().string(containsString("Buyer-agent workflow first")));

        mockMvc.perform(get("/roof-repair-credit-after-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("roof repair credit")))
                .andExpect(content().string(containsString("Check Roof Credit")))
                .andExpect(content().string(containsString("quote-needed status")))
                .andExpect(content().string(containsString("Before you send the Roof Credit")))
                .andExpect(content().string(containsString("Roof credit without overclaiming replacement")))
                .andExpect(content().string(containsString("System issue proof")))
                .andExpect(content().string(containsString("/home-repair?entry=roof_credit#packet-builder")));
    }

    @Test
    void buyerAgentCommercialPageTargetsSmallTeams() throws Exception {
        mockMvc.perform(get("/for-buyer-agents"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("For buyer-agent teams")))
                .andExpect(content().string(containsString("small buyer-agent teams")))
                .andExpect(content().string(containsString("Free first file")))
                .andExpect(content().string(containsString("Team setup only after use")))
                .andExpect(content().string(containsString("/home-repair?entry=agent_team#packet-builder")));
    }

    @Test
    void samplePacketPageShowsProofArtifact() throws Exception {
        mockMvc.perform(get("/sample-seller-credit-request-after-home-inspection"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sample pre-send review")))
                .andExpect(content().string(containsString("Illustrative ask review")))
                .andExpect(content().string(containsString("/home-repair?entry=credit")));
    }

    @Test
    void financingPageOwnsLenderSensitiveIntent() throws Exception {
        mockMvc.perform(get("/fha-va-inspection-repairs-and-seller-credit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("FHA and VA inspection issues")))
                .andExpect(content().string(containsString("Loan-sensitive deals")))
                .andExpect(content().string(containsString("/home-repair?entry=financing#packet-builder")));
    }

    @Test
    void toolLandingOwnsNarrowInspectionResponseQuestions() throws Exception {
        mockMvc.perform(get("/home-repair"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/index"))
                .andExpect(content().string(containsString("Buyers do not all search with the same words. The pre-send job underneath is still the same.")))
                .andExpect(content().string(containsString("Response Letter")))
                .andExpect(content().string(containsString("Repair Request")))
                .andExpect(content().string(containsString("Check My Ask Free")));
    }

    @Test
    void entrySpecificToolLandingKeepsObjectionContextAfterTheClick() throws Exception {
        mockMvc.perform(get("/home-repair").param("entry", "objection"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/index"))
                .andExpect(content().string(containsString("Started from Inspection Objection")))
                .andExpect(content().string(containsString("Use the state-form language if you must")))
                .andExpect(content().string(containsString("Searchers came in through")))
                .andExpect(content().string(containsString("Check Inspection Objection Ask")));
    }

    @Test
    void entrySpecificToolLandingKeepsExpandedSurfaceContextAfterTheClick() throws Exception {
        mockMvc.perform(get("/home-repair").param("entry", "credit_vs_repair"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/index"))
                .andExpect(content().string(containsString("Started from Credit Vs Repair")))
                .andExpect(content().string(containsString("credit-vs-repair decision")))
                .andExpect(content().string(containsString("Check Credit Vs Repair Ask")));

        mockMvc.perform(get("/home-repair").param("entry", "deadline"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/index"))
                .andExpect(content().string(containsString("Started from Inspection Deadline")))
                .andExpect(content().string(containsString("live deadline")))
                .andExpect(content().string(containsString("Check Inspection Deadline Ask")));

        mockMvc.perform(get("/home-repair").param("entry", "roof_credit"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/index"))
                .andExpect(content().string(containsString("Started from Roof Credit")))
                .andExpect(content().string(containsString("roof issue")))
                .andExpect(content().string(containsString("Check Roof Credit Ask")));
    }

    @Test
    void legacyStep2RedirectsBackToToolSurface() throws Exception {
        mockMvc.perform(post("/home-repair/step-2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("metroCode", "ATLANTA_SANDY_SPRINGS_GA")
                        .param("era", "1980_1995")
                        .param("relationship", "BUYING"))
                .andExpect(status().isSeeOther())
                .andExpect(redirectedUrl("/home-repair?metroCode=ATLANTA_SANDY_SPRINGS_GA&era=1980_1995&relationship=BUYING&legacy=planner"));
    }

    @Test
    void retiredRiskDetailRouteReturnsGoneRecoveryPage() throws Exception {
        mockMvc.perform(get("/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central"))
                .andExpect(status().isGone())
                .andExpect(header().string("X-Robots-Tag", containsString("noindex")))
                .andExpect(content().string(containsString("This archive page has been retired.")))
                .andExpect(content().string(containsString("Open inspection response tool")))
                .andExpect(content().string(not(containsString("[FINANCIAL RISK PROMOTION]"))))
                .andExpect(content().string(not(containsString("ERA_RISK:"))))
                .andExpect(content().string(not(containsString("ERA_LABOR_ADJUSTMENT:"))));
    }

    private byte[] sampleInspectionPhoto() throws IOException {
        BufferedImage image = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 42));
        graphics.drawString("Inspection Summary", 80, 120);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 34));
        graphics.drawString("1. Active roof leak above rear bedroom.", 80, 220);
        graphics.drawString("2. Water staining noted near the ceiling line.", 80, 300);
        graphics.dispose();

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        }
    }
}
