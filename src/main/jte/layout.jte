@import gg.jte.Content
@param Content content
@param String title = "LifeVerdict | Property Intelligence"
@param Content headContent = null
@param boolean showHeader = true

<!DOCTYPE html>
<html lang="en" class="bg-gray-100">
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
    @if(headContent != null)
        ${headContent}
    @endif
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/styles.css">
    <!-- Tailwind for utility classes within the app -->
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        /* Only keep critical overrides, move rest to styles.css */
        body { font-family: 'Inter', sans-serif; }
        /* Ensure main content doesn't hit header/footer directly */
        .app-container {
            width: 100%;
            max-width: 800px;
            margin: 40px auto;
            padding: 0 20px;
            flex: 1; /* Pushes footer down */
        }
    </style>
</head>
<body class="antialiased min-h-screen">

    @if(showHeader)
        @template.layout._header()
    @endif

    <div class="w-full min-h-screen pt-4 pb-12">
        <!-- AD_SLOT_TOP (Hidden for now to keep design clean) -->
        
        <main class="w-full">
            ${content}
        </main>
    </div>

    @template.layout._footer()

</body>
</html>
