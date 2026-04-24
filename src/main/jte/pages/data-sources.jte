@template.layout(
    title = "Data Sources | LifeVerdict",
    description = "The data layers and open downloads used by the LifeVerdict inspection response tool.",
    content = @`
        <div class="support-shell">
            <section class="support-hero">
                <div class="section-kicker">Data sources</div>
                <h1 class="support-title">Data Integrity & Sources</h1>
                <p class="support-lede">
                    LifeVerdict uses multiple layers of data, but the product still begins with the user's inspection text. Data supports the packet; it does not replace the report.
                </p>
                <div class="support-meta">
                    <span>Inspection-first</span>
                    <span>Cost basis + rule layers</span>
                    <span>Updated April 23, 2026</span>
                </div>
            </section>

            <section class="support-grid">
                <article class="support-card">
                    <h2>User-provided evidence</h2>
                    <p>
                        The primary source is the report text supplied by the user. That can include pasted inspection summaries, quick-add findings, PDF report text, TXT exports, and OCR-backed image uploads.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Repair basis libraries</h2>
                    <p>
                        Cost sizing draws from the integrated construction cost library, metro labor/logistics indices, metro master data, and the repair engine specification bundled with the app. These libraries help scope leverage; they do not define the packet on their own.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Rule layers</h2>
                    <p>
                        Lender-visible rules, component signals, must-request terms, and do-not-lead filters shape how findings are framed. These rules exist to keep the packet narrow, defensible, and usable in a real transaction.
                    </p>
                </article>
            </section>

            <section class="support-grid mt-5">
                <article class="support-card">
                    <h2>Open Data Downloads (CSV/JSON)</h2>
                    <p>
                        We publish derived metro-level open-data files for traceability and regional context.
                    </p>
                    <ul class="support-list">
                        <li><a href="/data/metro_unique_signals_2026.csv">metro_unique_signals_2026.csv</a></li>
                        <li><a href="/data/metro_unique_signals_2026.json">metro_unique_signals_2026.json</a></li>
                    </ul>
                    <p class="micro-note">Snapshot date: February 28, 2026. Sources include FEMA OpenFEMA disaster declarations and U.S. Census ACS 2024 5-year tables.</p>
                </article>

                <article class="support-card">
                    <h2>How to read the packet</h2>
                    <p>
                        The packet never claims that the open-data files are the same thing as the user's property. They are context layers. The report text, uploaded evidence, and matched lead items are what define the packet.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Validation signal</h2>
                    <p>
                        Success is measured by packet actions such as copy, print, and useful feedback in active deal flow. The core question is whether the packet is strong enough to use when a response is actually due.
                    </p>
                </article>
            </section>
        </div>
    `
)
