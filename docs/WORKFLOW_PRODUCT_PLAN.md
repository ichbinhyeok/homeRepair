# Workflow Product Plan

## Version Context

v1 was a broad repair-cost surface. It did not create enough product identity or commercial intent because it looked like another content directory.

v2 is the workflow version: an inspection ask pre-send check that can grow into a buyer-agent workspace after the packet proves useful.

## Raw-Data-First Thesis

Ignoring SEO residue and current UI shape, the source data is best suited for a `repair-risk decision engine`, not a generic repair-content site.

The strongest first product is:

`inspection report -> scoped negotiation packet -> reopenable transaction workspace`

## Business Modes Ranked

1. `Buyer agent workspace`
2. `Inspector white-label follow-up packet`
3. `Lender / insurer / transaction API`

The consumer landing page is the wedge, not the final business model.

## Product Rule

If a change does not make the artifact easier to reopen, defend, share, or send in a live transaction, it is secondary.

## Execution Phases

### Phase 1: Case Workspace
- Capture case metadata: property, buyer, agent, packet label.
- Keep a reopenable list of recent packets.
- Make the result read like a live deal file rather than a one-time result page.

### Phase 2: Agent Workflow
- Separate client-facing summary from agent-facing packet.
- Add case status and response-stage tracking.
- Add shareable links and print/export formats that look document-grade.

### Phase 3: Reuse And Team Adoption
- Reopen and revise packets across the same deal.
- Track evidence completeness and negotiation posture.
- Support inspector and buyer-agent handoff.

### Phase 4: Embedded Distribution
- White-label inspector output.
- Agent-team account workflow.
- API access for embedded underwriting or transaction use cases.

## Immediate Build Slice

This iteration implements the start of Phase 1:

- `deal file metadata` on intake
- `recent workspace` list
- `case workspace` framing on the result packet

That moves the product one step away from `calculator` and one step toward `workflow software`.
