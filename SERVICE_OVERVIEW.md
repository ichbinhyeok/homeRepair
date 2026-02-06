# 🦅 SERVICE OVERVIEW: LifeVerdict (Home Repair Engine)

> **⚠️ ATTENTION AI AGENTS:**
> This document serves as the **SINGLE SOURCE OF TRUTH** for the project's vision, architecture, and strategic intent. Read this before modifying core logic or SEO strategies.

---

## 1. VISION & PHILOSOPHY (Why this exists)

### The Problem: Information Asymmetry
Legacy home repair calculators are fundamentally broken. They rely on generic square footage multipliers (e.g., "$150 per sqft") that ignore the three critical dimensions of risk:
1.  **Era (Year Built):** A 1920s Victorian has different failure points (Knob & Tube, Lead) than a 1990s Spec House (Polybutylene, Builder Grade).
2.  **Geography (Metro):** Repair costs in **San Francisco** are ~3x higher than in **Memphis**.
3.  **Specific Defects:** "Hidden Killers" like FPE Panels, Aluminum Wiring, or Chinese Drywall are not "renovation costs"—they are **bankruptcy risks**.

### The Solution: "The Verdict"
We are not building a calculator. We are building a **Forensic Risk Engine**.
*   **Actuarial Approach:** We treat homes like financial assets with specific risk profiles.
*   **The Verdict:** Instead of just a price tag, we output a **"Verdict Tier"** (e.g., *Money Pit*, *Manageable*, *Safe Bet*) based on probabilistic modeling.
*   **Target Audience:** Homebuyers of "Fixer-Uppers", Investors, and homeowners needing objective data to negotiate.

---

## 2. CORE ARCHITECTURE

### Tech Stack
*   **Backend:** Java 21, Spring Boot 3.5.x
*   **Template Engine:** **JTE (Java Template Engine)** via `gg.jte`.
    *   *Why JTE?* Selected for extreme performance (compiled to Java classes) and type safety, crucial for pSEO at scale.
*   **Database:** H2 (In-memory for Dev/MVP), JPA/Hibernate.
*   **Build System:** Gradle (Kotlin DSL).

### Key Components
1.  **`VerdictEngineService`**: The brain. Takes `UserContext` (Zip, Era, Condition) and calculates the financial verdict.
2.  **`HomeRepairController`**: The consolidated web controller. Handles:
    *   Main UI (`/`)
    *   Verdict Generation (`/verdict`)
    *   Static Pages (`/about`, `/methodology`)
    *   *merged from legacy PageController to avoid mapping conflicts.*
3.  **`StaticPageGeneratorService`**: The pSEO factory. Generates thousands of static HTML files for SEO.
    *   Uses `StateHubPage` DTO for type-safe template rendering.

---

## 3. SEO STRATEGY (Programmatic SEO)

**Strategy Codename: "Seed & Sprout"**

We rejected the "Mass Spam" approach (generating 30k pages instantly) to avoid the Google Sandbox.

### Phase 1: The Seed (Current Status: ✅ DONE)
*   **What:** Generated **702** High-Quality "Level 1" pages (117 Metros × 6 Eras).
*   **Format:** Physical Static HTML Files (`/verdicts/{city}/{era}.html`).
*   **Hubs:** Created **40** State Hub Pages (`/verdicts/states/tx.html`) to structure internal linking.
*   **Goal:** Establish trusted "Authority Anchors" in the index.

### Phase 2: The Sprout (Next Step)
*   **What:** The ~28,000 "Level 2" pages (Specific Risks like "Roofing in Abilene 1950s").
*   **Format:** **Dynamic Rendering**.
    *   Instead of creating 28k files, we will serve these via a Controller endpoint that catches the URL pattern and renders the JTE template on-the-fly.
*   **Goal:** Efficiently scale long-tail keywords without "Empty Calorie" file bloat.

---

## 4. CRITICAL IMPLEMENTATION DETAILS

### 1. JTE Template Safety
*   **DTOs are Mandatory:** Do NOT pass raw `Map<String, Object>` to JTE templates. It causes compilation errors and runtime risks.
*   **Use `StateHubPage.java`** (and similar DTOs) ensuring fields are `@Data` and `@AllArgsConstructor`.

### 2. Controller Routing
*   All pSEO pages are routed through `HomeRepairController` or served statically from `src/main/resources/static`.
*   **Do not create new Controllers** for static content unless absolutely necessary; we recently refactored to specific consolidation.

### 3. Data Integrity
*   **RSMeans & BLS Data:** Hardcoded/Configured in `VerdictEngineService`.
*   **Era Definitions:** STRICTLY defined enums (`PRE_1950`, `1950_1970`, etc.). Do not use arbitrary date ranges.

---

## 5. AGENT INSTRUCTIONS (How to work on this repo)

1.  **Check Previous Context:** Always look at `.agent/strategy/` to understand the current strategic phase.
2.  **Run with Gradle:** Use `./gradlew bootRun` to start.
3.  **Process Management:** If build hangs, it's likely a daemon lock. Use `taskkill /F /IM java.exe` (Windows) to clear.
4.  **Verification:** When modifying pSEO logic, ALWAYS verify generation by checking `src/main/resources/static/home-repair/verdicts` before committing.

---
*Document Last Updated: 2026-02-06*
