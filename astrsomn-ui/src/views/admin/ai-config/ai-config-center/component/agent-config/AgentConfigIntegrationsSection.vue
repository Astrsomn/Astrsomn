<template>
  <AgentConfigSectionShell :step="3" title="功能组件">
    <div class="integrations-layout">
      <div class="subsection">
        <h3 class="subsection-title">
          <span class="title-line"></span>
          外部集成 (Integrations)
          <span class="title-line"></span>
        </h3>
        <div class="integrations-grid builder-tool-mcp-skin">
          <ToolCard :tools="tools" @add="emit('tool-add', $event)" @remove="emit('tool-remove', $event)"/>
          <McpCard :mcps="mcps" @add="emit('mcp-add', $event)" @remove="emit('mcp-remove', $event)"/>
          <RagCard
              :knowledge-keys="knowledgeKeys"
              @add="emit('knowledge-add', $event)"
              @remove="emit('knowledge-remove', $event)"
          />
        </div>
      </div>
    </div>
  </AgentConfigSectionShell>
</template>

<script lang="ts" setup>
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'
import ToolCard from '@/views/admin/ai-config/builder/component/left-center/ToolCard.vue'
import McpCard from '@/views/admin/ai-config/builder/component/left-center/McpCard.vue'
import RagCard from '@/views/admin/ai-config/builder/component/left-center/RagCard.vue'
import AgentConfigSectionShell from './AgentConfigSectionShell.vue'

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
</script>

<style scoped>
.integrations-layout {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.subsection {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.subsection-title {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-line {
  flex: 1;
  height: 1px;
  background: var(--border-subtle);
}

.integrations-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  min-height: 0;
}

@media (max-width: 960px) {
  .integrations-grid {
    grid-template-columns: 1fr;
  }
}

.integrations-grid > * {
  min-width: 0;
}

.integrations-grid :deep(.tool-card),
.integrations-grid :deep(.mcp-card),
.integrations-grid :deep(.rag-card) {
  box-shadow: none;
}

.integrations-grid :deep(.tool-card:hover),
.integrations-grid :deep(.mcp-card:hover),
.integrations-grid :deep(.rag-card:hover) {
  border-color: var(--border-subtle);
  box-shadow: none;
}

.builder-tool-mcp-skin {
  --ab-glass-bg: var(--bg-card);
  --ab-glass-border: var(--border-subtle);
  --ab-glass-shadow: none;
  --ab-glass-radius: var(--radius-lg);
  --ab-glass-haze: 8px;
  --ab-hover-line: var(--border-subtle);
  --ab-hover-shadow: none;
}
</style>
