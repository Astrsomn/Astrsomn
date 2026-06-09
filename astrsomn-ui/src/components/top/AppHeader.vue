<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <transition mode="out-in" name="fade-slide">
          <WorkspaceEnvSwitcher v-if="showWorkspaceEnv" key="env" :visible="true"/>

          <div v-else-if="showBack" key="back" class="page-nav-area">
            <button class="back-icon-btn" type="button" @click="handleBack">
              <PhCaretLeft :size="14" weight="bold"/>
            </button>
            <h1 class="page-title">{{ effectivePageTitle }}</h1>
          </div>

          <div v-else-if="effectivePageTitle" key="title" class="page-nav-area">
            <h1 class="page-title">{{ effectivePageTitle }}</h1>
          </div>
        </transition>
      </div>

      <div class="header-right">
        <a-tooltip v-if="isLoggedIn" :title="t.header.messagesTip">
          <button
              class="header-icon-btn"
              type="button"
              @click="openMessageDialog"
          >
            <PhBell :size="18" weight="regular"/>
            <span
                v-if="messageUnreadCount > 0"
                :class="{ 'badge-dot': true, 'badge-dot--lg': messageUnreadCount > 9 }"
            ></span>
          </button>
        </a-tooltip>

        <div class="actions-group">
          <slot name="actions">
            <DocLangTheme :showDoc="showDoc"/>
            <div v-if="isLoggedIn" class="header-divider"></div>
            <UserProfile v-if="isLoggedIn"/>
            <a-button v-else shape="round" size="small" type="primary" @click="handleLogin">{{ t.header.login }}</a-button>
          </slot>
        </div>
      </div>
    </div>

    <SystemMessageDialog v-model:open="messageDialogOpen" @unread-count-change="onUnreadCountChange"/>
  </header>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {useRouter} from 'vue-router'
import {PhBell, PhCaretLeft} from '@phosphor-icons/vue'

import DocLangTheme from './DocLangTheme.vue'

import SystemMessageDialog from '@/views/admin/system-config/system-message/component/SystemMessageDialog.vue'

import UserProfile from './UserProfile.vue'

import WorkspaceEnvSwitcher from './WorkspaceEnvSwitcher.vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('common')

interface Props {
  showBack?: boolean;
  pageTitle?: string;
  showDoc?: boolean;
  showWorkspaceEnv?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showBack: false,
  pageTitle: '', showDoc: false, showWorkspaceEnv: false,
});

const effectivePageTitle = computed(() => props.pageTitle || t.value.header.admin)

const router = useRouter();
const isLoggedIn = computed(() => !!localStorage.getItem('token'));

const handleBack = () => router.back();
const handleLogin = () => {
  localStorage.removeItem('token');
  router.push('/login');
};

const messageDialogOpen = ref(false)
const messageUnreadCount = ref(0)

const openMessageDialog = () => {
  messageDialogOpen.value = true
}

const onUnreadCountChange = (count: number) => {
  messageUnreadCount.value = count
}
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 56px;
  width: 100%;
  box-sizing: border-box;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  display: flex;
  align-items: center;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-default);
  transition: background 0.3s ease, border-color 0.3s ease;
  flex-shrink: 0;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin: 0 auto;
  padding: 0 32px;
}

.header-left {
  display: flex;
  align-items: center;
  min-width: 0;
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

/* ── 图标按钮（ph-bell、ph-book-open 等） ── */
.header-icon-btn {
  position: relative;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.header-icon-btn:hover {
  background: var(--bg-input);
  color: var(--text-primary);
}

/* 红色未读指示点 */
.badge-dot {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 6px;
  height: 6px;
  background: #ef4444;
  border-radius: 50%;
  box-shadow: 0 0 0 2px var(--bg-card);
}

.badge-dot--lg {
  width: 8px;
  height: 8px;
  top: 5px;
  right: 5px;
}

/* ── 返回按钮 ── */
.page-nav-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-icon-btn {
  width: 28px;
  height: 28px;
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  color: var(--text-secondary);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.back-icon-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--bg-input);
  transform: translateX(-2px);
}

.page-title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.actions-group {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-divider {
  width: 1px;
  height: 20px;
  background: var(--border-default);
  margin: 0 8px;
}

/* 覆写 DocLangTheme 和 UserProfile 内部按钮的视觉风格，使其与设计稿一致 */
.actions-group :deep(.doc-link) {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: var(--text-secondary);
  font-size: 18px;
  transition: all 0.2s ease;
}

.actions-group :deep(.doc-link:hover) {
  background: var(--bg-input);
  color: var(--text-primary);
}

.actions-group :deep(.doc-link :deep(.anticon)) {
  font-size: 18px;
}

.actions-group :deep(.lang-select) {
  width: 48px;
  color: var(--text-secondary);
}

.actions-group :deep(.lang-select :deep(.ant-select-selection-item)) {
  font-size: 12px !important;
  font-weight: 500;
  padding: 0 !important;
  line-height: 32px !important;
  color: var(--text-secondary) !important;
}

.actions-group :deep(.theme-toggle) {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 16px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.actions-group :deep(.theme-toggle:hover) {
  background: var(--bg-input);
  color: var(--text-primary);
}

.actions-group :deep(.user-profile) {
  display: flex;
  align-items: center;
}

.actions-group :deep(.avatar-box) {
  width: 32px;
  height: 32px;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid var(--border-default);
  background: var(--bg-input);
  color: var(--text-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.actions-group :deep(.user-profile:hover .avatar-box) {
  border-color: var(--primary);
}

.actions-group :deep(.user-status-dot) {
  width: 8px;
  height: 8px;
  border-width: 1.5px;
  bottom: 0;
  right: 0;
}

/* ── 过渡动画 ── */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.22s ease,
  transform 0.22s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(8px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}

.fade-slide-enter-to,
.fade-slide-leave-from {
  opacity: 1;
  transform: translateX(0);
}
</style>
