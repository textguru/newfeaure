package com.risk.management.repository;

import com.risk.management.model.Risk;
import com.risk.management.model.RiskCategory;
import com.risk.management.model.RiskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long> {

    List<Risk> findByCategory(RiskCategory category);

    List<Risk> findByStatus(RiskStatus status);

    List<Risk> findByOwnerContainingIgnoreCase(String owner);

    @Query("SELECT r FROM Risk r WHERE r.likelihood * r.impact >= :minScore ORDER BY r.likelihood * r.impact DESC")
    List<Risk> findByMinRiskScore(int minScore);

    @Query("SELECT COUNT(r) FROM Risk r WHERE r.status != 'CLOSED'")
    long countActiveRisks();

    @Query("SELECT r.status, COUNT(r) FROM Risk r GROUP BY r.status")
    List<Object[]> countByStatus();

    @Query("SELECT r.category, COUNT(r) FROM Risk r GROUP BY r.category")
    List<Object[]> countByCategory();
}
