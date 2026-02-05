# LivingCostCheck 전략 피봇 계획

> 작성일: 2026-02-05
> 상태: 논의 완료, 실행 대기

---

## 📋 현재 상황 진단

### 핵심 문제: 역순 설계
- ❌ 데이터 → 엔진 → 사용자 (현재)
- ✅ 사용자 페인포인트 → 검색어 → 엔진 (올바른 순서)

### 현재 자산
| 항목 | 상태 |
|------|------|
| pSEO 페이지 | 702개 (지역 × 연식 조합) |
| 계산 엔진 | 완성 (다양한 타겟 커버 가능) |
| 디자인 | 개선 필요 |
| 내부 링크 | 빈약, 고아 페이지 존재 |
| 제휴 수익 | 미구현 |

### 현재 pSEO 문제점
1. **검색 의도 미스매치**: "x$ 있는데 뭘 고쳐요?"는 실제 검색 패턴 아님
2. **타겟 모호**: 투자자/거주자/구매자 다 잡으려다 아무도 못 잡음
3. **위키백과 스타일**: 정보만 제공, 행동 유도력 약함
4. **pSEO 역할 모호**: 완전한 페이지인지, 계산기 입구인지 불명확

---

## 🎯 피봇 방향

### 타겟 확정
> **주택 구매 고려자 (Home Buyer)**

왜?
- 가장 높은 Intent (구매 결정 임박)
- 자연스러운 제휴 연결 (Home Inspection, Insurance, Warranty, Contractor)
- 불안 + 정보 니즈가 명확함

### 핵심 메시지 전환
| Before | After |
|--------|-------|
| "x$ 있으면 뭘 고칠까?" | "이 집 사도 되나? 숨겨진 비용은?" |
| 정보 제공자 (위키백과) | 결정 도우미 (어드바이저) |
| Homeowner 관점 | **Buyer 관점** |

### 사용자 검색 의도 (실제 검색 패턴)
✅ 타겟할 것:
- "hidden costs buying old house"
- "1980s home problems before buying"
- "home inspection red flags 1970s"
- "[지역] home repair cost"
- "is a [연식] home worth buying"

❌ 현재 (잘못된 가정):
- "x$ 있는데 뭘 고쳐요?"
- "[연식] 집 수리비"

---

## 🏗️ 리팩토링 계획

### Phase 1: pSEO 구조 전환

#### 현재 구조
```
Title: "What homeowners typically spend fixing [연식] homes in [지역]"
내용: 정보 나열
CTA: 계산기로 이동
```

#### 새로운 구조
```
Title: "Buying a [연식] Home in [지역]? The Real First-Year Cost Breakdown"

1. 훅: "이 집 진짜 괜찮은 거 맞아?" (불안 자극)
2. 이 연식/지역 구매자들이 모르고 당하는 것들
3. 숨겨진 비용 데이터 (기존 수리비 데이터 재활용)
4. "당신의 집은 어떨까?" → 계산기 CTA
5. 관련 페이지 내부 링크 (촘촘하게)
```

### Phase 2: 내부 링크 강화

#### 현재 상태
- 하단에 몇 개 링크만 존재
- 고아 페이지 있음
- 링크 구조 평면적

#### 목표 구조
```
[State 허브 페이지] (예: Texas)
    │
    ├── [City 페이지] Houston
    │     ├── 1950s (← 형제 페이지들 상호 링크)
    │     ├── 1970s
    │     ├── 1980s
    │     └── → Texas 허브로 올라가는 링크
    │
    ├── [City 페이지] Dallas
    │     └── ... (같은 구조)
    │
    └── [Cross-state 비교]
          └── Houston vs Phoenix (같은 연식)
```

#### 링크 규칙
- 모든 페이지 → State 허브 링크 (상위)
- 모든 페이지 → 같은 도시의 다른 연식 (형제)
- 모든 페이지 → 같은 연식의 다른 도시 (사촌)
- Footer에 주요 State 허브 링크

### Phase 3: 시드 페이지 전략

#### 사이트맵 제출
- 702개 전체 제출 OK (스팸 리스크 낮음)
- 단, "시드 페이지"를 특별히 고품질로 제작

#### 시드 페이지 후보 (20~30개)
1. **State 허브 페이지** (신규 생성 필요)
   - Texas Home Buying Guide (by Era)
   - California Hidden Costs Guide
   - Florida Home Age Analysis
   
2. **인기 도시 페이지** (기존 페이지 품질 강화)
   - Houston, LA, NYC, Phoenix, Chicago 등
   - 검색량 높은 도시 우선

3. **메인 계산기 페이지**
   - 가장 강력한 시드

---

## 🔧 기술적 변경사항

### 템플릿 수정 (702개 일괄 적용)
- `static-verdict.jte` 템플릿 수정
- Title, H1, 메타 디스크립션 구조 변경
- 콘텐츠 섹션 순서 재배치
- 내부 링크 섹션 강화

### State 허브 페이지 (신규)
- `/home-repair/verdicts/states/{state}.html`
- 해당 State의 모든 도시 링크
- "State별 주택 구매 가이드" 콘텐츠

### Schema Markup 강화
- 현재: FAQ Schema만 있음
- 추가: `WebApplication`, `Product` (계산기용)
- pSEO: `Article` + `HowTo` 혼합

### 정적 HTML vs 동적
- 정적 HTML 유지 (SEO에 문제없음)
- 문제는 콘텐츠, 구조가 아님

---

## 📊 수익 모델 (향후)

> 제휴는 서비스 스타일에서 자연스럽게 따라와야 함

### 잠재적 제휴 연결점
| 지점 | 제휴 가능성 |
|------|------------|
| 계산기 결과 페이지 | Home Warranty, Insurance Quote |
| pSEO 페이지 내 | Home Inspection 서비스 |
| 수리 항목별 | Contractor Matching (RepairPal 등) |
| 금융 계산 | 모기지, 리파이낸싱 |

### 우선순위
1. 서비스 완성 먼저
2. 트래픽 확보
3. 제휴 자연스럽게 연결

---

## ✅ 실행 체크리스트

### 즉시 (이번 주)
- [ ] State 허브 페이지 구조 설계
- [ ] pSEO 템플릿 새 구조 설계 (Buyer 관점)
- [ ] 내부 링크 규칙 정의

### 단기 (2주 내)
- [ ] 템플릿 수정 (`static-verdict.jte`)
- [ ] 702개 페이지 재생성
- [ ] State 허브 페이지 20~30개 생성
- [ ] 내부 링크 구현

### 중기 (1개월 내)
- [ ] 사이트맵 전체 제출
- [ ] Google Search Console 모니터링
- [ ] 인덱싱 상태 확인
- [ ] 필요시 조정

---

## ❓ 미결정 사항

1. **메인 계산기 UX**: 현재 상태 점검 필요
2. **디자인 개선 범위**: 전체 리디자인 vs 점진적 개선
3. **State 허브 콘텐츠**: 어느 정도 깊이로 작성할지
4. **제휴 사업자 선정**: 아직 미정

---

## 🏠 루트 페이지 전략

### 현재 상태 (`hub.jte`)
```
루트(/): "LifeVerdict - Decision tools that help you avoid expensive life mistakes"
         └── Home Repair (유일한 서비스)
         └── "Independent Platform" 표시
```

### 문제점
- 서비스가 1개뿐인데 **플랫폼** 느낌
- 사용자: "뭐가 많다고 해놓고 하나밖에 없네?" → 신뢰도 하락
- 불필요한 클릭 한 단계 추가

### 제안: 단일 제품 집중

```
[Before]
루트(/) → 플랫폼 허브 → /home-repair (실제 서비스)

[After]
루트(/) = Home Repair 계산기 (메인 영웅 섹션)
         + 하단에 "More tools coming" 정도만 힌트
```

### 구현 옵션
1. **리다이렉트**: `/` → `/home-repair`
2. **병합**: `hub.jte` 내용을 `index.jte` 스타일로 변경
3. **유지하되 축소**: 플랫폼 느낌 빼고, 바로 도구로 이동 유도

**권장**: 옵션 2 (병합) - 루트에서 바로 계산기 보여주기

---

## 🧮 계산기 + 결과 페이지 수정

### 현재 계산기 (`index.jte`) 분석

**좋은 점:**
- 깔끔한 3단계 폼 (지역, 연식, 목적)
- Trust Signal 섹션 있음
- Living/Buying/Investing 3가지 목적 선택

**문제점:**
- "What is your goal?" → Living이 기본값 (Buyer 타겟과 안 맞음)
- "Assess My Risk" → 모호한 CTA
- SEO 콘텐츠가 일반적

**수정 방향:**
```
1. 기본값 변경: Living → BUYING
2. CTA 변경: "Assess My Risk" → "See Hidden Costs Before You Buy"
3. 헤드라인: "The Honest Cost of Home Repair" 
            → "What Will This House Really Cost You?"
4. Trust Signal 재구성: Buyer 관점으로
```

### 현재 결과 페이지 (`result.jte`) 분석

**좋은 점:**
- Apple Health 스타일 UI (깔끔)
- 비용 범위 시각화
- 전략 설명 섹션
- Itemized 리스트

**문제점:**
- **제휴 연결점 없음** (가장 중요한 전환 지점인데)
- noindex 처리됨 (SEO 안됨 - 이건 의도적일 수 있음)
- "Start Another Analysis" → 더 강한 Next Step 필요

**수정 방향:**
```
결과 페이지 = 가장 높은 Intent 지점

추가할 것:
1. [제휴 자리] Home Inspection 추천 
   "이 집에 이런 문제가 예상됩니다. 전문 인스펙션으로 확인하세요"
   
2. [제휴 자리] Home Warranty Quote
   "예상 수리비 $X. 연 $Y로 보호받으세요"
   
3. [공유] 결과 저장/공유 기능
   "이 결과를 PDF로 저장 (협상에 활용하세요)"
   
4. [내부 링크] 관련 pSEO 페이지로 연결
   "이 지역의 다른 연식 집들은 어떨까요?"
```

---

## 🧠 pSEO 행동심리학 전략

> "사람들이 검색어를 치고, 사이트를 누르게 만들어야 한다"

### SERP에서 클릭받기 (CTR 최적화)

#### Title Tag 공식
```
[감정 트리거] + [구체적 수치/연식] + [지역] + [2026]

Before: "What homeowners typically spend fixing 1970s homes in Houston TX"
After:  "1970s Houston Home? Here's What You'll REALLY Pay (2026 Data)"

심리학 원리:
- "REALLY" = 진실 노출 암시 (호기심)
- "2026 Data" = 최신 정보 (신뢰)
- 질문형 = 대화 시작 느낌
```

#### Meta Description 공식
```
[공포/손실 회피] + [데이터 증거] + [해결 암시]

Before: "A data-driven analysis of historical repair costs..."
After:  "70% of 1970s home buyers miss $15,000+ in hidden repairs. 
        We analyzed 847 similar homes to show exactly what breaks first."

심리학 원리:
- "70%가 놓친다" = 사회적 증거 + 손실 회피
- "$15,000+" = 구체적 손실 금액
- "exactly what breaks first" = 순서/우선순위 (인간은 순서를 좋아함)
```

### 페이지 내 행동 유도 (On-Page 심리학)

#### 1. 도입부 (첫 5초)
```
❌ 현재: "A data-driven analysis of historical repair costs..."
✅ 개선: "You're about to buy a 1970s home in Houston. 
         Here's what the seller won't tell you."

원리: "You" = 개인화, "seller won't tell" = 비밀 공개 느낌
```

#### 2. 스캔 가능한 공포 리스트
```
❌ 현재: 일반적인 수리 항목 나열
✅ 개선: 
   🔴 DEAL KILLER: Lead pipes (90% chance at this age)
   🟡 NEGOTIATE: Roof has 3 years left ($14,000 replacement)
   🟢 MANAGEABLE: HVAC needs service ($800)

원리: 신호등 컬러 = 즉각적 위험 인지
      확률 = 구체성 + 불안 강화
```

#### 3. CTA 프레이밍 (손실 회피)
```
❌ 현재: "Start My Personal Estimate"
✅ 개선: "See What This House Will Cost You Before You Commit"

또는 더 강하게:
"Every Week You Wait = Another Buyer's Advantage"

원리: 
- 손실 회피 > 이익 추구 (2배 강력)
- 시간 압박 = 행동 촉구
```

#### 4. 내부 링크 프레이밍
```
❌ 현재: "Compare with Boston Cambridge MA (1970s)"
✅ 개선: "Is Boston actually cheaper? Quick comparison →"

원리: 
- 질문 = 호기심
- "Quick" = 시간 부담 감소
```

### 페이지 구조 심리학

```
[HOOK] 불안 자극 (첫 2줄)
       ↓
[EVIDENCE] 데이터로 공포 구체화
       ↓  
[HOPE] "하지만 알면 대비할 수 있다"
       ↓
[SOLUTION] 계산기 CTA
       ↓
[SOCIAL PROOF] "X명이 이 도구로 협상에 성공"
```

### 검색어/키워드 매핑

| 사용자 심리 상태 | 실제 검색어 | 우리 pSEO 타이틀 |
|---------------|-----------|----------------|
| 의심/불안 | "old house problems before buying" | "Buying an Old House? The Problems Nobody Mentions" |
| 비교 쇼핑 | "1970s vs 1980s home quality" | "1970s vs 1980s Homes: Which Actually Costs Less?" |
| 협상 준비 | "how to negotiate home repair costs" | "Use These Numbers to Negotiate Your 1970s Home Price" |
| 후회 방지 | "regret buying old house" | "What 1970s Home Buyers Wish They Knew" |

---

## 📝 논의 요약

> "엔진은 훌륭하다. 문제는 입구(pSEO)가 사용자의 실제 행동 패턴과 안 맞는 것이다."

핵심 피봇:
- **타겟**: 주택 구매자
- **메시지**: "이 집 사도 되나?"
- **pSEO 역할**: 정보 제공 → 구매 결정 도우미
- **수량**: 702개 유지, 품질 강화
- **사이트맵**: 전체 제출 OK
