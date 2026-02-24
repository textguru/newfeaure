# 위험관리 시스템 (Risk Management System)

Java Spring Boot + Vue.js 기반의 전사적 위험관리(ERM) 시스템입니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| 백엔드 | Java 17, Spring Boot 3.2, Spring Data JPA |
| 데이터베이스 | H2 (인메모리) |
| 프론트엔드 | Vue.js 3, Pinia, Vue Router |
| 차트 | Chart.js, vue-chartjs |
| 빌드 | Maven (백엔드), Vite (프론트엔드) |

## 주요 기능

- **대시보드**: 위험 현황 통계, 등급별/상태별/카테고리별 차트, 고위험 목록
- **위험 목록**: 위험 CRUD (등록/조회/수정/삭제), 카테고리·상태·등급·키워드 필터
- **위험 매트릭스**: 5x5 위험 매트릭스 시각화, 셀 클릭 시 해당 위험 상세
- **위험 점수 자동 계산**: 발생 가능성 × 영향도 → 등급 자동 산정
  - 15~25점: CRITICAL (심각)
  - 10~14점: HIGH (높음)
  - 5~9점:   MEDIUM (보통)
  - 1~4점:   LOW (낮음)

## 위험 카테고리

재무 / 운영 / 전략 / 컴플라이언스 / 보안 / 환경 / 평판 / 기술

## 실행 방법

### 백엔드

```bash
cd backend
./mvnw spring-boot:run
```

백엔드 서버: http://localhost:8080

### 프론트엔드

```bash
cd frontend
npm install
npm run dev
```

프론트엔드: http://localhost:5173

## API 엔드포인트

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | /api/risks | 전체 위험 목록 조회 |
| GET | /api/risks/{id} | 위험 단건 조회 |
| POST | /api/risks | 위험 등록 |
| PUT | /api/risks/{id} | 위험 수정 |
| DELETE | /api/risks/{id} | 위험 삭제 |
| GET | /api/risks/dashboard | 대시보드 통계 |
| GET | /api/risks/enums | 카테고리/상태 목록 |

H2 콘솔: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:riskdb`)
