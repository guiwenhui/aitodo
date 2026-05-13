<template>
  <div class="workspace-container">
    <header class="workspace-header">
      <div class="header-row">
        <div>
          <h1 class="page-title">工作台</h1>
          <p class="page-subtitle">按状态聚焦你的任务，击破拖延症。</p>
        </div>
        <div class="view-toggle">
          <button class="view-toggle-btn" :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'" title="列表视图">
            <i class="fas fa-list"></i>
          </button>
          <button class="view-toggle-btn" :class="{ active: viewMode === 'matrix' }" @click="viewMode = 'matrix'" title="四象限视图">
            <i class="fas fa-th-large"></i>
          </button>
        </div>
      </div>
    </header>

    <!-- 列表视图 -->
    <div v-if="viewMode === 'list'" class="task-board">
      <nav class="folder-tabs">
        <button v-for="tab in tabs" :key="tab.id" class="tab-btn" :class="{ active: currentTab === tab.id }" @click="currentTab = tab.id">
          <i :class="tab.icon"></i> {{ tab.name }}
          <span class="tab-count">{{ getTabCount(tab.id) }}</span>
        </button>
      </nav>
      <div class="task-list">
        <div v-if="filteredTasks.length === 0" class="empty-state">
          <div class="empty-icon">🍃</div>
          <p>这个文件夹空空如也</p>
        </div>
        <div v-for="task in filteredTasks" :key="task.id" class="task-card">
          <div class="card-left">
            <i class="fas fa-grip-vertical drag-handle"></i>
            <i :class="task.status === 1 ? 'fas fa-check-circle' : 'fas fa-circle-notch'" class="task-icon" :style="task.status === 1 ? 'color:#10B981' : ''"></i>
            <div>
              <div class="task-title" :class="{ 'completed-text': task.status === 1 }">{{ task.title }}</div>
              <div class="task-meta">
                <span><i class="far fa-calendar-plus"></i> 创建: {{ formatDate(task.createdAt) }}</span>
                <span v-if="task.deadline"><i class="far fa-clock"></i> 截止: {{ formatDate(task.deadline) }}</span>
              </div>
            </div>
          </div>
          <div class="card-right">
            <span class="status-badge" :class="getStatusClass(task.status)">{{ getStatusText(task.status) }}</span>
            <div class="task-actions">
              <button v-if="task.status !== 1" class="action-btn check" @click="completeTask(task.id)" title="完成"><i class="fas fa-check"></i></button>
              <button v-if="task.status === 0" class="action-btn focus" @click="enterFocusFlow(task.title)" title="心流"><i class="fas fa-headphones-alt"></i></button>
              <button class="action-btn delete" @click="deleteTask(task.id)" title="删除"><i class="fas fa-trash-alt"></i></button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 四象限视图 -->
    <div v-if="viewMode === 'matrix'" class="matrix-view">
      <div class="matrix-grid">
        <div v-for="q in quadrants" :key="q.id" class="matrix-quadrant" :class="q.colorClass">
          <div class="quadrant-header">
            <span class="quadrant-icon">{{ q.emoji }}</span>
            <span class="quadrant-title">{{ q.name }}</span>
            <span class="quadrant-count">{{ getQuadrantTasks(q.statusMatch).length }}</span>
          </div>
          <draggable
              class="quadrant-body"
              :modelValue="getQuadrantTasks(q.statusMatch)"
              @update:modelValue="() => {}"
              group="matrix"
              item-key="id"
              animation="200"
              @change="onMatrixChange($event, q.statusMatch)"
          >
            <template #item="{ element: task }">
              <div class="matrix-task-card">
                <div class="matrix-task-title">{{ task.title }}</div>
                <div class="matrix-task-meta">{{ formatDate(task.deadline) }}</div>
              </div>
            </template>
            <template #footer>
              <div v-if="getQuadrantTasks(q.statusMatch).length === 0" class="quadrant-empty">暂无任务</div>
            </template>
          </draggable>
        </div>
      </div>
    </div>

    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="side-card">
        <h2 class="card-title"><i class="fas fa-plus-circle"></i> 快速添加</h2>
        <form @submit.prevent="addTask">
          <div class="input-group">
            <input v-model="newTask.title" type="text" placeholder="要做点什么？" required autocomplete="off" />
            <input v-model="newTask.deadline" type="datetime-local" />
            <p class="hint"><i class="fas fa-bolt"></i> 不填时间则默认 15 分钟后截止</p>
            <button type="submit" :disabled="submitting">
              <template v-if="submitting"><i class="fas fa-spinner fa-spin"></i> 添加中...</template>
              <template v-else>添加任务</template>
            </button>
          </div>
        </form>
      </div>
      <div class="side-card ai-warning">
        <div class="ai-header"><i class="fas fa-robot"></i> AI 分析建议</div>
        <p class="ai-text">{{ aiWarning }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import draggable from 'vuedraggable'

const router = useRouter()

// === 数据 ===
const tasks = ref([])
const viewMode = ref('list')
const currentTab = ref('progress')
const submitting = ref(false)
const aiWarning = ref('你的状态非常棒！目前没有严重的拖延堆积，继续保持这种节奏。')
const newTask = ref({ title: '', deadline: '' })

const tabs = [
  { id: 'progress', name: '进行中', icon: 'fas fa-bullseye' },
  { id: 'attention', name: '需关注', icon: 'fas fa-bolt' },
  { id: 'archived', name: '已归档', icon: 'fas fa-archive' }
]

const quadrants = [
  { id: 'q1', name: '重要且紧急', emoji: '🔴', colorClass: 'q-red', statusMatch: 3 },
  { id: 'q2', name: '重要不紧急', emoji: '🟡', colorClass: 'q-yellow', statusMatch: 0 },
  { id: 'q3', name: '不重要但紧急', emoji: '🔵', colorClass: 'q-blue', statusMatch: 2 },
  { id: 'q4', name: '不重要不紧急', emoji: '⚪', colorClass: 'q-gray', statusMatch: 4 }
]

// === 计算属性 ===
const filteredTasks = computed(() => {
  if (currentTab.value === 'progress') return tasks.value.filter(t => t.status === 0)
  if (currentTab.value === 'attention') return tasks.value.filter(t => t.status === 2 || t.status === 3)
  if (currentTab.value === 'archived') return tasks.value.filter(t => t.status === 1)
  return []
})

const getTabCount = (tabId) => {
  if (tabId === 'progress') return tasks.value.filter(t => t.status === 0).length
  if (tabId === 'attention') return tasks.value.filter(t => t.status === 2 || t.status === 3).length
  if (tabId === 'archived') return tasks.value.filter(t => t.status === 1).length
  return 0
}

const getQuadrantTasks = (statusMatch) => {
  // 过滤出符合状态的任务，且不包括“已完成(1)”状态的任务，以防完成任务在四象限堆积
  return tasks.value.filter(t => t.status === statusMatch && t.status !== 1)
}

// === 工具函数 ===
const formatDate = (dateStr) => {
  if (!dateStr) return '无期限'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return '无期限'
  return `${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (s) => ({ 0: '正常', 1: '已完成', 2: '拖延', 3: '紧急', 4: '次要' }[s] || '未知')
const getStatusClass = (s) => ({ 0: 'badge-normal', 1: 'badge-done', 2: 'badge-delayed', 3: 'badge-urgent', 4: 'badge-normal' }[s] || 'badge-normal')

// === API 操作 ===
const fetchTasks = async () => {
  try {
    // 后端 TaskController: GET /task (返回裸数组，非 Result 包装)
    const res = await axios.get('/task')
    tasks.value = Array.isArray(res.data) ? res.data : (res.data?.data || [])
  } catch (e) {
    console.error('获取任务列表失败:', e)
  }
}

const addTask = async () => {
  if (!newTask.value.title.trim()) return
  submitting.value = true
  let deadline = newTask.value.deadline
  if (!deadline) {
    const now = new Date()
    now.setMinutes(now.getMinutes() + 15)
    deadline = new Date(now - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
  }
  try {
    // 后端 TaskController: POST /task/add 返回 201 CREATED
    const res = await axios.post('/task/add', { title: newTask.value.title, deadline })
    if (res.status === 200 || res.status === 201) {
      newTask.value = { title: '', deadline: '' }
      await fetchTasks()
    }
  } catch (e) {
    alert('添加失败，请重试')
  } finally {
    submitting.value = false
  }
}

const completeTask = async (id) => {
  try {
    await axios.post(`/task/${id}/complete`)
    await fetchTasks()
  } catch { alert('操作失败') }
}

const deleteTask = async (id) => {
  if (!confirm('确定删除此任务？')) return
  try {
    await axios.delete(`/task/${id}`)
    await fetchTasks()
  } catch { alert('删除失败') }
}

const enterFocusFlow = (title) => {
  router.push({ path: '/focus-flow', query: { task: title } })
}

const onMatrixChange = async (evt, newStatus) => {
  if (evt.added) {
    const task = evt.added.element
    const oldStatus = task.status
    if (oldStatus === newStatus) return

    // 先在前端更新状态以保证 UI 及时响应
    task.status = newStatus

    try {
      // 这里的接口应该更新任务状态。如果之前没有专门更新状态的接口，可以使用 PUT 完整更新
      await axios.put(`/task/${task.id}`, task)
    } catch (e) {
      console.error('更新状态失败', e)
      // 如果后端失败，回滚状态
      task.status = oldStatus
      alert('状态同步失败，请刷新页面')
    }
  }
}

// === 超时扫描 ===
let scanner = null
const startDeadlineScanner = () => {
  scanner = setInterval(() => {
    const now = new Date()
    let changed = false
    tasks.value.forEach(task => {
      if (task.status === 0 && task.deadline) {
        const dl = new Date(task.deadline.replace(' ', 'T'))
        if (!isNaN(dl.getTime()) && now > dl) {
          task.status = 2
          axios.post(`/task/${task.id}/delay`).catch(() => {})
          changed = true
        }
      }
    })
  }, 5000)
}

onMounted(() => {
  fetchTasks()
  startDeadlineScanner()
})

onUnmounted(() => { if (scanner) clearInterval(scanner) })
</script>

<style scoped>
:root {
  --bg-body: #F9FAFB; --bg-surface: #FFFFFF; --border-color: #E5E7EB;
  --text-primary: #111827; --text-secondary: #4B5563; --text-tertiary: #9CA3AF;
  --brand-primary: #111827;
  --status-normal-bg: #EEF2FF; --status-normal-text: #4F46E5;
  --status-urgent-bg: #FEF3C7; --status-urgent-text: #D97706;
  --status-delayed-bg: #FEE2E2; --status-delayed-text: #DC2626;
  --status-done-bg: #D1FAE5; --status-done-text: #059669;
  --radius-lg: 16px; --radius-md: 10px;
  --shadow-soft: 0 4px 20px rgba(0,0,0,0.03);
}

.workspace-container {
  max-width: 1200px; margin: 40px auto 60px; padding: 0 32px;
  display: grid; grid-template-columns: minmax(0,1fr) 340px; gap: 32px; align-items: start;
}
.workspace-header { grid-column: 1 / -1; margin-bottom: 8px; }
.header-row { display: flex; justify-content: space-between; align-items: flex-start; }
.page-title { color: #111827; font-size: 2rem; font-weight: 700; margin-bottom: 6px; }
.page-subtitle { color: #9CA3AF; font-size: 1rem; }

.view-toggle { display: flex; gap: 4px; background: #F3F4F6; border-radius: 10px; padding: 4px; }
.view-toggle-btn { width: 36px; height: 36px; border: none; border-radius: 8px; background: transparent; color: #9CA3AF; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.95rem; transition: all 0.2s; }
.view-toggle-btn:hover { color: #6B7280; }
.view-toggle-btn.active { background: #fff; color: #111827; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }

.task-board { background: #fff; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); border: 1px solid #E5E7EB; overflow: hidden; min-height: 500px; }

.folder-tabs { display: flex; gap: 8px; padding: 20px 24px 0; border-bottom: 1px solid #E5E7EB; background: #FAFAFA; }
.tab-btn { display: flex; align-items: center; gap: 8px; padding: 12px 20px; background: transparent; border: none; border-radius: 10px 10px 0 0; font-size: 0.95rem; font-weight: 600; color: #9CA3AF; cursor: pointer; transition: all 0.2s; position: relative; bottom: -1px; }
.tab-btn:hover { color: #111827; }
.tab-btn.active { background: #fff; color: #111827; border: 1px solid #E5E7EB; border-bottom-color: #fff; }
.tab-count { background: #F3F4F6; color: #4B5563; padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; }
.tab-btn.active .tab-count { background: #111827; color: white; }

.task-list { padding: 0; }
.task-card { padding: 20px 24px; border-bottom: 1px solid #E5E7EB; display: flex; justify-content: space-between; align-items: flex-start; transition: background 0.2s; }
.task-card:hover { background: #F9FAFB; }
.task-card:last-child { border-bottom: none; }

.card-left { display: flex; gap: 16px; }
.task-icon { font-size: 1.2rem; margin-top: 2px; color: #9CA3AF; }
.task-title { font-size: 1.05rem; font-weight: 600; color: #111827; margin-bottom: 6px; }
.completed-text { text-decoration: line-through; color: #9CA3AF; }
.task-meta { display: flex; gap: 16px; font-size: 0.8rem; color: #9CA3AF; align-items: center; }
.drag-handle { color: #D1D5DB; cursor: grab; font-size: 0.9rem; margin-right: 6px; }

.card-right { display: flex; align-items: center; gap: 16px; }
.status-badge { padding: 4px 10px; border-radius: 999px; font-size: 0.75rem; font-weight: 600; letter-spacing: 0.5px; }
.badge-normal { background: #EEF2FF; color: #4F46E5; }
.badge-urgent { background: #FEF3C7; color: #D97706; }
.badge-delayed { background: #FEE2E2; color: #DC2626; }
.badge-done { background: #D1FAE5; color: #059669; }

.task-actions { display: flex; gap: 8px; opacity: 0; transition: opacity 0.2s; }
.task-card:hover .task-actions { opacity: 1; }
.action-btn { width: 32px; height: 32px; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.85rem; transition: background 0.2s; }
.action-btn.check { color: #059669; background: #D1FAE5; }
.action-btn.check:hover { background: #10B981; color: white; }
.action-btn.focus { color: #6366F1; background: #EEF2FF; }
.action-btn.focus:hover { background: #6366F1; color: white; }
.action-btn.delete { color: #DC2626; background: transparent; }
.action-btn.delete:hover { background: #FEE2E2; }

.empty-state { text-align: center; padding: 60px 20px; color: #9CA3AF; }
.empty-icon { font-size: 3rem; margin-bottom: 12px; opacity: 0.5; }

.sidebar { display: flex; flex-direction: column; gap: 24px; }
.side-card { background: #fff; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); border: 1px solid #E5E7EB; padding: 24px; }
.card-title { color: #111827; font-size: 1.1rem; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.input-group { display: flex; flex-direction: column; gap: 12px; }
.input-group input { width: 100%; padding: 12px 14px; border: 1px solid #E5E7EB; border-radius: 10px; font-size: 0.95rem; background: #F9FAFB; transition: all 0.2s; }
.input-group input:focus { outline: none; border-color: #111827; background: #fff; box-shadow: 0 0 0 3px rgba(17,24,39,0.1); }
.input-group button[type="submit"] { width: 100%; padding: 12px; background: #111827; color: white; border: none; border-radius: 10px; font-size: 0.95rem; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.input-group button[type="submit"]:hover { background: #000; transform: translateY(-1px); }
.input-group button[type="submit"]:disabled { opacity: 0.7; cursor: not-allowed; }
.hint { font-size: 0.8rem; color: #9CA3AF; margin: 0 0 8px 4px; }

.ai-warning { background: linear-gradient(145deg, #FFFBEB, #fff); border-color: #FEF3C7; }
.ai-header { color: #D97706; font-weight: 600; margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
.ai-text { color: #92400E; font-size: 0.95rem; line-height: 1.6; }

/* 四象限 */
.matrix-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.matrix-quadrant { background: #fff; border-radius: 16px; border: 1px solid #F3F4F6; box-shadow: 0 1px 3px rgba(0,0,0,0.04); min-height: 180px; display: flex; flex-direction: column; overflow: hidden; }
.quadrant-header { padding: 14px 16px; display: flex; align-items: center; gap: 8px; }
.quadrant-title { font-size: 0.9rem; font-weight: 700; color: #374151; flex: 1; }
.quadrant-count { background: rgba(0,0,0,0.06); color: #6B7280; padding: 2px 8px; border-radius: 10px; font-size: 0.7rem; font-weight: 600; }
.quadrant-body { flex: 1; padding: 12px; display: flex; flex-direction: column; gap: 8px; min-height: 60px; }
.quadrant-empty { padding: 20px; text-align: center; color: #D1D5DB; font-size: 0.85rem; }
.q-red .quadrant-header { background: linear-gradient(135deg, #FEF2F2, #FFF); border-bottom: 2px solid #FCA5A5; }
.q-yellow .quadrant-header { background: linear-gradient(135deg, #FFFBEB, #FFF); border-bottom: 2px solid #FCD34D; }
.q-blue .quadrant-header { background: linear-gradient(135deg, #EFF6FF, #FFF); border-bottom: 2px solid #93C5FD; }
.q-gray .quadrant-header { background: linear-gradient(135deg, #F9FAFB, #FFF); border-bottom: 2px solid #D1D5DB; }
.matrix-task-card { background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 10px; padding: 12px 14px; cursor: grab; transition: all 0.2s; }
.matrix-task-card:hover { background: #fff; box-shadow: 0 4px 8px rgba(0,0,0,0.04); transform: translateY(-1px); }
.matrix-task-title { font-size: 0.9rem; font-weight: 600; color: #111827; margin-bottom: 4px; }
.matrix-task-meta { font-size: 0.75rem; color: #9CA3AF; }

@media (max-width: 960px) {
  .workspace-container { grid-template-columns: 1fr; padding: 0 16px; margin-top: 20px;}
  .task-actions { opacity: 1; }
  .matrix-grid { grid-template-columns: 1fr; }
  
  /* Sidebar moves to bottom, we can adjust its padding/margin */
  .sidebar { margin-top: 24px; }
}

@media (max-width: 768px) {
  .page-title { font-size: 1.5rem; }
  .header-row { flex-direction: column; gap: 12px; }
  .view-toggle { align-self: flex-start; }
  
  .folder-tabs { flex-wrap: wrap; padding: 12px 12px 0; }
  .tab-btn { flex: 1; justify-content: center; padding: 10px 8px; font-size: 0.85rem; }
  
  .task-card { padding: 16px 12px; flex-direction: column; gap: 12px; }
  .card-right { width: 100%; justify-content: space-between; }
  
  /* 触控优化：增大按钮点击区域 */
  .action-btn { width: 44px; height: 44px; font-size: 1rem; }
  .view-toggle-btn { width: 44px; height: 44px; }
  
  /* 移除 hover 效果，使用 active */
  @media (hover: none) and (pointer: coarse) {
    .task-card:hover { background: transparent; }
    .task-card:active { background: #F9FAFB; }
    .action-btn.check:hover { background: #D1FAE5; color: #059669; }
    .action-btn.check:active { background: #10B981; color: white; }
    .action-btn.focus:hover { background: #EEF2FF; color: #6366F1; }
    .action-btn.focus:active { background: #6366F1; color: white; }
    .action-btn.delete:hover { background: transparent; }
    .action-btn.delete:active { background: #FEE2E2; }
    .matrix-task-card:hover { background: #F9FAFB; transform: none; box-shadow: none; }
    .matrix-task-card:active { background: #fff; }
  }
}
</style>
