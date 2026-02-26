[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$OutputPath = "",
    [switch]$FailOnIssue
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

try {
    Add-Type -AssemblyName System.Net.Http -ErrorAction Stop | Out-Null
} catch {
    throw "Failed to load System.Net.Http assembly: $($_.Exception.Message)"
}

if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..")).Path "logs/seo-smoke-test.txt"
}

$outputDir = Split-Path -Parent $OutputPath
if ($outputDir -and -not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

$baseHost = $BaseUrl.TrimEnd("/")

$handler = [System.Net.Http.HttpClientHandler]::new()
$handler.AllowAutoRedirect = $false
$client = [System.Net.Http.HttpClient]::new($handler)
$client.Timeout = [TimeSpan]::FromSeconds(15)

function Invoke-HttpGet {
    param(
        [System.Net.Http.HttpClient]$HttpClient,
        [string]$BaseHostValue,
        [string]$Path
    )

    $url = "$BaseHostValue$Path"
    try {
        $response = $HttpClient.GetAsync($url).GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        $headers = @{}
        foreach ($h in $response.Headers) {
            $headers[$h.Key] = ($h.Value -join ", ")
        }
        foreach ($h in $response.Content.Headers) {
            $headers[$h.Key] = ($h.Value -join ", ")
        }
        return [pscustomobject]@{
            Url        = $url
            Path       = $Path
            StatusCode = [int]$response.StatusCode
            Headers    = $headers
            Body       = $body
            Error      = $null
        }
    } catch {
        return [pscustomobject]@{
            Url        = $url
            Path       = $Path
            StatusCode = 0
            Headers    = @{}
            Body       = ""
            Error      = $_.Exception.Message
        }
    }
}

function Add-TestResult {
    param(
        [System.Collections.Generic.List[object]]$Results,
        [string]$Name,
        [bool]$Passed,
        [string]$Details
    )
    $Results.Add([pscustomobject]@{
            Test    = $Name
            Passed  = $Passed
            Details = $Details
        }) | Out-Null
}

$results = New-Object System.Collections.Generic.List[object]

$health = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/home-repair"
if ($health.StatusCode -eq 0) {
    $msg = "Cannot reach $baseHost. Ensure the app is running before smoke tests. Error: $($health.Error)"
    Add-TestResult -Results $results -Name "Host Reachable" -Passed $false -Details $msg
    $results | ForEach-Object {
        Write-Host ("[{0}] {1} - {2}" -f ($(if ($_.Passed) { "PASS" } else { "FAIL" }), $_.Test, $_.Details))
    }
    $results | Set-Content -Path $OutputPath -Encoding UTF8
    throw $msg
}
Add-TestResult -Results $results -Name "Host Reachable" -Passed $true -Details "$baseHost responded with $($health.StatusCode)"

$l2Html = "/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central.html"
$l2TripleHtml = "/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central.html.html.html"
$l2Canonical = "/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central"

$respL2Html = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path $l2Html
$l2HtmlRedirectOk = @("301", "308") -contains "$($respL2Html.StatusCode)"
Add-TestResult -Results $results -Name "L2 .html redirects" -Passed $l2HtmlRedirectOk -Details "status=$($respL2Html.StatusCode)"

$location1 = $null
if ($respL2Html.Headers.ContainsKey("Location")) {
    $location1 = $respL2Html.Headers["Location"]
}
$redirectTargetOk = $false
if ($location1) {
    $redirectTargetOk = ($location1 -like "*/home-repair/verdicts/atlanta-sandy-springs-ga/1980-1995/hvac-heat-pump-central")
}
Add-TestResult -Results $results -Name "L2 redirect target canonical" -Passed $redirectTargetOk -Details "location=$location1"

$respL2Triple = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path $l2TripleHtml
$l2TripleRedirectOk = @("301", "308") -contains "$($respL2Triple.StatusCode)"
Add-TestResult -Results $results -Name "L2 .html.html.html redirects" -Passed $l2TripleRedirectOk -Details "status=$($respL2Triple.StatusCode)"

$respL2Canonical = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path $l2Canonical
$canonicalStatusOk = ($respL2Canonical.StatusCode -eq 200)
Add-TestResult -Results $results -Name "L2 canonical route returns 200" -Passed $canonicalStatusOk -Details "status=$($respL2Canonical.StatusCode)"

$canonicalTagOk = $respL2Canonical.Body -match "<link\s+rel=['""]canonical['""]"
Add-TestResult -Results $results -Name "L2 page contains canonical tag" -Passed $canonicalTagOk -Details "canonical tag detected=$canonicalTagOk"

$statesIndex = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/home-repair/verdicts/states"
Add-TestResult -Results $results -Name "States hub returns 200" -Passed ($statesIndex.StatusCode -eq 200) -Details "status=$($statesIndex.StatusCode)"

$statePage = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/home-repair/verdicts/states/tx.html"
Add-TestResult -Results $results -Name "State static page returns 200" -Passed ($statePage.StatusCode -eq 200) -Details "status=$($statePage.StatusCode)"

$riskIndex = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/home-repair/risks"
Add-TestResult -Results $results -Name "Risk index returns 200" -Passed ($riskIndex.StatusCode -eq 200) -Details "status=$($riskIndex.StatusCode)"

$riskHub = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/home-repair/risks/knob-and-tube-wiring"
Add-TestResult -Results $results -Name "Risk hub returns 200" -Passed ($riskHub.StatusCode -eq 200) -Details "status=$($riskHub.StatusCode)"

$riskMissing = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/home-repair/risks/not-a-real-risk"
Add-TestResult -Results $results -Name "Invalid risk slug returns 404" -Passed ($riskMissing.StatusCode -eq 404) -Details "status=$($riskMissing.StatusCode)"

$sitemap = Invoke-HttpGet -HttpClient $client -BaseHostValue $baseHost -Path "/sitemap.xml"
$sitemapStatusOk = ($sitemap.StatusCode -eq 200)
Add-TestResult -Results $results -Name "Sitemap returns 200" -Passed $sitemapStatusOk -Details "status=$($sitemap.StatusCode)"

$sitemapHasRiskIndex = $false
if ($sitemapStatusOk) {
    $sitemapHasRiskIndex = $sitemap.Body -like "*<loc>https://lifeverdict.com/home-repair/risks</loc>*"
}
Add-TestResult -Results $results -Name "Sitemap includes /home-repair/risks" -Passed $sitemapHasRiskIndex -Details "found=$sitemapHasRiskIndex"

$report = New-Object System.Collections.Generic.List[string]
$report.Add("SEO Smoke Test") | Out-Null
$report.Add("Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")") | Out-Null
$report.Add("Host: $baseHost") | Out-Null
$report.Add("") | Out-Null

$failedCount = 0
foreach ($result in $results) {
    $statusLabel = if ($result.Passed) { "PASS" } else { "FAIL" }
    if (-not $result.Passed) {
        $failedCount++
    }
    $report.Add("[$statusLabel] $($result.Test) :: $($result.Details)") | Out-Null
}

$report.Add("") | Out-Null
$report.Add("Total tests: $($results.Count)") | Out-Null
$report.Add("Failed tests: $failedCount") | Out-Null

$report | Set-Content -Path $OutputPath -Encoding UTF8
$report | ForEach-Object { Write-Host $_ }
Write-Host ""
Write-Host "Report written to $OutputPath"

if ($FailOnIssue -and $failedCount -gt 0) {
    throw "SEO smoke test failed: $failedCount test(s) failed."
}
