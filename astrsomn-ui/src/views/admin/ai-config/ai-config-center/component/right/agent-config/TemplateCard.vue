<template>
  <div class="template-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <FileTextOutlined/>
        </div>
        <h3 class="card-title">{{ t.agent.templateLabel }}</h3>
      </div>
      <button class="add-btn" @click.stop="drawerOpen = true">
        <PlusOutlined/>
      </button>
    </div>
    <div class="tpl-list">
      <div v-for="k in templateKeys" :key="k" class="tpl-tag">
        <span class="tpl-name">{{ k }}</span>
        <CloseOutlined class="tpl-close" @click.stop="emit('remove', k)"/>
      </div>
      <div v-if="!templateKeys.length" class="empty-hint">{{ t.agent.noTemplate }}</div>
    </div>

    <TemplateSelectorDrawer
      :open="drawerOpen"
      :selected-keys="templateKeys"
      @update:open="drawerOpen = $event"
      @add="emit('add', $event)"
      @remove="emit('remove', $event)"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {CloseOutlined, FileTextOutlined, PlusOutlined} from '@ant-design/icons-vue'
import TemplateSelectorDrawer from '@/views/admin/ai-config/ai-template/selector/TemplateSelectorDrawer.vue'
import {usePageTranslation} from '@/locales/pages.ts'

defineProps<{
  templateKeys: string[]
}>()

const emit = defineEmits<{
  (e: 'add', key: string): void
  (e: 'remove', key: string): void
}>()

const t = usePageTranslation('ai-config-center')

const drawerOpen = ref(false)
</script>

<style scoped>
.template-card {
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

.template-card:hover {
  border-color: #f97316;
  box-shadow: 0 0 15px color-mix(in srgb, #f97316 15%, transparent);
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
  background: color-mix(in srgb, #f97316 10%, transparent);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f97316;
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
  background: color-mix(in srgb, #f97316 8%, transparent);
  color: #f97316;
  border: 1px solid color-mix(in srgb, #f97316 20%, transparent);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: color-mix(in srgb, #f97316 15%, transparent);
  color: #f97316;
}

.add-btn .anticon {
  font-size: 14px;
}

.tpl-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
}

.tpl-tag {
  padding: 4px 8px;
  background: color-mix(in srgb, #f97316 8%, transparent);
  color: #f97316;
  border: 1px solid color-mix(in srgb, #f97316 20%, transparent);
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
}

.tpl-name {
  font-size: 11px;
}

.tpl-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.tpl-close:hover {
  color: var(--error);
}

.empty-hint {
  font-size: 12px;
  color: var(--text-muted);
  padding: 4px 0;
}
</style>
