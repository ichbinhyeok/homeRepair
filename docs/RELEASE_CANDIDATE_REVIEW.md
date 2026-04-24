# Release Candidate Review

Date: 2026-04-24

## Scope Locked

This release candidate is a product pivot, not a small landing-page edit.

- Version: v2.
- Prior version: v1 broad repair-cost pSEO.
- Core product: inspection ask pre-send check for buyer agents and buyers under contract.
- Core promise: before the request is sent, check what to ask, what to cut, and what fallback to use.
- Core artifact: sendability verdict, revised ask, cut list, evidence support, fallback, and agent-ready wording.
- Free validation: copy, print, packet generation, and feedback stay free until product pull is proven.

## Why This Release Exists

v1 failed because the public surface looked like broad home-repair cost content. It could attract impressions, but it did not strongly communicate a money-nearest buyer-agent workflow.

v2 exists to correct that mismatch. The product now leads with the tool action and uses SEO only as a set of high-intent entry points into the same pre-send packet workflow.

## Intentional Removals

- Removed legacy city/era/component static verdict pages from the public surface.
- Removed old static SEO generator/template paths tied to the broad repair-cost pSEO strategy.
- Current sitemap is tool-surface focused instead of broad legacy directory focused.

These removals are intentional because the old surface created broad repair-cost identity without enough buyer-agent transaction intent.

## New Release Assets

- Forty indexable transaction-decision surfaces converge into the same tool workflow.
- Buyer-agent team page, sample packet page, and FHA/VA financing page provide proof and commercial context.
- Evidence upload supports PDF, TXT, image OCR, and matched citations.
- Validation fixture set now includes 1,100 domain cases across financing, contracts, systems, persona, acquisition, negotiation failure, trust, SEO, and ops scenarios.

## Visual Review

Reviewed screenshots:

- `build/reports/playwright/seo-representative/desktop_home-repair.png`
- `build/reports/playwright/seo-representative/mobile_home-repair.png`

Verdict:

- Desktop passes first-screen recognition. The split view reads as an operational inspection desk, not a broad repair blog.
- Mobile passes the "understand within three scrolls" test. The top starts with the tool action, then shows the product poster and proof preview.
- The mobile tradeoff is deliberate: activation comes before brand theater. This is acceptable for a free validation tool because the user can act immediately.
- The strongest remaining design risk is page length after the hero. The 40-surface grid is SEO-useful but visually repetitive. Keep it below the fold and monitor scroll/click data.

## Release Risks

- Large diff: 757 deleted tracked files, mostly legacy static verdict pages. Reviewers should treat this as an intentional surface removal, not accidental data loss.
- OCR bundle size: `bootJar` is about 99.9 MB because OCR resources and dependencies are included.
- Search Console cannot judge the new direction until after deployment and recrawl. Current GSC is a pre-deploy baseline.
- Revenue should not be added before product-pull signals: generated packets, copy/print events, useful feedback, and team setup intent.

## Verification

- `.\gradlew.bat test` passed.
- `.\gradlew.bat playwrightTest` passed.
- `.\gradlew.bat bootJar` passed.
- `git diff --check` passed with CRLF warnings only.
- Public source/JTE scan found no remaining standalone `defense` language.

## Ship Gate

Go if:

- The large legacy deletion is accepted as the intended pSEO cleanup.
- The 99.9 MB application artifact is acceptable for the target deploy platform.
- The first post-deploy check confirms core pages return 200, sitemap exposes the new surfaces, and old static verdict URLs do not re-enter the index.

Do not ship if:

- The deploy platform has strict artifact-size limits below the current JAR size.
- The team wants to preserve old city/era/component pages as a temporary fallback.
- The product should be judged by old GSC data before the new version is deployed and indexed.
