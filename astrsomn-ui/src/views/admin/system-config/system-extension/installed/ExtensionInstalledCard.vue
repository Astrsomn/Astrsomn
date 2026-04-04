<template>
  <div class="extension-card" :class="{ 'is-selected': selected }">
    <div class="card-top-meta">
      <div class="status-indicator">
        <a-badge :status="record.status === 'APPLIED' ? 'processing' : 'default'" />
        <span class="status-text" :class="{ 'is-active': record.status === 'APPLIED' }">
          {{ statusLabel(record.status) }}
        </span>
      </div>
      <div class="version-tag">{{ record.version || 'v1.0.0' }}</div>
    </div>

    <div class="card-checkbox">
      <a-checkbox :checked="selected" @change="onToggle" />
    </div>

    <div class="card-inner">
      <header class="card-header">
        <div class="ext-icon-box">
          <span
            v-if="record.avatar?.trim()"
            class="ext-avatar-svg"
            v-html="record.avatar"
            aria-hidden="true"
          />
          <component v-else :is="getAntdIcon(record.type)" />
        </div>
        <div class="title-group">
          <h3 class="ext-name">{{ record.extensionName || '未命名扩展' }}</h3>
          <span class="author-info">By {{ record.author || 'Astrsomn' }}</span>
        </div>
      </header>

      <div class="info-content">
        <div class="key-badge">
          <code class="ext-key">{{ record.extensionKey || '—' }}</code>
        </div>

        <div class="jar-pill">
          <paper-clip-outlined class="jar-icon" />
          <span class="jar-name">{{ record.jarName || 'empty.jar' }}</span>
        </div>

        <p class="desc-text">{{ preview(record.description) }}</p>
      </div>

      <footer class="card-footer">
        <div v-if="record.type === 'MODEL_PROVIDER'" class="model-action-grid">
          <a-button type="text" size="small" class="action-btn" @click="emit('load-models')">
            <template #icon><cloud-download-outlined /></template>加载模型
          </a-button>
          <a-button type="text" size="small" class="action-btn danger-text" @click="emit('unload-models')">
            <template #icon><rest-outlined /></template>卸载模型
          </a-button>
        </div>

        <div class="action-bar-container">
          <div class="control-strip">
            <a-popconfirm title="确定应用该插件吗？" @confirm="emit('apply')" v-if="record.status !== 'APPLIED'">
              <button class="icon-control-btn success" :disabled="!record.jarName">
                <caret-right-outlined />
              </button>
            </a-popconfirm>

            <a-popconfirm title="确定禁用此插件吗？" @confirm="emit('disable-provider-models')" v-else>
              <button class="icon-control-btn warning">
                <pause-outlined />
              </button>
            </a-popconfirm>

            <div class="strip-divider"></div>

            <a-popconfirm title="确定卸载该插件吗？" @confirm="emit('uninstall')">
              <button class="icon-control-btn danger">
                <delete-outlined />
              </button>
            </a-popconfirm>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  RocketOutlined,
  BuildOutlined,
  AppstoreOutlined,
  PaperClipOutlined,
  CloudDownloadOutlined,
  RestOutlined,
  CaretRightOutlined,
  PauseOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import { preview, statusLabel, type ExtensionRow } from '../shared/extensionDisplay'

defineProps<{
  record: ExtensionRow
  selected?: boolean
}>()

const emit = defineEmits<{
  'toggle-select': [checked: boolean]
  'load-models': []
  'unload-models': []
  'disable-provider-models': []
  uninstall: []
  apply: []
}>()

const onToggle = (e: { target: { checked: boolean } }) => {
  emit('toggle-select', e.target.checked)
}

const getAntdIcon = (type: string) => {
  if (type === 'MODEL_PROVIDER') return RocketOutlined
  if (type === 'TOOL') return BuildOutlined
  return AppstoreOutlined
}
</script>

<style scoped>
.extension-card {
  --primary: #4f46e5;
  --bg-soft: #f8fafc;

  position: relative;
  background: #ffffff;
  border-radius: 2.5rem;
  border: 1px solid #f1f5f9;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.extension-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px -12px rgba(79, 70, 229, 0.1);
  border-color: var(--primary);
}

.extension-card.is-selected {
  border-width: 2px;
  border-color: var(--primary);
  background: #fdfdff;
}

.card-top-meta {
  position: absolute;
  top: 1.5rem;
  left: 1.5rem;
  right: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 2;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.8);
  padding: 4px 12px;
  border-radius: 1rem;
  backdrop-filter: blur(4px);
}

.status-text {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
}

.status-text.is-active {
  color: #10b981;
}

.version-tag {
  font-size: 11px;
  font-family: 'JetBrains Mono', monospace;
  color: #cbd5e1;
}

.card-checkbox {
  position: absolute;
  top: 4rem;
  right: 1.5rem;
}

.card-inner {
  padding: 3.5rem 2rem 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1.25rem;
  margin-bottom: 1.5rem;
}

.ext-icon-box {
  width: 3.5rem;
  height: 3.5rem;
  background: var(--bg-soft);
  color: var(--primary);
  border-radius: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.6rem;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.02);
}

.ext-avatar-svg {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.ext-icon-box :deep(svg) {
  width: 2rem;
  height: 2rem;
  max-width: 90%;
  max-height: 90%;
}

.ext-name {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 800;
  color: #1e293b;
  letter-spacing: -0.02em;
}

.author-info {
  font-size: 12px;
  color: #94a3b8;
}

.info-content {
  flex: 1;
}

.ext-key {
  font-size: 10px;
  background: #f1f5f9;
  color: #64748b;
  padding: 2px 8px;
  border-radius: 6px;
}

.jar-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #fffbeb;
  color: #b45309;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  margin: 1rem 0;
  border: 1px solid #fef3c7;
}

.desc-text {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 1.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.model-action-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 12px 0;
  border-top: 1px dashed #f1f5f9;
}

.action-btn {
  font-weight: 700 !important;
  font-size: 12px !important;
  color: #64748b !important;
}

.danger-text {
  color: #ef4444 !important;
}

.action-bar-container {
  margin-top: 0.5rem;
}

.control-strip {
  background: var(--bg-soft);
  padding: 8px;
  border-radius: 1.75rem;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
}

.icon-control-btn {
  width: 42px;
  height: 42px;
  border-radius: 1.25rem;
  border: none;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  font-size: 1.1rem;
}

.icon-control-btn.success {
  color: #10b981;
}
.icon-control-btn.success:hover {
  background: #10b981;
  color: #fff;
  transform: scale(1.1);
}

.icon-control-btn.warning {
  color: #f59e0b;
}
.icon-control-btn.warning:hover {
  background: #f59e0b;
  color: #fff;
  transform: scale(1.1);
}

.icon-control-btn.danger {
  color: #94a3b8;
}
.icon-control-btn.danger:hover {
  background: #ef4444;
  color: #fff;
  transform: scale(1.1);
}

.strip-divider {
  width: 1px;
  height: 24px;
  background: #e2e8f0;
}

:deep(.ant-checkbox-inner) {
  border-radius: 6px;
}
</style>
