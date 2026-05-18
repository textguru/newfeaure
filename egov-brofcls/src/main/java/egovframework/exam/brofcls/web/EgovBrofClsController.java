package egovframework.exam.brofcls.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.exam.brofcls.service.BrofClsListResultVO;
import egovframework.exam.brofcls.service.BrofClsSearchVO;
import egovframework.exam.brofcls.service.BrofClsVO;
import egovframework.exam.brofcls.service.BrofVO;
import egovframework.exam.brofcls.service.EgovBrofClsService;
import egovframework.exam.brofcls.service.TestInfoVO;

/**
 * 지사별 시행종목 조회 Controller.
 *
 * <p>화면설계서 SCR-001 / POP-001 진입 및 API-001 ~ API-004 엔드포인트를 제공한다.</p>
 *
 * <ul>
 *   <li>GET /exam/branch-subject : 화면 진입 (SCR-001)</li>
 *   <li>GET /exam/branch-subject/brof-list : API-001 지사 목록</li>
 *   <li>GET /exam/branch-subject/test-info-list : API-002 시험정보 목록</li>
 *   <li>GET /exam/branch-subject/list : API-003 시행종목 목록</li>
 *   <li>GET /exam/branch-subject/excel : API-004 엑셀 다운로드</li>
 * </ul>
 *
 * @author AIDD
 * @since 2026-05-18
 */
@Controller
@RequestMapping("/exam/branch-subject")
public class EgovBrofClsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EgovBrofClsController.class);

    @Resource(name = "egovBrofClsService")
    private EgovBrofClsService egovBrofClsService;

    /** SCR-001 화면 진입 */
    @GetMapping
    public String viewBrofClsSearch() {
        return "egovframework/exam/brofcls/EgovBrofClsSearch";
    }

    /** API-001 지사 드롭다운 목록 */
    @GetMapping("/brof-list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> selectBrofList() {
        List<BrofVO> list = egovBrofClsService.selectBrofList();
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("resultCode", "0000");
        response.put("resultMessage", "정상 처리되었습니다.");
        response.put("data", list);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /** API-002 지사별 시험정보 목록 (팝업) */
    @GetMapping("/test-info-list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> selectTestInfoList(
            @RequestParam("brofCd") String brofCd) {
        List<TestInfoVO> list = egovBrofClsService.selectTestInfoListByBrof(brofCd);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("resultCode", "0000");
        response.put("resultMessage", "정상 처리되었습니다.");
        response.put("data", list);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /** API-003 시행종목 목록 (페이징) */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> selectBrofClsList(
            @ModelAttribute BrofClsSearchVO searchVO) {
        BrofClsListResultVO result = egovBrofClsService.selectBrofClsList(searchVO);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("resultCode", "0000");
        response.put("resultMessage", "정상 처리되었습니다.");
        response.put("data", result);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * API-004 엑셀 다운로드.
     *
     * <p>REQ-004 규칙3 파일명 규칙:
     * 지사별시행종목_[지사명]_[시험명]_[시험일자]_[다운로드일시].xlsx</p>
     */
    @GetMapping("/excel")
    public void downloadExcel(@ModelAttribute BrofClsSearchVO searchVO,
                              @RequestParam(value = "brofNm", required = false, defaultValue = "지사") String brofNm,
                              @RequestParam(value = "testNm", required = false, defaultValue = "시험") String testNm,
                              HttpServletResponse response) throws Exception {

        List<BrofClsVO> list = egovBrofClsService.selectBrofClsListForExcel(searchVO);

        // REQ-004 예외처리1: 데이터 없을 경우 빈 엑셀이 아닌 별도 응답 처리
        // (UI에서 사전 차단되지만 방어적 처리)
        if (list == null || list.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String downloadTime = sdf.format(new Date());
        String rawFileName = "지사별시행종목_" + sanitize(brofNm) + "_" + sanitize(testNm)
                + "_" + searchVO.getTestYmd() + "_" + downloadTime + ".xlsx";
        String encodedFileName = URLEncoder.encode(rawFileName, StandardCharsets.UTF_8.name())
                .replace("+", "%20");

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);

        Workbook workbook = null;
        OutputStream out = null;
        try {
            workbook = new SXSSFWorkbook(100);
            Sheet sheet = workbook.createSheet("지사별 시행종목");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // REQ-004 규칙4: 첫 행은 컬럼 헤더
            String[] headers = {"순번", "종목코드", "종목명", "선택분야", "선택분야명",
                    "작업구분", "부", "시험일정공개여부"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            for (int i = 0; i < list.size(); i++) {
                BrofClsVO vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(vo.getRowNum());
                row.createCell(1).setCellValue(nullSafe(vo.getClsNo()));
                row.createCell(2).setCellValue(nullSafe(vo.getClsNm()));
                row.createCell(3).setCellValue(nullSafe(vo.getFldCd()));
                row.createCell(4).setCellValue(nullSafe(vo.getFldNm()));
                row.createCell(5).setCellValue(nullSafe(vo.getJobSeNm()));
                row.createCell(6).setCellValue(nullSafe(vo.getTestPtNo()));
                row.createCell(7).setCellValue(nullSafe(vo.getTestSchdRlsYn()));
            }

            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            LOGGER.error("엑셀 다운로드 실패", e);
            throw e;
        } finally {
            if (workbook != null) {
                try {
                    ((SXSSFWorkbook) workbook).dispose();
                    workbook.close();
                } catch (Exception ignore) {
                    LOGGER.debug("workbook close ignored", ignore);
                }
            }
        }
    }

    /** 파일명에 부적합한 문자 치환 */
    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
