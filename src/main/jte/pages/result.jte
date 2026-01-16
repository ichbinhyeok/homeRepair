@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict
@import com.livingcostcheck.home_repair.domain.VerdictHistory
@param Verdict verdict
@param VerdictHistory history

@template.layout(content = @`
    <div class="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        
        <!-- Header / Verdict Tier -->
        <div class="text-center mb-10">
            @if("APPROVED".equals(verdict.getTier()))
                <div class="inline-block bg-green-100 text-green-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-green-200">VERDICT: APPROVED</div>
                <h1 class="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">Green Light</h1>
                <p class="text-lg text-green-700 font-medium">${verdict.getHeadline()}</p>
            @elseif("WARNING".equals(verdict.getTier()))
                 <div class="inline-block bg-yellow-100 text-yellow-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-yellow-200">VERDICT: WARNING</div>
                <h1 class="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">Caution Required</h1>
                <p class="text-lg text-yellow-700 font-medium">${verdict.getHeadline()}</p>
            @else
                 <div class="inline-block bg-red-100 text-red-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-red-200">VERDICT: DENIED</div>
                <h1 class="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">Insufficient Budget</h1>
                <p class="text-lg text-red-700 font-medium">${verdict.getHeadline()}</p>
            @endif
        </div>

        <!-- CONTEXT SUMMARY -->
        <div class="bg-slate-50 border border-slate-200 rounded-lg p-4 mb-10 text-sm text-slate-600 flex flex-wrap justify-center gap-x-6 gap-y-2">
            <div><span class="font-bold text-slate-800">Location:</span> ${history.getZipCode() != null ? history.getZipCode() : "Unknown"}</div>
            <div><span class="font-bold text-slate-800">Era:</span> ${history.getDecade() != null ? history.getDecade().replace("_", "-") : "Unknown"}</div>
            <div>
                <span class="font-bold text-slate-800">Budget:</span> 
                $${history.getBudget() != null && !history.getBudget().equalsIgnoreCase("null") ? String.format("%,.0f", Double.parseDouble(history.getBudget())) : "0"}
            </div>
            <div><span class="font-bold text-slate-800">Goal:</span> ${history.getPurpose() != null ? history.getPurpose() : "General"}</div>
            <div><span class="font-bold text-slate-800">History:</span> ${history.getRepairHistory() != null && !history.getRepairHistory().isEmpty() ? history.getRepairHistory() : "None"}</div>
        </div>

        <!-- MUST DO Section (High Priority) -->
        <div class="mb-12">
            <h2 class="text-2xl font-bold text-slate-900 border-b-2 border-slate-200 pb-3 mb-6 flex items-center">
                <svg class="w-6 h-6 text-red-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
                CRITICAL / MUST DO
            </h2>
            
            @if(verdict.getMustDoExplanation() != null && !verdict.getMustDoExplanation().isEmpty())
                <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 text-red-700 text-sm">
                    <ul class="list-disc pl-4 space-y-1">
                        @for(var exp : verdict.getMustDoExplanation())
                            <li>${exp}</li>
                        @endfor
                    </ul>
                </div>
            @endif
            
            <div class="grid gap-6 md:grid-cols-2">
                @if(verdict.getPlan() != null && verdict.getPlan().getMustDo() != null)
                    @for(var item : verdict.getPlan().getMustDo())
                        <div class="bg-white rounded-xl shadow-md overflow-hidden border-l-4 ${item.isMandatory() ? "border-red-600" : "border-slate-800"}">
                            <div class="p-6">
                                <div class="flex justify-between items-start mb-4">
                                    <span class="bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded uppercase tracking-wider">${item.getCategory()}</span>
                                    <span class="text-lg font-bold text-slate-900">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                </div>
                                <h3 class="font-bold text-lg text-slate-800 mb-2 leading-snug">${item.getPrettyName()}</h3>
                                @if(item.getRiskFlags() != null && !item.getRiskFlags().isEmpty())
                                    <div class="space-y-1 mb-3">
                                        @for(var flag : item.getRiskFlags())
                                            <div class="text-xs text-red-600 font-semibold flex items-center">
                                                <svg class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
                                                ${flag}
                                            </div>
                                        @endfor
                                        @if(item.getRiskFlags().contains("HISTORY_RECHECK"))
                                            <p class="text-xs text-blue-600 italic mt-1">
                                                * Diagnostic check only based on provided repair history.
                                            </p>
                                        @endif
                                    </div>
                                @endif
                                <p class="text-sm text-slate-500">${item.getExplanation()}</p>
                            </div>
                        </div>
                    @endfor
                @endif
                
                @if(verdict.getPlan() == null || verdict.getPlan().getMustDo() == null || verdict.getPlan().getMustDo().isEmpty())
                    <div class="md:col-span-2 bg-green-50 p-6 rounded-lg text-center border border-green-100">
                        <p class="text-green-700 font-medium">No critical safety items detected based on your inputs.</p>
                    </div>
                @endif
            </div>
        </div>

        <!-- EXPERT STRATEGIC ADVICE -->
        <div class="bg-slate-900 rounded-xl shadow-xl overflow-hidden mb-12">
            <div class="px-6 py-4 border-b border-slate-700 flex items-center bg-slate-800">
                <svg class="w-5 h-5 text-blue-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/></svg>
                <h2 class="text-white font-bold tracking-wide uppercase text-sm">Expert Strategic Advice</h2>
            </div>
            <div class="p-6 text-slate-300">
                <div class="grid md:grid-cols-2 gap-8">
                    <div>
                        <h4 class="text-blue-400 font-bold text-xs uppercase mb-3 tracking-wider">Future Cost Advisory</h4>
                        @if(verdict.getFutureCostWarning() != null && !verdict.getFutureCostWarning().isEmpty())
                            <ul class="space-y-3">
                                @for(var warning : verdict.getFutureCostWarning())
                                    <li class="flex items-start text-sm">
                                        <span class="text-yellow-500 mr-2">⚠</span>
                                        ${warning}
                                    </li>
                                @endfor
                            </ul>
                        @else
                            <p class="text-sm text-slate-500 italic">No immediate deferred maintenance risks detected.</p>
                        @endif
                    </div>
                    <div>
                        <h4 class="text-blue-400 font-bold text-xs uppercase mb-3 tracking-wider">Upgrade Opportunities</h4>
                         @if(verdict.getUpgradeScenario() != null && !verdict.getUpgradeScenario().isEmpty())
                             <ul class="space-y-3">
                                @for(var opp : verdict.getUpgradeScenario())
                                    <li class="flex items-start text-sm">
                                        <span class="text-green-500 mr-2">↗</span>
                                        ${opp}
                                    </li>
                                @endfor
                            </ul>
                        @endif
                    </div>
                </div>
            </div>
        </div>

        <!-- SHOULD DO / OPTIONAL -->
        <h2 class="text-xl font-bold text-slate-800 border-b border-slate-200 pb-2 mb-6">Recommended (If Budget Allows)</h2>
        
        @if(verdict.getOptionalActions() != null && !verdict.getOptionalActions().isEmpty())
            <div class="mb-6 text-slate-600 text-sm italic">
                <ul class="list-disc pl-4 space-y-1">
                    @for(var opt : verdict.getOptionalActions())
                        <li>${opt}</li>
                    @endfor
                </ul>
            </div>
        @endif
        <div class="bg-white shadow rounded-lg overflow-hidden border border-slate-200 mb-12">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Item</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                        <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Est. Cost</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    @if(verdict.getPlan() != null && verdict.getPlan().getShouldDo() != null)
                        @for(var item : verdict.getPlan().getShouldDo())
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${item.getPrettyName()}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${item.getCategory()}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">$${String.format("%,.0f", item.getAdjustedCost())}</td>
                            </tr>
                        @endfor
                         @if(verdict.getPlan().getShouldDo().isEmpty())
                            <tr>
                                <td colspan="3" class="px-6 py-4 text-center text-sm text-gray-500 italic">No optional items suggested.</td>
                            </tr>
                        @endif
                    @endif
                </tbody>
            </table>
        </div>

        <!-- SKIPPED / DEFERRED ITEMS (Transparency) -->
        @if(verdict.getPlan() != null && verdict.getPlan().getSkipForNow() != null && !verdict.getPlan().getSkipForNow().isEmpty())
            <div class="opacity-75 grayscale hover:grayscale-0 transition duration-500">
                <h2 class="text-lg font-bold text-slate-500 border-b border-slate-200 pb-2 mb-6 flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/></svg>
                    Deferred / Budget Exceeded
                </h2>
                <div class="bg-slate-50 border border-slate-200 rounded-lg overflow-hidden mb-12">
                    <table class="min-w-full divide-y divide-gray-200">
                         <thead class="bg-gray-100">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Item (Skipped)</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Reason</th>
                                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Est. Cost</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200 text-slate-400">
                            @for(var item : verdict.getPlan().getSkipForNow())
                                <tr class="bg-slate-50/50">
                                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium line-through">${item.getPrettyName()}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-xs">${item.getRiskFlags() != null && !item.getRiskFlags().isEmpty() ? String.join(", ", item.getRiskFlags()) : "Low Priority"}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-right">$${String.format("%,.0f", item.getAdjustedCost())}</td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                     <p class="px-6 py-3 text-xs text-slate-500 bg-slate-100 border-t border-slate-200">
                        * These items were identified but excluded to keep you within budget or focus on critical safety first.
                    </p>
                </div>
            </div>
        @endif

        <div class="text-center">
            <a href="/home-repair" class="text-blue-600 hover:text-blue-800 font-medium text-sm">Start New Calculation &rarr;</a>
        </div>

    </div>
`)
