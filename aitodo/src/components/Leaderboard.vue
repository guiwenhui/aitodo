<template>
  <div class="leaderboard-wrapper">
    <div class="leaderboard-card">
      <div class="card-header">
        <div class="header-icon">
          <i class="fas fa-trophy"></i>
        </div>
        <h2>积分排行榜</h2>
        <p class="subtitle">看看谁是真正的抗拖延斗士！</p>
      </div>
      
      <div v-if="loading" class="state-container loading-state">
        <div class="spinner"></div>
        <p>正在拉取最新排名...</p>
      </div>
      
      <div v-else-if="error" class="state-container error-state">
        <i class="fas fa-exclamation-circle error-icon"></i>
        <p>{{ error }}</p>
      </div>
      
      <div class="table-responsive" v-else>
        <table class="leaderboard-table">
          <thead>
            <tr>
              <th width="15%">排名</th>
              <th width="35%">用户</th>
              <th width="25%">积分</th>
              <th width="25%">等级</th>
            </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in leaderboard" :key="item.userId" :class="{'top-three': index < 3}" :style="{ animationDelay: `${index * 0.1}s` }">
              <td class="rank-cell">
                <div class="rank-badge" :class="`rank-${index + 1}`">
                  <span v-if="index === 0">🏆</span>
                  <span v-else-if="index === 1">🥈</span>
                  <span v-else-if="index === 2">🥉</span>
                  <span v-else>{{ item.rank }}</span>
                </div>
              </td>
              <td class="user-cell">
                <div class="avatar-group">
                  <div class="avatar">
                    <img :src="`https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}`" alt="avatar">
                  </div>
                  <span class="username">{{ item.username }}</span>
                </div>
              </td>
              <td class="score-cell">
                <span class="score-value">{{ item.points }}</span>
                <span class="score-label">pts</span>
              </td>
              <td class="level-cell">
                <div class="level-indicator">
                  Lv.{{ item.level }}
                </div>
              </td>
            </tr>
            <tr v-if="!leaderboard || leaderboard.length === 0">
              <td colspan="4" class="empty-state">
                <i class="fas fa-ghost"></i>
                <p>排行榜空空如也，快去完成任务上榜吧！</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

const leaderboard = ref([])
const loading = ref(true)
const error = ref('')
let pollInterval = null

const fetchLeaderboard = async () => {
  try {
    error.value = ''
    const response = await axios.get('/stats/leaderboard')
    let rawData = response.data.data || []

    // 🌟 终极必杀技：前端强制兜底双重排序！
    // 不管后端怎么排的，我们在这里彻底接管：优先比等级(level)，等级相同比积分(points)
    rawData.sort((a, b) => {
      // 1. 先比等级 (降序：b - a)
      if (b.level !== a.level) {
        return b.level - a.level
      }
      // 2. 如果等级一样，再比积分 (降序)
      return b.points - a.points
    })

    // 🌟 重新洗牌后，强制覆盖排名 (rank)，确保奖杯图标对号入座
    leaderboard.value = rawData.map((item, index) => {
      return {
        ...item,
        rank: index + 1 // 第一名就是 1，第二名就是 2...
      }
    })

  } catch (err) {
    if (leaderboard.value.length === 0) {
      error.value = '加载排行榜数据失败: ' + (err.message || '未知错误')
    }
    console.error('Failed to fetch leaderboard:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loading.value = true
  fetchLeaderboard()
  // Poll every 5 seconds for instant updates
  pollInterval = setInterval(() => {
    fetchLeaderboard()
  }, 5000)
})

onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
})
</script>

<style scoped>
.leaderboard-wrapper {
  padding: 2rem 1rem;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.leaderboard-card {
  width: 100%;
  max-width: 800px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08), 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease;
}

.card-header {
  text-align: center;
  padding: 3rem 2rem 2rem;
  background: linear-gradient(135deg, #4a6fa5 0%, #16222A 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.card-header::after {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: radial-gradient(circle at top right, rgba(255,255,255,0.1) 0%, transparent 60%);
}

.header-icon {
  font-size: 3rem;
  color: #fbdb4a;
  margin-bottom: 1rem;
  animation: float 3s ease-in-out infinite;
  text-shadow: 0 0 20px rgba(251, 219, 74, 0.4);
}

.card-header h2 {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 1px;
}

.subtitle {
  color: rgba(255, 255, 255, 0.8);
  margin-top: 0.5rem;
  font-size: 1rem;
}

.table-responsive {
  padding: 1rem 2rem 2rem;
  overflow-x: auto;
}

.leaderboard-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 12px;
}

.leaderboard-table th {
  text-align: left;
  padding: 12px 20px;
  color: #718096;
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  border-bottom: 2px solid #edf2f7;
}

.leaderboard-table tbody tr {
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  animation: slideIn 0.5s ease backwards;
}

.leaderboard-table tbody tr:hover {
  transform: translateY(-3px) scale(1.01);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  z-index: 10;
  position: relative;
}

.leaderboard-table td {
  padding: 16px 20px;
  vertical-align: middle;
}

.leaderboard-table td:first-child {
  border-top-left-radius: 12px;
  border-bottom-left-radius: 12px;
}

.leaderboard-table td:last-child {
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
}

/* Rank Badge Styles */
.rank-badge {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1rem;
  background: #f7fafc;
  color: #4a5568;
  border: 2px solid #e2e8f0;
}

.rank-1 { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); border: none; font-size: 1.2rem; box-shadow: 0 4px 10px rgba(246, 211, 101, 0.4); }
.rank-2 { background: linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%); border: none; font-size: 1.1rem; box-shadow: 0 4px 10px rgba(142, 197, 252, 0.4); }
.rank-3 { background: linear-gradient(135deg, #fbc2eb 0%, #a6c1ee 100%); border: none; font-size: 1.1rem; box-shadow: 0 4px 10px rgba(251, 194, 235, 0.4); }

.top-three {
  background: linear-gradient(to right, rgba(255,255,255,1) 0%, rgba(245,247,250,1) 100%) !important;
}

/* User styles */
.avatar-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #edf2f7;
  overflow: hidden;
  border: 2px solid #fff;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.username {
  font-weight: 600;
  color: #2d3748;
  font-size: 1.05rem;
}

/* Score Styles */
.score-cell {
  font-family: 'Roboto Mono', monospace;
}

.score-value {
  font-weight: 800;
  font-size: 1.2rem;
  color: #4a6fa5;
}

.score-label {
  font-size: 0.8rem;
  color: #a0aec0;
  margin-left: 4px;
}

/* Level Styles */
.level-indicator {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(74, 111, 165, 0.1);
  color: #4a6fa5;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}

/* States */
.state-container {
  padding: 4rem 2rem;
  text-align: center;
  color: #718096;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(74, 111, 165, 0.2);
  border-left-color: #4a6fa5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.error-icon {
  font-size: 3rem;
  color: #e53e3e;
  margin-bottom: 1rem;
}

.empty-state {
  text-align: center;
  padding: 3rem !important;
  color: #a0aec0;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

/* Animations */
@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 640px) {
  .leaderboard-wrapper {
    padding: 1rem 0.5rem;
  }
  .card-header { padding: 2rem 1rem 1.5rem; }
  .table-responsive { padding: 0.5rem; }
  .leaderboard-table th, .leaderboard-table td { padding: 12px 10px; }
  .user-cell { min-width: 150px; }
}
</style>