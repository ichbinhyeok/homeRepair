@import gg.jte.Content
@param Content content
@param String title = "LifeVerdict | Inspection Response Copilot"
@param String description = null
@param Content headContent = null
@param boolean showHeader = true

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

    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,600;9..144,700&family=IBM+Plex+Mono:wght@500;600&family=Instrument+Sans:wght@400;500;600;700&display=swap" rel="stylesheet" />

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    @if(description != null)
        <meta name="description" content="${description}">
        <meta property="og:description" content="${description}">
        <meta name="twitter:description" content="${description}">
    @endif
    <meta property="og:title" content="${title}">
    <meta property="og:type" content="website">
    <meta name="twitter:card" content="summary">
    <meta name="twitter:title" content="${title}">
    <meta name="theme-color" content="#174a5a">
    <title>${title}</title>
    @if(headContent != null)
        ${headContent}
    @endif
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="antialiased">

    @if(showHeader)
        @template.layout._header()
    @endif

    <div class="site-stage w-full min-h-screen pt-4 pb-12">
        <!-- AD_SLOT_TOP (Hidden for now to keep design clean) -->
        
        <main class="w-full">
            ${content}
        </main>
    </div>

    @template.layout._footer()

</body>
</html>
