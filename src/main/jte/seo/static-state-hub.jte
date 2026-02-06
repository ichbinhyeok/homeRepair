@import com.livingcostcheck.home_repair.service.dto.verdict.StateHubPage
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink

@param StateHubPage page

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${page.stateName} Home Repair Costs: ${page.cities.size()} Cities Analyzed (2026)</title>
    <meta name="description" content="Compare repair costs across ${page.cities.size()} cities in ${page.stateName}. Get 2026 pricing data for roofing, HVAC, plumbing & more. Free cost breakdown by home era.">
    
    <link rel="canonical" href="${page.canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Outfit:wght@500;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/seo-report.css">
    
    <!-- Open Graph -->
    <meta property="og:type" content="website">
    <meta property="og:title" content="Home Repair Costs in ${page.stateName} (2026)">
    <meta property="og:description" content="View detailed repair cost indices for ${page.cities.size()} cities in ${page.stateName}.">
    <meta property="og:url" content="${page.canonicalUrl}">

    $unsafe{page.breadcrumbSchema}
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
            <h1 class="title-h1">${page.stateName} Market Report</h1>
            <p class="subtitle">Select your metro area to view forensic cost analysis reports by building era.</p>
        </div>
    </header>

    <main class="container" style="margin-top: -3rem; position: relative; z-index: 10;">
        <div class="bento-grid">
            
            <div class="span-12" style="background: white; border-radius: var(--radius-xl); padding: 2rem; box-shadow: var(--shadow-sm); border: 1px solid var(--slate-100);">
                <h2 style="margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid var(--slate-100);">Available Markets in ${page.stateName}</h2>
                
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 2rem;">
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
            
            <!-- CTA -->
             <div class="span-12" style="text-align: center; margin-top: 2rem; margin-bottom: 4rem;">
                <p style="margin-bottom: 1.5rem; color: var(--slate-600);">Don't see your city? Try the global search.</p>
                <a href="/home-repair" class="btn-primary">Search by Address &rarr;</a>
             </div>

        </div>
    </main>

    @template.layout._footer()
</body>
</html>
