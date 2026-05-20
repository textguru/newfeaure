package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 시행종목 Value Object.
 * EXAM_ITEM 테이블 매핑.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class ExamItemVO {

    /** 시행종목 순번 (PK) */
    private Long itemSeq;

    /** 시험 ID (FK) */
    private Integer examId;

    /** 지사 코드 (FK) */
    private String branchCode;

    /** 시험일자 (YYYYMMDD) */
    private String examDate;

    /** 종목 코드 (FK) */
    private String itemCode;

    /** 종목명 (조인 결과) */
    private String itemName;

    /** 선택분야 코드 */
    private String selectField;

    /** 선택분야명 (조인 결과) */
    private String selectFieldName;

    /** 작업구분 (필기, 실기, 면접 등) */
    private String workType;

    /** 부 (1부, 2부 등) */
    private String part;

    /** 시험일정공개여부 (Y/N) */
    private String exposeYn;
}
