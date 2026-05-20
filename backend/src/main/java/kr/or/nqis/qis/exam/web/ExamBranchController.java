package kr.or.nqis.qis.exam.web;

import kr.or.nqis.qis.exam.dto.response.ApiResponse;
import kr.or.nqis.qis.exam.dto.response.BranchResponse;
import kr.or.nqis.qis.exam.service.ExamBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 지사 Controller (API-01).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams/branches")
public class ExamBranchController {

    private final ExamBranchService examBranchService;

    /**
     * API-01: 지사 목록 조회.
     *
     * @return 전체 지사 목록
     */
    @GetMapping
    public ApiResponse<List<BranchResponse>> getBranches() {
        log.debug("[API-01] 지사 목록 조회 요청");
        return ApiResponse.success(examBranchService.selectBranchList());
    }
}
