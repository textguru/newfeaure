package com.risk.management.model;

public enum RiskStatus {
    IDENTIFIED("식별됨"),
    ANALYZING("분석중"),
    MITIGATING("대응중"),
    MONITORING("모니터링"),
    CLOSED("종료");

    private final String displayName;

    RiskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
