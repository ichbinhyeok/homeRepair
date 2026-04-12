<footer class="app-footer">
    <div class="footer-shell">
        <div class="footer-grid">
            <div class="footer-panel">
                <div class="footer-brand">LifeVerdict</div>
                <p>
                    A buyer-side inspection planning tool that turns home-age risk, metro labor rates, and inspection findings into a cleaner seller-credit request.
                </p>
            </div>

            <div class="footer-panel">
                <h4>Packet</h4>
                <div class="footer-links-stack">
                    <a href="/home-repair">Start planner</a>
                    <a href="/home-repair?relationship=BUYING">Build seller-credit packet</a>
                    <a href="/home-repair?relationship=BUYING&loanType=CONVENTIONAL">Conventional buyer</a>
                    <a href="/home-repair?relationship=BUYING&loanType=FHA">FHA buyer</a>
                </div>
            </div>

            <div class="footer-panel">
                <h4>Loan path</h4>
                <div class="footer-links-stack">
                    <a href="/home-repair?relationship=BUYING&loanType=VA">VA buyer</a>
                    <a href="/home-repair?relationship=BUYING&loanType=CASH">Cash offer</a>
                    <a href="/home-repair?relationship=BUYING&loanType=INVESTOR">Investor offer</a>
                    <a href="/home-repair?relationship=BUYING">Seller-credit packet</a>
                </div>
            </div>
        </div>

        <div class="footer-divider">
            <p class="footer-meta">
                Estimates are informational only and based on regional averages. Actual contractor pricing varies by layout, condition, and market timing. LifeVerdict does not provide engineering, legal, or contractor services.
            </p>
            <p class="footer-meta" style="margin-top: 10px;">
                Legal and support:
                <a href="/privacy-policy">Privacy policy</a>,
                <a href="/terms-of-service">Terms of Service</a>,
                <a href="/home-repair/disclaimer">Disclaimer</a>
            </p>
            <p class="footer-meta" style="margin-top: 14px; opacity: 0.7;">(c) 2026 LifeVerdict. All rights reserved.</p>
        </div>
    </div>
</footer>

<script>
    (function () {
        document.addEventListener("click", function (event) {
            var target = event.target;
            if (!target || typeof target.closest !== "function") {
                return;
            }

            var tracked = target.closest("[data-analytics-event]");
            if (!tracked) {
                return;
            }

            if (typeof window.gtag !== "function") {
                return;
            }

            var eventName = tracked.getAttribute("data-analytics-event");
            if (!eventName) {
                return;
            }

            window.gtag("event", eventName, {
                event_category: tracked.getAttribute("data-analytics-category") || "engagement",
                event_label: tracked.getAttribute("data-analytics-label") || "",
                page_path: window.location.pathname,
                link_url: tracked.getAttribute("href") || ""
            });
        }, true);
    })();
</script>
