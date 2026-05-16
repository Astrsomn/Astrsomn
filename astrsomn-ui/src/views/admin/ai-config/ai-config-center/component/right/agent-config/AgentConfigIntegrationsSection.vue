<template>
  <div class="integrations-grid">
    <!-- 扩展工具 -->
    <div class="integration-card glass-card" @click="triggerToolAdd">
      <div class="card-body">
        <div class="card-icon icon-blue">
          <ToolOutlined/>
        </div>
        <div class="card-text">
          <h4 class="card-title">扩展工具</h4>
          <p class="card-subtitle">已绑定 {{ tools.length }} 个插件</p>
        </div>
      </div>
      <div class="card-action action-blue">
        <PlusOutlined/>
      </div>
      <div ref="toolCardRef" class="sr-card">
        <ToolCard :tools="tools" @add="emit('tool-add', $event)" @remove="emit('tool-remove', $event)"/>
      </div>
    </div>

    <!-- 知识库 (RAG) -->
    <div class="integration-card glass-card has-accent" @click="triggerRagAdd">
      <div class="accent-stripe accent-green"></div>
      <div class="card-body">
        <div class="card-icon icon-green">
          <DatabaseOutlined/>
        </div>
        <div class="card-text">
          <h4 class="card-title">知识库 (RAG)</h4>
          <p class="card-subtitle subtitle-green">已关联 {{ knowledgeKeys.length }} 个向量库</p>
        </div>
      </div>
      <div class="card-action action-green">
        <SettingOutlined/>
      </div>
      <div ref="ragCardRef" class="sr-card">
        <RagCard
            :knowledge-keys="knowledgeKeys"
            @add="emit('knowledge-add', $event)"
            @remove="emit('knowledge-remove', $event)"
        />
      </div>
    </div>

    <!-- MCP 节点 -->
    <div class="integration-card glass-card" @click="triggerMcpAdd">
      <div class="card-body">
        <div class="card-icon icon-orange">
          <ApiOutlined/>
        </div>
        <div class="card-text">
          <h4 class="card-title">MCP 节点</h4>
          <p class="card-subtitle">{{ mcps.length ? `已绑定 ${mcps.length} 个节点` : '暂无协议端点' }}</p>
        </div>
      </div>
      <div class="card-action action-orange">
        <PlusOutlined/>
      </div>
      <div ref="mcpCardRef" class="sr-card">
        <McpCard :mcps="mcps" @add="emit('mcp-add', $event)" @remove="emit('mcp-remove', $event)"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {ApiOutlined, DatabaseOutlined, PlusOutlined, SettingOutlined, ToolOutlined} from '@ant-design/icons-vue'
import type {AiTool} from '@/api/aiTool.ts'
import type {AiMcp} from '@/api/aiMcp.ts'
import ToolCard from '@/views/admin/ai-config/builder/component/left-center/ToolCard.vue'
import McpCard from '@/views/admin/ai-config/builder/component/left-center/McpCard.vue'
import RagCard from '@/views/admin/ai-config/builder/component/left-center/RagCard.vue'

defineProps<{
  tools: AiTool[]
  mcps: AiMcp[]
  knowledgeKeys: string[]
}>()

const emit = defineEmits<{
  'tool-add': [tool: AiTool]
  'tool-remove': [toolKey: string]
  'mcp-add': [mcp: AiMcp]
  'mcp-remove': [mcpKey: string]
  'knowledge-add': [key: string]
  'knowledge-remove': [key: string]
}>()

const toolCardRef = ref<HTMLElement | null>(null)
const mcpCardRef = ref<HTMLElement | null>(null)
const ragCardRef = ref<HTMLElement | null>(null)

function triggerChildAddButton(parentEl: HTMLElement | null) {
  if (!parentEl) return
  const btn = parentEl.querySelector('.add-btn, .action-btn.primary') as HTMLElement | null
  btn?.click()
}

function triggerToolAdd() {
  triggerChildAddButton(toolCardRef.value)
}

function triggerMcpAdd() {
  triggerChildAddButton(mcpCardRef.value)
}

function triggerRagAdd() {
  triggerChildAddButton(ragCardRef.value)
}
</script>

<style scoped>
.integrations-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

@media (max-width: 960px) {
  .integrations-grid {
    grid-template-columns: 1fr;
  }
}

/* Glass card base */
.glass-card {
  background: var(--ac-glass-bg);
  backdrop-filter: var(--ac-glass-backdrop);
  -webkit-backdrop-filter: var(--ac-glass-backdrop);
  border: 1px solid var(--ac-glass-border);
  border-radius: 12px;
  box-shadow: var(--ac-glass-shadow);
}

.integration-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}

.integration-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--ac-glass-shadow), 0 8px 24px rgba(0, 0, 0, 0.06);
}

/* Accent stripe for RAG card */
.accent-stripe {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
}

.accent-green {
  background: #10b981;
}

/* Card body */
.card-body {
  display: flex;
  align-items: center;
  gap: 14px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.icon-blue {
  background: rgba(59, 130, 246, 0.1);
  color: var(--primary);
}

.icon-green {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.icon-orange {
  background: rgba(249, 115, 22, 0.1);
  color: #f97316;
}

.card-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.card-subtitle {
  font-size: 11px;
  color: var(--text-muted);
  margin: 0;
}

.subtitle-green {
  color: #10b981;
  font-weight: 500;
}

/* Action button */
.card-action {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  flex-shrink: 0;
  transition: all 0.2s;
}

.action-blue {
  background: var(--bg-elevated);
  color: var(--text-muted);
}

.integration-card:hover .action-blue {
  background: var(--primary);
  color: #fff;
}

.action-green {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.action-orange {
  background: var(--bg-elevated);
  color: var(--text-muted);
}

.integration-card:hover .action-orange {
  background: #f97316;
  color: #fff;
}

/* Hidden child card for drawer functionality */
.sr-card {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
