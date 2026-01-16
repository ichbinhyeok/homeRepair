@param String metroCode
<%-- Trigger Recompile --%>
@param String era

@template.layout(content = @`
    <div class="max-w-xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        
        <!-- Progress Indicator -->
        <div class="flex items-center justify-center mb-8 space-x-2">
            <div class="h-2 w-12 bg-blue-600 rounded-full"></div>
            <div class="h-2 w-12 bg-blue-600 rounded-full"></div>
            <div class="h-2 w-12 bg-slate-200 rounded-full"></div>
        </div>

        <div class="bg-white shadow-xl rounded-2xl overflow-hidden border border-slate-100 relative">
            
            <!-- TRUST BUILDING OVERLAY (Hidden by default) -->
            <div id="processingOverlay" class="hidden absolute inset-0 bg-white/95 backdrop-blur-sm z-50 flex flex-col items-center justify-center text-center p-8">
                <div class="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600 mb-6"></div>
                <h3 id="loadingText" class="text-xl font-bold text-slate-800 mb-2">Initializing Engine...</h3>
                <p class="text-sm text-slate-500">Please wait while we calculate.</p>
            </div>

            <div class="bg-slate-900 px-6 py-4">
                <h2 class="text-white font-bold text-lg flex items-center">
                    <span class="bg-blue-500 text-xs font-bold px-2 py-1 rounded mr-3">STEP 2</span>
                    Project Context
                </h2>
            </div>
            
            <form id="contextForm" action="/home-repair/verdict" method="POST" class="p-8 space-y-6">
                <!-- Hidden Inputs from Step 1 -->
                <input type="hidden" name="metroCode" value="${metroCode}">
                <input type="hidden" name="era" value="${era}">
                
                <!-- Budget -->
                <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">Total Budget ($)</label>
                    <div class="relative">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <span class="text-slate-500 sm:text-sm">$</span>
                        </div>
                        <input type="number" name="budget" required min="1000"
                               class="w-full pl-8 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                               placeholder="e.g. 10000">
                    </div>
                </div>

                <!-- Purpose -->
                <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">Primary Goal</label>
                    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                        <label class="cursor-pointer">
                            <input type="radio" name="purpose" value="LIVING" class="peer sr-only" required checked>
                            <div class="p-3 rounded-lg border border-slate-200 bg-slate-50 text-center peer-checked:bg-blue-50 peer-checked:border-blue-500 peer-checked:text-blue-700 transition hover:bg-gray-50">
                                <span class="block font-bold text-sm">Living</span>
                                <span class="text-xs text-slate-500">Comfort focus</span>
                            </div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="purpose" value="RESALE" class="peer sr-only">
                            <div class="p-3 rounded-lg border border-slate-200 bg-slate-50 text-center peer-checked:bg-green-50 peer-checked:border-green-500 peer-checked:text-green-700 transition hover:bg-gray-50">
                                <span class="block font-bold text-sm">Resale</span>
                                <span class="text-xs text-slate-500">ROI focus</span>
                            </div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="purpose" value="SAFETY" class="peer sr-only">
                            <div class="p-3 rounded-lg border border-slate-200 bg-slate-50 text-center peer-checked:bg-red-50 peer-checked:border-red-500 peer-checked:text-red-700 transition hover:bg-gray-50">
                                <span class="block font-bold text-sm">Safety</span>
                                <span class="text-xs text-slate-500">Critical only</span>
                            </div>
                        </label>
                </div>
            </div>

                <!-- History (Multi-Select) -->
                <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">Known History</label>
                    <p class="text-xs text-slate-500 mb-3">Select items that have been REPLACED in the last 10 years.</p>
                    <div class="grid grid-cols-2 gap-3">
                        <label class="flex items-center space-x-3 p-3 border border-slate-200 rounded-lg cursor-pointer hover:bg-slate-50 transition">
                            <input type="checkbox" name="history" value="ROOFING" class="h-5 w-5 text-blue-600 rounded border-gray-300 focus:ring-blue-500">
                            <span class="text-sm font-medium text-slate-700">Roofing</span>
                        </label>
                        <label class="flex items-center space-x-3 p-3 border border-slate-200 rounded-lg cursor-pointer hover:bg-slate-50 transition">
                            <input type="checkbox" name="history" value="HVAC" class="h-5 w-5 text-blue-600 rounded border-gray-300 focus:ring-blue-500">
                            <span class="text-sm font-medium text-slate-700">HVAC System</span>
                        </label>
                        <label class="flex items-center space-x-3 p-3 border border-slate-200 rounded-lg cursor-pointer hover:bg-slate-50 transition">
                            <input type="checkbox" name="history" value="PLUMBING" class="h-5 w-5 text-blue-600 rounded border-gray-300 focus:ring-blue-500">
                            <span class="text-sm font-medium text-slate-700">Plumbing</span>
                        </label>
                        <label class="flex items-center space-x-3 p-3 border border-slate-200 rounded-lg cursor-pointer hover:bg-slate-50 transition">
                            <input type="checkbox" name="history" value="ELECTRICAL" class="h-5 w-5 text-blue-600 rounded border-gray-300 focus:ring-blue-500">
                            <span class="text-sm font-medium text-slate-700">Electrical Panel</span>
                        </label>
                    </div>
                </div>

                <!-- Condition -->
                <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">History Condition</label>
                    <select name="condition" class="w-full pl-4 pr-10 py-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition">
                        <option value="NONE" selected>Excellent (Replace logic: Remove Item)</option>
                        <option value="MINOR">Good (Replace logic: Recheck Mode)</option>
                        <option value="POOR">Poor (Replace logic: Full Replace)</option>
                    </select>
                </div>

                <!-- FORENSIC QUESTIONS (Phase 4 - Era-Specific Visual Observations) -->
                <div id="forensicSection" class="hidden bg-amber-50 border-2 border-amber-200 rounded-xl p-6">
                    <div class="flex items-center mb-4">
                        <svg class="w-6 h-6 text-amber-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                        <label class="text-sm font-bold text-amber-900">Quick Visual Check (Optional)</label>
                    </div>
                    <p class="text-xs text-amber-700 mb-4">Answer YES only if you can visually confirm these items. This drastically improves accuracy.</p>
                    
                    <div class="space-y-3">
                        <!-- Federal Pacific Panel (1950-1990) -->
                        <label id="fpeQuestion" class="hidden flex items-start space-x-3 p-3 bg-white border border-amber-200 rounded-lg cursor-pointer hover:bg-amber-50 transition">
                            <input type="checkbox" name="isFpePanel" value="true" class="mt-0.5 h-5 w-5 text-red-600 rounded border-gray-300 focus:ring-red-500">
                            <div class="flex-1">
                                <span class="block text-sm font-semibold text-slate-800">🔴 Electrical Panel Brand</span>
                                <span class="text-xs text-slate-600">Check your breaker box. Do breakers have <strong>red tips</strong> or say "Federal Pacific" / "Stab-Lok"?</span>
                            </div>
                        </label>

                        <!-- Polybutylene (1978-1995) -->
                        <label id="polyQuestion" class="hidden flex items-start space-x-3 p-3 bg-white border border-amber-200 rounded-lg cursor-pointer hover:bg-amber-50 transition">
                            <input type="checkbox" name="isPolyB" value="true" class="mt-0.5 h-5 w-5 text-red-600 rounded border-gray-300 focus:ring-red-500">
                            <div class="flex-1">
                                <span class="block text-sm font-semibold text-slate-800">🔴 Plumbing Pipe Material</span>
                                <span class="text-xs text-slate-600">Look under sinks. Are the water supply pipes <strong>grey plastic</strong> (not white PVC)?</span>
                            </div>
                        </label>

                        <!-- Aluminum Wiring (1965-1975) -->
                        <label id="aluminumQuestion" class="hidden flex items-start space-x-3 p-3 bg-white border border-amber-200 rounded-lg cursor-pointer hover:bg-amber-50 transition">
                            <input type="checkbox" name="isAluminum" value="true" class="mt-0.5 h-5 w-5 text-red-600 rounded border-gray-300 focus:ring-red-500">
                            <div class="flex-1">
                                <span class="block text-sm font-semibold text-slate-800">🔴 Outlet Wiring</span>
                                <span class="text-xs text-slate-600">Remove an outlet cover. Are wire ends <strong>silver/grey</strong> instead of copper?</span>
                            </div>
                        </label>

                        <!-- Chinese Drywall (2001-2009) -->
                        <label id="drywallQuestion" class="hidden flex items-start space-x-3 p-3 bg-white border border-amber-200 rounded-lg cursor-pointer hover:bg-amber-50 transition">
                            <input type="checkbox" name="isChineseDrywall" value="true" class="mt-0.5 h-5 w-5 text-red-600 rounded border-gray-300 focus:ring-red-500">
                            <div class="flex-1">
                                <span class="block text-sm font-semibold text-slate-800">🔴 Sulfur Odor</span>
                                <span class="text-xs text-slate-600">Do you smell <strong>rotten eggs / sulfur</strong> when humid, especially near AC vents?</span>
                            </div>
                        </label>
                    </div>
                </div>

                <div class="pt-4">
                    <button type="submit" 
                            class="w-full bg-slate-900 text-white font-bold py-4 rounded-xl shadow-lg hover:bg-slate-800 transition transform active:scale-95 duration-150">
                        GENERATE VERDICT
                    </button>
                    <p class="text-xs text-center text-slate-400 mt-4">
                        By clicking, you accept that this is an estimation engine, not a formal quote.
                    </p>
                </div>
            </form>
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
