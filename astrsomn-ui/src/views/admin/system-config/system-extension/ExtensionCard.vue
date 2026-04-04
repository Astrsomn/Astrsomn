<template>
  <div class="extension-card" :class="{ 'is-selected': selected, 'is-marketplace': listScope === 'MARKETPLACE' }">
    <div v-if="listScope === 'INSTALLED'" class="card-checkbox">
      <a-checkbox :checked="selected" @change="onToggle" />
    </div>

    <div class="card-accent" aria-hidden="true" />

    <div class="card-inner">
      <header class="card-header">
        <div class="title-block">
          <h3 class="ext-name">{{ record.extensionName || '未命名扩展' }}</h3>
          <code class="ext-key">{{ record.extensionKey || '—' }}</code>
        </div>
        <div class="tag-row">
          <span class="type-pill">{{ extensionTypeLabel(record.type) }}</span>
          <template v-if="listScope === 'INSTALLED'">
            <a-tag :color="statusTagColor(record.status)">{{ statusLabel(record.status) }}</a-tag>
            <a-tag :color="appliedTagColor(record.applied)">{{ appliedLabel(record.applied) }}</a-tag>
          </template>
        </div>
      </header>

      <dl class="meta-grid">
        <div class="meta-item">
          <dt>版本</dt>
          <dd>{{ record.version || '—' }}</dd>
        </div>
        <div class="meta-item">
          <dt>作者</dt>
          <dd>{{ record.author || '—' }}</dd>
        </div>
        <div class="meta-item meta-full">
          <dt>jarName</dt>
          <dd><code class="jar-code">{{ record.jarName || '—' }}</code></dd>
        </div>
      </dl>

      <p class="desc-preview">{{ preview(record.description) }}</p>

      <footer class="card-actions">
        <template v-if="listScope === 'MARKETPLACE'">
          <a-button type="primary" ghost class="action-primary" @click="emit('install')">安装</a-button>
        </template>
        <template v-else>
          <template v-if="record.type === 'MODEL_PROVIDER'">
            <a-button type="link" class="action-link" :disabled="record.id == null" @click="emit('load-models')">
              加载模型
            </a-button>
            <a-button type="link" class="action-link" danger :disabled="record.id == null" @click="emit('unload-models')">
              卸载模型
            </a-button>
            <a-popconfirm
              title="将该厂商在当前环境下的全部 AI 模型状态设为停用（disabled），确认？"
              ok-text="确认"
              cancel-text="取消"
              @confirm="emit('disable-provider-models')"
            >
              <a-button type="link" class="action-link" :disabled="record.id == null">禁用</a-button>
            </a-popconfirm>
          </template>

          <a-popconfirm
            v-if="record.status === 'APPLIED'"
            :title="
              record.type === 'MODEL_PROVIDER'
                ? '卸载插件。模型类扩展：若仍有 AI 实例引用该厂商模型，服务端将拒绝卸载。'
                : '确定卸载该插件吗？'
            "
            ok-text="确认"
            cancel-text="取消"
            @confirm="emit('uninstall')"
          >
            <a-button type="link" danger class="action-link" :disabled="record.id == null">卸载</a-button>
          </a-popconfirm>

          <a-popconfirm
            v-else
            title="确定应用该插件吗？"
            ok-text="确认"
            cancel-text="取消"
            @confirm="emit('apply')"
          >
            <a-button type="link" class="action-link" :disabled="!record.jarName || record.id == null">应用</a-button>
          </a-popconfirm>
        </template>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  appliedLabel,
  appliedTagColor,
  extensionTypeLabel,
  preview,
  statusLabel,
  statusTagColor,
  type ExtensionRow
} from './extensionDisplay'
import type { SystemExtensionListScope } from '@/api/systemExtension'

defineProps<{
  record: ExtensionRow
  listScope: SystemExtensionListScope
  selected?: boolean
}>()

const emit = defineEmits<{
  'toggle-select': [checked: boolean]
  install: []
  'load-models': []
  'unload-models': []
  'disable-provider-models': []
  uninstall: []
  apply: []
}>()

const onToggle = (e: { target: { checked: boolean } }) => {
  emit('toggle-select', e.target.checked)
}
</script>

<style scoped>
.extension-card {
  position: relative;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
  min-height: 0;
}

.extension-card:hover {
  border-color: color-mix(in srgb, var(--primary) 45%, var(--border-default));
  box-shadow:
    var(--shadow-card),
    0 0 0 1px color-mix(in srgb, var(--primary) 18%, transparent);
  transform: translateY(-2px);
}

.extension-card.is-selected {
  border-color: var(--primary);
  box-shadow:
    0 0 0 1px color-mix(in srgb, var(--primary) 35%, transparent),
    0 12px 32px color-mix(in srgb, var(--primary) 12%, transparent);
}

.card-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--primary-gradient);
  opacity: 0.85;
}

.card-checkbox {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
}

.card-inner {
  padding: 16px 16px 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.extension-card.is-marketplace .card-inner {
  padding-right: 16px;
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-right: 28px;
}

.extension-card.is-marketplace .card-header {
  padding-right: 0;
}

.title-block {
  min-width: 0;
}

.ext-name {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
  letter-spacing: -0.02em;
  line-height: 1.3;
}

.ext-key {
  display: inline-block;
  max-width: 100%;
  font-size: 11px;
  padding: 3px 8px;
  border-radius: var(--radius-sm);
  background: var(--bg-input);
  border: 1px solid var(--border-input);
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.type-pill {
  font-size: 12px;
  font-weight: 600;
  color: var(--section-title);
  padding: 2px 10px;
  border-radius: var(--radius-max);
  background: color-mix(in srgb, var(--primary) 14%, var(--bg-elevated));
  border: 1px solid color-mix(in srgb, var(--primary) 28%, var(--border-default));
}

.meta-grid {
  margin: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 12px;
}

.meta-item {
  margin: 0;
}

.meta-item dt {
  margin: 0 0 2px;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.meta-item dd {
  margin: 0;
  font-size: 13px;
  color: var(--text-primary);
  word-break: break-word;
}

.meta-full {
  grid-column: 1 / -1;
}

.jar-code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-input);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-subtle);
  word-break: break-all;
  white-space: pre-wrap;
}

.desc-preview {
  margin: 0;
  font-size: 12px;
  line-height: 1.55;
  color: var(--text-muted);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px 2px;
  padding-top: 4px;
  border-top: 1px solid var(--border-subtle);
  margin-top: 2px;
}

.action-link {
  padding: 0 6px;
  height: auto;
  color: var(--primary-light);
}

.action-link:hover {
  color: var(--text-hover) !important;
}

.action-primary {
  border-color: color-mix(in srgb, var(--primary) 45%, var(--border-default));
  color: var(--primary-light);
}

.action-primary:hover {
  border-color: var(--primary) !important;
  color: var(--text-heading) !important;
  background: color-mix(in srgb, var(--primary) 12%, var(--bg-elevated)) !important;
}
</style>
