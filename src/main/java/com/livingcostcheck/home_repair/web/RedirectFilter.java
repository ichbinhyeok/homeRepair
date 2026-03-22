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
            writeGoneResponse(res, uri);
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

    private void writeGoneResponse(HttpServletResponse res, String uri) throws IOException {
        res.setStatus(HttpServletResponse.SC_GONE);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="robots" content="noindex">
                    <title>410 Gone | LifeVerdict</title>
                    <style>
                        :root {
                            color-scheme: light;
                            --bg: #f6f0e7;
                            --card: rgba(255,255,255,0.88);
                            --border: rgba(15,23,42,0.10);
                            --text: #16202c;
                            --muted: #556171;
                            --primary: #155e75;
                            --accent: #c7792c;
                        }
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            min-height: 100vh;
                            display: grid;
                            place-items: center;
                            padding: 24px;
                            font-family: Inter, "Segoe UI", sans-serif;
                            color: var(--text);
                            background:
                                radial-gradient(circle at top left, rgba(21, 94, 117, 0.12), transparent 30%),
                                radial-gradient(circle at bottom right, rgba(199, 121, 44, 0.14), transparent 28%),
                                var(--bg);
                        }
                        .card {
                            width: min(100%, 720px);
                            padding: 32px;
                            border-radius: 28px;
                            border: 1px solid var(--border);
                            background: var(--card);
                            backdrop-filter: blur(16px);
                            box-shadow: 0 24px 80px rgba(15, 23, 42, 0.10);
                        }
                        .eyebrow {
                            display: inline-flex;
                            align-items: center;
                            gap: 10px;
                            padding: 8px 12px;
                            border-radius: 9999px;
                            background: rgba(21, 94, 117, 0.08);
                            color: var(--primary);
                            font-size: 12px;
                            font-weight: 800;
                            letter-spacing: 0.08em;
                            text-transform: uppercase;
                        }
                        .eyebrow::before {
                            content: "";
                            width: 8px;
                            height: 8px;
                            border-radius: 9999px;
                            background: var(--accent);
                            box-shadow: 0 0 0 5px rgba(199, 121, 44, 0.14);
                        }
                        h1 {
                            margin: 18px 0 10px;
                            font-family: Outfit, Inter, sans-serif;
                            font-size: clamp(2rem, 5vw, 3rem);
                            line-height: 1.02;
                            letter-spacing: -0.04em;
                        }
                        p {
                            margin: 0 0 14px;
                            color: var(--muted);
                            line-height: 1.75;
                        }
                        code {
                            display: block;
                            margin-top: 18px;
                            padding: 14px 16px;
                            border-radius: 18px;
                            background: rgba(15, 23, 42, 0.04);
                            color: var(--text);
                            font-size: 13px;
                            overflow-wrap: anywhere;
                        }
                        .actions {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 12px;
                            margin-top: 24px;
                        }
                        .button {
                            display: inline-flex;
                            align-items: center;
                            justify-content: center;
                            padding: 13px 18px;
                            border-radius: 16px;
                            font-weight: 700;
                            text-decoration: none;
                        }
                        .button.primary {
                            background: var(--primary);
                            color: white;
                        }
                        .button.secondary {
                            border: 1px solid var(--border);
                            color: var(--text);
                            background: white;
                        }
                    </style>
                </head>
                <body>
                    <main class="card">
                        <div class="eyebrow">410 Gone</div>
                        <h1>This URL is no longer part of the site.</h1>
                        <p>
                            We removed malformed repeated-extension pages so search crawlers stop looping through bad variants.
                            The content you want may still exist at the planner or a canonical verdict page.
                        </p>
                        <p>
                            If you followed an old link, go back to the planner and rebuild the inspection view from a valid market and era.
                        </p>
                        <code>Requested URL: __REQUESTED_URI__</code>
                        <div class="actions">
                            <a class="button primary" href="/home-repair">Open seller credit planner</a>
                            <a class="button secondary" href="/home-repair/verdicts/states">Browse markets</a>
                        </div>
                    </main>
                </body>
                </html>
                """;
        res.getWriter().write(html.replace("__REQUESTED_URI__", uri));
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
