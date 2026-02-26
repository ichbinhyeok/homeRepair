[CmdletBinding()]
param(
    [string]$ProjectRoot = "",
    [string]$OutputPath = "",
    [int]$MaxExamples = 30,
    [switch]$FailOnIssue
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    $ProjectRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path
} else {
    $ProjectRoot = (Resolve-Path $ProjectRoot).Path
}

if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot "logs/internal-link-audit.txt"
}

$staticRoot = Join-Path $ProjectRoot "src/main/resources/static"
$homeRepairRoot = Join-Path $staticRoot "home-repair"

if (-not (Test-Path $homeRepairRoot)) {
    throw "Static home-repair root not found: $homeRepairRoot"
}

$outputDir = Split-Path -Parent $OutputPath
if ($outputDir -and -not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

function Convert-StaticPathToUrl {
    param(
        [string]$FilePath,
        [string]$StaticRootPath
    )
    $relative = $FilePath.Substring($StaticRootPath.Length).TrimStart("\", "/")
    return "/" + ($relative -replace "\\", "/")
}

function Normalize-InternalPath {
    param([string]$PathValue)
    $normalized = $PathValue.Trim()
    $hashIndex = $normalized.IndexOf("#")
    if ($hashIndex -ge 0) {
        $normalized = $normalized.Substring(0, $hashIndex)
    }
    $queryIndex = $normalized.IndexOf("?")
    if ($queryIndex -ge 0) {
        $normalized = $normalized.Substring(0, $queryIndex)
    }
    if ([string]::IsNullOrWhiteSpace($normalized)) {
        return "/"
    }
    if ($normalized.Length -gt 1) {
        $normalized = $normalized.TrimEnd("/")
    }
    return $normalized
}

function Add-Edge {
    param(
        [hashtable]$Adjacency,
        [string]$Source,
        [string]$Target
    )

    if (-not $Adjacency.ContainsKey($Source)) {
        $Adjacency[$Source] = New-Object System.Collections.Generic.HashSet[string]
    }
    $null = $Adjacency[$Source].Add($Target)
}

$knownDynamicPatterns = @(
    "^/$",
    "^/home-repair$",
    "^/home-repair/(about|methodology|editorial-policy|disclaimer|data-sources)$",
    "^/privacy-policy$",
    "^/terms-of-service$",
    "^/disclaimer$",
    "^/home-repair/verdicts/states$",
    "^/home-repair/risks$",
    "^/home-repair/risks/[a-z0-9-]+$",
    "^/home-repair/step-2$",
    "^/home-repair/verdict$",
    "^/home-repair/result/[0-9a-fA-F-]+$",
    "^/home-repair/verdicts/[a-z0-9-]+/(pre-1950|1950-1970|1970-1980|1980-1995|1995-2010|2010-present)/[a-z0-9-]+$"
)

$allStaticFiles = Get-ChildItem -Path $staticRoot -Recurse -File
$allStaticPaths = New-Object System.Collections.Generic.HashSet[string]
foreach ($file in $allStaticFiles) {
    $url = Convert-StaticPathToUrl -FilePath $file.FullName -StaticRootPath $staticRoot
    $null = $allStaticPaths.Add($url)
}

$htmlFiles = Get-ChildItem -Path $homeRepairRoot -Recurse -File -Filter *.html
$staticUrlToFile = @{}
$allStaticUrls = New-Object System.Collections.Generic.List[string]

foreach ($file in $htmlFiles) {
    $url = Convert-StaticPathToUrl -FilePath $file.FullName -StaticRootPath $staticRoot
    $staticUrlToFile[$url] = $file.FullName
    $allStaticUrls.Add($url) | Out-Null
}

$adjacency = @{}
$brokenHtmlLinks = New-Object System.Collections.Generic.List[object]
$unknownInternalLinks = New-Object System.Collections.Generic.List[object]
$relativeLinks = New-Object System.Collections.Generic.List[object]
$internalLinkCount = 0

$hrefRegex = [regex]"href\s*=\s*['""](?<href>[^'""]+)['""]"

foreach ($file in $htmlFiles) {
    $sourceUrl = Convert-StaticPathToUrl -FilePath $file.FullName -StaticRootPath $staticRoot
    $html = Get-Content -Path $file.FullName -Raw
    $matches = $hrefRegex.Matches($html)

    foreach ($match in $matches) {
        $href = $match.Groups["href"].Value.Trim()
        if ([string]::IsNullOrWhiteSpace($href)) {
            continue
        }
        if ($href.StartsWith("#") -or
            $href.StartsWith("mailto:", [System.StringComparison]::OrdinalIgnoreCase) -or
            $href.StartsWith("tel:", [System.StringComparison]::OrdinalIgnoreCase) -or
            $href.StartsWith("javascript:", [System.StringComparison]::OrdinalIgnoreCase)) {
            continue
        }

        $targetPath = $null

        if ($href -match "^https?://") {
            try {
                $uri = [uri]$href
                if ($uri.Host -notin @("lifeverdict.com", "www.lifeverdict.com", "localhost", "127.0.0.1")) {
                    continue
                }
                $targetPath = $uri.AbsolutePath
            } catch {
                continue
            }
        } elseif ($href.StartsWith("/")) {
            $targetPath = $href
        } else {
            $relativeLinks.Add([pscustomobject]@{
                    Source = $sourceUrl
                    Target = $href
                }) | Out-Null
            continue
        }

        $targetPath = Normalize-InternalPath -PathValue $targetPath
        Add-Edge -Adjacency $adjacency -Source $sourceUrl -Target $targetPath
        $internalLinkCount++

        if ($allStaticPaths.Contains($targetPath)) {
            continue
        }

        if ($targetPath.EndsWith(".html", [System.StringComparison]::OrdinalIgnoreCase)) {
            $brokenHtmlLinks.Add([pscustomobject]@{
                    Source = $sourceUrl
                    Target = $targetPath
                }) | Out-Null
            continue
        }

        $isKnownDynamic = $false
        foreach ($pattern in $knownDynamicPatterns) {
            if ($targetPath -match $pattern) {
                $isKnownDynamic = $true
                break
            }
        }
        if (-not $isKnownDynamic) {
            $unknownInternalLinks.Add([pscustomobject]@{
                    Source = $sourceUrl
                    Target = $targetPath
                }) | Out-Null
        }
    }
}

# Synthetic seed links so crawl-depth/orphan checks model the dynamic state hub entry point.
Add-Edge -Adjacency $adjacency -Source "/home-repair" -Target "/home-repair/verdicts/states"
$stateStaticPages = @($allStaticUrls | Where-Object { $_ -match "^/home-repair/verdicts/states/[a-z]{2}\.html$" })
foreach ($stateUrl in $stateStaticPages) {
    Add-Edge -Adjacency $adjacency -Source "/home-repair/verdicts/states" -Target $stateUrl
}

$inbound = @{}
foreach ($url in $allStaticUrls) {
    $inbound[$url] = 0
}
foreach ($source in $adjacency.Keys) {
    foreach ($target in $adjacency[$source]) {
        if ($inbound.ContainsKey($target) -and $source -ne $target) {
            $inbound[$target]++
        }
    }
}

$staticVerdictUrls = @($allStaticUrls | Where-Object { $_ -like "/home-repair/verdicts/*" })
$orphans = @($staticVerdictUrls | Where-Object { $inbound[$_] -eq 0 })

$seeds = @("/home-repair", "/home-repair/verdicts/states", "/home-repair/risks")
$depth = @{}
$queue = New-Object System.Collections.Generic.Queue[string]
foreach ($seed in $seeds) {
    if (-not $depth.ContainsKey($seed)) {
        $depth[$seed] = 0
        $queue.Enqueue($seed)
    }
}

while ($queue.Count -gt 0) {
    $node = $queue.Dequeue()
    if (-not $adjacency.ContainsKey($node)) {
        continue
    }
    foreach ($neighbor in $adjacency[$node]) {
        if (-not $depth.ContainsKey($neighbor)) {
            $depth[$neighbor] = $depth[$node] + 1
            $queue.Enqueue($neighbor)
        }
    }
}

$unreachable = @($staticVerdictUrls | Where-Object { -not $depth.ContainsKey($_) })
$reachableStaticDepths = @($staticVerdictUrls |
    Where-Object { $depth.ContainsKey($_) } |
    ForEach-Object { $depth[$_] })

$maxDepth = 0
if ($reachableStaticDepths.Count -gt 0) {
    $maxDepth = ($reachableStaticDepths | Measure-Object -Maximum).Maximum
}

$depthDistribution = @($staticVerdictUrls |
    Where-Object { $depth.ContainsKey($_) } |
    Group-Object { $depth[$_] } |
    Sort-Object { [int]$_.Name })

$report = New-Object System.Collections.Generic.List[string]
$report.Add("Internal Link and Crawl Audit") | Out-Null
$report.Add("Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")") | Out-Null
$report.Add("Project Root: $ProjectRoot") | Out-Null
$report.Add("Static pages scanned: $($allStaticUrls.Count)") | Out-Null
$report.Add("Internal links parsed: $internalLinkCount") | Out-Null
$report.Add("Broken .html links: $($brokenHtmlLinks.Count)") | Out-Null
$report.Add("Unknown internal links: $($unknownInternalLinks.Count)") | Out-Null
$report.Add("Relative links (not analyzed): $($relativeLinks.Count)") | Out-Null
$report.Add("Verdict/static pages with zero inbound links: $($orphans.Count)") | Out-Null
$report.Add("Verdict/static pages unreachable from seeds: $($unreachable.Count)") | Out-Null
$report.Add("Max click depth (reachable verdict/static pages): $maxDepth") | Out-Null

if ($depthDistribution.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Depth distribution (verdict/static pages):") | Out-Null
    foreach ($bucket in $depthDistribution) {
        $report.Add("  - depth $($bucket.Name): $($bucket.Count)") | Out-Null
    }
}

if ($brokenHtmlLinks.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Broken .html link examples:") | Out-Null
    foreach ($item in ($brokenHtmlLinks | Select-Object -First $MaxExamples)) {
        $report.Add("  - $($item.Source) -> $($item.Target)") | Out-Null
    }
    if ($brokenHtmlLinks.Count -gt $MaxExamples) {
        $report.Add("  - ... $($brokenHtmlLinks.Count - $MaxExamples) more omitted") | Out-Null
    }
}

if ($unknownInternalLinks.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Unknown internal link examples:") | Out-Null
    foreach ($item in ($unknownInternalLinks | Select-Object -First $MaxExamples)) {
        $report.Add("  - $($item.Source) -> $($item.Target)") | Out-Null
    }
    if ($unknownInternalLinks.Count -gt $MaxExamples) {
        $report.Add("  - ... $($unknownInternalLinks.Count - $MaxExamples) more omitted") | Out-Null
    }
}

if ($orphans.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Orphan verdict/static pages:") | Out-Null
    foreach ($url in ($orphans | Select-Object -First $MaxExamples)) {
        $report.Add("  - $url") | Out-Null
    }
    if ($orphans.Count -gt $MaxExamples) {
        $report.Add("  - ... $($orphans.Count - $MaxExamples) more omitted") | Out-Null
    }
}

if ($unreachable.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Unreachable verdict/static pages from crawl seeds:") | Out-Null
    foreach ($url in ($unreachable | Select-Object -First $MaxExamples)) {
        $report.Add("  - $url") | Out-Null
    }
    if ($unreachable.Count -gt $MaxExamples) {
        $report.Add("  - ... $($unreachable.Count - $MaxExamples) more omitted") | Out-Null
    }
}

$report | Set-Content -Path $OutputPath -Encoding UTF8
$report | ForEach-Object { Write-Host $_ }
Write-Host ""
Write-Host "Report written to $OutputPath"

if ($FailOnIssue -and ($brokenHtmlLinks.Count -gt 0 -or $orphans.Count -gt 0 -or $unreachable.Count -gt 0)) {
    throw "Internal link audit failed (broken/orphan/unreachable issues detected)."
}
