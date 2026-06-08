<template>
  <div
      :class="{
      'is-selected': selected,
    }"
      class="account-card"
  >
    <div class="card-header">
      <div class="flex items-center gap-2.5">
        <div class="icon-wrapper">
          <CustomerServiceOutlined class="icon" />
        </div>
        <div class="truncate max-w-[120px]">
          <h4 class="title">{{ account.accountName || t.card.defaultName }}</h4>
          <p class="sub-title">{{ account.id || 'AC-XXXX' }}</p>
        </div>
      </div>
      <span :class="account.envCode?.toLowerCase() || 'unset'" class="status-tag">
        {{ account.envCode || 'UNSET' }}
      </span>
    </div>

    <div class="card-body">
      <span class="body-label flex items-center gap-1">
        <TransactionOutlined class="label-icon" />
        {{ t.card.tokens }}
      </span>
      <span class="body-value">{{ formatTokens(account.accountTokens) }}</span>
    </div>

    <div class="card-footer">
      <span class="status-indicator flex items-center gap-1">
        <span class="status-dot"></span>
        {{ account.envCode ? '已启用' : '未配置' }}
      </span>
      <div class="action-group">
        <button class="action-btn" @click="onEdit" :title="t.card.edit">
          <EditOutlined class="action-icon" />
        </button>
        <a-popconfirm :title="t.card.deleteConfirm" @confirm="onDelete">
          <button class="action-btn delete" :title="t.card.delete">
            <DeleteOutlined class="action-icon" />
          </button>
        </a-popconfirm>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  CustomerServiceOutlined,
  DeleteOutlined,
  EditOutlined,
  TransactionOutlined,
} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-account')

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

const onEdit = () => emit('edit', props.account)
const onDelete = () => props.account.id && emit('delete', props.account.id)
</script>

<style scoped>
.account-card {
  --primary-color: #6366f1;
  --icon-bg: #eef2ff;
  --icon-color: #6366f1;
  --tag-bg: #e0f2fe;
  --tag-color: #0ea5e9;

  width: 100%;
  background: var(--bg-card);
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 130px;
  border: 1px solid var(--border-default);
  transition: all 0.2s ease;
}

.account-card:hover {
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
}

.account-card.is-selected {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.icon-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--icon-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon {
  width: 18px;
  height: 18px;
  color: var(--icon-color);
}

.title {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  padding: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sub-title {
  font-size: 9px;
  color: var(--text-muted);
  font-family: monospace;
  margin: 0;
  padding: 0;
}

.status-tag {
  font-size: 9px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  background: var(--bg-elevated);
  color: var(--text-muted);
  border: 1px solid var(--border-subtle);
  flex-shrink: 0;
}

.status-tag.prod,
.status-tag.dev {
  background: var(--tag-bg);
  color: var(--tag-color);
  border-color: rgba(14, 165, 233, 0.2);
}

.card-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  border-top: 1px solid var(--border-subtle);
  border-bottom: 1px solid var(--border-subtle);
  font-size: 10px;
  color: var(--text-muted);
}

.body-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.label-icon {
  width: 14px;
  height: 14px;
}

.body-value {
  font-weight: 700;
  color: var(--text-primary);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 10px;
  color: var(--text-muted);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--success);
}

.action-group {
  display: flex;
  gap: 6px;
  opacity: 0.6;
  transition: opacity 0.2s ease;
}

.account-card:hover .action-group {
  opacity: 1;
}

.action-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-muted);
}

.action-btn:hover {
  background: var(--primary-color);
  color: #fff;
}

.action-btn.delete:hover {
  background: var(--error);
}

.action-icon {
  width: 14px;
  height: 14px;
}
</style>