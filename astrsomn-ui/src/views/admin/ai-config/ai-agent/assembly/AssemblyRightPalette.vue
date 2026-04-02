<template>
  <aside class="assembly-palette palette-right">
    <!-- Tab页切换 -->
    <a-tabs class="palette-tabs" v-model:activeKey="activeTab">
      <!-- Prompt Tab -->
      <a-tab-pane key="prompts" tab="Prompt">
        <div class="search-row">
          <a-input
            v-model:value="kwPrompt"
            size="middle"
            placeholder="Prompt名称 / Key"
            allow-clear
            class="search-input"
            @pressEnter="$emit('searchPrompt')"
          >
            <template #prefix><SearchOutlined class="search-ico" /></template>
          </a-input>
          <a-button type="primary" class="search-btn search-btn-prompt" @click="$emit('searchPrompt')">
            <template #icon><SearchOutlined /></template>
          </a-button>
        </div>
        <a-pagination
          v-if="promptPage.total > 0"
          class="pane-pager-top"
          size="small"
          :current="promptPage.current"
          :total="promptPage.total"
          :page-size="pageSize"
          :show-size-changer="false"
          :hide-on-single-page="true"
          @change="(p: number) => $emit('prompt-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="prompts.length">
            <AssemblyDragChip
              v-for="row in prompts"
              :key="String(row.promptKey ?? row.id)"
              :payload="{ kind: 'prompt', data: row }"
              :title="row.promptTitle || row.promptKey || ''"
              :subtitle="row.promptKey"
              badge="Prompt"
              @drag-start="$emit('dragStart', $event)"
              @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">暂无数据</div>
        </div>
      </a-tab-pane>
      
      <!-- 工具Tab -->
      <a-tab-pane key="tools" tab="工具">
        <div class="search-row">
          <a-input
            v-model:value="kwTool"
            size="middle"
            placeholder="工具名 / Tool Key"
            allow-clear
            class="search-input"
            @pressEnter="$emit('searchTool')"
          >
            <template #prefix><SearchOutlined class="search-ico" /></template>
          </a-input>
          <a-button type="primary" class="search-btn search-btn-tool" @click="$emit('searchTool')">
            <template #icon><SearchOutlined /></template>
          </a-button>
        </div>
        <a-pagination
          v-if="toolPage.total > 0"
          class="pane-pager-top"
          size="small"
          :current="toolPage.current"
          :total="toolPage.total"
          :page-size="pageSize"
          :show-size-changer="false"
          :hide-on-single-page="true"
          @change="(p: number) => $emit('tool-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="tools.length">
            <AssemblyDragChip
              v-for="row in tools"
              :key="String(row.toolKey ?? row.id)"
              :payload="{ kind: 'tool', data: row }"
              :title="row.toolName || row.toolKey || ''"
              :subtitle="row.toolKey"
              badge="Tool"
              @drag-start="$emit('dragStart', $event)"
              @drag-end="$emit('dragEnd')"
            />
          </template>
          <div v-else class="palette-empty">暂无数据</div>
        </div>
      </a-tab-pane>
      
      <!-- MCP Tab -->
      <a-tab-pane key="mcps" tab="MCP">
        <div class="search-row">
          <a-input
            v-model:value="kwMcp"
            size="middle"
            placeholder="服务名 / MCP Key"
            allow-clear
            class="search-input"
            @pressEnter="$emit('searchMcp')"
          >
            <template #prefix><SearchOutlined class="search-ico" /></template>
          </a-input>
          <a-button type="primary" class="search-btn search-btn-mcp" @click="$emit('searchMcp')">
            <template #icon><SearchOutlined /></template>
          </a-button>
        </div>
        <a-pagination
          v-if="mcpPage.total > 0"
          class="pane-pager-top"
          size="small"
          :current="mcpPage.current"
          :total="mcpPage.total"
          :page-size="pageSize"
          :show-size-changer="false"
          :hide-on-single-page="true"
          @change="(p: number) => $emit('mcp-page', p)"
        />
        <div class="chip-scroll">
          <template v-if="mcps.length">
            <AssemblyDragChip
              v-for="row in mcps"
              :key="String(row.mcpKey ?? row.id)"
              :payload="{ kind: 'mcp', data: row }"
              :title="row.serverName || row.mcpKey || ''"
              :subtitle="row.mcpKey"
              badge="MCP"
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
import { ref } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
import type { AiPrompt } from '@/api/aiPrompt'
import type { AssemblyDragPayload } from './assemblyTypes'
import AssemblyDragChip from './AssemblyDragChip.vue'

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

.search-btn-tool {
  background: #1890ff !important;
  border-color: #1890ff !important;
}

.search-btn-mcp {
  background: #52c41a !important;
  border-color: #52c41a !important;
}

.search-btn-prompt {
  background: #722ed1 !important;
  border-color: #722ed1 !important;
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
