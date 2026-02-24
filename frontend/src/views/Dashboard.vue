<template>
  <div class="dashboard">
    <div class="page-header">
      <h1>대시보드</h1>
      <p class="subtitle">위험 현황 요약</p>
    </div>

    <div v-if="loading" class="loading">불러오는 중...</div>

    <template v-else>
      <!-- 요약 카드 -->
      <div class="stat-grid">
        <div class="stat-card">
          <div class="stat-label">전체 위험</div>
          <div class="stat-value">{{ stats.totalRisks }}</div>
          <div class="stat-sub">등록된 위험 항목</div>
        </div>
        <div class="stat-card active">
          <div class="stat-label">활성 위험</div>
          <div class="stat-value">{{ stats.activeRisks }}</div>
          <div class="stat-sub">종료 제외 건수</div>
        </div>
        <div class="stat-card critical">
          <div class="stat-label">심각 위험</div>
          <div class="stat-value">{{ stats.byLevel?.CRITICAL || 0 }}</div>
          <div class="stat-sub">위험 점수 15점 이상</div>
        </div>
        <div class="stat-card high">
          <div class="stat-label">높은 위험</div>
          <div class="stat-value">{{ stats.byLevel?.HIGH || 0 }}</div>
          <div class="stat-sub">위험 점수 10~14점</div>
        </div>
      </div>

      <!-- 차트 영역 -->
      <div class="chart-grid">
        <div class="card">
          <h3 class="chart-title">상태별 위험 현황</h3>
          <div class="chart-wrap">
            <Doughnut v-if="statusChartData" :data="statusChartData" :options="doughnutOptions" />
          </div>
        </div>
        <div class="card">
          <h3 class="chart-title">카테고리별 위험 분포</h3>
          <div class="chart-wrap">
            <Bar v-if="categoryChartData" :data="categoryChartData" :options="barOptions" />
          </div>
        </div>
        <div class="card">
          <h3 class="chart-title">등급별 위험 현황</h3>
          <div class="chart-wrap">
            <Bar v-if="levelChartData" :data="levelChartData" :options="levelBarOptions" />
          </div>
        </div>
      </div>

      <!-- 최근 고위험 목록 -->
      <div class="card top-risks">
        <h3 class="chart-title">고위험 항목 (점수 상위)</h3>
        <table v-if="topRisks.length">
          <thead>
            <tr>
              <th>위험명</th>
              <th>카테고리</th>
              <th>가능성</th>
              <th>영향도</th>
              <th>점수</th>
              <th>등급</th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="risk in topRisks" :key="risk.id">
              <td>{{ risk.title }}</td>
              <td>{{ CATEGORY_LABELS[risk.category] || risk.category }}</td>
              <td>{{ risk.likelihood }}</td>
              <td>{{ risk.impact }}</td>
              <td><strong>{{ risk.likelihood * risk.impact }}</strong></td>
              <td>
                <span :class="['badge', `badge-${riskLevel(risk).toLowerCase()}`]">
                  {{ LEVEL_LABELS[riskLevel(risk)] }}
                </span>
              </td>
              <td>
                <span :class="['badge', `status-${risk.status}`]">
                  {{ STATUS_LABELS[risk.status] || risk.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else class="empty">등록된 위험이 없습니다.</div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Doughnut, Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement, Tooltip, Legend,
  CategoryScale, LinearScale, BarElement, Title
} from 'chart.js'
import { riskApi } from '../services/api'

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title)

const CATEGORY_LABELS = {
  FINANCIAL: '재무', OPERATIONAL: '운영', STRATEGIC: '전략',
  COMPLIANCE: '컴플라이언스', SECURITY: '보안',
  ENVIRONMENTAL: '환경', REPUTATIONAL: '평판', TECHNOLOGY: '기술'
}

const STATUS_LABELS = {
  IDENTIFIED: '식별됨', ANALYZING: '분석중',
  MITIGATING: '대응중', MONITORING: '모니터링', CLOSED: '종료'
}

const LEVEL_LABELS = {
  CRITICAL: '심각', HIGH: '높음', MEDIUM: '보통', LOW: '낮음'
}

const loading = ref(true)
const stats = ref({})
const topRisks = ref([])

function riskLevel(risk) {
  const score = risk.likelihood * risk.impact
  if (score >= 15) return 'CRITICAL'
  if (score >= 10) return 'HIGH'
  if (score >= 5)  return 'MEDIUM'
  return 'LOW'
}

const doughnutOptions = {
  responsive: true,
  plugins: { legend: { position: 'bottom' } }
}

const barOptions = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
}

const levelBarOptions = {
  ...barOptions,
  plugins: { legend: { display: false } }
}

const statusChartData = computed(() => {
  if (!stats.value.byStatus) return null
  const s = stats.value.byStatus
  return {
    labels: Object.keys(s).map(k => STATUS_LABELS[k] || k),
    datasets: [{
      data: Object.values(s),
      backgroundColor: ['#c7d2fe','#fef08a','#fed7aa','#bfdbfe','#e5e7eb']
    }]
  }
})

const categoryChartData = computed(() => {
  if (!stats.value.byCategory) return null
  const c = stats.value.byCategory
  return {
    labels: Object.keys(c).map(k => CATEGORY_LABELS[k] || k),
    datasets: [{
      data: Object.values(c),
      backgroundColor: '#1a56db',
      borderRadius: 4
    }]
  }
})

const levelChartData = computed(() => {
  if (!stats.value.byLevel) return null
  const l = stats.value.byLevel
  const colors = { CRITICAL: '#dc2626', HIGH: '#ea580c', MEDIUM: '#ca8a04', LOW: '#16a34a' }
  return {
    labels: Object.keys(l).map(k => LEVEL_LABELS[k] || k),
    datasets: [{
      data: Object.values(l),
      backgroundColor: Object.keys(l).map(k => colors[k] || '#888'),
      borderRadius: 4
    }]
  }
})

onMounted(async () => {
  try {
    const [dashRes, risksRes] = await Promise.all([
      riskApi.getDashboard(),
      riskApi.getAll()
    ])
    stats.value = dashRes.data
    topRisks.value = [...risksRes.data]
      .sort((a, b) => (b.likelihood * b.impact) - (a.likelihood * a.impact))
      .slice(0, 5)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  padding: 2rem;
  max-width: 1200px;
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

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: var(--color-surface);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 1.25rem 1.5rem;
  border-left: 4px solid #94a3b8;
}

.stat-card.active { border-left-color: var(--color-primary); }
.stat-card.critical { border-left-color: var(--color-critical); }
.stat-card.high { border-left-color: var(--color-high); }

.stat-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 800;
  line-height: 1.2;
  margin: 0.25rem 0;
}

.stat-sub {
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.chart-title {
  font-size: 0.9375rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.chart-wrap {
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.top-risks { margin-bottom: 2rem; }
</style>
