package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 시행종목 조회 조건 Value Object.
 * REQ-003 / REQ-004 조회 입력값.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class ExamItemSearchVO {

    /** 시험 ID (필수) */
    private Integer examId;

    /** 지사 코드 (필수) */
    private String branchCode;

    /** 시험일자 YYYYMMDD (필수) */
    private String examDate;

    /** 페이지 번호 (기본 1) */
    private int page = 1;

    /** 페이지당 건수 (기본 20) */
    private int size = 20;

    /** OFFSET 계산 (MyBatis 페이징용) */
    public int getOffset() {
        return (page - 1) * size;
    }

    /** Oracle ROWNUM 페이징 시작 */
    public int getRowStart() {
        return getOffset() + 1;
    }

    /** Oracle ROWNUM 페이징 끝 */
    public int getRowEnd() {
        return page * size;
    }
}
