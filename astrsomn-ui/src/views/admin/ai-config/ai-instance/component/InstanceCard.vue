<template>
  <div
      :class="['instance-card', `instance-card--${cardVariant}`, { 'is-selected': selected, 'is-disabled': record.status !== 'enabled' }]"
      @click="emit('edit')"
  >
    <a-checkbox
        :checked="selected"
        class="card-checkbox"
        @click.stop="emit('toggle-select')"
    />

    <!-- Card Header -->
    <div class="card-header">
      <div class="card-avatar">
        <img v-if="providerAvatar" :src="providerAvatar" alt="" class="avatar-img"/>
        <div v-else :class="['type-icon-box', getModelTypeClass(record)]">
          <component :is="getModelIcon(record)"/>
        </div>
      </div>
      <div class="card-header-text">
        <h3 class="card-title">{{ record.instanceName || t.card.unnamed }}</h3>
        <span :class="['card-tag', cardVariant]">{{ typeLabel }}</span>
      </div>
    </div>

    <!-- Card Info -->
    <p class="card-info">{{ record.instanceKey || '—' }}</p>

    <!-- Dynamic Content -->
    <div class="card-params">
      <template v-if="isChat">
        <div class="param-row">
          <span class="param-label">{{ t.card.chat.temperature }}</span>
          <span class="param-value">{{ record.temperature ?? 0.7 }}</span>
        </div>
        <div class="param-row">
          <span class="param-label">{{ t.card.chat.topP }}</span>
          <span class="param-value">{{ record.topP ?? 1.0 }}</span>
        </div>
      </template>
      <template v-else-if="isEmbedding">
        <div class="param-row">
          <span class="param-label">{{ t.card.embedding.dimensions }}</span>
          <span class="param-value">{{ record.dimensions || 1536 }}D</span>
        </div>
        <div class="param-row">
          <span class="param-label">{{ t.card.embedding.env }}</span>
          <span class="param-value">{{ record.envCode || 'PROD' }}</span>
        </div>
      </template>
      <template v-else-if="isImage">
        <div class="param-row">
          <span class="param-label">{{ t.card.image.size }}</span>
          <span class="param-value">{{ imageSizeLabel }}</span>
        </div>
        <div class="param-row">
          <span class="param-label">{{ t.card.image.style }}</span>
          <span class="param-value">{{ imageStyleLabel }}</span>
        </div>
      </template>
      <template v-else>
        <div v-if="record.maxTokens != null" class="param-row">
          <span class="param-label">{{ t.card.other.maxOutput }}</span>
          <span class="param-value">{{ record.maxTokens }}</span>
        </div>
        <div v-if="record.topK != null" class="param-row">
          <span class="param-label">TopK</span>
          <span class="param-value">{{ record.topK }}</span>
        </div>
      </template>
    </div>

    <!-- Card Footer -->
    <div class="card-footer">
      <span class="meta-chip">
        <span :class="['status-dot', record.status === 'enabled' ? 'active' : '']"/>
        {{ record.status === 'enabled' ? t.card.status.enabled : t.card.status.disabled }}
      </span>
      <span class="meta-spacer"/>
      <a-popconfirm :title="t.card.deleteConfirm" @confirm="emit('delete')">
        <button class="action-delete-btn" @click.stop><DeleteOutlined/></button>
      </a-popconfirm>
      <button class="action-edit-link" @click.stop="emit('edit')">
        {{ t.card.quickEdit }} <RightOutlined/>
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  DeleteOutlined,
  FileImageOutlined,
  MessageOutlined,
  PartitionOutlined,
  RightOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'
import type {AiInstance} from '@/api/aiInstance.ts'
import {usePageTranslation} from '@/locales/pages.ts'

type InstanceCardVariant = 'chat' | 'embedding' | 'image' | 'other'

const t = usePageTranslation('ai-instance')

const props = defineProps<{
  record: AiInstance
  index: number
  selected: boolean
}>()

const emit = defineEmits(['toggle-select', 'edit', 'delete'])

function normalizeVariant(r: AiInstance): InstanceCardVariant {
  const t2 = (r.modelType || 'chat').toLowerCase().trim()
  if (t2 === 'embedding') return 'embedding'
  if (t2 === 'image') return 'image'
  if (t2 === 'chat' || t2 === '') return 'chat'
  return 'other'
}

const cardVariant = computed(() => normalizeVariant(props.record))
const isChat = computed(() => cardVariant.value === 'chat')
const isEmbedding = computed(() => cardVariant.value === 'embedding')
const isImage = computed(() => cardVariant.value === 'image')

const typeLabel = computed(() => {
  const raw = props.record.modelType?.trim()
  if (!raw) return t.value.modelType.chat
  const map: Record<string, string> = {
    chat: t.value.modelType.chat,
    embedding: t.value.modelType.embedding,
    image: t.value.modelType.image
  }
  return map[raw.toLowerCase()] || raw
})

const imageSizeLabel = computed(() => props.record.size?.trim() || t.value.card.unspecified)
const imageStyleLabel = computed(() => props.record.style?.trim() || t.value.card.defaultStyle)

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

const providerAvatar = computed(() => {
  const raw = props.record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
})
</script>

<style scoped>
.instance-card {
  --card-accent: #6366f1;

  position: relative;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 140px;
}

.instance-card:hover {
  border-color: var(--card-accent);
  box-shadow: 0 4px 20px -4px color-mix(in srgb, var(--card-accent) 15%, transparent);
  transform: translateY(-1px);
}

.instance-card.is-disabled {
  opacity: 0.6;
}

.instance-card.is-selected {
  border-color: var(--card-accent);
  box-shadow: 0 0 0 1px var(--card-accent);
}

/* ── Selection Checkbox ── */
.card-checkbox {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.15s;
}

.instance-card:hover .card-checkbox,
.instance-card.is-selected .card-checkbox {
  opacity: 1;
}

/* ── Card Header ── */
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-avatar {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 10px;
}

.type-icon-box {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
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

.card-header-text {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.card-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tag {
  font-size: 9px;
  font-weight: 600;
  padding: 1px 7px;
  border-radius: 10px;
  flex-shrink: 0;
  line-height: 18px;
}

.card-tag.chat {
  background: #eff6ff;
  color: #2563eb;
}

.card-tag.embedding {
  background: #ecfdf5;
  color: #047857;
}

.card-tag.image {
  background: #fff7ed;
  color: #c2410c;
}

.card-tag.other {
  background: #f5f3ff;
  color: #7c3aed;
}

/* ── Card Info ── */
.card-info {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.6;
  margin: 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ── Card Params ── */
.card-params {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 12px;
  background: var(--bg-surface);
  border-radius: 8px;
}

.param-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.param-label {
  color: var(--text-muted);
  font-weight: 500;
}

.param-value {
  color: var(--text-primary);
  font-weight: 600;
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
}

/* ── Card Footer ── */
.card-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto;
}

.meta-chip {
  font-size: 10px;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 7px;
  background: var(--bg-elevated);
  border-radius: 6px;
  font-weight: 500;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--text-muted);
  flex-shrink: 0;
}

.status-dot.active {
  background: #10b981;
}

.meta-spacer {
  flex: 1;
}

/* ── Action Buttons (reveal on hover) ── */
.action-delete-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border: 1px solid var(--border-subtle);
  border-radius: 6px;
  background: var(--bg-elevated);
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.15s;
  flex-shrink: 0;
  padding: 0;
  opacity: 0;
}

.instance-card:hover .action-delete-btn {
  opacity: 1;
}

.action-delete-btn:hover {
  color: var(--error);
  border-color: var(--error);
  background: color-mix(in srgb, var(--error) 8%, transparent);
}

.action-edit-link {
  font-size: 10px;
  color: var(--card-accent);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.15s;
}

.instance-card:hover .action-edit-link {
  opacity: 1;
}

.action-edit-link :deep(svg) {
  font-size: 8px;
}
</style>
