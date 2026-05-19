package com.sig.exam.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PaginationResponse<T> {
    private final List<T> list;
    private final Pagination pagination;

    public PaginationResponse(Page<T> page) {
        this.list = page.getContent();
        this.pagination = new Pagination(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasPrevious(),
                page.hasNext());
    }

    @Getter
    public static class Pagination {
        private final int currentPage;
        private final int pageSize;
        private final long totalCount;
        private final int totalPages;
        private final boolean hasPrevious;
        private final boolean hasNext;

        public Pagination(int currentPage, int pageSize, long totalCount,
                          int totalPages, boolean hasPrevious, boolean hasNext) {
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalCount = totalCount;
            this.totalPages = totalPages;
            this.hasPrevious = hasPrevious;
            this.hasNext = hasNext;
        }
    }
}
