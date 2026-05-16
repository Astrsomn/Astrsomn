<template>
  <header class="agent-config-header">
    <div class="header-left">
      <div class="header-logo">
        <img alt="Logo" class="logo-img" src="https://api.dicebear.com/7.x/shapes/svg?seed=Astrsomn">
        <span class="logo-text">Astrsomn</span>
      </div>
      <div class="header-divider"></div>
      <span class="header-title">Agent 配置</span>
    </div>
    <div class="header-right">
      <a-button class="cancel-btn" type="text" @click="emit('back')">取消</a-button>
      <a-button :loading="submitting" class="save-btn" type="primary" @click="emit('save')">
        保存更改
      </a-button>
    </div>
  </header>
</template>

<script lang="ts" setup>
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
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  background: var(--ac-glass-bg);
  backdrop-filter: var(--ac-glass-backdrop);
  border-bottom: 1px solid var(--ac-glass-border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-img {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.header-divider {
  width: 1px;
  height: 16px;
  background: var(--border-default);
}

.header-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cancel-btn {
  font-size: 13px;
  color: var(--text-secondary);
}

.cancel-btn:hover {
  color: var(--text-primary);
}

.save-btn {
  font-size: 13px;
  font-weight: 600;
  padding: 6px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}
</style>
