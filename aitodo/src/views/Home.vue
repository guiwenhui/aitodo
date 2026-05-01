<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="minimal-hero">
      <div class="hero-content">
        <h1 class="hero-title">告别拖延，拥抱高效生活</h1>
        <p class="hero-subtitle">AI 智能任务管理与拖延症分析，通过智能提醒和进度可视化，<br />帮助你建立高效工作习惯，战胜拖延，提升生活质量。</p>
        <div class="hero-actions">
          <router-link to="/tasks" class="btn-primary-large"><i class="fas fa-plus-circle"></i> 开始管理任务</router-link>
          <router-link to="/focus-flow" class="btn-secondary-large"><i class="fas fa-bell"></i> 查看 AI 提醒</router-link>
        </div>
        <div class="hero-stats">
          <div v-for="stat in heroStats" :key="stat.label" class="stat-item">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="minimal-features">
      <h2 class="section-title">核心功能</h2>
      <p class="section-subtitle">专为治疗拖延症设计的智能功能，帮助您建立高效的工作与生活习惯</p>
      <div class="features-grid">
        <div v-for="f in features" :key="f.title" class="feature-card">
          <div class="feature-icon"><i :class="f.icon"></i></div>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
        </div>
      </div>
    </section>

    <!-- Stats -->
    <section class="minimal-stats">
      <h2 class="stats-title">数据驱动的高效生活</h2>
      <p class="stats-subtitle">基于真实用户数据，AI Todo 已帮助数千用户提升工作效率</p>
      <div class="stats-grid">
        <div v-for="s in platformStats" :key="s.label" class="stat-card">
          <i :class="s.icon"></i>
          <h3>{{ s.value }}</h3>
          <p>{{ s.label }}</p>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="minimal-cta">
      <div class="cta-content">
        <h2 class="cta-title">立即开始高效生活</h2>
        <p class="cta-text">无需复杂的设置，立即开始使用 AI Todo 管理您的任务。让智能提醒和拖延分析帮助您建立持久的高效习惯。</p>
        <router-link to="/tasks" class="btn-primary-large"><i class="fas fa-rocket"></i> 免费开始使用</router-link>
      </div>
    </section>

    <!-- Footer -->
    <footer class="minimal-footer">
      <div class="footer-content">
        <div class="footer-logo"><i class="fas fa-robot"></i><h2>AI Todo</h2></div>
        <div class="footer-info"><p>版本 1.0.0</p><p>用技术治愈拖延症，提升生活品质</p></div>
      </div>
      <div class="footer-copyright"><p>© 2026 AI Todo - 保留所有权利</p></div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const heroStats = ref([
  { value: '98%', label: '用户效率提升' },
  { value: '2.5x', label: '任务完成率提高' },
  { value: '24/7', label: 'AI智能监控' }
])

const features = ref([
  { icon: 'fas fa-tasks', title: '智能任务管理', desc: '创建、编辑、删除任务，设置智能提醒和截止时间，支持多状态标记（正常、紧急、拖延、完成）' },
  { icon: 'fas fa-brain', title: 'AI拖延分析', desc: '基于任务状态和完成情况，AI智能分析拖延模式，提供个性化改进建议和提醒策略' },
  { icon: 'fas fa-chart-line', title: '进度可视化', desc: '实时统计任务完成率，可视化展示拖延情况，通过图表直观了解工作效率变化趋势' },
  { icon: 'fas fa-bell', title: '多风格提醒', desc: '幽默毒舌、紧急警告、温和鼓励等多种提醒风格，根据任务状态智能切换，提高提醒效果' }
])

const platformStats = ref([
  { icon: 'fas fa-check-circle', value: '15,842', label: '已完成任务' },
  { icon: 'fas fa-clock', value: '2,317', label: '小时时间节省' },
  { icon: 'fas fa-user-check', value: '94%', label: '用户满意度' },
  { icon: 'fas fa-chart-bar', value: '3.2x', label: '平均效率提升' }
])
</script>

<style scoped>
.minimal-hero { min-height: 85vh; display: flex; align-items: center; justify-content: center; padding: 2rem 5% 4rem; background: linear-gradient(135deg, var(--bg-lighter) 0%, #fff 100%); }
.hero-content { max-width: 800px; text-align: center; }
.hero-title { font-size: 3.5rem; font-weight: 700; line-height: 1.2; margin-bottom: 1.5rem; background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.hero-subtitle { font-size: 1.3rem; color: var(--text-light); margin-bottom: 3rem; font-weight: 400; line-height: 1.6; }
.hero-actions { display: flex; gap: 1.5rem; justify-content: center; margin-bottom: 4rem; }
.hero-stats { display: flex; justify-content: center; gap: 4rem; margin-top: 3rem; flex-wrap: wrap; }
.stat-item { text-align: center; }
.stat-number { font-size: 2.5rem; font-weight: 700; color: var(--primary); margin-bottom: 0.5rem; }
.stat-label { font-size: 1rem; color: var(--text-light); font-weight: 500; }

.minimal-features { padding: 6rem 5%; background: var(--bg); }
.section-title { text-align: center; font-size: 2.5rem; font-weight: 700; margin-bottom: 1rem; color: var(--text); }
.section-subtitle { text-align: center; font-size: 1.2rem; color: var(--text-light); margin-bottom: 4rem; max-width: 600px; margin-left: auto; margin-right: auto; }
.features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 2rem; max-width: 1200px; margin: 0 auto; }
.feature-card { background: var(--bg-light); border-radius: var(--radius); padding: 2.5rem 2rem; text-align: center; transition: var(--transition); border: 1px solid var(--border); }
.feature-card:hover { transform: translateY(-10px); box-shadow: var(--shadow-hover); border-color: transparent; }
.feature-icon { width: 70px; height: 70px; background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem; }
.feature-icon i { font-size: 1.8rem; color: white; }
.feature-card h3 { font-size: 1.4rem; font-weight: 600; margin-bottom: 1rem; color: var(--text); }
.feature-card p { color: var(--text-light); font-size: 1rem; line-height: 1.6; }

.minimal-stats { padding: 6rem 5%; background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%); color: white; text-align: center; }
.stats-title { font-size: 2.5rem; font-weight: 700; margin-bottom: 1rem; }
.stats-subtitle { font-size: 1.2rem; margin-bottom: 4rem; opacity: 0.9; }
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 2rem; max-width: 900px; margin: 0 auto; }
.stat-card { background: rgba(255,255,255,0.1); backdrop-filter: blur(10px); border-radius: var(--radius); padding: 2rem; border: 1px solid rgba(255,255,255,0.2); }
.stat-card i { font-size: 2.5rem; margin-bottom: 1rem; opacity: 0.9; }
.stat-card h3 { font-size: 2.5rem; font-weight: 700; margin-bottom: 0.5rem; }
.stat-card p { font-size: 1rem; opacity: 0.9; }

.minimal-cta { padding: 6rem 5%; background: var(--bg-lighter); text-align: center; }
.cta-content { max-width: 600px; margin: 0 auto; }
.cta-title { font-size: 2.5rem; font-weight: 700; margin-bottom: 1.5rem; color: var(--text); }
.cta-text { font-size: 1.2rem; color: var(--text-light); margin-bottom: 3rem; line-height: 1.6; }

.minimal-footer { background: var(--bg); padding: 4rem 5% 2rem; border-top: 1px solid var(--border); }
.footer-content { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 2rem; }
.footer-logo { display: flex; align-items: center; gap: 12px; }
.footer-logo i { font-size: 1.8rem; color: var(--primary); }
.footer-logo h2 { font-size: 1.5rem; font-weight: 600; color: var(--text); }
.footer-info p { color: var(--text-light); font-size: 1rem; margin-bottom: 0.5rem; }
.footer-copyright { text-align: center; width: 100%; margin-top: 3rem; padding-top: 2rem; border-top: 1px solid var(--border); color: var(--text-lighter); font-size: 0.9rem; }

@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; }
  .hero-actions { flex-direction: column; align-items: center; gap: 1rem; }
  .footer-content { flex-direction: column; text-align: center; }
}
</style>
