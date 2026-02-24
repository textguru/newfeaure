<template>
  <div class="app-layout">
    <aside class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">⚠</span>
        <div>
          <div class="logo-title">위험관리</div>
          <div class="logo-sub">Risk Management</div>
        </div>
      </div>
      <nav class="sidebar-nav">
        <RouterLink to="/dashboard" class="nav-item" active-class="active">
          <span class="nav-icon">📊</span> 대시보드
        </RouterLink>
        <RouterLink to="/risks" class="nav-item" active-class="active">
          <span class="nav-icon">📋</span> 위험 목록
        </RouterLink>
        <RouterLink to="/matrix" class="nav-item" active-class="active">
          <span class="nav-icon">🗺</span> 위험 매트릭스
        </RouterLink>
      </nav>
      <div class="sidebar-footer">
        <div class="footer-text">Risk Management System</div>
        <div class="footer-version">v1.0.0</div>
      </div>
    </aside>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 230px;
  background: #1e293b;
  color: #e2e8f0;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1.5rem 1.25rem;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.logo-icon {
  font-size: 1.75rem;
  line-height: 1;
}

.logo-title {
  font-size: 1rem;
  font-weight: 700;
  color: #fff;
}

.logo-sub {
  font-size: 0.7rem;
  color: #94a3b8;
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.6rem 0.875rem;
  border-radius: 6px;
  font-size: 0.875rem;
  color: #94a3b8;
  transition: background 0.15s, color 0.15s;
}

.nav-item:hover {
  background: rgba(255,255,255,0.06);
  color: #e2e8f0;
}

.nav-item.active {
  background: rgba(26, 86, 219, 0.25);
  color: #93c5fd;
}

.nav-icon { font-size: 1rem; }

.sidebar-footer {
  padding: 1rem 1.25rem;
  border-top: 1px solid rgba(255,255,255,0.08);
}

.footer-text {
  font-size: 0.7rem;
  color: #475569;
}

.footer-version {
  font-size: 0.65rem;
  color: #334155;
}

.main-content {
  margin-left: 230px;
  flex: 1;
  min-height: 100vh;
  overflow: auto;
}
</style>
