@import java.util.List
@param List<String> metros

@template.layout(
    title = "LifeVerdict Home Repair | 2026 Renovation Cost Calculator",
    description = "Calculate accurate 2026 home repair costs for any US metro area. Data-driven estimates for flipping, buying, or renovating older homes.",
    content = @`
        <div class="max-w-4xl mx-auto px-4 py-12">
            <!-- Hero Section -->
            <div class="text-center mb-16">
                <h1 class="text-4xl md:text-6xl font-extrabold text-slate-900 tracking-tight mb-4">
                    The <span class="text-blue-600">Honest Cost</span> of<br>Home Repair in 2026.
                </h1>
                <p class="text-lg text-slate-600 max-w-2xl mx-auto mb-8">
                    Don't guess. We calculate repair costs using 24,000+ local labor rates and material prices across every US metro area.
                </p>

                <!-- Initial Form -->
                <div class="bg-white p-6 rounded-2xl shadow-xl border border-slate-200 max-w-lg mx-auto">
                    <form action="/home-repair/step-2" method="post" class="space-y-4">
                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">Where is the house?</label>
                            <select name="metroCode" class="w-full p-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-slate-50">
                                @for(String metro : metros)
                                    <option value="${metro}">${metro.replace('_', ' ')}</option>
                                @endfor
                            </select>
                        </div>

                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">When was it built?</label>
                            <select name="era" class="w-full p-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-slate-50">
                                <option value="PRE_1950">Pre-1950 (Historic Era)</option>
                                <option value="1950_1970">1950 - 1970 (Mid-Century)</option>
                                <option value="1970_1980">1970 - 1980 (The Lead/Asbestos Era)</option>
                                <option value="1980_1995">1980 - 1995 (Modern Transition)</option>
                                <option value="1995_2010">1995 - 2010 (The Boom Era)</option>
                                <option value="2010_PRESENT">2010 - Present (Modern Standards)</option>
                            </select>
                        </div>

                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">What is your goal?</label>
                            <div class="grid grid-cols-3 gap-2">
                                <label class="cursor-pointer h-full">
                                    <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50 transition-all">
                                        Living<span class="text-xs font-normal text-slate-500 mt-1 block">Fixing it up</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    <input type="radio" name="relationship" value="BUYING" checked class="peer sr-only">
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50 transition-all">
                                        Buying<span class="text-xs font-normal text-slate-500 mt-1 block">Due diligence</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50 transition-all">
                                        Investing<span class="text-xs font-normal text-slate-500 mt-1 block">Flipping/Rent</span>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <button type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl transition-all shadow-lg hover:shadow-xl mt-4">
                            See Hidden Costs Before You Buy &rarr;
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
