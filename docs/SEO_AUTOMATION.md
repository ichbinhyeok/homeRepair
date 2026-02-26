# SEO Automation Scripts

This project now includes automation for:

1. Page quality checks (title/description/canonical/FAQ city consistency)
2. Internal-link and crawl-graph checks (orphan pages, depth, broken links)
3. HTTP smoke checks (200/301/404 and canonical redirect behavior)

## Scripts

- `scripts/seo_content_audit.ps1`
- `scripts/internal_link_audit.ps1`
- `scripts/seo_smoke_test.ps1`
- `scripts/run_seo_audits.ps1` (wrapper)

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

## Notes

- The wrapper exits non-zero when any audit fails.
- `seo_smoke_test.ps1` expects the app to be running at `-BaseUrl`.
