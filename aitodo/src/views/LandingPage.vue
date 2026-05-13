<template>
  <div class="landing-page">
    <!-- ========== Transparent Navbar ========== -->
    <header class="landing-nav" :class="{ scrolled: isScrolled }">
      <div class="nav-container">
        <!-- Logo -->
        <router-link to="/landing" class="nav-logo" id="landing-logo">
          <div class="logo-icon">
            <Sparkles :size="20" />
          </div>
          <span class="logo-text">AITodo</span>
        </router-link>

        <!-- Center Nav Links (desktop) -->
        <nav class="nav-links" id="landing-nav-links">
          <a href="#features" @click.prevent="scrollToSection('features')">核心功能</a>
          <a href="#focus" @click.prevent="scrollToSection('focus')">专注心流</a>
          <a href="#leaderboard" @click.prevent="scrollToSection('leaderboard')">排行榜</a>
          <a href="#faq" @click.prevent="scrollToSection('faq')">常见问题</a>
        </nav>

        <!-- Right Buttons -->
        <div class="nav-actions">
          <template v-if="isLoggedIn">
            <router-link to="/tasks" class="nav-btn-cta" id="landing-cta-btn">
              <Zap :size="16" />
              进入工作台
            </router-link>
            <div class="landing-user-avatar" @click="$router.push('/tasks')">
              <img :src="avatarUrl" :alt="username" />
              <span class="landing-user-name">{{ username }}</span>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-btn-login" id="landing-login-btn">登录</router-link>
            <router-link to="/login" class="nav-btn-cta" id="landing-cta-btn">
              <Zap :size="16" />
              免费开始
            </router-link>
          </template>
        </div>

        <!-- Mobile Hamburger -->
        <button class="mobile-toggle" @click="mobileMenuOpen = !mobileMenuOpen" id="mobile-menu-toggle" aria-label="Toggle menu">
          <Menu v-if="!mobileMenuOpen" :size="24" />
          <X v-else :size="24" />
        </button>
      </div>

      <!-- Mobile Menu -->
      <transition name="slide-down">
        <div v-if="mobileMenuOpen" class="mobile-menu">
          <a href="#features" @click.prevent="scrollToSection('features'); mobileMenuOpen = false">核心功能</a>
          <a href="#focus" @click.prevent="scrollToSection('focus'); mobileMenuOpen = false">专注心流</a>
          <a href="#leaderboard" @click.prevent="scrollToSection('leaderboard'); mobileMenuOpen = false">排行榜</a>
          <a href="#faq" @click.prevent="scrollToSection('faq'); mobileMenuOpen = false">常见问题</a>
          <div class="mobile-menu-actions">
            <template v-if="isLoggedIn">
              <router-link to="/tasks" class="nav-btn-cta" style="flex:1;justify-content:center"><Zap :size="16" />进入工作台</router-link>
            </template>
            <template v-else>
              <router-link to="/login" class="nav-btn-login">登录</router-link>
              <router-link to="/login" class="nav-btn-cta"><Zap :size="16" />免费开始</router-link>
            </template>
          </div>
        </div>
      </transition>
    </header>

    <!-- ========== Hero Section ========== -->
    <section class="hero-section" id="hero">
      <!-- Animated background particles -->
      <div class="hero-bg">
        <div class="hero-glow hero-glow-1"></div>
        <div class="hero-glow hero-glow-2"></div>
        <div class="hero-glow hero-glow-3"></div>
        <div class="hero-grid"></div>
      </div>

      <div class="hero-container">
        <div class="hero-content-left">
          <div class="hero-badge" v-observe-visibility>
            <Sparkles :size="14" />
            <span>AI 驱动的下一代效率工具</span>
          </div>
          <h1 class="hero-title" v-observe-visibility>
            告别拖延，用 AI<br/>
            <span class="highlight-text">重塑你的生产力</span>
          </h1>
          <p class="hero-subtitle" v-observe-visibility>
            不仅仅是待办清单。AITodo 结合 AI Agent 与游戏化机制，通过自然语言对话管理任务、<br class="hide-mobile" />
            沉浸式心流体验与多维数据追踪，彻底治愈你的拖延症。
          </p>
          <div class="hero-buttons" v-observe-visibility>
            <router-link to="/login" class="btn-hero-primary" id="hero-primary-btn">
              立即开启体验 >|
            </router-link>
            <a href="#features" @click.prevent="scrollToSection('features')" class="btn-hero-secondary" id="hero-secondary-btn">
              了解工作原理 <span class="green-dot"></span>
            </a>
          </div>
          
          <div class="hero-trusted" v-observe-visibility>
            <span class="trusted-label">Trusted by:</span>
            <div class="trusted-logos">
              <span class="t-logo">Google</span>
              <span class="t-logo">Notion</span>
              <span class="t-logo">Shopify</span>
              <span class="t-logo">HubSpot</span>
            </div>
          </div>
        </div>

        <div class="hero-content-right">
          <!-- Dashboard Mockup -->
          <div class="hero-mockup" v-observe-visibility>
            <div class="mockup-window">
              <div class="mockup-toolbar">
                <div class="mockup-dots">
                  <span class="dot red"></span>
                  <span class="dot yellow"></span>
                  <span class="dot green"></span>
                </div>
                <div class="mockup-url">
                  <Lock :size="12" />
                  <span>app.aitodo.com/dashboard</span>
                </div>
                <div class="mockup-dots" style="visibility:hidden">
                  <span class="dot"></span>
                </div>
              </div>
              <div class="mockup-body">
                <!-- Sidebar -->
                <div class="mock-sidebar">
                  <div class="mock-logo-row"><div class="mock-avatar"></div><div class="mock-line w60"></div></div>
                  <div class="mock-nav-item active"><ListTodo :size="14" /><span>任务</span></div>
                  <div class="mock-nav-item"><BarChart3 :size="14" /><span>统计</span></div>
                  <div class="mock-nav-item"><Timer :size="14" /><span>心流</span></div>
                  <div class="mock-nav-item"><Trophy :size="14" /><span>排行</span></div>
                </div>
                <!-- Main content -->
                <div class="mock-main">
                  <div class="mock-header-row">
                    <div class="mock-line w40 bold"></div>
                    <div class="mock-badge-row">
                      <div class="mock-stat-badge"><span class="badge-num">12</span><span class="badge-label">待办</span></div>
                      <div class="mock-stat-badge done"><span class="badge-num">48</span><span class="badge-label">已完成</span></div>
                      <div class="mock-stat-badge streak"><span class="badge-num">7🔥</span><span class="badge-label">连续天</span></div>
                    </div>
                  </div>
                  <div class="mock-tasks">
                    <div class="mock-task" v-for="(t, i) in mockTasks" :key="i">
                      <div class="mock-check" :class="{ checked: t.done }">
                        <Check v-if="t.done" :size="10" />
                      </div>
                      <div class="mock-task-text">
                        <div class="mock-line" :class="{ 'line-through': t.done }" style="width: 100%">{{ t.text }}</div>
                      </div>
                      <div class="mock-priority" :class="t.priority">{{ t.tag }}</div>
                    </div>
                  </div>
                  <!-- Mini chart area -->
                  <div class="mock-chart-row">
                    <div class="mock-chart">
                      <div class="chart-title">本周完成趋势</div>
                      <div class="mini-bars">
                        <div class="bar" v-for="(h, i) in [40,65,50,80,70,90,60]" :key="i" :style="{ height: h + '%' }"></div>
                      </div>
                    </div>
                    <div class="mock-chart">
                      <div class="chart-title">时间分布</div>
                      <div class="mini-pie">
                        <div class="pie-circle"></div>
                        <div class="pie-legend">
                          <div class="pie-item"><span class="pie-dot c1"></span>工作 45%</div>
                          <div class="pie-item"><span class="pie-dot c2"></span>学习 30%</div>
                          <div class="pie-item"><span class="pie-dot c3"></span>生活 25%</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== Features Section ========== -->
    <section class="features-section" id="features">
      <div class="section-container">
        <div class="section-header" v-observe-visibility>
          <div class="section-badge">
            <Layers :size="14" />
            <span>核心功能</span>
          </div>
          <h2 class="section-title">为你的效率而生的<span class="gradient-text">超能力</span></h2>
          <p class="section-desc">四大核心模块，覆盖从任务规划到习惯养成的完整闭环。</p>
        </div>
        <div class="features-grid">
          <div class="feature-card" v-for="(f, i) in features" :key="i" v-observe-visibility :style="{ '--delay': i * 0.1 + 's' }">
            <div class="feature-icon-wrap" :style="{ background: f.gradient }">
              <span class="feature-emoji">{{ f.emoji }}</span>
            </div>
            <h3>{{ f.title }}</h3>
            <p>{{ f.desc }}</p>
            <div class="feature-glow" :style="{ background: f.glowColor }"></div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== Focus Flow Section ========== -->
    <section class="focus-section" id="focus">
      <div class="section-container">
        <div class="focus-layout" v-observe-visibility>
          <div class="focus-text">
            <div class="section-badge">
              <Timer :size="14" />
              <span>专注心流</span>
            </div>
            <h2 class="section-title left-align">进入<span class="gradient-text">心流状态</span>，<br/>让深度工作成为习惯</h2>
            <p class="section-desc left-align">科学的番茄钟计时与沉浸式白噪音结合，屏蔽一切干扰。当你完成一次深度专注，AI 会记录并分析你的心流数据，帮助你发现最佳工作节奏。</p>
            <div class="focus-stats">
              <div class="focus-stat">
                <span class="stat-value">2.5x</span>
                <span class="stat-label">效率提升</span>
              </div>
              <div class="focus-divider"></div>
              <div class="focus-stat">
                <span class="stat-value">94%</span>
                <span class="stat-label">用户满意度</span>
              </div>
              <div class="focus-divider"></div>
              <div class="focus-stat">
                <span class="stat-value">45min</span>
                <span class="stat-label">最佳专注时长</span>
              </div>
            </div>
          </div>
          <div class="focus-visual">
            <div class="focus-timer-card">
              <div class="timer-ring">
                <svg viewBox="0 0 120 120">
                  <circle cx="60" cy="60" r="54" class="ring-bg" />
                  <circle cx="60" cy="60" r="54" class="ring-progress" />
                </svg>
                <div class="timer-display">
                  <span class="timer-time">25:00</span>
                  <span class="timer-label">专注中</span>
                </div>
              </div>
              <div class="timer-controls">
                <div class="timer-btn-sm"><SkipBack :size="16" /></div>
                <div class="timer-btn-play"><Pause :size="20" /></div>
                <div class="timer-btn-sm"><SkipForward :size="16" /></div>
              </div>
              <div class="noise-bar">
                <Volume2 :size="14" />
                <span>下雨声 · 森林</span>
                <div class="noise-wave">
                  <span v-for="i in 5" :key="i" class="wave-bar" :style="{ animationDelay: i * 0.15 + 's' }"></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== Leaderboard Section ========== -->
    <section class="leaderboard-section" id="leaderboard">
      <div class="section-container">
        <div class="section-header" v-observe-visibility>
          <div class="section-badge">
            <Trophy :size="14" />
            <span>排行榜</span>
          </div>
          <h2 class="section-title">与全网达人一起<span class="gradient-text">抗击拖延</span></h2>
          <p class="section-desc">积分、徽章与排名，用正向激励驱动你的每一次进步。</p>
        </div>
        <div class="lb-cards" v-observe-visibility>
          <div class="lb-card" v-for="(u, i) in leaderboardUsers" :key="i">
            <div class="lb-rank" :class="'rank-' + (i + 1)">{{ i + 1 }}</div>
            <div class="lb-avatar" :style="{ background: u.color }">{{ u.initial }}</div>
            <div class="lb-info">
              <div class="lb-name">{{ u.name }}</div>
              <div class="lb-badges">
                <span v-for="b in u.badges" :key="b">{{ b }}</span>
              </div>
            </div>
            <div class="lb-score">
              <Flame :size="14" />
              {{ u.score }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== FAQ Section ========== -->
    <section class="faq-section" id="faq">
      <div class="section-container">
        <div class="section-header" v-observe-visibility>
          <div class="section-badge">
            <HelpCircle :size="14" />
            <span>常见问题</span>
          </div>
          <h2 class="section-title">你可能想知道的</h2>
        </div>
        <div class="faq-list" v-observe-visibility>
          <div class="faq-item" v-for="(q, i) in faqs" :key="i" @click="toggleFaq(i)">
            <div class="faq-question">
              <span>{{ q.question }}</span>
              <ChevronDown :size="18" :class="{ rotated: q.open }" />
            </div>
            <transition name="faq-expand">
              <div v-if="q.open" class="faq-answer">{{ q.answer }}</div>
            </transition>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== Bottom CTA ========== -->
    <section class="bottom-cta" v-observe-visibility>
      <div class="cta-bg">
        <div class="cta-glow cta-glow-1"></div>
        <div class="cta-glow cta-glow-2"></div>
      </div>
      <div class="section-container cta-inner">
        <h2>准备好找回被浪费的时间了吗？</h2>
        <p>加入数千名已经告别拖延的用户，开启你的高效人生。</p>
        <router-link to="/login" class="btn-hero-primary" id="bottom-cta-btn">
          <Rocket :size="18" />
          立即免费开始
          <div class="btn-shine"></div>
        </router-link>
      </div>
    </section>

    <!-- ========== Footer ========== -->
    <footer class="landing-footer">
      <div class="footer-container">
        <div class="footer-top">
          <div class="footer-brand">
            <div class="nav-logo">
              <div class="logo-icon"><Sparkles :size="18" /></div>
              <span class="logo-text">AITodo</span>
            </div>
            <p class="footer-brand-desc">用 AI 技术治愈拖延症，重塑你的生产力。</p>
          </div>
          <div class="footer-links-group">
            <div class="footer-col">
              <h4>产品</h4>
              <a href="#features" @click.prevent="scrollToSection('features')">核心功能</a>
              <a href="#focus" @click.prevent="scrollToSection('focus')">专注心流</a>
              <a href="#leaderboard" @click.prevent="scrollToSection('leaderboard')">排行榜</a>
            </div>
            <div class="footer-col">
              <h4>支持</h4>
              <a href="#faq" @click.prevent="scrollToSection('faq')">常见问题</a>
              <a href="#">使用文档</a>
              <a href="#">联系我们</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 AITodo. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import axios from 'axios'
import {
  Sparkles, Zap, Menu, X, Rocket, Play, Lock, Check, Layers,
  Timer, Trophy, Flame, HelpCircle, ChevronDown, BarChart3,
  ListTodo, Pause, SkipBack, SkipForward, Volume2
} from 'lucide-vue-next'

/* ===== Scroll state ===== */
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

const scrollToSection = (id) => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

/* ===== IntersectionObserver directive ===== */
const vObserveVisibility = {
  mounted(el) {
    el.classList.add('fade-hidden')
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          const delay = getComputedStyle(el).getPropertyValue('--delay') || '0s'
          el.style.transitionDelay = delay
          el.classList.add('fade-visible')
          observer.unobserve(el)
        }
      },
      { threshold: 0.1 }
    )
    observer.observe(el)
  }
}

/* ===== Data ===== */
const mockTasks = [
  { text: '完成产品 PRD 文档撰写', done: false, priority: 'urgent', tag: '紧急' },
  { text: '准备周五团队分享 PPT', done: false, priority: 'high', tag: '重要' },
  { text: '回复客户需求邮件', done: true, priority: 'normal', tag: '日常' },
  { text: '阅读《深度工作》第三章', done: true, priority: 'low', tag: '成长' },
]

const features = reactive([
  {
    emoji: '🧠',
    title: 'AI Agent 智能调度',
    desc: '无需繁琐点击，一句话自然语言即可创建、拆解和规划复杂任务。',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    glowColor: 'rgba(102, 126, 234, 0.15)',
  },
  {
    emoji: '⏱️',
    title: '专注心流模式',
    desc: '科学的计时器与白噪音结合，屏蔽干扰，帮你快速进入深度工作状态。',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    glowColor: 'rgba(240, 147, 251, 0.15)',
  },
  {
    emoji: '📊',
    title: '游戏化数据追踪',
    desc: '多维度的饼图与折线图分析你的时间花销，见证你的每一次进步。',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    glowColor: 'rgba(79, 172, 254, 0.15)',
  },
  {
    emoji: '🏆',
    title: '达人排行榜',
    desc: '与全网用户共同抗击拖延，通过积分与成就徽章获取正向反馈。',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    glowColor: 'rgba(250, 112, 154, 0.15)',
  },
])

const leaderboardUsers = [
  { name: '效率达人 Max', initial: 'M', color: 'linear-gradient(135deg, #667eea, #764ba2)', score: '9,280', badges: ['🔥', '⚡', '👑'] },
  { name: '专注高手 Luna', initial: 'L', color: 'linear-gradient(135deg, #f093fb, #f5576c)', score: '8,750', badges: ['🔥', '🎯'] },
  { name: '时间管理者 Leo', initial: 'L', color: 'linear-gradient(135deg, #4facfe, #00f2fe)', score: '7,930', badges: ['⚡', '📊'] },
  { name: '习惯养成者 Mia', initial: 'M', color: 'linear-gradient(135deg, #fa709a, #fee140)', score: '6,540', badges: ['🎯'] },
]

const faqs = reactive([
  { question: 'AITodo 是免费的吗？', answer: '是的！AITodo 提供功能完整的免费版本，包含 AI 任务管理、专注心流、排行榜等核心功能。我们也提供 Pro 版本解锁更多高级分析功能。', open: false },
  { question: 'AI Agent 是如何工作的？', answer: 'AITodo 的 AI Agent 基于先进的自然语言处理技术，你只需用日常语言描述任务，AI 就能自动创建、分类、设置优先级，甚至将复杂任务拆解为可执行的子步骤。', open: false },
  { question: '我的数据安全吗？', answer: '绝对安全。所有数据通过 SSL 加密传输，存储在安全的云服务器上。我们严格遵守隐私保护政策，绝不会将你的个人数据用于任何第三方用途。', open: false },
  { question: '支持哪些平台？', answer: 'AITodo 目前支持 Web 端（桌面及移动浏览器），未来将推出 iOS 和 Android 原生应用，以及 Chrome 浏览器扩展。', open: false },
])

const toggleFaq = (i) => { faqs[i].open = !faqs[i].open }

/* ===== 用户登录状态（复用 Navbar 的逻辑） ===== */
const isLoggedIn = ref(false)
const username = ref('User')
const avatarUrl = ref('')

const getDefaultAvatar = (name) => {
  const seed = encodeURIComponent(name || 'User')
  return `https://api.dicebear.com/7.x/notionists/svg?seed=${seed}&backgroundColor=transparent`
}

const checkLoginStatus = async () => {
  try {
    const res = await axios.get('/stats/user')
    if (res.data && res.data.code === 200 && res.data.data) {
      isLoggedIn.value = true
      username.value = localStorage.getItem('username') || 'User'
      const storedAvatar = localStorage.getItem('avatarUrl')
      avatarUrl.value = storedAvatar || getDefaultAvatar(username.value)
    }
  } catch {
    isLoggedIn.value = false
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  checkLoginStatus()
})
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
@import '../assets/css/landing.css';
@import '../assets/css/landing-sections.css';

/* ===== 已登录用户头像（Landing Navbar） ===== */
.landing-user-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 100px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(0, 0, 0, 0.02);
  transition: all 0.25s;
}
.landing-user-avatar:hover {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.15);
}
.landing-user-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}
.landing-user-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1a1a1a;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
