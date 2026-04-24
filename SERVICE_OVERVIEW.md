# SERVICE OVERVIEW: LifeVerdict v2

> Source of truth for the current product direction. Read this before changing product, routing, SEO, or pSEO behavior.

## 1. Current Product Thesis

LifeVerdict v2 is an **inspection ask pre-send check** for buyer agents and buyers under contract.

The core job is:

1. A buyer receives inspection findings during an active transaction.
2. The buyer or agent drafts a repair request, seller-credit ask, objection, counter, or fallback.
3. LifeVerdict checks the request before it is sent: what to ask for, what to cut, what evidence is missing, and what fallback to use if the seller pushes back.
4. LifeVerdict produces a copy-ready packet with sendability verdict, revised ask, cut list, caveats, fallback, and agent-ready wording.

The product should not present itself as a broad home-repair cost directory. The prior pSEO experiment proved that city/era/component pages can create generic repair-cost impressions without establishing buyer-side inspection negotiation intent.

## 2. Version History

### v1: broad repair-cost pSEO

v1 tried to use a large city/era/component repair-cost surface to discover traffic, then narrow into winners later.

It failed for five reasons:

- The visible surface looked like repair-cost education, while the useful backend logic was closer to buyer-side transaction decision support.
- Generic repair-cost searches were not money-nearest enough; many visitors were browsing, not preparing a live ask.
- The product identity became broad and weak. Google had little reason to treat LifeVerdict as a unique tool.
- The tool was hidden behind informational surface area, so users did not immediately understand what action to take.
- Search Console improvements mostly reflected legacy repair-cost visibility, not proof of buyer-agent workflow demand.

### v2: inspection ask pre-send check

v2 narrows the wedge to one transaction moment:

`before the inspection request is sent`

The acquisition surface can be broader than one keyword, but every surface must map back to the same job:

`check what to ask, what to cut, and what fallback to use`

See `docs/PRODUCT_EVOLUTION.md` for the full v1-to-v2 narrative.

## 3. Product Boundaries

Primary workflow:

- Buyer under contract.
- Inspection response, objection, option, or resolution window is active.
- Output must be something a buyer agent can review, copy, print, or send.

Primary customer:

- Buyer agents and 2-10 seat buyer-agent teams.

Secondary customer:

- Buyers under contract who need a stronger first draft before using their agent workflow.

Useful context:

- Report evidence.
- Loan posture.
- Quote support.
- Deadline and deal stage.
- Contract/form path.
- Local market and home-era context.

Non-goals:

- Broad renovation budgeting.
- Generic city repair-cost directory.
- Component-by-component contractor lead-gen pages.
- Inspector report-writing suite.
- Indexed personal result pages.
- Paywall before product pull is proven.

## 4. Architecture

- Backend: Java 21, Spring Boot.
- Templates: JTE.
- Main controller: `HomeRepairController`.
- Main service: `InspectionResponseService`.
- Evidence service: `InspectionDocumentService`.
- Main tool route: `/home-repair`.
- Result route: `/home-repair/result/{uuid}` with `noindex`.
- Acquisition surfaces: `AcquisitionSurface` enum plus `pages/hub.jte`.
- Commercial/trust proof: `/for-buyer-agents`, `/sample-seller-credit-request-after-home-inspection`, `/fha-va-inspection-repairs-and-seller-credit`.

v2 intentionally removes the old broad static verdict page system from the public surface. Do not reintroduce city/era/component pages unless a specific page maps to a live inspection-response transaction decision.

## 5. SEO Rules

SEO supports the tool. SEO is not the product.

Indexable surface should stay narrow around transaction-decision intent:

- `/`
- `/home-repair`
- `/inspection-response-letter`
- `/seller-credit-after-home-inspection`
- `/repair-request-vs-seller-credit-after-inspection`
- `/what-to-ask-for-after-home-inspection`
- `/repair-request-after-home-inspection`
- `/inspection-objection-after-home-inspection`
- `/inspection-contingency-deadline-after-home-inspection`
- high-intent counter, financing, form/deadline, and system-specific ask pages

Do not expand pSEO again unless the query represents a real transaction decision, such as:

- `repair request after home inspection`
- `seller credit after inspection`
- `what to ask for after home inspection`
- `seller refused repairs after inspection`
- `inspection objection notice`
- `FHA inspection repairs seller credit`

If a page attracts generic component-cost queries but does not create tool opens, packets, copy/print actions, or buyer-agent intent, treat it as v1 drift.

## 6. Success Metrics

v2 should be judged by product pull before monetization.

Primary product metrics:

- Inspection findings submitted.
- Packet generated.
- Weak items cut before send.
- Ask summary copied.
- Agent message copied.
- Packet printed.
- Feedback marked useful.
- Second file run.
- Buyer-agent team setup requested.

Search metrics:

- Impressions by v2 surface URL.
- Clicks by v2 surface URL.
- CTR by v2 surface URL.
- Query mix moving from repair-cost browsing toward inspection-response negotiation.
- Tool open after landing.
- Packet generated after tool open.

Kill or re-pivot if users do not copy, print, reuse, or request team setup after generating packets. The artifact is valuable only if it helps the user avoid an overbroad request and move to the next message faster.

## 7. Monetization Rule

Stay in validation mode until product pull is visible.

- Copy and print actions remain free.
- Email capture remains optional, not an unlock gate.
- Affiliate or contractor-lead CTAs stay off the primary buyer flow.
- Monetization starts only after repeated packet actions prove the artifact is useful.

Likely paid layers after validation:

- Broker-ready export.
- Team templates.
- Saved case libraries.
- Repeated desk review.
- Small-team workflow support.

See `docs/PIVOT_PLAN.md` for operating gates and `docs/RELEASE_CANDIDATE_REVIEW.md` for current release status.
