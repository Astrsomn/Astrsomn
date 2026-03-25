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
        <a-button 
          type="text" 
          class="switch-btn" 
          @click="handleSwitch"
          v-if="showSwitch"
        >
          <template #icon>
            <swap-outlined />
          </template>
          {{ switchText }}
        </a-button>
        <slot name="actions">
          <DocLangTheme :showDoc="showDoc" />
          <UserProfile v-if="isLoggedIn" />
          <a-button v-else type="primary" shape="round" class="login-btn" @click="handleLogin">
            登录 / 注册
          </a-button>
        </slot>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue';
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

const isLoggedIn = computed(() => {
  const token = localStorage.getItem('token');
  return !!token;
});

const switchText = computed(() => {
  return props.switchTarget === 'chat' ? '去聊天' : '去后台';
});

const handleSwitch = () => {
  if (props.switchTarget === 'chat') {
    localStorage.setItem('lastAdminPath', route.path);
    router.push('/');
  } else {
    const lastPath = localStorage.getItem('lastAdminPath');
    router.push(lastPath || '/admin');
  }
};

const handleBack = () => {
  router.push('/admin');
};

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
  background: rgba(var(--bg-surface), 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
}

.header-container {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
  display: flex;
  align-items: center;
}

.brand-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-box {
  width: 32px;
  height: 32px;
  background: transparent;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: none;
  overflow: hidden;
}

.logo-img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  object-position: 50% 0%;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  letter-spacing: -0.5px;
}

.brand-status {
  display: block;
  font-size: 11px;
  color: var(--text-muted);
  margin-top: -2px;
}

.page-nav-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid var(--border-default);
  background: var(--bg-elevated);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  transform: translateX(-3px);
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  letter-spacing: -0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.switch-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  font-size: 13px;
  color: var(--text-secondary);
  border: 1px solid var(--border-default);
  border-radius: 6px;
  transition: all 0.2s ease;
}

.switch-btn:hover {
  color: var(--primary);
  border-color: var(--primary);
  background: var(--primary-hover);
}

.login-btn {
  padding: 6px 20px;
}

.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from { opacity: 0; transform: translateX(-10px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(10px); }

@media (max-width: 768px) {
  .header-container { padding: 0 16px; }
  .brand-status { display: none; }
}
</style>
