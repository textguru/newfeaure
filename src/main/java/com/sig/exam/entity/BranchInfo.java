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
@Table(name = "BRANCH_INFO")
@Getter
@Setter
@NoArgsConstructor
public class BranchInfo {

    @Id
    @Column(name = "BRANCH_CODE", length = 10, nullable = false)
    private String branchCode;

    @Column(name = "BRANCH_NAME", length = 100, nullable = false)
    private String branchName;

    @Column(name = "BRANCH_ADDR", length = 200)
    private String branchAddr;

    @Column(name = "BRANCH_TEL", length = 20)
    private String branchTel;

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
