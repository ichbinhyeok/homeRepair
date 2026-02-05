@import com.fasterxml.jackson.databind.JsonNode
@param String title
@param String h1
@param JsonNode riskData
@param String canonicalUrl

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <meta name="description" content="Learn about ${h1}. Definition, risks, and remediation costs for homeowners.">
    <link rel="canonical" href="${canonicalUrl}">
    <link rel="stylesheet" href="/css/styles.css">
    
    <style>
        .glossary-container { max-width: 800px; margin: 40px auto; padding: 0 20px; }
        .risk-card { background: #fee2e2; border-left: 6px solid #ef4444; padding: 24px; margin: 30px 0; border-radius: 8px; }
        .definition-box { background: #f8fafc; padding: 30px; border-radius: 12px; margin-bottom: 40px; border: 1px solid #e2e8f0; }
        .meta-tag { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 0.85em; font-weight: bold; margin-right: 10px; }
        .meta-tag.critical { background: #fecaca; color: #b91c1c; }
        .meta-tag.info { background: #e0f2fe; color: #0369a1; }
    </style>
</head>
<body>
    @template.layout._header()

    <div class="glossary-container">
        <!-- Breadcrumb -->
        <nav style="margin-bottom: 20px; font-size: 0.9em; color: #64748b;">
            <a href="/" style="color: #64748b; text-decoration: none;">Home</a> > 
            <a href="/home-repair" style="color: #64748b; text-decoration: none;">Home Repair</a> > 
            <span style="color: #334155;">Glossary</span>
        </nav>

        <script type="application/ld+json">
        {
          "@context": "https://schema.org",
          "@type": "BreadcrumbList",
          "itemListElement": [
            {"@type": "ListItem", "position": 1, "name": "Home", "item": "https://lifeverdict.com/"},
            {"@type": "ListItem", "position": 2, "name": "Home Repair", "item": "https://lifeverdict.com/home-repair"},
            {"@type": "ListItem", "position": 3, "name": "Glossary", "item": "https://lifeverdict.com/home-repair/guides/${title}"}
          ]
        }
        </script>
        
        <script type="application/ld+json">
        {
          "@context": "https://schema.org",
          "@type": "DefinedTerm",
          "name": "${title.split(" - ")[0]}",
          "description": "${riskData.has("definition") ? riskData.get("definition").asText().replace("\"", "'") : "Definition of " + title}",
          "inDefinedTermSet": "https://lifeverdict.com/home-repair"
        }
        </script>

        <h1 style="font-size: 2.5em; margin-bottom: 10px;">${h1}</h1>
        <p style="font-size: 1.2em; color: #64748b; line-height: 1.6; margin-bottom: 40px;">
            Essential home repair guide for historical property owners.
        </p>

        <!-- Definition Section -->
        <section class="definition-box">
            <h2 style="margin-top: 0; color: #334155;">Definition</h2>
            <p style="font-size: 1.1em; line-height: 1.7; color: #1e293b;">
                ${riskData.has("definition") ? riskData.get("definition").asText() : "Definition not available."}
            </p>
        </section>

        <!-- Risk Analysis -->
        <section class="risk-card">
            <h2 style="margin-top: 0; color: #991b1b;">⚠️ Why is this a risk?</h2>
            
            <div style="margin-bottom: 20px;">
                @if(riskData.has("damage_scenario"))
                    <p style="font-size: 1.05em; line-height: 1.6; color: #7f1d1d;">
                        ${riskData.get("damage_scenario").asText()}
                    </p>
                @endif
            </div>

            <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                @if(riskData.has("severity"))
                     <span class="meta-tag critical">Severity: ${riskData.get("severity").asText()}</span>
                @endif
                @if(riskData.has("remedy_multiplier"))
                     <span class="meta-tag info">Cost Impact: ${riskData.get("remedy_multiplier").asText()}x Base</span>
                @endif
            </div>
        </section>

        <!-- CTA -->
        <div style="background: #f0fdf4; padding: 30px; border-radius: 12px; text-align: center; border: 2px solid #bbf7d0; margin-top: 60px;">
            <h3 style="margin-top: 0; color: #166534;">Concerned about this in your home?</h3>
            <p style="margin-bottom: 20px; color: #15803d;">Get a personalized repair estimate based on your home's era and location.</p>
            <a href="/home-repair" style="display: inline-block; background: #16a34a; color: white; padding: 12px 24px; border-radius: 6px; text-decoration: none; font-weight: bold;">Check My Home</a>
        </div>

    </div>

    @template.layout._footer()
</body>
</html>
