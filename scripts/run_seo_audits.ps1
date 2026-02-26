[CmdletBinding()]
param(
    [string]$ProjectRoot = "",
    [string]$BaseUrl = "http://localhost:8080",
    [string]$OutputDir = "",
    [int]$MaxExamples = 25,
    [switch]$SkipHttp
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    $ProjectRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path
} else {
    $ProjectRoot = (Resolve-Path $ProjectRoot).Path
}

if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $OutputDir = Join-Path $ProjectRoot "logs"
}
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

$contentAuditScript = Join-Path $PSScriptRoot "seo_content_audit.ps1"
$linkAuditScript = Join-Path $PSScriptRoot "internal_link_audit.ps1"
$smokeScript = Join-Path $PSScriptRoot "seo_smoke_test.ps1"

foreach ($required in @($contentAuditScript, $linkAuditScript, $smokeScript)) {
    if (-not (Test-Path $required)) {
        throw "Required script not found: $required"
    }
}

$failures = New-Object System.Collections.Generic.List[string]

Write-Host "=== Running SEO Content Audit ==="
try {
    & $contentAuditScript `
        -ProjectRoot $ProjectRoot `
        -OutputPath (Join-Path $OutputDir "seo-content-audit.txt") `
        -MaxExamples $MaxExamples `
        -FailOnIssue
} catch {
    $failures.Add("seo_content_audit.ps1 :: $($_.Exception.Message)") | Out-Null
    Write-Host "Content audit failed."
}

Write-Host ""
Write-Host "=== Running Internal Link Audit ==="
try {
    & $linkAuditScript `
        -ProjectRoot $ProjectRoot `
        -OutputPath (Join-Path $OutputDir "internal-link-audit.txt") `
        -MaxExamples $MaxExamples `
        -FailOnIssue
} catch {
    $failures.Add("internal_link_audit.ps1 :: $($_.Exception.Message)") | Out-Null
    Write-Host "Internal link audit failed."
}

if (-not $SkipHttp) {
    Write-Host ""
    Write-Host "=== Running SEO Smoke Test ==="
    try {
        & $smokeScript `
            -BaseUrl $BaseUrl `
            -OutputPath (Join-Path $OutputDir "seo-smoke-test.txt") `
            -FailOnIssue
    } catch {
        $failures.Add("seo_smoke_test.ps1 :: $($_.Exception.Message)") | Out-Null
        Write-Host "SEO smoke test failed."
    }
} else {
    Write-Host ""
    Write-Host "Skipping HTTP smoke tests (-SkipHttp enabled)."
}

Write-Host ""
if ($failures.Count -eq 0) {
    Write-Host "All SEO audits passed."
    Write-Host "Reports saved under: $OutputDir"
} else {
    Write-Host "SEO audits completed with failures:"
    foreach ($failure in $failures) {
        Write-Host " - $failure"
    }
    throw "One or more SEO audits failed. Check reports in $OutputDir"
}
