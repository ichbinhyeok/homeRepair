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
                @if("APPROVED".equals(verdict.getTier()))
                    <div class="inline-block bg-green-100 text-green-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-green-200 uppercase">VERDICT: APPROVED</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Strategic Investment Options</h1>
                    <p class="text-xl text-green-700 font-medium">${verdict.getHeadline()}</p>
                @elseif("WARNING".equals(verdict.getTier()))
                     <div class="inline-block bg-yellow-100 text-yellow-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-yellow-200 uppercase">VERDICT: WARNING</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Budget Caution</h1>
                    <p class="text-xl text-yellow-700 font-medium">${verdict.getHeadline()}</p>
                @else
                     <div class="inline-block bg-red-100 text-red-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-red-200 uppercase">VERDICT: DENIED</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Insufficient Budget</h1>
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

            <!-- THREE-TIER STRATEGY CARDS -->
            @if(verdict.getStrategyOptions() != null && !verdict.getStrategyOptions().isEmpty())
                <div class="mb-16">
                    <h2 class="text-3xl font-bold text-slate-900 text-center mb-8 tracking-tight">Select Investment Strategy</h2>
                    <div class="grid md:grid-cols-3 gap-8">
                        @for(var option : verdict.getStrategyOptions())
                            @if(option.getStrategyType() == StrategyType.SAFETY_FLIP)
                                <!-- TIER 1: Safety/Flip -->
                                <div class="bg-white rounded-2xl shadow-xl overflow-hidden border border-slate-200 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 flex flex-col">
                                    <div class="bg-slate-100 border-b border-slate-200 px-6 py-4">
                                        <div class="text-[10px] font-black tracking-widest uppercase mb-1 text-slate-500">Tier 1: Minimalist</div>
                                        <h3 class="text-xl font-extrabold text-slate-800">${option.getName()}</h3>
                                    </div>
                                    <div class="p-6 flex-grow">
                                        <div class="text-4xl font-black text-slate-900 mb-2 tracking-tighter">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-sm text-slate-600 mb-6 leading-relaxed">${option.getDescription()}</p>
                                        
                                        <div class="space-y-4">
                                            <div>
                                                <div class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Primary Goal</div>
                                                <p class="text-sm text-slate-700 font-medium">${option.getGoal()}</p>
                                            </div>
                                            <div>
                                                <div class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Construction Scope</div>
                                                <p class="text-sm text-slate-700 font-medium">${option.getMaterialGrade()}</p>
                                            </div>
                                        </div>

                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <div class="mt-6 pt-6 border-t border-slate-100">
                                                <div class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-3">Included Benchmarks</div>
                                                <ul class="space-y-2">
                                                    @for(var highlight : option.getKeyHighlights())
                                                        <li class="text-xs text-slate-600 flex items-start">
                                                            <svg class="w-3.5 h-3.5 text-slate-400 mr-2 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                            <span>${highlight}</span>
                                                        </li>
                                                    @endfor
                                                </ul>
                                            </div>
                                        @endif
                                    </div>
                                    <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 text-[10px] text-slate-400 font-bold uppercase tracking-widest">
                                        Includes ${option.getPlan().getMustDo().size()} critical items
                                    </div>
                                </div>
                            @elseif(option.getStrategyType() == StrategyType.STANDARD_LIVING)
                                <!-- TIER 2: Standard Living (FEATURED) -->
                                <div class="bg-white rounded-2xl shadow-2xl overflow-hidden border-2 border-blue-500 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 relative flex flex-col z-10">
                                    <div class="absolute top-0 right-0 bg-blue-500 text-white font-black text-[10px] px-3 py-1 rounded-bl-lg tracking-widest uppercase">Best ROI</div>
                                    <div class="bg-blue-600 px-6 py-4">
                                        <div class="text-[10px] font-black tracking-widest uppercase mb-1 text-blue-200">Tier 2: Balanced</div>
                                        <h3 class="text-xl font-extrabold text-white">${option.getName()}</h3>
                                    </div>
                                    <div class="p-6 flex-grow">
                                        <div class="text-5xl font-black text-slate-900 mb-2 tracking-tighter">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-sm text-slate-600 mb-6 leading-relaxed font-medium">${option.getDescription()}</p>
                                        
                                        <div class="space-y-4">
                                            <div>
                                                <div class="text-[10px] font-bold text-blue-500 uppercase tracking-widest mb-1">Primary Goal</div>
                                                <p class="text-sm text-slate-800 font-bold">${option.getGoal()}</p>
                                            </div>
                                            <div>
                                                <div class="text-[10px] font-bold text-blue-500 uppercase tracking-widest mb-1">Construction Scope</div>
                                                <p class="text-sm text-slate-800 font-bold">${option.getMaterialGrade()}</p>
                                            </div>
                                        </div>

                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <div class="mt-6 pt-6 border-t border-slate-100">
                                                <div class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-3">Included Benchmarks</div>
                                                <ul class="space-y-2">
                                                    @for(var highlight : option.getKeyHighlights())
                                                        <li class="text-xs text-slate-700 flex items-start">
                                                            <svg class="w-4 h-4 text-blue-500 mr-2 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                            <span class="font-medium">${highlight}</span>
                                                        </li>
                                                    @endfor
                                                </ul>
                                            </div>
                                        @endif
                                    </div>
                                    <div class="px-6 py-4 bg-blue-50 border-t border-blue-100 text-[10px] text-blue-600 font-black uppercase tracking-widest">
                                        Includes ${option.getPlan().getMustDo().size()} essential items
                                    </div>
                                </div>
                            @elseif(option.getStrategyType() == StrategyType.FOREVER_HOME)
                                <!-- TIER 3: Forever Home -->
                                <div class="bg-white rounded-2xl shadow-xl overflow-hidden border border-purple-200 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 flex flex-col">
                                    <div class="bg-purple-50 border-b border-purple-100 px-6 py-4">
                                        <div class="text-[10px] font-black tracking-widest uppercase mb-1 text-purple-400">Tier 3: Asset Protection</div>
                                        <h3 class="text-xl font-extrabold text-purple-900">${option.getName()}</h3>
                                    </div>
                                    <div class="p-6 flex-grow">
                                        <div class="text-4xl font-black text-purple-900 mb-2 tracking-tighter">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-sm text-slate-600 mb-6 leading-relaxed">${option.getDescription()}</p>
                                        
                                        <div class="space-y-4">
                                            <div>
                                                <div class="text-[10px] font-bold text-purple-400 uppercase tracking-widest mb-1">Primary Goal</div>
                                                <p class="text-sm text-purple-800 font-medium">${option.getGoal()}</p>
                                            </div>
                                            <div>
                                                <div class="text-[10px] font-bold text-purple-400 uppercase tracking-widest mb-1">Construction Scope</div>
                                                <p class="text-sm text-purple-800 font-medium">${option.getMaterialGrade()}</p>
                                            </div>
                                        </div>

                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <div class="mt-6 pt-6 border-t border-slate-100">
                                                <div class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-3">Included Benchmarks</div>
                                                <ul class="space-y-2">
                                                    @for(var highlight : option.getKeyHighlights())
                                                        <li class="text-xs text-slate-600 flex items-start">
                                                            <svg class="w-3.5 h-3.5 text-purple-400 mr-2 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                            <span>${highlight}</span>
                                                        </li>
                                                    @endfor
                                                </ul>
                                            </div>
                                        @endif
                                    </div>
                                    <div class="px-6 py-4 bg-purple-50 border-t border-purple-100 text-[10px] text-purple-400 font-black uppercase tracking-widest">
                                        Includes ${option.getPlan().getMustDo().size() + option.getPlan().getShouldDo().size()} total items
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

            <!-- DETAILED BREAKDOWN (Expandable/Accordion style) - Using STANDARD tier as default -->
            @if(verdict.getPlan() != null)
                <div class="bg-white rounded-2xl shadow-xl overflow-hidden mb-12 border border-slate-200">
                    <div class="bg-slate-900 text-white px-8 py-5 flex items-center justify-between">
                        <h2 class="text-lg font-bold tracking-tight">Forensic Itemized Breakdown <span class="text-slate-500 text-sm font-normal ml-2">(Standard Tier)</span></h2>
                        <div class="text-[10px] font-black tracking-widest text-slate-400 uppercase">Audit Version 2026.1.2</div>
                    </div>
                    
                    <!-- Must Do Items -->
                    @if(verdict.getPlan().getMustDo() != null && !verdict.getPlan().getMustDo().isEmpty())
                        <div class="p-8 border-b border-slate-100">
                            <div class="flex items-center justify-between mb-6">
                                <h3 class="text-sm font-black text-slate-900 uppercase tracking-widest flex items-center">
                                    <span class="w-2 h-6 bg-red-600 mr-3 rounded-full"></span>
                                    Critical & Mandatory Repairs
                                </h3>
                                <span class="bg-red-50 text-red-700 text-[10px] font-black px-2 py-1 rounded border border-red-100 uppercase">High Priority</span>
                            </div>
                            
                            <div class="grid lg:grid-cols-2 gap-6">
                                @for(var item : verdict.getPlan().getMustDo())
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
                                @endfor
                            </div>
                        </div>
                    @endif

                    <!-- Should Do Items -->
                    @if(verdict.getPlan().getShouldDo() != null && !verdict.getPlan().getShouldDo().isEmpty())
                        <div class="p-8 bg-slate-50/50">
                            <div class="flex items-center justify-between mb-6">
                                <h3 class="text-sm font-black text-slate-900 uppercase tracking-widest flex items-center">
                                    <span class="w-2 h-6 bg-blue-500 mr-3 rounded-full"></span>
                                    Recommended Asset Improvements
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
