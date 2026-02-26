[CmdletBinding()]
param(
    [string]$ProjectRoot = "",
    [string]$OutputPath = "",
    [int]$MaxExamples = 25,
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
    $OutputPath = Join-Path $ProjectRoot "logs/seo-content-audit.txt"
}

$verdictRoot = Join-Path $ProjectRoot "src/main/resources/static/home-repair/verdicts"
if (-not (Test-Path $verdictRoot)) {
    throw "Verdict static directory not found: $verdictRoot"
}

$outputDir = Split-Path -Parent $OutputPath
if ($outputDir -and -not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

function Get-FirstMatch {
    param(
        [string]$Text,
        [string]$Pattern
    )
    $options = [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
    $match = [regex]::Match($Text, $Pattern, $options)
    if ($match.Success) {
        return $match.Groups[1].Value.Trim()
    }
    return $null
}

function Normalize-Token {
    param([string]$Value)
    if ([string]::IsNullOrWhiteSpace($Value)) {
        return ""
    }
    return (($Value.ToLowerInvariant()) -replace "[^a-z0-9]", "")
}

$l1Pattern = "^[^/\\]+[/\\](pre-1950|1950-1970|1970-1980|1980-1995|1995-2010|2010-present)\.html$"
$issues = New-Object System.Collections.Generic.List[object]
$pages = New-Object System.Collections.Generic.List[object]

$l1Files = Get-ChildItem -Path $verdictRoot -Recurse -File -Filter *.html | Where-Object {
    $relativePath = $_.FullName.Substring($verdictRoot.Length).TrimStart("\", "/")
    $relativePath -match $l1Pattern
}

foreach ($file in $l1Files) {
    $relativePath = $file.FullName.Substring($verdictRoot.Length).TrimStart("\", "/")
    $relativeUrl = "/home-repair/verdicts/" + ($relativePath -replace "\\", "/")
    $expectedCanonical = "https://lifeverdict.com$relativeUrl"
    $html = Get-Content -Path $file.FullName -Raw

    $title = Get-FirstMatch -Text $html -Pattern "<title>(.*?)</title>"
    $description = Get-FirstMatch -Text $html -Pattern "<meta\s+name=['""]description['""]\s+content=['""]([^'""]+)['""]"
    if (-not $description) {
        $description = Get-FirstMatch -Text $html -Pattern "<meta\s+content=['""]([^'""]+)['""]\s+name=['""]description['""]"
    }
    $canonical = Get-FirstMatch -Text $html -Pattern "<link\s+rel=['""]canonical['""]\s+href=['""]([^'""]+)['""]"
    if (-not $canonical) {
        $canonical = Get-FirstMatch -Text $html -Pattern "<link\s+href=['""]([^'""]+)['""]\s+rel=['""]canonical['""]"
    }

    $faqCity = Get-FirstMatch -Text $html -Pattern "How much should I budget for repairs on a [^?]+ home in ([^?]+)\?"
    $marketCity = Get-FirstMatch -Text $html -Pattern "Local Market Factors:\s*([^<]+)</h3>"

    if ([string]::IsNullOrWhiteSpace($title)) {
        $issues.Add([pscustomobject]@{
                Type    = "MissingTitle"
                Url     = $relativeUrl
                Details = "No <title> tag found."
            }) | Out-Null
    }

    if ([string]::IsNullOrWhiteSpace($description)) {
        $issues.Add([pscustomobject]@{
                Type    = "MissingDescription"
                Url     = $relativeUrl
                Details = "No meta description found."
            }) | Out-Null
    }

    if ([string]::IsNullOrWhiteSpace($canonical)) {
        $issues.Add([pscustomobject]@{
                Type    = "MissingCanonical"
                Url     = $relativeUrl
                Details = "No canonical link found."
            }) | Out-Null
    } elseif ($canonical -ne $expectedCanonical) {
        $issues.Add([pscustomobject]@{
                Type    = "CanonicalMismatch"
                Url     = $relativeUrl
                Details = "Expected '$expectedCanonical' but found '$canonical'."
            }) | Out-Null
    }

    if ($faqCity -and $marketCity) {
        $faqCityCore = ($faqCity -split ",")[0].Trim()
        $marketCityCore = ($marketCity -split ",")[0].Trim()
        if ((Normalize-Token $faqCityCore) -ne (Normalize-Token $marketCityCore)) {
            $issues.Add([pscustomobject]@{
                    Type    = "FaqCityMismatch"
                    Url     = $relativeUrl
                    Details = "FAQ city '$faqCity' does not match market section city '$marketCity'."
                }) | Out-Null
        }
    }

    $pages.Add([pscustomobject]@{
            Url         = $relativeUrl
            Title       = $title
            Description = $description
            Canonical   = $canonical
        }) | Out-Null
}

$duplicateTitles = $pages | Where-Object { -not [string]::IsNullOrWhiteSpace($_.Title) } |
    Group-Object -Property Title |
    Where-Object { $_.Count -gt 1 }

foreach ($group in $duplicateTitles) {
    $sampleUrls = ($group.Group | Select-Object -First 5 | ForEach-Object { $_.Url }) -join ", "
    $issues.Add([pscustomobject]@{
            Type    = "DuplicateTitle"
            Url     = "<multiple>"
            Details = "'$($group.Name)' appears $($group.Count) times. Examples: $sampleUrls"
        }) | Out-Null
}

$duplicateDescriptions = $pages | Where-Object { -not [string]::IsNullOrWhiteSpace($_.Description) } |
    Group-Object -Property Description |
    Where-Object { $_.Count -gt 1 }

foreach ($group in $duplicateDescriptions) {
    $sampleUrls = ($group.Group | Select-Object -First 5 | ForEach-Object { $_.Url }) -join ", "
    $issues.Add([pscustomobject]@{
            Type    = "DuplicateDescription"
            Url     = "<multiple>"
            Details = "'$($group.Name)' appears $($group.Count) times. Examples: $sampleUrls"
        }) | Out-Null
}

$report = New-Object System.Collections.Generic.List[string]
$report.Add("SEO Content Audit") | Out-Null
$report.Add("Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")") | Out-Null
$report.Add("Project Root: $ProjectRoot") | Out-Null
$report.Add("Pages Scanned (L1 verdict pages): $($pages.Count)") | Out-Null
$report.Add("Issues Found: $($issues.Count)") | Out-Null

$issueCounts = @($issues | Group-Object -Property Type | Sort-Object Count -Descending)
if ($issueCounts.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Issue Summary:") | Out-Null
    foreach ($group in $issueCounts) {
        $report.Add("  - $($group.Name): $($group.Count)") | Out-Null
    }
}

if ($issues.Count -gt 0) {
    $report.Add("") | Out-Null
    $report.Add("Issue Examples:") | Out-Null
    foreach ($issue in ($issues | Select-Object -First $MaxExamples)) {
        $report.Add("  - [$($issue.Type)] $($issue.Url) :: $($issue.Details)") | Out-Null
    }
    if ($issues.Count -gt $MaxExamples) {
        $report.Add("  - ... $($issues.Count - $MaxExamples) more issues omitted") | Out-Null
    }
}

$report | Set-Content -Path $OutputPath -Encoding UTF8
$report | ForEach-Object { Write-Host $_ }
Write-Host ""
Write-Host "Report written to $OutputPath"

if ($FailOnIssue -and $issues.Count -gt 0) {
    throw "SEO content audit found $($issues.Count) issues."
}
