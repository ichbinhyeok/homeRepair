---
description: pSEO 피봇 및 대규모 확장 (4,000+ 페이지) 실행 계획
---

# 🚀 pSEO Pivot & Expansion Master Plan

## 1. Strategy Overview
- **Target**: High-Intent Home Buyers ("Is this old house safe?")
- **Scale**: ~4,200 Pages (702 Hubs + 3,500 Specific Risks)
- **SEO Strategy**: "Seed & Sprout"
  - Submit 702 Level 1 pages via Sitemap (The Seed)
  - Interlink 3,500 Level 2 pages naturally (The Sprout)

## 2. Directory Structure
```
/home-repair/verdicts/
  ├── abilene-tx/
  │     ├── pre-1950/
  │     │     ├── index.html           (Level 1 Hub)
  │     │     ├── knob-and-tube.html   (Level 2 Risk)
  │     │     ├── lead-paint.html      (Level 2 Risk)
  │     │     └── galvanized-pipe.html (Level 2 Risk)
  │     └── 1950-1970/
  │           └── ...
  └── ...
```

## 3. Execution Phase

### Phase 1: Template Engineering (Design First)
- [ ] **Step 1.1**: Refactor Level 1 Template (`static-verdict.jte`)
  - [x] Analyze `risk_factors_by_year.json` structure
  - [ ] Implement "Buyer Fear/Reality" Hook
  - [ ] Add dynamic internal links to Level 2 pages
- [ ] **Step 1.2**: Create Level 2 Template (`static-risk-detail.jte`)
  - [ ] Focus on specific problem + local cost
  - [ ] "Why this matters in [City]" section

### Phase 2: Build System Upgrade
- [ ] **Step 2.1**: Update Page Generation Logic in `build.gradle` (or Java Task)
  - [ ] Implement nested loop: City -> Era -> Risk Factors
  - [ ] Ensure unique filenames/slugs
- [ ] **Step 2.2**: Optimize linking structure
  - [ ] Ensure Level 2 points back to Level 1
  - [ ] Ensure Level 2 points to related Level 2s

### Phase 3: Core UX Improvements
- [ ] **Step 3.1**: Fix Root Page (`hub.jte`)
  - [ ] Merge into single product landing page
- [ ] **Step 3.2**: Update Calculator Form (`index.jte`)
  - [ ] Default to "Buying"
  - [ ] Update SEO copy to match "Buyer" intent

### Phase 4: Deployment & Indexing
- [ ] **Step 4.1**: Generate Sitemap.xml (Level 1 Only)
- [ ] **Step 4.2**: Verify internal link graph
- [ ] **Step 4.3**: Deploy

## 4. Key Metrics to Watch
- **Indexing Rate**: How fast does Google find Level 2 pages?
- **CTR**: Are "Specific Risk" titles getting clicks?
- **Conversion**: Traffic -> Calculator usage
