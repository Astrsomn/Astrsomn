<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <transition mode="out-in" name="fade-slide">
          <WorkspaceEnvSwitcher v-if="showWorkspaceEnv" key="env" :visible="true"/>

          <div v-else-if="showBack" key="back" class="page-nav-area">
            <button class="back-icon-btn" type="button" @click="handleBack">
              <arrow-left-outlined/>
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
          <a-badge :count="messageUnreadCount" :offset="[-2, 2]" :overflow-count="99">
            <a-button class="header-icon-btn" shape="circle" size="small" type="text" @click="openMessageDialog">
              <template #icon>
                <bell-outlined/>
              </template>
            </a-button>
          </a-badge>
        </a-tooltip>

        <div class="actions-group">
          <slot name="actions">
            <DocLangTheme :showDoc="showDoc"/>
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
import {useRouter} from 'vue-router';
import {ArrowLeftOutlined, BellOutlined} from '@ant-design/icons-vue';


import DocLangTheme from './DocLangTheme.vue';

import SystemMessageDialog from '@/views/admin/system-config/system-message/component/SystemMessageDialog.vue';

import UserProfile from './UserProfile.vue';

import WorkspaceEnvSwitcher from './WorkspaceEnvSwitcher.vue';
import {usePageTranslation} from '@/locales/pages.ts';

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
  height: 60px;
  width: 100%;
  box-sizing: border-box;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--border-default);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin: 0 auto;
  padding: 0 24px;
}


.header-left {
  display: flex;
  align-items: center;
  min-width: 0;

}


.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon-btn {
  color: var(--text-secondary);
}

.header-icon-btn :deep(.anticon) {
  color: var(--text-secondary);
}

.header-icon-btn:hover {
  color: var(--text-primary);
  background: var(--bg-card-hover, transparent);
}

.header-icon-btn:hover :deep(.anticon) {
  color: var(--text-primary);
}


.page-nav-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-icon-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-default);
  background-color: var(--bg-card);
  color: var(--text-primary);
  border-radius: var(--radius-max);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.back-icon-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  transform: translateX(-2px);
}

.page-title {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--text-heading);
}

.actions-group {
  display: flex;
  align-items: center;
  gap: 12px;
}


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