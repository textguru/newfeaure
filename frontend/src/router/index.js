import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import RiskList from '../views/RiskList.vue'
import RiskMatrix from '../views/RiskMatrix.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', name: 'Dashboard', component: Dashboard },
  { path: '/risks', name: 'RiskList', component: RiskList },
  { path: '/matrix', name: 'RiskMatrix', component: RiskMatrix },
]

export default createRouter({
  history: createWebHistory(),
  routes
})
