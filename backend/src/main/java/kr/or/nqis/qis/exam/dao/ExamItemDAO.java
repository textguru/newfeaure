package kr.or.nqis.qis.exam.dao;

import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 시행종목 DAO Interface.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Mapper
public interface ExamItemDAO {

    /**
     * 시행종목 목록 조회 (페이징 적용, REQ-003).
     *
     * @param searchVO 조회 조건
     * @return 시행종목 목록 (현재 페이지)
     */
    List<ExamItemResponse> selectExamItemList(ExamItemSearchVO searchVO);

    /**
     * 시행종목 전체 목록 조회 (페이징 없음, REQ-004 엑셀용).
     *
     * @param searchVO 조회 조건
     * @return 시행종목 전체 목록
     */
    List<ExamItemResponse> selectExamItemListAll(ExamItemSearchVO searchVO);

    /**
     * 시행종목 전체 건수 조회 (페이징 메타).
     *
     * @param searchVO 조회 조건
     * @return 전체 건수
     */
    int selectExamItemListTotlCnt(ExamItemSearchVO searchVO);
}
