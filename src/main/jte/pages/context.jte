@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.ContextBriefing
@param String metroCode
@param String relationship
@param String era
@param ContextBriefing contextBriefing

@template.layout(content = @`
    <div class="max-w-3xl mx-auto py-12 px-6 lg:px-8">
        
        <%-- HEADER --%>
        <div class="text-center mb-10">
            <h1 class="text-3xl md:text-4xl font-bold tracking-tight text-slate-900 mb-3">Project Context.</h1>
            <p class="text-lg text-slate-500 max-w-2xl mx-auto">
                We've detected distinct localized risk factors for this property. Refine the analysis below.
            </p>
        </div>

        <%-- CONTEXT BRIEFING (Data Card) --%>
        <div class="bg-white rounded-[24px] shadow-sm border border-slate-200 p-6 mb-8 flex flex-col md:flex-row gap-6 items-start md:items-center justify-between transition-all hover:shadow-md">
             <div class="flex items-center gap-4">
                 <div class="w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center text-blue-600 text-xl">
                    📊
                 </div>
                 <div>
                     <h3 class="font-bold text-slate-900 text-sm">Detected Risk Profile</h3>
                     <div class="flex items-center gap-2 mt-1">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-800">
                            Risk: ${contextBriefing.getRegionalRisk()}
                        </span>
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-800">
                            Labor: ${contextBriefing.getLaborMarketRate()}
                        </span>
                     </div>
                 </div>
             </div>
             <div class="text-xs text-slate-500 max-w-sm text-left md:text-right">
                "${contextBriefing.getEraFeatureReason()}"
             </div>
        </div>

        <div class="bg-white rounded-[32px] shadow-xl shadow-slate-200/50 overflow-hidden border border-slate-100 relative">
            
            <!-- LOADING OVERLAY -->
            <div id="processingOverlay" class="hidden absolute inset-0 bg-white/80 backdrop-blur-md z-50 flex flex-col items-center justify-center text-center p-8 transition-opacity duration-500">
                <div class="w-16 h-16 border-4 border-blue-500/30 border-t-blue-500 rounded-full animate-spin mb-6"></div>
                <h3 id="loadingText" class="text-2xl font-bold text-slate-900 tracking-tight mb-2">Analyzing...</h3>
                <p class="text-slate-500 font-medium">Processing 200+ localized data points.</p>
            </div>
            
            <form id="contextForm" action="/home-repair/verdict" method="POST" class="p-8 md:p-10 space-y-10">
                <!-- Hidden Inputs from Step 1 -->
                <input type="hidden" name="metroCode" value="${metroCode}">
                <input type="hidden" name="era" value="${era}">
                <input type="hidden" name="relationship" value="${relationship}">
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                    <!-- Budget -->
                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Total Capital Budget</label>
                        <div class="relative group">
                            <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-medium">$</span>
                            <input type="number" name="budget" required min="0" value="0"
                                   class="w-full pl-8 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl font-semibold text-slate-900 focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
                                   placeholder="0 (No Limit)">
                        </div>
                        <p class="text-[10px] text-slate-400 mt-2 pl-1">Set to 0 for unlimited scope.</p>
                    </div>

                    <!-- Sqft -->
                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Property Size (Sq Ft)</label>
                        <input type="number" name="sqft" min="100" step="10"
                               class="w-full px-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl font-semibold text-slate-900 focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
                               placeholder="Avg for area">
                        <p class="text-[10px] text-slate-400 mt-2 pl-1">Optional. Defaults to regional average.</p>
                    </div>
                </div>

                <!-- History Grid -->
                <div>
                    @if("LIVING".equals(relationship) || "INVESTING".equals(relationship))
                        <div class="mb-8">
                            <h3 class="text-lg font-bold text-slate-900 mb-1">Verify Updates</h3>
                            <p class="text-sm text-slate-500 mb-6">Select items that have been verified as <span class="text-slate-900 font-semibold">100% replaced</span> in the last 10 years.</p>
                            
                            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                                <%-- Helper for Checkbox Card --%>
                                <label class="cursor-pointer group relative">
                                    <input type="checkbox" name="history" value="ROOFING" class="peer sr-only">
                                    <div class="h-full p-4 rounded-2xl border-2 border-slate-100 bg-slate-50 text-slate-600 transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-700 hover:border-slate-200 flex flex-col items-center justify-center text-center gap-2">
                                        <span class="text-2xl opacity-50 group-hover:scale-110 transition-transform">🏠</span>
                                        <span class="text-xs font-bold uppercase tracking-wide">Roof</span>
                                    </div>
                                    <div class="absolute top-2 right-2 w-4 h-4 bg-blue-500 rounded-full text-white flex items-center justify-center opacity-0 peer-checked:opacity-100 transition-all scale-50 peer-checked:scale-100">
                                        <svg class="w-2.5 h-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M5 13l4 4L19 7"></path></svg>
                                    </div>
                                </label>

                                <label class="cursor-pointer group relative">
                                    <input type="checkbox" name="history" value="HVAC" class="peer sr-only">
                                    <div class="h-full p-4 rounded-2xl border-2 border-slate-100 bg-slate-50 text-slate-600 transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-700 hover:border-slate-200 flex flex-col items-center justify-center text-center gap-2">
                                        <span class="text-2xl opacity-50 group-hover:scale-110 transition-transform">❄️</span>
                                        <span class="text-xs font-bold uppercase tracking-wide">HVAC</span>
                                    </div>
                                    <div class="absolute top-2 right-2 w-4 h-4 bg-blue-500 rounded-full text-white flex items-center justify-center opacity-0 peer-checked:opacity-100 transition-all scale-50 peer-checked:scale-100">
                                        <svg class="w-2.5 h-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M5 13l4 4L19 7"></path></svg>
                                    </div>
                                </label>

                                <label class="cursor-pointer group relative">
                                    <input type="checkbox" name="history" value="PLUMBING" class="peer sr-only">
                                    <div class="h-full p-4 rounded-2xl border-2 border-slate-100 bg-slate-50 text-slate-600 transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-700 hover:border-slate-200 flex flex-col items-center justify-center text-center gap-2">
                                        <span class="text-2xl opacity-50 group-hover:scale-110 transition-transform">💧</span>
                                        <span class="text-xs font-bold uppercase tracking-wide">Plumbing</span>
                                    </div>
                                    <div class="absolute top-2 right-2 w-4 h-4 bg-blue-500 rounded-full text-white flex items-center justify-center opacity-0 peer-checked:opacity-100 transition-all scale-50 peer-checked:scale-100">
                                        <svg class="w-2.5 h-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M5 13l4 4L19 7"></path></svg>
                                    </div>
                                </label>

                                <label class="cursor-pointer group relative">
                                    <input type="checkbox" name="history" value="ELEC_PANEL" class="peer sr-only">
                                    <div class="h-full p-4 rounded-2xl border-2 border-slate-100 bg-slate-50 text-slate-600 transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-700 hover:border-slate-200 flex flex-col items-center justify-center text-center gap-2">
                                        <span class="text-2xl opacity-50 group-hover:scale-110 transition-transform">⚡</span>
                                        <span class="text-xs font-bold uppercase tracking-wide">Electric</span>
                                    </div>
                                    <div class="absolute top-2 right-2 w-4 h-4 bg-blue-500 rounded-full text-white flex items-center justify-center opacity-0 peer-checked:opacity-100 transition-all scale-50 peer-checked:scale-100">
                                        <svg class="w-2.5 h-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M5 13l4 4L19 7"></path></svg>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <div class="p-6 bg-slate-50 rounded-2xl border border-slate-100">
                            <h4 class="text-sm font-bold text-slate-700 mb-4 uppercase tracking-wide">Cosmetic/Comfort Updates</h4>
                            <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                                <label class="flex items-center gap-3 p-3 bg-white rounded-xl border border-slate-200 cursor-pointer hover:border-blue-400 transition-colors">
                                    <input type="checkbox" name="history" value="KITCHEN_REMODEL" class="w-4 h-4 rounded text-blue-600 focus:ring-blue-500">
                                    <span class="text-sm font-medium text-slate-700">Kitchen</span>
                                </label>
                                <label class="flex items-center gap-3 p-3 bg-white rounded-xl border border-slate-200 cursor-pointer hover:border-blue-400 transition-colors">
                                    <input type="checkbox" name="history" value="BATH_REMODEL" class="w-4 h-4 rounded text-blue-600 focus:ring-blue-500">
                                    <span class="text-sm font-medium text-slate-700">Bath</span>
                                </label>
                                <label class="flex items-center gap-3 p-3 bg-white rounded-xl border border-slate-200 cursor-pointer hover:border-blue-400 transition-colors">
                                    <input type="checkbox" name="history" value="FLOORING" class="w-4 h-4 rounded text-blue-600 focus:ring-blue-500">
                                    <span class="text-sm font-medium text-slate-700">Floors</span>
                                </label>
                                <label class="flex items-center gap-3 p-3 bg-white rounded-xl border border-slate-200 cursor-pointer hover:border-blue-400 transition-colors">
                                    <input type="checkbox" name="history" value="WINDOWS" class="w-4 h-4 rounded text-blue-600 focus:ring-blue-500">
                                    <span class="text-sm font-medium text-slate-700">Windows</span>
                                </label>
                            </div>
                        </div>
                    @else
                        <!-- BUYING MODE -->
                        <div class="bg-blue-50 border border-blue-100 rounded-2xl p-6 flex flex-col md:flex-row gap-4 items-center text-center md:text-left">
                            <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 text-xl flex-shrink-0">
                                🛡️
                            </div>
                            <div>
                                <h4 class="font-bold text-blue-900">Purchase Protection Mode Active</h4>
                                <p class="text-sm text-blue-700 mt-1 leading-relaxed">
                                    We assume <span class="font-bold">zero prior maintenance</span> to give you the most conservative, worst-case leverage for negotiation. All systems will be analyzed.
                                </p>
                            </div>
                        </div>
                    @endif
                </div>

                <!-- FORENSIC QUESTIONS (Refined) -->
                <div id="forensicSection" class="hidden">
                    <div class="border-t border-slate-100 pt-8 mt-8">
                        <div class="flex items-center gap-3 mb-6">
                            <span class="w-2 h-8 bg-amber-400 rounded-full"></span>
                            <h3 class="text-lg font-bold text-slate-900">Forensic Checks</h3>
                        </div>
                        
                        <div class="space-y-4">
                            <!-- Template for Forensic Question -->
                            <div id="fpeQuestion" class="hidden relative group cursor-pointer">
                                <label class="flex items-start gap-4 p-5 rounded-2xl bg-amber-50/50 border border-amber-100 hover:border-amber-300 transition-all">
                                    <input type="checkbox" name="isFpePanel" value="true" class="mt-1 w-5 h-5 text-amber-600 rounded focus:ring-amber-500">
                                    <div>
                                        <p class="font-bold text-slate-800 text-sm">Review Electrical Panel</p>
                                        <p class="text-xs text-slate-600 mt-1">Do breakers have <strong class="text-slate-900">red tips</strong> or "Stab-Lok" branding?</p>
                                    </div>
                                </label>
                            </div>

                            <div id="polyQuestion" class="hidden relative group cursor-pointer">
                                <label class="flex items-start gap-4 p-5 rounded-2xl bg-amber-50/50 border border-amber-100 hover:border-amber-300 transition-all">
                                    <input type="checkbox" name="isPolyB" value="true" class="mt-1 w-5 h-5 text-amber-600 rounded focus:ring-amber-500">
                                    <div>
                                        <p class="font-bold text-slate-800 text-sm">Check Under Sink</p>
                                        <p class="text-xs text-slate-600 mt-1">Are supply pipes <strong class="text-slate-900">grey flexible plastic</strong> (instead of copper/white)?</p>
                                    </div>
                                </label>
                            </div>

                            <div id="aluminumQuestion" class="hidden relative group cursor-pointer">
                                <label class="flex items-start gap-4 p-5 rounded-2xl bg-amber-50/50 border border-amber-100 hover:border-amber-300 transition-all">
                                    <input type="checkbox" name="isAluminum" value="true" class="mt-1 w-5 h-5 text-amber-600 rounded focus:ring-amber-500">
                                    <div>
                                        <p class="font-bold text-slate-800 text-sm">Inspect Wiring</p>
                                        <p class="text-xs text-slate-600 mt-1">Do outlet wires look <strong class="text-slate-900">silver/aluminum</strong> instead of copper?</p>
                                    </div>
                                </label>
                            </div>

                            <div id="drywallQuestion" class="hidden relative group cursor-pointer">
                                <label class="flex items-start gap-4 p-5 rounded-2xl bg-amber-50/50 border border-amber-100 hover:border-amber-300 transition-all">
                                    <input type="checkbox" name="isChineseDrywall" value="true" class="mt-1 w-5 h-5 text-amber-600 rounded focus:ring-amber-500">
                                    <div>
                                        <p class="font-bold text-slate-800 text-sm">Smell Check</p>
                                        <p class="text-xs text-slate-600 mt-1">Is there a persistent <strong class="text-slate-900">rotten egg / sulfur odor</strong>?</p>
                                    </div>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pt-6">
                    <button type="submit" 
                            class="w-full bg-slate-900 text-white font-bold py-5 rounded-2xl text-lg shadow-xl shadow-slate-200 hover:shadow-2xl hover:bg-black hover:scale-[1.01] transition-all duration-300 active:scale-[0.98]">
                        Run Analysis Engine
                    </button>
                    <p class="text-xs text-center text-slate-400 mt-4 font-medium">
                        Secure SSL Connection • 256-bit Encryption
                    </p>
                </div>
            </form>
        </div>
    </div>
        </div>
    </div>

    <script>
        document.getElementById('contextForm').addEventListener('submit', function(e) {
            // e.preventDefault(); // Un-comment to test animation without submit
            const overlay = document.getElementById('processingOverlay');
            const textElement = document.getElementById('loadingText');
            
            overlay.classList.remove('hidden');
            
            const messages = [
                "Mapping 2026 localized labor rates...",
                "Scanning era-specific risks...",
                "Optimizing budget based on repair history...",
                "Finalizing Verdict..."
            ];
            
            let i = 0;
            setInterval(() => {
                if (i < messages.length) {
                    textElement.textContent = messages[i];
                    i++;
                }
            }, 800); // Change text every 800ms
        });

        // Phase 4: Show Forensic Questions based on Era
        (function() {
            const era = document.querySelector('input[name="era"]').value;
            const forensicSection = document.getElementById('forensicSection');
            const fpeQ = document.getElementById('fpeQuestion');
            const polyQ = document.getElementById('polyQuestion');
            const alQ = document.getElementById('aluminumQuestion');
            const drywallQ = document.getElementById('drywallQuestion');
            
            let showSection = false;
            
            // Federal Pacific Panels (1950-1990)
            if (era === '1950_1970' || era === '1970_1980' || era === '1980_1995') {
                fpeQ.classList.remove('hidden');
                showSection = true;
            }
            
            // Polybutylene Plumbing (1978-1995)
            if (era === '1970_1980' || era === '1980_1995') {
                polyQ.classList.remove('hidden');
                showSection = true;
            }
            
            // Aluminum Wiring (1965-1975)
            if (era === '1950_1970' || era === '1970_1980') {
                alQ.classList.remove('hidden');
                showSection = true;
            }
            
            // Chinese Drywall (2001-2009)
            if (era === '1995_2010') {
                drywallQ.classList.remove('hidden');
                showSection = true;
            }
            
            // Show section if any questions are applicable
            if (showSection) {
                forensicSection.classList.remove('hidden');
            }
        })();
    </script>
`)
