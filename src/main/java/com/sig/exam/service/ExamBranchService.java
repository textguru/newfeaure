package com.sig.exam.service;

import com.sig.exam.dto.response.BranchResponse;
import com.sig.exam.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamBranchService {

    private final BranchRepository branchRepository;

    public List<BranchResponse> getBranches() {
        return branchRepository.findByUseYnOrderByBranchCodeAsc("Y").stream()
                .map(BranchResponse::from)
                .toList();
    }
}
