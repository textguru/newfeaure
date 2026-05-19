package com.sig.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "SELECT_FIELD")
@IdClass(SelectFieldId.class)
@Getter
@Setter
@NoArgsConstructor
public class SelectField {

    @Id
    @Column(name = "ITEM_CODE", length = 20, nullable = false)
    private String itemCode;

    @Id
    @Column(name = "SELECT_FIELD", length = 20, nullable = false)
    private String selectField;

    @Column(name = "SELECT_FIELD_NAME", length = 100, nullable = false)
    private String selectFieldName;

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
