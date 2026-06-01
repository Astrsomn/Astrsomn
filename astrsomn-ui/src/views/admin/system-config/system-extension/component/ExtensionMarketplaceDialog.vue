<template>
  <AstModal
      :open="open"
      body-height="80vh"
      content-background="var(--bg-card)"
      header-height="64px"
      main-background="var(--bg-surface)"
      main-padding="0"
      max-width="80vw"
      width="80vw"
      @cancel="emit('cancel')"
      @update:open="emit('update:open', $event)"
  >
    <template #header-logo>
      <AppstoreOutlined/>
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
import {AppstoreOutlined} from '@ant-design/icons-vue'
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
.marketplace-shell {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.marketplace-content {
  flex: 1;
  overflow-y: auto;
  background: var(--bg-card);
}

.content-card {
  padding: 24px;
  border-radius: var(--radius-md);
}

.marketplace-shell > :deep(.extension-sider) {
  flex-shrink: 0;
}

.marketplace-content::-webkit-scrollbar {
  width: 6px;
}

.marketplace-content::-webkit-scrollbar-track {
  background: var(--bg-surface);
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
