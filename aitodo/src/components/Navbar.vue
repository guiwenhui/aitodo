<template>
  <nav class="minimal-nav">
    <div class="nav-container">
      <router-link to="/" class="nav-brand">
        <i class="fas fa-robot"></i>
        <h1>AI Todo</h1>
      </router-link>

      <ul class="nav-links">
        <li>
          <router-link to="/" exact-active-class="active">
            <i class="fas fa-home"></i> 首页
          </router-link>
        </li>
        <li>
          <router-link to="/tasks" active-class="active">
            <i class="fas fa-tasks"></i> 任务管理
          </router-link>
        </li>
        <li>
          <router-link to="/focus-flow" active-class="active">
            <i class="fas fa-bell"></i> Flow心流
          </router-link>
        </li>
        <li>
          <router-link to="/leaderboard" active-class="active">
            <i class="fas fa-trophy"></i> 积分排行榜
          </router-link>
        </li>
        <li>
          <router-link to="/health" active-class="active">
            <i class="fas fa-heartbeat"></i> 健康检查
          </router-link>
        </li>

        <!-- 已登录：头像区域 -->
        <li v-if="isLoggedIn">
          <div class="pf-profile-area">
            <button class="pf-trigger" @click.stop="toggleDropdown" type="button" aria-label="用户菜单">
              <img :src="avatarUrl" alt="头像" class="pf-avatar-img" />
              <span class="pf-tooltip">{{ username }}</span>
            </button>

            <Transition name="dropdown-fade">
              <div v-if="showDropdown" class="pf-dropdown">
                <div class="pf-dropdown-header">
                  <div class="pf-dropdown-avatar">
                    <img :src="avatarUrl" alt="头像" class="pf-avatar-img" />
                  </div>
                  <div>
                    <div class="pf-dropdown-name">{{ username }}</div>
                    <div class="pf-dropdown-role">个人账户</div>
                  </div>
                </div>
                <div class="pf-dropdown-body">
                  <button class="pf-dropdown-item" @click="triggerAvatarUpload">
                    <i class="fas fa-camera"></i>
                    <span>修改头像</span>
                  </button>
                  <div class="pf-dropdown-divider"></div>
                  <button class="pf-dropdown-item pf-danger" @click="logout">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>退出登录</span>
                  </button>
                </div>
              </div>
            </Transition>
          </div>
        </li>

        <!-- 已登录：统计铃铛 -->
        <li v-if="isLoggedIn">
          <div class="stats-bell-area">
            <button class="stats-bell-btn" @click.stop="toggleStatsPanel" type="button" aria-label="数据统计">
              <i class="fas fa-bell"></i>
              <span v-if="showBellDot" class="stats-bell-dot"></span>
            </button>

            <Transition name="dropdown-fade">
              <div v-if="showStatsPanel" class="stats-dropdown">
                <div class="stats-dropdown-head">
                  <h3><i class="fas fa-chart-bar"></i> 我的数据统计</h3>
                </div>
                <div class="stats-dropdown-body">
                  <div v-if="statsLoading" class="sm-loading">
                    <i class="fas fa-spinner fa-spin"></i>
                    <p style="margin-top:10px">加载中...</p>
                  </div>
                  <div v-else>
                    <!-- 用户信息卡 -->
                    <div class="sm-user-card">
                      <div class="sm-level-badge">Lv{{ statsData.user.level || 1 }}</div>
                      <div class="sm-user-info">
                        <h4>积分: {{ statsData.user.score || 0 }}</h4>
                        <p>等级 {{ statsData.user.level || 1 }} · 继续努力！</p>
                      </div>
                    </div>

                    <!-- 完成比例 -->
                    <div class="sm-section-label">📊 完成比例</div>
                    <div class="sm-ratio-row">
                      <div class="sm-ratio-card sm-good">
                        <div class="sm-ratio-num">{{ statsData.ratio.completed || 0 }}</div>
                        <div class="sm-ratio-label">已完成</div>
                      </div>
                      <div class="sm-ratio-card sm-bad">
                        <div class="sm-ratio-num">{{ statsData.ratio.procrastinated || 0 }}</div>
                        <div class="sm-ratio-label">已拖延</div>
                      </div>
                    </div>

                    <!-- 成就 -->
                    <div v-if="statsData.achievements.length > 0">
                      <div class="sm-section-label">🏅 成就</div>
                      <div class="sm-achievements">
                        <div v-for="ach in statsData.achievements" :key="ach.name" class="sm-ach-badge">
                          <span class="sm-ach-icon">{{ ach.icon || '🌟' }}</span>
                          <div class="sm-ach-text">
                            <h5>{{ ach.name }}</h5>
                            <p>{{ ach.description }}</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </Transition>
          </div>
        </li>

        <!-- 未登录 -->
        <li v-if="!isLoggedIn">
          <router-link to="/login" class="pf-login-link">
            <i class="fas fa-sign-in-alt"></i> 登录
          </router-link>
        </li>
      </ul>
    </div>

    <!-- 隐藏的文件上传 -->
    <input
      v-if="isLoggedIn"
      ref="fileInputRef"
      type="file"
      accept="image/*"
      style="display:none"
      @change="handleAvatarUpload"
    />
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// ========== 用户状态 ==========
const isLoggedIn = ref(false)
const username = ref('User')
const avatarUrl = ref('')
const showDropdown = ref(false)
const fileInputRef = ref(null)

// ========== 统计面板 ==========
const showStatsPanel = ref(false)
const showBellDot = ref(true)
const statsLoading = ref(false)
const statsLoaded = ref(false)
const statsData = ref({
  user: { score: 0, level: 1 },
  ratio: { completed: 0, procrastinated: 0 },
  daily: { dates: [], completedTasks: [], procrastinationCounts: [] },
  achievements: []
})

// ========== 默认头像 ==========
const getDefaultAvatar = (name) => {
  const seed = encodeURIComponent(name || 'User')
  return `https://api.dicebear.com/7.x/notionists/svg?seed=${seed}&backgroundColor=transparent`
}

// ========== 初始化：检查登录状态 ==========
const checkLoginStatus = async () => {
  try {
    // 尝试获取当前用户信息（依赖 Session cookie）
    const res = await axios.get('/stats/user')
    if (res.data && res.data.code === 200 && res.data.data) {
      isLoggedIn.value = true
      username.value = localStorage.getItem('username') || 'User'
      // 如果有头像就用，没有就用 Dicebear
      const storedAvatar = localStorage.getItem('avatarUrl')
      avatarUrl.value = storedAvatar || getDefaultAvatar(username.value)
    }
  } catch {
    isLoggedIn.value = false
    avatarUrl.value = getDefaultAvatar('Guest')
  }
}

// ========== 下拉菜单 ==========
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
  showStatsPanel.value = false
}

const toggleStatsPanel = () => {
  showStatsPanel.value = !showStatsPanel.value
  showDropdown.value = false
  if (showStatsPanel.value && !statsLoaded.value) {
    loadStats()
  }
}

const closeAllPanels = () => {
  showDropdown.value = false
  showStatsPanel.value = false
}

// ========== 加载统计数据 ==========
const loadStats = async () => {
  statsLoading.value = true
  try {
    const [userRes, ratioRes, dailyRes, achRes] = await Promise.all([
      axios.get('/stats/user').catch(() => ({ data: { data: { score: 0, level: 1 } } })),
      axios.get('/stats/ratio').catch(() => ({ data: { data: { completed: 0, procrastinated: 0 } } })),
      axios.get('/stats/daily').catch(() => ({ data: { data: { dates: [], completedTasks: [], procrastinationCounts: [] } } })),
      axios.get('/stats/achievements').catch(() => ({ data: { data: [] } }))
    ])

    statsData.value = {
      user: userRes.data?.data || { score: 0, level: 1 },
      ratio: ratioRes.data?.data || { completed: 0, procrastinated: 0 },
      daily: dailyRes.data?.data || { dates: [], completedTasks: [], procrastinationCounts: [] },
      achievements: achRes.data?.data || []
    }
    statsLoaded.value = true
    showBellDot.value = false
  } catch (e) {
    console.error('加载统计数据失败:', e)
  } finally {
    statsLoading.value = false
  }
}

// ========== 头像上传 ==========
const triggerAvatarUpload = () => {
  showDropdown.value = false
  fileInputRef.value?.click()
}

const handleAvatarUpload = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { alert('请选择图片文件'); return }
  if (file.size > 5 * 1024 * 1024) { alert('图片大小不能超过 5MB'); return }

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await axios.post('/user/uploadAvatar', formData)
    if (res.data?.code === 200 && res.data.data?.avatarUrl) {
      const newUrl = res.data.data.avatarUrl + '?t=' + Date.now()
      avatarUrl.value = newUrl
      localStorage.setItem('avatarUrl', res.data.data.avatarUrl)
    } else {
      alert(res.data?.msg || '上传失败')
    }
  } catch {
    alert('网络异常，请稍后重试')
  } finally {
    if (fileInputRef.value) fileInputRef.value.value = ''
  }
}

// ========== 退出登录 ==========
const logout = () => {
  window.location.href = '/logout'
}

// ========== 全局点击关闭 ==========
onMounted(() => {
  checkLoginStatus()
  document.addEventListener('click', closeAllPanels)
})

onUnmounted(() => {
  document.removeEventListener('click', closeAllPanels)
})
</script>

<style scoped>
/* ===== 导航栏主体 ===== */
.minimal-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 1000;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  border-bottom: 1px solid #f0f0f0;
  width: 100%;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.nav-brand i {
  font-size: 1.8rem;
  color: var(--primary);
}

.nav-brand h1 {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text);
  margin: 0;
}

.nav-links {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 24px;
  align-items: center;
}

.nav-links li {
  display: flex;
  align-items: center;
}

.nav-links a {
  text-decoration: none;
  color: var(--text-light);
  font-weight: 500;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 6px;
  position: relative;
  transition: var(--transition);
}

.nav-links a:hover,
.nav-links a.active {
  color: var(--primary);
}

.nav-links a.active::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary);
  border-radius: 1px;
}

/* ===== Profile Trigger ===== */
.pf-profile-area {
  position: relative;
  display: flex;
  align-items: center;
}

.pf-trigger {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  padding: 0;
  outline: none;
  position: relative;
  flex-shrink: 0;
}

.pf-trigger:hover {
  border-color: rgba(255, 179, 102, 0.5);
  box-shadow: 0 0 0 4px rgba(255, 179, 102, 0.15);
  transform: scale(1.05);
}

.pf-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.pf-tooltip {
  position: absolute;
  bottom: -36px;
  left: 50%;
  transform: translateX(-50%) translateY(8px);
  background: rgba(30, 30, 30, 0.85);
  backdrop-filter: blur(4px);
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 8px;
  white-space: nowrap;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.2s ease, transform 0.2s ease;
  z-index: 1010;
}

.pf-trigger:hover .pf-tooltip {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
}

/* ===== Dropdown ===== */
.pf-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 260px;
  background: #fff;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.06), 0 4px 12px rgba(0, 0, 0, 0.03);
  z-index: 1050;
  overflow: hidden;
}

.pf-dropdown-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  background: #fafbfc;
}

.pf-dropdown-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pf-dropdown-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.pf-dropdown-role {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}

.pf-dropdown-body {
  padding: 8px 0;
}

.pf-dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  font-size: 13px;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  font-family: inherit;
}

.pf-dropdown-item:hover {
  background: #FFF2E5;
  color: #FFB366;
}

.pf-dropdown-item i {
  width: 18px;
  text-align: center;
  font-size: 15px;
  color: #999;
  transition: all 0.2s ease;
}

.pf-dropdown-item:hover i {
  color: #FFB366;
}

.pf-dropdown-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.04);
  margin: 6px 0;
}

.pf-dropdown-item.pf-danger {
  color: #e74c3c;
}

.pf-dropdown-item.pf-danger i {
  color: #e74c3c;
}

.pf-dropdown-item.pf-danger:hover {
  background: #fef2f2;
  color: #d32f2f;
}

.pf-login-link {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ===== Stats Bell ===== */
.stats-bell-area {
  position: relative;
  display: flex;
  align-items: center;
  margin-left: 8px;
}

.stats-bell-btn {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #f0f4ff 0%, #e8eef8 100%);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative;
  outline: none;
}

.stats-bell-btn:hover {
  background: linear-gradient(135deg, #dce6f8 0%, #c9d8f0 100%);
  transform: scale(1.1);
  box-shadow: 0 4px 16px rgba(74, 111, 165, 0.2);
}

.stats-bell-btn i {
  font-size: 16px;
  color: #4a6fa5;
}

.stats-bell-dot {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 8px;
  height: 8px;
  background: #e74c3c;
  border-radius: 50%;
  border: 2px solid #fff;
  animation: dotPulse 2s infinite;
}

@keyframes dotPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

/* ===== Stats Dropdown ===== */
.stats-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 340px;
  background: #fff;
  border-radius: 20px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.04);
  z-index: 1050;
  overflow: hidden;
}

.stats-dropdown-head {
  padding: 16px 20px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8faff 0%, #fff 100%);
}

.stats-dropdown-head h3 {
  font-size: 15px;
  font-weight: 700;
  color: #2d3748;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.stats-dropdown-head h3 i {
  color: #4a6fa5;
  font-size: 14px;
}

.stats-dropdown-body {
  padding: 14px 20px 18px;
  max-height: 420px;
  overflow-y: auto;
}

.stats-dropdown-body::-webkit-scrollbar {
  width: 4px;
}

.stats-dropdown-body::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 4px;
}

/* Stats inner cards */
.sm-user-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #4a6fa5 0%, #6b8cbc 100%);
  border-radius: 14px;
  color: #fff;
  margin-bottom: 14px;
}

.sm-level-badge {
  width: 42px;
  height: 42px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
}

.sm-user-info h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0;
}

.sm-user-info p {
  font-size: 11px;
  opacity: 0.85;
  margin: 2px 0 0;
}

.sm-section-label {
  font-size: 11px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: 14px 0 8px;
}

.sm-ratio-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 4px;
}

.sm-ratio-card {
  padding: 12px;
  border-radius: 12px;
  text-align: center;
  border: 1px solid #f0f0f0;
}

.sm-ratio-card.sm-good {
  background: #f0faf0;
}

.sm-ratio-card.sm-bad {
  background: #fff5f5;
}

.sm-ratio-num {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.2;
}

.sm-ratio-card.sm-good .sm-ratio-num {
  color: #388e3c;
}

.sm-ratio-card.sm-bad .sm-ratio-num {
  color: #d32f2f;
}

.sm-ratio-label {
  font-size: 11px;
  color: #888;
  margin-top: 4px;
}

.sm-achievements {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sm-ach-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #fafbfc;
  border: 1px solid #eee;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.sm-ach-badge:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.sm-ach-icon {
  font-size: 18px;
}

.sm-ach-text h5 {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.sm-ach-text p {
  font-size: 10px;
  color: #999;
  margin: 0;
}

.sm-loading {
  text-align: center;
  padding: 30px 0;
  color: #999;
}

/* ===== Dropdown Transition ===== */
.dropdown-fade-enter-active {
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.dropdown-fade-leave-active {
  transition: all 0.2s ease;
}

.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.96);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .minimal-nav {
    top: auto;
    bottom: 0;
    border-bottom: none;
    border-top: 1px solid #f0f0f0;
    padding-bottom: env(safe-area-inset-bottom); /* iOS 安全区域 */
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  }

  .nav-container {
    height: var(--mobile-nav-height, 65px);
    padding: 0 10px;
  }

  .nav-brand {
    display: none; /* 移动端隐藏 Logo */
  }

  .nav-links {
    width: 100%;
    justify-content: space-between;
    gap: 0;
  }

  .nav-links li {
    flex: 1;
    display: flex;
    justify-content: center;
  }

  .nav-links a {
    flex-direction: column;
    gap: 4px;
    font-size: 0.7rem;
    padding: 8px 0;
    width: 100%;
    justify-content: center;
  }

  .nav-links a i {
    font-size: 1.2rem;
    margin-bottom: 2px;
  }

  .nav-links a.active::after {
    top: 0;
    bottom: auto;
    height: 3px;
    border-radius: 0 0 3px 3px;
  }

  /* 登录和状态图标也调整为居中 */
  .pf-profile-area, .stats-bell-area {
    margin: 0;
    justify-content: center;
    width: 100%;
  }

  .pf-login-link {
    flex-direction: column;
    font-size: 0.7rem;
  }

  .pf-login-link i {
    font-size: 1.2rem;
  }

  /* 下拉菜单改为向上弹出 */
  .pf-dropdown, .stats-dropdown {
    top: auto;
    bottom: calc(100% + 15px);
    right: 10px;
    width: calc(100vw - 20px);
    max-width: 340px;
    transform-origin: bottom center;
  }

  .dropdown-fade-enter-from,
  .dropdown-fade-leave-to {
    opacity: 0;
    transform: translateY(10px) scale(0.96); /* 依然可以使用略微下移来表现收起 */
  }
}
</style>
