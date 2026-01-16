@import gg.jte.Content
@param Content content

<!DOCTYPE html>
<html lang="en" class="bg-gray-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Repair Verdict</title>
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

    @template.layout._header()

    <div class="app-container">
        <!-- AD_SLOT_TOP -->
        <div class="w-full mb-8 bg-gray-100 h-24 flex items-center justify-center text-gray-400 text-sm border-2 border-dashed border-gray-300 rounded-lg">
            AD_SLOT_TOP (Placeholder)
        </div>

        <main class="w-full bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
            ${content}
        </main>
    </div>

    @template.layout._footer()

</body>
</html>
