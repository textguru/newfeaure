package com.sig.exam.dto.response;

import com.sig.exam.entity.BranchInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BranchResponse {
    private String branchCode;
    private String branchName;

    public static BranchResponse from(BranchInfo b) {
        return new BranchResponse(b.getBranchCode(), b.getBranchName());
    }
}
