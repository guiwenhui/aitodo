<template>
  <div class="register-page">
    <header>
      <div class="header-left"><div class="brand">AI Todo</div></div>
      <div class="header-right"><router-link to="/login" class="login-link">Sign in</router-link></div>
    </header>

    <main>
      <div class="register-card">
        <h2>创建账号</h2>
        <p class="subtitle">加入 AI Todo，开始高效管理任务</p>

        <Transition name="fade">
          <div v-if="errorMsg" class="error-message" :class="errorType">
            <i class="fas fa-exclamation-circle"></i> {{ errorMsg }}
          </div>
        </Transition>

        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <input v-model="form.username" type="text" placeholder="请输入用户名" required />
          </div>
          <div class="form-group">
            <input v-model="form.email" type="email" placeholder="请输入邮箱" required />
          </div>
          <div class="form-group">
            <input v-model="form.password" type="password" placeholder="请输入密码 (至少6位)" required minlength="6" />
          </div>
          <div class="form-group">
            <input v-model="form.confirmPassword" type="password" placeholder="确认密码" required />
          </div>
          <button type="submit" class="btn-register" :disabled="loading">
            <template v-if="loading"><i class="fas fa-spinner fa-spin"></i> 注册中...</template>
            <template v-else>注册</template>
          </button>
          <div class="footer-text">已有账号？ <router-link to="/login">立即登录</router-link></div>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })
const loading = ref(false)
const errorMsg = ref('')
const errorType = ref('error')

const handleRegister = async () => {
  if (form.password !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'; errorType.value = 'error'; return
  }
  loading.value = true; errorMsg.value = ''
  try {
    const res = await axios.post('/user/register', { username: form.username, email: form.email, password: form.password })
    if (res.data?.code === 200) {
      errorMsg.value = '注册成功！即将跳转到登录页...'; errorType.value = 'success'
      setTimeout(() => router.push('/login'), 1500)
    } else {
      errorMsg.value = res.data?.msg || '注册失败，请重试'; errorType.value = 'error'
    }
  } catch {
    errorMsg.value = '网络错误，请稍后重试'; errorType.value = 'error'
  } finally { loading.value = false }
}
</script>

<style scoped>
.register-page { min-height: 100vh; display: flex; flex-direction: column; background: #F5F0E6; font-family: 'Inter', sans-serif; }
header { padding: 24px 5%; display: flex; justify-content: space-between; align-items: center; }
.brand { font-size: 24px; font-weight: 700; }
.login-link { font-size: 14px; font-weight: 500; color: #333; }
main { flex: 1; display: flex; justify-content: center; align-items: center; padding: 40px 20px; }
.register-card { background: #fff; width: 100%; max-width: 440px; padding: 48px; border-radius: 24px; box-shadow: 0 10px 40px rgba(0,0,0,0.04); text-align: center; }
.register-card h2 { font-size: 28px; font-weight: 700; margin-bottom: 8px; }
.subtitle { font-size: 14px; color: #666; margin-bottom: 24px; }
.error-message { padding: 12px 16px; border-radius: 12px; font-size: 13px; font-weight: 500; margin-bottom: 20px; text-align: left; }
.error-message.error { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.error-message.success { background: #e8f5e9; color: #2e7d32; border: 1px solid #c8e6c9; }
.form-group { margin-bottom: 16px; }
.form-group input { width: 100%; padding: 14px 16px; border: 1px solid #E0E0E0; border-radius: 12px; font-size: 14px; outline: none; transition: all 0.3s; }
.form-group input:focus { border-color: #FFB366; }
.btn-register { width: 100%; padding: 14px; background: #FFB366; color: white; border: none; border-radius: 12px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.3s; margin-bottom: 24px; display: flex; justify-content: center; align-items: center; gap: 8px; }
.btn-register:hover { background: #FFA04D; }
.btn-register:disabled { opacity: 0.7; cursor: not-allowed; }
.footer-text { font-size: 14px; color: #666; }
.footer-text a { color: #FFB366; font-weight: 600; }
.fade-enter-active { transition: all 0.3s; }
.fade-leave-active { transition: all 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
