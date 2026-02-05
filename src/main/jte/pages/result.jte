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

                <!-- Neighbor Market Comparison -->
                @if(verdict.getComparisonData() != null)
                <div class="result-card p-0 overflow-hidden border-0 shadow-sm mb-5" style="border-radius: 32px; background: var(--slate-50);">
                    <div class="row g-0">
                        <div class="col-md-7 p-5">
                            <div class="d-flex align-items-center mb-3">
                                <span class="badge bg-white text-primary border px-3 py-2" style="font-size: 0.75rem; font-weight: 800; border-radius: 99px;">2026 MARKET BENCHMARK</span>
                            </div>
                            <h3 class="display-5 fw-bold mb-3" style="font-family: 'Outfit'; color: var(--slate-900);">
                                <span class="text-danger">+${String.format("%.0f", verdict.getComparisonData().getDeltaPercentage())}%</span> Liability
                            </h3>
                            <p class="lead text-muted mb-4" style="font-size: 1.15rem;">
                                This property carries a significantly higher repair liability compared to a modern benchmark home built in the 2010+ era in this area.
                            </p>
                            <div class="p-4 bg-white rounded-4 border border-dashed">
                                <p class="mb-0 text-muted" style="font-size: 0.95rem;">
                                    <strong>Comps Gap:</strong> 1950s homes in this zip code inherit an average of 
                                    <span class="text-dark fw-bold">$${String.format("%,.0f", verdict.getComparisonData().getCostDelta())}</span> 
                                    more in statistical risk than modern builds.
                                </p>
                            </div>
                        </div>
                        <div class="col-md-5 d-none d-md-flex align-items-center justify-content-center border-start bg-white" style="position: relative; overflow: hidden;">
                            <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 150%; height: 150%; opacity: 0.03; font-family: 'Outfit'; font-weight: 900; font-size: 20rem; color: var(--slate-900); pointer-events: none; user-select: none;">COMPS</div>
                            <div class="text-center">
                                <div class="h6 text-uppercase text-muted mb-2 ls-wider">Benchmark</div>
                                <div class="h2 fw-bold text-dark">$${String.format("%,.0f", verdict.getComparisonData().getModernBenchmarkCost())}</div>
                                <div class="text-muted small">2010+ Modern Baseline</div>
                            </div>
                        </div>
                    </div>
                </div>
                @endif

                <!-- System Integrity Timeline -->
                @if(verdict.getPlan() != null && verdict.getPlan().getMustDo() != null)
                <div class="result-card p-5 mb-5 shadow-sm border-0" style="border-radius: 32px; background: white;">
                    <div class="d-flex align-items-center mb-5">
                        <div class="bg-primary rounded-circle p-2 me-3 d-flex align-items-center justify-content-center" style="width: 48px; height: 48px;">
                            <i class="bi bi-clock-history text-white h4 mb-0"></i>
                        </div>
                        <div>
                            <h3 class="mb-1 fw-bold" style="font-family: 'Outfit';">System Integrity Timeline</h3>
                            <p class="mb-0 text-muted">Estimated remaining component lifespans for this property</p>
                        </div>
                    </div>

                    <div class="row g-4">
                        @for(var item : verdict.getPlan().getMustDo())
                            @if(item.getRiskFlags() != null && (item.getRiskFlags().stream().anyMatch(f -> f.contains("DEAD") || f.contains("WATCH") || f.contains("VERIFIED"))))
                            <div class="col-md-6 col-lg-4">
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
                                    <p class="small text-muted mb-0">
                                        ${item.getExplanation() != null && item.getExplanation().contains(".") ? item.getExplanation().split("\\.")[0] + "." : item.getExplanation()}
                                    </p>
                                </div>
                            </div>
                            @endif
                        @endfor
                        
                        <!-- Always show some stability for balance -->
                        <div class="col-md-6 col-lg-4">
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
                @endif

                <div class="mt-16 text-center">
                     <p class="text-xs text-slate-400 mb-4">Calculations generated at ${new java.util.Date().toString()}</p>
                     <a href="/" class="text-sm font-semibold text-blue-600 hover:text-black transition">Start Another Analysis &rarr;</a>
                </div>
            </div>
        `
)
