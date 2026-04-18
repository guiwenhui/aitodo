<template>
  <div class="leaderboard">
    <h2>积分排行榜</h2>

    <div v-if="loading" class="loading">
      <i class="fas fa-spinner fa-spin"></i> 数据加载中...
    </div>

    <div v-else-if="error" class="error">
      <i class="fas fa-exclamation-triangle"></i> {{ error }}
    </div>

    <div v-else-if="leaderboard.length === 0" class="empty-state">
      <div class="empty-icon">🏆</div>
      <p>暂无排行榜数据</p>
      <span class="empty-hint">快去完成几个任务，抢占冠军宝座吧！</span>
    </div>

    <div v-else>
      <div class="podium">
        <div v-for="(item, index) in topThree" :key="item.userId || index"
             class="podium-item" :class="'rank-' + (index + 1)">

          <div class="badge">{{ getBadge(index) }}</div>

          <div class="avatar-wrap">
            <img :src="item.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}`" class="avatar-img" />
          </div>

          <div class="username">{{ item.username }}</div>
          <div class="points">{{ getPoints(item) }} 积分</div>
          <div class="level">Lv.{{ getLevel(item) }}</div>
        </div>
      </div>

      <table v-if="restList.length > 0">
        <thead>
        <tr>
          <th>排名</th>
          <th>用户</th>
          <th>积分</th>
          <th>等级</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in restList" :key="item.userId || item.rank">
          <td>{{ item.rank }}</td>
          <td>
            <div class="table-user">
              <img :src="item.avatarUrl || `https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}`" class="table-avatar" />
              <span>{{ item.username }}</span>
            </div>
          </td>
          <td>{{ getPoints(item) }}</td>
          <td>Lv.{{ getLevel(item) }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const leaderboard = ref([])
const loading = ref(true)
const error = ref('')

// 截取前三名和剩余人员
const topThree = computed(() => leaderboard.value.slice(0, 3))
const restList = computed(() => leaderboard.value.slice(3))

// 辅助方法：获取徽章
const getBadge = (index) => {
  if (index === 0) return '🏆';
  if (index === 1) return '🥈';
  if (index === 2) return '🥉';
  return '';
}

// 辅助方法：兼容所有可能的后端命名
const getLevel = (item) => parseInt(item.level || item.currentLevel || 0, 10);
const getPoints = (item) => parseInt(item.points || item.totalPoints || 0, 10);

const fetchLeaderboard = async () => {
  try {
    loading.value = true
    error.value = ''

    const response = await axios.get('/stats/leaderboard')

    if (response.data && response.data.code === 200) {
      let rawData = response.data.data || []

      // 🌟 终极防弹排序：即使后端乱了，前端也能拨乱反正
      rawData.sort((a, b) => {
        const lvA = getLevel(a);
        const lvB = getLevel(b);
        const ptA = getPoints(a);
        const ptB = getPoints(b);

        if (lvA !== lvB) {
          return lvB - lvA; // 降序：等级大的在前面 (如 11 级在 10 级前面)
        }
        return ptB - ptA;   // 降序：积分大的在前面
      })

      // 重新分配绝对排名
      leaderboard.value = rawData.map((item, index) => ({
        ...item,
        rank: index + 1
      }))

    } else {
      error.value = '接口请求失败: ' + (response.data?.msg || '未知错误');
    }
  } catch (err) {
    error.value = '请求超时或服务未启动: ' + err.message
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLeaderboard()
})
</script>

<style scoped>
.leaderboard { max-width: 800px; margin: 0 auto; padding: 20px; text-align: center; }

/* 🌟 CSS Order 魔法：无视 DOM 顺序，强制规定视觉位置！ */
.podium { display: flex; justify-content: center; align-items: flex-end; gap: 15px; margin-bottom: 40px; margin-top: 30px; }
.podium-item { display: flex; flex-direction: column; align-items: center; justify-content: center; width: 30%; max-width: 150px; border-radius: 16px 16px 0 0; padding: 15px 10px; box-shadow: 0 -4px 10px rgba(0,0,0,0.05); }

/* 第一名：强制在视觉的 第 2 个位置 (中间) */
.rank-1 { order: 2; height: 210px; background: linear-gradient(to bottom, #FFFAF0, #fff); border: 2px solid #ffd700; border-bottom: none; transform: translateY(-20px); z-index: 2; box-shadow: 0 -8px 20px rgba(255, 215, 0, 0.2); }

/* 第二名：强制在视觉的 第 1 个位置 (左边) */
.rank-2 { order: 1; height: 160px; background: #f8f9fa; border: 1px solid #e0e0e0; border-bottom: none; }

/* 第三名：强制在视觉的 第 3 个位置 (右边) */
.rank-3 { order: 3; height: 140px; background: #fffaf5; border: 1px solid #f5d0b5; border-bottom: none; }

.badge { font-size: 2.5rem; margin-bottom: 5px; }
.rank-1 .badge { font-size: 3.5rem; margin-bottom: 5px; }
.avatar-wrap { width: 44px; height: 44px; border-radius: 50%; overflow: hidden; margin-bottom: 8px; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
.rank-1 .avatar-wrap { width: 54px; height: 54px; border-color: #ffd700; }
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.username { font-weight: bold; margin-bottom: 5px; color: #333; }
.points { font-size: 0.9rem; color: #666; }
.level { font-size: 0.8rem; color: #999; margin-top: 5px; font-weight: bold;}

/* 剩余表格及空状态样式 */
table { width: 100%; border-collapse: collapse; margin-top: 20px; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.02);}
th, td { padding: 16px; border-bottom: 1px solid #f5f5f5; text-align: center; vertical-align: middle;}
th { background-color: #f8f9fa; color: #666; font-weight: 600; }
tr:hover { background-color: #fafbfc; }
.table-user { display: flex; align-items: center; justify-content: center; gap: 10px; }
.table-avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; border: 1px solid #eee; }
.empty-state { padding: 80px 20px; background: #fff; border-radius: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); margin-top: 30px; }
.empty-icon { font-size: 4.5rem; margin-bottom: 20px; filter: grayscale(100%) opacity(0.3); }
.empty-state p { font-size: 1.2rem; font-weight: 600; color: #333; margin-bottom: 8px; }
.empty-hint { font-size: 0.95rem; color: #999; }
.error { color: #dc2626; background: #fef2f2; padding: 15px; border-radius: 12px; margin-top: 20px; }
</style>