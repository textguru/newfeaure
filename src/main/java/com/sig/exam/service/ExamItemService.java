package com.sig.exam.service;

import com.sig.exam.controller.exception.BusinessException;
import com.sig.exam.dto.response.ExamItemResponse;
import com.sig.exam.repository.ExamItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamItemService {

    private final ExamItemRepository examItemRepository;

    /**
     * REQ-003: 시행종목 목록 조회.
     * - 필수 조건 검증 (examId, branchCode, examDate)
     * - 정렬: itemCode ASC → part ASC
     * - 페이징: 페이지 내 순번 1부터
     */
    public Page<ExamItemResponse> searchExamItems(Integer examId, String branchCode,
                                                  LocalDate examDate, Pageable pageable) {
        validateSearchConditions(examId, branchCode, examDate);

        Page<ExamItemResponse> results =
                examItemRepository.searchExamItems(examId, branchCode, examDate, pageable);

        long globalIndex = (long) pageable.getPageNumber() * pageable.getPageSize() + 1;
        for (ExamItemResponse r : results) {
            r.setSeq(globalIndex++);
        }
        return results;
    }

    private void validateSearchConditions(Integer examId, String branchCode, LocalDate examDate) {
        if (examId == null || branchCode == null || branchCode.isBlank() || examDate == null) {
            throw new BusinessException("조회 조건을 확인해 주세요.");
        }
    }
}
