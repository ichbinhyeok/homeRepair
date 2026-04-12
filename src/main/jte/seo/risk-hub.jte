@param String riskSlug
@param String titleH1
@param java.util.List<com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink> l1Links
@param String baseUrl
@param java.util.List<java.util.Map<String, String>> faqItems

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
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="robots" content="noindex,follow" />
    <title>${titleH1} - Expert Cost & Negotiation Guide</title>
    <meta name="description" content="Complete guide on ${titleH1}: inspection checklist, typical costs, and negotiation strategies." />
    <link rel="canonical" href="${baseUrl}/home-repair/risks/${riskSlug}" />
    <link rel="stylesheet" href="/css/seo-report.css" />
    <!-- FAQ Schema -->
    <script type="application/ld+json">
    {
      "@context": "https://schema.org",
      "@type": "FAQPage",
      "mainEntity": [
      @for(int i = 0; i < faqItems.size(); i++)
        {
          "@type": "Question",
          "name": "${faqItems.get(i).get("q")}",
          "acceptedAnswer": {
            "@type": "Answer",
            "text": "${faqItems.get(i).get("a")}"
          }
        }${i < faqItems.size() - 1 ? "," : ""}
      @endfor
      ]
    }
    </script>
</head>
<body>
    @template.layout._header()
    <main class="container" style="padding: 4rem 1rem;">
        <div class="breadcrumbs" style="margin-bottom: 2rem;">
            <a href="/">Home</a> 
            <span class="separator">/</span>
            <a href="/home-repair/risks">Risks Hub</a>
            <span class="separator">/</span>
            <span class="current">${titleH1}</span>
        </div>
        
        <h1 style="font-size: 2.5rem; margin-bottom: 1rem;">${titleH1}</h1>
        <p style="font-size: 1.25rem; color: var(--slate-600); margin-bottom: 3rem; max-width: 800px;">
            A complete safety, inspection, and negotiation guide for home buyers dealing with ${riskSlug.replace("-", " ")}.
            Identify the core issues, get leverage, and understand the true cost to repair.
        </p>
        
        <div class="bento-grid">
            <div class="bento-card span-8">
                <h2>Forensic Inspection Checklist</h2>
                <ul style="line-height: 1.8; margin-top: 1rem; color: var(--slate-700);">
                    <li>Verify the installation era against the building permit.</li>
                    <li>Visually inspect exposed joints, wires, or structural connections.</li>
                    <li>Check for previous localized DIY repairs or band-aid solutions.</li>
                    <li>Look for signs of localized water damage, heat scorching, or corrosion.</li>
                    <li>Review any past home warranties or disclosures regarding this system.</li>
                    <li>Ensure current local code compliance if replacing partially.</li>
                </ul>
            </div>
            
            <div class="bento-card span-4" style="background: var(--slate-50);">
                 <h2>Negotiation Copy</h2>
                 <p style="font-size: 0.9rem; color: var(--slate-500); margin-bottom: 1rem;">Use this template to request a seller credit.</p>
                 <div style="background: white; padding: 1.5rem; border: 1px dashed var(--slate-300); border-radius: var(--radius-lg); font-family: monospace; font-size: 0.85rem; color: var(--slate-700); line-height: 1.6;">
                    "Our home inspector has identified a critical safety issue with the ${riskSlug.replace("-", " ")}. Since this system poses structural and longevity liabilities, we request a full seller credit based on formal local contractor bids to properly address this immediately after closing."
                 </div>
            </div>
            
            <div class="bento-card span-12">
                <h2>Where It Shows Up (By Era)</h2>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-top: 1.5rem;">
                    <div style="padding: 1rem; border: 1px solid var(--slate-200); border-radius: var(--radius-md);">
                        <h3 style="font-size: 1.1rem;">Pre-1950</h3>
                        <p style="color: var(--slate-600); font-size: 0.95rem; margin-top: 0.5rem;">Highest risk category. Systems are typically beyond their expected 50-year lifespan.</p>
                    </div>
                    <div style="padding: 1rem; border: 1px solid var(--slate-200); border-radius: var(--radius-md);">
                        <h3 style="font-size: 1.1rem;">1950-1970</h3>
                        <p style="color: var(--slate-600); font-size: 0.95rem; margin-top: 0.5rem;">Period of rapid building. Expect experimental materials that have begun failing.</p>
                    </div>
                    <div style="padding: 1rem; border: 1px solid var(--slate-200); border-radius: var(--radius-md);">
                        <h3 style="font-size: 1.1rem;">1970-1995</h3>
                        <p style="color: var(--slate-600); font-size: 0.95rem; margin-top: 0.5rem;">Moderate risk. Polybutylene and aluminum systems are highly localized here.</p>
                    </div>
                </div>
            </div>
            
            <div class="bento-card span-12" style="background: var(--slate-900); color: white;">
                <h2 style="color: white; margin-bottom: 1rem;">Check Costs in Your Market</h2>
                <p style="color: var(--slate-400); margin-bottom: 1.5rem;">Review forensic benchmark data for specific cities.</p>
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1rem;">
                    @for(var link : l1Links)
                        <a href="${link.getHref()}" style="color: #6ee7b7; text-decoration: none; padding: 0.5rem 0; border-bottom: 1px solid rgba(255,255,255,0.1);" data-analytics-event="risk_hub_to_l1_click" data-analytics-category="navigation" data-analytics-label="${riskSlug}">${link.getText()} &rarr;</a>
                    @endfor
                </div>
                <div style="margin-top: 2rem;">
                    <a href="/home-repair/verdicts/states" class="btn-outline" style="border-color: rgba(255,255,255,0.2); color: white;" data-analytics-event="risk_hub_to_states_click" data-analytics-category="navigation" data-analytics-label="${riskSlug}">Browse All States Hub</a>
                </div>
            </div>
            
            <!-- FAQ Section -->
            <div class="bento-card span-12">
                <h2>Frequently Asked Questions</h2>
                <div style="margin-top: 2rem;">
                    @for(var faq : faqItems)
                    <div style="margin-bottom: 1.5rem;">
                        <h3 style="font-size: 1.1rem;">${faq.get("q")}</h3>
                        <p style="color: var(--slate-600); margin-top: 0.5rem;">${faq.get("a")}</p>
                    </div>
                    @endfor
                </div>
            </div>
        </div>
    </main>
    @template.layout._footer()
</body>
</html>
