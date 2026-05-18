<template>
  <div class="tool-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <ToolOutlined/>
        </div>
        <h3 class="card-title">扩展插件 (Tools)</h3>
      </div>
      <button class="add-btn" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="tool-list">
      <div v-for="t in tools" :key="t.toolKey" class="tool-tag">
        <span class="tool-letter">{{ (t.toolName || t.toolKey || 'T').charAt(0).toUpperCase() }}</span>
        <span class="tool-name">{{ t.toolName || t.toolKey }}</span>
        <CloseOutlined class="tool-close" @click.stop="emit('remove', t.toolKey!)"/>
      </div>
      <div v-if="!tools.length" class="empty-hint">暂未绑定工具</div>
    </div>

    <ToolSelectorDrawer
      :open="drawerOpen"
      :selected-keys="selectedToolKeys"
      @update:open="drawerOpen = $event"
      @add="emit('add', $event)"
      @remove="emit('remove', $event)"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { CloseOutlined, PlusOutlined, ToolOutlined } from '@ant-design/icons-vue'
import { type AiTool } from '@/api/aiTool.ts'
import ToolSelectorDrawer from '@/views/admin/ai-config/ai-tool/selector/ToolSelectorDrawer.vue'

const props = defineProps<{
  tools: AiTool[]
}>()

const emit = defineEmits<{
  (e: 'add', tool: AiTool): void
  (e: 'remove', toolKey: string): void
}>()

const drawerOpen = ref(false)

const selectedToolKeys = computed(() =>
  props.tools.map((t) => t.toolKey).filter(Boolean) as string[],
)
</script>

<style scoped>
.tool-card {
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.tool-card:hover {
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
  background: #dbeafe;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
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
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #dbeafe;
  color: #1d4ed8;
}

.add-btn .anticon {
  font-size: 14px;
}

.tool-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.tool-tag {
  padding: 4px 8px;
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  font-weight: 600;
}

.tool-letter {
  font-size: 10px;
}

.tool-name {
  font-size: 11px;
  font-weight: bold;
}

.tool-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.tool-close:hover {
  color: #ef4444;
}

.empty-hint {
  font-size: 12px;
  color: #94a3b8;
  padding: 4px 0;
}
</style>
