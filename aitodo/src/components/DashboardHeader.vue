<template>
  <div class="user-stats-card">
    <div class="stat-item score">
      <div class="stat-icon"><i class="fas fa-star"></i></div>
      <div class="stat-content">
        <div class="label">我的积分</div>
        <div class="value">{{ internalScore }}</div>
      </div>
    </div>
    <div class="stat-divider"></div>
    <div class="stat-item level">
      <div class="stat-icon"><i class="fas fa-crown"></i></div>
      <div class="stat-content">
        <div class="label">当前等级</div>
        <div class="value">Lv.{{ internalLevel }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  // 通过 props 传入积分和等级
  score: {
    type: [Number, String],
    default: 0
  },
  level: {
    type: [Number, String],
    default: 1
  },
  // 是否自动从后端获取数据（如果提供了 score 和 level props，则优先使用 props）
  autoFetch: {
    type: Boolean,
    default: false
  },
  // 后端 API 地址
  apiUrl: {
    type: String,
    default: '/stats/user'
  }
})

// 内部状态，当 autoFetch 为 true 且未通过 props 传入时使用
const internalScore = ref(props.score)
const internalLevel = ref(props.level)

// 从后端获取数据的方法
const fetchData = async () => {
  try {
    const response = await axios.get(props.apiUrl)
    // 后端返回 Result 对象，实际数据在 data 字段中
    const data = response.data.data || response.data
    internalScore.value = data.score || 0
    internalLevel.value = data.level || 1
  } catch (error) {
    console.error('Failed to fetch score and level:', error)
    // 保持默认值
  }
}

// 如果启用了自动获取且没有通过 props 传入值，则在组件挂载时获取数据
onMounted(() => {
  if (props.autoFetch && (props.score === 0 || props.level === 1)) {
    fetchData()
  }
})

// 暴露 fetchData 方法供父组件调用
defineExpose({ fetchData })
</script>

<style scoped>
.user-stats-card {
  display: flex;
  align-items: center;
  background: white;
  padding: 0.75rem 1.5rem;
  border-radius: 9999px; /* Pill shape */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  gap: 1.5rem;
  transition: all 0.3s ease;
}

.user-stats-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.stat-divider {
  width: 1px;
  height: 2rem;
  background: #e2e8f0;
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
}

.score .stat-icon {
  background: #fffbeb;
  color: #f59e0b;
}

.level .stat-icon {
  background: #eff6ff;
  color: #3b82f6;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 0.75rem;
  color: #64748b;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.value {
  font-size: 1.1rem;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.2;
}
</style>