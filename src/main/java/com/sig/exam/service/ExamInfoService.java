package com.sig.exam.service;

import com.sig.exam.controller.exception.BusinessException;
import com.sig.exam.dto.response.ExamInfoPopupResponse;
import com.sig.exam.repository.ExamInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamInfoService {

    private final ExamInfoRepository examInfoRepository;

    /**
     * REQ-001: 지사별 시험정보 목록 조회 (팝업용). 시행종목 건수 포함.
     */
    public List<ExamInfoPopupResponse> searchExamInfosForPopup(String branchCode) {
        if (branchCode == null || branchCode.isBlank()) {
            throw new BusinessException("지사 코드를 입력해 주세요.");
        }
        return examInfoRepository.findByBranchCodeWithItemCount(branchCode);
    }
}
