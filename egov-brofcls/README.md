# 지사별 시행종목 조회 - eGovFrame 5.0 소스코드 산출물

본 산출물은 첨부된 4종 문서(요구사항정의서, 화면설계서, 화면 레이아웃, 테이블정의서)를 입력으로 하여
전자정부프레임워크 5.0 표준에 따라 생성된 결과물입니다.

## 1. 생성된 파일 일람

```
egov-brofcls/
├── src/main/java/egovframework/exam/brofcls/
│   ├── service/
│   │   ├── EgovBrofClsService.java          (Service 인터페이스)
│   │   ├── BrofClsSearchVO.java             (검색 조건 VO)
│   │   ├── BrofVO.java                      (지사 VO - TB_BROF)
│   │   ├── TestInfoVO.java                  (시험정보 VO)
│   │   ├── BrofClsVO.java                   (시행종목 목록 행 VO)
│   │   ├── BrofClsListResultVO.java         (목록 응답 + 페이징 메타)
│   │   └── impl/
│   │       ├── EgovBrofClsServiceImpl.java  (Service 구현체)
│   │       └── BrofClsDAO.java              (DAO, EgovAbstractMapper 상속)
│   └── web/
│       └── EgovBrofClsController.java       (Controller, 4 API + 화면 진입)
├── src/main/resources/egovframework/sqlmap/exam/brofcls/
│   └── BrofCls_SQL.xml                      (MyBatis Mapper)
├── src/main/webapp/WEB-INF/jsp/egovframework/exam/brofcls/
│   └── EgovBrofClsSearch.jsp                (SCR-001 + POP-001 단일 화면)
├── src/test/java/egovframework/exam/brofcls/
│   ├── EgovBrofClsServiceImplTest.java      (Service 단위테스트, JUnit5 + Mockito)
│   └── EgovBrofClsControllerTest.java       (Controller 단위테스트, MockMvc)
└── sql/
    └── ddl_brofcls.sql                       (테이블 6개 DDL + 샘플 데이터)
```

## 2. 4계층 구조 매핑

| 계층 | 산출물 | 화면설계서 API 매핑 |
|------|--------|---------------------|
| Controller | EgovBrofClsController | API-001~004 모두 |
| Service | EgovBrofClsServiceImpl | 비즈니스 로직(검증, 순번 부여, 페이징 계산) |
| DAO | BrofClsDAO + BrofCls_SQL.xml | 5개 SQL (목록 카운트 별도) |
| VO | 6종 | 테이블정의서 컬럼 매핑 + 화면 응답 모델 |

## 3. 요구사항 추적 (RTM)

| REQ | API | 구현 위치 | 단위테스트 |
|-----|-----|-----------|-----------|
| REQ-001 시험정보 선택 | API-002 | `selectTestInfoListByBrof` | 3개 케이스 |
| REQ-002 조회 조건 설정 | API-001 | `selectBrofList` | 1개 케이스 |
| REQ-003 목록 조회 | API-003 | `selectBrofClsList` | 4개 케이스 |
| REQ-004 엑셀 다운로드 | API-004 | `downloadExcel` + `selectBrofClsListForExcel` | 2개 케이스 |

## 4. 방법론 표준 적용 사항

- **단위테스트**: given/when은 AI가 작성, then(assert)은 `// TODO` 주석으로 남겨 개발자가 비즈니스 로직을 이해한 뒤 직접 작성하도록 함. 통과적 수용 방지 목적.
- **메시지 ID**: 화면설계서 MSG-001 ~ MSG-004에 정의된 메시지 문구를 Service 예외 메시지와 JSP에서 정확히 일치시킴.
- **eGovFrame 표준 상속**: `EgovAbstractServiceImpl`, `EgovAbstractMapper` 사용.
- **패키지 규칙**: `egovframework.{도메인}.{기능}.{계층}` 구조 준수.

## 5. 빌드 검증을 위해 추가로 필요한 자산

본 산출물은 도메인 코드만 포함하며, **실제 빌드/구동까지 가려면** 다음이 추가 필요합니다.

### 5.1 프로젝트 템플릿 (필수)
- `pom.xml` 또는 `build.gradle` (eGovFrame 5.0 BOM 적용)
  - 핵심 의존성: `org.egovframe.rte:org.egovframe.rte.psl.dataaccess`, `org.egovframe.rte.fdl.cmmn`
  - Spring 5.x, MyBatis 3.5+, Apache POI 5.x, JUnit Jupiter 5, Mockito 5
- `web.xml`, `dispatcher-servlet.xml`, `context-mapper.xml`, `context-datasource.xml`
- 공통 코드: `BaseController`, 글로벌 예외 핸들러 (`@ControllerAdvice`), 공통 응답 포맷
- 로깅 설정 (`logback.xml`)

### 5.2 런타임 환경
- 로컬 DB (MariaDB 권장) + `application-local.yml` 또는 jdbc 프로파일
- DDL 실행: `sql/ddl_brofcls.sql` 적용

### 5.3 검증 자산
- PMD ruleset (정적분석 자동화)
- Selenium WebDriver 설정 (JSP 렌더링 테스트, 화면 단위 테스트 3계층 중 마지막 계층)

## 6. 빌드/실행 단계 도달 가능성

| 단계 | 현재 산출물만으로 | 5.1~5.3 추가 후 |
|------|------------------|------------------|
| 컴파일 통과 | △ (의존성 누락) | ○ |
| 단위테스트 실행 | △ (then TODO로 인해 실제 검증 불가) | ○ (개발자가 then 작성 후) |
| WAR 빌드 | × | ○ |
| 로컬 구동 + 화면 호출 | × | ○ |
| PMD 정적분석 통과 | 미실행 | ○ (룰셋 적용 후) |

## 7. SQL 호환성 주의

`BrofCls_SQL.xml`은 **MariaDB/MySQL의 `LIMIT #{firstIndex}, #{pageSize}` 구문**으로 작성되었습니다.
Oracle 환경 적용 시 `ROWNUM` 또는 `OFFSET ... FETCH` 구문으로 페이징 부분을 분리 수정해야 합니다.
