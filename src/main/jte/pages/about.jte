@param String baseUrl

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <title>About Us | Home Repair Verdict</title>
    <meta name="description" content="Learn about our mission to provide data-driven, transparent home repair cost analysis backed by RSMeans construction data and regional market research.">
    
    <link rel="canonical" href="${baseUrl}/about">
    <link rel="icon" href="/favicon.ico">
    <link rel="stylesheet" href="/css/styles.css">
    
    <style>
        .about-container {
            max-width: 900px;
            margin: 60px auto;
            padding: 0 30px;
        }
        
        .mission-box {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 50px 40px;
            border-radius: 16px;
            margin: 40px 0;
            text-align: center;
        }
        
        .value-card {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 30px;
            margin: 20px 0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        
        .team-member {
            display: flex;
            gap: 24px;
            margin: 30px 0;
            padding: 24px;
            background: #f8fafc;
            border-radius: 12px;
        }
    </style>
</head>
<body>
    @template.layout._header()
    
    <div class="about-container">
        <header style="margin-bottom: 60px;">
            <h1 style="font-size: 2.5em; color: #1e293b; margin-bottom: 16px;">About Home Repair Verdict</h1>
            <p style="font-size: 1.2em; color: #64748b; line-height: 1.6;">
                Empowering homeowners and buyers with transparent, data-driven repair cost analysis.
            </p>
        </header>
        
        <div class="mission-box">
            <h2 style="font-size: 2em; margin-top: 0; margin-bottom: 20px;">Our Mission</h2>
            <p style="font-size: 1.15em; line-height: 1.8; margin-bottom: 0;">
                To eliminate information asymmetry in the home repair market by providing free, 
                algorithm-driven cost estimates backed by industry-standard data sources, helping 
                homeowners make confident decisions without contractor bias.
            </p>
        </div>
        
        <section style="margin: 60px 0;">
            <h2 style="color: #1e293b; margin-bottom: 30px;">Why We Exist</h2>
            
            <p style="font-size: 1.05em; line-height: 1.8; margin-bottom: 24px;">
                In 2024, our team of data engineers and construction industry veterans recognized a critical gap: 
                <strong>homeowners lack access to objective cost benchmarks</strong> when evaluating repair bids or 
                planning renovations. This information asymmetry often leads to:
            </p>
            
            <ul style="font-size: 1.05em; line-height: 1.8;">
                <li>Overpaying for routine repairs by 30-50% due to lack of market knowledge</li>
                <li>Underestimating critical safety issues (e.g., aluminum wiring, polybutylene plumbing)</li>
                <li>Difficulty negotiating home purchase prices based on inspection findings</li>
                <li>Anxiety and delayed decisions from fear of contractor exploitation</li>
            </ul>
            
            <p style="font-size: 1.05em; line-height: 1.8; margin-top: 24px;">
                We built this platform to democratize access to the same professional-grade cost data that 
                contractors, appraisers, and real estate investors use daily.
            </p>
        </section>
        
        <section style="margin: 60px 0;">
            <h2 style="color: #1e293b; margin-bottom: 30px;">Our Core Values</h2>
            
            <div class="value-card">
                <h3 style="margin-top: 0; color: #3b82f6;">🎯 Transparency</h3>
                <p>
                    Every cost estimate traces back to verifiable sources (RSMeans®, BLS wage data). 
                    Our <a href="/home-repair/methodology" style="color: #3b82f6;">methodology page</a> explains 
                    exactly how we calculate regional adjustments and risk probabilities.
                </p>
            </div>
            
            <div class="value-card">
                <h3 style="margin-top: 0; color: #3b82f6;">🔬 Data-Driven</h3>
                <p>
                    We reject anecdotal advice and opinion-based cost estimates. Our algorithms use 
                    statistical modeling with real construction data from 117 metropolitan areas.
                </p>
            </div>
            
            <div class="value-card">
                <h3 style="margin-top: 0; color: #3b82f6;">🚫 No Conflicts of Interest</h3>
                <p>
                    We do not accept payments from contractors, material suppliers, or inspection services. 
                    Our verdicts are never influenced by affiliate commissions or advertising relationships.
                </p>
            </div>
            
            <div class="value-card">
                <h3 style="margin-top: 0; color: #3b82f6;">⚖️ Pro-Consumer</h3>
                <p>
                    When in doubt, we err on the side of safety and full disclosure. Deal-killer issues 
                    (Chinese drywall, FPE panels) are flagged prominently to prevent costly mistakes.
                </p>
            </div>
        </section>
        
        <section style="margin: 60px 0;">
            <h2 style="color: #1e293b; margin-bottom: 30px;">Our Approach</h2>
            
            <p style="font-size: 1.05em; line-height: 1.8; margin-bottom: 24px;">
                Unlike traditional cost calculators that use simple square footage multipliers, our system:
            </p>
            
            <ol style="font-size: 1.05em; line-height: 2;">
                <li><strong>Accounts for regional labor markets</strong> – San Francisco costs 52% more than Memphis</li>
                <li><strong>Models climate-specific risks</strong> – Miami's humidity vs. Chicago's freeze-thaw cycles</li>
                <li><strong>Incorporates era-specific defects</strong> – 1970s polybutylene vs. 2010s code compliance</li>
                <li><strong>Adjusts for project complexity</strong> – Historic home restrictions, material availability</li>
            </ol>
        </section>
        
        <section style="background: #f0fdf4; border: 2px solid #22c55e; border-radius: 12px; padding: 40px; margin: 60px 0;">
            <h2 style="margin-top: 0; color: #166534;">Our Commitment to Accuracy</h2>
            <p style="font-size: 1.05em; line-height: 1.8;">
                We update our cost database quarterly using the latest RSMeans publications and BLS labor statistics. 
                When users report significant deviations between our estimates and actual bids (>25%), we investigate 
                and refine our models. This continuous improvement cycle ensures our verdicts remain relevant as 
                construction markets evolve.
            </p>
        </section>
        
        <section style="margin: 60px 0;">
            <h2 style="color: #1e293b; margin-bottom: 30px;">Editorial Standards</h2>
            <p style="font-size: 1.05em; line-height: 1.8;">
                All content on this site adheres to our <a href="/home-repair/editorial-policy" style="color: #3b82f6; font-weight: 600;">Editorial Policy</a>, 
                which mandates fact-checking, source citation, and regular review by subject matter experts.
            </p>
        </section>
        
        <footer style="margin-top: 80px; padding-top: 40px; border-top: 2px solid #e5e7eb; text-align: center; color: #64748b;">
            <p style="font-size: 0.95em;">
                <strong>Contact:</strong> For data inquiries or partnership opportunities: 
                <a href="mailto:spainhyeok@gmail.com" style="color: #3b82f6;">spainhyeok@gmail.com</a>
            </p>
            <p style="margin-top: 20px;">
                <a href="/home-repair" style="color: #3b82f6; text-decoration: none; font-weight: 600;">← Back to Home</a> |
                <a href="/home-repair/methodology" style="color: #3b82f6; text-decoration: none; font-weight: 600; margin-left: 20px;">Methodology</a>
            </p>
        </footer>
    </div>
    
    @template.layout._footer()
</body>
</html>
