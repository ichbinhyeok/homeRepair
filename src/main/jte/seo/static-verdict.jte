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
@param Verdict verdict
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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <title>${title}</title>
    <meta name="description" content="Forensic cost analysis for ${eraName} homes in ${metroName}. Discover hidden liabilities, life expectancy of key systems, and negotiation leverage points for 2026.">
    
    <link rel="canonical" href="${canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&family=Inter:wght@400;600&display=swap" rel="stylesheet">
    
    <!-- Open Graph -->
    <meta property="og:type" content="article">
    <meta property="og:title" content="${title}">
    <meta property="og:url" content="${canonicalUrl}">
    
    $unsafe{faqSchema}
    $unsafe{breadcrumbSchema}

    <style>
        :root {
            --primary: #0f172a;
            --accent: #2563eb;
            --danger: #ef4444;
            --warning: #f59e0b;
            --success: #10b981;
            --slate-50: #f8fafc;
            --slate-100: #f1f5f9;
            --slate-200: #e2e8f0;
            --slate-400: #94a3b8;
            --slate-600: #475569;
            --slate-900: #0f172a;
        }
        
        body { 
            background: #fff; 
            color: var(--slate-900); 
            font-family: 'Inter', sans-serif; 
            margin: 0; 
            -webkit-font-smoothing: antialiased;
        }

        h1, h2, h3 { font-family: 'Outfit', sans-serif; letter-spacing: -0.02em; }
        
        .report-header { 
            background: linear-gradient(135deg, white 0%, var(--slate-50) 100%);
            color: var(--slate-900); 
            padding: 80px 20px; 
            text-align: center;
            position: relative;
            overflow: hidden;
            border-bottom: 1px solid var(--slate-200);
        }
        .report-header::before {
            content: ""; position: absolute; inset: 0;
            background-image: radial-gradient(var(--slate-200) 1px, transparent 1px);
            background-size: 30px 30px;
            opacity: 0.5;
        }

        .container { max-width: 1100px; margin: 0 auto; padding: 0 24px; }
        
        .badge-market { background: var(--slate-100); color: var(--accent); padding: 8px 16px; border-radius: 99px; font-size: 0.75rem; font-weight: 800; text-transform: uppercase; margin-bottom: 24px; display: inline-block; border: 1px solid var(--slate-200); }
        .title-h1 { font-size: 3.5rem; line-height: 1.1; margin: 0 auto 24px; max-width: 800px; font-weight: 800; color: var(--slate-900); }
        .subtitle { font-size: 1.25rem; color: var(--slate-600); max-width: 650px; margin: 0 auto; line-height: 1.6; }

        /* Bento Grid */
        .bento-grid { 
            display: grid; 
            grid-template-columns: repeat(12, 1fr); 
            gap: 24px; 
            margin-top: -40px; 
            position: relative; 
            z-index: 10; 
        }
        .bento-card { 
            background: white; 
            border-radius: 32px; 
            padding: 40px; 
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05), 0 4px 6px -2px rgba(0,0,0,0.05);
            border: 1px solid var(--slate-200);
        }
        .span-12 { grid-column: span 12; }
        .span-8 { grid-column: span 12; }
        .span-4 { grid-column: span 12; }

        @media (min-width: 768px) {
            .span-8 { grid-column: span 8; }
            .span-4 { grid-column: span 4; }
            .title-h1 { font-size: 4.5rem; }
        }

        /* Stats Card */
        .stat-banner { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
        @media (min-width: 768px) { .stat-banner { grid-template-columns: repeat(4, 1fr); } }
        .stat-item { text-align: center; }
        .stat-val { font-size: 2.5rem; font-weight: 800; font-family: 'Outfit'; display: block; }
        .stat-label { font-size: 0.7rem; color: var(--slate-400); text-transform: uppercase; font-weight: 700; }

        /* Comps Section (Light Style) */
        .comps-card { background: var(--slate-50); border: 1px solid var(--slate-200); overflow: hidden; position: relative; }
        .comps-delta { font-size: 4rem; font-weight: 800; color: var(--danger); line-height: 1; }
        
        /* Timeline */
        .timeline-box { display: flex; flex-direction: column; gap: 24px; margin-top: 32px; }
        .timeline-item { display: flex; gap: 16px; align-items: flex-start; }
        .dot { width: 12px; height: 12px; border-radius: 50%; margin-top: 6px; flex-shrink: 0; }
        
        /* Action Table */
        .risk-list { margin-top: 40px; }
        .risk-row { display: flex; justify-content: space-between; align-items: center; padding: 24px 0; border-bottom: 1px solid var(--slate-200); }
        .risk-info h4 { margin: 0; font-size: 1.25rem; }
        .risk-info p { margin: 4px 0 0; color: var(--slate-600); font-size: 0.95rem; }

        .cta-footer { background: var(--slate-900); padding: 100px 0; text-align: center; border-radius: 64px 64px 0 0; margin-top: 100px; color: white; }
        .btn-premium { background: var(--accent); color: white; padding: 20px 48px; border-radius: 16px; text-decoration: none; font-weight: 700; font-size: 1.125rem; display: inline-block; transition: transform 0.2s; }
        .btn-premium:hover { transform: scale(1.05); }

        .link-hub { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; margin-top: 60px; padding: 40px 0; border-top: 1px solid var(--slate-200); }
        .link-hub a { color: var(--slate-400); text-decoration: none; font-size: 0.85rem; }
        .link-hub a:hover { color: var(--accent); }
    </style>
</head>
<body>
    @template.layout._header()

    <header class="report-header">
        <div class="container">
            <span class="badge-market">Verified Market Data: 2026</span>
            <h1 class="title-h1">${h1Content}</h1>
            <p class="subtitle">A forensic analysis of home repair liabilities and hidden risks in the ${metroName} metro for ${eraName} properties.</p>
        </div>
    </header>

    <main class="container" style="margin-top: 20px;">
        <nav style="margin-bottom: 24px; font-size: 0.85rem; color: var(--slate-400); font-family: 'Outfit';">
            <a href="/" style="color: var(--slate-400); text-decoration: none;">Home</a> 
            <span style="margin: 0 8px; opacity: 0.5;">&sol;</span>
            <a href="/home-repair" style="color: var(--slate-400); text-decoration: none;">Home Repair</a>
            <span style="margin: 0 8px; opacity: 0.5;">&sol;</span>
            <span style="color: var(--slate-900); font-weight: 600;">${metroName} (${eraName})</span>
        </nav>
        <div class="bento-grid">
            <!-- 1. EXECUTIVE SUMMARY CARD -->
            <div class="bento-card span-12">
                <div class="stat-banner">
                    <div class="stat-item">
                        <span class="stat-val" style="color: var(--danger);">${verdict.getPlan().getMustDo().size()}</span>
                        <span class="stat-label">Critical Risks</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-val">$${lowPrice}</span>
                        <span class="stat-label">Min. Repair Cost</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-val">$${highPrice}</span>
                        <span class="stat-label">Market Liability</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-val" style="color: var(--accent);">1.5x</span>
                        <span class="stat-label">Negotiation Multiplier</span>
                    </div>
                </div>
            </div>

            <!-- 2. NEIGHBOR COMPARISON (COMPS) -->
            @if(verdict.getComparisonData() != null)
                <div class="bento-card span-8 comps-card">
                    <div style="position: relative; z-index: 2;">
                        <span class="stat-label" style="color: var(--slate-400);">Neighbor Market Comparison</span>
                        <h3 class="comps-delta">+${String.format("%.0f", verdict.getComparisonData().getDeltaPercentage())}%</h3>
                        <p style="font-size: 1.25rem; font-weight: 600; color: var(--slate-900);">Higher liability than modern homes.</p>
                        <p style="color: var(--slate-600); line-height: 1.6;">
                            Buying in ${metroName} means inherited risks. ${eraName.replace(" Era", "s")} carry an additional 
                            <strong>$${String.format("%,.0f", verdict.getComparisonData().getCostDelta())}</strong> 
                            in statistical risk compared to 2010+ builds in the same zip code.
                        </p>
                    </div>
                    <div style="position: absolute; right: -20px; bottom: -20px; font-size: 12rem; font-weight: 900; opacity: 0.03; color: var(--slate-900); pointer-events: none;">COMPS</div>
                </div>
            @endif

            <!-- 3. TIMELINE BOX -->
            <div class="bento-card span-4">
                <span class="stat-label">System Integrity Timeline</span>
                <div class="timeline-box">
                    @for(RiskAdjustedItem item : verdict.getPlan().getMustDo())
                        <div class="timeline-item">
                            <div class="dot" style="background: ${item.getRiskFlags().stream().anyMatch(f -> f.contains("DEAD")) ? "var(--danger)" : "var(--warning)"};"></div>
                            <div>
                                <div style="font-weight: 700; font-size: 0.95rem;">${item.getPrettyName()}</div>
                                <div style="font-size: 0.75rem; color: var(--slate-400); text-transform: uppercase;">Statistically Due</div>
                            </div>
                        </div>
                    @endfor
                    @if(verdict.getPlan().getMustDo().size() < 3)
                         <div class="timeline-item">
                            <div class="dot" style="background: var(--success);"></div>
                            <div>
                                <div style="font-weight: 700; font-size: 0.95rem;">Structure & Foundation</div>
                                <div style="font-size: 0.75rem; color: var(--slate-400); text-transform: uppercase;">Statistically Stable</div>
                            </div>
                        </div>
                    @endif
                </div>
            </div>

            <!-- 4. REGIONAL INSIGHT -->
            <div class="bento-card span-12" style="background: var(--slate-50); border: 1px solid var(--slate-200);">
                <h3 style="margin-top: 0; color: var(--accent);">📊 Market Intelligence: ${metroName}</h3>
                <p style="font-size: 1.1rem; line-height: 1.7; color: var(--slate-600); border-left: 4px solid var(--accent); padding-left: 24px;">
                    ${regionalInsight}
                </p>
                <div style="margin-top: 24px; padding: 20px; background: white; border-radius: 16px; border: 1px dashed var(--slate-200);">
                    <p style="font-style: italic; color: var(--slate-600); margin: 0; font-size: 0.95rem;">
                        "<strong>Expert Strategy:</strong> ${verdict.getContextBriefing() != null ? verdict.getContextBriefing().getDynamicNarrative() : ""}"
                    </p>
                </div>
            </div>

            <!-- 5. ITEMIZED DEEP DIVE -->
            <div class="bento-card span-12">
                <h3>Identified Risk Profile</h3>
                <div class="risk-list">
                    @for(var item : verdict.getPlan().getMustDo())
                        <div class="risk-row">
                            <div class="risk-info">
                                <h4>${item.getPrettyName()}</h4>
                                <p>${item.getExplanation().split("\\.")[0]}.</p>
                            </div>
                            <div style="text-align: right;">
                                <div style="font-weight: 800; font-size: 1.25rem; color: var(--slate-900);">$${String.format("%,.0f", item.getAdjustedCost())}</div>
                                <a href="/home-repair/verdicts/${metroCode.toLowerCase().replace("_", "-")}/${era.toLowerCase().replace("_", "-")}/${item.getItemCode().toLowerCase().replace("_", "-")}.html" style="color: var(--accent); font-size: 0.75rem; text-decoration: none; font-weight: 700; text-transform: uppercase;">View Audit &rarr;</a>
                            </div>
                        </div>
                    @endfor
                </div>
            </div>
        </div>

        <!-- 6. FAQ SECTION -->
        <section style="margin-top: 100px;">
            <h2 style="font-size: 2.5rem; text-align: center;">Frequently Asked Questions.</h2>
            <div style="max-width: 800px; margin: 60px auto;">
                @for(var faq : faqItems)
                    <div style="margin-bottom: 40px; border-bottom: 1px solid var(--slate-100); padding-bottom: 32px;">
                        <h4 style="font-size: 1.35rem; font-weight: 800; margin-bottom: 16px; display: flex; gap: 12px; color: var(--slate-900);">
                            <span style="color: var(--accent);">?</span> ${faq.get("question")}
                        </h4>
                        <div style="padding-left: 28px; color: var(--slate-600); line-height: 1.8; font-size: 1.05rem;">
                            ${faq.get("answer")}
                        </div>
                    </div>
                @endfor
            </div>
        </section>

        <!-- 7. INTERNAL LINKS HUB -->
        <div class="link-hub">
            @for(var link : cityLinks)
                <a href="${link.getHref()}">${link.getText()}</a>
            @endfor
            @for(var link : stateLinks)
                <a href="${link.getHref()}">${link.getText()}</a>
            @endfor
        </div>
    </main>

    <section class="cta-footer">
        <div class="container">
            <h2 style="font-size: 3rem; margin-bottom: 24px; font-weight: 800;">Don't Buy Without a Verdict.</h2>
            <p style="font-size: 1.25rem; color: var(--slate-400); margin-bottom: 48px; max-width: 600px; margin-left: auto; margin-right: auto;">
                Get a custom repair profile for any specific address in ${metroName}. 100% Free forensic check.
            </p>
            <a href="/home-repair" class="btn-premium">Check Your Address Now &rarr;</a>
            <p style="margin-top: 32px; font-size: 0.85rem; opacity: 0.5;">No email required. Instant results based on local market indices.</p>
        </div>
    </section>

    @template.layout._footer()
</body>
</html>
