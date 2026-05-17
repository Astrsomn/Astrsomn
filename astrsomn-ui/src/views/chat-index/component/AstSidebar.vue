<template>
  <div class="sidebar-root">
    <div :class="{ collapsed }" class="sidebar-shell">
      <aside class="session-sidebar">
        <div class="sidebar-top">
          <a-button class="collapse-btn" size="small" type="text" @click="toggleCollapsed">
            <MenuFoldOutlined/>
          </a-button>
          <a-button class="new-chat-btn" @click="emit('create')">
            <template #icon>
              <PlusOutlined/>
            </template>
            <span>新会话</span>
          </a-button>
        </div>

        <div class="session-title">最近会话</div>

        <div class="session-list-wrap">
          <SessionList
              :deletable="true"
              :items="items"
              :loading="loading"
              :selected-keys="selectedMemoryKey ? [selectedMemoryKey] : []"
              @delete="(item) => emit('delete', item)"
              @open="(memoryKey) => emit('open', memoryKey)"
          />
        </div>

        <div class="sidebar-footer">
          <span class="footer-version">{{ versionText }}</span>
          <a-button class="settings-btn" type="text" @click="settingsOpen = true">
            <SettingOutlined/>
          </a-button>
        </div>
      </aside>

      <button v-if="collapsed" class="collapsed-toggle" type="button" @click="toggleCollapsed">
        <MenuUnfoldOutlined/>
      </button>
    </div>

    <AstModal
      :open="settingsOpen"
      body-height="auto"
      content-background="var(--bg-surface)"
      header-height="56px"
      main-padding="20px 24px"
      max-body-height="80vh"
      max-width="480px"
      width="100%"
      wrap-class-name="settings-modal-wrap"
      @cancel="settingsOpen = false"
      @update:open="settingsOpen = $event"
  >
    <template #header-title>设置</template>
    <div class="settings-body">
      <div class="setting-item">
        <span class="setting-label">主题</span>
        <a-select :value="appConfig.defaultTheme" size="small" style="width: 120px">
          <a-select-option value="light">浅色</a-select-option>
          <a-select-option value="dark">深色</a-select-option>
        </a-select>
      </div>
      <div class="setting-item">
        <span class="setting-label">语言</span>
        <a-select :value="appConfig.defaultLanguage" size="small" style="width: 120px">
          <a-select-option value="zh-CN">简体中文</a-select-option>
          <a-select-option value="en-US">English</a-select-option>
        </a-select>
      </div>
      <div class="setting-item">
        <span class="setting-label">底栏自动隐藏</span>
        <a-switch :checked="appConfig.bottomNavAutoHide" size="small"/>
      </div>
    </div>
  </AstModal>
  </div>
</template>

<script lang="ts" setup>
import {MenuFoldOutlined, MenuUnfoldOutlined, PlusOutlined, SettingOutlined} from '@ant-design/icons-vue'
import {computed, ref} from 'vue'
import AstModal from '@/components/home/AstModal.vue'
import SessionList from '@/views/chat-index/component/chat-session/SessionList.vue'
import type {ChatSessionItem} from '@/views/chat-index/utils/types.ts'
import {appConfig} from '@/config/config.ts'

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

const versionText = computed(() => `Astrsomn v${appConfig.version}`)

const settingsOpen = ref(false)

const toggleCollapsed = () => {
  emit('update:collapsed', !collapsed.value)
}
</script>

<style scoped>
.sidebar-shell {
  width: 256px;
  min-width: 256px;
  height: calc(100vh - 60px);
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
  width: 64px;
  min-width: 64px;
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
  scrollbar-width: thin;
  scrollbar-color: rgba(127, 143, 170, 0.25) transparent;
}

.session-list-wrap::-webkit-scrollbar {
  width: 5px;
}

.session-list-wrap::-webkit-scrollbar-track {
  background: transparent;
}

.session-list-wrap::-webkit-scrollbar-thumb {
  background: rgba(127, 143, 170, 0.3);
  border-radius: 999px;
  transition: background 0.2s;
}

.session-list-wrap::-webkit-scrollbar-thumb:hover {
  background: rgba(127, 143, 170, 0.5);
}

.sidebar-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
}

.footer-version {
  font-size: 11px;
  color: var(--text-muted, #94a3b8);
}

.settings-btn {
  color: var(--text-secondary);
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.settings-btn:hover {
  color: var(--text-primary);
}

.collapsed-toggle {
  position: absolute;
  top: 12px;
  left: 50%;
  transform: translateX(-50%);
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.2s ease;
}

.collapsed-toggle:hover {
  color: var(--text-primary);
  border-color: var(--text-muted);
  background: var(--bg-card);
}

/* settings modal — low-key, no shadow / border / bg emphasis */
:global(.settings-modal-wrap .ant-modal-content) {
  box-shadow: none;
  border: none;
  background: var(--bg-surface);
}

:global(.settings-modal-wrap .ant-modal) {
  box-shadow: none;
}

.settings-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.setting-label {
  font-size: 13px;
  color: var(--text-primary);
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
