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
          <span v-else class="icon-text">🏅</span>
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
    achievements.value = response.data.data || response.data || []
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

<style scoped>
.achievement-list { padding: 20px 0; }
.achievements { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 16px; }
.achievement-item { display: flex; align-items: center; gap: 16px; padding: 16px; background: #fff; border-radius: 12px; border: 1px solid #E5E7EB; box-shadow: 0 1px 2px rgba(0,0,0,0.05); transition: transform 0.2s; }
.achievement-icon { width: 56px; height: 56px; display: flex; justify-content: center; align-items: center; background: #F3F4F6; border-radius: 12px; font-size: 1.5rem; flex-shrink: 0; overflow: hidden; }
.achievement-icon img { width: 100%; height: 100%; object-fit: cover; }
.achievement-name { font-weight: 600; font-size: 1rem; color: #111827; margin-bottom: 4px; }
.achievement-description { font-size: 0.85rem; color: #6B7280; line-height: 1.4; }

.loading, .error, .empty { text-align: center; padding: 40px; color: #6B7280; font-size: 0.95rem; }
.error { color: #EF4444; }

/* 📱 响应式 */
@media (max-width: 768px) {
  .achievements { grid-template-columns: 1fr; }
  .achievement-item { padding: 12px; }
  .achievement-icon { width: 48px; height: 48px; font-size: 1.2rem; }
  
  @media (hover: none) and (pointer: coarse) {
    .achievement-item:active { transform: scale(0.98); background: #F9FAFB; }
  }
}
</style>