# Pivot Plan: v2 Validation-First Inspection Ask Tool

Date: 2026-04-24

## Decision

LifeVerdict should validate product pull before monetization.

The current v2 bet is not "repair-cost SEO" or "request-list generation." It is:

1. A buyer is under contract.
2. The inspection report creates deal anxiety.
3. The buyer or agent needs to know what ask is safe to send.
4. LifeVerdict checks the proposed ask, cuts weak items, prepares fallback posture, and turns the result into agent-ready language.

No paywall, affiliate CTA, or contractor-lead monetization should be introduced until product actions prove the packet is useful.

## Why v1 Failed

v1 tried to use broad city/era/component pSEO to create discovery.

That failed because:

- The public surface looked like a repair-cost directory, not a transaction tool.
- The query mix was too generic and too far from a live inspection-response decision.
- The useful backend logic was hidden behind informational pages.
- Google had little reason to understand LifeVerdict as a unique buyer-side workflow.
- Higher impressions did not translate into strong CTR, packet use, or commercial intent.

The lesson is not "never use SEO." The lesson is:

`Use SEO only for high-intent transaction-decision surfaces that open the same tool.`

## What v2 Is

v2 is an inspection ask pre-send check.

The product takes:

- proposed repair request,
- seller-credit ask,
- inspection findings,
- agent note,
- optional report upload,
- optional loan/deadline/quote/case context.

The product returns:

- Send / Revise / Do Not Send verdict,
- revised ask,
- fallback posture,
- cut list,
- missing evidence,
- number basis,
- lender/form caveats,
- agent-ready wording.

## Primary Validation Signals

Track these before revenue:

- `PACKET_GENERATED`
- `COPY_PACKET`
- `COPY_AGENT_REQUEST`
- `COPY_ASK_SUMMARY`
- `PRINT_PACKET`
- `SAVE_PACKET`
- `SUBMIT_FEEDBACK`
- `TEAM_SETUP_INTEREST`

The strongest signal is `COPY_PACKET` or `PRINT_PACKET` after a generated result. Email capture is secondary and must stay optional.

## Monetization Gate

Do not monetize until one of these is true over a fixed 14-day window:

- At least 30 generated packets and 10 copied or printed packets.
- At least 5 optional emails from users who marked the packet useful.
- At least 3 agents or buyer-agent teams ask to reuse the workflow.

If none happen, improve the packet instead of adding payment.

## Product Surface

Keep `/home-repair` tool-first:

- First input: proposed ask or inspection findings.
- Context input: loan posture, quote support, deadline, stage, form path.
- Optional input: market, era, red-flag systems, case metadata.
- Output: send posture, request summary, agent request, keep/verify/cut split, defensible fallback.

Support this with high-intent entry pages:

- response letter,
- seller credit,
- repair request vs seller credit,
- what to ask,
- repair request,
- objection,
- deadline,
- counter/refusal,
- financing,
- form/deadline,
- high-stakes defect categories.

Avoid:

- city/era directory browsing as the main experience,
- generic renovation budgeting,
- contractor affiliate CTAs before validation,
- paywalling print or copy actions before proof of pull,
- surface pages that are only doorway pages and do not answer a distinct transaction job.

## Implementation Sequence

### Phase 1: Measure Product Pull

- Remove gated print/email unlock.
- Make copy and print free.
- Log server-side engagement events.
- Keep optional email only as feedback/follow-up.
- Review event counts weekly through admin validation signals.

### Phase 2: Improve The Artifact

- Put the pre-send verdict before the packet so the product is not perceived as a prettier request-list builder.
- Add richer packet formatting for agent copy/paste.
- Add seller-response fallback.
- Add what-not-to-ask-for.
- Add report/evidence upload after manual finding input shows usage.

### Phase 3: Distribution

- Publish tool-first SEO surfaces only around transaction decisions.
- Keep the visitor's original intent after the click.
- Create an agent-facing share link.
- Create buyer-agent team proof.
- Consider inspector add-on positioning only after the packet proves value.

### Phase 4: Monetization

Only after validation:

- Buyer one-off export: `$9-$19`.
- Agent pack: `$29-$49` for a small report bundle.
- Team workflow: monthly saved cases, templates, exports, and desk review support.
- Inspector add-on or white-label follow-up packet if embedded distribution becomes viable.

## Kill Rule

If users generate packets but do not copy, print, save, submit useful feedback, or request team setup, the problem is not pricing. The artifact is not valuable enough yet.

If SEO pages get traffic but the query mix drifts back to generic repair-cost browsing, the problem is not page count. The acquisition surface is reverting to v1.
