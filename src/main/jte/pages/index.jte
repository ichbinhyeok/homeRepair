@import java.util.List
@param List<String> metros
@param String selectedMetroCode = null
@param String selectedEra = null
@param String selectedRelationship = "BUYING"

@template.layout(
    title = "Home inspection seller credit calculator | LifeVerdict",
    description = "Calculate a seller credit after inspection, tighten the repair request, and build a buyer-side packet before you close.",
    content = @`
        <div class="app-shell py-10 md:py-14">
            <section class="tool-hero">
                <div class="hero-panel">
                    <div class="hero-eyebrow">Buyer under inspection contingency</div>
                    <h1 class="hero-title">
                        Turn inspection findings into a <span class="hero-accent">seller-credit packet</span>.
                    </h1>
                    <p class="hero-copy">
                        Use this when the inspection is back and you need one defensible ask, one fallback, and one script to send.
                    </p>

                    <div class="hero-bullet-grid">
                        <div class="hero-bullet">
                            <strong>One persona</strong>
                            <span>Built for buyers under contract, not broad renovation browsing.</span>
                        </div>
                        <div class="hero-bullet">
                            <strong>One artifact</strong>
                            <span>Leave with a seller-credit packet, not a pile of notes.</span>
                        </div>
                        <div class="hero-bullet">
                            <strong>One CTA</strong>
                            <span>Build the packet, copy the request, and move the deal forward.</span>
                        </div>
                    </div>
                </div>

                <div class="tool-form-panel">
                    <div class="panel-kicker">Start the packet</div>
                    <h2 class="panel-title">Build a seller-credit packet.</h2>
                    <p class="panel-note">
                        Best when the inspection report is in hand and you need a fast ask before the response window closes.
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
                                <label class="block text-left text-sm font-semibold text-slate-700">Primary workflow</label>
                                <span class="text-xs font-semibold text-cyan-800 bg-cyan-50 border border-cyan-100 px-2 py-1 rounded-full">Buyer first</span>
                            </div>
                            <p class="text-left text-xs text-slate-500 mb-3">The planner is tuned for buyers. Other modes stay available, but they are secondary.</p>
                            <div class="grid grid-cols-3 gap-2">
                                <label class="cursor-pointer h-full">
                                    @if("LIVING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="LIVING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-xl border border-slate-200 peer-checked:bg-slate-50 peer-checked:border-slate-400 peer-checked:text-slate-800 hover:bg-slate-50 transition-all">
                                        Living<span class="text-xs font-normal text-slate-500 mt-1 block">Secondary</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    @if("BUYING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="BUYING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="BUYING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-xl border-2 border-cyan-300 bg-cyan-50 text-cyan-800 peer-checked:border-cyan-600 peer-checked:bg-cyan-50 peer-checked:text-cyan-800 hover:bg-cyan-100 transition-all">
                                        Buying<span class="text-xs font-normal text-cyan-800/80 mt-1 block">Main flow</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    @if("INVESTING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="INVESTING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-xl border border-slate-200 peer-checked:bg-slate-50 peer-checked:border-slate-400 peer-checked:text-slate-800 hover:bg-slate-50 transition-all">
                                        Investing<span class="text-xs font-normal text-slate-500 mt-1 block">Secondary</span>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <button type="submit" data-analytics-event="start_packet" data-analytics-category="acquisition" data-analytics-label="home_primary" class="w-full bg-cyan-800 hover:bg-cyan-900 text-white font-bold py-4 rounded-2xl transition-all shadow-lg hover:shadow-xl mt-4">
                            Build Seller-Credit Packet &rarr;
                        </button>
                    </form>

                    <ul class="tool-panel-list">
                        <li>Size the seller-credit ask from inspection findings.</li>
                        <li>Keep the repair request focused on what matters now.</li>
                        <li>Leave with a copy-ready packet for your agent.</li>
                    </ul>
                </div>
            </section>

            <section class="signal-grid">
                <div class="signal-card">
                    <div class="signal-step">1</div>
                    <h3>Findings</h3>
                    <p>Paste the inspection items that actually change the deal.</p>
                </div>
                <div class="signal-card">
                    <div class="signal-step">2</div>
                    <h3>Credit range</h3>
                    <p>Get a defensible ask anchored to local labor and the loan posture.</p>
                </div>
                <div class="signal-card">
                    <div class="signal-step">3</div>
                    <h3>Packet</h3>
                    <p>Copy the request your agent can send without rewriting the deal from scratch.</p>
                </div>
            </section>

            <section class="spotlight-grid">
                <div class="spotlight-panel warm">
                    <div class="section-kicker">Why this exists</div>
                    <h2 class="text-3xl font-bold text-slate-900 mt-3">Most inspection reports do not tell you what to ask for.</h2>
                    <ul>
                        <li>They list issues without separating negotiation leverage from noise.</li>
                        <li>They do not turn the report into a seller-credit packet.</li>
                        <li>They leave timing and wording to the buyer.</li>
                    </ul>
                </div>
                <div class="spotlight-panel cool">
                    <div class="section-kicker">What you leave with</div>
                    <h2 class="text-3xl font-bold text-slate-900 mt-3">A packet your agent can use immediately.</h2>
                    <ul>
                        <li><strong>Target ask:</strong> a starting number tied to the inspection.</li>
                        <li><strong>Defensible fallback:</strong> the lower bound if the seller pushes back.</li>
                        <li><strong>Agent script:</strong> copy-ready language for the response window.</li>
                    </ul>
                </div>
            </section>
        </div>
    `
)
