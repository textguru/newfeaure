package com.sig.exam.controller;

import com.sig.exam.dto.response.ApiResponse;
import com.sig.exam.dto.response.ExamInfoPopupResponse;
import com.sig.exam.service.ExamInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamInfoController {

    private final ExamInfoService examInfoService;

    @GetMapping("/infos")
    public ApiResponse<List<ExamInfoPopupResponse>> getExamInfos(
            @RequestParam("branchCode") String branchCode) {
        return ApiResponse.success(examInfoService.searchExamInfosForPopup(branchCode));
    }
}
