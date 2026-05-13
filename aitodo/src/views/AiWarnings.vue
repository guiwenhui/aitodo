<template>
  <div class="warnings-page">
    <div class="ambient-blur"></div>
    <div class="warnings-container">
      <header class="page-header">
        <h1 class="title">AI 认知洞察</h1>
        <p class="subtitle">仅显示最近3天的通知与状态分析</p>
      </header>

      <div v-if="loading" class="loading-state">
        <i class="fas fa-circle-notch fa-spin"></i>
        <span>正在分析数据...</span>
      </div>

      <div v-else-if="filteredWarnings.length === 0" class="empty-state">
        <i class="fas fa-check-circle"></i>
        <p>太棒了！最近3天没有收到任何拖延警告。</p>
      </div>

      <div v-else class="warnings-list">
        <div 
          v-for="warning in filteredWarnings" 
          :key="warning.taskId" 
          class="warning-card"
          :class="warning.warningStyle"
        >
          <div class="warning-indicator"></div>
          <div class="warning-content">
            <div class="warning-header">
              <span class="task-title">{{ warning.title }}</span>
              <span class="task-time">{{ formatTime(warning.deadline) }}</span>
            </div>
            <p class="warning-text">{{ warning.aiWarning }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const warnings = ref([])
const loading = ref(true)

// 过滤最近 3 天的警告
const filteredWarnings = computed(() => {
  const now = new Date()
  const threeDaysAgo = new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000)
  
  return warnings.value.filter(w => {
    if (!w.deadline) return false
    const taskDate = new Date(w.deadline.replace(' ', 'T'))
    return taskDate >= threeDaysAgo
  }).sort((a, b) => new Date(b.deadline.replace(' ', 'T')) - new Date(a.deadline.replace(' ', 'T')))
})

const fetchWarnings = async () => {
  try {
    const res = await axios.get('/task/my-ai-warnings')
    // 后端返回的是 { aiWarnings: [...] }，或者被统一的 Result 包装了 { data: { aiWarnings: [...] } }
    let data = []
    if (res.data?.aiWarnings) {
      data = res.data.aiWarnings
    } else if (res.data?.data?.aiWarnings) {
      data = res.data.data.aiWarnings
    } else if (Array.isArray(res.data)) {
      data = res.data
    }
    warnings.value = data
  } catch (error) {
    console.error('Failed to load warnings:', error)
  } finally {
    loading.value = false
  }
}

const formatTime = (dateStr) => {
  if (!dateStr) return '未知时间'
  const d = new Date(dateStr.replace(' ', 'T'))
  if (isNaN(d.getTime())) return dateStr
  return `${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  fetchWarnings()
})
</script>

<style scoped>
/* 简约高级风 - 浅色毛玻璃背景 */
.warnings-page {
  min-height: 100vh;
  background-color: #FAFAFA;
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Inter", "Segoe UI", sans-serif;
}

.ambient-blur {
  position: fixed;
  top: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(99,102,241,0.05) 0%, rgba(255,255,255,0) 70%);
  z-index: 0;
  pointer-events: none;
}

.warnings-container {
  max-width: 700px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.page-header {
  margin-bottom: 40px;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
  letter-spacing: -0.02em;
}

.subtitle {
  font-size: 1rem;
  color: #6B7280;
}

/* 状态展示 */
.loading-state, .empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #9CA3AF;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.03);
  backdrop-filter: blur(12px);
}

.loading-state i, .empty-state i {
  font-size: 2rem;
  margin-bottom: 16px;
  display: block;
}

.empty-state i {
  color: #10B981;
  opacity: 0.8;
}

.empty-state p {
  font-size: 1.05rem;
  color: #4B5563;
}

/* 警告卡片列表 */
.warnings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.warning-card {
  display: flex;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(0, 0, 0, 0.04);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.warning-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.04);
}

/* 指示条 */
.warning-indicator {
  width: 4px;
  flex-shrink: 0;
  background: #E5E7EB;
}

.warning-content {
  padding: 20px 24px;
  flex: 1;
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.task-title {
  font-weight: 600;
  font-size: 1.05rem;
  color: #111827;
}

.task-time {
  font-size: 0.85rem;
  color: #9CA3AF;
}

.warning-text {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #4B5563;
  margin: 0;
}

/* 风格差异化 (低饱和度、极简高级) */
.urgent_warning .warning-indicator {
  background: #F87171; /* 柔和红 */
}
.urgent_warning {
  background: linear-gradient(to right, rgba(254, 242, 242, 0.5), rgba(255, 255, 255, 0.8));
  border-color: rgba(248, 113, 113, 0.1);
}

.overdue .warning-indicator {
  background: #9CA3AF; /* 灰色 */
}
.overdue .task-title {
  color: #6B7280;
  text-decoration: line-through;
}

.delayed_humorous .warning-indicator {
  background: #FBBF24; /* 柔和黄 */
}
.delayed_humorous {
  background: linear-gradient(to right, rgba(255, 251, 235, 0.5), rgba(255, 255, 255, 0.8));
}

.completed .warning-indicator {
  background: #34D399; /* 柔和绿 */
}

/* 📱 响应式 */
@media (max-width: 768px) {
  .warnings-page { padding: 20px 16px; }
  .title { font-size: 1.6rem; }
  .subtitle { font-size: 0.9rem; }
  .warning-content { padding: 16px; }
  
  /* 移除 hover 效果，使用 active */
  @media (hover: none) and (pointer: coarse) {
    .warning-card:hover { transform: none; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02); }
    .warning-card:active { transform: scale(0.98); background: rgba(255, 255, 255, 0.9); }
  }
}
</style>