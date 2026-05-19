package com.sig.exam.service;

import com.sig.exam.controller.exception.BusinessException;
import com.sig.exam.controller.exception.ResourceNotFoundException;
import com.sig.exam.dto.response.ExamItemResponse;
import com.sig.exam.entity.ExamInfo;
import com.sig.exam.repository.BranchRepository;
import com.sig.exam.repository.ExamInfoRepository;
import com.sig.exam.repository.ExamItemRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final ExamItemRepository examItemRepository;
    private final ExamInfoRepository examInfoRepository;
    private final BranchRepository branchRepository;

    private static final String[] HEADERS = {
            "순번", "종목코드", "종목명", "선택분야", "선택분야명", "작업구분", "부", "시험일정공개여부"
    };

    public record ExcelFile(String fileName, byte[] content) {}

    /**
     * REQ-004: 시행종목 엑셀 파일 생성.
     * 파일명: 지사별시행종목_[지사명]_[시험명]_[시험일자]_[다운로드일시].xlsx
     */
    public ExcelFile exportExcel(Integer examId, String branchCode, LocalDate examDate) {
        if (examId == null || branchCode == null || branchCode.isBlank() || examDate == null) {
            throw new BusinessException("조회 조건을 확인해 주세요.");
        }

        List<ExamItemResponse> items =
                examItemRepository.searchAllExamItems(examId, branchCode, examDate);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException("다운로드할 데이터가 없습니다.");
        }

        ExamInfo examInfo = examInfoRepository.findById(examId)
                .orElseThrow(() -> new BusinessException("시험정보를 찾을 수 없습니다."));
        String branchName = branchRepository.findById(branchCode)
                .map(b -> b.getBranchName())
                .orElse(branchCode);

        byte[] content = createExcelFile(items);
        String fileName = buildFileName(branchName, examInfo.getExamName(), examDate);
        return new ExcelFile(fileName, content);
    }

    private String buildFileName(String branchName, String examName, LocalDate examDate) {
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "지사별시행종목_" + branchName + "_" + examName + "_"
                + examDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + "_" + now + ".xlsx";
    }

    private byte[] createExcelFile(List<ExamItemResponse> items) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("시행종목 목록");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < items.size(); i++) {
                ExamItemResponse item = items.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(nvl(item.getItemCode()));
                row.createCell(2).setCellValue(nvl(item.getItemName()));
                row.createCell(3).setCellValue(nvl(item.getSelectField()));
                row.createCell(4).setCellValue(nvl(item.getSelectFieldName()));
                row.createCell(5).setCellValue(nvl(item.getWorkType()));
                row.createCell(6).setCellValue(nvl(item.getPart()));
                row.createCell(7).setCellValue(nvl(item.getExposeYn()));
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("엑셀 파일 생성에 실패했습니다.");
        }
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }
}
