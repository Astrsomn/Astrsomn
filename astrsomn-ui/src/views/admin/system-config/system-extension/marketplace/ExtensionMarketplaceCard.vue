<template>
  <div class="extension-card is-marketplace">
    <div class="card-top-meta">
      <div class="version-tag">{{ record.version || 'v1.0.0' }}</div>
    </div>

    <div class="card-inner">
      <header class="card-header">
        <div class="ext-icon-box">
          <component :is="getAntdIcon(record.type)" />
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
        <div class="action-bar-container">
          <a-button type="primary" block class="install-main-btn" @click="emit('install')">
            <template #icon><download-outlined /></template>安装到环境
          </a-button>
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
  DownloadOutlined
} from '@ant-design/icons-vue'
import { preview, type ExtensionRow } from '../shared/extensionDisplay'

defineProps<{
  record: ExtensionRow
}>()

const emit = defineEmits<{
  install: []
}>()

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

.card-top-meta {
  position: absolute;
  top: 1.5rem;
  left: 1.5rem;
  right: 1.5rem;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  z-index: 2;
}

.version-tag {
  font-size: 11px;
  font-family: 'JetBrains Mono', monospace;
  color: #cbd5e1;
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

.action-bar-container {
  margin-top: 0.5rem;
}

.install-main-btn {
  height: 48px !important;
  border-radius: 16px !important;
  font-weight: 800 !important;
  background: var(--primary) !important;
  box-shadow: 0 8px 16px -4px rgba(79, 70, 229, 0.3) !important;
}
</style>
