@import com.livingcostcheck.home_repair.web.AcquisitionSurface
@param AcquisitionSurface surface = com.livingcostcheck.home_repair.web.AcquisitionSurface.LETTER
@param java.util.List<AcquisitionSurface> relatedSurfaces = java.util.List.of()
@param String canonicalUrl = "https://lifeverdict.com/inspection-response-letter"

@template.layout(
    title = surface.pageTitle(),
    description = surface.pageDescription(),
    showHeader = true,
    headContent = @`
        <link rel="canonical" href="${canonicalUrl}">
        <script type="application/ld+json">
        {
          "@context": "https://schema.org",
          "@type": "SoftwareApplication",
          "name": "LifeVerdict ${surface.navLabel()} Tool",
          "applicationCategory": "BusinessApplication",
          "operatingSystem": "Web",
          "url": "${canonicalUrl}",
          "description": "${surface.pageDescription()}",
          "offers": {
            "@type": "Offer",
            "price": "0",
            "priceCurrency": "USD"
          }
        }
        </script>
    `,
    content = @`
    <div class="app-shell page-pad">
        <section class="tool-hero">
            <div class="hero-panel">
                <div class="hero-eyebrow">${surface.heroEyebrow()}</div>
                <div class="variant-switcher" aria-label="Related starting points">
                    @for(AcquisitionSurface route : relatedSurfaces)
                        <a href="${route.path()}"
                           data-analytics-event="switch_acquisition_surface"
                           data-analytics-category="navigation"
                           data-analytics-label="${surface.code()}_to_${route.code()}"
                           class="${route == surface ? "variant-link active" : "variant-link"}">${route.navLabel()}</a>
                    @endfor
                </div>

                <h1 class="hero-title">
                    Before you send the ${surface.navLabel()}, <span class="hero-accent">check what to ask, what to cut, and what to send if the seller pushes back</span>.
                </h1>
                <p class="hero-copy">
                    This is not an article page. Paste the proposed request and inspection findings, then get a Send, Revise, or Do Not Send verdict with the weak items removed and the fallback wording ready.
                </p>
                @if(surface == AcquisitionSurface.LETTER)
                    <p class="micro-note">
                        Need an inspection response letter after a home inspection? Start here, then let the packet build the ask, fallback, and evidence-backed note.
                    </p>
                @endif

                <div class="tool-mode-strip" aria-label="Tool workflow">
                    <div class="tool-mode-step">
                        <span>Input</span>
                        <strong>Draft ask + inspection findings</strong>
                    </div>
                    <div class="tool-mode-step">
                        <span>Check</span>
                        <strong>Overreach, evidence, seller pushback</strong>
                    </div>
                    <div class="tool-mode-step">
                        <span>Output</span>
                        <strong>Sendable ask, cut list, fallback note</strong>
                    </div>
                </div>

                <div class="hero-actions">
                    <a href="${surface.primaryCtaHref()}"
                       data-analytics-event="open_packet_tool"
                       data-analytics-category="acquisition"
                       data-analytics-label="${surface.primaryAnalyticsLabel()}"
                       class="button-primary">${surface.primaryCtaLabel()}</a>
                    <a href="/home-repair/methodology" class="button-secondary">See How The Tool Works</a>
                </div>

                <div class="packet-peek">
                    <div class="packet-peek-top">
                        <div>
                            <div class="packet-peek-kicker">Pre-send review preview</div>
                            <strong>${surface.navLabel()} sendability check</strong>
                        </div>
                        <div class="packet-peek-status">Verdict: revise before send</div>
                    </div>

                    <div class="packet-peek-amounts">
                        <div class="packet-peek-amount">
                            <span>Tool-reviewed ask</span>
                            <strong>$35,500</strong>
                        </div>
                        <div class="packet-peek-amount">
                            <span>Fallback floor</span>
                            <strong>$30,500</strong>
                        </div>
                    </div>

                    <div class="packet-peek-note">
                        The checker keeps serious leverage in scope, cuts weak items that make the buyer look unreasonable, and prepares the fallback if the seller counters.
                    </div>

                    <ul class="packet-peek-list">
                        <li>Verdict appears before the packet language.</li>
                        <li>The cut list explains what should not lead the first ask.</li>
                        <li>The fallback gives the agent a second move if the seller pushes back.</li>
                    </ul>

                    <div class="packet-peek-proof">
                        <span class="packet-proof-chip">Send posture</span>
                        <span class="packet-proof-chip">Number basis</span>
                        <span class="packet-proof-chip">Cut list</span>
                        <span class="packet-proof-chip">Seller fallback</span>
                    </div>
                </div>

                <div class="hero-intent-strip">
                    @for(String chip : surface.intentChips())
                        <span class="intent-chip">${chip}</span>
                    @endfor
                </div>
            </div>

            <div class="tool-form-panel">
                <div class="panel-kicker">Tool entry mode</div>
                <h2 class="panel-title">${surface.panelTitle()}</h2>
                <p class="panel-note">
                    ${surface.panelNote()}
                </p>

                <div class="tool-entry-card">
                    <div class="tool-entry-row">
                        <span>01</span>
                        <div>
                            <strong>Paste the ask you are considering.</strong>
                            <p>Use the buyer's draft, agent note, repair addendum language, or the seller-credit number they want to send.</p>
                        </div>
                    </div>
                    <div class="tool-entry-row">
                        <span>02</span>
                        <div>
                            <strong>Add the facts the other side will use to push back.</strong>
                            <p>Report text, quote support, loan pressure, deadline, and seller response change whether the ask is safe to send.</p>
                        </div>
                    </div>
                    <div class="tool-entry-row">
                        <span>03</span>
                        <div>
                            <strong>Leave with the next message.</strong>
                            <p>The output gives the verdict, revised ask, cut list, fallback, and buyer-agent wording together.</p>
                        </div>
                    </div>
                </div>

                <div class="mt-6 flex flex-wrap gap-3">
                    <a href="${surface.primaryCtaHref()}"
                       data-analytics-event="open_packet_tool"
                       data-analytics-category="acquisition"
                       data-analytics-label="${surface.primaryAnalyticsLabel()}_secondary"
                       class="button-primary">Run Free Pre-Send Check</a>
                    <a href="/home-repair/methodology" class="button-secondary">See Tool Method</a>
                </div>

                <div class="support-callout">
                    <strong>Buyer-agent workflow first</strong>
                    Use it when the next step is to decide what to ask for and whether it is safe to send. Buyers can use it directly, but the review is shaped for the buyer-agent workflow.
                </div>
                <div class="support-callout">
                    <strong>Small buyer-agent teams are the money-nearest ICP</strong>
                    If you handle financed inspection-response files repeatedly, this is meant to become the fastest way to turn a proposed ask into one defensible request, one fallback, and one evidence-backed note.
                </div>
            </div>
        </section>

        <section class="spotlight-grid">
            <div class="spotlight-panel warm">
                <div class="section-kicker">What goes in</div>
                <h2>Only the details that can actually move the deal.</h2>
                <ul>
                    <li>Inspection findings, disclosures, and notes that change leverage.</li>
                    <li>Optional report upload for matched evidence pages and OCR-backed scans.</li>
                    <li>Loan and timing context only when it changes how the ask should be framed.</li>
                </ul>
            </div>

            <div class="spotlight-panel cool">
                <div class="section-kicker">What comes out</div>
                <h2>A pre-send review that becomes the message, the cut list, and the fallback.</h2>
                <ul>
                    <li>Send posture, opening ask, defensible fallback, and a clear boundary around what should stay out.</li>
                    <li>Agent-ready wording instead of a defect list the buyer has to rewrite under pressure.</li>
                    <li>Evidence references and next actions in the same packet.</li>
                </ul>
            </div>
        </section>

        <section class="sample-output-shell">
            <div class="sample-case-panel">
                <div class="sample-case-head">
                    <div>
                        <div class="section-kicker">Sample tool run</div>
                        <h2>${surface.sampleCase().title()}</h2>
                    </div>
                    @if(surface.hasDedicatedSampleCase())
                        <span class="sample-case-badge">Dedicated high-intent example</span>
                    @else
                        <span class="sample-case-badge">Cluster example</span>
                    @endif
                </div>

                <div class="sample-case-grid">
                    <div class="sample-case-input">
                        <span>Situation</span>
                        <p>${surface.sampleCase().situation()}</p>
                    </div>
                    <div class="sample-case-input">
                        <span>Proposed ask</span>
                        <p>${surface.sampleCase().proposedAsk()}</p>
                    </div>
                    <div class="sample-case-output">
                        <span>Tool verdict</span>
                        <strong>${surface.sampleCase().verdict()}</strong>
                    </div>
                    <div class="sample-case-output">
                        <span>Revised ask</span>
                        <p>${surface.sampleCase().revisedAsk()}</p>
                    </div>
                    <div class="sample-case-output">
                        <span>Fallback</span>
                        <p>${surface.sampleCase().fallback()}</p>
                    </div>
                    <div class="sample-case-warning">
                        <span>Trust gate</span>
                        <p>${surface.sampleCase().trustWarning()}</p>
                    </div>
                </div>

                <div class="sample-case-reason">
                    <strong>Why this is useful</strong>
                    <p>${surface.sampleCase().whyItWorks()}</p>
                </div>
            </div>
        </section>

        <section class="intent-shell">
            <div class="section-kicker">${surface.proofSection().kicker()}</div>
            <h2 class="intent-title">${surface.proofSection().summary()}</h2>
            <div class="support-grid mt-5">
                @for(AcquisitionSurface.ProofCard proofCard : surface.proofSection().cards())
                    <article class="support-card intent-card">
                        <h3>${proofCard.title()}</h3>
                        <p>${proofCard.body()}</p>
                    </article>
                @endfor
            </div>
        </section>

        <section class="intent-shell">
            <div class="section-kicker">Why agents can trust it</div>
            <h2 class="intent-title">The packet is useful because it shows what it cut, not just what it asks for.</h2>
            <div class="support-grid mt-5">
                <article class="support-card intent-card">
                    <h3>Evidence stays attached</h3>
                    <p>The strongest items stay tied to report language, page references, and OCR-backed scans so the ask does not feel invented after the fact.</p>
                </article>
                <article class="support-card intent-card">
                    <h3>The ask stays narrow</h3>
                    <p>LifeVerdict separates true leverage from cosmetic noise, ordinary maintenance, and wish-list upgrades before they weaken the first response.</p>
                </article>
                <article class="support-card intent-card">
                    <h3>The next move is included</h3>
                    <p>The result is not just analysis. It includes the send posture, revised wording, cut list, fallback, and evidence checklist a buyer agent can review.</p>
                </article>
            </div>
        </section>

        <section class="intent-shell">
            <div class="section-kicker">Who this is for</div>
            <h2 class="intent-title">The surface is broad enough for search, but the product customer is still narrow.</h2>
            <div class="support-grid support-rail mt-5">
                <article class="support-card intent-card">
                    <h3>Primary customer</h3>
                    <p>Buyer agents who need to check whether a proposed inspection ask is defensible across live deals.</p>
                </article>
                <article class="support-card intent-card">
                    <h3>Secondary customer</h3>
                    <p>Buyers under contract who want a stronger first draft before sending the packet through their agent workflow.</p>
                </article>
                <article class="support-card intent-card">
                    <h3>Not the customer</h3>
                    <p>Not an inspector report-writing suite, not a lender workflow, and not a broad homeowner repair planning site.</p>
                </article>
            </div>
        </section>

        <section class="intent-shell">
            <div class="section-kicker">Repeat-use path</div>
            <h2 class="intent-title">The money signal is not a page view. It is a buyer-agent team running a real file twice.</h2>
            <div class="support-grid support-rail mt-5">
                <article class="support-card intent-card">
                    <h3>1. Run one live file free</h3>
                    <p>Start with the proposed ask, report language, loan context, and deadline. No email gate is needed to validate the first packet.</p>
                </article>
                <article class="support-card intent-card">
                    <h3>2. Copy or print the packet</h3>
                    <p>The useful signal is whether the agent keeps the revised ask, evidence checklist, and fallback instead of rewriting from scratch.</p>
                </article>
                <article class="support-card intent-card">
                    <h3>3. Request team setup</h3>
                    <p>Only after a generated packet does LifeVerdict ask whether the team wants templates, broker-ready export, or repeat workflow support.</p>
                </article>
            </div>
        </section>

        <section class="intent-shell">
            <div class="section-kicker">Common starting points</div>
            <h2 class="intent-title">Start from the question that sounds most like yours.</h2>
            <div class="support-grid support-rail mt-5">
                    @for(AcquisitionSurface.PromptCard prompt : surface.promptCards())
                        <article class="support-card intent-card">
                            <h3>${prompt.title()}</h3>
                            <p>${prompt.body()}</p>
                            <a href="${surface.primaryCtaHref()}"
                               data-analytics-event="open_packet_tool"
                               data-analytics-category="acquisition"
                               data-analytics-label="${surface.primaryAnalyticsLabel()}_prompt"
                               class="intent-link">${prompt.ctaLabel()}</a>
                        </article>
                    @endfor
                </div>
            </section>

        <section class="intent-shell">
            <div class="section-kicker">Commercial and proof pages</div>
            <h2 class="intent-title">Use these when the visitor needs category proof before they open the tool.</h2>
            <div class="support-grid support-rail mt-5">
                <article class="support-card intent-card">
                    <h3>For buyer-agent teams</h3>
                    <p>A commercial landing page for small buyer-agent teams handling financed post-inspection files.</p>
                    <a href="/for-buyer-agents"
                       data-analytics-event="open_acquisition_surface"
                       data-analytics-category="navigation"
                       data-analytics-label="agent_team"
                       class="intent-link">Open buyer-agent page -></a>
                </article>
                <article class="support-card intent-card">
                    <h3>Sample packet</h3>
                    <p>A proof page that shows what the finished inspection ask pre-send review actually looks like.</p>
                    <a href="/sample-seller-credit-request-after-home-inspection"
                       data-analytics-event="open_acquisition_surface"
                       data-analytics-category="navigation"
                       data-analytics-label="sample_packet"
                       class="intent-link">Open sample packet -></a>
                </article>
                <article class="support-card intent-card">
                    <h3>FHA / VA repairs</h3>
                    <p>A lender-sensitive entry page for buyers and agents who are worried about financed inspection issues.</p>
                    <a href="/fha-va-inspection-repairs-and-seller-credit"
                       data-analytics-event="open_acquisition_surface"
                       data-analytics-category="navigation"
                       data-analytics-label="financing"
                       class="intent-link">Open financing page -></a>
                </article>
            </div>
        </section>

        <section class="intent-shell">
            <div class="section-kicker">Questions before you start</div>
            <h2 class="intent-title">What most buyers want to know before they open the tool.</h2>
            <div class="support-grid support-rail mt-5">
                @for(AcquisitionSurface.FaqItem faqItem : surface.faqItems())
                    <article class="support-card intent-card">
                        <h3>${faqItem.question()}</h3>
                        <p>${faqItem.answer()}</p>
                    </article>
                @endfor
            </div>
        </section>
    </div>
    `
)
