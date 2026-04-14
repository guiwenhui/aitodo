<template>
  <div class="modern-dashboard">
    <header class="dashboard-header">
      <h2 class="title">我的工作台</h2>
      <p class="subtitle">今天也要保持专注，击破拖延。</p>
    </header>

    <nav class="folder-tabs">
      <button
          v-for="tab in tabs"
          :key="tab.id"
          class="tab-btn"
          :class="{ 'active': currentTab === tab.id }"
          @click="currentTab = tab.id"
      >
        <span class="tab-icon">{{ tab.icon }}</span>
        <span class="tab-name">{{ tab.name }}</span>
        <span class="tab-count" v-if="getTaskCount(tab.id) > 0">{{ getTaskCount(tab.id) }}</span>
      </button>
    </nav>

    <div class="task-container">
      <TransitionGroup name="list" tag="div" class="task-list">

        <div v-if="filteredTasks.length === 0" class="empty-state" key="empty">
          <div class="empty-icon">🍃</div>
          <p>这个文件夹空空如也，去享受生活吧！</p>
        </div>

        <div
            v-for="task in filteredTasks"
            :key="task.id"
            class="task-card"
        >
          <div class="card-left">
            <div class="checkbox" :class="{ 'checked': task.status === 1 }"></div>
            <div class="task-info">
              <h3 class="task-title" :class="{ 'completed-text': task.status === 1 }">{{ task.title }}</h3>
              <div class="task-meta">
                <i class="fas fa-clock"></i>
                <span>{{ formatDate(task.deadline) }}</span>
              </div>
            </div>
          </div>

          <div class="card-right">
            <span class="status-badge" :class="getStatusClass(task.status)">
              {{ getStatusText(task.status) }}
            </span>
            <div class="hover-actions">
              <button class="action-btn check" v-if="task.status !== 1" @click="updateTaskStatus(task.id, 1)">
                <i class="fas fa-check"></i>
              </button>
              <button class="action-btn delete">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>

      </TransitionGroup>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// --- 1. 状态定义 ---
// 当前选中的选项卡，默认打开"需关注"
const currentTab = ref('attention')

const tabs = [
  { id: 'progress', name: '进行中', icon: '🎯' },
  { id: 'attention', name: '需关注', icon: '🚨' },
  { id: 'archived', name: '已归档', icon: '📦' }
]

// --- 2. 模拟数据 (这里替换为你的 axios.get 请求) ---
const tasks = ref([
  { id: 1, title: '完成项目文档编写', deadline: '2026-04-02T18:00:00', status: 0 }, // 0: 正常
  { id: 2, title: '学习Vue3框架新特性', deadline: '2026-03-31T23:59:00', status: 3 }, // 3: 紧急
  { id: 3, title: '整理书桌和工作区', deadline: '2026-03-27T17:00:00', status: 2 }, // 2: 拖延
  { id: 4, title: '晨跑锻炼30分钟', deadline: '2026-03-30T08:00:00', status: 1 },  // 1: 已完成
])

// --- 3. 核心计算属性：根据当前 Tab 过滤任务 ---
const filteredTasks = computed(() => {
  if (currentTab.value === 'progress') {
    return tasks.value.filter(t => t.status === 0)
  }
  if (currentTab.value === 'attention') {
    // 需关注 = 紧急(3) + 拖延(2)
    return tasks.value.filter(t => t.status === 2 || t.status === 3)
  }
  if (currentTab.value === 'archived') {
    return tasks.value.filter(t => t.status === 1)
  }
  return []
})

// 获取每个分类的数量
const getTaskCount = (tabId) => {
  if (tabId === 'progress') return tasks.value.filter(t => t.status === 0).length
  if (tabId === 'attention') return tasks.value.filter(t => t.status === 2 || t.status === 3).length
  if (tabId === 'archived') return tasks.value.filter(t => t.status === 1).length
  return 0
}

// --- 4. 工具函数 ---
const formatDate = (dateStr) => {
  if (!dateStr) return '无期限'
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const getStatusText = (status) => {
  const map = { 0: '进行中', 1: '已完成', 2: '已拖延', 3: '紧急' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = { 0: 'badge-normal', 1: 'badge-done', 2: 'badge-delayed', 3: 'badge-urgent' }
  return map[status] || 'badge-normal'
}

// 模拟操作
const updateTaskStatus = (id, newStatus) => {
  const task = tasks.value.find(t => t.id === id)
  if (task) task.status = newStatus
}
</script>

<style scoped>
/* ==================
   现代化极简 UI 样式
   ================== */
.modern-dashboard {
  max-width: 800px; /* 居中阅读流最佳宽度 */
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 32px;
}
.title { font-size: 1.75rem; color: #111827; font-weight: 700; margin-bottom: 8px; }
.subtitle { color: #6B7280; font-size: 1rem; }

/* 🌟 选项卡设计 */
.folder-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  border-bottom: 1px solid #E5E7EB;
  padding-bottom: 12px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  color: #6B7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn:hover {
  background: #F3F4F6;
  color: #374151;
}

.tab-btn.active {
  background: #111827; /* 纯黑强调 */
  color: #FFFFFF;
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  color: inherit;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  margin-left: 4px;
}
.tab-btn:not(.active) .tab-count { background: #E5E7EB; color: #4B5563; }

/* 🌟 任务卡片设计 */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #FFFFFF;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05), 0 4px 6px -2px rgba(0,0,0,0.02);
  border: 1px solid #F3F4F6;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.task-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05);
  transform: translateY(-2px);
  border-color: #E5E7EB;
}

/* 左侧信息区 */
.card-left { display: flex; align-items: flex-start; gap: 16px; }

/* 模拟单选框 */
.checkbox { width: 22px; height: 22px; border: 2px solid #D1D5DB; border-radius: 6px; margin-top: 2px; transition: all 0.2s; }
.checkbox.checked { background: #10B981; border-color: #10B981; }

.task-title { font-size: 1.05rem; font-weight: 600; color: #111827; margin-bottom: 6px; transition: all 0.2s; }
.completed-text { color: #9CA3AF; text-decoration: line-through; }
.task-meta { font-size: 0.85rem; color: #6B7280; display: flex; align-items: center; gap: 6px; }

/* 右侧操作区 */
.card-right { display: flex; align-items: center; gap: 16px; }

.status-badge { padding: 4px 12px; border-radius: 999px; font-size: 0.75rem; font-weight: 600; letter-spacing: 0.5px; }
.badge-normal { background: #EEF2FF; color: #4F46E5; }
.badge-urgent { background: #FEF3C7; color: #D97706; }
.badge-delayed { background: #FEE2E2; color: #DC2626; }
.badge-done { background: #D1FAE5; color: #059669; }

/* 悬浮操作 (默认隐藏) */
.hover-actions { display: flex; gap: 8px; opacity: 0; transition: opacity 0.2s; position: absolute; right: 24px; background: #fff; padding-left: 10px; }
.task-card:hover .hover-actions { opacity: 1; }

.action-btn { width: 32px; height: 32px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.85rem; transition: background 0.2s; }
.action-btn.check { color: #059669; background: #D1FAE5; } .action-btn.check:hover { background: #10B981; color: white; }
.action-btn.delete { color: #DC2626; background: transparent; } .action-btn.delete:hover { background: #FEE2E2; }

/* 空状态 */
.empty-state { text-align: center; padding: 60px 0; color: #9CA3AF; }
.empty-icon { font-size: 3rem; margin-bottom: 16px; opacity: 0.5; }

/* 🌟 Vue Transition 动画魔法 */
.list-enter-active, .list-leave-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(15px); }
.list-leave-to { opacity: 0; transform: translateX(-30px); }
</style>