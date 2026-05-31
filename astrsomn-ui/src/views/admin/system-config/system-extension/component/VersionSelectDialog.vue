<template>
  <AstModal
      :open="open"
      body-height="auto"
      content-background="var(--bg-card)"
      header-height="56px"
      main-background="var(--bg-surface)"
      main-padding="0"
      max-width="520px"
      width="520px"
      @cancel="emit('cancel')"
      @update:open="emit('update:open', $event)"
  >
    <template #header-title>选择版本</template>
    <template #header-subtitle>{{ extensionName }} — 请选择要安装的版本</template>

    <div class="version-dialog-body">
      <div v-if="loading" class="version-loading">
        <a-spin />
        <span>加载版本列表...</span>
      </div>

      <div v-else-if="versions.length === 0" class="version-empty">
        <a-empty description="暂无可用的版本" />
      </div>

      <div v-else class="version-list">
        <div
            v-for="v in versions"
            :key="v.version"
            :class="['version-item', { selected: selectedVersion === v.version, installed: v.version === installedVersion }]"
            @click="selectedVersion = v.version"
        >
          <div class="version-left">
            <span class="version-tag">{{ v.version }}</span>
            <span v-if="v.version === installedVersion" class="installed-badge">当前版本</span>
            <span v-if="v.version === latestVersion" class="latest-badge">最新</span>
          </div>
          <div class="version-right">
            <span class="version-downloads">{{ v.downloadCount || 0 }} 次下载</span>
          </div>
          <div v-if="v.changelog" class="version-changelog">
            {{ v.changelog }}
          </div>
        </div>
      </div>
    </div>

    <div class="version-footer">
      <a-button @click="emit('cancel')">取消</a-button>
      <a-button
          :disabled="!selectedVersion || selectedVersion === installedVersion"
          :loading="installing"
          type="primary"
          @click="handleConfirm"
      >
        {{ selectedVersion === installedVersion ? '已安装此版本' : '安装' }}
      </a-button>
    </div>
  </AstModal>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue'
import {message} from 'ant-design-vue'
import AstModal from '@/components/home/AstModal.vue'
import {extensionMarketplaceApi, type ExtensionMarketplaceVersion} from '@/api/extensionMarketplace.ts'

const props = defineProps<{
  open: boolean
  extensionName: string
  pluginId: string
  installedVersion?: string
  latestVersion?: string
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
  cancel: []
  confirm: [pluginId: string, version: string]
}>()

const versions = ref<ExtensionMarketplaceVersion[]>([])
const selectedVersion = ref('')
const loading = ref(false)
const installing = ref(false)

async function fetchVersions(pluginId: string) {
  if (!pluginId) return
  loading.value = true
  versions.value = []
  selectedVersion.value = ''
  try {
    const resp = await extensionMarketplaceApi.getVersions(pluginId)
    versions.value = resp.list || []
    // Auto-select the first (latest) version
    if (versions.value.length > 0) {
      selectedVersion.value = versions.value[0].version || ''
    }
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '获取版本列表失败')
  } finally {
    loading.value = false
  }
}

watch(
    () => props.open && props.pluginId,
    (shouldFetch) => {
      if (shouldFetch) {
        void fetchVersions(props.pluginId)
      } else if (!props.open) {
        versions.value = []
        selectedVersion.value = ''
        installing.value = false
      }
    }
)

function handleConfirm() {
  if (!selectedVersion.value) return
  installing.value = true
  emit('confirm', props.pluginId, selectedVersion.value)
}
</script>

<style scoped>
.version-dialog-body {
  padding: 16px 20px;
  min-height: 120px;
  max-height: 360px;
  overflow-y: auto;
}

.version-loading {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
  padding: 32px 0;
  color: var(--text-muted);
  font-size: 13px;
}

.version-empty {
  padding: 32px 0;
}

.version-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.version-item {
  padding: 12px 14px;
  border: 1px solid var(--border-default);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.version-item:hover {
  border-color: var(--primary);
  background: var(--bg-elevated);
}

.version-item.selected {
  border-color: var(--primary);
  background: rgba(59, 130, 246, 0.06);
}

.version-item.installed {
  border-color: var(--border-default);
  background: var(--bg-surface);
}

.version-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.version-tag {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.installed-badge {
  padding: 1px 7px;
  border-radius: 5px;
  font-size: 11px;
  color: var(--primary);
  background: rgba(59, 130, 246, 0.12);
}

.latest-badge {
  padding: 1px 7px;
  border-radius: 5px;
  font-size: 11px;
  color: #10b981;
  background: rgba(16, 185, 129, 0.12);
}

.version-right {
  margin-top: 4px;
}

.version-downloads {
  font-size: 12px;
  color: var(--text-muted);
}

.version-changelog {
  margin-top: 6px;
  padding: 8px 10px;
  border-radius: 6px;
  background: var(--bg-surface);
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.version-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
}

.version-dialog-body::-webkit-scrollbar {
  width: 6px;
}

.version-dialog-body::-webkit-scrollbar-track {
  background: transparent;
}

.version-dialog-body::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 3px;
}
</style>
