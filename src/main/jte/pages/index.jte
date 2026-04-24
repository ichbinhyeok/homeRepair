@import com.livingcostcheck.home_repair.web.AcquisitionSurface
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionWorkspaceSummary
@import java.util.List
@param List<String> metros
@param String selectedMetroCode = null
@param String selectedEra = null
@param String selectedRelationship = "BUYING"
@param String selectedEntry = "direct"
@param AcquisitionSurface selectedSurface = null
@param String errorMessage = null
@param String legacyNotice = null
@param List<AcquisitionSurface> acquisitionSurfaces = java.util.List.of()
@param List<InspectionWorkspaceSummary> recentWorkspaces = java.util.List.of()

@template.layout(
    title = "agent_team".equals(selectedEntry)
        ? "Buyer-agent inspection request check before send | LifeVerdict"
        : "financing".equals(selectedEntry)
            ? "FHA / VA inspection request check before send | LifeVerdict"
        : selectedSurface != null
        ? "Check a " + selectedSurface.navLabel() + " after inspection | LifeVerdict"
        : "Free home inspection repair request check before send | LifeVerdict",
    description = "agent_team".equals(selectedEntry)
        ? "Check what to ask for, what to cut, and what fallback to use before a buyer-agent inspection request is sent."
        : "financing".equals(selectedEntry)
            ? "Check what to ask, what to repair first, and what credit wording to avoid before an FHA or VA inspection request is sent."
        : selectedSurface != null
        ? "Keep the " + selectedSurface.navLabel().toLowerCase() + " posture and check what to ask, what to cut, and what fallback to use."
        : "After a home inspection, paste the proposed request and findings to check whether it is send-ready, too broad, or unsafe to send.",
    headContent = @`
        <link rel="canonical" href="https://lifeverdict.com/home-repair">
    `,
    content = @`
        <div class="app-shell page-pad">
            <section class="tool-hero">
                <div class="tool-form-panel" id="packet-builder">
                    <div class="panel-kicker">
                        @if("agent_team".equals(selectedEntry))
                            Started from Buyer-Agent Teams
                        @elseif("financing".equals(selectedEntry))
                            Started from FHA / VA Repairs
                        @elseif(selectedSurface != null)
                            Started from ${selectedSurface.navLabel()}
                        @else
                            Buyer-agent inspection desk
                        @endif
                    </div>
                    <h2 class="panel-title">
                        @if("agent_team".equals(selectedEntry))
                            Buyer-agent team intake: check the live-file ask before anyone sends it.
                        @elseif("financing".equals(selectedEntry))
                            Lender-sensitive intake: check whether the ask creates FHA / VA / lender review risk.
                        @elseif(selectedSurface != null)
                            ${selectedSurface.panelTitle()}
                        @else
                            Paste the request before you send it. LifeVerdict will mark what survives, what to cut, and what fallback to use.
                        @endif
                    </h2>
                    <p class="panel-note">
                        @if("agent_team".equals(selectedEntry))
                            This click came from a buyer-agent team page. Keep the team context, paste the proposed ask and findings, then check whether it is send-ready or needs revision.
                        @elseif("financing".equals(selectedEntry))
                            This click came from a financing-sensitive page. Keep the FHA / VA / lender-visible framing, then use the deal settings below to catch credit-vs-repair and closing-risk problems.
                        @elseif(selectedSurface != null)
                            ${selectedSurface.panelNote()} Open the pre-send check below to keep that posture, then use optional settings only if the live deal needs more precision.
                        @else
                            Fastest path: paste the proposed ask and the inspection findings, optionally upload the report, and get a free sendability verdict. Local context stays optional.
                        @endif
                    </p>

                    @if(errorMessage != null && !errorMessage.isBlank())
                        <div class="mt-4 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-left text-sm font-semibold text-red-700">
                            ${errorMessage}
                        </div>
                    @endif
                    @if(legacyNotice != null && !legacyNotice.isBlank())
                        <div class="mt-4 rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-left text-sm font-semibold text-amber-800">
                            ${legacyNotice}
                        </div>
                    @endif

                    <div class="signal-grid mt-6">
                        <div class="signal-card">
                            <div class="signal-step">01</div>
                            <h3>Paste the draft</h3>
                            <p>Drop in the buyer's repair request, seller-credit ask, or agent note before it goes out.</p>
                        </div>
                        <div class="signal-card">
                            <div class="signal-step">02</div>
                            <h3>Cut weak items</h3>
                            <p>Separate leverage from cosmetic noise, age-only complaints, and betterment requests.</p>
                        </div>
                        <div class="signal-card">
                            <div class="signal-step">03</div>
                            <h3>Get the next message</h3>
                            <p>Leave with Send, Revise, or Do Not Send plus the revised ask and fallback.</p>
                        </div>
                    </div>

                    <form action="/home-repair/verdict" method="post" enctype="multipart/form-data" class="space-y-5 mt-6">
                        <input type="hidden" name="relationship" value="BUYING">
                        <input type="hidden" name="condition" value="SEVERE">
                        <input type="hidden" name="budget" value="50000">
                        <input type="hidden" name="sqft" value="1800">
                        <input type="hidden" name="entry" value="${selectedEntry}">

                        <div class="intake-section" id="inspection-intake">
                            <div class="micro-label">Start here</div>
                            <label class="field-label">Paste the repair request or seller-credit ask before you send it</label>
                            <textarea name="inspectionReportText" rows="8" placeholder="Example:&#10;Proposed ask: $18,500 seller credit for roof, panel, and HVAC.&#10;1. Active roof leak above rear bedroom.&#10;2. Federal Pacific panel flagged by inspector.&#10;3. HVAC failed to maintain temperature during inspection."></textarea>
                            <p class="field-note">The job is not to preserve every defect. The job is to decide what to ask, what to cut, and what fallback still works if the seller pushes back.</p>
                        </div>

                        <div class="intake-section">
                            <div class="micro-label">Evidence</div>
                            <label class="field-label">Upload inspection PDF, TXT, or photos to support the pre-send check</label>
                            <input type="file" name="inspectionReportFile" accept=".pdf,.txt,.png,.jpg,.jpeg,application/pdf,text/plain,image/png,image/jpeg">
                            <p class="field-note">Without evidence, the result stays conservative. Uploading the report lets the pre-send review cite matching pages or OCR-backed excerpts.</p>
                        </div>

                        <div class="intake-section">
                            <div class="micro-label">Optional pressure notes</div>
                            <div class="grid grid-cols-1 gap-3 mt-3">
                                <input type="text" name="inspectionFinding" placeholder="e.g. Buyer wants to ask for every item in the report">
                                <input type="text" name="inspectionFinding" placeholder="e.g. Seller is already pushing back on roof credit">
                            </div>
                            <p class="field-note">Use these for real-world pressure that changes whether the ask is too broad, too weak, or lender-sensitive.</p>
                        </div>

                        <button type="submit" data-analytics-event="generate_response_packet" data-analytics-category="activation" data-analytics-label="home_${selectedEntry}" class="button-primary w-full">
                            @if("agent_team".equals(selectedEntry))
                                Check Buyer-Agent Ask ->
                            @elseif("financing".equals(selectedEntry))
                                Check FHA / VA Ask ->
                            @elseif(selectedSurface != null)
                                Check ${selectedSurface.navLabel()} Ask ->
                            @else
                                Check Before Sending Free ->
                            @endif
                        </button>

                        <p class="field-note text-center">
                            Pre-send check, revised packet, copy, print, and evidence matching stay free during validation.
                        </p>

                        <details class="intake-section rounded-[22px] border border-[rgba(16,32,42,0.10)] bg-white/60 p-4">
                            <summary class="cursor-pointer text-sm font-semibold text-slate-800">Optional deal settings</summary>
                            <p class="field-note mt-3">
                                These defaults already work for the first draft. Open this only if the deal posture matters right now.
                            </p>

                            <div class="form-grid-three mt-4">
                                <div>
                                    <label class="field-label">Contract workflow</label>
                                    <select name="contractWorkflow">
                                        <option value="AUTO" selected>Auto-detect from state and entry</option>
                                        <option value="CA_INVESTIGATION">California investigation contingency</option>
                                        <option value="TX_OPTION">Texas option period / amendment flow</option>
                                        <option value="FL_AS_IS">Florida FR/Bar AS IS contract</option>
                                        <option value="FL_STANDARD">Florida standard residential contract</option>
                                        <option value="CO_OBJECTION">Colorado inspection objection / resolution</option>
                                        <option value="GENERAL_AMENDMENT">General amendment / addendum flow</option>
                                    </select>
                                    <p class="field-note">Use this if the deal already has a known contract form. It sharpens the handoff instructions.</p>
                                </div>
                                <div>
                                    <label class="field-label">Deal stage</label>
                                    <select name="dealStage">
                                        <option value="DRAFTING_FIRST_NOTICE" selected>Drafting first notice</option>
                                        <option value="NOTICE_SENT_WAITING">Notice sent, waiting on seller</option>
                                        <option value="COUNTER_RECEIVED">Seller counter received</option>
                                        <option value="TERMS_AGREED_NEED_AMENDMENT">Terms agreed, amendment still needs to be drafted</option>
                                        <option value="TERMINATION_CONSIDERED">Termination is being considered</option>
                                        <option value="CONTINGENCY_REMOVED_OR_EXPIRED">Contingency removed or expired</option>
                                    </select>
                                    <p class="field-note">This changes the handoff guidance. A first ask and a post-counter file do not need the same next step.</p>
                                </div>
                                <div>
                                    <label class="field-label">Loan posture</label>
                                    <select name="loanType">
                                        <option value="CONVENTIONAL" selected>Conventional buyer</option>
                                        <option value="FHA">FHA buyer</option>
                                        <option value="VA">VA buyer</option>
                                        <option value="CASH">Cash offer</option>
                                        <option value="INVESTOR">Investor / DSCR</option>
                                    </select>
                                    <p class="field-note">This changes negotiation pressure and wording risk, not the core job.</p>
                                </div>
                                <div>
                                    <label class="field-label">Outside quote support</label>
                                    <select name="quoteSupport">
                                        <option value="NONE" selected>No contractor quote yet</option>
                                        <option value="HAS_ONE">One quote in hand</option>
                                        <option value="MULTIPLE">Multiple quotes in hand</option>
                                    </select>
                                    <p class="field-note">Use quotes as support, not as a broader repair wishlist.</p>
                                </div>
                                <div>
                                    <label class="field-label">Response timeline</label>
                                    <select name="closingWindow">
                                        <option value="UNDER_7_DAYS">Under 7 days</option>
                                        <option value="SEVEN_TO_TWENTY_ONE_DAYS" selected>7-21 days</option>
                                        <option value="TWENTY_ONE_TO_FORTY_FIVE_DAYS">21-45 days</option>
                                        <option value="FLEXIBLE">Flexible / still evaluating</option>
                                    </select>
                                    <p class="field-note">Shorter windows reward a tighter, more defensible first ask.</p>
                                </div>
                                <div>
                                    <label class="field-label">Exact response deadline</label>
                                    <input type="datetime-local" name="responseDeadlineAt">
                                    <p class="field-note">Optional, but high-value. Use the live contract cutoff if you know it.</p>
                                </div>
                            </div>
                        </details>

                        <details class="intake-section rounded-[22px] border border-[rgba(16,32,42,0.10)] bg-white/60 p-4">
                            <summary class="cursor-pointer text-sm font-semibold text-slate-800">Optional case-file details</summary>
                            <p class="field-note mt-3">
                                Add these if you want the result to read like a reusable deal file instead of a one-time packet.
                            </p>

                            <div class="form-grid-two mt-4">
                                <div>
                                    <label class="field-label">Case label</label>
                                    <input type="text" name="caseLabel" placeholder="e.g. Maple Street response window">
                                </div>
                                <div>
                                    <label class="field-label">Property address</label>
                                    <input type="text" name="propertyAddress" placeholder="e.g. 123 Maple St, Atlanta, GA">
                                </div>
                                <div>
                                    <label class="field-label">Buyer / client name</label>
                                    <input type="text" name="clientName" placeholder="e.g. Mina Kim">
                                </div>
                                <div>
                                    <label class="field-label">Buyer agent</label>
                                    <input type="text" name="agentName" placeholder="e.g. Alex Park">
                                </div>
                            </div>
                        </details>

                        <details class="intake-section rounded-[22px] border border-[rgba(16,32,42,0.10)] bg-white/60 p-4">
                            <summary class="cursor-pointer text-sm font-semibold text-slate-800">Optional local property context</summary>
                            <p class="field-note mt-3">
                                Skip this for the fastest free pass. If you leave it blank, the packet uses a broad U.S. baseline and typical mid-age housing stock.
                            </p>

                            <div class="form-grid-two mt-4">
                                <div>
                                    <label class="field-label">Market</label>
                                    <select name="metroCode">
                                        <option value="">Use broad U.S. baseline (fastest)</option>
                                        @for(String metro : metros)
                                            @if(selectedMetroCode != null && selectedMetroCode.equals(metro))
                                                <option value="${metro}" selected>${metro.replace('_', ' ')}</option>
                                            @else
                                                <option value="${metro}">${metro.replace('_', ' ')}</option>
                                            @endif
                                        @endfor
                                    </select>
                                </div>

                                <div>
                                    <label class="field-label">Home age band</label>
                                    <select name="era">
                                        <option value="">Use typical mid-age housing stock</option>
                                        @if("PRE_1950".equals(selectedEra))
                                            <option value="PRE_1950" selected>Pre-1950</option>
                                        @else
                                            <option value="PRE_1950">Pre-1950</option>
                                        @endif
                                        @if("1950_1970".equals(selectedEra))
                                            <option value="1950_1970" selected>1950 - 1970</option>
                                        @else
                                            <option value="1950_1970">1950 - 1970</option>
                                        @endif
                                        @if("1970_1980".equals(selectedEra))
                                            <option value="1970_1980" selected>1970 - 1980</option>
                                        @else
                                            <option value="1970_1980">1970 - 1980</option>
                                        @endif
                                        @if("1980_1995".equals(selectedEra))
                                            <option value="1980_1995" selected>1980 - 1995</option>
                                        @else
                                            <option value="1980_1995">1980 - 1995</option>
                                        @endif
                                        @if("1995_2010".equals(selectedEra))
                                            <option value="1995_2010" selected>1995 - 2010</option>
                                        @else
                                            <option value="1995_2010">1995 - 2010</option>
                                        @endif
                                        @if("2010_PRESENT".equals(selectedEra))
                                            <option value="2010_PRESENT" selected>2010 - Present</option>
                                        @else
                                            <option value="2010_PRESENT">2010 - Present</option>
                                        @endif
                                    </select>
                                </div>
                            </div>

                            <div class="grid grid-cols-2 gap-3 mt-4">
                                <label class="flex items-center gap-2 text-sm"><input type="checkbox" name="isFpePanel" value="true" class="rounded"> FPE / Zinsco panel</label>
                                <label class="flex items-center gap-2 text-sm"><input type="checkbox" name="isPolyB" value="true" class="rounded"> Polybutylene pipes</label>
                                <label class="flex items-center gap-2 text-sm"><input type="checkbox" name="isAluminum" value="true" class="rounded"> Aluminum wiring</label>
                                <label class="flex items-center gap-2 text-sm"><input type="checkbox" name="history" value="ROOFING" class="rounded"> Roof recently replaced</label>
                            </div>
                        </details>
                    </form>

                    <ul class="tool-panel-list">
                        <li>Three-minute intake to first packet.</li>
                        <li>Evidence upload supports PDF, TXT, and phone-photo OCR.</li>
                        <li>Local context and case-file fields are optional, not blockers.</li>
                    </ul>
                </div>

                <div class="hero-panel">
                    <div class="hero-eyebrow">
                        @if("agent_team".equals(selectedEntry))
                            Started from buyer-agent team acquisition. Free during validation.
                        @elseif("financing".equals(selectedEntry))
                            Started from FHA / VA / lender-sensitive acquisition. Free during validation.
                        @elseif(selectedSurface != null)
                            Started from ${selectedSurface.navLabel()} search intent. Free during validation.
                        @else
                            Pre-send inspection request check. Free during validation.
                        @endif
                    </div>
                    <h1 class="hero-title">
                        @if("agent_team".equals(selectedEntry))
                            Check the <span class="hero-accent">buyer-agent ask</span> before it leaves the team.
                        @elseif("financing".equals(selectedEntry))
                            Check the <span class="hero-accent">financing-sensitive ask</span> before it creates closing risk.
                        @elseif(selectedSurface != null)
                            Keep the <span class="hero-accent">${selectedSurface.navLabel()}</span> posture, then check if the ask is defensible.
                        @else
                            Before you send the inspection repair request, <span class="hero-accent">check what to ask, what to cut, and what fallback to use</span>.
                        @endif
                    </h1>
                    <p class="hero-copy">
                        @if("agent_team".equals(selectedEntry))
                            This user came in through a buyer-agent commercial page. Keep the team-oriented framing: one live file, one proposed ask, one sendability verdict, and one revised packet.
                        @elseif("financing".equals(selectedEntry))
                            This user came in through a loan-sensitive page. Keep the lender-visible framing: narrow the ask, keep serious items in scope, and avoid credit language that pretends to be underwriting advice.
                        @elseif(selectedSurface != null)
                            ${selectedSurface.heroLead()} The pre-send check keeps that starting question, but still builds the revised ask, cut list, fallback, evidence list, and buyer-agent note in one artifact.
                        @else
                            Paste the proposed request and report findings. LifeVerdict checks number basis, evidence, seller pushback, lender boundary, form path, and send posture before giving you copy-ready wording.
                        @endif
                    </p>

                    <div class="hero-actions">
                        <a href="#inspection-intake" class="button-primary">
                            @if("agent_team".equals(selectedEntry))
                                Open Buyer-Agent Intake
                            @elseif("financing".equals(selectedEntry))
                                Open FHA / VA Intake
                            @elseif(selectedSurface != null)
                                Open ${selectedSurface.navLabel()} Intake
                            @else
                                Check My Ask Free
                            @endif
                        </a>
                        <a href="/home-repair/methodology" class="button-secondary">See Method</a>
                    </div>

                    @if("agent_team".equals(selectedEntry))
                        <p class="micro-note">
                            Searchers came in through <strong>Buyer-Agent Teams</strong>. The tool should preserve that commercial context after the click.
                        </p>
                    @elseif("financing".equals(selectedEntry))
                        <p class="micro-note">
                            Searchers came in through <strong>FHA / VA Repairs</strong>. The tool should preserve the lender-sensitive context after the click.
                        </p>
                    @elseif(selectedSurface != null)
                        <p class="micro-note">
                            Searchers came in through <strong>${selectedSurface.navLabel()}</strong>. The tool should keep that promise after the click, not reset them into a generic flow.
                        </p>
                    @endif

                    <div class="packet-peek">
                        <div class="packet-peek-top">
                            <div>
                                <div class="packet-peek-kicker">Pre-send preview</div>
                                <strong>What the review decides before the message goes out</strong>
                            </div>
                            <div class="packet-peek-status">Revise before send</div>
                        </div>

                        <div class="packet-peek-amounts">
                            <div class="packet-peek-amount">
                                <span>Reviewed ask</span>
                                <strong>$35,500</strong>
                            </div>
                            <div class="packet-peek-amount">
                                <span>Defensible floor</span>
                                <strong>$30,500</strong>
                            </div>
                        </div>

                        <div class="packet-peek-note">
                            Verdict: keep roof leak and unsafe panel in the ask, move HVAC to verify, remove cosmetic trim, and prepare the fallback if the seller counters.
                        </div>

                        <ul class="packet-peek-list">
                            <li>Number basis is marked quote-backed, evidence-backed, or estimate-only.</li>
                            <li>Weak items are cut before they dilute seller leverage.</li>
                            <li>FHA / VA and form-path warnings stay visible before copy or print.</li>
                        </ul>

                        <div class="packet-peek-proof">
                            <span class="packet-proof-chip">Send posture</span>
                            <span class="packet-proof-chip">Number basis</span>
                            <span class="packet-proof-chip">Cut list</span>
                            <span class="packet-proof-chip">Seller pushback</span>
                        </div>
                    </div>
                </div>
            </section>

            <section class="signal-grid">
                <div class="signal-card">
                    <div class="signal-step">01</div>
                    <h3>Paste the message</h3>
                    <p>Use the buyer's proposed request, seller-credit number, report summary, or agent note.</p>
                </div>
                <div class="signal-card">
                    <div class="signal-step">02</div>
                    <h3>Run the cut</h3>
                    <p>Safety, financing, active leak, structural, evidence, and form boundaries decide what survives.</p>
                </div>
                <div class="signal-card">
                    <div class="signal-step">03</div>
                    <h3>Copy the reviewed ask</h3>
                    <p>Leave with the revised ask, fallback posture, and agent-ready language after the weak items are cut.</p>
                </div>
            </section>

            <section class="intent-shell">
                <div class="section-kicker">Other starting points</div>
                <h2 class="intent-title">Buyers do not all search with the same words. The pre-send job underneath is still the same.</h2>
                <div class="support-grid support-rail mt-5">
                    @for(AcquisitionSurface route : acquisitionSurfaces)
                        <article class="support-card intent-card">
                            <h3>${route.navLabel()}</h3>
                            <p>${route.pageDescription()}</p>
                            <a href="${route.path()}"
                               data-analytics-event="open_acquisition_surface"
                               data-analytics-category="navigation"
                               data-analytics-label="${route.code()}"
                               class="intent-link">Open this surface -></a>
                        </article>
                    @endfor
                </div>
            </section>

            <section class="intent-shell">
                <div class="section-kicker">Proof before activation</div>
                <h2 class="intent-title">Some visitors need category proof, not the tool first.</h2>
                <div class="support-grid support-rail mt-5">
                    <article class="support-card intent-card">
                        <h3>For buyer-agent teams</h3>
                        <p>Commercial page for the real ICP: small buyer-agent teams handling financed inspection-response deals.</p>
                        <a href="/for-buyer-agents"
                           data-analytics-event="open_acquisition_surface"
                           data-analytics-category="navigation"
                           data-analytics-label="agent_team"
                           class="intent-link">Open buyer-agent page -></a>
                    </article>
                    <article class="support-card intent-card">
                        <h3>Sample pre-send review</h3>
                        <p>Public proof of what the sendability verdict looks like before someone pastes a live report into the tool.</p>
                        <a href="/sample-seller-credit-request-after-home-inspection"
                           data-analytics-event="open_acquisition_surface"
                           data-analytics-category="navigation"
                           data-analytics-label="sample_packet"
                           class="intent-link">Open sample packet -></a>
                    </article>
                    <article class="support-card intent-card">
                        <h3>FHA / VA repairs</h3>
                        <p>Financing-sensitive page for buyers and agents who need to narrow the first ask without creating loan friction.</p>
                        <a href="/fha-va-inspection-repairs-and-seller-credit"
                           data-analytics-event="open_acquisition_surface"
                           data-analytics-category="navigation"
                           data-analytics-label="financing"
                           class="intent-link">Open financing page -></a>
                    </article>
                </div>
            </section>

            <section class="intent-shell">
                <div class="section-kicker">Why it works</div>
                <h2 class="intent-title">Built for the message before it is sent, not broad repair browsing.</h2>
                <div class="support-grid support-rail mt-5">
                    <article class="support-card intent-card">
                        <h3>Primary user</h3>
                        <p>Buyer agents are the main customer because they repeat this job across multiple live deals and need a packet they can send quickly.</p>
                    </article>
                    <article class="support-card intent-card">
                        <h3>Secondary user</h3>
                        <p>Buyers under contract can still use it directly, but the output is intentionally shaped for the agent conversation rather than solo DIY planning.</p>
                    </article>
                    <article class="support-card intent-card">
                        <h3>Evidence-backed output</h3>
                        <p>The packet keeps the strongest items tied to report language, page references, and OCR-backed scans when a file is uploaded.</p>
                    </article>
                    <article class="support-card intent-card">
                        <h3>Who it is not for</h3>
                        <p>Not an inspector report suite, not a lender workflow, and not a broad homeowner renovation site.</p>
                    </article>
                </div>
            </section>

            @if(!recentWorkspaces.isEmpty())
                <section class="spotlight-grid">
                    <div class="spotlight-panel cool" style="grid-column: 1 / -1;">
                        <div class="section-kicker">Recent workspaces</div>
                        <h2>This is a negotiation workspace, not a one-shot output.</h2>
                        <div class="signal-grid mt-6">
                            @for(InspectionWorkspaceSummary workspace : recentWorkspaces)
                                <a href="${workspace.detailUrl()}" class="signal-card" style="text-decoration: none; color: inherit;">
                                    <div class="signal-step">${workspace.createdAtLabel()}</div>
                                    <h3>${workspace.caseLabel()}</h3>
                                    <p>${workspace.propertyAddress()}</p>
                                    <p><strong>${workspace.clientLabel()}</strong> / ${workspace.agentLabel()}</p>
                                    <p>${workspace.marketLabel()} / ${workspace.loanTypeLabel()}</p>
                                </a>
                            @endfor
                        </div>
                        <div class="mt-6">
                            <a href="/home-repair/workspace" class="button-primary">Open Full Workspace -></a>
                        </div>
                    </div>
                </section>
            @endif
        </div>
    `
)
