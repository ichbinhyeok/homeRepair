<footer class="app-footer">
    <div class="footer-shell">
        <div class="footer-grid">
            <div class="footer-panel">
                <div class="footer-brand">LifeVerdict</div>
                <p>
                    An inspection response copilot built for buyer agents first, and for buyers under contract who need a packet their agent can actually send.
                </p>
                <p class="footer-note">Free during validation.</p>
            </div>

            <div class="footer-panel">
                <h4>Product</h4>
                <div class="footer-links-stack">
                    <a href="/home-repair">Open the intake tool</a>
                    <a href="/for-buyer-agents">For buyer-agent teams</a>
                    <a href="/sample-seller-credit-request-after-home-inspection">Sample packet</a>
                    <a href="/home-repair/about">About the product</a>
                    <a href="/home-repair/methodology">Methodology</a>
                    <a href="/home-repair/data-sources">Data sources</a>
                </div>
            </div>

            <div class="footer-panel">
                <h4>Trust & legal</h4>
                <div class="footer-links-stack">
                    <a href="/home-repair/disclaimer">Disclaimer</a>
                    <a href="/home-repair/editorial-policy">Editorial policy</a>
                    <a href="/privacy-policy">Privacy policy</a>
                    <a href="/terms-of-service">Terms of service</a>
                </div>
            </div>
        </div>

        <div class="footer-divider">
            <p class="footer-meta">
                Packet language and ask ranges are informational only. Actual contractor pricing, underwriting treatment, and negotiation outcomes vary by property, market, contract, jurisdiction, and deal terms. LifeVerdict does not provide engineering, legal, lending, brokerage, or contractor services.
            </p>
            <p class="footer-meta footer-meta-soft">(c) 2026 LifeVerdict. All rights reserved.</p>
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
