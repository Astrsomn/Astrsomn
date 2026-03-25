<template>
  <div class="admin-shell">
    <AppHeader 
      :showBrand="isHome" 
      :showBack="!isHome"
      :pageTitle="pageTitle"
      :showDoc="true"
      :showSwitch="true"
      switchTarget="chat"
    />

    <main class="shell-content">
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';

const route = useRoute();

const isHome = computed(() => route.path === '/admin');
const pageTitle = computed(() => (route.meta.title as string) || '管理后台');
</script>

<style scoped>
/* 基础布局 */
.admin-shell {
  height: 100vh;
  background-color: var(--bg-base);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  overflow: hidden;
}

/* 主体内容区 */
.shell-content {
  flex: 1;
  padding: 32px 0;
  overflow-y: auto;
}

.content-wrapper {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 32px;
}

/* 动画效果 */
.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter-from { opacity: 0; transform: translateY(8px); }
.page-fade-leave-to { opacity: 0; }

/* 响应式适配 */
@media (max-width: 768px) {
  .content-wrapper { padding: 0 16px; }
}
</style>