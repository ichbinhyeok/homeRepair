# Domain Validation Case Set

Date: 2026-04-24

This is not direct customer research. It is a synthetic validation set built from public real-estate, inspection, lender, and contract-form research. Its purpose is to stop the product from looking credible while making weak domain assumptions.

## Version Context

These cases validate v2, not v1.

v1 asked whether broad repair-cost data could create useful pSEO surfaces. v2 asks whether inspection findings can produce a pre-send packet that a buyer agent would trust enough to copy, print, or reuse.

## Validation Boundary

LifeVerdict should not claim to be a final repair price, legal form, lender ruling, or substitute for a licensed agent, broker, attorney, lender, appraiser, or contractor.

The product can credibly aim to be:

- a first-draft inspection negotiation packet,
- a scope narrowing tool,
- an evidence checklist,
- a seller-credit / repair-request / objection handoff aid,
- a readiness gate that says what still needs review before sending.

The product is dangerous if it behaves like:

- a definitive repair-cost oracle,
- an automatic legal notice generator,
- a lender approval engine,
- a state-form completion authority,
- a tool that tells buyers to bypass their agent.

## Research Signals Used

- HomeGauge CRL and Spectora Repair Request Builder prove that `report -> request list` already exists.
- HomeGauge explicitly supports buyer/agent collaboration, request lists, repair addendum attachment, document history, and monetary compensation requests.
- Spectora lets agents select recommendations, add credit amounts or comments, preview, and create a link/PDF.
- Palmtech and InspectForge position request lists and agent dashboards as expected inspection software features.
- Redfin and Rocket Mortgage materials show that inspection-period negotiations, seller concessions, credits, walk-away decisions, and safety/structural findings remain active buyer problems.
- Reddit buyer threads repeatedly show the same practical confusion: what is reasonable, whether credits are too much, whether a seller will be offended, when safety items matter, and whether FHA/VA/lender-required repairs change the negotiation.
- TREC materials show Texas option-period and amendment logic is state-specific.
- C.A.R. materials show California request-for-repair, seller response, amendment, and contingency-removal language is state-specific.
- Colorado official forms show inspection objection / resolution timing and lender written communication matter.
- FHA/HUD and VA materials show financing-sensitive issues should be treated as constraints, not generic repair complaints.

## 30 Synthetic Domain Users

| ID | Persona | Role | Domain pain | What they would trust |
| --- | --- | --- | --- | --- |
| U01 | First-time-buyer-heavy agent in Atlanta | Buyer agent | Client wants every defect turned into credit | Narrow ask, excluded items, plain client explanation |
| U02 | VA-focused agent in Colorado Springs | Buyer agent | Seller credit may not solve lender-visible repair risk | VA-aware notes, evidence, lender-confirmation gate |
| U03 | Texas option-period agent in Austin | Buyer agent | Deadline pressure and amendment wording | Option-period status, TREC amendment handoff |
| U04 | California buyer agent in Sacramento | Buyer agent | Request-for-repair and contingency-removal sequencing | RR/RRRR/AEA adjacent handoff and deadline warning |
| U05 | Florida AS IS agent in Tampa | Buyer agent | Buyer thinks AS IS means no leverage | Inspection-period path, credit vs terminate framing |
| U06 | Junior agent on a 4-agent team | Buyer agent | Does not know what to leave out | Strong exclusion logic and review gates |
| U07 | Team lead supervising juniors | Team lead | Output quality varies by agent | Repeatable packet structure and hard gates |
| U08 | Transaction coordinator | TC | Missing dates, form paths, and signed resolution steps | Deadline, status, and next-document checklist |
| U09 | Listing-side agent | Listing agent | Buyer sends bloated wishlist | Focused safety/system ask and no cosmetic noise |
| U10 | Small inspection company owner | Inspector | Already has request-list tooling | White-label proof only if it adds negotiation judgment |
| U11 | Home inspector using Spectora | Inspector | Agents already use embedded request builders | Differentiation from simple selection/PDF tools |
| U12 | Loan officer on FHA files | Loan officer | Credits and repairs are treated differently by file | Lender-visible caveats and confirmation requirement |
| U13 | Appraiser-adjacent reviewer | Appraisal reviewer | Safety and MPR issues cannot be guessed casually | Avoids definitive lender-required language |
| U14 | First-time buyer in Massachusetts | Buyer | Unsure if $4k credit is reasonable for minor items | Clear "do not lead" list and low-pressure ask |
| U15 | Buyer in a competitive seller market | Buyer | Afraid seller will cancel or refuse | Fallback and seller-pushback script |
| U16 | Buyer with older roof concern | Buyer | Needs to know credit vs repair vs walk-away | Evidence and quote-needed status |
| U17 | Investor buyer | Buyer | Wants numbers but not client education | Fast scope and no over-explaining |
| U18 | Condo buyer | Buyer | HOA/shared components make ask ambiguous | Shared responsibility warning |
| U19 | Relocation buyer's agent | Buyer agent | Remote buyer needs proof and confidence | Client-ready explanation and evidence refs |
| U20 | Broker-owner | Broker | Liability and consistency risk | Disclaimers, audit trail, readiness gates |
| U21 | Texas listing agent | Listing agent | Option period expired but buyer still asks | Not-sendable gate when rights are expired |
| U22 | Colorado transaction lead | TC | Objection/resolution deadline can terminate contract | State-specific objection path and deadline gate |
| U23 | California broker reviewer | Broker | Contingency removal can be mishandled | Form-adjacent warnings without legal completion |
| U24 | Florida agent on AS IS contract | Buyer agent | Buyer can inspect/cancel but request leverage is nuanced | Contract type prompt and AS IS caveat |
| U25 | FHA-heavy buyer agent | Buyer agent | Appraisal-required repairs can derail closing | Financing risk gate and repair-vs-credit warning |
| U26 | VA-heavy buyer agent | Buyer agent | Seller may resist VA-required repairs | VA MPR warning and lender confirmation |
| U27 | Buyer agent in high-cost metro | Buyer agent | National averages can understate scope | Metro basis and quote-needed label |
| U28 | Buyer agent in lower-cost metro | Buyer agent | Overstated numbers can kill credibility | Local cost basis and conservative ask |
| U29 | Seller-side attorney state market | Listing side | Wrong process can create contract risk | Strong "agent/legal form review" gate |
| U30 | New buyer-agent team marketer | Team ops | Needs visible work product to prove value | Sample packet, before/after, client-ready artifact |

## Golden Case Set

Each case below should become a fixture once real inspection reports are available. For now, it is a synthetic domain test. A packet passes only if it scopes the ask, shows what it excluded, labels the evidence status, and avoids overclaiming legal/lender certainty.

| Case | Persona | State | Input pattern | Expected lead scope | Expected exclusions | Required trust gate | Product verdict |
| --- | --- | --- | --- | --- | --- | --- | --- |
| C01 | U01 | GA | Active roof leak, older HVAC near failure, loose fixtures, paint wear | Roof leak + HVAC performance | Fixtures, paint | Evidence + quote-needed | Useful if ask is narrower than full wishlist |
| C02 | U02 | CO | VA buyer, peeling paint, missing handrail, roof leak, objection deadline tomorrow | Safety/MPR-like items + roof | Cosmetic wear | Deadline + lender confirmation | Useful, but only as agent/lender review draft |
| C03 | U03 | TX | Option period ends tonight, foundation crack, GFCI missing, old appliances | Foundation + electrical safety | Working old appliances | Option-period alive | High value if it warns about amendment/termination timing |
| C04 | U04 | CA | Buyer wants seller to repair HVAC, sewer, cosmetic cabinets before contingency removal | HVAC + sewer | Cabinet preferences | CA form path | Useful if it separates RR/AEA handoff from final legal form |
| C05 | U05 | FL | AS IS contract, inspection period open, roof age, active plumbing leak, cosmetic tile | Plumbing leak + roof insurability risk | Tile | AS IS inspection-period caveat | Useful if it avoids saying seller must repair |
| C06 | U06 | NC | Junior agent has 80-line report summary with mixed defects | Top 3 safety/system items | Everything weak/cosmetic | Scope discipline | Strong training value |
| C07 | U07 | AZ | Team lead reviews junior's $35k ask with one real system issue and many minor items | One system issue + fallback | Minor maintenance | Approval before send | Useful if it reduces liability |
| C08 | U08 | CO | Objection sent, seller counter received, resolution deadline approaching | Counter comparison | New demands outside objection | Workflow state | Useful if next step is resolution/termination, not new first ask |
| C09 | U09 | IL | Buyer asks for every old but functioning appliance | Safety defects only if any | Working old appliances | "Do not lead" explanation | Useful as seller-side sanity check |
| C10 | U10 | MO | Inspector wants agent-friendly add-on beyond request list | Scope judgment + PDF proof | Report-writing features | Differentiation from CRL/RRB | Conditional, not first ICP |
| C11 | U11 | CO | Inspector already provides Spectora request list | Negotiation intelligence only | Duplicate request builder | Embedded/white-label gate | Weak as standalone sale |
| C12 | U12 | FL | FHA buyer, seller credit requested for defective steps and roof leak | Items likely needing repair clarity | Cosmetic credits | Lender confirmation | Useful if it says credit may not cure lender condition |
| C13 | U13 | TX | Appraiser flags peeling paint and missing utilities separately from inspection | Appraisal-required items | Buyer preference asks | Lender/appraiser not definitive | Useful only with cautious language |
| C14 | U14 | MA | Clean townhouse, garage reverse sensor, loose sink, old working appliances | Garage safety sensor only | Old working appliances | Low ask or no ask | Very useful if it says not to over-ask |
| C15 | U15 | WA | Competitive backup offer, deck safety and exposed electrical | Deck + electrical | Cosmetic deck stain | Seller pushback fallback | Useful if it balances risk of losing deal |
| C16 | U16 | OR | Roof near end of life, no active leak, insurance concern | Roof verification/quote, not full replacement demand | Cosmetic roof age panic | Quote needed | Useful if conservative |
| C17 | U17 | GA | Investor wants $40k credit for rehab list | Safety/system leverage only | Investor upgrades | Market posture | Low ICP unless packet is fast and terse |
| C18 | U18 | FL | Condo roof/exterior is HOA responsibility; interior leak damage visible | Interior damage + HOA verification | Shared roof replacement ask | Responsibility gate | Useful if it catches shared components |
| C19 | U19 | NC | Remote buyer cannot attend inspection, asks agent to explain risk | Top risks + evidence refs | Long raw report | Client-ready note | Strong if evidence is clear |
| C20 | U20 | CA | Broker audits file before agent sends request | Form path, evidence, buyer approval | Unsupported claims | Audit trail | Strong if gates are strict |
| C21 | U21 | TX | Option period expired; buyer still wants repairs | No new first ask unless contract rights remain | New repair demand | Not sendable | Critical fail if product suggests sending |
| C22 | U22 | CO | Inspection objection filed; no written resolution yet | Preserve rights and deadline | Informal side promises | Resolution deadline | High value if it warns termination/default path |
| C23 | U23 | CA | Buyer signs request language but not contingency removal | Review by broker | Automatic removal assumption | State form caution | Useful if it refuses certainty |
| C24 | U24 | FL | AS IS buyer discovers mold-like staining and old polybutylene piping | Health/plumbing risk + specialist verification | Broad renovation ask | Inspection-period alive | Useful if it supports terminate/credit decision |
| C25 | U25 | PA | FHA buyer, missing handrails, exposed wiring, broken window | Safety/MPR-like repair path | Pure credits without lender check | Financing risk | Strong if it says repair may be required |
| C26 | U26 | VA | VA buyer, failed septic, chipping paint, seller refuses repairs | Septic + paint/MPR concern | Cosmetic upgrades | Lender/MPR confirmation | Strong if it flags close-risk |
| C27 | U27 | CA | High-cost metro, sewer lateral issue, roof leak | Sewer + roof | Cosmetic finishes | Local quote needed | Useful if local basis is visible |
| C28 | U28 | OH | Lower-cost metro, minor electrical, small plumbing, worn carpet | Electrical/plumbing only if safety/system | Carpet | Conservative amount | Useful if numbers do not inflate |
| C29 | U29 | NJ | Attorney review market; buyer wants direct legal language | Scope summary only | Legal notice completion | Attorney/agent review | Useful only with strong role boundary |
| C30 | U30 | AZ | Team wants marketing proof of agent value after inspection | Sample before/after packet | Deep workflow setup | Proof artifact | Useful for acquisition, not core calculation validation |

## Review Result

Synthetic pass likelihood:

- Strong product fit: 17 / 30
- Useful as review/checklist but not final output: 9 / 30
- Weak or wrong first customer: 4 / 30

The strongest cases are not the ones with the most complex repairs. They are the cases where the buyer-agent has to narrow scope under time pressure and explain why the ask is defensible.

The weakest cases are inspector-owned workflows, cash/luxury/investor workflows, and legal-form completion workflows. Those are either already owned by other systems or carry too much process liability for the current product.

## Red-Team Findings

1. The biggest trust risk is not the UI. It is overconfident dollar output.
   The product should keep the opening ask framed as a negotiation range derived from scoped exposure, not as a final repair estimate.

2. The second biggest trust risk is lender language.
   FHA/VA/lender-visible should be labeled as a risk signal that needs confirmation, not a determination.

3. The third biggest trust risk is state-form language.
   The product can say "move this into the relevant objection / amendment / request workflow." It should not imply it has completed the official form.

4. Evidence matching improves trust, but OCR and report parsing can be wrong.
   OCR-backed citations should remain visibly marked and should trigger a review gate.

5. The product should earn trust by excluding items.
   A packet that asks for too much will feel like a generic AI wishlist. A packet that explains why it left items out feels more like an experienced agent.

6. A "Ready to send" label may be too strong without owner, deadline, evidence, form path, and buyer approval.
   If any of these are missing, the product should default to `Draft only`.

7. Current tests prove software behavior, not domain truth.
   They catch regressions in packet structure, evidence display, and readiness gates. They do not prove that the ask amount is market-correct.

## What This Means For Product

The product should be positioned as:

`A buyer-agent inspection negotiation pre-send review tool.`

Not:

`An AI that knows the correct repair credit.`

The next trust layer should add:

- `basis labels` beside every number: inspection-only, data-estimated, quote-backed, contractor-backed,
- `quote-needed` flags when a lead item is carrying too much ask value,
- `state/form review required` label where official document flow matters,
- `lender confirmation needed` label for FHA/VA or lender-visible items,
- `do-not-send reason` when deadline/form/evidence/buyer approval is missing.

Implemented trust layer:

- `Agent pre-send review` now appears in the generated packet.
- It checks number basis, evidence support, seller pushback, financing boundary, form boundary, and send posture.
- This is the intended differentiator from request-list builders: the product should not merely list requested repairs; it should say whether the packet is safe to send.
- The product surface now leads with `Inspection Ask Pre-Send Check` instead of `packet generator`. The packet is the artifact after review, not the category promise.

## Sources

- HomeGauge Create Request List: https://support.homegauge.com/hc/en-us/articles/360057992253-Create-Request-List
- HomeGauge CRL product page: https://www.homegauge.com/one/create-request-list/
- Spectora Repair Request Builder: https://www.spectora.com/r/repair-request-builder/
- InspectForge home inspection software: https://www.inspectforge.com/
- Palmtech request-list positioning: https://www.palmtech.com/palmtech-11-inspector/
- Redfin February 2026 contract cancellations: https://www.redfin.com/news/contract-cancellations-february-2026/
- Redfin contingent offers fall-through article: https://www.redfin.com/blog/how-often-do-contingent-offers-fall-through/
- Rocket Mortgage reasonable requests after inspection: https://www.rocketmortgage.com/learn/reasonable-requests-after-home-inspection
- Rocket Mortgage seller concessions: https://www.rocketmortgage.com/learn/seller-concessions
- Rocket Mortgage after inspection next steps: https://www.rocketmortgage.com/learn/after-home-inspection-what-next
- TREC contracts index: https://www.trec.texas.gov/pdf/contracts
- TREC FAQ on option period and inspection repairs: https://www.trec.texas.gov/special-links/frequently-asked-questions/
- TREC Amendment to Contract Form 39-10: https://www.trec.texas.gov/sites/default/files/pdf-forms/39-10_0.pdf
- C.A.R. investigation contingency guide: https://www.car.org/-/media/CAR/Documents/Transaction-Center/PDF/QUICK-GUIDES/Quick-Guide---The-Investigation-Contingency.pdf
- C.A.R. buyer requests for repairs guide: https://www.car.org/-/media/CAR/Documents/Transaction-Center/PDF/QUICK-GUIDES/Quick-Guide--Buyer-Requests-for-Repairs-REVISED-3822.pdf
- Colorado Inspection Objection Notice: https://dre.colorado.gov/sites/dre/files/documents/Inspection%20Objection%20Notice%20%28fillable%29_for%20use%20on%20or%20after%20January%201%2C%202026.pdf
- HUD/FHA property valuation and repair conditions: https://www.hud.gov/sites/documents/4155-2_4.PDF
- VA Circular 26-24-14: https://www.benefits.va.gov/HOMELOANS/documents/circulars/26-24-14.pdf
- Reddit first-time buyer credit reasonableness example: https://www.reddit.com/r/FirstTimeHomeBuyer/comments/1sderuq/inspection_results_question_about_credits/
- Reddit seller-credit pushback example: https://www.reddit.com/r/FirstTimeHomeBuyer/comments/1poz2j5/sellers_agent_offended_by_inspection_credit/
- Reddit Texas option-period timing example: https://www.reddit.com/r/FirstTimeHomeBuyer/comments/1sedb2s/option_period_expired_even_at_the_request_for_the/
