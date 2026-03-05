# SEO Tracking Log

## Purpose
Track Search Console outcomes after SEO/code changes so we can decide whether to wait, iterate metadata/content, or fix technical issues.

## Update Cadence
- Check every 7 days.
- Use fixed comparison windows: last 28 days vs previous 28 days.
- Record absolute dates in every entry.

## KPI Guardrails
- Primary KPI: CTR on `/home-repair*`.
- Secondary KPI: impressions and average position.
- Technical KPI: impressions on malformed URL variants (for example `.html.html`) should trend down after canonical/410 rules.

## Entry: 2026-03-05
### Scope
- Property: `sc-domain:lifeverdict.com`
- Focus path: `/home-repair*`
- Current period: `2026-02-06` to `2026-03-05`
- Previous period: `2026-01-09` to `2026-02-05`

### Metrics Snapshot
- `/home-repair*` (current): `4 clicks`, `6,627 impressions`, `0.060% CTR`, `9.56 avg position`
- `/home-repair*` (previous): `2 clicks`, `491 impressions`, `0.407% CTR`, `15.87 avg position`
- Delta summary: impressions increased strongly, rank improved, CTR dropped materially.

### Technical Signals
- Repeated extension variants (`/home-repair/verdicts/...html.html...`): `0 clicks`, `515 impressions`, `0% CTR`, `20.90 avg position`
- State pages (`/home-repair/verdicts/states/*`): `0 clicks`, `979 impressions`, `0% CTR`, `5.45 avg position`

### Actions Implemented (Code)
- Added hard cut for repeated extensions under `/home-repair/verdicts/**` to return `410 Gone`.
- Added tests for repeated extension cases (risk/L1/state).
- Expanded state-hub and state-index content and metadata in templates:
  - stronger title/description copy,
  - FAQ structured data,
  - usage guidance and context sections.

### Assessment
- Status: **Do not pause fully**.
- Reason: impressions and position are improving, but CTR is still too low and malformed URL demand still exists.

### Next Checkpoints
- `2026-03-12`
  - Verify malformed variant impressions begin declining after deploy.
  - Check if state page CTR moves above `0%`.
- `2026-03-19`
  - Re-evaluate `/home-repair*` CTR trend.
  - If CTR is still under `0.15%`, prepare another title/description iteration for top-impression pages.

## Reusable Weekly Template
```
## Entry: YYYY-MM-DD
### Scope
- Property:
- Focus path:
- Current period:
- Previous period:

### Metrics Snapshot
- /home-repair* (current):
- /home-repair* (previous):
- Delta summary:

### Technical Signals
- malformed URL variants:
- states pages:

### Changes Since Last Entry
- code/template/redirect/sitemap changes:

### Assessment
- status:
- reason:

### Next Checkpoints
- YYYY-MM-DD:
- YYYY-MM-DD:
```
