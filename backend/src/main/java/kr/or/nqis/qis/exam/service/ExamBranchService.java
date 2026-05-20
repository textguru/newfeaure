package kr.or.nqis.qis.exam.service;

import kr.or.nqis.qis.exam.dto.response.BranchResponse;

import java.util.List;

/**
 * 지사 Service Interface (API-01).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
public interface ExamBranchService {

    /**
     * 사용 가능한 전체 지사 목록 조회.
     *
     * @return 지사 응답 DTO 목록
     */
    List<BranchResponse> selectBranchList();
}
