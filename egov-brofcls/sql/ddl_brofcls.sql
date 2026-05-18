-- ============================================================
-- 지사별 시행종목 조회 - DDL 스크립트
-- 테이블정의서 V1.0 기준
-- DBMS: MariaDB / MySQL 호환
-- ============================================================

-- 외래키 제약 일시 해제 (재생성 편의)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS TB_TEST_CLS;
DROP TABLE IF EXISTS TB_BROF_TEST;
DROP TABLE IF EXISTS TB_FLD;
DROP TABLE IF EXISTS TB_CLS;
DROP TABLE IF EXISTS TB_TEST;
DROP TABLE IF EXISTS TB_BROF;

-- ============================================================
-- TB_BROF : 지사 마스터
-- ============================================================
CREATE TABLE TB_BROF (
    BROF_CD     VARCHAR(10)  NOT NULL COMMENT '지사코드',
    BROF_NM     VARCHAR(100) NOT NULL COMMENT '지사명',
    USE_YN      CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT '사용여부 Y/N',
    REG_DT      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    RGTR_NO     VARCHAR(10)  NULL     COMMENT '등록자번호',
    CONSTRAINT PK_BROF PRIMARY KEY (BROF_CD)
) COMMENT='지사';

-- ============================================================
-- TB_TEST : 시험정보 마스터
-- ============================================================
CREATE TABLE TB_TEST (
    TEST_NO     VARCHAR(10)  NOT NULL COMMENT '시험번호',
    TEST_NM     VARCHAR(100) NOT NULL COMMENT '시험명',
    TEST_YMD    CHAR(8)      NOT NULL COMMENT '시험일자 YYYYMMDD',
    USE_YN      CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    REG_DT      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    RGTR_NO     VARCHAR(10)  NULL     COMMENT '등록자번호',
    CONSTRAINT PK_TEST PRIMARY KEY (TEST_NO),
    INDEX IDX_TEST_01 (TEST_YMD)
) COMMENT='시험정보';

-- ============================================================
-- TB_BROF_TEST : 지사별 시험정보 매핑
-- ============================================================
CREATE TABLE TB_BROF_TEST (
    BROF_CD     VARCHAR(10) NOT NULL COMMENT '지사코드',
    TEST_NO     VARCHAR(10) NOT NULL COMMENT '시험번호',
    REG_DT      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    RGTR_NO     VARCHAR(10) NULL     COMMENT '등록자번호',
    CONSTRAINT PK_BROF_TEST PRIMARY KEY (BROF_CD, TEST_NO),
    CONSTRAINT FK_BROF_TEST_01 FOREIGN KEY (BROF_CD) REFERENCES TB_BROF(BROF_CD),
    CONSTRAINT FK_BROF_TEST_02 FOREIGN KEY (TEST_NO) REFERENCES TB_TEST(TEST_NO)
) COMMENT='지사별 시험정보 매핑';

-- ============================================================
-- TB_CLS : 종목 마스터
-- ============================================================
CREATE TABLE TB_CLS (
    CLS_NO      VARCHAR(10)  NOT NULL COMMENT '종목번호',
    CLS_NM      VARCHAR(100) NOT NULL COMMENT '종목명',
    USE_YN      CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    CONSTRAINT PK_CLS PRIMARY KEY (CLS_NO)
) COMMENT='종목';

-- ============================================================
-- TB_FLD : 선택분야 코드 마스터
-- ============================================================
CREATE TABLE TB_FLD (
    FLD_CD      VARCHAR(10)  NOT NULL COMMENT '분야코드',
    FLD_NM      VARCHAR(100) NOT NULL COMMENT '분야명',
    USE_YN      CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    CONSTRAINT PK_FLD PRIMARY KEY (FLD_CD)
) COMMENT='선택분야';

-- ============================================================
-- TB_TEST_CLS : 지사별 시행종목 (핵심 테이블)
-- ============================================================
CREATE TABLE TB_TEST_CLS (
    TEST_CLS_SN       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '시험종목일련번호',
    BROF_CD           VARCHAR(10)  NOT NULL COMMENT '지사코드',
    TEST_NO           VARCHAR(10)  NOT NULL COMMENT '시험번호',
    CLS_NO            VARCHAR(10)  NOT NULL COMMENT '종목번호',
    FLD_CD            VARCHAR(10)  NULL     COMMENT '분야코드',
    JOB_SE_CD         VARCHAR(10)  NOT NULL COMMENT '작업구분코드 1:필기 2:실기',
    TEST_PT_NO        VARCHAR(10)  NOT NULL COMMENT '시험부번호',
    TEST_SCHD_RLS_YN  CHAR(1)      NOT NULL DEFAULT 'N' COMMENT '시험일정공개여부',
    REG_DT            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    RGTR_NO           VARCHAR(10)  NULL     COMMENT '등록자번호',
    MDFCN_DT          DATETIME     NULL     COMMENT '수정일시',
    MDFR_NO           VARCHAR(10)  NULL     COMMENT '수정자번호',
    CONSTRAINT PK_TEST_CLS PRIMARY KEY (TEST_CLS_SN),
    CONSTRAINT FK_TEST_CLS_01 FOREIGN KEY (BROF_CD) REFERENCES TB_BROF(BROF_CD),
    CONSTRAINT FK_TEST_CLS_02 FOREIGN KEY (TEST_NO) REFERENCES TB_TEST(TEST_NO),
    CONSTRAINT FK_TEST_CLS_03 FOREIGN KEY (CLS_NO) REFERENCES TB_CLS(CLS_NO),
    CONSTRAINT FK_TEST_CLS_04 FOREIGN KEY (FLD_CD) REFERENCES TB_FLD(FLD_CD),
    INDEX IDX_TEST_CLS_01 (BROF_CD, TEST_NO, CLS_NO, TEST_PT_NO)
) COMMENT='지사별 시행종목';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 샘플 데이터
-- ============================================================
INSERT INTO TB_BROF (BROF_CD, BROF_NM) VALUES
    ('B001', '서울지사'),
    ('B002', '부산지사'),
    ('B003', '광주지사');

INSERT INTO TB_TEST (TEST_NO, TEST_NM, TEST_YMD) VALUES
    ('T001', '2026년 정기 기사 1회', '20260601'),
    ('T002', '2026년 정기 기사 2회', '20260901');

INSERT INTO TB_BROF_TEST (BROF_CD, TEST_NO) VALUES
    ('B001', 'T001'),
    ('B001', 'T002'),
    ('B002', 'T001');

INSERT INTO TB_CLS (CLS_NO, CLS_NM) VALUES
    ('A001', '정보처리기사'),
    ('A002', '전기기사'),
    ('B001', '건축기사'),
    ('B002', '토목기사');

INSERT INTO TB_FLD (FLD_CD, FLD_NM) VALUES
    ('01', '소프트웨어'),
    ('02', '전력설비'),
    ('03', '토목시공');

INSERT INTO TB_TEST_CLS (BROF_CD, TEST_NO, CLS_NO, FLD_CD, JOB_SE_CD, TEST_PT_NO, TEST_SCHD_RLS_YN) VALUES
    ('B001', 'T001', 'A001', '01', '1', '1', 'Y'),
    ('B001', 'T001', 'A001', '01', '2', '2', 'Y'),
    ('B001', 'T001', 'A002', '02', '1', '1', 'N'),
    ('B001', 'T001', 'B001', '01', '2', '1', 'Y'),
    ('B001', 'T001', 'B002', '03', '1', '2', 'Y');
