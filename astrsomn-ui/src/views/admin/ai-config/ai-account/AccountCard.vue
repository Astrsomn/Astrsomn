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
}

const props = defineProps<{ account: AiAccount }>()
const emit = defineEmits(['edit', 'delete'])

const maskSecret = (v?: string) => v ? `${v.slice(0, 6)}••${v.slice(-4)}` : '••••-••••'
const formatTokens = (t?: number) => {
  if (t === undefined) return '0'
  return t >= 1000 ? (t / 1000).toFixed(1) + 'k' : t.toString()
}
const formatTime = (t?: string) => t ? t.split('T')[0] : 'N/A'

const onEdit = () => emit('edit', props.account)
const onDelete = () => props.account.id && emit('delete', props.account.id)
</script>

<style scoped>
.c-dev-card {
  background: #ffffff;
  border: 1px solid #f0f0f0;
  border-radius: 4px; /* 较硬的微圆角 */
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
}

.c-dev-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
}

/* Header */
.card-head {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.status-group { display: flex; align-items: center; gap: 8px; }

.env-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 1px 6px;
  border-radius: 2px;
  background: #eee;
  color: #8c8c8c;
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

.credentials-shelf {
  display: flex; flex-direction: column; gap: 8px;
}

.cred-item {
  display: flex; align-items: center; gap: 10px;
  background: #f8f9fa;
  padding: 6px 10px;
  border-radius: 2px;
  border-left: 2px solid #eee;
}

.cred-item .i { color: #bfbfbf; font-size: 12px; }

.mono {
  font-family: 'JetBrains Mono', 'Roboto Mono', monospace;
  font-size: 12px; color: #595959; letter-spacing: 0.02em;
}

/* Footer & Actions */
.card-footer {
  padding: 10px 16px;
  border-top: 1px solid #f5f5f5;
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

.btn-edit:hover { background: #e6f7ff; color: #1890ff; }
.btn-delete:hover { background: #fff1f0; color: #ff4d4f; }
</style>