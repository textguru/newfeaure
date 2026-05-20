package kr.or.nqis.qis.exam.web;

import kr.or.nqis.qis.exam.dto.response.ApiResponse;
import kr.or.nqis.qis.exam.dto.response.ExamItemListResponse;
import kr.or.nqis.qis.exam.service.ExamItemService;
import kr.or.nqis.qis.exam.service.ExcelExportService;
import kr.or.nqis.qis.exam.service.ExcelExportService.ExcelDownloadResult;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 시행종목 Controller (API-03, API-04).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams/items")
public class ExamItemController {

    private static final String EXCEL_MIME =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ExamItemService examItemService;
    private final ExcelExportService excelExportService;

    /**
     * API-03: 시행종목 목록 조회 (페이징).
     */
    @GetMapping
    public ApiResponse<ExamItemListResponse> getExamItems(
            @RequestParam("examId") Integer examId,
            @RequestParam("branchCode") String branchCode,
            @RequestParam("examDate") String examDate,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        log.debug("[API-03] 시행종목 조회 요청 - examId: {}, branchCode: {}, examDate: {}, page: {}, size: {}",
                examId, branchCode, examDate, page, size);

        ExamItemSearchVO searchVO = new ExamItemSearchVO();
        searchVO.setExamId(examId);
        searchVO.setBranchCode(branchCode);
        searchVO.setExamDate(examDate);
        searchVO.setPage(page);
        searchVO.setSize(size);

        return ApiResponse.success(examItemService.selectExamItemList(searchVO));
    }

    /**
     * API-04: 시행종목 엑셀 다운로드.
     */
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam("examId") Integer examId,
            @RequestParam("branchCode") String branchCode,
            @RequestParam("examDate") String examDate
    ) {
        log.debug("[API-04] 엑셀 다운로드 요청 - examId: {}, branchCode: {}, examDate: {}",
                examId, branchCode, examDate);

        ExcelDownloadResult result = excelExportService.exportExcel(examId, branchCode, examDate);
        String encodedFileName = URLEncoder.encode(result.fileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_MIME));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        headers.setContentLength(result.content().length);

        return new ResponseEntity<>(result.content(), headers, 200);
    }
}
