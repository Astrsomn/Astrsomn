<template>
  <article :class="{ selected: isSelected }" class="plugin-card">
    <div class="plugin-main">
      <div class="plugin-icon">
        <img v-if="item.avatar?.trim()" :alt="item.extensionName" :src="item.avatar" class="avatar-img"/>
        <component :is="iconComponent" v-else/>
      </div>
      <div class="plugin-info">
        <div v-if="showActions" class="status-row">
          <span :class="item.applied === 'Y' ? 'enabled' : 'disabled'" class="status-dot"/>
          <span :class="item.applied === 'Y' ? 'enabled' : 'disabled'" class="status-text">
            {{ item.applied === 'Y' ? '已启用' : '未启用' }}
          </span>
        </div>
        <h3 class="plugin-title">{{ item.extensionName || '未命名扩展' }}</h3>
        <div class="plugin-meta">By {{ item.author || 'Astrsomn' }} · {{ item.version || 'v1.0.0' }}</div>
        <div class="plugin-tags">
          <span class="tag">{{ extensionTypeLabel(item.type) }}</span>
          <span class="tag">{{ item.extensionCode || 'Provider' }}</span>
          <span class="tag soft">{{ item.jarName || 'classpath dependency' }}</span>
        </div>
      </div>
      <div class="plugin-desc">
        <p>{{ preview(item.description) }}</p>
      </div>
    </div>

    <div class="action-row">
      <template v-if="showActions">
        <a-button
            v-if="item.applied === 'Y' && item.type === 'MODEL_PROVIDER'"
            class="action-btn"
            type="default"
            @click="$emit('loadModels', item)"
        >
          <template #icon>
            <CloudDownloadOutlined/>
          </template>
          加载模型
        </a-button>
        <a-button
            v-if="item.applied === 'Y' && item.type === 'MODEL_PROVIDER'"
            class="action-btn"
            danger
            @click="$emit('unloadModels', item)"
        >
          <template #icon>
            <RestOutlined/>
          </template>
          卸载模型
        </a-button>
        <a-popconfirm
            v-if="item.applied === 'N'"
            cancel-text="取消"
            ok-text="确定"
            title="确定应用该插件吗？"
            @confirm="$emit('apply', item.id)"
        >
          <a-button class="action-btn" type="primary">
            <template #icon>
              <CaretRightOutlined/>
            </template>
            启用插件
          </a-button>
        </a-popconfirm>
        <a-popconfirm
            v-else
            cancel-text="取消"
            ok-text="确定"
            title="确定取消启用吗？插件将恢复为未启用状态。"
            @confirm="$emit('revokeApply', item.id)"
        >
          <a-button class="action-btn">
            <template #icon>
              <PauseOutlined/>
            </template>
            禁用插件
          </a-button>
        </a-popconfirm>
        <a-popconfirm
            v-if="isUninstallable"
            cancel-text="取消"
            ok-text="确定"
            title="确定卸载该插件吗？"
            @confirm="$emit('uninstall', item.id)"
        >
          <a-button class="action-btn icon-btn" danger>
            <template #icon>
              <DeleteOutlined/>
            </template>
          </a-button>
        </a-popconfirm>

      </template>
      <template v-else>
        <a-button class="action-btn" type="primary" @click="$emit('install', item)">
          <template #icon>
            <DownloadOutlined/>
          </template>
          安装到环境
        </a-button>
      </template>
    </div>
  </article>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {
  AppstoreOutlined,
  BuildOutlined,
  CaretRightOutlined,
  CloudDownloadOutlined,
  DeleteOutlined,
  DownloadOutlined,
  PauseOutlined,
  RestOutlined,
  RocketOutlined
} from '@ant-design/icons-vue'
import {
  type ExtensionRow,
  extensionTypeLabel,
  isUninstallableExtension,
  preview
} from '@/views/admin/system-config/system-extension/model-dialog/extensionDisplay.ts'

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
  border: 1px solid #f1f5f9;
  border-radius: var(--radius-md);
  padding: 18px;
  transition: all 0.3s ease;
  background: #fff;
  border-top: 1px solid var(--border-default, rgba(0, 0, 0, 0.1));
}

.plugin-card:hover {
  border-color: #dbeafe;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.plugin-card.selected {
  border-color: #3b82f6;
  background: #f8fafc;
}

.plugin-main {
  display: grid;
  grid-template-columns: 64px 1.2fr 1fr;
  gap: 16px;
  align-items: start;
}

.plugin-icon {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  background: #eff6ff;
  color: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.avatar-svg,
.avatar-img {
  width: 48px;
  height: 48px;
}

.avatar-img {
  border-radius: 8px;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  transition: background-color 0.3s ease;
}

.status-dot.enabled {
  background: #3b82f6;
}

.status-dot.disabled {
  background: #cbd5e1;
}

.status-text {
  font-size: 12px;
  font-weight: 700;
}

.status-text.enabled {
  color: #3b82f6;
}

.status-text.disabled {
  color: #94a3b8;
}

.plugin-title {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.plugin-meta {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.plugin-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.tag {
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 12px;
  color: #2563eb;
  background: #eff6ff;
}

.tag.soft {
  color: #94a3b8;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
}

.plugin-desc {
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

.plugin-desc p {
  margin: 0;
}

.action-row {
  margin-top: 14px;
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.action-btn {
  border-radius: 10px;
  transition: all 0.2s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.icon-btn {
  width: 40px;
  padding-inline: 0;
}

@media (max-width: 1200px) {
  .plugin-main {
    grid-template-columns: 64px 1fr;
  }

  .plugin-desc {
    grid-column: 1 / -1;
  }
}
</style>
