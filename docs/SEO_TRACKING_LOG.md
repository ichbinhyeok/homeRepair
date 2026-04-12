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

## Entry: 2026-04-01
### Scope
- Property: `sc-domain:lifeverdict.com`
- Focus path: `/home-repair*`
- Current period: `2026-03-05` to `2026-04-01`
- Previous period: `2026-02-05` to `2026-03-04`

### Metrics Snapshot
- `/home-repair*` (current): `7 clicks`, `2,918 impressions`, `0.240% CTR`, `9.23 avg position`
- `/home-repair*` (previous): `4 clicks`, `6,913 impressions`, `0.058% CTR`, `9.56 avg position`
- Delta summary: total impressions dropped materially, but CTR improved more than 4x and clicks rose slightly. Cleanup is reducing noisy discovery while improving click efficiency.

### Technical Signals
- Repeated extension variants (`.html.html` family): `0 clicks`, `27 impressions`, `0% CTR`, `38.04 avg position`
- State pages (`/home-repair/verdicts/states/*`): `2 clicks`, `720 impressions`, `0.278% CTR`, `6.12 avg position`
- Support pages (`/home-repair`, `/about`, `/methodology`, `/editorial-policy`, `/data-sources`, `/disclaimer`): `0 clicks`, `187 impressions`, `0% CTR`, `4.98 avg position`
- Deep item pages (`/home-repair/verdicts/{metro}/{era}/{item}` canonical extensionless): `1 click`, `219 impressions`, `0.457% CTR`, `19.91 avg position`

### Search Console / Indexing Checks
- Sitemap status:
  - `https://lifeverdict.com/sitemap.xml`
  - `lastSubmitted`: `2026-03-22`
  - `lastDownloaded`: `2026-03-30`
  - `warnings`: `0`
  - `errors`: `0`
- URL inspection checks still show priority URLs as `Submitted and indexed`:
  - `/home-repair`
  - `/verdicts/states/tx.html`
  - `/verdicts/states/fl.html`
  - `/verdicts/pittsburgh-pa/pre-1950.html`
  - `/verdicts/chicago-naperville-il/1950-1970.html`
- Caveat: some winner URLs still show older `last crawl` dates in February 2026, which suggests the latest copy/positioning changes have not been fully re-crawled on every priority page yet.

### Query Mix Check
- Query mix is still noisy and heavily skewed toward generic component-cost lookups (for example windows, drywall, siding, tankless heater, decking).
- Buyer-intent negotiation language is not yet clearly visible in top query rows.
- Interpretation: the cleanup improved CTR by removing bad surfaces faster than it improved query targeting.

### Changes Since Last Entry
- Confirmed sitemap was re-submitted and re-downloaded without errors.
- Confirmed support-page impressions dropped sharply after `noindex` and sitemap reductions.
- Confirmed malformed repeated-extension demand is collapsing (`517 -> 27 impressions`).
- Confirmed state pages now produce non-zero CTR and 2 clicks.
- Confirmed deep-item pages are reduced but not yet fully gone from query mix.

### Assessment
- Status: **Keep running the pivot**
- Reason: this is the first checkpoint where technical cleanup clearly worked. CTR is now above the prior `0.15%` guardrail and close to the `0.30%` target. The remaining problem is not technical debt first; it is query mix and winner-page focus.

### Next Checkpoints
- `2026-04-08`
  - Re-run inspection on `pittsburgh-pa/pre-1950.html` and `chicago-naperville-il/1950-1970.html` to see if Google has re-crawled the updated copy.
  - Check whether `/home-repair*` CTR moves from `0.240%` toward `0.30%+`.
  - Verify deep-item impressions continue falling below `150`.
- `2026-04-15`
  - Re-check top queries and see whether any buyer-intent or seller-credit phrasing appears.
  - If query mix is still dominated by generic component-cost terms, narrow the published metro/era set and strengthen negotiation-language titles on the current winners.

## Entry: 2026-04-01 (Product Pivot Deepening)
### Scope
- Property: `sc-domain:lifeverdict.com`
- Focus path: `/home-repair*`
- Strategic frame: `inspection findings -> seller credit negotiation packet`

### External Validation
- Current competitor and market review still supports the pivot toward inspection-response workflow over generic repair education.
- Seller concessions remain a live market behavior in 2025 reporting, and inspection negotiation guidance from Redfin, Rocket Mortgage, and NAR still points to a documented credit-request workflow rather than broad DIY education.
- Internal synthesis from `Kant`, `Curie`, and `Kepler` converged on the same gap:
  - the product needed a tighter `decision artifact`
  - BUYING had too many mixed CTAs
  - the query mix needed stronger `seller credit after inspection` language

### Product / UX Changes
- Added `loanType` to the intake and result workflow.
- BUYING results now surface:
  - `target ask`
  - `defensible fallback`
  - `loan posture`
  - `copy-ready agent negotiation script`
- BUYING no longer leads with post-purchase calendar content.
- BUYING action hierarchy now prioritizes negotiation output first and vendor quote proof second.

### SEO / Query-Mix Changes
- Static winner-page titles were tightened toward `home inspection seller credit calculator`.
- Static descriptions now lead with `seller credit range after inspection` instead of generic repair-cost framing.
- Landing and hub copy now state more clearly that BUYING is the main workflow and other modes are secondary.

### What This Should Change
- Better alignment between query intent and page promise.
- Better first-screen conversion for BUYING users.
- Lower risk of drifting back into generic component-cost traffic.

### Risks
- Loan-type guidance is heuristic product logic, not legal or underwriting advice.
- Query mix may still stay noisy if the indexed metro/era surface remains too broad.
- Deep detail pages are still a risk surface if Google keeps surfacing them for generic component-cost searches.

### Next Checkpoints
- `2026-04-08`
  - Check whether top queries begin to show more `seller credit`, `after inspection`, `repair request` phrasing.
  - Confirm BUYING result pages do not show the `12-Month Security Calendar`.
- `2026-04-15`
  - If query mix is still generic, cut more metro/era pages and consider noindexing non-winning city/era pages.

## Entry: 2026-04-01 (Winner-Only Indexing)
### Scope
- Property: `sc-domain:lifeverdict.com`
- Focus path: `/home-repair*`
- Current period reviewed in GSC: `2026-03-05` to `2026-04-01`
- Strategic question: whether the project should stay broad or pivot harder into `inspection -> seller credit negotiation`

### Search Console Findings That Forced The Change
- Top-query mix is still wrong:
  - `argon gas windows baton rouge`
  - `double hung windows albuquerque`
  - `sheetrock finishing west hartford ct`
  - `siding cost gainesville`
  - `replacement cost 1950 or older`
- Current click concentration is extremely narrow:
  - `pittsburgh-pa/pre-1950.html`
  - `tulsa-ok/pre-1950.html`
  - `little-rock-north-little-rock-ar/1950-1970.html`
  - `states/fl.html`
  - `states/tx.html`
- State pages with impressions but no clicks still include:
  - `CA`
  - `IL`
  - `NY`
  - `NC`
  - `SC`
- Interpretation: technical cleanup worked, but Google still understands too much of the site as a generic component-cost directory instead of a buyer-side negotiation tool.

### External Validation
- Current market research still supports the `inspection -> credit request` wedge:
  - [Redfin seller concessions](https://www.redfin.com/news/home-seller-concessions-march-2025/)
  - [Rocket Mortgage: after inspection](https://www.rocketmortgage.com/learn/after-home-inspection-what-next)
  - [Rocket Mortgage: reasonable requests after inspection](https://www.rocketmortgage.com/learn/reasonable-requests-after-home-inspection)
  - [NAR seller concession](https://www.nar.realtor/closing/seller-concession)
  - [Homelight reasonable asks after inspection](https://www.homelight.com/blog/buyer-what-is-reasonable-to-ask-for-after-home-inspection/)
- Product-like competitors are closer to `repair estimate / negotiation packet` than broad education:
  - [Inspectify repair estimate](https://app2.inspectify.com/teams/368UGZ4tUJW3ziB32eHdD78T/repair_estimate)
  - [Repair Pricer negotiation](https://www.repairpricer.com/can-you-negotiate-after-inspection/)

### Decision
- Kill the broad index strategy.
- Keep the business only as:
  - older-home buyer
  - active inspection contingency
  - seller-credit negotiation packet

### Code Changes Implemented
- Sitemap is now winner-only. It keeps only:
  - `/`
  - `/home-repair`
  - `/home-repair/verdicts/states/tx.html`
  - `/home-repair/verdicts/states/fl.html`
  - `/home-repair/verdicts/pittsburgh-pa/pre-1950.html`
  - `/home-repair/verdicts/tulsa-ok/pre-1950.html`
  - `/home-repair/verdicts/little-rock-north-little-rock-ar/1950-1970.html`
  - `/home-repair/verdicts/chicago-naperville-il/1950-1970.html`
- Static metro/era verdict pages now split into:
  - winner pages: `index,follow`
  - non-winner pages: `noindex,follow`
- Static verdict templates no longer push crawlers into:
  - deep detail `Inspection notes` links
  - risk hub links as a primary navigation pattern
  - broad related-market clusters that point to non-winning pages
- State hubs now split into:
  - `TX`, `FL`: indexable state landings
  - all other states: `noindex,follow`
- State hubs now only expose city/era links when those targets are themselves winner pages.

### Why This Is More Correct
- The previous structure let Google keep discovering thousands of low-signal combinations.
- Query mix proved that generic repair-component demand was crowding out the intended buyer-intent wedge.
- This change makes the public search surface match the actual product strategy:
  - fewer pages
  - tighter intent
  - stronger packet-focused routing

### Continue / Kill Rules
- Continue only if the next 4-8 weeks show:
  - `/home-repair*` CTR `0.30%+`
  - at least `10` clicks in the period
  - top queries begin showing `seller credit`, `after inspection`, or `repair request`
  - BUYING users actually copy, print, or save the negotiation packet
- Kill if:
  - CTR falls back under `0.20%`
  - clicks stay flat
  - query mix remains dominated by generic component-cost lookups
  - packet actions do not materialize

## Entry: 2026-04-12
### Scope
- Property: `sc-domain:lifeverdict.com`
- Focus path: `/home-repair*`
- Current period: `2026-03-16` to `2026-04-12`
- Previous period: `2026-02-16` to `2026-03-15`

### Metrics Snapshot
- `/home-repair*` (current): `6 clicks`, `2,654 impressions`, `0.226% CTR`, `7.99 avg position`
- `/home-repair*` (previous): `6 clicks`, `7,367 impressions`, `0.081% CTR`, `8.35 avg position`
- Delta summary: cleanup still improved click efficiency and rank quality, but absolute clicks stayed flat. The site is healthier technically, not yet validated commercially.

### Technical Signals
- Repeated extension variants (`.html.html` family): `0 clicks`, `4 impressions`, one sampled malformed URL now returns `404 / Not found`
- State pages (`/home-repair/verdicts/states/*`): `2 clicks`, winner states still carry the only meaningful state-level demand
- Support pages (`/home-repair`, `/about`, `/methodology`, `/editorial-policy`, `/data-sources`, `/disclaimer`): `0 clicks`, `159 impressions`, cleanup held
- Deep item pages (`/home-repair/verdicts/{metro}/{era}/{item}`): one sampled URL drew `1 click / 5 impressions`, but inspection showed `Excluded by 'noindex' tag`

### Search Console / Inspection Findings
- Root `/home-repair`: `0 clicks`, `43 impressions`, `4.33 avg position`; indexed and last crawled `2026-04-10`
- State winners:
  - `states/fl.html`: `1 click`, `70 impressions`, `1.43% CTR`, `7.03 avg position`
  - `states/tx.html`: `1 click`, `94 impressions`, `1.06% CTR`, `5.55 avg position`
- State near-winners with visibility but no proof:
  - `states/ca.html`: `0 clicks`, `103 impressions`, `6.36 avg position`
  - `states/il.html`: `0 clicks`, `90 impressions`, `6.04 avg position`
- Priority verdict checks:
  - `chicago-naperville-il/1950-1970.html`: indexed, last crawl `2026-04-11`
  - `pittsburgh-pa/pre-1950.html`: still indexed with stale crawl `2026-02-10`; snippet classification lag remains a risk
- Sitemap:
  - `https://lifeverdict.com/sitemap.xml`
  - `lastSubmitted`: `2026-03-22`
  - `lastDownloaded`: `2026-04-08`
  - `warnings`: `0`
  - `errors`: `0`

### Query-Mix Conclusion
- The cleanup thesis was correct.
- The wedge-validation thesis is still unproven.
- Buyer-intent query families such as `seller credit`, `repair request`, `after inspection`, and equivalent negotiation phrasing still did not show up with real volume in the reviewed rows.
- Interpretation: Google is ranking the surviving pages better, but the site is still semantically too close to a repair-cost directory instead of a buyer-side negotiation tool.

### Changes Implemented On 2026-04-12
- Reclassified root, state hub, verdict, and risk-detail copy from `repair-cost / market data` framing to `seller credit after inspection / repair request / negotiation packet`.
- Tightened static verdict metadata, H1s, subtitles, FAQ answers, HowTo schema, breadcrumb schema, and CTA copy toward `after inspection` intent.
- Tightened state-hub metadata and labels toward buyer negotiation language instead of broad market research language.
- Reworked internal-link anchor text to stop reinforcing `analysis / market data / forensic report` semantics.
- Preserved winner-only indexing and regenerated static pages so the public surface stays narrow while copy reclassification propagates.

### Assessment
- Status: **Continue, but only as a narrow inspection-negotiation bet**
- Reason: this is not dead technically. It is still unproven strategically. The key failure mode is not crawl/index hygiene anymore; it is semantic mismatch and lack of evidence that real buyers search for this exact workflow enough to support the business.

### Continue / Kill Rules From This Point
- Continue if the next 2-6 weeks show:
  - `/home-repair*` CTR holding above `0.20%`
  - clicks rising above the current flat `6`
  - top queries starting to include buyer-intent negotiation phrasing
  - packet-related user actions showing up in product analytics
- Kill or re-pivot if:
  - clicks stay flat while impressions keep shrinking
  - winner pages still attract mostly generic repair-component demand
  - stale snippet classification on priority pages does not clear after re-crawl
  - there is still no evidence of buyer-intent demand by the next review window

### Next Checkpoints
- `2026-04-19`
  - Re-run inspection on `/home-repair`, `states/tx.html`, `states/fl.html`, `pittsburgh-pa/pre-1950.html`, and `chicago-naperville-il/1950-1970.html`
  - Check whether the updated `seller credit after inspection` titles/descriptions are live in Search Console inspection and snippets
- `2026-04-26`
  - Re-check top queries for buyer-intent phrasing
  - Decide whether to keep the current wedge, shrink further to only the 2-4 proven pages, or pivot the product again
- Manual re-crawl after deploy:
  - re-submit `https://lifeverdict.com/sitemap.xml`
  - request indexing for `/home-repair`
  - request indexing for `/home-repair/verdicts/states/tx.html`
  - request indexing for `/home-repair/verdicts/states/fl.html`
  - request indexing for `/home-repair/verdicts/pittsburgh-pa/pre-1950.html`
  - request indexing for `/home-repair/verdicts/chicago-naperville-il/1950-1970.html`

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
