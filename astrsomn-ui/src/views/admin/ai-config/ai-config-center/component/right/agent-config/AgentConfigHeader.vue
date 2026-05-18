<template>
  <header class="agent-config-header">
    <div class="header-left">
      <a-button class="back-btn" type="text" @click="emit('back')">
        <ArrowLeftOutlined />
      </a-button>
      <div class="header-divider"></div>
      <span class="header-title">智能体配置</span>
    </div>
    <div class="header-right">
      <a-button :loading="submitting" class="save-btn" type="primary" @click="emit('save')">
        <template #icon><SaveOutlined /></template>
        保存
      </a-button>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons-vue'

defineProps<{
  submitting: boolean
}>()

const emit = defineEmits<{
  back: []
  save: []
}>()
</script>

<style scoped>
.agent-config-header {
  position: sticky;
  top: 0;
  z-index: 50;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px) saturate(180%);
  -webkit-backdrop-filter: blur(12px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: var(--text-secondary);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.back-btn:hover {
  background: rgba(0, 0, 0, 0.04);
  color: var(--text-primary);
}

.header-divider {
  width: 1px;
  height: 16px;
  background: rgba(0, 0, 0, 0.08);
}

.header-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  letter-spacing: -0.01em;
  font-variant-numeric: tabular-nums;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.save-btn {
  height: 36px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.save-btn:active {
  transform: translateY(0);
}
</style>
