package com.sig.exam.repository;

import com.sig.exam.dto.response.ExamItemResponse;
import com.sig.exam.entity.ExamItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExamItemRepository extends JpaRepository<ExamItem, Long> {

    String SELECT_CLAUSE = """
            SELECT new com.sig.exam.dto.response.ExamItemResponse(
                ei.itemCode, ic.itemName, ei.selectField, sf.selectFieldName,
                ei.workType, ei.part, ei.exposeYn)
            FROM ExamItem ei
            JOIN ItemCode ic ON ic.itemCode = ei.itemCode
            LEFT JOIN SelectField sf ON sf.itemCode = ei.itemCode AND sf.selectField = ei.selectField
            WHERE ei.examId = :examId AND ei.branchCode = :branchCode AND ei.examDate = :examDate
            ORDER BY ei.itemCode ASC, ei.part ASC
            """;

    String COUNT_CLAUSE = """
            SELECT COUNT(ei) FROM ExamItem ei
            WHERE ei.examId = :examId AND ei.branchCode = :branchCode AND ei.examDate = :examDate
            """;

    @Query(value = SELECT_CLAUSE, countQuery = COUNT_CLAUSE)
    Page<ExamItemResponse> searchExamItems(@Param("examId") Integer examId,
                                           @Param("branchCode") String branchCode,
                                           @Param("examDate") LocalDate examDate,
                                           Pageable pageable);

    @Query(SELECT_CLAUSE)
    List<ExamItemResponse> searchAllExamItems(@Param("examId") Integer examId,
                                              @Param("branchCode") String branchCode,
                                              @Param("examDate") LocalDate examDate);
}
