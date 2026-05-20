package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.ExamInfoDAO;
import kr.or.nqis.qis.exam.dto.response.ExamInfoPopupResponse;
import kr.or.nqis.qis.exam.exception.BusinessException;
import kr.or.nqis.qis.exam.service.ExamInfoService;
import kr.or.nqis.qis.exam.vo.ExamInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 시험정보 Service 구현체 (API-02).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamInfoServiceImpl implements ExamInfoService {

    private final ExamInfoDAO examInfoDAO;

    /**
     * REQ-001: 지사별 시험정보 목록 조회 (팝업).
     * - 선택된 지사 데이터만 필터링
     * - menuCache 적용 (TTL 12h)
     */
    @Override
    @Cacheable(cacheNames = "menuCache", key = "#branchCode")
    public List<ExamInfoPopupResponse> selectExamInfosForPopup(String branchCode) {
        if (branchCode == null || branchCode.isBlank()) {
            throw new BusinessException("지사 코드를 입력해 주세요.");
        }

        ExamInfoVO searchVO = new ExamInfoVO();
        searchVO.setBranchCode(branchCode);

        List<ExamInfoVO> examInfos = examInfoDAO.selectExamInfoList(searchVO);

        if (examInfos.isEmpty()) {
            log.info("조회된 시험정보가 없습니다. branchCode: {}", branchCode);
            return List.of();
        }

        return examInfos.stream().map(ExamInfoPopupResponse::from).toList();
    }
}
