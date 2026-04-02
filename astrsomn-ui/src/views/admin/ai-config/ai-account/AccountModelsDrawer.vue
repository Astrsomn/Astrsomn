<template>
  <a-drawer
    :open="props.open"
    placement="right"
    :width="500"
    :maskClosable="false"
    :closable="false" 
    @close="emit('update:open', false)"
    root-class-name="c-models-drawer"
  >
    <template #title>
      <div class="custom-drawer-header">
        <div class="header-main">
          <div class="avatar-circle">
            <DeploymentUnitOutlined />
          </div>
          <div class="title-content">
            <div class="title-row">
              <span class="main-name">{{ props.account?.accountName || '关联模型库' }}</span>
              <a-tag v-if="props.account?.envCode" :class="['env-tag', props.account.envCode.toLowerCase()]">
                {{ props.account.envCode }}
              </a-tag>
            </div>
            <div class="sub-key">
              <KeyOutlined /> {{ props.account?.accountKey || 'NO_KEY_INDEX' }}
            </div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="emit('update:open', false)">
          <CloseOutlined />
        </a-button>
      </div>
    </template>

    <a-spin :spinning="props.loading">
      <div class="card-list-container">
        <template v-if="props.models.length > 0">
          <div 
            v-for="(item, index) in props.models" 
            :key="item.id || index" 
            class="model-card-item"
          >
            <div class="m-card-head">
              <div class="m-name-box">
                <span class="m-name">{{ item.modelName }}</span>
                <span class="m-type-badge">{{ item.modelType }}</span>
              </div>
              <div :class="['status-indicator', item.status]">
                <span class="dot"></span>
                {{ item.status === 'enabled' ? '运行中' : '已禁用' }}
              </div>
            </div>

            <div class="m-card-body">
              <div class="info-row">
                <span class="info-label">模型标识</span>
                <code class="info-value mono">{{ item.modelKey }}</code>
              </div>
              <div class="info-row">
                <span class="info-label">供应商</span>
                <div class="provider-wrapper">
                  <span class="provider-tag" :data-provider="item.provider?.toLowerCase()">
                    {{ item.provider }}
                  </span>
                </div>
              </div>
            </div>

            <div class="m-card-footer-line"></div>
          </div>
        </template>

        <div v-else-if="!props.loading" class="empty-wrapper">
          <a-empty :image="simpleImage">
            <template #description>
              <span class="empty-text">当前账号尚未关联任何模型资产</span>
            </template>
            <a-button type="primary" ghost size="small">去关联</a-button>
          </a-empty>
        </div>
      </div>
    </a-spin>
  </a-drawer>
</template>

<script setup lang="ts">
import { Empty } from 'ant-design-vue';
import { 
  DeploymentUnitOutlined, 
  KeyOutlined, 
  CloseOutlined,
  DeploymentUnitOutlined as ModelIcon
} from '@ant-design/icons-vue'
import type { AiAccount } from '@/api/aiAccount'
import type { AiModel } from '@/api/aiModel'

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;

const props = defineProps<{
  open: boolean
  account?: AiAccount
  loading: boolean
  models: AiModel[]
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
}>()
</script>

<style scoped>
/* 抽屉整体容器微调 */
:deep(.ant-drawer-header) {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to bottom, #ffffff, #fcfdff);
}

:deep(.ant-drawer-body) {
  padding: 20px;
  background-color: #f8fafc; /* 给背景一点点灰度，衬托白色的卡片 */
}

/* 自定义头部样式 */
.custom-drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-circle {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.title-content {
  display: flex;
  flex-direction: column;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.main-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.sub-key {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: 'JetBrains Mono', monospace;
}

/* 环境标签 C端配色 */
.env-tag {
  border-radius: 6px;
  font-size: 10px;
  font-weight: 800;
  border: none;
}
.env-tag.prod { background: #fee2e2; color: #ef4444; }
.env-tag.dev { background: #e0f2fe; color: #0ea5e9; }

.close-btn { color: #94a3b8; font-size: 18px; }

/* 卡片列表容器 */
.card-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 单个模型卡片样式 */
.model-card-item {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px;
  border: 1px solid #f1f5f9;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.model-card-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px -8px rgba(0, 0, 0, 0.05);
  border-color: #e2e8f0;
}

/* 卡片头部：名称与状态 */
.m-card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.m-name-box {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.m-name {
  font-size: 15px;
  font-weight: 700;
  color: #334155;
}

.m-type-badge {
  font-size: 10px;
  color: #64748b;
  background: #f1f5f9;
  padding: 1px 6px;
  border-radius: 4px;
  width: fit-content;
}

/* 状态呼吸灯 */
.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
}

.status-indicator.enabled { color: #10b981; }
.status-indicator.disabled { color: #94a3b8; }

.status-indicator .dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-indicator.enabled .dot {
  box-shadow: 0 0 0 rgba(16, 185, 129, 0.4);
  animation: pulse 2s infinite;
}

/* 卡片内容行 */
.m-card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 12px;
  color: #94a3b8;
}

.info-value.mono {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12px;
  background: #f8fafc;
  padding: 2px 8px;
  border-radius: 6px;
  color: #475569;
}

/* 供应商标签样式 */
.provider-tag {
  padding: 3px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: #f1f5f9;
  color: #475569;
}

/* 为常见供应商定制颜色 */
.provider-tag[data-provider*="openai"] { background: #dcfce7; color: #15803d; }
.provider-tag[data-provider*="claude"] { background: #ffedd5; color: #c2410c; }
.provider-tag[data-provider*="azure"] { background: #e0f2fe; color: #0369a1; }

.m-card-footer-line {
  position: absolute;
  bottom: 0; left: 20px; right: 20px;
  height: 2px;
  background: #f8fafc;
}

/* 空状态 */
.empty-wrapper {
  padding: 60px 0;
  background: white;
  border-radius: 20px;
}
.empty-text { color: #cbd5e1; font-size: 13px; }

@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
  70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(16, 185, 129, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}
</style>