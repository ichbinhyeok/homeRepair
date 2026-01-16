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
                    <div class="inline-block bg-green-100 text-green-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-green-200">VERDICT: APPROVED</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Strategic Investment Options</h1>
                    <p class="text-xl text-green-700 font-medium">${verdict.getHeadline()}</p>
                @elseif("WARNING".equals(verdict.getTier()))
                     <div class="inline-block bg-yellow-100 text-yellow-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-yellow-200">VERDICT: WARNING</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Budget Caution</h1>
                    <p class="text-xl text-yellow-700 font-medium">${verdict.getHeadline()}</p>
                @else
                     <div class="inline-block bg-red-100 text-red-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-red-200">VERDICT: DENIED</div>
                    <h1 class="text-5xl font-extrabold text-slate-900 tracking-tight mb-3">Insufficient Budget</h1>
                    <p class="text-xl text-red-700 font-medium">${verdict.getHeadline()}</p>
                @endif
            </div>

            <!-- CONTEXT SUMMARY -->
            <div class="bg-white/80 backdrop-blur-sm border border-slate-200 rounded-2xl p-6 mb-12 shadow-lg">
                <div class="flex flex-wrap justify-center gap-x-8 gap-y-3 text-sm text-slate-600">
                    <div><span class="font-bold text-slate-800">Location:</span> ${history.getZipCode() != null ? history.getZipCode() : "Unknown"}</div>
                    <div><span class="font-bold text-slate-800">Era:</span> ${history.getDecade() != null ? history.getDecade().replace("_", "-") : "Unknown"}</div>
                    <div>
                        <span class="font-bold text-slate-800">Budget:</span> 
                        $${history.getBudget() != null && !history.getBudget().equalsIgnoreCase("null") ? String.format("%,.0f", Double.parseDouble(history.getBudget())) : "0"}
                    </div>
                    <div><span class="font-bold text-slate-800">Goal:</span> ${history.getPurpose() != null ? history.getPurpose() : "General"}</div>
                </div>
            </div>

            <!-- THREE-TIER STRATEGY CARDS -->
            @if(verdict.getStrategyOptions() != null && !verdict.getStrategyOptions().isEmpty())
                <div class="mb-16">
                    <h2 class="text-3xl font-bold text-slate-900 text-center mb-8">Choose Your Strategy</h2>
                    <div class="grid md:grid-cols-3 gap-8">
                        @for(var option : verdict.getStrategyOptions())
                            @if(option.getStrategyType() == StrategyType.SAFETY_FLIP)
                                <!-- TIER 1: Safety/Flip -->
                                <div class="bg-gradient-to-br from-slate-50 to-slate-100 rounded-2xl shadow-xl overflow-hidden border-2 border-slate-300 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300">
                                    <div class="bg-slate-600 text-white px-6 py-4">
                                        <div class="text-xs font-black tracking-widest uppercase mb-1 opacity-90">Tier 1</div>
                                        <h3 class="text-2xl font-extrabold">${option.getName()}</h3>
                                    </div>
                                    <div class="p-6">
                                        <div class="text-4xl font-black text-slate-900 mb-2">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-sm text-slate-600 font-medium mb-4">${option.getDescription()}</p>
                                        <div class="bg-slate-50 rounded-lg p-3 mb-4 border border-slate-200">
                                            <div class="text-xs font-bold text-slate-500 uppercase mb-2">Goal</div>
                                            <p class="text-sm text-slate-700">${option.getGoal()}</p>
                                        </div>
                                        <div class="mb-4">
                                            <div class="text-xs font-bold text-slate-500 uppercase mb-2">Materials</div>
                                            <p class="text-sm text-slate-700">${option.getMaterialGrade()}</p>
                                        </div>
                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <ul class="space-y-1 mb-4">
                                                @for(var highlight : option.getKeyHighlights())
                                                    <li class="text-sm text-slate-600 flex items-start">
                                                        <svg class="w-4 h-4 text-slate-500 mr-2 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                        <span>${highlight}</span>
                                                    </li>
                                                @endfor
                                            </ul>
                                        @endif
                                        <div class="text-xs text-slate-500 mt-4 pt-4 border-t border-slate-200">
                                            Includes ${option.getPlan().getMustDo().size()} critical items
                                        </div>
                                    </div>
                                </div>
                            @elseif(option.getStrategyType() == StrategyType.STANDARD_LIVING)
                                <!-- TIER 2: Standard Living (FEATURED) -->
                                <div class="bg-gradient-to-br from-blue-600 to-blue-700 rounded-2xl shadow-2xl overflow-hidden border-2 border-blue-400 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 relative">
                                    <div class="absolute top-0 right-0 bg-yellow-400 text-yellow-900 font-black text-xs px-3 py-1 rounded-bl-lg">RECOMMENDED</div>
                                    <div class="bg-blue-700 text-white px-6 py-4">
                                        <div class="text-xs font-black tracking-widest uppercase mb-1 opacity-90">Tier 2</div>
                                        <h3 class="text-2xl font-extrabold">${option.getName()}</h3>
                                    </div>
                                    <div class="p-6 text-white">
                                        <div class="text-5xl font-black mb-2">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-sm text-blue-100 font-medium mb-4">${option.getDescription()}</p>
                                        <div class="bg-blue-800/50 rounded-lg p-3 mb-4 border border-blue-400/30">
                                            <div class="text-xs font-bold text-blue-200 uppercase mb-2">Goal</div>
                                            <p class="text-sm text-white">${option.getGoal()}</p>
                                        </div>
                                        <div class="mb-4">
                                            <div class="text-xs font-bold text-blue-200 uppercase mb-2">Materials</div>
                                            <p class="text-sm text-white">${option.getMaterialGrade()}</p>
                                        </div>
                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <ul class="space-y-1 mb-4">
                                                @for(var highlight : option.getKeyHighlights())
                                                    <li class="text-sm text-blue-100 flex items-start">
                                                        <svg class="w-4 h-4 text-blue-300 mr-2 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                        <span>${highlight}</span>
                                                    </li>
                                                @endfor
                                            </ul>
                                        @endif
                                        <div class="text-xs text-blue-200 mt-4 pt-4 border-t border-blue-500/50">
                                            Includes ${option.getPlan().getMustDo().size()} essential items
                                        </div>
                                    </div>
                                </div>
                            @elseif(option.getStrategyType() == StrategyType.FOREVER_HOME)
                                <!-- TIER 3: Forever Home -->
                                <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-2xl shadow-xl overflow-hidden border-2 border-purple-300 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300">
                                    <div class="bg-purple-600 text-white px-6 py-4">
                                        <div class="text-xs font-black tracking-widest uppercase mb-1 opacity-90">Tier 3</div>
                                        <h3 class="text-2xl font-extrabold">${option.getName()}</h3>
                                    </div>
                                    <div class="p-6">
                                        <div class="text-4xl font-black text-purple-900 mb-2">$${String.format("%,.0f", option.getTotalCost())}</div>
                                        <p class="text-sm text-purple-700 font-medium mb-4">${option.getDescription()}</p>
                                        <div class="bg-purple-50 rounded-lg p-3 mb-4 border border-purple-200">
                                            <div class="text-xs font-bold text-purple-500 uppercase mb-2">Goal</div>
                                            <p class="text-sm text-purple-900">${option.getGoal()}</p>
                                        </div>
                                        <div class="mb-4">
                                            <div class="text-xs font-bold text-purple-500 uppercase mb-2">Materials</div>
                                            <p class="text-sm text-purple-900">${option.getMaterialGrade()}</p>
                                        </div>
                                        @if(option.getKeyHighlights() != null && !option.getKeyHighlights().isEmpty())
                                            <ul class="space-y-1 mb-4">
                                                @for(var highlight : option.getKeyHighlights())
                                                    <li class="text-sm text-purple-700 flex items-start">
                                                        <svg class="w-4 h-4 text-purple-500 mr-2 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                                                        <span>${highlight}</span>
                                                    </li>
                                                @endfor
                                            </ul>
                                        @endif
                                        <div class="text-xs text-purple-600 mt-4 pt-4 border-t border-purple-200">
                                            Includes ${option.getPlan().getMustDo().size() + option.getPlan().getShouldDo().size()} total items
                                        </div>
                                    </div>
                                </div>
                            @endif
                        @endfor
                    </div>
                </div>
            @endif

            <!-- NEGOTIATION LEVERAGE (If exists in any strategy) -->
            @if(verdict.getStrategyOptions() != null && verdict.getStrategyOptions().stream().anyMatch(opt -> opt.getNegotiationCopy() != null && !opt.getNegotiationCopy().isEmpty()))
                <div class="bg-gradient-to-r from-green-600 to-emerald-700 rounded-2xl shadow-2xl overflow-hidden mb-12">
                    <div class="px-6 py-4 border-b border-green-500 flex items-center">
                        <svg class="w-6 h-6 text-green-200 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                        <h2 class="text-white font-bold tracking-wide uppercase text-lg">Negotiation Leverage Copy</h2>
                    </div>
                    <div class="p-6">
                        @for(var option : verdict.getStrategyOptions())
                            @if(option.getStrategyType() == StrategyType.SAFETY_FLIP && option.getNegotiationCopy() != null && !option.getNegotiationCopy().isEmpty())
                                <div class="bg-white rounded-lg p-5 shadow-inner">
                                    <pre class="text-sm text-slate-700 whitespace-pre-wrap font-mono leading-relaxed">${option.getNegotiationCopy()}</pre>
                                    <button onclick="navigator.clipboard.writeText(this.previousElementSibling.innerText)" class="mt-4 bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-6 rounded-lg shadow transition-colors">
                                        📋 Copy to Clipboard
                                    </button>
                                </div>
                            @endif
                        @endfor
                    </div>
                </div>
            @endif

            <!-- DETAILED BREAKDOWN (Expandable/Accordion style) - Using STANDARD tier as default -->
            @if(verdict.getPlan() != null)
                <div class="bg-white rounded-2xl shadow-xl overflow-hidden mb-12">
                    <div class="bg-slate-800 text-white px-6 py-4">
                        <h2 class="text-xl font-bold">Detailed Breakdown (Standard Tier)</h2>
                    </div>
                    
                    <!-- Must Do Items -->
                    @if(verdict.getPlan().getMustDo() != null && !verdict.getPlan().getMustDo().isEmpty())
                        <div class="p-6 border-b border-slate-200">
                            <h3 class="text-lg font-bold text-slate-900 mb-4 flex items-center">
                                <svg class="w-5 h-5 text-red-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
                                Essential Repairs
                            </h3>
                            <div class="grid md:grid-cols-2 gap-4">
                                @for(var item : verdict.getPlan().getMustDo())
                                    <div class="bg-slate-50 rounded-lg p-4 border-l-4 ${item.isMandatory() ? "border-red-600" : "border-slate-600"}">
                                        <div class="flex justify-between items-start mb-2">
                                            <span class="text-xs font-bold text-slate-500 uppercase">${item.getCategory()}</span>
                                            <span class="text-lg font-bold text-slate-900">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                        </div>
                                        <h4 class="font-bold text-slate-800 mb-1">${item.getPrettyName()}</h4>
                                        @if(item.getExplanation() != null && !item.getExplanation().isEmpty())
                                            <p class="text-xs text-slate-600">${item.getExplanation()}</p>
                                        @endif
                                    </div>
                                @endfor
                            </div>
                        </div>
                    @endif

                    <!-- Should Do Items -->
                    @if(verdict.getPlan().getShouldDo() != null && !verdict.getPlan().getShouldDo().isEmpty())
                        <div class="p-6">
                            <h3 class="text-lg font-bold text-slate-800 mb-4">Recommended (If Budget Allows)</h3>
                            <div class="grid md:grid-cols-2 gap-4">
                                @for(var item : verdict.getPlan().getShouldDo())
                                    <div class="bg-blue-50 rounded-lg p-4">
                                        <div class="flex justify-between items-start mb-2">
                                            <span class="text-xs font-bold text-blue-600 uppercase">${item.getCategory()}</span>
                                            <span class="text-md font-bold text-slate-800">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                        </div>
                                        <h4 class="font-semibold text-slate-700">${item.getPrettyName()}</h4>
                                    </div>
                                @endfor
                            </div>
                        </div>
                    @endif
                </div>
            @endif

            <!-- CTA -->
            <div class="text-center">
                <a href="/home-repair" class="inline-block bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-lg shadow-lg hover:shadow-xl transition-all">
                    ← Start New Calculation
                </a>
            </div>

        </div>
    </div>
`)
