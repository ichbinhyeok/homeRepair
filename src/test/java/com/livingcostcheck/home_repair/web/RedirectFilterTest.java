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

        assertEquals(301, result.statusCode);
        assertEquals("/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas", result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldRedirectRiskWithRepeatedHtmlToExtensionlessCanonical() throws Exception {
        FilterResult result = execute(
                "/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas.html.html.html",
                "utm_source=gsc");

        assertEquals(301, result.statusCode);
        assertEquals(
                "/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas?utm_source=gsc",
                result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldRedirectL1WithoutExtensionToHtmlCanonical() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/miami-ft-lauderdale-fl/1995-2010", null);

        assertEquals(301, result.statusCode);
        assertEquals("/home-repair/verdicts/miami-ft-lauderdale-fl/1995-2010.html", result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldRedirectL1RepeatedHtmlToSingleHtml() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/mobile-al/1970-1980.html.html", null);

        assertEquals(301, result.statusCode);
        assertEquals("/home-repair/verdicts/mobile-al/1970-1980.html", result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldRedirectStatePageWithoutExtensionToHtmlCanonical() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/states/ca", null);

        assertEquals(301, result.statusCode);
        assertEquals("/home-repair/verdicts/states/ca.html", result.location);
        assertFalse(result.chainInvoked);
    }

    @Test
    void shouldPassThroughCanonicalRiskUrl() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/erie-pa/1950-1970/water-heater-tankless-gas", null);

        assertEquals(200, result.statusCode);
        assertEquals(null, result.location);
        assertTrue(result.chainInvoked);
    }

    @Test
    void shouldPassThroughCanonicalL1Url() throws Exception {
        FilterResult result = execute("/home-repair/verdicts/miami-ft-lauderdale-fl/1995-2010.html", null);

        assertEquals(200, result.statusCode);
        assertEquals(null, result.location);
        assertTrue(result.chainInvoked);
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
        return new FilterResult(response.getStatus(), response.getHeader("Location"), chainInvoked.get());
    }

    private static class FilterResult {
        private final int statusCode;
        private final String location;
        private final boolean chainInvoked;

        private FilterResult(int statusCode, String location, boolean chainInvoked) {
            this.statusCode = statusCode;
            this.location = location;
            this.chainInvoked = chainInvoked;
        }
    }
}
