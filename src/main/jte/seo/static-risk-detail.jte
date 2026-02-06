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
@param com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem item
@param com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict verdict
@param String baseUrl
@param String canonicalUrl
@param String faqSchema
@param String regionalInsight
@param String parentUrl
@param String breadcrumbSchema
@param String dateString
@param List<InternalLink> otherRisks

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>${title}</title>
    <meta name="description" content="Detailed cost analysis for ${item.getPrettyName()} in ${metroName}. Review repair costs, negotiation tactics, and safety risks before buying a ${eraName} home." />
    
    <link rel="canonical" href="${canonicalUrl}" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Outfit:wght@500;700;800&display=swap" rel="stylesheet" />
    
    <!-- Preload critical fonts for LCP performance -->
    <link rel="preload" href="https://fonts.googleapis.com/css2?family=Outfit:wght@700&display=swap" as="style" />
    <link rel="preload" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" as="style" />
    
    <link rel="stylesheet" href="/css/seo-report.css" />
    
    <!-- Open Graph -->
    <meta property="og:type" content="article" />
    <meta property="og:title" content="${item.getPrettyName()} Cost in ${metroName}" />
    <meta property="og:description" content="Don't overpay. See the real cost to fix ${item.getPrettyName()} in ${metroName} (${eraName})." />
    <meta property="og:url" content="${canonicalUrl}" />
    
    $unsafe{faqSchema}
    $unsafe{breadcrumbSchema}
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
                <a href="${parentUrl}">${metroName} ${eraName}</a>
                <span class="separator">/</span>
                <span class="current">${item.getPrettyName()}</span>
            </div>
            
            <span class="badge-market">Market Data: ${dateString}</span>
            <h1 class="title-h1">${h1Content}</h1>
            <p class="subtitle">Detailed breakdown of repair liabilities for ${item.getPrettyName()} in ${metroName} (Updated ${dateString}).</p>
        </div>
    </header>

    <main id="main-content" class="container" style="margin-top: -3rem; position: relative; z-index: 10;" role="main">
        <div class="bento-grid">
            
            <!-- 1. COST CARD (Top Priority) -->
            <div class="bento-card span-12" style="display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 2rem;">
                <div>
                    <span class="stat-label">Estimated Repair Cost</span>
                    <div style="font-size: 3.5rem; font-weight: 800; color: var(--primary); line-height: 1.1;">
                        $${String.format("%,.0f", item.getAdjustedCost())}
                    </div>
                    <p style="color: var(--slate-500); margin-top: 0.5rem;">
                        Includes labor (local rates) + materials + mobilization.
                    </p>
                </div>
                <!-- Mini Stats -->
                <div style="display: flex; gap: 2rem; background: var(--slate-50); padding: 1.5rem; border-radius: var(--radius-lg);">
                    <div>
                        <span class="stat-label">Mandatory?</span>
                        <div style="font-weight: 700; font-size: 1.25rem; color: var(--slate-900);">
                            ${item.isMandatory() ? "Yes" : "Recommended"}
                        </div>
                    </div>
                    <div>
                        <span class="stat-label">Safety Risk?</span>
                        <div style="font-weight: 700; font-size: 1.25rem; color: var(--slate-900);">
                            ${item.getRiskFlags().size() > 0 ? "Critical" : "Standard"}
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2. DEFINITION & IMPACT (Phase 2 Data) -->
            <div class="bento-card span-8">
                 <h2 style="font-size: 1.5rem; margin-bottom: 1.5rem;">Technical Breakdown</h2>
                 
                 <div style="margin-bottom: 2rem;">
                    <h3 style="font-size: 1.1rem; color: var(--slate-900); margin-bottom: 0.5rem;">What is it?</h3>
                    <p style="font-size: 1.05rem; color: var(--slate-600); line-height: 1.7;">
                        ${item.getDefinition() != null ? item.getDefinition() : item.getExplanation()}
                    </p>
                 </div>
                 
                 @if(item.getDamageScenario() != null)
                 <div style="margin-bottom: 2rem; padding: 1.5rem; background: #fff1f2; border: 1px solid #fecdd3; border-radius: var(--radius-lg);">
                    <h3 style="font-size: 1.1rem; color: #be123c; margin-bottom: 0.5rem;">🚨 Failure Scenario</h3>
                    <p style="color: #be123c; line-height: 1.6;">
                        ${item.getDamageScenario()}
                    </p>
                 </div>
                 @endif
                 
                 <div>
                    <h3 style="font-size: 1.1rem; color: var(--slate-900); margin-bottom: 0.5rem;">Regional Impact</h3>
                     <div class="insight-card" style="padding: 1.5rem; border: 1px solid var(--slate-200); border-radius: var(--radius-lg);">
                        ${regionalInsight}
                     </div>
                 </div>
            </div>
            
            <!-- 3. SIDEBAR: NEGOTIATION -->
            <div class="bento-card span-4" style="background: var(--slate-50);">
                <h3 style="margin-bottom: 1rem;">Negotiation Copy</h3>
                <p style="font-size: 0.9rem; color: var(--slate-500); margin-bottom: 1rem;">
                    Send this to your agent to request a credit.
                </p>
                <div style="background: white; padding: 1.5rem; border: 1px dashed var(--slate-300); border-radius: var(--radius-lg); font-family: monospace; font-size: 0.85rem; color: var(--slate-700); line-height: 1.6;">
                    "We have identified that the ${item.getPrettyName()} presents an immediate liability of approx. $${String.format("%,.0f", item.getAdjustedCost())}. We request a seller credit of this amount to address this critical safety concern immediately after closing."
                </div>
            </div>

            <!-- 4. MESH LINKING (L2 Cross-Linking) -->
            @if(otherRisks != null && !otherRisks.isEmpty())
            <div class="bento-card span-12">
                <h3 style="font-size: 1.25rem; font-family: 'Outfit', sans-serif;">Related Inspection Points in ${metroName}</h3>
                <p style="color: var(--slate-500); margin-bottom: 1.5rem;">Review other potential liabilities common for ${eraName} homes in this region.</p>
                
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1rem;">
                    @for(var link : otherRisks)
                        <a href="${link.href}" class="mesh-link-card" 
                           style="display: flex; align-items: center; justify-content: space-between; padding: 1.25rem; background: white; border: 1px solid var(--slate-200); border-radius: var(--radius-md); text-decoration: none; transition: all 0.2s;">
                            <span style="font-weight: 600; color: var(--slate-900);">${link.text}</span>
                            <span style="color: var(--primary); font-weight: bold;">&rarr;</span>
                        </a>
                    @endfor
                </div>
            </div>
            @endif
            
            <!-- 5. CTA -->
             <div class="bento-card span-12" style="text-align: center; padding: 4rem 2rem;">
                <h2 style="margin-bottom: 1.5rem;">Don't rely on averages.</h2>
                <p style="margin-bottom: 2rem; max-width: 600px; margin-left: auto; margin-right: auto; color: var(--slate-600);">
                    Every home is different. Get a specific audit for the exact address you are buying.
                </p>
                <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
                    <a href="${parentUrl}" class="btn-outline">Back to Full Report</a>
                    <a href="/home-repair" class="btn-primary">Run Address Check &rarr;</a>
                </div>
            </div>

        </div>
    </main>

    @template.layout._footer()
</body>
</html>
