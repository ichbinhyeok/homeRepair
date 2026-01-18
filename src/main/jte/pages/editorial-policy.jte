@param String baseUrl

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <title>Editorial Policy | Home Repair Verdict</title>
    <meta name="description" content="Our editorial standards, fact-checking processes, and commitment to unbiased, evidence-based home repair cost analysis.">
    
    <link rel="canonical" href="${baseUrl}/editorial-policy">
    <link rel="icon" href="/favicon.ico">
    <link rel="stylesheet" href="/css/styles.css">
    
    <style>
        .policy-container {
            max-width: 900px;
            margin: 60px auto;
            padding: 0 30px;
        }
        
        .principle-box {
            background: white;
            border-left: 4px solid #3b82f6;
            padding: 24px;
            margin: 20px 0;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    @template.layout._header()
    
    <div class="policy-container">
        <header style="margin-bottom: 60px;">
            <h1 style="font-size: 2.5em; color: #1e293b; margin-bottom: 16px;">Editorial Policy</h1>
            <p style="font-size: 1.1em; color: #64748b; line-height: 1.6;">
                Our commitment to accuracy, transparency, and editorial independence.
            </p>
            <p style="color: #64748b; margin-top: 12px;">
                <strong>Last Updated:</strong> January 2026
            </p>
        </header>
        
        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">Core Editorial Principles</h2>
            
            <div class="principle-box">
                <h3 style="margin-top: 0; color: #1e40af;">1. Evidence-Based Analysis Only</h3>
                <p style="line-height: 1.8;">
                    Every cost estimate, risk assessment, and repair recommendation must be traceable to a verifiable data source:
                </p>
                <ul style="line-height: 1.8;">
                    <li><strong>Primary sources:</strong> RSMeans® Construction Cost Data, BLS wage statistics, ASHRAE climate data</li>
                    <li><strong>Secondary sources:</strong> Peer-reviewed construction research, ICC building code archives</li>
                    <li><strong>Prohibited sources:</strong> Anonymous contractor quotes, unverified user testimonials, opinion blogs</li>
                </ul>
            </div>
            
            <div class="principle-box">
                <h3 style="margin-top: 0; color: #1e40af;">2. Algorithmic Transparency</h3>
                <p style="line-height: 1.8;">
                    Our cost calculation methodology is fully documented on the <a href="/home-repair/methodology" style="color: #3b82f6;">Methodology page</a>. 
                    We do NOT use proprietary "black box" algorithms that hide how estimates are derived. Any user should be able to 
                    manually reproduce our calculations given the same input data.
                </p>
            </div>
            
            <div class="principle-box">
                <h3 style="margin-top: 0; color: #1e40af;">3. Zero Tolerance for Conflicts of Interest</h3>
                <p style="line-height: 1.8;">
                    <strong>We accept NO financial compensation from:</strong>
                </p>
                <ul style="line-height: 1.8;">
                    <li>Contractors or construction companies seeking preferential recommendations</li>
                    <li>Material suppliers attempting to bias product selections</li>
                    <li>Home inspection services offering referral fees</li>
                    <li>Real estate platforms conditioning data access on favorable outcomes</li>
                </ul>
                <p style="line-height: 1.8; margin-top: 16px;">
                    Our business model is advertising-based (display ads only), ensuring complete editorial independence.
                </p>
            </div>
            
            <div class="principle-box">
                <h3 style="margin-top: 0; color: #1e40af;">4. Conservative Risk Flagging</h3>
                <p style="line-height: 1.8;">
                    When dealing with safety-critical issues (electrical hazards, structural defects, hazardous materials), 
                    we intentionally err on the side of caution. It is better to over-warn users about potential Chinese drywall 
                    or polybutylene plumbing than to minimize legitimate risks.
                </p>
            </div>
        </section>
        
        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">Data Update Cadence</h2>
            
            <table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
                <thead>
                    <tr style="background: #f1f5f9; border-bottom: 2px solid #cbd5e1;">
                        <th style="padding: 12px; text-align: left; font-weight: 600;">Data Type</th>
                        <th style="padding: 12px; text-align: left; font-weight: 600;">Update Frequency</th>
                    </tr>
                </thead>
                <tbody>
                    <tr style="border-bottom: 1px solid #e5e7eb;">
                        <td style="padding: 12px;">RSMeans Cost Data</td>
                        <td style="padding: 12px;"><strong>Quarterly</strong> (Jan, Apr, Jul, Oct)</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e7eb;">
                        <td style="padding: 12px;">BLS Labor Statistics</td>
                        <td style="padding: 12px;"><strong>Annually</strong> (May release)</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #e5e7eb;">
                        <td style="padding: 12px;">Climate Zone Data</td>
                        <td style="padding: 12px;"><strong>Static</strong> (ASHRAE standard)</td>
                    </tr>
                    <tr>
                        <td style="padding: 12px;">Defect Probability Models</td>
                        <td style="padding: 12px;"><strong>Bi-annually</strong> (based on claim data)</td>
                    </tr>
                </tbody>
            </table>
        </section>
        
        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">Corrections Policy</h2>
            
            <p style="font-size: 1.05em; line-height: 1.8; margin-bottom: 20px;">
                If you identify a factual error, data inaccuracy, or calculation mistake:
            </p>
            
            <ol style="font-size: 1.05em; line-height: 2;">
                <li><strong>Report it:</strong> Email <a href="mailto:spainhyeok@gmail.com" style="color: #3b82f6;">spainhyeok@gmail.com</a> 
                    with specific details and supporting documentation</li>
                <li><strong>Review timeline:</strong> We investigate all correction requests within 5 business days</li>
                <li><strong>Transparency:</strong> If an error is confirmed, we update the affected content and publish a 
                    correction notice stating what was changed and when</li>
            </ol>
        </section>
        
        <section style="background: #fef3c7; border: 2px solid #f59e0b; border-radius: 12px; padding: 30px; margin: 60px 0;">
            <h3 style="margin-top: 0; color: #92400e;">Important Disclaimer</h3>
            <p style="line-height: 1.8; margin-bottom: 0;">
                <strong>This platform provides informational estimates only. It does NOT replace:</strong>
            </p>
            <ul style="line-height: 1.8; margin-bottom: 0;">
                <li>Professional home inspections by licensed inspectors</li>
                <li>Detailed contractor quotes for actual job sites</li>
                <li>Engineering assessments for structural issues</li>
                <li>Legal advice regarding building code compliance or liability</li>
            </ul>
        </section>
        
        <footer style="margin-top: 80px; padding-top: 40px; border-top: 2px solid #e5e7eb; text-align: center; color: #64748b;">
            <p style="margin-top: 20px;">
                <a href="/home-repair" style="color: #3b82f6; text-decoration: none; font-weight: 600;">← Back to Home</a> |
                <a href="/home-repair/about" style="color: #3b82f6; text-decoration: none; font-weight: 600; margin-left: 20px;">About Us</a>
            </p>
        </footer>
    </div>
    
    @template.layout._footer()
</body>
</html>
