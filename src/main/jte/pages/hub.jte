@import java.util.List
@param List<String> metros

@template.layout(
    title = "LifeVerdict | Inspection Report Seller Credit Planner",
    showHeader = true,
    content = @`
    <div class="app-shell py-10 md:py-14">
        <section class="tool-hero">
            <div class="hero-panel">
                <div class="hero-eyebrow">Inspection negotiation planner</div>
                <h1 class="hero-title">
                    Build the <span class="hero-accent">Seller Credit Plan</span> Before You Close.
                </h1>
                <p class="hero-copy">
                    Use age, metro, and visible inspection pressure to figure out what needs a quote first, what belongs in the ask, and how hard to push before the deal moves on without you.
                </p>

                <div class="hero-bullet-grid">
                    <div class="hero-bullet">
                        <strong>Under-contract workflow</strong>
                        <span>Built for buyers reacting to inspection findings, not generic renovation budgeting.</span>
                    </div>
                    <div class="hero-bullet">
                        <strong>Localized leverage</strong>
                        <span>Metro labor rates and climate pressure change what a fair credit request looks like.</span>
                    </div>
                    <div class="hero-bullet">
                        <strong>Negotiation packet</strong>
                        <span>Keep the math, the priorities, and the wording in one place before you talk to the seller.</span>
                    </div>
                </div>
            </div>

            <div class="tool-form-panel">
                <div class="panel-kicker">Start the buyer flow</div>
                <h2 class="panel-title">Choose the market and era.</h2>
                <p class="panel-note">The planner is strongest when you already know the city and have a rough inspection summary in hand.</p>

                <form action="/home-repair/step-2" method="post" class="space-y-4 mt-6">
                    <div>
                        <label class="block text-left text-sm font-semibold text-slate-700 mb-1">Where is the house?</label>
                        <select name="metroCode" class="w-full p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-700 focus:border-cyan-700 bg-white">
                            @for(String metro : metros)
                                <option value="${metro}">${metro.replace('_', ' ')}</option>
                            @endfor
                        </select>
                    </div>

                    <div>
                        <label class="block text-left text-sm font-semibold text-slate-700 mb-1">When was it built?</label>
                        <select name="era" class="w-full p-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-700 focus:border-cyan-700 bg-white">
                            <option value="PRE_1950">Pre-1950 (Historic Era)</option>
                            <option value="1950_1970">1950 - 1970 (Mid-Century)</option>
                            <option value="1970_1980">1970 - 1980 (Lead / Asbestos Era)</option>
                            <option value="1980_1995">1980 - 1995 (Modern Transition)</option>
                            <option value="1995_2010">1995 - 2010 (Boom Era)</option>
                            <option value="2010_PRESENT">2010 - Present (Modern Standards)</option>
                        </select>
                    </div>

                    <div>
                        <div class="flex items-center justify-between mb-1">
                            <label class="block text-left text-sm font-semibold text-slate-700">What is your goal?</label>
                            <span class="text-xs font-semibold text-cyan-800 bg-cyan-50 border border-cyan-100 px-2 py-1 rounded-full">Buying recommended</span>
                        </div>
                        <div class="grid grid-cols-3 gap-2">
                            <label class="cursor-pointer">
                                <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                <div class="p-3 text-sm font-medium text-center rounded-xl border border-slate-200 peer-checked:bg-cyan-50 peer-checked:border-cyan-500 peer-checked:text-cyan-800 hover:bg-slate-50">
                                    Living<br><span class="text-xs font-normal text-slate-500">Fixing it up</span>
                                </div>
                            </label>
                            <label class="cursor-pointer">
                                <input type="radio" name="relationship" value="BUYING" checked class="peer sr-only">
                                <div class="p-3 text-sm font-medium text-center rounded-xl border border-cyan-300 bg-cyan-50 peer-checked:bg-cyan-50 peer-checked:border-cyan-500 peer-checked:text-cyan-800 hover:bg-cyan-100">
                                    Buying<br><span class="text-xs font-normal text-cyan-800/80">Inspection negotiation</span>
                                </div>
                            </label>
                            <label class="cursor-pointer">
                                <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                <div class="p-3 text-sm font-medium text-center rounded-xl border border-slate-200 peer-checked:bg-cyan-50 peer-checked:border-cyan-500 peer-checked:text-cyan-800 hover:bg-slate-50">
                                    Investing<br><span class="text-xs font-normal text-slate-500">Flipping / rent</span>
                                </div>
                            </label>
                        </div>
                    </div>

                    <button type="submit" class="w-full bg-cyan-800 hover:bg-cyan-900 text-white font-bold py-4 rounded-2xl transition-all shadow-lg hover:shadow-xl mt-4">
                        Start Buyer Credit Plan &rarr;
                    </button>
                </form>
            </div>
        </section>

        <section class="signal-grid">
            <div class="signal-card">
                <div class="signal-step">1</div>
                <h3>Inspection triage</h3>
                <p>Focus the conversation on findings that actually matter in due diligence and negotiation.</p>
            </div>
            <div class="signal-card">
                <div class="signal-step">2</div>
                <h3>Localized range</h3>
                <p>Labor market, supply pressure, and climate wear move the credit range more than generic national averages.</p>
            </div>
            <div class="signal-card">
                <div class="signal-step">3</div>
                <h3>Agent-ready packet</h3>
                <p>Save a print-friendly summary and a script you can reuse when the inspection response window is tight.</p>
            </div>
        </section>
    </div>
    `
)
