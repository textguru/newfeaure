package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 지사 Value Object.
 * BRANCH_INFO 테이블 매핑.
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

    /** 지사 주소 */
    private String branchAddr;

    /** 지사 연락처 */
    private String branchTel;

    /** 사용여부 (Y/N) */
    private String useYn;
}
