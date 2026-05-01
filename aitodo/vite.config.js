import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    base: '/',
    plugins: [vue()],

    build: {
        // 打包输出到 Spring Boot 的静态资源目录
        outDir: '../src/main/resources/static',
        emptyOutDir: false,
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
            // 统一代理所有后端 API 路径到 Spring Boot
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '/task': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '/stats': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '/user': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '/uploads': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            '/logout': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
        },
    },
})