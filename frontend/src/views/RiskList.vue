<template>
  <div class="risk-list-page">
    <div class="page-header">
      <div>
        <h1>위험 목록</h1>
        <p class="subtitle">등록된 모든 위험을 관리합니다</p>
      </div>
      <button class="btn-primary" @click="openCreateModal">+ 위험 등록</button>
    </div>

    <!-- 필터 바 -->
    <div class="filter-bar card">
      <select v-model="filterCategory" @change="applyFilter">
        <option value="">전체 카테고리</option>
        <option v-for="(label, key) in CATEGORY_LABELS" :key="key" :value="key">{{ label }}</option>
      </select>
      <select v-model="filterStatus" @change="applyFilter">
        <option value="">전체 상태</option>
        <option v-for="(label, key) in STATUS_LABELS" :key="key" :value="key">{{ label }}</option>
      </select>
      <select v-model="filterLevel">
        <option value="">전체 등급</option>
        <option value="CRITICAL">심각</option>
        <option value="HIGH">높음</option>
        <option value="MEDIUM">보통</option>
        <option value="LOW">낮음</option>
      </select>
      <input v-model="searchQuery" placeholder="위험명 검색..." />
    </div>

    <div v-if="loading" class="loading">불러오는 중...</div>

    <div v-else class="card table-card">
      <table v-if="filteredRisks.length">
        <thead>
          <tr>
            <th>ID</th>
            <th>위험명</th>
            <th>카테고리</th>
            <th>가능성</th>
            <th>영향도</th>
            <th>점수</th>
            <th>등급</th>
            <th>상태</th>
            <th>담당자</th>
            <th>작업</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="risk in filteredRisks" :key="risk.id">
            <td class="id-col">#{{ risk.id }}</td>
            <td>
              <div class="risk-title">{{ risk.title }}</div>
              <div class="risk-desc">{{ risk.description }}</div>
            </td>
            <td>
              <span class="category-tag">{{ CATEGORY_LABELS[risk.category] || risk.category }}</span>
            </td>
            <td>
              <div class="score-bar">
                <div class="score-fill likelihood" :style="{width: (risk.likelihood/5*100)+'%'}"></div>
                <span>{{ risk.likelihood }}/5</span>
              </div>
            </td>
            <td>
              <div class="score-bar">
                <div class="score-fill impact" :style="{width: (risk.impact/5*100)+'%'}"></div>
                <span>{{ risk.impact }}/5</span>
              </div>
            </td>
            <td><strong class="score-num" :class="`level-${riskLevel(risk)}`">{{ risk.likelihood * risk.impact }}</strong></td>
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
            <td>{{ risk.owner || '-' }}</td>
            <td class="action-col">
              <button class="btn-icon edit" @click="openEditModal(risk)" title="수정">✏️</button>
              <button class="btn-icon delete" @click="confirmDelete(risk)" title="삭제">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty">
        <p>조건에 맞는 위험이 없습니다.</p>
      </div>
    </div>

    <!-- 등록/수정 모달 -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingRisk ? '위험 수정' : '위험 등록' }}</h3>
          <button class="btn-icon" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>위험명 *</label>
            <input v-model="form.title" placeholder="위험 항목명을 입력하세요" />
          </div>
          <div class="form-group">
            <label>설명</label>
            <textarea v-model="form.description" rows="3" placeholder="위험에 대한 상세 설명"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>카테고리 *</label>
              <select v-model="form.category">
                <option value="">선택하세요</option>
                <option v-for="(label, key) in CATEGORY_LABELS" :key="key" :value="key">{{ label }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>상태</label>
              <select v-model="form.status">
                <option v-for="(label, key) in STATUS_LABELS" :key="key" :value="key">{{ label }}</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>발생 가능성 (1~5) *</label>
              <div class="slider-group">
                <input type="range" min="1" max="5" v-model.number="form.likelihood" />
                <span class="slider-val">{{ form.likelihood }}</span>
              </div>
            </div>
            <div class="form-group">
              <label>영향도 (1~5) *</label>
              <div class="slider-group">
                <input type="range" min="1" max="5" v-model.number="form.impact" />
                <span class="slider-val">{{ form.impact }}</span>
              </div>
            </div>
          </div>
          <div class="risk-preview" v-if="form.likelihood && form.impact">
            위험 점수: <strong :class="`level-${previewLevel}`">{{ form.likelihood * form.impact }}점</strong>
            &nbsp;/&nbsp;
            등급: <span :class="['badge', `badge-${previewLevel.toLowerCase()}`]">{{ LEVEL_LABELS[previewLevel] }}</span>
          </div>
          <div class="form-group">
            <label>대응 방안</label>
            <textarea v-model="form.mitigation" rows="2" placeholder="위험 완화 방안 또는 대응 계획"></textarea>
          </div>
          <div class="form-group">
            <label>담당자</label>
            <input v-model="form.owner" placeholder="담당 부서 또는 담당자명" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeModal">취소</button>
          <button class="btn-primary" @click="saveRisk" :disabled="saving">
            {{ saving ? '저장 중...' : (editingRisk ? '수정 저장' : '등록') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 삭제 확인 모달 -->
    <div v-if="deletingRisk" class="modal-overlay" @click.self="deletingRisk = null">
      <div class="modal" style="max-width:420px">
        <div class="modal-header">
          <h3>위험 삭제</h3>
        </div>
        <div class="modal-body">
          <p><strong>"{{ deletingRisk.title }}"</strong> 항목을 삭제하시겠습니까?</p>
          <p style="color:var(--color-text-muted);margin-top:0.5rem;font-size:0.875rem">이 작업은 되돌릴 수 없습니다.</p>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="deletingRisk = null">취소</button>
          <button class="btn-danger" @click="deleteRisk">삭제</button>
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
const { loading, fetchRisks, createRisk, updateRisk, deleteRisk: storeDelete } = store

const filterCategory = ref('')
const filterStatus = ref('')
const filterLevel = ref('')
const searchQuery = ref('')

const showModal = ref(false)
const editingRisk = ref(null)
const deletingRisk = ref(null)
const saving = ref(false)

const defaultForm = () => ({
  title: '', description: '', category: '', status: 'IDENTIFIED',
  likelihood: 3, impact: 3, mitigation: '', owner: ''
})
const form = ref(defaultForm())

function riskLevel(risk) {
  const score = risk.likelihood * risk.impact
  if (score >= 15) return 'CRITICAL'
  if (score >= 10) return 'HIGH'
  if (score >= 5)  return 'MEDIUM'
  return 'LOW'
}

const previewLevel = computed(() => riskLevel(form.value))

const filteredRisks = computed(() => {
  return store.risks
    .filter(r => !filterCategory.value || r.category === filterCategory.value)
    .filter(r => !filterStatus.value || r.status === filterStatus.value)
    .filter(r => !filterLevel.value || riskLevel(r) === filterLevel.value)
    .filter(r => !searchQuery.value || r.title.toLowerCase().includes(searchQuery.value.toLowerCase()))
    .sort((a, b) => (b.likelihood * b.impact) - (a.likelihood * a.impact))
})

function applyFilter() {}

function openCreateModal() {
  editingRisk.value = null
  form.value = defaultForm()
  showModal.value = true
}

function openEditModal(risk) {
  editingRisk.value = risk
  form.value = { ...risk }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingRisk.value = null
}

async function saveRisk() {
  if (!form.value.title || !form.value.category) {
    alert('위험명과 카테고리는 필수 입력 항목입니다.')
    return
  }
  saving.value = true
  try {
    if (editingRisk.value) {
      await updateRisk(editingRisk.value.id, form.value)
    } else {
      await createRisk(form.value)
    }
    closeModal()
  } catch (e) {
    alert('저장에 실패했습니다. 다시 시도해주세요.')
  } finally {
    saving.value = false
  }
}

function confirmDelete(risk) {
  deletingRisk.value = risk
}

async function deleteRisk() {
  try {
    await storeDelete(deletingRisk.value.id)
    deletingRisk.value = null
  } catch (e) {
    alert('삭제에 실패했습니다.')
  }
}

onMounted(() => fetchRisks())
</script>

<style scoped>
.risk-list-page {
  padding: 2rem;
  max-width: 1200px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
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

.filter-bar {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.filter-bar select,
.filter-bar input {
  width: auto;
  min-width: 160px;
}

.table-card { padding: 0; overflow: hidden; }

.id-col { color: var(--color-text-muted); font-size: 0.8rem; }

.risk-title { font-weight: 500; }
.risk-desc {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.category-tag {
  font-size: 0.75rem;
  background: var(--color-primary-light);
  color: var(--color-primary);
  padding: 0.2em 0.5em;
  border-radius: 4px;
  white-space: nowrap;
}

.score-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.8rem;
}

.score-fill {
  height: 6px;
  border-radius: 3px;
  flex-shrink: 0;
}

.score-fill.likelihood { background: #f59e0b; }
.score-fill.impact { background: #ef4444; }

.score-num { font-size: 1rem; }

.action-col {
  display: flex;
  gap: 0.25rem;
}

.btn-icon {
  background: none;
  border: none;
  padding: 0.25rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

.btn-icon:hover { background: var(--color-border); }

.slider-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.slider-group input[type="range"] {
  flex: 1;
  padding: 0;
  border: none;
  background: none;
}

.slider-val {
  font-size: 1.25rem;
  font-weight: 700;
  width: 2rem;
  text-align: center;
  color: var(--color-primary);
}

.risk-preview {
  background: #f8fafc;
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  font-size: 0.875rem;
}
</style>
