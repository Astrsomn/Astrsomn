<template>
  <aside class="assembly-palette palette-right">
    <!-- Tab页切换 -->
    <a-tabs v-model:activeKey="activeTab" class="palette-tabs">
      <!-- Prompt Tab -->
      <a-tab-pane key="prompts" tab="Prompt">
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon"/>
          <input
              v-model="kwPrompt"
              :placeholder="t.rightPalette.promptPlaceholder"
              type="text"
              @keyup.enter="$emit('searchPrompt')"
          />
          <button class="search-submit-btn" type="button" @click="$emit('searchPrompt')">{{ t.rightPalette.search }}</button>
        </div>
        <a-pagination
            v-if="promptPage.total > 0"
            :current="promptPage.current"
            :hide-on-single-page="true"
            :page-size="pageSize"
            :show-size-changer="false"
            :total="promptPage.total"
            class="pane-pager-top"
            size="small"
            @change="(p: number) => $emit('prompt-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="prompts.length">
            <AssemblyDragChip
                v-for="row in prompts"
                :key="String(row.promptKey ?? row.id)"
                :payload="{ kind: 'prompt', data: row }"
                :subtitle="row.promptKey"
                :title="row.promptTitle || row.promptKey || ''"
                badge="Prompt"
                @drag-start="$emit('dragStart', $event)"
                @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">{{ t.rightPalette.noData }}</div>
        </div>
      </a-tab-pane>

      <!-- 工具Tab -->
      <a-tab-pane key="tools" :tab="t.rightPalette.toolTab">
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon"/>
          <input
              v-model="kwTool"
              :placeholder="t.rightPalette.toolPlaceholder"
              type="text"
              @keyup.enter="$emit('searchTool')"
          />
          <button class="search-submit-btn" type="button" @click="$emit('searchTool')">{{ t.rightPalette.search }}</button>
        </div>
        <a-pagination
            v-if="toolPage.total > 0"
            :current="toolPage.current"
            :hide-on-single-page="true"
            :page-size="pageSize"
            :show-size-changer="false"
            :total="toolPage.total"
            class="pane-pager-top"
            size="small"
            @change="(p: number) => $emit('tool-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="tools.length">
            <AssemblyDragChip
                v-for="row in tools"
                :key="String(row.toolKey ?? row.id)"
                :payload="{ kind: 'tool', data: row }"
                :subtitle="row.toolKey"
                :title="row.toolName || row.toolKey || ''"
                badge="Tool"
                @drag-start="$emit('dragStart', $event)"
                @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">{{ t.rightPalette.noData }}</div>
        </div>
      </a-tab-pane>

      <!-- MCP Tab -->
      <a-tab-pane key="mcps" :tab="t.rightPalette.mcpTab">
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon"/>
          <input
              v-model="kwMcp"
              :placeholder="t.rightPalette.mcpPlaceholder"
              type="text"
              @keyup.enter="$emit('searchMcp')"
          />
          <button class="search-submit-btn" type="button" @click="$emit('searchMcp')">{{ t.rightPalette.search }}</button>
        </div>
        <a-pagination
            v-if="mcpPage.total > 0"
            :current="mcpPage.current"
            :hide-on-single-page="true"
            :page-size="pageSize"
            :show-size-changer="false"
            :total="mcpPage.total"
            class="pane-pager-top"
            size="small"
            @change="(p: number) => $emit('mcp-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="mcps.length">
            <AssemblyDragChip
                v-for="row in mcps"
                :key="String(row.mcpKey ?? row.id)"
                :payload="{ kind: 'mcp', data: row }"
                :subtitle="row.mcpKey"
                :title="row.serverName || row.mcpKey || ''"
                badge="MCP"
                @drag-start="$emit('dragStart', $event)"
                @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">{{ t.rightPalette.noData }}</div>
        </div>
      </a-tab-pane>
    </a-tabs>
  </aside>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {SearchOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'
import type {AiPrompt} from '@/api/aiPrompt'
import type {AssemblyDragPayload} from './assemblyTypes'
import AssemblyDragChip from './AssemblyDragChip.vue'

const t = usePageTranslation('ai-agent')

defineProps<{
  pageSize: number
  tools: AiTool[]
  mcps: AiMcp[]
  prompts: AiPrompt[]
  toolPage: { current: number; total: number }
  mcpPage: { current: number; total: number }
  promptPage: { current: number; total: number }
}>()

defineEmits<{
  searchTool: []
  searchMcp: []
  searchPrompt: []
  'tool-page': [page: number]
  'mcp-page': [page: number]
  'prompt-page': [page: number]
  dragStart: [payload: AssemblyDragPayload]
  dragEnd: []
}>()

const activeTab = ref('prompts')
const kwTool = ref('')
const kwMcp = ref('')
const kwPrompt = ref('')

function getKeywords() {
  return {
    tool: kwTool.value,
    mcp: kwMcp.value,
    prompt: kwPrompt.value
  }
}

defineExpose({getKeywords})
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
  padding-inline: 8px;
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

.palette-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.palette-tabs :deep(.ant-tabs-content) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.palette-tabs :deep(.ant-tabs-tabpane) {
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

.chip-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 70vh;
  padding: 0 12px 12px;
}

.palette-empty {
  font-size: 12px;
  color: var(--assembly-empty-text);
  padding: 16px 4px;
  text-align: center;
}
</style>
