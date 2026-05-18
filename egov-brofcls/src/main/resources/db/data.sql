-- 원본 sql/ddl_brofcls.sql 의 샘플 데이터와 동일.

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
