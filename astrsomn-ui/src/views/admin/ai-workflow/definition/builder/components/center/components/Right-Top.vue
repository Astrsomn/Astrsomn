<template>
  <div class="canvas-actions">
    <a-tooltip title="保存草稿">
      <a-button :loading="saving" class="action-btn save-btn" shape="circle" @click="$emit('save')">
        <template #icon>
          <SaveOutlined/>
        </template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="调试运行">
      <a-button class="action-btn debug-btn" disabled shape="circle">
        <template #icon>
          <BugOutlined/>
        </template>
      </a-button>
    </a-tooltip>
    <a-tooltip title="发布流程">
      <a-button class="action-btn publish-btn" disabled shape="circle">
        <template #icon>
          <RocketOutlined/>
        </template>
      </a-button>
    </a-tooltip>
  </div>
</template>

<script lang="ts" setup>
import {BugOutlined, RocketOutlined, SaveOutlined} from '@ant-design/icons-vue'

defineProps<{
  saving?: boolean
}>()

defineEmits<{
  save: []
}>()
</script>

<style scoped>
.canvas-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 6;
  display: flex;
  gap: 10px;
}

.action-btn {
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

.action-btn:disabled {
  opacity: 0.55;
}
</style>
