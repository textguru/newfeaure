package com.sig.exam.controller;

import com.sig.exam.dto.response.ApiResponse;
import com.sig.exam.dto.response.BranchResponse;
import com.sig.exam.service.ExamBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamBranchController {

    private final ExamBranchService examBranchService;

    @GetMapping("/branches")
    public ApiResponse<List<BranchResponse>> getBranches() {
        return ApiResponse.success(examBranchService.getBranches());
    }
}
