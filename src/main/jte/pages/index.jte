@import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping.MetroMasterData
@param MetroMasterData metroData

@template.layout(content = @`
    <div class="max-w-xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-10">
            <h1 class="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">Verdict Engine</h1>
            <p class="text-lg text-slate-600">Neutral, data-driven home repair advice.</p>
        </div>

        <div class="bg-white shadow-xl rounded-2xl overflow-hidden border border-slate-100">
            <div class="bg-slate-900 px-6 py-4">
                <h2 class="text-white font-bold text-lg flex items-center">
                    <span class="bg-blue-500 text-xs font-bold px-2 py-1 rounded mr-3">STEP 1</span>
                    Location & Construction Era
                </h2>
            </div>
            
            <form action="/home-repair/step-2" method="POST" class="p-8 space-y-6">
                <!-- Metro Code Selection -->
                <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">Select Metro Area</label>
                    <p class="text-xs text-slate-500 mb-3">Loads 2026 localized labor & material rates.</p>
                    <div class="relative">
                        <select name="metroCode" required 
                                class="w-full pl-4 pr-10 py-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none text-slate-800 font-medium transition cursor-pointer">
                            <option value="" disabled selected>Choose your location...</option>
                            @if(metroData != null && metroData.getData() != null)
                                @for(var entry : metroData.getData().entrySet())
                                    <option value="${entry.getKey()}">${entry.getKey().replace("_", " ")}</option>
                                @endfor
                            @endif
                        </select>
                        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-slate-500">
                            <svg class="h-4 w-4 fill-current" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/></svg>
                        </div>
                    </div>
                </div>

                <!-- Era Selection -->
                <div>
                    <label class="block text-sm font-bold text-slate-700 mb-2">Construction Era</label>
                    <p class="text-xs text-slate-500 mb-3">Identifies era-specific risks (e.g., Polybutylene, Lead).</p>
                    <div class="relative">
                        <select name="era" required
                                class="w-full pl-4 pr-10 py-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none text-slate-800 font-medium transition cursor-pointer">
                            <option value="" disabled selected>Select build period...</option>
                            <option value="PRE_1950">Pre-1950 (Early Century)</option>
                            <option value="1950_1970">1950-1970 (Mid-Century)</option>
                            <option value="1970_1980">1970-1980 (Energy Crisis)</option>
                            <option value="1980_1995">1980-1995 (Modern Testing)</option>
                            <option value="1995_2010">1995-2010 (Pre-Recession)</option>
                            <option value="2010_PRESENT">2010-Present (High Efficiency)</option>
                        </select>
                         <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-slate-500">
                            <svg class="h-4 w-4 fill-current" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/></svg>
                        </div>
                    </div>
                </div>

                <button type="submit" 
                        class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl shadow-lg hover:shadow-xl transition transform active:scale-95 duration-150 flex justify-center items-center mt-6">
                    CONTINUE
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 ml-2" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd" />
                    </svg>
                </button>
            </form>
        </div>
        
        <div class="mt-8 text-center text-xs text-slate-400">
            &copy; 2026 Verdict Engine. All data localized by market.
        </div>
    </div>
`)
