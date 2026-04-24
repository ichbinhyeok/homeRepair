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
            "/inspection-response-letter",
            "/seller-credit-after-home-inspection",
            "/repair-request-vs-seller-credit-after-inspection",
            "/what-to-ask-for-after-home-inspection",
            "/repair-request-after-home-inspection",
            "/inspection-objection-after-home-inspection",
            "/inspection-contingency-deadline-after-home-inspection",
            "/seller-refused-repairs-after-inspection",
            "/seller-counter-offer-after-home-inspection",
            "/reasonable-requests-after-home-inspection",
            "/fha-inspection-repairs-seller-credit",
            "/va-inspection-repairs-seller-credit",
            "/lender-required-repairs-after-inspection",
            "/seller-credit-limits-after-home-inspection",
            "/roof-repair-credit-after-inspection",
            "/for-buyer-agents",
            "/sample-seller-credit-request-after-home-inspection",
            "/fha-va-inspection-repairs-and-seller-credit",
            "/home-repair",
            "/home-repair/about",
            "/home-repair/methodology",
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

        if (path.equals("/inspection-response-letter")) {
            assertTrue(lower.contains("inspection response letter"));
            assertTrue(lower.contains("check the letter free"));
            assertTrue(lower.contains("this is not an article page."));
            assertTrue(lower.contains("pre-send review preview"));
            assertTrue(lower.contains("what to ask"));
            assertTrue(lower.contains("what to cut"));
            assertTrue(lower.contains("repeat-use path"));
            assertTrue(lower.contains("response letter"));
            assertTrue(lower.contains("seller credit"));
            assertTrue(lower.contains("repair request"));
            return;
        }

        if (path.equals("/seller-credit-after-home-inspection")) {
            assertTrue(lower.contains("seller credit request"));
            assertTrue(lower.contains("check credit ask free"));
            return;
        }

        if (path.equals("/repair-request-vs-seller-credit-after-inspection")) {
            assertTrue(lower.contains("repair request vs seller credit"));
            assertTrue(lower.contains("check credit vs repair"));
            return;
        }

        if (path.equals("/what-to-ask-for-after-home-inspection")) {
            assertTrue(lower.contains("what should i ask for after a home inspection"));
            assertTrue(lower.contains("check my first ask"));
            return;
        }

        if (path.equals("/repair-request-after-home-inspection")) {
            assertTrue(lower.contains("repair request"));
            assertTrue(lower.contains("check repair request free"));
            return;
        }

        if (path.equals("/inspection-objection-after-home-inspection")) {
            assertTrue(lower.contains("inspection objection"));
            assertTrue(lower.contains("check objection ask free"));
            return;
        }

        if (path.equals("/inspection-contingency-deadline-after-home-inspection")) {
            assertTrue(lower.contains("inspection contingency deadline"));
            assertTrue(lower.contains("check deadline-sensitive ask"));
            return;
        }

        if (path.equals("/seller-refused-repairs-after-inspection")) {
            assertTrue(lower.contains("seller refused repairs"));
            assertTrue(lower.contains("check seller refusal"));
            return;
        }

        if (path.equals("/seller-counter-offer-after-home-inspection")) {
            assertTrue(lower.contains("seller counter offer"));
            assertTrue(lower.contains("check seller counter"));
            assertTrue(lower.contains("seller countered far below the ask"));
            return;
        }

        if (path.equals("/reasonable-requests-after-home-inspection")) {
            assertTrue(lower.contains("reasonable requests"));
            assertTrue(lower.contains("check reasonable requests"));
            return;
        }

        if (path.equals("/fha-inspection-repairs-seller-credit")) {
            assertTrue(lower.contains("fha inspection repairs"));
            assertTrue(lower.contains("check fha ask"));
            return;
        }

        if (path.equals("/va-inspection-repairs-seller-credit")) {
            assertTrue(lower.contains("va inspection repairs"));
            assertTrue(lower.contains("check va ask"));
            assertTrue(lower.contains("va repair gate before credit language"));
            return;
        }

        if (path.equals("/lender-required-repairs-after-inspection")) {
            assertTrue(lower.contains("lender-required repairs"));
            assertTrue(lower.contains("check lender-sensitive ask"));
            assertTrue(lower.contains("lender-required repair risk"));
            return;
        }

        if (path.equals("/seller-credit-limits-after-home-inspection")) {
            assertTrue(lower.contains("seller credit limits"));
            assertTrue(lower.contains("check credit limit risk"));
            assertTrue(lower.contains("credit request may exceed usable limits"));
            return;
        }

        if (path.equals("/roof-repair-credit-after-inspection")) {
            assertTrue(lower.contains("roof repair credit"));
            assertTrue(lower.contains("check roof credit"));
            return;
        }

        if (path.equals("/home-repair")) {
            assertTrue(lower.contains("pre-send inspection request check"));
            assertTrue(lower.contains("paste the request before you send it."));
            assertTrue(lower.contains("what fallback to use"));
            assertTrue(lower.contains("buyer agents first"));
            return;
        }

        if (path.equals("/for-buyer-agents")) {
            assertTrue(lower.contains("for buyer-agent teams"));
            assertTrue(lower.contains("inspection ask pre-send desk"));
            assertTrue(lower.contains("free first file"));
            assertTrue(lower.contains("team setup only after use"));
            return;
        }

        if (path.equals("/sample-seller-credit-request-after-home-inspection")) {
            assertTrue(lower.contains("sample pre-send review"));
            assertTrue(lower.contains("illustrative ask review"));
            return;
        }

        if (path.equals("/fha-va-inspection-repairs-and-seller-credit")) {
            assertTrue(lower.contains("loan-sensitive deals"));
            assertTrue(lower.contains("fha and va inspection issues"));
            return;
        }

        if (path.equals("/home-repair/about")) {
            assertTrue(lower.contains("inspection response window"));
            assertTrue(lower.contains("buyer-agent workflow first"));
            return;
        }

        if (path.equals("/home-repair/methodology")) {
            assertTrue(lower.contains("how the ask pre-send check works."));
            assertTrue(lower.contains("scoped exposure"));
            return;
        }

        if (path.equals("/home-repair/data-sources")) {
            assertTrue(lower.contains("open data downloads (csv/json)"));
            assertTrue(lower.contains("metro_unique_signals_2026.csv"));
            assertTrue(lower.contains("metro_unique_signals_2026.json"));
            return;
        }
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
