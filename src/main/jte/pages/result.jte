@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict
@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.StrategyOption
@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.StrategyType
@import com.livingcostcheck.home_repair.domain.VerdictHistory
@param Verdict verdict
@param VerdictHistory history

@template.layout(content = @`
    <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-slate-100 py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-7xl mx-auto">
            
            <!-- Header / Verdict Tier -->
            <div class="text-center mb-12">
                <!-- YMYL DISCLAIMER (MANDATORY) -->
                <div class="inline-block bg-slate-100 border border-slate-200 text-slate-500 text-[10px] uppercase font-bold px-3 py-1 rounded mb-6 tracking-widest">
                    This assessment is a financial risk analysis based on construction cost data. <br class="hidden sm:inline"> It is intended to support negotiation, not replace professional inspections.
                </div>
                <br>
                @if("LOW_RISK".equals(verdict.getTier()))
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
                                        <div class="text-6xl font-black text-slate-900 mb-2 tracking-tighter">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-lg text-slate-600 mb-8 leading-relaxed font-medium">${option.getDescription()}</p>
                                        
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

            <!-- CTA -->
            <div class="text-center pb-20">
                <a href="/home-repair" class="inline-flex items-center bg-slate-900 hover:bg-black text-white font-bold py-4 px-12 rounded-2xl shadow-xl hover:shadow-2xl transition-all transform hover:-translate-y-1 active:scale-95">
                    <svg class="w-5 h-5 mr-3 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 15l-3-3m0 0l3-3m-3 3h8M3 12a9 9 0 1118 0 9 9 0 01-18 0z"/></svg>
                    Start New Forensic Audit
                </a>
                <p class="mt-6 text-xs text-slate-400 font-medium">Data authorized by 2026 RSMeans & Integrated Cost Library v2.0</p>
            </div>

        </div>
    </div>
`)
