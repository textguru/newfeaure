package com.sig.exam.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamItemResponse {
    private long seq;
    private String itemCode;
    private String itemName;
    private String selectField;
    private String selectFieldName;
    private String workType;
    private String part;
    private String exposeYn;

    public ExamItemResponse(String itemCode, String itemName, String selectField,
                            String selectFieldName, String workType, String part, String exposeYn) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.selectField = selectField;
        this.selectFieldName = selectFieldName;
        this.workType = workType;
        this.part = part;
        this.exposeYn = exposeYn;
    }
}
