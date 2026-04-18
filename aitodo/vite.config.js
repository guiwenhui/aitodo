import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path' // 引入 path 模块

export default defineConfig({
    base: '/vue/',
    plugins: [vue()],

    build: {
        // 🌟 核心修复：使用相对路径！
        // 这样无论你的项目文件夹叫什么名字、拷贝到哪台电脑，都能精准打包到当前的 Spring Boot 目录下！
        outDir: '../src/main/resources/static/vue',

        emptyOutDir: true,

        rollupOptions: {
            output: {
                entryFileNames: 'assets/index.js',
                chunkFileNames: 'assets/[name].js',
                assetFileNames: 'assets/[name].[ext]'
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