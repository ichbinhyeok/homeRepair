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
    <nav class="bg-white border-b border-slate-200 sticky top-0 z-50">
        <div class="max-w-5xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
            <div class="flex items-center space-x-2">
                <span class="text-2xl">🏠</span>
                <span class="font-bold text-slate-800 tracking-tight text-2xl">LivingCost<span class="text-blue-600">Check</span></span>
            </div>
            <div class="text-xs font-medium text-slate-400">
                VERDICT ID: ${java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase()}
            </div>
        </div>
    </nav>

    <%-- 2. HERO SECTION --%>
    <header class="relative w-full border-b overflow-hidden
        ${"LOW_RISK".equals(verdict.getTier()) ? "bg-emerald-50 border-emerald-200" : 
          "CONDITIONAL".equals(verdict.getTier()) ? "bg-amber-50 border-amber-200" : 
          verdict.isDealKiller() ? "bg-slate-900 border-slate-700" : "bg-rose-50 border-rose-200"}">
        
        <div class="absolute inset-0 hero-pattern"></div>
        <div class="relative max-w-5xl mx-auto px-4 sm:px-6 py-12 sm:py-16">
            
            <%-- Deal Killer Overlay Logic --%>
            @if(verdict.isDealKiller())
                <div class="absolute top-0 left-0 w-full bg-red-600 text-white text-center py-2 font-bold text-sm tracking-widest uppercase animate-pulse">
                     Transaction Critical Alert
                </div>
            @endif

            <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6">
                <div>
                     <span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide mb-4 inline-block shadow-sm 
                        ${"LOW_RISK".equals(verdict.getTier()) ? "bg-emerald-100 text-emerald-800" : 
                          "CONDITIONAL".equals(verdict.getTier()) ? "bg-amber-100 text-amber-800" : 
                          verdict.isDealKiller() ? "bg-red-600 text-white" : "bg-rose-100 text-rose-800"}">
                        Analysis Complete
                    </span>
                    <h1 class="text-4xl sm:text-5xl font-black tracking-tight leading-tight mb-4
                        ${"LOW_RISK".equals(verdict.getTier()) ? "text-emerald-900" : 
                          "CONDITIONAL".equals(verdict.getTier()) ? "text-amber-900" : 
                          verdict.isDealKiller() ? "text-white" : "text-rose-900"}">
                        ${verdict.getHeadline().split("\\.")[0]}.
                    </h1>
                     <p class="text-lg opacity-80 max-w-2xl font-medium leading-relaxed
                        ${"LOW_RISK".equals(verdict.getTier()) ? "text-emerald-900" : 
                          "CONDITIONAL".equals(verdict.getTier()) ? "text-amber-900" : 
                          verdict.isDealKiller() ? "text-white" : "text-rose-900"}">
                        ${verdict.getHeadline().contains(".") && verdict.getHeadline().split("\\.").length > 1 ? verdict.getHeadline().split("\\.")[1] : ""}
                    </p>
                    
                    @if(verdict.isDealKiller())
                        <div class="mt-6 p-4 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl text-white">
                             <p class="font-bold flex items-center gap-2">
                                <span>⚠️</span> ${verdict.getDealKillerMessage()}
                            </p>
                        </div>
                    @endif
                </div>
                <div class="hidden sm:block text-8xl opacity-20 transform rotate-12 select-none grayscale">
                    ${"LOW_RISK".equals(verdict.getTier()) ? "✅" : 
                      "CONDITIONAL".equals(verdict.getTier()) ? "⚠️" : 
                      verdict.isDealKiller() ? "☠️" : "🛑"}
                </div>
            </div>
        </div>
    </header>

    <%-- 3. BENTO GRID DASHBOARD --%>
    <main class="flex-grow max-w-5xl mx-auto px-4 sm:px-6 py-10 w-full relative -mt-8">
        
        <div class="grid grid-cols-1 md:grid-cols-12 gap-6">

            <%-- CARD 1: COST ESTIMATE (Hero Card) --%>
            <div class="bento-card md:col-span-8 flex flex-col justify-between relative overflow-hidden ring-1 ring-slate-900/5">
                <div class="absolute top-0 right-0 w-32 h-32 bg-blue-50 rounded-full blur-3xl -mr-16 -mt-16 pointer-events-none"></div>
                
                <div>
                    <h2 class="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-1">Estimated Capital Requirement</h2>
                    <div class="flex items-baseline gap-2 mt-2">
                        <span class="text-5xl font-black text-slate-900 tracking-tighter">
                            ${verdict.getCostRangeLabel().contains("(") ? verdict.getCostRangeLabel().substring(0, verdict.getCostRangeLabel().indexOf("(")) : verdict.getCostRangeLabel()}
                        </span>
                    </div>
                    <p class="text-slate-500 text-sm mt-2 font-medium">
                        ${verdict.getCostRangeLabel().contains("(") ? verdict.getCostRangeLabel().substring(verdict.getCostRangeLabel().indexOf("(")) : ""}
                    </p>
                </div>

                <div class="mt-8 space-y-4">
                     @if(verdict.getPrimaryCostDriver() != null)
                        <div class="bg-slate-50 rounded-xl p-4 border border-slate-100 flex items-start gap-3">
                            <span class="text-xl">📉</span>
                            <div>
                                <p class="text-xs font-bold text-slate-500 uppercase">Primary Cost Driver</p>
                                <p class="font-bold text-slate-800 text-sm mt-0.5">${verdict.getPrimaryCostDriver().replace("Primary Cost Driver:", "").trim()}</p>
                            </div>
                        </div>
                    @endif
                    
                    <div class="flex items-center gap-2 text-xs text-slate-400">
                        <span>ℹ️ Includes material/labor for ${verdict.getItemsAnalyzed()} data points</span>
                    </div>
                </div>
            </div>

            <%-- CARD 2: STRATEGY (Actionable) --%>
            <div class="bento-card md:col-span-4 bg-slate-900 text-white border-none shadow-xl flex flex-col justify-between">
                <div>
                    <div class="flex items-center gap-2 mb-4 opacity-80">
                         <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
                         <span class="text-xs font-bold uppercase tracking-widest">Active Strategy</span>
                    </div>
                    <h3 class="text-2xl font-bold mb-2">${verdict.getStrategyUsed().replace("_", " ")}</h3>
                    <p class="text-sm text-slate-300 leading-relaxed opacity-90">
                        ${verdict.getStrategyExplanation()}
                    </p>
                </div>
                <div class="mt-6 pt-4 border-t border-slate-700">
                    <button onclick="document.getElementById('details-section').scrollIntoView({behavior: 'smooth'})" 
                            class="w-full py-2 text-sm font-bold text-center bg-white/10 hover:bg-white/20 rounded-lg transition-colors text-white">
                        View Itemized Plan ↓
                    </button>
                </div>
            </div>

            <%-- CARD 3: MARKET CONTEXT (Evidence) --%>
            <div class="bento-card md:col-span-6">
                <h3 class="text-sm font-bold text-slate-800 mb-4 flex items-center gap-2">
                    <span>🌍</span> Market Context Signals
                </h3>
                <div class="space-y-4">
                    <%-- Context Item 1: Risk --%>
                    <div>
                        <div class="flex justify-between items-center text-xs mb-1">
                            <span class="font-semibold text-slate-600">Regional Risk</span>
                            <span class="text-slate-900 font-bold">${verdict.getContextBriefing().getRegionalRisk()}</span>
                        </div>
                        <div class="w-full bg-slate-200 rounded-full h-1.5 mb-1">
                            <div class="bg-blue-600 h-1.5 rounded-full" style="width: 75%"></div>
                        </div>
                        <p class="text-[10px] text-slate-400 bg-slate-50 p-1.5 rounded border border-slate-100">
                            ${verdict.getContextBriefing().getRegionalRiskReason()}
                        </p>
                    </div>

                     <%-- Context Item 2: Labor --%>
                     <div>
                        <div class="flex justify-between items-center text-xs mb-1">
                            <span class="font-semibold text-slate-600">Labor Market</span>
                            <span class="text-slate-900 font-bold">${verdict.getContextBriefing().getLaborMarketRate()}</span>
                        </div>
                        <div class="w-full bg-slate-200 rounded-full h-1.5 mb-1">
                            <div class="bg-indigo-600 h-1.5 rounded-full" style="width: 85%"></div>
                        </div>
                        <p class="text-[10px] text-slate-400 bg-slate-50 p-1.5 rounded border border-slate-100">
                            ${verdict.getContextBriefing().getLaborMarketRateReason()}
                        </p>
                    </div>

                    <%-- Context Item 3: Era Feature --%>
                    <div>
                        <div class="flex justify-between items-center text-xs mb-1">
                            <span class="font-semibold text-slate-600">Construction Norms</span>
                            <span class="text-slate-900 font-bold">${verdict.getContextBriefing().getEraFeature()}</span>
                        </div>
                        <div class="w-full bg-slate-200 rounded-full h-1.5 mb-1">
                            <div class="bg-indigo-400 h-1.5 rounded-full" style="width: 60%"></div>
                        </div>
                        <p class="text-[10px] text-slate-400 bg-slate-50 p-1.5 rounded border border-slate-100">
                            ${verdict.getContextBriefing().getEraFeatureReason()}
                        </p>
                    </div>
                </div>
            </div>

             <%-- CARD 4: NEGOTIATION LEVERAGE (Money) --%>
            <div class="bento-card md:col-span-6 bg-gradient-to-br from-white to-slate-50">
                 <h3 class="text-sm font-bold text-slate-800 mb-4 flex items-center gap-2">
                    <span>🤝</span> Negotiation Power
                </h3>
                 <div class="bg-white border border-slate-200 rounded-xl p-4 shadow-sm mb-4">
                    <p class="text-xs text-slate-500 uppercase font-bold mb-1">Leverage Value</p>
                    <div class="text-3xl font-black text-slate-900 tracking-tight">
                        $${String.format("%,.0f", verdict.getExactCostEstimate() * 1.5)}
                    </div>
                    <p class="text-xs text-green-600 font-bold mt-1">
                        +50% buffer for unseen conditions
                    </p>
                 </div>
                 <div class="text-xs text-slate-500 leading-snug">
                    <p class="mb-2">Use this figure to negotiate credits. Copy the script below:</p>
                    <div class="bg-slate-100 p-2 rounded text-slate-600 font-mono text-[10px] border border-slate-200 select-all">
                        "Independent analysis indicates $${String.format("%,.0f", verdict.getExactCostEstimate() * 1.5)} in immediate correction exposure..."
                    </div>
                 </div>
            </div>

        </div>

    </main>

    <%-- 4. DETAILED BREAKDOWN (Secondary Info) --%>
    <section id="details-section" class="bg-white border-t border-slate-200 py-12 md:py-20">
         <div class="max-w-4xl mx-auto px-4 sm:px-6">
            <div class="text-center mb-12">
                <h3 class="text-2xl font-bold text-slate-900 tracking-tight">Technical Breakdown</h3>
                <p class="text-slate-500 mt-2">Itemized verification of the analysis.</p>
            </div>
            
            <%-- DIAGNOSTICS --%>
            @if(verdict.getSkippedStrategies() != null && !verdict.getSkippedStrategies().isEmpty())
                <details class="mb-8 group">
                    <summary class="flex items-center cursor-pointer text-xs font-bold text-slate-400 uppercase tracking-widest hover:text-slate-600 transition-colors">
                        <span class="mr-2">▶</span> Analysis Diagnostics (Skipped Strategies)
                    </summary>
                    <div class="mt-4 space-y-2 pl-4 border-l-2 border-slate-100">
                        @for(String skip : verdict.getSkippedStrategies())
                            <div class="text-xs text-slate-500 font-mono">
                                ${skip}
                            </div>
                        @endfor
                    </div>
                </details>
            @endif

            <%-- MUST DO --%>
            @if(verdict.getPlan().getMustDo() != null && !verdict.getPlan().getMustDo().isEmpty())
                <div class="mb-12">
                    <h4 class="text-sm font-bold text-rose-600 uppercase tracking-widest mb-6 border-b border-rose-100 pb-2">CRITICAL PRIORITY (Must Do)</h4>
                    <div class="space-y-4">
                        @for(RiskAdjustedItem item : verdict.getPlan().getMustDo())
                            <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center p-4 bg-white border border-slate-200 rounded-xl hover:border-rose-200 transition-colors">
                                <div>
                                    <div class="flex items-center gap-2">
                                        <span class="font-bold text-slate-800">${item.getPrettyName()}</span>
                                        @if(item.getIsForensicConfirmed())
                                            <span class="px-2 py-0.5 rounded-full bg-rose-100 text-rose-700 text-[10px] font-bold">FORENSIC MATCH</span>
                                        @endif
                                        @if(item.getIsCodeMandated())
                                            <span class="px-2 py-0.5 rounded-full bg-slate-100 text-slate-600 text-[10px] font-bold">CODE</span>
                                        @endif
                                    </div>
                                    <p class="text-sm text-slate-500 mt-1">${item.getExplanation()}</p>
                                </div>
                                <div class="mt-2 sm:mt-0 text-right">
                                    <span class="block font-bold text-slate-900">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                </div>
                            </div>
                        @endfor
                    </div>
                </div>
            @endif

            <div class="text-center pt-8">
                 <a href="/" class="inline-flex items-center justify-center px-6 py-3 border border-slate-300 shadow-sm text-base font-medium rounded-md text-slate-700 bg-white hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-slate-500">
                    Start New Analysis
                </a>
            </div>

         </div>
    </section>

</body>
</html>
