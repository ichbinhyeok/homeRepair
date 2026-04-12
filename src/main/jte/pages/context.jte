@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.ContextBriefing
@param String metroCode
@param String era
@param String relationship
@param ContextBriefing contextBriefing

@template.layout(
    title = "Inspection Intake | LifeVerdict",
    description = "Paste inspection findings to build a seller-credit request.",
    content = @`
        <div class="app-shell narrow-shell py-8 md:py-10">
            <div class="briefing-card mb-8">
                <div class="section-kicker">Inspection intake</div>
                <h2 class="text-3xl font-bold text-slate-900 mt-3">Turn the report into a seller-credit request.</h2>
                <p class="panel-note mt-3">
                    Start with the inspection summary, visible disclosures, or showing notes. The goal is not perfect scope. The goal is one defensible ask before the response window closes.
                </p>

                <div class="briefing-grid">
                    <div class="briefing-stat">
                        <strong>Labor market</strong>
                        <span>${contextBriefing.getLaborMarketRate()} (${contextBriefing.getLaborMarketRateReason()})</span>
                    </div>
                    <div class="briefing-stat">
                        <strong>Regional risk</strong>
                        <span>${contextBriefing.getRegionalRisk()} (${contextBriefing.getRegionalRiskReason()})</span>
                    </div>
                    <div class="briefing-stat">
                        <strong>Era feature</strong>
                        <span>${contextBriefing.getEraFeature()} (${contextBriefing.getEraFeatureReason()})</span>
                    </div>
                </div>

                <p class="text-sm text-slate-500 mt-5">Market: <strong>${metroCode}</strong> | Era: <strong>${era}</strong></p>
            </div>

            <form action="/home-repair/verdict" method="post" class="workflow-stack">
                <input type="hidden" name="metroCode" value="${metroCode}">
                <input type="hidden" name="era" value="${era}">
                <input type="hidden" name="relationship" value="${relationship}">

                <div class="workflow-card">
                    <div class="section-kicker">Step 1</div>
                    <h3 class="text-xl font-bold text-slate-900 mb-2">1. Set the buyer posture</h3>
                    <p class="text-sm text-slate-600 mb-4">These inputs only shape the seller-credit packet. If you are unsure, keep the defaults and move on.</p>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Financing mode</label>
                            <select name="loanType" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                                <option value="CONVENTIONAL" selected>Conventional loan</option>
                                <option value="FHA">FHA loan</option>
                                <option value="VA">VA loan</option>
                                <option value="CASH">Cash offer</option>
                                <option value="INVESTOR">Investor / DSCR</option>
                            </select>
                            <p class="text-xs text-slate-500 mt-1">This changes how tightly the packet should stay focused on lender-visible issues.</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Repair budget ceiling ($)</label>
                            <input type="number" name="budget" min="1000" step="1" value="50000" placeholder="e.g. 50000" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                            <p class="text-xs text-slate-500 mt-1">Optional. Use your best guess for what you would still tolerate after closing.</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Approx. house size (SqFt)</label>
                            <input type="number" name="sqft" min="500" max="10000" step="1" value="1800" placeholder="e.g. 2000" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                            <p class="text-xs text-slate-500 mt-1">Optional. Interior living area. If unsure, leave the default 1800.</p>
                        </div>
                    </div>

                    <div class="mt-6">
                        <label class="block text-sm font-medium text-slate-700 mb-2">How rough does the house feel overall?</label>
                        <div class="grid grid-cols-3 gap-3">
                            <label class="cursor-pointer h-full">
                                <input type="radio" name="condition" value="NONE" class="peer sr-only">
                                <div class="p-3 h-full flex flex-col justify-center items-center text-center border rounded-xl peer-checked:bg-emerald-50 peer-checked:border-emerald-500 hover:bg-slate-50 transition-all">
                                    <div class="font-semibold">Turnkey</div>
                                    <div class="text-xs text-slate-500 mt-1">Move-in ready</div>
                                </div>
                            </label>
                            <label class="cursor-pointer h-full">
                                <input type="radio" name="condition" value="MINOR" checked class="peer sr-only">
                                <div class="p-3 h-full flex flex-col justify-center items-center text-center border rounded-xl peer-checked:bg-amber-50 peer-checked:border-amber-500 hover:bg-slate-50 transition-all">
                                    <div class="font-semibold">Average</div>
                                    <div class="text-xs text-slate-500 mt-1">Dated / lived in</div>
                                </div>
                            </label>
                            <label class="cursor-pointer h-full">
                                <input type="radio" name="condition" value="SEVERE" class="peer sr-only">
                                <div class="p-3 h-full flex flex-col justify-center items-center text-center border rounded-xl peer-checked:bg-rose-50 peer-checked:border-rose-500 hover:bg-slate-50 transition-all">
                                    <div class="font-semibold">Fixer upper</div>
                                    <div class="text-xs text-slate-500 mt-1">Needs work</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="workflow-card border-l-4 border-l-cyan-700">
                    <div class="section-kicker">Step 2</div>
                    <h3 class="text-xl font-bold text-slate-900 mb-2">2. Paste the top findings</h3>
                    <p class="text-sm text-slate-600 mb-4">Keep it short. These notes decide what belongs in the ask and what can wait.</p>

                    <div class="grid grid-cols-1 gap-4">
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Finding 1</label>
                            <input type="text" name="inspectionFinding" placeholder="e.g. Active roof leak above rear bedroom" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Finding 2</label>
                            <input type="text" name="inspectionFinding" placeholder="e.g. Federal Pacific panel flagged by inspector" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Finding 3</label>
                            <input type="text" name="inspectionFinding" placeholder="Optional third finding" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Outside quote support</label>
                            <select name="quoteSupport" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                                <option value="NONE" selected>No contractor quotes yet</option>
                                <option value="HAS_ONE">One quote in hand</option>
                                <option value="MULTIPLE">Multiple quotes in hand</option>
                            </select>
                            <p class="text-xs text-slate-500 mt-1">This changes how aggressively the packet should hold the target ask.</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Closing timeline</label>
                            <select name="closingWindow" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                                <option value="UNDER_7_DAYS">Under 7 days</option>
                                <option value="SEVEN_TO_TWENTY_ONE_DAYS" selected>7-21 days</option>
                                <option value="TWENTY_ONE_TO_FORTY_FIVE_DAYS">21-45 days</option>
                                <option value="FLEXIBLE">Flexible / not under contract</option>
                            </select>
                            <p class="text-xs text-slate-500 mt-1">Shorter timelines usually favor cleaner credits over longer repair negotiations.</p>
                        </div>
                    </div>
                </div>

                <details class="group workflow-card open:ring-2 open:ring-cyan-100">
                    <summary class="flex justify-between items-center cursor-pointer list-none select-none">
                        <div class="flex items-center gap-3">
                            <span class="flex items-center justify-center w-8 h-8 rounded-full bg-cyan-50 text-cyan-700 font-bold text-sm">3</span>
                            <div>
                                <h3 class="text-lg font-bold text-slate-900">Tighten the packet (Optional)</h3>
                                <p class="text-xs text-slate-500">Add layout details if you want a narrower credit range.</p>
                            </div>
                        </div>
                        <span class="text-slate-400 group-open:rotate-180 transition-transform duration-300">
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                        </span>
                    </summary>

                    <div class="mt-6 pt-6 border-t border-slate-100 animate-in fade-in slide-in-from-top-2 duration-300">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label class="block text-sm font-medium text-slate-700 mb-1">Bathrooms</label>
                                <select name="bathrooms" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                                    <option value="1">1 Bathroom</option>
                                    <option value="2" selected>2 Bathrooms (Avg)</option>
                                    <option value="3">3 Bathrooms</option>
                                    <option value="4">4 Bathrooms</option>
                                    <option value="5">5+ Bathrooms</option>
                                </select>
                                <p class="text-xs text-slate-500 mt-1">Affects plumbing and remodel scope.</p>
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-slate-700 mb-1">Number of stories</label>
                                <select name="stories" class="w-full p-3 border border-slate-300 rounded-xl bg-white">
                                    <option value="1" selected>1 Story (Ranch / Bungalow)</option>
                                    <option value="2">2 Stories</option>
                                    <option value="3">3+ Stories (Townhouse)</option>
                                </select>
                                <p class="text-xs text-slate-500 mt-1">Affects roofing and foundation footprint.</p>
                            </div>
                        </div>

                        <div class="mt-6">
                            <label class="block text-sm font-medium text-slate-700 mb-2">Roof material</label>
                            <div class="grid grid-cols-3 gap-3">
                                <label class="cursor-pointer">
                                    <input type="radio" name="roofType" value="ASPHALT" checked class="peer sr-only">
                                    <div class="p-3 text-center border rounded-xl peer-checked:bg-cyan-800 peer-checked:text-white peer-checked:border-cyan-800 hover:bg-slate-50 transition-all">
                                        <div class="font-semibold text-sm">Asphalt</div>
                                    </div>
                                </label>
                                <label class="cursor-pointer">
                                    <input type="radio" name="roofType" value="METAL" class="peer sr-only">
                                    <div class="p-3 text-center border rounded-xl peer-checked:bg-cyan-800 peer-checked:text-white peer-checked:border-cyan-800 hover:bg-slate-50 transition-all">
                                        <div class="font-semibold text-sm">Metal</div>
                                    </div>
                                </label>
                                <label class="cursor-pointer">
                                    <input type="radio" name="roofType" value="SLATE_TILE" class="peer sr-only">
                                    <div class="p-3 text-center border rounded-xl peer-checked:bg-cyan-800 peer-checked:text-white peer-checked:border-cyan-800 hover:bg-slate-50 transition-all">
                                        <div class="font-semibold text-sm">Slate / Tile</div>
                                    </div>
                                </label>
                            </div>
                            <p class="text-xs text-slate-500 mt-2">Material costs differ sharply across quote categories.</p>
                        </div>
                    </div>
                </details>

                <div class="workflow-card border-l-4 border-l-amber-500">
                    <div class="section-kicker">Step 4</div>
                    <h3 class="text-xl font-bold text-slate-900 mb-2">4. Confirm the red flags</h3>
                    <p class="text-sm text-slate-600 mb-4">Check only what you already saw in the inspection summary, disclosures, or showing notes.</p>

                    <div class="space-y-3">
                        <label class="flex items-center gap-3 p-3 border rounded-xl hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isFpePanel" value="true" class="w-5 h-5 text-cyan-700 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Federal Pacific / Zinsco panel</span>
                                <p class="text-xs text-slate-500">Breakers with colored switches or branding "Stab-Lok"</p>
                            </div>
                        </label>

                        <label class="flex items-center gap-3 p-3 border rounded-xl hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isPolyB" value="true" class="w-5 h-5 text-cyan-700 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Polybutylene pipes (gray plastic)</span>
                                <p class="text-xs text-slate-500">Typically found near water heater or under sinks (Era: 1978-1995)</p>
                            </div>
                        </label>

                        <label class="flex items-center gap-3 p-3 border rounded-xl hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isAluminum" value="true" class="w-5 h-5 text-cyan-700 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Aluminum wiring</span>
                                <p class="text-xs text-slate-500">Silver-colored wire conductor (Era: 1965-1973)</p>
                            </div>
                        </label>

                        <label class="flex items-center gap-3 p-3 border rounded-xl hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isChineseDrywall" value="true" class="w-5 h-5 text-cyan-700 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Chinese drywall symptoms</span>
                                <p class="text-xs text-slate-500">Sulfur smell, blackened copper wiring (Era: 2001-2009)</p>
                            </div>
                        </label>
                    </div>
                </div>

                <div class="workflow-card">
                    <div class="section-kicker">Step 5</div>
                    <h3 class="text-xl font-bold text-slate-900 mb-2">5. Already verified systems</h3>
                    <p class="text-sm text-slate-600 mb-4">Only check items explicitly listed as new or recently replaced. This keeps the packet from over-asking.</p>

                    <div class="grid grid-cols-2 gap-3">
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="ROOFING" class="rounded"> Roof</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="HVAC" class="rounded"> HVAC</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="PLUMBING" class="rounded"> Plumbing</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="ELECTRICAL" class="rounded"> Electrical</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="WINDOWS" class="rounded"> Windows</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="KITCHEN" class="rounded"> Kitchen</label>
                    </div>
                </div>

                <div class="pt-2">
                    <button type="submit" data-analytics-event="build_packet" data-analytics-category="engagement" data-analytics-label="intake_submit" class="w-full bg-cyan-800 hover:bg-cyan-900 text-white font-bold py-4 rounded-2xl transition-all shadow-lg text-lg">
                        Build Seller-Credit Packet &rarr;
                    </button>
                    <p class="text-center text-xs text-slate-500 mt-4">
                        This is a negotiation estimate, not a professional inspection or contractor quote.
                    </p>
                </div>
            </form>
        </div>
    `
)
