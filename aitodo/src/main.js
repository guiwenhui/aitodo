import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import './assets/css/global.css'

// 🔧 关键：让 Axios 每次请求都携带 Cookie（JSESSIONID），否则后端 Session 无法识别用户
axios.defaults.withCredentials = true

createApp(App).use(router).mount('#app')
