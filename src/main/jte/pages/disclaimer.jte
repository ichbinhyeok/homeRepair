<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Disclaimer & Terms | Home Repair Verdict</title>
    <meta name="description" content="Legal disclaimer and terms of use for the Home Repair Verdict cost estimation engine.">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        .page-content {
            max-width: 800px;
            margin: 60px auto;
            padding: 0 24px;
        }
        .disclaimer-list li {
            margin-bottom: 16px;
        }
    </style>
</head>
<body>
    @template.layout._header()

    <main class="page-content">
        <h1 style="margin-bottom: 24px; font-size: 2.5rem;">Disclaimer</h1>
        <p class="explanation-text" style="font-size: 1.125rem; margin-bottom: 40px;">
            Please read the following important legal disclosures regarding the use of the Home Repair Verdict engine.
        </p>

        <section class="card">
            <h2 class="section-title text-warning">
                <span>⚠️</span> Informational Use Only
            </h2>
            <p class="explanation-text">
                The content, data, and estimates provided by Home Repair Verdict are for <strong>informational and educational purposes only</strong>. 
                They are computer-generated estimates based on statistical averages and historical data.
            </p>
            <ul class="explanation-text disclaimer-list" style="list-style-type: disc; padding-left: 20px;">
                <li><strong>Not a Substitute for Professional Advice:</strong> This information is not intended to replace the services of a licensed professional engineer, architect, or home inspector.</li>
                <li><strong>Not a Binding Offer:</strong> The costs presented are estimates of fair market value, not offers to perform work.</li>
            </ul>
        </section>

        <section class="card">
            <h2 class="section-title">Accuracy & Assumptions</h2>
            <p class="explanation-text">
                While we strive for accuracy using high-quality data sources, actual home repair costs can vary significantly based on:
            </p>
            <ul class="explanation-text disclaimer-list" style="list-style-type: disc; padding-left: 20px;">
                <li><strong>Standard Conditions Assumed:</strong> Our estimates assume "standard" access and site conditions. Unique constraints (e.g., steep slopes, limited access) will increase costs.</li>
                <li><strong>Market Volatility:</strong> Supply chain disruptions or local labor shortages can cause prices to fluctuate rapidly outside of quarterly index updates.</li>
                <li><strong>Hidden Defects:</strong> No algorithm can detect unseen issues behind walls or under ground. Actual scope of work may exceed visible estimates.</li>
            </ul>
        </section>

        <section class="card">
            <h2 class="section-title">Limitation of Liability</h2>
            <p class="explanation-text">
                The LifeVerdict research team, its operators, and affiliates shall not be held liable for any decisions made based on the information provided on this website. 
                This includes, but is not limited to:
            </p>
            <ul class="explanation-text disclaimer-list" style="list-style-type: disc; padding-left: 20px;">
                <li>Buying or selling decisions regarding real estate.</li>
                <li>Financial planning or budgeting errors.</li>
                <li>Disputes with contractors or insurance providers.</li>
            </ul>
            <p class="explanation-text" style="margin-top: 20px;">
                By using this site, you acknowledge that you are responsible for your own due diligence.
            </p>
        </section>

    </main>

    @template.layout._footer()
</body>
</html>
