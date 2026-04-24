# Product Evolution: v1 To v2

Date: 2026-04-24

## One-Line Summary

LifeVerdict v1 failed because it looked like a broad repair-cost content site. LifeVerdict v2 exists because the real money-nearest problem is narrower: helping buyer agents decide whether a post-inspection ask is safe to send.

## v1: What It Was

v1 was a broad home-repair and repair-cost pSEO product.

It used:

- city pages,
- era pages,
- component repair-cost pages,
- static verdict pages,
- generic repair-risk and cost framing,
- tool logic behind a surface that mostly looked informational.

The intended growth motion was:

1. Go broad with pSEO.
2. Find winning pages.
3. Narrow into the winners.
4. Use the tool layer to differentiate after the visitor arrived.

## Why v1 Failed

### 1. The visible surface and the valuable product did not match

The backend was moving toward risk, prioritization, and buyer-side decision support. The public surface still looked like repair-cost browsing.

That confused the product identity:

- Google saw repair-cost content.
- Users saw an informational directory.
- The actual tool value was not obvious above the fold.

### 2. The pSEO surface was broad but not money-nearest

City/era/component pages can generate impressions, but many visitors are not under contract and do not need to send anything today.

That means traffic could grow while commercial intent stayed weak.

### 3. The wedge was narrow and the surface was also narrow at the wrong time

The earlier wedge did not create a strong enough immediate user action. It was neither broad enough to become a dominant content destination nor narrow enough to own a painful transaction moment.

The correct shape is:

`narrow wedge, wider surface`

For this project, the wedge is one live transaction job. The wider surface should be many high-intent phrasings of that same job.

### 4. The product was too easy to classify as generic

Broad repair-cost education competes with large publishers, contractor sites, marketplaces, AI summaries, and local service content.

LifeVerdict does not win by being another repair-cost source. It has a better chance if it is seen as a transaction tool that produces a usable packet.

### 5. Search Console improved in the wrong way

Technical cleanup improved rankings and impressions, but the query mix still leaned toward generic repair-cost or component phrases.

That is not proof of a buyer-agent workflow business. It is proof that v1 could be crawled and ranked for old identity signals.

## The v2 Decision

v2 pivots the product from:

`broad repair-cost pSEO`

to:

`inspection ask pre-send check`

The product now starts from the moment right before a buyer or buyer agent sends a repair request, seller-credit ask, objection, counter, or fallback.

## v2: What It Is

v2 is a tool-first workflow for inspection-response negotiation.

The core job:

1. Paste the proposed ask, seller-credit number, agent note, report finding, or inspection text.
2. Add optional evidence, loan, deadline, quote, and case context.
3. Receive a sendability verdict.
4. See what survives, what should be cut, what evidence is missing, and what fallback to use.
5. Copy or print an agent-ready packet.

The core output includes:

- Send / Revise / Do Not Send posture
- revised ask
- fallback posture
- cut list
- missing evidence
- number basis
- lender/form caveats
- agent-ready wording
- case/workspace framing

## v2 Target Customer

Primary:

- buyer agents,
- small buyer-agent teams,
- transaction coordinators working with buyer agents,
- especially financed FHA, VA, and conventional files where wording, deadline, and lender sensitivity matter.

Secondary:

- buyers under contract who need a stronger first draft before sending the packet through their agent workflow.

Not first:

- broad homeowners,
- renovation planners,
- contractor lead buyers,
- inspector report-writing software users,
- lender or insurer enterprise workflow teams.

## v2 Acquisition Strategy

v2 still uses SEO, but not as broad information bait.

The acquisition surface should be a set of transaction-decision pages:

- inspection response letter,
- seller credit after home inspection,
- repair request vs seller credit,
- what to ask for after home inspection,
- inspection objection,
- inspection deadline,
- seller refused repairs,
- seller counter,
- FHA/VA repair and credit pages,
- roof, sewer, electrical, foundation, mold, HVAC, plumbing, water-intrusion, polybutylene, and FPE-specific ask pages.

Every page must converge on the same product:

`check the ask before it is sent`

## What Changed In The Codebase

- Legacy broad static verdict pages were removed from the active public surface.
- Old static SEO generator/template paths were removed.
- `/home-repair` became the core tool entry point.
- Forty indexable tool-first acquisition surfaces now route into the same packet workflow.
- Buyer-agent, sample-packet, and FHA/VA proof pages support trust and commercial context.
- Evidence upload supports PDF, TXT, image OCR, and matched citations.
- Validation fixtures now cover 1,100 domain cases.

## Success Metrics For v2

v2 should be judged by product pull first and search second.

Primary signals:

- packet generated,
- copy packet,
- copy agent request,
- print packet,
- useful feedback,
- second file run,
- buyer-agent team setup click or request.

Search signals:

- impressions by v2 surface URL,
- clicks by v2 surface URL,
- query mix moving away from repair-cost browsing and toward inspection-response negotiation,
- tool-open rate after landing,
- packet-generated rate after tool open.

## Monetization Rule

Do not monetize before product pull.

Revenue comes later if the v2 artifact proves useful. Likely paid layers are:

- broker-ready export,
- team templates,
- saved case libraries,
- repeated desk review,
- small-team workflow support.

## Kill Rule

If visitors generate packets but do not copy, print, reuse, save, or request team setup, the problem is not pricing. The packet is not useful enough.

If pages get impressions but attract generic repair-cost queries, the problem is not page count. The surface is drifting back toward v1.
