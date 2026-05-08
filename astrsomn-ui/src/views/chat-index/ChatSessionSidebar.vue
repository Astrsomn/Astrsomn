<template>
  <div class="sidebar-shell" :class="{ collapsed }">
    <aside class="session-sidebar">
      <div class="sidebar-top">
        <a-button class="new-chat-btn" @click="emit('create')">
          <template #icon><PlusOutlined /></template>
          <span>新会话</span>
        </a-button>
        <a-button size="small" type="text" class="collapse-btn" @click="toggleCollapsed">
          <MenuFoldOutlined />
        </a-button>
      </div>

      <div class="session-title">最近会话</div>

      <div class="session-list-wrap">
        <SessionList
          :loading="loading"
          :items="items"
          :selected-keys="selectedMemoryKey ? [selectedMemoryKey] : []"
          :deletable="true"
          @open="(memoryKey) => emit('open', memoryKey)"
          @delete="(item) => emit('delete', item)"
        />
      </div>

      <div class="sidebar-footer">
        <CodeOutlined class="footer-icon" />
        <div class="footer-version">
          <strong>{{ versionText }}</strong>
          <span v-if="buildTime">{{ buildTime }}</span>
        </div>
      </div>
    </aside>

    <button v-if="collapsed" class="collapsed-toggle" type="button" @click="toggleCollapsed">
      <MenuUnfoldOutlined />
    </button>
  </div>
</template>

<script setup lang="ts">
import { CodeOutlined, MenuFoldOutlined, MenuUnfoldOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { computed } from 'vue'
import SessionList from '@/components/chat-session/SessionList.vue'
import type { ChatSessionItem } from '@/components/chat-session/types'

const props = defineProps<{
  loading: boolean
  items: ChatSessionItem[]
  selectedMemoryKey: string
  collapsed?: boolean
}>()

const emit = defineEmits<{
  (e: 'open', memoryKey: string): void
  (e: 'create'): void
  (e: 'delete', item: ChatSessionItem): void
  (e: 'update:collapsed', value: boolean): void
}>()

const collapsed = computed(() => Boolean(props.collapsed))

const versionText = computed(() => {
  const envVersion = (import.meta.env.VITE_APP_PKG_VERSION as string | undefined)?.trim()
  return envVersion ? `Astrsomn ${envVersion}` : 'Astrsomn v0.0.0'
})

const buildTime = computed(() => {
  const envBuildTime = (import.meta.env.VITE_APP_BUILD_TIME as string | undefined)?.trim()
  return envBuildTime || ''
})

const toggleCollapsed = () => {
  emit('update:collapsed', !collapsed.value)
}
</script>

<style scoped>
.sidebar-shell {
  width: 256px;
  min-width: 256px;
  position: relative;
  transition: width 0.28s ease, min-width 0.28s ease;
}

.session-sidebar {
  width: 256px;
  height: 100%;
  border-right: 1px solid var(--border-default);
  background: var(--bg-surface);
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;
  transition: transform 0.28s ease, opacity 0.22s ease;
}

.sidebar-shell.collapsed {
  width: 36px;
  min-width: 36px;
}

.sidebar-shell.collapsed .session-sidebar {
  transform: translateX(-100%);
  opacity: 0;
  pointer-events: none;
}

.sidebar-top {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  padding: 4px;
}

.brand-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.brand-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.brand-text {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.brand-text strong {
  font-size: 13px;
  line-height: 1.2;
}

.brand-text span {
  font-size: 11px;
  color: var(--text-secondary);
}

.collapse-btn {
  color: var(--text-secondary);
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.new-chat-btn {
  flex: 1;
  height: 32px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  color: var(--text-secondary);
  box-shadow: none;
}

.new-chat-btn:hover,
.new-chat-btn:focus {
  color: var(--text-primary);
  border-color: var(--border-default);
  background: var(--bg-input);
}

.session-title {
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--text-secondary);
  text-transform: uppercase;
  padding: 2px 10px;
}

.session-list-wrap {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 0 4px 0 2px;
}

.sidebar-footer {
  border-top: 1px solid var(--border-default);
  background: rgba(148, 163, 184, 0.05);
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
}

.footer-icon {
  color: var(--text-secondary);
  flex-shrink: 0;
}

.footer-version {
  min-width: 0;
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.footer-version strong {
  font-size: 12px;
  color: var(--text-primary);
}

.footer-version span {
  font-size: 11px;
  color: var(--text-secondary);
}

.collapsed-toggle {
  position: absolute;
  top: 10px;
  left: 4px;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.collapsed-toggle:hover {
  color: var(--text-primary);
  border-color: var(--text-muted);
}

@media (max-width: 640px) {
  .sidebar-shell,
  .session-sidebar {
    width: 100%;
    min-width: 100%;
    max-height: 260px;
    border-right: 1px solid var(--border-default);
    border-bottom: 1px solid var(--border-default);
  }

  .sidebar-shell.collapsed {
    width: 100%;
    min-width: 100%;
  }

  .sidebar-shell.collapsed .session-sidebar {
    transform: none;
    opacity: 1;
    pointer-events: auto;
  }

  .collapsed-toggle {
    display: none;
  }
}
</style>
