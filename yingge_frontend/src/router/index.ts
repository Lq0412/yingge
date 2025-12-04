import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../pages/LoginPage.vue'
import TryOnPage from '../pages/TryOnPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/app' },
    { path: '/login', component: LoginPage },
    { path: '/app', component: TryOnPage },
  ],
})

export default router
