package kr.or.nqis.qis.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 공통 API 응답 DTO.
 *
 * @param <T> 응답 데이터 타입
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "정상 처리되었습니다.");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
