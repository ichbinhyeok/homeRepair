@import com.livingcostcheck.home_repair.service.dto.verdict.StateHubPage
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink

@param StateHubPage page

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Repair Costs in ${page.stateName} (2026 Report) | LifeVerdict</title>
    <meta name="description" content="Forensic home repair cost analysis for all major cities in ${page.stateName}. Review 2026 construction data for Pre-1950s to modern homes.">
    
    <link rel="canonical" href="${page.canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    
    <!-- Open Graph -->
    <meta property="og:type" content="website">
    <meta property="og:title" content="Home Repair Costs in ${page.stateName} (2026)">
    <meta property="og:description" content="View detailed repair cost indices for ${page.cities.size()} cities in ${page.stateName}.">
    <meta property="og:url" content="${page.canonicalUrl}">

    $unsafe{page.breadcrumbSchema}

    <style>
        :root {
            --primary: #0f172a;
            --accent: #2563eb;
            --slate-50: #f8fafc;
            --slate-100: #f1f5f9;
            --slate-200: #e2e8f0;
            --slate-600: #475569;
            --slate-900: #0f172a;
        }
        body {
            font-family: 'Inter', sans-serif;
            color: var(--slate-900);
            background: #fff;
            margin: 0;
            line-height: 1.6;
        }
        .container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
        
        .hero {
            background: var(--slate-900);
            color: white;
            padding: 80px 0;
            text-align: center;
            border-radius: 0 0 40px 40px;
            margin-bottom: 60px;
        }
        .hero h1 { font-family: 'Outfit', sans-serif; font-size: 3.5rem; margin-bottom: 16px; font-weight: 800; }
        .hero p { font-size: 1.25rem; color: #94a3b8; max-width: 600px; margin: 0 auto; }

        .city-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 32px;
            margin-bottom: 80px;
        }
        .city-card {
            background: white;
            border: 1px solid var(--slate-200);
            border-radius: 24px;
            padding: 32px;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .city-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1);
            border-color: var(--accent);
        }
        .city-name {
            font-family: 'Outfit', sans-serif;
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 24px;
            color: var(--slate-900);
            border-bottom: 2px solid var(--slate-100);
            padding-bottom: 16px;
        }
        .era-list {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .era-link {
            font-size: 0.85rem;
            text-decoration: none;
            color: var(--slate-600);
            background: var(--slate-50);
            padding: 8px 12px;
            border-radius: 8px;
            border: 1px solid var(--slate-200);
            transition: all 0.2s;
        }
        .era-link:hover {
            background: var(--accent);
            color: white;
            border-color: var(--accent);
        }
    </style>
</head>
<body>
    @template.layout._header()

    <!-- Hero -->
    <header class="hero">
        <div class="container">
            <span style="color: var(--accent); font-weight: 700; text-transform: uppercase; letter-spacing: 1px; font-size: 0.85rem;">State Market Index</span>
            <h1>${page.stateName} Repair Verdicts</h1>
            <p>Select a metro area to view forensic cost analysis reports by home era.</p>
        </div>
    </header>

    <main class="container">
        <!-- Breadcrumb -->
        <nav style="margin-bottom: 40px; font-size: 0.9rem; color: var(--slate-600);">
            <a href="/" style="text-decoration: none; color: var(--slate-600);">Home</a> / 
            <a href="/home-repair" style="text-decoration: none; color: var(--slate-600);">Home Repair</a> / 
            <span style="font-weight: 600; color: var(--slate-900);">${page.stateName}</span>
        </nav>

        <div class="city-grid">
            @for(var city : page.cities)
                <div class="city-card">
                    <div class="city-name">${city.name}</div>
                    <div class="era-list">
                        @for(var era : city.eras)
                            <a href="${era.getHref()}" class="era-link">${era.getText()}</a>
                        @endfor
                    </div>
                </div>
            @endfor
        </div>
    </main>

    @template.layout._footer()
</body>
</html>
