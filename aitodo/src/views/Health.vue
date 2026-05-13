<template>
  <div class="health-container">
    <header class="health-header">
      <h1 class="health-title">系统健康检查</h1>
      <p class="health-subtitle">实时监控系统运行状态，确保服务稳定可靠。所有健康指标每10秒自动更新。</p>
    </header>

    <div class="status-grid">
      <div class="status-card">
        <div class="status-icon" :class="statusIconClass"><i :class="statusIconName"></i></div>
        <h3 class="status-name" :style="{ color: statusColor }">{{ statusName }}</h3>
        <div class="status-value">{{ healthData.status }}</div>
        <div class="status-label">服务状态</div>
        <div class="status-timestamp">最后更新: {{ lastUpdateTime }}</div>
      </div>
      <div class="status-card">
        <div class="status-icon healthy"><i class="fas fa-heartbeat"></i></div>
        <h3 class="status-name">服务名称</h3>
        <div class="status-value">{{ healthData.service }}</div>
        <div class="status-label">应用标识</div>
      </div>
      <div class="status-card">
        <div class="status-icon healthy"><i class="fas fa-code-branch"></i></div>
        <h3 class="status-name">版本</h3>
        <div class="status-value">{{ healthData.version }}</div>
        <div class="status-label">当前版本</div>
      </div>
    </div>

    <div class="health-details">
      <h2 class="details-title">健康详情</h2>
      <div class="details-grid">
        <div class="detail-item"><div class="detail-label">API 端点</div><div class="detail-value">/api/health</div></div>
        <div class="detail-item"><div class="detail-label">Ping 测试</div><div class="detail-value" :style="{ color: pingOk ? '#10b981' : '#ef4444' }">{{ pingOk ? '正常' : '失败' }}</div></div>
        <div class="detail-item"><div class="detail-label">响应时间</div><div class="detail-value">{{ responseTimeMs }}ms</div></div>
        <div class="detail-item"><div class="detail-label">检查次数</div><div class="detail-value">{{ checkCount }}</div></div>
      </div>
    </div>

    <div class="health-actions">
      <button class="btn-primary-large" @click="refresh"><i class="fas fa-sync-alt"></i> 立即刷新</button>
      <router-link to="/" class="btn-secondary-large"><i class="fas fa-home"></i> 返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const healthData = ref({ status: 'UP', service: 'AI Todo', version: '1.0.0' })
const checkCount = ref(0)
const responseTimeMs = ref(0)
const pingOk = ref(true)
const lastUpdateTime = ref('加载中...')
let timer = null

const statusIconClass = computed(() => healthData.value.status === 'UP' ? 'healthy' : 'danger')
const statusIconName = computed(() => healthData.value.status === 'UP' ? 'fas fa-check-circle' : 'fas fa-times-circle')
const statusName = computed(() => healthData.value.status === 'UP' ? '运行正常' : '服务异常')
const statusColor = computed(() => healthData.value.status === 'UP' ? '' : '#ef4444')

const fetchHealth = async () => {
  const start = Date.now()
  try {
    const res = await fetch('/api/health')
    const data = await res.json()
    healthData.value = { status: data.status || 'UP', service: data.service || 'AI Todo', version: data.version || '1.0.0' }
    responseTimeMs.value = Date.now() - start
    pingOk.value = true
  } catch {
    healthData.value.status = 'DOWN'
    responseTimeMs.value = Date.now() - start
    pingOk.value = false
  }
  checkCount.value++
  lastUpdateTime.value = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const refresh = () => fetchHealth()

onMounted(() => { fetchHealth(); timer = setInterval(fetchHealth, 10000) })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
:root { --success: #10b981; --warning: #f59e0b; --danger: #ef4444; }
.health-container { max-width: 800px; margin: 40px auto; padding: 0 20px; }
.health-header { text-align: center; margin-bottom: 3rem; }
.health-title { font-size: 2.5rem; font-weight: 700; margin-bottom: 1rem; background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.health-subtitle { font-size: 1.1rem; color: var(--text-light); max-width: 600px; margin: 0 auto; }
.status-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }
.status-card { background: var(--bg-light); border-radius: var(--radius); padding: 2rem; border: 1px solid var(--border); transition: var(--transition); text-align: center; }
.status-card:hover { transform: translateY(-5px); box-shadow: var(--shadow-hover); border-color: transparent; }
.status-icon { font-size: 2.5rem; margin-bottom: 1rem; }
.status-icon.healthy { color: #10b981; }
.status-icon.danger { color: #ef4444; }
.status-name { font-size: 1.25rem; font-weight: 600; margin-bottom: 0.5rem; color: var(--text); }
.status-value { font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--primary); }
.status-label { font-size: 0.875rem; color: var(--text-light); }
.status-timestamp { font-size: 0.75rem; color: var(--text-lighter); margin-top: 1rem; font-style: italic; }
.health-details { background: var(--bg-light); border-radius: var(--radius); padding: 2rem; border: 1px solid var(--border); margin-bottom: 3rem; }
.details-title { font-size: 1.5rem; font-weight: 600; margin-bottom: 1.5rem; color: var(--text); }
.details-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; }
.detail-item { padding: 1rem; background: var(--bg); border-radius: var(--radius); border: 1px solid var(--border); }
.detail-label { font-size: 0.85rem; color: var(--text-light); margin-bottom: 0.5rem; text-transform: uppercase; font-weight: 500; }
.detail-value { font-size: 1.1rem; font-weight: 600; color: var(--text); }
.health-actions { display: flex; gap: 1rem; justify-content: center; margin-bottom: 3rem; }
@media (max-width: 768px) { .health-title { font-size: 2rem; } .health-actions { flex-direction: column; align-items: center; } }
</style>
