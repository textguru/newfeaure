/*
 * 절차서 포털 데이터 (프로토타입 더미 데이터)
 * ---------------------------------------------------------------
 * 폐쇄망/오프라인(file://) 환경에서도 동작하도록 fetch(JSON) 대신
 * 전역 변수 PROC_DATA 로 데이터를 주입한다.
 *
 * 실제 운영 시 담당자는 보통 이 파일과 docs/ 폴더만 수정하면 된다.
 *   1) docs/ 에 실제 절차서 PDF 추가
 *   2) 아래 documents[] 에 항목 추가 (file 경로를 PDF 파일명과 일치)
 *   3) 필요 시 processSteps[](공정 단계) 수정
 */
window.PROC_DATA = {
  siteTitle: "표준 절차서 포털",
  siteSubtitle: "공정도 기준 절차서 / 작업표준 / 점검표 통합 열람",
  org: "○○ 제조",

  // 공정도 단계 (왼쪽 → 오른쪽 순서대로 표시)
  processSteps: [
    { id: "p01", code: "IQC", name: "입고검사", desc: "수입 자재 검사 및 합부 판정" },
    { id: "p02", code: "WHS", name: "자재보관", desc: "자재 입출고 및 보관 관리" },
    { id: "p03", code: "MAC", name: "가공",     desc: "기계 가공 작업" },
    { id: "p04", code: "ASM", name: "조립",     desc: "부품 조립 작업" },
    { id: "p05", code: "QC",  name: "검사",     desc: "공정/최종 품질 검사" },
    { id: "p06", code: "PKG", name: "포장",     desc: "제품 포장 작업" },
    { id: "p07", code: "SHP", name: "출하",     desc: "출하 및 배송 관리" },
  ],

  // 문서 종류 (목록 필터용)
  docTypes: ["절차서", "작업표준", "점검표", "관리계획"],

  // 절차서 목록
  documents: [
    {
      id: "DOC-IQC-001", title: "입고검사 절차서", step: "p01", type: "절차서",
      rev: "Rev.3", date: "2026-01-15", owner: "품질보증팀",
      keywords: ["입고", "수입검사", "샘플링", "합부판정"],
      file: "docs/DOC-IQC-001.pdf",
    },
    {
      id: "DOC-WHS-001", title: "자재 보관 표준", step: "p02", type: "작업표준",
      rev: "Rev.2", date: "2025-11-03", owner: "자재팀",
      keywords: ["보관", "입출고", "선입선출", "재고"],
      file: "docs/DOC-WHS-001.pdf",
    },
    {
      id: "DOC-MAC-001", title: "가공 작업 표준", step: "p03", type: "작업표준",
      rev: "Rev.5", date: "2026-02-20", owner: "생산1팀",
      keywords: ["가공", "절삭", "치수", "공차"],
      file: "docs/DOC-MAC-001.pdf",
    },
    {
      id: "DOC-MAC-002", title: "설비 일상점검 점검표", step: "p03", type: "점검표",
      rev: "Rev.1", date: "2026-03-10", owner: "설비보전팀",
      keywords: ["설비", "일상점검", "예방보전", "체크리스트"],
      file: "docs/DOC-MAC-002.pdf",
    },
    {
      id: "DOC-ASM-001", title: "조립 작업 절차서", step: "p04", type: "절차서",
      rev: "Rev.4", date: "2025-12-01", owner: "생산2팀",
      keywords: ["조립", "체결", "토크", "지그"],
      file: "docs/DOC-ASM-001.pdf",
    },
    {
      id: "DOC-QC-001", title: "공정검사 절차서", step: "p05", type: "절차서",
      rev: "Rev.2", date: "2026-01-28", owner: "품질관리팀",
      keywords: ["공정검사", "샘플링", "SPC", "관리도"],
      file: "docs/DOC-QC-001.pdf",
    },
    {
      id: "DOC-QC-002", title: "최종검사 관리계획서", step: "p05", type: "관리계획",
      rev: "Rev.3", date: "2026-04-05", owner: "품질관리팀",
      keywords: ["최종검사", "관리계획", "CTQ", "출하판정"],
      file: "docs/DOC-QC-002.pdf",
    },
    {
      id: "DOC-PKG-001", title: "포장 작업 표준", step: "p06", type: "작업표준",
      rev: "Rev.1", date: "2025-10-12", owner: "포장팀",
      keywords: ["포장", "라벨", "완충재", "박스"],
      file: "docs/DOC-PKG-001.pdf",
    },
    {
      id: "DOC-SHP-001", title: "출하/배송 절차서", step: "p07", type: "절차서",
      rev: "Rev.2", date: "2026-02-02", owner: "물류팀",
      keywords: ["출하", "배송", "상차", "운송"],
      file: "docs/DOC-SHP-001.pdf",
    },
  ],
};
