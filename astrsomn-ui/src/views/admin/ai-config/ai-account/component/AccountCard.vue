<template>
  <div
      :class="{
      'is-selected': selected,
    }"
      class="account-card-400"
  >
    <div class="card-header-status">
      <div :class="account.envCode?.toLowerCase() || 'unset'" class="status-chip">
        <span class="status-dot"></span>
        <span class="status-text">{{ account.envCode || 'UNSET' }}</span>
      </div>
      <div class="header-checkbox">
        <a-checkbox :checked="selected" @change="onCheckboxChange"/>
      </div>
    </div>

    <div class="card-content">
      <div class="avatar-section">
        <div class="avatar-glow">
          <CustomerServiceOutlined/>
        </div>
        <div class="live-badge" title="在线">
          <span class="live-dot"></span>
        </div>
      </div>

      <h3 :title="account.accountName" class="title">
        {{ account.accountName || 'AI 助手实例' }}
      </h3>

      <div class="token-capsule">
        <TransactionOutlined class="token-icon"/>
        <span class="token-num">{{ formatTokens(account.accountTokens).split('.')[0] }}</span>
        <span v-if="formatTokens(account.accountTokens).includes('.')" class="token-decimal">
          .{{ formatTokens(account.accountTokens).split('.')[1] }}
        </span>
        <span class="token-unit">Tokens</span>
      </div>

      <div class="description-box">
        <div class="model-label">已启用的模型</div>
        <div class="model-tags">
          <template v-if="usedModels.length > 0">
            <span v-for="m in usedModels.slice(0, 3)" :key="m" class="model-chip">
              {{ m }}
            </span>
            <span v-if="usedModels.length > 3" class="more-text" @click="emitShowModels">
              +{{ usedModels.length - 3 }}
            </span>
          </template>
          <span v-else class="empty-text">暂未关联模型</span>
          <button class="manage-link" @click.stop="emitShowModels">
            管理
            <RightOutlined style="font-size: 10px;"/>
          </button>
        </div>
      </div>
    </div>

    <div class="card-footer-action">
      <div class="user-meta">
        <user-outlined class="meta-icon"/>
        <span class="meta-info">{{ account.createUser || 'Sys' }}</span>
        <span class="divider">/</span>
        <span class="meta-info">{{ formatTime(account.createTime) }}</span>
      </div>

      <div class="action-group">
        <button class="action-circle-btn" @click="onEdit">
          <edit-outlined/>
        </button>
        <a-popconfirm title="确定要释放该助手吗？" @confirm="onDelete">
          <button class="action-circle-btn delete">
            <delete-outlined/>
          </button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  CustomerServiceOutlined,
  DeleteOutlined,
  EditOutlined,
  RightOutlined,
  TransactionOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

interface AiAccount {
  id?: number | string;
  accountName?: string;
  envCode?: string;
  apiKey?: string;
  apiSecret?: string;
  accountTokens?: number | null;
  createTime?: string;
  createUser?: string;
  usedModelNames?: string;
  usedModelKeys?: string;
}

const props = withDefaults(defineProps<{ account: AiAccount; selected?: boolean }>(), {
  selected: false
})
const emit = defineEmits(['edit', 'delete', 'show-models', 'toggle'])

const formatTokens = (t?: number | null) => {
  if (t == null) return '0'
  return t >= 1000 ? (t / 1000).toFixed(1) + 'k' : t.toString()
}
const formatTime = (t?: string) => t ? t.split('T')[0] : 'N/A'

const usedModels = computed(() => {
  const raw = props.account.usedModelNames || props.account.usedModelKeys || ''
  return raw.split(',').map(s => s.trim()).filter(Boolean)
})

const onEdit = () => emit('edit', props.account)
const onDelete = () => props.account.id && emit('delete', props.account.id)
const emitShowModels = () => emit('show-models', props.account)
const onToggle = (checked: boolean) => {
  if (props.account.id == null) return
  emit('toggle', props.account.id, checked)
}
const onCheckboxChange = (e: { target?: { checked?: boolean } }) => {
  onToggle(Boolean(e?.target?.checked))
}
</script>

<style scoped>
.account-card-400 {
  --primary-color: var(--primary);
  --text-main: var(--text-primary);
  --text-muted: var(--text-muted);
  --card-bg: var(--bg-card);
  --card-border-subtle: var(--border-subtle);
  --card-border-default: var(--border-default);

  width: 100%;
  max-width: 320px;
  min-height: 400px;
  background: var(--card-bg);
  border-radius: 32px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  border: 1px solid var(--card-border-default);
}

.account-card-400:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.account-card-400.is-selected {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--primary) 28%, transparent);
}

.card-header-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.status-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: var(--bg-elevated);
  border-radius: 100px;
}

.status-chip.prod,
.status-chip.dev {
  background: rgba(59, 130, 246, 0.1);
}

.status-chip.prod .status-dot,
.status-chip.dev .status-dot {
  background: var(--primary);
}

.status-chip.prod .status-text,
.status-chip.dev .status-text {
  color: var(--primary);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-muted);
}

.status-text {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-muted);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.avatar-section {
  position: relative;
  margin-bottom: 20px;
}

.avatar-glow {
  width: 64px;
  height: 64px;
  background: var(--primary-gradient);
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
  border: 1px solid var(--border-default);
}

.live-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 18px;
  height: 18px;
  background: var(--card-bg);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--card-bg);
}

.live-dot {
  width: 8px;
  height: 8px;
  background: var(--success);
  border-radius: 50%;
  animation: live-pulse 2s infinite;
}

.title {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-main);
  margin: 0 0 16px;
  letter-spacing: -0.5px;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.token-capsule {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
  padding: 8px 20px;
  background: var(--bg-elevated);
  border-radius: 100px;
  border: 1px solid var(--border-default);
  margin-bottom: 24px;
  transition: all 0.2s;
}

.token-capsule:hover {
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.token-icon {
  font-size: 13px;
  color: var(--primary-color);
  margin-right: 6px;
}

.token-num {
  font-size: 22px;
  font-weight: 800;
  line-height: 1;
  font-family: 'Poppins', sans-serif;
  color: var(--text-main);
}

.token-decimal {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-main);
  opacity: 0.8;
}

.token-unit {
  font-size: 11px;
  font-weight: 600;
  margin-left: 2px;
  color: var(--text-muted);
}

.description-box {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.model-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.model-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  justify-content: center;
}

.model-chip {
  background: var(--primary-hover);
  color: var(--primary);
  padding: 4px 12px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 500;
  border: 1px solid var(--primary);
}

.more-text {
  font-size: 11px;
  color: var(--text-muted);
  cursor: pointer;
  font-weight: 600;
}

.empty-text {
  font-size: 12px;
  color: var(--text-muted);
  font-style: italic;
}

.manage-link {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  background: none;
  border: none;
  color: var(--primary-color);
  font-size: 11px;
  cursor: pointer;
  padding: 0;
  margin-left: auto;
  font-weight: 500;
  transition: all 0.2s;
}

.manage-link:hover {
  opacity: 0.7;
}

.card-footer-action {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.meta-icon {
  font-size: 12px;
  color: var(--text-muted);
}

.meta-info {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
}

.divider {
  color: var(--border-default);
  font-size: 10px;
}

.action-group {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.action-circle-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid var(--border-default);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.action-circle-btn:hover {
  background: var(--primary-color);
  color: #fff;
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.action-circle-btn.delete:hover {
  background: var(--error);
  border-color: var(--error);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}

@keyframes live-pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }
  70% {
    box-shadow: 0 0 0 4px rgba(16, 185, 129, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}
</style>