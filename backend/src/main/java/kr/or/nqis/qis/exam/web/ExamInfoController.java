package kr.or.nqis.qis.exam.web;

import kr.or.nqis.qis.exam.dto.response.ApiResponse;
import kr.or.nqis.qis.exam.dto.response.ExamInfoPopupResponse;
import kr.or.nqis.qis.exam.service.ExamInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 시험정보 Controller (API-02).
 * REQ-001: 시험정보 팝업 조회.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams/infos")
public class ExamInfoController {

    private final ExamInfoService examInfoService;

    /**
     * API-02: 지사별 시험정보 목록 조회 (팝업).
     *
     * @param branchCode 지사 코드 (필수)
     * @return 시험정보 목록
     */
    @GetMapping
    public ApiResponse<List<ExamInfoPopupResponse>> getExamInfos(@RequestParam("branchCode") String branchCode) {
        log.debug("[API-02] 시험정보 팝업 조회 요청 - branchCode: {}", branchCode);
        return ApiResponse.success(examInfoService.selectExamInfosForPopup(branchCode));
    }
}
