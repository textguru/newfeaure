import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { riskApi } from '../services/api'

export const useRiskStore = defineStore('risk', () => {
  const risks = ref([])
  const loading = ref(false)
  const error = ref(null)

  const totalRisks = computed(() => risks.value.length)
  const criticalRisks = computed(() =>
    risks.value.filter(r => getRiskLevel(r) === 'CRITICAL')
  )

  function getRiskLevel(risk) {
    const score = risk.likelihood * risk.impact
    if (score >= 15) return 'CRITICAL'
    if (score >= 10) return 'HIGH'
    if (score >= 5)  return 'MEDIUM'
    return 'LOW'
  }

  async function fetchRisks(params = {}) {
    loading.value = true
    error.value = null
    try {
      const res = await riskApi.getAll(params)
      risks.value = res.data
    } catch (e) {
      error.value = '위험 목록을 불러오는데 실패했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function createRisk(risk) {
    const res = await riskApi.create(risk)
    risks.value.push(res.data)
    return res.data
  }

  async function updateRisk(id, risk) {
    const res = await riskApi.update(id, risk)
    const idx = risks.value.findIndex(r => r.id === id)
    if (idx !== -1) risks.value[idx] = res.data
    return res.data
  }

  async function deleteRisk(id) {
    await riskApi.delete(id)
    risks.value = risks.value.filter(r => r.id !== id)
  }

  return {
    risks, loading, error,
    totalRisks, criticalRisks,
    getRiskLevel,
    fetchRisks, createRisk, updateRisk, deleteRisk
  }
})
