<template>
  <AgentConfigSectionShell :step="2" title="工具与知识">
    <div class="integrations-panel">
      <div class="integrations-row builder-tool-mcp-skin">
        <ToolCard :tools="tools" @add="emit('tool-add', $event)" @remove="emit('tool-remove', $event)" />
        <McpCard :mcps="mcps" @add="emit('mcp-add', $event)" @remove="emit('mcp-remove', $event)" />
        <RagCard
          :knowledge-keys="knowledgeKeys"
          @add="emit('knowledge-add', $event)"
          @remove="emit('knowledge-remove', $event)"
        />
      </div>
    </div>
  </AgentConfigSectionShell>
</template>

<script setup lang="ts">
import type { AiTool } from '@/api/aiTool'
import type { AiMcp } from '@/api/aiMcp'
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
.integrations-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  box-shadow: none;
}

.integrations-row {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  gap: 16px;
  min-height: 0;
}

.integrations-row > * {
  flex: 1;
  min-width: 0;
}

.integrations-row :deep(.tool-card),
.integrations-row :deep(.mcp-card),
.integrations-row :deep(.rag-card) {
  box-shadow: none;
}

.integrations-row :deep(.tool-card:hover),
.integrations-row :deep(.mcp-card:hover),
.integrations-row :deep(.rag-card:hover) {
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
