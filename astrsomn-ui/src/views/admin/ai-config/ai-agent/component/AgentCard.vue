<template>
  <div
      :class="{ 'is-disabled': record.status !== 'enabled', 'is-selected': selected }"
      class="agent-card-v3"
  >
    <div class="aurora-glow"></div>

    <div class="card-top-bar">
      <div :class="record.envCode" class="env-tag">
        <span class="status-dot"></span>
        {{ record.envCode || t.card.defaultEnv }}
      </div>
      <div class="card-checkbox">
        <a-checkbox :checked="selected" @change="onToggle"/>
      </div>
    </div>

    <div class="card-header">
      <div class="avatar-container">
        <div class="avatar-main">
          <span class="avatar-letter">{{ initialLetter }}</span>
        </div>
        <div :class="record.status" :title="record.status === 'enabled' ? t.card.running : t.card.disabled"
             class="status-badge"></div>
      </div>

      <div class="header-content">
        <div class="title-area">
          <h3 class="agent-name">{{ record.agentName }}</h3>
          <p class="create-time">{{ t.card.createdAt }} {{ formatTime(record.createTime) }}</p>
        </div>
      </div>
    </div>

    <div class="key-section" :title="t.card.clickToCopyKey" @click="copyAgentKey">
      <div class="key-label">
        <key-outlined/>
        <span>AGENT KEY</span>
      </div>
      <div class="key-value">
        <code>{{ record.agentKey || t.card.unassignedKey }}</code>
        <copy-outlined v-if="record.agentKey" class="copy-icon"/>
      </div>
    </div>

    <div class="card-body">
      <p class="description">{{ record.description || t.card.noDescription }}</p>

      <div class="config-grid">
        <div class="config-item config-item-model">
          <div class="item-label">
            <robot-outlined/>
            {{ t.card.modelInstance }}
          </div>
          <div class="item-value item-value-with-logo">
            <span
                v-if="providerAvatarMarkup"
                aria-hidden="true"
                class="provider-logo"
                v-html="providerAvatarMarkup"
            />
            <span class="highlight item-value-text">
              {{ record.chatInstanceName || record.modelName || t.card.notConfigured }}
            </span>
          </div>
        </div>
        <div class="config-item">
          <div class="item-label">
            <file-text-outlined/>
            {{ t.card.promptStrategy }}
          </div>
          <div class="item-value">
            {{ record.promptTitle || t.card.defaultStrategy }}
          </div>
        </div>
      </div>
    </div>

    <div class="card-footer">
      <button class="action-btn edit" @click="emit('edit', record)">
        <edit-outlined/>
        <span>{{ t.card.editConfig }}</span>
      </button>
      <div class="btn-divider"></div>
      <a-popconfirm :title="t.card.deleteConfirm" @confirm="onConfirmDelete">
        <button class="action-btn delete">
          <delete-outlined/>
          <span>{{ t.card.delete }}</span>
        </button>
      </a-popconfirm>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {message} from 'ant-design-vue'
import {usePageTranslation} from '@/locales/pages.ts'
import {
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  FileTextOutlined,
  KeyOutlined,
  RobotOutlined,
} from '@ant-design/icons-vue'

const props = defineProps<{ record: any; selected?: boolean }>()
const emit = defineEmits(['edit', 'delete', 'toggle'])

const t = usePageTranslation('ai-agent')


const onToggle = (e: any) => {
  emit('toggle', props.record.id, Boolean(e?.target?.checked))
}

const initialLetter = computed(() => props.record.agentName?.charAt(0).toUpperCase() || '?')

const providerAvatarMarkup = computed(() => {
  const raw = props.record?.providerAvatar
  return typeof raw === 'string' && raw.trim() ? raw.trim() : ''
})

const formatTime = (raw?: string) => {
  if (!raw) return '--'

  return raw.replace('T', ' ').slice(0, 16)
}

const onConfirmDelete = () => props.record.id && emit('delete', props.record.id)

const copyAgentKey = async () => {
  if (!props.record.agentKey) return
  try {
    await navigator.clipboard.writeText(props.record.agentKey)
    message.success(t.value.card.keyCopied)
  } catch {
    message.error(t.value.card.copyFailed)
  }
}
</script>

<style scoped>

.agent-card-v3 {
  --primary-blue: var(--primary);
  --light-blue: var(--primary-hover);
  --border-color: var(--border-default);
  --text-main: var(--text-heading);
  --text-sub: var(--text-muted);
  max-width: 400px;
  max-height: 400px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}


.aurora-glow {
  position: absolute;
  top: -60px;
  right: -60px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, color-mix(in srgb, var(--primary) 8%, transparent) 0%, transparent 70%);
  z-index: 0;
  pointer-events: none;
}


.agent-card-v3:hover {
  transform: translateY(-4px);
  border-color: var(--primary-light);
  box-shadow: var(--shadow-card);
}

.agent-card-v3.is-selected {
  border-color: var(--primary-blue);
  background: var(--bg-elevated);
  box-shadow: 0 0 0 2px var(--primary-hover);
}


.card-top-bar {
  padding: 16px 16px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.env-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: var(--bg-input);
  border-radius: 8px;
  font-size: 10px;
  font-weight: 800;
  color: var(--text-sub);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.env-tag.prod, .env-tag.production {
  background: var(--success);
  color: var(--text-heading);
}

.status-dot {
  width: 6px;
  height: 6px;
  background: currentColor;
  border-radius: 50%;
}


.card-header {
  padding: 16px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.avatar-main {
  width: 52px;
  height: 52px;
  background: var(--primary-gradient);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px -4px color-mix(in srgb, var(--primary) 30%, transparent);
}

.avatar-letter {
  color: white;
  font-size: 22px;
  font-weight: 800;
}

.status-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 3px solid var(--bg-card);
  background: var(--text-muted);
}

.status-badge.enabled {
  background: var(--success);
  box-shadow: 0 0 8px color-mix(in srgb, var(--success) 40%, transparent);
}

.header-content {
  flex: 1;
  min-width: 0;
}

.agent-name {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: var(--text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.create-time {
  font-size: 11px;
  color: var(--text-sub);
  margin-top: 2px;
}


.key-section {
  margin: 0 16px 16px;
  padding: 12px;
  background: var(--bg-elevated);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px dashed var(--border-default);
}

.key-section:hover {
  background: var(--light-blue);
  border-color: var(--primary-light);
}

.key-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 9px;
  font-weight: 800;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.key-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--primary-blue);
  font-family: ui-monospace, monospace;
  font-size: 12px;
}

.copy-icon {
  font-size: 12px;
  opacity: 0.5;
}


.card-body {
  padding: 0 16px 16px;
  flex: 1;
}

.description {
  font-size: 13px;
  color: var(--text-sub);
  line-height: 1.6;
  margin-bottom: 16px;
  height: 40px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.config-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.config-item {
  padding: 10px;
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 12px;
}

.item-label {
  font-size: 10px;
  color: var(--text-muted);
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-value {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-value.highlight {
  color: var(--primary-blue);
}

.item-value-with-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.provider-logo {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.provider-logo :deep(svg) {
  width: 22px;
  height: 22px;
  display: block;
}

.item-value-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}


.card-footer {
  display: flex;
  border-top: 1px solid var(--border-default);
  padding: 8px;
  gap: 8px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 0;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-sub);
  border-radius: 10px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: var(--bg-input);
  color: var(--primary-blue);
}

.action-btn.delete:hover {
  background: var(--error);
  color: var(--text-heading);
}

.btn-divider {
  width: 1px;
  height: 20px;
  background: var(--border-default);
  align-self: center;
}


.is-disabled {
  opacity: 0.7;
}

.is-disabled .avatar-main {
  filter: grayscale(1);
  opacity: 0.6;
}
</style>