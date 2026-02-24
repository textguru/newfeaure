package com.risk.management.service;

import com.risk.management.model.Risk;
import com.risk.management.model.RiskCategory;
import com.risk.management.model.RiskStatus;
import com.risk.management.repository.RiskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RiskService {

    private final RiskRepository riskRepository;

    public RiskService(RiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    public List<Risk> getAllRisks() {
        return riskRepository.findAll();
    }

    public Optional<Risk> getRiskById(Long id) {
        return riskRepository.findById(id);
    }

    public Risk createRisk(Risk risk) {
        return riskRepository.save(risk);
    }

    public Risk updateRisk(Long id, Risk updated) {
        return riskRepository.findById(id).map(risk -> {
            risk.setTitle(updated.getTitle());
            risk.setDescription(updated.getDescription());
            risk.setCategory(updated.getCategory());
            risk.setLikelihood(updated.getLikelihood());
            risk.setImpact(updated.getImpact());
            risk.setStatus(updated.getStatus());
            risk.setMitigation(updated.getMitigation());
            risk.setOwner(updated.getOwner());
            return riskRepository.save(risk);
        }).orElseThrow(() -> new NoSuchElementException("위험 ID " + id + "를 찾을 수 없습니다"));
    }

    public void deleteRisk(Long id) {
        if (!riskRepository.existsById(id)) {
            throw new NoSuchElementException("위험 ID " + id + "를 찾을 수 없습니다");
        }
        riskRepository.deleteById(id);
    }

    public List<Risk> getRisksByCategory(RiskCategory category) {
        return riskRepository.findByCategory(category);
    }

    public List<Risk> getRisksByStatus(RiskStatus status) {
        return riskRepository.findByStatus(status);
    }

    public Map<String, Object> getDashboardStats() {
        List<Risk> allRisks = riskRepository.findAll();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRisks", allRisks.size());
        stats.put("activeRisks", riskRepository.countActiveRisks());

        // 등급별 집계
        long critical = allRisks.stream().filter(r -> "CRITICAL".equals(r.getRiskLevel())).count();
        long high     = allRisks.stream().filter(r -> "HIGH".equals(r.getRiskLevel())).count();
        long medium   = allRisks.stream().filter(r -> "MEDIUM".equals(r.getRiskLevel())).count();
        long low      = allRisks.stream().filter(r -> "LOW".equals(r.getRiskLevel())).count();

        Map<String, Long> byLevel = new LinkedHashMap<>();
        byLevel.put("CRITICAL", critical);
        byLevel.put("HIGH", high);
        byLevel.put("MEDIUM", medium);
        byLevel.put("LOW", low);
        stats.put("byLevel", byLevel);

        // 상태별 집계
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (RiskStatus s : RiskStatus.values()) {
            byStatus.put(s.name(), allRisks.stream().filter(r -> r.getStatus() == s).count());
        }
        stats.put("byStatus", byStatus);

        // 카테고리별 집계
        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (RiskCategory c : RiskCategory.values()) {
            byCategory.put(c.name(), allRisks.stream().filter(r -> r.getCategory() == c).count());
        }
        stats.put("byCategory", byCategory);

        // 위험 매트릭스용 데이터 (likelihood x impact grid)
        int[][] matrix = new int[5][5];
        for (Risk r : allRisks) {
            if (r.getLikelihood() != null && r.getImpact() != null) {
                matrix[r.getLikelihood() - 1][r.getImpact() - 1]++;
            }
        }
        stats.put("matrix", matrix);

        return stats;
    }
}
