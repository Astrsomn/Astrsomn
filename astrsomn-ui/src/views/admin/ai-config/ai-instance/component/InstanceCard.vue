<template>
  <div
      :class="['toc-card', `toc-card--${cardVariant}`, { 'is-selected': selected }]"
      :style="{ '--delay': index }"
  >
    <div class="deco-bubble bubble-1"/>
    <div class="deco-bubble bubble-2"/>
    <div aria-hidden="true" class="deco-accent-bar"/>

    <div class="toc-checkbox-wrapper" @click.stop="emit('toggle-select')">
      <div :class="['custom-check', { active: selected }]">
        <check-outlined v-if="selected"/>
      </div>
    </div>

    <div class="card-actions">
      <a-tooltip title="快速编辑">
        <div class="action-icon-btn edit" @click.stop="emit('edit')">
          <edit-outlined/>
        </div>
      </a-tooltip>
      <a-popconfirm title="确定删除此配置？" @confirm="emit('delete')">
        <div class="action-icon-btn delete" @click.stop>
          <delete-outlined/>
        </div>
      </a-popconfirm>
    </div>

    <div class="toc-card-body" @click="emit('edit')">
      <div class="toc-card-header">
        <div class="header-type-block">
          <span
              v-if="providerAvatarMarkup"
              aria-hidden="true"
              class="provider-logo"
              v-html="providerAvatarMarkup"
          />
          <div :class="['type-icon-box', getModelTypeClass(record)]">
            <component :is="getModelIcon(record)"/>
          </div>
        </div>
        <div class="title-area">
          <div class="top-row">
            <h4 :title="record.instanceName" class="name">{{ record.instanceName || '未命名配置' }}</h4>
            <div :class="['status-glow', record.status]">
              <span class="dot"></span>
              {{ record.status === 'enabled' ? '运行中' : '已停用' }}
            </div>
          </div>
          <p class="sub-key">{{ record.instanceKey || 'NO_INSTANCE_ID' }}</p>
        </div>
      </div>

      <div class="dynamic-content-wrapper">
        <template v-if="isChat">
          <div class="params-label">模型推理参数</div>
          <div class="mini-progress-item">
            <div class="p-labels"><span>温度 (Temp)</span> <b>{{ record.temperature ?? 0.7 }}</b></div>
            <div class="p-track">
              <div :style="{ width: `${(record.temperature || 0) / 2 * 100}%` }" class="p-thumb temp"></div>
            </div>
          </div>
          <div class="mini-progress-item">
            <div class="p-labels"><span>核采样 (TopP)</span> <b>{{ record.topP ?? 1.0 }}</b></div>
            <div class="p-track">
              <div :style="{ width: `${(record.topP || 0) * 100}%` }" class="p-thumb topp"></div>
            </div>
          </div>
        </template>

        <template v-else-if="isEmbedding">
          <div class="params-label">向量空间规格</div>
          <div class="capability-grid">
            <div class="cap-item">
              <div class="cap-icon v-dim">
                <cluster-outlined/>
              </div>
              <div class="cap-info">
                <span class="l">维度</span>
                <span class="v">{{ record.dimensions || 1536 }}D</span>
              </div>
            </div>
            <div class="cap-item">
              <div class="cap-icon v-env">
                <environment-outlined/>
              </div>
              <div class="cap-info">
                <span class="l">部署环境</span>
                <span class="v">{{ record.envCode || 'PROD' }}</span>
              </div>
            </div>
          </div>
        </template>

        <template v-else-if="isImage">
          <div class="params-label">生图配置预设</div>
          <div class="image-spec-grid">
            <div class="spec-block">
              <span class="s-label">画幅</span>
              <span class="s-value">{{ imageSizeLabel }}</span>
            </div>
            <div class="spec-block">
              <span class="s-label">风格</span>
              <span class="s-value">{{ imageStyleLabel }}</span>
            </div>
          </div>
        </template>

        <template v-else>
          <div class="params-label">实例概览</div>
          <div class="other-spec">
            <span class="other-type-pill">{{ typeLabel }}</span>
            <div v-if="record.maxTokens != null" class="other-row">
              <span>最大输出</span>
              <b>{{ record.maxTokens }}</b>
            </div>
            <div v-if="record.topK != null" class="other-row">
              <span>TopK</span>
              <b>{{ record.topK }}</b>
            </div>
          </div>
        </template>
      </div>

      <div class="toc-card-meta">
        <div class="provider-info">
          <api-outlined class="m-icon"/>
          <span class="provider-label">{{ record.modelKey || '未关联端点' }}</span>
        </div>
        <div class="time-info">
          <history-outlined/>
          <span class="time-ago">{{ formatUpdateTime(record.updateTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  ApiOutlined,
  CheckOutlined,
  ClusterOutlined,
  DeleteOutlined,
  EditOutlined,
  EnvironmentOutlined,
  FileImageOutlined,
  HistoryOutlined,
  MessageOutlined,
  PartitionOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import type {AiInstance} from '@/api/aiInstance.ts'

dayjs.extend(relativeTime)

type InstanceCardVariant = 'chat' | 'embedding' | 'image' | 'other'

const props = defineProps<{
  record: AiInstance
  index: number
  selected: boolean
}>()

const emit = defineEmits(['toggle-select', 'edit', 'delete'])

const formatUpdateTime = (time?: string) => (time ? dayjs(time).fromNow() : '暂无记录')

function normalizeVariant(r: AiInstance): InstanceCardVariant {
  const t = (r.modelType || 'chat').toLowerCase().trim()
  if (t === 'embedding') return 'embedding'
  if (t === 'image') return 'image'
  if (t === 'chat' || t === '') return 'chat'
  return 'other'
}

const cardVariant = computed(() => normalizeVariant(props.record))
const isChat = computed(() => cardVariant.value === 'chat')
const isEmbedding = computed(() => cardVariant.value === 'embedding')
const isImage = computed(() => cardVariant.value === 'image')

const typeLabel = computed(() => {
  const raw = props.record.modelType?.trim()
  if (!raw) return '对话'
  const map: Record<string, string> = {
    chat: '对话',
    embedding: '向量',
    image: '图像'
  }
  return map[raw.toLowerCase()] || raw
})

const imageSizeLabel = computed(() => props.record.size?.trim() || '未指定')
const imageStyleLabel = computed(() => props.record.style?.trim() || '默认')

const getModelIcon = (r: AiInstance) => {
  switch (normalizeVariant(r)) {
    case 'embedding':
      return PartitionOutlined
    case 'image':
      return FileImageOutlined
    case 'other':
      return SettingOutlined
    default:
      return MessageOutlined
  }
}

const getModelTypeClass = (r: AiInstance) => normalizeVariant(r)

const providerAvatarMarkup = computed(() => {
  const raw = props.record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
})
</script>

<style scoped>
.toc-card {
  --card-accent: #6366f1;
  --card-accent-mid: #4f46e5;
  --card-accent-soft: rgba(99, 102, 241, 0.12);
  --card-selected-bg: var(--bg-elevated);
  --card-hover-border: rgba(99, 102, 241, 0.35);
  --bubble-1: #c4b5fd;
  --bubble-2: #93c5fd;
  --content-bg: var(--bg-card);
  --content-border: var(--border-default);
  --params-label: var(--text-secondary);

  background: var(--bg-base);
  border-radius: 28px;
  border: 1px solid var(--border-default);
  position: relative;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
  box-shadow 0.35s ease,
  border-color 0.35s ease,
  background 0.35s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-card);
}


.toc-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-overview),
  0 0 0 1px var(--card-accent-soft);
  border-color: var(--card-hover-border);
}

.deco-accent-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  z-index: 3;
  background: linear-gradient(90deg, var(--card-accent), var(--card-accent-mid));
  opacity: 0.95;
}

.deco-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0.22;
  filter: blur(32px);
  z-index: 0;
  pointer-events: none;
}

.bubble-1 {
  width: 120px;
  height: 120px;
  top: -40px;
  right: -30px;
  background: var(--bubble-1);
}

.bubble-2 {
  width: 88px;
  height: 88px;
  bottom: -24px;
  left: -24px;
  background: var(--bubble-2);
}

.toc-card.is-selected {
  border-color: var(--card-accent);
  background: var(--card-selected-bg);
  box-shadow: 0 12px 28px -8px var(--card-accent-soft),
  0 0 0 1px var(--card-accent-soft);
}

.toc-checkbox-wrapper {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
  cursor: pointer;
}

.custom-check {
  width: 26px;
  height: 26px;
  border-radius: 10px;
  border: 2px solid var(--border-default);
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.custom-check.active {
  background: var(--card-accent);
  border-color: var(--card-accent-mid);
  color: white;
  box-shadow: 0 4px 12px var(--card-accent-soft);
}


.card-actions {
  position: absolute;
  top: 60px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
  z-index: 10;
}

.toc-card:hover .card-actions {
  opacity: 1;
  transform: translateX(0);
}

.action-icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: var(--bg-card);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-default);
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-secondary);
}

.action-icon-btn:hover {
  transform: scale(1.1);
  background: var(--bg-elevated);
}

.action-icon-btn.edit:hover {
  color: var(--card-accent);
  border-color: var(--card-accent-soft);
}

.action-icon-btn.delete:hover {
  color: var(--error);
  border-color: rgba(239, 68, 68, 0.2);
}


.toc-card-body {
  padding: 24px;
  position: relative;
  z-index: 1;
  cursor: pointer;
}

.toc-card-header {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.header-type-block {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.provider-logo {
  display: flex;
  align-items: center;
  justify-content: center;
}

.provider-logo :deep(svg) {
  width: 28px;
  height: 28px;
  display: block;
}


.type-icon-box {
  width: 54px;
  height: 54px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.1);
}

.type-icon-box.chat {
  background: linear-gradient(135deg, #6366f1, #2563eb);
}

.type-icon-box.embedding {
  background: linear-gradient(135deg, #10b981, #047857);
}

.type-icon-box.image {
  background: linear-gradient(135deg, #fb923c, #c2410c);
}

.type-icon-box.other {
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
}

.title-area .name {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.title-area .sub-key {
  font-size: 11px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  margin: 2px 0 0 0;
}

.status-glow {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 800;
  padding: 2px 10px;
  border-radius: 20px;
  background: var(--bg-elevated);
  color: var(--text-secondary);
}

.status-glow.enabled {
  background: rgba(16, 185, 129, 0.2);
  color: var(--success);
}

.status-glow .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-glow.enabled .dot {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(16, 185, 129, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}

.dynamic-content-wrapper {
  background: var(--content-bg);
  backdrop-filter: blur(6px);
  border-radius: 20px;
  padding: 16px;
  margin-bottom: 20px;
  border: 1px solid var(--content-border);
  transition: background 0.3s ease, border-color 0.3s ease;
}

.params-label {
  font-size: 10px;
  font-weight: 800;
  color: var(--params-label);
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 12px;
  opacity: 0.9;
}


.mini-progress-item {
  margin-bottom: 12px;
}

.p-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 6px;
  color: var(--text-secondary);
}

.p-track {
  height: 6px;
  background: var(--border-default);
  border-radius: 10px;
  overflow: hidden;
}

.p-thumb {
  height: 100%;
  border-radius: 10px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.toc-card--chat .p-thumb.temp {
  background: linear-gradient(90deg, #fbbf24, #ea580c);
}

.toc-card--chat .p-thumb.topp {
  background: linear-gradient(90deg, #818cf8, #4f46e5);
}


.capability-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.cap-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cap-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.toc-card--embedding .cap-icon.v-dim {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.toc-card--embedding .cap-icon.v-env {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.cap-info {
  display: flex;
  flex-direction: column;
}

.cap-info .l {
  font-size: 10px;
  color: var(--text-secondary);
  font-weight: 600;
}

.cap-info .v {
  font-size: 12px;
  color: var(--text-primary);
  font-weight: 700;
}

.image-spec-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.toc-card--image .spec-block {
  padding: 10px 8px;
  background: var(--bg-elevated);
  border: 1px solid rgba(251, 146, 60, 0.35);
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.toc-card--image .spec-block .s-label {
  font-size: 9px;
  color: #ea580c;
  font-weight: 800;
}

.toc-card--image .spec-block .s-value {
  font-size: 12px;
  color: var(--text-primary);
  font-weight: 700;
  word-break: break-all;
}

.other-spec {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.other-type-pill {
  align-self: flex-start;
  font-size: 11px;
  font-weight: 800;
  padding: 6px 12px;
  border-radius: 999px;
  background: var(--card-accent-soft);
  color: var(--card-accent-mid);
  border: 1px solid var(--card-hover-border);
}

.other-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-secondary);
  padding: 8px 10px;
  background: var(--bg-elevated);
  border-radius: 12px;
  border: 1px solid var(--content-border);
}

.other-row b {
  color: var(--text-primary);
  font-weight: 800;
}


.toc-card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: var(--text-secondary);
  padding: 0 4px;
}

.provider-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: var(--text-secondary);
}

.provider-info .m-icon {
  color: var(--card-accent);
  opacity: 0.85;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>