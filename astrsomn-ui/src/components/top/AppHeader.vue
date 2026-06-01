<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <transition mode="out-in" name="fade-slide">
          <div v-if="showBrand" key="logo" class="brand-area">
            <div class="logo-box">
              <img alt="Astrsomn" class="logo-img" src="../../assets/Astrsomn-logo.png"/>
            </div>

            <button
                v-if="showSwitch"
                class="brand-interactive-wrapper"
                type="button"
                @click="handleSwitch"
            >
              <div class="flip-content">
                <div class="flip-layer layer-front">
                  <span class="brand-name">Astrsomn</span>

                </div>

                <div class="flip-layer layer-back">
                  <span class="action-text">{{ switchActionText }}</span>
                  <component :is="switchIcon" class="action-icon"/>
                </div>
              </div>
            </button>

            <div v-else class="brand-text-static">
              <span class="brand-name">Astrsomn</span>
              <span class="brand-status">{{ brandStatus }}</span>
            </div>
          </div>

          <div v-else-if="showBack" key="back" class="page-nav-area">
            <button class="back-icon-btn" type="button" @click="handleBack">
              <arrow-left-outlined/>
            </button>
            <h1 class="page-title">{{ effectivePageTitle }}</h1>
          </div>
        </transition>
      </div>

      <div class="header-right">
        <WorkspaceEnvSwitcher :visible="showWorkspaceEnv"/>

        <div class="v-line-divider"></div>

        <div class="actions-group">
          <slot name="actions">
            <DocLangTheme :showDoc="showDoc"/>
            <UserProfile v-if="isLoggedIn"/>
            <a-button v-else shape="round" size="small" type="primary" @click="handleLogin">{{ t.header.login }}</a-button>
          </slot>
        </div>
      </div>
    </div>
  </header>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router';
import {AppstoreOutlined, ArrowLeftOutlined, SwapOutlined} from '@ant-design/icons-vue';


import DocLangTheme from './DocLangTheme.vue';

import UserProfile from './UserProfile.vue';

import WorkspaceEnvSwitcher from './WorkspaceEnvSwitcher.vue';
import {usePageTranslation} from '@/locales/pages.ts';

const t = usePageTranslation('common')

interface Props {
  showBrand?: boolean;
  showBack?: boolean;
  brandStatus?: string;
  pageTitle?: string;
  showDoc?: boolean;
  showSwitch?: boolean;
  switchTarget?: 'chat' | 'admin';
  showWorkspaceEnv?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showBrand: true, showBack: false, brandStatus: 'AI Assistant',
  pageTitle: '', showDoc: false, showSwitch: false,
  switchTarget: 'chat', showWorkspaceEnv: false,
});

const effectivePageTitle = computed(() => props.pageTitle || t.value.header.admin)

const router = useRouter();
const route = useRoute();
const isClicking = ref(false);
const isLoggedIn = computed(() => !!localStorage.getItem('token'));

const switchActionText = computed(() => props.switchTarget === 'chat' ? t.value.header.chatNow : t.value.header.admin);
const switchIcon = computed(() => props.switchTarget === 'chat' ? SwapOutlined : AppstoreOutlined);

const handleSwitch = () => {
  isClicking.value = true;
  setTimeout(() => {
    const dest = props.switchTarget === 'chat' ? '/' : (localStorage.getItem('lastAdminPath') || '/admin');
    if (props.switchTarget === 'chat') localStorage.setItem('lastAdminPath', route.path);
    router.push(dest);
    isClicking.value = false;
  }, 200);
};
const handleBack = () => router.back();
const handleLogin = () => {
  localStorage.removeItem('token');
  router.push('/login');
};
</script>

<style scoped>
.app-header {
  --primary-glow: color-mix(in srgb, var(--primary) 20%, transparent);
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
  min-width: 240px;

}

.brand-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-box {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}


.brand-interactive-wrapper {
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
  outline: none;
  height: 40px;
  perspective: 1000px;
  overflow: hidden;
}

.flip-content {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
}

.brand-interactive-wrapper:hover .flip-content {
  transform: translateY(-40px);
}

.flip-layer {
  height: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  backface-visibility: hidden;
}


.layer-front .brand-name {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-heading);
  letter-spacing: -0.5px;
  line-height: 1.2;
  font-style: italic;
}

.layer-front .brand-status {
  font-size: 11px;
  color: var(--text-muted);
  line-height: 1.2;
}


.layer-back {
  flex-direction: row !important;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.brand-interactive-wrapper:hover .layer-back {
  opacity: 1;
}

.action-text {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary);
  letter-spacing: 1px;
  text-shadow: 0 0 12px var(--primary-glow);
}

.action-icon {
  font-size: 12px;
  color: var(--primary);
}


.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.v-line-divider {
  width: 1px;
  height: 18px;
  background: var(--border-subtle);
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

@media (max-width: 768px) {
  .brand-status {
    display: none;
  }
}
</style>