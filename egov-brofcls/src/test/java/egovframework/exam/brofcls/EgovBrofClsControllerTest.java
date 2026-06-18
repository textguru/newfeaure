package egovframework.exam.brofcls;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import egovframework.exam.brofcls.service.BrofClsListResultVO;
import egovframework.exam.brofcls.service.BrofClsSearchVO;
import egovframework.exam.brofcls.service.BrofClsVO;
import egovframework.exam.brofcls.service.BrofVO;
import egovframework.exam.brofcls.service.EgovBrofClsService;
import egovframework.exam.brofcls.service.TestInfoVO;
import egovframework.exam.brofcls.web.EgovBrofClsController;

/**
 * EgovBrofClsController 단위 테스트 (MockMvc 기반).
 *
 * <p>AIDD 방법론 표준 적용: given/when은 작성, then은 개발자가 직접 작성.
 * 화면설계서 6장 화면-API-REQ 매핑에 따라 4개 엔드포인트를 검증한다.</p>
 *
 * @author AIDD
 * @since 2026-05-18
 */
@ExtendWith(MockitoExtension.class)
class EgovBrofClsControllerTest {

    @Mock
    private EgovBrofClsService egovBrofClsService;

    @InjectMocks
    private EgovBrofClsController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ============================================================
    // SCR-001 화면 진입
    // ============================================================

    @Test
    @DisplayName("SCR-001: GET /exam/branch-subject → JSP 뷰 반환")
    void viewBrofClsSearch_화면진입() throws Exception {
        // given (특별한 준비 불필요)

        // when
        var result = mockMvc.perform(get("/exam/branch-subject"));

        // then  // TODO: HTTP 상태가 200인지 확인 (status().isOk())
        // TODO: 뷰명이 "egovframework/exam/brofcls/EgovBrofClsSearch"인지 확인
    }

    // ============================================================
    // API-001 지사 목록
    // ============================================================

    @Test
    @DisplayName("API-001: GET /brof-list → 지사 목록 JSON 응답")
    void selectBrofList_API() throws Exception {
        // given
        BrofVO b = new BrofVO();
        b.setBrofCd("B001"); b.setBrofNm("서울지사");
        when(egovBrofClsService.selectBrofList()).thenReturn(Arrays.asList(b));

        // when
        var result = mockMvc.perform(get("/exam/branch-subject/brof-list"));

        // then  // TODO: status 200 확인
        // TODO: JSON 응답의 resultCode가 "0000"인지 확인
        // TODO: data 배열의 첫 항목 brofCd가 "B001"인지 jsonPath로 확인
    }

    // ============================================================
    // API-002 시험정보 목록
    // ============================================================

    @Test
    @DisplayName("API-002: GET /test-info-list?brofCd=B001 → 시험정보 목록")
    void selectTestInfoList_API() throws Exception {
        // given
        TestInfoVO t = new TestInfoVO();
        t.setTestNo("T001"); t.setTestNm("2026년 1회"); t.setTestYmd("20260601"); t.setClsCount(38);
        when(egovBrofClsService.selectTestInfoListByBrof(anyString()))
                .thenReturn(Arrays.asList(t));

        // when
        var result = mockMvc.perform(get("/exam/branch-subject/test-info-list")
                .param("brofCd", "B001"));

        // then  // TODO: status 200 확인
        // TODO: data 배열의 첫 항목 testYmd가 "20260601"인지 확인
        // TODO: clsCount가 38인지 확인
    }

    @Test
    @DisplayName("API-002: brofCd 파라미터 누락 시 400 Bad Request")
    void selectTestInfoList_파라미터누락() throws Exception {
        // given (파라미터 없이 요청)

        // when
        var result = mockMvc.perform(get("/exam/branch-subject/test-info-list"));

        // then  // TODO: status가 400(Bad Request)인지 확인
    }

    // ============================================================
    // API-003 시행종목 목록
    // ============================================================

    @Test
    @DisplayName("API-003 검증기준1: 3개 조건 모두 전달 시 목록 정상 응답")
    void selectBrofClsList_API_정상() throws Exception {
        // given
        BrofClsListResultVO result = new BrofClsListResultVO();
        result.setTotalCount(2);
        result.setPageNo(1);
        result.setPageSize(20);
        result.setTotalPages(1);
        BrofClsVO r = new BrofClsVO();
        r.setRowNum(1); r.setClsNo("A001"); r.setClsNm("정보처리기사");
        result.setList(Arrays.asList(r));
        when(egovBrofClsService.selectBrofClsList(any(BrofClsSearchVO.class))).thenReturn(result);

        // when
        var actual = mockMvc.perform(get("/exam/branch-subject/list")
                .param("brofCd", "B001")
                .param("testNo", "T001")
                .param("testYmd", "20260601")
                .param("pageNo", "1")
                .param("pageSize", "20"));

        // then  // TODO: status 200 확인
        // TODO: data.totalCount가 2인지 확인
        // TODO: data.list[0].rowNum이 1인지 확인
        // TODO: data.list[0].clsNm이 "정보처리기사"인지 확인
    }

    @Test
    @DisplayName("API-003 검증기준2: 결과 없음 - totalCount 0, 빈 목록")
    void selectBrofClsList_API_결과없음() throws Exception {
        // given
        BrofClsListResultVO result = new BrofClsListResultVO();
        result.setTotalCount(0);
        result.setList(Collections.<BrofClsVO>emptyList());
        when(egovBrofClsService.selectBrofClsList(any(BrofClsSearchVO.class))).thenReturn(result);

        // when
        var actual = mockMvc.perform(get("/exam/branch-subject/list")
                .param("brofCd", "B001")
                .param("testNo", "T001")
                .param("testYmd", "20260601"));

        // then  // TODO: status 200 확인
        // TODO: data.totalCount가 0인지 확인
        // TODO: data.list가 빈 배열인지 확인
    }

    // ============================================================
    // API-004 엑셀 다운로드
    // ============================================================

    @Test
    @DisplayName("REQ-004 검증기준1: 엑셀 다운로드 - Content-Type 및 파일명 헤더 확인")
    void downloadExcel_정상다운로드() throws Exception {
        // given
        BrofClsVO r = new BrofClsVO();
        r.setRowNum(1); r.setClsNo("A001"); r.setClsNm("정보처리기사");
        r.setFldCd("01"); r.setFldNm("소프트웨어"); r.setJobSeNm("필기");
        r.setTestPtNo("1"); r.setTestSchdRlsYn("Y");
        when(egovBrofClsService.selectBrofClsListForExcel(any(BrofClsSearchVO.class)))
                .thenReturn(Arrays.asList(r));

        // when
        var actual = mockMvc.perform(get("/exam/branch-subject/excel")
                .param("brofCd", "B001")
                .param("testNo", "T001")
                .param("testYmd", "20260601")
                .param("brofNm", "서울지사")
                .param("testNm", "2026년1회"));

        // then  // TODO: status 200 확인
        // TODO: Content-Type 헤더가 xlsx MIME 타입인지 확인
        //       (application/vnd.openxmlformats-officedocument.spreadsheetml.sheet)
        // TODO: Content-Disposition 헤더에 "지사별시행종목_"이 포함되는지 확인
        // TODO: Content-Disposition 헤더에 시험일자 "20260601"이 포함되는지 확인
    }

    @Test
    @DisplayName("REQ-004 검증기준3: 결과 없을 때 204 No Content")
    void downloadExcel_데이터없음() throws Exception {
        // given
        when(egovBrofClsService.selectBrofClsListForExcel(any(BrofClsSearchVO.class)))
                .thenReturn(Collections.<BrofClsVO>emptyList());

        // when
        var actual = mockMvc.perform(get("/exam/branch-subject/excel")
                .param("brofCd", "B001")
                .param("testNo", "T001")
                .param("testYmd", "20260601"));

        // then  // TODO: status가 204(No Content)인지 확인
    }
}
