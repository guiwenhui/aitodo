<template>
  <div class="leaderboard">
    <h2>积分排行榜</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <table v-else>
      <thead>
        <tr>
          <th>排名</th>
          <th>用户名</th>
          <th>积分</th>
          <th>等级</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in leaderboard" :key="item.rank">
          <td>{{ item.rank }}</td>
          <td>{{ item.username }}</td>
          <td>{{ item.points }}</td>
          <td>{{ item.level }}</td>
        </tr>
      </tbody>
    </table>
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

<!-- 不生成 CSS 样式，由用户根据需要自行添加 -->
<style scoped>
/* 样式由用户自行实现 */
</style>