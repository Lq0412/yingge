<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/app')
}
</script>

<template>
  <div class="page">
    <header class="topbar">
      <div class="brand" @click="goHome">
        <span class="dot"></span>
        <span class="title">yingge try-on</span>
      </div>
      <nav class="links">
        <router-link to="/app">体验</router-link>
        <router-link to="/login">登录</router-link>
      </nav>
    </header>
    <main class="body">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: radial-gradient(circle at 10% 20%, #f4f8ff 0, #f7f7fb 35%, #fdfdfd 100%);
  color: #0f172a;
  font-family: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 22px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #e5e7eb;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  letter-spacing: 0.4px;
  cursor: pointer;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563eb, #22c55e);
}

.links {
  display: flex;
  gap: 14px;
  font-weight: 600;
}

.links a {
  color: #0f172a;
  text-decoration: none;
  padding: 6px 10px;
  border-radius: 8px;
}

.links a.router-link-active {
  background: #0f172a;
  color: #fff;
}

.body {
  padding: 18px;
  max-width: 1200px;
  margin: 0 auto;
}

@media (max-width: 720px) {
  .body {
    padding: 12px;
  }
}
</style>
