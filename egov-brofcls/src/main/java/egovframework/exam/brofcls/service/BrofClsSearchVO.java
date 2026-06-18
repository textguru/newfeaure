package egovframework.exam.brofcls.service;

import java.io.Serializable;

/**
 * 지사별 시행종목 조회 검색 조건 VO.
 *
 * <p>SCR-001 화면의 조회 조건(지사, 시험정보, 시험일자)과
 * 페이징 정보를 담는다. API-003 / API-004 요청 파라미터로 사용된다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
public class BrofClsSearchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 지사코드 (필수) */
    private String brofCd;

    /** 시험번호 (필수) */
    private String testNo;

    /** 시험일자 YYYYMMDD (필수) */
    private String testYmd;

    /** 현재 페이지 번호 (1-base, 기본값 1) */
    private int pageNo = 1;

    /** 페이지당 건수 (REQ-003 규칙5: 20건 고정) */
    private int pageSize = 20;

    /** 페이징 OFFSET 계산용 (DAO 내부 사용) */
    public int getFirstIndex() {
        return (pageNo - 1) * pageSize;
    }

    public String getBrofCd() {
        return brofCd;
    }

    public void setBrofCd(String brofCd) {
        this.brofCd = brofCd;
    }

    public String getTestNo() {
        return testNo;
    }

    public void setTestNo(String testNo) {
        this.testNo = testNo;
    }

    public String getTestYmd() {
        return testYmd;
    }

    public void setTestYmd(String testYmd) {
        this.testYmd = testYmd;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
