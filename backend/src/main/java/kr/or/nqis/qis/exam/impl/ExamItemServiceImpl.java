package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.ExamItemDAO;
import kr.or.nqis.qis.exam.dto.response.ExamItemListResponse;
import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.dto.response.PaginationResponse;
import kr.or.nqis.qis.exam.exception.BusinessException;
import kr.or.nqis.qis.exam.service.ExamItemService;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 시행종목 Service 구현체 (API-03).
 * REQ-003 시행종목 목록 조회.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamItemServiceImpl implements ExamItemService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ExamItemDAO examItemDAO;

    /**
     * REQ-003: 시행종목 목록 조회 (페이징).
     * - 필수 조건 검증 (examId, branchCode, examDate)
     * - 정렬: ITEM_CODE ASC -> PART ASC
     * - 페이징: 20건/페이지 기본
     */
    @Override
    public ExamItemListResponse selectExamItemList(ExamItemSearchVO searchVO) {
        validateSearchConditions(searchVO);
        normalizePaging(searchVO);

        int totalCount = examItemDAO.selectExamItemListTotlCnt(searchVO);

        List<ExamItemResponse> list = totalCount == 0
                ? List.of()
                : examItemDAO.selectExamItemList(searchVO);

        // 순번 부여 (전체 결과 기준, 페이지에 따라 연속)
        long seq = searchVO.getOffset() + 1L;
        for (ExamItemResponse response : list) {
            response.setSeq(seq++);
        }

        PaginationResponse pagination = PaginationResponse.of(
                searchVO.getPage(),
                searchVO.getSize(),
                totalCount
        );

        return new ExamItemListResponse(list, pagination);
    }

    /**
     * REQ-003 필수 조건 검증.
     * 누락 시 "조회 조건을 확인해 주세요." 메시지로 BusinessException 발생.
     */
    private void validateSearchConditions(ExamItemSearchVO searchVO) {
        if (searchVO == null
                || searchVO.getExamId() == null
                || searchVO.getBranchCode() == null
                || searchVO.getBranchCode().isBlank()
                || searchVO.getExamDate() == null
                || searchVO.getExamDate().isBlank()) {
            throw new BusinessException("조회 조건을 확인해 주세요.");
        }
    }

    /**
     * 페이징 기본값 보정.
     */
    private void normalizePaging(ExamItemSearchVO searchVO) {
        if (searchVO.getPage() < 1) {
            searchVO.setPage(1);
        }
        if (searchVO.getSize() < 1) {
            searchVO.setSize(DEFAULT_PAGE_SIZE);
        }
    }
}
