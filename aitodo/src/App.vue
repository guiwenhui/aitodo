<template>
  <div>
    <!-- 简约导航栏 -->
    <nav class="minimal-nav" style="position: fixed; top: 0; left: 0; right: 0; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); z-index: 1000; box-shadow: 0 2px 20px rgba(0, 0, 0, 0.05); padding: 1.2rem 5%;">
        <div class="nav-container" style="max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center;">
            <a href="/" class="nav-brand" style="display: flex; align-items: center; gap: 12px; text-decoration: none;">
                <i class="fas fa-robot" style="font-size: 1.8rem; color: #4a6fa5;"></i>
                <h1 style="font-size: 1.5rem; font-weight: 600; color: #333; margin: 0;">AI Todo + 拖延症治疗器</h1>
            </a>
            <ul class="nav-links" style="display: flex; gap: 2rem; list-style: none; margin: 0; padding: 0;">
                <li><a href="/" style="text-decoration: none; color: #666; font-weight: 500; font-size: 1rem; padding: 0.5rem 0; position: relative; transition: all 0.3s ease;"><i class="fas fa-home"></i> 首页</a></li>
                <li><a href="/tasks" style="text-decoration: none; color: #666; font-weight: 500; font-size: 1rem; padding: 0.5rem 0; position: relative; transition: all 0.3s ease;"><i class="fas fa-tasks"></i> 任务管理</a></li>
                <li><a href="/ai-warnings" style="text-decoration: none; color: #666; font-weight: 500; font-size: 1rem; padding: 0.5rem 0; position: relative; transition: all 0.3s ease;"><i class="fas fa-bell"></i> AI提醒</a></li>
                <li><a href="/leaderboard" style="text-decoration: none; color: #4a6fa5; font-weight: 500; font-size: 1rem; padding: 0.5rem 0; position: relative; transition: all 0.3s ease;"><i class="fas fa-trophy"></i> 积分排行榜</a></li>
                <li><a href="/health" style="text-decoration: none; color: #666; font-weight: 500; font-size: 1rem; padding: 0.5rem 0; position: relative; transition: all 0.3s ease;"><i class="fas fa-heartbeat"></i> 健康检查</a></li>

                <!-- 用户登录状态处理由于在Vue内部直接获取较为复杂，简化为返回任务或退出 -->
                <li>
                    <a href="/logout" style="text-decoration: none; color: #666; font-weight: 500; font-size: 1rem; padding: 0.5rem 0; position: relative; transition: all 0.3s ease;"><i class="fas fa-sign-out-alt"></i> 退出</a>
                </li>
            </ul>
        </div>
    </nav>
    <div style="margin-top: 100px; padding: 20px;">
      <Dashboard />
    </div>
  </div>
</template>

<script setup>
import Dashboard from './views/Dashboard.vue'
</script>