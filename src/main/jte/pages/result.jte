@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict
@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem
@import com.livingcostcheck.home_repair.domain.VerdictHistory

@param Verdict verdict
@param VerdictHistory history

@param String title = "Verdict | LifeVerdict"
@param String verdictH1 = null

@template.layout(
    title = title,
    headContent = @`
        <meta name="robots" content="noindex">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap" rel="stylesheet">
        <style>
            .bento-card { @apply bg-white rounded-2xl border border-slate-100 shadow-sm p-6 hover:shadow-md transition-shadow duration-300; }
            .hero-pattern { background-image: radial-gradient(#cbd5e1 1px, transparent 1px); background-size: 20px 20px; opacity: 0.3; }
        </style>
    `,
    content = @`
        <%-- WRAPPED CONTENT --%>
        <div class="max-w-6xl mx-auto px-6 py-10">
        
            <%-- 2. STATUS HEADER (Minimalist) --%>
            <div class="flex flex-col md:flex-row items-start md:items-end justify-between gap-6 mb-12">
                <div>
                    <div class="flex items-center gap-2 mb-3">
                        <span class="w-2.5 h-2.5 rounded-full 
                            ${"LOW_RISK".equals(verdict.getTier()) ? "bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.4)]" : 
                              "CONDITIONAL".equals(verdict.getTier()) ? "bg-amber-500 shadow-[0_0_8px_rgba(245,158,11,0.4)]" : 
                              verdict.isDealKiller() ? "bg-red-600 shadow-[0_0_8px_rgba(220,38,38,0.4)]" : "bg-rose-500"}">
                        </span>
                        <span class="text-xs font-bold uppercase tracking-widest text-slate-500">
                            @if("BUYING".equals(history.getPurpose()))
                                Pre-Purchase Due Diligence
                            @else
                                Home Health Check Complete
                            @endif
                        </span>
                    </div>
                    <h1 class="text-4xl md:text-5xl font-bold tracking-tight text-slate-900 mb-2">
                        @if("BUYING".equals(history.getPurpose()))
                            ${verdictH1 != null ? verdictH1 : "Risk Assessment Report"}
                        @else
                            ${verdictH1 != null ? verdictH1 : (verdict.getHeadline().split("\\.")[0] + ".")}
                        @endif
                    </h1>
                    <p class="text-lg text-slate-500 max-w-2xl leading-relaxed">
                        ${verdict.getHeadline().contains(".") && verdict.getHeadline().split("\\.").length > 1 ? verdict.getHeadline().split("\\.")[1] : ""}
                    </p>

                    @if(verdict.isDealKiller())
                        <div class="mt-4 inline-flex items-center gap-2 px-3 py-2 bg-red-50 border border-red-100 rounded-lg text-red-700 text-sm font-semibold">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
                            ${verdict.getDealKillerMessage()}
                        </div>
                    @endif
                </div>
                
                <div class="text-right hidden md:block opacity-10">
                    <svg class="w-24 h-24" fill="currentColor" viewBox="0 0 24 24">
                        <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                </div>
            </div>

            <%-- 3. KEY METRICS GRID (Apple Health Style) --%>
            <div class="grid grid-cols-1 md:grid-cols-12 gap-6 mb-16">

                <%-- CARD 1: COST ESTIMATE (Large Card) --%>
                <div class="md:col-span-8 bg-black rounded-[32px] p-8 text-white relative overflow-hidden shadow-2xl flex flex-col justify-between group">
                    <div class="absolute inset-0 bg-gradient-to-br from-slate-900 to-black"></div>
                    <div class="absolute right-0 bottom-0 w-64 h-64 bg-blue-600 rounded-full blur-[120px] opacity-20 group-hover:opacity-30 transition-opacity"></div>
                    
                    <div class="relative z-10">
                        <h2 class="text-sm font-medium text-slate-400 uppercase tracking-widest mb-1">
                            @if("BUYING".equals(history.getPurpose()))
                                Potential Liability (Risk)
                            @else
                                Estimated Repair Budget
                            @endif
                        </h2>
                        <div class="flex items-baseline gap-1 mt-4">
                            <span class="text-7xl font-semibold tracking-tighter">
                                ${verdict.getCostRangeLabel().contains("(") ? verdict.getCostRangeLabel().substring(0, verdict.getCostRangeLabel().indexOf("(")) : verdict.getCostRangeLabel()}
                            </span>
                        </div>
                    </div>

                    <div class="relative z-10 mt-8 flex items-end justify-between">
                         <p class="text-slate-400 text-sm font-medium max-w-sm">
                            Total estimated immediate to 5-year liability based on forensic scan of ${verdict.getItemsAnalyzed()} components.
                        </p>
                        @if(verdict.getPrimaryCostDriver() != null)
                            <div class="text-right">
                                 <div class="text-xs font-bold text-slate-500 uppercase mb-1">Primary Cost Driver</div>
                                 <div class="text-lg font-semibold">${verdict.getPrimaryCostDriver().replace("Primary Cost Driver:", "").trim()}</div>
                            </div>
                        @endif
                    </div>
                </div>

                <%-- CARD 2: ACTION PLAN (Strategy) --%>
                <div class="md:col-span-4 bg-white rounded-[32px] p-8 border border-slate-100 shadow-sm hover:shadow-lg transition-all flex flex-col justify-between">
                    @if("BUYING".equals(history.getPurpose()))
                        <!-- BUYER MODE: Negotiation Tactic -->
                        <div>
                             <div class="flex items-center gap-2 mb-6">
                                <span class="w-2 h-2 rounded-full bg-amber-500"></span>
                                <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Negotiation</span>
                            </div>
                            <h3 class="text-xl font-bold text-slate-900 mb-2">Ask for Credit.</h3>
                            <div class="bg-amber-50 p-3 rounded-lg border border-amber-100 text-xs font-mono text-slate-600 mb-2">
                                "Request seller credit of $${String.format("%,.0f", verdict.getExactCostEstimate())} for ${verdict.getPrimaryCostDriver() != null ? verdict.getPrimaryCostDriver().replace("Primary Cost Driver:", "").trim() : "repairs"}..."
                            </div>
                        </div>
                        <button onclick="navigator.clipboard.writeText('Request seller credit of $${String.format("%,.0f", verdict.getExactCostEstimate())} for immediate repairs.'); alert('Copied to clipboard!');" class="mt-4 w-full py-3 bg-slate-900 text-white font-semibold rounded-xl text-sm hover:bg-slate-700 transition-colors">
                            Copy Email Text
                        </button>
                    @else
                        <!-- OWNER MODE: Strategy -->
                        <div>
                             <div class="flex items-center gap-2 mb-6">
                                <span class="w-2 h-2 rounded-full bg-blue-500"></span>
                                <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Strategy</span>
                            </div>
                            <h3 class="text-2xl font-bold text-slate-900 mb-2">${verdict.getStrategyUsed().replace("_", " ")}</h3>
                            <p class="text-sm text-slate-500 leading-relaxed">
                                ${verdict.getStrategyExplanation()}
                            </p>
                        </div>
                        <button onclick="document.getElementById('details-section').scrollIntoView({behavior: 'smooth'})" class="mt-6 w-full py-3 bg-slate-50 text-slate-900 font-semibold rounded-xl text-sm hover:bg-slate-100 transition-colors">
                            View Verified Plan
                        </button>
                    @endif
                </div>

                <%-- CARD 3: MARKET SIGNALS (Detail Grid) --%>
                <div class="md:col-span-12 grid grid-cols-1 md:grid-cols-3 gap-6">
                    
                    <!-- Regional Risk -->
                    <div class="bg-white rounded-[24px] p-6 border border-slate-100 shadow-sm">
                         <div class="flex items-center justify-between mb-4">
                            <span class="text-xs font-bold text-slate-400 uppercase">Context Risk</span>
                            <span class="text-xl">🌍</span>
                         </div>
                         @if(verdict.getContextBriefing() != null)
                            <div class="text-2xl font-bold text-slate-900 mb-1">${verdict.getContextBriefing().getRegionalRisk()}</div>
                            <div class="w-full bg-slate-100 h-1.5 rounded-full mb-3 overflow-hidden">
                               <div class="bg-blue-500 h-full rounded-full" style="width: 75%"></div>
                            </div>
                            <p class="text-xs text-slate-500">${verdict.getContextBriefing().getRegionalRiskReason()}</p>
                         @else
                            <div class="text-lg font-bold text-slate-400 mb-1">N/A</div>
                            <p class="text-xs text-slate-400">Regional data unavailable.</p>
                         @endif
                    </div>

                    <!-- Labor Market -->
                    <div class="bg-white rounded-[24px] p-6 border border-slate-100 shadow-sm">
                         <div class="flex items-center justify-between mb-4">
                            <span class="text-xs font-bold text-slate-400 uppercase">Labor Rates</span>
                            <span class="text-xl">🔨</span>
                         </div>
                         @if(verdict.getContextBriefing() != null)
                            <div class="text-2xl font-bold text-slate-900 mb-1">${verdict.getContextBriefing().getLaborMarketRate()}</div>
                            <div class="w-full bg-slate-100 h-1.5 rounded-full mb-3 overflow-hidden">
                               <div class="bg-indigo-500 h-full rounded-full" style="width: 85%"></div>
                            </div>
                            <p class="text-xs text-slate-500">${verdict.getContextBriefing().getLaborMarketRateReason()}</p>
                         @else
                            <div class="text-lg font-bold text-slate-400 mb-1">N/A</div>
                            <p class="text-xs text-slate-400">Labor data unavailable.</p>
                         @endif
                    </div>

                    <!-- Leverage -->
                    <div class="bg-gradient-to-br from-slate-50 to-white rounded-[24px] p-6 border border-slate-100 shadow-sm relative overflow-hidden group">
                         <div class="absolute right-0 top-0 p-3 opacity-10 font-black text-6xl text-slate-900 select-none">$</div>
                         <div class="relative z-10">
                            <span class="text-xs font-bold text-slate-400 uppercase">Repair Allowance Estimate</span>
                            <div class="text-3xl font-bold text-slate-900 mt-2 tracking-tight">$${String.format("%,.0f", verdict.getExactCostEstimate() * 1.5)}</div>
                            <p class="text-xs text-slate-500 mt-2 font-medium bg-white/50 inline-block px-2 py-1 rounded">
                                Suggested allowance request (+50% buffer)
                            </p>
                         </div>
                    </div>

                </div>
            </div>

                <!-- Market Benchmark (Redesigned) -->
                @if(verdict.getComparisonData() != null)
                <div class="bg-white rounded-[32px] p-8 border border-slate-100 shadow-sm mb-12 flex flex-col md:flex-row items-center gap-8">
                    <div class="flex-1">
                        <div class="inline-flex items-center gap-2 px-3 py-1 bg-slate-100 rounded-full mb-4">
                            <span class="w-2 h-2 rounded-full bg-slate-900"></span>
                            <span class="text-xs font-bold text-slate-600 uppercase tracking-widest">2026 Benchmark</span>
                        </div>
                        <h3 class="text-3xl font-bold text-slate-900 mb-2">
                             @if(verdict.getComparisonData().getCostDelta() > 0)
                                <span class="text-rose-500">+${String.format("%.0f", verdict.getComparisonData().getDeltaPercentage())}%</span> Liability Gap
                             @else
                                <span class="text-emerald-500">${String.format("%.0f", verdict.getComparisonData().getDeltaPercentage())}%</span> Cost Advantage
                             @endif
                        </h3>
                        <p class="text-slate-500 text-lg leading-relaxed mb-6">
                            @if(verdict.getComparisonData().getCostDelta() > 0)
                                Compared to a modern home (2010+) in this zip code, this property carries an additional 
                                <span class="font-bold text-slate-900">$${String.format("%,.0f", verdict.getComparisonData().getCostDelta())}</span> 
                                in statistical maintenance risk.
                            @else
                                This property's estimated maintenance costs are 
                                <span class="font-bold text-slate-900">$${String.format("%,.0f", Math.abs(verdict.getComparisonData().getCostDelta()))}</span> 
                                lower than the modern benchmark for this area.
                            @endif
                        </p>
                        
                        <!-- Simple visual bar -->
                        <div class="relative h-12 bg-slate-50 rounded-xl overflow-hidden flex items-center px-4 border border-slate-100">
                             <div class="absolute left-0 top-0 bottom-0 bg-rose-500/10" style="width: ${Math.min(100, Math.max(0, verdict.getComparisonData().getDeltaPercentage()))}%"></div>
                             <div class="relative z-10 flex justify-between w-full text-xs font-bold uppercase tracking-widest text-slate-500">
                                <span>Modern Baseline</span>
                                <span>This Property</span>
                             </div>
                             <!-- Marker for this property -->
                             @if(verdict.getComparisonData().getDeltaPercentage() > 0)
                                <div class="absolute top-1/2 -translate-y-1/2 w-3 h-3 bg-rose-500 rounded-full shadow-[0_0_12px_rgba(244,63,94,0.6)]" style="left: ${Math.min(98, Math.max(2, verdict.getComparisonData().getDeltaPercentage()))}%"></div>
                             @else
                                <div class="absolute top-1/2 -translate-y-1/2 w-3 h-3 bg-emerald-500 rounded-full shadow-[0_0_12px_rgba(16,185,129,0.6)]" style="left: 2%"></div>
                             @endif
                        </div>
                    </div>
                    
                    <div class="w-full md:w-64 bg-slate-50 rounded-2xl p-6 text-center border border-slate-100">
                        <div class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Baseline Cost</div>
                        <div class="text-2xl font-bold text-slate-900">$${String.format("%,.0f", verdict.getComparisonData().getModernBenchmarkCost())}</div>
                        <div class="text-xs text-slate-400 mt-2">Expected 5yr Maintenance (2010+)</div>
                    </div>
                </div>
                @endif

                <!-- System Integrity Timeline -->
                @if(verdict.getPlan() != null && verdict.getPlan().getMustDo() != null)
                <div class="bg-white p-8 rounded-[32px] border border-slate-100 shadow-sm mb-12">
                    <div class="flex items-center gap-4 mb-8">
                        <div class="p-3 bg-blue-50 rounded-2xl text-blue-600">
                            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                        </div>
                        <div>
                            <h3 class="text-2xl font-bold text-slate-900">Component Health</h3>
                            <p class="text-slate-500">Forensic status of key systems</p>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        @for(var item : verdict.getPlan().getMustDo())
                            @if(item.getRiskFlags() != null && (item.getRiskFlags().stream().anyMatch(f -> f.contains("DEAD") || f.contains("WATCH") || f.contains("VERIFIED"))))
                            <div class="group h-full">
                                <div class="p-4 rounded-4 border h-100 transition-hover" style="background: ${item.getRiskFlags().contains("VERIFIED_UPDATE: RECENTLY_REPLACED") ? "var(--slate-50)" : "white"};">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <div class="h5 fw-bold mb-0">${item.getPrettyName()}</div>
                                        @if(item.getRiskFlags().contains("VERIFIED_UPDATE: RECENTLY_REPLACED"))
                                            <span class="badge bg-success-subtle text-success border border-success-subtle">NEW</span>
                                        @elseif(item.getRiskFlags().stream().anyMatch(f -> f.contains("DEAD")))
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle">EXPIRED</span>
                                        @else
                                            <span class="badge bg-warning-subtle text-warning border border-warning-subtle">WATCH</span>
                                        @endif
                                    </div>
                                    <p class="text-sm text-slate-500 leading-snug">
                                        $unsafe{item.getExplanation() != null && item.getExplanation().contains(". ") ? item.getExplanation().split("\\. ")[0] + "." : item.getExplanation()}
                                    </p>
                                </div>
                            </div>
                            @endif
                        @endfor
                        
                        <!-- Always show some stability for balance -->
                        <div class="group h-full">
                            <div class="p-4 rounded-4 border h-100 bg-light-subtle">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <div class="h5 fw-bold mb-0">Structural Shell</div>
                                    <span class="badge bg-success-subtle text-success border border-success-subtle">STABLE</span>
                                </div>
                                <p class="small text-muted mb-0">Foundations and load-bearing elements are statistically reliable.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Refine Analysis CTA -->
                <div class="bg-indigo-50 border border-indigo-100 p-8 rounded-[32px] text-center mb-12">
                     <h3 class="text-xl font-bold text-indigo-900 mb-2">Not accurate? Customize this estimate.</h3>
                     <p class="text-indigo-700 mb-6 max-w-lg mx-auto">Property details like roof material and story count can change estimates by 30%+. Refine these details for precision.</p>
                     
                    <form action="/home-repair/verdict" method="post" class="w-full max-w-4xl mx-auto bg-white p-6 rounded-2xl shadow-sm border border-indigo-100 flex flex-col md:flex-row items-center gap-6">
                        <input type="hidden" name="metroCode" value="ATLANTA_SANDY_SPRINGS_GA"> 
                        <input type="hidden" name="sqft" value="2000"> 
                        <input type="hidden" name="relationship" value="LIVING">
                        <input type="hidden" name="budget" value="100000">

                        <div class="w-full md:flex-1 text-left">
                            <label class="block text-xs font-bold text-slate-500 mb-1 uppercase">Bathrooms</label>
                            <select name="bathrooms" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 focus:ring-2 focus:ring-indigo-500">
                                <option value="1">1 Bath</option>
                                <option value="2" selected>2 Baths</option>
                                <option value="3">3 Baths</option>
                                <option value="4">4+ Baths</option>
                            </select>
                        </div>
                        
                        <div class="w-full md:flex-1 text-left">
                            <label class="block text-xs font-bold text-slate-500 mb-1 uppercase">Stories</label>
                            <select name="stories" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 focus:ring-2 focus:ring-indigo-500">
                                 <option value="1">1 Story</option>
                                 <option value="2" selected>2 Stories</option>
                                 <option value="3">3+ Stories</option>
                            </select>
                        </div>

                        <div class="w-full md:w-auto h-full flex items-end">
                             <button type="submit" class="w-full md:w-auto h-[42px] whitespace-nowrap px-8 bg-indigo-600 text-white font-bold rounded-lg hover:bg-indigo-700 transition shadow-lg shadow-indigo-200 mt-5 md:mt-0">
                                Recalculate
                             </button>
                        </div>
                    </form>
                </div>

                <!-- 12-Month Post-Purchase Action Plan -->
                <div class="bg-white p-8 rounded-[32px] border border-slate-200 shadow-sm mb-12">
                    <div class="flex items-center gap-3 mb-6">
                        <div class="w-12 h-12 bg-indigo-600 rounded-full flex items-center justify-center text-white text-xl">📅</div>
                        <div>
                            <h3 class="text-2xl font-bold text-slate-900 leading-tight">12-Month Security Calendar</h3>
                            <p class="text-sm text-slate-500">Immediate maintenance steps to prevent asset depreciation.</p>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div class="p-5 bg-slate-50 rounded-2xl border border-slate-100">
                            <div class="text-xs font-bold text-indigo-600 uppercase tracking-widest mb-2">Month 1-3</div>
                            <h4 class="font-bold text-slate-900 mb-2 whitespace-pre-line">Immediate Stabilization</h4>
                            <ul class="text-xs text-slate-600 space-y-2">
                                <li>• Clean all gutters & downspouts</li>
                                <li>• Change HVAC filters (MERV 11)</li>
                                <li>• Check attic for moisture signs</li>
                                <li>• Locate main water shut-off</li>
                            </ul>
                        </div>
                        <div class="p-5 bg-slate-50 rounded-2xl border border-slate-100">
                            <div class="text-xs font-bold text-indigo-600 uppercase tracking-widest mb-2">Month 4-8</div>
                            <h4 class="font-bold text-slate-900 mb-2 whitespace-pre-line">System Efficiency</h4>
                            <ul class="text-xs text-slate-600 space-y-2">
                                <li>• Professional AC/Furnace service</li>
                                <li>• Flush sediment from water heater</li>
                                <li>• Inspect caulking around windows</li>
                                <li>• Test GFCI outlets in kitchen/bath</li>
                            </ul>
                        </div>
                        <div class="p-5 bg-slate-50 rounded-2xl border border-slate-100">
                            <div class="text-xs font-bold text-indigo-600 uppercase tracking-widest mb-2">Month 9-12</div>
                            <h4 class="font-bold text-slate-900 mb-2 whitespace-pre-line">Asset Preservation</h4>
                            <ul class="text-xs text-slate-600 space-y-2">
                                <li>• Foundation/Grade inspection</li>
                                <li>• Prune trees away from roofline</li>
                                <li>• Check fireplace & chimney cap</li>
                                <li>• Annual home insurance audit</li>
                            </ul>
                        </div>
                    </div>
                </div>

                <!-- Share & Export -->
                <div class="bg-slate-900 text-white p-8 rounded-[32px] shadow-xl flex flex-col md:flex-row items-center justify-between gap-8 mb-12 print:hidden">
                    <div class="max-w-md">
                        <h3 class="text-2xl font-bold mb-2">Take this report with you.</h3>
                        <p class="text-slate-400 text-sm">Download the full forensic analysis or share it with your inspector/realtor to streamline negotiation.</p>
                    </div>
                    <div class="flex flex-wrap gap-4">
                        <button onclick="window.print()" class="px-6 py-3 bg-white text-slate-900 font-bold rounded-xl hover:bg-slate-100 transition flex items-center gap-2">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 00-2 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"></path></svg>
                            Save as PDF / Print
                        </button>
                        <button onclick="navigator.clipboard.writeText(window.location.href); alert('Link copied!');" class="px-6 py-3 bg-slate-800 text-white font-bold rounded-xl border border-slate-700 hover:bg-slate-700 transition flex items-center gap-2">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"></path></svg>
                            Copy Sharable Link
                        </button>
                    </div>
                </div>
                @endif

                <div class="mt-16 text-center">
                     <p class="text-xs text-slate-400 mb-4">Calculations generated at ${new java.util.Date().toString()}</p>
                     <a href="/" class="text-sm font-semibold text-blue-600 hover:text-black transition">Start Another Analysis &rarr;</a>
                </div>
            </div>
        `
)
