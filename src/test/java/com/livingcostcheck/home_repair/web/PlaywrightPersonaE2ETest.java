package com.livingcostcheck.home_repair.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
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

    private Browser.NewContextOptions contextOptions;
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
        contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1440, 900);
        context = browser.newContext(contextOptions);
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void buyerPersonaCanReachVerdictWithoutInternalTokens() throws IOException {
        completeStepOne("BUYING", "1980_1995");

        page.fill("input[name='budget']", "68000");
        page.fill("input[name='sqft']", "2100");
        page.selectOption("select[name='loanType']", "FHA");
        page.locator("input[name='inspectionFinding']").nth(0).fill("Active roof leak above the garage");
        page.locator("input[name='inspectionFinding']").nth(1).fill("Federal Pacific panel flagged by inspector");
        page.selectOption("select[name='quoteSupport']", "HAS_ONE");
        page.selectOption("select[name='closingWindow']", "SEVEN_TO_TWENTY_ONE_DAYS");
        page.locator("input[name='condition'][value='SEVERE']")
                .check(new Locator.CheckOptions().setForce(true));
        page.check("input[name='isFpePanel']");
        page.check("input[name='isPolyB']");
        page.check("input[name='history'][value='ROOFING']");
        page.check("input[name='history'][value='HVAC']");
        page.locator("form[action='/home-repair/verdict'] button[type='submit']").click();

        page.waitForURL("**/home-repair/result/**");
        String bodyText = page.locator("body").innerText();

        assertTrue(bodyText.contains("Ready-To-Send Seller Credit Packet"));
        assertTrue(bodyText.contains("FHA financing"));
        assertTrue(bodyText.contains("Keep this send-today packet tight."));
        assertTrue(bodyText.contains("Active roof leak above the garage"));
        assertFalse(bodyText.contains("12-Month Security Calendar"));
        assertFalse(bodyText.contains("Component Health"));
        assertFalse(bodyText.contains("Share this analysis."));
        assertNoInternalLeak(bodyText);

        saveScreenshot("buyer-persona-result.png");
    }

    @Test
    void ownerPersonaOnMobileCanRecalculateFromResultPage() throws IOException {
        recreateMobileContext();
        completeStepOne("LIVING", "2010_PRESENT");

        page.fill("input[name='budget']", "54000");
        page.fill("input[name='sqft']", "1600");
        page.locator("input[name='condition'][value='MINOR']")
                .check(new Locator.CheckOptions().setForce(true));
        page.locator("summary:has-text('Tighten the send-today packet')").click();
        page.locator("label:has(input[name='roofType'][value='METAL'])").click();
        page.check("input[name='history'][value='PLUMBING']");
        page.locator("form[action='/home-repair/verdict'] button[type='submit']").click();

        page.waitForURL("**/home-repair/result/**");
        assertTrue(page.locator("text=Capital Expenditure Budget").first().isVisible());

        page.selectOption("form[action='/home-repair/verdict'] select[name='bathrooms']", "4");
        page.selectOption("form[action='/home-repair/verdict'] select[name='stories']", "3");
        page.locator("button:has-text('Recalculate')").click();
        page.waitForURL("**/home-repair/result/**");

        String bodyText = page.locator("body").innerText();
        assertNoInternalLeak(bodyText);

        saveScreenshot("owner-mobile-recalculate.png");
    }

    @Test
    void investorPersonaCanCompleteFlowWithoutBuyerOnlyUi() throws IOException {
        completeStepOne("INVESTING", "1970_1980");

        page.fill("input[name='budget']", "72000");
        page.fill("input[name='sqft']", "1850");
        page.locator("input[name='condition'][value='SEVERE']")
                .check(new Locator.CheckOptions().setForce(true));
        page.check("input[name='history'][value='ELECTRICAL']");
        page.check("input[name='history'][value='PLUMBING']");
        page.locator("form[action='/home-repair/verdict'] button[type='submit']").click();

        page.waitForURL("**/home-repair/result/**");
        String bodyText = page.locator("body").innerText();
        assertFalse(bodyText.contains("Vendor-Agnostic Forensic Audit"));
        assertTrue(bodyText.contains("View Verified Plan"));
        assertNoInternalLeak(bodyText);

        saveScreenshot("investor-persona-result.png");
    }

    @Test
    void buyerPersonaCanUnlockLeadCapturePanel() throws IOException {
        completeStepOne("BUYING", "1995_2010");

        page.fill("input[name='budget']", "58000");
        page.fill("input[name='sqft']", "1950");
        page.locator("input[name='condition'][value='MINOR']")
                .check(new Locator.CheckOptions().setForce(true));
        page.locator("form[action='/home-repair/verdict'] button[type='submit']").click();

        page.waitForURL("**/home-repair/result/**");
        page.fill("#report-unlock-form input[name='email']", "persona-beta@example.com");
        page.click("#report-unlock-form button[type='submit']");

        Locator unlocked = page.locator("#unlocked-actions");
        unlocked.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertTrue(unlocked.innerText().contains("Saved."));
        assertTrue(unlocked.innerText().contains("Print Packet"));

        saveScreenshot("buyer-unlock-flow.png");
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
        var response = page.navigate(url("/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central.html.html"));

        assertTrue(response != null);
        assertTrue(response.status() == 410);
        assertTrue(page.url().endsWith("/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central.html.html"));
        assertTrue(page.locator("body").innerText().contains("This URL is no longer part of the site."));
        assertTrue(page.locator("a[href='/home-repair']").first().isVisible());

        saveScreenshot("risk-detail-gone.png");
    }

    private void completeStepOne(String relationship, String era) {
        page.navigate(url("/home-repair"));
        page.locator("select[name='metroCode']").selectOption(new SelectOption().setIndex(0));
        page.locator("select[name='era']").selectOption(era);
        page.locator("input[name='relationship'][value='" + relationship + "']")
                .check(new Locator.CheckOptions().setForce(true));
        page.locator("form[action='/home-repair/step-2'] button[type='submit']").click();
        page.waitForURL("**/home-repair/step-2");
        assertTrue(page.url().contains("/home-repair/step-2"));
    }

    private void recreateMobileContext() {
        if (context != null) {
            context.close();
        }
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(390, 844));
        page = context.newPage();
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
