<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="minimal-hero">
      <div class="hero-content">
        <div class="hero-brand" aria-label="AI Todo Logo">
          <span class="hero-brand-name">AI Todo</span>
        </div>
        <h1 class="hero-title">拥抱高效生活</h1>
        <p class="hero-subtitle">从任务拆解到智能提醒，帮你稳定推进每一天。</p>
        <div class="hero-actions">
          <router-link to="/tasks" class="btn-primary-large"><i class="fas fa-plus-circle"></i> 开始管理任务</router-link>
          <router-link to="/focus-flow" class="btn-secondary-large"><i class="fas fa-bell"></i> 查看 AI 提醒</router-link>
        </div>
      </div>
    </section>

    <!-- Overview -->
    <section class="minimal-overview">
      <div class="overview-card">
        <h2 class="section-title">核心能力与效率成果</h2>
        <p class="section-subtitle">一个页面看清 AI Todo 的能力和价值，帮助你以更低心智负担建立高效习惯。</p>
        <div class="overview-content">
          <div class="overview-block">
            <h3 class="overview-subtitle">核心功能</h3>
            <div class="features-grid">
              <div v-for="f in features" :key="f.title" class="feature-card">
                <div class="feature-icon"><i :class="f.icon"></i></div>
                <h3>{{ f.title }}</h3>
                <p>{{ f.desc }}</p>
              </div>
            </div>
          </div>
          <div class="overview-divider"></div>
          <div class="overview-block">
            <h3 class="overview-subtitle">效率数据</h3>
            <div class="stats-grid">
              <div v-for="s in platformStats" :key="s.label" class="stat-card">
                <i :class="s.icon"></i>
                <h3>{{ s.value }}</h3>
                <p>{{ s.label }}</p>
              </div>
            </div>
          </div>
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
.minimal-hero { min-height: auto; display: flex; align-items: center; justify-content: center; padding: 3rem 5% 2rem; background: linear-gradient(135deg, var(--bg-lighter) 0%, #fff 100%); }
.hero-content,
.overview-card { max-width: 1200px; width: 100%; padding: 3rem; border-radius: calc(var(--radius) * 1.2); background: linear-gradient(180deg, #ffffff 0%, var(--bg-light) 100%); border: 1px solid var(--border); box-shadow: var(--shadow); }
.hero-content { text-align: left; }
.hero-brand { display: inline-flex; align-items: center; margin-bottom: 1rem; }
.hero-brand-name { font-size: 1.05rem; font-weight: 700; color: #1e2a3a; letter-spacing: 0.02em; }
.hero-title { font-size: 3.2rem; font-weight: 700; line-height: 1.22; margin-bottom: 1rem; color: #1f2937; max-width: 760px; }
.hero-subtitle { font-size: 1.15rem; color: #5f6876; margin: 0 0 2.2rem; font-weight: 400; line-height: 1.75; max-width: 760px; }
.hero-actions { display: flex; gap: 1rem; justify-content: flex-start; margin-bottom: 0; }
.hero-actions .btn-primary-large,
.hero-actions .btn-secondary-large { min-width: 210px; padding: 0.95rem 1.5rem; font-size: 1rem; border-radius: 12px; }

.minimal-overview { padding: 6rem 5%; background: var(--bg); }
.overview-card { margin: 0 auto; }
.section-title { text-align: center; font-size: 2.5rem; font-weight: 700; margin-bottom: 1rem; color: var(--text); }
.section-subtitle { text-align: center; font-size: 1.15rem; color: var(--text-light); margin-bottom: 2.5rem; max-width: 760px; margin-left: auto; margin-right: auto; line-height: 1.7; }
.overview-content { display: flex; flex-direction: column; gap: 2rem; }
.overview-block { width: 100%; }
.overview-subtitle { font-size: 1.3rem; font-weight: 600; color: var(--text); margin-bottom: 1.5rem; }
.overview-divider { width: 100%; height: 1px; background: linear-gradient(to right, transparent 0%, var(--border) 20%, var(--border) 80%, transparent 100%); }
.features-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 1.25rem; }
.feature-card { background: #ffffff; border-radius: var(--radius); padding: 1.5rem; text-align: left; transition: var(--transition); border: 1px solid var(--border); }
.feature-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-hover); }
.feature-icon { width: 48px; height: 48px; background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%); border-radius: 12px; display: flex; align-items: center; justify-content: center; margin: 0 0 1rem; }
.feature-icon i { font-size: 1.2rem; color: white; }
.feature-card h3 { font-size: 1.1rem; font-weight: 600; margin-bottom: 0.6rem; color: var(--text); }
.feature-card p { color: var(--text-light); font-size: 0.95rem; line-height: 1.65; margin: 0; }
.stats-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 1.25rem; }
.stat-card { background: #ffffff; border-radius: var(--radius); padding: 1.4rem 1.2rem; border: 1px solid var(--border); text-align: left; }
.stat-card i { font-size: 1.1rem; color: var(--primary); margin-bottom: 0.8rem; }
.stat-card h3 { font-size: 1.8rem; font-weight: 700; margin-bottom: 0.3rem; color: var(--text); }
.stat-card p { font-size: 0.95rem; color: var(--text-light); margin: 0; }

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
  .home-page {
    background: #f5f7fa;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .minimal-cta,
  .minimal-footer {
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border: none;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .minimal-hero {
    min-height: auto;
    align-items: stretch;
    justify-content: flex-start;
    background: transparent;
    padding: 0;
    border-radius: 0;
    box-shadow: none;
    border: none;
  }

  .hero-content,
  .overview-card {
    text-align: left;
    background: #ffffff;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border-radius: 16px;
    padding: 20px;
  }

  .hero-brand {
    margin: 0 0 10px;
  }

  .hero-brand-name {
    font-size: 13px;
    color: #334155;
  }

  .hero-title {
    font-size: 28px;
    font-weight: 600;
    line-height: 1.28;
    margin-bottom: 10px;
    color: #111827;
    max-width: none;
  }

  .hero-subtitle {
    font-size: 14px;
    color: #6b7280;
    margin-bottom: 14px;
    line-height: 1.65;
    text-align: left;
    max-width: none;
  }

  .hero-actions {
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
    margin-bottom: 0;
  }

  .hero-actions .btn-primary-large,
  .hero-actions .btn-secondary-large {
    width: 100%;
    max-width: none;
    justify-content: center;
    min-height: 48px;
    padding: 12px 14px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 12px;
    box-shadow: none;
  }

  .section-title,
  .cta-title {
    text-align: left;
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 0;
    color: #111827;
    line-height: 1.5;
  }

  .section-subtitle,
  .cta-text {
    text-align: left;
    font-size: 14px;
    color: #6b7280;
    margin-bottom: 0;
    line-height: 1.6;
  }

  .features-grid,
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
    max-width: none;
  }

  .minimal-overview {
    padding: 0;
    background: transparent;
  }

  .overview-content {
    gap: 16px;
  }

  .overview-subtitle {
    font-size: 16px;
    margin-bottom: 10px;
  }

  .feature-card {
    background: #ffffff;
    border: none;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    padding: 16px;
    text-align: left;
  }

  .feature-icon {
    display: none;
  }

  .feature-card h3 {
    font-size: 16px;
    color: #111827;
    margin-bottom: 6px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .feature-card p {
    font-size: 13px;
    color: #6b7280;
    line-height: 1.6;
    margin-bottom: 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .stat-card {
    background: #f9fafb;
    border: none;
    border-radius: 12px;
    box-shadow: none;
    backdrop-filter: none;
    padding: 16px;
    text-align: left;
  }

  .stat-card i {
    font-size: 18px;
    margin-bottom: 8px;
    color: #6b7280;
    opacity: 1;
  }

  .stat-card h3 {
    font-size: 24px;
    margin-bottom: 4px;
    color: #111827;
    line-height: 1.4;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .stat-card p {
    font-size: 13px;
    color: #6b7280;
    opacity: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .cta-content {
    text-align: left;
  }

  .minimal-cta {
    display: none;
  }

  .footer-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    text-align: left;
  }

  .footer-info p {
    font-size: 13px;
    color: #6b7280;
    margin-bottom: 0;
    line-height: 1.6;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .footer-copyright {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #e5e7eb;
    color: #9ca3af;
    font-size: 12px;
  }
}

@media (max-width: 768px) and (hover: none) and (pointer: coarse) {
  .feature-card:hover {
    transform: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border-color: transparent;
  }

  .feature-card:active {
    background: #f9fafb;
  }
}
</style>
