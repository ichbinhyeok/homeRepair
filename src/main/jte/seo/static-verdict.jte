@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink
@import java.util.List
@import java.util.Map

@param String title
@param String h1Content
@param String metroCode
@param String metroName
@param String era
@param String eraName
@param com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict verdict
@param List<InternalLink> eraLinks
@param List<InternalLink> cityLinks
@param String baseUrl
@param String canonicalUrl
@param String faqSchema
@param List<InternalLink> stateLinks
@param String climateFragment
@param String eraFragment
@param String costFragment
@param String regionalInsight
@param List<Map<String, String>> faqItems
@param String lowPrice
@param String highPrice
@param String breadcrumbSchema
@param String metroRisk
@param String climateZone
@param String foundation
@param String avgHouseAge
@param String howToSchema
@param String stateHubUrl = null
@param String stateName = null
@param String dateString

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>${title}</title>
    <meta name="description" content="Buying a ${eraName} home in ${metroName}. Review repair costs and expert negotiation tips for 2026." />
    
    <link rel="canonical" href="${canonicalUrl}" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Outfit:wght@500;700;800&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="/css/seo-report.css" />
    
    <!-- Open Graph -->
    <meta property="og:type" content="article" />
    <meta property="og:title" content="${title}" />
    <meta property="og:description" content="Forensic cost analysis for ${eraName} homes in ${metroName}. 2026 Market Data." />
    <meta property="og:url" content="${canonicalUrl}" />
    
    $unsafe{faqSchema}
    $unsafe{breadcrumbSchema}
    $unsafe{howToSchema}
</head>
<body>
    @template.layout._header()

    <header class="report-header">
        <div class="container">
            <div class="breadcrumbs">
                <a href="/">Home</a> 
                <span class="separator">/</span>
                <a href="/home-repair">Market Data</a>
                <span class="separator">/</span>
                @if(stateHubUrl != null)
                    <a href="${stateHubUrl}">${stateName}</a>
                    <span class="separator">/</span>
                @endif
                <span class="current">${metroName}</span>
            </div>
            
            <span class="badge-market">Market Data: ${dateString}</span>
            <h1 class="title-h1">${h1Content}</h1>
            <p class="subtitle">A forensic analysis of hidden liabilities and repair costs for ${eraName} properties in the ${metroName} area (Updated ${dateString}).</p>
            
            <div style="margin-top: 2rem;">
                <a href="/home-repair" class="btn-primary" style="background: white; color: var(--primary);">
                    Check Specific Address &rarr;
                </a>
                <p style="margin-top: 1rem; font-size: 0.85rem; opacity: 0.7;">
                    <span style="opacity: 0.8;">Data sources:</span> RSMeans, BLS, NAHB
                </p>
            </div>
        </div>
    </header>

    <main class="container" style="margin-top: -3rem; position: relative; z-index: 10;">
        <div class="bento-grid">
            <!-- 1. EXECUTIVE SUMMARY CARD -->
            <div class="bento-card span-8">
                <div class="stat-banner">
                    <div class="stat-item">
                        <span class="stat-val text-danger">${verdict.getPlan().getMustDo().size()}</span>
                        <span class="stat-label">Critical Risks</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-val">$${lowPrice}</span>
                        <span class="stat-label">Min. Repairs</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-val">$${highPrice}</span>
                        <span class="stat-label">Est. Liability</span>
                    </div>
                </div>
                
                <div style="margin-top: 2rem; padding-top: 2rem; border-top: 1px solid var(--slate-100); text-align: center;">
                     <p style="color: var(--slate-500); margin-bottom: 1rem;">
                        Homebuyers in <strong>${metroName}</strong> typically underestimate repair costs by <strong>30-40%</strong>. 
                        Use this data to negotiate.
                     </p>
                </div>
            </div>

            <!-- 2. NEIGHBOR COMPARISON (COMPS) -->
            @if(verdict.getComparisonData() != null)
                <div class="bento-card span-4" style="background: var(--slate-900); color: white; border: none; position: relative; overflow: hidden;">
                    <!-- Background gradient effect -->
                    <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: radial-gradient(circle at top right, rgba(255,255,255,0.05), transparent 60%); pointer-events: none;"></div>
                    
                    <span class="stat-label" style="color: var(--slate-400);">VS. Modern Homes</span>
                    
                    <h3 style="font-size: 3.5rem; color: white; margin: 1rem 0;">
                        @if(verdict.getComparisonData().getCostDelta() > 0)
                            <span style="color: #fca5a5;">+${String.format("%.0f", verdict.getComparisonData().getDeltaPercentage())}%</span>
                        @else
                            <span style="color: #6ee7b7;">${String.format("%.0f", verdict.getComparisonData().getDeltaPercentage())}%</span>
                        @endif
                    </h3>
                    
                    <p style="color: var(--slate-300); font-size: 0.95rem; line-height: 1.6;">
                        Comparing ${eraName} homes to 2010+ builds in ${metroName}. 
                        @if(verdict.getComparisonData().getCostDelta() > 0)
                            You are taking on 
                            <strong style="color: white; border-bottom: 1px dashed var(--slate-500);">$${String.format("%,.0f", verdict.getComparisonData().getCostDelta())}</strong> 
                            in inherited liability.
                        @else
                            This home has a 
                            <strong style="color: #6ee7b7; border-bottom: 1px dashed var(--slate-500);">$${String.format("%,.0f", Math.abs(verdict.getComparisonData().getCostDelta()))}</strong> 
                            cost advantage vs new builds.
                        @endif
                    </p>
                </div>
            @else
                <!-- Fallback if no comparison data -->
                <div class="bento-card span-4">
                    <span class="stat-label">Market Intelligence</span>
                    <p style="margin-top: 1rem;">Detailed cost comparison data is processing.</p>
                </div>
            @endif

            <!-- 3. METRO & CLIMATE INTELLIGENCE (NEW PHASE 2) -->
            <div class="bento-card span-12 insight-card">
                <div class="insight-header">
                    <span class="insight-icon">🌍</span>
                    <h3>Local Market Factors: ${metroName}</h3>
                </div>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem;">
                    <div>
                        <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 0.5rem;">Regional Risk</h4>
                        <p style="font-weight: 600; font-size: 1.1rem; color: var(--slate-900);">${metroRisk != null ? metroRisk : "Standard Regional Risks"}</p>
                    </div>
                    <div>
                        <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 0.5rem;">Climate Zone</h4>
                        <p style="font-weight: 600; font-size: 1.1rem; color: var(--slate-900);">Zone ${climateZone != null ? climateZone : "US-Standard"}</p>
                        <p style="font-size: 0.85rem; color: var(--slate-500); margin-top: 0.25rem;">Impacts HVAC & insulation lifespan.</p>
                    </div>
                    <div>
                        <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 0.5rem;">Foundation Type</h4>
                        <p style="font-weight: 600; font-size: 1.1rem; color: var(--slate-900);">${foundation != null ? foundation : "Mixed Types"}</p>
                    </div>
                </div>
                <div style="margin-top: 1.5rem; padding: 1.5rem; background: rgba(255,255,255,0.6); border-radius: var(--radius-lg);">
                    <p style="color: var(--slate-600); font-style: italic;">
                        "${regionalInsight}"
                    </p>
                </div>
            </div>

            <!-- 4. CRITICAL RISK AUDIT (DYNAMIC LINKS) -->
            <div class="bento-card span-12">
                <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 2rem;">
                    <h3>Forensic Risk Audit</h3>
                    <span style="font-size: 0.9rem; color: var(--slate-500);">Verify these items immediately</span>
                </div>
                
                <div class="risk-list">
                    @for(var item : verdict.getPlan().getMustDo())
                        <div class="risk-row">
                            <div class="risk-info">
                                <h4>${item.getPrettyName()}</h4>
                                
                                <!-- Definition & Scenario (Phase 2 Data) -->
                                <p class="text-sm text-slate-500 leading-snug" style="margin-bottom: 0.5rem;">
                                    $unsafe{item.getExplanation() != null ? item.getExplanation() : item.getDefinition()}
                                </p>
                                
                                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                    @if(item.getDamageScenario() != null)
                                        <span style="font-size: 0.75rem; background: var(--slate-100); padding: 0.25rem 0.5rem; border-radius: 4px; color: var(--slate-600);">
                                            ⚠️ ${item.getDamageScenario()}
                                        </span>
                                    @endif
                                    
                                    @for(String flag : item.getRiskFlags())
                                        <span style="font-size: 0.75rem; background: #fff1f2; color: #e11d48; padding: 0.25rem 0.5rem; border-radius: 4px; border: 1px solid #fecdd3;">
                                            ${flag.replace("_", " ")}
                                        </span>
                                    @endfor
                                </div>
                            </div>
                            
                            <div class="risk-cost">
                                <span class="amount">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                <a href="/home-repair/verdicts/${metroCode.toLowerCase().replace("_", "-")}/${era.toLowerCase().replace("_", "-")}/${item.getItemCode().toLowerCase().replace("_", "-")}.html" class="action-link">
                                    View Details &rarr;
                                </a>
                            </div>
                        </div>
                    @endfor
                </div>
            </div>
            
            <!-- 5. TIMELINE (VISUAL) -->
            <div class="bento-card span-6">
                <h3>System Lifespan Projection</h3>
                <div class="timeline-box">
                    @for(RiskAdjustedItem item : verdict.getPlan().getMustDo())
                       <div class="timeline-item">
                            <div class="dot" style="background: var(--danger);"></div>
                            <div style="flex-grow: 1;">
                                <div style="display: flex; justify-content: space-between;">
                                    <span style="font-weight: 600;">${item.getPrettyName()}</span>
                                    <span style="font-size: 0.85rem; color: var(--danger); font-weight: 600;">Due Now</span>
                                </div>
                                <div style="height: 6px; background: var(--slate-100); border-radius: 100px; margin-top: 4px; overflow: hidden;">
                                    <div style="width: 100%; height: 100%; background: var(--danger);"></div>
                                </div>
                            </div>
                       </div>
                    @endfor
                    <!-- Show some stable items to contrast -->
                     <div class="timeline-item">
                        <div class="dot" style="background: var(--success);"></div>
                        <div style="flex-grow: 1;">
                            <div style="display: flex; justify-content: space-between;">
                                <span style="font-weight: 600;">Foundation Structure</span>
                                <span style="font-size: 0.85rem; color: var(--success); font-weight: 600;">Stable</span>
                            </div>
                            <div style="height: 6px; background: var(--slate-100); border-radius: 100px; margin-top: 4px;">
                                <div style="width: 25%; height: 100%; background: var(--success);"></div>
                            </div>
                        </div>
                   </div>
                </div>
            </div>
            
            <!-- 6. PRE-EMPTIVE CTA -->
            <div class="bento-card span-6" style="background: var(--primary); color: white; display: flex; flex-direction: column; justify-content: center; text-align: center;">
                <h3 style="color: white; margin-bottom: 1rem;">Get the Full Picture</h3>
                <p style="color: rgba(255,255,255,0.9); margin-bottom: 2rem;">
                    Generic era data isn't enough. Get a custom audit for a specific address, tuned to its exact permit history.
                </p>
                <a href="/home-repair" class="btn-primary" style="background: white; color: var(--primary);">
                    Run Free Address Check
                </a>
            </div>

        </div>

        <!-- 7. FAQ SECTION -->
        <section style="margin-top: 6rem; max-width: 800px; margin-left: auto; margin-right: auto;">
            <h2 style="text-align: center; margin-bottom: 3rem;">Frequently Asked Questions</h2>
            
            @for(var faq : faqItems)
                <div class="faq-item">
                    <div class="faq-question">
                        <span class="faq-icon">?</span>
                        ${faq.get("question")}
                    </div>
                    <div class="faq-answer">
                        ${faq.get("answer")}
                    </div>
                </div>
            @endfor
        </section>

        <!-- 8. INTERNAL LINKS HUB -->
        <div class="bento-card span-12" style="margin-top: 4rem; background: transparent; border: none; box-shadow: none;">
            <h3 style="margin-bottom: 1.5rem;">Explore Other Markets</h3>
            <div class="link-hub" style="display: flex; flex-wrap: wrap; gap: 1rem;">
                <!-- State Links (Priority) -->
                @for(var link : stateLinks)
                     <a href="${link.getHref()}" class="btn-outline" style="font-size: 0.85rem; padding: 0.5rem 1rem;">${link.getText()}</a>
                @endfor
            </div>
            
            <h4 style="margin: 2rem 0 1rem; color: var(--slate-500); font-size: 1rem; text-transform: uppercase;">Nearby Cities</h4>
             <div class="link-hub" style="display: flex; flex-wrap: wrap; gap: 0.75rem;">
                @for(var link : cityLinks)
                     <a href="${link.getHref()}" style="color: var(--slate-500); font-size: 0.9rem;">${link.getText()}</a>
                @endfor
            </div>
        </div>
    </main>
    
    <!-- Sticky Mobile CTA -->
    <div class="sticky-cta cta-floater">
        <span>Buying in ${metroName}?</span>
        <a href="/home-repair" class="btn-sm">Check Cost &rarr;</a>
    </div>

    @template.layout._footer()
</body>
</html>
