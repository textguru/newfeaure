package egovframework.exam.brofcls.service;

import java.io.Serializable;

/**
 * 지사별 시행종목 목록 행 VO.
 *
 * <p>화면설계서 CMP-008 그리드 컬럼 정의에 1:1 매핑된다.
 * TB_TEST_CLS를 중심으로 TB_CLS, TB_FLD 조인 결과를 담는다.
 * jobSeNm은 JOB_SE_CD를 화면 표시값(필기/실기)으로 변환한 값이다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
public class BrofClsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 순번 (조회 결과 기준 1부터, 페이징 누적) */
    private int rowNum;

    /** 종목코드 (CLS_NO) */
    private String clsNo;

    /** 종목명 (CLS_NM) */
    private String clsNm;

    /** 선택분야 코드 (FLD_CD, nullable) */
    private String fldCd;

    /** 선택분야명 (FLD_NM, nullable) */
    private String fldNm;

    /** 작업구분명 (필기/실기) */
    private String jobSeNm;

    /** 부 (TEST_PT_NO) */
    private String testPtNo;

    /** 시험일정공개여부 (Y/N) */
    private String testSchdRlsYn;

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public String getClsNo() {
        return clsNo;
    }

    public void setClsNo(String clsNo) {
        this.clsNo = clsNo;
    }

    public String getClsNm() {
        return clsNm;
    }

    public void setClsNm(String clsNm) {
        this.clsNm = clsNm;
    }

    public String getFldCd() {
        return fldCd;
    }

    public void setFldCd(String fldCd) {
        this.fldCd = fldCd;
    }

    public String getFldNm() {
        return fldNm;
    }

    public void setFldNm(String fldNm) {
        this.fldNm = fldNm;
    }

    public String getJobSeNm() {
        return jobSeNm;
    }

    public void setJobSeNm(String jobSeNm) {
        this.jobSeNm = jobSeNm;
    }

    public String getTestPtNo() {
        return testPtNo;
    }

    public void setTestPtNo(String testPtNo) {
        this.testPtNo = testPtNo;
    }

    public String getTestSchdRlsYn() {
        return testSchdRlsYn;
    }

    public void setTestSchdRlsYn(String testSchdRlsYn) {
        this.testSchdRlsYn = testSchdRlsYn;
    }
}
