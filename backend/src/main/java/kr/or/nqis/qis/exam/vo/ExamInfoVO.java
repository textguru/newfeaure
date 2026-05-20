package kr.or.nqis.qis.exam.vo;

import lombok.Data;

/**
 * 시험정보 Value Object.
 * EXAM_INFO 테이블 매핑.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class ExamInfoVO {

    /** 시험 ID */
    private Integer examId;

    /** 시험명 */
    private String examName;

    /** 시험일자 (YYYYMMDD) */
    private String examDate;

    /** 지사 코드 */
    private String branchCode;

    /** 시험유형 */
    private String examType;

    /** 시험설명 */
    private String examDesc;

    /** 시행종목 건수 (조회용 파생 컬럼) */
    private Integer itemCount;

    /** 사용여부 (Y/N) */
    private String useYn;
}
