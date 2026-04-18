<template>
  <div class="leaderboard">
    <h2>积分排行榜</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>

      <div class="podium" v-if="leaderboard.length > 0">

        <div class="podium-item rank-2" v-if="leaderboard.length > 1">
          <div class="badge">🥈</div>
          <div class="username">{{ leaderboard[1].username }}</div>
          <div class="points">{{ leaderboard[1].points }} 积分</div>
          <div class="level">Lv.{{ leaderboard[1].level }}</div>
        </div>

        <div class="podium-item rank-1" v-if="leaderboard.length > 0">
          <div class="badge">🏆</div>
          <div class="username">{{ leaderboard[0].username }}</div>
          <div class="points">{{ leaderboard[0].points }} 积分</div>
          <div class="level">Lv.{{ leaderboard[0].level }}</div>
        </div>

        <div class="podium-item rank-3" v-if="leaderboard.length > 2">
          <div class="badge">🥉</div>
          <div class="username">{{ leaderboard[2].username }}</div>
          <div class="points">{{ leaderboard[2].points }} 积分</div>
          <div class="level">Lv.{{ leaderboard[2].level }}</div>
        </div>

      </div>

      <table v-if="leaderboard.length > 3">
        <thead>
        <tr>
          <th>排名</th>
          <th>用户名</th>
          <th>积分</th>
          <th>等级</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in leaderboard.slice(3)" :key="item.rank">
          <td>{{ item.rank }}</td>
          <td>{{ item.username }}</td>
          <td>{{ item.points }}</td>
          <td>{{ item.level }}</td>
        </tr>
        </tbody>
      </table>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const leaderboard = ref([])
const loading = ref(true)
const error = ref('')

const fetchLeaderboard = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await axios.get('/stats/leaderboard')
    // 后端返回 Result 对象，实际数据在 data 字段中
    leaderboard.value = response.data.data || []
  } catch (err) {
    error.value = '加载排行榜数据失败: ' + (err.message || '未知错误')
    console.error('Failed to fetch leaderboard:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLeaderboard()
})
</script>

<style scoped>
.leaderboard {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

/* 🌟 领奖台布局核心样式 */
.podium {
  display: flex;
  justify-content: center;
  align-items: flex-end; /* 底部对齐，让中间的最高 */
  gap: 15px;
  margin-bottom: 40px;
  margin-top: 30px;
}

.podium-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 30%;
  max-width: 150px;
  border-radius: 12px 12px 0 0; /* 底部平齐，顶部圆角 */
  padding: 15px 10px;
  box-shadow: 0 -4px 10px rgba(0,0,0,0.05);
}

/* 第一名：最高，放中间，特殊高亮 */
.rank-1 {
  height: 180px;
  background: linear-gradient(to bottom, #fffdf0, #fff);
  border: 2px solid #ffd700;
  border-bottom: none;
  transform: translateY(-20px); /* 整体向上拔高 */
  z-index: 2;
  box-shadow: 0 -8px 20px rgba(255, 215, 0, 0.2);
}

/* 第二名：左边，次高 */
.rank-2 {
  height: 140px;
  background: #f8f9fa;
  border: 1px solid #e0e0e0;
  border-bottom: none;
}

/* 第三名：右边，最低 */
.rank-3 {
  height: 120px;
  background: #fffaf5;
  border: 1px solid #f5d0b5;
  border-bottom: none;
}

/* 徽章字体大小 */
.badge { font-size: 2.5rem; margin-bottom: 10px; }
.rank-1 .badge { font-size: 3.5rem; margin-bottom: 15px; }

/* 文本样式 */
.username { font-weight: bold; margin-bottom: 5px; color: #333; }
.points { font-size: 0.9rem; color: #666; }
.level { font-size: 0.8rem; color: #999; margin-top: 5px; }

/* 🌟 剩余表格样式 */
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  text-align: center;
}

th {
  background-color: #f8f9fa;
  color: #666;
  font-weight: 600;
}

tr:hover {
  background-color: #fcfcfc;
}
</style>