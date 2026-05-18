package egovframework.exam.brofcls.service;

import java.io.Serializable;
import java.util.List;

/**
 * 시행종목 목록 조회 응답 VO.
 *
 * <p>API-003 응답에 사용되며 페이징 메타데이터를 함께 제공한다.
 * 화면설계서 CMP-006(전체건수), CMP-009(페이지 네비게이션) 데이터 소스이다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
public class BrofClsListResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 목록 데이터 */
    private List<BrofClsVO> list;

    /** 전체 건수 */
    private int totalCount;

    /** 현재 페이지 */
    private int pageNo;

    /** 페이지당 건수 */
    private int pageSize;

    /** 전체 페이지 수 */
    private int totalPages;

    public List<BrofClsVO> getList() {
        return list;
    }

    public void setList(List<BrofClsVO> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
