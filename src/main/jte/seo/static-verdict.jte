@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink
@import java.util.List

@param String metroCode
@param String metroName
@param String era
@param String eraName
@param Verdict verdict
@param List<InternalLink> eraLinks
@param List<InternalLink> cityLinks
@param String baseUrl
@param String canonicalUrl

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <title>${metroName} ${eraName} Home Repair Cost Guide [2026 Updated] | Verdict Engine</title>
    <meta name="description" content="${verdict.getTier()}: ${verdict.getHeadline()} Analysis of ${eraName} homes in ${metroName} with detailed cost breakdowns and critical repair priorities based on 2026 RSMeans data.">
    
    <link rel="canonical" href="${canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    
    <!-- Open Graph / Social Media -->
    <meta property="og:type" content="article">
    <meta property="og:title" content="${metroName} ${eraName} Home Repair Cost [2026 Updated]">
    <meta property="og:description" content="${verdict.getHeadline()}">
    <meta property="og:url" content="${canonicalUrl}">
    
    <!-- Schema.org Structured Data -->
    <script type="application/ld+json">
    {
        "@context": "https://schema.org",
        "@type": "LocalBusiness",
        "name": "Home Repair Cost Analysis - ${metroName}",
        "description": "Professional home repair cost analysis for ${eraName} homes in ${metroName}",
        "areaServed": {
            "@type": "City",
            "name": "${metroName}"
        },
        "offers": {
            "@type": "AggregateOffer",
            "priceCurrency": "USD",
            "offerCount": "${verdict.getPlan().getMustDo().size()}"
        }
    }
    </script>
    
    <script type="application/ld+json">
    {
        "@context": "https://schema.org",
        "@type": "HowTo",
        "name": "How to Budget for ${eraName} Home Repairs in ${metroName}",
        "description": "${verdict.getHeadline()}",
        "step": [
        @for(var item : verdict.getPlan().getMustDo())
            {
                "@type": "HowToStep",
                "name": "${item.getPrettyName()}",
                "text": "${item.getExplanation() != null ? item.getExplanation().replace("\"", "'") : ""}"
            }@if(!item.equals(verdict.getPlan().getMustDo().get(verdict.getPlan().getMustDo().size() - 1))),@endif
        @endfor
        ]
    }
    </script>
    
    <style>
        /* Page-specific minimal styles that sit on top of shared styles.css */
        .verdict-container {
            max-width: 800px;
            margin: 40px auto;
            padding: 0 20px;
        }

        h1 {
            color: #1a1a1a;
            border-bottom: 3px solid #4CAF50;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        
        .verdict-tier {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 4px;
            font-weight: bold;
            margin-bottom: 30px;
            font-size: 1.1em;
        }
        .verdict-tier.APPROVED { background: #4CAF50; color: white; }
        .verdict-tier.WARNING { background: #FF9800; color: white; }
        .verdict-tier.DENIED { background: #f44336; color: white; }
        
        .headline {
            font-size: 1.3em;
            margin: 0 0 40px;
            padding: 20px;
            background: #f5f5f5;
            border-left: 5px solid #4CAF50;
            line-height: 1.5;
        }
        
        section { margin-bottom: 50px; }
        
        .must-do-item {
            margin: 20px 0;
            padding: 20px;
            background: #fff3cd;
            border-left: 4px solid #ff9800;
            border-radius: 4px;
        }
        .must-do-item h3 {
            margin-top: 0;
            color: #d68000;
        }
        
        .should-do {
            background: #e3f2fd;
            padding: 20px;
            margin: 20px 0;
            border-left: 4px solid #2196F3;
            border-radius: 4px;
        }
        
        .cta-section {
            text-align: center;
            background: #f0fdf4;
            padding: 40px;
            border-radius: 10px;
            border: 2px solid #bbf7d0;
        }
        
        .internal-links {
            padding: 30px;
            background: #fff;
            border: 1px solid #eee;
            border-radius: 8px;
        }
        .internal-links h3 {
            margin-top: 0;
            font-size: 1.1em;
            border-bottom: none;
        }
        .internal-links a {
            display: inline-block;
            margin-right: 15px;
            margin-bottom: 10px;
            color: #1976D2;
            text-decoration: none;
        }
        .internal-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    @template.layout._header()

    <div class="verdict-container">
        
        <!-- Main Verdict Card -->
        <article class="card verdict-header-card">
            <header class="verdict-header">
                <div class="badge ${verdict.getTier()}">${verdict.getTier()}</div>
                <h1>${metroName} Home Repair Cost Analysis — ${eraName}</h1>
                <p class="data-meta">Updated for 2026 • Based on RSMeans Construction Data</p>
            </header>

            <section class="verdict-summary">
                <h2 class="section-title">
                    <span>📊</span> Verdict Summary
                </h2>
                <div class="headline explanation-text">
                    ${verdict.getHeadline()}
                </div>
            </section>
        </article>
    
        <!-- Critical Repairs Section -->
        <section id="must-do-repairs" class="card">
            <h2 class="section-title text-danger">
                <span>🔴</span> Critical Repairs (Must-Do)
            </h2>
            <p class="explanation-text" style="margin-bottom: 24px;">
                The following repairs are critical safety or structural issues common to ${eraName} homes in this region. 
                Immediate attention is recommended.
            </p>
            
            @if(verdict.getPlan().getMustDo().isEmpty())
                <p class="data-meta">No critical repairs identified for this specific scenario.</p>
            @else
                <div class="data-list">
                    @for(var item : verdict.getPlan().getMustDo())
                        <div class="data-item" style="position: relative;">
                            <h3>${item.getPrettyName()}</h3>
                            @if(item.getExplanation() != null && !item.getExplanation().isEmpty())
                                <p class="explanation-text" style="font-size: 0.9em; margin-bottom: 12px;">${item.getExplanation()}</p>
                            @endif
                            
                            <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px;">
                                @if(item.isMandatory())
                                    <span style="background: #fee2e2; color: #991b1b; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold; text-transform: uppercase;">Code Mandatory</span>
                                @endif
                                
                                @if(item.getRiskFlags() != null)
                                    @for(var flag : item.getRiskFlags())
                                        @if(flag.contains("ERA_RISK"))
                                            <span style="background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold;">HISTORICAL RISK</span>
                                        @endif
                                    @endfor
                                @endif

                                @if(item.getCompoundingBadge() != null)
                                    <span style="background: #eff6ff; color: #1e40af; border: 1px solid #dbeafe; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold;">
                                        MULT: ${item.getCompoundingBadge()}
                                    </span>
                                @endif
                            </div>

                            <div class="data-cost" style="font-weight: 800; color: #1e293b;">
                                Estimated Cost: $${String.format("%,.0f", item.getAdjustedCost())}
                            </div>
                        </div>
                    @endfor
                </div>
            @endif
        </section>
    
        <!-- Recommended Repairs Section -->
        @if(!verdict.getPlan().getShouldDo().isEmpty())
            <section id="recommended-repairs" class="card">
                <h2 class="section-title text-success">
                    <span>💡</span> Recommended Improvements (Should-Do)
                </h2>
                <p class="explanation-text" style="margin-bottom: 24px;">
                    These improvements prevent future damage and increase property value.
                </p>
                <div class="data-list">
                    @for(var item : verdict.getPlan().getShouldDo())
                        <div class="data-item">
                            <h3>${item.getPrettyName()}</h3>
                            <div class="data-cost">
                                Est. $${String.format("%,.0f", item.getAdjustedCost())}
                            </div>
                        </div>
                    @endfor
                </div>
            </section>
        @endif
    
        <!-- Call to Action -->
        <section id="cta" class="card" style="text-align: center; border-top: 4px solid var(--color-primary); background-color: #F8FAFC; margin-top: 40px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
            <h2 class="section-title" style="justify-content: center; margin-bottom: 16px; border: none; color: #1e293b;">
                Need a Custom Analysis?
            </h2>
            <p class="explanation-text" style="margin-bottom: 32px; font-size: 1.05em;">
                This verdict is based on a standard budget. Your actual costs may vary based on specific conditions found during inspection.
            </p>
            <a href="/" class="header-cta" style="font-size: 1.1em; padding: 16px 32px; background-color: var(--color-primary); color: white; border-radius: 6px; text-decoration: none; font-weight: 600; display: inline-block;">
                Recalculate with your own budget
            </a>
        </section>
    
        <!-- Internal Navigation -->
        <section class="internal-links" style="margin-top: 60px;">
            <h3 style="margin-bottom: 16px;">🏘️ Explore Other Eras in ${metroName}</h3>
            <div style="display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 32px;">
            @for(var link : eraLinks)
                <a href="${link.getHref()}" style="padding: 6px 12px; background: white; border: 1px solid #E5E7EB; border-radius: 6px; font-size: 0.9em; color: var(--color-text-secondary);">${link.getText()}</a>
            @endfor
            </div>
    
            <h3 style="margin-bottom: 16px;">🌆 Compare with Nearby Cities (${eraName})</h3>
            <div style="display: flex; flex-wrap: wrap; gap: 12px;">
            @for(var link : cityLinks)
                <a href="${link.getHref()}" style="padding: 6px 12px; background: white; border: 1px solid #E5E7EB; border-radius: 6px; font-size: 0.9em; color: var(--color-text-secondary);">${link.getText()}</a>
            @endfor
            </div>
        </section>
    </div>

    @template.layout._footer()
</body>
</html>
