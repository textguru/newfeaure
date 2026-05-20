package kr.or.nqis.qis.exam.dao;

import kr.or.nqis.qis.exam.vo.ExamInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 시험정보 DAO Interface.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Mapper
public interface ExamInfoDAO {

    /**
     * 지사별 시험정보 목록 조회 (REQ-001).
     *
     * @param searchVO 조회 조건 (branchCode)
     * @return 시험정보 목록 (시행종목 건수 포함)
     */
    List<ExamInfoVO> selectExamInfoList(ExamInfoVO searchVO);

    /**
     * 단건 시험정보 조회 (REQ-004 파일명 생성용).
     *
     * @param examInfoVO 시험 ID
     * @return 시험정보
     */
    ExamInfoVO selectExamInfo(ExamInfoVO examInfoVO);
}
