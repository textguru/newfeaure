package egovframework.exam.brofcls.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import egovframework.exam.brofcls.service.BrofClsListResultVO;
import egovframework.exam.brofcls.service.BrofClsSearchVO;
import egovframework.exam.brofcls.service.BrofClsVO;
import egovframework.exam.brofcls.service.BrofVO;
import egovframework.exam.brofcls.service.EgovBrofClsService;
import egovframework.exam.brofcls.service.TestInfoVO;

/**
 * 지사별 시행종목 조회 Service 구현체.
 *
 * <p>전자정부프레임워크 5.0 표준 EgovAbstractServiceImpl을 상속한다.
 * 화면설계서 6장의 화면-API-REQ 매핑에 따라 4개 API를 구현한다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
@Service("egovBrofClsService")
public class EgovBrofClsServiceImpl extends EgovAbstractServiceImpl
        implements EgovBrofClsService {

    @Resource(name = "brofClsDAO")
    private BrofClsDAO brofClsDAO;

    @Override
    public List<BrofVO> selectBrofList() {
        return brofClsDAO.selectBrofList();
    }

    @Override
    public List<TestInfoVO> selectTestInfoListByBrof(String brofCd) {
        // REQ-001 데이터 요구사항: 지사 기준 필터링은 DAO/SQL에서 수행
        if (brofCd == null || brofCd.trim().isEmpty()) {
            throw new IllegalArgumentException("지사코드는 필수입니다.");
        }
        return brofClsDAO.selectTestInfoListByBrof(brofCd);
    }

    @Override
    public BrofClsListResultVO selectBrofClsList(BrofClsSearchVO searchVO) {
        // REQ-003 규칙4: 시험정보, 지사, 시험일자 모두 필수
        validateRequiredSearchConditions(searchVO);

        int totalCount = brofClsDAO.selectBrofClsListTotalCount(searchVO);

        BrofClsListResultVO result = new BrofClsListResultVO();
        result.setTotalCount(totalCount);
        result.setPageNo(searchVO.getPageNo());
        result.setPageSize(searchVO.getPageSize());
        result.setTotalPages(calculateTotalPages(totalCount, searchVO.getPageSize()));

        if (totalCount == 0) {
            // REQ-003 예외처리1: 조회 결과 없을 경우 빈 목록 반환
            // 메시지 표시는 화면 책임 (MSG-002)
            result.setList(java.util.Collections.<BrofClsVO>emptyList());
            return result;
        }

        List<BrofClsVO> list = brofClsDAO.selectBrofClsList(searchVO);

        // REQ-003 규칙3: 순번 부여 (페이징 누적 기준)
        int rowOffset = searchVO.getFirstIndex();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRowNum(rowOffset + i + 1);
        }
        result.setList(list);

        return result;
    }

    @Override
    public List<BrofClsVO> selectBrofClsListForExcel(BrofClsSearchVO searchVO) {
        validateRequiredSearchConditions(searchVO);

        List<BrofClsVO> list = brofClsDAO.selectBrofClsListForExcel(searchVO);
        // 엑셀 다운로드 시에도 순번 부여 (전체 목록 기준 1부터)
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRowNum(i + 1);
        }
        return list;
    }

    /**
     * REQ-003 규칙4 / 화면설계서 MSG-003 대응 필수 조건 검증.
     */
    private void validateRequiredSearchConditions(BrofClsSearchVO searchVO) {
        if (searchVO == null) {
            throw new IllegalArgumentException("조회 조건을 확인해 주세요.");
        }
        if (isEmpty(searchVO.getBrofCd())
                || isEmpty(searchVO.getTestNo())
                || isEmpty(searchVO.getTestYmd())) {
            throw new IllegalArgumentException("조회 조건을 확인해 주세요.");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int calculateTotalPages(int totalCount, int pageSize) {
        if (totalCount == 0 || pageSize <= 0) {
            return 0;
        }
        return (totalCount + pageSize - 1) / pageSize;
    }
}
