<template>
  <div class="achievement-list">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载成就数据中...</div>

    <!-- 错误状态 -->
    <div v-if="error" class="error">加载失败: {{ error }}</div>

    <!-- 成就列表 -->
    <div v-if="!loading && !error && achievements.length > 0" class="achievements">
      <div v-for="achievement in achievements" :key="achievement.id" class="achievement-item">
        <!-- 成就图标 -->
        <div class="achievement-icon">
          <img
            v-if="achievement.icon && achievement.icon.startsWith('http')"
            :src="achievement.icon"
            :alt="achievement.name"
            width="64"
            height="64"
          />
          <span v-else-if="achievement.icon" class="icon-text">{{ achievement.icon }}</span>
          <img
            v-else
          <img src='../assets/placeholder-64.png' alt="placeholder">
            width="64"
            height="64"
          />
        </div>

        <!-- 成就信息 -->
        <div class="achievement-info">
          <div class="achievement-name">{{ achievement.name }}</div>
          <div v-if="achievement.description" class="achievement-description">
            {{ achievement.description }}
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && !error && achievements.length === 0" class="empty">
      暂无成就
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 响应式数据
const achievements = ref([])
const loading = ref(false)
const error = ref(null)

// 获取成就数据
const fetchAchievements = async () => {
  loading.value = true
  error.value = null

  try {
    // 调用示例接口 /stats/achievements
    const response = await axios.get('/stats/achievements')

    // 假设后端返回的数据格式为数组
    // [
    //   { id: 1, name: '成就名称', description: '成就描述', icon: '🎯', ... },
    //   ...
    // ]
    achievements.value = response.data || []
  } catch (err) {
    console.error('获取成就数据失败:', err)
    error.value = err.message || '未知错误'
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchAchievements()
})

// 暴露刷新方法供父组件调用
defineExpose({
  refresh: fetchAchievements
})
</script>

<!-- 不生成 CSS 样式，由用户根据需要自行添加 -->
<style scoped>
/* 样式由用户自行实现 */
</style>