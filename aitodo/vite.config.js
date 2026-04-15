import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: '/vue/',
  plugins: [vue()],

  // 🌟 自动化流配置
  build: {
    // 1. 自动把打包结果丢到 Spring Boot 静态资源目录 (请确保路径对应你的实际项目结构)
    outDir: '/Users/guiwenhui/Documents/workplace/sheji/ai-todo-procrastination-cure/src/main/resources/static/vue',
    // 每次打包前自动清空旧文件
    emptyOutDir: true,

    // 2. 🌟 核心魔法：固定文件名！去掉乱码 Hash！
    rollupOptions: {
      output: {
        entryFileNames: 'assets/index.js',   // 固定 JS 入口名字
        chunkFileNames: 'assets/[name].js',  // 固定其他分包 JS 名字
        assetFileNames: 'assets/[name].[ext]' // 固定 CSS 等静态资源名字
      }
    }
  },

  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api'),
      },
    },
  },
})