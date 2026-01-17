@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict
@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.StrategyOption
@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.StrategyType
@import com.livingcostcheck.home_repair.domain.VerdictHistory
@param Verdict verdict
@param VerdictHistory history

@template.layout(content = @`
    <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-slate-100 py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-7xl mx-auto">
            
            @if("INSUFFICIENT_DATA".equals(verdict.getTier()))
                <div class="max-w-3xl mx-auto text-center mt-12 mb-20">
                    <div class="inline-flex justify-center items-center w-20 h-20 bg-slate-100 rounded-full mb-8 shadow-inner border border-slate-200">
                        <svg class="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    </div>
                    <h1 class="text-4xl font-extrabold text-slate-900 tracking-tight mb-4">Insufficient Data for Assessment</h1>
                    <p class="text-xl text-slate-500 mb-10 leading-relaxed font-medium">${verdict.getHeadline()}</p>
                    
                    <!-- Explanation Card -->
                    <div class="bg-amber-50 border border-amber-200 rounded-2xl p-8 text-left mb-10 shadow-sm relative overflow-hidden">
                        <div class="absolute top-0 right-0 p-4 opacity-10">
                            <svg class="w-24 h-24 text-amber-500" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
                        </div>
                        <h3 class="text-amber-900 font-bold mb-4 flex items-center relative z-10">
                            <span class="bg-amber-100 text-amber-800 rounded px-2 py-0.5 text-xs font-black uppercase tracking-wide mr-3 border border-amber-200">Why?</span>
                            We couldn't perform a reliable analysis
                        </h3>
                        <ul class="space-y-3 relative z-10">
                            @for(var reason : verdict.getMustDoExplanation())
                                <li class="flex items-start text-amber-900 text-sm font-medium">
                                    <svg class="w-5 h-5 text-amber-500 mr-2 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                                    ${reason}
                                </li>
                            @endfor
                        </ul>
                    </div>
                    
                    <!-- Transparency Detail -->
                    <div class="bg-white rounded-2xl shadow-lg p-8 border border-slate-100 mb-12 text-left">
                        <h3 class="font-bold text-slate-900 mb-6 text-sm uppercase tracking-widest border-b border-slate-100 pb-2">Analysis Status Diagnostics</h3>
                            @if(verdict.getSkippedStrategies() != null)
                            <div class="space-y-4">
                                @for(var skip : verdict.getSkippedStrategies())
                                    <div class="flex flex-col sm:flex-row sm:items-start p-3 bg-slate-50 rounded-lg border border-slate-100">
                                            <span class="text-[10px] font-black uppercase tracking-widest text-slate-400 w-32 flex-shrink-0 pt-0.5 mb-1 sm:mb-0">${skip.split(":")[0]}</span>
                                            <span class="text-sm text-slate-600 font-mono">${skip.substring(skip.indexOf(":")+1)}</span>
                                    </div>
                                @endfor
                            </div>
                        @else
                            <p class="text-sm text-slate-400 italic">No diagnosis details available.</p>
                        @endif
                    </div>

                    <a href="/home-repair" class="inline-flex items-center bg-slate-900 hover:bg-black text-white font-bold py-4 px-10 rounded-xl shadow-xl hover:shadow-2xl transition-all transform hover:-translate-y-1 active:scale-95 text-sm tracking-wide">
                        <svg class="w-4 h-4 mr-2.5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 15l-3-3m0 0l3-3m-3 3h8M3 12a9 9 0 1118 0 9 9 0 01-18 0z"/></svg>
                        Test Another Property
                    </a>
                </div>
            @else
            <!-- SUCCESS PATH CONTENT -->
            <div class="text-center mb-12">
                <!-- YMYL DISCLAIMER (MANDATORY) -->
                <div class="inline-block bg-slate-100 border border-slate-200 text-slate-500 text-[10px] uppercase font-bold px-3 py-1 rounded mb-6 tracking-widest">
                    This assessment is a financial risk analysis based on construction cost data. <br class="hidden sm:inline"> It is intended to support negotiation, not replace professional inspections.
                </div>
                <br>
                
                @if(verdict.isDealKiller())
                    <!-- DEAL KILLER CRITICAL ALERT -->
                    <div class="inline-block bg-red-600 text-white font-black text-xs px-4 py-1.5 rounded-full mb-6 tracking-widest border border-red-700 shadow-lg uppercase animate-pulse">
                        ⛔ CRITICAL TRANSACTION ALERT
                    </div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-4">Transaction Killer Detected</h1>
                    <div class="bg-red-50 border-2 border-red-500 rounded-2xl p-6 max-w-2xl mx-auto text-left shadow-xl relative overflow-hidden">
                        <div class="absolute top-0 right-0 -mt-2 -mr-2 w-24 h-24 bg-red-200 rounded-full opacity-50 blur-xl"></div>
                        <div class="flex items-start relative z-10">
                            <span class="text-3xl mr-4">🚨</span>
                            <div>
                                <h3 class="text-red-900 font-bold text-lg mb-1">Fatal Defect Identified</h3>
                                <p class="text-red-700 font-bold text-lg">${verdict.getDealKillerMessage()}</p>
                                <p class="text-red-600 text-sm mt-2 font-medium">This issue typically renders a home uninsurable or requires a full gut renovation. Proceed with extreme caution.</p>
                            </div>
                        </div>
                    </div>
                @elseif("LOW_RISK".equals(verdict.getTier()))
                    <div class="inline-block bg-emerald-100 text-emerald-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-emerald-200 uppercase">STATUS: LOW RISK</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Financial Risk Assessment</h1>
                    <p class="text-xl text-emerald-700 font-medium">${verdict.getHeadline()}</p>
                @elseif("CONDITIONAL".equals(verdict.getTier()))
                     <div class="inline-block bg-amber-100 text-amber-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-amber-200 uppercase">STATUS: CONDITIONAL</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Conditional Feasibility</h1>
                    <p class="text-xl text-amber-700 font-medium">${verdict.getHeadline()}</p>
                @else
                     <div class="inline-block bg-red-100 text-red-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-red-200 uppercase">STATUS: HIGH FINANCIAL RISK</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Critical Funding Gap</h1>
                    <p class="text-xl text-red-700 font-medium">${verdict.getHeadline()}</p>
                @endif
            </div>

            <!-- CONTEXT SUMMARY -->
            <div class="bg-white/80 backdrop-blur-sm border border-slate-200 rounded-2xl p-6 mb-12 shadow-lg">
                <div class="flex flex-wrap justify-center gap-x-8 gap-y-4 text-sm text-slate-600">
                    <div><span class="font-bold text-slate-800">Location:</span> ${history.getZipCode() != null ? history.getZipCode() : "Unknown"}</div>
                    <div><span class="font-bold text-slate-800">Era:</span> ${history.getDecade() != null ? history.getDecade().replace("_", "-") : "Unknown"}</div>
                    <div>
                        <span class="font-bold text-slate-800">Budget:</span> 
                        $${history.getBudget() != null && !history.getBudget().equalsIgnoreCase("null") ? String.format("%,.0f", Double.parseDouble(history.getBudget())) : "0"}
                    </div>
                    <div><span class="font-bold text-slate-800">Strategy Goal:</span> ${history.getPurpose() != null ? history.getPurpose() : "General"}</div>
                    
                    <!-- Forensic Validation Badges -->
                    @if(Boolean.TRUE.equals(history.getIsFpePanel()) || Boolean.TRUE.equals(history.getIsPolyB()) || Boolean.TRUE.equals(history.getIsAluminum()) || Boolean.TRUE.equals(history.getIsChineseDrywall()))
                        <div class="flex gap-2">
                             <span class="font-bold text-slate-800">Forensic Checks:</span>
                             @if(Boolean.TRUE.equals(history.getIsFpePanel()))
                                <span class="bg-amber-100 text-amber-800 border border-amber-200 px-2 py-0.5 rounded text-[10px] font-black tracking-tight">FPE PANEL ✅</span>
                             @endif
                             @if(Boolean.TRUE.equals(history.getIsPolyB()))
                                <span class="bg-amber-100 text-amber-800 border border-amber-200 px-2 py-0.5 rounded text-[10px] font-black tracking-tight">POLY-B PIPES ✅</span>
                             @endif
                             @if(Boolean.TRUE.equals(history.getIsAluminum()))
                                <span class="bg-amber-100 text-amber-800 border border-amber-200 px-2 py-0.5 rounded text-[10px] font-black tracking-tight">ALUMINUM WIRING ✅</span>
                             @endif
                             @if(Boolean.TRUE.equals(history.getIsChineseDrywall()))
                                <span class="bg-amber-100 text-amber-800 border border-amber-200 px-2 py-0.5 rounded text-[10px] font-black tracking-tight">CHINESE DRYWALL ✅</span>
                             @endif
                        </div>
                    @endif
                </div>
            </div>

            <!-- YMYL-SAFE COST RANGE SUMMARY (PRIMARY DISPLAY) -->
            <div class="bg-gradient-to-br from-white to-slate-50 rounded-2xl shadow-2xl overflow-hidden mb-12 border-2 border-slate-200">
                <div class="bg-slate-900 text-white px-8 py-5">
                    <h2 class="text-lg font-bold tracking-tight">Estimated Budget Planning Range</h2>
                    <p class="text-sm text-slate-400 mt-1">Based on ${verdict.getItemsAnalyzed()} construction items analyzed</p>
                </div>
                <div class="p-8">
                    <div class="text-center mb-6">
                        <div class="text-6xl font-black text-slate-900 mb-3 tracking-tight">
                            ${verdict.getCostRangeLabel() != null ? verdict.getCostRangeLabel() : "Range not available"}
                        </div>
                        <p class="text-base text-slate-600 font-medium leading-relaxed max-w-2xl mx-auto">
                            This range represents typical 2026 construction costs for 
                            @if("BUYING".equals(history.getPurpose()))
                                code-mandatory safety repairs
                            @elseif("INVESTING".equals(history.getPurpose()))
                                essential loss-prevention work
                            @else
                                functional living standards
                            @endif
                            in your metro area.
                        </p>
                        
                        <!-- DIY TRUST DISCLAIMER -->
                        <div class="mt-4 flex items-center justify-center space-x-2 text-xs text-slate-400 bg-slate-50 inline-block px-4 py-2 rounded-lg border border-slate-100">
                             <svg class="w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                             <span>
                                <strong>DIY Note:</strong> We use market labor rates because that's how the home's value is assessed. 
                                Even if you DIY, the market deducts this professional cost from the home's value.
                             </span>
                        </div>
                    </div>
                    
                    <!-- Expandable Detailed Estimate Section -->
                    <details class="mt-8 bg-amber-50 border border-amber-200 rounded-xl overflow-hidden">
                        <summary class="px-6 py-4 cursor-pointer hover:bg-amber-100 transition-colors flex items-center justify-between group">
                            <div class="flex items-center">
                                <svg class="w-5 h-5 text-amber-600 mr-3 group-open:rotate-90 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
                                <span class="text-sm font-bold text-amber-900 uppercase tracking-wide">View Detailed Estimate Breakdown</span>
                            </div>
                            <span class="text-xs text-amber-700 font-medium">Click to expand</span>
                        </summary>
                        <div class="px-6 py-4 bg-white border-t border-amber-200">
                            <!-- YMYL Disclaimer (MANDATORY) -->
                            <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6">
                                <div class="flex">
                                    <svg class="h-5 w-5 text-yellow-400 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
                                    <div>
                                        <p class="text-sm font-bold text-yellow-800">⚠️ Important: This is a Risk Comparison Tool Only</p>
                                        <p class="text-xs text-yellow-700 mt-1">
                                            This estimate is for budget planning and risk comparison purposes only. 
                                            <strong>Actual costs may vary ±50% or more</strong> after professional inspection. 
                                            Do not make financial decisions based solely on this estimate. 
                                            Always obtain multiple quotes from licensed contractors.
                                        </p>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Detailed Cost Breakdown -->
                            <div class="space-y-4">
                                <div class="flex justify-between items-baseline">
                                    <span class="text-sm font-bold text-slate-700">Estimated Total Cost:</span>
                                    <span class="text-3xl font-black text-slate-900 tracking-tight">
                                        $${verdict.getExactCostEstimate() != null ? String.format("%,.0f", verdict.getExactCostEstimate()) : "N/A"}
                                    </span>
                                </div>
                                <div class="text-xs text-slate-500 space-y-1">
                                    <p>✓ Based on 2026 metro-specific labor rates and material costs</p>
                                    <p>✓ Includes era-based risk adjustments</p>
                                    <p>✓ Accounts for ${verdict.getItemsAnalyzed()} construction line items</p>
                                    <p class="text-amber-600 font-bold">⚠ Subject to site-specific conditions and contractor availability</p>
                                </div>
                            </div>
                        </div>
                    </details>
                </div>
            </div>

            <!-- SINGLE VERDICT STRATEGY CARD (Relationship Based) -->
            @if(verdict.getStrategyOptions() != null && !verdict.getStrategyOptions().isEmpty())
                <div class="mb-16">
                    <h2 class="text-3xl font-bold text-slate-900 text-center mb-8 tracking-tight">
                        @if("BUYING".equals(history.getPurpose()))
                            Deal Killer Analysis (Mandatory Repairs)
                        @elseif("INVESTING".equals(history.getPurpose()))
                             Capital Preservation Baseline (Loss Prevention)
                        @else
                            Recommended Repair Plan (Functional Stability)
                        @endif
                    </h2>
                    
                    <div class="max-w-2xl mx-auto">
                        @for(var option : verdict.getStrategyOptions())
                            <%-- LOGIC: BUYING -> SAFETY_FLIP, OTHERS -> STANDARD_LIVING (for now) --%>
                            @if(("BUYING".equals(history.getPurpose()) && option.getStrategyType() == StrategyType.SAFETY_FLIP) || 
                                (!"BUYING".equals(history.getPurpose()) && option.getStrategyType() == StrategyType.STANDARD_LIVING))
                                
                                <div class="bg-white rounded-2xl shadow-xl overflow-hidden border-2 @if("BUYING".equals(history.getPurpose())) border-red-500 @else border-blue-500 @endif hover:shadow-2xl transition-all duration-300 flex flex-col">
                                    <div class="@if("BUYING".equals(history.getPurpose())) bg-red-600 @else bg-blue-600 @endif px-6 py-4">
                                        <div class="text-[10px] font-black tracking-widest uppercase mb-1 @if("BUYING".equals(history.getPurpose())) text-red-200 @else text-blue-200 @endif">
                                            @if("BUYING".equals(history.getPurpose())) DEAL KILLER SCOPE @else PRIMARY STRATEGY @endif
                                        </div>
                                        <h3 class="text-2xl font-extrabold text-white">${option.getName()}</h3>
                                    </div>
                                    <div class="p-8 flex-grow">
                                        <p class="text-lg text-slate-600 mb-6 leading-relaxed font-medium">${option.getDescription()}</p>
                                        
                                        <div class="grid grid-cols-2 gap-6 mb-8">
                                            <div>
                                                <div class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Primary Goal</div>
                                                <p class="text-base text-slate-800 font-bold">${option.getGoal()}</p>
                                            </div>
                                            <div>
                                                <div class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Scope</div>
                                                <p class="text-base text-slate-800 font-bold">${option.getMaterialGrade()}</p>
                                            </div>
                                        </div>

                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <div class="pt-6 border-t border-slate-100">
                                                <div class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-4">What's Included</div>
                                                <ul class="space-y-3">
                                                    @for(var highlight : option.getKeyHighlights())
                                                        <li class="text-sm text-slate-700 flex items-start">
                                                            <svg class="w-5 h-5 @if("BUYING".equals(history.getPurpose())) text-red-500 @else text-blue-500 @endif mr-3 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                            <span class="font-medium">${highlight}</span>
                                                        </li>
                                                    @endfor
                                                </ul>
                                            </div>
                                        @endif
                                    </div>
                                    <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 text-xs text-slate-500 font-bold uppercase tracking-widest text-center">
                                        Total Items: ${option.getPlan().getMustDo().size()} Critical / ${option.getPlan().getShouldDo().size()} Recommended
                                    </div>
                                </div>
                            @endif
                        @endfor
                    </div>
                </div>
            @endif

            <!-- NEGOTIATION LEVERAGE (If exists in any strategy) -->
            @if(verdict.getStrategyOptions() != null && verdict.getStrategyOptions().stream().anyMatch(opt -> opt.getNegotiationCopy() != null && !opt.getNegotiationCopy().isEmpty()))
                <div class="bg-gradient-to-r from-slate-900 to-slate-800 rounded-2xl shadow-2xl overflow-hidden mb-12 border border-slate-700">
                    <div class="px-6 py-4 border-b border-slate-700 flex items-center justify-between">
                        <div class="flex items-center">
                            <svg class="w-6 h-6 text-emerald-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                            <h2 class="text-white font-bold tracking-widest uppercase text-sm">Negotiation Leverage Script</h2>
                        </div>
                        @for(var option : verdict.getStrategyOptions())
                            @if(option.getStrategyType() == StrategyType.SAFETY_FLIP && option.getNegotiationLeverage() > 0)
                                <div class="bg-emerald-500/10 text-emerald-400 text-[10px] font-black px-3 py-1.5 rounded border border-emerald-500/20 tracking-widest">
                                    ESTIMATED LEVERAGE VALUE: $${String.format("%,.0f", option.getNegotiationLeverage())}
                                </div>
                            @endif
                        @endfor
                    </div>
                    <div class="p-8">
                        @for(var option : verdict.getStrategyOptions())
                            @if(option.getStrategyType() == StrategyType.SAFETY_FLIP && option.getNegotiationCopy() != null && !option.getNegotiationCopy().isEmpty())
                                <div class="bg-slate-950/50 rounded-xl p-6 shadow-inner border border-slate-700/50">
                                    <pre class="text-sm text-slate-300 whitespace-pre-wrap font-mono leading-relaxed">${option.getNegotiationCopy()}</pre>
                                </div>
                                <div class="mt-6 flex justify-center">
                                    <button onclick="const btn = this; navigator.clipboard.writeText(this.parentElement.previousElementSibling.innerText).then(() => { btn.innerText = '✅ Copied to Clipboard'; setTimeout(() => btn.innerText = '📋 Copy Professional Script', 2000); })" 
                                            class="bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-3 px-10 rounded-xl shadow-lg transition-all transform active:scale-95 text-sm tracking-wide">
                                        📋 Copy Professional Script
                                    </button>
                                </div>
                            @endif
                        @endfor
                    </div>
                </div>
            @endif

            <!-- EXCLUSION NOTICES (Transparency) -->
            @if(verdict.getExclusionNote() != null && !verdict.getExclusionNote().isEmpty())
                <div class="bg-blue-50 border border-blue-100 rounded-2xl p-6 mb-12 flex items-start">
                    <div class="bg-blue-100 rounded-full p-2 mr-4 flex-shrink-0">
                        <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                    </div>
                    <div>
                        <h3 class="text-blue-900 font-bold text-sm uppercase tracking-wide mb-2">Scope Adjustments based on History</h3>
                        <ul class="space-y-1">
                            @for(var note : verdict.getExclusionNote())
                                <li class="text-blue-700 text-sm font-medium flex items-center">
                                    <span class="w-1.5 h-1.5 bg-blue-400 rounded-full mr-2"></span>
                                    ${note}
                                </li>
                            @endfor
                        </ul>
                    </div>
                </div>
            @endif

            <!-- DETAILED BREAKDOWN (Expandable/Accordion style) - Using STANDARD tier as default -->
            @if(verdict.getPlan() != null)
                <div class="bg-white rounded-2xl shadow-xl overflow-hidden mb-12 border border-slate-200">
                    <div class="bg-slate-900 text-white px-8 py-5 flex items-center justify-between">
                        <h2 class="text-lg font-bold tracking-tight">Forensic Itemized Breakdown <span class="text-slate-500 text-sm font-normal ml-2">(Standard Tier)</span></h2>
                        <div class="text-[10px] font-black tracking-widest text-slate-400 uppercase">Audit Version 2026.1.2</div>
                    </div>
                    
                    <!-- Must Do Items (Split for LIVING Phase 1/2, Combined for others) -->
                    @if(verdict.getPlan().getMustDo() != null && !verdict.getPlan().getMustDo().isEmpty())
                        <div class="p-8 border-b border-slate-100">
                            <div class="flex items-center justify-between mb-6">
                                <h3 class="text-sm font-black text-slate-900 uppercase tracking-widest flex items-center">
                                    <span class="w-2 h-6 bg-red-600 mr-3 rounded-full"></span>
                                    @if("LIVING".equals(history.getPurpose()) || history.getPurpose() == null)
                                        Phase 1: Immediate Safety & Critical Repairs (Now)
                                    @else
                                        Critical & Mandatory Repairs
                                    @endif
                                </h3>
                                <span class="bg-red-50 text-red-700 text-[10px] font-black px-2 py-1 rounded border border-red-100 uppercase">High Priority</span>
                            </div>
                            
                            <div class="grid lg:grid-cols-2 gap-6">
                                @for(var item : verdict.getPlan().getMustDo())
                                    <%-- FOR LIVING MODE: Only show Critical/Safety in Phase 1 --%>
                                    @if(!"LIVING".equals(history.getPurpose()) && history.getPurpose() != null || (item.getCategory().contains("SAFETY") || item.isMandatory() || item.getRiskFlags().stream().anyMatch(f -> f.contains("CRITICAL"))))
                                        <div class="bg-white rounded-xl p-6 border border-slate-100 shadow-sm hover:border-slate-300 transition-colors relative">
                                            <div class="flex justify-between items-start mb-3">
                                                <div class="flex flex-col gap-1">
                                                    <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none">${item.getCategory()}</span>
                                                    <h4 class="font-bold text-slate-900 text-lg leading-tight mt-1">${item.getPrettyName()}</h4>
                                                </div>
                                                <div class="text-right">
                                                    <div class="text-xl font-black text-slate-900 tracking-tighter">$${String.format("%,.0f", item.getAdjustedCost())}</div>
                                                </div>
                                            </div>
                                            
                                            @if(item.getExplanation() != null && !item.getExplanation().isEmpty())
                                                <p class="text-xs text-slate-500 mb-4 leading-relaxed">${item.getExplanation()}</p>
                                            @endif
                                            
                                            <!-- Risk Flags & Badges -->
                                            <div class="flex flex-wrap gap-2 mt-auto">
                                                @if(item.isMandatory())
                                                    <span class="bg-red-100 text-red-800 text-[9px] font-bold px-1.5 py-0.5 rounded tracking-tighter uppercase">Code Mandatory</span>
                                                @endif
                                                
                                                @if(item.getRiskFlags() != null)
                                                    @for(var flag : item.getRiskFlags())
                                                        @if(flag.contains("FORENSIC_CONFIRMATION"))
                                                            <span class="bg-amber-100 text-amber-800 border border-amber-200 text-[9px] font-black px-1.5 py-0.5 rounded tracking-tight shadow-sm">⚠️ FORENSIC CONFIRMED</span>
                                                        @elseif(flag.contains("ERA_RISK"))
                                                            <span class="bg-slate-100 text-slate-700 border border-slate-200 text-[9px] font-black px-1.5 py-0.5 rounded tracking-tight">HISTORICAL RISK</span>
                                                        @endif
                                                    @endfor
                                                @endif

                                                @if(item.getCompoundingBadge() != null)
                                                    <span class="bg-blue-50 text-blue-700 border border-blue-100 text-[9px] font-black px-1.5 py-0.5 rounded tracking-tight">
                                                        🔄 MULTIPLIER: ${item.getCompoundingBadge()}
                                                    </span>
                                                @endif
                                            </div>
                                        </div>
                                    @endif
                                @endfor
                            </div>
                        </div>

                        <!-- PHASE 2: SYSTEM STABILITY (LIVING ONLY - Non-Critical Must Dos) -->
                        @if("LIVING".equals(history.getPurpose()) || history.getPurpose() == null)
                             <div class="p-8 border-b border-slate-100 bg-slate-50/30">
                                <div class="flex items-center justify-between mb-6">
                                    <h3 class="text-sm font-black text-slate-800 uppercase tracking-widest flex items-center">
                                        <span class="w-2 h-6 bg-blue-500 mr-3 rounded-full"></span>
                                        Phase 2: System Stability & Function (Next)
                                    </h3>
                                    <span class="bg-blue-50 text-blue-700 text-[10px] font-black px-2 py-1 rounded border border-blue-100 uppercase">Medium Priority</span>
                                </div>
                                <div class="grid lg:grid-cols-2 gap-6">
                                    @for(var item : verdict.getPlan().getMustDo())
                                        <%-- LOGIC: Items NOT Safety/Critical --%>
                                        @if(!(item.getCategory().contains("SAFETY") || item.isMandatory() || item.getRiskFlags().stream().anyMatch(f -> f.contains("CRITICAL"))))
                                             <div class="bg-white rounded-xl p-6 border border-slate-100 shadow-sm hover:border-slate-300 transition-colors relative">
                                                <div class="flex justify-between items-start mb-3">
                                                    <div class="flex flex-col gap-1">
                                                        <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none">${item.getCategory()}</span>
                                                        <h4 class="font-bold text-slate-800 text-lg leading-tight mt-1">${item.getPrettyName()}</h4>
                                                    </div>
                                                    <div class="text-right">
                                                        <div class="text-xl font-black text-slate-800 tracking-tighter">$${String.format("%,.0f", item.getAdjustedCost())}</div>
                                                    </div>
                                                </div>
                                                @if(item.getExplanation() != null && !item.getExplanation().isEmpty())
                                                    <p class="text-xs text-slate-500 mb-4 leading-relaxed">${item.getExplanation()}</p>
                                                @endif
                                            </div>
                                        @endif
                                    @endfor
                                </div>
                             </div>
                        @endif
                    @endif

                    <!-- Should Do Items (Asset / Cosmetic) -->
                    @if(verdict.getPlan().getShouldDo() != null && !verdict.getPlan().getShouldDo().isEmpty())
                        @if("HIGH_FINANCIAL_RISK".equals(verdict.getTier()) || "CONDITIONAL".equals(verdict.getTier()) || "BUYING".equals(history.getPurpose()))
                            <!-- BLOCKED VIEW: SAFETY FIRST PROTOCOL -->
                            <div class="p-8 bg-slate-100 border-t border-slate-200 relative overflow-hidden group select-none grayscale-[0.8]">
                                <div class="absolute inset-0 bg-slate-200/40 z-10 flex items-center justify-center backdrop-blur-[2px]">
                                    <div class="bg-white px-6 py-4 rounded-xl shadow-2xl border border-red-200 flex items-center transform group-hover:scale-105 transition-transform z-20">
                                        <span class="text-3xl mr-4">🔒</span>
                                        <div>
                                            <h4 class="font-bold text-slate-900">Optional Upgrades Locked</h4>
                                            <p class="text-xs text-red-600 font-bold">Resolve Safety Risks to Unlock</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="flex items-center justify-center mb-6 opacity-40 blur-[1px]">
                                    <h3 class="text-sm font-black text-slate-900 uppercase tracking-widest flex items-center">
                                        Recommended Asset Improvements
                                    </h3>
                                </div>
                                
                                <div class="grid lg:grid-cols-2 gap-4 opacity-40 blur-[1px]">
                                    @for(var item : verdict.getPlan().getShouldDo())
                                        <div class="bg-white rounded-lg p-5 border border-slate-100 flex items-center justify-between">
                                            <div class="flex flex-col">
                                                <span class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">${item.getCategory()}</span>
                                                <h4 class="font-bold text-slate-700 text-sm mt-0.5">${item.getPrettyName()}</h4>
                                            </div>
                                            <div class="text-base font-black text-slate-900 tracking-tight">$${String.format("%,.0f", item.getAdjustedCost())}</div>
                                        </div>
                                    @endfor
                                </div>
                            </div>
                        @else
                            <!-- NORMAL VIEW: UNLOCKED -->
                            <div class="p-8 bg-slate-50/50">
                                <div class="flex items-center justify-between mb-6">
                                    <h3 class="text-sm font-black text-slate-900 uppercase tracking-widest flex items-center">
                                        <span class="w-2 h-6 bg-blue-500 mr-3 rounded-full"></span>
                                        @if("LIVING".equals(history.getPurpose()) || history.getPurpose() == null)
                                            Phase 3: Future Upgrades (Later)
                                        @else
                                            Recommended Asset Improvements
                                        @endif
                                    </h3>
                                    <span class="text-slate-400 text-[10px] font-bold uppercase tracking-widest italic">Optional but advised</span>
                                </div>
                                
                                <div class="grid lg:grid-cols-2 gap-4">
                                    @for(var item : verdict.getPlan().getShouldDo())
                                        <div class="bg-white rounded-lg p-5 border border-slate-100 flex items-center justify-between group hover:border-blue-200 transition-colors">
                                            <div class="flex flex-col">
                                                <span class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">${item.getCategory()}</span>
                                                <h4 class="font-bold text-slate-700 text-sm mt-0.5 group-hover:text-blue-600 transition-colors">${item.getPrettyName()}</h4>
                                            </div>
                                            <div class="text-base font-black text-slate-900 tracking-tight">$${String.format("%,.0f", item.getAdjustedCost())}</div>
                                        </div>
                                    @endfor
                                </div>
                            </div>
                        @endif
                    @endif
                </div>
            @endif

            <!-- METHODOLOGY & STRATEGY TRANSPARENCY (New Phase 3) -->
            <div class="max-w-4xl mx-auto mb-16">
                 <div class="flex items-center justify-center mb-6">
                    <span class="h-px w-12 bg-slate-200"></span>
                    <span class="text-xs text-slate-400 font-bold uppercase tracking-widest mx-4">Analysis Methodology</span>
                    <span class="h-px w-12 bg-slate-200"></span>
                </div>
                
                <div class="bg-white rounded-xl border border-slate-200 p-6 flex flex-col md:flex-row items-start md:items-center justify-between shadow-sm">
                    <div class="mb-4 md:mb-0">
                        <div class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-1">Active Strategy Engine</div>
                        <div class="font-bold text-slate-800 flex items-center">
                            @if(verdict.getStrategyUsed() != null)
                                ${verdict.getStrategyUsed()}
                            @else
                                STANDARD_LIVING
                            @endif
                            
                            <!-- Status Badge -->
                             @if(verdict.getSkippedStrategies() != null && !verdict.getSkippedStrategies().isEmpty())
                                <span class="ml-3 bg-amber-100 text-amber-800 text-[9px] px-1.5 py-0.5 rounded font-black tracking-tight border border-amber-200">ADAPTIVE MODE</span>
                             @else
                                <span class="ml-3 bg-emerald-100 text-emerald-800 text-[9px] px-1.5 py-0.5 rounded font-black tracking-tight border border-emerald-200">OPTIMAL DATA</span>
                             @endif
                        </div>
                    </div>
                    
                    @if(verdict.getStrategyExplanation() != null)
                        <div class="md:max-w-md text-xs text-slate-500 leading-relaxed md:text-right border-l md:border-l-0 border-slate-100 pl-4 md:pl-0">
                            ${verdict.getStrategyExplanation()}
                        </div>
                    @endif
                </div>
            </div>

            <!-- CTA -->
            <div class="text-center pb-20">
                <a href="/home-repair" class="inline-flex items-center bg-slate-900 hover:bg-black text-white font-bold py-4 px-12 rounded-2xl shadow-xl hover:shadow-2xl transition-all transform hover:-translate-y-1 active:scale-95">
                    <svg class="w-5 h-5 mr-3 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 15l-3-3m0 0l3-3m-3 3h8M3 12a9 9 0 1118 0 9 9 0 01-18 0z"/></svg>
                    Start New Forensic Audit
                </a>
                <p class="mt-6 text-xs text-slate-400 font-medium">Data authorized by 2026 RSMeans & Integrated Cost Library v2.0</p>
            </div>
            
            @endif <!-- End of INSUFFICIENT_DATA check -->

        </div>
    </div>
`)
