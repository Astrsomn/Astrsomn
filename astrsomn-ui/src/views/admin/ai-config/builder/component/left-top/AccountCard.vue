<template>
  <div class="node-connector">
    <div class="account-card" :class="{ 'has-account': account }">
      <div class="card-header">
        <div class="header-left">
          <div class="icon-badge">
            <SafetyCertificateOutlined />
          </div>
          <div class="header-info">
            <span class="card-label">账号</span>
            <span v-if="account?.accountKey" class="account-key-display">{{ account.accountKey }}</span>
          </div>
        </div>
      </div>
      <template v-if="account">
        <div class="card-title">{{ account.accountName }}</div>
        <div class="card-subtitle">
          <template v-if="account.usedModelCount">
            关联 {{ account.usedModelCount }} 个模型
          </template>
          <template v-else>余额充足</template>
        </div>
      </template>
      <template v-else>
        <div class="card-placeholder">请选择或添加账号</div>
      </template>
      <div class="card-icon">
        <DoubleRightOutlined />
      </div>
      <div class="card-overlay">
        <button class="overlay-btn" title="切换账号" @click.stop="emit('switch')">
          <SwapOutlined />
        </button>
        <button class="overlay-btn" title="添加新账号" @click.stop="emit('add')">
          <PlusOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { SafetyCertificateOutlined, SwapOutlined, PlusOutlined, DoubleRightOutlined } from '@ant-design/icons-vue'
import type { AiAccount } from '@/api/aiAccount'

interface Props {
  account?: AiAccount
}

defineProps<Props>()

const emit = defineEmits<{
  (e: 'add'): void
  (e: 'switch'): void
}>()
</script>

<style scoped>
.node-connector {
  flex: 1;
  position: relative;
}

.account-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-left: 4px solid #f97316;
  border-radius: var(--radius-xl);
  padding: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100px;
  display: flex;
  flex-direction: column;
}

.account-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);

}

.account-card.has-account {
  border-left-color: #f97316;
}

.account-card:not(.has-account) {
  border-left-color: var(--border-default);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-badge {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #f97316 0%, #fb923c 100%);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.25);
}

.icon-badge .anticon {
  font-size: 16px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-label {
  font-size: 10px;
  font-weight: bold;
  color: #f97316;
  text-transform: uppercase;
}

.account-key-display {
  font-size: 11px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.card-icon {
  color: var(--text-hint);
  font-size: 16px;
  position: absolute;
  top: 50%;
  right: 16px;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-weight: bold;
  font-size: 14px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.card-placeholder {
  font-size: 13px;
  color: var(--text-hint);
  padding: 8px 0;
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.account-card:hover .card-overlay {
  opacity: 1;
  pointer-events: auto;
}

.overlay-btn {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  color: var(--primary);
  cursor: pointer;
  transition: all 0.2s;
}

.overlay-btn:hover {
  transform: scale(1.1);
  color: var(--primary-light);
  background: var(--primary);
  color: white;
}

.overlay-btn .anticon {
  font-size: 16px;
}
</style>
