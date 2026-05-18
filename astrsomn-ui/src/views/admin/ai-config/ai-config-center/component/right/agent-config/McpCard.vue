<template>
  <div class="mcp-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <ApiOutlined/>
        </div>
        <h3 class="card-title">MCP 服务</h3>
      </div>
      <button class="add-btn" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="mcp-list">
      <div v-for="m in mcps" :key="m.mcpKey" class="mcp-tag">
        <span class="mcp-letter">{{ (m.serverName || m.mcpKey || 'M').charAt(0).toUpperCase() }}</span>
        <span class="mcp-name">{{ m.serverName || m.mcpKey }}</span>
        <CloseOutlined class="mcp-close" @click.stop="emit('remove', m.mcpKey!)"/>
      </div>
      <div v-if="!mcps.length" class="empty-hint">暂未绑定 MCP</div>
    </div>

    <McpSelectorDrawer
      :open="drawerOpen"
      :selected-keys="selectedMcpKeys"
      @update:open="drawerOpen = $event"
      @add="emit('add', $event)"
      @remove="emit('remove', $event)"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { ApiOutlined, CloseOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { type AiMcp } from '@/api/aiMcp.ts'
import McpSelectorDrawer from '@/views/admin/ai-config/ai-mcp/selector/McpSelectorDrawer.vue'

const props = defineProps<{
  mcps: AiMcp[]
}>()

const emit = defineEmits<{
  (e: 'add', mcp: AiMcp): void
  (e: 'remove', mcpKey: string): void
}>()

const drawerOpen = ref(false)

const selectedMcpKeys = computed(() =>
  props.mcps.map((m) => m.mcpKey).filter(Boolean) as string[],
)
</script>

<style scoped>
.mcp-card {
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.mcp-card:hover {
  border-color: var(--ab-hover-line, #3b82f6);
  box-shadow: var(--ab-hover-shadow, 0 0 15px rgba(59, 130, 246, 0.15));
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  background: #f3e8ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9333ea;
}

.icon-badge .anticon {
  font-size: 16px;
}

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: #334155;
  margin: 0;
}

.add-btn {
  width: 28px;
  height: 28px;
  background: #faf5ff;
  color: #9333ea;
  border: 1px solid #e9d5ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #f3e8ff;
  color: #7e22ce;
}

.add-btn .anticon {
  font-size: 14px;
}

.mcp-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.mcp-tag {
  padding: 4px 8px;
  background: #faf5ff;
  color: #9333ea;
  border: 1px solid #e9d5ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  font-weight: 600;
}

.mcp-letter {
  font-size: 10px;
}

.mcp-name {
  font-size: 11px;
  font-weight: bold;
}

.mcp-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.mcp-close:hover {
  color: #ef4444;
}

.empty-hint {
  font-size: 12px;
  color: #94a3b8;
  padding: 4px 0;
}
</style>
