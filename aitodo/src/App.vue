<template>
  <div id="app-root">
    <Navbar v-if="showNav" />
    <main :class="{ 'has-nav': showNav }">
      <router-view />
    </main>
    <AiAgent v-if="showNav" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import AiAgent from './components/AiAgent.vue'
import { useShortcuts } from './composables/useShortcuts.js'

// 全局快捷键
useShortcuts()

const route = useRoute()
const showNav = computed(() => route.meta.hideNav !== true)
</script>

<style>
/* 给有导航栏的页面留出顶部空间 */
main.has-nav {
  padding-top: 60px;
}

@media (max-width: 768px) {
  main.has-nav {
    padding-top: 0;
  }
}
</style>