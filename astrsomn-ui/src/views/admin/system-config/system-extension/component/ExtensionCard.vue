<template>
  <article :class="{ selected: isSelected }" class="plugin-card">
    <div class="plugin-icon">
      <img v-if="item.avatar?.trim()" :alt="item.extensionName" :src="item.avatar" class="avatar-img" />
      <component :is="iconComponent" v-else />
    </div>

    <div class="plugin-info">
      <div class="info-head">
        <span v-if="showActions" :class="item.applied === 'Y' ? 'enabled' : 'disabled'" class="status-dot" />
        <h3 class="plugin-title">{{ item.extensionName || t.card.unnamedExtension }}</h3>
        <span class="plugin-meta">By {{ item.author || 'Astrsomn' }} · {{ item.version || 'v1.0.0' }}</span>
      </div>
      <div class="info-tags">
        <span class="tag type-tag">{{ extensionTypeLabel(item.type) }}</span>
        <span class="tag code-tag">{{ item.extensionCode || item.providerCode || 'Provider' }}</span>
        <span class="tag jar-tag">{{ item.jarName || 'classpath dependency' }}</span>
      </div>
    </div>

    <div class="plugin-desc">
      <p>{{ preview(item.description) }}</p>
    </div>

    <div class="action-row">
      <template v-if="showActions">
        <a-popconfirm
          v-if="item.applied === 'N'"
          :cancel-text="t.card.cancel"
          :ok-text="t.card.confirm"
          :title="t.card.confirmApply"
          @confirm="$emit('apply', item.id)"
        >
          <a-button size="small" class="action-btn" type="primary">
            <template #icon><CaretRightOutlined /></template>
            {{ t.card.apply }}
          </a-button>
        </a-popconfirm>
        <a-popconfirm
          v-else
          :cancel-text="t.card.cancel"
          :ok-text="t.card.confirm"
          :title="t.card.confirmDisable"
          @confirm="$emit('revokeApply', item.id)"
        >
          <a-button size="small" class="action-btn">
            <template #icon><PauseOutlined /></template>
            {{ t.card.disable }}
          </a-button>
        </a-popconfirm>
        <a-popconfirm
          v-if="isUninstallable"
          :cancel-text="t.card.cancel"
          :ok-text="t.card.confirm"
          :title="t.card.confirmUninstall"
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
          {{ t.card.installed }}
        </a-button>
        <a-button v-else size="small" class="action-btn" type="primary" @click="$emit('install', item)">
          <template #icon><DownloadOutlined /></template>
          {{ t.card.install }}
        </a-button>
      </template>
    </div>
  </article>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {usePageTranslation} from '@/locales/pages.ts'
import {
  AppstoreOutlined,
  BuildOutlined,
  CaretRightOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  DownloadOutlined,
  PauseOutlined,
  RocketOutlined,
} from '@ant-design/icons-vue'
import {
  type ExtensionRow,
  extensionTypeLabel,
  isUninstallableExtension,
  preview,
} from '@/views/admin/system-config/system-extension/utils/extensionDisplay.ts'

const t = usePageTranslation('system-extension')

const props = defineProps<{
  item: ExtensionRow
  showActions?: boolean
  isSelected?: boolean
}>()

defineEmits<{
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
  border-color: color-mix(in srgb, var(--primary) 35%, transparent);
  background: var(--bg-elevated);
}

.plugin-card.selected {
  border-color: var(--primary);
  background: color-mix(in srgb, var(--primary) 8%, transparent);
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--primary) 15%, transparent);
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
  box-shadow: 0 0 4px color-mix(in srgb, var(--primary) 50%, transparent);
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
  background: color-mix(in srgb, var(--primary) 12%, transparent);
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
  border: 1px solid var(--border-subtle);
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
