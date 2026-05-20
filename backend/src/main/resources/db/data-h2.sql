-- ========================================================================
-- 로컬 개발용 샘플 데이터 (H2)
-- ========================================================================

-- 지사
INSERT INTO BRANCH_INFO(BRANCH_CODE, BRANCH_NAME, USE_YN, REG_ID, UPD_ID)
VALUES ('BR001', '서울지사', 'Y', 'system', 'system');
INSERT INTO BRANCH_INFO(BRANCH_CODE, BRANCH_NAME, USE_YN, REG_ID, UPD_ID)
VALUES ('BR002', '부산지사', 'Y', 'system', 'system');
INSERT INTO BRANCH_INFO(BRANCH_CODE, BRANCH_NAME, USE_YN, REG_ID, UPD_ID)
VALUES ('BR003', '대구지사', 'Y', 'system', 'system');

-- 시험정보
INSERT INTO EXAM_INFO(EXAM_ID, EXAM_NAME, EXAM_DATE, BRANCH_CODE, EXAM_TYPE, USE_YN, REG_ID, UPD_ID)
VALUES (1, '2026년 1차 전기공사기사', DATE '2026-06-15', 'BR001', '필기', 'Y', 'system', 'system');
INSERT INTO EXAM_INFO(EXAM_ID, EXAM_NAME, EXAM_DATE, BRANCH_CODE, EXAM_TYPE, USE_YN, REG_ID, UPD_ID)
VALUES (2, '2026년 1차 용접산업기사', DATE '2026-06-20', 'BR001', '실기', 'Y', 'system', 'system');
INSERT INTO EXAM_INFO(EXAM_ID, EXAM_NAME, EXAM_DATE, BRANCH_CODE, EXAM_TYPE, USE_YN, REG_ID, UPD_ID)
VALUES (3, '2026년 3회 통합시험', DATE '2026-08-10', 'BR002', '통합', 'Y', 'system', 'system');

-- 종목코드
INSERT INTO ITEM_CODE(ITEM_CODE, ITEM_NAME, ITEM_TYPE, USE_YN, REG_ID, UPD_ID)
VALUES ('I001', '전기공사기사', '기사', 'Y', 'system', 'system');
INSERT INTO ITEM_CODE(ITEM_CODE, ITEM_NAME, ITEM_TYPE, USE_YN, REG_ID, UPD_ID)
VALUES ('I002', '용접산업기사', '산업기사', 'Y', 'system', 'system');
INSERT INTO ITEM_CODE(ITEM_CODE, ITEM_NAME, ITEM_TYPE, USE_YN, REG_ID, UPD_ID)
VALUES ('I003', '국어', '학과', 'Y', 'system', 'system');

-- 선택분야
INSERT INTO SELECT_FIELD(ITEM_CODE, SELECT_FIELD, SELECT_FIELD_NAME, USE_YN, REG_ID, UPD_ID)
VALUES ('I001', 'S01', '고압 전기작업', 'Y', 'system', 'system');
INSERT INTO SELECT_FIELD(ITEM_CODE, SELECT_FIELD, SELECT_FIELD_NAME, USE_YN, REG_ID, UPD_ID)
VALUES ('I001', 'S02', '저압 전기작업', 'Y', 'system', 'system');
INSERT INTO SELECT_FIELD(ITEM_CODE, SELECT_FIELD, SELECT_FIELD_NAME, USE_YN, REG_ID, UPD_ID)
VALUES ('I002', 'S01', '아크용접', 'Y', 'system', 'system');

-- 시행종목
INSERT INTO EXAM_ITEM(ITEM_SEQ, EXAM_ID, BRANCH_CODE, EXAM_DATE, ITEM_CODE,
                     SELECT_FIELD, WORK_TYPE, PART, EXPOSE_YN, REG_ID, UPD_ID)
VALUES (1, 1, 'BR001', DATE '2026-06-15', 'I001', 'S01', '필기', '1부', 'Y', 'system', 'system');
INSERT INTO EXAM_ITEM(ITEM_SEQ, EXAM_ID, BRANCH_CODE, EXAM_DATE, ITEM_CODE,
                     SELECT_FIELD, WORK_TYPE, PART, EXPOSE_YN, REG_ID, UPD_ID)
VALUES (2, 1, 'BR001', DATE '2026-06-15', 'I001', 'S02', '필기', '1부', 'Y', 'system', 'system');
INSERT INTO EXAM_ITEM(ITEM_SEQ, EXAM_ID, BRANCH_CODE, EXAM_DATE, ITEM_CODE,
                     SELECT_FIELD, WORK_TYPE, PART, EXPOSE_YN, REG_ID, UPD_ID)
VALUES (3, 1, 'BR001', DATE '2026-06-15', 'I001', 'S01', '실기', '2부', 'N', 'system', 'system');
