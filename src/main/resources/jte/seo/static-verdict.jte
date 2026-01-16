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
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
            line-height: 1.6;
            color: #333;
        }
        h1 {
            color: #1a1a1a;
            border-bottom: 3px solid #4CAF50;
            padding-bottom: 10px;
        }
        .verdict-tier {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 4px;
            font-weight: bold;
            margin: 10px 0;
        }
        .verdict-tier.APPROVED { background: #4CAF50; color: white; }
        .verdict-tier.WARNING { background: #FF9800; color: white; }
        .verdict-tier.DENIED { background: #f44336; color: white; }
        .headline {
            font-size: 1.2em;
            margin: 20px 0;
            padding: 15px;
            background: #f5f5f5;
            border-left: 4px solid #4CAF50;
        }
        .must-do-item {
            margin: 20px 0;
            padding: 15px;
            background: #fff3cd;
            border-left: 4px solid #ff9800;
        }
        .must-do-item h3 {
            margin-top: 0;
            color: #d68000;
        }
        .should-do {
            background: #e3f2fd;
            padding: 15px;
            margin: 20px 0;
            border-left: 4px solid #2196F3;
        }
        .internal-links {
            margin: 40px 0;
            padding: 20px;
            background: #f9f9f9;
            border: 1px solid #ddd;
        }
        .internal-links h3 {
            margin-top: 0;
        }
        .internal-links a {
            display: block;
            padding: 8px 0;
            color: #1976D2;
            text-decoration: none;
        }
        .internal-links a:hover {
            text-decoration: underline;
        }
        .cta-button {
            display: inline-block;
            padding: 15px 30px;
            background: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 1.1em;
            font-weight: bold;
            margin: 30px 0;
            text-align: center;
        }
        .cta-button:hover {
            background: #45a049;
        }
        footer {
            margin-top: 60px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            color: #666;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <header>
        <h1>2026 ${metroName} Home Repair Cost Analysis — ${eraName}</h1>
        <div class="verdict-tier ${verdict.getTier()}">${verdict.getTier()}</div>
    </header>

    <section class="verdict-summary">
        <div class="headline">${verdict.getHeadline()}</div>
    </section>

    <section id="must-do-repairs">
        <h2>🔴 Critical Repairs for ${eraName} Homes in ${metroName}</h2>
        
        @if(verdict.getPlan().getMustDo().isEmpty())
            <p>No critical repairs identified for this scenario.</p>
        @else
            @for(var item : verdict.getPlan().getMustDo())
                <div class="must-do-item">
                    <h3>${item.getPrettyName()}</h3>
                    @if(item.getExplanation() != null && !item.getExplanation().isEmpty())
                        <p>${item.getExplanation()}</p>
                    @endif
                    <p><strong>Estimated Cost:</strong> $${String.format("%,.0f", item.getAdjustedCost())}</p>
                </div>
            @endfor
        @endif
    </section>

    @if(!verdict.getPlan().getShouldDo().isEmpty())
        <section id="recommended-repairs">
            <h2>💡 Recommended Improvements</h2>
            <div class="should-do">
                <ul>
                    @for(var item : verdict.getPlan().getShouldDo())
                        <li>${item.getPrettyName()} — $${String.format("%,.0f", item.getAdjustedCost())}</li>
                    @endfor
                </ul>
            </div>
        </section>
    @endif

    <section id="cta">
        <h2>Get Your Custom Analysis</h2>
        <p>This page shows costs for a typical $15,000 budget scenario. Get a personalized verdict for your exact budget and situation:</p>
        <a href="/" class="cta-button">📊 Calculate Your Custom Verdict</a>
    </section>

    <section class="internal-links">
        <h3>🏘️ Explore Other Eras in ${metroName}</h3>
        @for(var link : eraLinks)
            <a href="${link.getHref()}">${link.getText()}</a>
        @endfor

        <h3 style="margin-top: 30px;">🌆 Compare with Nearby Cities (${eraName})</h3>
        @for(var link : cityLinks)
            <a href="${link.getHref()}">${link.getText()}</a>
        @endfor
    </section>

    <footer>
        <p><strong>Data Sources:</strong> 2026 RSMeans & U.S. Bureau of Labor Statistics Combined Data</p>
        <p><strong>Last Updated:</strong> January 2026</p>
        <p><strong>Disclaimer:</strong> Cost estimates are based on regional averages and historical data. Actual contractor quotes may vary based on specific property conditions, access, and market dynamics. This analysis is for informational purposes only and does not constitute professional advice.</p>
    </footer>
</body>
</html>
