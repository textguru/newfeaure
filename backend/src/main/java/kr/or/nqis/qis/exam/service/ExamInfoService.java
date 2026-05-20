package kr.or.nqis.qis.exam.service;

import kr.or.nqis.qis.exam.dto.response.ExamInfoPopupResponse;

import java.util.List;

/**
 * 시험정보 Service Interface (API-02).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
public interface ExamInfoService {

    /**
     * REQ-001: 지사별 시험정보 목록 조회 (팝업용).
     *
     * @param branchCode 지사 코드 (필수)
     * @return 시험정보 응답 DTO 목록
     */
    List<ExamInfoPopupResponse> selectExamInfosForPopup(String branchCode);
}
