package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.ExamInfoDAO;
import kr.or.nqis.qis.exam.dao.ExamItemDAO;
import kr.or.nqis.qis.exam.dto.response.ExamItemResponse;
import kr.or.nqis.qis.exam.exception.BusinessException;
import kr.or.nqis.qis.exam.exception.SystemException;
import kr.or.nqis.qis.exam.service.ExcelExportService;
import kr.or.nqis.qis.exam.vo.ExamInfoVO;
import kr.or.nqis.qis.exam.vo.ExamItemSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 엑셀 다운로드 Service 구현체 (API-04).
 * REQ-004 시행종목 엑셀 다운로드.
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final String[] HEADERS = {
            "순번", "종목코드", "종목명", "선택분야", "선택분야명",
            "작업구분", "부", "시험일정공개여부"
    };

    private static final DateTimeFormatter DOWNLOAD_DT_FMT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ExamItemDAO examItemDAO;
    private final ExamInfoDAO examInfoDAO;

    /**
     * REQ-004: 엑셀 파일 생성.
     * - 페이징 없이 전체 데이터 조회
     * - 파일명: 지사별시행종목_[지사명]_[시험명]_[시험일자]_[다운로드일시].xlsx
     */
    @Override
    public ExcelDownloadResult exportExcel(Integer examId, String branchCode, String examDate) {
        validateRequestParams(examId, branchCode, examDate);

        ExamItemSearchVO searchVO = new ExamItemSearchVO();
        searchVO.setExamId(examId);
        searchVO.setBranchCode(branchCode);
        searchVO.setExamDate(examDate);

        List<ExamItemResponse> items = examItemDAO.selectExamItemListAll(searchVO);
        if (items.isEmpty()) {
            log.warn("다운로드할 데이터가 없습니다. examId={}, branchCode={}, examDate={}",
                    examId, branchCode, examDate);
            throw new BusinessException("다운로드할 데이터가 없습니다.", HttpStatus.NOT_FOUND);
        }

        ExamInfoVO examInfoQuery = new ExamInfoVO();
        examInfoQuery.setExamId(examId);
        ExamInfoVO examInfo = examInfoDAO.selectExamInfo(examInfoQuery);
        if (examInfo == null) {
            log.error("시험정보를 찾을 수 없습니다. examId={}", examId);
            throw new BusinessException("시험정보를 찾을 수 없습니다.");
        }

        byte[] content = createExcelFile(items);
        String fileName = buildFileName(examInfo.getExamName(), branchCode, examDate);

        return new ExcelDownloadResult(fileName, content);
    }

    private void validateRequestParams(Integer examId, String branchCode, String examDate) {
        if (examId == null
                || branchCode == null || branchCode.isBlank()
                || examDate == null || examDate.isBlank()) {
            throw new BusinessException("조회 조건을 확인해 주세요.");
        }
    }

    /**
     * 파일명 생성.
     * 형식: 지사별시행종목_[지사명]_[시험명]_[시험일자]_[다운로드일시].xlsx
     * 지사명을 별도 조회 없이 BRANCH_CODE 그대로 사용해도 무방하나, 가독성을 위해 BranchDAO 조회를 두지 않고
     * 시험일자(YYYYMMDD) -> YYYY-MM-DD 변환만 처리한다.
     */
    private String buildFileName(String examName, String branchCode, String examDate) {
        String downloadDt = LocalDateTime.now().format(DOWNLOAD_DT_FMT);
        String examDateFormatted = formatExamDate(examDate);
        String safeExamName = sanitize(examName);
        String safeBranch = sanitize(branchCode);
        return String.format("지사별시행종목_%s_%s_%s_%s.xlsx",
                safeBranch, safeExamName, examDateFormatted, downloadDt);
    }

    private String formatExamDate(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.length() != 8) {
            return yyyymmdd != null ? yyyymmdd : "";
        }
        return yyyymmdd.substring(0, 4) + "-" + yyyymmdd.substring(4, 6) + "-" + yyyymmdd.substring(6, 8);
    }

    /**
     * 파일명에 사용할 수 없는 문자 치환.
     */
    private String sanitize(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private byte[] createExcelFile(List<ExamItemResponse> items) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("시행종목 목록");

            CellStyle headerStyle = createHeaderStyle(workbook);

            // 헤더 행
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            for (int i = 0; i < items.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                ExamItemResponse item = items.get(i);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(nullToEmpty(item.getItemCode()));
                row.createCell(2).setCellValue(nullToEmpty(item.getItemName()));
                row.createCell(3).setCellValue(nullToEmpty(item.getSelectField()));
                row.createCell(4).setCellValue(nullToEmpty(item.getSelectFieldName()));
                row.createCell(5).setCellValue(nullToEmpty(item.getWorkType()));
                row.createCell(6).setCellValue(nullToEmpty(item.getPart()));
                row.createCell(7).setCellValue(nullToEmpty(item.getExposeYn()));
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("엑셀 파일 생성 실패", e);
            throw new SystemException("엑셀 파일 생성에 실패했습니다.", e);
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
