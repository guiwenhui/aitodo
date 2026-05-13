<template>
  <div class="login-page">
    <header>
      <div class="header-left">
        <div class="brand">AI Todo</div>
      </div>
      <div class="header-right">
        <router-link to="/register" class="signup-link">Sign up</router-link>
      </div>
    </header>

    <main>
      <div class="login-card">
        <h2>Agent Login</h2>
        <p class="subtitle"></p>

        <Transition name="fade">
          <div v-if="errorMsg" class="error-message" :class="errorType">
            <i class="fas fa-exclamation-circle"></i> {{ errorMsg }}
          </div>
        </Transition>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <div class="input-wrapper">
              <input v-model="username" type="text" placeholder="请输入用户名或邮箱" required />
              <div class="input-icon"><i class="fa-regular fa-circle-user"></i></div>
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper">
              <input v-model="password" :type="showPassword ? 'text' : 'password'" placeholder="请输入密码" required />
              <div class="input-icon toggle-password" @click="showPassword = !showPassword">{{ showPassword ? 'Show' : 'Hide' }}</div>
            </div>
          </div>
          <a href="#" class="trouble-link">登录遇到问题？</a>
          <button type="submit" class="btn-signin" :disabled="loading">
            <template v-if="loading"><i class="fas fa-spinner fa-spin"></i> loading...</template>
            <template v-else>登录</template>
          </button>
          <div class="divider"><span>或使用以下方式登录</span></div>
          <div class="social-group">
            <button type="button" class="btn-social"><img src="https://www.gstatic.com/images/branding/product/2x/googleg_48dp.png" alt="Google" /></button>
            <button type="button" class="btn-social"><i class="fa-brands fa-apple" style="font-size:22px"></i></button>
            <button type="button" class="btn-social"><i class="fa-brands fa-facebook" style="font-size:22px;color:#1877F2"></i></button>
          </div>
          <div class="footer-text">还没有账号？ <router-link to="/register">立即注册</router-link></div>
        </form>
      </div>
    </main>

    <footer class="page-footer">
      <div class="copyright">版权所有 @wework 2026</div>
      <div class="footer-links">
        <router-link to="/">返回首页</router-link>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const errorType = ref('error')

const handleLogin = async () => {
  if (!username.value.trim() || !password.value.trim()) {
    errorMsg.value = '请输入用户名和密码'; errorType.value = 'error'; return
  }
  loading.value = true; errorMsg.value = ''
  try {
    const res = await axios.post('/user/login', { username: username.value, password: password.value })
    if (res.data?.code === 200) {
      errorMsg.value = '登录成功!'; errorType.value = 'success'
      localStorage.setItem('userId', res.data.data.userId)
      localStorage.setItem('username', res.data.data.username)
      if (res.data.data.avatarUrl) {
        localStorage.setItem('avatarUrl', res.data.data.avatarUrl)
      }
      setTimeout(() => router.push('/tasks'), 1000)
    } else {
      errorMsg.value = res.data?.msg || '用户名或密码无效'; errorType.value = 'error'
    }
  } catch {
    errorMsg.value = '网络错误，请稍后重试'; errorType.value = 'error'
  } finally { loading.value = false }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; flex-direction: column; background-color: #F5F0E6; font-family: 'Inter', sans-serif; }
header { padding: 24px 5%; display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; gap: 24px; }
.brand { font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }
.signup-link { font-size: 14px; font-weight: 500; color: #333; text-decoration: none; }
main { flex: 1; display: flex; justify-content: center; align-items: center; padding: 40px 20px; }
.login-card { background: #fff; width: 100%; max-width: 440px; padding: 48px; border-radius: 24px; box-shadow: 0 10px 40px rgba(0,0,0,0.04); text-align: center; }
.login-card h2 { font-size: 28px; font-weight: 700; margin-bottom: 8px; }
.subtitle { font-size: 14px; color: #666; margin-bottom: 24px; }
.error-message { padding: 12px 16px; border-radius: 12px; font-size: 13px; font-weight: 500; margin-bottom: 20px; text-align: left; }
.error-message.error { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.error-message.success { background: #e8f5e9; color: #2e7d32; border: 1px solid #c8e6c9; }
.form-group { margin-bottom: 20px; position: relative; text-align: left; }
.input-wrapper { position: relative; display: flex; align-items: center; }
.form-group input { width: 100%; padding: 14px 16px; border: 1px solid #E0E0E0; border-radius: 12px; font-size: 14px; outline: none; transition: all 0.3s; font-family: inherit; }
.form-group input:focus { border-color: #FFB366; }
.input-icon { position: absolute; right: 16px; color: #ccc; cursor: pointer; }
.toggle-password { font-size: 12px; font-weight: 600; color: #333; user-select: none; }
.trouble-link { display: block; text-align: left; font-size: 13px; color: #666; text-decoration: none; margin-bottom: 24px; }
.trouble-link:hover { color: #FFB366; }
.btn-signin { width: 100%; padding: 14px; background: #FFB366; color: white; border: none; border-radius: 12px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.3s; margin-bottom: 32px; display: flex; justify-content: center; align-items: center; gap: 8px; }
.btn-signin:hover { background: #FFA04D; }
.btn-signin:disabled { opacity: 0.7; cursor: not-allowed; }
.divider { display: flex; align-items: center; margin-bottom: 24px; color: #666; font-size: 13px; }
.divider::before, .divider::after { content: ""; flex: 1; height: 1px; background: #E0E0E0; }
.divider span { padding: 0 12px; color: #999; }
.social-group { display: flex; gap: 12px; margin-bottom: 32px; }
.btn-social { flex: 1; display: flex; justify-content: center; align-items: center; height: 48px; border: 1px solid #E0E0E0; border-radius: 12px; background: white; cursor: pointer; transition: all 0.3s; }
.btn-social:hover { background: #f9f9f9; border-color: #ccc; }
.btn-social img { width: 20px; height: 20px; }
.footer-text { font-size: 14px; color: #666; }
.footer-text a { color: #FFB366; font-weight: 600; }
.page-footer { padding: 24px 5%; display: flex; justify-content: space-between; font-size: 12px; color: #666; }
.footer-links a { color: #666; margin-left: 24px; transition: all 0.3s; }
.footer-links a:hover { color: #FFB366; }
.fade-enter-active { transition: all 0.3s ease; }
.fade-leave-active { transition: all 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-5px); }
@media (max-width: 600px) { .login-card { padding: 32px 24px; } }
</style>
