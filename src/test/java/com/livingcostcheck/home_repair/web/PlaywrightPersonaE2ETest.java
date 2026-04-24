package com.livingcostcheck.home_repair.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaywrightPersonaE2ETest {

    private static Playwright playwright;
    private static Browser browser;

    @LocalServerPort
    private int port;

    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void stopBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 900));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void rootRedirectsToInspectionResponseLetterLanding() throws IOException {
        page.navigate(url("/"));
        page.waitForURL("**/inspection-response-letter");

        String bodyText = page.locator("body").innerText();
        String normalizedBodyText = bodyText.toLowerCase();

        assertTrue(normalizedBodyText.contains("need an inspection response letter after a home inspection?"));
        assertTrue(normalizedBodyText.contains("check the letter free"));
        assertTrue(page.locator("a[href='/home-repair?entry=letter#packet-builder']").first().isVisible());

        saveScreenshot("inspection-response-letter-landing.png");
    }

    @Test
    void sellerCreditSurfaceFunnelsIntoSamePacketTool() throws IOException {
        page.navigate(url("/seller-credit-after-home-inspection"));

        String bodyText = page.locator("body").innerText();
        String normalizedBodyText = bodyText.toLowerCase();

        assertTrue(normalizedBodyText.contains("seller credit request"));
        assertTrue(normalizedBodyText.contains("check credit ask free"));
        assertTrue(page.locator("a[href='/home-repair?entry=credit#packet-builder']").first().isVisible());

        saveScreenshot("seller-credit-surface.png");
    }

    @Test
    void landingIsToolFirstInsteadOfDirectoryFirst() throws IOException {
        page.navigate(url("/home-repair"));
        String bodyText = page.locator("body").innerText();
        String normalizedBodyText = bodyText.toLowerCase();

        assertTrue(normalizedBodyText.contains("pre-send inspection request check"));
        assertTrue(normalizedBodyText.contains("free during validation"));
        assertTrue(normalizedBodyText.contains("paste the request before you send it."));
        assertTrue(normalizedBodyText.contains("pre-send preview"));
        assertTrue(normalizedBodyText.contains("response letter"));
        assertTrue(normalizedBodyText.contains("seller credit"));
        assertTrue(normalizedBodyText.contains("inspection objection"));
        assertTrue(normalizedBodyText.contains("check my ask free"));
        assertTrue(page.locator("form[action='/home-repair/verdict']").first().isVisible());
        assertTrue(page.locator("a[href='/inspection-response-letter']").first().isVisible());
        assertTrue(page.locator("a[href='/repair-request-after-home-inspection']").first().isVisible());
        assertFalse(bodyText.contains("Start planner"));

        saveScreenshot("tool-first-landing.png");
    }

    @Test
    void buyerCanGeneratePacketDirectlyFromInspectionFindings() throws IOException {
        page.navigate(url("/home-repair"));

        page.locator("summary:has-text('Optional case-file details')").click();
        page.locator("input[name='caseLabel']").fill("Maple Street response window");
        page.locator("input[name='propertyAddress']").fill("123 Maple St, Atlanta, GA");
        page.locator("input[name='clientName']").fill("Mina Kim");
        page.locator("input[name='agentName']").fill("Alex Park");
        page.locator("textarea[name='inspectionReportText']").fill("""
                 Active roof leak above the garage
                 Federal Pacific panel flagged by inspector
                 Minor paint scuffs in hallway
                 """);
        page.locator("summary:has-text('Optional deal settings')").click();
        page.selectOption("select[name='loanType']", "FHA");
        page.selectOption("select[name='quoteSupport']", "HAS_ONE");
        page.selectOption("select[name='closingWindow']", "SEVEN_TO_TWENTY_ONE_DAYS");
        page.locator("summary:has-text('Optional local property context')").click();
        page.locator("select[name='metroCode']").selectOption("ATLANTA_SANDY_SPRINGS_GA");
        page.locator("select[name='era']").selectOption("1980_1995");
        page.check("input[name='isFpePanel']");
        page.check("input[name='history'][value='ROOFING']");
        page.locator("form[action='/home-repair/verdict'] button[type='submit']").click();

        page.waitForURL("**/home-repair/result/**");
        String bodyText = page.locator("body").innerText();
        String normalizedBodyText = bodyText.toLowerCase();

        assertTrue(normalizedBodyText.contains("inspection ask pre-send check"));
        assertTrue(bodyText.contains("FHA financing"));
        assertTrue(bodyText.contains("Active roof leak above the garage"));
        assertTrue(bodyText.contains("Federal Pacific panel flagged by inspector"));
        assertTrue(bodyText.contains("Maple Street response window"));
        assertTrue(bodyText.contains("123 Maple St, Atlanta, GA"));
        assertTrue(bodyText.contains("Mina Kim"));
        assertTrue(bodyText.contains("Alex Park"));
        assertTrue(bodyText.contains("Cut before sending"));
        assertTrue(bodyText.contains("lender-visible"));
        assertFalse(bodyText.contains("12-Month Security Calendar"));
        assertNoInternalLeak(bodyText);

        saveScreenshot("buyer-direct-packet-result.png");
    }

    @Test
    void buyerCanUseFreePacketActionsWithoutLeadGate() throws IOException {
        page.navigate(url("/home-repair"));
        page.locator("textarea[name='inspectionReportText']").fill("Main sewer line shows active backup risk");
        page.locator("form[action='/home-repair/verdict'] button[type='submit']").click();

        page.waitForURL("**/home-repair/result/**");
        String bodyText = page.locator("body").innerText();
        assertTrue(bodyText.contains("Validation mode: no payment or email required."));
        assertTrue(bodyText.contains("Broad U.S. baseline"));
        assertTrue(bodyText.contains("Print Pre-Send Check"));
        assertFalse(page.locator("#report-unlock-form").isVisible());

        page.click("#validation-feedback-form button[type='submit']");
        assertTrue(page.locator("#packet-action-status").innerText().contains("Marked useful."));

        saveScreenshot("buyer-validation-actions.png");
    }

    @Test
    void invalidAffiliateRedirectTargetReturnsToHomePage() {
        page.navigate(url(
                "/home-repair/track?verdictId=00000000-0000-0000-0000-000000000001&type=AFFILIATE&target=https://evil.example"));
        page.waitForURL("**/home-repair");
        assertTrue(page.url().endsWith("/home-repair"));
    }

    @Test
    void riskDetailHtmlVariantReturnsGoneInBrowser() throws IOException {
        var response = page.navigate(url(
                "/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central.html.html"));

        assertTrue(response != null);
        assertTrue(response.status() == 410);
        assertTrue(page.url()
                .endsWith("/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central.html.html"));
        assertTrue(page.locator("body").innerText().contains("This archive page has been retired."));
        assertTrue(page.locator("a[href='/home-repair']").first().isVisible());

        saveScreenshot("risk-detail-gone.png");
    }

    private void saveScreenshot(String filename) throws IOException {
        Path reportDir = Paths.get("build", "reports", "playwright");
        Files.createDirectories(reportDir);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(reportDir.resolve(filename))
                .setFullPage(true));
    }

    private String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    private void assertNoInternalLeak(String text) {
        assertFalse(text.contains("[FINANCIAL RISK PROMOTION]"));
        assertFalse(text.contains("ERA_RISK:"));
        assertFalse(text.contains("ERA_LABOR_ADJUSTMENT:"));
    }
}
