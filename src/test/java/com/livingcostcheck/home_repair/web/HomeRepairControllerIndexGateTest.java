package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.seo.VerdictSeoService;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.SortedPlan;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class HomeRepairControllerIndexGateTest {

    private HomeRepairController controller;
    private Method gateMethod;

    @BeforeEach
    void setUp() throws Exception {
        controller = new HomeRepairController(
                mock(HomeRepairRepository.class),
                mock(EventLogRepository.class),
                mock(VerdictEngineService.class),
                mock(VerdictSeoService.class));

        gateMethod = HomeRepairController.class.getDeclaredMethod(
                "shouldIndexRiskDetail",
                RiskAdjustedItem.class,
                Verdict.class,
                Object.class);
        gateMethod.setAccessible(true);
    }

    @Test
    void shouldKeepRiskDetailsNoindexedEvenWhenNarrativesAreWeak() throws Exception {
        String sharedDefinition = repeat(
                "Asphalt shingle roof systems from legacy construction eras often present brittle underlayment, flashing fatigue, and hidden nail line lift that is not visible from curb-side walkthroughs.",
                2);
        String sharedScenario = repeat(
                "When storm events align with thermal cycling, seams open and moisture penetrates decking, driving secondary costs for sheathing replacement, attic mold remediation, and interior drywall restoration.",
                2);
        String sharedExplanation = repeat(
                "Replacement is typically prioritized during negotiation because insurers and lenders flag deferred envelope failures that materially increase near-term loss probability.",
                2);

        RiskAdjustedItem target = buildItem(
                "ROOFING_ASPHALT_ARCHITECTURAL",
                "Asphalt Roof Replacement",
                sharedDefinition,
                sharedScenario,
                sharedExplanation);

        RiskAdjustedItem peerOne = buildItem(
                "ROOFING_METAL_STANDING_SEAM",
                "Metal Roof Replacement",
                sharedDefinition,
                sharedScenario,
                sharedExplanation);

        RiskAdjustedItem peerTwo = buildItem(
                "ROOFING_TILE_CLAY",
                "Tile Roof Replacement",
                sharedDefinition,
                sharedScenario,
                sharedExplanation);

        Verdict verdict = buildVerdict(target, peerOne, peerTwo);
        boolean shouldIndex = invokeGate(target, verdict, new Object());

        assertFalse(shouldIndex);
    }

    @Test
    void shouldKeepRiskDetailsNoindexedEvenWhenNarrativesAreStrong() throws Exception {
        RiskAdjustedItem target = buildItem(
                "PLUMBING_MAIN_SEWER_REPLACEMENT",
                "Cast Iron Sewer Line Replacement",
                repeat(
                        "Cast-iron sewer trunks installed before modern sleeve standards corrode from the interior wall, reducing hydraulic capacity and creating offset joints that trap solids below slab pathways.",
                        2),
                repeat(
                        "Failure commonly appears as repeat backups, foundation-edge moisture, and hydrostatic pressure transfer into finished spaces, which triggers sanitation risk and substantial restoration scope.",
                        2),
                repeat(
                        "A full line replacement restores flow reliability, improves inspection outcomes for financing, and prevents cascading remediation events tied to wastewater intrusion.",
                        2));

        RiskAdjustedItem peerOne = buildItem(
                "HVAC_HEAT_PUMP_CENTRAL",
                "Central Heat Pump Replacement",
                repeat(
                        "Heat pump assemblies degrade through compressor wear, refrigerant loss, and coil fouling that reduce seasonal efficiency and elevate electrical draw during peak demand periods.",
                        2),
                repeat(
                        "If not corrected, temperature instability and latent humidity spikes accelerate finish damage while emergency replacement windows force premium labor pricing.",
                        2),
                repeat(
                        "Planned replacement supports predictable operating cost, warranty coverage continuity, and better buyer confidence in utility risk forecasting.",
                        2));

        RiskAdjustedItem peerTwo = buildItem(
                "ELECTRICAL_PANEL_UPGRADE",
                "Electrical Panel Upgrade",
                repeat(
                        "Legacy load centers frequently lack modern arc-fault and surge protection capacity, creating breaker trip instability under contemporary appliance usage patterns.",
                        2),
                repeat(
                        "Panel failure pathways include overheated bus bars, nuisance outages, and increased fire exposure when branch circuits exceed practical service thresholds.",
                        2),
                repeat(
                        "Service upgrades improve insurability, reduce underwriting friction, and align the property with present-day safety expectations.",
                        2));

        Verdict verdict = buildVerdict(target, peerOne, peerTwo);
        boolean shouldIndex = invokeGate(target, verdict, new Object());

        assertFalse(shouldIndex);
    }

    private boolean invokeGate(RiskAdjustedItem item, Verdict verdict, Object metroData) throws Exception {
        return (boolean) gateMethod.invoke(controller, item, verdict, metroData);
    }

    private Verdict buildVerdict(RiskAdjustedItem target, RiskAdjustedItem peerOne, RiskAdjustedItem peerTwo) {
        return Verdict.builder()
                .plan(SortedPlan.builder()
                        .mustDo(List.of(target, peerOne, peerTwo))
                        .build())
                .build();
    }

    private RiskAdjustedItem buildItem(
            String code,
            String name,
            String definition,
            String scenario,
            String explanation) {
        return RiskAdjustedItem.builder()
                .itemCode(code)
                .prettyName(name)
                .adjustedCost(6400.0)
                .mandatory(true)
                .riskFlags(List.of("CRITICAL_SEVERITY_SURCHARGE"))
                .definition(definition)
                .damageScenario(scenario)
                .explanation(explanation)
                .build();
    }

    private String repeat(String sentence, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(sentence);
        }
        return sb.toString();
    }
}
