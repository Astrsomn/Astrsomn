<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <transition name="fade-slide" mode="out-in">
          <div v-if="showBrand" class="brand-area" key="logo">
            <div class="logo-box">
              <img src="../../assets/Astrsomn-logo.png" class="logo-img" alt="Astrsomn"/>
            </div>

            <button
                type="button"
                class="brand-interactive-wrapper"
                @click="handleSwitch"
                v-if="showSwitch"
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

          <div v-else-if="showBack" class="page-nav-area" key="back">
            <button type="button" class="back-icon-btn" @click="handleBack">
              <arrow-left-outlined/>
            </button>
            <h1 class="page-title">{{ pageTitle }}</h1>
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
            <a-button v-else type="primary" size="small" shape="round" @click="handleLogin">登录</a-button>
          </slot>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import {computed, ref} from 'vue'
import {useRouter, useRoute} from 'vue-router';
import {
  ArrowLeftOutlined,
  SwapOutlined,
  AppstoreOutlined
} from '@ant-design/icons-vue';
// Vetur occasionally misses Vue SFC default exports in script setup files.
// @ts-ignore
import DocLangTheme from './DocLangTheme.vue';
// @ts-ignore
import UserProfile from './UserProfile.vue';
// @ts-ignore
import WorkspaceEnvSwitcher from './WorkspaceEnvSwitcher.vue';

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
  pageTitle: '管理后台', showDoc: false, showSwitch: false,
  switchTarget: 'chat', showWorkspaceEnv: false,
});

const router = useRouter();
const route = useRoute();
const isClicking = ref(false);
const isLoggedIn = computed(() => !!localStorage.getItem('token'));

// 💡 动态文字逻辑：根据目标判断
const switchActionText = computed(() => props.switchTarget === 'chat' ? '立即聊天' : '管理后台');
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
  --primary-glow: rgba(59, 130, 246, 0.2);
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

/* --- 左侧区域 --- */
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

/* 💡 翻转交互设计 */
.brand-interactive-wrapper {
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
  outline: none;
  height: 40px;
  perspective: 1000px; /* 3D 视距 */
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

/* 正面：Logo 文字 */
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

/* 反面：功能文字 */
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
  letter-spacing: 1px; /* 加宽间距更显高级 */
  text-shadow: 0 0 12px var(--primary-glow);
}

.action-icon {
  font-size: 12px;
  color: var(--primary);
}

/* --- 右侧区域 --- */
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

/* --- 返回导航 (修复对齐和动画) --- */
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

@media (max-width: 768px) {
  .brand-status {
    display: none;
  }
}
</style>