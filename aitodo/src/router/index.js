import { createRouter, createWebHistory } from 'vue-router'

/* ========================================================
   Vue Router — AiTodo SPA 路由配置
   ======================================================== */

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: 'AI Todo - 简约高效的任务管理' }
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: () => import('../views/Tasks.vue'),
    meta: { title: '任务管理 - AI Todo' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '卷王战绩 - AI Todo' }
  },
  {
    path: '/focus-flow',
    name: 'FocusFlow',
    component: () => import('../views/FocusFlow.vue'),
    meta: { title: '专注心流 - AI Todo' }
  },
  {
    path: '/ai-warnings',
    name: 'AiWarnings',
    component: () => import('../views/AiWarnings.vue'),
    meta: { title: 'Flow心流 - AI Todo' }
  },
  {
    path: '/leaderboard',
    name: 'Leaderboard',
    component: () => import('../views/LeaderboardPage.vue'),
    meta: { title: '积分排行榜 - AI Todo' }
  },
  {
    path: '/health',
    name: 'Health',
    component: () => import('../views/Health.vue'),
    meta: { title: '健康检查 - AI Todo' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录 - AI Todo', hideNav: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册 - AI Todo', hideNav: true }
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 动态更新页面标题
router.afterEach((to) => {
  document.title = to.meta.title || 'AI Todo'
})

export default router
