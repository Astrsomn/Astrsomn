<template>
  <a-card :bordered="false" class="c-side-card">
    <div class="deco-bubble bubble-1"></div>
    <div class="deco-bubble bubble-2"></div>

    <div class="card-inner">
      <div class="card-header">
        <div class="title-group">
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

        <div class="detail-item keys">
          <span class="item-label">API 凭据</span>
          <div class="key-rows">
            <div class="key-mono">
              <KeyOutlined class="k-icon" />
              <code>{{ maskSecret(account.apiKey) }}</code>
            </div>
            <div class="key-mono">
              <LockOutlined class="k-icon" />
              <code>{{ maskSecret(account.apiSecret) }}</code>
            </div>
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
  UserOutlined, KeyOutlined, LockOutlined, TransactionOutlined,
  HistoryOutlined, EditOutlined, DeleteOutlined, RobotOutlined,
  CustomerServiceOutlined, RightOutlined
} from '@ant-design/icons-vue'

interface AiAccount {
  id?: number | string; accountName?: string; envCode?: string;
  apiKey?: string; apiSecret?: string; accountTokens?: number;
  createTime?: string; createUser?: string; usedModelNames?: string; usedModelKeys?: string;
}

const props = defineProps<{ account: AiAccount }>()
const emit = defineEmits(['edit', 'delete', 'show-models'])

const maskSecret = (v?: string) => v ? `${v.slice(0, 6)}***${v.slice(-4)}` : '••••-••••'
const formatTokens = (t?: number) => {
  if (t === undefined) return '0'
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
</script>

<style scoped>
/* 定义讨喜的 C 端色彩系统 */
:deep(.ant-card-body) { padding: 0; }

.c-side-card {
  --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 温暖的紫蓝渐变 */
  --token-bg: #fdf2f8; /* 极浅的粉色，用于核心数据区背景 */
  --tag-bg: #e0f2fe; /* 极浅的蓝色，用于模型标签 */
  --text-main: #334155;
  --text-sub: #94a3b8;
  
  position: relative;
  border-radius: 24px; /* 超大圆角 */
  background: #ffffff;
  border: 1px solid #f1f5f9;
  box-shadow: 0 10px 25px -5px rgba(118, 75, 162, 0.08); /* 带有主色调的柔和阴影 */
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); /* 带有弹性的过渡 */
  overflow: hidden;
  margin-bottom: 20px;
}

.c-side-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 35px -5px rgba(118, 75, 162, 0.15);
  border-color: #e2e8f0;
}

/* 背景装饰球 */
.deco-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
  filter: blur(20px);
  z-index: 1;
}
.bubble-1 {
  width: 100px; height: 100px;
  background: #a5b4fc;
  top: -40px; right: -30px;
}
.bubble-2 {
  width: 80px; height: 80px;
  background: #fbcfe8;
  bottom: -30px; left: -20px;
}

.card-inner {
  position: relative;
  z-index: 2; /* 确保内容在装饰球上方 */
  padding: 24px;
}

/* 头部：更丰富、更有趣 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.robot-icon {
  width: 44px; height: 44px;
  border-radius: 14px;
  background: var(--primary-gradient);
  color: white;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  box-shadow: 0 4px 10px rgba(118, 75, 162, 0.3);
}

.text-info { display: flex; flex-direction: column; gap: 2px; }

.account-title {
  margin: 0; font-size: 18px; font-weight: 700; color: var(--text-main);
  max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}

.meta-under-title {
  display: flex; align-items: center; gap: 6px;
  font-size: 12px; color: var(--text-sub);
}
.meta-under-title .divider { color: #e2e8f0; }

.status-tags { display: flex; align-items: center; gap: 8px; }

.c-env-tag {
  font-size: 10px; font-weight: 800; padding: 3px 8px; border-radius: 99px;
  text-transform: uppercase; letter-spacing: 0.5px;
}
/* PROD 使用清爽的紫色，DEV 使用柔和的蓝色 */
.c-env-tag.prod { background: #f3e8ff; color: #7e22ce; border: 1px solid #e9d5ff; }
.c-env-tag.dev { background: #e0f2fe; color: #0369a1; border: 1px solid #bae6fd; }
.c-env-tag:not(.prod):not(.dev) { background: #f1f5f9; color: #64748b; }

.c-live-dot {
  width: 10px; height: 10px;
  background: #4ade80; /* 绿宝石色 */
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #4ade80, 0 0 10px #4ade80;
  animation: live-pulse 2s infinite;
}

/* 核心 Token 区：像一个精美的流量包卡片 */
.token-section {
  background: var(--token-bg);
  border-radius: 16px;
  padding: 16px 20px;
  margin-bottom: 20px;
  border: 1px solid #fbcfe8;
}

.token-label {
  font-size: 12px; color: #db2777; /* 粉色调 */
  font-weight: 600; display: flex; align-items: center; gap: 5px;
  margin-bottom: 8px;
}

.token-value-wrapper {
  display: flex; align-items: baseline;
  color: #c2185b; /* 稍深的粉色 */
}
.token-num { font-size: 36px; font-weight: 800; line-height: 1; font-family: 'Poppins', sans-serif; }
.token-decimal { font-size: 20px; font-weight: 700; opacity: 0.8; }
.token-unit { font-size: 14px; font-weight: 600; margin-left: 6px; color: #db2777; opacity: 0.7; }

.token-progress {
  height: 6px; background: #fce7f3; border-radius: 99px;
  margin-top: 10px; overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: linear-gradient(to right, #ec4899, #db2777); /* 粉色渐变 */
  border-radius: 99px;
}

/* 详情区：模块化、干净 */
.details-section {
  display: flex; flex-direction: column; gap: 16px;
  background: #f8fafc; /* 极浅灰底色区隔 */
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 24px;
}

.detail-item { display: flex; flex-direction: column; gap: 8px; }

.item-label {
  font-size: 12px; font-weight: 600; color: var(--text-sub);
  text-transform: uppercase; letter-spacing: 0.5px;
}

/* 模型标签：莫兰迪色系/冰淇淋色系 */
.model-tags { display: flex; flex-wrap: wrap; gap: 6px; align-items: center; }
.pastel-tag {
  background: var(--tag-bg); color: #0284c7;
  padding: 3px 10px; border-radius: 8px; font-size: 12px;
  font-weight: 500; border: 1px solid #bae6fd;
}
.more-text { font-size: 12px; color: var(--text-sub); cursor: pointer; font-weight: 600; }
.manage-btn { padding: 0; height: auto; font-size: 12px; margin-left: auto; color: #764ba2; }

/* 密钥：单色、代码感 */
.key-rows { display: flex; flex-direction: column; gap: 6px; }
.key-mono {
  display: flex; align-items: center; gap: 8px;
  background: white; padding: 4px 10px; border-radius: 8px; border: 1px solid #e2e8f0;
}
.k-icon { color: #cbd5e1; font-size: 12px; }
.key-mono code { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #64748b; letter-spacing: 0.5px; }

/* 操作按钮：悬浮流式设计 */
.card-actions {
  position: absolute;
  top: 24px; right: 24px;
  display: flex; gap: 6px;
  opacity: 0; /* 默认隐藏 */
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.c-side-card:hover .card-actions {
  opacity: 1; /* 悬浮显示 */
  transform: translateX(0);
}

.action-btn {
  width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 10px;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(4px); /* 毛玻璃效果 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  color: var(--text-sub);
  transition: all 0.2s;
}
.action-btn:hover { transform: scale(1.1); }
.action-btn.edit:hover { background: #e0f2fe; color: #0369a1; }
.action-btn.delete:hover { background: #fee2e2; color: #dc2626; }

/* 动画定义 */
@keyframes live-pulse {
  0% { box-shadow: 0 0 0 0 rgba(74, 222, 128, 0.7); }
  70% { box-shadow: 0 0 0 8px rgba(74, 222, 128, 0); }
  100% { box-shadow: 0 0 0 0 rgba(74, 222, 128, 0); }
}

@keyframes live-pulse {
  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(74, 222, 128, 0.7), 0 0 10px #4ade80; }
  70% { transform: scale(1.05); box-shadow: 0 0 0 8px rgba(74, 222, 128, 0), 0 0 10px #4ade80; }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(74, 222, 128, 0), 0 0 10px #4ade80; }
}

/* 引入谷歌字体，增加数字的 C 端高级感 (实际项目中在 HTML head 引入) */
/* @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@700;800&display=swap'); */
</style>