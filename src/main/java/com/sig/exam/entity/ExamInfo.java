package com.sig.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "EXAM_INFO")
@Getter
@Setter
@NoArgsConstructor
public class ExamInfo {

    @Id
    @Column(name = "EXAM_ID", nullable = false)
    private Integer examId;

    @Column(name = "EXAM_NAME", length = 200, nullable = false)
    private String examName;

    @Column(name = "EXAM_DATE", nullable = false)
    private LocalDate examDate;

    @Column(name = "BRANCH_CODE", length = 10, nullable = false)
    private String branchCode;

    @Column(name = "EXAM_TYPE", length = 20)
    private String examType;

    @Column(name = "EXAM_DESC", length = 500)
    private String examDesc;

    @Column(name = "USE_YN", length = 1, nullable = false)
    private String useYn = "Y";

    @Column(name = "REG_DT", nullable = false)
    private LocalDate regDt = LocalDate.now();

    @Column(name = "REG_ID", length = 50, nullable = false)
    private String regId = "SYSTEM";

    @Column(name = "UPD_DT", nullable = false)
    private LocalDate updDt = LocalDate.now();

    @Column(name = "UPD_ID", length = 50, nullable = false)
    private String updId = "SYSTEM";
}
