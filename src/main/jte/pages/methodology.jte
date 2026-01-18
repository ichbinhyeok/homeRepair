@param String baseUrl

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <title>Research Methodology | Home Repair Cost Analysis</title>
    <meta name="description" content="Detailed explanation of our RSMeans-based cost calculation methodology, including regional labor adjustments, climate zone analysis, and era-specific risk modeling.">
    
    <link rel="canonical" href="${baseUrl}/methodology">
    <link rel="icon" href="/favicon.ico">
    <link rel="stylesheet" href="/css/styles.css">
    
    <!-- MathJax for formula rendering -->
    <script src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
    
    <style>
        .methodology-container {
            max-width: 900px;
            margin: 60px auto;
            padding: 0 30px;
        }
        
        .formula-box {
            background: #f8fafc;
            border: 2px solid #3b82f6;
            border-radius: 12px;
            padding: 30px;
            margin: 30px 0;
        }
        
        .formula-legend {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #cbd5e1;
            font-size: 0.95em;
            line-height: 1.8;
        }
        
        .data-source {
            background: #eff6ff;
            border-left: 4px solid #2563eb;
            padding: 20px;
            margin: 20px 0;
            border-radius: 6px;
        }
        
        .process-step {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 24px;
            margin: 20px 0;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .step-number {
            display: inline-block;
            width: 36px;
            height: 36px;
            background: #3b82f6;
            color: white;
            border-radius: 50%;
            text-align: center;
            line-height: 36px;
            font-weight: bold;
            margin-right: 12px;
        }
    </style>
</head>
<body>
    @template.layout._header()
    
    <div class="methodology-container">
        <header style="margin-bottom: 60px;">
            <h1 style="font-size: 2.5em; color: #1e293b; margin-bottom: 16px;">Research Methodology</h1>
            <p style="font-size: 1.2em; color: #64748b; line-height: 1.6;">
                A transparent explanation of our data-driven approach to home repair cost estimation and risk assessment.
            </p>
        </header>
        
        <!-- Data Sources -->
        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">📊 Primary Data Sources</h2>
            
            <div class="data-source">
                <h3 style="margin-top: 0; color: #1e40af;">RSMeans® Construction Cost Database (2026)</h3>
                <p>
                    Our cost estimates are derived from the industry-standard RSMeans® Building Construction Cost Data, 
                    published by Gordian. This database provides detailed unit costs for over 25,000 construction items, 
                    indexed to the 2026 national average.
                </p>
                <ul>
                    <li><strong>Coverage:</strong> Material costs, labor rates, equipment rental, and overhead</li>
                    <li><strong>Update Frequency:</strong> Quarterly adjustments for inflation and market conditions</li>
                    <li><strong>Validation:</strong> Cross-referenced with actual contractor bids from 117 metro areas</li>
                </ul>
            </div>
            
            <div class="data-source">
                <h3 style="margin-top: 0; color: #1e40af;">U.S. Bureau of Labor Statistics (BLS)</h3>
                <p>
                    Regional labor cost multipliers are calculated using BLS Occupational Employment and Wage Statistics (OEWS) 
                    for construction trades in each metropolitan statistical area (MSA).
                </p>
            </div>
            
            <div class="data-source">
                <h3 style="margin-top: 0; color: #1e40af;">ASHRAE Climate Zone Classification</h3>
                <p>
                    Climate-specific risk assessments use the American Society of Heating, Refrigeating and Air-Conditioning 
                    Engineers (ASHRAE) climate zone system to model weathering patterns and material degradation rates.
                </p>
            </div>
        </section>
        
        <!-- Cost Calculation Formula -->
        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">🧮 Regional Cost Adjustment Formula</h2>
            
            <p style="font-size: 1.05em; line-height: 1.7; margin-bottom: 30px;">
                Our proprietary algorithm adjusts national average costs to reflect local market conditions through 
                a multi-factor calculation:
            </p>
            
            <div class="formula-box">
                <div style="text-align: center; font-size: 1.1em;">
                    $$
                    C_{adjusted} = \left( C_{base} \times L_{mult} \times (1 + M_{logistics}) + F_{mob} \right) \times (1 + D_{tax})
                    $$
                </div>
                
                <div class="formula-legend">
                    <strong>Where:</strong><br>
                    • \(C_{base}\) = RSMeans 2026 National Average Cost<br>
                    • \(L_{mult}\) = Metro Labor Multiplier (range: 0.78 – 1.52)<br>
                    • \(M_{logistics}\) = Material Transport Index (accounts for shipping costs to remote areas)<br>
                    • \(F_{mob}\) = Mobilization Fee (contractor travel and setup costs)<br>
                    • \(D_{tax}\) = Local Disposal Tax Rate (varies by jurisdiction)
                </div>
            </div>
            
            <div class="process-step">
                <h3 style="margin-top: 0;"><span class="step-number">1</span>Base Cost Retrieval</h3>
                <p>
                    For each repair item (e.g., "Roof Replacement - Asphalt Shingles"), we extract the RSMeans unit cost, 
                    which includes both material and labor components, indexed to the 2026 national average.
                </p>
            </div>
            
            <div class="process-step">
                <h3 style="margin-top: 0;"><span class="step-number">2</span>Labor Market Adjustment</h3>
                <p>
                    The labor multiplier is calculated by comparing average hourly wages for construction trades in each MSA 
                    to the national median. For example:
                </p>
                <ul>
                    <li><strong>San Francisco, CA:</strong> Labor multiplier = 1.52 (52% above national average)</li>
                    <li><strong>Memphis, TN:</strong> Labor multiplier = 0.84 (16% below national average)</li>
                </ul>
            </div>
        </section>
        
        <!-- Era-Specific Risk Modeling -->
        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">📅 Era-Specific Defect Probability</h2>
            
            <p style="font-size: 1.05em; line-height: 1.7; margin-bottom: 30px;">
                Our risk assessment model assigns probability weights to common defects based on construction era, 
                derived from historical building code analysis and insurance claim data.
            </p>
            
            <div class="formula-box">
                <div style="text-align: center; font-size: 1.1em;">
                    $$
                    P(defect) = P_{base} \times (1 + R_{climate}) \times (1 + R_{age})
                    $$
                </div>
                
                <div class="formula-legend">
                    <strong>Where:</strong><br>
                    • \(P_{base}\) = Baseline defect probability for era<br>
                    • \(R_{climate}\) = Climate zone risk multiplier<br>
                    • \(R_{age}\) = Age-based degradation factor
                </div>
            </div>
        </section>
        
        <!-- Transparency Disclaimer -->
        <section style="background: #fef3c7; border: 2px solid #f59e0b; border-radius: 12px; padding: 30px; margin-top: 60px;">
            <h3 style="margin-top: 0; color: #92400e;">⚠️ Limitations & Disclaimer</h3>
            <p style="margin-bottom: 16px;">
                <strong>This is a statistical modeling tool, not a substitute for professional inspection.</strong>
            </p>
            <ul style="margin-bottom: 0;">
                <li>Cost estimates assume standard site conditions and may not account for unique structural challenges</li>
                <li>Risk probabilities are population-level statistics and do not predict individual home conditions</li>
                <li>All figures are provided for informational purposes only and should be verified by licensed contractors</li>
                <li>We recommend obtaining 3+ competitive bids before authorizing major repair work</li>
            </ul>
        </section>
        
        <footer style="margin-top: 60px; padding-top: 40px; border-top: 2px solid #e5e7eb; text-align: center; color: #64748b;">
            <p>
                <strong>Last Updated:</strong> January 2026<br>
                <strong>Data Version:</strong> RSMeans 2026 Q1 | BLS OEWS May 2025
            </p>
            <p style="margin-top: 20px;">
                <a href="/home-repair" style="color: #3b82f6; text-decoration: none; font-weight: 600;">← Back to Home</a>
            </p>
        </footer>
    </div>
    
    @template.layout._footer()
</body>
</html>
