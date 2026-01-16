<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Calculation Methodology | Home Repair Verdict</title>
    <meta name="description" content="Learn how the Home Repair Verdict calculates Incident Recovery Costs and Planned Repair Costs using RSMeans data and regional indices.">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        .page-content {
            max-width: 800px;
            margin: 60px auto;
            padding: 0 24px;
        }
        .formula-box {
            background: #f8f9fa;
            border-left: 4px solid var(--color-primary);
            padding: 20px;
            margin: 24px 0;
            font-family: monospace;
            color: var(--color-text-primary);
        }
    </style>
</head>
<body>
    @template.layout._header()

    <main class="page-content">
        <h1 style="margin-bottom: 24px; font-size: 2.5rem;">Calculation Methodology</h1>
        <p class="explanation-text" style="font-size: 1.125rem; margin-bottom: 40px;">
            The Home Repair Verdict engine utilizes a deterministic algorithmic approach to forecast maintenance liabilities for residential properties. 
            Our model distinguishes between <strong>Incident Recovery Cost (IRC)</strong> and <strong>Planned Repair Cost (PRC)</strong> to provide a complete financial risk profile.
        </p>

        <section class="card">
            <h2 class="section-title">1. Core Calculation Formula</h2>
            <p class="explanation-text">
                Every repair verdict is derived from a baseline cost indexed to the property's specific conditions. 
                The fundamental equation for determining the calculated risk is:
            </p>
            <div class="formula-box">
                Verdict Cost = Base Cost × (Regional Material Index + Regional Labor Index) × Era Risk Multiplier
            </div>
            <p class="explanation-text">
                This formula ensures that a roof replacement in San Francisco reflects local labor rates, while the same repair in rural Ohio reflects lower overheads, 
                adjusted further by the structural complexities of the home's vintage.
            </p>
        </section>

        <section class="card">
            <h2 class="section-title">2. Incident Recovery Cost (IRC) vs. Planned Repair Cost (PRC)</h2>
            
            <div class="data-item">
                <h3 style="margin-top: 0;">Incident Recovery Cost (IRC)</h3>
                <p class="explanation-text" style="margin-bottom: 0;">
                    IRC represents the financial exposure from "surprise" failures. These are high-probability catastrophic events associated with specific building eras 
                    (e.g., Cast Iron plumbing failure in 1950s homes). Our model treats these not as "if" but "when," calculating the full cost of emergency remediation.
                </p>
            </div>

            <div class="data-item">
                <h3 style="margin-top: 0;">Planned Repair Cost (PRC)</h3>
                <p class="explanation-text" style="margin-bottom: 0;">
                    PRC covers predictable lifespan expirations. This includes HVAC systems, roofing materials, and water heaters that have reached the end of their useful life 
                    based on the home's age. These costs are amortized over the expected ownership period in our comprehensive reports.
                </p>
            </div>
        </section>

        <section class="card">
            <h2 class="section-title">3. Era-Based Risk Multipliers</h2>
            <p class="explanation-text">
                Homes from different decades carry unique structural risks that standard estimates miss. We apply specific multipliers to account for hidden complexity:
            </p>
            <ul style="list-style-type: disc; padding-left: 20px; margin-top: 16px; line-height: 1.8; color: var(--color-text-secondary);">
                <li><strong>Pre-1950:</strong> High risk for Knob & Tube wiring and unreinforced masonry. Multiplier accounts for code-compliance upgrades during repair.</li>
                <li><strong>1950-1970:</strong> High risk for galvanized plumbing and asbestos-containing materials. Abatement costs are factored into the multiplier.</li>
                <li><strong>1970-1980:</strong> Specific adjustments for Aluminum wiring and lead paint remediation standards.</li>
            </ul>
        </section>

        <section class="card">
            <h2 class="section-title">4. Regional Indexing</h2>
            <p class="explanation-text">
                We track construction indices across 117 US Metro areas. Our database updates quarterly to reflect local fluctuations in:
            </p>
            <ul style="list-style-type: disc; padding-left: 20px; margin-top: 16px; line-height: 1.8; color: var(--color-text-secondary);">
                <li>Union and Non-Union Labor Rates</li>
                <li>Commodity Material Pricing (Lumber, Copper, Concrete)</li>
                <li>Fuel & Transport Surcharges</li>
            </ul>
        </section>

    </main>

    @template.layout._footer()
</body>
</html>
