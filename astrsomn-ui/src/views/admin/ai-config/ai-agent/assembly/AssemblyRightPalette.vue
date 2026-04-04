<template>
  <aside class="assembly-palette palette-right">
    <!-- Tab页切换 -->
    <a-tabs class="palette-tabs" v-model:activeKey="activeTab">
      <!-- Prompt Tab -->
      <a-tab-pane key="prompts" tab="Prompt">
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon" />
          <input
            v-model="kwPrompt"
            type="text"
            placeholder="Prompt名称 / Key"
            @keyup.enter="$emit('searchPrompt')"
          />
          <button type="button" class="search-submit-btn" @click="$emit('searchPrompt')">搜索</button>
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
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon" />
          <input
            v-model="kwTool"
            type="text"
            placeholder="工具名 / Tool Key"
            @keyup.enter="$emit('searchTool')"
          />
          <button type="button" class="search-submit-btn" @click="$emit('searchTool')">搜索</button>
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
        <div class="search-input-wrapper">
          <SearchOutlined class="search-icon" />
          <input
            v-model="kwMcp"
            type="text"
            placeholder="服务名 / MCP Key"
            @keyup.enter="$emit('searchMcp')"
          />
          <button type="button" class="search-submit-btn" @click="$emit('searchMcp')">搜索</button>
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
  padding-inline: 8px;
}

/* 与 InstanceList 一致的 pill 搜索条（侧栏全宽） */
.search-input-wrapper {
  width: 100%;
  height: 52px;
  background: #fff;
  border-radius: 26px;
  padding: 0 8px 0 16px;
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  flex-shrink: 0;
  box-shadow:
    0 1px 2px rgba(15, 23, 42, 0.06),
    0 4px 12px rgba(15, 23, 42, 0.08),
    0 12px 28px rgba(15, 23, 42, 0.06);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-input-wrapper:focus-within {
  border-color: #3b82f6;
  box-shadow:
    0 0 0 3px rgba(59, 130, 246, 0.22),
    0 4px 14px rgba(37, 99, 235, 0.2),
    0 14px 32px rgba(15, 23, 42, 0.12);
}

.search-icon {
  color: #3b82f6;
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
}

.search-submit-btn {
  flex-shrink: 0;
  background: linear-gradient(180deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.2) inset,
    0 2px 4px rgba(29, 78, 216, 0.35),
    0 6px 14px rgba(37, 99, 235, 0.28);
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
