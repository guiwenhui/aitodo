/**
 * useShortcuts — 全局键盘快捷键 composable
 *
 * N → 跳转任务页面（新建任务）
 * / → 聚焦 AI 搜索框
 * F → 跳转专注模式
 *
 * 自动屏蔽：当焦点在 input / textarea / contenteditable 内时不触发
 */
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

export function useShortcuts() {
  const router = useRouter()

  /** 判断当前焦点是否在表单元素中 */
  function isTyping(e) {
    if (!e.target) return false
    const tag = e.target.tagName
    if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return true
    if (e.target.isContentEditable) return true
    return false
  }

  function handleKeyDown(e) {
    // 忽略修饰键组合（Ctrl / Alt / Meta）
    if (e.ctrlKey || e.altKey || e.metaKey) return
    // 忽略正在输入的场景
    if (isTyping(e)) return

    if (!e.key) return
    const key = e.key.toLowerCase()

    switch (key) {
      case 'n':
        e.preventDefault()
        // SPA 跳转到任务管理页面
        router.push('/tasks')
        break

      case '/':
        e.preventDefault()
        // 尝试聚焦页面上的 AI 助手输入框
        focusAiInput()
        break

      case 'f':
        e.preventDefault()
        // SPA 跳转到专注模式
        router.push('/focus-flow')
        break
    }
  }

  function focusAiInput() {
    // 寻找新的极简 AI 助手输入框
    const aiInput = document.querySelector('.ai-input')
    if (aiInput) {
      aiInput.focus()
    } else {
      console.log('[快捷键] 未找到 AI 输入框')
    }
  }

  onMounted(() => {
    // 使用 capture: true 在捕获阶段拦截，防止子组件 stopPropagation 导致全局快捷键失效
    window.addEventListener('keydown', handleKeyDown, { capture: true })
  })

  onUnmounted(() => {
    window.removeEventListener('keydown', handleKeyDown, { capture: true })
  })
}
