<template>
  <div class="focus-flow-layout" :class="[currentMode, { 'immersive-lock': isRunning }]">

    <div class="ambient-breathe"></div>

    <!-- 沉浸锁定遮罩提示 -->
    <div v-if="isRunning" class="lock-badge">
      <i class="fas fa-lock"></i> 沉浸模式 · 专注中
    </div>

    <main class="focus-container">

      <!-- 任务选择区 -->
      <header class="task-focus-area">
        <p class="section-label">当前专注目标</p>
        <div class="task-selector" v-if="!isRunning && !activeTask">
          <input
              type="text"
              v-model="newTaskInput"
              placeholder="写下你接下来要专注的一件事..."
              @keyup.enter="setTask"
              class="clean-input"
          />
        </div>
        <div class="active-task-display" v-else>
          <h2 class="task-title">{{ activeTask || '未命名任务' }}</h2>
          <button v-if="!isRunning" class="btn-text" @click="clearTask">重新选择</button>
        </div>
      </header>

      <!-- 计时器 -->
      <section class="timer-section">
        <div class="time-display" :class="{ 'is-running': isRunning }">
          <span class="digits">{{ formattedMinutes }}</span>
          <span class="colon">:</span>
          <span class="digits">{{ formattedSeconds }}</span>
        </div>
        <div class="timer-progress-bar">
          <div class="timer-progress-fill" :style="{ width: progressPercent + '%' }"></div>
        </div>
      </section>

      <!-- 控制按钮 -->
      <section class="controls-section">
        <div class="main-controls">
          <button class="btn-primary huge-btn" @click="toggleTimer">
            <i :class="isRunning ? 'fas fa-pause' : 'fas fa-play'"></i>
            {{ isRunning ? '暂停心流' : '进入心流' }}
          </button>
          <button v-if="isRunning" class="btn-abandon" @click="abandonTimer">
            <i class="fas fa-times"></i> 放弃
          </button>
        </div>

        <div class="secondary-controls" v-if="!isRunning">
          <button class="btn-icon" @click="resetTimer" title="重置时间">
            <i class="fas fa-undo"></i>
          </button>
          <div class="mode-switch">
            <button class="mode-btn" :class="{ active: currentMode === 'focus' }" @click="switchMode('focus')">
              专注 (25m)
            </button>
            <button class="mode-btn" :class="{ active: currentMode === 'break' }" @click="switchMode('break')">
              短休 (5m)
            </button>
          </div>
        </div>
      </section>

      <!-- 🌟 白噪音播放器 -->
      <section class="noise-player" v-if="!isRunning || whiteNoiseActive">
        <p class="section-label" style="margin-bottom: 16px;">🎧 白噪音</p>
        <div class="noise-options">
          <button
              v-for="opt in noiseOptions"
              :key="opt.id"
              class="noise-btn"
              :class="{ active: currentNoise === opt.id }"
              @click="selectNoise(opt.id)"
          >
            <span class="noise-emoji">{{ opt.icon }}</span>
            <span class="noise-name">{{ opt.name }}</span>
          </button>
        </div>
        <div class="noise-controls" v-if="currentNoise">
          <button class="noise-play-btn" @click="toggleNoise">
            <i :class="whiteNoiseActive ? 'fas fa-pause' : 'fas fa-play'"></i>
          </button>
          <div class="volume-slider-wrap">
            <i class="fas fa-volume-down vol-icon"></i>
            <input type="range" min="0" max="100" v-model.number="noiseVolume" @input="updateVolume" class="volume-slider" />
            <i class="fas fa-volume-up vol-icon"></i>
          </div>
        </div>
      </section>

    </main>

    <footer class="focus-footer">
      <p v-if="currentMode === 'focus'">"不要等状态来了才开始，开始之后状态自然会来。"</p>
      <p v-else>"休息是为了走得更远。站起来，活动一下。"</p>
    </footer>

    <!-- 积分恭喜弹窗 -->
    <Transition name="toast-fade">
      <div v-if="showPointsToast" class="points-toast">
        <div class="points-toast-inner">
          <div class="points-icon">🎉</div>
          <h3>恭喜！专注完成</h3>
          <p class="points-value">+{{ earnedPoints }} 积分</p>
          <p class="points-desc">你坚持了 {{ focusDurationMin }} 分钟，继续保持！</p>
          <button class="points-close-btn" @click="showPointsToast = false">太棒了</button>
        </div>
      </div>
    </Transition>

    <!-- 隐藏的 audio 元素 -->
    <audio ref="audioRef" loop></audio>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

// --- 模式 ---
const currentMode = ref('focus')
const FOCUS_TIME = 25 * 60
const BREAK_TIME = 5 * 60

const timeLeft = ref(FOCUS_TIME)
const totalTime = ref(FOCUS_TIME)
const isRunning = ref(false)
let timerInterval = null

// 任务
const newTaskInput = ref('')
const activeTask = ref('')

// 白噪音
const currentNoise = ref(null)
const whiteNoiseActive = ref(false)
const noiseVolume = ref(60)
const audioRef = ref(null)
const noiseOptions = [
  { id: 'rain', name: '雨声', icon: '🌧️', src: '/audio/rain.mp3' },
  { id: 'cafe', name: '咖啡馆', icon: '☕', src: '/audio/cafe.mp3' },
]

// 积分
const showPointsToast = ref(false)
const earnedPoints = ref(0)
const focusDurationMin = ref(0)

// --- 计算属性 ---
const formattedMinutes = computed(() => String(Math.floor(timeLeft.value / 60)).padStart(2, '0'))
const formattedSeconds = computed(() => String(timeLeft.value % 60).padStart(2, '0'))
const progressPercent = computed(() => {
  if (totalTime.value === 0) return 0
  return ((totalTime.value - timeLeft.value) / totalTime.value) * 100
})

// --- 任务 ---
const setTask = () => {
  if (newTaskInput.value.trim()) {
    activeTask.value = newTaskInput.value.trim()
    newTaskInput.value = ''
  }
}
const clearTask = () => {
  activeTask.value = ''
  if (isRunning.value) toggleTimer()
}

// --- 计时器 ---
const toggleTimer = () => {
  if (isRunning.value) {
    clearInterval(timerInterval)
    isRunning.value = false
    unlockUI()
  } else {
    if (!activeTask.value && currentMode.value === 'focus') {
      activeTask.value = '未命名专注时刻'
    }
    isRunning.value = true
    lockUI()
    timerInterval = setInterval(() => {
      if (timeLeft.value > 0) {
        timeLeft.value--
      } else {
        clearInterval(timerInterval)
        isRunning.value = false
        onTimerComplete()
      }
    }, 1000)
  }
}

const resetTimer = () => {
  clearInterval(timerInterval)
  isRunning.value = false
  timeLeft.value = currentMode.value === 'focus' ? FOCUS_TIME : BREAK_TIME
  totalTime.value = timeLeft.value
  unlockUI()
}

const abandonTimer = () => {
  clearInterval(timerInterval)
  isRunning.value = false
  timeLeft.value = currentMode.value === 'focus' ? FOCUS_TIME : BREAK_TIME
  totalTime.value = timeLeft.value
  unlockUI()
  console.log('[番茄钟] 用户放弃本次专注，不发放积分')
}

const switchMode = (mode) => {
  if (isRunning.value) {
    if (!confirm('计时正在进行中，切换模式将重置时间，确定吗？')) return
    unlockUI()
  }
  clearInterval(timerInterval)
  isRunning.value = false
  currentMode.value = mode
  timeLeft.value = mode === 'focus' ? FOCUS_TIME : BREAK_TIME
  totalTime.value = timeLeft.value
}

// --- 完成结算 ---
async function onTimerComplete() {
  unlockUI()

  if (currentMode.value === 'focus') {
    // 积分结算：1分钟 = 1分
    const minutes = Math.round(totalTime.value / 60)
    earnedPoints.value = minutes
    focusDurationMin.value = minutes
    showPointsToast.value = true

    await grantUserPoints(minutes)

    // 自动切换到休息
    switchMode('break')
  } else {
    switchMode('focus')
  }
}

// 模拟积分 API
async function grantUserPoints(points) {
  console.log(`[积分结算] 发放 ${points} 积分`)
  // TODO: 替换为真实 API
  // await axios.post('/stats/points', { points })
  return new Promise(resolve => setTimeout(resolve, 300))
}

// --- 沉浸式锁定 ---
function lockUI() {
  const nav = document.querySelector('.minimal-nav')
  if (nav) nav.style.display = 'none'
}
function unlockUI() {
  const nav = document.querySelector('.minimal-nav')
  if (nav) nav.style.display = ''
}

// --- 白噪音 ---
const selectNoise = (id) => {
  if (currentNoise.value === id) return
  currentNoise.value = id
  const opt = noiseOptions.find(o => o.id === id)
  if (audioRef.value && opt) {
    audioRef.value.src = opt.src
    audioRef.value.volume = noiseVolume.value / 100
    if (whiteNoiseActive.value) {
      audioRef.value.play().catch(() => {})
    }
  }
}

const toggleNoise = () => {
  if (!audioRef.value || !currentNoise.value) return
  if (whiteNoiseActive.value) {
    audioRef.value.pause()
    whiteNoiseActive.value = false
  } else {
    audioRef.value.play().catch(() => {})
    whiteNoiseActive.value = true
  }
}

const updateVolume = () => {
  if (audioRef.value) audioRef.value.volume = noiseVolume.value / 100
}

// --- 生命周期 ---
onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const taskFromUrl = urlParams.get('task')
  if (taskFromUrl) activeTask.value = taskFromUrl
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
  unlockUI()
  if (audioRef.value) audioRef.value.pause()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Roboto+Mono:wght@400;700&display=swap');

.focus-flow-layout {
  min-height: calc(100vh - 80px);
  display: flex; flex-direction: column; justify-content: center; align-items: center;
  position: relative; font-family: 'Inter', -apple-system, sans-serif;
  overflow: hidden; transition: background-color 1s ease, color 1s ease;
}
.focus-flow-layout.focus { background-color: #FAFAFA; color: #0F172A; }
.focus-flow-layout.break { background-color: #F0FDF4; color: #064E3B; }

/* 沉浸锁定时全屏 */
.focus-flow-layout.immersive-lock { min-height: 100vh; }

.lock-badge {
  position: fixed; top: 16px; left: 50%; transform: translateX(-50%);
  background: rgba(15,23,42,0.85); color: #fff; padding: 8px 20px;
  border-radius: 999px; font-size: 0.8rem; font-weight: 600;
  z-index: 9999; backdrop-filter: blur(8px);
  display: flex; align-items: center; gap: 8px;
  animation: fadeIn 0.5s ease;
}

.ambient-breathe {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 60vw; height: 60vw; border-radius: 50%;
  filter: blur(120px); z-index: 0; opacity: 0.3;
  pointer-events: none; animation: breathe 8s ease-in-out infinite alternate;
}
.focus .ambient-breathe { background: radial-gradient(circle, rgba(226,232,240,0.8) 0%, rgba(250,250,250,0) 70%); }
.break .ambient-breathe { background: radial-gradient(circle, rgba(167,243,208,0.6) 0%, rgba(240,253,244,0) 70%); }

@keyframes breathe {
  0% { transform: translate(-50%, -50%) scale(0.9); opacity: 0.2; }
  100% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.5; }
}
@keyframes fadeIn { from { opacity: 0; transform: translateX(-50%) translateY(-10px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }

.focus-container {
  position: relative; z-index: 10;
  display: flex; flex-direction: column; align-items: center;
  width: 100%; max-width: 600px; padding: 40px 24px;
}

.task-focus-area { text-align: center; margin-bottom: 48px; width: 100%; }
.section-label { font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 2px; color: #94A3B8; margin-bottom: 16px; }

.clean-input {
  width: 100%; max-width: 400px;
  background: transparent; border: none; border-bottom: 2px solid #E2E8F0;
  padding: 12px 0; font-size: 1.2rem; font-weight: 500; text-align: center; color: inherit;
  transition: border-color 0.3s;
}
.clean-input:focus { outline: none; border-bottom-color: #0F172A; }
.clean-input::placeholder { color: #CBD5E1; font-weight: 400; }

.active-task-display { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.task-title { font-size: 1.5rem; font-weight: 600; margin: 0; line-height: 1.4; color: inherit; }
.btn-text { background: transparent; border: none; font-size: 0.85rem; font-weight: 500; color: #94A3B8; cursor: pointer; }
.btn-text:hover { color: #64748B; }

/* Timer */
.timer-section { margin-bottom: 48px; text-align: center; }
.time-display {
  font-family: 'Roboto Mono', monospace;
  font-size: 7rem; font-weight: 700; line-height: 1; letter-spacing: -6px;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.5s cubic-bezier(0.16, 1, 0.3, 1); color: inherit;
}
.time-display.is-running { transform: scale(1.05); text-shadow: 0 20px 40px rgba(0,0,0,0.05); }
.colon { margin: 0 -10px; opacity: 0.3; animation: blink 2s infinite; }
@keyframes blink { 0%, 100% { opacity: 0.3; } 50% { opacity: 0; } }

.timer-progress-bar {
  width: 200px; height: 4px; background: #E2E8F0; border-radius: 4px;
  margin: 20px auto 0; overflow: hidden;
}
.timer-progress-fill {
  height: 100%; background: linear-gradient(90deg, #4F46E5, #7C3AED);
  border-radius: 4px; transition: width 1s linear;
}
.break .timer-progress-fill { background: linear-gradient(90deg, #059669, #10B981); }

/* Controls */
.controls-section { display: flex; flex-direction: column; align-items: center; gap: 24px; width: 100%; }
.main-controls { display: flex; align-items: center; gap: 16px; }

.huge-btn {
  display: flex; align-items: center; gap: 12px;
  background: #0F172A; color: #FFF; border: none; border-radius: 999px;
  padding: 20px 48px; font-size: 1.2rem; font-weight: 600; cursor: pointer;
  box-shadow: 0 10px 25px -5px rgba(15,23,42,0.2); transition: all 0.3s;
}
.huge-btn:hover { transform: translateY(-4px); box-shadow: 0 20px 35px -10px rgba(15,23,42,0.3); }
.break .huge-btn { background: #059669; box-shadow: 0 10px 25px -5px rgba(5,150,105,0.2); }

.btn-abandon {
  display: flex; align-items: center; gap: 8px;
  background: transparent; border: 2px solid #FCA5A5; color: #DC2626;
  border-radius: 999px; padding: 14px 28px; font-size: 1rem; font-weight: 600;
  cursor: pointer; transition: all 0.2s;
}
.btn-abandon:hover { background: #FEF2F2; border-color: #DC2626; }

.secondary-controls {
  display: flex; align-items: center; justify-content: center; gap: 24px;
  background: #FFFFFF; padding: 8px 16px; border-radius: 999px;
  border: 1px solid #F1F5F9; box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}
.btn-icon {
  width: 40px; height: 40px; border-radius: 50%; border: none; background: transparent; color: #94A3B8;
  font-size: 1.1rem; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center;
}
.btn-icon:hover { background: #F1F5F9; color: #0F172A; }

.mode-switch { display: flex; align-items: center; background: #F8FAFC; border-radius: 999px; padding: 4px; }
.mode-btn {
  border: none; background: transparent; color: #64748B; padding: 8px 16px; border-radius: 999px;
  font-size: 0.85rem; font-weight: 600; cursor: pointer; transition: all 0.2s;
}
.mode-btn.active { background: #FFFFFF; color: #0F172A; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }

/* 🎧 白噪音 */
.noise-player {
  margin-top: 32px; text-align: center; width: 100%;
  padding: 24px; background: rgba(255,255,255,0.6);
  border-radius: 20px; border: 1px solid #F1F5F9;
}

.noise-options { display: flex; justify-content: center; gap: 12px; margin-bottom: 16px; }
.noise-btn {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 14px 20px; border: 2px solid #E2E8F0; border-radius: 14px;
  background: #fff; cursor: pointer; transition: all 0.2s; min-width: 90px;
}
.noise-btn:hover { border-color: #94A3B8; }
.noise-btn.active { border-color: #4F46E5; background: #EEF2FF; }
.noise-emoji { font-size: 1.6rem; }
.noise-name { font-size: 0.8rem; font-weight: 600; color: #6B7280; }
.noise-btn.active .noise-name { color: #4F46E5; }

.noise-controls {
  display: flex; align-items: center; justify-content: center; gap: 16px;
}
.noise-play-btn {
  width: 40px; height: 40px; border-radius: 50%;
  border: none; background: #4F46E5; color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  font-size: 0.9rem; transition: all 0.2s;
}
.noise-play-btn:hover { background: #4338CA; transform: scale(1.05); }

.volume-slider-wrap {
  display: flex; align-items: center; gap: 8px;
}
.vol-icon { font-size: 0.8rem; color: #94A3B8; }
.volume-slider {
  -webkit-appearance: none; appearance: none;
  width: 120px; height: 4px; border-radius: 4px;
  background: #E2E8F0; outline: none;
}
.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none; appearance: none;
  width: 16px; height: 16px; border-radius: 50%;
  background: #4F46E5; cursor: pointer;
}

/* 积分弹窗 */
.points-toast {
  position: fixed; inset: 0; z-index: 10000;
  background: rgba(15,23,42,0.5); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
}
.points-toast-inner {
  background: #fff; border-radius: 24px; padding: 40px 48px;
  text-align: center; box-shadow: 0 25px 60px rgba(0,0,0,0.15);
  max-width: 360px; width: 90%;
}
.points-icon { font-size: 3rem; margin-bottom: 16px; }
.points-toast-inner h3 { font-size: 1.4rem; font-weight: 700; color: #111827; margin: 0 0 8px; }
.points-value {
  font-size: 2.5rem; font-weight: 800; color: #4F46E5;
  margin: 12px 0;
  background: linear-gradient(135deg, #4F46E5, #7C3AED);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
}
.points-desc { font-size: 0.95rem; color: #6B7280; margin-bottom: 24px; }
.points-close-btn {
  padding: 12px 32px; background: #111827; color: #fff;
  border: none; border-radius: 999px; font-size: 1rem; font-weight: 600;
  cursor: pointer; transition: all 0.2s;
}
.points-close-btn:hover { background: #000; transform: translateY(-2px); }

/* Transition */
.toast-fade-enter-active { transition: all 0.4s ease; }
.toast-fade-leave-active { transition: all 0.3s ease; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; }
.toast-fade-enter-from .points-toast-inner { transform: scale(0.9) translateY(20px); }

/* Footer */
.focus-footer {
  position: absolute; bottom: 40px; text-align: center; width: 100%;
  font-size: 0.9rem; color: #94A3B8; font-style: italic; letter-spacing: 0.5px;
}

@media (max-width: 640px) {
  .time-display { font-size: 5rem; letter-spacing: -2px; }
  .huge-btn { padding: 16px 32px; font-size: 1.1rem; }
  .main-controls { flex-direction: column; }
  .noise-options { flex-wrap: wrap; }
}
</style>