<template>
  <aside class="assembly-palette palette-left">
    <a-tabs v-model:activeKey="activeTab" size="small" class="left-tabs">
      <a-tab-pane key="chat" tab="对话">
        <div class="search-row">
          <a-input
            v-model:value="keywords.chat"
            size="middle"
            placeholder="预设名称 / Instance Key"
            allow-clear
            class="search-input"
            @pressEnter="$emit('search', 'chat', keywords.chat)"
          >
            <template #prefix><SearchOutlined class="search-ico" /></template>
          </a-input>
          <a-button type="primary" class="search-btn search-btn-chat" @click="$emit('search', 'chat', keywords.chat)">
            <template #icon><SearchOutlined /></template>
          </a-button>
        </div>
        <a-pagination
          v-if="chatPage.total > 0"
          class="pane-pager-top"
          size="small"
          :current="chatPage.current"
          :total="chatPage.total"
          :page-size="pageSize"
          :show-size-changer="false"
          :hide-on-single-page="true"
          @change="(p: number) => $emit('chat-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="chatItems.length">
            <AssemblyDragChip
              v-for="row in chatItems"
              :key="String(row.instanceKey ?? row.id)"
              :payload="{ kind: 'instance', instanceModelType: 'chat', data: row }"
              :title="row.instanceName || row.instanceKey || ''"
              :subtitle="row.instanceKey"
              badge="Chat"
              @drag-start="$emit('dragStart', $event)"
              @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">暂无数据</div>
        </div>
      </a-tab-pane>
      
      <a-tab-pane key="embedding" tab="向量">
        <div class="search-row">
          <a-input
            v-model:value="keywords.embedding"
            size="middle"
            placeholder="预设名称 / Instance Key"
            allow-clear
            class="search-input"
            @pressEnter="$emit('search', 'embedding', keywords.embedding)"
          >
            <template #prefix><SearchOutlined class="search-ico" /></template>
          </a-input>
          <a-button type="primary" class="search-btn search-btn-emb" @click="$emit('search', 'embedding', keywords.embedding)">
            <template #icon><SearchOutlined /></template>
          </a-button>
        </div>
        <a-pagination
          v-if="embeddingPage.total > 0"
          class="pane-pager-top"
          size="small"
          :current="embeddingPage.current"
          :total="embeddingPage.total"
          :page-size="pageSize"
          :show-size-changer="false"
          :hide-on-single-page="true"
          @change="(p: number) => $emit('embedding-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="embeddingItems.length">
            <AssemblyDragChip
              v-for="row in embeddingItems"
              :key="String(row.instanceKey ?? row.id)"
              :payload="{ kind: 'instance', instanceModelType: 'embedding', data: row }"
              :title="row.instanceName || row.instanceKey || ''"
              :subtitle="row.instanceKey"
              badge="Emb"
              @drag-start="$emit('dragStart', $event)"
              @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">暂无数据</div>
        </div>
      </a-tab-pane>
      
      <a-tab-pane key="image" tab="图像">
        <div class="search-row">
          <a-input
            v-model:value="keywords.image"
            size="middle"
            placeholder="预设名称 / Instance Key"
            allow-clear
            class="search-input"
            @pressEnter="$emit('search', 'image', keywords.image)"
          >
            <template #prefix><SearchOutlined class="search-ico" /></template>
          </a-input>
          <a-button type="primary" class="search-btn search-btn-img" @click="$emit('search', 'image', keywords.image)">
            <template #icon><SearchOutlined /></template>
          </a-button>
        </div>
        <a-pagination
          v-if="imagePage.total > 0"
          class="pane-pager-top"
          size="small"
          :current="imagePage.current"
          :total="imagePage.total"
          :page-size="pageSize"
          :show-size-changer="false"
          :hide-on-single-page="true"
          @change="(p: number) => $emit('image-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="imageItems.length">
            <AssemblyDragChip
              v-for="row in imageItems"
              :key="String(row.instanceKey ?? row.id)"
              :payload="{ kind: 'instance', instanceModelType: 'image', data: row }"
              :title="row.instanceName || row.instanceKey || ''"
              :subtitle="row.instanceKey"
              badge="Img"
              @drag-start="$emit('dragStart', $event)"
              @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">暂无数据</div>
        </div>
      </a-tab-pane>
    </a-tabs>
  </aside>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import type { AiInstance } from '@/api/aiInstance'
import type { AssemblyDragPayload, InstanceModelType } from './assemblyTypes'
import AssemblyDragChip from './AssemblyDragChip.vue'

defineProps<{
  pageSize: number
  chatItems: AiInstance[]
  embeddingItems: AiInstance[]
  imageItems: AiInstance[]
  chatPage: { current: number; total: number }
  embeddingPage: { current: number; total: number }
  imagePage: { current: number; total: number }
}>()

defineEmits<{
  search: [modelType: InstanceModelType, keyword: string]
  'chat-page': [page: number]
  'embedding-page': [page: number]
  'image-page': [page: number]
  dragStart: [payload: AssemblyDragPayload]
  dragEnd: []
}>()

const activeTab = ref<'chat' | 'embedding' | 'image'>('chat')

const keywords = reactive<Record<InstanceModelType, string>>({
  chat: '',
  embedding: '',
  image: ''
})

function getKeywords(): Record<InstanceModelType, string> {
  return { ...keywords }
}

defineExpose({ getKeywords })
</script>

<style scoped>
.assembly-palette {
  border-radius: 16px;
  border: 1px solid var(--assembly-palette-border);
  background: var(--assembly-palette-bg);
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - 100px);
  overflow: hidden;
}

.left-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.left-tabs :deep(.ant-tabs-content) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.left-tabs :deep(.ant-tabs-tabpane) {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pane-pager-top {
  margin-bottom: 8px;
  text-align: center;
  flex-shrink: 0;
}

.search-row {
  display: flex;
  align-items: stretch;
  gap: 8px;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.search-ico {
  color: var(--assembly-search-icon);
}

.search-row :deep(.ant-input-affix-wrapper) {
  border-radius: 12px;
  min-height: 36px;
  padding-inline: 11px;
}

.search-btn {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  min-height: 36px;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.search-btn-chat {
  background: #1890ff !important;
  border-color: #1890ff !important;
}

.search-btn-emb {
  background: #52c41a !important;
  border-color: #52c41a !important;
}

.search-btn-img {
  background: #fa8c16 !important;
  border-color: #fa8c16 !important;
}

.chip-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 0;
  padding: 0 12px 12px;
}

.palette-empty {
  font-size: 12px;
  color: var(--assembly-empty-text);
  padding: 16px 4px;
  text-align: center;
}
</style>
