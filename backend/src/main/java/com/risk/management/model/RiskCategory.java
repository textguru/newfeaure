package com.risk.management.model;

public enum RiskCategory {
    FINANCIAL("재무"),
    OPERATIONAL("운영"),
    STRATEGIC("전략"),
    COMPLIANCE("컴플라이언스"),
    SECURITY("보안"),
    ENVIRONMENTAL("환경"),
    REPUTATIONAL("평판"),
    TECHNOLOGY("기술");

    private final String displayName;

    RiskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
