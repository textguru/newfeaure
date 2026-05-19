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
@Table(name = "ITEM_CODE")
@Getter
@Setter
@NoArgsConstructor
public class ItemCode {

    @Id
    @Column(name = "ITEM_CODE", length = 20, nullable = false)
    private String itemCode;

    @Column(name = "ITEM_NAME", length = 100, nullable = false)
    private String itemName;

    @Column(name = "ITEM_TYPE", length = 20)
    private String itemType;

    @Column(name = "CERT_LEVEL", length = 20)
    private String certLevel;

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
