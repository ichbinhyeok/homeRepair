@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict
@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem
@import com.livingcostcheck.home_repair.domain.VerdictHistory

@param Verdict verdict
@param VerdictHistory history

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verdict | LivingCostCheck</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; }
        .bento-card { @apply bg-white rounded-2xl border border-slate-100 shadow-sm p-6 hover:shadow-md transition-shadow duration-300; }
        .hero-pattern { background-image: radial-gradient(#cbd5e1 1px, transparent 1px); background-size: 20px 20px; opacity: 0.3; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 antialiased min-h-screen flex flex-col">

    <%-- 1. BRAND HEADER --%>
    <nav class="sticky top-0 z-50 bg-white/80 backdrop-blur-xl border-b border-slate-200/60">
        <div class="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
            <div class="flex items-center gap-2">
                <span class="text-xl">🏠</span>
                <span class="font-semibold text-slate-900 tracking-tight text-lg">LivingCost<span class="text-blue-600">Check</span></span>
            </div>
            <div class="flex items-center gap-3">
                <span class="px-2 py-1 bg-slate-100 rounded-md text-[10px] font-mono text-slate-500">
                    ID: ${java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase()}
                </span>
                <a href="/" class="text-xs font-semibold text-blue-600 hover:text-blue-700 transition">New Scan</a>
            </div>
        </div>
    </nav>

    <main class="max-w-6xl mx-auto px-6 py-10">
        
        <%-- 2. STATUS HEADER (Minimalist) --%>
        <div class="flex flex-col md:flex-row items-start md:items-end justify-between gap-6 mb-12">
            <div>
                <div class="flex items-center gap-2 mb-3">
                    <span class="w-2.5 h-2.5 rounded-full 
                        ${"LOW_RISK".equals(verdict.getTier()) ? "bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.4)]" : 
                          "CONDITIONAL".equals(verdict.getTier()) ? "bg-amber-500 shadow-[0_0_8px_rgba(245,158,11,0.4)]" : 
                          verdict.isDealKiller() ? "bg-red-600 shadow-[0_0_8px_rgba(220,38,38,0.4)]" : "bg-rose-500"}">
                    </span>
                    <span class="text-xs font-bold uppercase tracking-widest text-slate-500">Forensic Analysis Complete</span>
                </div>
                <h1 class="text-4xl md:text-5xl font-bold tracking-tight text-slate-900 mb-2">
                    ${verdict.getHeadline().split("\\.")[0]}.
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
                    <h2 class="text-sm font-medium text-slate-400 uppercase tracking-widest mb-1">Capital Exposure Estimate</h2>
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
                             <div class="text-xs font-bold text-slate-500 uppercase mb-1">Primary Driver</div>
                             <div class="text-lg font-semibold">${verdict.getPrimaryCostDriver().replace("Primary Cost Driver:", "").trim()}</div>
                        </div>
                    @endif
                </div>
            </div>

            <%-- CARD 2: ACTION PLAN (Strategy) --%>
            <div class="md:col-span-4 bg-white rounded-[32px] p-8 border border-slate-100 shadow-sm hover:shadow-lg transition-all flex flex-col justify-between">
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
                        <span class="text-xs font-bold text-slate-400 uppercase">Negotiation Value</span>
                        <div class="text-3xl font-bold text-slate-900 mt-2 tracking-tight">$${String.format("%,.0f", verdict.getExactCostEstimate() * 1.5)}</div>
                        <p class="text-xs text-slate-500 mt-2 font-medium bg-white/50 inline-block px-2 py-1 rounded">
                            Suggested credit request (+50% buffer)
                        </p>
                     </div>
                </div>

            </div>
        </div>

        <%-- 4. DETAILS SECTION --%>
        <section id="details-section" class="border-t border-slate-100 pt-16">
            <h3 class="text-xl font-bold text-slate-900 mb-8">Itemized Verification.</h3>
            
            <%-- MUST DO LIST --%>
            @if(verdict.getPlan().getMustDo() != null && !verdict.getPlan().getMustDo().isEmpty())
                <div class="space-y-4">
                    @for(RiskAdjustedItem item : verdict.getPlan().getMustDo())
                        <div class="flex flex-col sm:flex-row items-baseline justify-between p-6 bg-white border border-slate-100 rounded-2xl hover:border-slate-300 transition-colors group">
                            <div class="flex-1 pr-6">
                                <div class="flex items-center gap-3 mb-2">
                                    <h4 class="font-bold text-slate-900">${item.getPrettyName()}</h4>
                                    
                                     @if(Boolean.TRUE.equals(item.getIsForensicConfirmed()))
                                        <span class="px-2 py-0.5 rounded-md bg-rose-50 text-rose-600 text-[10px] font-bold uppercase tracking-wide border border-rose-100">Forensic Match</span>
                                    @endif
                                    @if(Boolean.TRUE.equals(item.getIsCodeMandated()))
                                        <span class="px-2 py-0.5 rounded-md bg-slate-100 text-slate-600 text-[10px] font-bold uppercase tracking-wide">Code</span>
                                    @endif
                                </div>
                                <p class="text-sm text-slate-500 group-hover:text-slate-700 transition-colors">${item.getExplanation()}</p>
                            </div>
                            <div class="font-mono font-semibold text-slate-900 mt-4 sm:mt-0">
                                $${String.format("%,.0f", item.getAdjustedCost())}
                            </div>
                        </div>
                    @endfor
                </div>
            @endif

            <div class="mt-16 text-center">
                 <p class="text-xs text-slate-400 mb-4">Calculations generated at ${new java.util.Date().toString()}</p>
                 <a href="/" class="text-sm font-semibold text-blue-600 hover:text-black transition">Start Another Analysis &rarr;</a>
            </div>
        </section>

    </main>

</body>
</html>
