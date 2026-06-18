package egovframework.exam.brofcls.service;

import java.io.Serializable;

/**
 * 시험정보 VO. TB_TEST + TB_TEST_CLS 집계 매핑.
 *
 * <p>POP-001(시험정보 선택 팝업, API-002)의 응답 항목으로 사용된다.
 * clsCount는 해당 시험의 시행종목 개수 집계값이다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
public class TestInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 시험번호 (PK) */
    private String testNo;

    /** 시험명 */
    private String testNm;

    /** 시험일자 YYYYMMDD */
    private String testYmd;

    /** 시행종목 수 (TB_TEST_CLS COUNT) */
    private int clsCount;

    public String getTestNo() {
        return testNo;
    }

    public void setTestNo(String testNo) {
        this.testNo = testNo;
    }

    public String getTestNm() {
        return testNm;
    }

    public void setTestNm(String testNm) {
        this.testNm = testNm;
    }

    public String getTestYmd() {
        return testYmd;
    }

    public void setTestYmd(String testYmd) {
        this.testYmd = testYmd;
    }

    public int getClsCount() {
        return clsCount;
    }

    public void setClsCount(int clsCount) {
        this.clsCount = clsCount;
    }
}
