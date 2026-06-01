<template>
  <aside class="assembly-palette palette-left">
    <a-tabs v-model:activeKey="activeTab" class="left-tabs" size="small">
      <a-tab-pane key="chat" :tab="t.leftPalette.chatTab">
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon"/>
          <input
              v-model="keywords.chat"
              :placeholder="t.leftPalette.chatPlaceholder"
              type="text"
              @keyup.enter="$emit('search', 'chat', keywords.chat)"
          />
          <button class="search-submit-btn" type="button" @click="$emit('search', 'chat', keywords.chat)">{{ t.leftPalette.search }}</button>
        </div>
        <a-pagination
            v-if="chatPage.total > 0"
            :current="chatPage.current"
            :hide-on-single-page="true"
            :page-size="pageSize"
            :show-size-changer="false"
            :total="chatPage.total"
            class="pane-pager-top"
            size="small"
            @change="(p: number) => $emit('chat-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="chatItems.length">
            <AssemblyDragChip
                v-for="row in chatItems"
                :key="String(row.instanceKey ?? row.id)"
                :payload="{ kind: 'instance', instanceModelType: 'chat', data: row }"
                :subtitle="row.instanceKey"
                :title="row.instanceName || row.instanceKey || ''"
                badge="Chat"
                @drag-start="$emit('dragStart', $event)"
                @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">{{ t.leftPalette.noData }}</div>
        </div>
      </a-tab-pane>

      <a-tab-pane key="image" :tab="t.leftPalette.imageTab">
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon"/>
          <input
              v-model="keywords.image"
              :placeholder="t.leftPalette.imagePlaceholder"
              type="text"
              @keyup.enter="$emit('search', 'image', keywords.image)"
          />
          <button class="search-submit-btn" type="button" @click="$emit('search', 'image', keywords.image)">{{ t.leftPalette.search }}
          </button>
        </div>
        <a-pagination
            v-if="imagePage.total > 0"
            :current="imagePage.current"
            :hide-on-single-page="true"
            :page-size="pageSize"
            :show-size-changer="false"
            :total="imagePage.total"
            class="pane-pager-top"
            size="small"
            @change="(p: number) => $emit('image-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="imageItems.length">
            <AssemblyDragChip
                v-for="row in imageItems"
                :key="String(row.instanceKey ?? row.id)"
                :payload="{ kind: 'instance', instanceModelType: 'image', data: row }"
                :subtitle="row.instanceKey"
                :title="row.instanceName || row.instanceKey || ''"
                badge="Img"
                @drag-start="$emit('dragStart', $event)"
                @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">{{ t.leftPalette.noData }}</div>
        </div>
      </a-tab-pane>
    </a-tabs>
  </aside>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
import {SearchOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {AiInstance} from '@/api/aiInstance'
import type {AssemblyDragPayload, InstanceModelType} from './assemblyTypes'
import AssemblyDragChip from './AssemblyDragChip.vue'

const t = usePageTranslation('ai-agent')

defineProps<{
  pageSize: number
  chatItems: AiInstance[]
  imageItems: AiInstance[]
  chatPage: { current: number; total: number }
  imagePage: { current: number; total: number }
}>()

defineEmits<{
  search: [modelType: InstanceModelType, keyword: string]
  'chat-page': [page: number]
  'image-page': [page: number]
  dragStart: [payload: AssemblyDragPayload]
  dragEnd: []
}>()

const activeTab = ref<'chat' | 'image'>('chat')

const keywords = reactive<Record<InstanceModelType, string>>({
  chat: '',
  image: ''
})

function getKeywords(): Record<InstanceModelType, string> {
  return {...keywords}
}

defineExpose({getKeywords})
</script>

<style scoped>
.assembly-palette {
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
  background: var(--assembly-palette-bg);
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - 80px);
  overflow: hidden;
  padding-inline: 10px;
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


.search-input-wrapper {
  width: 100%;
  height: 52px;
  background: var(--bg-card);
  border-radius: 26px;
  padding: 0 8px 0 16px;
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  flex-shrink: 0;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-default);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-input-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: var(--shadow-overview, 0 0 0 3px color-mix(in srgb, var(--primary) 22%, transparent));
}

.search-icon {
  color: var(--primary);
  font-size: 18px;
  flex-shrink: 0;
}

.search-input-wrapper input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  font-size: 14px;
  margin-left: 8px;
  background: transparent;
  color: var(--text-primary);
}

.search-submit-btn {
  flex-shrink: 0;
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  box-shadow: var(--chat-send-btn-shadow);
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
