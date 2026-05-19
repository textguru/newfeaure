package com.sig.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "정상 처리되었습니다.");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
