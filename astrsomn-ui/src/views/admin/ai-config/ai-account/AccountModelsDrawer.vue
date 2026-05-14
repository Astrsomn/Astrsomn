<template>
  <AstrsomnDrawerShell
      :open="props.open"
      :width="500"
      root-class-name="c-models-drawer"
      @update:open="(value) => emit('update:open', value)"
  >
    <template #icon>
      <DeploymentUnitOutlined/>
    </template>
    <template #title>
      {{ props.account?.accountName || '关联模型库' }}
    </template>
    <template #title-extra>
      <a-tag v-if="props.account?.envCode" :class="['env-tag', props.account.envCode.toLowerCase()]">
        {{ props.account.envCode }}
      </a-tag>
    </template>
    <template #subtitle>
      <KeyOutlined/>
      {{ props.account?.accountKey || 'NO_KEY_INDEX' }}
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
                  <span :data-provider="item.extensionCode?.toLowerCase()" class="provider-tag">
                    {{ item.extensionCode }}
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
            <a-button ghost size="small" type="primary">去关联</a-button>
          </a-empty>
        </div>
      </div>
    </a-spin>
    <template v-if="$slots.footer" #footer>
      <slot name="footer"/>
    </template>
  </AstrsomnDrawerShell>
</template>

<script lang="ts" setup>
import {Empty} from 'ant-design-vue'
import {DeploymentUnitOutlined, KeyOutlined} from '@ant-design/icons-vue'
import AstrsomnDrawerShell from '@/components/home/AstrsomnDrawerShell.vue'
import type {AiAccount} from '@/api/aiAccount'
import type {AiModel} from '@/api/aiModel'

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE

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
.env-tag {
  border-radius: 6px;
  font-size: 10px;
  font-weight: 800;
  border: none;
}

.env-tag.prod {
  background: #fee2e2;
  color: #ef4444;
}

.env-tag.dev {
  background: #e0f2fe;
  color: #0ea5e9;
}

.card-list-container {
  padding-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.model-card-item {
  cursor: pointer;
  background: #ffffff;
  border-radius: var(--radius-md);
  padding: 20px;
  border: 1px solid var(--border-default);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.model-card-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px -8px rgba(0, 0, 0, 0.05);
  border-color: #e2e8f0;
}

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

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
}

.status-indicator.enabled {
  color: #10b981;
}

.status-indicator.disabled {
  color: #94a3b8;
}

.status-indicator .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-indicator.enabled .dot {
  box-shadow: 0 0 0 rgba(16, 185, 129, 0.4);
  animation: pulse 2s infinite;
}

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

.provider-tag[data-provider*="openai"] {
  background: #dcfce7;
  color: #15803d;
}

.provider-tag[data-provider*="claude"] {
  background: #ffedd5;
  color: #c2410c;
}

.provider-tag[data-provider*="azure"] {
  background: #e0f2fe;
  color: #0369a1;
}

.m-card-footer-line {
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: #f8fafc;
}

.empty-wrapper {
  padding: 60px 0;
  background: white;
  border-radius: 20px;
}

.empty-text {
  color: #cbd5e1;
  font-size: 13px;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 6px rgba(16, 185, 129, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}
</style>