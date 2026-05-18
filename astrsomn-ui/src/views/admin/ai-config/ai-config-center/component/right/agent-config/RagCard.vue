<template>
  <div class="rag-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <DatabaseOutlined/>
        </div>
        <h3 class="card-title">知识库 (RAG)</h3>
      </div>
      <button class="action-btn primary" title="选择知识库" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="kb-list">
      <div v-for="k in knowledgeKeys" :key="k" class="kb-tag">
        <span class="kb-name">{{ k }}</span>
        <CloseOutlined class="kb-close" @click.stop="emit('remove', k)"/>
      </div>
      <div v-if="!knowledgeKeys.length" class="empty-hint">暂未关联知识库</div>
    </div>

    <VecStoreSelectorDrawer
      :open="drawerOpen"
      :selected-keys="knowledgeKeys"
      @update:open="drawerOpen = $event"
      @add="emit('add', $event)"
      @remove="emit('remove', $event)"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {CloseOutlined, DatabaseOutlined, PlusOutlined} from '@ant-design/icons-vue'
import VecStoreSelectorDrawer from '@/views/admin/ai-vector/vec-store/selector/VecStoreSelectorDrawer.vue'

defineProps<{
  knowledgeKeys: string[]
}>()

const emit = defineEmits<{
  (e: 'add', kbKey: string, title?: string): void
  (e: 'remove', kbKey: string): void
}>()

const drawerOpen = ref(false)
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
  background: #10b981;
  color: #fff;
  box-shadow: 0 1px 3px rgba(16, 185, 129, 0.35);
}

.action-btn.primary:hover {
  color: #fff;
  background: #059669;
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
  color: #ef4444;
}

.empty-hint {
  font-size: 12px;
  color: var(--text-muted);
  padding: 4px 0;
}
</style>
