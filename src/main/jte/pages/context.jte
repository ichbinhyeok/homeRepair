@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.ContextBriefing
@param String metroCode
@param String era
@param String relationship
@param ContextBriefing contextBriefing

@template.layout(
    title = "Home Details | LifeVerdict",
    description = "Enter home details for accurate risk assessment.",
    content = @`
        <div class="max-w-2xl mx-auto px-4 py-8">
            <!-- Context Briefing Card -->
            <div class="bg-blue-50 border border-blue-200 rounded-xl p-6 mb-8">
                <h2 class="text-lg font-bold text-blue-900 mb-2">Market Intelligence: ${metroCode} (${era})</h2>
                <ul class="space-y-2 text-sm text-blue-800">
                    <li class="flex items-start gap-2">
                        <span class="mt-0.5">👷</span> 
                        <span><strong>Labor Market:</strong> ${contextBriefing.getLaborMarketRate()} (${contextBriefing.getLaborMarketRateReason()})</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="mt-0.5">🌦️</span>
                        <span><strong>Regional Risk:</strong> ${contextBriefing.getRegionalRisk()} (${contextBriefing.getRegionalRiskReason()})</span>
                    </li>
                    <li class="flex items-start gap-2">
                        <span class="mt-0.5">🏚️</span>
                        <span><strong>Era Feature:</strong> ${contextBriefing.getEraFeature()} (${contextBriefing.getEraFeatureReason()})</span>
                    </li>
                </ul>
            </div>

            <form action="/home-repair/verdict" method="post" class="space-y-8">
                <input type="hidden" name="metroCode" value="${metroCode}">
                <input type="hidden" name="era" value="${era}">
                <input type="hidden" name="relationship" value="${relationship}">

                <!-- Core Details -->
                <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                    <h3 class="text-xl font-bold text-slate-900 mb-4">1. Project Scope</h3>
                    
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">Renovation Budget ($)</label>
                            <input type="number" name="budget" required min="1000" step="1" placeholder="e.g. 50000" class="w-full p-3 border border-slate-300 rounded-lg">
                            <p class="text-xs text-slate-500 mt-1">Cash available for repairs</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-slate-700 mb-1">House Size (SqFt)</label>
                            <input type="number" name="sqft" required min="500" max="10000" step="1" placeholder="e.g. 2000" class="w-full p-3 border border-slate-300 rounded-lg">
                            <p class="text-xs text-slate-500 mt-1">Interior living area. (Unsure? Use 1800 for 3-Bed)</p>
                        </div>
                    </div>

                    <div class="mt-6">
                        <label class="block text-sm font-medium text-slate-700 mb-2">General Condition</label>
                        <div class="grid grid-cols-3 gap-3">
                            <label class="cursor-pointer h-full">
                                <input type="radio" name="condition" value="NONE" class="peer sr-only">
                                <div class="p-3 h-full flex flex-col justify-center items-center text-center border rounded-lg peer-checked:bg-green-50 peer-checked:border-green-500 hover:bg-slate-50 transition-all">
                                    <div class="font-semibold">Turnkey</div>
                                    <div class="text-xs text-slate-500 mt-1">Move-in ready</div>
                                </div>
                            </label>
                            <label class="cursor-pointer h-full">
                                <input type="radio" name="condition" value="MINOR" checked class="peer sr-only">
                                <div class="p-3 h-full flex flex-col justify-center items-center text-center border rounded-lg peer-checked:bg-yellow-50 peer-checked:border-yellow-500 hover:bg-slate-50 transition-all">
                                    <div class="font-semibold">Average</div>
                                    <div class="text-xs text-slate-500 mt-1">Dated / Lived In</div>
                                </div>
                            </label>
                            <label class="cursor-pointer h-full">
                                <input type="radio" name="condition" value="SEVERE" class="peer sr-only">
                                <div class="p-3 h-full flex flex-col justify-center items-center text-center border rounded-lg peer-checked:bg-red-50 peer-checked:border-red-500 hover:bg-slate-50 transition-all">
                                    <div class="font-semibold">Fixer Upper</div>
                                    <div class="text-xs text-slate-500 mt-1">Needs Work</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- House Configuration -->
                <!-- House Configuration (Optional) -->
                <details class="group bg-white p-6 rounded-xl border border-slate-200 shadow-sm open:ring-2 open:ring-blue-100">
                    <summary class="flex justify-between items-center cursor-pointer list-none select-none">
                        <div class="flex items-center gap-3">
                             <span class="flex items-center justify-center w-8 h-8 rounded-full bg-blue-50 text-blue-600 font-bold text-sm">2</span>
                             <div>
                                <h3 class="text-lg font-bold text-slate-900">Advanced Specs (Optional)</h3>
                                <p class="text-xs text-slate-500">Refine accuracy with detailed layout info</p>
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
                                <select name="bathrooms" class="w-full p-3 border border-slate-300 rounded-lg bg-slate-50 focus:bg-white transition-colors">
                                    <option value="1">1 Bathroom</option>
                                    <option value="2" selected>2 Bathrooms (Avg)</option>
                                    <option value="3">3 Bathrooms</option>
                                    <option value="4">4 Bathrooms</option>
                                    <option value="5">5+ Bathrooms</option>
                                </select>
                                <p class="text-xs text-slate-500 mt-1">Affects plumbing and remodel scope</p>
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-slate-700 mb-1">Number of Stories</label>
                                <select name="stories" class="w-full p-3 border border-slate-300 rounded-lg bg-slate-50 focus:bg-white transition-colors">
                                    <option value="1" selected>1 Story (Ranch/Bungalow)</option>
                                    <option value="2">2 Stories</option>
                                    <option value="3">3+ Stories (Townhouse)</option>
                                </select>
                                <p class="text-xs text-slate-500 mt-1">Affects roofing and foundation footprint</p>
                            </div>
                        </div>
                        <div class="mt-6">
                            <label class="block text-sm font-medium text-slate-700 mb-2">Roof Material</label>
                            <div class="grid grid-cols-3 gap-3">
                                <label class="cursor-pointer">
                                    <input type="radio" name="roofType" value="ASPHALT" checked class="peer sr-only">
                                    <div class="p-3 text-center border rounded-lg peer-checked:bg-blue-600 peer-checked:text-white peer-checked:border-blue-600 hover:bg-slate-50 transition-all">
                                        <div class="font-semibold text-sm">Asphalt</div>
                                    </div>
                                </label>
                                <label class="cursor-pointer">
                                    <input type="radio" name="roofType" value="METAL" class="peer sr-only">
                                    <div class="p-3 text-center border rounded-lg peer-checked:bg-blue-600 peer-checked:text-white peer-checked:border-blue-600 hover:bg-slate-50 transition-all">
                                        <div class="font-semibold text-sm">Metal</div>
                                    </div>
                                </label>
                                <label class="cursor-pointer">
                                    <input type="radio" name="roofType" value="SLATE_TILE" class="peer sr-only">
                                    <div class="p-3 text-center border rounded-lg peer-checked:bg-blue-600 peer-checked:text-white peer-checked:border-blue-600 hover:bg-slate-50 transition-all">
                                        <div class="font-semibold text-sm">Slate/Tile</div>
                                    </div>
                                </label>
                            </div>
                            <p class="text-xs text-slate-500 mt-2">Material costs differ by 300%+</p>
                        </div>
                    </div>
                </details>

                <!-- Phase 3: Forensic Clues -->
                <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm border-l-4 border-l-orange-400">
                    <h3 class="text-xl font-bold text-slate-900 mb-2">2. Visual Inspection (Forensic Clues)</h3>
                    <p class="text-sm text-slate-600 mb-4">Check any items you have visually confirmed in photos or during a showing.</p>
                    
                    <div class="space-y-3">
                        <label class="flex items-center gap-3 p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isFpePanel" value="true" class="w-5 h-5 text-blue-600 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Federal Pacific / Zinsco Panel</span>
                                <p class="text-xs text-slate-500">Breakers with colored switches or branding "Stab-Lok"</p>
                            </div>
                        </label>

                        <label class="flex items-center gap-3 p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isPolyB" value="true" class="w-5 h-5 text-blue-600 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Polybutylene Pipes (Gray Plastic)</span>
                                <p class="text-xs text-slate-500">Typically found near water heater or under sinks (Era: 1978-1995)</p>
                            </div>
                        </label>

                        <label class="flex items-center gap-3 p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isAluminum" value="true" class="w-5 h-5 text-blue-600 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Aluminum Wiring</span>
                                <p class="text-xs text-slate-500">Silver-colored wire conductor (Era: 1965-1973)</p>
                            </div>
                        </label>
                        
                         <label class="flex items-center gap-3 p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
                            <input type="checkbox" name="isChineseDrywall" value="true" class="w-5 h-5 text-blue-600 rounded">
                            <div>
                                <span class="font-medium text-slate-900">Chinese Drywall Symptoms</span>
                                <p class="text-xs text-slate-500">Sulfur smell, blackened copper wiring (Era: 2001-2009)</p>
                            </div>
                        </label>
                    </div>
                </div>

                <!-- History -->
                <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                    <h3 class="text-xl font-bold text-slate-900 mb-2">3. Verified Updates (From Listing)</h3>
                    <p class="text-sm text-slate-600 mb-4">Only check items explicitly listed as "New" or "Replaced" (e.g. "New Roof 2024").</p>
                    
                    <div class="grid grid-cols-2 gap-3">
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="ROOFING" class="rounded"> Roof</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="HVAC" class="rounded"> HVAC</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="PLUMBING" class="rounded"> Plumbing</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="ELECTRICAL" class="rounded"> Electrical</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="WINDOWS" class="rounded"> Windows</label>
                        <label class="flex items-center gap-2"><input type="checkbox" name="history" value="KITCHEN" class="rounded"> Kitchen</label>
                    </div>
                </div>

                <div class="pt-4">
                    <button type="submit" class="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-4 rounded-xl transition-all shadow-lg text-lg">
                        Calculate Final Verdict &rarr;
                    </button>
                    <p class="text-center text-xs text-slate-500 mt-4">
                        By clicking Calculate, you acknowledge this is an algorithmic estimate, not a professional inspection.
                    </p>
                </div>
            </form>
        </div>
    `
)
