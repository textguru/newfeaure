package egovframework.exam.brofcls.service;

import java.util.List;

/**
 * 지사별 시행종목 조회 Service 인터페이스.
 *
 * <p>화면설계서 6장 화면-API-REQ 매핑에 따라 다음 4개 API를 제공한다.</p>
 * <ul>
 *   <li>API-001 / REQ-002 : 지사 드롭다운 목록 조회</li>
 *   <li>API-002 / REQ-001 : 지사별 시험정보 목록 조회 (팝업)</li>
 *   <li>API-003 / REQ-003 : 지사별 시행종목 목록 조회 (페이징)</li>
 *   <li>API-004 / REQ-004 : 지사별 시행종목 전체 조회 (엑셀 다운로드용)</li>
 * </ul>
 *
 * @author AIDD
 * @since 2026-05-18
 */
public interface EgovBrofClsService {

    /**
     * API-001: 사용중인 지사 목록 조회.
     *
     * @return 지사 목록 (지사명 오름차순)
     */
    List<BrofVO> selectBrofList();

    /**
     * API-002: 선택된 지사에서 시행되는 시험정보 목록 조회.
     *
     * <p>TB_BROF_TEST 매핑 기반으로 필터링한다.
     * REQ-001 규칙2: 선택된 지사에 해당하는 데이터만 표시.</p>
     *
     * @param brofCd 지사코드
     * @return 시험정보 목록
     */
    List<TestInfoVO> selectTestInfoListByBrof(String brofCd);

    /**
     * API-003: 시행종목 목록을 페이징하여 조회.
     *
     * <p>REQ-003 규칙6 정렬 기준 적용: 종목코드 ASC → 부 ASC.</p>
     *
     * @param searchVO 검색 조건 (brofCd, testNo, testYmd, pageNo, pageSize)
     * @return 목록 + 페이징 메타
     */
    BrofClsListResultVO selectBrofClsList(BrofClsSearchVO searchVO);

    /**
     * API-004: 엑셀 다운로드용 전체 목록 조회 (페이징 없음).
     *
     * <p>REQ-004 규칙2: 다운로드 건수 제한 없음. 정렬 기준은 화면 목록과 동일.</p>
     *
     * @param searchVO 검색 조건 (brofCd, testNo, testYmd)
     * @return 전체 목록
     */
    List<BrofClsVO> selectBrofClsListForExcel(BrofClsSearchVO searchVO);
}
