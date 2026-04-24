# Domain ICP Review

Date: 2026-04-24

This document is based on external web research and product-surface review, not on direct user interviews.
The personas below are synthetic, but each is grounded in current industry sources.

## Version Context

v1 was a broad repair-cost pSEO surface. The ICP review explains why that was structurally weak: the market does not need another generic repair-cost directory.

v2 is the inspection ask pre-send check. It is aimed at the buyer-agent workflow where scope, evidence, financing, deadline, and sendability determine whether the next message is credible.

## Cold Verdict

1. The market already has `report -> request list` tools.
   Spectora, HomeGauge, and Palmtech already let agents or buyers select findings, enter credits/comments, save progress, and send/print request lists.

2. That means a prettier request builder is not enough.
   The real pain is deciding:
   - what belongs in the ask,
   - what should be excluded,
   - when to ask for credit vs repairs,
   - what will create lender or deadline risk,
   - how to defend the number quickly.

3. The strongest customer is not `all buyers`.
   The strongest customer is the buyer agent working financed deals, especially first-time-buyer-heavy deals where inspection anxiety, lender sensitivity, and justification pressure all collide.

4. Inspectors are not the first wedge.
   Inspectors already have embedded workflow options through inspection-report software. A standalone product has weaker pull there unless it is embedded or white-labeled.

5. The product category is not `home repair cost`.
   The winning category is closer to `inspection negotiation intelligence for buyer agents`.

## Evidence Behind The Verdict

- Spectora Repair Request Builder supports sorting, credit/comment entry, preview, email/text sending, and revisit-without-losing-progress.
- HomeGauge Create Request List supports buyer/agent collaboration and editing on the same request list.
- Palmtech positions its Request List as something agents love because it helps create the inspection response and send it to the seller's agent.
- Colorado's official `Inspection Objection Notice` shows the output is not a generic letter in every market, and explicitly warns that inspection resolutions can alter loan terms and delay funding.
- Colorado's transaction file checklist includes inspection report, inspection objection notice, inspection resolution, appraised value objection notice, lender letter, and addenda in the same file set.
- NAR and Florida Realtors materials show buyer-agent services and compensation are now explicit, negotiable, and expected to be defined in writing.
- VA guidance and HUD/FHA property guidance show financing constraints are real and should shape what gets escalated in the packet.

## Synthetic Personas

### Persona 1: 9-year buyer agent, first-time buyer heavy, FHA/VA mix
- Handles emotional buyers who want to ask for everything in the report.
- Real pain: narrowing the list without looking weak.
- Current workaround: long email plus 2-3 contractor texts.
- Cold take on us: useful only if it helps choose the right ask and avoids lender trouble.

### Persona 2: 12-year buyer agent in suburban financed deals
- Knows sellers will reject laundry-list requests.
- Real pain: turning a 60-page report into a short, credible ask before the deadline.
- Current workaround: report markup, spreadsheet, and edited boilerplate email.
- Cold take on us: likes the packet idea, does not care about generic education.

### Persona 3: 7-year team lead with 3 agents
- Needs juniors to produce cleaner inspection responses.
- Real pain: quality control and consistency across agents.
- Current workaround: shared templates and manual review.
- Cold take on us: would adopt if it makes junior output less embarrassing.

### Persona 4: 6-year transaction coordinator
- Lives in deadline and paperwork land, not negotiation authority.
- Real pain: missing objection/resolution steps or letting the file get messy.
- Current workaround: checklist, calendar reminders, chasing signatures.
- Cold take on us: wants output that lines up with real file documents, not just a nice-looking summary.

### Persona 5: 15-year home inspector using modern report software
- Already has request-list tooling in the report workflow.
- Real pain: agent adoption, referrals, smoother post-report experience.
- Current workaround: Spectora/HomeGauge/Palmtech request lists.
- Cold take on us: standalone tool is weak; white-label or embedded layer is more interesting.

### Persona 6: 8-year buyer agent after the NAR settlement changes
- Must justify compensation and services more clearly.
- Real pain: proving value to buyers in writing, not just "trust me."
- Current workaround: buyer presentation, manual negotiation notes, ad hoc updates.
- Cold take on us: strong if it visibly demonstrates negotiation work product.

### Persona 7: 10-year VA/FHA-focused agent
- Sees more lender/appraisal-sensitive issues than average.
- Real pain: not all credits and repair structures are equally finance-safe.
- Current workaround: call lender, gather estimates, push for safe structure.
- Cold take on us: product wins only if financing constraints are first-class, not sidebar tips.

### Persona 8: 11-year luxury/cash-market buyer agent
- Fewer financing constraints, more waived or softer contingencies.
- Real pain is speed, access, and winning offers, not inspection packet quality.
- Cold take on us: low urgency customer, wrong first wedge.

## What Experts Would Say Is Still Missing

- State-form-aware output, not just a general packet
- Stronger lender-aware guidance for credits, repairs, and timing
- Better support for quote/evidence gathering under deadline
- Cleaner proof of why certain findings were excluded
- A clear reason for an experienced agent to switch from existing report-linked tools

## What To Build Because Of This

1. `Scope engine first`
   The product must be best at deciding what belongs in the ask and what does not.

2. `Finance-aware negotiation output`
   Lender-visible, appraisal-sensitive, and closing-delay risk must shape the packet.

3. `Form-adjacent output`
   The product should produce language that maps cleanly into objection / repair / credit workflows, not act like every market uses a "response letter."

4. `Agent-proof packet`
   The output should make an experienced buyer agent look sharper, faster, and more defensible in front of the client and the listing side.

5. `No drift into generic homeowner content`
   Broad repair education weakens the category.

## What Not To Build First

- Generic repair encyclopedia pages
- Broad homeowner maintenance tools
- Inspector report-writing features
- Enterprise lender workflow
- Login-heavy team infrastructure before the core packet is truly better than current workarounds

## Validation Case Set

The current 30-persona synthetic validation set lives in:

- `docs/DOMAIN_VALIDATION_CASE_SET.md`
- `src/test/resources/inspection-validation-cases.json`

Use it as a red-team benchmark, not as proof of accuracy. The product should pass these cases by staying conservative, showing evidence status, explaining exclusions, and avoiding definitive legal, lender, or contractor-price claims.

## Source Links

- Spectora Repair Request Builder: https://support.spectora.com/en/articles/2155740-spectora-s-repair-request-builder
- Spectora guide for agents: https://www.spectora.com/r/repair-request-builder/
- HomeGauge Create Request List: https://support.homegauge.com/en/articles/11933264-create-request-list
- Palmtech Request List: https://www.palmtech.com/request-list-benefits/
- Colorado Inspection Objection Notice: https://dre.colorado.gov/sites/dre/files/documents/Inspection%20Objection%20Notice%20%28fillable%29_for%20use%20on%20or%20after%20January%201%2C%202026.pdf
- Colorado Transaction File Checklist: https://dre.colorado.gov/sites/dre/files/documents/Transaction%20File%20Checklist%20-%20FINAL%2005.25%20%28fillable%29.pdf
- Colorado transaction file requirements: https://dre.colorado.gov/transaction-file-requirements-and
- California investigation contingency guide: https://www.car.org/-/media/CAR/Documents/Transaction-Center/PDF/QUICK-GUIDES/Quick-Guide---The-Investigation-Contingency.pdf
- California buyer requests for repairs guide: https://www.car.org/-/media/CAR/Documents/Transaction-Center/PDF/QUICK-GUIDES/Quick-Guide--Buyer-Requests-for-Repairs-REVISED-3822.pdf
- Texas contracts index: https://www.trec.texas.gov/pdf/contracts
- Texas Amendment to Contract (Form 39-10): https://www.trec.texas.gov/forms/amendment-contract-1
- Texas Notice of Buyer's Termination of Contract: https://www.trec.texas.gov/forms/notice-buyers-termination-contract
- Florida contract law library: https://www.floridarealtors.org/law-ethics/library/florida-real-estate-contract-laws
- Florida FR/Bar AS IS contract: https://www.floridarealtors.org/sites/default/files/2026-02/AS%20IS%20Residential%20Contract%20for%20Sale%20and%20Purchase%20%28FloridaRealtors-FloridaBar-ASIS-7x%29_Redlined%5B1%5D.pdf
- Florida FR/Bar Residential Contract: https://www.floridarealtors.org/sites/default/files/2024-12/Residential%20Contract%20for%20Sale%20and%20Purchase%20%28FloridaRealtors-FloridaBar-7%29%20Redline%20%281%29.pdf
- NAR written buyer agreements guide: https://www.nar.realtor/the-facts/consumer-guide-to-negotiating-written-buyer-agreements
- Florida Realtors buyer broker FAQ: https://www.floridarealtors.org/law-ethics/nar-settlement-faqs
- VA Circular 26-24-14: https://www.benefits.va.gov/HOMELOANS/documents/circulars/26-24-14.pdf
- HUD/FHA repair conditions: https://archives.hud.gov/offices/hsg/sfh/ref/sfhp1-22.cfm
- Redfin negotiation after inspection: https://www.redfin.com/blog/negotiating-after-home-inspection/
- Zillow inspection negotiation / repairs: https://www.zillow.com/learn/what-fixes-are-mandatory-after-a-home-inspection/
- Zillow buyer repair responsibility / contingency data: https://www.zillow.com/learn/who-pays-for-home-inspection/
