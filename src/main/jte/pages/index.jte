@import java.util.List
@param List<String> metros
@param String selectedMetroCode = null
@param String selectedEra = null
@param String selectedRelationship = "BUYING"

@template.layout(
    title = "Inspection report seller credit tool by city and era | LifeVerdict",
    description = "Turn an older home's inspection into a repair budget, seller-credit range, and negotiation plan before you buy.",
    content = @`
        <div class="app-shell py-10 md:py-14">
            <section class="tool-hero">
                <div class="hero-panel">
                    <div class="hero-eyebrow">Buyer-side inspection workflow</div>
                    <h1 class="hero-title">
                        Turn an Inspection Report Into a <span class="hero-accent">Seller Credit Plan</span>.
                    </h1>
                    <p class="hero-copy">
                        Stop guessing what belongs in the repair ask. We use local labor rates, build-era risk, and inspection priorities to sort what needs documentation now, what deserves a quote first, and what credit range is defensible before closing.
                    </p>

                    <div class="hero-bullet-grid">
                        <div class="hero-bullet">
                            <strong>Localized repair math</strong>
                            <span>Metro labor pressure and age-driven failure patterns shape the budget.</span>
                        </div>
                        <div class="hero-bullet">
                            <strong>Buyer negotiation logic</strong>
                            <span>Separate real repair leverage from cosmetic noise and post-close items.</span>
                        </div>
                        <div class="hero-bullet">
                            <strong>Copy-ready packet</strong>
                            <span>Leave with a credit range, priority list, and request summary your agent can use.</span>
                        </div>
                    </div>
                </div>

                <div class="tool-form-panel">
                    <div class="panel-kicker">Start the planner</div>
                    <h2 class="panel-title">Build a cleaner repair ask.</h2>
                    <p class="panel-note">
                        Best for buyers under contract who need to react fast once the inspection comes back.
                    </p>

                    <form action="/home-repair/step-2" method="post" class="space-y-4 mt-6">
                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">Where is the house?</label>
                            <select name="metroCode" class="w-full p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-700 focus:border-cyan-700 bg-white">
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
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">When was it built?</label>
                            <select name="era" class="w-full p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-700 focus:border-cyan-700 bg-white">
                                @if("PRE_1950".equals(selectedEra))
                                    <option value="PRE_1950" selected>Pre-1950 (Historic Era)</option>
                                @else
                                    <option value="PRE_1950">Pre-1950 (Historic Era)</option>
                                @endif
                                @if("1950_1970".equals(selectedEra))
                                    <option value="1950_1970" selected>1950 - 1970 (Mid-Century)</option>
                                @else
                                    <option value="1950_1970">1950 - 1970 (Mid-Century)</option>
                                @endif
                                @if("1970_1980".equals(selectedEra))
                                    <option value="1970_1980" selected>1970 - 1980 (Lead / Asbestos Era)</option>
                                @else
                                    <option value="1970_1980">1970 - 1980 (Lead / Asbestos Era)</option>
                                @endif
                                @if("1980_1995".equals(selectedEra))
                                    <option value="1980_1995" selected>1980 - 1995 (Modern Transition)</option>
                                @else
                                    <option value="1980_1995">1980 - 1995 (Modern Transition)</option>
                                @endif
                                @if("1995_2010".equals(selectedEra))
                                    <option value="1995_2010" selected>1995 - 2010 (Boom Era)</option>
                                @else
                                    <option value="1995_2010">1995 - 2010 (Boom Era)</option>
                                @endif
                                @if("2010_PRESENT".equals(selectedEra))
                                    <option value="2010_PRESENT" selected>2010 - Present (Modern Standards)</option>
                                @else
                                    <option value="2010_PRESENT">2010 - Present (Modern Standards)</option>
                                @endif
                            </select>
                        </div>

                        <div>
                            <div class="flex items-center justify-between mb-1">
                                <label class="block text-left text-sm font-semibold text-slate-700">What is your goal?</label>
                                <span class="text-xs font-semibold text-cyan-800 bg-cyan-50 border border-cyan-100 px-2 py-1 rounded-full">Buying recommended</span>
                            </div>
                            <p class="text-left text-xs text-slate-500 mb-3">This flow is sharpest when you are already thinking about the inspection response.</p>
                            <div class="grid grid-cols-3 gap-2">
                                <label class="cursor-pointer h-full">
                                    @if("LIVING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="LIVING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-xl border border-slate-200 peer-checked:bg-slate-50 peer-checked:border-slate-400 peer-checked:text-slate-800 hover:bg-slate-50 transition-all">
                                        Living<span class="text-xs font-normal text-slate-500 mt-1 block">Maintenance planning</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    @if("BUYING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="BUYING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="BUYING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-xl border-2 border-cyan-300 bg-cyan-50 text-cyan-800 peer-checked:border-cyan-600 peer-checked:bg-cyan-50 peer-checked:text-cyan-800 hover:bg-cyan-100 transition-all">
                                        Buying<span class="text-xs font-normal text-cyan-800/80 mt-1 block">Inspection negotiation</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    @if("INVESTING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="INVESTING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-xl border border-slate-200 peer-checked:bg-slate-50 peer-checked:border-slate-400 peer-checked:text-slate-800 hover:bg-slate-50 transition-all">
                                        Investing<span class="text-xs font-normal text-slate-500 mt-1 block">Deal screening</span>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <button type="submit" class="w-full bg-cyan-800 hover:bg-cyan-900 text-white font-bold py-4 rounded-2xl transition-all shadow-lg hover:shadow-xl mt-4">
                            Start My Seller Credit Plan &rarr;
                        </button>
                    </form>

                    <ul class="tool-panel-list">
                        <li>Size a localized repair budget in minutes.</li>
                        <li>See which findings belong in the ask and which can wait.</li>
                        <li>Walk away with a copy-ready seller credit summary.</li>
                    </ul>
                </div>
            </section>

            <section class="signal-grid">
                <div class="signal-card">
                    <div class="signal-step">1</div>
                    <h3>Inspection triage</h3>
                    <p>We separate findings that matter in negotiation from homeowner noise you can handle later.</p>
                </div>
                <div class="signal-card">
                    <div class="signal-step">2</div>
                    <h3>Credit range</h3>
                    <p>Localized labor data and era-specific wear patterns shape a cleaner, more defensible ask.</p>
                </div>
                <div class="signal-card">
                    <div class="signal-step">3</div>
                    <h3>Negotiation packet</h3>
                    <p>You leave with a request summary your agent can copy, edit, and send before momentum dies.</p>
                </div>
            </section>

            <section class="spotlight-grid">
                <div class="spotlight-panel warm">
                    <div class="section-kicker">Why buyers stall</div>
                    <h2 class="text-3xl font-bold text-slate-900 mt-3">Most inspection guides stop one step too early.</h2>
                    <ul>
                        <li>They tell you what can go wrong, but not what actually belongs in the repair request.</li>
                        <li>They lean on national averages instead of the labor market where you are buying.</li>
                        <li>They ignore timing: what needs a quote now versus what can wait until after closing.</li>
                    </ul>
                </div>
                <div class="spotlight-panel cool">
                    <div class="section-kicker">What changes here</div>
                    <h2 class="text-3xl font-bold text-slate-900 mt-3">LifeVerdict behaves more like a negotiation workbench.</h2>
                    <ul>
                        <li><strong>Inspection priorities:</strong> what to verify first for a given era and region.</li>
                        <li><strong>Localized cost math:</strong> how metro labor and climate alter the budget.</li>
                        <li><strong>Negotiation use:</strong> what belongs in the seller-credit request and how far to push.</li>
                    </ul>
                </div>
            </section>
        </div>
    `
)
