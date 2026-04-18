<template>
  <div class="leaderboard-wrapper">
    <div class="ambient-glow"></div>

    <div class="leaderboard-container">
      <div class="header-content">
        <h2 class="title">荣誉排行榜</h2>
        <p class="subtitle">Rankings & Achievements</p>
      </div>

      <div v-if="loading" class="state-box">
        <div class="spinner"></div>
        <p>正在同步数据...</p>
      </div>

      <div v-else-if="error" class="state-box error-box">
        <span class="error-text"><i class="fas fa-exclamation-triangle"></i> {{ error }}</span>
      </div>

      <div v-else-if="leaderboard.length === 0" class="empty-state">
        <div class="empty-circle">
          <div class="pulse-ring"></div>
        </div>
        <p>暂无数据</p>
        <span>等待第一位探索者</span>
      </div>

      <div v-else class="content-body">
        <div class="podium-wrapper">
          <div v-for="item in topThree" :key="item.userId || item.rank"
               class="podium-card" :class="'rank-' + item.rank">

            <div class="rank-number">0{{ item.rank }}</div>

            <div class="avatar-container">
              <img :src="item.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}`"
                   class="avatar" />
              <div v-if="item.rank === 1" class="crown-icon">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
              </div>
            </div>

            <div class="user-info">
              <span class="name">{{ item.username }}</span>
              <div class="stat-text">
                <span>Lv.{{ getLevel(item) }}</span>
                <span class="dot">·</span>
                <span class="points">{{ getPoints(item) }} pts</span>
              </div>
            </div>
          </div>
        </div>

        <div class="table-container">
          <table class="minimal-table">
            <thead>
            <tr>
              <th class="col-rank">Rank</th>
              <th class="col-user text-left">User</th>
              <th class="col-level">Level</th>
              <th class="col-points text-right">Points</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in restList" :key="item.userId || item.rank" class="table-row">
              <td class="col-rank">
                <span class="rank-num">{{ item.rank < 10 ? '0' + item.rank : item.rank }}</span>
              </td>
              <td class="col-user">
                <div class="user-cell">
                  <img :src="item.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}`" class="mini-avatar" />
                  <span class="user-name">{{ item.username }}</span>
                </div>
              </td>
              <td class="col-level"><span class="lv-text">Lv.{{ getLevel(item) }}</span></td>
              <td class="col-points text-right"><span class="points-text">{{ getPoints(item) }}</span></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const leaderboard = ref([])
const loading = ref(true)
const error = ref('')

const getLevel = (item) => parseInt(item.level || item.currentLevel || 0, 10);
const getPoints = (item) => parseInt(item.points || item.totalPoints || 0, 10);

const topThree = computed(() => {
  const top = leaderboard.value.slice(0, 3)
  if (top.length === 3) return [top[1], top[0], top[2]]
  if (top.length === 2) return [top[1], top[0]]
  return top
})

const restList = computed(() => leaderboard.value.slice(3))

const fetchLeaderboard = async () => {
  try {
    loading.value = true
    const response = await axios.get('/stats/leaderboard')
    if (response.data?.code === 200) {
      let data = response.data.data || []
      data.sort((a, b) => (getLevel(b) !== getLevel(a)) ? getLevel(b) - getLevel(a) : getPoints(b) - getPoints(a))
      leaderboard.value = data.map((item, index) => ({ ...item, rank: index + 1 }))
    } else {
      error.value = '数据同步失败，请稍后再试'
    }
  } catch (err) {
    error.value = '服务器连接超时'
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchLeaderboard() })
</script>

<style scoped>
/* 🌟 背景黑魔法：点阵 + 微光晕 */
.leaderboard-wrapper {
  position: relative;
  min-height: 100vh;
  padding: 40px 20px;
  background-color: #fafafa;
  /* 精致的点阵网格 */
  background-image: radial-gradient(#d1d5db 1px, transparent 1px);
  background-size: 24px 24px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  overflow: hidden;
}

/* 顶部紫蓝色环境光晕 */
.ambient-glow {
  position: absolute;
  top: -150px;
  left: 50%;
  transform: translateX(-50%);
  width: 800px;
  height: 400px;
  background: radial-gradient(ellipse at center, rgba(99, 102, 241, 0.15) 0%, rgba(250, 250, 250, 0) 70%);
  z-index: 0;
  pointer-events: none;
}

/* 🌟 主容器：悬浮卡片感 */
.leaderboard-container {
  position: relative;
  z-index: 1;
  max-width: 880px;
  margin: 0 auto;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  box-shadow: 0 20px 40px -15px rgba(0, 0, 0, 0.05), 0 0 0 1px rgba(229, 231, 235, 0.4);
  color: #111827;
  animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* 头部 */
.header-content { margin-bottom: 48px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid rgba(229, 231, 235, 0.5); padding-bottom: 20px;}
.title { font-size: 1.5rem; font-weight: 700; color: #111827; margin: 0; letter-spacing: -0.02em; }
.subtitle { color: #8b92a5; font-size: 0.85rem; font-weight: 500; text-transform: uppercase; letter-spacing: 0.1em; background: rgba(243, 244, 246, 0.8); padding: 4px 12px; border-radius: 20px;}

/* 领奖台区域 */
.podium-wrapper { display: flex; justify-content: center; align-items: flex-end; gap: 24px; margin-bottom: 64px; }

.podium-card {
  width: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 16px 24px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px -1px rgba(0, 0, 0, 0.02);
}

.podium-card:hover { transform: translateY(-4px); box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02); border-color: #d1d5db; }

/* 领奖台层级微调 */
.rank-1 { height: 220px; border-top: 3px solid #6366f1; transform: translateY(-12px); box-shadow: 0 12px 20px -5px rgba(99, 102, 241, 0.1); }
.rank-1:hover { transform: translateY(-16px); box-shadow: 0 15px 25px -5px rgba(99, 102, 241, 0.15); }
.rank-2 { height: 180px; }
.rank-3 { height: 180px; }

/* 极简数字 */
.rank-number { position: absolute; top: -16px; background: #ffffff; padding: 0 12px; font-size: 1.25rem; font-weight: 700; color: #d1d5db; font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; border-radius: 4px;}
.rank-1 .rank-number { color: #6366f1; }

/* 头像设计 */
.avatar-container { position: relative; margin-bottom: 20px; }
.avatar { width: 64px; height: 64px; border-radius: 50%; border: 1px solid #e5e7eb; object-fit: cover; }
.rank-1 .avatar { width: 76px; height: 76px; border: 2px solid #6366f1; padding: 2px; }

/* 第一名皇冠角标 */
.crown-icon { position: absolute; bottom: -6px; right: -6px; background: #111827; color: white; border-radius: 50%; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; border: 2px solid #fff; }

/* 用户信息 */
.user-info { text-align: center; width: 100%; }
.user-info .name { display: block; font-weight: 600; font-size: 0.95rem; color: #111827; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;}
.stat-text { display: flex; align-items: center; justify-content: center; gap: 6px; font-size: 0.8rem; color: #6b7280; font-weight: 500; }
.dot { color: #d1d5db; font-weight: bold; }
.points { color: #6366f1; font-weight: 600; }

/* 表格区域 */
.table-container { background: transparent; }
.minimal-table { width: 100%; border-collapse: separate; border-spacing: 0 8px; text-align: left; }
.minimal-table th { padding: 0 16px 12px; font-size: 0.75rem; font-weight: 600; color: #8b92a5; text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid rgba(229, 231, 235, 0.5); }
.minimal-table td { padding: 16px; background: #ffffff; vertical-align: middle; transition: transform 0.2s, box-shadow 0.2s;}

/* 表格行的圆角与阴影独立化 */
.minimal-table td:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.minimal-table td:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }
.table-row { box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02); }
.table-row:hover td { transform: scale(1.005); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03); background: #ffffff;}

.col-rank { width: 80px; text-align: center; }
.col-user { width: auto; }
.col-level { width: 100px; text-align: center; }
.col-points { width: 120px; }
.text-left { text-align: left !important; }
.text-right { text-align: right !important; }

/* 内部细节 */
.rank-num { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; color: #9ca3af; font-size: 0.875rem; font-weight: 600;}
.user-cell { display: flex; align-items: center; gap: 12px; }
.mini-avatar { width: 32px; height: 32px; border-radius: 50%; border: 1px solid #f3f4f6; }
.user-name { font-weight: 500; color: #111827; font-size: 0.9rem; }
.lv-text { color: #6b7280; font-size: 0.85rem; font-weight: 500; }
.points-text { font-family: ui-monospace, SFMono-Regular, monospace; font-weight: 600; color: #111827; font-size: 0.9rem;}

/* 状态框动效优化 */
.state-box { padding: 80px 0; display: flex; flex-direction: column; align-items: center; gap: 16px; color: #8b92a5; font-size: 0.875rem; }
.spinner { width: 24px; height: 24px; border: 2px solid rgba(229, 231, 235, 0.5); border-top: 2px solid #6366f1; border-radius: 50%; animation: spin 0.8s linear infinite; }
.error-text { color: #ef4444; background: #fef2f2; padding: 10px 20px; border-radius: 8px; font-weight: 500; display: flex; align-items: center; gap: 8px;}

/* 空状态动画 */
.empty-state { padding: 100px 0; text-align: center; display: flex; flex-direction: column; align-items: center;}
.empty-circle { width: 64px; height: 64px; border-radius: 50%; border: 1px solid #e5e7eb; margin-bottom: 20px; position: relative; display: flex; align-items: center; justify-content: center;}
.pulse-ring { position: absolute; width: 100%; height: 100%; border-radius: 50%; border: 2px solid #6366f1; opacity: 0; animation: pulse 2s infinite cubic-bezier(0.4, 0, 0.6, 1); }
.empty-state p { margin: 0 0 4px 0; font-weight: 600; color: #111827; font-size: 1.1rem;}
.empty-state span { font-size: 0.875rem; color: #8b92a5; }

/* 全局动画 */
@keyframes spin { 100% { transform: rotate(360deg); } }
@keyframes fadeUp { 0% { opacity: 0; transform: translateY(20px); } 100% { opacity: 1; transform: translateY(0); } }
@keyframes pulse { 0% { transform: scale(0.8); opacity: 0.5; } 100% { transform: scale(1.5); opacity: 0; } }

/* 响应式调整 */
@media (max-width: 768px) {
  .leaderboard-container { padding: 20px; }
  .podium-wrapper { gap: 12px; }
  .podium-card { width: 30%; padding: 20px 8px 16px; }
}
</style>