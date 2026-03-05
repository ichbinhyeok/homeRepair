@param java.util.List<String> states
@param String baseUrl

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
    <title>Home Repair Cost Reports by State (2026)</title>
    <meta name="description" content="Open state-level home repair cost hubs, compare city and era reports, and plan inspection budgets before you buy." />
    <link rel="canonical" href="${baseUrl}/home-repair/verdicts/states" />
    <link rel="stylesheet" href="/css/seo-report.css" />
</head>
<body>
    @template.layout._header()
    <main class="container" style="padding: 4rem 1rem;">
        <h1>Browse Home Repair Cost Hubs by State</h1>
        <p>Select your state, then open your city and home era report to estimate likely repair liability before inspection.</p>
        <p style="color: var(--slate-600); max-width: 760px; line-height: 1.7;">
            These reports are benchmark guides. Confirm final pricing with licensed inspector findings and local contractor bids.
        </p>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); gap: 1rem; margin-top: 2rem;">
            @for(String state : states)
                <a href="/home-repair/verdicts/states/${state.toLowerCase()}.html" class="btn-outline" style="text-align: center;">${state}</a>
            @endfor
        </div>
    </main>
    @template.layout._footer()
</body>
</html>
