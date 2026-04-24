# SEO Automation Scripts

## Version Context

These scripts were created during the v1 pSEO period, but they still matter for v2.

v1 optimized broad static repair-cost pages. v2 uses SEO differently: audits should confirm that the site exposes a narrow set of tool-first transaction surfaces and does not drift back into a broad repair-cost directory.

## Current Use

Use the automation to check:

1. Titles, descriptions, canonicals, and page quality.
2. Internal-link and crawl-graph health.
3. HTTP smoke behavior for 200/301/404/410 and canonical redirects.
4. Whether retired legacy URLs stay out of the active crawl/index surface.

## Scripts

- `scripts/seo_content_audit.ps1`
- `scripts/internal_link_audit.ps1`
- `scripts/seo_smoke_test.ps1`
- `scripts/seo_smoke_test.sh`
- `scripts/run_seo_audits.ps1`

## Quick Start

Run static audits only:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run_seo_audits.ps1 -SkipHttp
```

Run all audits including HTTP checks:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run_seo_audits.ps1 -BaseUrl http://localhost:8080
```

## Individual Runs

```powershell
powershell -ExecutionPolicy Bypass -File scripts/seo_content_audit.ps1
powershell -ExecutionPolicy Bypass -File scripts/internal_link_audit.ps1
powershell -ExecutionPolicy Bypass -File scripts/seo_smoke_test.ps1 -BaseUrl http://localhost:8080
```

## Reports

Default report files are written to `logs/`:

- `logs/seo-content-audit.txt`
- `logs/internal-link-audit.txt`
- `logs/seo-smoke-test.txt`

## v2 Audit Rule

An SEO pass is not successful just because pages are crawlable.

For v2, a page is useful only if it:

- targets a live inspection-response transaction decision,
- opens the same pre-send packet workflow,
- preserves intent after the click,
- avoids generic repair-cost framing,
- creates tool opens, packet generation, copy/print, useful feedback, or buyer-agent setup intent.

## Notes

- The wrapper exits non-zero when any audit fails.
- `seo_smoke_test.ps1` expects the app to be running at `-BaseUrl`.
