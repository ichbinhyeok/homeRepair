@import java.util.List
@param List<String> metros

@template.layout(
    title = "LifeVerdict Home Repair | The Real Cost of Buying a Fixer-Upper",
    showHeader = true,
    content = @`
    <div class="max-w-4xl mx-auto px-4 py-12">
        <!-- Hero Section -->
        <div class="text-center mb-16">
            <h1 class="text-4xl md:text-6xl font-extrabold text-slate-900 tracking-tight mb-4">
                Is that "Fixer-Upper"<br><span class="text-rose-600">Actually a Money Pit?</span>
            </h1>
            <p class="text-lg text-slate-600 max-w-2xl mx-auto mb-8">
                Detailed 2026 repair cost analysis for homebuyers. Don't sign the contract until you check the hidden costs.
            </p>

            <!-- Calculator Form (Same as Index) -->
            <div class="bg-white p-6 rounded-2xl shadow-xl border border-slate-200 max-w-lg mx-auto text-left">
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
                            <label class="cursor-pointer">
                                <input type="radio" name="relationship" value="LIVING" class="peer sr-only">
                                <div class="p-3 text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50">
                                    Living<br><span class="text-xs font-normal text-slate-500">Fixing it up</span>
                                </div>
                            </label>
                            <label class="cursor-pointer">
                                <input type="radio" name="relationship" value="BUYING" checked class="peer sr-only">
                                <div class="p-3 text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50">
                                    Buying<br><span class="text-xs font-normal text-slate-500">Due diligence</span>
                                </div>
                            </label>
                            <label class="cursor-pointer">
                                <input type="radio" name="relationship" value="INVESTING" class="peer sr-only">
                                <div class="p-3 text-sm font-medium text-center rounded-lg border border-slate-200 peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 hover:bg-slate-50">
                                    Investing<br><span class="text-xs font-normal text-slate-500">Flipping/Rent</span>
                                </div>
                            </label>
                        </div>
                    </div>

                    <button type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl transition-all shadow-lg hover:shadow-xl mt-4">
                        Check Hidden Costs &rarr;
                    </button>
                </form>
            </div>
        </div>

        <!-- Trust Signals -->
        <div class="grid md:grid-cols-3 gap-8 py-12 border-t border-slate-200">
            <div class="text-center">
                <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">1</div>
                <h3 class="font-bold text-slate-900 mb-2">Forensic Data</h3>
                <p class="text-sm text-slate-600">We analyze 45+ specific risk factors based on the exact year the home was built.</p>
            </div>
            <div class="text-center">
                <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">2</div>
                <h3 class="font-bold text-slate-900 mb-2">Local Material Pricing</h3>
                <p class="text-sm text-slate-600">Costs are adjusted for your specific metro area's supply chain and taxes.</p>
            </div>
            <div class="text-center">
                <div class="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4 text-blue-600 font-bold text-xl">3</div>
                <h3 class="font-bold text-slate-900 mb-2">Negotiation Leverage</h3>
                <p class="text-sm text-slate-600">Get a PDF report you can use to negotiate a lower purchase price.</p>
            </div>
        </div>
    </div>
    `
)
