package com.sig.exam.repository;

import com.sig.exam.entity.BranchInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<BranchInfo, String> {
    List<BranchInfo> findByUseYnOrderByBranchCodeAsc(String useYn);
}
