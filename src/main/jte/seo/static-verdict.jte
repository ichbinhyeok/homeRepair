@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink
@import com.livingcostcheck.home_repair.util.TextUtil
@import java.util.List
@import java.util.Map

@param String title
@param String h1Content
@param String metroCode
@param String metroName
@param String msaName = null
@param String era
@param String eraName
@param com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict verdict
@param List<InternalLink> eraLinks
@param List<InternalLink> cityLinks
@param String baseUrl
@param String canonicalUrl
@param String calculatorUrl = "/home-repair"
@param String robotsDirective = "index,follow"
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
@param Integer femaDisasterCount = null
@param Double ownerOccupancyRate = null
@param Integer medianYearBuilt = null
@param Double repairPressureIndex = null
@param String openDataCsvUrl = null

@param String stateHubUrl = null
@param String stateName = null
@param String dateString

<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Google tag (gtag.js) -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=G-JV6YLYZHN8"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'G-JV6YLYZHN8');
    </script>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>${title}</title>
    <meta name="description" content="Get a seller credit range after inspection for ${eraName} homes in ${metroName}. See likely repair costs ($${lowPrice}-$${highPrice}), what to verify first, and what belongs in the repair request before you close." />
    <meta name="robots" content="${robotsDirective}" />
    
    <link rel="canonical" href="${canonicalUrl}" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Outfit:wght@500;700;800&display=swap" rel="stylesheet" />
    
    <!-- Preload critical fonts to improve LCP/CLS -->
    <link rel="preload" href="https://fonts.googleapis.com/css2?family=Outfit:wght@700&display=swap" as="style" />
    <link rel="preload" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" as="style" />
    
    <link rel="stylesheet" href="/css/seo-report.css" />
    
    <!-- Open Graph -->
    <meta property="og:type" content="article" />
    <meta property="og:title" content="${title}" />
    <meta property="og:description" content="Seller credit range after inspection for ${eraName} homes in ${metroName}." />
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
                <a href="/home-repair">Inspection Negotiation</a> 
                <span class="separator">/</span>
                @if(stateHubUrl != null)
                    <a href="${stateHubUrl}">${stateName}</a>
                    <span class="separator">/</span>
                @endif
                <span class="current">${metroName}</span>
            </div>
            
            <span class="badge-market">Seller-credit packet guide: ${dateString}</span>
            <h1 class="title-h1">${h1Content}</h1>
            <p class="subtitle">Use local repair math and inspection priorities to decide what belongs in the seller-credit request, what to verify first, and how hard to push before closing on a ${eraName} property in ${metroName} (Updated ${dateString}).</p>
            
            <div style="margin-top: 2rem;">
                <a href="${calculatorUrl}" class="btn-primary" style="background: white; color: var(--primary);" data-analytics-event="cta_customize_estimate" data-analytics-category="conversion" data-analytics-label="l1_header">
                    Start Seller Credit Plan &rarr;
                </a>
                <p style="margin-top: 1rem; font-size: 0.85rem; opacity: 0.7;">
                    <span style="opacity: 0.8;">Data sources:</span> RSMeans, BLS, Census ACS, FEMA OpenFEMA
                </p>
            </div>
        </div>
    </header>

    <main id="main-content" class="container" style="margin-top: -3rem; position: relative; z-index: 10;" role="main">
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
                        Repair costs vary widely by property condition and bids in <strong>${metroName}</strong>.
                        Use these benchmarks to decide what belongs in the repair request, what to quote first, and what credit to request before closing.
                     </p>
                </div>
            </div>
            


            <!-- 2. NEIGHBOR COMPARISON (COMPS) -->
            @if(verdict.getComparisonData() != null)
                <div class="bento-card span-4" style="background: var(--slate-900); color: white; border: none; position: relative; overflow: hidden;">
                    <!-- Background gradient effect -->
                    <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: radial-gradient(circle at top right, rgba(255,255,255,0.05), transparent 60%); pointer-events: none;"></div>
                    
                    <span class="stat-label" style="color: var(--slate-400);">Negotiation Context</span>
                    
                    <h3 class="text-delta-responsive" style="color: white; margin: 1rem 0;">
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
                    <span class="stat-label">Negotiation Context</span>
                    <p style="margin-top: 1rem;">Local leverage data is still processing for this market.</p>
                </div>
            @endif

            <!-- 3. METRO & CLIMATE INTELLIGENCE (NEW PHASE 2) -->
            <div class="bento-card span-12 insight-card">
                <div class="insight-header">
                    <span class="insight-icon">🌍</span>
                    <h3>What Changes The Ask In ${metroName}</h3>
                </div>
                <!-- Responsive Grid for Insight -->
                <style>
                    .insight-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 2rem; }
                    .text-delta-responsive { font-size: 3.5rem; }
                    @media (max-width: 640px) {
                        .insight-grid { grid-template-columns: 1fr; gap: 1.5rem; }
                        .text-delta-responsive { font-size: 2.25rem; }
                    }
                </style>
                <div class="insight-grid">
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
                @if(femaDisasterCount != null || ownerOccupancyRate != null || medianYearBuilt != null || repairPressureIndex != null)
                <div style="margin-top: 1.5rem; padding: 1.25rem; background: #f8fafc; border: 1px solid var(--slate-200); border-radius: var(--radius-md);">
                    <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 1rem;">Local negotiation signals (2016-2026)</h4>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem;">
                        <div>
                            <p style="font-size: 0.8rem; color: var(--slate-500); margin-bottom: 0.2rem;">MSA Coverage</p>
                            <p style="font-weight: 700; color: var(--slate-900);">${msaName != null ? msaName : (metroName + " MSA")}</p>
                        </div>
                        @if(femaDisasterCount != null)
                        <div>
                            <p style="font-size: 0.8rem; color: var(--slate-500); margin-bottom: 0.2rem;">FEMA Major Disasters</p>
                            <p style="font-weight: 700; color: var(--slate-900);">${femaDisasterCount}</p>
                        </div>
                        @endif
                        @if(ownerOccupancyRate != null)
                        <div>
                            <p style="font-size: 0.8rem; color: var(--slate-500); margin-bottom: 0.2rem;">Owner Occupancy (State)</p>
                            <p style="font-weight: 700; color: var(--slate-900);">${String.format("%.1f", ownerOccupancyRate)}%</p>
                        </div>
                        @endif
                        @if(medianYearBuilt != null)
                        <div>
                            <p style="font-size: 0.8rem; color: var(--slate-500); margin-bottom: 0.2rem;">Median Year Built (State)</p>
                            <p style="font-weight: 700; color: var(--slate-900);">${medianYearBuilt}</p>
                        </div>
                        @endif
                        @if(repairPressureIndex != null)
                        <div>
                            <p style="font-size: 0.8rem; color: var(--slate-500); margin-bottom: 0.2rem;">Repair Pressure Index</p>
                            <p style="font-weight: 700; color: var(--slate-900);">${String.format("%.1f", repairPressureIndex)} / 100</p>
                        </div>
                        @endif
                    </div>
                    @if(openDataCsvUrl != null)
                    <p style="margin-top: 0.85rem; font-size: 0.82rem; color: var(--slate-500);">
                        Source file: <a href="${openDataCsvUrl}" style="color: var(--primary);">metro_unique_signals_2026.csv</a> (unit: MSA)
                    </p>
                    @endif
                </div>
                @endif
                <div style="margin-top: 1.5rem; padding: 1.5rem; background: rgba(255,255,255,0.6); border-radius: var(--radius-lg);">
                    <p style="color: var(--slate-600); font-style: italic;">
                        "${regionalInsight}"
                    </p>
                </div>
            </div>

            <!-- 4. CRITICAL RISK AUDIT (DYNAMIC LINKS) -->
            <div class="bento-card span-12">
                <div class="risk-audit-header" style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
                    <h3 style="margin-bottom: 0;">Inspection Negotiation Checklist</h3>
                    <span style="font-size: 0.9rem; color: var(--slate-500);">Verify these items before final credits</span>
                </div>
                
                <!-- BLUF: Bottom Line Up Front (AEO Target) -->
                <div style="background: linear-gradient(135deg, #eff6ff, #f0fdf4); border-left: 4px solid var(--primary); padding: 1.5rem 2rem; border-radius: 0 var(--radius-md) var(--radius-md) 0; margin-bottom: 2rem;">
                    <p style="font-size: 1.05rem; font-weight: 600; color: var(--slate-900); margin-bottom: 0.5rem;">
                        📋 Bottom Line for Buyers:
                    </p>
                    <p style="color: var(--slate-700); line-height: 1.7;">
                        A ${eraName} home in ${metroName} carries an estimated <strong>$${lowPrice}&ndash;$${highPrice}</strong> in post-inspection repair burden across <strong>${verdict.getPlan().getMustDo().size()} critical systems</strong>. Use this range to separate documentation items from true repair credits before closing. These figures reflect ${dateString} ${metroName}-area labor rates and material pricing.
                    </p>
                </div>

                <!-- AEO (Answer Engine Optimization) Table -->
                <div style="margin-bottom: 2rem; overflow-x: auto;">
                    <table style="width: 100%; border-collapse: collapse; background: white; border: 1px solid var(--slate-200); text-align: left; border-radius: var(--radius-md); overflow: hidden;">
                        <thead>
                            <tr style="background: var(--slate-50); border-bottom: 2px solid var(--slate-200);">
                                <th style="padding: 1rem; color: var(--slate-700); font-weight: 700;">Inspection Item</th>
                                <th style="padding: 1rem; color: var(--slate-700); font-weight: 700;">Repair Budget</th>
                                <th style="padding: 1rem; color: var(--slate-700); font-weight: 700;">Credit Priority</th>
                            </tr>
                        </thead>
                        <tbody>
                            @for(var item : verdict.getPlan().getMustDo())
                            <tr style="border-bottom: 1px solid var(--slate-100);">
                                <td style="padding: 1rem; font-weight: 600; color: var(--slate-900);">${item.getPrettyName()}</td>
                                <td style="padding: 1rem; color: var(--slate-700); font-weight: 500;">$${String.format("%,.0f", item.getAdjustedCost())}</td>
                                <td style="padding: 1rem; color: #e11d48; font-size: 0.85rem; font-weight: 600;">${item.getRiskFlags().size() > 0 ? "High" : "Moderate"}</td>
                            </tr>
                            @endfor
                        </tbody>
                    </table>
                </div>
                
                <div class="risk-list">
                    @for(var item : verdict.getPlan().getMustDo())
                        <div class="risk-row">
                            <div class="risk-info">
                                <h4>${item.getPrettyName()}</h4>
                                
                                <!-- Definition & Scenario (Phase 2 Data) -->
                                <p class="text-sm text-slate-500 leading-snug" style="margin-bottom: 0.5rem;">
                                    ${TextUtil.sanitizeUserFacingExplanation(item.getExplanation(), item.getDefinition())}
                                </p>
                                
                                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                    @if(item.getDamageScenario() != null)
                                        <span style="font-size: 0.75rem; background: var(--slate-100); padding: 0.25rem 0.5rem; border-radius: 4px; color: var(--slate-600);">
                                            ⚠️ ${item.getDamageScenario()}
                                        </span>
                                    @endif
                                    
                                    @for(String flag : TextUtil.toUserFacingRiskFlags(item.getRiskFlags()))
                                        <span style="font-size: 0.75rem; background: #fff1f2; color: #e11d48; padding: 0.25rem 0.5rem; border-radius: 4px; border: 1px solid #fecdd3;">
                                            ${flag}
                                        </span>
                                    @endfor
                                </div>
                            </div>
                            
                            <div class="risk-cost">
                                <span class="amount">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                <span style="font-size: 0.85rem; color: var(--slate-500);">
                                    Use the inspection report and one quote to support this line item.
                                </span>
                            </div>
                        </div>
                    @endfor
                </div>
            </div>

            <!-- EDITORIAL ANALYSIS: Era + Climate + Cost Context (HCU Differentiation) -->
            <div class="bento-card span-12" style="border-top: 3px solid var(--primary);">
                <h3 style="margin-bottom: 1.5rem;">Why ${eraName} Homes in ${metroName} Create Seller-Credit Pressure</h3>
                
                <div style="display: grid; gap: 1.5rem;">
                    <!-- Era-Specific Analysis -->
                    <div style="padding: 1.25rem; background: var(--slate-50); border-radius: var(--radius-md);">
                        <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 0.75rem; letter-spacing: 0.05em;">🏗️ Construction Era Analysis</h4>
                        <p style="color: var(--slate-700); line-height: 1.8;">${eraFragment}</p>
                    </div>
                    
                    <!-- Climate Impact -->
                    <div style="padding: 1.25rem; background: var(--slate-50); border-radius: var(--radius-md);">
                        <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 0.75rem; letter-spacing: 0.05em;">🌡️ Climate Impact on ${metroName} Homes</h4>
                        <p style="color: var(--slate-700); line-height: 1.8;">${climateFragment}</p>
                    </div>
                    
                    <!-- Cost Economics -->
                    <div style="padding: 1.25rem; background: var(--slate-50); border-radius: var(--radius-md);">
                        <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--slate-500); margin-bottom: 0.75rem; letter-spacing: 0.05em;">💰 Local negotiation economics</h4>
                        <p style="color: var(--slate-700); line-height: 1.8;">${costFragment}</p>
                    </div>
                </div>
            </div>
            
            <div class="bento-card span-12 insight-card" style="margin-bottom: 2rem;">
                <h3 style="margin-bottom: 1rem;">What Belongs In The Repair Request</h3>
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 1rem;">
                    <div style="padding: 1rem; background: white; border-radius: var(--radius-md); border: 1px solid var(--slate-200);">
                        <strong style="display: block; margin-bottom: 0.35rem;">Safety and financing blockers</strong>
                        <span style="color: var(--slate-600); font-size: 0.92rem;">Electrical, active leaks, structural movement, plumbing failures, and anything a lender or insurer will flag.</span>
                    </div>
                    <div style="padding: 1rem; background: white; border-radius: var(--radius-md); border: 1px solid var(--slate-200);">
                        <strong style="display: block; margin-bottom: 0.35rem;">Quote-backed system repairs</strong>
                        <span style="color: var(--slate-600); font-size: 0.92rem;">Use one contractor quote to anchor the ask for big-ticket systems rather than stacking every cosmetic item.</span>
                    </div>
                    <div style="padding: 1rem; background: white; border-radius: var(--radius-md); border: 1px solid var(--slate-200);">
                        <strong style="display: block; margin-bottom: 0.35rem;">Defer cosmetic upgrades</strong>
                        <span style="color: var(--slate-600); font-size: 0.92rem;">Paint, finishes, and elective updates should stay out of the main seller-credit packet unless they hide a safety issue.</span>
                    </div>
                </div>
            </div>
            
            <!-- 5. TIMELINE (VISUAL) -->
            <div class="bento-card span-6">
                <h3>What to Verify First</h3>
                <div class="timeline-box">
                    @for(RiskAdjustedItem item : verdict.getPlan().getMustDo())
                       <div class="timeline-item">
                            <div class="dot" style="background: var(--danger);"></div>
                            <div style="flex-grow: 1;">
                                <div style="display: flex; justify-content: space-between;">
                                    <span style="font-weight: 600;">${item.getPrettyName()}</span>
                                    <span style="font-size: 0.85rem; color: var(--danger); font-weight: 600;">Quote Now</span>
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
                <h3 style="color: white; margin-bottom: 1rem;">Turn This Inspection Context Into A Real Credit Ask</h3>
                <p style="color: rgba(255,255,255,0.9); margin-bottom: 2rem;">
                    This guide is only the framing layer. Add the actual inspection findings to tighten the ask, narrow the fallback, and build a cleaner seller-credit packet.
                </p>
                <a href="${calculatorUrl}" class="btn-primary" style="background: white; color: var(--primary);" data-analytics-event="cta_customize_estimate" data-analytics-category="conversion" data-analytics-label="l1_body">
                    Build My Credit Range &rarr;
                </a>
            </div>

        </div>

        <!-- 7. FAQ SECTION -->
        <section style="margin-top: 6rem; max-width: 800px; margin-left: auto; margin-right: auto;">
            <h2 style="text-align: center; margin-bottom: 3rem;">Buyer Questions After Inspection</h2>
            
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
        @if(!stateLinks.isEmpty() || !cityLinks.isEmpty())
            <div class="bento-card span-12" style="margin-top: 4rem; background: transparent; border: none; box-shadow: none;">
                <h3 style="margin-bottom: 1.5rem;">Explore Similar Negotiation Markets</h3>
                @if(!stateLinks.isEmpty())
                    <div class="link-hub" style="display: flex; flex-wrap: wrap; gap: 1rem;">
                        @for(var link : stateLinks)
                             <a href="${link.getHref()}" class="btn-outline" style="font-size: 0.85rem; padding: 0.5rem 1rem;">${link.getText()}</a>
                        @endfor
                    </div>
                @endif
                
                @if(!cityLinks.isEmpty())
                    <h4 style="margin: 2rem 0 1rem; color: var(--slate-500); font-size: 1rem; text-transform: uppercase;">Related Regional Markets</h4>
                    <div class="link-hub" style="display: flex; flex-wrap: wrap; gap: 0.75rem;">
                        @for(var link : cityLinks)
                             <a href="${link.getHref()}" style="color: var(--slate-500); font-size: 0.9rem;">${link.getText()}</a>
                        @endfor
                    </div>
                @endif
            </div>
        @endif
    </main>
    
    <!-- Sticky Mobile CTA -->
    <div class="sticky-cta cta-floater">
        <span>Buying in ${metroName}?</span>
        <a href="${calculatorUrl}" class="btn-sm" data-analytics-event="cta_customize_estimate" data-analytics-category="conversion" data-analytics-label="l1_sticky">Build Credit Plan &rarr;</a>
    </div>

    @template.layout._footer()
</body>
</html>
