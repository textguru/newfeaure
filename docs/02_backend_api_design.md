# 백엔드 API 설계서 - 지사별 시행종목 조회

## 1. 기술 스택 제안

| 구분 | 기술 | 비고 |
|:---|:---|:---|
| **프레임워크** | Spring Boot 3.2.x | Java 기반 REST API |
| **ORM** | MyBatis | 동적 SQL 지원 (NQIS 표준 준수) |
| **데이터베이스** | Oracle Database | 기존 설계서 기준 |
| **엑셀 생성** | Apache POI (.xlsx) | 파일 다운로드 지원 |
| **인증** | JWT (HMAC-SHA256) | SSO 연동, Stateless API |
| **캐싱** | Ehcache 3.x | Spring Cache Abstraction |

---

## 2. 프로젝트 구조

### 2.1 패키지 구조 (NQIS 표준 준수)

```
src/main/java/kr/or/nqis/qis/exam/
├── web/
│   ├── ExamBranchController.java          # 지사 목록 조회
│   ├── ExamInfoController.java            # 시험정보 팝업 조회
│   └── ExamItemController.java            # 시행종목 조회/엑셀 다운로드
├── service/
│   ├── ExamBranchService.java             # Service Interface
│   ├── ExamInfoService.java               # Service Interface
│   ├── ExamItemService.java               # Service Interface
│   └── ExcelExportService.java            # Service Interface
├── impl/
│   ├── ExamBranchServiceImpl.java         # Service Implementation
│   ├── ExamInfoServiceImpl.java           # Service Implementation
│   ├── ExamItemServiceImpl.java           # Service Implementation
│   └── ExcelExportServiceImpl.java        # Service Implementation
├── dao/
│   ├── BranchDAO.java                     # DAO Interface (MyBatis Mapper)
│   ├── ExamInfoDAO.java                   # DAO Interface
│   └── ExamItemDAO.java                   # DAO Interface
├── vo/
│   ├── BranchVO.java                      # 지사 Value Object
│   ├── ExamInfoVO.java                    # 시험정보 Value Object
│   ├── ExamItemVO.java                    # 시행종목 Value Object
│   └── ExamItemSearchVO.java              # 조회 조건 Value Object
├── dto/
│   ├── response/
│   │   ├── BranchResponse.java            # 지사 응답 DTO
│   │   ├── ExamInfoPopupResponse.java     # 시험정보 팝업 응답 DTO
│   │   ├── ExamItemResponse.java          # 시행종목 응답 DTO
│   │   ├── PaginationResponse.java        # 페이징 응답 DTO
│   │   └── ApiResponse.java               # 공통 응답 DTO
│   └── excel/
│       └── ExamItemExcelData.java         # 엑셀导出数据 DTO
├── exception/
│   ├── BusinessException.java             # 비즈니스 예외
│   ├── SystemException.java               # 시스템 예외
│   └── GlobalExceptionHandler.java        # 전역 예외 처리
└── config/
    ├── SecurityConfig.java                # JWT 보안 설정
    ├── MyBatisConfig.java                 # MyBatis 설정
    └── CacheConfig.java                   # Ehcache 설정
```

### 2.2 리소스 구조

```
src/main/resources/
├── sqlmap/mapper/
│   ├── BranchMapper.xml                   # 지사 SQL 매핑
│   ├── ExamInfoMapper.xml                 # 시험정보 SQL 매핑
│   └── ExamItemMapper.xml                 # 시행종목 SQL 매핑
├── properties/
│   ├── globals.properties                 # 전역 설정
│   └── message.properties                 # 메시지 속성
├── ehcache.xml                            # 캐시 설정
└── log4j2.xml                             # 로그 설정
```

---

## 3. API 엔드포인트 정의

### 3.1 전체 API 목록

| 구분 | HTTP Method | API URL | 설명 | REQ |
|:---|:---|:---|:---|:---|
| 지사 목록 조회 | `GET` | `/api/v1/exams/branches` | 전체 지사 목록 조회 | SCR-001 |
| 시험정보 팝업 조회 | `GET` | `/api/v1/exams/infos` | 지사별 시험정보 목록 조회 | REQ-001 |
| 시행종목 목록 조회 | `GET` | `/api/v1/exams/items` | 시행종목 목록 조회 (페이징) | REQ-003 |
| 시행종목 엑셀 다운로드 | `GET` | `/api/v1/exams/items/excel` | 시행종목 엑셀 파일 다운로드 | REQ-004 |

---

### 3.2 API 상세 스펙

---

#### API-01: 지사 목록 조회

**REQ 매핑**: REQ-002 규칙 4 (지사 단일 선택)

| 항목 | 내용 |
|:---|:---|
| **Method** | `GET` |
| **URL** | `/api/v1/exams/branches` |
| **설명** | 사용 가능한 전체 지사 목록을 조회한다. |
| **인증** | JWT 토큰 인증 필요 |

**Request**
```http
GET /api/v1/exams/branches HTTP/1.1
Host: example.com
Authorization: Bearer {JWT_TOKEN}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "branchCode": "BR001",
      "branchName": "서울지사"
    },
    {
      "branchCode": "BR002",
      "branchName": "부산지사"
    }
  ],
  "message": "정상 처리되었습니다."
}
```

**예외 처리**
| 예외 상황 | HTTP Status | 응답 메시지 |
|:---|:---|:---|
| 비인증 접근 | 401 Unauthorized | "로그인이 필요합니다." |
| 서버 내부 오류 | 500 Internal Server Error | "시스템 오류가 발생했습니다." |

---

#### API-02: 시험정보 팝업 조회

**REQ 매핑**: REQ-001 규칙 1~3, 예외 처리 1

| 항목 | 내용 |
|:---|:---|
| **Method** | `GET` |
| **URL** | `/api/v1/exams/infos?branchCode={branchCode}` |
| **설명** | 선택된 지사에서 시행되는 시험정보 목록을 조회한다. |
| **인증** | JWT 토큰 인증 필요 |
| **캐시 적용** | `menuCache` (TTL: 12h) |

**Request**
```http
GET /api/v1/exams/infos?branchCode=BR001 HTTP/1.1
Host: example.com
Authorization: Bearer {JWT_TOKEN}
```

**Query Parameter**

| 이름 | 타입 | 필수 | 설명 |
|:---|:---|:---|:---|
| `branchCode` | String | O | 지사 코드 (단일 선택) |

**Response**
```json
{
  "success": true,
  "data": [
    {
      "examId": 1,
      "examName": "2026년 1차 전기공사기사",
      "examDate": "20260615",
      itemCount: 12
    },
    {
      "examId": 2,
      "examName": "2026년 1차 용접산업기사",
      "examDate": "20260620",
      itemCount: 8
    }
  ],
  "message": "정상 처리되었습니다."
}
```

**응답 데이터 필드**

| 필명 | 타입 | 설명 |
|:---|:---|:---|
| `examId` | Integer | 시험 ID |
| `examName` | String | 시험명 |
| `examDate` | String | 시험일자 (YYYYMMDD) |
| `itemCount` | Integer | 시행종목 건수 |

**예외 처리**
| 예외 상황 | HTTP Status | 응답 메시지 |
|:---|:---|:---|
| branchCode 미전달 | 400 Bad Request | "지사 코드를 입력해 주세요." |
| 조회된 시험정보 없음 | 200 OK (data: []) | "조회된 시험정보가 없습니다." (프론트엔드 메시지) |
| 비인증 접근 | 401 Unauthorized | "로그인이 필요합니다." |

---

#### API-03: 시행종목 목록 조회

**REQ 매핑**: REQ-003 규칙 1~6, 예외 처리 1~2

| 항목 | 내용 |
|:---|:---|
| **Method** | `GET` |
| **URL** | `/api/v1/exams/items` |
| **설명** | 설정된 조회 조건에 해당하는 시행종목 목록을 조회한다. |
| **인증** | JWT 토큰 인증 필요 |

**Request**
```http
GET /api/v1/exams/items?examId=1&branchCode=BR001&examDate=20260615&page=1&size=20 HTTP/1.1
Host: example.com
Authorization: Bearer {JWT_TOKEN}
```

**Query Parameter**

| 이름 | 타입 | 필수 | 설명 |
|:---|:---|:---|:---|
| `examId` | Integer | O | 시험 ID |
| `branchCode` | String | O | 지사 코드 |
| `examDate` | String | O | 시험일자 (YYYYMMDD) |
| `page` | Integer | X | 페이지 번호 (기본값: 1) |
| `size` | Integer | X | 페이지당 건수 (기본값: 20) |

**Response**
```json
{
  "success": true,
  "data": {
    "list": [
      {
        "seq": 1,
        "itemCode": "I001",
        "itemName": "전기공사기사",
        "selectField": "S01",
        "selectFieldName": "고압 전기작업",
        "workType": "필기",
        "part": "1부",
        "exposeYn": "Y"
      },
      {
        "seq": 2,
        "itemCode": "I001",
        "itemName": "전기공사기사",
        "selectField": "S02",
        "selectFieldName": "저압 전기작업",
        "workType": "필기",
        "part": "1부",
        "exposeYn": "Y"
      },
      {
        "seq": 3,
        "itemCode": "I001",
        "itemName": "전기공사기사",
        "selectField": "S01",
        "selectFieldName": "고압 전기작업",
        "workType": "실기",
        "part": "2부",
        "exposeYn": "N"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalCount": 45,
      "totalPages": 3,
      "hasPrevious": false,
      "hasNext": true
    }
  },
  "message": "정상 처리되었습니다."
}
```

**응답 데이터 필드**

| 필명 | 타입 | 설명 |
|:---|:---|:---|
| `seq` | Integer | 순번 (페이지 내 기준 1부터) |
| `itemCode` | String | 종목 코드 |
| `itemName` | String | 종목명 |
| `selectField` | String | 선택분야 코드 |
| `selectFieldName` | String | 선택분야명 |
| `workType` | String | 작업구분 (예: 필기, 실기, 면접) |
| `part` | String | 부 (예: 1부, 2부) |
| `exposeYn` | String | 시험일정공개여부 (Y/N) |

**페이징 정보 필드**

| 필명 | 타입 | 설명 |
|:---|:---|:---|
| `currentPage` | Integer | 현재 페이지 번호 |
| `pageSize` | Integer | 페이지당 건수 |
| `totalCount` | Integer | 전체 건수 |
| `totalPages` | Integer | 전체 페이지 수 |
| `hasPrevious` | Boolean | 이전 페이지 존재 여부 |
| `hasNext` | Boolean | 다음 페이지 존재 여부 |

**정렬 규칙**
- 기본 정렬: `ITEM_CODE ASC` → `PART ASC`

**예외 처리**
| 예외 상황 | HTTP Status | 응답 메시지 |
|:---|:---|:---|
| 필수 파라미터 누락 | 400 Bad Request | "조회 조건을 확인해 주세요." |
| 유효하지 않은 examId | 400 Bad Request | "조회 조건을 확인해 주세요." |
| 조회 결과 없음 | 200 OK (data: []) | "조회된 데이터가 없습니다." (프론트엔드 메시지) |
| 비인증 접근 | 401 Unauthorized | "로그인이 필요합니다." |

---

#### API-04: 시행종목 엑셀 다운로드

**REQ 매핑**: REQ-004 규칙 1~4, 예외 처리 1

| 항목 | 내용 |
|:---|:---|
| **Method** | `GET` |
| **URL** | `/api/v1/exams/items/excel` |
| **설명** | 조회된 시행종목 목록을 엑셀 파일로 다운로드한다. |
| **인증** | JWT 토큰 인증 필요 |
| **응답 타입** | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |

**Request**
```http
GET /api/v1/exams/items/excel?examId=1&branchCode=BR001&examDate=20260615 HTTP/1.1
Host: example.com
Authorization: Bearer {JWT_TOKEN}
```

**Query Parameter**

| 이름 | 타입 | 필수 | 설명 |
|:---|:---|:---|:---|
| `examId` | Integer | O | 시험 ID |
| `branchCode` | String | O | 지사 코드 |
| `examDate` | String | O | 시험일자 (YYYYMMDD) |

**Response**
```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="지사별시행종목_서울지사_2026년 1차 전기공사기사_20260615143025.xlsx"
Content-Length: {파일크기}

[바이너리 엑셀 파일 데이터]
```

**파일명 생성 규칙**
```
지사별시행종목_[지사명]_[시험명]_[시험일자]_[다운로드일시].xlsx
```
- 시험일자: `YYYY-MM-DD` 형식
- 다운로드일시: `YYYYMMDDHHmmss` 형식

**엑셀 구조**

| 행 | 내용 |
|:---|:---|
| 1행 (헤더) | 순번, 종목코드, 종목명, 선택분야, 선택분야명, 작업구분, 부, 시험일정공개여부 |
| 2행 이하 | 조회된 데이터 전체 (건수 제한 없음) |

**예외 처리**
| 예외 상황 | HTTP Status | 응답 메시지 |
|:---|:---|:---|
| 필수 파라미터 누락 | 400 Bad Request | "조회 조건을 확인해 주세요." |
| 조회 결과 없음 | 404 Not Found | "다운로드할 데이터가 없습니다." |
| 비인증 접근 | 401 Unauthorized | "로그인이 필요합니다." |

---

## 4. 핵심 로직 설계

### 4.1 Service Interface

```java
package kr.or.nqis.qis.exam.service;

import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.dto.response.PaginationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 시행종목 Service Interface.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
public interface ExamItemService {

    /**
     * REQ-003: 시행종목 목록 조회
     * - 필수 조건 검증 (examId, branchCode, examDate)
     * - 정렬: ITEM_CODE ASC → PART ASC
     * - 페이징: 20건/페이지
     *
     * @param searchVO 조회 조건 VO
     * @return 시행종목 목록 및 페이징 정보
     */
    List<ExamItemResponse> selectExamItemList(ExamItemSearchVO searchVO);

    /**
     * REQ-003: 전체 건수 조회
     *
     * @param searchVO 조회 조건 VO
     * @return 전체 건수
     */
    int selectExamItemListTotlCnt(ExamItemSearchVO searchVO);
}
```

### 4.2 Service Implementation

```java
package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.ExamItemDAO;
import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.exception.BusinessException;
import kr.or.nqis.qis.exam.service.ExamItemService;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 시행종목 Service Implementation.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamItemServiceImpl implements ExamItemService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ExamItemDAO examItemDAO;

    /**
     * REQ-003: 시행종목 목록 조회
     * - 필수 조건 검증 (examId, branchCode, examDate)
     * - 정렬: ITEM_CODE ASC → PART ASC
     * - 페이징: 20건/페이지
     */
    @Override
    public List<ExamItemResponse> selectExamItemList(ExamItemSearchVO searchVO) {

        // 필수 조건 검증
        validateSearchConditions(searchVO);

        // 시행종목 조회 (DAO를 통한 MyBatis SQL 실행)
        List<ExamItemResponse> result = examItemDAO.selectExamItemList(searchVO);

        // 순번 부여 (페이지 내 기준 1부터)
        long seq = (searchVO.getPage() - 1) * searchVO.getSize() + 1;
        for (ExamItemResponse response : result) {
            response.setSeq(seq++);
        }

        return result;
    }

    @Override
    public int selectExamItemListTotlCnt(ExamItemSearchVO searchVO) {
        validateSearchConditions(searchVO);
        return examItemDAO.selectExamItemListTotlCnt(searchVO);
    }

    /**
     * 조회 조건 검증
     *
     * @param searchVO 조회 조건 VO
     */
    private void validateSearchConditions(ExamItemSearchVO searchVO) {
        if (searchVO.getExamId() == null
                || searchVO.getBranchCode() == null
                || searchVO.getExamDate() == null) {
            throw new BusinessException("조회 조건을 확인해 주세요.");
        }
    }
}
```

### 4.3 ExamInfoService - 시험정보 팝업 조회

```java
package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.ExamInfoDAO;
import kr.or.nqis.qis.exam.dto.response.ExamInfoPopupResponse;
import kr.or.nqis.qis.exam.exception.BusinessException;
import kr.or.nqis.qis.exam.service.ExamInfoService;
import kr.or.nqis.qis.exam.vo.ExamInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 시험정보 Service Implementation.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamInfoServiceImpl implements ExamInfoService {

    private final ExamInfoDAO examInfoDAO;

    /**
     * REQ-001: 지사별 시험정보 목록 조회 (팝업용)
     * - 선택된 지사에 해당하는 데이터만 필터링
     * - 시행종목 건수 포함
     * - 캐시 적용 (menuCache)
     */
    @Override
    @Cacheable(cacheNames = "menuCache", key = "#branchCode")
    public List<ExamInfoPopupResponse> selectExamInfosForPopup(String branchCode) {

        if (branchCode == null || branchCode.isBlank()) {
            throw new BusinessException("지사 코드를 입력해 주세요.");
        }

        ExamInfoVO searchVO = new ExamInfoVO();
        searchVO.setBranchCode(branchCode);

        List<ExamInfoVO> examInfos = examInfoDAO.selectExamInfoList(searchVO);

        if (examInfos.isEmpty()) {
            // 프론트엔드에서 "조회된 시험정보가 없습니다." 표시
            log.info("조회된 시험정보가 없습니다. branchCode: {}", branchCode);
            return List.of();
        }

        return examInfos.stream()
                .map(ExamInfoPopupResponse::from)
                .toList();
    }
}
```

### 4.4 ExcelExportService - 엑셀 생성

```java
package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.ExamInfoDAO;
import kr.or.nqis.qis.exam.dao.ExamItemDAO;
import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.exception.BusinessException;
import kr.or.nqis.qis.exam.service.ExcelExportService;
import kr.or.nqis.qis.exam.vo.ExamInfoVO;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 엑셀 생성 Service Implementation.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelExportServiceImpl implements ExcelExportService {

    private final ExamItemDAO examItemDAO;
    private final ExamInfoDAO examInfoDAO;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * REQ-004: 시행종목 엑셀 파일 생성
     * - 파일명: 지사별시행종목_[지사명]_[시험명]_[시험일자]_[다운로드일시].xlsx
     * - 전체 데이터 다운로드 (건수 제한 없음)
     */
    @Override
    public byte[] exportExcel(Integer examId, String branchCode, String examDate) {

        // 데이터 조회 (페이징 없음)
        ExamItemSearchVO searchVO = new ExamItemSearchVO();
        searchVO.setExamId(examId);
        searchVO.setBranchCode(branchCode);
        searchVO.setExamDate(examDate);

        List<ExamItemResponse> items = examItemDAO.selectExamItemList(searchVO);

        if (items.isEmpty()) {
            log.warn("다운로드할 데이터가 없습니다. examId: {}, branchCode: {}", examId, branchCode);
            throw new BusinessException("다운로드할 데이터가 없습니다.");
        }

        // 시험정보 조회 (파일명용)
        ExamInfoVO examInfoVO = new ExamInfoVO();
        examInfoVO.setExamId(examId);
        ExamInfoVO examInfo = examInfoDAO.selectExamInfo(examInfoVO);

        if (examInfo == null) {
            log.error("시험정보를 찾을 수 없습니다. examId: {}", examId);
            throw new BusinessException("시험정보를 찾을 수 없습니다.");
        }

        // 엑셀 생성
        return createExcelFile(items, examInfo);
    }

    /**
     * 엑셀 파일 생성
     *
     * @param items 조회된 시행종목 목록
     * @param examInfo 시험정보
     * @return 엑셀 파일 바이트 배열
     */
    private byte[] createExcelFile(List<ExamItemResponse> items, ExamInfoVO examInfo) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("시행종목 목록");

            // 헤더 행 생성
            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"순번", "종목코드", "종목명", "선택분야", "선택분야명",
                    "작업구분", "부", "시험일정공개여부"};
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 데이터 행 생성
            for (int i = 0; i < items.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                ExamItemResponse item = items.get(i);

                row.createCell(0).setCellValue(i + 1);                                    // 순번
                row.createCell(1).setCellValue(item.getItemCode() != null ? item.getItemCode() : "");   // 종목코드
                row.createCell(2).setCellValue(item.getItemName() != null ? item.getItemName() : "");   // 종목명
                row.createCell(3).setCellValue(item.getSelectField() != null ? item.getSelectField() : ""); // 선택분야
                row.createCell(4).setCellValue(item.getSelectFieldName() != null ? item.getSelectFieldName() : ""); // 선택분야명
                row.createCell(5).setCellValue(item.getWorkType() != null ? item.getWorkType() : "");   // 작업구분
                row.createCell(6).setCellValue(item.getPart() != null ? item.getPart() : "");          // 부
                row.createCell(7).setCellValue(item.getExposeYn() != null ? item.getExposeYn() : "");  // 시험일정공개여부
            }

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 바이트 배열로 변환
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("엑셀 파일 생성에 실패했습니다.", e);
            throw new BusinessException("엑셀 파일 생성에 실패했습니다.");
        }
    }
}
```

---

## 5. DAO (MyBatis Mapper)

### 5.1 ExamItemDAO

```java
package kr.or.nqis.qis.exam.dao;

import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 시행종목 DAO Interface.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Mapper
public interface ExamItemDAO {

    /**
     * 시행종목 목록 조회
     *
     * @param searchVO 조회 조건
     * @return 시행종목 목록
     */
    List<ExamItemResponse> selectExamItemList(ExamItemSearchVO searchVO);

    /**
     * 시행종목 전체 건수 조회
     *
     * @param searchVO 조회 조건
     * @return 전체 건수
     */
    int selectExamItemListTotlCnt(ExamItemSearchVO searchVO);
}
```

### 5.2 ExamItemMapper.xml (SQL 매핑)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="kr.or.nqis.qis.exam.dao.ExamItemDAO">

    <!-- 시행종목 응답 ResultMap -->
    <resultMap id="examItemResultMap" type="kr.or.nqis.qis.exam.dto.response.ExamItemResponse">
        <result column="SEQ" property="seq"/>
        <result column="ITEM_CD" property="itemCode"/>
        <result column="ITEM_NM" property="itemName"/>
        <result column="SELECT_FIELD" property="selectField"/>
        <result column="SELECT_FIELD_NM" property="selectFieldName"/>
        <result column="WORK_TYPE" property="workType"/>
        <result column="PART" property="part"/>
        <result column="EXPOSE_YN" property="exposeYn"/>
    </resultMap>

    <!-- 시행종목 목록 조회 (REQ-003) -->
    <select id="selectExamItemList" resultMap="examItemResultMap">
        SELECT
            ROW_NUMBER() OVER (
                ORDER BY ic.ITEM_CODE ASC, ei.PART ASC
            ) AS RN,
            ic.ITEM_CODE AS ITEM_CD,
            ic.ITEM_NM,
            sf.SELECT_FIELD,
            sf.SELECT_FIELD_NM,
            ei.WORK_TYPE,
            ei.PART,
            ei.EXPOSE_YN
        FROM TB_EXAM_ITEM ei
        INNER JOIN TB_ITEM_CODE ic
            ON ei.ITEM_CODE = ic.ITEM_CODE
            AND ic.USE_YN = 'Y'
        LEFT JOIN TB_SELECT_FIELD sf
            ON ei.ITEM_CODE = sf.ITEM_CODE
            AND ei.SELECT_FIELD = sf.SELECT_FIELD
            AND sf.USE_YN = 'Y'
        WHERE ei.EXAM_ID = #{examId}
          AND ei.BRANCH_CODE = #{branchCode}
          AND ei.EXAM_DT = #{examDate}
          AND ei.USE_YN = 'Y'
        ORDER BY ic.ITEM_CODE ASC, ei.PART ASC
    </select>

    <!-- 시행종목 전체 건수 조회 -->
    <select id="selectExamItemListTotlCnt" resultType="int">
        SELECT COUNT(*)
        FROM TB_EXAM_ITEM ei
        INNER JOIN TB_ITEM_CODE ic
            ON ei.ITEM_CODE = ic.ITEM_CODE
            AND ic.USE_YN = 'Y'
        WHERE ei.EXAM_ID = #{examId}
          AND ei.BRANCH_CODE = #{branchCode}
          AND ei.EXAM_DT = #{examDate}
          AND ei.USE_YN = 'Y'
    </select>

</mapper>
```

---

## 6. VO (Value Object)

### 6.1 ExamItemSearchVO - 조회 조건

```java
package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 시행종목 조회 조건 Value Object.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class ExamItemSearchVO {

    /** 시험 ID */
    private Integer examId;

    /** 지사 코드 */
    private String branchCode;

    /** 시험일자 (YYYYMMDD) */
    private String examDate;

    /** 페이지 번호 */
    private int page = 1;

    /** 페이지당 건수 */
    private int size = 20;

    /** 시작 인덱스 */
    public int getStartIndex() {
        return (page - 1) * size;
    }

    /** 조회용 파라미터 (MyBatis LIMIT/OFFSET) */
    public int getPageStart() {
        return page;
    }

    public int getPageEnd() {
        return page + size - 1;
    }
}
```

### 6.2 BranchVO

```java
package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 지사 Value Object.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class BranchVO {

    /** 지사 코드 */
    private String branchCode;

    /** 지사명 */
    private String branchName;

    /** 사용여부 */
    private String useYn;
}
```

### 6.3 ExamInfoVO

```java
package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 시험정보 Value Object.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class ExamInfoVO {

    /** 시험 ID */
    private Integer examId;

    /** 시험명 */
    private String examName;

    /** 시험일자 (YYYYMMDD) */
    private String examDate;

    /** 지사 코드 */
    private String branchCode;

    /** 시행종목 건수 */
    private Integer itemCount;

    /** 사용여부 */
    private String useYn;
}
```

---

## 7. 공통 응답 DTO

### 7.1 ApiResponse - 공통 응답

```java
package kr.or.nqis.qis.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 공통 API 응답 DTO.
 *
 * @author NQIS-AR
 * @since 1.0.0
 * @param <T> 응답 데이터 타입
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    /**
     * 성공 응답 생성
     *
     * @param data 응답 데이터
     * @return 성공 응답 DTO
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "정상 처리되었습니다.");
    }

    /**
     * 실패 응답 생성
     *
     * @param message 오류 메시지
     * @return 실패 응답 DTO
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
```

### 7.2 PaginationResponse - 페이징 응답

```java
package kr.or.nqis.qis.exam.dto.response;

import lombok.Data;

/**
 * 페이징 응답 DTO.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class PaginationResponse {
    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPages;
    private boolean hasPrevious;
    private boolean hasNext;
}
```

---

## 8. 예외 처리 설계

### 8.1 BusinessException - 비즈니스 예외

```java
package kr.or.nqis.qis.exam.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
```

### 8.2 SystemException - 시스템 예외

```java
package kr.or.nqis.qis.exam.exception;

import lombok.Getter;

/**
 * 시스템 예외.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Getter
public class SystemException extends RuntimeException {

    private final String message;

    public SystemException(String message) {
        super(message);
        this.message = message;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
```

### 8.3 GlobalExceptionHandler - 전역 예외 처리

```java
package kr.or.nqis.qis.exam.exception;

import kr.or.nqis.qis.exam.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 Handler.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 로직 오류 - message: {}", e.getMessage());
        ApiResponse<Void> response = ApiResponse.error(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 유효성 검사 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("유효성 검사 오류 - message: {}", e.getMessage());
        ApiResponse<Void> response = ApiResponse.error("잘못된 요청입니다.");
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 시스템 예외 처리
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(SystemException e) {
        log.error("시스템 오류 발생", e);
        ApiResponse<Void> response = ApiResponse.error("시스템 오류가 발생했습니다.");
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예기치 않은 오류 발생", e);
        ApiResponse<Void> response = ApiResponse.error("시스템 오류가 발생했습니다.");
        return ResponseEntity.internalServerError().body(response);
    }
}
```

---

## 9. 데이터베이스 테이블 설계 (NQIS 표준 준수)

### 9.1 테이블 명명 규칙

| 테이블명 | 설명 |
|:---|:---|
| `TB_BRANCH` | 지사 정보 테이블 |
| `TB_EXAM_INFO` | 시험정보 테이블 |
| `TB_EXAM_ITEM` | 시행종목 테이블 |
| `TB_ITEM_CODE` | 종목코드 테이블 |
| `TB_SELECT_FIELD` | 선택분야 테이블 |

### 9.2 인덱스 명명 규칙

| 인덱스명 | 타입 | 대상 테이블 | 설명 |
|:---|:---|:---|:---|
| `PK_TB_EXAM_INFO` | Primary Key | TB_EXAM_INFO | 시험 ID |
| `IX_TB_EXAM_INFO_01` | Non-Unique | TB_EXAM_INFO | 지사 코드 + 사용여부 |
| `IX_TB_EXAM_ITEM_01` | Non-Unique | TB_EXAM_ITEM | 시험 ID + 지사 코드 + 시험일자 |
| `IX_TB_ITEM_CODE_01` | Non-Unique | TB_ITEM_CODE | 종목 코드 + 사용여부 |
| `FK_TB_EXAM_ITEM_01` | Foreign Key | TB_EXAM_ITEM | TB_ITEM_CODE 참조 |

### 9.3 주요 컬럼 정의

| 테이블 | 컬럼명 | 데이터 타입 | 길이 | 설명 |
|:---|:---|:---|:---|:---|
| TB_EXAM_INFO | EXAM_ID | NUMBER | 10 | 시험 ID (PK) |
| TB_EXAM_INFO | EXAM_NM | VARCHAR2 | 100 | 시험명 |
| TB_EXAM_INFO | EXAM_DT | CHAR | 8 | 시험일자 (YYYYMMDD) |
| TB_EXAM_INFO | BRANCH_CD | VARCHAR2 | 10 | 지사 코드 |
| TB_EXAM_INFO | USE_YN | CHAR | 1 | 사용여부 (Y/N) |
| TB_EXAM_ITEM | EXAM_ITEM_ID | NUMBER | 10 | 시행종목 ID (PK) |
| TB_EXAM_ITEM | EXAM_ID | NUMBER | 10 | 시험 ID (FK) |
| TB_EXAM_ITEM | ITEM_CODE | VARCHAR2 | 20 | 종목 코드 (FK) |
| TB_EXAM_ITEM | BRANCH_CD | VARCHAR2 | 10 | 지사 코드 |
| TB_EXAM_ITEM | EXAM_DT | CHAR | 8 | 시험일자 |
| TB_EXAM_ITEM | WORK_TYPE | VARCHAR2 | 10 | 작업구분 |
| TB_EXAM_ITEM | PART | VARCHAR2 | 10 | 부 |
| TB_EXAM_ITEM | EXPOSE_YN | CHAR | 1 | 시험일정공개여부 (Y/N) |
| TB_EXAM_ITEM | USE_YN | CHAR | 1 | 사용여부 (Y/N) |
| TB_ITEM_CODE | ITEM_CODE | VARCHAR2 | 20 | 종목 코드 (PK) |
| TB_ITEM_CODE | ITEM_NM | VARCHAR2 | 100 | 종목명 |
| TB_ITEM_CODE | USE_YN | CHAR | 1 | 사용여부 (Y/N) |
| TB_SELECT_FIELD | ITEM_CODE | VARCHAR2 | 20 | 종목 코드 (FK) |
| TB_SELECT_FIELD | SELECT_FIELD | VARCHAR2 | 10 | 선택분야 코드 |
| TB_SELECT_FIELD | SELECT_FIELD_NM | VARCHAR2 | 100 | 선택분야명 |
| TB_SELECT_FIELD | USE_YN | CHAR | 1 | 사용여부 (Y/N) |

---

## 10. API 호출 흐름도

```
[FRONTEND]                    [BACKEND API]                   [DATABASE]
    │                             │                              │
    │ 1. 지사 선택                │                              │
    ├─────────────────────────────►│                              │
    │    GET /api/v1/exams/infos  │                              │
    │    ?branchCode=BR001        │                              │
    │                             │── MyBatis SQL 실행          │
    │                             │    SELECT * FROM TB_EXAM_INFO│
    │                             │    WHERE BRANCH_CD = ?       │
    │                             │      AND USE_YN = 'Y'        │
    │                             │      (캐시 Hit 시 생략)      │
    │                             │                              │
    │ ◄── 시험정보 목록 ──────────┤                              │
    │    (팝업 표시)              │                              │
    │                             │                              │
    │ 2. 시험정보 선택 (팝업)      │                              │
    ├─────────────────────────────►│                              │
    │    시험ID 선택              │                              │
    │                             │── MyBatis SQL 실행          │
    │                             │    SELECT EXAM_DT            │
    │                             │    FROM TB_EXAM_INFO         │
    │                             │    WHERE EXAM_ID = ?         │
    │                             │                              │
    │ ◄── 시험일자 자동 세팅 ─────┤                              │
    │    (readonly)               │                              │
    │                             │                              │
    │ 3. 조회 버튼 클릭           │                              │
    ├─────────────────────────────►│                              │
    │    GET /api/v1/exams/items  │                              │
    │    ?examId=1&...&page=1     │                              │
    │                             │── 검증 (필수 조건)           │
    │                             │                              │
    │                             │── MyBatis SQL 실행          │
    │                             │    TB_EXAM_ITEM JOIN         │
    │                             │    TB_ITEM_CODE,             │
    │                             │    TB_SELECT_FIELD           │
    │                             │    ORDER BY ITEM_CODE ASC    │
    │                             │             , PART ASC       │
    │                             │    LIMIT 20 OFFSET 0         │
    │                             │                              │
    │ ◄── 시행종목 목록 + 페이징 ─┤                              │
    │                             │                              │
    │ 4. 엑셀 다운로드 클릭       │                              │
    ├─────────────────────────────►│                              │
    │    GET /api/v1/exams/items  │                              │
    │    /excel?examId=1&...      │                              │
    │                             │── MyBatis SQL 실행 (전체)   │
    │                             │                              │
    │                             │── Apache POI로 엑셀 생성     │
    │                             │                              │
    │ ◄── .xlsx 파일 다운로드 ────┤                              │
    │    (자동 저장)              │                              │
```

---

## 11. 검증 규칙 요약

| REQ | 검증 항목 | 규칙 | 예외 메시지 |
|:---|:---|:---|:---|
| REQ-001 | 지사 코드 입력 | `branchCode` 필수 | "지사 코드를 입력해 주세요." |
| REQ-001 | 시험정보 존재 | 조회 결과 0건 시 | "조회된 시험정보가 없습니다." |
| REQ-002 | 시험일자 readonly | 팝업 선택 후 수정 불가 | (프론트엔드 처리) |
| REQ-003 | 필수 조건 3개 | examId, branchCode, examDate 모두 필수 | "조회 조건을 확인해 주세요." |
| REQ-003 | 페이징 | 20건/페이지, 기본 정렬 ITEM_CODE→PART | - |
| REQ-003 | 조회 결과 없음 | 0건 시 | "조회된 데이터가 없습니다." |
| REQ-004 | 다운로드 전 조회 | 1건 이상 존재 시만 버튼 활성화 | "다운로드할 데이터가 없습니다." |

---

## 12. 캐시 전략

| 캐시명 | TTL | Heap Size | 적용 데이터 |
|:---|:---|:---|:---|
| `menuCache` | 12h | 200 | 시험정보 팝업 목록 |
| `commonCodeCache` | 24h | 500 | 공통 코드 목록 |

---

*문서 종료*
