@import com.livingcostcheck.home_repair.domain.VerdictHistory
@import com.livingcostcheck.home_repair.service.dto.VerdictResult
@import com.livingcostcheck.home_repair.service.VerdictService

@param VerdictHistory history
@param VerdictResult verdict
@param VerdictService helper

@template.layout(content = @`
    <div class="bg-gray-50 min-h-screen pb-12">
        <!-- HEADER / VERDICT BOX -->
        <div class="bg-gray-900 pt-8 pb-12 px-4 shadow-xl">
            <div class="max-w-3xl mx-auto text-center space-y-4">
                <span class="inline-block px-4 py-1 bg-red-600 text-white text-xs font-black uppercase tracking-widest rounded animate-pulse">
                    Binding Verdict
                </span>
                <h1 class="text-4xl md:text-5xl font-black text-white uppercase leading-tight tracking-tight">
                    ${verdict.getTitle()}
                </h1>
                <p class="text-gray-400 font-mono text-sm">Case ID: ${history.getId().toString().substring(0,8)} | Context: ${history.getDecade()} Build</p>
                
                <div class="mt-8 p-6 bg-white rounded-lg shadow-lg border-l-8 border-black text-left">
                     <p class="text-xl font-bold text-gray-900 mb-2">Conclusion:</p>
                     <p class="text-gray-800 leading-relaxed font-medium">
                        ${verdict.getCode().replace("_", " ")} is your ONLY logical path.
                        ${verdict.getRecommendedAction()} is the required action.
                     </p>
                </div>
            </div>
        </div>

        <div class="max-w-3xl mx-auto px-4 -mt-6">
            <!-- AD_SLOT_MIDDLE -->
            <div class="w-full bg-gray-200 h-20 mb-8 rounded flex items-center justify-center text-gray-400 text-xs border border-dashed border-gray-300">
                AD_SLOT_MIDDLE (High CPM)
            </div>

            <!-- DEFENSE SECTION (LOSS AVERSION) -->
            <section class="bg-white rounded-xl shadow-sm border border-gray-200 p-8 mb-8 relative overflow-hidden">
                <div class="absolute top-0 right-0 bg-gray-100 text-gray-500 text-xs font-bold px-3 py-1 rounded-bl">DEFENSE</div>
                <h2 class="text-2xl font-bold text-gray-900 mb-4 flex items-center">
                    <span class="text-red-600 mr-2">☠️</span> The Cost of Inaction
                </h2>
                <p class="text-gray-600 leading-relaxed mb-6">
                    ${verdict.getDefenseText()}
                </p>
                <div class="bg-red-50 p-4 rounded border border-red-100 text-sm text-red-800 font-medium">
                    Warning: Failing to address this could compound costs by 300% within 18 months.
                </div>
                
                <!-- CTA: Expert Lead -->
                <a href="/home-repair/track?verdictId=${history.getId()}&type=AFFILIATE&target=https://example.com/contractors" 
                   target="_blank"
                   class="block w-full mt-6 bg-white border-2 border-red-600 text-red-600 font-bold py-3 px-6 rounded hover:bg-red-50 text-center transition">
                    Find Certified Experts (Prevent Loss) &rarr;
                </a>
            </section>

            <!-- OFFENSE SECTION (ROI) -->
            <section class="bg-white rounded-xl shadow-sm border border-gray-200 p-8 mb-8 relative overflow-hidden">
                <div class="absolute top-0 right-0 bg-blue-100 text-blue-800 text-xs font-bold px-3 py-1 rounded-bl">OFFENSE</div>
                <h2 class="text-2xl font-bold text-gray-900 mb-4 flex items-center">
                    <span class="text-blue-600 mr-2">💎</span> Maximizing Value
                </h2>
                <p class="text-gray-600 leading-relaxed mb-6">
                    ${verdict.getOffenseText()}
                </p>
                
                <!-- CTA: Affiliate Materials -->
                <a href="/home-repair/track?verdictId=${history.getId()}&type=AFFILIATE&target=https://example.com/materials" 
                   target="_blank"
                   class="block w-full mt-6 bg-blue-600 text-white font-bold py-3 px-6 rounded hover:bg-blue-700 shadow-lg text-center transition transform hover:-translate-y-1">
                    Get ${verdict.getRecommendedAction()} Materials &rarr;
                </a>
            </section>

             <!-- EMAIL CAPTURE (ASSET) -->
            <section class="bg-gray-800 rounded-xl shadow-lg p-8 mb-12 text-center text-white">
                <h3 class="text-xl font-bold mb-2">Get Your Local Cost Breakdown</h3>
                <p class="text-gray-400 text-sm mb-6">We have compiled a PDF report for Zip Code <span class="text-white font-mono">${history.getZipCode()}</span>.</p>
                
                <form id="emailForm" class="flex flex-col sm:flex-row gap-3">
                    <input type="email" name="email" required placeholder="Enter your email" 
                           class="flex-1 px-4 py-3 rounded text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <button type="submit" class="bg-blue-500 hover:bg-blue-400 text-white font-bold py-3 px-6 rounded transition">
                        Send Report
                    </button>
                </form>
                <div id="emailSuccess" class="hidden mt-4 text-green-400 font-bold text-sm">
                    ✓ Report Sent! Check your inbox.
                </div>
            </section>

            <!-- PV BOOSTERS (RE-CALCULATE) -->
            <div class="text-center space-y-4">
                <p class="text-gray-500 text-xs uppercase tracking-widest font-bold">Simulate Other Scenarios</p>
                <div class="flex flex-col gap-3">
                     <a href="${helper.getCalculationRedirectUrl(history.getZipCode(), history.getBudget(), history.getDecade(), history.getPurpose(), "boost_budget")}"
                       class="w-full bg-white border border-gray-300 text-gray-600 font-medium py-3 rounded hover:bg-gray-50 text-sm transition">
                        What if I add $5,000 to budget?
                    </a>
                    
                    @if(!"Flip/Sell".equals(history.getPurpose()))
                    <a href="${helper.getCalculationRedirectUrl(history.getZipCode(), history.getBudget(), history.getDecade(), history.getPurpose(), "sell_mode")}"
                       class="w-full bg-white border border-gray-300 text-gray-600 font-medium py-3 rounded hover:bg-gray-50 text-sm transition">
                        What if I sell immediately?
                    </a>
                    @endif
                </div>
            </div>
        </div>

        <!-- Script for AJAX Lead Capture -->
        <script>
            document.getElementById('emailForm').addEventListener('submit', function(e) {
                e.preventDefault();
                const email = this.email.value;
                const verdictId = '${history.getId().toString()}';
                
                fetch('/home-repair/api/lead?verdictId=' + verdictId + '&email=' + encodeURIComponent(email), {
                    method: 'POST'
                })
                .then(response => {
                    if (response.ok) {
                        document.getElementById('emailForm').classList.add('hidden');
                        document.getElementById('emailSuccess').classList.remove('hidden');
                    }
                });
            });
        </script>
    </div>
`)
