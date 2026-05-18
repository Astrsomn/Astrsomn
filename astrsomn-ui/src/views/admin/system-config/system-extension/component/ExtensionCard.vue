<template>
  <article :class="{ selected: isSelected }" class="plugin-card">
    <div class="plugin-icon">
      <img v-if="item.avatar?.trim()" :alt="item.extensionName" :src="item.avatar" class="avatar-img" />
      <component :is="iconComponent" v-else />
    </div>

    <div class="plugin-info">
      <div class="info-head">
        <span v-if="showActions" :class="item.applied === 'Y' ? 'enabled' : 'disabled'" class="status-dot" />
        <h3 class="plugin-title">{{ item.extensionName || '未命名扩展' }}</h3>
        <span class="plugin-meta">By {{ item.author || 'Astrsomn' }} · {{ item.version || 'v1.0.0' }}</span>
      </div>
      <div class="info-tags">
        <span class="tag type-tag">{{ extensionTypeLabel(item.type) }}</span>
        <span class="tag code-tag">{{ item.extensionCode || 'Provider' }}</span>
        <span class="tag jar-tag">{{ item.jarName || 'classpath dependency' }}</span>
      </div>
    </div>

    <div class="plugin-desc">
      <p>{{ preview(item.description) }}</p>
    </div>

    <div class="action-row">
      <template v-if="showActions">
        <a-button
          v-if="item.applied === 'Y' && item.type === 'MODEL_PROVIDER'"
          size="small"
          class="action-btn"
          type="default"
          @click="$emit('loadModels', item)"
        >
          <template #icon><CloudDownloadOutlined /></template>
          加载模型
        </a-button>
        <a-button
          v-if="item.applied === 'Y' && item.type === 'MODEL_PROVIDER'"
          size="small"
          class="action-btn"
          danger
          @click="$emit('unloadModels', item)"
        >
          <template #icon><RestOutlined /></template>
          卸载模型
        </a-button>
        <a-popconfirm
          v-if="item.applied === 'N'"
          cancel-text="取消"
          ok-text="确定"
          title="确定应用该插件吗？"
          @confirm="$emit('apply', item.id)"
        >
          <a-button size="small" class="action-btn" type="primary">
            <template #icon><CaretRightOutlined /></template>
            启用
          </a-button>
        </a-popconfirm>
        <a-popconfirm
          v-else
          cancel-text="取消"
          ok-text="确定"
          title="确定取消启用吗？插件将恢复为未启用状态。"
          @confirm="$emit('revokeApply', item.id)"
        >
          <a-button size="small" class="action-btn">
            <template #icon><PauseOutlined /></template>
            禁用
          </a-button>
        </a-popconfirm>
        <a-popconfirm
          v-if="isUninstallable"
          cancel-text="取消"
          ok-text="确定"
          title="确定卸载该插件吗？"
          @confirm="$emit('uninstall', item.id)"
        >
          <a-button size="small" class="action-btn icon-only" danger>
            <template #icon><DeleteOutlined /></template>
          </a-button>
        </a-popconfirm>
      </template>
      <template v-else>
        <a-button v-if="item.installed" size="small" class="action-btn" disabled>
          <template #icon><CheckCircleOutlined /></template>
          已安装
        </a-button>
        <a-button v-else size="small" class="action-btn" type="primary" @click="$emit('install', item)">
          <template #icon><DownloadOutlined /></template>
          安装到环境
        </a-button>
      </template>
    </div>
  </article>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import {
  AppstoreOutlined,
  BuildOutlined,
  CaretRightOutlined,
  CheckCircleOutlined,
  CloudDownloadOutlined,
  DeleteOutlined,
  DownloadOutlined,
  PauseOutlined,
  RestOutlined,
  RocketOutlined,
} from '@ant-design/icons-vue'
import {
  type ExtensionRow,
  extensionTypeLabel,
  isUninstallableExtension,
  preview,
} from '@/views/admin/system-config/system-extension/utils/extensionDisplay.ts'

const props = defineProps<{
  item: ExtensionRow
  showActions?: boolean
  isSelected?: boolean
}>()

defineEmits<{
  (e: 'loadModels', item: ExtensionRow): void
  (e: 'unloadModels', item: ExtensionRow): void
  (e: 'apply', id: number | string | undefined): void
  (e: 'revokeApply', id: number | string | undefined): void
  (e: 'uninstall', id: number | string | undefined): void
  (e: 'install', item: ExtensionRow): void
  (e: 'toggleSelect', item: ExtensionRow, checked: boolean): void
}>()

const iconComponent = computed(() => {
  if (props.item.type === 'MODEL_PROVIDER') return RocketOutlined
  if (props.item.type === 'VECTOR_STORE') return BuildOutlined
  return AppstoreOutlined
})

const isUninstallable = computed(() => isUninstallableExtension(props.item))
</script>

<style scoped>
.plugin-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 16px;
  border: 1px solid var(--border-default);
  border-radius: 10px;
  background: var(--bg-card);
  transition: all 0.2s ease;
}

.plugin-card:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: var(--bg-elevated);
}

.plugin-card.selected {
  border-color: var(--primary);
  background: rgba(59, 130, 246, 0.08);
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.15);
}


.plugin-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: var(--primary-hover);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.avatar-img {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  object-fit: cover;
}


.plugin-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-head {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  flex-shrink: 0;
}

.status-dot.enabled {
  background: var(--primary);
  box-shadow: 0 0 4px rgba(59, 130, 246, 0.5);
}

.status-dot.disabled {
  background: var(--text-muted);
}

.plugin-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.plugin-meta {
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
  flex-shrink: 0;
}

.info-tags {
  display: flex;
  gap: 6px;
  flex-wrap: nowrap;
}

.tag {
  padding: 1px 7px;
  border-radius: 5px;
  font-size: 11px;
  line-height: 18px;
  white-space: nowrap;
}

.type-tag {
  color: var(--primary);
  background: rgba(59, 130, 246, 0.12);
}

.code-tag {
  color: var(--text-secondary);
  background: var(--bg-surface);
  border: 1px solid var(--border-default);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.jar-tag {
  color: var(--text-muted);
  background: transparent;
  border: 1px solid var(--border-subtle, rgba(255, 255, 255, 0.06));
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
}


.plugin-desc {
  width: 180px;
  flex-shrink: 0;
}

.plugin-desc p {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}


.action-row {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-shrink: 0;
}

.action-btn {
  border-radius: 7px;
  font-size: 12px;
  height: 30px;
  padding: 0 10px;
  transition: all 0.15s ease;
}

.action-btn.icon-only {
  width: 30px;
  padding: 0;
}


@media (max-width: 1100px) {
  .plugin-desc {
    display: none;
  }
}

@media (max-width: 768px) {
  .plugin-card {
    flex-wrap: wrap;
    gap: 8px;
    padding: 10px 12px;
  }

  .action-row {
    width: 100%;
    justify-content: flex-end;
  }

  .plugin-desc {
    display: none;
  }
}
</style>
