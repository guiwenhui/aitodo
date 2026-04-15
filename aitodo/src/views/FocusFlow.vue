<template>
  <div class="focus-flow-layout" :class="currentMode">

    <div class="ambient-breathe"></div>

    <main class="focus-container">

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

      <section class="timer-section">
        <div class="time-display" :class="{ 'is-running': isRunning }">
          <span class="digits">{{ formattedMinutes }}</span>
          <span class="colon">:</span>
          <span class="digits">{{ formattedSeconds }}</span>
        </div>
      </section>

      <section class="controls-section">
        <button class="btn-primary huge-btn" @click="toggleTimer">
          <i :class="isRunning ? 'fas fa-pause' : 'fas fa-play'"></i>
          {{ isRunning ? '暂停心流' : '进入心流' }}
        </button>

        <div class="secondary-controls">
          <button class="btn-icon" @click="resetTimer" title="重置时间">
            <i class="fas fa-undo"></i>
          </button>

          <div class="mode-switch">
            <button
                class="mode-btn"
                :class="{ active: currentMode === 'focus' }"
                @click="switchMode('focus')"
            >
              专注 (25m)
            </button>
            <button
                class="mode-btn"
                :class="{ active: currentMode === 'break' }"
                @click="switchMode('break')"
            >
              短休 (5m)
            </button>
          </div>

          <button class="btn-icon" @click="toggleWhiteNoise" :class="{ 'is-active': whiteNoiseOn }" title="白噪音 (模拟)">
            <i class="fas fa-headphones-alt"></i>
          </button>
        </div>
      </section>

    </main>

    <footer class="focus-footer">
      <p v-if="currentMode === 'focus'">“不要等状态来了才开始，开始之后状态自然会来。”</p>
      <p v-else>“休息是为了走得更远。站起来，活动一下。”</p>
    </footer>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// --- 状态定义 ---
const currentMode = ref('focus') // 'focus' | 'break'
const FOCUS_TIME = 25 * 60
const BREAK_TIME = 5 * 60

const timeLeft = ref(FOCUS_TIME)
const isRunning = ref(false)
let timerInterval = null

// 任务状态
const newTaskInput = ref('')
const activeTask = ref('')

// 白噪音模拟状态
const whiteNoiseOn = ref(false)

// --- 计算属性 ---
const formattedMinutes = computed(() => {
  return String(Math.floor(timeLeft.value / 60)).padStart(2, '0')
})
const formattedSeconds = computed(() => {
  return String(timeLeft.value % 60).padStart(2, '0')
})

// --- 交互逻辑 ---
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

const toggleTimer = () => {
  if (isRunning.value) {
    clearInterval(timerInterval)
    isRunning.value = false
  } else {
    // 强制要求输入任务才能专注
    if (!activeTask.value && currentMode.value === 'focus') {
      activeTask.value = '未命名专注时刻'
    }

    isRunning.value = true
    timerInterval = setInterval(() => {
      if (timeLeft.value > 0) {
        timeLeft.value--
      } else {
        // 倒计时结束
        clearInterval(timerInterval)
        isRunning.value = false
        console.log('Ding! Timer finished.') // 提示音预留位置

        // 自动切换模式
        if (currentMode.value === 'focus') {
          switchMode('break')
        } else {
          switchMode('focus')
        }
      }
    }, 1000)
  }
}

const resetTimer = () => {
  clearInterval(timerInterval)
  isRunning.value = false
  timeLeft.value = currentMode.value === 'focus' ? FOCUS_TIME : BREAK_TIME
}

const switchMode = (mode) => {
  if (isRunning.value) {
    if(!confirm('计时正在进行中，切换模式将重置时间，确定吗？')) return
  }
  clearInterval(timerInterval)
  isRunning.value = false
  currentMode.value = mode
  timeLeft.value = mode === 'focus' ? FOCUS_TIME : BREAK_TIME
}

const toggleWhiteNoise = () => {
  whiteNoiseOn.value = !whiteNoiseOn.value
}

// 🌟 核心生命周期：自动拦截并填入 URL 中的任务标题
onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const taskFromUrl = urlParams.get('task');

  if (taskFromUrl) {
    activeTask.value = taskFromUrl;
  }
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
})
</script>

<style scoped>
/* =======================================================
   Clean Focus Flow - 极致极简白静大厂风
   ======================================================= */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Roboto+Mono:wght@400;700&display=swap');

.focus-flow-layout {
  min-height: calc(100vh - 80px); /* 适应顶部导航栏高度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  font-family: 'Inter', -apple-system, sans-serif;
  overflow: hidden;
  transition: background-color 1s ease, color 1s ease;
}

/* 根据状态改变底层背景色 */
.focus-flow-layout.focus { background-color: #FAFAFA; color: #0F172A; }
.focus-flow-layout.break { background-color: #F0FDF4; color: #064E3B; }

/* 1. 缓慢的正念呼吸背景 */
.ambient-breathe {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 60vw; height: 60vw;
  border-radius: 50%;
  filter: blur(120px);
  z-index: 0;
  opacity: 0.3;
  pointer-events: none;
  animation: breathe 8s ease-in-out infinite alternate;
}
.focus .ambient-breathe { background: radial-gradient(circle, rgba(226, 232, 240, 0.8) 0%, rgba(250, 250, 250, 0) 70%); }
.break .ambient-breathe { background: radial-gradient(circle, rgba(167, 243, 208, 0.6) 0%, rgba(240, 253, 244, 0) 70%); }

@keyframes breathe {
  0% { transform: translate(-50%, -50%) scale(0.9); opacity: 0.2; }
  100% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.5; }
}

.focus-container {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 600px;
  padding: 40px 24px;
}

.task-focus-area { text-align: center; margin-bottom: 60px; width: 100%; }
.section-label { font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 2px; color: #94A3B8; margin-bottom: 16px; }

.clean-input {
  width: 100%; max-width: 400px;
  background: transparent; border: none; border-bottom: 2px solid #E2E8F0;
  padding: 12px 0; font-size: 1.2rem; font-weight: 500; text-align: center; color: inherit;
  transition: border-color 0.3s;
}
.clean-input:focus { outline: none; border-bottom-color: #0F172A; }
.clean-input::placeholder { color: #CBD5E1; font-weight: 400; }
.break .clean-input:focus { border-bottom-color: #059669; }

.active-task-display { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.task-title { font-size: 1.5rem; font-weight: 600; margin: 0; line-height: 1.4; color: inherit; }
.btn-text { background: transparent; border: none; font-size: 0.85rem; font-weight: 500; color: #94A3B8; cursor: pointer; transition: color 0.2s; }
.btn-text:hover { color: #64748B; }

.timer-section { margin-bottom: 80px; }
.time-display {
  font-family: 'Roboto Mono', monospace;
  font-size: 8rem; font-weight: 700; line-height: 1; letter-spacing: -6px;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.5s cubic-bezier(0.16, 1, 0.3, 1); color: inherit;
}
.time-display.is-running { transform: scale(1.05); text-shadow: 0 20px 40px rgba(0,0,0,0.05); }

.colon { margin: 0 -10px; opacity: 0.3; animation: blink 2s infinite; }
@keyframes blink { 0%, 100% { opacity: 0.3; } 50% { opacity: 0; } }

.controls-section { display: flex; flex-direction: column; align-items: center; gap: 32px; width: 100%; }

.huge-btn {
  display: flex; align-items: center; gap: 12px;
  background: #0F172A; color: #FFF; border: none; border-radius: 999px;
  padding: 20px 48px; font-size: 1.2rem; font-weight: 600; cursor: pointer;
  box-shadow: 0 10px 25px -5px rgba(15, 23, 42, 0.2); transition: all 0.3s;
}
.huge-btn:hover { transform: translateY(-4px); box-shadow: 0 20px 35px -10px rgba(15, 23, 42, 0.3); }
.break .huge-btn { background: #059669; box-shadow: 0 10px 25px -5px rgba(5, 150, 105, 0.2); }
.break .huge-btn:hover { box-shadow: 0 20px 35px -10px rgba(5, 150, 105, 0.3); }

.secondary-controls {
  display: flex; align-items: center; justify-content: center; gap: 24px;
  background: #FFFFFF; padding: 8px 16px; border-radius: 999px;
  border: 1px solid #F1F5F9; box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}
.break .secondary-controls { background: rgba(255,255,255,0.6); border-color: #D1FAE5; }

.btn-icon {
  width: 40px; height: 40px; border-radius: 50%; border: none; background: transparent; color: #94A3B8;
  font-size: 1.1rem; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center;
}
.btn-icon:hover { background: #F1F5F9; color: #0F172A; }
.btn-icon.is-active { color: #6366F1; background: #EEF2FF; }

.mode-switch { display: flex; align-items: center; background: #F8FAFC; border-radius: 999px; padding: 4px; }
.break .mode-switch { background: rgba(255,255,255,0.5); }
.mode-btn {
  border: none; background: transparent; color: #64748B; padding: 8px 16px; border-radius: 999px;
  font-size: 0.85rem; font-weight: 600; cursor: pointer; transition: all 0.2s;
}
.mode-btn.active { background: #FFFFFF; color: #0F172A; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.break .mode-btn.active { color: #064E3B; }

.focus-footer {
  position: absolute; bottom: 40px; text-align: center; width: 100%;
  font-size: 0.9rem; color: #94A3B8; font-style: italic; letter-spacing: 0.5px;
}

@media (max-width: 640px) {
  .time-display { font-size: 5rem; letter-spacing: -2px; }
  .huge-btn { padding: 16px 32px; font-size: 1.1rem; }
  .secondary-controls { flex-wrap: wrap; }
}
</style>