package com.risk.management.config;

import com.risk.management.model.Risk;
import com.risk.management.model.RiskCategory;
import com.risk.management.model.RiskStatus;
import com.risk.management.repository.RiskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RiskRepository repo) {
        return args -> {
            // 샘플 위험 데이터
            Risk[] samples = {
                createRisk("서버 해킹 가능성", "외부 공격자에 의한 서버 침해 위험",
                    RiskCategory.SECURITY, 3, 5, RiskStatus.MITIGATING,
                    "방화벽 강화, 취약점 스캔 정기 실시", "IT보안팀"),

                createRisk("개인정보 유출", "고객 개인정보 무단 접근 및 유출 위험",
                    RiskCategory.COMPLIANCE, 2, 5, RiskStatus.MONITORING,
                    "접근 권한 최소화, 암호화 적용", "개인정보보호팀"),

                createRisk("핵심 인력 이탈", "주요 개발자 및 관리자 퇴직으로 인한 업무 공백",
                    RiskCategory.OPERATIONAL, 3, 4, RiskStatus.ANALYZING,
                    "직원 만족도 향상 프로그램 운영, 지식이전 체계 구축", "HR팀"),

                createRisk("경쟁사 신제품 출시", "경쟁사의 혁신 제품으로 인한 시장 점유율 하락",
                    RiskCategory.STRATEGIC, 4, 4, RiskStatus.IDENTIFIED,
                    "R&D 투자 확대, 차별화 전략 수립", "전략기획팀"),

                createRisk("환율 변동 리스크", "달러/원화 환율 급등으로 인한 원가 상승",
                    RiskCategory.FINANCIAL, 3, 3, RiskStatus.MONITORING,
                    "환헤지 계약 체결, 외화 조달 다변화", "재무팀"),

                createRisk("클라우드 서비스 장애", "AWS/Azure 주요 서비스 중단으로 인한 서비스 불가",
                    RiskCategory.TECHNOLOGY, 2, 5, RiskStatus.MITIGATING,
                    "멀티 클라우드 전략, DR 계획 수립", "인프라팀"),

                createRisk("공급망 차질", "주요 부품 공급업체 생산 중단으로 인한 납기 지연",
                    RiskCategory.OPERATIONAL, 2, 3, RiskStatus.MONITORING,
                    "복수 공급업체 계약, 안전재고 확보", "구매팀"),

                createRisk("규제 위반 과징금", "금융 규정 위반으로 인한 행정 제재 및 과징금",
                    RiskCategory.COMPLIANCE, 1, 5, RiskStatus.CLOSED,
                    "컴플라이언스 교육 강화, 정기 감사", "준법감시팀"),
            };

            for (Risk r : samples) {
                repo.save(r);
            }
        };
    }

    private Risk createRisk(String title, String desc, RiskCategory category,
                             int likelihood, int impact, RiskStatus status,
                             String mitigation, String owner) {
        Risk r = new Risk();
        r.setTitle(title);
        r.setDescription(desc);
        r.setCategory(category);
        r.setLikelihood(likelihood);
        r.setImpact(impact);
        r.setStatus(status);
        r.setMitigation(mitigation);
        r.setOwner(owner);
        return r;
    }
}
