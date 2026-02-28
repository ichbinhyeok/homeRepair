@import com.livingcostcheck.home_repair.seo.StateHubGeneratorService
@import java.util.List
@import java.util.Map

@param String stateName
@param String stateCode
@param List<StateHubGeneratorService.CityInfo> cities
@param Map<String, Double> avgCostByEra
@param String climateDescription
@param String title
@param String h1
@param String baseUrl
@param String canonicalUrl

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

    <title>${title}</title>
    <meta name="description" content="Comprehensive home repair cost analysis for ${stateName}. Compare costs across ${cities.size()} cities, climate-specific risks, and era-based repair priorities.">

    <link rel="canonical" href="${canonicalUrl}">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/seo-report.css">

    <style>
        .state-hub-container {
            max-width: 1200px;
            margin: 60px auto;
            padding: 0 30px;
        }

        .state-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 60px 40px;
            border-radius: 16px;
            margin-bottom: 40px;
            text-align: center;
        }

        .city-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 16px;
            margin: 30px 0;
        }

        .city-card {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 20px;
            transition: all 0.2s;
        }

        .city-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }

        .era-chip {
            color: #3b82f6;
            text-decoration: none;
            font-size: 0.78em;
            font-weight: 600;
            padding: 4px 8px;
            border: 1px solid #bfdbfe;
            border-radius: 999px;
            background: #eff6ff;
            white-space: nowrap;
        }

        .stat-box {
            background: #f8fafc;
            border-left: 4px solid #3b82f6;
            padding: 24px;
            margin: 20px 0;
            border-radius: 8px;
        }
    </style>
</head>
<body>
    @template.layout._header()

    <div class="state-hub-container">
        <div class="state-header">
            <h1 style="font-size: 2.5em; margin: 0 0 16px 0;">${h1}</h1>
            <p style="font-size: 1.2em; opacity: 0.95; margin: 0;">
                Detailed cost analysis for ${cities.size()} cities across ${stateName}
            </p>
        </div>

        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">${stateName} Market Overview</h2>

            <div class="stat-box">
                <h3 style="margin-top: 0; color: #1e40af;">Climate Characteristics</h3>
                <p style="line-height: 1.8;">${climateDescription}</p>
            </div>

            <div class="stat-box">
                <h3 style="margin-top: 0; color: #1e40af;">Average Repair Costs by Era</h3>
                <table style="width: 100%; border-collapse: collapse; margin-top: 16px;">
                    <thead>
                        <tr style="background: #f1f5f9; border-bottom: 2px solid #cbd5e1;">
                            <th style="padding: 12px; text-align: left;">Era</th>
                            <th style="padding: 12px; text-align: right;">Avg Cost Range</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var entry : avgCostByEra.entrySet())
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 12px;">${entry.getKey()}</td>
                                <td style="padding: 12px; text-align: right; font-weight: 600;">
                                    $${String.format("%,.0f", entry.getValue())}
                                </td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
        </section>

        <section style="margin-bottom: 60px;">
            <h2 style="color: #1e293b; margin-bottom: 24px;">Explore Cities in ${stateName}</h2>
            <p style="color: #64748b; margin-bottom: 30px;">
                Select a city and era to view detailed repair cost analysis.
            </p>

            !{
                String[] eraLabels = new String[] {
                    "Pre-1950",
                    "1950-1970",
                    "1970-1980",
                    "1980-1995",
                    "1995-2010",
                    "2010-Present"
                };
                String[] eraSlugs = new String[] {
                    "pre-1950",
                    "1950-1970",
                    "1970-1980",
                    "1980-1995",
                    "1995-2010",
                    "2010-present"
                };
            }

            <div class="city-grid">
                @for(var city : cities)
                    <div class="city-card">
                        <h3 style="margin: 0 0 12px 0; font-size: 1.1em; color: #1e293b;">${city.name()}</h3>
                        <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                            @for(int i = 0; i < eraLabels.length; i++)
                                <a href="/home-repair/verdicts/${city.slug()}/${eraSlugs[i]}.html" class="era-chip">
                                    ${eraLabels[i]}
                                </a>
                            @endfor
                        </div>
                    </div>
                @endfor
            </div>
        </section>

        <section style="background: #eff6ff; border: 2px solid #3b82f6; border-radius: 12px; padding: 40px; margin: 60px 0;">
            <h2 style="margin-top: 0; color: #1e40af;">${stateName} Regional Insights</h2>
            <ul style="line-height: 2; font-size: 1.05em;">
                <li><strong>Labor Market:</strong> Costs vary significantly between metro and rural areas</li>
                <li><strong>Building Codes:</strong> State-specific requirements may affect renovation costs</li>
                <li><strong>Climate Impact:</strong> ${climateDescription}</li>
                <li><strong>Material Availability:</strong> Regional supply chains influence pricing</li>
            </ul>
        </section>

        <footer style="margin-top: 80px; padding-top: 40px; border-top: 2px solid #e5e7eb; text-align: center; color: #64748b;">
            <p>
                <a href="/" style="color: #3b82f6; text-decoration: none; font-weight: 600;">Back to Home</a> |
                <a href="/home-repair/methodology" style="color: #3b82f6; text-decoration: none; font-weight: 600; margin-left: 20px;">Methodology</a>
            </p>
        </footer>
    </div>

    @template.layout._footer()
</body>
</html>