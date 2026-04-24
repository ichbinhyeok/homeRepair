@template.layout(
    title = "Methodology | LifeVerdict",
    description = "How LifeVerdict checks whether an inspection ask is send-ready before turning it into a response packet.",
    content = @`
        <div class="support-shell">
            <section class="support-hero">
                <div class="section-kicker">Methodology</div>
                <h1 class="support-title">How the ask pre-send check works.</h1>
                <p class="support-lede">
                    The product first checks whether the proposed ask is send-ready. Only then does it assemble the revised response artifact for the inspection window.
                </p>
                <div class="support-meta">
                    <span>Buyer-agent workflow first</span>
                    <span>Scoped exposure model</span>
                    <span>Updated April 23, 2026</span>
                </div>
            </section>

            <section class="support-stat-grid">
                <article class="support-stat">
                    <strong>1. Parse</strong>
                    <span>Read pasted findings, uploaded report text, and OCR-backed scans into a normalized finding set.</span>
                </article>
                <article class="support-stat">
                    <strong>2. Scope</strong>
                    <span>Match findings to lead anchors, verify items, and do-not-lead items before any ask range is built.</span>
                </article>
                <article class="support-stat">
                    <strong>3. Defend</strong>
                    <span>Produce send posture, number basis, evidence checks, fallback, agent note, and readiness gates in one artifact.</span>
                </article>
            </section>

            <section class="spotlight-grid mt-5">
                <div class="spotlight-panel cool">
                    <div class="section-kicker">What this is for</div>
                    <h2>Buyer agents first, then buyers under contract who need a reviewed first draft.</h2>
                    <ul>
                        <li>Buyer agents who need a narrow, defensible first ask fast.</li>
                        <li>Buyers under contract with a short response window.</li>
                        <li>Inspection asks that need a send, revise, or do-not-send verdict quickly.</li>
                    </ul>
                </div>
                <div class="spotlight-panel warm">
                    <div class="section-kicker">What this is not</div>
                    <h2>Not an inspector suite, broad repair-cost blog, full renovation planner, or lender approval promise.</h2>
                    <ul>
                        <li>The product is not trying to replace inspection report software.</li>
                        <li>The product is not trying to estimate every cosmetic project.</li>
                        <li>The packet is not a contractor bid or underwriting guarantee.</li>
                        <li>The output is intentionally narrower than the total future maintenance backlog.</li>
                    </ul>
                </div>
            </section>

            <section class="support-grid mt-5">
                <article class="support-card">
                    <h2>Inspection extraction</h2>
                    <p>
                        The first layer is the user's own report language. Pasted text and quick-add findings are normalized into a concise set of inspection items. If the user uploads a PDF, TXT, or image, the evidence layer tries to match the strongest items back to report pages.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Lead vs. verify vs. cut</h2>
                    <p>
                        Each finding is evaluated against must-request terms, verify-next terms, component signals, and verdict-plan anchors. The goal is not to preserve every detail from the report. The goal is to keep only what improves negotiation leverage and cut what weakens the ask.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Scoped exposure</h2>
                    <p>
                        Cost data is used only after the findings are scoped. Scoped exposure means lead items plus a disciplined verify reserve, so the ask reflects the live deal instead of the entire future repair backlog.
                    </p>
                </article>
            </section>

            <section class="support-grid mt-5">
                <article class="support-card">
                    <h2>Ask range construction</h2>
                    <p>
                        The defensible, target, and stretch asks are influenced by lead-anchor strength, quote support, closing window, loan posture, lender-visible signals, and matched report evidence. If quote or evidence support is missing, the model stays more conservative and keeps the result in review mode.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Evidence and OCR</h2>
                    <p>
                        If a citation is marked <code>(OCR)</code>, the packet is telling the user that the line was recovered from a scan or photo and should be checked against the source page before sending. OCR raises usability, but it never removes the need for user verification.
                    </p>
                </article>

                <article class="support-card">
                    <h2>Pre-send gates</h2>
                    <p>
                        The visible verdict is based on hard gates: deadline, form path, evidence, financing boundary, stage, ownership, number basis, seller pushback, and send posture. Confidence is secondary; gates decide whether the ask is safe to send.
                    </p>
                </article>
            </section>

            <section class="support-card mt-5">
                <h2>Limitations</h2>
                <ul class="support-list">
                    <li>The reviewed ask is not a binding contractor quote.</li>
                    <li>Hidden conditions, access constraints, and local contractor availability can materially change actual pricing.</li>
                    <li>Lender-visible logic is an aid to negotiation framing, not a promise of appraisal or underwriting outcomes.</li>
                    <li>Users should confirm OCR-marked excerpts against the source page before sending.</li>
                </ul>
            </section>

            <section class="intent-shell">
                <div class="section-kicker">Method questions</div>
                <h2 class="intent-title">Three practical questions behind the product choices.</h2>
                <div class="support-grid mt-5">
                    <article class="support-card intent-card">
                        <h3>Why seller credit first?</h3>
                        <p>Because buyer agents usually need one clean, defensible ask more than they need a sprawling repair specification. Seller-credit posture can survive short response windows better, but the product still checks whether credit wording is appropriate.</p>
                    </article>
                    <article class="support-card intent-card">
                        <h3>Why can local context be optional?</h3>
                    <p>Speed matters. The first-pass job is to judge the ask. Local market and age context improve precision, but a broad U.S. baseline is better than losing the user before the pre-send check exists.</p>
                    </article>
                    <article class="support-card intent-card">
                        <h3>Why is the core flow free during validation?</h3>
                        <p>Because the current proof point is whether buyers and agents actually use the pre-send check, copy the revised packet, and trust it enough to review real files. Charging before that proof would slow the learning loop.</p>
                    </article>
                </div>
            </section>
        </div>
    `
)
