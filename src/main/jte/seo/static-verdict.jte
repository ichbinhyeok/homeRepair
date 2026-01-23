@import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*
@import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink
@import java.util.List
@import java.util.Map

@param String title
@param String h1Content
@param String metroCode
@param String metroName
@param String era
@param String eraName
@param Verdict verdict
@param List<InternalLink> eraLinks
@param List<InternalLink> cityLinks
@param String baseUrl
@param String canonicalUrl
@param String faqSchema
@param List<InternalLink> stateLinks
@param String climateFragment
@param String eraFragment
@param String costFragment
@param String regionalInsight
@param List<Map<String, String>> faqItems
@param String lowPrice
@param String highPrice

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <title>${title}</title>
    <!-- Description optimized for curiosity, matching the H1 tone -->
    <meta name="description" content="${h1Content} LifeVerdict analyzes specific risk factors for ${eraName} homes in ${metroName}. See the full breakdown before you buy.">
    
    <link rel="canonical" href="${canonicalUrl}">
    <link rel="icon" href="/favicon.ico">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    
    <!-- Open Graph / Social Media -->
    <meta property="og:type" content="article">
    <meta property="og:title" content="${title}">
    <meta property="og:description" content="${h1Content}">
    <meta property="og:url" content="${canonicalUrl}">
    
    <!-- Schema.org Structured Data -->
    <script type="application/ld+json">
    {
        "@context": "https://schema.org",
        "@type": "WebApplication",
        "name": "Home Repair Cost Calculator - ${metroName}",
        "applicationCategory": "FinanceApplication",
        "description": "Professional home repair cost analysis calculator for ${eraName} homes in ${metroName}",
        "operatingSystem": "Any",
        "offers": {
            "@type": "AggregateOffer",
            "priceCurrency": "USD",
            "lowPrice": "${lowPrice}",
            "highPrice": "${highPrice}"
        },
        "featureList": "Cost estimation, Repair prioritization, Era-specific analysis"
    }
    </script>
    
    <script type="application/ld+json">
    {
        "@context": "https://schema.org",
        "@type": "HowTo",
        "name": "How to Budget for ${eraName} Home Repairs in ${metroName}",
        "description": "${verdict.getHeadline()}",
        "step": [
        @for(var item : verdict.getPlan().getMustDo())
            {
                "@type": "HowToStep",
                "name": "${item.getPrettyName()}",
                "text": "${item.getExplanation() != null ? item.getExplanation().replace("\"", "'") : ""}"
            }@if(!item.equals(verdict.getPlan().getMustDo().get(verdict.getPlan().getMustDo().size() - 1))),@endif
        @endfor
        ]
    }
    </script>
    
    <!-- FAQ Schema for Rich Snippets (CTR Boost) -->
    $unsafe{faqSchema}
    
    <style>
        /* Page-specific minimal styles that sit on top of shared styles.css */
        .verdict-container {
            max-width: 800px;
            margin: 40px auto;
            padding: 0 20px;
        }

        h1 {
            color: #1a1a1a;
            border-bottom: 3px solid #4CAF50;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        
        .verdict-tier {
            display: inline-block;
            padding: 8px 16px;
            border-radius: 4px;
            font-weight: bold;
            margin-bottom: 30px;
            font-size: 1.1em;
        }
        .verdict-tier.APPROVED { background: #4CAF50; color: white; }
        .verdict-tier.WARNING { background: #FF9800; color: white; }
        .verdict-tier.DENIED { background: #f44336; color: white; }
        
        .headline {
            font-size: 1.3em;
            margin: 0 0 40px;
            padding: 20px;
            background: #f5f5f5;
            border-left: 5px solid #4CAF50;
            line-height: 1.5;
        }
        
        section { margin-bottom: 50px; }
        
        .must-do-item {
            margin: 20px 0;
            padding: 20px;
            background: #fff3cd;
            border-left: 4px solid #ff9800;
            border-radius: 4px;
        }
        .must-do-item h3 {
            margin-top: 0;
            color: #d68000;
        }
        
        .should-do {
            background: #e3f2fd;
            padding: 20px;
            margin: 20px 0;
            border-left: 4px solid #2196F3;
            border-radius: 4px;
        }
        
        .cta-section {
            text-align: center;
            background: #f0fdf4;
            padding: 40px;
            border-radius: 10px;
            border: 2px solid #bbf7d0;
        }
        
        .internal-links {
            padding: 30px;
            background: #fff;
            border: 1px solid #eee;
            border-radius: 8px;
        }
        .internal-links h3 {
            margin-top: 0;
            font-size: 1.1em;
            border-bottom: none;
        }
        .internal-links a {
            display: inline-block;
            margin-right: 15px;
            margin-bottom: 10px;
            color: #1976D2;
            text-decoration: none;
        }
        .internal-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    @template.layout._header()

    <div class="verdict-container">
        <!-- TOP AD PLACEHOLDER (LEADERBOARD) -->
        <div class="ad-placeholder-top" style="width: 100%; height: 90px; background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 8px; margin-bottom: 30px; display: flex; align-items: center; justify-content: center; color: #94a3b8; font-size: 12px; font-weight: 500; text-transform: uppercase; letter-spacing: 1px;">
            <span>&mdash; Advertisement Placeholder &mdash;</span>
        </div>
        
        <!-- Main Verdict Card -->
        <article class="card verdict-header-card">
            <header class="verdict-header">
                <div>
                    <div class="flex items-center gap-2 mb-3">
                        <span class="px-3 py-1 bg-blue-50 text-blue-700 text-xs font-bold rounded-full uppercase tracking-widest border border-blue-100">Market Benchmark Report</span>
                    </div>
                     <h1 class="text-3xl md:text-5xl font-bold tracking-tight text-slate-900 mb-4 leading-tight">
                        ${h1Content}
                    </h1>
                     <p class="text-lg text-slate-500 max-w-2xl leading-relaxed">
                        A data-driven analysis of historical repair costs for ${eraName} properties in the ${metroName} area.
                    </p>
                </div>
            </header>

            <section class="verdict-summary">
                <h2 class="section-title">
                    <span>📊</span> Regional Cost Analysis
                </h2>
                <div class="headline explanation-text">
                    ${verdict.getHeadline()}
                </div>
                
                @if(verdict.getContextBriefing() != null && verdict.getContextBriefing().getDynamicNarrative() != null)
                    <div class="card pSEO-narrative" style="background: #f8fafc; border: 1px solid #e2e8f0; padding: 24px; border-radius: 12px; font-style: italic; line-height: 1.6; color: #475569; margin-bottom: 30px;">
                        <strong>Expert Insight:</strong> ${verdict.getContextBriefing().getDynamicNarrative()}
                    </div>
                @endif
                
                <!-- FragmentLibrary: Unique contextual insights -->
                <div class="contextual-insights" style="display: grid; gap: 16px; margin-top: 24px;">
                    <div style="background: #eff6ff; border-left: 4px solid #3b82f6; padding: 16px; border-radius: 8px;">
                        <strong style="color: #1e40af;">🌡️ Climate Impact:</strong> ${climateFragment}
                    </div>
                    <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 16px; border-radius: 8px;">
                        <strong style="color: #047857;">🏡 Era Context:</strong> ${eraFragment}
                    </div>
                    <div style="background: #fef3c7; border-left: 4px solid #f59e0b; padding: 16px; border-radius: 8px;">
                        <strong style="color: #d97706;">💰 Local Market:</strong> ${costFragment}
                    </div>
                </div>

                <!-- Phase 2: Regional Insight (Why This Region Is Different) -->
                @if(regionalInsight != null && !regionalInsight.isEmpty())
                    <div class="regional-insight-block" style="background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%); border: 2px solid #0284c7; padding: 20px; border-radius: 12px; margin-top: 24px;">
                        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
                            <span style="font-size: 1.3em;">🔍</span>
                            <strong style="color: #075985; font-size: 1.05em;">Why ${metroName} is different</strong>
                        </div>
                        <p style="margin: 0; line-height: 1.7; color: #0c4a6e; font-size: 0.95em;">
                            ${regionalInsight}
                        </p>
                    </div>
                @endif
            </section>

            <!-- MONETIZATION PLACEHOLDER: VERIFIED PROS -->
            <section class="verified-pros-ghost" style="background: linear-gradient(135deg, #eff6ff 0%, #fef2f2 100%); padding: 24px; border-radius: 16px; border: 1px dashed #3b82f6; margin-bottom: 40px;">
                <h3 style="margin-top:0; font-size: 1rem; color: #1e40af; display: flex; align-items: center; gap: 8px;">
                    <span>🛡️</span> Verified Local Specialists in ${metroName}
                </h3>
                <p style="font-size: 0.85rem; color: #64748b;">Finding qualified contractors for ${eraName} homes requires specialized expertise. Our certified network is launching soon in this region.</p>
                <div style="display: flex; gap: 12px; margin-top: 16px; opacity: 0.6;">
                    <div style="flex: 1; background: white; height: 12px; border-radius: 6px;"></div>
                    <div style="flex: 1; background: white; height: 12px; border-radius: 6px;"></div>
                </div>
            </section>
        </article>
    
        <!-- Critical Repairs Section -->
        <section id="must-do-repairs" class="card">
            <h2 class="section-title text-danger">
                <span>🔴</span> Critical Repairs (Must-Do)
            </h2>
            <p class="explanation-text" style="margin-bottom: 24px;">
                The following repairs are critical safety or structural issues common to ${eraName} homes in this region. 
                Immediate attention is recommended.
            </p>
            
            @if(verdict.getPlan().getMustDo().isEmpty())
                <p class="data-meta">No critical repairs identified for this specific scenario.</p>
            @else
                <div class="data-list">
                    @for(var item : verdict.getPlan().getMustDo())
                        <div class="data-item">
                            <h3 style="margin: 0 0 12px 0; font-size: 1.1em; color: #1e293b;">${item.getPrettyName()}</h3>
                            
                            @if(item.getExplanation() != null && !item.getExplanation().isEmpty())
                                <p class="explanation-text" style="font-size: 0.9em; margin: 0 0 16px 0; line-height: 1.6; color: #475569;">
                                    ${item.getExplanation().replace("**", "").replace("*", "")}
                                </p>
                            @endif
                            
                            <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px;">
                                @if(item.isMandatory())
                                    <span style="background: #fee2e2; color: #991b1b; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: 700; text-transform: uppercase;">Code Mandatory</span>
                                @endif
                                
                                @if(item.getRiskFlags() != null)
                                    @for(var flag : item.getRiskFlags())
                                        @if(flag.contains("ERA_RISK"))
                                            <span style="background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: 700;">HISTORICAL RISK</span>
                                        @endif
                                    @endfor
                                @endif

                                @if(item.getCompoundingBadge() != null)
                                    <span style="background: #eff6ff; color: #1e40af; border: 1px solid #dbeafe; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: 700;">
                                        MULT: ${item.getCompoundingBadge()}
                                    </span>
                                @endif
                            </div>

                            <div class="data-cost" style="margin-top: auto; padding-top: 16px; border-top: 1px solid #f1f5f9; font-weight: 700; color: #0f172a; font-size: 1.1em; display: flex; justify-content: space-between; align-items: center;">
                                <span>Estimated Cost:</span>
                                <span>$${String.format("%,.0f", item.getAdjustedCost())}</span>
                            </div>
                        </div>
                    @endfor
                </div>
            @endif
        </section>

    <style>
        .data-item {
            display: flex;
            flex-direction: column;
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            transition: transform 0.2s;
        }
        .data-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        }

        @media (min-width: 768px) {
            .data-list {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
            }
        }
    </style>
    
        <!-- Recommended Repairs Section -->
        @if(!verdict.getPlan().getShouldDo().isEmpty())
            <section id="recommended-repairs" class="card">
                <h2 class="section-title text-success">
                    <span>💡</span> Recommended Improvements (Should-Do)
                </h2>
                <p class="explanation-text" style="margin-bottom: 24px;">
                    These improvements prevent future damage and increase property value.
                </p>
                <div class="data-list">
                    @for(var item : verdict.getPlan().getShouldDo())
                        <div class="data-item">
                            <h3>${item.getPrettyName()}</h3>
                            <div class="data-cost">
                                Est. $${String.format("%,.0f", item.getAdjustedCost())}
                            </div>
                        </div>
                    @endfor
                </div>
            </section>
        @endif
    
        <section id="cta" class="card" style="text-align: center; border-top: 4px solid var(--color-primary); background-color: #F8FAFC; margin-top: 40px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
            <h2 class="section-title" style="justify-content: center; margin-bottom: 16px; border: none; color: #1e293b;">
                Calculated your true cost yet?
            </h2>
            <p class="explanation-text" style="margin-bottom: 32px; font-size: 1.05em;">
                This chart shows market averages. Your home's condition could be <strong>50% higher or lower</strong>.
            </p>
            <a href="/home-repair" class="header-cta" style="font-size: 1.1em; padding: 16px 32px; background-color: var(--color-primary); color: white; border-radius: 6px; text-decoration: none; font-weight: 600; display: inline-block;">
                Start My Personal Estimate
            </a>
        </section>

        <!-- BOTTOM AD PLACEHOLDER (RECTANGLE / NATIVE) -->
        <div class="ad-placeholder-bottom" style="width: 100%; min-height: 250px; background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 12px; margin-top: 40px; display: flex; align-items: center; justify-content: center; color: #94a3b8; font-size: 12px; font-weight: 500; text-transform: uppercase;">
            <div style="text-align: center;">
                <p>&mdash; Recommended For You &mdash;</p>
                <div style="display: flex; gap: 20px; margin-top: 20px;">
                    <div style="width: 150px; height: 100px; background: #e2e8f0; border-radius: 4px;"></div>
                    <div style="width: 150px; height: 100px; background: #e2e8f0; border-radius: 4px;"></div>
                    <div style="width: 150px; height: 100px; background: #e2e8f0; border-radius: 4px;"></div>
                </div>
            </div>
        </div>
    
        <!-- FAQ Section (Visible Content matching FAQ Schema) -->
        @if(faqItems != null && !faqItems.isEmpty())
            <section class="card faq-section" style="margin-top: 60px;" itemscope itemtype="https://schema.org/FAQPage">
                <h2 class="section-title" style="border-bottom: 2px solid #e5e7eb; padding-bottom: 16px; margin-bottom: 32px;">
                    <span>❓</span> Frequently Asked Questions
                </h2>
                
                @for(var faq : faqItems)
                    <div class="faq-item" style="margin-bottom: 32px; padding-bottom: 32px; border-bottom: 1px solid #f1f5f9;" itemscope itemprop="mainEntity" itemtype="https://schema.org/Question">
                        <h3 style="font-size: 1.15em; color: #1e293b; margin: 0 0 12px 0; font-weight: 600;" itemprop="name">
                            ${faq.get("question")}
                        </h3>
                        <div style="color: #475569; line-height: 1.7; padding-left: 16px; border-left: 3px solid #cbd5e1;" itemscope itemprop="acceptedAnswer" itemtype="https://schema.org/Answer">
                            <p style="margin: 0;" itemprop="text">${faq.get("answer")}</p>
                        </div>
                    </div>
                @endfor
            </section>
        @endif
    
        <!-- Internal Navigation -->
        <section class="internal-links" style="margin-top: 60px;">
            <h3 style="margin-bottom: 16px;">🏘️ Explore Other Eras in ${metroName}</h3>
            <div style="display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 32px;">
            @for(var link : eraLinks)
                <a href="${link.getHref()}" style="padding: 6px 12px; background: white; border: 1px solid #E5E7EB; border-radius: 6px; font-size: 0.9em; color: var(--color-text-secondary);">${link.getText()}</a>
            @endfor
            </div>
    
            <h3 style="margin-bottom: 16px;">🌆 Compare with Nearby Cities (${eraName})</h3>
            <div style="display: flex; flex-wrap: wrap; gap: 12px;">
            @for(var link : cityLinks)
                <a href="${link.getHref()}" style="padding: 6px 12px; background: white; border: 1px solid #E5E7EB; border-radius: 6px; font-size: 0.9em; color: var(--color-text-secondary);">${link.getText()}</a>
            @endfor
            </div>
        </section>

        <!-- State Hub Links (Hub-and-Spoke SEO) -->
        @if(stateLinks != null && !stateLinks.isEmpty())
            <section class="internal-links" style="margin-top: 40px; padding-top: 40px; border-top: 2px solid #e5e7eb;">
                <h3 style="margin-bottom: 16px;">🏛️ Other Cities in the Same State (${eraName})</h3>
                <div style="display: flex; flex-wrap: wrap; gap: 12px;">
                @for(var link : stateLinks)
                    <a href="${link.getHref()}" style="padding: 6px 12px; background: white; border: 1px solid #E5E7EB; border-radius: 6px; font-size: 0.9em; color: var(--color-text-secondary);">${link.getText()}</a>
                @endfor
                </div>
            </section>
        @endif
    </div>

    @template.layout._footer()
</body>
</html>
