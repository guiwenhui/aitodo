<template>
  <div class="ai-agent-wrapper" :class="{ 'is-focused': isFocused, 'is-loading': isLoading }">
    <div class="ai-glass-input-container">
      <!-- 流动的光影层 -->
      <div class="fluid-light-accent"></div>
      
      <!-- AI 图标 -->
      <div class="ai-icon-wrapper">
        <i class="fas fa-sparkles ai-icon" :class="{ 'fa-spin': isLoading }"></i>
      </div>

      <!-- 核心输入框 -->
      <input
        type="text"
        class="ai-input"
        v-model="userInput"
        placeholder="例如：今天八点前洗完澡..."
        maxlength="500"
        :disabled="isLoading"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @keydown.enter="sendRequest"
        ref="inputRef"
      >
      
      <!-- 快捷键提示 / 回车提示 -->
      <div class="ai-action-hint">
        <span v-if="!isFocused" class="shortcut-key">/</span>
        <span v-else-if="userInput.trim()" class="enter-key"><i class="fas fa-level-down-alt" style="transform: rotate(90deg);"></i></span>
      </div>
    </div>
    
    <!-- Toast 通知容器 -->
    <div class="ai-toast" :class="[toastType, { 'show': showToast }]">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

// 状态
const isFocused = ref(false)
const userInput = ref('')
const isLoading = ref(false)
const inputRef = ref(null)

// Toast 状态
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('info')
let toastTimeout = null

// 发送请求
const sendRequest = async () => {
  const text = userInput.value.trim()
  if (!text) return

  isLoading.value = true
  
  try {
    const res = await axios.post('/api/agent/create-tasks', { userInput: text })
    
    if (res.data.code === 200) {
      displayToast('🎉 ' + res.data.msg, 'success')
      userInput.value = ''
      inputRef.value?.blur()
      
      // 延迟刷新或跳转到任务列表
      setTimeout(() => {
        if (router.currentRoute.value.path === '/tasks') {
          window.location.reload()
        } else {
          router.push('/tasks')
        }
      }, 1500)
    } else {
      displayToast('⚠️ ' + (res.data.msg || '创建失败'), 'error')
    }
  } catch (error) {
    if (error.response?.status === 401 || error.response?.data?.code === 401) {
      displayToast('🔒 请先登录', 'error')
      setTimeout(() => router.push('/login'), 1500)
    } else {
      displayToast('❌ 网络异常，请检查网络后重试', 'error')
    }
  } finally {
    isLoading.value = false
  }
}

// 显示 Toast
const displayToast = (message, type) => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = false
  
  setTimeout(() => {
    showToast.value = true
    if (toastTimeout) clearTimeout(toastTimeout)
    toastTimeout = setTimeout(() => {
      showToast.value = false
    }, 3000)
  }, 10)
}

onUnmounted(() => {
  if (toastTimeout) clearTimeout(toastTimeout)
})
</script>

<style scoped>
/* ---------- 容器与定位 ---------- */
.ai-agent-wrapper {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

@media (max-width: 768px) {
  .ai-agent-wrapper {
    bottom: calc(var(--mobile-nav-height, 65px) + 15px);
  }
}

/* ---------- 毛玻璃输入框核心 ---------- */
.ai-glass-input-container {
  position: relative;
  display: flex;
  align-items: center;
  width: 320px;
  height: 56px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05), inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275); /* 平滑生长的回弹动画 */
}

/* 焦点时的生长动画与质感变化 */
.ai-agent-wrapper.is-focused .ai-glass-input-container {
  width: 580px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 20px 40px rgba(99, 102, 241, 0.12), inset 0 1px 0 rgba(255, 255, 255, 1);
  border-color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 640px) {
  .ai-agent-wrapper.is-focused .ai-glass-input-container {
    width: calc(100vw - 40px);
  }
}

/* ---------- 流动的光影效果 (Fluid Light Accents) ---------- */
.fluid-light-accent {
  position: absolute;
  top: -2px; left: -2px; right: -2px; bottom: -2px;
  border-radius: 30px;
  background: linear-gradient(
    60deg, 
    transparent 20%, 
    rgba(99, 102, 241, 0.4) 40%, 
    rgba(236, 72, 153, 0.4) 60%, 
    transparent 80%
  );
  background-size: 300% 300%;
  z-index: -1;
  filter: blur(8px);
  opacity: 0;
  transition: opacity 0.5s ease;
  pointer-events: none;
}

.ai-agent-wrapper.is-focused .fluid-light-accent {
  opacity: 1;
  animation: fluid-flow 4s ease infinite;
}

@keyframes fluid-flow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* ---------- 图标 ---------- */
.ai-icon-wrapper {
  margin-left: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-icon {
  font-size: 18px;
  color: #6366f1;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: transform 0.3s ease;
}

.ai-agent-wrapper.is-focused .ai-icon {
  transform: scale(1.1);
}

/* ---------- 输入框 ---------- */
.ai-input {
  flex: 1;
  height: 100%;
  background: transparent;
  border: none;
  outline: none;
  padding: 0 16px;
  font-size: 15px;
  font-weight: 500;
  color: #1e293b;
  font-family: inherit;
}

.ai-input::placeholder {
  color: #94a3b8;
  font-weight: 400;
  transition: opacity 0.3s ease;
}

.ai-agent-wrapper.is-focused .ai-input::placeholder {
  opacity: 0.6;
}

.ai-input:disabled {
  color: #94a3b8;
  cursor: not-allowed;
}

/* ---------- 提示按键 ---------- */
.ai-action-hint {
  margin-right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.shortcut-key, .enter-key {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 6px;
  font-size: 12px;
  color: #64748b;
  font-family: monospace;
  font-weight: 600;
  transition: all 0.3s ease;
}

.enter-key {
  background: #6366f1;
  color: white;
  border: none;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
  animation: pop-in 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes pop-in {
  0% { transform: scale(0.5); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

/* ---------- Toast 通知 ---------- */
.ai-toast {
  position: absolute;
  top: -60px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  pointer-events: none;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  white-space: nowrap;
}

.ai-toast.show {
  opacity: 1;
  transform: translateY(0);
}

.ai-toast.success { background: rgba(16, 185, 129, 0.9); backdrop-filter: blur(8px); }
.ai-toast.error { background: rgba(239, 68, 68, 0.9); backdrop-filter: blur(8px); }
.ai-toast.info { background: rgba(99, 102, 241, 0.9); backdrop-filter: blur(8px); }
</style>
