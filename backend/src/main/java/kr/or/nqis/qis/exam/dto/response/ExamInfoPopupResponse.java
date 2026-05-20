package kr.or.nqis.qis.exam.dto.response;

import kr.or.nqis.qis.exam.vo.ExamInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 시험정보 팝업 응답 DTO (API-02).
 * REQ-001 시험정보 선택 팝업용.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class ExamInfoPopupResponse {

    private Integer examId;
    private String examName;
    private String examDate;
    private Integer itemCount;

    public static ExamInfoPopupResponse from(ExamInfoVO vo) {
        return new ExamInfoPopupResponse(
                vo.getExamId(),
                vo.getExamName(),
                vo.getExamDate(),
                vo.getItemCount() != null ? vo.getItemCount() : 0
        );
    }
}
