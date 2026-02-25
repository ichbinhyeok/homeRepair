@param java.util.List<String> hubs

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Critical Home Repair Risks</title>
    <meta name="description" content="Explore major home repair risks and inspection concerns." />
    <link rel="canonical" href="https://lifeverdict.com/home-repair/risks" />
    <link rel="stylesheet" href="/css/seo-report.css" />
</head>
<body>
    @template.layout._header()
    <main class="container" style="padding: 4rem 1rem;">
        <h1>Critical Home Risks (Knowledge Base)</h1>
        <p>Discover in-depth information on critical safety risks and home repair issues.</p>
        <div style="display: grid; gap: 1rem; margin-top: 2rem;">
            @for(String hub : hubs)
                <a href="/home-repair/risks/${hub}" class="btn-outline" style="text-align: left; max-width: 400px; padding: 1.5rem; font-weight: 600;">${hub.replace("-", " ").toUpperCase()} Guides &rarr;</a>
            @endfor
        </div>
        
        <div style="margin-top: 4rem;">
            <a href="/home-repair/verdicts/states" class="btn-outline">Browse By State</a>
        </div>
    </main>
    @template.layout._footer()
</body>
</html>
