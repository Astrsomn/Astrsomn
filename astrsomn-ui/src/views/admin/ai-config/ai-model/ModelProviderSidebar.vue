<template>
  <aside class="provider-sidebar" aria-label="按模型扩展筛选供应商">
    <h1 class="provider-sidebar-title">模型提供商</h1>
    <a-spin :spinning="loading" class="provider-spin">
      <div class="provider-menu-scroll">
        <a-menu
          mode="inline"
          :selected-keys="menuSelectedKeys"
          class="provider-side-menu"
          @click="onMenuClick"
        >
          <a-menu-item :key="ALL_KEY">
            <span class="menu-row menu-row--all">全部</span>
          </a-menu-item>
          <a-menu-item v-for="it in items" :key="providerRowKey(it)">
            <span class="menu-row">
              <span
                v-if="it.avatar?.trim()"
                class="menu-avatar"
                v-html="it.avatar"
                aria-hidden="true"
              />
              <span class="menu-label">{{ it.extensionName || providerRowKey(it) || '—' }}</span>
            </span>
          </a-menu-item>
        </a-menu>
        <div v-if="!loading && items.length === 0" class="provider-empty">暂无模型类扩展</div>
      </div>
    </a-spin>
  </aside>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { systemExtensionApi, type SystemExtension } from '@/api/systemExtension'

const ALL_KEY = '__all__'

const props = defineProps<{
  /** 与 AI_MODEL.provider / 列表筛选 supplier 一致；空表示全部 */
  selectedKey?: string | null
}>()

const emit = defineEmits<{
  'update:selectedKey': [value: string | undefined]
}>()

const loading = ref(false)
const items = ref<SystemExtension[]>([])

const menuSelectedKeys = computed(() => {
  const p = props.selectedKey
  if (p === undefined || p === null || String(p).trim() === '') {
    return [ALL_KEY]
  }
  return [String(p)]
})

function providerRowKey(it: SystemExtension): string {
  return String(it.providerCode?.trim() || it.extensionKey?.trim() || '')
}

async function load() {
  loading.value = true
  try {
    const resp = await systemExtensionApi.queryPage({
      pageNo: 1,
      pageSize: 500,
      param: {
        type: 'MODEL_PROVIDER',
        listScope: 'INSTALLED'
      }
    })
    items.value = (resp?.list ?? []).filter((x) => providerRowKey(x))
  } catch (e: unknown) {
    const err = e as { message?: string }
    message.error(err?.message || '加载模型提供商失败')
    items.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void load()
})

function onMenuClick(info: { key: string | number }) {
  const key = String(info.key)
  if (key === ALL_KEY) {
    emit('update:selectedKey', undefined)
  } else {
    emit('update:selectedKey', key)
  }
}
</script>

<style scoped>
.provider-sidebar {
  flex: 0 0 200px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  min-height: calc(100vh - 70px);
  max-height: calc(100vh - 70px);
  overflow: hidden;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  background: var(--bg-card);
  padding: 12px 0 16px;
  box-shadow: var(--shadow-card, 0 16px 32px rgba(15, 23, 42, 0.04));
}

.provider-spin {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.provider-spin :deep(.ant-spin-container) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.provider-menu-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.provider-sidebar-title {
  padding: 0 16px 10px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.04em;
}

.provider-side-menu {
  border-inline-end: none !important;
  background: transparent !important;
}

.provider-side-menu :deep(.ant-menu-item) {
  margin-inline: 8px;
  width: auto;
  border-radius: var(--radius-max);
  height: auto;
  line-height: 1.3;
  padding-block: 8px;
}

.menu-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.menu-row--all {
  font-weight: 600;
}

.menu-avatar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-avatar :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}

.menu-label {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}

.provider-empty {
  padding: 16px;
  font-size: 12px;
  color: var(--text-secondary);
  text-align: center;
}
</style>
