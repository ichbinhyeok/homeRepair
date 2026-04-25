package com.livingcostcheck.home_repair.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcquisitionSurfaceTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Test
    void acquisitionPortfolioContainsFortyNarrowToolFirstSurfaces() {
        assertEquals(40, AcquisitionSurface.indexableSurfaces().size());

        Set<String> codes = new HashSet<>();
        Set<String> paths = new HashSet<>();
        for (AcquisitionSurface surface : AcquisitionSurface.indexableSurfaces()) {
            assertTrue(codes.add(surface.code()), "duplicate code: " + surface.code());
            assertTrue(paths.add(surface.path()), "duplicate path: " + surface.path());
            assertTrue(surface.path().startsWith("/"), surface.code() + " path must be root-relative");
            assertTrue(surface.primaryCtaHref().contains("entry=" + surface.code()),
                    surface.code() + " should preserve entry context");
            assertFalse(surface.pageTitle().isBlank(), surface.code() + " title");
            assertFalse(surface.pageDescription().isBlank(), surface.code() + " description");
            assertFalse(surface.intentChips().isEmpty(), surface.code() + " intent chips");
            assertFalse(surface.sampleCase().title().isBlank(), surface.code() + " sample title");
            assertFalse(surface.sampleCase().verdict().isBlank(), surface.code() + " sample verdict");
            assertEquals(3, surface.proofSection().cards().size(), surface.code() + " proof cards");
        }
    }

    @Test
    void moneyNearestSurfacesHaveDedicatedSampleCases() {
        Set<String> titles = new HashSet<>();
        for (AcquisitionSurface surface : EnumSet.of(
                AcquisitionSurface.SELLER_CREDIT_LIMITS,
                AcquisitionSurface.FHA_REPAIRS,
                AcquisitionSurface.VA_REPAIRS,
                AcquisitionSurface.CREDIT,
                AcquisitionSurface.CREDIT_VS_REPAIR,
                AcquisitionSurface.LENDER_REQUIRED_REPAIRS,
                AcquisitionSurface.SELLER_COUNTER,
                AcquisitionSurface.SELLER_REFUSED,
                AcquisitionSurface.DEADLINE,
                AcquisitionSurface.ROOF_CREDIT)) {
            assertTrue(surface.hasDedicatedSampleCase(), surface.code() + " should have a dedicated sample");
            assertTrue(titles.add(surface.sampleCase().title()), "duplicate sample title: " + surface.sampleCase().title());
        }
    }

    @Test
    void expandedSurfacesResolveByPathAndCode() {
        assertSame(AcquisitionSurface.SELLER_REFUSED,
                AcquisitionSurface.fromPath("/seller-refused-repairs-after-inspection", "letter"));
        assertSame(AcquisitionSurface.ROOF_CREDIT,
                AcquisitionSurface.fromCode("roof-credit", "letter"));
        assertTrue(AcquisitionSurface.isSurfaceCode("fha_repairs"));
        assertTrue(AcquisitionSurface.isSurfaceCode("fha-repairs"));
    }

    @Test
    void relatedSurfacesStayFocusedInsteadOfDumpingAllFortyLinksIntoTheHero() {
        for (AcquisitionSurface surface : AcquisitionSurface.indexableSurfaces()) {
            assertTrue(AcquisitionSurface.relatedSurfaces(surface).contains(surface));
            assertTrue(AcquisitionSurface.relatedSurfaces(surface).size() <= 10);
        }
    }

    @Test
    void softwareApplicationSchemaJsonIsValidForEveryIndexableSurface() throws Exception {
        for (AcquisitionSurface surface : AcquisitionSurface.indexableSurfaces()) {
            String schemaJson = RootController.softwareApplicationSchemaJson(surface);

            assertFalse(schemaJson.contains("\\-"), surface.code() + " schema must not use invalid JSON hyphen escapes");
            JsonNode schema = JSON.readTree(schemaJson);
            assertEquals("https://schema.org", schema.get("@context").asText());
            assertEquals("SoftwareApplication", schema.get("@type").asText());
            assertEquals("https://lifeverdict.com" + surface.path(), schema.get("url").asText());
            assertEquals(surface.pageDescription(), schema.get("description").asText());
        }
    }
}
