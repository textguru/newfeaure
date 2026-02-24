package com.risk.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risks")
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "위험명은 필수입니다")
    @Size(max = 200)
    @Column(nullable = false)
    private String title;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @NotNull(message = "카테고리는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskCategory category;

    @NotNull(message = "발생 가능성은 필수입니다")
    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer likelihood;

    @NotNull(message = "영향도는 필수입니다")
    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer impact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskStatus status = RiskStatus.IDENTIFIED;

    @Size(max = 500)
    @Column(length = 500)
    private String mitigation;

    @Size(max = 100)
    private String owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 위험 점수 계산 (가능성 x 영향도)
    @Transient
    public int getRiskScore() {
        if (likelihood == null || impact == null) return 0;
        return likelihood * impact;
    }

    // 위험 등급
    @Transient
    public String getRiskLevel() {
        int score = getRiskScore();
        if (score >= 15) return "CRITICAL";
        if (score >= 10) return "HIGH";
        if (score >= 5)  return "MEDIUM";
        return "LOW";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public RiskCategory getCategory() { return category; }
    public void setCategory(RiskCategory category) { this.category = category; }

    public Integer getLikelihood() { return likelihood; }
    public void setLikelihood(Integer likelihood) { this.likelihood = likelihood; }

    public Integer getImpact() { return impact; }
    public void setImpact(Integer impact) { this.impact = impact; }

    public RiskStatus getStatus() { return status; }
    public void setStatus(RiskStatus status) { this.status = status; }

    public String getMitigation() { return mitigation; }
    public void setMitigation(String mitigation) { this.mitigation = mitigation; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
