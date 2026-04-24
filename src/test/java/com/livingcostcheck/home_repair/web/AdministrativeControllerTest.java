package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.seo.SitemapGenerator;
import com.livingcostcheck.home_repair.service.AcquisitionTelemetryService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdministrativeControllerTest {

    @Test
    void validationSignalsRecommendWinningVariantWhenEnoughDataExists() {
        EventLogRepository eventLogRepository = mock(EventLogRepository.class);
        SitemapGenerator sitemapGenerator = mock(SitemapGenerator.class);
        AcquisitionTelemetryService acquisitionTelemetryService = mock(AcquisitionTelemetryService.class);
        when(eventLogRepository.findByCreatedAtAfter(any())).thenReturn(List.of(
                generated("credit"), generated("credit"), generated("credit"), generated("credit"), generated("credit"),
                generated("credit"), generated("credit"), generated("credit"), generated("credit"), generated("credit"),
                copied("credit"), copied("credit"), copied("credit"), copied("credit"), copied("credit"),
                generated("letter"), generated("letter"), generated("letter"), generated("letter"), generated("letter"),
                generated("letter"), generated("letter"), generated("letter"), generated("letter"), generated("letter"),
                copied("letter"), copied("letter"), copied("letter")));
        when(acquisitionTelemetryService.readSince(any())).thenReturn(List.of(
                toolOpen("credit"), toolOpen("credit"), toolOpen("credit"),
                toolOpen("letter"), toolOpen("letter")));

        AdministrativeController controller = new AdministrativeController(sitemapGenerator, eventLogRepository,
                acquisitionTelemetryService,
                "letter");

        Map<String, Object> summary = controller.validationSignals();
        @SuppressWarnings("unchecked")
        Map<String, Object> acquisitionReview = (Map<String, Object>) summary.get("acquisitionReview");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> leaderboard = (List<Map<String, Object>>) acquisitionReview.get("leaderboard");

        assertEquals("letter", acquisitionReview.get("defaultSurface"));
        assertEquals("credit", acquisitionReview.get("currentLeader"));
        assertEquals("credit", acquisitionReview.get("recommendedWinner"));
        assertEquals("letter", acquisitionReview.get("runnerUp"));
        assertEquals(Boolean.TRUE, acquisitionReview.get("enoughDataToRecommend"));
        assertEquals(Boolean.TRUE, acquisitionReview.get("enoughDataToLock"));
        assertEquals(Boolean.FALSE, acquisitionReview.get("defaultMatchesWinner"));
        assertEquals("credit", leaderboard.get(0).get("entry"));
        assertEquals("Seller Credit", leaderboard.get(0).get("label"));
        assertEquals("/seller-credit-after-home-inspection", leaderboard.get(0).get("path"));
        assertEquals("credit", acquisitionReview.get("topOfFunnelLeader"));
        assertTrue(String.valueOf(acquisitionReview.get("winnerReason")).contains("Recommend credit"));
        assertTrue(summary.containsKey("activationFunnel"));
        assertTrue(summary.containsKey("moneySignalReady"));
    }

    @Test
    void validationSignalsHoldRecommendationWhenOnlyOneVariantHasTraffic() {
        EventLogRepository eventLogRepository = mock(EventLogRepository.class);
        SitemapGenerator sitemapGenerator = mock(SitemapGenerator.class);
        AcquisitionTelemetryService acquisitionTelemetryService = mock(AcquisitionTelemetryService.class);
        when(eventLogRepository.findByCreatedAtAfter(any())).thenReturn(List.of(
                generated("credit"), generated("credit"), generated("credit"), generated("credit"), generated("credit"),
                copied("credit"), copied("credit")));
        when(acquisitionTelemetryService.readSince(any())).thenReturn(List.of(
                surfaceView("credit"), toolOpen("credit")));

        AdministrativeController controller = new AdministrativeController(sitemapGenerator, eventLogRepository,
                acquisitionTelemetryService,
                "letter");

        Map<String, Object> summary = controller.validationSignals();
        @SuppressWarnings("unchecked")
        Map<String, Object> acquisitionReview = (Map<String, Object>) summary.get("acquisitionReview");

        assertEquals("credit", acquisitionReview.get("currentLeader"));
        assertNull(acquisitionReview.get("recommendedWinner"));
        assertEquals(Boolean.FALSE, acquisitionReview.get("enoughDataToRecommend"));
        assertEquals(Boolean.FALSE, acquisitionReview.get("enoughDataToLock"));
        assertTrue(String.valueOf(acquisitionReview.get("winnerReason"))
                .contains("Keep testing until at least 2 landing variants each have 5 generated packets"));
    }

    @Test
    void validationSignalsExposeTopOfFunnelAndIcpSignals() {
        EventLogRepository eventLogRepository = mock(EventLogRepository.class);
        SitemapGenerator sitemapGenerator = mock(SitemapGenerator.class);
        AcquisitionTelemetryService acquisitionTelemetryService = mock(AcquisitionTelemetryService.class);
        when(eventLogRepository.findByCreatedAtAfter(any())).thenReturn(List.of(
                generated("objection"),
                generated("objection"),
                copied("objection"),
                agentDesk("objection")));
        when(acquisitionTelemetryService.readSince(any())).thenReturn(List.of(
                surfaceView("objection"), surfaceView("objection"), toolOpen("objection")));

        AdministrativeController controller = new AdministrativeController(sitemapGenerator, eventLogRepository,
                acquisitionTelemetryService,
                "letter");

        Map<String, Object> summary = controller.validationSignals();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> entryBreakdown = (Map<String, Map<String, Object>>) summary.get("entryBreakdown");
        @SuppressWarnings("unchecked")
        Map<String, Object> objection = entryBreakdown.get("objection");
        @SuppressWarnings("unchecked")
        Map<String, Object> acquisitionReview = (Map<String, Object>) summary.get("acquisitionReview");

        assertEquals(2L, objection.get("surfaceViews"));
        assertEquals(1L, objection.get("toolOpens"));
        assertEquals(1L, objection.get("agentDeskRequests"));
        assertEquals("objection", acquisitionReview.get("topOfFunnelLeader"));
        assertEquals("objection", acquisitionReview.get("currentIcpLeader"));
        @SuppressWarnings("unchecked")
        Map<String, Object> activationFunnel = (Map<String, Object>) summary.get("activationFunnel");
        assertEquals(2L, activationFunnel.get("surfaceViews"));
        assertEquals(1L, activationFunnel.get("toolOpens"));
        assertEquals(1L, activationFunnel.get("agentDeskRequests"));
        assertTrue(String.valueOf(activationFunnel.get("rule")).contains("surface view -> tool open"));
    }

    private EventLog generated(String entry) {
        return new EventLog(UUID.randomUUID(), EventLog.EventType.PACKET_GENERATED, "entry=" + entry + "|action=generated");
    }

    private EventLog copied(String entry) {
        return new EventLog(UUID.randomUUID(), EventLog.EventType.COPY_PACKET, "entry=" + entry + "|action=full_packet");
    }

    private EventLog agentDesk(String entry) {
        return new EventLog(UUID.randomUUID(), EventLog.EventType.REQUEST_AGENT_DESK, "entry=" + entry + "|email=agent%40example.com");
    }

    private AcquisitionTelemetryService.TelemetryEvent surfaceView(String entry) {
        return new AcquisitionTelemetryService.TelemetryEvent(LocalDateTime.now(), entry, "SURFACE_VIEW",
                "/" + entry);
    }

    private AcquisitionTelemetryService.TelemetryEvent toolOpen(String entry) {
        return new AcquisitionTelemetryService.TelemetryEvent(LocalDateTime.now(), entry, "TOOL_OPEN",
                "/home-repair");
    }
}
