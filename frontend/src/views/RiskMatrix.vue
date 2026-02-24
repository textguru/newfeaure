<template>
  <div class="matrix-page">
    <div class="page-header">
      <div>
        <h1>위험 매트릭스</h1>
        <p class="subtitle">발생 가능성(X) × 영향도(Y) 기반 위험 분포</p>
      </div>
    </div>

    <div v-if="loading" class="loading">불러오는 중...</div>

    <div v-else class="matrix-layout">
      <!-- 매트릭스 -->
      <div class="card matrix-card">
        <div class="matrix-container">
          <!-- Y축 레이블 -->
          <div class="y-axis-label">← 영향도 (낮음)</div>

          <div class="matrix-grid-wrap">
            <!-- Y축 숫자 -->
            <div class="y-labels">
              <div v-for="y in [5,4,3,2,1]" :key="y" class="y-label">{{ y }}</div>
            </div>

            <!-- 매트릭스 셀 -->
            <div class="matrix-grid">
              <template v-for="y in [5,4,3,2,1]" :key="y">
                <div
                  v-for="x in [1,2,3,4,5]"
                  :key="x"
                  class="matrix-cell"
                  :class="cellClass(x, y)"
                  @click="selectCell(x, y)"
                  :title="`가능성:${x} × 영향도:${y} = ${x*y}점`"
                >
                  <div class="cell-score">{{ x * y }}</div>
                  <div v-if="getRisksAt(x, y).length" class="cell-risks">
                    <div
                      v-for="risk in getRisksAt(x, y)"
                      :key="risk.id"
                      class="risk-dot"
                      :title="risk.title"
                    >{{ risk.id }}</div>
                  </div>
                </div>
              </template>
            </div>

            <!-- X축 숫자 -->
            <div class="x-labels">
              <div v-for="x in [1,2,3,4,5]" :key="x" class="x-label">{{ x }}</div>
            </div>
          </div>

          <div class="x-axis-label">발생 가능성 (높음) →</div>
        </div>

        <!-- 범례 -->
        <div class="legend">
          <div class="legend-item"><span class="legend-box low"></span> 낮음 (1~4점)</div>
          <div class="legend-item"><span class="legend-box medium"></span> 보통 (5~9점)</div>
          <div class="legend-item"><span class="legend-box high"></span> 높음 (10~14점)</div>
          <div class="legend-item"><span class="legend-box critical"></span> 심각 (15~25점)</div>
        </div>
      </div>

      <!-- 사이드 패널 -->
      <div class="side-panel">
        <!-- 선택된 셀의 위험 목록 -->
        <div v-if="selectedCell" class="card selected-risks">
          <h3>가능성 {{ selectedCell.x }} × 영향도 {{ selectedCell.y }}</h3>
          <p class="cell-info">점수: <strong>{{ selectedCell.x * selectedCell.y }}</strong></p>
          <div v-if="getRisksAt(selectedCell.x, selectedCell.y).length">
            <div
              v-for="risk in getRisksAt(selectedCell.x, selectedCell.y)"
              :key="risk.id"
              class="risk-card-mini"
            >
              <div class="risk-mini-header">
                <span class="mini-id">#{{ risk.id }}</span>
                <span :class="['badge', `badge-${riskLevel(risk).toLowerCase()}`]">
                  {{ LEVEL_LABELS[riskLevel(risk)] }}
                </span>
              </div>
              <div class="risk-mini-title">{{ risk.title }}</div>
              <div class="risk-mini-meta">{{ CATEGORY_LABELS[risk.category] }} · {{ STATUS_LABELS[risk.status] }}</div>
            </div>
          </div>
          <div v-else class="empty">이 위치에 위험이 없습니다.</div>
        </div>

        <!-- 전체 위험 요약 -->
        <div class="card">
          <h3>전체 위험 목록</h3>
          <div class="all-risks-list">
            <div
              v-for="risk in sortedRisks"
              :key="risk.id"
              class="risk-card-mini clickable"
              @click="selectCell(risk.likelihood, risk.impact)"
            >
              <div class="risk-mini-header">
                <span class="mini-id">#{{ risk.id }}</span>
                <span class="mini-score" :class="`level-${riskLevel(risk)}`">
                  {{ risk.likelihood * risk.impact }}점
                </span>
                <span :class="['badge', `badge-${riskLevel(risk).toLowerCase()}`]">
                  {{ LEVEL_LABELS[riskLevel(risk)] }}
                </span>
              </div>
              <div class="risk-mini-title">{{ risk.title }}</div>
              <div class="risk-mini-meta">
                가능성 {{ risk.likelihood }} × 영향도 {{ risk.impact }}
                · {{ STATUS_LABELS[risk.status] }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRiskStore } from '../stores/risk'

const CATEGORY_LABELS = {
  FINANCIAL: '재무', OPERATIONAL: '운영', STRATEGIC: '전략',
  COMPLIANCE: '컴플라이언스', SECURITY: '보안',
  ENVIRONMENTAL: '환경', REPUTATIONAL: '평판', TECHNOLOGY: '기술'
}
const STATUS_LABELS = {
  IDENTIFIED: '식별됨', ANALYZING: '분석중',
  MITIGATING: '대응중', MONITORING: '모니터링', CLOSED: '종료'
}
const LEVEL_LABELS = { CRITICAL: '심각', HIGH: '높음', MEDIUM: '보통', LOW: '낮음' }

const store = useRiskStore()
const loading = ref(true)
const selectedCell = ref(null)

function riskLevel(risk) {
  const score = risk.likelihood * risk.impact
  if (score >= 15) return 'CRITICAL'
  if (score >= 10) return 'HIGH'
  if (score >= 5)  return 'MEDIUM'
  return 'LOW'
}

function cellClass(x, y) {
  const score = x * y
  if (score >= 15) return 'cell-critical'
  if (score >= 10) return 'cell-high'
  if (score >= 5)  return 'cell-medium'
  return 'cell-low'
}

function getRisksAt(x, y) {
  return store.risks.filter(r => r.likelihood === x && r.impact === y)
}

function selectCell(x, y) {
  selectedCell.value = { x, y }
}

const sortedRisks = computed(() =>
  [...store.risks].sort((a, b) => (b.likelihood * b.impact) - (a.likelihood * a.impact))
)

onMounted(async () => {
  await store.fetchRisks()
  loading.value = false
})
</script>

<style scoped>
.matrix-page {
  padding: 2rem;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-header h1 {
  font-size: 1.5rem;
  font-weight: 700;
}

.subtitle {
  color: var(--color-text-muted);
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.matrix-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 1.5rem;
  align-items: start;
}

.matrix-card { padding: 1.5rem; }

.matrix-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.y-axis-label {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  margin-bottom: 0.5rem;
  align-self: flex-start;
  padding-left: 2rem;
}

.matrix-grid-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.y-labels {
  display: flex;
  flex-direction: column;
  float: left;
}

.matrix-grid-wrap {
  display: grid;
  grid-template-rows: auto auto auto;
  grid-template-areas: "ylabels grid" ". xlabels";
}

.y-labels {
  grid-area: ylabels;
  display: flex;
  flex-direction: column;
}

.y-label {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  color: var(--color-text-muted);
  width: 24px;
}

.matrix-grid {
  grid-area: grid;
  display: grid;
  grid-template-columns: repeat(5, 80px);
  grid-template-rows: repeat(5, 80px);
  gap: 3px;
  background: #e5e7eb;
  border: 2px solid #e5e7eb;
  border-radius: 4px;
}

.x-labels {
  grid-area: xlabels;
  display: flex;
  padding-left: 24px;
}

.x-label {
  width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  color: var(--color-text-muted);
}

.matrix-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 2px;
  transition: opacity 0.15s, transform 0.1s;
  padding: 4px;
  gap: 4px;
}

.matrix-cell:hover { opacity: 0.85; transform: scale(1.04); }

.cell-low      { background: #dcfce7; }
.cell-medium   { background: #fef9c3; }
.cell-high     { background: #ffedd5; }
.cell-critical { background: #fee2e2; }

.cell-score {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
}

.cell-risks {
  display: flex;
  flex-wrap: wrap;
  gap: 2px;
  justify-content: center;
}

.risk-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #1a56db;
  color: #fff;
  font-size: 0.6rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.x-axis-label {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  margin-top: 0.5rem;
}

.legend {
  display: flex;
  gap: 1.25rem;
  margin-top: 1.5rem;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.8rem;
}

.legend-box {
  width: 16px;
  height: 16px;
  border-radius: 3px;
}

.legend-box.low      { background: #dcfce7; border: 1px solid #86efac; }
.legend-box.medium   { background: #fef9c3; border: 1px solid #fde047; }
.legend-box.high     { background: #ffedd5; border: 1px solid #fdba74; }
.legend-box.critical { background: #fee2e2; border: 1px solid #fca5a5; }

/* 사이드 패널 */
.side-panel {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.selected-risks h3,
.side-panel .card h3 {
  font-size: 0.9375rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.cell-info {
  font-size: 0.85rem;
  color: var(--color-text-muted);
  margin-bottom: 0.75rem;
}

.all-risks-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-height: 400px;
  overflow-y: auto;
}

.risk-card-mini {
  background: #f8fafc;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 0.625rem 0.75rem;
}

.risk-card-mini.clickable {
  cursor: pointer;
  transition: background 0.15s;
}

.risk-card-mini.clickable:hover { background: var(--color-primary-light); }

.risk-mini-header {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin-bottom: 0.25rem;
}

.mini-id {
  font-size: 0.7rem;
  color: var(--color-text-muted);
}

.mini-score {
  font-size: 0.75rem;
  font-weight: 700;
}

.risk-mini-title {
  font-size: 0.8125rem;
  font-weight: 500;
}

.risk-mini-meta {
  font-size: 0.7rem;
  color: var(--color-text-muted);
  margin-top: 0.125rem;
}
</style>
