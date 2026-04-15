<template>
  <div class="flow-layout">
    <div class="ambient-background"></div>

    <main class="editorial-container">

      <header class="hero-header">
        <div class="hero-content">
          <p class="greeting">保持专注，击破拖延。</p>
          <h1 class="master-title">卷王战绩</h1>
        </div>

        <div class="my-status-inline">
          <div class="status-item">
            <span class="label">当前修炼</span>
            <span class="value">Lv.{{ userStats.level }}</span>
          </div>
          <div class="status-sep"></div>
          <div class="status-item">
            <span class="label">累计积分</span>
            <span class="value accent">{{ userStats.score }} <span class="pts">PTS</span></span>
          </div>
        </div>
      </header>

      <section class="trend-section">
        <p class="section-micro-title">全站效率涌动趋势</p>
        <div ref="lineChartRef" class="fluid-chart"></div>
      </section>

      <section v-if="topThree.length > 0" class="podium-section">
        <div class="podium-grid">

          <div class="podium-item rank-2" v-if="topThree[1]">
            <div class="avatar-wrapper">
              <img :src="`https://api.dicebear.com/7.x/notionists/svg?seed=${topThree[1].username}&backgroundColor=transparent`" alt="">
              <div class="rank-badge">2</div>
            </div>
            <h3 class="hero-name">{{ topThree[1].username }}</h3>
            <p class="hero-score">{{ topThree[1].points }} PTS</p>
          </div>

          <div class="podium-item rank-1" v-if="topThree[0]">
            <div class="crown-icon"><i class="fas fa-crown"></i></div>
            <div class="avatar-wrapper glow">
              <img :src="`https://api.dicebear.com/7.x/notionists/svg?seed=${topThree[0].username}&backgroundColor=transparent`" alt="">
            </div>
            <h3 class="hero-name">{{ topThree[0].username }}</h3>
            <p class="hero-score">{{ topThree[0].points }} PTS</p>
            <span class="level-tag">Lv.{{ topThree[0].level }}</span>
          </div>

          <div class="podium-item rank-3" v-if="topThree[2]">
            <div class="avatar-wrapper">
              <img :src="`https://api.dicebear.com/7.x/notionists/svg?seed=${topThree[2].username}&backgroundColor=transparent`" alt="">
              <div class="rank-badge">3</div>
            </div>
            <h3 class="hero-name">{{ topThree[2].username }}</h3>
            <p class="hero-score">{{ topThree[2].points }} PTS</p>
          </div>

        </div>
      </section>

      <section class="followers-section" v-if="restOfList.length > 0">
        <div class="list-container">
          <div
              v-for="item in restOfList"
              :key="item.username"
              class="list-row"
          >
            <div class="row-rank">{{ String(item.rank).padStart(2, '0') }}</div>
            <div class="row-user">
              <img class="mini-avatar" :src="`https://api.dicebear.com/7.x/notionists/svg?seed=${item.username}&backgroundColor=transparent`" alt="">
              <span class="mini-name">{{ item.username }}</span>
            </div>
            <div class="row-level">Lv.{{ item.level }}</div>
            <div class="row-score">{{ item.points }} <span class="pts">PTS</span></div>
          </div>
        </div>
      </section>

      <div v-if="loading" class="editorial-loading">
        <div class="pulse-circle"></div>
        <p>正在同步宇宙专注力...</p>
      </div>

    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const userStats = ref({ score: 0, level: 1 })
const leaderboard = ref([])
const loading = ref(true)
const lineChartRef = ref(null)
let lineChart = null

// 核心：分离前三名和其他人，前三名重新排序为 [亚军, 冠军, 季军]
const topThree = computed(() => {
  if (leaderboard.value.length < 3) return leaderboard.value;
  return [leaderboard.value[1], leaderboard.value[0], leaderboard.value[2]];
})
const restOfList = computed(() => leaderboard.value.slice(3))

const fetchData = async () => {
  loading.value = true
  try {
    const [userRes, boardRes, lineRes] = await Promise.all([
      axios.get('/stats/user').catch(() => ({ data: { data: { score: 0, level: 1 } } })),
      axios.get('/stats/leaderboard').catch(() => ({ data: { data: [] } })),
      axios.get('/stats/daily').catch(() => ({ data: { data: { dates: [], completedTasks: [], procrastinationCounts: [] } } }))
    ])

    userStats.value = userRes.data.data || userRes.data
    leaderboard.value = boardRes.data.data || boardRes.data || []

    // 渲染无边界图表
    renderFluidChart(lineRes.data.data || lineRes.data)
  } catch (error) {
    console.error('Data sync failed:', error)
  } finally {
    loading.value = false
  }
}

// 🎨 将 Echarts 极致简化，变成一条装饰性的背景数据流
const renderFluidChart = (lineData) => {
  if (!lineChartRef.value) return;
  lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    tooltip: { show: false }, // 关闭一切干扰
    grid: { left: '-5%', right: '-5%', bottom: '0%', top: '20%', containLabel: false },
    xAxis: { type: 'category', show: false, data: lineData.dates || [] },
    yAxis: { type: 'value', show: false },
    series: [
      {
        type: 'line', smooth: 0.6, showSymbol: false,
        lineStyle: { width: 3, color: '#111827' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{offset: 0, color: 'rgba(17, 24, 39, 0.08)'}, {offset: 1, color: 'rgba(17, 24, 39, 0)'}]) },
        data: lineData.completedTasks || []
      }
    ]
  })
}

const handleResize = () => lineChart?.resize()
onMounted(() => { fetchData(); window.addEventListener('resize', handleResize) })
onUnmounted(() => { window.removeEventListener('resize', handleResize); lineChart?.dispose() })
</script>

<style scoped>
/* =======================================================
   Flow: 极致去边界化 (Editorial / Apple Design)
   ======================================================= */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Playfair+Display:ital,wght@0,600;0,700;1,600&display=swap');

.flow-layout {
  min-height: 100vh;
  position: relative;
  background-color: #FAFAFC;
  font-family: 'Inter', -apple-system, sans-serif;
  overflow-x: hidden;
  padding-bottom: 120px;
}

/* 极其柔和的底部光晕，替代生硬的渐变 */
.ambient-background {
  position: fixed; top: -20vh; left: 10vw; width: 80vw; height: 60vh;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.06) 0%, rgba(255,255,255,0) 70%);
  z-index: 0; pointer-events: none;
}

.editorial-container { position: relative; z-index: 10; max-width: 1000px; margin: 0 auto; padding: 60px 24px; }

/* 1. 杂志感头部排版 */
.hero-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 60px; }
.greeting { font-size: 0.95rem; font-weight: 600; color: #64748B; text-transform: uppercase; letter-spacing: 2px; margin: 0 0 12px 0; }
.master-title {
  font-family: 'Playfair Display', serif; /* 使用高级衬线体提升荣誉感 */
  font-size: 4rem; font-weight: 700; color: #0F172A; margin: 0; line-height: 1.1; letter-spacing: -1.5px;
}

.my-status-inline { display: flex; align-items: center; gap: 24px; background: rgba(255,255,255,0.6); backdrop-filter: blur(10px); padding: 16px 32px; border-radius: 999px; border: 1px solid rgba(0,0,0,0.03); box-shadow: 0 20px 40px -20px rgba(0,0,0,0.05); }
.status-item { display: flex; flex-direction: column; align-items: center; }
.status-item .label { font-size: 0.75rem; color: #94A3B8; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 2px; }
.status-item .value { font-size: 1.5rem; font-weight: 700; color: #0F172A; }
.status-item .value.accent { color: #6366F1; }
.status-sep { width: 1px; height: 30px; background: rgba(0,0,0,0.08); }
.pts { font-size: 0.85rem; font-weight: 600; opacity: 0.7; }

/* 2. 效率波谱图：融入背景的数据线 */
.trend-section { margin-bottom: 60px; position: relative; }
.section-micro-title { font-size: 0.75rem; font-weight: 700; color: #CBD5E1; text-transform: uppercase; letter-spacing: 3px; text-align: center; margin-bottom: -20px; position: relative; z-index: 2; }
.fluid-chart { width: 100%; height: 160px; pointer-events: none; }

/* 3. 领奖台：视觉核心重塑 */
.podium-section { margin-bottom: 80px; }
.podium-grid { display: flex; justify-content: center; align-items: flex-end; gap: 40px; }

.podium-item { display: flex; flex-direction: column; align-items: center; position: relative; transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.podium-item:hover { transform: translateY(-10px); }

/* 头像与发光 */
.avatar-wrapper { position: relative; border-radius: 50%; padding: 4px; background: #FFFFFF; box-shadow: 0 10px 30px -10px rgba(0,0,0,0.1); margin-bottom: 20px; }
.avatar-wrapper img { width: 100%; height: 100%; border-radius: 50%; background: #F8FAFC; }

.rank-badge { position: absolute; bottom: -5px; right: -5px; width: 28px; height: 28px; background: #1E293B; color: #FFF; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.8rem; border: 2px solid #FFF; }

.hero-name { font-size: 1.1rem; font-weight: 700; color: #0F172A; margin: 0 0 4px; }
.hero-score { font-size: 0.9rem; font-weight: 600; color: #64748B; margin: 0; }

/* 冠军特殊放大 */
.rank-1 { z-index: 10; margin: 0 20px; }
.rank-1 .avatar-wrapper { width: 140px; height: 140px; }
.rank-1 .avatar-wrapper.glow { box-shadow: 0 0 0 4px rgba(255,255,255,0.8), 0 20px 50px -10px rgba(99, 102, 241, 0.4); }
.rank-1 .hero-name { font-size: 1.6rem; letter-spacing: -0.5px; margin-top: 8px; }
.rank-1 .hero-score { font-size: 1.1rem; color: #6366F1; font-weight: 700; }
.level-tag { margin-top: 8px; background: #1E293B; color: #FFF; font-size: 0.75rem; font-weight: 700; padding: 4px 12px; border-radius: 999px; }
.crown-icon { position: absolute; top: -35px; color: #F59E0B; font-size: 2rem; filter: drop-shadow(0 4px 10px rgba(245, 158, 11, 0.4)); animation: float 3s ease-in-out infinite; }

@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }

/* 亚军季军大小 */
.rank-2 .avatar-wrapper, .rank-3 .avatar-wrapper { width: 100px; height: 100px; }

/* 4. 无边框行列表：极简主义的典范 */
.list-container { display: flex; flex-direction: column; gap: 8px; }
.list-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 32px; border-radius: 20px;
  background: transparent;
  border-bottom: 1px solid rgba(0,0,0,0.03); /* 极弱的分隔线 */
  transition: all 0.2s ease;
}
/* 鼠标悬停时，底层微微泛白，没有边框的生硬感 */
.list-row:hover { background: rgba(255,255,255,0.8); box-shadow: 0 10px 30px -15px rgba(0,0,0,0.05); border-color: transparent; transform: scale(1.02); z-index: 5; position: relative; }

.row-rank { width: 40px; font-family: 'Playfair Display', serif; font-size: 1.4rem; font-weight: 700; color: #CBD5E1; font-style: italic; }
.row-user { flex: 1; display: flex; align-items: center; gap: 16px; }
.mini-avatar { width: 40px; height: 40px; border-radius: 50%; background: #F1F5F9; }
.mini-name { font-size: 1.1rem; font-weight: 600; color: #334155; }
.row-level { width: 100px; text-align: center; font-size: 0.85rem; font-weight: 600; color: #94A3B8; }
.row-score { width: 120px; text-align: right; font-size: 1.2rem; font-weight: 700; color: #0F172A; }

.editorial-loading { text-align: center; padding: 100px 0; color: #94A3B8; font-weight: 500; letter-spacing: 1px; }
.pulse-circle { width: 40px; height: 40px; border-radius: 50%; background: rgba(99, 102, 241, 0.1); margin: 0 auto 20px; animation: pulse 1.5s ease-out infinite; }
@keyframes pulse { 0% { transform: scale(0.5); opacity: 1; } 100% { transform: scale(2); opacity: 0; } }

/* 📱 响应式 */
@media (max-width: 900px) {
  .hero-header { flex-direction: column; align-items: center; text-align: center; gap: 32px; }
  .master-title { font-size: 3rem; }
  .podium-grid { align-items: center; flex-direction: column; gap: 60px; }
  .rank-1 { order: -1; } /* 手机上冠军排第一 */
  .list-row { padding: 16px; flex-wrap: wrap; }
  .row-level { display: none; }
}
</style>