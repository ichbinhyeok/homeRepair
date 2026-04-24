package com.livingcostcheck.home_repair.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcquisitionTelemetryServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void preservesExpandedAcquisitionSurfaceCodesInsteadOfCollapsingToDirect() {
        Path telemetryPath = tempDir.resolve("acquisition-events.csv");
        AcquisitionTelemetryService service = new AcquisitionTelemetryService(telemetryPath.toString());

        service.recordSurfaceView("seller_credit_limits", "/seller-credit-limits-after-home-inspection");
        service.recordSurfaceView("roof-credit", "/roof-repair-credit-after-inspection");
        service.recordToolOpen("lender_required_repairs", "/home-repair?entry=lender_required_repairs");

        List<AcquisitionTelemetryService.TelemetryEvent> events = service.readSince(LocalDateTime.now().minusDays(1));

        assertEquals(List.of("seller_credit_limits", "roof_credit", "lender_required_repairs"),
                events.stream().map(AcquisitionTelemetryService.TelemetryEvent::surfaceCode).toList());
        assertEquals(List.of("SURFACE_VIEW", "SURFACE_VIEW", "TOOL_OPEN"),
                events.stream().map(AcquisitionTelemetryService.TelemetryEvent::stage).toList());
    }
}
