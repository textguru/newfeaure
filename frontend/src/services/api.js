import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

export const riskApi = {
  getAll: (params = {}) => api.get('/risks', { params }),
  getById: (id) => api.get(`/risks/${id}`),
  create: (risk) => api.post('/risks', risk),
  update: (id, risk) => api.put(`/risks/${id}`, risk),
  delete: (id) => api.delete(`/risks/${id}`),
  getDashboard: () => api.get('/risks/dashboard'),
  getEnums: () => api.get('/risks/enums'),
}

export default api
