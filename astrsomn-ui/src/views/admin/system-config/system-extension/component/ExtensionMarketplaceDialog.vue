<template>
  <AstModal
      :open="open"
      body-height="80vh"
      content-background="var(--bg-default)"
      header-height="64px"
      main-background="var(--bg-default)"
      main-padding="0"
      max-width="80vw"
      width="80vw"
      @cancel="emit('cancel')"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <div class="header-logo-box">
        <PhPuzzlePiece :size="20" weight="duotone"/>
      </div>
    </template>
    <template #header-title>{{ t.marketplace.title }}</template>
    <template #header-subtitle>{{ t.marketplace.subtitle }}</template>
    <div class="marketplace-shell">
      <Sidebar
          :selected-menu-keys="selectedMenuKeys"
          @menu-click="onMenuClick"
      />
      <div class="marketplace-content">
        <div class="content-card">
          <ExtensionCenterPage
              :active-tab="activeTab"
              @update:active-tab="onPanelSync"
          />
        </div>
      </div>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {PhPuzzlePiece} from '@phosphor-icons/vue'
import {usePageTranslation} from '@/locales/pages.ts'
import AstModal from '@/components/home/AstModal.vue'
import ExtensionCenterPage from '@/views/admin/system-config/system-extension/component/ExtensionCenterPage.vue'
import Sidebar from '@/views/admin/system-config/system-extension/component/Sidebar.vue'

const t = usePageTranslation('system-extension')

type ExtensionPanel = 'marketplace' | 'installed'

defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  cancel: []
}>()

const activeTab = ref<ExtensionPanel>('marketplace')
const selectedMenuKeys = computed(() => [activeTab.value])

function onMenuClick(key: string) {
  if (key === 'marketplace' || key === 'installed') {
    activeTab.value = key as ExtensionPanel
  }
}

function onPanelSync(tab: ExtensionPanel) {
  activeTab.value = tab
}
</script>

<style scoped>
.header-logo-box {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: var(--primary);
  background: rgba(59, 130, 246, 0.10);
  border: 1px solid rgba(59, 130, 246, 0.18);
  flex-shrink: 0;
}

/* ── 主壳布局 ── */
.marketplace-shell {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
  gap: 0;
  background: var(--bg-default);
}

.marketplace-content {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  background: var(--bg-default);
  padding: 20px 24px;
}

.content-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 16px;
  padding: 0;
  min-height: 100%;
  overflow: hidden;
  transition: border-color 0.2s ease;
}

.content-card:hover {
  border-color: var(--primary);
}

.marketplace-shell > :deep(.extension-sider) {
  flex-shrink: 0;
}

/* ── 滚动条 ── */
.marketplace-content::-webkit-scrollbar {
  width: 6px;
}

.marketplace-content::-webkit-scrollbar-track {
  background: var(--bg-default);
  border-radius: 3px;
}

.marketplace-content::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 3px;
}

.marketplace-content::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}
</style>
