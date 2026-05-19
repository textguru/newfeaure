package com.sig.exam.repository;

import com.sig.exam.dto.response.ExamInfoPopupResponse;
import com.sig.exam.entity.ExamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamInfoRepository extends JpaRepository<ExamInfo, Integer> {

    @Query("""
            SELECT new com.sig.exam.dto.response.ExamInfoPopupResponse(
                e.examId, e.examName, e.examDate,
                (SELECT COUNT(i) FROM ExamItem i WHERE i.examId = e.examId))
            FROM ExamInfo e
            WHERE e.branchCode = :branchCode AND e.useYn = 'Y'
            ORDER BY e.examDate ASC, e.examId ASC
            """)
    List<ExamInfoPopupResponse> findByBranchCodeWithItemCount(@Param("branchCode") String branchCode);
}
