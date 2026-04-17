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

      <div v-else class="leaderboard-content">

        <div class="olympic-podium" v-if="leaderboard.length > 0">

          <div class="podium-item silver-medal" v-if="rank2">
            <div class="medal-badge">🥈</div>
            <img :src="rank2.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${rank2.username}`" class="avatar-img" />
            <div class="user-name">{{ rank2.username }}</div>
            <div class="score-info"><span class="pts">{{ rank2.points }}</span> pts</div>
            <div class="level-tag">Lv.{{ rank2.level }}</div>
          </div>

          <div class="podium-item gold-medal" v-if="rank1">
            <div class="medal-badge winner-badge">🏆</div>
            <img :src="rank1.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${rank1.username}`" class="avatar-img" />
            <div class="user-name">{{ rank1.username }}</div>
            <div class="score-info"><span class="pts">{{ rank1.points }}</span> pts</div>
            <div class="level-tag">Lv.{{ rank1.level }}</div>
          </div>

          <div class="podium-item bronze-medal" v-if="rank3">
            <div class="medal-badge">🥉</div>
            <img :src="rank3.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${rank3.username}`" class="avatar-img" />
            <div class="user-name">{{ rank3.username }}</div>
            <div class="score-info"><span class="pts">{{ rank3.points }}</span> pts</div>
            <div class="level-tag">Lv.{{ rank3.level }}</div>
          </div>

        </div>

        <div class="table-responsive" v-if="leaderboard.length > 3">
          <table class="leaderboard-table">
            <tbody>
            <tr v-for="(item, index) in leaderboard.slice(3)" :key="item.userId || index">
              <td width="15%" class="rank-cell">
                <div class="rank-number">{{ index + 4 }}</div>
              </td>
              <td width="35%" class="user-cell">
                <div class="avatar-group-small">
                  <img :src="item.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}`" class="avatar-sm" />
                  <span class="username-sm">{{ item.username }}</span>
                </div>
              </td>
              <td width="25%" class="score-cell">
                <span class="score-value">{{ item.points }}</span> <span class="score-label">pts</span>
              </td>
              <td width="25%" class="level-cell">
                <div class="level-indicator">Lv.{{ item.level }}</div>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div v-if="!leaderboard || leaderboard.length === 0" class="empty-state">
          <i class="fas fa-ghost"></i>
          <p>排行榜空空如也，快去完成任务上榜吧！</p>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

const leaderboard = ref([])
const loading = ref(true)
const error = ref('')
let pollInterval = null

// 🌟 绝杀逻辑：精准提取出真正的 1、2、3 名数据，防止 DOM 渲染错位！
const rank1 = computed(() => leaderboard.value[0] || null) // 真正的第 1 名
const rank2 = computed(() => leaderboard.value[1] || null) // 真正的第 2 名
const rank3 = computed(() => leaderboard.value[2] || null) // 真正的第 3 名

const fetchLeaderboard = async () => {
  try {
    error.value = ''
    const response = await axios.get('/stats/leaderboard')
    let rawData = response.data.data || []

    // 严谨的双重排序：等级优先，同级比积分
    rawData.sort((a, b) => {
      const levelA = Number(a.level) || 0;
      const levelB = Number(b.level) || 0;
      const pointsA = Number(a.points) || 0;
      const pointsB = Number(b.points) || 0;

      if (levelA !== levelB) {
        return levelB - levelA;
      }
      return pointsB - pointsA;
    })

    leaderboard.value = rawData.map((item, index) => ({
      ...item,
      rank: index + 1
    }))

  } catch (err) {
    if (leaderboard.value.length === 0) {
      error.value = '加载排行榜数据失败: ' + (err.message || '未知错误')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loading.value = true
  fetchLeaderboard()
  pollInterval = setInterval(() => {
    fetchLeaderboard()
  }, 5000)
})

onUnmounted(() => {
  if (pollInterval) clearInterval(pollInterval)
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
  max-width: 900px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08), 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-header {
  text-align: center;
  padding: 3rem 2rem 2rem;
  background: linear-gradient(135deg, #4a6fa5 0%, #16222A 100%);
  color: white;
  position: relative;
}

.header-icon {
  font-size: 3rem;
  color: #fbdb4a;
  margin-bottom: 1rem;
  animation: float 3s ease-in-out infinite;
  text-shadow: 0 0 20px rgba(251, 219, 74, 0.4);
}

.card-header h2 { margin: 0; font-size: 2rem; font-weight: 700; letter-spacing: 1px; }
.subtitle { color: rgba(255, 255, 255, 0.8); margin-top: 0.5rem; }

/* ==============================
   🌟 完美的奥运会领奖台 CSS
   ============================== */
.olympic-podium {
  display: flex;
  justify-content: center;
  align-items: flex-end; /* 底部对齐，让中间最高 */
  gap: 15px;
  padding: 3rem 1rem 1rem;
  margin-bottom: 1.5rem;
}

.podium-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #ffffff;
  border-radius: 16px 16px 0 0; /* 底部直角，像真实的台子 */
  padding: 20px 15px;
  width: 30%;
  max-width: 180px;
  position: relative;
  transition: transform 0.3s ease;
  box-shadow: 0 -4px 12px rgba(0,0,0,0.03);
}

.podium-item:hover {
  transform: translateY(-5px);
}

/* 🏅 第一名：最高，视觉最凸显 */
.gold-medal {
  height: 220px; /* 拔高第一名 */
  background: linear-gradient(to bottom, #fffcf5 0%, #ffffff 100%);
  border: 2px solid rgba(246, 211, 101, 0.5);
  border-bottom: none;
  box-shadow: 0 -10px 25px rgba(246, 211, 101, 0.15);
  z-index: 2;
}

/* 🥈 第二名：左边，次高 */
.silver-medal {
  height: 180px;
  border: 1px solid #e2e8f0;
  border-bottom: none;
}

/* 🥉 第三名：右边，最低 */
.bronze-medal {
  height: 160px;
  border: 1px solid #e2e8f0;
  border-bottom: none;
}

.medal-badge {
  font-size: 1.8rem;
  position: absolute;
  top: -20px;
  background: white;
  border-radius: 50%;
  padding: 5px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.winner-badge {
  font-size: 2.5rem;
  top: -25px;
}

.avatar-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  margin-top: 15px;
  margin-bottom: 12px;
  object-fit: cover;
}

.gold-medal .avatar-img {
  width: 75px;
  height: 75px;
  border: 3px solid #f6d365;
  margin-top: 25px;
}

.user-name { font-weight: 700; color: #2d3748; font-size: 1.1rem; margin-bottom: 4px; text-align: center; }
.score-info { color: #718096; font-size: 0.9rem; margin-bottom: 8px; }
.pts { font-family: 'Roboto Mono', monospace; font-weight: 800; color: #4a6fa5; font-size: 1.2rem; }
.level-tag { background: rgba(74, 111, 165, 0.1); color: #4a6fa5; padding: 4px 12px; border-radius: 20px; font-weight: 700; font-size: 0.85rem; }

/* ==============================
   📋 剩余列表 CSS
   ============================== */
.table-responsive { padding: 0 2rem 2rem; overflow-x: auto; }
.leaderboard-table { width: 100%; border-collapse: separate; border-spacing: 0 8px; }
.leaderboard-table td { padding: 12px 20px; background: #fff; vertical-align: middle; border-top: 1px solid #f7fafc; border-bottom: 1px solid #f7fafc; }
.leaderboard-table tr { box-shadow: 0 2px 4px rgba(0,0,0,0.02); transition: transform 0.2s; }
.leaderboard-table tr:hover { transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0,0,0,0.05); }
.leaderboard-table td:first-child { border-radius: 12px 0 0 12px; border-left: 1px solid #f7fafc; }
.leaderboard-table td:last-child { border-radius: 0 12px 12px 0; border-right: 1px solid #f7fafc; }

.rank-number { width: 30px; height: 30px; background: #f7fafc; color: #718096; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.avatar-group-small { display: flex; align-items: center; gap: 12px; }
.avatar-sm { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 1px solid #e2e8f0; }
.username-sm { font-weight: 600; color: #2d3748; }

.state-container { padding: 4rem 2rem; text-align: center; color: #718096; }
.spinner { width: 40px; height: 40px; border: 4px solid rgba(74, 111, 165, 0.2); border-left-color: #4a6fa5; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 1rem; }
.empty-state { text-align: center; padding: 3rem !important; color: #a0aec0; }
.empty-state i { font-size: 3rem; margin-bottom: 1rem; opacity: 0.5; }

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 640px) {
  .olympic-podium { align-items: flex-end; gap: 5px; padding: 2rem 0.5rem 1rem; }
  .podium-item { padding: 15px 5px; width: 32%; }
  .gold-medal { height: 180px; }
  .silver-medal { height: 150px; }
  .bronze-medal { height: 130px; }
  .avatar-img { width: 40px; height: 40px; margin-top: 10px; }
  .gold-medal .avatar-img { width: 50px; height: 50px; margin-top: 15px; }
  .user-name { font-size: 0.9rem; }
  .pts { font-size: 1rem; }
  .table-responsive { padding: 0 1rem 1rem; }
}
</style>