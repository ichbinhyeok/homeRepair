# Growth Playbook (홈 리페어 SEO 전략 노트)

## 1. Current Baseline (현재 기준선)
- **배포일**: 2026-02-25 배포 후 2주차 시작점
- **페이지 구조 (PSEO)**:
  - **L1 (City/Era)**: `/home-repair/verdicts/{metro}/{era}.html` (Seed 전략으로 ~702개 정적 생성)
  - **L2 (Risk Detail)**: `/home-repair/verdicts/{metro}/{era}/{riskItem}` (`.html` 제거 및 동적 매핑, 무한 URL 생성 차단)
  - **허브 (State/Risk)**: 발견성(Discoverability) 극대화를 위한 L1 상위 Hub 50개 + 5대 고품질 Risk Hub
- **P0 방어 요약**: L2 리다이렉트 및 404 구조화 완비, 허위 Product 스키마 제거, 과장 문구 보수적 수정, Sitemap 정합성 완료. 중복 URL/스팸 페널티 차단.

## 2. KPI 정의 (핵심 지표)
**Google Search Console (GSC)**
- **Impressions / Clicks**: 일간/주간 트렌드 (노출이 먼저 시작되어야 함)
- **CTR**: 제목 및 Meta Description 최적화를 위한 핵심 기준
- **Average Position**: 타겟 키워드(`{metro} home repair cost`)의 평균 랭킹
- **Page Indexing**: "Indexed(생성됨)" vs "Crawled - currently not indexed(크롤링됨, 인덱싱되지 않음)" vs "Duplicate/canonical" (오류 증감 트렌드 주의)
- **Sitemaps**: Last read(마지막 읽은 날짜) 및 Discovered URLs 확인

**서버 로그 (Server Logs)**
- **404 에러 모니터링**: 악성 봇 크롤링 및 깨진 내부 링크 감지
- **301 리다이렉트**: L2 .html 요청이 정상적으로 301 처리되는지 확인
- **Bot User-Agent**: Googlebot/Bingbot 크롤 빈도 및 타겟 경로 분석

## 3. Phase Gate (단계 전환 조건)
- **Phase 1 (인덱싱 안정화)** 통과 조건:
  - `IF (Indexed pages >= 200 AND sitemap last read <= 7 days AND duplicate/canonical errors trending down) THEN` Move to Phase 2.
- **Phase 2 (확장)** 시작 조건:
  - `IF (Impressions/day >= 50 OR Clicks/day >= 5) THEN` Start adding more L1 cities/eras or expanding semantic hubs.
- **Phase 3 (수익)** 시작 조건:
  - `IF (월 세션 >= 3,000 AND 상위 20페이지 중 5페이지 이상이 안정적으로 Impressions 추세 확보) THEN` Active lead capture / monetization layer expansion.

## 4. Phase 별 To-do
**P0 (즉각 / 현재 진행 완료)**
- Canonical/Redirect 100% 대응 및 L2 .html.html 등 변종 스파이더 트랩 301 컷.
- 5 Risk Hub 배포 및 내부 링크 메시 강화.
- 가짜 스키마 스팸 및 YMYL 공격성 문구 걷어내기.

**P1 (Phase 1: 이번 주 ~ 이번 달 집중 코어 워크)**
- GSC 지속 확인: Sitemap 제출(`sitemap.xml`) 및 "Crawled - currently not indexed" 페이지 샘플 검사. 필요시 핵심 허브 5개 수동 등록.
- 서버 로그에서 301/404 패턴 찾기 (특히 낡은 크롤러 봇 패턴).

**P2 (Phase 2: 확장 조건 달성 시)**
- 새로운 Market/City 추가 배포.
- 허브(Risk/State) 컨텐츠에 Local Insight 문단 보강.

**P3 (Phase 3: 트래픽 발생 시)**
- Email Capture Lead Magnet 최적화 및 CTA 배치 실험 (Sticky Button 전환율 A/B 테스트).

## 5. Anti-pattern (절대 하지 말 것!)
- **무한 URL 변형 방치**: `/?sort=`, `/.html.html.htm` 등 무의미한 URL 파라미터나 확장자 변형 허용 (무조건 정규화/차단).
- **허위 평점 스키마**: 실제 유저 데이터 없이 "rating: 4.8"을 찍어 Product 스키마에 넣는 행위 (수동 페널티 직행).
- **Thin 대량 생성**: 단순히 Metro 명만 치환한 컨텐츠를 수만 개 양산 (크롤 예산 낭비, 인덱싱 거부).
- **개인화 페이지 Index 허용**: `/result/{uuid}` 와 같은 개인별 고유 산출물을 index 열어두기 (noindex 필수).

## 6. Templates / Routines
**GSC 체크 루틴 (주 2회)**
- 화/금 오전 10시 GSC 접속.
- Page Indexing 탭에서 `Duplicate without user-selected canonical`, `Crawled - currently not indexed` 리스트업 및 원인 파악.
- 핵심 Risk Hub 5개의 Impression 관찰 및 Query 확인.

**배포 후 체크리스트 (릴리즈 점검)**
- [ ] 24h: Sitemap이 Fetch 성공했는지, 에러가 없는지 GSC 확인.
- [ ] 24h: `seo_smoke_test.sh` 통과 여부 및 L2 301 리다이렉트 정상 동작 재확인.
- [ ] 7d: GSC에서 첫 Impressions 등장 키워드 기록, Index/Crawled 비율 기록.

**제목/메타 수정 규칙**
- CTR이 1.5% 미만인 Top Query(포지션 30위 이내) 페이지는 Title을 "연도 + Cost" 등 단정적이고 끌리는 형태(Clickbait가 아닌 정확성)로 수정.
- GSC에서 뽑힌 "실제 검색어"를 H2/H3 서브헤더에 자연스럽게 녹이기.
