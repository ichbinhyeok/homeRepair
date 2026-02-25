$ErrorActionPreference = 'SilentlyContinue'
Write-Host "=========================================="
Write-Host " BULK TESTING: P0 & P1 CTA REFACORTING "
Write-Host "=========================================="
Write-Host ""

Write-Host "[1. Static Page Bulk Check]"
$staticFiles = Get-ChildItem -Path "src\main\resources\static\home-repair\verdicts" -Recurse -Filter "*.html" | Select-Object -First 20
$passCount = 0
$failCount = 0
foreach ($file in $staticFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match 'Customize Your Estimate' -and $content -notmatch 'Check Specific Address') {
        $passCount++
    } else {
        $failCount++
    }
}
Write-Host " Checked 20 random static files."
Write-Host "   - Passes (New CTA found): $passCount"
Write-Host "   - Fails (Old CTA found): $failCount"
Write-Host ""

Write-Host "[2. SEO L2 Pages .html Extension Redirection Test]"
$redirectTest = Invoke-WebRequest -Uri "http://localhost:8080/home-repair/verdicts/abilene-tx/1980-1995/hvac-heat-pump-central.html" -MaximumRedirection 0 -UseBasicParsing
if ($redirectTest.StatusCode -eq 301 -or $redirectTest.StatusCode -eq 308) {
    Write-Host " Redirection Test: PASS (301 Redirect found for .html suffix)"
    Write-Host "   -> Redirects to: $($redirectTest.Headers.Location)"
} else {
    Write-Host " Redirection Test: FAIL"
}
Write-Host ""

Write-Host "[3. /track Affiliate Whitelist Redirection Test]"
# Generate a fake UUID just to bypass any UUID parsing error (the DB will just not find it, but isValidTarget validates string first)
$trackTest = Invoke-WebRequest -Uri "http://localhost:8080/home-repair/track?verdictId=00000000-0000-0000-0000-000000000000&type=AFFILIATE&target=https://angi.com/test" -MaximumRedirection 0 -UseBasicParsing
if ($trackTest.StatusCode -eq 302 -and $trackTest.Headers.Location -match 'angi.com') {
    Write-Host " Affiliate Redirect Test: PASS (Redirects to angi.com successfully)"
} else {
    Write-Host " Affiliate Redirect Test: FAIL (Blocked or Failed)"
}
Write-Host ""

Write-Host "=========================================="
Write-Host "             ALL TESTS DONE             "
Write-Host "=========================================="
