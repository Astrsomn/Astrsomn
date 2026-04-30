<template>
  <div class="admin-shell">
    <AppHeader 
      :showBrand="!showExtensionBackHeader"
      :showBack="showExtensionBackHeader"
      :page-title="headerPageTitle"
      :showDoc="true"
      :showSwitch="true"
      :show-workspace-env="true"
      switchTarget="chat"
    />

    <main class="shell-content" @scroll="handleScroll">
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>

    <BottomNav :auto-hide="bottomNavAutoHide" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/top/AppHeader.vue';
import BottomNav from '@/components/bottom/BottomNav.vue';

const route = useRoute();

const leafMeta = () => {
  const m = route.matched[route.matched.length - 1];
  return m?.meta;
};

const showExtensionBackHeader = computed(() => {
  return (
    route.name === 'AdminSystemExtension' ||
    route.name === 'AdminWorkflowDefinitionBuilder' ||
    route.name === 'AdminWorkflowDefinitionEditBuilder'
  );
});

const headerPageTitle = computed(() => {
  const title = leafMeta()?.title;
  return typeof title === 'string' && title.trim() ? title : '管理后台';
});

/** Dock 根页：始终展开底部栏；二级页：用 auto-hide 收起，悬停底部区域唤醒 */
const showAdminDock = computed(() => leafMeta()?.showAdminDock === true);
const bottomNavAutoHide = computed(() => !showAdminDock.value);

const handleScroll = () => {
  // 滚动时的处理逻辑
};
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
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: color-mix(in srgb, var(--text-muted) 24%, transparent) transparent;
}

.shell-content::-webkit-scrollbar {
  width: 7px;
}

.shell-content::-webkit-scrollbar-track {
  background: transparent;
}

.shell-content::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: color-mix(in srgb, var(--text-muted) 18%, transparent);
}

.shell-content::-webkit-scrollbar-thumb:hover {
  background: color-mix(in srgb, var(--text-muted) 28%, transparent);
}

.content-wrapper {
  margin: 60px auto 0;
  width: 100%;

}



/* 让顶部 Header 覆盖在内容上方，这样 Header 透明时能看到底下页面内容 */
:deep(.app-header) {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .content-wrapper { padding: 0 16px; }
}
</style>