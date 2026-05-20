package kr.or.nqis.qis.exam.service;

import kr.or.nqis.qis.exam.dto.response.ExamItemListResponse;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;

/**
 * 시행종목 Service Interface (API-03).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
public interface ExamItemService {

    /**
     * REQ-003: 시행종목 목록 조회 (페이징 포함).
     *
     * @param searchVO 조회 조건 VO
     * @return 시행종목 목록 + 페이징 메타
     */
    ExamItemListResponse selectExamItemList(ExamItemSearchVO searchVO);
}
