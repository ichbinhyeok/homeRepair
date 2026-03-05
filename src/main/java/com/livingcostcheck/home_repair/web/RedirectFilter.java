package com.livingcostcheck.home_repair.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RedirectFilter implements Filter {

    private static final Pattern VERDICT_REPEATED_EXTENSION = Pattern.compile(
            "^/home-repair/verdicts/.+?(?:\\.(?:html?|HTML?)){2,}$");
    private static final Pattern RISK_TRAILING_EXTENSION = Pattern.compile(
            "^(/home-repair/verdicts/[^/]+/[^/]+/[^/]+?)(?:\\.(?:html?|HTML?))+$");
    private static final Pattern STATE_PAGE_WITH_EXTENSION = Pattern.compile(
            "^(/home-repair/verdicts/states/[a-zA-Z]{2})(?:\\.(?:html?|HTML?))+$");
    private static final Pattern STATE_PAGE_WITHOUT_EXTENSION = Pattern.compile(
            "^(/home-repair/verdicts/states/[a-zA-Z]{2})$");
    private static final Pattern L1_VERDICT_WITH_EXTENSION = Pattern.compile(
            "^(/home-repair/verdicts/(?!states/)[^/]+/[^/]+?)(?:\\.(?:html?|HTML?))+$");
    private static final Pattern L1_VERDICT_WITHOUT_EXTENSION = Pattern.compile(
            "^(/home-repair/verdicts/(?!states/)[^/.]+/[^/.]+)$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // Spider-trap hard cut: repeated .html variants should not be kept in crawl loops.
        if (VERDICT_REPEATED_EXTENSION.matcher(uri).matches()) {
            res.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        String canonicalUri = normalizeUri(uri);

        if (!canonicalUri.equals(uri)) {
            String location = canonicalUri;
            String queryString = req.getQueryString();
            if (queryString != null && !queryString.isBlank()) {
                location += "?" + queryString;
            }
            res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            res.setHeader("Location", location);
            return;
        }

        chain.doFilter(request, response);
    }

    private String normalizeUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return "";
        }

        Matcher riskMatcher = RISK_TRAILING_EXTENSION.matcher(uri);
        if (riskMatcher.matches()) {
            return riskMatcher.group(1);
        }

        Matcher stateExtMatcher = STATE_PAGE_WITH_EXTENSION.matcher(uri);
        if (stateExtMatcher.matches()) {
            return stateExtMatcher.group(1) + ".html";
        }

        Matcher stateNoExtMatcher = STATE_PAGE_WITHOUT_EXTENSION.matcher(uri);
        if (stateNoExtMatcher.matches()) {
            return stateNoExtMatcher.group(1) + ".html";
        }

        Matcher l1ExtMatcher = L1_VERDICT_WITH_EXTENSION.matcher(uri);
        if (l1ExtMatcher.matches()) {
            return l1ExtMatcher.group(1) + ".html";
        }

        Matcher l1NoExtMatcher = L1_VERDICT_WITHOUT_EXTENSION.matcher(uri);
        if (l1NoExtMatcher.matches()) {
            return l1NoExtMatcher.group(1) + ".html";
        }

        return uri;
    }
}
