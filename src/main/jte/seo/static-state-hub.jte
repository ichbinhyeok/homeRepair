@import com.livingcostcheck.home_repair.service.dto.verdict.StateHubPage
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink

@param StateHubPage page

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
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${page.stateName} Home Repair Cost Guide (2026): ${page.cities.size()} City Benchmarks</title>
    <meta name="description" content="City-by-city repair cost benchmarks for ${page.stateName}. Compare pre-1950 to modern homes, open metro reports by era, and plan inspection budgets before closing.">
    
    <link rel="canonical" href="${page.canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Outfit:wght@500;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/seo-report.css">
    
    <!-- Open Graph -->
    <meta property="og:type" content="website">
    <meta property="og:title" content="${page.stateName} Home Repair Cost Guide (2026)">
    <meta property="og:description" content="Compare ${page.cities.size()} city benchmarks in ${page.stateName} and drill into era-based repair liabilities.">
    <meta property="og:url" content="${page.canonicalUrl}">

    $unsafe{page.breadcrumbSchema}
    <script type="application/ld+json">
    {
      "@context": "https://schema.org",
      "@type": "FAQPage",
      "mainEntity": [
        {
          "@type": "Question",
          "name": "How should I use this ${page.stateName} repair cost page?",
          "acceptedAnswer": {
            "@type": "Answer",
            "text": "Start with your nearest city, then open the home era that matches your property. Use that report to set inspection priorities and a seller credit target before closing."
          }
        },
        {
          "@type": "Question",
          "name": "Why are costs different across cities in ${page.stateName}?",
          "acceptedAnswer": {
            "@type": "Answer",
            "text": "Repair estimates vary by local labor rates, climate risk, and housing stock age. The city-level reports account for those market differences."
          }
        },
        {
          "@type": "Question",
          "name": "Does this replace a home inspection?",
          "acceptedAnswer": {
            "@type": "Answer",
            "text": "No. Use these benchmarks to prepare, but confirm with a licensed inspector and contractor bids for the specific property."
          }
        }
      ]
    }
    </script>
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
                <span class="current">${page.stateName}</span>
            </div>
            
            <span class="badge-market">State Index</span>
            <h1 class="title-h1">${page.stateName} Home Repair Cost Map</h1>
            <p class="subtitle">Compare ${page.cities.size()} metro markets, open era-specific reports, and build a realistic repair budget before you buy.</p>
        </div>
    </header>

    <main class="container" style="margin-top: -3rem; position: relative; z-index: 10;">
        <div class="bento-grid">

            <div class="span-12" style="background: white; border-radius: var(--radius-xl); border: 1px solid var(--slate-100); box-shadow: var(--shadow-sm); padding: 1.5rem;">
                <h2 style="margin-bottom: 0.75rem;">How To Use This State Report</h2>
                <p style="color: var(--slate-600); line-height: 1.7; margin-bottom: 0.75rem;">
                    This page is built for buyers comparing repair exposure across cities in ${page.stateName}. It helps you narrow where costs are likely to be higher before inspection.
                </p>
                <ol style="margin: 0; padding-left: 1.1rem; color: var(--slate-700); line-height: 1.8;">
                    <li>Choose your target city below.</li>
                    <li>Open the matching home era report (Pre-1950 through 2010-Present).</li>
                    <li>Use the estimate range to set inspection focus and negotiation range.</li>
                </ol>
            </div>
            
            <!-- State Market Grid -->
            <div class="span-12 state-grid-container" style="background: white; border-radius: var(--radius-xl); box-shadow: var(--shadow-sm); border: 1px solid var(--slate-100);">
                <style>
                    .state-grid-container { padding: 2rem; }
                    .city-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 2rem; }
                    @media (max-width: 640px) {
                        .state-grid-container { padding: 1rem; }
                        .city-grid { grid-template-columns: 1fr; gap: 1rem; } /* Full width on mobile */
                    }
                </style>
                <h2 style="margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid var(--slate-100);">Available Markets in ${page.stateName}</h2>
                
                <div class="city-grid">
                    @for(var city : page.cities)
                        <div class="city-group">
                            <h3 style="font-size: 1.25rem; margin-bottom: 1rem; color: var(--slate-900); display: flex; align-items: center; gap: 0.5rem;">
                                <span style="display: inline-block; width: 6px; height: 6px; background: var(--primary); border-radius: 50%;"></span>
                                ${city.name}
                            </h3>
                            <div style="display: flex; flex-wrap: wrap; gap: 0.5rem;">
                                @for(var era : city.eras)
                                    <a href="${era.getHref()}" class="btn-outline" style="font-size: 0.8rem; padding: 0.4rem 0.8rem;">
                                        ${era.getText()}
                                    </a>
                                @endfor
                            </div>
                        </div>
                    @endfor
                </div>
            </div>

            <div class="span-12" style="background: white; border-radius: var(--radius-xl); border: 1px solid var(--slate-100); box-shadow: var(--shadow-sm); padding: 1.5rem;">
                <h2 style="margin-bottom: 0.75rem;">What Drives Differences Across ${page.stateName}</h2>
                <p style="color: var(--slate-600); line-height: 1.7; margin-bottom: 0.75rem;">
                    Repair liability changes by metro because labor pricing, weather stress, and average home age are not uniform statewide.
                </p>
                <ul style="margin: 0; padding-left: 1.1rem; color: var(--slate-700); line-height: 1.8;">
                    <li>Labor and permit cost variation between metros.</li>
                    <li>Different climate pressure on roofs, envelopes, and plumbing.</li>
                    <li>Different mix of older housing stock and deferred maintenance.</li>
                </ul>
                <p style="color: var(--slate-600); line-height: 1.7; margin-top: 0.75rem;">
                    For methodology and data sources, review <a href="/home-repair/methodology">Methodology</a> and <a href="/home-repair/data-sources">Data Sources</a>.
                </p>
            </div>
            
            <!-- CTA -->
             <div class="span-12" style="text-align: center; margin-top: 2rem; margin-bottom: 4rem;">
                <p style="margin-bottom: 1.5rem; color: var(--slate-600);">Need a property-specific estimate beyond city averages?</p>
                <a href="/home-repair" class="btn-primary">Start With Your Property &rarr;</a>
             </div>

        </div>
    </main>

    @template.layout._footer()
</body>
</html>
