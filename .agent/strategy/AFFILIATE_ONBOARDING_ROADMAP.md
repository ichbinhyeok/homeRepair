---
title: "homeRepair 제휴(어필리에이트) 체결 가이드 & 90일 로드맵"
date: "2026-02-25"
author: "AI Assistant"
purpose: "한 달 뒤 오가닉 트래픽 확인 시점에 이 문서만 보고 즉시 실행할 수 있는 체크리스트"
---

# 🗺️ homeRepair 제휴 체결 가이드 & 90일 로드맵

> **이 문서의 목적:** 한 달 뒤(혹은 오가닉 트래픽이 일정 수준 도달했을 때) 돌아와서,
> "누구랑 맺어야 하지? 어디서 가입하지? 코드 어디를 고치지?"를 
> **이 문서 하나만 보고** 30분 안에 시작할 수 있도록 정리한 실전 가이드입니다.

---

## 📊 제휴 시작 전 확인할 선행 조건 (Go/No-Go)

| 항목 | 기준 | 확인 방법 |
|---|---|---|
| 월간 오가닉 세션 | 최소 500+ (이상적: 1,500+) | Google Analytics (GA4) |
| 주요 트래픽 의도 | BUYING이 50% 이상 | GA의 `/home-repair/verdict` 이벤트 or `/track` 이벤트 로그 |
| P0 버그 수정 완료 | `/track` 화이트리스트 해제됨 | 브라우저에서 `/track?target=https://angi.com` 테스트 |
| Editorial Policy 정합성 | "제휴 링크 포함 가능" 문구 반영 | About/Disclaimer 페이지 육안 확인 |

> ❌ 위 4개 중 하나라도 미완이면 제휴 가입해도 승인 안 되거나 돈이 안 들어옵니다.

---

## 🏢 어디서 가입해야 하나? (제휴 플랫폼 목록)

### 1순위: 수리 견적 / 시공 리드 (메인 수익원)

| 플랫폼 | 가입 URL | 수익 모델 | 예상 CPL | 비고 |
|---|---|---|---|---|
| **Angi (구 Angie's List)** | https://www.angi.com/research/partnerships/ | 리드당 (CPL) | $15~$60 | 카테고리별(roof/plumbing 등) 분리 가능 |
| **HomeAdvisor** | https://pro.homeadvisor.com/partnerships/ | 리드당 (CPL) | $15~$50 | Angi와 같은 그룹. 둘 다 가입 가능 |
| **Thumbtack** | https://www.thumbtack.com/partners/ | 마켓플레이스 전송 | $10~$30 | 카테고리 폭이 넓음 (점검/견적도 가능) |
| **Networx** | https://www.networx.com/affiliate.html | 리드당 (CPL) | $10~$25 | 심사 비교적 느슨함 |

### 2순위: CJ/ShareASale 등 네트워크 경유 (다양한 오퍼)

| 네트워크 | 가입 URL | 내부 검색어 | 추천 오퍼 |
|---|---|---|---|
| **CJ Affiliate** | https://www.cj.com/ | "home warranty", "home improvement" | Choice Home Warranty, Select 등 |
| **ShareASale** | https://www.shareasale.com/ | "home", "warranty" | 다수의 홈서비스 업체 |
| **Impact** | https://impact.com/ | "home services" | 다양한 B2C 브랜드 |

### 3순위: 백업/보조 수익원 (이메일 수집 후 활용)

| 용도 | 서비스 | 비고 |
|---|---|---|
| 이메일 마케팅 | Mailchimp Free / Brevo | 수집한 이메일로 후속 오퍼 발송 |
| PDF 리포트 결제 | Stripe / Gumroad | 소액 PDF 판매 테스트용 |

---

## 🔧 가입 후 코드 어디를 고쳐야 하나? (파일별 액션)

### Step 1: 제휴 링크 받기
각 플랫폼 가입 후 받게 되는 **제휴 링크(Affiliate URL)**를 메모합니다.
- 예: `https://www.angi.com/companylist/roofing.htm?aff=YOUR_ID`
- 예: `https://www.homeadvisor.com/ext/YOUR_ID?service=plumbing`

### Step 2: `/track` 엔드포인트에 제휴 URL 등록

**파일:** `src/main/java/com/livingcostcheck/home_repair/web/HomeRepairController.java`
**메서드:** `isValidTarget(String target)`

```java
// 변경 전 (하드코딩 — P0에서 이미 수정했어야 함)
return target.startsWith("https://example.com") ||
       target.startsWith("http://localhost");

// 변경 후 (제휴 파트너 도메인 허용)
private static final List<String> ALLOWED_DOMAINS = List.of(
    "angi.com", "homeadvisor.com", "thumbtack.com",
    "networx.com", "shareasale.com", "cj.com"
);

private boolean isValidTarget(String target) {
    if (target == null) return false;
    return ALLOWED_DOMAINS.stream().anyMatch(d -> target.contains(d));
}
```

### Step 3: result.jte에 제휴 CTA 링크 실제 URL 삽입

**파일:** `src/main/jte/pages/result.jte`
**위치:** BUYING 분기(`"BUYING".equals(history.getPurpose())`) 아래의 CTA 영역

```html
<!-- BUYING: 협상 카드 아래 -->
@if("BUYING".equals(history.getPurpose()))
    <a href="/home-repair/track?target=https://angi.com/YOUR_ROOF_LINK&cat=roof&src=buying"
       class="btn-primary">
        Get a Local Roofing Quote →
    </a>
@else
    <!-- LIVING: 전략 블록 아래 -->
    <a href="/home-repair/track?target=https://homeadvisor.com/YOUR_PLUMBING_LINK&cat=plumbing&src=living"
       class="btn-primary">
        Find a Top-rated Plumber →
    </a>
@endif
```

> ⚡ 실제로는 `primaryCostDriver`를 읽어서 카테고리를 동적으로 결정해야 합니다.
> 위는 가장 단순한 하드코딩 예시입니다.

### Step 4: 리스크별 카테고리 → 제휴 URL 매핑 설정

**파일 (신규 생성 권장):** `src/main/resources/affiliate-links.yml`

```yaml
affiliate:
  links:
    ROOF_SHINGLES:
      buying: "https://angi.com/companylist/roofing.htm?aff=YOUR_ID"
      living: "https://homeadvisor.com/task.roofing-repair.YOUR_ID"
    PLUMBING_SUPPLY:
      buying: "https://thumbtack.com/k/plumbers/YOUR_ID"
      living: "https://angi.com/companylist/plumbing.htm?aff=YOUR_ID"
    ELECTRICAL_PANEL:
      buying: "https://homeadvisor.com/task.electrician.YOUR_ID"
      living: "https://homeadvisor.com/task.electrician.YOUR_ID"
    FOUNDATION:
      buying: "https://angi.com/companylist/foundation-repair.htm?aff=YOUR_ID"
      living: "https://angi.com/companylist/foundation-repair.htm?aff=YOUR_ID"
    HVAC:
      buying: "https://thumbtack.com/k/hvac-repair/YOUR_ID"
      living: "https://thumbtack.com/k/hvac-repair/YOUR_ID"
  default: "https://angi.com/?aff=YOUR_ID"  # 매핑 안 되는 리스크의 폴백
```

### Step 5: L1 정적 페이지 CTA 교체

**파일:** `src/main/jte/seo/static-verdict.jte`
**위치:** Line 99~101 (`Check Specific Address` 버튼), Line 302~310 (Pre-emptive CTA)

```
변경 전: "Check Specific Address →"  /  "Run Free Address Check"
변경 후: "Customize Your Estimate →"  /  "Get an Exact Estimate"
```

**파일:** `src/main/jte/seo/static-risk-detail.jte`
**위치:** Line 173~180 (하단 CTA)

```
변경 전: "Run Address Check →"
변경 후: "Get a Detailed Quote →"  (해당 리스크 카테고리의 제휴 링크로 연결)
```

---

## 📅 90일 로드맵

### Phase 1: 기반 구축 (Day 1~14) — 지금 즉시

| 순서 | 작업 | 파일 | 완료 기준 |
|---|---|---|---|
| 1 | Address 허위 카피 삭제 | `static-verdict.jte`, `static-risk-detail.jte` | "Address" 단어 0건 |
| 2 | `/track` 화이트리스트 해제 | `HomeRepairController.java` | 외부 URL 리다이렉트 정상 |
| 3 | `.html` 리다이렉트 홉 제거 | `InternalLinkBuilder.java` | L2 링크에 `.html` 0건 |
| 4 | `/api/lead` 보상 추가 | `HomeRepairController.java` | 이메일 제출 → PDF 링크 반환 |
| 5 | Editorial Policy 수정 | About/Disclaimer 페이지 | "제휴 링크 포함 가능" 명시 |
| 6 | BUYING/LIVING CTA 분기 추가 | `result.jte` | 각 분기 아래 CTA 버튼 확인 |

### Phase 2: 관찰 (Day 15~30) — 트래픽 수집

| 순서 | 작업 | 확인 방법 |
|---|---|---|
| 1 | GA에서 오가닉 세션 수 확인 | GA4 대시보드 |
| 2 | `/track` 이벤트 로그에서 CTA 클릭 횟수 확인 | DB의 `EventLog` 테이블 |
| 3 | BUYING vs LIVING 비율 확인 | `VerdictHistory` 테이블의 `purpose` 컬럼 집계 |
| 4 | 어떤 카테고리(roof/plumbing 등)가 클릭 많은지 확인 | `/track?cat=XXX` 파라미터 집계 |

> 🎯 **이 시점에서의 Go/No-Go 판단:**
> - 월 500+ 세션 & CTA 클릭률 3%+ → Phase 3 진입 (제휴 가입)
> - 월 500 미만 → SEO/콘텐츠 확장에 집중 (P3: Risk Hub 확장 등)

### Phase 3: 제휴 체결 & 수익 시작 (Day 31~60) — 이 문서의 핵심

| 순서 | 작업 | 소요 시간 |
|---|---|---|
| 1 | Angi/HomeAdvisor 파트너 프로그램 가입 신청 | 30분 |
| 2 | CJ/ShareASale 퍼블리셔 가입 | 30분 |
| 3 | 승인 후 제휴 링크(URL) 수령 | 1~7일 (심사) |
| 4 | `affiliate-links.yml` 작성 (위 Step 4 참고) | 20분 |
| 5 | `result.jte`의 CTA href를 실제 제휴 URL로 교체 | 30분 |
| 6 | 브라우저 테스트: CTA 클릭 → 플랫폼 폼 도착 확인 | 10분 |
| 7 | 1주일간 실제 수익 발생 여부 모니터링 | 매일 5분 |

### Phase 4: 최적화 & 확장 (Day 61~90)

| 순서 | 작업 |
|---|---|
| 1 | 어떤 카테고리(roof/plumbing)가 CPL 가장 높은지 데이터 분석 |
| 2 | 전환율 낮은 CTA의 카피라이팅 A/B 테스트 |
| 3 | 수익이 검증된 템플릿(코드)을 락인(Lock-in)하여 다른 도메인/사이트에 복제 |
| 4 | Risk Hub 확장 (L2 카테고리 5개 → 15개) |

---

## 💵 수익 시뮬레이션 (최종 정리)

### 수익 구조 요약
```
사용자 방문 → 리스크 분석 결과 확인 → CTA 클릭 → 제휴 플랫폼 폼 작성 → 우리가 수수료 수령
```

### 페르소나별 수익 흐름

| 페르소나 | CTA 문구 | 제휴 카테고리 | 예상 CPL | 예상 전환율 |
|---|---|---|---|---|
| BUYING (80%) | "Get a local [카테고리] quote" | 점검/견적 | $15~$35 | 2~3% |
| LIVING (20%) | "Find a top-rated [카테고리] pro" | 즉시 수리 | $25~$60 | 1~2% |

### 월간 수익 예측

| 월 트래픽 | BUYING 수익 | LIVING 수익 | **합계** |
|---|---|---|---|
| 1,000명 | $240 (16건×$15) | $80 (2건×$40) | **$320** |
| 2,000명 | $640 (32건×$20) | $240 (6건×$40) | **$880** |
| 3,000명 | $1,080 (54건×$20) | $360 (9건×$40) | **$1,440** |
| 5,000명 | $2,000 (100건×$20) | $600 (15건×$40) | **$2,600** |

> 💡 **월 2,000명이면 목표($700/100만원) 돌파 가능.**
> 월 5,000명이면 $2,600(약 350만 원)까지 현실적으로 도달 가능합니다.

---

## ⚠️ 주의사항 (실수 방지 체크리스트)

- [ ] "Inspection"이라는 단어를 CTA나 제휴 신청서에 쓰지 않기 (verify/quote/condition check 사용)
- [ ] Editorial Policy에 "제휴 링크 포함 가능" 문구 반드시 반영 후 가입
- [ ] `/track` 화이트리스트가 제휴 파트너 도메인을 허용하는지 반드시 테스트
- [ ] 제휴 플랫폼의 브랜드 키워드 입찰 금지(PPC) 정책 확인 (예: Choice Home Warranty는 브랜드명 입찰 금지)
- [ ] 가짜 리드(봇)를 방지하기 위해 `/api/lead`에 Rate Limit 적용 확인
