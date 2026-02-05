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
    <link rel="alternate" hreflang="en-US" href="${canonicalUrl}" />
    <link rel="icon" href="/favicon.ico">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    <!-- Tailwind CSS (via CDN for static pages or compiled) -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: {
                        sans: ['Inter', 'sans-serif'],
                    },
                }
            }
        }
    </script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap" rel="stylesheet">
    
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

    <!-- Phase 3: Breadcrumb Schema -->
    <script type="application/ld+json">
    {
      "@context": "https://schema.org",
      "@type": "BreadcrumbList",
      "itemListElement": [
        {"@type": "ListItem", "position": 1, "name": "Home", "item": "${baseUrl}/"},
        {"@type": "ListItem", "position": 2, "name": "Home Repair", "item": "${baseUrl}/home-repair"},
        {"@type": "ListItem", "position": 3, "name": "${metroName}", "item": "${baseUrl}/home-repair/states/${metroCode.split("_")[metroCode.split("_").length-1].toLowerCase()}"},
        {"@type": "ListItem", "position": 4, "name": "${eraName}"}
      ]
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
</head>
<body class="bg-slate-50 font-sans text-slate-900 antialiased selection:bg-blue-100">

    <!-- Navigation -->
    <nav class="border-b border-slate-200 bg-white sticky top-0 z-50 shadow-sm backdrop-blur-md bg-white/90">
        <div class="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
            <a href="/home-repair" class="font-bold text-xl tracking-tight text-slate-900 group">
                LifeVerdict <span class="text-slate-400 font-normal group-hover:text-blue-500 transition-colors">| Home Context</span>
            </a>
            <a href="/home-repair" class="text-sm font-semibold text-white bg-blue-600 px-4 py-2 rounded-lg hover:bg-blue-700 transition shadow-sm">
                Open Calculator &rarr;
            </a>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="max-w-4xl mx-auto px-4 py-12">
        
        <!-- Headers -->
        <div class="mb-10 text-center">
            <div class="inline-flex items-center gap-2 px-3 py-1 bg-blue-50 text-blue-700 text-xs font-bold uppercase tracking-widest rounded-full mb-6 border border-blue-100">
                Pre-Purchase Cost Analysis
            </div>
            <h1 class="text-3xl md:text-5xl font-extrabold text-slate-900 mb-6 tracking-tight leading-tight">
                Buying a <span class="text-blue-600">${eraName}</span> Home<br class="hidden md:block"> in ${metroName}?
            </h1>
            <p class="text-lg md:text-xl text-slate-600 max-w-2xl mx-auto leading-relaxed">
                Before you sign, check the hidden costs. Based on ${metroName}'s climate risks and ${eraName} building codes, here is your negotiation leverage.
            </p>
        </div>

        <!-- Bento Grid Summary -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-16">
            
            <!-- Cost Card -->
            <div class="bg-slate-900 rounded-2xl p-8 text-white relative overflow-hidden shadow-2xl group">
                <div class="relative z-10 flex flex-col h-full justify-between">
                    <div>
                        <div class="text-slate-400 text-xs font-bold uppercase tracking-widest mb-2 flex items-center gap-2">
                            <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span> Estimated Hidden Liability
                        </div>
                        <div class="text-4xl md:text-5xl font-bold tracking-tighter mb-4">${verdict.getCostRangeLabel()}</div>
                        <p class="text-sm text-slate-400 border-l-2 border-slate-700 pl-3">
                            Potential immediate repair costs for ${eraName} homes in this zip code area.
                        </p>
                    </div>
                </div>
                <!-- Abstract Background -->
                <div class="absolute right-0 bottom-0 w-48 h-48 bg-blue-600 rounded-full blur-[100px] opacity-20 group-hover:opacity-30 transition-opacity duration-500"></div>
                <div class="absolute -top-10 -left-10 w-32 h-32 bg-purple-500 rounded-full blur-[80px] opacity-10"></div>
            </div>

            <!-- Negotiation Card -->
            <div class="bg-white rounded-2xl p-8 border border-slate-200 shadow-lg relative overflow-hidden flex flex-col justify-between">
                <div class="relative z-10">
                    <div class="text-blue-600 text-xs font-bold uppercase tracking-widest mb-2">Negotiation Strategy</div>
                    <h3 class="text-2xl font-bold text-slate-900 mb-2">Request a Repair Allowance</h3>
                    <p class="text-slate-600 text-sm mb-6 leading-relaxed">
                        Don't pay full price for someone else's deferred maintenance. Use this data to negotiate a price reduction.
                    </p>
                </div>
                <a href="/home-repair?metroCode=${metroCode}&era=${era}" class="inline-flex items-center justify-center w-full py-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl transition-all shadow-md hover:shadow-xl hover:-translate-y-0.5 group">
                    Calculate Your Exact Leverage
                    <svg class="ml-2 w-4 h-4 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"/></svg>
                </a>
            </div>
        </div>

        <!-- Content Body -->
        <div class="prose prose-slate prose-lg max-w-none mb-16">
            
            <div class="bg-white p-8 rounded-2xl shadow-sm border border-slate-100 mb-12">
                <h3 class="flex items-center gap-2 text-slate-900 mt-0">
                    <span>🚨</span> Top Risk Factors for ${eraName}
                </h3>
                <p>
                    In ${metroName}, homes built during the ${eraName} era face unique challenges. 
                    ${regionalInsight}
                </p>
                <div class="bg-amber-50 border-l-4 border-amber-500 p-4 rounded-r-lg my-6 not-prose">
                    <p class="text-amber-900 text-sm font-semibold m-0 flex items-start gap-2">
                        <svg class="w-5 h-5 shrink-0 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
                        <span>${eraFragment}</span>
                    </p>
                </div>

                @if(!verdict.getPlan().getMustDo().isEmpty())
                    <h4 class="text-sm font-bold text-slate-500 uppercase tracking-widest mb-4 mt-8">Common Deal Killers</h4>
                    <div class="space-y-4 not-prose">
                        @for(var item : verdict.getPlan().getMustDo())
                            <div class="flex items-start justify-between p-4 bg-slate-50 rounded-lg border border-slate-100 hover:border-slate-200 transition-colors">
                                <div>
                                    <h5 class="font-bold text-slate-900 text-base mb-1">${item.getPrettyName()}</h5>
                                    <p class="text-sm text-slate-600 leading-snug">${item.getExplanation()}</p>
                                </div>
                                <div class="text-right pl-4">
                                    <span class="block font-bold text-slate-900 text-sm">$${String.format("%,.0f", item.getAdjustedCost())}</span>
                                    @if(item.isMandatory())
                                        <span class="inline-block px-2 py-0.5 bg-red-100 text-red-700 text-[10px] font-bold rounded uppercase mt-1">Required</span>
                                    @endif
                                </div>
                            </div>
                        @endfor
                    </div>
                @endif
            </div>

            <h3>🏗️ Local Market Context (${metroName})</h3>
            <p>${climateFragment}</p>
            <p>${costFragment}</p>

            <!-- FAQ Section -->
            <h3 class="mt-12 mb-6">Frequently Asked Questions</h3>
            <div class="not-prose grid gap-4">
                @for(var faq : faqItems)
                    <div class="border border-slate-200 rounded-xl p-5 bg-white hover:border-blue-200 transition-colors">
                        <h4 class="font-bold text-slate-900 mb-2 flex items-start gap-2">
                            <span class="text-blue-500">Q.</span> ${faq.get("question")}
                        </h4>
                        <p class="text-slate-600 text-sm pl-6 border-l-2 border-slate-100">${faq.get("answer")}</p>
                    </div>
                @endfor
            </div>

        </div>

        <!-- Internal Linking -->
        <div class="border-t border-slate-200 pt-12">
            <h3 class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-6">Compare with other eras in ${metroName}</h3>
            <div class="grid grid-cols-2 md:grid-cols-3 gap-3 mb-10">
                @for(InternalLink link : eraLinks)
                     <a href="${link.getHref()}" class="text-sm text-slate-600 bg-white border border-slate-200 px-3 py-2 rounded-lg hover:border-blue-300 hover:text-blue-600 transition-colors text-center">
                        ${link.getText()}
                    </a>
                @endfor
            </div>

            <h3 class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-6">Other Cities in This State</h3>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                @for(InternalLink link : stateLinks)
                    <a href="${link.getHref()}" class="text-sm text-slate-500 hover:text-blue-600 hover:underline">
                        ${link.getText()}
                    </a>
                @endfor
            </div>
        </div>
        
        <!-- Sticky Mobile CTA -->
        <div class="fixed bottom-0 left-0 right-0 p-4 bg-white border-t border-slate-200 md:hidden shadow-[0_-4px_20px_rgba(0,0,0,0.1)] z-50">
            <a href="/home-repair?metroCode=${metroCode}&era=${era}" class="block w-full py-3 bg-blue-600 text-white text-center font-bold rounded-xl active:bg-blue-700 shadow-lg">
                Calculate Hidden Costs
            </a>
        </div>

    </main>
    
    <!-- Footer -->
    <footer class="bg-slate-900 text-slate-400 py-12 mt-12 mb-20 md:mb-0">
        <div class="max-w-4xl mx-auto px-4 text-center">
            <a href="/" class="text-xl font-bold text-white mb-4 block">LifeVerdict</a>
            <p class="text-sm mb-6 max-w-md mx-auto">Helping homebuyers avoid money pits with data-driven repair cost analysis.</p>
            <div class="flex justify-center gap-6 text-sm">
                <a href="/privacy-policy" class="hover:text-white transition-colors">Privacy</a>
                <a href="/terms-of-service" class="hover:text-white transition-colors">Terms</a>
                <a href="/data-sources" class="hover:text-white transition-colors">Data Sources</a>
                <a href="/methodology" class="hover:text-white transition-colors">Methodology</a>
            </div>
            <p class="text-xs text-slate-600 mt-8">&copy; 2026 LifeVerdict. All rights reserved.</p>
        </div>
    </footer>
</body>
</html>
