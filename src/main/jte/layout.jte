@import gg.jte.Content
@param Content content

<!DOCTYPE html>
<html lang="en" class="bg-gray-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Repair Verdict</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        /* Custom pure UX overrides */
        body { font-family: 'Inter', sans-serif; }
    </style>
</head>
<body class="antialiased min-h-screen flex flex-col items-center justify-center p-4">

    <!-- AD_SLOT_TOP -->
    <div class="w-full max-w-4xl mx-auto mb-4 bg-gray-200 h-24 flex items-center justify-center text-gray-400 text-sm border-2 border-dashed border-gray-300">
        AD_SLOT_TOP (Placeholder)
    </div>

    <main class="w-full max-w-md bg-white rounded-xl shadow-2xl overflow-hidden border border-gray-100">
        ${content}
    </main>

    <footer class="mt-8 text-center text-gray-400 text-xs">
        <p>&copy; 2025 LivingCostCheck.com</p>
    </footer>

</body>
</html>
