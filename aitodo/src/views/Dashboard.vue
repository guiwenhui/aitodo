<template>
  <div class="dashboard-page">
    <!-- 顶部：积分等级组件 -->
    <div class="dashboard-header-section">
      <DashboardHeader :auto-fetch="true" />
      <button @click="showLeaderboard = !showLeaderboard" class="leaderboard-toggle-btn">
        {{ showLeaderboard ? '隐藏排行榜' : '显示积分排行榜' }}
      </button>
    </div>

    <!-- 排行榜区域 -->
    <div v-if="showLeaderboard" class="leaderboard-section">
      <Leaderboard />
    </div>

    <!-- 中间：图表区域，使用 flex 或 grid 布局 -->
    <div class="charts-section">
      <div class="line-chart-wrapper">
        <LineChart />
      </div>
      <div class="pie-chart-wrapper">
        <PieChart />
      </div>
    </div>

    <!-- 底部：成就列表 -->
    <div class="achievements-section">
      <AchievementList />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DashboardHeader from '../components/DashboardHeader.vue'
import LineChart from '../components/LineChart.vue'
import PieChart from '../components/PieChart.vue'
import AchievementList from '../components/AchievementList.vue'
import Leaderboard from '../components/Leaderboard.vue'

// 控制排行榜显示状态
const showLeaderboard = ref(false)

// 可以在此处添加页面级别的状态管理或方法
// 例如：统一刷新所有组件的数据
// const headerRef = ref()
// const lineChartRef = ref()
// const pieChartRef = ref()
// const achievementListRef = ref()

// const refreshAll = () => {
//   headerRef.value?.fetchData()
//   lineChartRef.value?.refreshData()
//   pieChartRef.value?.refreshData()
//   achievementListRef.value?.refresh()
// }
</script>
<style scoped>
</style>