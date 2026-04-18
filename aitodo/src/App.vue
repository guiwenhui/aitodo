<template>
  <component :is="currentComponent" />
</template>

<script setup>
import { shallowRef, onMounted } from 'vue'

// 1. 引入对应的页面组件
import Leaderboard from './components/Leaderboard.vue' // 🌟 引入排行榜组件
import AiWarnings from './views/AiWarnings.vue'
import FocusFlow from './views/FocusFlow.vue'
import Dashboard from './views/Dashboard.vue'

const currentComponent = shallowRef(null)

onMounted(() => {
  const path = window.location.pathname

  // 2. 修正路由指向：让 /leaderboard 路径直接对应 Leaderboard.vue
  if (path.includes('/leaderboard')) {
    currentComponent.value = Leaderboard // 🌟 关键修改：直接挂载排行榜
  }
  else if (path.includes('/ai-warnings')) {
    currentComponent.value = AiWarnings
  }
  else if (path.includes('/focus-flow')) {
    currentComponent.value = FocusFlow
  }
  else if (path.includes('/dashboard')) { // 如果有仪表盘需求可以单独给路径
    currentComponent.value = Dashboard
  }
  else {
    currentComponent.value = null
  }
})
</script>