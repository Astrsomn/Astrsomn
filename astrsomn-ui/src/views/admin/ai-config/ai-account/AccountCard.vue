<template>
  <a-card :bordered="false" class="c-dev-card">
    <div class="card-head">
      <div class="status-group">
        <div class="env-badge" :class="account.envCode?.toLowerCase()">
          {{ account.envCode || 'UNSET' }}
        </div>
        <div class="live-tag">
          <span class="dot"></span>
          <span class="tag-text">LIVE</span>
        </div>
      </div>
      <div class="user-info" title="CREATOR">
        <UserOutlined />
        <span class="user-name">{{ account.createUser || 'Sys' }}</span>
      </div>
    </div>

    <div class="card-main">
      <div class="title-row">
        <h3 class="account-title">{{ account.accountName || 'Unnamed_Instance' }}</h3>
        <div class="token-metric" title="REMAINING_TOKENS">
          <TransactionOutlined />
          <span class="token-val">{{ formatTokens(account.accountTokens) }}</span>
        </div>
      </div>

      <div v-if="usedModels.length > 0" class="models-shelf">
        <div class="models-top">
          <span class="models-label">使用模型</span>
          <a-button
            type="link"
            size="small"
            class="models-link"
            @click.stop="emitShowModels"
          >
            侧边栏
          </a-button>
        </div>
        <div class="models-chips">
          <a-tag v-for="m in usedModels.slice(0, 3)" :key="m" class="model-chip">
            {{ m }}
          </a-tag>
          <span v-if="usedModels.length > 3" class="more-text">+{{ usedModels.length - 3 }}</span>
        </div>
      </div>

      <div v-else class="models-shelf empty-models">
        <span class="models-label">使用模型</span>
        <span class="no-models">暂无关联模型</span>
        <a-button
          type="link"
          size="small"
          class="models-link"
          @click.stop="emitShowModels"
        >
          查看
        </a-button>
      </div>

      <div class="credentials-shelf">
        <div class="cred-item">
          <KeyOutlined class="i" />
          <code class="mono">{{ maskSecret(account.apiKey) }}</code>
        </div>
        <div class="cred-item">
          <LockOutlined class="i" />
          <code class="mono">{{ maskSecret(account.apiSecret) }}</code>
        </div>
      </div>
    </div>

    <div class="card-footer">
      <div class="time-box">
        <HistoryOutlined />
        <span>{{ formatTime(account.createTime) }}</span>
      </div>
      <div class="action-btns">
        <a-button type="text" size="small" class="btn-edit" @click="onEdit">
          <EditOutlined />
        </a-button>
        <a-popconfirm title="Terminate this service?" @confirm="onDelete" ok-text="Yes" cancel-text="No">
          <a-button type="text" size="small" danger class="btn-delete">
            <DeleteOutlined />
          </a-button>
        </a-popconfirm>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  UserOutlined,
  KeyOutlined,
  LockOutlined,
  TransactionOutlined,
  HistoryOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'

interface AiAccount {
  id?: number | string
  accountName?: string
  envCode?: string
  apiKey?: string
  apiSecret?: string
  accountTokens?: number
  createTime?: string
  createUser?: string

  usedModelCount?: number
  usedModelKeys?: string
  usedModelNames?: string
}

const props = defineProps<{ account: AiAccount }>()
const emit = defineEmits(['edit', 'delete', 'show-models'])

const maskSecret = (v?: string) => v ? `${v.slice(0, 6)}••${v.slice(-4)}` : '••••-••••'
const formatTokens = (t?: number) => {
  if (t === undefined) return '0'
  return t >= 1000 ? (t / 1000).toFixed(1) + 'k' : t.toString()
}
const formatTime = (t?: string) => t ? t.split('T')[0] : 'N/A'

const onEdit = () => emit('edit', props.account)
const onDelete = () => props.account.id && emit('delete', props.account.id)

const parseCsv = (raw?: string) => {
  if (!raw) return []
  return raw
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
}

const usedModels = computed(() => {
  const names = parseCsv(props.account.usedModelNames)
  if (names.length > 0) return names
  return parseCsv(props.account.usedModelKeys)
})

const emitShowModels = () => emit('show-models', props.account)
</script>

<style scoped>
.c-dev-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
}

.c-dev-card:hover {
  border-color: color-mix(in srgb, var(--border-default) 55%, var(--primary) 25%);
  box-shadow: 0 10px 25px -5px color-mix(in srgb, var(--primary) 12%, transparent);
}

/* Header */
.card-head {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: color-mix(in srgb, var(--bg-card) 92%, var(--bg-surface));
  border-bottom: 1px solid var(--border-subtle);
}

.status-group { display: flex; align-items: center; gap: 8px; }

.env-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 1px 6px;
  border-radius: 2px;
  background: color-mix(in srgb, var(--bg-card) 70%, var(--bg-surface));
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}
.env-badge.prod { background: #000; color: #fff; }
.env-badge.dev { background: #1890ff; color: #fff; }

.live-tag {
  display: flex; align-items: center; gap: 4px;
  font-size: 9px; font-weight: 700; color: #bfbfbf;
}
.live-tag .dot {
  width: 5px; height: 5px; background: #52c41a; border-radius: 50%;
}

.user-info {
  font-size: 11px; color: #bfbfbf;
  display: flex; align-items: center; gap: 4px;
}

/* Main Content */
.card-main { padding: 16px; }

.title-row {
  display: flex; justify-content: space-between; align-items: baseline;
  margin-bottom: 16px;
}

.account-title {
  margin: 0; font-size: 16px; font-weight: 700; color: #1a1a1a;
  letter-spacing: -0.01em;
}

.token-metric {
  font-size: 12px; color: #52c41a; font-weight: 600;
  display: flex; align-items: center; gap: 4px;
}

.models-shelf {
  margin-bottom: 14px;
  padding: 12px 12px;
  border: 1px solid var(--border-subtle);
  background: color-mix(in srgb, var(--bg-surface) 72%, var(--bg-card));
  border-radius: var(--radius-sm);
}

.models-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.models-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
}

.models-link :deep(.ant-btn-link) {
  padding: 0;
}

.models-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.model-chip :deep(.ant-tag) {
  border-radius: 999px;
}

.more-text {
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-models {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.no-models {
  font-size: 12px;
  color: var(--text-secondary);
}

.credentials-shelf {
  display: flex; flex-direction: column; gap: 8px;
}

.cred-item {
  display: flex; align-items: center; gap: 10px;
  background: color-mix(in srgb, var(--bg-surface) 72%, var(--bg-card));
  padding: 6px 10px;
  border-radius: var(--radius-xs);
  border-left: 2px solid var(--border-subtle);
}

.cred-item .i { color: #bfbfbf; font-size: 12px; }

.mono {
  font-family: 'JetBrains Mono', 'Roboto Mono', monospace;
  font-size: 12px; color: #595959; letter-spacing: 0.02em;
}

/* Footer & Actions */
.card-footer {
  padding: 10px 16px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-box {
  font-size: 10px; color: #d9d9d9;
  display: flex; align-items: center; gap: 4px;
  font-family: 'JetBrains Mono', monospace;
}

.action-btns { display: flex; gap: 4px; }

.btn-edit, .btn-delete {
  font-size: 14px;
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 4px;
  color: #bfbfbf;
  transition: all 0.2s;
}

.btn-edit:hover { background: color-mix(in srgb, #1890ff 16%, transparent); color: #1890ff; }
.btn-delete:hover { background: color-mix(in srgb, #ff4d4f 12%, transparent); color: #ff4d4f; }
</style>