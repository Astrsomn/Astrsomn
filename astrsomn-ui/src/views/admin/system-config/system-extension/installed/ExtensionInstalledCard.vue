<template>
  <div class="extension-card" :class="{ 'is-selected': selected }">
    <div class="card-top-meta">
      <div class="status-indicator">
        <a-badge :status="record.applied === 'Y' ? 'processing' : 'default'" />
        <span class="status-text" :class="{ 'is-active': record.applied === 'Y' }">
          {{ record.applied === 'Y' ? '已启用' : '未启用' }}
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
          <span class="jar-name">{{ record.jarName || 'classpath dependency' }}</span>
        </div>

        <p class="desc-text">{{ preview(record.description) }}</p>
      </div>

      <footer class="card-footer">
        <div v-if="record.type === 'MODEL_PROVIDER' && record.applied === 'Y'" class="model-action-grid">
          <a-button size="small" class="action-btn btn-primary" @click="emit('load-models')">
            <template #icon><cloud-download-outlined /></template>加载模型
          </a-button>
          <a-button size="small" class="action-btn btn-danger" @click="emit('unload-models')">
            <template #icon><rest-outlined /></template>卸载模型
          </a-button>
        </div>

        <div class="action-bar-container">
          <div class="control-strip">
            <template v-if="record.applied === 'N'">
              <a-popconfirm
                title="确定应用该插件吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="emit('apply')"
              >
                <a-button size="small" class="strip-action btn-success">
                  <template #icon><caret-right-outlined /></template>
                  启用
                </a-button>
              </a-popconfirm>

              <template v-if="isUninstallableExtension(record)">
                <div class="strip-divider"></div>

                <a-popconfirm
                  title="确定卸载该插件吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="emit('uninstall')"
                >
                  <a-button size="small" class="strip-action btn-danger">
                    <template #icon><delete-outlined /></template>
                    卸载插件
                  </a-button>
                </a-popconfirm>
              </template>
            </template>
            <template v-else>
              <a-popconfirm
                title="确定取消启用吗？插件将恢复为「未启用」状态。"
                ok-text="确定"
                cancel-text="取消"
                @confirm="emit('revoke-apply')"
              >
                <a-button size="small" class="strip-action btn-warning">
                  <template #icon><pause-outlined /></template>
                  禁用插件
                </a-button>
              </a-popconfirm>
            </template>
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
import { isUninstallableExtension, preview, type ExtensionRow } from '../shared/extensionDisplay'

defineProps<{
  record: ExtensionRow
  selected?: boolean
}>()

const emit = defineEmits<{
  'toggle-select': [checked: boolean]
  'load-models': []
  'unload-models': []
  'revoke-apply': []
  uninstall: []
  apply: []
}>()

const onToggle = (e: { target: { checked: boolean } }) => {
  emit('toggle-select', e.target.checked)
}

const getAntdIcon = (type?: string) => {
  if (type === 'MODEL_PROVIDER') return RocketOutlined
  if (type === 'TOOL') return BuildOutlined
  return AppstoreOutlined
}
</script>

<style scoped>
.extension-card {
  --primary: #4f46e5;
  --bg-soft: #f8fafc;
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;

  position: relative;
  background: #ffffff;
  border-radius: 2.5rem;
  border: 1px solid #f1f5f9;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  min-height: 380px;
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
  background: rgba(255, 255, 255, 0.85);
  padding: 4px 12px;
  border-radius: 1rem;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.status-text {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
}

.status-text.is-active {
  color: var(--success);
}

.version-tag {
  font-size: 11px;
  font-family: 'JetBrains Mono', monospace;
  color: #cbd5e1;
}

.card-checkbox {
  position: absolute;
  top: 3.5rem;
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
  margin-bottom: 1.2rem;
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
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* --- 按钮优化部分 --- */

.card-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.model-action-grid {
  display: flex;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px dashed #f1f5f9;
}

.action-bar-container {
  width: 100%;
}

.control-strip {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 通用按钮样式优化 */
.action-btn, .strip-action {
  flex: 1;
  min-height: 42px;
  font-weight: 700 !important;
  font-size: 13px !important;
  border-radius: 14px !important;
  border: none !important;
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  color: #ffffff !important;
}

.action-btn:hover, .strip-action:hover {
  transform: translateY(-2px);
  filter: brightness(1.1);
}

.action-btn:active, .strip-action:active {
  transform: translateY(0);
}

/* 颜色分类 - 使用渐变增强视觉感 */
.btn-primary {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%) !important;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.25) !important;
}
.btn-primary:hover {
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.35) !important;
}

.btn-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.25) !important;
}
.btn-success:hover {
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.35) !important;
}

.btn-danger {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%) !important;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25) !important;
}
.btn-danger:hover {
  box-shadow: 0 6px 16px rgba(239, 68, 68, 0.35) !important;
}

.btn-warning {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%) !important;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.25) !important;
}
.btn-warning:hover {
  box-shadow: 0 6px 16px rgba(245, 158, 11, 0.35) !important;
}

.strip-divider {
  width: 1px;
  height: 24px;
  background: #f1f5f9;
  margin: 0 2px;
}

:deep(.ant-checkbox-inner) {
  border-radius: 6px;
}
</style>