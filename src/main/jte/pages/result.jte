@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionDefenseSignal
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionEvidenceRef
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowEvent
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowSummary
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionExclusionItem
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionReadinessGate
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponsePacket
@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionWorkspaceSummary
@import com.livingcostcheck.home_repair.domain.VerdictHistory
@param Verdict verdict
@param VerdictHistory history
@param InspectionResponsePacket packet
@param InspectionCaseWorkflowSummary caseWorkflow = new InspectionCaseWorkflowSummary("DRAFT", "Draft", "No workflow data is attached to this render path yet.", "Open the saved result route to record review and send activity.", java.util.List.of())
@param InspectionWorkspaceSummary workspace
@param String title = "Inspection Ask Pre-Send Check | LifeVerdict"
@param String verdictH1 = null
@param String sellerCreditSummaryJs = ""
@param String agentNegotiationScriptJs = ""
@param String fullPacketTextJs = ""
@param String acquisitionEntry = "direct"

@template.layout(
    title = title,
    description = "Pre-send verdict for an inspection ask, including send posture, number basis, evidence support, seller pushback risk, and agent-ready language.",
    headContent = @`
        <meta name="robots" content="noindex">
        <style>
            .packet-stage {
                --ink: #10202a;
                --muted: #5f6d74;
                --line: rgba(16, 32, 42, 0.10);
                --cyan: #174a5a;
                --amber: #c96a3d;
                --paper: #fffaf4;
                max-width: 1180px;
                margin: 0 auto;
                padding: 42px 24px 80px;
            }
            .packet-stage::before {
                content: "";
                position: fixed;
                inset: 0;
                z-index: -1;
                background:
                    radial-gradient(circle at 12% 16%, rgba(201, 106, 61, 0.12), transparent 28%),
                    radial-gradient(circle at 88% 12%, rgba(23, 74, 90, 0.12), transparent 30%),
                    repeating-linear-gradient(180deg, rgba(16, 32, 42, 0.02) 0, rgba(16, 32, 42, 0.02) 1px, transparent 1px, transparent 38px),
                    linear-gradient(180deg, #f8f1e9 0%, #f4ede3 100%);
            }
            .packet-hero {
                display: grid;
                grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.75fr);
                gap: 24px;
                align-items: stretch;
                margin-bottom: 24px;
            }
            .hero-panel,
            .ask-panel,
            .packet-card,
            .copy-panel {
                border: 1px solid var(--line);
                background: rgba(255, 250, 244, 0.88);
                box-shadow: 0 22px 70px rgba(16, 32, 42, 0.08);
                backdrop-filter: blur(14px);
            }
            .hero-panel {
                border-radius: 32px;
                padding: 38px;
                position: relative;
                overflow: hidden;
            }
            .hero-panel::after {
                content: "";
                position: absolute;
                right: -90px;
                top: -90px;
                width: 220px;
                height: 220px;
                border-radius: 999px;
                background: rgba(23, 74, 90, 0.08);
                filter: blur(4px);
            }
            .packet-kicker {
                display: inline-flex;
                align-items: center;
                gap: 10px;
                padding: 9px 12px;
                border-radius: 999px;
                background: rgba(23, 74, 90, 0.08);
                color: var(--cyan);
                font-family: var(--font-mono);
                font-size: 11px;
                font-weight: 600;
                letter-spacing: 0.14em;
                text-transform: uppercase;
            }
            .packet-kicker::before {
                content: "";
                width: 8px;
                height: 8px;
                border-radius: 999px;
                background: var(--amber);
                box-shadow: 0 0 0 5px rgba(199, 121, 44, 0.15);
            }
            .hero-title {
                margin-top: 22px;
                max-width: 760px;
                color: var(--ink);
                font-family: var(--font-display);
                font-size: clamp(44px, 7vw, 82px);
                line-height: 0.94;
                letter-spacing: -0.04em;
                font-weight: 600;
            }
            .hero-copy {
                max-width: 720px;
                margin-top: 20px;
                color: var(--muted);
                font-size: 18px;
                line-height: 1.7;
            }
            .context-strip {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-top: 26px;
            }
            .case-dossier {
                margin-top: 22px;
                border: 1px solid rgba(15, 23, 42, 0.10);
                border-radius: 24px;
                background: rgba(255, 255, 255, 0.76);
                padding: 18px;
            }
            .case-dossier-grid {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 14px;
            }
            .case-dossier-item span {
                display: block;
                color: var(--muted);
                font-family: var(--font-mono);
                font-size: 11px;
                font-weight: 600;
                letter-spacing: 0.14em;
                text-transform: uppercase;
            }
            .case-dossier-item strong {
                display: block;
                margin-top: 6px;
                color: var(--ink);
                font-size: 16px;
                line-height: 1.45;
            }
            .context-pill {
                border: 1px solid rgba(15, 23, 42, 0.10);
                border-radius: 999px;
                background: rgba(255, 255, 255, 0.62);
                padding: 9px 12px;
                color: #334155;
                font-family: var(--font-mono);
                font-size: 12px;
                font-weight: 600;
            }
            .trust-deck {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 12px;
                margin-top: 22px;
            }
            .trust-card {
                border: 1px solid rgba(15, 23, 42, 0.10);
                border-radius: 22px;
                background: rgba(255, 255, 255, 0.70);
                padding: 16px;
            }
            .trust-card strong {
                display: block;
                color: var(--ink);
                font-size: 15px;
                line-height: 1.35;
            }
            .trust-list {
                display: grid;
                gap: 8px;
                margin-top: 12px;
            }
            .trust-list li {
                list-style: none;
                color: #475569;
                font-size: 13px;
                line-height: 1.5;
            }
            .trust-list li::before {
                content: "Check";
                display: inline-flex;
                margin-right: 7px;
                color: var(--amber);
                font-family: var(--font-mono);
                font-size: 10px;
                font-weight: 700;
                letter-spacing: 0.08em;
                text-transform: uppercase;
            }
            .ask-panel {
                border-radius: 32px;
                padding: 28px;
                background:
                    linear-gradient(160deg, rgba(16, 32, 42, 0.97), rgba(23, 74, 90, 0.95)),
                    #10202a;
                color: white;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                min-height: 100%;
            }
            .ask-label {
                color: #a5f3fc;
                font-family: var(--font-mono);
                font-size: 11px;
                font-weight: 600;
                letter-spacing: 0.16em;
                text-transform: uppercase;
            }
            .ask-amount {
                margin-top: 10px;
                font-family: var(--font-display);
                font-size: clamp(52px, 7vw, 86px);
                line-height: 0.92;
                letter-spacing: -0.04em;
                font-weight: 600;
            }
            .ask-summary {
                margin-top: 18px;
                color: rgba(255, 255, 255, 0.78);
                line-height: 1.6;
            }
            .quality-box {
                margin-top: 22px;
                border: 1px solid rgba(255, 255, 255, 0.14);
                border-radius: 22px;
                padding: 16px;
                background: rgba(255, 255, 255, 0.08);
            }
            .quality-top {
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 12px;
                color: white;
                font-weight: 700;
            }
            .quality-top span {
                color: #a5f3fc;
                font-family: var(--font-mono);
                font-size: 11px;
                letter-spacing: 0.14em;
                text-transform: uppercase;
            }
            .quality-meter {
                height: 8px;
                margin-top: 12px;
                border-radius: 999px;
                overflow: hidden;
                background: rgba(255, 255, 255, 0.16);
            }
            .quality-meter span {
                display: block;
                height: 100%;
                border-radius: inherit;
                background: linear-gradient(90deg, #fbbf24, #67e8f9);
            }
            .quality-note {
                margin-top: 10px;
                color: rgba(255, 255, 255, 0.72);
                font-size: 13px;
                line-height: 1.55;
            }
            .ask-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 10px;
                margin-top: 24px;
            }
            .ask-mini {
                border: 1px solid rgba(255, 255, 255, 0.14);
                border-radius: 20px;
                padding: 14px;
                background: rgba(255, 255, 255, 0.08);
            }
            .ask-mini span {
                display: block;
                color: rgba(255, 255, 255, 0.56);
                font-family: var(--font-mono);
                font-size: 10px;
                font-weight: 600;
                letter-spacing: 0.12em;
                text-transform: uppercase;
            }
            .ask-mini strong {
                display: block;
                margin-top: 6px;
                font-size: 21px;
            }
            .action-row {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-top: 28px;
            }
            .packet-button {
                border: 0;
                border-radius: 16px;
                padding: 13px 16px;
                font-weight: 900;
                transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
            }
            .packet-button:hover {
                transform: translateY(-1px);
            }
            .packet-button.primary {
                background: var(--ink);
                color: white;
                box-shadow: 0 14px 36px rgba(15, 23, 42, 0.20);
            }
            .packet-button.secondary {
                border: 1px solid var(--line);
                background: white;
                color: var(--ink);
            }
            .packet-grid {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 18px;
                margin: 24px 0;
            }
            .packet-card {
                border-radius: 26px;
                padding: 24px;
            }
            .card-kicker {
                color: var(--amber);
                font-family: var(--font-mono);
                font-size: 11px;
                font-weight: 600;
                letter-spacing: 0.14em;
                text-transform: uppercase;
            }
            .packet-card h2,
            .copy-panel h2 {
                margin-top: 9px;
                color: var(--ink);
                font-family: var(--font-display);
                font-size: 26px;
                line-height: 1.08;
                letter-spacing: -0.03em;
                font-weight: 600;
            }
            .packet-list {
                margin-top: 18px;
                display: grid;
                gap: 10px;
            }
            .packet-list li {
                list-style: none;
                border: 1px solid rgba(15, 23, 42, 0.08);
                border-radius: 16px;
                padding: 12px 13px;
                background: rgba(255, 255, 255, 0.72);
                color: #334155;
                line-height: 1.45;
            }
            .must-card {
                background: linear-gradient(180deg, rgba(255, 247, 237, 0.92), rgba(255, 255, 255, 0.86));
            }
            .verify-card {
                background: linear-gradient(180deg, rgba(236, 254, 255, 0.86), rgba(255, 255, 255, 0.86));
            }
            .avoid-card {
                background: linear-gradient(180deg, rgba(248, 250, 252, 0.96), rgba(255, 255, 255, 0.86));
            }
            .copy-layout {
                display: grid;
                grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
                gap: 18px;
            }
            .copy-panel {
                border-radius: 28px;
                padding: 26px;
            }
            .script-box {
                margin-top: 18px;
                border: 1px solid rgba(15, 23, 42, 0.10);
                border-radius: 22px;
                background: #122632;
                color: #e2e8f0;
                padding: 20px;
                line-height: 1.65;
                white-space: pre-wrap;
            }
            .support-box {
                margin-top: 18px;
                border-radius: 22px;
                background: var(--paper);
                border: 1px solid rgba(199, 121, 44, 0.18);
                padding: 18px;
                color: #7c2d12;
                line-height: 1.6;
            }
            .validation-panel {
                margin-top: 18px;
                border: 1px dashed rgba(21, 94, 117, 0.30);
                border-radius: 24px;
                background: rgba(236, 254, 255, 0.74);
                padding: 18px;
            }
            .validation-panel strong {
                display: block;
                color: #164e63;
                font-weight: 900;
            }
            .validation-panel p {
                color: #0e7490;
                font-size: 13px;
                margin-top: 4px;
            }
            .feedback-row {
                display: flex;
                gap: 8px;
                margin-top: 14px;
            }
            .feedback-row input {
                min-width: 0;
                flex: 1;
                border: 1px solid rgba(15, 23, 42, 0.12);
                border-radius: 14px;
                padding: 11px 12px;
                outline: none;
            }
            #packet-action-status {
                margin-top: 10px;
                min-height: 20px;
                color: #155e75;
                font-size: 13px;
                font-weight: 800;
            }
            @media (max-width: 900px) {
                .packet-hero,
                .copy-layout,
                .packet-grid {
                    grid-template-columns: 1fr;
                }
                .case-dossier-grid {
                    grid-template-columns: 1fr;
                }
                .trust-deck {
                    grid-template-columns: 1fr;
                }
                .hero-panel {
                    padding: 28px;
                }
            }
            @media print {
                header,
                footer,
                .print-hidden,
                .action-row,
                .validation-panel {
                    display: none !important;
                }
                .packet-stage {
                    padding: 0;
                }
                .packet-stage::before {
                    display: none;
                }
                .hero-panel,
                .ask-panel,
                .packet-card,
                .copy-panel {
                    box-shadow: none;
                    background: white !important;
                    color: black !important;
                    break-inside: avoid;
                }
                .packet-hero,
                .copy-layout,
                .packet-grid {
                    grid-template-columns: 1fr;
                }
                .script-box {
                    color: black;
                    background: white;
                }
            }
        </style>
    `,
    content = @`
        <div class="packet-stage">
            <section class="packet-hero">
                <div class="hero-panel">
                    <div class="packet-kicker">Inspection ask pre-send check</div>
                    <h1 class="hero-title">${packet.title()}</h1>
                    <p class="hero-copy">${packet.subtitle()}</p>

                    @if(verdict.isDealKiller())
                        <div class="mt-5 inline-flex rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-bold text-red-700">
                            ${verdict.getDealKillerMessage()}
                        </div>
                    @endif

                    <div class="context-strip">
                        <span class="context-pill">${packet.loanTypeLabel()}</span>
                        <span class="context-pill">${packet.closingWindowLabel()}</span>
                        <span class="context-pill">${packet.quoteSupportLabel()}</span>
                        <span class="context-pill">Ref ${history.getId().toString().substring(0, 8)}</span>
                    </div>

                    <div class="trust-deck">
                        <div class="trust-card">
                            <div class="card-kicker">Why verdict</div>
                            <strong>Why this verdict was chosen</strong>
                            <ul class="trust-list">
                                @for(String item : packet.verdictRationale())
                                    <li>${item}</li>
                                @endfor
                            </ul>
                        </div>
                        <div class="trust-card">
                            <div class="card-kicker">Weak evidence</div>
                            <strong>What is missing before send</strong>
                            <ul class="trust-list">
                                @for(String item : packet.missingEvidence())
                                    <li>${item}</li>
                                @endfor
                            </ul>
                        </div>
                        <div class="trust-card">
                            <div class="card-kicker">Caveats</div>
                            <strong>Boundaries the agent should not overstate</strong>
                            <ul class="trust-list">
                                @for(String item : packet.reviewCaveats())
                                    <li>${item}</li>
                                @endfor
                            </ul>
                        </div>
                    </div>

                    <div class="case-dossier">
                        <div class="card-kicker">Case workspace</div>
                        <div class="case-dossier-grid mt-4">
                            <div class="case-dossier-item">
                                <span>Case</span>
                                <strong>${workspace.caseLabel()}</strong>
                            </div>
                            <div class="case-dossier-item">
                                <span>Property</span>
                                <strong>${workspace.propertyAddress()}</strong>
                            </div>
                            <div class="case-dossier-item">
                                <span>Market</span>
                                <strong>${workspace.marketLabel()}</strong>
                            </div>
                            <div class="case-dossier-item">
                                <span>Buyer</span>
                                <strong>${workspace.clientLabel()}</strong>
                            </div>
                            <div class="case-dossier-item">
                                <span>Agent</span>
                                <strong>${workspace.agentLabel()}</strong>
                            </div>
                            <div class="case-dossier-item">
                                <span>Opened</span>
                                <strong>${workspace.createdAtLabel()}</strong>
                            </div>
                        </div>
                    </div>

                    <div class="action-row print-hidden">
                        <button onclick="window.copyNegotiationPacket();" class="packet-button primary">Copy Revised Packet</button>
                        <button onclick="window.copyAgentRequest();" class="packet-button secondary">Copy Reviewed Ask</button>
                        <button onclick="window.printNegotiationPacket();" class="packet-button secondary">Print Pre-Send Check</button>
                        <a href="/home-repair/workspace" class="packet-button secondary" style="text-decoration: none;">Open Workspace</a>
                    </div>
                </div>

                <aside class="ask-panel">
                    <div>
                        <div class="ask-label">Reviewed seller-credit ask</div>
                        <div class="ask-amount">$${packet.targetAskLabel()}</div>
                        <p class="ask-summary">${packet.sellerCreditSummary()}</p>
                        <div class="quality-box">
                            <div class="quality-top">
                                <span>${packet.readinessLabel()}</span>
                                <strong>${packet.readinessPassCount()}/${packet.readinessGates().size()} gates</strong>
                            </div>
                            <div class="quality-meter"><span style="width: ${packet.confidenceScore()}%"></span></div>
                            <p class="quality-note">${packet.readinessNote()}</p>
                            <p class="quality-subnote">Confidence ${packet.confidenceScore()}% is secondary. Hard gates decide whether this is send-ready.</p>
                        </div>
                    </div>
                    <div>
                        <div class="ask-grid">
                            <div class="ask-mini">
                                <span>Defensible floor</span>
                                <strong>$${packet.defendableAskLabel()}</strong>
                            </div>
                            <div class="ask-mini">
                                <span>Stretch ask</span>
                                <strong>$${packet.stretchAskLabel()}</strong>
                            </div>
                        </div>
                        <p class="mt-5 text-sm leading-6 text-cyan-50/75">${packet.loanTypeNote()}</p>
                        <p class="mt-3 text-sm leading-6 text-cyan-50/75">${packet.responseDeadlineNote()}</p>
                        <p class="mt-3 text-sm leading-6 text-cyan-50/75">${packet.evidenceNote()}</p>
                    </div>
                </aside>
            </section>

            <section class="packet-grid">
                <article class="packet-card must-card">
                    <div class="card-kicker">1. Survives first review</div>
                    <h2>Keep in the ask</h2>
                    <ul class="packet-list">
                        @for(String item : packet.mustFixNow())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </article>

                <article class="packet-card verify-card">
                    <div class="card-kicker">2. Not ready to lead</div>
                    <h2>Verify before asking</h2>
                    <ul class="packet-list">
                        @for(String item : packet.verifyNext())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </article>

                <article class="packet-card avoid-card">
                    <div class="card-kicker">3. Weakens sendability</div>
                    <h2>Cut before sending</h2>
                    <ul class="packet-list">
                        @for(String item : packet.doNotLead())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </article>
            </section>

            <section class="copy-layout">
                <article class="copy-panel">
                    <div class="card-kicker">Agent pre-send review</div>
                    <h2>Can this ask survive pushback?</h2>
                    <div class="gate-list">
                        @for(InspectionDefenseSignal signal : packet.defenseSignals())
                            <div class="gate-item gate-${signal.status().toLowerCase()}">
                                <div class="gate-head">
                                    <span class="gate-badge gate-${signal.status().toLowerCase()}">${signal.status()}</span>
                                    <strong>${signal.label()}</strong>
                                </div>
                                <p class="gate-note">${signal.detail()}</p>
                                <p class="gate-note mt-2"><strong>Next:</strong> ${signal.action()}</p>
                            </div>
                        @endfor
                    </div>
                </article>

                <aside class="copy-panel">
                    <div class="card-kicker">Differentiator</div>
                    <h2>This is the part a request-list builder does not decide.</h2>
                    <div class="support-box">
                        The review separates a defendable ask from a long defect list. It marks the number basis, evidence support, seller pushback risk, financing boundary, form boundary, and send posture before anything leaves review mode.
                    </div>
                </aside>
            </section>

            <section class="copy-layout">
                <article class="copy-panel">
                    <div class="card-kicker">Agent-ready language</div>
                    <h2>Use this after the pre-send gates are cleared</h2>
                    <div class="script-box">${packet.agentNegotiationScript()}</div>
                    <div class="action-row print-hidden">
                        <button onclick="window.copyAgentRequest();" class="packet-button primary">Copy Reviewed Ask</button>
                        <button onclick="window.copyAskSummary();" class="packet-button secondary">Copy Ask Summary</button>
                    </div>
                </article>

                <aside class="copy-panel">
                    <div class="card-kicker">Fallback posture</div>
                    <h2>If the seller pushes back</h2>
                    <div class="support-box">${packet.fallbackScript()}</div>

                    <div class="mt-6">
                        <div class="card-kicker">Evidence checklist</div>
                        <ul class="packet-list">
                            @for(String item : packet.evidenceChecklist())
                                <li>${item}</li>
                            @endfor
                        </ul>
                    </div>
                    <div class="mt-6">
                        <div class="card-kicker">Missing or weak evidence</div>
                        <h2>Do not call this file-ready until these are fixed</h2>
                        <ul class="packet-list">
                            @for(String item : packet.missingEvidence())
                                <li>${item}</li>
                            @endfor
                        </ul>
                    </div>
                    @if(!packet.matchedEvidence().isEmpty())
                        <div class="mt-6">
                            <div class="card-kicker">Matched report evidence</div>
                            <h2>Pages backing the ask</h2>
                            @if(packet.evidenceSourceLabel() != null && !packet.evidenceSourceLabel().isBlank())
                                <p class="mt-3 text-slate-600 leading-7">Source: ${packet.evidenceSourceLabel()}</p>
                            @endif
                            <p class="mt-3 text-sm text-slate-500 leading-6">Any citation marked <strong>(OCR)</strong> came from a scan or photo and should be checked against the original page before sending.</p>
                            <ul class="packet-list">
                                @for(InspectionEvidenceRef evidence : packet.matchedEvidence())
                                    <li>
                                        <strong>${evidence.findingLabel()}</strong>
                                        <div class="mt-2 text-sm text-slate-600 leading-6">
                                            @for(String citation : evidence.citations())
                                                <div>${citation}</div>
                                            @endfor
                                        </div>
                                    </li>
                                @endfor
                            </ul>
                        </div>
                    @endif
                </aside>
            </section>

            <section class="copy-layout">
                <article class="copy-panel">
                    <div class="card-kicker">Hard readiness gates</div>
                    <h2>What still blocks a safe send</h2>
                    <ul class="packet-list">
                        @for(String item : packet.verdictRationale())
                            <li>${item}</li>
                        @endfor
                    </ul>
                    <div class="gate-list">
                        @for(InspectionReadinessGate gate : packet.readinessGates())
                            <div class="gate-item gate-${gate.status().toLowerCase()}">
                                <div class="gate-head">
                                    <span class="gate-badge gate-${gate.status().toLowerCase()}">${gate.status()}</span>
                                    <strong>${gate.label()}</strong>
                                </div>
                                <p class="gate-note">${gate.note()}</p>
                            </div>
                        @endfor
                    </div>
                    <div class="card-kicker mt-6">Score inputs</div>
                    <ul class="packet-list">
                        @for(String item : packet.confidenceReasons())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </article>

                <aside class="copy-panel">
                    <div class="card-kicker">Next actions</div>
                    <h2>What to do before sending</h2>
                    <ul class="packet-list">
                        @for(String item : packet.nextActions())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </aside>
            </section>

            <section class="packet-grid">
                <article class="packet-card">
                    <div class="card-kicker">Source findings</div>
                    <h2>Inspection intake readback</h2>
                    <ul class="packet-list">
                        @for(String item : packet.originalFindings())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </article>

                <article class="packet-card">
                    <div class="card-kicker">Negotiation basis</div>
                    <h2>${packet.dataAnchor()}</h2>
                    <p class="mt-4 text-slate-600 leading-7">${packet.dataAnchorNote()}</p>
                    @if(!packet.pricingBreakdown().isEmpty())
                        <ul class="packet-list mt-5">
                            @for(String item : packet.pricingBreakdown())
                                <li>${item}</li>
                            @endfor
                        </ul>
                    @endif
                    <div class="card-kicker mt-6">Financing pressure</div>
                    <p class="mt-3 text-slate-600 leading-7">${packet.lenderVisibleNote()}</p>
                    @if(!packet.lenderVisibleSignals().isEmpty())
                        <ul class="packet-list mt-5">
                            @for(String item : packet.lenderVisibleSignals())
                                <li>${item}</li>
                            @endfor
                        </ul>
                    @endif
                </article>

                <article class="packet-card">
                    <div class="card-kicker">Cut from the ask</div>
                    <h2>Excluded on purpose</h2>
                    <ul class="packet-list">
                        @for(InspectionExclusionItem item : packet.excludedFindings())
                            <li>
                                <strong>${item.findingLabel()}</strong>
                                <div class="mt-2 text-sm text-slate-600 leading-6">${item.reason()}</div>
                            </li>
                        @endfor
                    </ul>
                </article>
            </section>

            <section class="copy-layout">
                <article class="copy-panel">
                    <div class="card-kicker">${packet.workflowLabel()}</div>
                    <h2>${packet.workflowTitle()}</h2>
                    <div class="support-box">${packet.workflowNote()}</div>
                </article>

                <aside class="copy-panel">
                    <div class="card-kicker">File handoff</div>
                    <h2>Move it into the real transaction file</h2>
                    <ul class="packet-list">
                        @for(String item : packet.workflowSteps())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </aside>
            </section>

            <section class="copy-layout">
                <article class="copy-panel">
                    <div class="card-kicker">Case workflow</div>
                    <h2>${caseWorkflow.currentLabel()}</h2>
                    <div class="support-box">${caseWorkflow.currentNote()}</div>
                    <p class="mt-4 text-slate-600 leading-7"><strong>Next:</strong> ${caseWorkflow.recommendedNextAction()}</p>

                    <div class="workflow-actions print-hidden mt-6">
                        <div class="feedback-row">
                            <input type="text" id="workflow-actor" placeholder="Actor / owner name">
                            <input type="text" id="workflow-note" placeholder="Optional audit note">
                        </div>
                        <div class="action-row">
                            <button type="button" class="packet-button secondary" onclick="window.recordWorkflowState('REQUEST_REVIEW')">Request Review</button>
                            <button type="button" class="packet-button secondary" onclick="window.recordWorkflowState('BUYER_APPROVED')">Record Buyer Approval</button>
                            <button type="button" class="packet-button secondary" onclick="window.recordWorkflowState('MARK_SENT')">Mark Sent</button>
                            <button type="button" class="packet-button secondary" onclick="window.recordWorkflowState('COUNTER_RECEIVED')">Record Seller Counter</button>
                            <button type="button" class="packet-button secondary" onclick="window.recordWorkflowState('RESOLUTION_SIGNED')">Mark Resolution Signed</button>
                            <button type="button" class="packet-button secondary" onclick="window.recordWorkflowState('MARK_TERMINATED')">Mark Terminated</button>
                        </div>
                    </div>
                </article>

                <aside class="copy-panel">
                    <div class="card-kicker">Audit timeline</div>
                    <h2>What happened in this case</h2>
                    <div class="timeline-list">
                        @for(InspectionCaseWorkflowEvent event : caseWorkflow.timeline())
                            <div class="timeline-item">
                                <div class="timeline-top">
                                    <strong>${event.label()}</strong>
                                    @if(event.timestampLabel() != null && !event.timestampLabel().isBlank())
                                        <span>${event.timestampLabel()}</span>
                                    @endif
                                </div>
                                @if(event.note() != null && !event.note().isBlank())
                                    <p class="timeline-note">${event.note()}</p>
                                @endif
                            </div>
                        @endfor
                    </div>
                </aside>
            </section>

            <section class="copy-panel">
                <div class="card-kicker">Validation mode</div>
                <h2>Use the pre-send check freely, then tell us if it held up</h2>
                <div class="validation-panel" id="validation-actions">
                    <strong>Validation mode: no payment or email required.</strong>
                <p>Copy, print, and use the revised packet after review. The core product stays free while we confirm that people actually rely on the pre-send check in the real inspection response window.</p>
                    <form id="validation-feedback-form" onsubmit="event.preventDefault(); window.saveValidationSignal(this);">
                        <div class="feedback-row">
                            <input type="email" name="email" placeholder="Optional email for follow-up">
                            <button type="submit" class="packet-button primary">Mark Useful</button>
                        </div>
                    </form>
                    <div id="packet-action-status" aria-live="polite"></div>
                </div>
            </section>

            <section class="copy-panel">
                <div class="card-kicker">Buyer-agent desk</div>
                <h2>Fastest paid path: small teams that want help on live files</h2>
                <div class="validation-panel agent-desk-panel">
                    <strong>Why this is the easy-money layer</strong>
                    <p>
                        Spectora, HomeGauge, and Palmtech already sell request-list builders inside inspector software. The easier paid layer here is not replacing those stacks. It is helping buyer agents turn live inspection findings into a narrow packet they can send, defend, and reuse across deals.
                    </p>
                    <form id="agent-desk-form" onsubmit="event.preventDefault(); window.requestAgentDesk(this);">
                        <div class="agent-desk-grid mt-4">
                            <input type="email" name="email" placeholder="Work email" required>
                            <select name="role">
                                <option value="buyer_agent">Buyer agent</option>
                                <option value="team_lead">Team lead</option>
                                <option value="transaction_coordinator">Transaction coordinator</option>
                                <option value="inspector_partner">Inspector partner</option>
                            </select>
                            <select name="teamSize">
                                <option value="solo">Solo</option>
                                <option value="2_5">2-5 people</option>
                                <option value="6_10">6-10 people</option>
                                <option value="11_plus">11+ people</option>
                            </select>
                            <select name="monthlyVolume">
                                <option value="1_4">1-4 packets / month</option>
                                <option value="5_10">5-10 packets / month</option>
                                <option value="11_25">11-25 packets / month</option>
                                <option value="26_plus">26+ packets / month</option>
                            </select>
                        </div>
                        <textarea name="note" class="mt-3" rows="3" placeholder="What would make this worth paying for? e.g. rush review, team template, broker-ready export, office rollout"></textarea>
                        <div class="action-row mt-4">
                            <button type="submit" class="packet-button primary">Request Team Setup</button>
                        </div>
                    </form>
                    <p class="agent-desk-note">This does not gate the free packet. It just identifies the highest-intent buyer-agent teams.</p>
                    <div id="agent-desk-status" aria-live="polite"></div>
                </div>
            </section>
        </div>

        <script>
            (function () {
                const verdictId = '${history.getId().toString()}';
                const packetEntry = '${acquisitionEntry}';
                const fullPacketText = '${fullPacketTextJs}';
                const agentNegotiationScript = '${agentNegotiationScriptJs}';
                const sellerCreditSummary = '${sellerCreditSummaryJs}';

                function setStatus(message) {
                    const status = document.getElementById('packet-action-status');
                    if (status) {
                        status.textContent = message;
                    }
                }

                function setAgentDeskStatus(message) {
                    const status = document.getElementById('agent-desk-status');
                    if (status) {
                        status.textContent = message;
                    }
                }

                function logPacketEvent(eventType, target) {
                    const body = new URLSearchParams({
                        verdictId: verdictId,
                        eventType: eventType,
                        target: 'entry=' + (packetEntry || 'direct') + '|action=' + (target || 'buying_packet')
                    });
                    fetch('/home-repair/api/event', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body
                    }).catch(function () {});
                }

                async function copyText(text, eventType, target, message) {
                    try {
                        await navigator.clipboard.writeText(text);
                    } catch (error) {
                        const textarea = document.createElement('textarea');
                        textarea.value = text;
                        document.body.appendChild(textarea);
                        textarea.select();
                        document.execCommand('copy');
                        textarea.remove();
                    }
                    logPacketEvent(eventType, target);
                    setStatus(message);
                }

                window.copyNegotiationPacket = function () {
                    copyText(fullPacketText, 'COPY_PACKET', 'full_packet', 'Copied full packet.');
                };

                window.copyAgentRequest = function () {
                    copyText(agentNegotiationScript, 'COPY_AGENT_REQUEST', 'agent_request', 'Copied agent request.');
                };

                window.copyAskSummary = function () {
                    copyText(sellerCreditSummary, 'COPY_ASK_SUMMARY', 'ask_summary', 'Copied ask summary.');
                };

                window.printNegotiationPacket = function () {
                    logPacketEvent('PRINT_PACKET', 'print_packet');
                    setStatus('Print dialog opened.');
                    window.print();
                };

                window.saveValidationSignal = function (form) {
                    logPacketEvent('SUBMIT_FEEDBACK', 'marked_useful');
                    const email = form.querySelector("input[name='email']").value.trim();
                    if (email) {
                        const body = new URLSearchParams({ verdictId: verdictId, email: email, entry: packetEntry || 'direct' });
                        fetch('/home-repair/api/lead', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body
                        }).catch(function () {});
                    }
                    setStatus('Marked useful.');
                };

                window.requestAgentDesk = function (form) {
                    const formData = new FormData(form);
                    const email = (formData.get('email') || '').toString().trim();
                    if (!email) {
                        setAgentDeskStatus('Work email is required.');
                        return;
                    }
                    const body = new URLSearchParams({
                        verdictId: verdictId,
                        email: email,
                        entry: packetEntry || 'direct',
                        role: (formData.get('role') || '').toString(),
                        teamSize: (formData.get('teamSize') || '').toString(),
                        monthlyVolume: (formData.get('monthlyVolume') || '').toString(),
                        note: (formData.get('note') || '').toString().trim()
                    });

                    fetch('/home-repair/api/agent-desk', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body
                    }).then(async function (response) {
                        const message = await response.text();
                        if (!response.ok) {
                            setAgentDeskStatus(message || 'Agent desk request failed.');
                            return;
                        }
                        setAgentDeskStatus(message || 'Agent desk request saved.');
                        form.reset();
                    }).catch(function () {
                        setAgentDeskStatus('Agent desk request failed.');
                    });
                };

                window.recordWorkflowState = function (workflowAction) {
                    const actor = (document.getElementById('workflow-actor') || {}).value || '';
                    const note = (document.getElementById('workflow-note') || {}).value || '';
                    const body = new URLSearchParams({
                        verdictId: verdictId,
                        workflowAction: workflowAction,
                        actor: actor.trim(),
                        note: note.trim()
                    });

                    fetch('/home-repair/api/workflow-state', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body
                    }).then(async function (response) {
                        const message = await response.text();
                        if (!response.ok) {
                            setStatus(message || 'Workflow update failed.');
                            return;
                        }
                        setStatus(message || 'Workflow state recorded.');
                        window.location.reload();
                    }).catch(function () {
                        setStatus('Workflow update failed.');
                    });
                };
            })();
        </script>
    `
)
