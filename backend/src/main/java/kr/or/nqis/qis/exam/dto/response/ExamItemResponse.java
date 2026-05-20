package kr.or.nqis.qis.exam.dto.response;

import lombok.Data;

/**
 * 시행종목 응답 DTO (API-03).
 * REQ-003 시행종목 목록 조회 결과.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
public class ExamItemResponse {

    /** 순번 */
    private long seq;

    /** 종목 코드 */
    private String itemCode;

    /** 종목명 */
    private String itemName;

    /** 선택분야 코드 */
    private String selectField;

    /** 선택분야명 */
    private String selectFieldName;

    /** 작업구분 (필기, 실기, 면접 등) */
    private String workType;

    /** 부 */
    private String part;

    /** 시험일정공개여부 (Y/N) */
    private String exposeYn;
}
