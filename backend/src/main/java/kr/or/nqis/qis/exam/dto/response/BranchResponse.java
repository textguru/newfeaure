package kr.or.nqis.qis.exam.dto.response;

import kr.or.nqis.qis.exam.vo.BranchVO;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 지사 목록 응답 DTO (API-01).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class BranchResponse {

    private String branchCode;
    private String branchName;

    public static BranchResponse from(BranchVO vo) {
        return new BranchResponse(vo.getBranchCode(), vo.getBranchName());
    }
}
