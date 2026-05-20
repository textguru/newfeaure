# 지사별 시행종목 조회 (QIS - 시험관리)

요구사항 정의서 v1.0 / 백엔드 API 설계서 / DB 설계서 / UI 목업을 기반으로 구현된
"지사별 시행종목 조회" 기능 소스코드입니다.

## 디렉터리 구조

```
.
├── backend/                       # Spring Boot 3.2 + MyBatis + POI 백엔드
│   ├── pom.xml
│   └── src/main/
│       ├── java/kr/or/nqis/qis/exam/
│       │   ├── ExamApplication.java       # @SpringBootApplication
│       │   ├── web/                       # REST Controller
│       │   ├── service/                   # Service Interface
│       │   ├── impl/                      # Service 구현체
│       │   ├── dao/                       # MyBatis Mapper 인터페이스
│       │   ├── vo/                        # Value Object
│       │   ├── dto/response/              # 응답 DTO
│       │   ├── exception/                 # 예외 / 전역 핸들러
│       │   └── config/                    # Security, Cache 설정
│       └── resources/
│           ├── application.yml
│           ├── ehcache.xml
│           ├── sqlmap/mapper/             # MyBatis SQL XML
│           └── db/                        # Oracle / H2 스키마, 샘플 데이터
├── frontend/                      # 정적 HTML + CSS + Vanilla JS
│   ├── index.html
│   ├── css/style.css
│   └── js/main.js
└── docs/                          # 설계서 원본
```

## 구현 요구사항 매핑

| REQ    | 위치 |
|--------|------|
| REQ-001 시험정보 선택       | `ExamInfoController` / `ExamInfoServiceImpl` / `frontend/js/main.js openExamModal` |
| REQ-002 조회 조건 설정       | `ExamItemSearchVO` / `frontend/js/main.js confirmExamSelection` |
| REQ-003 시행종목 목록 조회   | `ExamItemController#getExamItems` / `ExamItemServiceImpl` / `ExamItemMapper.xml` |
| REQ-004 엑셀 다운로드        | `ExamItemController#downloadExcel` / `ExcelExportServiceImpl` |

## API 엔드포인트

| Method | URL | 설명 |
|:---|:---|:---|
| GET | `/api/v1/exams/branches` | 지사 목록 조회 |
| GET | `/api/v1/exams/infos?branchCode=` | 시험정보 팝업 조회 (REQ-001) |
| GET | `/api/v1/exams/items?examId=&branchCode=&examDate=&page=&size=` | 시행종목 목록 (REQ-003) |
| GET | `/api/v1/exams/items/excel?examId=&branchCode=&examDate=` | 엑셀 다운로드 (REQ-004) |

## 로컬 실행

기본 프로필은 `local` 이며 H2 인메모리 DB로 기동됩니다.

```bash
cd backend
./mvnw spring-boot:run
```

기동 후 `frontend/index.html`을 정적 호스팅하거나, Spring Boot 정적 리소스 디렉터리에
복사하여 같은 호스트에서 서빙하면 됩니다.

## 운영(Oracle) 전환

`application.yml`의 `spring.profiles.active`를 `prod` 등으로 두고,
`spring.datasource.*` 값을 Oracle 접속 정보로 채운 뒤 `db/schema-oracle.sql`로 스키마를
선반영하면 됩니다.
