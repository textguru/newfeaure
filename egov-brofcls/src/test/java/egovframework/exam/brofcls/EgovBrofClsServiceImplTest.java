package egovframework.exam.brofcls;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import egovframework.exam.brofcls.service.BrofClsListResultVO;
import egovframework.exam.brofcls.service.BrofClsSearchVO;
import egovframework.exam.brofcls.service.BrofClsVO;
import egovframework.exam.brofcls.service.BrofVO;
import egovframework.exam.brofcls.service.TestInfoVO;
import egovframework.exam.brofcls.service.impl.BrofClsDAO;
import egovframework.exam.brofcls.service.impl.EgovBrofClsServiceImpl;

/**
 * EgovBrofClsService 단위 테스트.
 *
 * <p>AIDD 방법론 표준 적용: AI가 given/when 단계를 생성하고,
 * then(assert) 단계는 개발자가 비즈니스 로직을 이해한 뒤 작성하도록
 * TODO로 남겨둔다. 이는 개발자의 통과적 수용을 방지하기 위한 설계이다.</p>
 *
 * <p>검증 기준은 요구사항정의서 각 REQ의 Acceptance Criteria와 일치해야 한다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
@ExtendWith(MockitoExtension.class)
class EgovBrofClsServiceImplTest {

    @Mock
    private BrofClsDAO brofClsDAO;

    @InjectMocks
    private EgovBrofClsServiceImpl service;

    private BrofClsSearchVO validSearchVO;

    @BeforeEach
    void setUp() {
        validSearchVO = new BrofClsSearchVO();
        validSearchVO.setBrofCd("B001");
        validSearchVO.setTestNo("T001");
        validSearchVO.setTestYmd("20260601");
        validSearchVO.setPageNo(1);
        validSearchVO.setPageSize(20);
    }

    // ============================================================
    // REQ-002 / API-001 : 지사 목록 조회
    // ============================================================

    @Test
    @DisplayName("API-001: 사용중인 지사 목록 조회 성공")
    void selectBrofList_정상조회() {
        // given
        BrofVO b1 = new BrofVO();
        b1.setBrofCd("B001"); b1.setBrofNm("서울지사");
        BrofVO b2 = new BrofVO();
        b2.setBrofCd("B002"); b2.setBrofNm("부산지사");
        when(brofClsDAO.selectBrofList()).thenReturn(Arrays.asList(b1, b2));

        // when
        List<BrofVO> actual = service.selectBrofList();

        // then  // TODO: 반환된 목록의 크기 확인 (= 2)
        // TODO: 첫 번째 항목의 brofCd가 "B001"인지 확인
        // TODO: DAO.selectBrofList()가 정확히 1회 호출되었는지 verify
    }

    // ============================================================
    // REQ-001 / API-002 : 지사별 시험정보 목록
    // ============================================================

    @Test
    @DisplayName("API-002 검증기준1: 지사코드로 시험정보 팝업 목록 반환")
    void selectTestInfoListByBrof_정상조회() {
        // given
        TestInfoVO t = new TestInfoVO();
        t.setTestNo("T001"); t.setTestNm("2026년 1회 시험"); t.setTestYmd("20260601"); t.setClsCount(38);
        when(brofClsDAO.selectTestInfoListByBrof("B001"))
                .thenReturn(Collections.singletonList(t));

        // when
        List<TestInfoVO> actual = service.selectTestInfoListByBrof("B001");

        // then  // TODO: 반환 리스트 크기가 1인지 확인
        // TODO: 첫 항목의 testYmd가 "20260601"인지 확인 (검증기준2와 연계)
        // TODO: clsCount가 38인지 확인
    }

    @Test
    @DisplayName("API-002 검증기준3: 등록된 시험정보가 없을 때 빈 목록 반환")
    void selectTestInfoListByBrof_빈목록() {
        // given
        when(brofClsDAO.selectTestInfoListByBrof(anyString()))
                .thenReturn(Collections.<TestInfoVO>emptyList());

        // when
        List<TestInfoVO> actual = service.selectTestInfoListByBrof("B999");

        // then  // TODO: 반환 리스트가 빈 리스트인지 확인 (MSG-001 트리거 조건)
        // TODO: null이 아닌 빈 리스트가 반환되는지 확인
    }

    @Test
    @DisplayName("API-002: 지사코드 null/empty 입력 시 IllegalArgumentException")
    void selectTestInfoListByBrof_지사코드없음() {
        // given (입력만 빈 값으로 설정, DAO mock 불필요)
        String emptyBrofCd = "";

        // when / then  // TODO: assertThrows로 IllegalArgumentException 발생 검증
        // TODO: 예외 메시지에 "지사코드"가 포함되는지 확인
        // TODO: DAO가 호출되지 않았는지 verify (never())
    }

    // ============================================================
    // REQ-003 / API-003 : 시행종목 목록 조회
    // ============================================================

    @Test
    @DisplayName("REQ-003 검증기준1: 3개 조건 충족 시 목록 조회 성공 + 순번 부여")
    void selectBrofClsList_정상조회_순번부여() {
        // given
        List<BrofClsVO> dbResult = new ArrayList<BrofClsVO>();
        BrofClsVO r1 = new BrofClsVO(); r1.setClsNo("A001"); r1.setTestPtNo("1");
        BrofClsVO r2 = new BrofClsVO(); r2.setClsNo("A001"); r2.setTestPtNo("2");
        dbResult.add(r1); dbResult.add(r2);

        when(brofClsDAO.selectBrofClsListTotalCount(any(BrofClsSearchVO.class))).thenReturn(2);
        when(brofClsDAO.selectBrofClsList(any(BrofClsSearchVO.class))).thenReturn(dbResult);

        // when
        BrofClsListResultVO actual = service.selectBrofClsList(validSearchVO);

        // then  // TODO: totalCount가 2인지 확인
        // TODO: 1페이지 첫 행의 rowNum이 1인지 확인
        // TODO: 1페이지 두 번째 행의 rowNum이 2인지 확인 (REQ-003 규칙3)
        // TODO: totalPages가 1인지 확인 (2건 / 20 = 1페이지)
    }

    @Test
    @DisplayName("REQ-003 검증기준2: 조회 결과 없을 때 빈 목록 + totalCount=0")
    void selectBrofClsList_조회결과없음() {
        // given
        when(brofClsDAO.selectBrofClsListTotalCount(any(BrofClsSearchVO.class))).thenReturn(0);

        // when
        BrofClsListResultVO actual = service.selectBrofClsList(validSearchVO);

        // then  // TODO: totalCount가 0인지 확인
        // TODO: list가 빈 리스트인지 확인 (MSG-002 트리거 조건)
        // TODO: DAO.selectBrofClsList()가 호출되지 않았는지 verify (never())
    }

    @Test
    @DisplayName("REQ-003 검증기준3: 필수 조건 미입력 시 IllegalArgumentException")
    void selectBrofClsList_필수조건누락() {
        // given
        BrofClsSearchVO incomplete = new BrofClsSearchVO();
        incomplete.setBrofCd("B001");
        // testNo, testYmd 누락

        // when / then  // TODO: assertThrows로 IllegalArgumentException 발생 검증
        // TODO: 예외 메시지가 "조회 조건을 확인해 주세요."인지 확인 (MSG-003 일치)
        // TODO: DAO가 호출되지 않았는지 verify (never())
    }

    @Test
    @DisplayName("REQ-003: 2페이지 조회 시 순번이 21부터 시작")
    void selectBrofClsList_2페이지_순번이어짐() {
        // given
        validSearchVO.setPageNo(2);
        List<BrofClsVO> page2 = new ArrayList<BrofClsVO>();
        BrofClsVO r = new BrofClsVO(); r.setClsNo("B001"); r.setTestPtNo("1");
        page2.add(r);

        when(brofClsDAO.selectBrofClsListTotalCount(any(BrofClsSearchVO.class))).thenReturn(21);
        when(brofClsDAO.selectBrofClsList(any(BrofClsSearchVO.class))).thenReturn(page2);

        // when
        BrofClsListResultVO actual = service.selectBrofClsList(validSearchVO);

        // then  // TODO: 2페이지 첫 행의 rowNum이 21인지 확인
        // TODO: totalPages가 2인지 확인 (21건 / 20 = 2페이지)
    }

    // ============================================================
    // REQ-004 / API-004 : 엑셀 다운로드용 전체 목록
    // ============================================================

    @Test
    @DisplayName("REQ-004 검증기준1: 엑셀용 전체 목록 조회 - 페이징 없이 전체 반환 + 순번 부여")
    void selectBrofClsListForExcel_정상조회() {
        // given
        List<BrofClsVO> dbResult = new ArrayList<BrofClsVO>();
        for (int i = 0; i < 25; i++) {
            BrofClsVO r = new BrofClsVO();
            r.setClsNo("X" + i); r.setTestPtNo("1");
            dbResult.add(r);
        }
        when(brofClsDAO.selectBrofClsListForExcel(any(BrofClsSearchVO.class))).thenReturn(dbResult);

        // when
        List<BrofClsVO> actual = service.selectBrofClsListForExcel(validSearchVO);

        // then  // TODO: 반환 리스트 크기가 25인지 확인 (페이징 제한 없음)
        // TODO: 첫 행의 rowNum이 1, 마지막 행의 rowNum이 25인지 확인
        // TODO: DAO.selectBrofClsListForExcel()이 1회 호출되었는지 verify
    }
}
