<template>
  <AstPageShell empty-text="暂无系统扩展。">
    <div class="extension-page">
      <div class="extension-shell">
        <Sidebar
            :selected-menu-keys="selectedMenuKeys"
            @menu-click="onMenuClick"
        />
        <div class="extension-content">
          <div class="content-card">
            <ExtensionCenterPage :active-tab="activePanel" @update:active-tab="onPanelSync"/>
          </div>
        </div>
      </div>
    </div>
  </AstPageShell>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from 'vue'
import AstPageShell from '@/components/home/AstPageShell.vue'
import ExtensionCenterPage from './component/ExtensionCenterPage.vue'
import Sidebar from './component/Sidebar.vue'
import {useRoute} from 'vue-router'

type ExtensionPanel = 'marketplace' | 'installed'
const route = useRoute()
const panelFromQuery = String(route.query.panel || '')
const activePanel = ref<ExtensionPanel>(panelFromQuery === 'marketplace' ? 'marketplace' : 'installed')
const selectedMenuKeys = computed(() => [activePanel.value])

function onMenuClick(key: string) {
  if (key === 'marketplace' || key === 'installed') {
    activePanel.value = key as ExtensionPanel
  }
}

function onPanelSync(tab: ExtensionPanel) {
  activePanel.value = tab
}

watch(
    () => route.query.panel,
    (panel) => {
      if (panel === 'marketplace' || panel === 'installed') {
        activePanel.value = panel
      }
    }
)
</script>

<style scoped>

.extension-page {
  padding: 0;
  height: calc(100vh - 70px);
  overflow: hidden;
  background: var(--bg-surface);
}

.extension-shell {
  display: flex;
  width: 100%;
  height: 100%;
  background-color: var(--bg-surface);
  overflow: hidden;
}


.extension-content {
  flex: 1;
  background: var(--bg-card);
  overflow-y: auto;
}

.content-card {
  padding: 24px;
}


.extension-content::-webkit-scrollbar {
  width: 6px;
}

.extension-content::-webkit-scrollbar-track {
  background: var(--bg-surface);
  border-radius: 3px;
}

.extension-content::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 3px;
}

.extension-content::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}
</style>