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
@param RiskAdjustedItem item
@param Verdict verdict
@param String baseUrl
@param String canonicalUrl
@param String faqSchema
@param String regionalInsight
@param String parentUrl
@param String breadcrumbSchema

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- Long-Tail SEO Title -->
    <title>${title}</title>
    <meta name="description" content="Detailed cost analysis for ${item.getPrettyName()} in ${metroName}. Review repair costs, negotiation tactics, and safety risks before buying a ${eraName} home.">
    
    <link rel="canonical" href="${canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    
    <!-- Open Graph -->
    <meta property="og:type" content="article">
    <meta property="og:title" content="${item.getPrettyName()} Cost in ${metroName}">
    <meta property="og:description" content="Don't overpay. See the real cost to fix ${item.getPrettyName()} in ${metroName} (${eraName}).">
    <meta property="og:url" content="${canonicalUrl}">
    
    $unsafe{faqSchema}
    $unsafe{breadcrumbSchema}

    <style>
        :root {
            --color-danger: #dc2626;
            --color-danger-bg: #fef2f2;
            --color-warning: #d97706;
            --color-warning-bg: #fffbeb;
            --color-success: #059669;
            --color-success-bg: #ecfdf5;
            --color-slate-900: #0f172a;
            --color-slate-600: #475569;
        }
        
        .report-container { max-width: 800px; margin: 0 auto; padding: 20px; font-family: 'Inter', system-ui, sans-serif; }
        
        .breadcrumb { font-size: 0.9rem; color: #64748b; margin-bottom: 20px; }
        .breadcrumb a { color: #3b82f6; text-decoration: none; }
        .breadcrumb a:hover { text-decoration: underline; }

        .hero { margin-bottom: 40px; padding-bottom: 20px; border-bottom: 1px solid #e2e8f0; }
        .hero h1 { font-size: 2.2rem; font-weight: 800; color: var(--color-slate-900); margin-bottom: 12px; line-height: 1.2; }
        .risk-badge { display: inline-block; padding: 4px 12px; border-radius: 99px; font-size: 0.8rem; font-weight: 700; text-transform: uppercase; margin-bottom: 16px; background: var(--color-danger-bg); color: var(--color-danger); }
        
        .card { background: white; border: 1px solid #e2e8f0; padding: 24px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        .card h2 { font-size: 1.25rem; font-weight: 700; color: var(--color-slate-900); margin-bottom: 16px; border-bottom: 1px solid #f1f5f9; padding-bottom: 12px; }
        
        .cost-display { background: #1e293b; color: white; padding: 24px; border-radius: 8px; text-align: center; margin-bottom: 24px; }
        .cost-value { font-size: 2.5rem; font-weight: 800; color: #3b82f6; }
        .cost-label { opacity: 0.8; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.05em; }

        .negotiation-box { background: #fffbeb; border: 1px solid #fcd34d; padding: 20px; border-radius: 8px; }
        .copy-text { background: white; padding: 12px; border: 1px dashed #cbd5e1; font-family: monospace; font-size: 0.9rem; color: #334155; margin-top: 12px; }

        .cta-btn { display: inline-block; background: #3b82f6; color: white; padding: 16px 32px; font-weight: 700; border-radius: 8px; text-decoration: none; width: 100%; text-align: center; box-sizing: border-box; }
        .cta-btn:hover { background: #2563eb; }
    </style>
</head>
<body>
    @template.layout._header()

    <main class="report-container">
        
        <!-- Breadcrumb -->
        <nav class="breadcrumb">
            <a href="/home-repair">Home</a> &gt; 
            <a href="${parentUrl}">Buying a ${eraName} Home in ${metroName}</a> &gt; 
            <span>Risk Detail</span>
        </nav>

        <!-- 1. HERO -->
        <section class="hero">
            <span class="risk-badge">Verified Risk Factor</span>
            <h1>${h1Content}</h1>
            <p style="font-size: 1.1rem; color: var(--color-slate-600); line-height: 1.6;">
                A critical analysis of <strong>${item.getPrettyName()}</strong> for homebuyers in ${metroName}.
                Don't ignore this on your inspection report.
            </p>
        </section>

        <!-- 2. COST REVEAL -->
        <section class="cost-display">
            <div class="cost-value">$${String.format("%,.0f", item.getAdjustedCost())}</div>
            <div class="cost-label">Estimated Repair Cost in ${metroName}</div>
            <div style="font-size: 0.8rem; margin-top: 8px; opacity: 0.6;">*Includes labor, materials, and local mobilization fees</div>
        </section>

        <!-- 3. THE RISK (Why it matters) -->
        <section class="card">
            <h2>⚠️ Why is this a problem?</h2>
            <div style="font-size: 1rem; color: #334155; line-height: 1.7;">
                <p>
                    <strong>${item.getPrettyName()}</strong> is common in ${eraName} era homes. 
                    ${item.getExplanation() != null ? item.getExplanation() : "This system has exceeded its expected lifespan and poses a financial risk."}
                </p>
                <div style="margin-top: 16px; padding: 16px; background: #f1f5f9; border-radius: 8px;">
                    <strong>Regional Context:</strong> ${regionalInsight}
                </div>
            </div>
        </section>

        <!-- 4. NEGOTIATION TACTIC -->
        <section class="card negotiation-box">
            <h2 style="color: #92400e; border-bottom-color: #fcd34d;">💡 Negotiation Strategy</h2>
            <p style="color: #92400e;">Copy-paste this text to your real estate agent to request a credit from the seller:</p>
            <div class="copy-text">
                "We've identified that the ${item.getPrettyName()} is heavily depreciated and presents an immediate liability of approx. $${String.format("%,.0f", item.getAdjustedCost())}. Given the ${eraName} age of the home, we request a credit of $${String.format("%,.0f", item.getAdjustedCost())} to address this safety/structural concern immediately after closing."
            </div>
        </section>

        <!-- Related Risks (Cross-linking) -->
        @if(verdict.getPlan().getMustDo().size() > 1)
            <section class="card">
                <h2>🔍 Other Critical Risks in this Home</h2>
                <div style="display: flex; flex-wrap: wrap; gap: 12px;">
                    @for(var other : verdict.getPlan().getMustDo())
                        @if(!other.getItemCode().equals(item.getItemCode()))
                            <a href="/home-repair/verdicts/${metroCode.toLowerCase().replace("_", "-")}/${era.toLowerCase().replace("_", "-")}/${other.getItemCode().toLowerCase().replace("_", "-")}.html" 
                               style="background: #f8fafc; color: #475569; padding: 8px 16px; border-radius: 8px; text-decoration: none; font-size: 0.9rem; border: 1px solid #e2e8f0;">
                                ${other.getPrettyName()} Audit &rarr;
                            </a>
                        @endif
                    @endfor
                </div>
            </section>
        @endif

        <!-- 5. CTA -->
        <section style="margin: 40px 0;">
            <h3 style="text-align: center; margin-bottom: 16px;">See the full list of risks for this home</h3>
            <a href="${parentUrl}" class="cta-btn" style="background: white; color: #3b82f6; border: 2px solid #3b82f6;">&larr; Back to ${eraName} Report</a>
            <br><br>
            <a href="/home-repair" class="cta-btn">Start New Analysis for Specific Address &rarr;</a>
        </section>

    </main>

    @template.layout._footer()
</body>
</html>
