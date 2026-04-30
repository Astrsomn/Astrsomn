<template>
  <AdminPageShell empty-text="暂无系统扩展。">
    <div class="extension-page">
      <div class="extension-shell">
        <ExtensionSidebar 
          :selected-menu-keys="selectedMenuKeys" 
          @menu-click="onMenuClick" 
        />
        <div class="extension-content">
          <div class="content-card">
            <ExtensionCenterPage :active-tab="activePanel" @update:active-tab="onPanelSync" />
          </div>
        </div>
      </div>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import ExtensionCenterPage from './component/ExtensionCenterPage.vue'
import ExtensionSidebar from './component/ExtensionSidebar.vue'

type ExtensionPanel = 'marketplace' | 'installed'
const activePanel = ref<ExtensionPanel>('installed')
const selectedMenuKeys = computed(() => [activePanel.value])

function onMenuClick(key: string) {
  if (key === 'marketplace' || key === 'installed') {
    activePanel.value = key as ExtensionPanel
  }
}

function onPanelSync(tab: ExtensionPanel) {
  activePanel.value = tab
}
</script>

<style scoped>
/* 布局容器：与 AdminModuleShell 保持一致 */
.extension-page {
  padding: 0;
  height: calc(100vh - 70px);
  overflow: hidden;
  background: #f8fafc;
}

.extension-shell {
  display: flex;
  width: 100%;
  height: 100%;
  background-color: #f8fafc;
  overflow: hidden;
}

/* 主内容区 */
.extension-content {
  flex: 1;
  background: #ffffff;
  overflow-y: auto;
}

.content-card {
  padding: 24px;
}

/* 滚动条样式 */
.extension-content::-webkit-scrollbar {
  width: 6px;
}

.extension-content::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 3px;
}

.extension-content::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 3px;
}

.extension-content::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>