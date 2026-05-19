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
@Table(name = "EXAM_ITEM")
@Getter
@Setter
@NoArgsConstructor
public class ExamItem {

    @Id
    @Column(name = "ITEM_SEQ", nullable = false)
    private Long itemSeq;

    @Column(name = "EXAM_ID", nullable = false)
    private Integer examId;

    @Column(name = "BRANCH_CODE", length = 10, nullable = false)
    private String branchCode;

    @Column(name = "EXAM_DATE", nullable = false)
    private LocalDate examDate;

    @Column(name = "ITEM_CODE", length = 20, nullable = false)
    private String itemCode;

    @Column(name = "SELECT_FIELD", length = 20)
    private String selectField;

    @Column(name = "WORK_TYPE", length = 20)
    private String workType;

    @Column(name = "PART", length = 10)
    private String part;

    @Column(name = "EXPOSE_YN", length = 1, nullable = false)
    private String exposeYn = "N";

    @Column(name = "MAX_CANDINATE")
    private Integer maxCandinate;

    @Column(name = "CURRENT_CANDINATE", nullable = false)
    private Integer currentCandinate = 0;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status = "READY";

    @Column(name = "REG_DT", nullable = false)
    private LocalDate regDt = LocalDate.now();

    @Column(name = "REG_ID", length = 50, nullable = false)
    private String regId = "SYSTEM";

    @Column(name = "UPD_DT", nullable = false)
    private LocalDate updDt = LocalDate.now();

    @Column(name = "UPD_ID", length = 50, nullable = false)
    private String updId = "SYSTEM";
}
