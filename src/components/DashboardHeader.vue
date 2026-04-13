<template>
  <div class="dashboard-header">
    <div class="score-section">
      <div class="label">积分</div>
      <div class="value">{{ score }}</div>
    </div>
    <div class="level-section">
      <div class="label">等级</div>
      <div class="value">{{ level }}</div>
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
    default: '/api/user/stats'
  }
})

// 内部状态，当 autoFetch 为 true 且未通过 props 传入时使用
const internalScore = ref(props.score)
const internalLevel = ref(props.level)

// 从后端获取数据的方法
const fetchData = async () => {
  try {
    const response = await axios.get(props.apiUrl)
    // 假设后端返回 { score: 100, level: 5 } 格式的数据
    const data = response.data
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

<!-- 不生成 CSS 样式，由用户根据需要自行添加 -->
<style scoped>
/* 样式由用户自行实现 */
</style>