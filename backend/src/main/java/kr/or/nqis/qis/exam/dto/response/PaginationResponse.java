package kr.or.nqis.qis.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 페이징 응답 DTO.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class PaginationResponse {

    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPages;
    private boolean hasPrevious;
    private boolean hasNext;

    public static PaginationResponse of(int currentPage, int pageSize, int totalCount) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 0;
        boolean hasPrev = currentPage > 1;
        boolean hasNext = currentPage < totalPages;
        return new PaginationResponse(currentPage, pageSize, totalCount, totalPages, hasPrev, hasNext);
    }
}
