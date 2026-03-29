<template>
  <a-card :bordered="false" class="account-card">
    <div class="card-header">
      <div class="title-group">
        <h3 class="account-name" :title="account.accountName">
          {{ account.accountName || '未命名账户' }}
        </h3>
        <a-tag v-if="account.envCode" :color="getEnvColor(account.envCode)" class="custom-tag">
          {{ account.envCode }}
        </a-tag>
      </div>
      <div class="header-action">
        <a-dropdown placement="bottomRight">
          <MoreOutlined class="more-icon" />
          <template #overlay>
            <a-menu>
              <a-menu-item key="edit" @click="onEdit">
                <EditOutlined /> 编辑账户
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="delete" danger>
                <a-popconfirm title="确定删除此账户吗？" @confirm="onDelete">
                  <DeleteOutlined /> 删除账户
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </div>

    <div class="card-body">
      <div class="info-grid">
        <div class="info-cell full-width">
          <div class="cell-label"><KeyOutlined /> API Key</div>
          <div class="cell-value secret-font">
            {{ maskSecret(account.apiKey) }}
            <a-typography-paragraph
              v-if="account.apiKey"
              :copyable="{ text: account.apiKey }"
              class="copy-btn"
            />
          </div>
        </div>

        <div class="info-cell">
          <div class="cell-label"><DollarOutlined /> 剩余额度</div>
          <div class="cell-value highlight">{{ formatTokens(account.accountTokens) }}</div>
        </div>

        <div class="info-cell">
          <div class="cell-label"><UserOutlined /> 创建人</div>
          <div class="cell-value">{{ account.createUser || '系统' }}</div>
        </div>
      </div>
    </div>

    <div class="card-footer">
      <span class="time-stamp">
        <CalendarOutlined /> 开启于 {{ formatTime(account.createTime) }}
      </span>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { 
  KeyOutlined, 
  DollarOutlined, 
  CalendarOutlined, 
  UserOutlined,
  MoreOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'

interface AiAccount {
  id?: number | string
  accountName?: string
  envCode?: string
  apiKey?: string
  accountTokens?: number
  createTime?: string
  createUser?: string
}

const props = defineProps<{
  account: AiAccount
}>()

const emit = defineEmits<{
  (e: 'edit', account: AiAccount): void
  (e: 'delete', id: number | string): void
}>()

// 根据环境返回不同颜色
const getEnvColor = (env: string) => {
  const map: Record<string, string> = {
    'PROD': 'red',
    'STAG': 'orange',
    'TEST': 'green',
    'DEV': 'blue'
  }
  return map[env.toUpperCase()] || 'blue'
}

const maskSecret = (value: string | undefined) => {
  if (!value) return '—'
  return value.length > 12 ? `${value.slice(0, 6)} •••• ${value.slice(-4)}` : '••••••••'
}

const formatTokens = (tokens: number | undefined) => {
  if (tokens === undefined || tokens === null) return '0'
  return tokens >= 10000 ? `${(tokens / 1000).toFixed(1)}k` : tokens.toLocaleString()
}

const formatTime = (time: string | undefined) => {
  if (!time) return '—'
  // 简易格式化：2023-10-24
  return time.split('T')[0]
}

const onEdit = () => emit('edit', props.account)
const onDelete = () => props.account.id && emit('delete', props.account.id)
</script>

<style scoped>
.account-card {
  border-radius: 3px;
  background: #ffffff;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f0f0f0;
  overflow: hidden;
  position: relative;
}

.account-card:hover {
  box-shadow: 0 8px 24px rgba(149, 157, 165, 0.15);
  border-color: #e6f7ff;
  transform: translateY(-4px);
}

/* Header */
.card-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to right, #fafafa, #ffffff);
  border-bottom: 1px solid #f5f5f5;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow: hidden;
}

.account-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.custom-tag {
  border-radius: var(--radius-sm);
  font-weight: 500;
  font-size: 11px;
  line-height: 18px;
}

.more-icon {
  cursor: pointer;
  padding: 4px;
  border-radius: var(--radius-sm);
  color: #8c8c8c;
  transition: all 0.2s;
}

.more-icon:hover {
  background: #f0f0f0;
  color: #1890ff;
}

/* Body */
.card-body {
  padding: 16px 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.full-width {
  grid-column: span 2;
  background: #f8f9fb;
  padding: 8px 12px;
  border-radius: var(--radius-md);
}

.cell-label {
  font-size: 12px;
  color: #8c8c8c;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cell-label .anticon {
  font-size: 14px;
}

.cell-value {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cell-value.highlight {
  color: #1890ff;
  font-family: 'PingFang SC', sans-serif;
}

.secret-font {
  font-family: 'Roboto Mono', monospace;
  letter-spacing: 1px;
}

.copy-btn {
  margin-bottom: 0 !important;
  font-size: 12px;
}

/* Footer */
.card-footer {
  padding: 12px 20px;
  background: #ffffff;
  border-top: 1px dashed #f0f0f0;
}

.time-stamp {
  font-size: 12px;
  color: #bfbfbf;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 移动端适配 */
@media (max-width: 576px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .full-width {
    grid-column: span 1;
  }
}
</style>