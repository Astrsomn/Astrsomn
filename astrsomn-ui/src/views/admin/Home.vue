<template>
  <div class="admin-shell">
    <!-- 左侧：ActivityBar（顶部含 logo） + AdminSidebar（路由感知，贯穿顶部） -->
    <div class="left-section">
      <ActivityBar />
      <AdminSidebar />
    </div>

    <!-- 右侧：AppHeader + 主内容 -->
    <div class="right-section">
      <AppHeader
          :page-title="headerPageTitle"
          :show-workspace-env="isAdminRoute"
          :showBack="showExtensionBackHeader"
          :showDoc="true"
      />

      <main class="shell-content" @scroll="handleScroll">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition mode="out-in" name="page-fade">
              <component :is="Component"/>
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted} from 'vue';
import {useRoute} from 'vue-router';
import AppHeader from '@/components/top/AppHeader.vue';
import ActivityBar from '@/components/global/ActivityBar.vue';
import AdminSidebar from '@/components/global/AdminSidebar.vue';
import {useGuide} from '@/composables/useGuide.ts';

const route = useRoute();
const {initGuideAfterLogin} = useGuide();

onMounted(() => {
  initGuideAfterLogin(800);
});

const isAdminRoute = computed(() => route.path.startsWith('/admin'));
const isChatRoute = computed(
    () => route.path === '/' || route.path === '' || route.path === '/chat'
);

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
  if (isChatRoute.value) {
    return '大模型聊天';
  }
  const title = leafMeta()?.title;
  return typeof title === 'string' && title.trim() ? title : '管理后台';
});

const handleScroll = () => {

};
</script>

<style scoped>
.admin-shell {
  height: 100vh;
  background-color: var(--bg-base);
  color: var(--text-primary);
  display: flex;
  flex-direction: row;
  transition: background-color 0.3s ease;
  overflow: hidden;
}

/* ============ 左侧：ActivityBar + AdminSidebar ============ */
.left-section {
  display: flex;
  flex-direction: row;
  flex-shrink: 0;
  height: 100%;
  position: relative;
  z-index: 50;
}

/* 覆盖 ActivityBar 的 fixed 定位，让它回到 flex 容器内并贯穿整个高度 */
:deep(.activity-bar) {
  position: relative;
  left: auto;
  top: auto;
  bottom: auto;
  height: 100%;
  width: 76px;
  border-right: 1px solid var(--border-default);
}

/* 覆盖 SidebarShell 的高度，让它贯穿到顶部 */
:deep(.ast-sidebar) {
  height: 100%;
}

/* AstSidebar（原 chat-index 的会话侧边栏）需要充满 AdminSidebar 容器 */
:deep(.admin-sidebar-wrapper) .sidebar-root,
:deep(.admin-sidebar-wrapper) .sidebar-shell {
  height: 100%;
  max-height: 100%;
}

/* ============ 右侧：AppHeader + 主内容 ============ */
.right-section {
  flex: 1;
  min-width: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: var(--bg-base);
}

:deep(.app-header) {
  position: sticky;
  top: 0;
  left: auto;
  right: auto;
  z-index: 1000;
  flex-shrink: 0;
  background: var(--bg-card);
}

.shell-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
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
  flex: 1;
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 0 16px;
  }
}
</style>


