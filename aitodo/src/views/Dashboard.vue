<template>
  <div class="dashboard-page">

    <div class="dashboard-header-section">
      <DashboardHeader :auto-fetch="true" />
      <button @click="showLeaderboard = !showLeaderboard" class="leaderboard-toggle-btn">
        <i class="fas" :class="showLeaderboard ? 'fa-eye-slash' : 'fa-trophy'"></i>
        {{ showLeaderboard ? '收起排行榜' : '查看排行榜' }}
      </button>
    </div>

    <transition name="fade-slide">
      <div v-if="showLeaderboard" class="leaderboard-section">
        <Leaderboard />
      </div>
    </transition>

    <div class="workspace-grid">

      <div class="task-manager-wrapper">
        <ModernTaskList />
      </div>

      <div class="stats-sidebar">
        <div class="chart-card">
          <LineChart ref="lineChartRef" />
        </div>

        <div class="chart-card">
          <PieChart ref="pieChartRef" />
        </div>

        <div class="achievement-card">
          <h3 class="sidebar-title"><i class="fas fa-medal"></i> 我的成就</h3>
          <AchievementList ref="achievementListRef" />
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DashboardHeader from '../components/DashboardHeader.vue'
import Leaderboard from '../components/Leaderboard.vue'
import LineChart from '../components/LineChart.vue'
import PieChart from '../components/PieChart.vue'
import AchievementList from '../components/AchievementList.vue'
// 🌟 引入你刚刚新建的高级任务组件
import ModernTaskList from '../components/ModernTaskList.vue'

const showLeaderboard = ref(false)

const lineChartRef = ref()
const pieChartRef = ref()
const achievementListRef = ref()
</script>

<style scoped>
.dashboard-page {
  max-width: 1400px; /* 拓宽最大宽度以适应左右分栏 */
  margin: 0 auto;
  padding: 24px;
}

/* 顶部栏样式优化 */
.dashboard-header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #ffffff;
  padding: 16px 24px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.leaderboard-toggle-btn {
  background: #0f172a;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.leaderboard-toggle-btn:hover {
  background: #1e293b;
  transform: translateY(-2px);
}

.leaderboard-section {
  margin-bottom: 24px;
}

/* 🌟 核心：工作区网格布局 */
.workspace-grid {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 1fr); /* 左 2 份，右 1 份 */
  gap: 24px;
  align-items: start;
}

.task-manager-wrapper {
  background: #ffffff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/* 右侧侧边栏卡片样式 */
.stats-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.chart-card, .achievement-card {
  background: #ffffff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f1f5f9;
}

.sidebar-title {
  font-size: 1.1rem;
  color: #1e293b;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 动画效果 */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式适配：屏幕较小时自动变为上下堆叠 */
@media (max-width: 1024px) {
  .workspace-grid {
    grid-template-columns: 1fr;
  }
}
</style>