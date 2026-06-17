<template>
  <div class="rag-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <DatabaseOutlined/>
        </div>
        <h3 class="card-title">{{ t.agent.knowledgeBaseLabel }}</h3>
      </div>
      <button class="action-btn primary" :title="t.agent.selectKnowledgeBase" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="kb-list">
      <div v-for="entry in knowledgeKeys" :key="entry.key" class="kb-tag" :class="{ 'kb-tag--orphaned': entry._orphaned }">
        <a-tooltip v-if="entry._orphaned" :title="t.agent.toolNotFound">
          <ExclamationCircleOutlined class="kb-warn-icon" />
        </a-tooltip>
        <span class="kb-name">{{ entry.key }}</span>
        <CloseOutlined class="kb-close" @click.stop="emit('remove', entry.key)"/>
      </div>
      <div v-if="!knowledgeKeys.length" class="empty-hint">{{ t.agent.noKnowledgeBase }}</div>
    </div>

    <VecStoreSelectorDrawer
      :open="drawerOpen"
      :selected-keys="activeKeys"
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
import {CloseOutlined, DatabaseOutlined, ExclamationCircleOutlined, PlusOutlined} from '@ant-design/icons-vue'
import VecStoreSelectorDrawer from '@/views/admin/ai-vector/vector-center/form/VecStoreSelectorDrawer.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const props = defineProps<{
  knowledgeKeys: { key: string; _orphaned?: boolean }[]
}>()

const emit = defineEmits<{
  (e: 'add', kbKey: string, title?: string): void
  (e: 'remove', kbKey: string): void
}>()

const t = usePageTranslation('ai-config-center')

const drawerOpen = ref(false)

const activeKeys = computed(() => props.knowledgeKeys.map((e) => e.key))

const orphanedKeys = computed(() =>
  props.knowledgeKeys.filter((e) => e._orphaned).map((e) => e.key),
)
</script>

<style scoped>
.rag-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background: var(--bg-card);
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--border-default);
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--shadow-card);
  padding: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.rag-card:hover {
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
  background: #d1fae5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #059669;
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

.action-btn {
  width: 28px;
  height: 28px;
  background: transparent;
  color: #94a3b8;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #2563eb;
}

.action-btn.primary {
  background: var(--success);
  color: var(--text-heading);
  box-shadow: 0 1px 3px color-mix(in srgb, var(--success) 35%, transparent);
}

.action-btn.primary:hover {
  color: var(--text-heading);
  background: var(--success);
}

.action-btn .anticon {
  font-size: 12px;
}

.kb-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.kb-tag {
  padding: 4px 8px;
  background: #ecfdf5;
  color: #059669;
  border: 1px solid #a7f3d0;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
}

.kb-name {
  font-size: 11px;
}

.kb-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.kb-close:hover {
  color: var(--error);
}

/* ── 孤立条目（知识库已删除） ── */
.kb-tag--orphaned {
  background: color-mix(in srgb, var(--error) 8%, transparent) !important;
  border-color: color-mix(in srgb, var(--error) 25%, transparent) !important;
  color: var(--error) !important;
}

.kb-warn-icon {
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
