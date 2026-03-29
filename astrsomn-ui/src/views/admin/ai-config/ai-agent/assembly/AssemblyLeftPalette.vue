<template>
  <aside class="assembly-palette palette-left">
    <!-- 上：对话 -->
    <div class="palette-pane palette-pane-top">
      <div class="pane-label pane-label-chat">对话</div>
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
        <a-button type="primary" class="search-btn" @click="$emit('search', 'chat', keywords.chat)">
          <template #icon><SearchOutlined /></template>
        </a-button>
      </div>
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
      <a-pagination
        v-if="chatPage.total > 0"
        class="pane-pager"
        size="small"
        :current="chatPage.current"
        :total="chatPage.total"
        :page-size="pageSize"
        :show-size-changer="false"
        :hide-on-single-page="true"
        @change="(p: number) => $emit('chat-page', p)"
      />
    </div>

    <!-- 下：向量 / 图像 Tab -->
    <div class="palette-pane palette-pane-bottom">
      <a-tabs v-model:activeKey="bottomTab" size="small" class="bottom-tabs">
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
          <a-pagination
            v-if="embeddingPage.total > 0"
            class="pane-pager"
            size="small"
            :current="embeddingPage.current"
            :total="embeddingPage.total"
            :page-size="pageSize"
            :show-size-changer="false"
            :hide-on-single-page="true"
            @change="(p: number) => $emit('embedding-page', p)"
          />
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
          <a-pagination
            v-if="imagePage.total > 0"
            class="pane-pager"
            size="small"
            :current="imagePage.current"
            :total="imagePage.total"
            :page-size="pageSize"
            :show-size-changer="false"
            :hide-on-single-page="true"
            @change="(p: number) => $emit('image-page', p)"
          />
        </a-tab-pane>
      </a-tabs>
    </div>
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

const bottomTab = ref<'embedding' | 'image'>('embedding')

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
  border: 1px solid #f0f0f0;
  background: var(--component-background, #fff);
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: 100%;
  overflow: hidden;
}

.palette-pane {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.palette-pane-top {
  flex: 1;
  padding: 10px 12px 12px;
  margin: 0 10px;
  border-bottom: 1px solid #f0f0f0;
  background: transparent;
}

.palette-pane-bottom {
  flex: 1.15;
  padding: 0 10px 10px;
  margin: 0 10px 10px;
  min-height: 0;
  background: transparent;
}

.pane-label {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
}

.pane-label-chat {
  color: #1890ff;
}

.search-row {
  display: flex;
  align-items: stretch;
  gap: 8px;
  margin-bottom: 8px;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.search-ico {
  color: #bfbfbf;
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

.search-btn-emb {
  background: #722ed1 !important;
  border-color: #722ed1 !important;
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
  min-height: 100px;
}

.pane-pager {
  margin-top: 8px;
  text-align: center;
}

.bottom-tabs {
  min-height: 0;
  padding: 8px 4px 4px;
}

.bottom-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 10px;
}

.bottom-tabs :deep(.ant-tabs-tab) {
  border-radius: 10px 10px 0 0;
}

.bottom-tabs :deep(.ant-tabs-content-holder) {
  overflow: visible;
}

.bottom-tabs :deep(.ant-tabs-tabpane) {
  padding-top: 0;
}

.palette-empty {
  font-size: 12px;
  color: #bfbfbf;
  padding: 16px 4px;
  text-align: center;
}
</style>
