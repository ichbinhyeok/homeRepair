# Design System - LifeVerdict

## Product Context
- **Version:** v2. v1 was the broad repair-cost pSEO product and should not guide current UI decisions.
- **What this is:** An inspection ask pre-send check that turns report findings into a seller-credit ask, fallback posture, cut list, and agent-ready negotiation packet.
- **Who it's for:** Buyers under contract and buyer agents working inside the inspection response window.
- **Space/industry:** Residential real-estate transaction support, inspection negotiation, buyer decision support.
- **Project type:** Tool-first web app with supporting trust/legal/reference pages.

## Aesthetic Direction
- **Direction:** Transaction dossier
- **Decoration level:** Intentional
- **Mood:** Calm, high-stakes, evidence-forward. It should feel closer to a case file or negotiation desk than a home-improvement blog or contractor directory.
- **Reference principle:** The product should visually communicate deadline, defensibility, and sendability before it communicates "home repair."

## Typography
- **Display/Hero:** `Fraunces`
  Why: It gives the artifact weight and authority without feeling like a generic SaaS dashboard.
- **Body/UI:** `Instrument Sans`
  Why: Clean, readable, modern, and less interchangeable than Inter/Roboto.
- **Data/Labels:** `IBM Plex Mono`
  Why: Best for evidence labels, packet refs, and small metadata where precision matters.
- **Scale:**
  - Hero: `clamp(3rem, 7vw, 5.4rem)`
  - H1: `clamp(2.4rem, 5vw, 4rem)`
  - H2: `clamp(1.55rem, 3vw, 2.3rem)`
  - H3: `1.15rem`
  - Body: `1rem`
  - Small/meta: `0.82rem`

## Color
- **Approach:** Restrained with one warm signal accent.
- **Canvas:** `#f4ede3`
- **Paper:** `#fffaf4`
- **Ink:** `#10202a`
- **Muted:** `#5f6d74`
- **Primary:** `#174a5a`
- **Primary hover:** `#103845`
- **Accent / action warmth:** `#c96a3d`
- **Line/border:** `rgba(16, 32, 42, 0.10)`
- **Success:** `#1f6a4b`
- **Warning:** `#a36117`
- **Danger:** `#b14a2b`
- **Dark mode:** Not a priority surface. If added later, preserve the paper/ink contrast model instead of using neon SaaS defaults.

## Spacing
- **Base unit:** `8px`
- **Density:** Comfortable but tight enough to feel operational.
- **Scale:** `4 / 8 / 12 / 16 / 24 / 32 / 48 / 72`

## Layout
- **Approach:** Hybrid
- **Grid:** Bold editorial hero + disciplined tool/support panels underneath.
- **Max widths:**
  - Marketing/tool shell: `1240px`
  - Support/legal reading width: `1120px`
  - Reading column inside support cards: `72ch`
- **Radius scale:**
  - small `14px`
  - medium `22px`
  - large `32px`

## Motion
- **Approach:** Minimal-functional
- **Intent:** Slight lift, shadow, and color response for affordances. No decorative choreography.
- **Durations:**
  - micro `100ms`
  - short `180ms`
  - medium `260ms`

## Rules
- Do not describe the product using the previous repair-cost directory framing.
- Do not design v2 like a blog, directory, cost calculator, or contractor marketplace.
- Do not leak internal transition language like "old surface/new surface" into user-facing hero copy.
- Do not use generic home-improvement imagery or renovation-blog tropes.
- Always bias the interface toward the artifact: ask range, evidence, fallback, agent note.
- Support pages must read like trust infrastructure for the packet product, not for a broad repair estimator.

## Decisions Log
| Date | Decision | Rationale |
|------|----------|-----------|
| 2026-04-23 | Adopted the "transaction dossier" system | The product needs to feel like a high-stakes negotiation tool, not a repair-cost content site. |
