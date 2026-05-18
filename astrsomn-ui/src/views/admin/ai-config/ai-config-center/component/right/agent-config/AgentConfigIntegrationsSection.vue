<template>
  <div class="integrations-section">
    <div class="section-header">
      <div class="section-header-icon">
        <AppstoreOutlined />
      </div>
      <div class="section-header-text">
        <h3 class="section-title">扩展集成</h3>
        <p class="section-desc">连接工具、知识库和 MCP 节点</p>
      </div>
    </div>

    <div class="integrations-grid">
      <!-- 扩展工具 -->
      <div class="integration-card" @click="triggerToolAdd">
        <div class="card-icon-wrapper icon-blue">
          <ToolOutlined />
        </div>
        <div class="card-content">
          <h4 class="card-title">扩展工具</h4>
          <p class="card-count">
            <span class="count-number">{{ tools.length }}</span>
            <span class="count-label">个工具已绑定</span>
          </p>
        </div>
        <div class="card-action">
          <PlusOutlined />
        </div>
        <div ref="toolCardRef" class="sr-card">
          <ToolCard :tools="tools" @add="emit('tool-add', $event)" @remove="emit('tool-remove', $event)" />
        </div>
      </div>

      <!-- 知识库 (RAG) -->
      <div class="integration-card" @click="triggerRagAdd">
        <div class="card-icon-wrapper icon-green">
          <DatabaseOutlined />
        </div>
        <div class="card-content">
          <h4 class="card-title">知识库</h4>
          <p class="card-count">
            <span class="count-number count-green">{{ knowledgeKeys.length }}</span>
            <span class="count-label">个向量库已关联</span>
          </p>
        </div>
        <div class="card-action action-green">
          <SettingOutlined />
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
      <div class="integration-card" @click="triggerMcpAdd">
        <div class="card-icon-wrapper icon-purple">
          <ApiOutlined />
        </div>
        <div class="card-content">
          <h4 class="card-title">MCP 节点</h4>
          <p class="card-count">
            <span class="count-number count-purple">{{ mcps.length }}</span>
            <span class="count-label">{{ mcps.length ? '个节点已连接' : '暂无端点' }}</span>
          </p>
        </div>
        <div class="card-action action-purple">
          <PlusOutlined />
        </div>
        <div ref="mcpCardRef" class="sr-card">
          <McpCard :mcps="mcps" @add="emit('mcp-add', $event)" @remove="emit('mcp-remove', $event)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ApiOutlined, DatabaseOutlined, PlusOutlined, SettingOutlined, ToolOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import type { AiTool } from '@/api/aiTool.ts'
import type { AiMcp } from '@/api/aiMcp.ts'
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
.integrations-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

/* Section header */
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-header-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.04);
  color: var(--text-muted);
  font-size: 16px;
  flex-shrink: 0;
}

.section-header-text {
  flex: 1;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.4;
}

.section-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.4;
}

/* Grid layout */
.integrations-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  flex: 1;
  align-content: start;
}

@media (max-width: 960px) {
  .integrations-grid {
    grid-template-columns: 1fr;
  }
}

/* Integration card */
.integration-card {
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: relative;
  overflow: hidden;
}

.integration-card:hover {
  border-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

/* Icon wrapper */
.card-icon-wrapper {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  transition: transform 0.2s;
}

.integration-card:hover .card-icon-wrapper {
  transform: scale(1.05);
}

.icon-blue {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.icon-green {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.icon-purple {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

/* Card content */
.card-content {
  flex: 1;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 6px;
}

.card-count {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin: 0;
}

.count-number {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
  font-variant-numeric: tabular-nums;
}

.count-number.count-green {
  color: #10b981;
}

.count-number.count-purple {
  color: #8b5cf6;
}

.count-label {
  font-size: 12px;
  color: var(--text-muted);
}

/* Action button */
.card-action {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  background: rgba(0, 0, 0, 0.04);
  color: var(--text-muted);
  transition: all 0.2s;
}

.integration-card:hover .card-action {
  background: #3b82f6;
  color: #fff;
}

.integration-card:hover .action-green {
  background: #10b981;
}

.integration-card:hover .action-purple {
  background: #8b5cf6;
}

/* Hidden child card */
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
