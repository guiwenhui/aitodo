<template>
  <component :is="currentComponent" />
</template>

<script setup>
import { shallowRef, onMounted } from 'vue'

// 1. 引入你写好的三个高级页面组件
import Dashboard from './views/Dashboard.vue'
import AiWarnings from './views/AiWarnings.vue'
import FocusFlow from './views/FocusFlow.vue' // 🌟 必须导入心流舱！

const currentComponent = shallowRef(null)

onMounted(() => {
  const path = window.location.pathname

  // 2. 根据路径精准判断
  if (path.includes('/leaderboard')) {
    currentComponent.value = Dashboard
  }
  else if (path.includes('/ai-warnings')) {
    currentComponent.value = AiWarnings
  }
  else if (path.includes('/focus-flow')) { // 🌟 新增心流舱的专属通道！
    currentComponent.value = FocusFlow
  }
  else {
    // 🌟 绝杀修复：如果不匹配任何 Vue 页面，必须立刻休眠 (置为 null)！
    // 绝对不能写默认渲染 Dashboard，否则它会跑到 tasks.html 里疯狂捣乱！
    currentComponent.value = null
  }
})
</script>

<style>
/* 全局样式交给后端的 style.css，这里保持干净即可 */
</style>