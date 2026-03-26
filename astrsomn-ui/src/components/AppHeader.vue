<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <transition name="fade-slide" mode="out-in">
          <div v-if="showBrand" class="brand-area" key="logo">
            <div class="logo-box">
              <img :src="logoUrl" class="logo-img" alt="Astrsomn" />
            </div>
            <div class="brand-text">
              <span class="brand-name">Astrsomn</span>
              <span class="brand-status">{{ brandStatus }}</span>
            </div>
          </div>

          <div v-else-if="showBack" class="page-nav-area" key="back">
            <button type="button" class="back-btn" @click="handleBack" title="返回首页">
              <arrow-left-outlined />
            </button>
            <h1 class="page-title">{{ pageTitle }}</h1>
          </div>
        </transition>
      </div>

      <div class="header-right">
        <button 
          v-if="showSwitch" 
          class="toc-switch-btn" 
          @click="handleSwitch"
          :class="{ 'btn-clicking': isClicking }"
        >
          <div class="btn-inner">
            <swap-outlined class="switch-icon" />
            <span class="switch-text">{{ switchText }}</span>
          </div>
        </button>

        <div class="actions-group">
          <slot name="actions">
            <DocLangTheme :showDoc="showDoc" />
            <UserProfile v-if="isLoggedIn" />
            <a-button v-else type="primary" shape="round" class="login-btn" @click="handleLogin">
              登录 / 注册
            </a-button>
          </slot>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ArrowLeftOutlined, SwapOutlined } from '@ant-design/icons-vue';
import logoUrl from '@/assets/Astrsomn-logo.png';
import DocLangTheme from '@/views/admin/components/DocLangTheme.vue';
import UserProfile from '@/views/admin/components/UserProfile.vue';

interface Props {
  showBrand?: boolean;
  showBack?: boolean;
  brandStatus?: string;
  pageTitle?: string;
  showDoc?: boolean;
  showSwitch?: boolean;
  switchTarget?: 'chat' | 'admin';
}

const props = withDefaults(defineProps<Props>(), {
  showBrand: true,
  showBack: false,
  brandStatus: 'AI Assistant',
  pageTitle: '管理后台',
  showDoc: false,
  showSwitch: false,
  switchTarget: 'chat',
});

const router = useRouter();
const route = useRoute();
const isClicking = ref(false);

const isLoggedIn = computed(() => !!localStorage.getItem('token'));
const switchText = computed(() => props.switchTarget === 'chat' ? '去聊天' : '去后台');

const handleSwitch = () => {
  isClicking.value = true;
  // 关键：延迟 200ms 执行跳转，让 CSS 的 active 缩放动画能被肉眼观察到
  setTimeout(() => {
    if (props.switchTarget === 'chat') {
      localStorage.setItem('lastAdminPath', route.path);
      router.push('/');
    } else {
      const lastPath = localStorage.getItem('lastAdminPath');
      router.push(lastPath || '/admin');
    }
    isClicking.value = false;
  }, 200);
};

const handleBack = () => router.push('/admin');
const handleLogin = () => {
  localStorage.removeItem('token');
  router.push('/login');
};
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 64px;
  width: 100%;
  background: var(--bg-surface); /* 直接使用变量 */
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  height: 100%;
}

/* --- 修复：返回按钮与文字同一行 --- */
.page-nav-area {
  display: flex;
  align-items: center; /* 核心：垂直居中 */
  flex-direction: row; /* 显式声明水平排列 */
  gap: 12px;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid var(--border-default);
  background: var(--bg-elevated);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.back-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  transform: translateX(-2px);
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  line-height: 1; /* 防止行高撑开布局 */
}

/* --- 品牌区域 --- */
.brand-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-box {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-heading);
}

.brand-status {
  font-size: 11px;
  color: var(--text-muted);
}

/* --- 核心优化：ToC 风格切换按钮 --- */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toc-switch-btn {
  border: none;
  outline: none;
  background: var(--bg-elevated);
  padding: 2px; /* 留出边框感 */
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.btn-inner {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 18px;
  background: var(--bg-elevated);
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.toc-switch-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.toc-switch-btn:hover .btn-inner {
  color: var(--primary);
  background: var(--primary-hover); /* 或者使用透明度叠加 */
}

.toc-switch-btn:hover .switch-icon {
  transform: rotate(180deg);
}

/* 点击时的 Q 弹动画 */
.toc-switch-btn:active, .btn-clicking {
  transform: scale(0.92);
}

.switch-icon {
  font-size: 14px;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.actions-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* --- 切换动画：丝滑过渡 --- */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-15px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(15px);
}

@media (max-width: 768px) {
  .brand-status, .switch-text { display: none; }
}
</style>