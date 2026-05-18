package egovframework.exam.brofcls.service.impl;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import egovframework.exam.brofcls.service.BrofClsSearchVO;
import egovframework.exam.brofcls.service.BrofClsVO;
import egovframework.exam.brofcls.service.BrofVO;
import egovframework.exam.brofcls.service.TestInfoVO;

/**
 * 지사별 시행종목 조회 DAO.
 *
 * <p>전자정부프레임워크 5.0 표준 EgovAbstractMapper를 상속하여
 * MyBatis SqlSession을 통해 DB 접근을 수행한다.
 * Mapper namespace는 BrofCls.* 이다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
@Repository("brofClsDAO")
public class BrofClsDAO extends EgovAbstractMapper {

    /** API-001: 지사 목록 */
    public List<BrofVO> selectBrofList() {
        return selectList("BrofCls.selectBrofList");
    }

    /** API-002: 지사별 시험정보 목록 */
    public List<TestInfoVO> selectTestInfoListByBrof(String brofCd) {
        return selectList("BrofCls.selectTestInfoListByBrof", brofCd);
    }

    /** API-003: 시행종목 목록 전체 건수 */
    public int selectBrofClsListTotalCount(BrofClsSearchVO searchVO) {
        return (Integer) selectOne("BrofCls.selectBrofClsListTotalCount", searchVO);
    }

    /** API-003: 시행종목 목록 (페이징) */
    public List<BrofClsVO> selectBrofClsList(BrofClsSearchVO searchVO) {
        return selectList("BrofCls.selectBrofClsList", searchVO);
    }

    /** API-004: 엑셀용 전체 시행종목 목록 (페이징 없음) */
    public List<BrofClsVO> selectBrofClsListForExcel(BrofClsSearchVO searchVO) {
        return selectList("BrofCls.selectBrofClsListForExcel", searchVO);
    }
}
