package com.sig.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExamInfoPopupResponse {
    private final Integer examId;
    private final String examName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate examDate;

    private final Integer itemCount;

    public ExamInfoPopupResponse(Integer examId, String examName, LocalDate examDate, Long itemCount) {
        this.examId = examId;
        this.examName = examName;
        this.examDate = examDate;
        this.itemCount = itemCount == null ? 0 : itemCount.intValue();
    }
}
