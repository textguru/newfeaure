package kr.or.nqis.qis.exam.impl;

import kr.or.nqis.qis.exam.dao.BranchDAO;
import kr.or.nqis.qis.exam.dto.response.BranchResponse;
import kr.or.nqis.qis.exam.service.ExamBranchService;
import kr.or.nqis.qis.exam.vo.BranchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 지사 Service 구현체 (API-01).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamBranchServiceImpl implements ExamBranchService {

    private final BranchDAO branchDAO;

    @Override
    @Cacheable(cacheNames = "commonCodeCache", key = "'branchList'")
    public List<BranchResponse> selectBranchList() {
        List<BranchVO> branches = branchDAO.selectBranchList();
        return branches.stream().map(BranchResponse::from).toList();
    }
}
