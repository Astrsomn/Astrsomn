<template>
  <div class="tool-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <ToolOutlined/>
        </div>
        <h3 class="card-title">{{ t.agent.extensionToolsLabel }}</h3>
      </div>
      <button class="add-btn" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="tool-list">
      <div v-for="tool in tools" :key="tool.toolKey" class="tool-tag" :class="{ 'tool-tag--orphaned': tool._orphaned }">
        <a-tooltip v-if="tool._orphaned" :title="t.agent.toolNotFound">
          <ExclamationCircleOutlined class="tool-warn-icon" />
        </a-tooltip>
        <span class="tool-letter">{{ (tool.toolName || tool.toolKey || 'T').charAt(0).toUpperCase() }}</span>
        <span class="tool-name">{{ tool.toolName || tool.toolKey }}</span>
        <CloseOutlined class="tool-close" @click.stop="emit('remove', tool.toolKey || '')"/>
      </div>
      <div v-if="!tools.length" class="empty-hint">{{ t.agent.noToolsBound }}</div>
    </div>

    <ToolSelectorDrawer
      :open="drawerOpen"
      :selected-keys="selectedToolKeys"
      :orphaned-keys="orphanedKeys"
      :orphaned-hint="t.agent.toolNotFound"
      @update:open="drawerOpen = $event"
      @add="emit('add', $event)"
      @remove="emit('remove', $event)"
    />
  </div>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {CloseOutlined, ExclamationCircleOutlined, PlusOutlined, ToolOutlined} from '@ant-design/icons-vue'
import {type AiTool} from '@/api/aiTool.ts'
import ToolSelectorDrawer from '@/views/admin/ai-config/ai-tool/selector/ToolSelectorDrawer.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  tools: AiTool[]
}>()

const emit = defineEmits<{
  (e: 'add', tool: AiTool): void
  (e: 'remove', toolKey: string): void
}>()

const t = usePageTranslation('ai-config-center')

const drawerOpen = ref(false)

const selectedToolKeys = computed(() =>
  props.tools.map((t) => t.toolKey).filter(Boolean) as string[],
)

const orphanedKeys = computed(() =>
  props.tools.filter((t) => (t as any)._orphaned).map((t) => t.toolKey).filter(Boolean) as string[],
)
</script>

<style scoped>
.tool-card {
  background: var(--bg-card);
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--border-default);
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--shadow-card);
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.tool-card:hover {
  border-color: var(--primary);
  box-shadow: 0 0 15px color-mix(in srgb, var(--primary) 15%, transparent);
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
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
}

.icon-badge .anticon {
  font-size: 16px;
}

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
  margin: 0;
}

.add-btn {
  width: 28px;
  height: 28px;
  background: color-mix(in srgb, var(--primary) 8%, transparent);
  color: var(--primary);
  border: 1px solid color-mix(in srgb, var(--primary) 20%, transparent);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: color-mix(in srgb, var(--primary) 15%, transparent);
  color: var(--primary);
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
  background: color-mix(in srgb, var(--primary) 8%, transparent);
  color: var(--primary);
  border: 1px solid color-mix(in srgb, var(--primary) 20%, transparent);
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
  color: var(--error);
}

/* ── 孤立条目（工具已删除） ── */
.tool-tag--orphaned {
  background: color-mix(in srgb, var(--error) 8%, transparent) !important;
  border-color: color-mix(in srgb, var(--error) 25%, transparent) !important;
  color: var(--error) !important;
}

.tool-warn-icon {
  font-size: 11px;
  color: var(--error);
  flex-shrink: 0;
}

.empty-hint {
  font-size: 12px;
  color: var(--text-muted);
  padding: 4px 0;
}
</style>
