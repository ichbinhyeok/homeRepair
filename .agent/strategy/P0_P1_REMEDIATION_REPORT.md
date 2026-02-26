# P0 / P1 개선 보고서 — LifeVerdict Home Repair pSEO

> **작성일**: 2026-02-26  
> **기준**: 코드베이스 전체 검증 (Java 25개 클래스, JTE 22개 템플릿, 742개 정적 HTML)  
> **목적**: 매출/인덱싱/신뢰에 직접 영향을 미치는 최우선 수정 항목 정리

---

## 🔴 P0 — 즉시 수정 (고치지 않으면 인덱싱/수익에 마이너스)

### P0-1. L1 내부링크 404 문제 (URL 정규화)

**현상**: `InternalLinkBuilder.buildVerdictUrl()`이 `.html` 없이 URL을 생성하는데,  
L1에는 컨트롤러 라우트가 없고 정적 파일만 `{era}.html`로 존재함.

**영향 범위**: L1 페이지 안의 "같은 도시 다른 연식", "인근 도시", L2→L1 백링크 전부

| 링크 소스 | 생성 URL | 실제 파일 | 결과 |
|---|---|---|---|
| `InternalLinkBuilder.buildVerdictUrl()` | `/verdicts/{metro}/{era}` | `{era}.html` | ❌ **404** |
| `InternalLinkBuilder.buildRiskUrl()` | `/verdicts/{metro}/{era}/{risk}` | 컨트롤러 동적 | ✅ OK |
| `buildL1LinksForRiskHub()` 하드코딩 | `/verdicts/{slug}` | `{era}.html` | ❌ **404** |
| `HomeRepairController.viewRiskDetail` parentUrl | `/verdicts/{metro}/{era}` | `{era}.html` | ❌ **404** |
| State Hub 내부링크 | `.html` 포함 (buildCanonicalUrl) | `{era}.html` | ✅ OK |
| State Index 링크 | `states/{state}.html` | `{state}.html` | ✅ OK |

**해결**: 선택지 2개 중 하나

- **(A) 빠른 수정**: `InternalLinkBuilder.buildVerdictUrl()`에 `.html` suffix 추가  
  + `buildL1LinksForRiskHub()` 하드코딩 링크에도 `.html` 추가  
  + `HomeRepairController.viewRiskDetail()`의 `parentUrl`에도 `.html` 추가

- **(B) 깔끔한 수정**: `/verdicts/{metro}/{era}` 컨트롤러 라우트 추가 →  
  `.html` canonical로 301 리다이렉트 (L2에서 이미 하고 있는 패턴)

**추천**: **(A)** — 변경량이 적고 정적 파일 서빙의 성능 이점을 유지함

---

### P0-2. Canonical URL 불일치 (중복 URL 위험)

**현상**: 같은 L1 페이지에 대해 URL이 2가지 형태로 존재할 수 있음

| 출처 | canonical | `.html`? |
|---|---|---|
| `StaticPageGeneratorService.buildCanonicalUrl()` | `.html` 포함 | ✅ |
| `SitemapGenerator.buildVerdictUrl()` | `.html` 포함 | ✅ |
| 정적 HTML 파일의 `<link rel="canonical">` | `.html` 포함 | ✅ |
| `InternalLinkBuilder` 내부링크 | `.html` **없음** | ❌ |

**영향**: 크롤러가 `.html` / non-`.html` 을 별개 URL로 취급 → 페이지랭크 분산, 인덱싱 혼란

**해결**: P0-1과 동시에 해결됨. 내부링크를 `.html`로 통일하면 canonical과 일치.

---

### P0-3. 가짜 `generateProductSchema()` 코드 잔재 삭제

**현상**: `StaticPageGeneratorService.generateProductSchema()`에 **가짜 리뷰 생성 코드**가 남아있음

```java
String rating = String.format("%.1f", 4.5 + (new Random(m.hashCode()).nextDouble() * 0.5));
String reviewCount = String.valueOf(50 + new Random((m + e).hashCode()).nextInt(150));
```

현재는 `templateData.put("productSchema", "")` 로 비활성화됐지만 **메서드 자체가 존재**.

**영향**: 
- 누군가 실수로 재활성화하면 **Google Schema Spam 수동 조치** 대상
- 코드 리뷰 시 "이 사이트는 가짜 리뷰를 만든다"는 인상

**해결**: `generateProductSchema()` 메서드 전체 삭제 + `@param String productSchema` 템플릿 파라미터 제거

---

### P0-4. Canonical URL 경로 오류 (Info Pages)

**현상**: Methodology, About, Editorial Policy 페이지의 canonical URL에 `/home-repair/` prefix가 빠짐

| 페이지 | canonical (현재) | 실제 URL |
|---|---|---|
| methodology.jte | `${baseUrl}/methodology` | `/home-repair/methodology` |
| about.jte | `${baseUrl}/about` | `/home-repair/about` |
| editorial-policy.jte | `${baseUrl}/editorial-policy` | `/home-repair/editorial-policy` |

**영향**: `baseUrl`이 `https://lifeverdict.com`이면 canonical이 `https://lifeverdict.com/methodology`가 되는데,  
실제 라우트는 `/home-repair/methodology`임. → **canonical 불일치 = 인덱싱 오류**

**해결**: canonical에 `/home-repair/` prefix 추가:
- `${baseUrl}/home-repair/methodology`
- `${baseUrl}/home-repair/about`
- `${baseUrl}/home-repair/editorial-policy`

---

### P0-5. Header "Browse by State" 링크가 TX 하드코딩

**현상**: `_header.jte` 라인 49:
```html
<a href="/home-repair/verdicts/states/tx.html">Browse by State</a>
```

**영향**: 전체 State Index(`/home-repair/verdicts/states`)로 가야 하는 네비게이션이 Texas 페이지로만 가고 있음.  
다른 주 사용자는 네비게이션이 무의미해짐.

**해결**: href를 `/home-repair/verdicts/states`로 변경

---

## 🟡 P1 — 조기 수정 (SEO 품질/신뢰/수익에 영향)

### P1-1. 에러 핸들링에서 Stacktrace 노출

**현상**: `HomeRepairController.generateVerdict()` 라인 153-157:
```java
model.addAttribute("errorMessage", "DEBUG ERROR: " + e.toString() + "\nAT: " + stackTrace);
```

`GlobalExceptionHandler`는 안전한 메시지를 반환하지만, `generateVerdict()`는 **직접 catch에서 상세 정보를 노출**.

**영향**: 내부 클래스 구조, 패키지명 등이 사용자에게 보임 (보안 취약점)

**해결**: `"An error occurred while generating your verdict. Please try again."` 같은 일반 메시지로 교체

---

### P1-2. SEO 템플릿 간 CSS 불일치

**현상**: 

| 템플릿 | CSS 파일 |
|---|---|
| `static-verdict.jte` | `/css/seo-report.css` |
| `static-risk-detail.jte` | `/css/seo-report.css` |
| `state-hub.jte` (동적) | `/css/styles.css` |
| `methodology.jte` | `/css/styles.css` |
| `about.jte` | `/css/styles.css` |

**영향**: State Hub 동적 렌더 페이지가 정적 State Hub와 다른 스타일을 사용.  
사용자가 State Index → State Hub → L1으로 이동할 때 **UI가 갑자기 바뀌면 신뢰도 하락**.

**해결**: 모든 SEO/Trust 페이지를 `seo-report.css`로 통일

---

### P1-3. Risk Hub FAQ가 Generic (데이터 미주입)

**현상**: `risk-hub.jte`의 FAQ Schema/본문이 **전체 리스크에 동일한 텍스트** 사용:

```json
"text": "Costs vary significantly depending on region and severity, 
         but it ranges between $1,500 to over $15,000."
```

5개 Risk Hub(knob-and-tube, polybutylene, FPE, asbestos, galvanized) 전부 **같은 답변**.

**영향**: 
- Google이 "thin/duplicate content"으로 판단할 수 있음
- AEO에서 인용 가치 없음 (구체적인 데이터가 없으므로)

**해결**: 각 `riskSlug`별로 고유한 비용 범위, 특성, 검사 방법을 매핑하여 주입

---

### P1-4. CSV 리드 저장 동시성 문제

**현상**: `HomeRepairController.captureLead()` 라인 459-478:
```java
try (java.io.FileWriter fw = new java.io.FileWriter(file, true);
     java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
```

`synchronized` 없이 파일에 append. 동시 요청 시 **데이터 깨짐** 가능.

**해결**: 
- 즉시: `synchronized` 블록 추가
- 중기: H2 DB의 EventLog 테이블로 통합 (이미 EventLog 엔티티/레포지토리 있음)

---

### P1-5. L1 → L2 "View Details" 링크의 일관성

**현상**: `static-verdict.jte` 라인 279:
```html
<a href="/home-repair/verdicts/${metroCode...}/${era...}/${item.getItemCode()...}">
```

이 링크는 `.html` 없이 올바르게 생성됨 (L2 컨트롤러가 처리).  
**그러나** `metroCode`가 대문자+언더스코어 원본이고 `era`도 마찬가지라서 **slug 변환이 템플릿에서 직접 수행됨**.

만약 변환 로직이 `InternalLinkBuilder.buildRiskUrl()`과 다르면 **링크 불일치** 발생.

**해결**: 템플릿에서 직접 slug 변환하지 말고, Java에서 미리 URL 리스트를 만들어서 전달

---

### P1-6. Google Analytics 태그 빠진 페이지

**현상**: 확인 결과 `state-index.jte`와 `risk-index.jte`에는 **GA 태그가 없음**.

| 템플릿 | GA 태그 | 
|---|---|
| static-verdict.jte | ✅ |
| static-risk-detail.jte | ✅ |
| static-state-hub.jte | ✅ |
| state-hub.jte (동적) | ✅ |
| **state-index.jte** | ❌ |
| **risk-index.jte** | ❌ |
| methodology.jte | ✅ |
| about.jte | ✅ |

**영향**: State Index, Risk Index 페이지의 트래픽이 GA에서 잡히지 않음

**해결**: 해당 페이지에 GA snippet 추가

---

### P1-7. About 페이지의 "No Conflicts of Interest" 톤 충돌

**현상**: `about.jte` 라인 123-127:
> "We do not accept payments from contractors, material suppliers, or inspection services.  
> Our verdicts are never influenced by affiliate commissions or advertising relationships."

vs `disclaimer.jte` 라인 89:
> "All affiliate links are labeled with `rel="nofollow sponsored"`"

vs 코드의 `ALLOWED_DOMAINS` = angi.com, homeadvisor.com, thumbtack.com...

**영향**: "우리는 제휴 관계가 없다"고 About에서 말하면서, Disclaimer에서 제휴 링크를 인정하고 있음.  
**E-E-A-T에서 일관성은 핵심 신호**임.

**해결**: About 페이지 문구를 다음과 같이 수정:
> "Our analysis methodology and risk scoring are never influenced by commercial relationships.  
> To sustain free access, we may earn referral fees from partner services — but these do not  
> affect which items appear, their risk ratings, or their cost estimates."

---

## 📋 실행 순서 요약

| 순서 | 항목 | 난이도 | 영향 |
|---|---|---|---|
| 1 | **P0-1** L1 내부링크 `.html` 추가 | 쉬움 (3줄 수정) | 인덱싱 + 내부링크 구조 복구 |
| 2 | **P0-4** Info Pages canonical 경로 수정 | 쉬움 (3줄 수정) | canonical 정합성 |
| 3 | **P0-5** Header 네비게이션 링크 수정 | 쉬움 (1줄 수정) | UX/내부링크 |
| 4 | **P0-3** 가짜 ProductSchema 삭제 | 쉬움 (30줄 삭제) | Schema Spam 방지 |
| 5 | **P1-1** Stacktrace 노출 제거 | 쉬움 (2줄 수정) | 보안 |
| 6 | **P1-6** 빠진 GA 태그 추가 | 쉬움 (템플릿 2개) | 분석 정확도 |
| 7 | **P1-7** About 톤 수정 | 쉬움 (카피 수정) | E-E-A-T 일관성 |
| 8 | **P1-2** CSS 통일 | 중간 | 디자인 일관성 |
| 9 | **P1-3** Risk Hub FAQ 개별화 | 중간 | 콘텐츠 품질 |
| 10 | **P1-4** CSV 동시성 수정 | 중간 | 데이터 무결성 |
| 11 | **P1-5** L2 링크 생성 로직 통합 | 중간 | 코드 품질 |

---

> **Note**: P0 항목 1~4번은 작업 시간 합산 ~30분 이내.  
> 전부 "정적 생성 재빌드" 전에 반영해야 합니다.
