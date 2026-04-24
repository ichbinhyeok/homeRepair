# LifeVerdict

LifeVerdict is currently a **v2 inspection ask pre-send check** for buyer agents and buyers under contract.

The product helps a user paste a proposed inspection repair request, seller-credit ask, report finding, or agent note before it is sent. It returns a sendability verdict, revised ask, cut list, fallback posture, evidence support, and agent-ready wording.

## Version History

### v1: broad repair-cost pSEO

The first version tried to win through many city, era, and component repair-cost pages.

Why it failed:

- The wedge was too broad: generic home-repair cost pages attracted repair browsing, not live transaction urgency.
- The surface was too informational: users and Google saw a content directory more than a tool.
- The product identity was diluted: the visible pages said "repair cost" while the useful backend logic was closer to buyer-side inspection negotiation.
- Traffic quality was weak: impressions improved in Search Console, but CTR, buyer-agent intent, and packet actions did not prove commercial pull.

### v2: inspection ask pre-send check

The current version narrows the product to one transaction moment:

`inspection findings -> what to ask -> what to cut -> fallback -> sendable packet`

The target customer is a buyer agent or small buyer-agent team handling post-inspection negotiations. Buyers under contract can use it directly, but the output is shaped for the buyer-agent workflow.

## Current Product

- Core route: `/home-repair`
- Core ICP: buyer agents and 2-10 seat buyer-agent teams
- Secondary ICP: buyers under contract
- Core artifact: pre-send review packet
- Validation mode: free packet generation, copy, print, evidence matching, and feedback
- Monetization rule: do not charge until copy/print/reuse/team-setup signals prove pull

## Documentation Map

- `SERVICE_OVERVIEW.md`: source of truth for current product direction.
- `docs/PRODUCT_EVOLUTION.md`: v1 failure and v2 pivot narrative.
- `docs/PIVOT_PLAN.md`: validation-first operating plan.
- `docs/GROWTH_PLAYBOOK.md`: acquisition surfaces, measurement, and post-deploy SEO rules.
- `docs/RELEASE_CANDIDATE_REVIEW.md`: release scope, risks, visual review, and ship gate.
- `docs/CUSTOMER_POSITIONING.md`: target customer and product boundaries.
- `DESIGN.md`: visual system and interface rules.

## Build And Test

```powershell
.\gradlew.bat test
.\gradlew.bat playwrightTest
.\gradlew.bat bootJar
```

Current release-candidate verification is documented in `docs/RELEASE_CANDIDATE_REVIEW.md`.
