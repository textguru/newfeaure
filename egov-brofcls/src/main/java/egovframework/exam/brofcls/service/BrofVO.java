package egovframework.exam.brofcls.service;

import java.io.Serializable;

/**
 * 지사 VO. TB_BROF 매핑.
 *
 * <p>API-001(지사 드롭다운 목록) 응답에 사용된다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
public class BrofVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 지사코드 (PK) */
    private String brofCd;

    /** 지사명 */
    private String brofNm;

    public String getBrofCd() {
        return brofCd;
    }

    public void setBrofCd(String brofCd) {
        this.brofCd = brofCd;
    }

    public String getBrofNm() {
        return brofNm;
    }

    public void setBrofNm(String brofNm) {
        this.brofNm = brofNm;
    }
}
