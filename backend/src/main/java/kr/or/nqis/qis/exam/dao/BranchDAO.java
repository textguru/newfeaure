package kr.or.nqis.qis.exam.dao;

import kr.or.nqis.qis.exam.vo.BranchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 지사 DAO Interface.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Mapper
public interface BranchDAO {

    /**
     * 사용 가능한 전체 지사 목록 조회.
     *
     * @return 지사 목록
     */
    List<BranchVO> selectBranchList();
}
