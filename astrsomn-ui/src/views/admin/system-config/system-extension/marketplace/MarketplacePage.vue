<template>
  <div class="extension-subpage">
    <AdminListToolbar>
      <template #left>
        <AstrsomnSearchPill
          v-model="extensionNameInput"
          placeholder="搜索扩展名称"
          layout="toolbar"
          @search="fetchList"
        />
      </template>

    </AdminListToolbar>

    <a-tabs v-model:activeKey="typeTabKey" class="type-tabs" @change="onTypeTabChange">
      <a-tab-pane key="ALL" tab="全部" />
      <a-tab-pane key="MODEL_PROVIDER" tab="模型" />
      <a-tab-pane key="VECTOR_STORE" tab="向量库" />
      <a-tab-pane key="MCP" tab="MCP" />
    </a-tabs>


    <div v-if="list.length > 0" class="extension-grid">
      <ExtensionMarketplaceCard
        v-for="item in list"
        :key="rowKey(item)"
        :record="item"
        @install="installFromCatalog(item)"
      />
    </div>
    <div v-else class="extension-empty">
      <a-empty description="暂无市场插件" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import AdminListToolbar from '@/components/home/AdminListToolbar.vue'
import AstrsomnSearchPill from '@/components/home/AstrsomnSearchPill.vue'
import ExtensionMarketplaceCard from './ExtensionMarketplaceCard.vue'
import type { ExtensionRow } from '../shared/extensionDisplay'
import { systemExtensionApi, type SystemExtension } from '@/api/systemExtension'

const typeTabKey = ref('ALL')
const extensionNameInput = ref('')
const list = ref<ExtensionRow[]>([])

function rowKey(record: ExtensionRow) {
  return String(record.extensionKey ?? '')
}

const onTypeTabChange = () => {
  void fetchList()
}

const fetchList = async () => {
  const typeQ = typeTabKey.value === 'ALL' ? undefined : typeTabKey.value
  let rows = await systemExtensionApi.marketplaceCatalog(typeQ)
  const n = extensionNameInput.value?.trim().toLowerCase()
  if (n) {
    rows = rows.filter((r) => (r.extensionName || '').toLowerCase().includes(n))
  }
  list.value = rows as ExtensionRow[]
}

const installFromCatalog = async (item: ExtensionRow) => {
  const payload: SystemExtension = {
    extensionKey: item.extensionKey,
    extensionName: item.extensionName,
    type: item.type,
    version: item.version,
    author: item.author,
    description: item.description,
    jarName: item.jarName,
    providerCode: item.providerCode,
    avatar: item.avatar,
    applied: 'N',
    status: 'INSTALLED'
  }
  const msg = await systemExtensionApi.create(payload)
  message.success(msg)
  message.info('可在「已安装插件」中查看、应用插件或加载模型。')
}

void fetchList()
</script>

<style scoped>
.type-tabs {
  margin: 12px 0 8px;
}

.type-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.type-tabs :deep(.ant-tabs-tab) {
  color: var(--text-secondary);
}

.type-tabs :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: var(--primary-light) !important;
}

.type-tabs :deep(.ant-tabs-ink-bar) {
  background: var(--primary-gradient);
}

.extension-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 16px;
}

.extension-empty {
  margin-top: 48px;
  padding: 24px;
}

.extension-empty :deep(.ant-empty-description) {
  color: var(--text-muted);
}



@media (max-width: 720px) {
  .extension-grid {
    grid-template-columns: 1fr;
  }
}
</style>
