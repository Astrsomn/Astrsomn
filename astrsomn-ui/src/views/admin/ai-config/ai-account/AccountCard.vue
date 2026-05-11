<template>
  <a-card :bordered="false" class="c-side-card" :class="{ 'is-selected': selected }">
    <div class="deco-bubble bubble-1"></div>
    <div class="deco-bubble bubble-2"></div>

    <div class="card-inner">
      <div class="card-header">
        <div class="title-group">
          <a-checkbox
            class="card-check"
            :checked="selected"
            @change="onCheckboxChange"
            @click.stop
          />
          <div class="robot-icon">
            <CustomerServiceOutlined />
          </div>
          <div class="text-info">
            <h3 class="account-title" :title="account.accountName">
              {{ account.accountName || 'AI 助手实例' }}
            </h3>
            <div class="meta-under-title">
              <span class="creator">{{ account.createUser || 'Sys' }}</span>
              <span class="divider">|</span>
              <span class="time">{{ formatTime(account.createTime) }}</span>
            </div>
          </div>
        </div>
        
        <div class="status-tags">
          <span class="c-env-tag" :class="account.envCode?.toLowerCase() || 'unset'">
            {{ account.envCode || 'UNSET' }}
          </span>
          <span class="c-live-dot" title="在线"></span>
        </div>
      </div>

      <div class="token-section">
        <div class="token-label">
          <TransactionOutlined /> 剩余 Tokens
        </div>
        <div class="token-value-wrapper">
          <span class="token-num">{{ formatTokens(account.accountTokens).split('.')[0] }}</span>
          <span class="token-decimal" v-if="formatTokens(account.accountTokens).includes('.')">
            .{{ formatTokens(account.accountTokens).split('.')[1] }}
          </span>
          <span class="token-unit">Tokens</span>
        </div>
        <div class="token-progress">
          <div class="progress-bar" style="width: 70%"></div>
        </div>
      </div>

      <div class="details-section">
        <div class="detail-item models">
          <span class="item-label">已启用的模型</span>
          <div class="model-tags">
            <template v-if="usedModels.length > 0">
              <span v-for="m in usedModels.slice(0, 3)" :key="m" class="pastel-tag">
                {{ m }}
              </span>
              <span v-if="usedModels.length > 3" class="more-text" @click="emitShowModels">
                +{{ usedModels.length - 3 }}
              </span>
            </template>
            <span v-else class="empty-text">暂未关联模型</span>
            <a-button type="link" size="small" class="manage-btn" @click.stop="emitShowModels">
              管理 <RightOutlined style="font-size: 10px;" />
            </a-button>
          </div>
        </div>
      </div>

      <div class="card-actions">
        <a-tooltip title="编辑配置" placement="top">
          <a-button type="text" class="action-btn edit" @click="onEdit">
            <EditOutlined />
          </a-button>
        </a-tooltip>
        <a-popconfirm title="确定要释放该助手吗？" @confirm="onDelete">
          <a-tooltip title="释放实例" placement="top">
            <a-button type="text" danger class="action-btn delete">
              <DeleteOutlined />
            </a-button>
          </a-tooltip>
        </a-popconfirm>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  TransactionOutlined,
  EditOutlined, DeleteOutlined, CustomerServiceOutlined, RightOutlined
} from '@ant-design/icons-vue'

interface AiAccount {
  id?: number | string; accountName?: string; envCode?: string;
  apiKey?: string; apiSecret?: string; accountTokens?: number | null;
  createTime?: string; createUser?: string; usedModelNames?: string; usedModelKeys?: string;
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
:deep(.ant-card-body) { padding: 0; }

.c-side-card {
  position: relative;
  border-radius: 8px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-card);
  transition: var(--transition-pop);
  overflow: hidden;
  width: 100%;
  max-width: 360px;
  height: 260px;
  margin-bottom: 12px;
}

.c-side-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-overview);
  border-color: var(--primary);
}

.c-side-card.is-selected {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--primary) 28%, transparent);
}

/* 背景装饰球 */
.deco-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
  filter: blur(20px);
  z-index: 1;
}
.bubble-1 {
  width: 100px; height: 100px;
  background: rgba(59, 130, 246, 0.3);
  top: -40px; right: -30px;
}
.bubble-2 {
  width: 80px; height: 80px;
  background: rgba(16, 185, 129, 0.3);
  bottom: -30px; left: -20px;
}

.card-inner {
  position: relative;
  z-index: 2;
  padding: 16px;
}

/* 头部：更丰富、更有趣 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-check {
  margin-right: 2px;
}

.robot-icon {
  width: 34px; height: 34px;
  border-radius: 8px;
  background: var(--primary-gradient);
  color: white;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3);
}

.text-info { display: flex; flex-direction: column; gap: 2px; }

.account-title {
  margin: 0; font-size: 14px; font-weight: 700; color: var(--text-primary);
  max-width: 130px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}

.meta-under-title {
  display: flex; align-items: center; gap: 6px;
  font-size: 11px; color: var(--text-secondary);
}
.meta-under-title .divider { color: var(--border-default); }

.status-tags { display: flex; align-items: center; gap: 8px; }

.c-env-tag {
  font-size: 9px; font-weight: 800; padding: 2px 6px; border-radius: 99px;
  text-transform: uppercase; letter-spacing: 0.5px;
}
/* PROD 使用清爽的紫色，DEV 使用柔和的蓝色 */
.c-env-tag.prod { background: var(--primary-hover); color: var(--primary); border: 1px solid var(--primary); }
.c-env-tag.dev { background: var(--primary-hover); color: var(--primary); border: 1px solid var(--primary); }
.c-env-tag:not(.prod):not(.dev) { background: var(--bg-elevated); color: var(--text-secondary); }

.c-live-dot {
  width: 8px; height: 8px;
  background: var(--success); /* 绿宝石色 */
  border-radius: 50%;
  border: 2px solid var(--bg-card);
  box-shadow: 0 0 0 2px var(--success), 0 0 10px var(--success);
  animation: live-pulse 2s infinite;
}

/* 核心 Token 区 */
.token-section {

  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  border: 1px solid var(--border-default);
}

.token-label {
  font-size: 11px; color: var(--primary);
  font-weight: 600; display: flex; align-items: center; gap: 5px;
  margin-bottom: 6px;
}

.token-value-wrapper {
  display: flex; align-items: baseline;
  color: var(--text-primary);
}
.token-num { font-size: 24px; font-weight: 800; line-height: 1; font-family: 'Poppins', sans-serif; }
.token-decimal { font-size: 14px; font-weight: 700; opacity: 0.8; }
.token-unit { font-size: 11px; font-weight: 600; margin-left: 4px; color: var(--text-secondary); }

.token-progress {
  height: 5px; background: var(--border-default); border-radius: 99px;
  margin-top: 8px; overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: var(--primary-gradient);
  border-radius: 99px;
}

/* 详情区：模块化、干净 */
.details-section {
  display: flex; flex-direction: column; gap: 10px;

  border-radius: 8px;
  padding: 10px;
  margin-bottom: 6px;
}

.detail-item { display: flex; flex-direction: column; gap: 6px; }

.item-label {
  font-size: 11px; font-weight: 600; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}

/* 模型标签：莫兰迪色系/冰淇淋色系 */
.model-tags { display: flex; flex-wrap: wrap; gap: 4px; align-items: center; }
.pastel-tag {
  background: var(--primary-hover); color: var(--primary);
  padding: 2px 8px; border-radius: 8px; font-size: 11px;
  font-weight: 500; border: 1px solid var(--primary);
}
.more-text { font-size: 11px; color: var(--text-secondary); cursor: pointer; font-weight: 600; }
.manage-btn { padding: 0; height: auto; font-size: 11px; margin-left: auto; color: var(--primary); }

/* 操作按钮 */
.card-actions {
  position: absolute;
  top: 16px; right: 16px;
  display: flex; gap: 4px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.c-side-card:hover .card-actions {
  opacity: 1;
  transform: translateX(0);
}

.action-btn {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px;
  background: var(--bg-elevated);
  box-shadow: var(--shadow-card);
  color: var(--text-secondary);
  transition: all 0.2s;
  border: 1px solid var(--border-default);
}
.action-btn:hover { transform: scale(1.1); }
.action-btn.edit:hover { background: var(--primary-hover); color: var(--primary); border-color: var(--primary); }
.action-btn.delete:hover { background: rgba(239, 68, 68, 0.1); color: var(--error); border-color: var(--error); }

/* 动画定义 */
@keyframes live-pulse {
  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7), 0 0 10px var(--success); }
  70% { transform: scale(1.05); box-shadow: 0 0 0 8px rgba(16, 185, 129, 0), 0 0 10px var(--success); }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0), 0 0 10px var(--success); }
}
</style>