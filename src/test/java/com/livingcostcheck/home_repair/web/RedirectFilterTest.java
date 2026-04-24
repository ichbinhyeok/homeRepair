package com.livingcostcheck.home_repair.web;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedirectFilterTest {

    private final RedirectFilter filter = new RedirectFilter();

    @Test
    void shouldRedirectRiskHtmlToExtensionlessCanonical() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas.html", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
        assertTrue(result.body.contains("archive page has been retired"));
    }

    @Test
    void shouldReturnGoneForRiskWithRepeatedHtml() throws Exception {
        FilterResult result = execute(
                "/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas.html.html.html",
                "utm_source=gsc");

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
        assertTrue(result.body.contains("archive page has been retired"));
        assertTrue(result.body.contains("Open inspection response tool"));
    }

    @Test
    void shouldRetireL1WithoutExtension() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/miami-ft-lauderdale-fl/1995-2010", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldReturnGoneForL1RepeatedHtml() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/mobile-al/1970-1980.html.html", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
        assertTrue(result.body.contains("archive page has been retired"));
    }

    @Test
    void shouldReturnGoneForStateRepeatedHtml() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/states/ca.html.html", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
        assertTrue(result.body.contains("Read methodology"));
    }

    @Test
    void shouldRetireStatePageWithoutExtension() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/states/ca", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldRetireCanonicalRiskUrl() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldRetireCanonicalL1Url() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/miami-ft-lauderdale-fl/1995-2010.html", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
        assertEquals("noindex,noarchive", result.robotsHeader);
    }

    @Test
    void shouldRetireRiskArchivePath() throws Exception {
        FilterResult result = execute("/home-repair/risks/fpe-electrical-panel", null);

        assertEquals(410, result.statusCode);
        assertEquals(null, result.location);
        assertFalse(result.chainInvoked);
        assertEquals("noindex,noarchive", result.robotsHeader);
    }

    private FilterResult execute(String uri, String queryString) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        if (queryString != null) {
            request.setQueryString(queryString);
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainInvoked = new AtomicBoolean(false);
        FilterChain chain = (req, res) -> chainInvoked.set(true);

        filter.doFilter(request, response, chain);
        return new FilterResult(
                response.getStatus(),
                response.getHeader("Location"),
                response.getHeader("X-Robots-Tag"),
                response.getHeader("Link"),
                chainInvoked.get(),
                response.getContentAsString());
    }

    private static class FilterResult {
        private final int statusCode;
        private final String location;
        private final String robotsHeader;
        private final String linkHeader;
        private final boolean chainInvoked;
        private final String body;

        private FilterResult(int statusCode,
                String location,
                String robotsHeader,
                String linkHeader,
                boolean chainInvoked,
                String body) {
            this.statusCode = statusCode;
            this.location = location;
            this.robotsHeader = robotsHeader;
            this.linkHeader = linkHeader;
            this.chainInvoked = chainInvoked;
            this.body = body;
        }
    }
}
