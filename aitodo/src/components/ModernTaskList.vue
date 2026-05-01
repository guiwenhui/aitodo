<template>
  <div class="modern-dashboard">
    <header class="dashboard-header">
      <div class="header-top">
        <div>
          <h2 class="title">我的工作台</h2>
          <p class="subtitle">今天也要保持专注，击破拖延。</p>
        </div>
        <!-- 视图切换按钮 -->
        <div class="view-switcher">
          <button class="view-btn" :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'" title="列表视图">
            <i class="fas fa-list"></i>
          </button>
          <button class="view-btn" :class="{ active: viewMode === 'matrix' }" @click="viewMode = 'matrix'" title="四象限视图">
            <i class="fas fa-th-large"></i>
          </button>
        </div>
      </div>
    </header>

    <!-- ========== 列表视图 ========== -->
    <template v-if="viewMode === 'list'">
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
        <div v-if="filteredTasks.length === 0" class="empty-state">
          <div class="empty-icon">🍃</div>
          <p>这个文件夹空空如也，去享受生活吧！</p>
        </div>

        <draggable
            v-else
            v-model="filteredTasks"
            item-key="id"
            handle=".drag-handle"
            ghost-class="drag-ghost"
            animation="250"
            @end="onListDragEnd"
            class="task-list"
        >
          <template #item="{ element: task }">
            <div class="task-card">
              <div class="card-left">
                <i class="fas fa-grip-vertical drag-handle"></i>
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
          </template>
        </draggable>
      </div>
    </template>

    <!-- ========== 四象限视图 ========== -->
    <template v-if="viewMode === 'matrix'">
      <div class="matrix-grid">
        <div
            v-for="q in quadrants"
            :key="q.id"
            class="matrix-quadrant"
            :class="q.colorClass"
        >
          <div class="quadrant-header">
            <span class="quadrant-icon">{{ q.icon }}</span>
            <span class="quadrant-title">{{ q.name }}</span>
            <span class="quadrant-count">{{ getQuadrantTasks(q.id).length }}</span>
          </div>

          <draggable
              :list="getQuadrantTasks(q.id)"
              :group="{ name: 'matrix' }"
              item-key="id"
              ghost-class="drag-ghost"
              animation="200"
              @end="(evt) => onMatrixDragEnd(evt, q.id)"
              class="quadrant-body"
          >
            <template #item="{ element: task }">
              <div class="matrix-task-card">
                <div class="matrix-task-title">{{ task.title }}</div>
                <div class="matrix-task-meta">{{ formatDate(task.deadline) }}</div>
              </div>
            </template>
          </draggable>

          <div v-if="getQuadrantTasks(q.id).length === 0" class="quadrant-empty">
            暂无任务
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import draggable from 'vuedraggable'

// --- 视图模式 ---
const viewMode = ref('list') // 'list' | 'matrix'

// --- 选项卡 ---
const currentTab = ref('attention')
const tabs = [
  { id: 'progress', name: '进行中', icon: '🎯' },
  { id: 'attention', name: '需关注', icon: '🚨' },
  { id: 'archived', name: '已归档', icon: '📦' }
]

// --- 四象限定义 ---
const quadrants = [
  { id: 'q1', name: '重要且紧急', icon: '🔴', colorClass: 'q-red',    statusMatch: 3 },
  { id: 'q2', name: '重要不紧急', icon: '🟡', colorClass: 'q-yellow', statusMatch: 0 },
  { id: 'q3', name: '不重要但紧急', icon: '🔵', colorClass: 'q-blue', statusMatch: 2 },
  { id: 'q4', name: '不重要不紧急', icon: '⚪', colorClass: 'q-gray', statusMatch: 1 }
]

// --- 任务数据 ---
const tasks = ref([
  { id: 1, title: '完成项目文档编写', deadline: '2026-04-02T18:00:00', status: 0 },
  { id: 2, title: '学习Vue3框架新特性', deadline: '2026-03-31T23:59:00', status: 3 },
  { id: 3, title: '整理书桌和工作区', deadline: '2026-03-27T17:00:00', status: 2 },
  { id: 4, title: '晨跑锻炼30分钟', deadline: '2026-03-30T08:00:00', status: 1 },
])

// --- 列表视图：过滤 ---
const filteredTasks = computed({
  get() {
    if (currentTab.value === 'progress') return tasks.value.filter(t => t.status === 0)
    if (currentTab.value === 'attention') return tasks.value.filter(t => t.status === 2 || t.status === 3)
    if (currentTab.value === 'archived') return tasks.value.filter(t => t.status === 1)
    return []
  },
  set(newList) {
    // 当拖拽产生新顺序时，更新底层数据
    const otherTasks = tasks.value.filter(t => !newList.find(n => n.id === t.id))
    tasks.value = [...otherTasks, ...newList]
  }
})

const getTaskCount = (tabId) => {
  if (tabId === 'progress') return tasks.value.filter(t => t.status === 0).length
  if (tabId === 'attention') return tasks.value.filter(t => t.status === 2 || t.status === 3).length
  if (tabId === 'archived') return tasks.value.filter(t => t.status === 1).length
  return 0
}

// --- 四象限视图：根据 status 分组 ---
const getQuadrantTasks = (qId) => {
  const q = quadrants.find(x => x.id === qId)
  if (!q) return []
  return tasks.value.filter(t => t.status === q.statusMatch)
}

// --- 工具函数 ---
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

const updateTaskStatus = (id, newStatus) => {
  const task = tasks.value.find(t => t.id === id)
  if (task) task.status = newStatus
}

// --- 拖拽回调 ---
const onListDragEnd = (evt) => {
  console.log('[列表拖拽] 新顺序:', filteredTasks.value.map(t => ({ id: t.id, title: t.title })))
  syncOrderToBackend(filteredTasks.value)
}

const onMatrixDragEnd = (evt, targetQuadrant) => {
  const movedTaskId = evt.item?.__draggable_context?.element?.id
  if (movedTaskId) {
    const q = quadrants.find(x => x.id === targetQuadrant)
    if (q) {
      const task = tasks.value.find(t => t.id === movedTaskId)
      if (task) {
        task.status = q.statusMatch
        console.log(`[四象限拖拽] 任务 "${task.title}" 移至 ${q.name}，新 status=${q.statusMatch}`)
      }
    }
  }
  syncOrderToBackend(tasks.value)
}

// --- 预留后端同步 ---
async function syncOrderToBackend(orderedTasks) {
  const payload = orderedTasks.map((t, i) => ({ id: t.id, sortOrder: i, status: t.status }))
  console.log('[预留 API] 待同步数据:', payload)
  // TODO: 调用后端 API
  // await axios.post('/task/reorder', payload)
}
</script>

<style scoped>
.modern-dashboard {
  max-width: 800px;
  margin: 0 auto;
}

/* Header */
.dashboard-header { margin-bottom: 32px; }
.header-top { display: flex; justify-content: space-between; align-items: flex-start; }
.title { font-size: 1.75rem; color: #111827; font-weight: 700; margin-bottom: 8px; }
.subtitle { color: #6B7280; font-size: 1rem; }

/* 视图切换 */
.view-switcher {
  display: flex; gap: 4px; background: #F3F4F6; border-radius: 10px; padding: 4px;
}
.view-btn {
  width: 36px; height: 36px; border: none; border-radius: 8px; background: transparent;
  color: #9CA3AF; cursor: pointer; display: flex; align-items: center; justify-content: center;
  font-size: 0.95rem; transition: all 0.2s ease;
}
.view-btn:hover { color: #6B7280; }
.view-btn.active { background: #fff; color: #111827; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }

/* Tabs */
.folder-tabs {
  display: flex; gap: 12px; margin-bottom: 24px;
  border-bottom: 1px solid #E5E7EB; padding-bottom: 12px;
}
.tab-btn {
  display: flex; align-items: center; gap: 8px; padding: 10px 16px;
  background: transparent; border: none; border-radius: 8px;
  font-size: 0.95rem; font-weight: 600; color: #6B7280; cursor: pointer; transition: all 0.2s ease;
}
.tab-btn:hover { background: #F3F4F6; color: #374151; }
.tab-btn.active { background: #111827; color: #FFFFFF; }
.tab-count {
  background: rgba(255,255,255,0.2); color: inherit;
  padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; margin-left: 4px;
}
.tab-btn:not(.active) .tab-count { background: #E5E7EB; color: #4B5563; }

/* Task List */
.task-list { display: flex; flex-direction: column; gap: 16px; }

.task-card {
  display: flex; justify-content: space-between; align-items: center;
  background: #FFFFFF; padding: 20px 24px; border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05), 0 4px 6px -2px rgba(0,0,0,0.02);
  border: 1px solid #F3F4F6; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); position: relative;
}
.task-card:hover { box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05); transform: translateY(-2px); border-color: #E5E7EB; }

/* 拖拽手柄 */
.drag-handle {
  color: #D1D5DB; cursor: grab; font-size: 0.9rem; margin-right: 4px;
  transition: color 0.2s;
}
.drag-handle:hover { color: #9CA3AF; }
.drag-handle:active { cursor: grabbing; }

/* 拖拽幽灵 */
.drag-ghost {
  opacity: 0.4; background: #EEF2FF !important; border-color: #4F46E5 !important;
}

.card-left { display: flex; align-items: flex-start; gap: 12px; }
.checkbox { width: 22px; height: 22px; border: 2px solid #D1D5DB; border-radius: 6px; margin-top: 2px; transition: all 0.2s; flex-shrink: 0; }
.checkbox.checked { background: #10B981; border-color: #10B981; }
.task-title { font-size: 1.05rem; font-weight: 600; color: #111827; margin-bottom: 6px; transition: all 0.2s; }
.completed-text { color: #9CA3AF; text-decoration: line-through; }
.task-meta { font-size: 0.85rem; color: #6B7280; display: flex; align-items: center; gap: 6px; }

.card-right { display: flex; align-items: center; gap: 16px; }
.status-badge { padding: 4px 12px; border-radius: 999px; font-size: 0.75rem; font-weight: 600; letter-spacing: 0.5px; }
.badge-normal { background: #EEF2FF; color: #4F46E5; }
.badge-urgent { background: #FEF3C7; color: #D97706; }
.badge-delayed { background: #FEE2E2; color: #DC2626; }
.badge-done { background: #D1FAE5; color: #059669; }

.hover-actions { display: flex; gap: 8px; opacity: 0; transition: opacity 0.2s; position: absolute; right: 24px; background: #fff; padding-left: 10px; }
.task-card:hover .hover-actions { opacity: 1; }

.action-btn { width: 32px; height: 32px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.85rem; transition: background 0.2s; }
.action-btn.check { color: #059669; background: #D1FAE5; } .action-btn.check:hover { background: #10B981; color: white; }
.action-btn.delete { color: #DC2626; background: transparent; } .action-btn.delete:hover { background: #FEE2E2; }

.empty-state { text-align: center; padding: 60px 0; color: #9CA3AF; }
.empty-icon { font-size: 3rem; margin-bottom: 16px; opacity: 0.5; }

/* ========== 四象限视图 ========== */
.matrix-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 16px;
}

.matrix-quadrant {
  background: #fff; border-radius: 16px; border: 1px solid #F3F4F6;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04); min-height: 200px;
  display: flex; flex-direction: column; overflow: hidden;
}
.q-red    .quadrant-header { background: linear-gradient(135deg, #FEF2F2, #FFF); border-bottom: 2px solid #FCA5A5; }
.q-yellow .quadrant-header { background: linear-gradient(135deg, #FFFBEB, #FFF); border-bottom: 2px solid #FCD34D; }
.q-blue   .quadrant-header { background: linear-gradient(135deg, #EFF6FF, #FFF); border-bottom: 2px solid #93C5FD; }
.q-gray   .quadrant-header { background: linear-gradient(135deg, #F9FAFB, #FFF); border-bottom: 2px solid #D1D5DB; }

.quadrant-header {
  padding: 14px 16px; display: flex; align-items: center; gap: 8px;
}
.quadrant-icon { font-size: 1rem; }
.quadrant-title { font-size: 0.9rem; font-weight: 700; color: #374151; flex: 1; }
.quadrant-count {
  background: rgba(0,0,0,0.06); color: #6B7280; padding: 2px 8px;
  border-radius: 10px; font-size: 0.7rem; font-weight: 600;
}

.quadrant-body {
  flex: 1; padding: 12px; display: flex; flex-direction: column; gap: 8px; min-height: 60px;
}

.matrix-task-card {
  background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 10px;
  padding: 12px 14px; cursor: grab; transition: all 0.2s;
}
.matrix-task-card:hover { background: #fff; box-shadow: 0 4px 8px rgba(0,0,0,0.04); transform: translateY(-1px); }
.matrix-task-card:active { cursor: grabbing; }
.matrix-task-title { font-size: 0.9rem; font-weight: 600; color: #111827; margin-bottom: 4px; }
.matrix-task-meta { font-size: 0.75rem; color: #9CA3AF; }

.quadrant-empty {
  padding: 20px; text-align: center; color: #D1D5DB; font-size: 0.85rem;
}

/* Vue Transition */
.list-enter-active, .list-leave-active { transition: all 0.4s ease; }
.list-enter-from { opacity: 0; transform: translateY(15px); }
.list-leave-to { opacity: 0; transform: translateX(-30px); }

@media (max-width: 640px) {
  .matrix-grid { grid-template-columns: 1fr; }
  .header-top { flex-direction: column; gap: 12px; }
}
</style>