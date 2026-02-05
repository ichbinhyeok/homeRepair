@import java.util.List
@param List<String> metros
@param String prefilledMetro = null
@param String prefilledEra = null

@template.layout(
    title = "LifeVerdict Home Repair | 2026 Renovation Cost Calculator",
    description = "Calculate accurate 2026 home repair costs for any US metro area. Data-driven estimates for flipping, buying, or renovating older homes.",
    content = @`
        <div class="max-w-4xl mx-auto px-4 py-12">
            <!-- Hero Section -->
            <div class="text-center mb-16">
                <h1 class="text-4xl md:text-6xl font-extrabold text-slate-900 tracking-tight mb-4">
                    Know the <span class="text-blue-600">Hidden Costs</span> Before<br>You Make an Offer.
                </h1>
                <p class="text-lg text-slate-600 max-w-2xl mx-auto mb-8">
                    Avoid expensive surprises. We analyze era-specific risks and local labor rates to calculate your true renovation budget.
                </p>

                <!-- Initial Form -->
                <div class="bg-white p-6 rounded-2xl shadow-xl border border-slate-200 max-w-lg mx-auto">
                    <form action="/home-repair/step-2" method="post" class="space-y-4">
                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">Where is the house?</label>
                            <select name="metroCode" class="w-full p-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-slate-50">
                                @for(String metro : metros)
                                    <option value="${metro}" selected="${metro.equals(prefilledMetro) ? true : null}">${metro.replace('_', ' ')}</option>
                                @endfor
                            </select>
                        </div>

                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">When was it built?</label>
                            <select name="era" class="w-full p-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-slate-50">
                                <option value="PRE_1950" selected="${"PRE_1950".equals(prefilledEra) ? true : null}">Pre-1950 (Historic Era)</option>
                                <option value="1950_1970" selected="${"1950_1970".equals(prefilledEra) ? true : null}">1950 - 1970 (Mid-Century)</option>
                                <option value="1970_1980" selected="${"1970_1980".equals(prefilledEra) ? true : null}">1970 - 1980 (The Lead/Asbestos Era)</option>
                                <option value="1980_1995" selected="${"1980_1995".equals(prefilledEra) ? true : null}">1980 - 1995 (Modern Transition)</option>
                                <option value="1995_2010" selected="${"1995_2010".equals(prefilledEra) ? true : null}">1995 - 2010 (The Boom Era)</option>
                                <option value="2010_PRESENT" selected="${"2010_PRESENT".equals(prefilledEra) ? true : null}">2010 - Present (Modern Standards)</option>
                            </select>
                        </div>

                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">What is your goal?</label>
                            <div class="grid grid-cols-3 gap-2">
                                <label class="cursor-pointer">
                                    <input type="radio" name="relationship" value="BUYING" checked class="peer sr-only">
                                    <div class="p-3 text-sm font-medium text-center rounded-lg border border-slate-200 bg-blue-50 border-blue-500 text-blue-700 peer-checked:bg-blue-600 peer-checked:text-white peer-checked:border-blue-600 hover:bg-slate-50">
                                        Buying<br><span class="text-xs font-normal opacity-80">Due diligence</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer">
                                    <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                    <div class="p-3 text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-slate-800 peer-checked:text-white peer-checked:border-slate-800 hover:bg-slate-50">
                                        Living<br><span class="text-xs font-normal text-slate-500 peer-checked:text-slate-300">Renovating</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer">
                                    <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                    <div class="p-3 text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-slate-800 peer-checked:text-white peer-checked:border-slate-800 hover:bg-slate-50">
                                        Investing<br><span class="text-xs font-normal text-slate-500 peer-checked:text-slate-300">Flipping</span>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <button type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl transition-all shadow-lg hover:shadow-xl mt-4 text-lg">
                            Calculate Hidden Costs &rarr;
                        </button>
                    </form>
                </div>
            </div>

            <!-- Trust Signals -->
            <div class="grid md:grid-cols-3 gap-8 py-12 border-t border-slate-200">
                <div class="text-center">
                    <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">1</div>
                    <h3 class="font-bold text-slate-900 mb-2">Localized Labor Rates</h3>
                    <p class="text-sm text-slate-600">We adjust every cost based on 2026 labor market data for your specific zip code area.</p>
                </div>
                <div class="text-center">
                    <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">2</div>
                    <h3 class="font-bold text-slate-900 mb-2">Era-Specific Risks</h3>
                    <p class="text-sm text-slate-600">From knob & tube wiring to polybutylene pipes, we know what dangers hide in each decade.</p>
                </div>
                <div class="text-center">
                    <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">3</div>
                    <h3 class="font-bold text-slate-900 mb-2">Investment Grade</h3>
                    <p class="text-sm text-slate-600">Our algorithm separates "nice to have" from "deal killers" to protect your capital.</p>
                </div>
            </div>

            <!-- State Links (SEO Discovery) -->
            <section class="py-12 border-t border-slate-200">
                <h2 class="text-2xl font-bold text-slate-900 mb-6">Explore Estimates by State</h2>
                <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4 text-sm">
                    <a href="/home-repair/states/al.html" class="text-blue-600 hover:underline">Alabama</a>
                    <a href="/home-repair/states/ak.html" class="text-blue-600 hover:underline">Alaska</a>
                    <a href="/home-repair/states/az.html" class="text-blue-600 hover:underline">Arizona</a>
                    <a href="/home-repair/states/ar.html" class="text-blue-600 hover:underline">Arkansas</a>
                    <a href="/home-repair/states/ca.html" class="text-blue-600 hover:underline font-semibold">California</a>
                    <a href="/home-repair/states/co.html" class="text-blue-600 hover:underline">Colorado</a>
                    <a href="/home-repair/states/ct.html" class="text-blue-600 hover:underline">Connecticut</a>
                    <a href="/home-repair/states/de.html" class="text-blue-600 hover:underline">Delaware</a>
                    <a href="/home-repair/states/fl.html" class="text-blue-600 hover:underline font-semibold">Florida</a>
                    <a href="/home-repair/states/ga.html" class="text-blue-600 hover:underline">Georgia</a>
                    <a href="/home-repair/states/hi.html" class="text-blue-600 hover:underline">Hawaii</a>
                    <a href="/home-repair/states/id.html" class="text-blue-600 hover:underline">Idaho</a>
                    <a href="/home-repair/states/il.html" class="text-blue-600 hover:underline">Illinois</a>
                    <a href="/home-repair/states/in.html" class="text-blue-600 hover:underline">Indiana</a>
                    <a href="/home-repair/states/ia.html" class="text-blue-600 hover:underline">Iowa</a>
                    <a href="/home-repair/states/ks.html" class="text-blue-600 hover:underline">Kansas</a>
                    <a href="/home-repair/states/ky.html" class="text-blue-600 hover:underline">Kentucky</a>
                    <a href="/home-repair/states/la.html" class="text-blue-600 hover:underline">Louisiana</a>
                    <a href="/home-repair/states/me.html" class="text-blue-600 hover:underline">Maine</a>
                    <a href="/home-repair/states/md.html" class="text-blue-600 hover:underline">Maryland</a>
                    <a href="/home-repair/states/ma.html" class="text-blue-600 hover:underline">Massachusetts</a>
                    <a href="/home-repair/states/mi.html" class="text-blue-600 hover:underline">Michigan</a>
                    <a href="/home-repair/states/mn.html" class="text-blue-600 hover:underline">Minnesota</a>
                    <a href="/home-repair/states/ms.html" class="text-blue-600 hover:underline">Mississippi</a>
                    <a href="/home-repair/states/mo.html" class="text-blue-600 hover:underline">Missouri</a>
                    <a href="/home-repair/states/mt.html" class="text-blue-600 hover:underline">Montana</a>
                    <a href="/home-repair/states/ne.html" class="text-blue-600 hover:underline">Nebraska</a>
                    <a href="/home-repair/states/nv.html" class="text-blue-600 hover:underline">Nevada</a>
                    <a href="/home-repair/states/nh.html" class="text-blue-600 hover:underline">New Hampshire</a>
                    <a href="/home-repair/states/nj.html" class="text-blue-600 hover:underline">New Jersey</a>
                    <a href="/home-repair/states/nm.html" class="text-blue-600 hover:underline">New Mexico</a>
                    <a href="/home-repair/states/ny.html" class="text-blue-600 hover:underline font-semibold">New York</a>
                    <a href="/home-repair/states/nc.html" class="text-blue-600 hover:underline">North Carolina</a>
                    <a href="/home-repair/states/nd.html" class="text-blue-600 hover:underline">North Dakota</a>
                    <a href="/home-repair/states/oh.html" class="text-blue-600 hover:underline">Ohio</a>
                    <a href="/home-repair/states/ok.html" class="text-blue-600 hover:underline">Oklahoma</a>
                    <a href="/home-repair/states/or.html" class="text-blue-600 hover:underline">Oregon</a>
                    <a href="/home-repair/states/pa.html" class="text-blue-600 hover:underline">Pennsylvania</a>
                    <a href="/home-repair/states/ri.html" class="text-blue-600 hover:underline">Rhode Island</a>
                    <a href="/home-repair/states/sc.html" class="text-blue-600 hover:underline">South Carolina</a>
                    <a href="/home-repair/states/sd.html" class="text-blue-600 hover:underline">South Dakota</a>
                    <a href="/home-repair/states/tn.html" class="text-blue-600 hover:underline">Tennessee</a>
                    <a href="/home-repair/states/tx.html" class="text-blue-600 hover:underline font-semibold">Texas</a>
                    <a href="/home-repair/states/ut.html" class="text-blue-600 hover:underline">Utah</a>
                    <a href="/home-repair/states/vt.html" class="text-blue-600 hover:underline">Vermont</a>
                    <a href="/home-repair/states/va.html" class="text-blue-600 hover:underline">Virginia</a>
                    <a href="/home-repair/states/wa.html" class="text-blue-600 hover:underline">Washington</a>
                    <a href="/home-repair/states/wv.html" class="text-blue-600 hover:underline">West Virginia</a>
                    <a href="/home-repair/states/wi.html" class="text-blue-600 hover:underline">Wisconsin</a>
                    <a href="/home-repair/states/wy.html" class="text-blue-600 hover:underline">Wyoming</a>
                </div>
            </section>

            <!-- Popular Cities (Discovery) -->
            <section class="py-12 border-t border-slate-200">
                <h2 class="text-2xl font-bold text-slate-900 mb-6">Popular Estimates</h2>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <a href="/home-repair/verdicts/houston-the-woodlands-tx/1950-1970.html" class="block p-4 bg-slate-50 rounded-lg hover:bg-white hover:shadow-md transition">
                        <span class="font-bold block text-slate-900">Houston 1950s Homes</span>
                        <span class="text-sm text-slate-500">Known for humidity & flood risks</span>
                    </a>
                    <a href="/home-repair/verdicts/los-angeles-long-beach-ca/pre-1950.html" class="block p-4 bg-slate-50 rounded-lg hover:bg-white hover:shadow-md transition">
                        <span class="font-bold block text-slate-900">Los Angeles Historic Homes</span>
                        <span class="text-sm text-slate-500">Seismic retrofitting needed</span>
                    </a>
                    <a href="/home-repair/verdicts/miami-ft-lauderdale-fl/1980-1995.html" class="block p-4 bg-slate-50 rounded-lg hover:bg-white hover:shadow-md transition">
                        <span class="font-bold block text-slate-900">Miami 1980s Condos</span>
                        <span class="text-sm text-slate-500">Hurricane code updates</span>
                    </a>
                    <a href="/home-repair/verdicts/chicago-naperville-il/pre-1950.html" class="block p-4 bg-slate-50 rounded-lg hover:bg-white hover:shadow-md transition">
                        <span class="font-bold block text-slate-900">Chicago Brick Bungalows</span>
                        <span class="text-sm text-slate-500">Winterizing & masonry</span>
                    </a>
                    <a href="/home-repair/verdicts/phoenix-mesa-chandler-az/1995-2010.html" class="block p-4 bg-slate-50 rounded-lg hover:bg-white hover:shadow-md transition">
                        <span class="font-bold block text-slate-900">Phoenix Stucco Homes</span>
                        <span class="text-sm text-slate-500">Heat damage & AC lifespan</span>
                    </a>
                    <a href="/home-repair/verdicts/seattle-tacoma-bellevue-wa/1950-1970.html" class="block p-4 bg-slate-50 rounded-lg hover:bg-white hover:shadow-md transition">
                        <span class="font-bold block text-slate-900">Seattle Mid-Century</span>
                        <span class="text-sm text-slate-500">Moisture & foundation issues</span>
                    </a>
                </div>
            </section>

            <!-- SEO Content -->
            <div class="prose prose-slate max-w-none mt-12">
                <h2>Why standard home estimates fail</h2>
                <p>Most online calculators use national averages ($150/sqft) that are meaningless in high-cost metros like San Francisco or specific climate zones like Miami. They also ignore <strong>age-related degradation</strong>.</p>
                <p>LifeVerdict is different. We combine:</p>
                <ul>
                    <li><strong>Climate Zone Impact:</strong> How local weather accelerates wear (e.g., salt air, freeze-thaw).</li>
                    <li><strong>Era-Specific Building Codes:</strong> What was legal in 1970 but dangerous today.</li>
                    <li><strong>2026 Supply Chain Pricing:</strong> Real-time material and labor cost adjustments.</li>
                </ul>
            </div>
        </div>
    `
)
