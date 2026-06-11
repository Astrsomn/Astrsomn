<template>
  <div class="sidebar-root">
    <div :class="{ collapsed }" class="sidebar-shell">
      <aside class="session-sidebar">
        <div class="sidebar-top">
          <AstSearchInput
              v-model="searchText"
              class="sidebar-search-pill"
              layout="fluid"
              :placeholder="t.sidebar.searchSessions"
              @search="handleSearch"
          />
          <a-button class="new-chat-btn" @click="emit('create')">
            <template #icon>
              <PlusOutlined/>
            </template>
          </a-button>
        </div>

        <div class="session-title">{{ t.sidebar.recentSessions }}</div>

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
          <a-button class="collapse-btn" size="small" type="text" @click="toggleCollapsed">
            <MenuFoldOutlined/>
          </a-button>
          <span class="footer-version">{{ versionText }}</span>
          <a-button class="settings-btn" type="text" :title="t.sidebar.settings" @click="settingsOpen = true">
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
    <template #header-title>{{ t.sidebar.settings }}</template>
    <div class="settings-body">
      <div class="setting-item">
        <span class="setting-label">{{ t.sidebar.theme }}</span>
        <a-select :value="appConfig.defaultTheme" size="small" style="width: 120px">
          <a-select-option value="light">{{ t.sidebar.light }}</a-select-option>
          <a-select-option value="dark">{{ t.sidebar.dark }}</a-select-option>
        </a-select>
      </div>
      <div class="setting-item">
        <span class="setting-label">{{ t.sidebar.language }}</span>
        <a-select :value="appConfig.defaultLanguage" size="small" style="width: 120px">
          <a-select-option value="zh-CN">{{ t.sidebar.chinese }}</a-select-option>
          <a-select-option value="en-US">{{ t.sidebar.english }}</a-select-option>
        </a-select>
      </div>
      <div class="setting-item">
        <span class="setting-label">{{ t.sidebar.bottomNavAutoHide }}</span>
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
import AstSearchInput from '@/components/home/AstSearchInput.vue'
import SessionList from '@/views/chat-index/component/chat-session/SessionList.vue'
import type {ChatSessionItem} from '@/views/chat-index/utils/types.ts'
import {appConfig} from '@/config/config.ts'
import {usePageTranslation} from '@/locales/pages.ts'

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

const searchText = ref('')
const handleSearch = () => {
}

const versionText = computed(() => `Astrsomn v${appConfig.version}`)

const settingsOpen = ref(false)

const t = usePageTranslation('chat-index')

const toggleCollapsed = () => {
  emit('update:collapsed', !collapsed.value)
}
</script>

<style scoped>
.sidebar-shell {
  width: 288;
  min-width: 288;
  height: calc(100vh - 60px);
  position: relative;
  transition: width 0.28s ease, min-width 0.28s ease;
}

.session-sidebar {
  width: 288px;
  height: 100%;
  border-right: 1px solid var(--border-default);
  background: var(--bg-surface);
  padding: 0;
  display: flex;
  flex-direction: column;
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
  height: 56px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  border-bottom: 1px solid var(--border-default);
}

.sidebar-search-pill {
  flex: 1;
  min-width: 0;
  border: none;
}

.new-chat-btn {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  color: var(--text-secondary);
  box-shadow: none;
  padding: 0;
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
  padding: 10px 14px 4px;
  flex-shrink: 0;
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
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  border-top: 1px solid var(--border-default);
}

.collapse-btn {
  color: var(--text-secondary);
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.footer-version {
  flex: 1;
  font-size: 11px;
  color: var(--text-muted, #94a3b8);
  text-align: center;
}

.settings-btn {
  color: var(--text-secondary);
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.settings-btn:hover {
  color: var(--text-primary);
}

.collapsed-toggle {
  position: fixed;
  left: 92px;
  bottom: 24px;
  z-index: 100;
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
  background: var(--bg-surface);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
  padding: 0;
}

.collapsed-toggle:hover {
  color: var(--text-primary);
  border-color: var(--border-default);
  background: var(--bg-card);
}


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
