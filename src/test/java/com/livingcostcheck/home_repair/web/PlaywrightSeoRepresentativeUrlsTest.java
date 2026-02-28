package com.livingcostcheck.home_repair.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaywrightSeoRepresentativeUrlsTest {

    private static final List<String> REPRESENTATIVE_URLS = List.of(
            "/home-repair/verdicts/abilene-tx/pre-1950.html",
            "/home-repair/verdicts/riverside-san-bernardino-ca/1995-2010.html",
            "/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central",
            "/home-repair/verdicts/miami-ft-lauderdale-fl/1995-2010/hvac-heat-pump-central",
            "/home-repair/data-sources");

    private static Playwright playwright;
    private static Browser browser;

    @LocalServerPort
    private int port;

    static {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void shutdown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void representativeUrlsDesktopUxSmoke() throws IOException {
        runViewportAudit("desktop", 1440, 900);
    }

    @Test
    void representativeUrlsMobileUxSmoke() throws IOException {
        runViewportAudit("mobile", 390, 844);
    }

    private void runViewportAudit(String viewportLabel, int width, int height) throws IOException {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(width, height));
        try {
            for (String path : REPRESENTATIVE_URLS) {
                Page page = context.newPage();
                try {
                    List<String> consoleErrors = new ArrayList<>();
                    List<String> pageErrors = new ArrayList<>();
                    List<String> requestFailures = new ArrayList<>();

                    page.onConsoleMessage(msg -> {
                        if ("error".equalsIgnoreCase(msg.type())) {
                            String text = msg.text();
                            if (!isIgnorableExternalError(text)) {
                                consoleErrors.add(text);
                            }
                        }
                    });
                    page.onPageError(pageErrors::add);
                    page.onRequestFailed(req -> {
                        String url = req.url();
                        if (!isIgnorableExternalUrl(url)) {
                            requestFailures.add(url + " => " + req.failure());
                        }
                    });

                    Response response = page.navigate(url(path));
                    assertTrue(response != null, "no navigation response for " + path);
                    assertTrue(response.status() < 400,
                            "non-success status " + response.status() + " for " + path);
                    page.waitForTimeout(800);

                    assertTrue(page.locator("main").first().isVisible(),
                            "main element should be visible: " + path);

                    Number overflowValue = (Number) page.evaluate(
                            "() => Math.max(0, document.documentElement.scrollWidth - window.innerWidth)");
                    double overflow = overflowValue.doubleValue();
                    String overflowDebug = overflow > 4.0 ? collectOverflowDebug(page) : "";
                    assertTrue(overflow <= 4.0,
                            "horizontal overflow detected (" + overflow + "px): " + path + " [" + viewportLabel
                                    + "] offenders=" + overflowDebug);

                    String bodyText = page.locator("body").innerText();
                    assertNoInternalLeak(bodyText, path);
                    assertExpectedBlocks(path, page, bodyText);

                    assertTrue(pageErrors.isEmpty(), "page JS errors found on " + path + ": " + pageErrors);
                    assertTrue(consoleErrors.isEmpty(), "console errors found on " + path + ": " + consoleErrors);
                    assertTrue(requestFailures.isEmpty(), "failed core requests on " + path + ": " + requestFailures);

                    saveScreenshot(page, screenshotName(path, viewportLabel));
                } finally {
                    page.close();
                }
            }
        } finally {
            context.close();
        }
    }

    private void assertExpectedBlocks(String path, Page page, String bodyText) {
        String lower = bodyText.toLowerCase(Locale.ROOT);

        if (path.equals("/home-repair/data-sources")) {
            assertTrue(lower.contains("open data downloads (csv/json)"));
            assertTrue(lower.contains("metro_unique_signals_2026.csv"));
            assertTrue(lower.contains("metro_unique_signals_2026.json"));
            return;
        }

        if (path.matches("^/home-repair/verdicts/[^/]+/[^/]+\\.html$")) {
            assertTrue(lower.contains("public data signals"), "missing Public Data Signals block on " + path);
            assertTrue(lower.contains("fema major disasters"), "missing FEMA metric on " + path);
            assertTrue(lower.contains("owner occupancy"), "missing owner occupancy metric on " + path);
            assertTrue(lower.contains("median year built"), "missing median year metric on " + path);
            assertTrue(lower.contains("repair pressure index"), "missing RPI metric on " + path);
            assertTrue(lower.contains("source file:"), "missing source file label on " + path);
            assertTrue(page.locator("a[href='/data/metro_unique_signals_2026.csv']").first().isVisible());
            return;
        }

        assertTrue(lower.contains("technical breakdown"));
        assertTrue(lower.contains("open data context"));
        assertTrue(page.locator("a[href='/data/metro_unique_signals_2026.csv']").first().isVisible());
    }

    private void assertNoInternalLeak(String bodyText, String path) {
        assertFalse(bodyText.contains("[FINANCIAL RISK PROMOTION]"), "internal token leak on " + path);
        assertFalse(bodyText.contains("ERA_RISK:"), "risk flag leak on " + path);
        assertFalse(bodyText.contains("ERA_LABOR_ADJUSTMENT:"), "labor flag leak on " + path);
    }

    private static boolean isIgnorableExternalError(String text) {
        if (text == null) {
            return false;
        }
        String lower = text.toLowerCase();
        return lower.contains("googletagmanager")
                || lower.contains("google-analytics")
                || lower.contains("gtag");
    }

    private static boolean isIgnorableExternalUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.contains("googletagmanager.com")
                || url.contains("google-analytics.com")
                || url.contains("fonts.googleapis.com")
                || url.contains("fonts.gstatic.com");
    }

    private String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    private String screenshotName(String path, String viewportLabel) {
        String sanitized = path.replace("/", "_")
                .replace("?", "_")
                .replace("=", "_")
                .replace("&", "_");
        return viewportLabel + sanitized + ".png";
    }

    private void saveScreenshot(Page page, String filename) throws IOException {
        Path reportDir = Paths.get("build", "reports", "playwright", "seo-representative");
        Files.createDirectories(reportDir);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(reportDir.resolve(filename))
                .setFullPage(true));
    }

    private String collectOverflowDebug(Page page) {
        Object details = page.evaluate("""
                () => {
                  const vw = window.innerWidth;
                  const offenders = [];
                  for (const el of document.querySelectorAll('body *')) {
                    const style = window.getComputedStyle(el);
                    if (style.display === 'none' || style.visibility === 'hidden') continue;
                    const rect = el.getBoundingClientRect();
                    const overflow = rect.right - vw;
                    if (overflow > 1) {
                      offenders.push({
                        tag: el.tagName.toLowerCase(),
                        id: el.id || '',
                        cls: (el.className || '').toString().trim().slice(0, 80),
                        overflow: Math.round(overflow * 10) / 10,
                        width: Math.round(rect.width * 10) / 10
                      });
                    }
                  }
                  return offenders.slice(0, 12);
                }
                """);
        return String.valueOf(details);
    }
}
