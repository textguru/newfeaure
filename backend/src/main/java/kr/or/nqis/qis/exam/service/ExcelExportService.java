package kr.or.nqis.qis.exam.service;

/**
 * 엑셀 다운로드 Service Interface (API-04).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
public interface ExcelExportService {

    /**
     * REQ-004: 시행종목 엑셀 파일 생성.
     *
     * @param examId 시험 ID
     * @param branchCode 지사 코드
     * @param examDate 시험일자
     * @return 엑셀 파일 결과 (파일명 + 바이트 배열)
     */
    ExcelDownloadResult exportExcel(Integer examId, String branchCode, String examDate);

    /**
     * 엑셀 다운로드 결과 (파일명 + 바이트 배열).
     */
    record ExcelDownloadResult(String fileName, byte[] content) {}
}
