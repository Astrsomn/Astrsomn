<template>
  <AstrsomnModal
    :open="open"
    width="80vw"
    max-width="80vw"
    body-height="80vh"
    header-height="64px"
    content-background="var(--bg-card)"
    main-background="#f8fafc"
    main-padding="0"
    @update:open="emit('update:open', $event)"
    @cancel="emit('cancel')"
  >
    <template #header-logo>
      <AppstoreOutlined />
    </template>
    <template #header-title>插件市场</template>
    <template #header-subtitle>浏览、安装和管理插件扩展</template>
    <template #header-actions>
      <a-button @click="emit('cancel')">关闭</a-button>
    </template>

    <div class="marketplace-shell">
      <ExtensionSidebar
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
  </AstrsomnModal>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { AppstoreOutlined } from '@ant-design/icons-vue'
import AstrsomnModal from '@/components/home/AstrsomnModal.vue'
import ExtensionCenterPage from '@/views/admin/system-config/system-extension/component/ExtensionCenterPage.vue'
import ExtensionSidebar from '@/views/admin/system-config/system-extension/component/ExtensionSidebar.vue'

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
  background: #ffffff;
}

.content-card {
  padding: 24px;
}

.marketplace-shell > :deep(.extension-sider) {
  flex-shrink: 0;
}

.marketplace-content::-webkit-scrollbar {
  width: 6px;
}

.marketplace-content::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 3px;
}

.marketplace-content::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 3px;
}

.marketplace-content::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>
