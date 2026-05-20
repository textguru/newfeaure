package kr.or.nqis.qis.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 시행종목 목록 + 페이징 응답 래퍼 DTO (API-03).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class ExamItemListResponse {

    private List<ExamItemResponse> list;
    private PaginationResponse pagination;
}
