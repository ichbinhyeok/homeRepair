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
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
public class RedirectFilter implements Filter {

    private static final Pattern LEGACY_ARCHIVE_PATH = Pattern.compile(
            "^/home-repair/(?:verdicts|risks)(?:/.*)?$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        if (LEGACY_ARCHIVE_PATH.matcher(uri).matches()) {
            writeRetiredArchiveResponse(res, uri);
            return;
        }

        chain.doFilter(request, response);
    }

    private void writeRetiredArchiveResponse(HttpServletResponse res, String uri) throws IOException {
        res.setStatus(HttpServletResponse.SC_GONE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setContentType("text/html;charset=UTF-8");
        res.setHeader("X-Robots-Tag", "noindex,noarchive");

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="robots" content="noindex,noarchive">
                    <title>Archive Retired | LifeVerdict</title>
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,600&family=IBM+Plex+Mono:wght@500;600&family=Instrument+Sans:wght@400;500;600&display=swap" rel="stylesheet">
                    <style>
                        :root {
                            --canvas: #f4ede3;
                            --paper: rgba(255, 250, 244, 0.94);
                            --ink: #10202a;
                            --muted: #5f6d74;
                            --line: rgba(16, 32, 42, 0.10);
                            --primary: #174a5a;
                            --accent: #c96a3d;
                        }
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            min-height: 100vh;
                            display: grid;
                            place-items: center;
                            padding: 24px;
                            color: var(--muted);
                            font-family: "Instrument Sans", "Segoe UI", sans-serif;
                            background:
                                radial-gradient(circle at 12% 16%, rgba(201, 106, 61, 0.12), transparent 28%),
                                radial-gradient(circle at 88% 12%, rgba(23, 74, 90, 0.12), transparent 30%),
                                repeating-linear-gradient(180deg, rgba(16, 32, 42, 0.018) 0, rgba(16, 32, 42, 0.018) 1px, transparent 1px, transparent 36px),
                                linear-gradient(180deg, #f8f1e9 0%, var(--canvas) 100%);
                        }
                        main {
                            width: min(100%, 760px);
                            border: 1px solid var(--line);
                            border-radius: 32px;
                            padding: 34px;
                            background: var(--paper);
                            box-shadow: 0 28px 84px rgba(16, 32, 42, 0.10);
                            backdrop-filter: blur(16px);
                        }
                        .eyebrow {
                            display: inline-flex;
                            align-items: center;
                            gap: 10px;
                            color: var(--primary);
                            font-family: "IBM Plex Mono", monospace;
                            font-size: 12px;
                            font-weight: 600;
                            letter-spacing: 0.12em;
                            text-transform: uppercase;
                        }
                        .eyebrow::before {
                            content: "";
                            width: 8px;
                            height: 8px;
                            border-radius: 999px;
                            background: var(--accent);
                            box-shadow: 0 0 0 5px rgba(201, 106, 61, 0.16);
                        }
                        h1 {
                            margin: 18px 0 12px;
                            color: var(--ink);
                            font-family: "Fraunces", Georgia, serif;
                            font-size: clamp(2.2rem, 5vw, 4rem);
                            line-height: 0.96;
                            letter-spacing: -0.05em;
                            font-weight: 600;
                        }
                        p {
                            margin: 0 0 14px;
                            line-height: 1.75;
                            font-size: 1rem;
                        }
                        strong {
                            color: var(--ink);
                        }
                        code {
                            display: block;
                            margin-top: 18px;
                            padding: 14px 16px;
                            border-radius: 18px;
                            background: rgba(16, 32, 42, 0.04);
                            color: var(--ink);
                            font-family: "IBM Plex Mono", monospace;
                            font-size: 12px;
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
                            border: 1px solid var(--line);
                            background: white;
                            color: var(--ink);
                        }
                    </style>
                </head>
                <body>
                    <main>
                        <div class="eyebrow">410 Archive Retired</div>
                        <h1>This archive page has been retired.</h1>
                        <p>
                            We removed the old verdict and risk archive from the public product surface so
                            <strong>LifeVerdict stays focused on the inspection response tool</strong>.
                        </p>
                        <p>
                            If you came from an old search result or saved link, start from the live intake and
                            rebuild the packet from actual inspection findings.
                        </p>
                        <code>Requested URL: __REQUESTED_URI__</code>
                        <div class="actions">
                            <a class="button primary" href="/home-repair">Open inspection response tool</a>
                            <a class="button secondary" href="/home-repair/methodology">Read methodology</a>
                        </div>
                    </main>
                </body>
                </html>
                """;

        res.getWriter().write(html.replace("__REQUESTED_URI__", uri));
    }
}
