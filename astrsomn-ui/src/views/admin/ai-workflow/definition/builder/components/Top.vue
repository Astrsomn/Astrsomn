<template>
  <header class="builder-header">
    <div class="header-left">
      <button type="button" class="back-icon-btn" @click="emit('back')">
        <ArrowLeftOutlined />
      </button>
      <div class="title-wrap">
        <a-input
          :value="workflowName"
          class="title-input"
          placeholder="请输入流程名称"
          @update:value="onWorkflowNameChange"
        />
        <span>{{ subtitle }}</span>
      </div>
    </div>
    <div class="header-right">
      <a-tooltip title="保存草稿">
        <a-button class="icon-btn save-btn" shape="circle" :loading="saving" @click="emit('save')">
          <template #icon><SaveOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="调试运行">
        <a-button class="icon-btn debug-btn" shape="circle" disabled>
          <template #icon><BugOutlined /></template>
        </a-button>
      </a-tooltip>
      <a-tooltip title="发布流程">
        <a-button class="icon-btn publish-btn" shape="circle" disabled>
          <template #icon><RocketOutlined /></template>
        </a-button>
      </a-tooltip>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ArrowLeftOutlined, BugOutlined, RocketOutlined, SaveOutlined } from '@ant-design/icons-vue'

defineProps<{
  workflowName: string
  subtitle: string
  saving?: boolean
}>()

const emit = defineEmits<{
  back: []
  save: []
  'update:workflowName': [value: string]
}>()

const onWorkflowNameChange = (value: string) => {
  emit('update:workflowName', value)
}
</script>

<style scoped>
.builder-header {
  height: 64px;
  border: 1px solid #eef2f7;
  border-radius: 12px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.back-icon-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #d9e1ec;
  background-color: #fff;
  color: #334155;
  border-radius: 999px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.back-icon-btn:hover {
  border-color: #1677ff;
  color: #1677ff;
  transform: translateX(-2px);
}

.title-wrap {
  min-width: 320px;
}

.title-input {
  width: 360px;
}

.title-wrap span {
  display: block;
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
}

.header-right {
  display: flex;
  gap: 10px;
}

.icon-btn {
  border: none;
  color: #fff;
}

.save-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.debug-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.publish-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.icon-btn:disabled {
  opacity: 0.55;
  color: #fff;
}
</style>
