package com.sig.exam.controller;

import com.sig.exam.dto.response.ApiResponse;
import com.sig.exam.dto.response.ExamItemResponse;
import com.sig.exam.dto.response.PaginationResponse;
import com.sig.exam.service.ExamItemService;
import com.sig.exam.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamItemController {

    private final ExamItemService examItemService;
    private final ExcelExportService excelExportService;

    @GetMapping("/items")
    public ApiResponse<PaginationResponse<ExamItemResponse>> getExamItems(
            @RequestParam(value = "examId", required = false) Integer examId,
            @RequestParam(value = "branchCode", required = false) String branchCode,
            @RequestParam(value = "examDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<ExamItemResponse> result =
                examItemService.searchExamItems(examId, branchCode, examDate, pageable);
        return ApiResponse.success(new PaginationResponse<>(result));
    }

    @GetMapping("/items/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam(value = "examId", required = false) Integer examId,
            @RequestParam(value = "branchCode", required = false) String branchCode,
            @RequestParam(value = "examDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate) {

        ExcelExportService.ExcelFile file =
                excelExportService.exportExcel(examId, branchCode, examDate);

        String encodedName = URLEncoder.encode(file.fileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file.content());
    }
}
