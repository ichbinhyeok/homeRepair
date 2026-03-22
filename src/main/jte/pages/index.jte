@import java.util.List
@param List<String> metros
@param String selectedMetroCode = null
@param String selectedEra = null
@param String selectedRelationship = "BUYING"

@template.layout(
    title = "Inspection repair budget by city and era | LifeVerdict",
    description = "Turn an older home's inspection into a repair budget and seller-credit plan. Compare city and era costs before you buy.",
    content = @`
        <div class="max-w-4xl mx-auto px-4 py-12">
            <!-- Hero Section -->
            <div class="text-center mb-16">
                <h1 class="text-4xl md:text-6xl font-extrabold text-slate-900 tracking-tight mb-4">
                    Turn the Inspection Into a <span class="text-blue-600">Repair Budget</span><br>Before You Buy.
                </h1>
                <p class="text-lg text-slate-600 max-w-2xl mx-auto mb-8">
                    Use city labor rates, build-era risk patterns, and inspection priorities to decide what to budget and what seller credit to ask for.
                </p>

                <!-- Initial Form -->
                <div class="bg-white p-6 rounded-2xl shadow-xl border border-slate-200 max-w-lg mx-auto">
                    <form action="/home-repair/step-2" method="post" class="space-y-4">
                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">Where is the house?</label>
                            <select name="metroCode" class="w-full p-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-slate-50">
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
                            <select name="era" class="w-full p-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-slate-50">
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
                                    <option value="1970_1980" selected>1970 - 1980 (The Lead/Asbestos Era)</option>
                                @else
                                    <option value="1970_1980">1970 - 1980 (The Lead/Asbestos Era)</option>
                                @endif
                                @if("1980_1995".equals(selectedEra))
                                    <option value="1980_1995" selected>1980 - 1995 (Modern Transition)</option>
                                @else
                                    <option value="1980_1995">1980 - 1995 (Modern Transition)</option>
                                @endif
                                @if("1995_2010".equals(selectedEra))
                                    <option value="1995_2010" selected>1995 - 2010 (The Boom Era)</option>
                                @else
                                    <option value="1995_2010">1995 - 2010 (The Boom Era)</option>
                                @endif
                                @if("2010_PRESENT".equals(selectedEra))
                                    <option value="2010_PRESENT" selected>2010 - Present (Modern Standards)</option>
                                @else
                                    <option value="2010_PRESENT">2010 - Present (Modern Standards)</option>
                                @endif
                            </select>
                        </div>

                        <div>
                            <label class="block text-left text-sm font-semibold text-slate-700 mb-1">What is your goal?</label>
                            <div class="grid grid-cols-3 gap-2">
                                <label class="cursor-pointer h-full">
                                    @if("LIVING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="LIVING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50 transition-all">
                                        Living<span class="text-xs font-normal text-slate-500 mt-1 block">Fixing it up</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    @if("BUYING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="BUYING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="BUYING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50 transition-all">
                                        Buying<span class="text-xs font-normal text-slate-500 mt-1 block">Due diligence</span>
                                    </div>
                                </label>
                                <label class="cursor-pointer h-full">
                                    @if("INVESTING".equals(selectedRelationship))
                                        <input type="radio" name="relationship" value="INVESTING" class="peer sr-only" checked>
                                    @else
                                        <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                    @endif
                                    <div class="p-3 h-full flex flex-col justify-center items-center text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50 transition-all">
                                        Investing<span class="text-xs font-normal text-slate-500 mt-1 block">Flipping/Rent</span>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <button type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl transition-all shadow-lg hover:shadow-xl mt-4">
                            Build My Inspection Budget &rarr;
                        </button>
                    </form>
                </div>
            </div>

            <!-- Trust Signals -->
            <div class="grid md:grid-cols-3 gap-8 py-12 border-t border-slate-200">
                <div class="text-center">
                    <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">1</div>
                    <h3 class="font-bold text-slate-900 mb-2">Inspection Budget</h3>
                    <p class="text-sm text-slate-600">We convert likely repair findings into a localized budget using 2026 labor and material pricing.</p>
                </div>
                <div class="text-center">
                    <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">2</div>
                    <h3 class="font-bold text-slate-900 mb-2">Inspector Priorities</h3>
                    <p class="text-sm text-slate-600">From knob and tube wiring to failing roof systems, we surface what should be verified first.</p>
                </div>
                <div class="text-center">
                    <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">3</div>
                    <h3 class="font-bold text-slate-900 mb-2">Seller Credit Plan</h3>
                    <p class="text-sm text-slate-600">We separate true negotiation items from noise so you can back a repair request with numbers.</p>
                </div>
            </div>

            <!-- SEO Content -->
            <div class="prose prose-slate max-w-none mt-12">
                <h2>Why generic inspection advice fails buyers</h2>
                <p>Most checklists tell you what can go wrong, but not what the repair burden means in your metro. Most cost calculators do the opposite: they use national averages and ignore <strong>age-related degradation</strong>.</p>
                <p>LifeVerdict is different. We combine:</p>
                <ul>
                    <li><strong>Inspection Priorities:</strong> Which systems should be checked first for a given era and region.</li>
                    <li><strong>City-Specific Repair Math:</strong> How local labor and material pricing change the budget.</li>
                    <li><strong>Negotiation Use:</strong> What part of the burden is strong enough to support a seller-credit request.</li>
                </ul>
            </div>
        </div>
    `
)
