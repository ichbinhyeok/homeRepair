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

## Entry: 2026-03-22
### Scope
- Property: `sc-domain:lifeverdict.com`
- Focus path: `/home-repair*`
- Current period: `2026-02-23` to `2026-03-22`
- Previous period: `2026-01-26` to `2026-02-22`

### Metrics Snapshot
- `/home-repair*` (current): `6 clicks`, `7,567 impressions`, `0.079% CTR`, `7.71 avg position`
- `/home-repair*` (previous): `3 clicks`, `1,740 impressions`, `0.172% CTR`, `19.11 avg position`
- Delta summary: impressions and rank improved sharply, but CTR fell further below target.

### Technical Signals
- Repeated extension variants (`.html.html` family): `0 clicks`, `431 impressions`, `0% CTR`, `14.53 avg position`
- State pages (`/home-repair/verdicts/states/*`): `2 clicks`, `1,255 impressions`, `0.159% CTR`, `5.01 avg position`
- Support pages (`/home-repair`, `/about`, `/methodology`, `/editorial-policy`, `/data-sources`, `/disclaimer`): `0 clicks`, `1,060 impressions`, `0% CTR`, `4.00 avg position`
- Deep item pages (`/home-repair/verdicts/{metro}/{era}/{item}`): `0 clicks`, `1,303 impressions`, `0% CTR`, `14.12 avg position`

### Changes Since Last Entry
- Confirmed repeated-extension variants now return `410 Gone` at the edge, but Google still shows stale indexed variants pending re-crawl.
- Confirmed valid target URLs such as `/home-repair`, `states/ca.html`, `states/tx.html`, and `pittsburgh-pa/pre-1950.html` are indexed.
- Reduced indexable surface in code:
  - support pages now emit `noindex,follow`,
  - deep item detail pages now default to `noindex,follow`,
  - support pages removed from sitemap.
- Tightened positioning again on `2026-03-22` after internal review:
  - home/root flows now frame the product as an `inspection repair budget` and `seller-credit plan`,
  - static verdict titles, H1s, metadata, CTA labels, and FAQ prompts now target:
    - what the inspection budget is,
    - what the buyer should verify first,
    - what seller credit to request,
  - dynamic buyer result pages now use `Inspection Budget & Credit Plan` framing instead of generic audit framing,
  - intake flow now captures top inspection findings, quote support, and closing timeline,
  - result pages now build an `inspection-to-credit` packet with:
    - must-fix now,
    - verify next,
    - can defer,
    - copy-ready seller credit summary,
    - copy-ready agent request script,
  - state hub descriptions now align to inspection-budget and negotiation language,
  - duplicate state hub generation logic was removed so only one generator owns `states/*.html`.
- Rewrote root, state hub, and verdict metadata toward budget/risk/inspection intent instead of generic “audit” copy.

### Assessment
- Status: **Keep project, kill the current expansion strategy**
- Reason: the site is getting discovery, but most new impressions come from low-click surfaces. Salvage path is a tighter index footprint plus stronger intent matching on winner pages.

### Deploy / Re-crawl Checklist
- Re-submit the sitemap after deploy: `https://lifeverdict.com/sitemap.xml`
- Do not rely on sitemap alone. Also run URL Inspection / Request Indexing on the highest-signal URLs:
  - `https://lifeverdict.com/home-repair`
  - `https://lifeverdict.com/home-repair/verdicts/states/tx.html`
  - `https://lifeverdict.com/home-repair/verdicts/states/fl.html`
  - `https://lifeverdict.com/home-repair/verdicts/pittsburgh-pa/pre-1950.html`
  - `https://lifeverdict.com/home-repair/verdicts/chicago-naperville-il/1950-1970.html`
- For repeated-extension junk URLs, do not submit them again. Keep `410 Gone` in place and monitor whether impressions decay after re-crawl.

### Current Operating Hypothesis
- This is no longer a broad `home repair cost calculator` play.
- Current wedge is:
  - older-home buyer due diligence,
  - inspection-to-budget translation,
  - seller-credit negotiation support.
- Success depends on better CTR and better click mix on a smaller set of pages, not on maximizing total indexed surface.

### Next Checkpoints
- `2026-03-29`
  - Confirm sitemap was re-submitted.
  - Check whether requested priority URLs show updated titles/descriptions/snippets in inspection.
  - Verify support pages begin dropping from indexed queries.
  - Check whether deep item page impressions start trending down after `noindex`.
- `2026-04-05`
  - Verify malformed variant impressions trend meaningfully below `100`.
  - Re-check `/home-repair*` CTR and compare top page mix.
  - Specifically check whether clicks shift toward buyer-intent pages with updated positioning.
  - If CTR is still under `0.15%`, narrow published city/era set further.

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
