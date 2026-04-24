package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.service.AcquisitionTelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/p-seo")
public class AdministrativeController {

    private static final int WINDOW_DAYS = 14;
    private static final long MIN_RECOMMEND_GENERATED = 5L;
    private static final long MIN_LOCK_GENERATED = 10L;
    private static final long MIN_LOCK_COPY_OR_PRINT = 3L;
    private static final double MIN_LOCK_MARGIN = 0.10;

    private final com.livingcostcheck.home_repair.seo.SitemapGenerator sitemapGenerator;
    private final EventLogRepository eventLogRepository;
    private final AcquisitionTelemetryService acquisitionTelemetryService;
    private final AcquisitionSurface defaultSurface;

    public AdministrativeController(
            com.livingcostcheck.home_repair.seo.SitemapGenerator sitemapGenerator,
            EventLogRepository eventLogRepository,
            AcquisitionTelemetryService acquisitionTelemetryService,
            @Value("${app.acquisition.default-surface:letter}") String defaultSurfaceCode) {
        this.sitemapGenerator = sitemapGenerator;
        this.eventLogRepository = eventLogRepository;
        this.acquisitionTelemetryService = acquisitionTelemetryService;
        this.defaultSurface = AcquisitionSurface.defaultSurface(defaultSurfaceCode);
    }

    @GetMapping("/generate")
    public String generate() {
        try {
            String sitemapPath = "src/main/resources/static/sitemap.xml";
            int sitemapCount = sitemapGenerator.generateSitemap(sitemapPath, java.util.Collections.emptyList());
            log.info("ADMIN: Legacy pSEO generation retired. Regenerated tool-only sitemap.");
            return "Legacy pSEO generation is retired.\n"
                    + "Public surface stays focused on the inspection response tool.\n"
                    + "Sitemap updated: " + sitemapCount + " URLs.";
        } catch (Exception e) {
            log.error("Tool-only sitemap generation failed", e);
            return "FAILED: tool-only sitemap generation error: " + e.getMessage();
        }
    }

    @GetMapping("/validation-signals")
    public Map<String, Object> validationSignals() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(WINDOW_DAYS);
        List<EventLog> recentEvents = eventLogRepository.findByCreatedAtAfter(cutoff);
        List<AcquisitionTelemetryService.TelemetryEvent> telemetryEvents = acquisitionTelemetryService.readSince(cutoff);
        Map<String, Long> counts = recentEvents.stream()
                .collect(Collectors.groupingBy(event -> event.getEventType().name(), LinkedHashMap::new,
                        Collectors.counting()));

        Map<String, Map<String, Long>> entryBreakdown = recentEvents.stream()
                .collect(Collectors.groupingBy(this::extractEntryCode, LinkedHashMap::new,
                        Collectors.groupingBy(event -> event.getEventType().name(), LinkedHashMap::new,
                                Collectors.counting())));

        Map<String, Map<String, Long>> telemetryBreakdown = telemetryEvents.stream()
                .collect(Collectors.groupingBy(AcquisitionTelemetryService.TelemetryEvent::surfaceCode,
                        LinkedHashMap::new,
                        Collectors.groupingBy(AcquisitionTelemetryService.TelemetryEvent::stage,
                                LinkedHashMap::new,
                                Collectors.counting())));

        LinkedHashSet<String> allEntries = new LinkedHashSet<>();
        AcquisitionSurface.indexableSurfaces().forEach(surface -> allEntries.add(surface.code()));
        allEntries.add("direct");
        allEntries.addAll(entryBreakdown.keySet());
        allEntries.addAll(telemetryBreakdown.keySet());

        Map<String, Map<String, Object>> entrySummary = new LinkedHashMap<>();
        allEntries.forEach(entry -> {
            Map<String, Long> eventCounts = entryBreakdown.getOrDefault(entry, Map.of());
            Map<String, Long> telemetryCounts = telemetryBreakdown.getOrDefault(entry, Map.of());
            long entryGenerated = eventCounts.getOrDefault(EventLog.EventType.PACKET_GENERATED.name(), 0L);
            long entryCopiedOrPrinted = eventCounts.getOrDefault(EventLog.EventType.COPY_PACKET.name(), 0L)
                    + eventCounts.getOrDefault(EventLog.EventType.PRINT_PACKET.name(), 0L);
            long entryEmails = eventCounts.getOrDefault(EventLog.EventType.SUBMIT_EMAIL.name(), 0L);
            long entryAgentDesk = eventCounts.getOrDefault(EventLog.EventType.REQUEST_AGENT_DESK.name(), 0L);
            long surfaceViews = telemetryCounts.getOrDefault("SURFACE_VIEW", 0L);
            long toolOpens = telemetryCounts.getOrDefault("TOOL_OPEN", 0L);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("eventCounts", eventCounts);
            row.put("surfaceViews", surfaceViews);
            row.put("toolOpens", toolOpens);
            row.put("generatedPackets", entryGenerated);
            row.put("copiedOrPrintedPackets", entryCopiedOrPrinted);
            row.put("optionalEmails", entryEmails);
            row.put("agentDeskRequests", entryAgentDesk);
            row.put("viewToToolOpenRate", surfaceViews == 0 ? 0.0 : ((double) toolOpens / (double) surfaceViews));
            row.put("toolOpenToGeneratedRate", toolOpens == 0 ? 0.0 : ((double) entryGenerated / (double) toolOpens));
            row.put("activationRate", entryGenerated == 0 ? 0.0 : ((double) entryCopiedOrPrinted / (double) entryGenerated));
            row.put("teamSetupRate", entryGenerated == 0 ? 0.0 : ((double) entryAgentDesk / (double) entryGenerated));
            entrySummary.put(entry, row);
        });

        long generated = counts.getOrDefault(EventLog.EventType.PACKET_GENERATED.name(), 0L);
        long copiedOrPrinted = counts.getOrDefault(EventLog.EventType.COPY_PACKET.name(), 0L)
                + counts.getOrDefault(EventLog.EventType.PRINT_PACKET.name(), 0L);
        long optionalEmails = counts.getOrDefault(EventLog.EventType.SUBMIT_EMAIL.name(), 0L);
        long agentDeskRequests = counts.getOrDefault(EventLog.EventType.REQUEST_AGENT_DESK.name(), 0L);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("windowDays", WINDOW_DAYS);
        summary.put("eventCounts", counts);
        summary.put("generatedPackets", generated);
        summary.put("copiedOrPrintedPackets", copiedOrPrinted);
        summary.put("optionalEmails", optionalEmails);
        summary.put("agentDeskRequests", agentDeskRequests);
        summary.put("activationFunnel", buildActivationFunnel(telemetryEvents, counts));
        summary.put("entryBreakdown", entrySummary);
        summary.put("acquisitionReview", buildAcquisitionReview(entrySummary));
        summary.put("monetizationReady",
                generated >= 30 && copiedOrPrinted >= 10 || optionalEmails >= 5 || agentDeskRequests >= 3);
        summary.put("moneySignalReady", generated >= 30 && copiedOrPrinted >= 10 && agentDeskRequests >= 3);
        summary.put("rule",
                "Start monetization only after 30 generated and 10 copied/printed packets, or 3 buyer-agent desk requests, or 5 optional emails in 14 days.");
        return summary;
    }

    private Map<String, Object> buildActivationFunnel(
            List<AcquisitionTelemetryService.TelemetryEvent> telemetryEvents,
            Map<String, Long> counts) {
        long surfaceViews = telemetryEvents.stream()
                .filter(event -> "SURFACE_VIEW".equals(event.stage()))
                .count();
        long toolOpens = telemetryEvents.stream()
                .filter(event -> "TOOL_OPEN".equals(event.stage()))
                .count();
        long generated = counts.getOrDefault(EventLog.EventType.PACKET_GENERATED.name(), 0L);
        long copied = counts.getOrDefault(EventLog.EventType.COPY_PACKET.name(), 0L);
        long printed = counts.getOrDefault(EventLog.EventType.PRINT_PACKET.name(), 0L);
        long copiedOrPrinted = copied + printed;
        long agentDeskRequests = counts.getOrDefault(EventLog.EventType.REQUEST_AGENT_DESK.name(), 0L);

        Map<String, Object> funnel = new LinkedHashMap<>();
        funnel.put("surfaceViews", surfaceViews);
        funnel.put("toolOpens", toolOpens);
        funnel.put("generatedPackets", generated);
        funnel.put("copiedPackets", copied);
        funnel.put("printedPackets", printed);
        funnel.put("copiedOrPrintedPackets", copiedOrPrinted);
        funnel.put("agentDeskRequests", agentDeskRequests);
        funnel.put("surfaceViewToToolOpenRate", rate(toolOpens, surfaceViews));
        funnel.put("toolOpenToGeneratedRate", rate(generated, toolOpens));
        funnel.put("generatedToCopyPrintRate", rate(copiedOrPrinted, generated));
        funnel.put("generatedToTeamSetupRate", rate(agentDeskRequests, generated));
        funnel.put("copyPrintToTeamSetupRate", rate(agentDeskRequests, copiedOrPrinted));
        funnel.put("primaryBottleneck",
                primaryBottleneck(surfaceViews, toolOpens, generated, copiedOrPrinted, agentDeskRequests));
        funnel.put("moneySignal",
                agentDeskRequests > 0
                        ? "Team setup intent exists. Qualify whether this came from repeat live-file usage."
                        : "No team setup intent yet. Keep the product free and optimize for generated plus copied/printed packets.");
        funnel.put("rule",
                "Read the funnel as surface view -> tool open -> generated packet -> copied/printed packet -> buyer-agent desk request.");
        return funnel;
    }

    private double rate(long numerator, long denominator) {
        return denominator == 0L ? 0.0 : (double) numerator / (double) denominator;
    }

    private String primaryBottleneck(
            long surfaceViews,
            long toolOpens,
            long generated,
            long copiedOrPrinted,
            long agentDeskRequests) {
        if (surfaceViews == 0L) {
            return "No surface traffic yet. Acquisition pages still need impressions before product quality can be judged.";
        }
        if (toolOpens == 0L) {
            return "Landing pages are not pushing visitors into the tool.";
        }
        if (generated == 0L) {
            return "Tool opens are not turning into generated packets.";
        }
        if (copiedOrPrinted == 0L) {
            return "Generated packets are not being used after the result page.";
        }
        if (agentDeskRequests == 0L) {
            return "Packet usage exists, but buyer-agent team setup intent has not appeared yet.";
        }
        return "The funnel has reached buyer-agent desk intent. Manually qualify repeat-use demand before monetization.";
    }

    private Map<String, Object> buildAcquisitionReview(Map<String, Map<String, Object>> entrySummary) {
        Comparator<VariantPerformance> byPerformance = Comparator
                .comparingDouble(VariantPerformance::activationRate)
                .thenComparingLong(VariantPerformance::copiedOrPrintedPackets)
                .thenComparingLong(VariantPerformance::toolOpens)
                .thenComparingLong(VariantPerformance::optionalEmails)
                .thenComparingLong(VariantPerformance::generatedPackets)
                .reversed()
                .thenComparing(VariantPerformance::entry);
        Comparator<VariantPerformance> byTraffic = Comparator
                .comparingDouble(VariantPerformance::viewToToolOpenRate)
                .thenComparingLong(VariantPerformance::toolOpens)
                .thenComparingDouble(VariantPerformance::toolOpenToGeneratedRate)
                .thenComparingLong(VariantPerformance::generatedPackets)
                .reversed()
                .thenComparing(VariantPerformance::entry);
        Comparator<VariantPerformance> byIcpIntent = Comparator
                .comparingLong(VariantPerformance::agentDeskRequests)
                .thenComparingDouble(VariantPerformance::teamSetupRate)
                .thenComparingLong(VariantPerformance::generatedPackets)
                .reversed()
                .thenComparing(VariantPerformance::entry);

        List<VariantPerformance> leaderboard = acquisitionEntryDescriptors(entrySummary.keySet()).stream()
                .map(descriptor -> VariantPerformance.from(descriptor, entrySummary.get(descriptor.entry())))
                .sorted(byPerformance)
                .toList();
        List<VariantPerformance> trafficLeaderboard = acquisitionEntryDescriptors(entrySummary.keySet()).stream()
                .map(descriptor -> VariantPerformance.from(descriptor, entrySummary.get(descriptor.entry())))
                .sorted(byTraffic)
                .toList();
        List<VariantPerformance> icpLeaderboard = acquisitionEntryDescriptors(entrySummary.keySet()).stream()
                .map(descriptor -> VariantPerformance.from(descriptor, entrySummary.get(descriptor.entry())))
                .sorted(byIcpIntent)
                .toList();

        VariantPerformance currentLeader = leaderboard.stream()
                .filter(row -> row.generatedPackets() > 0)
                .findFirst()
                .orElse(null);
        VariantPerformance topOfFunnelLeader = trafficLeaderboard.stream()
                .filter(row -> row.surfaceViews() > 0 || row.toolOpens() > 0)
                .findFirst()
                .orElse(null);
        VariantPerformance currentIcpLeader = icpLeaderboard.stream()
                .filter(row -> row.agentDeskRequests() > 0)
                .findFirst()
                .orElse(null);

        List<VariantPerformance> eligible = leaderboard.stream()
                .filter(row -> row.generatedPackets() >= MIN_RECOMMEND_GENERATED)
                .toList();

        boolean enoughDataToRecommend = eligible.size() >= 2;
        VariantPerformance recommendedWinner = enoughDataToRecommend ? eligible.get(0) : null;
        VariantPerformance runnerUp = enoughDataToRecommend ? eligible.get(1) : null;

        boolean enoughDataToLock = recommendedWinner != null
                && recommendedWinner.generatedPackets() >= MIN_LOCK_GENERATED
                && recommendedWinner.copiedOrPrintedPackets() >= MIN_LOCK_COPY_OR_PRINT
                && runnerUp != null
                && recommendedWinner.activationRate() - runnerUp.activationRate() >= MIN_LOCK_MARGIN;

        Map<String, Object> review = new LinkedHashMap<>();
        review.put("defaultSurface", defaultSurface.code());
        review.put("currentLeader", currentLeader == null ? null : currentLeader.entry());
        review.put("topOfFunnelLeader", topOfFunnelLeader == null ? null : topOfFunnelLeader.entry());
        review.put("currentIcpLeader", currentIcpLeader == null ? null : currentIcpLeader.entry());
        review.put("recommendedWinner", recommendedWinner == null ? null : recommendedWinner.entry());
        review.put("runnerUp", runnerUp == null ? null : runnerUp.entry());
        review.put("defaultMatchesWinner",
                recommendedWinner != null && recommendedWinner.entry().equals(defaultSurface.code()));
        review.put("enoughDataToRecommend", enoughDataToRecommend);
        review.put("enoughDataToLock", enoughDataToLock);
        review.put("leaderboard", leaderboard.stream().map(VariantPerformance::toMap).toList());
        review.put("trafficRule",
                "Use surface-view to tool-open rate to judge the landing promise. Use tool-open to packet-generated and copy/print activation to judge the handoff.");
        review.put("icpRule",
                "Use buyer-agent team-setup requests as the money-nearest signal. Do not assume the best packet-copy surface is also the best revenue-intent surface.");
        review.put("comparisonRule",
                "Use copy/print activation as the primary signal. Start comparing only after at least 5 generated packets on 2 landing variants.");
        review.put("lockRule",
                "Lock a winner only after the leader has at least 10 generated packets, 3 copy/print actions, and a 10-point activation-rate lead over the runner-up.");
        review.put("winnerReason", buildWinnerReason(currentLeader, recommendedWinner, runnerUp, enoughDataToRecommend,
                enoughDataToLock));
        return review;
    }

    private String buildWinnerReason(
            VariantPerformance currentLeader,
            VariantPerformance recommendedWinner,
            VariantPerformance runnerUp,
            boolean enoughDataToRecommend,
            boolean enoughDataToLock) {
        if (currentLeader == null) {
            return "No acquisition variant data yet. Wait for generated packets on at least 2 landing variants before choosing a winner.";
        }
        if (!enoughDataToRecommend) {
            return String.format(Locale.US,
                    "Current leader: %s at %.1f%% copy/print activation (%d/%d). Keep testing until at least 2 landing variants each have 5 generated packets.",
                    currentLeader.entry(),
                    currentLeader.activationRate() * 100.0,
                    currentLeader.copiedOrPrintedPackets(),
                    currentLeader.generatedPackets());
        }
        if (!enoughDataToLock) {
            return String.format(Locale.US,
                "Current winner candidate: %s at %.1f%% copy/print activation (%d/%d), ahead of %s at %.1f%%. Keep testing until the leader clears the lock rule.",
                    recommendedWinner.entry(),
                    recommendedWinner.activationRate() * 100.0,
                    recommendedWinner.copiedOrPrintedPackets(),
                    recommendedWinner.generatedPackets(),
                    runnerUp.entry(),
                    runnerUp.activationRate() * 100.0);
        }
        return String.format(Locale.US,
                "Recommend %s. It leads on copy/print activation at %.1f%% (%d/%d), ahead of %s at %.1f%%, and clears the lock rule.",
                recommendedWinner.entry(),
                recommendedWinner.activationRate() * 100.0,
                recommendedWinner.copiedOrPrintedPackets(),
                recommendedWinner.generatedPackets(),
                runnerUp.entry(),
                runnerUp.activationRate() * 100.0);
    }

    private List<AcquisitionEntryDescriptor> acquisitionEntryDescriptors(java.util.Set<String> observedEntries) {
        LinkedHashMap<String, AcquisitionEntryDescriptor> descriptors = new LinkedHashMap<>();
        for (AcquisitionSurface surface : AcquisitionSurface.indexableSurfaces()) {
            descriptors.put(surface.code(),
                    new AcquisitionEntryDescriptor(surface.code(), surface.navLabel(), surface.path()));
        }
        descriptors.put("agent_team",
                new AcquisitionEntryDescriptor("agent_team", "Buyer-Agent Teams", "/for-buyer-agents"));
        descriptors.put("sample_packet",
                new AcquisitionEntryDescriptor("sample_packet", "Sample Packet",
                        "/sample-seller-credit-request-after-home-inspection"));
        descriptors.put("financing",
                new AcquisitionEntryDescriptor("financing", "FHA/VA Repairs",
                        "/fha-va-inspection-repairs-and-seller-credit"));
        for (String observedEntry : observedEntries) {
            if (!"direct".equals(observedEntry) && !descriptors.containsKey(observedEntry)) {
                descriptors.put(observedEntry,
                        new AcquisitionEntryDescriptor(observedEntry, observedEntry.replace('_', ' '), "/home-repair"));
            }
        }
        return descriptors.values().stream()
                .filter(descriptor -> !"direct".equals(descriptor.entry()))
                .toList();
    }

    private String extractEntryCode(EventLog event) {
        if (event.getTarget() == null || event.getTarget().isBlank()) {
            return "unknown";
        }
        for (String token : event.getTarget().split("\\|")) {
            String trimmed = token.trim();
            if (trimmed.startsWith("entry=") && trimmed.length() > 6) {
                return trimmed.substring(6);
            }
        }
        return "direct";
    }

    private record VariantPerformance(
            String entry,
            String label,
            String path,
            long surfaceViews,
            long toolOpens,
            long generatedPackets,
            long copiedOrPrintedPackets,
            long optionalEmails,
            long agentDeskRequests) {
        static VariantPerformance from(AcquisitionEntryDescriptor descriptor, Map<String, Object> row) {
            if (row == null) {
                return new VariantPerformance(descriptor.entry(), descriptor.label(), descriptor.path(), 0L, 0L, 0L, 0L,
                        0L, 0L);
            }
            return new VariantPerformance(
                    descriptor.entry(),
                    descriptor.label(),
                    descriptor.path(),
                    asLong(row.get("surfaceViews")),
                    asLong(row.get("toolOpens")),
                    asLong(row.get("generatedPackets")),
                    asLong(row.get("copiedOrPrintedPackets")),
                    asLong(row.get("optionalEmails")),
                    asLong(row.get("agentDeskRequests")));
        }

        double activationRate() {
            if (generatedPackets == 0L) {
                return 0.0;
            }
            return (double) copiedOrPrintedPackets / (double) generatedPackets;
        }

        double viewToToolOpenRate() {
            if (surfaceViews == 0L) {
                return 0.0;
            }
            return (double) toolOpens / (double) surfaceViews;
        }

        double toolOpenToGeneratedRate() {
            if (toolOpens == 0L) {
                return 0.0;
            }
            return (double) generatedPackets / (double) toolOpens;
        }

        double teamSetupRate() {
            if (generatedPackets == 0L) {
                return 0.0;
            }
            return (double) agentDeskRequests / (double) generatedPackets;
        }

        Map<String, Object> toMap() {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("entry", entry);
            row.put("label", label);
            row.put("path", path);
            row.put("surfaceViews", surfaceViews);
            row.put("toolOpens", toolOpens);
            row.put("viewToToolOpenRate", viewToToolOpenRate());
            row.put("toolOpenToGeneratedRate", toolOpenToGeneratedRate());
            row.put("generatedPackets", generatedPackets);
            row.put("copiedOrPrintedPackets", copiedOrPrintedPackets);
            row.put("optionalEmails", optionalEmails);
            row.put("agentDeskRequests", agentDeskRequests);
            row.put("teamSetupRate", teamSetupRate());
            row.put("activationRate", activationRate());
            row.put("eligibleForRecommendation", generatedPackets >= MIN_RECOMMEND_GENERATED);
            row.put("eligibleForLock", generatedPackets >= MIN_LOCK_GENERATED
                    && copiedOrPrintedPackets >= MIN_LOCK_COPY_OR_PRINT);
            return row;
        }

        private static long asLong(Object value) {
            if (value instanceof Number number) {
                return number.longValue();
            }
            return 0L;
        }
    }

    private record AcquisitionEntryDescriptor(
            String entry,
            String label,
            String path) {
    }
}
