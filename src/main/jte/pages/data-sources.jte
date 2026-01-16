<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Data Sources & Standards | Home Repair Verdict</title>
    <meta name="description" content="Our home repair estimates are powered by RSMeans Construction Data and U.S. Bureau of Labor Statistics findings for 2026.">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        .page-content {
            max-width: 800px;
            margin: 60px auto;
            padding: 0 24px;
        }
    </style>
</head>
<body>
    @template.layout._header()

    <main class="page-content">
        <h1 style="margin-bottom: 24px; font-size: 2.5rem;">Data Integrity & Sources</h1>
        <p class="explanation-text" style="font-size: 1.125rem; margin-bottom: 40px;">
            To ensure the highest level of accuracy, the Home Repair Verdict engine does not rely on crowdsourced user data or "guesstimates." 
            We strictly utilize actuarial-grade datasets used by the insurance and construction industries.
        </p>

        <section class="card">
            <h2 class="section-title">Primary Construction Data</h2>
            <p class="explanation-text">
                Material costs, equipment rental rates, and productivity baselines are derived from:
            </p>
            <div class="data-item">
                <h3 style="margin-top: 0;">RSMeans® Data from Gordian</h3>
                <p class="explanation-text" style="margin-bottom: 0;">
                    The North American standard for construction cost engineering. We utilize their residential cost datasets to establish baseline pricing for 
                    over 45 individual repair categories, from roofing to foundation work. 
                    <br><br>
                    <em>Current Baseline: Q4 2025 Data (Forward projected to 2026)</em>
                </p>
            </div>
        </section>

        <section class="card">
            <h2 class="section-title">Labor Market Data</h2>
            <p class="explanation-text">
                Labor rates vary significantly by zip code. We normalize these using federal statistics:
            </p>
            <div class="data-item">
                <h3 style="margin-top: 0;">U.S. Bureau of Labor Statistics (BLS)</h3>
                <p class="explanation-text" style="margin-bottom: 0;">
                    We incorporate data from the <strong>Occupational Employment and Wage Statistics (OEWS)</strong> program to determine prevailing wage rates 
                    for key trades (Electricians, Plumbers, Roofers, Carpenters) in each Metropolitan Statistical Area (MSA).
                </p>
            </div>
        </section>

        <section class="card">
            <h2 class="section-title">Regional & Housing Data</h2>
            <p class="explanation-text">
                Housing stock age and localized risk factors are cross-referenced with:
            </p>
            <ul style="list-style-type: disc; padding-left: 20px; line-height: 1.8; color: var(--color-text-secondary); margin-bottom: 16px;">
                <li><strong>U.S. Census Bureau:</strong> American Housing Survey (AHS) data for housing age distribution.</li>
                <li><strong>FEMA & USGS:</strong> Regional geological and flood risk maps that influence foundation repair probabilities.</li>
            </ul>
        </section>
        
        <div style="text-align: center; margin-top: 60px; font-size: 0.9rem; color: var(--color-text-tertiary);">
            <p>Data Verification ID: HRV-2026-Q1-ALPHA</p>
        </div>

    </main>

    @template.layout._footer()
</body>
</html>
