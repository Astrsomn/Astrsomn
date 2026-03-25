<template>
  <div class="admin-shell">
    <header class="shell-header">
      <div class="header-container">
        <div class="header-left">
          <transition name="fade-slide" mode="out-in">
            <div v-if="isHome" class="brand-area" key="logo">
              <div class="logo-box">
                <img :src="logoUrl" class="logo-img" alt="Astrsomn" />
              </div>
              <div class="brand-text">
                <h1 class="brand-title">Astrsomn</h1>
                <p class="brand-subtitle">Langchain4j 快速配置框架</p>
              </div>
            </div>
            <div v-else class="page-nav-area" key="back">
              <button type="button" class="back-btn" @click="goBack" title="返回首页">
                <arrow-left-outlined />
              </button>
              <h1 class="page-title">{{ pageTitle }}</h1>
            </div>
          </transition>
        </div>

        <div class="header-right">
          <div class="action-items">
            <DocLangTheme :showDoc="true"/>
          </div>

          <a-dropdown :trigger="['click']" placement="bottomRight">
            <div class="user-profile">
              <div class="avatar-box">{{ avatarChar }}</div>
              <div class="user-status-dot"></div>
            </div>
            <template #overlay>
              <a-menu class="custom-dropdown" @click="handleMenuClick">
                <a-menu-item key="home">
                  <home-outlined /> <span>回到首页</span>
                </a-menu-item>
                <a-menu-item key="password">
                  <lock-outlined /> <span>安全设置</span>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" class="logout-item">
                  <logout-outlined /> <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </header>

    <main class="shell-content">
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { 
  ArrowLeftOutlined, 
  HomeOutlined, 
  LockOutlined, 
  LogoutOutlined 
} from '@ant-design/icons-vue';
import logoUrl from '@/assets/Astrsomn-logo.png';
import DocLangTheme from '@/components/DocLangTheme.vue';

const route = useRoute();
const router = useRouter();

const isHome = computed(() => route.path === '/admin');
const pageTitle = computed(() => (route.meta.title as string) || '管理后台');

const avatarChar = computed(() => {
  try {
    const raw = localStorage.getItem('userInfo');
    if (raw) {
      const info = JSON.parse(raw) as { username?: string };
      if (info.username) return info.username.charAt(0).toUpperCase();
    }
  } catch { /* ignore */ }
  return 'U';
});
const goBack = () => router.push('/admin');

const handleMenuClick = ({ key }: { key: string }) => {
  switch (key) {
    case 'home': router.push('/'); break;
    case 'password': message.info('安全设置开发中'); break;
    case 'logout':
      localStorage.clear();
      message.success('已安全退出');
      router.push('/login');
      break;
  }
};
</script>

<style scoped>
/* 基础布局 */
.admin-shell {
  min-height: 100vh;
  background-color: var(--bg-base);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
}

/* 导航栏样式：毛玻璃与层次感 */
.shell-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 72px;
  background-color: var(--bg-surface);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
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

/* 左侧品牌区 */
.brand-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-box {
  width: 42px;
  height: 42px;
  border-radius: var(--radius-lg);
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: none;
  overflow: hidden;
}

.logo-img {
  width: 76px;
  height: 76px;
  object-fit: cover;
  object-position: 50% 0%;
}

.brand-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-heading);
  letter-spacing: -0.5px;
  line-height: 1.2;
}

.brand-subtitle {
  margin: 2px 0 0;
  font-size: 12px;
  color: var(--text-muted);
}

/* 返回与标题区 */
.page-nav-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  width: 38px;
  height: 38px;
  border-radius: var(--radius-md);
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
  font-size: 20px;
  font-weight: 600;
  color: var(--text-heading);
}

/* 右侧工具栏 */
.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.action-items {
  display: flex;
  align-items: center;
  background: var(--bg-input);
  padding: 4px 12px;
  border-radius: 30px;
  border: 1px solid var(--border-subtle);
}

.divider {
  width: 1px;
  height: 16px;
  background: var(--border-default);
  margin: 0 12px;
}

.custom-select {
  width: 90px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* 用户头像 */
.user-profile {
  position: relative;
  cursor: pointer;
}

.avatar-box {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--bg-elevated);
  border: 2px solid var(--border-default);
  color: var(--accent-blue);
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.user-profile:hover .avatar-box {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-hover);
}

.user-status-dot {
  position: absolute;
  bottom: 1px;
  right: 1px;
  width: 10px;
  height: 10px;
  background: var(--success);
  border: 2px solid var(--bg-surface);
  border-radius: 50%;
}

/* 主体内容区 */
.shell-content {
  flex: 1;
  padding: 32px 0;
  overflow-y: auto;
}

.content-wrapper {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 32px;
}

/* 下拉菜单美化 */
.custom-dropdown {
  background-color: var(--bg-card) !important;
  border: 1px solid var(--border-default) !important;
  box-shadow: var(--shadow-card) !important;
  padding: 8px !important;
  border-radius: var(--radius-lg) !important;
}

.custom-dropdown :deep(.ant-dropdown-menu-item) {
  border-radius: var(--radius-sm);
  padding: 8px 16px;
  color: var(--text-secondary);
}

.custom-dropdown :deep(.ant-dropdown-menu-item:hover) {
  background-color: var(--primary-hover) !important;
  color: var(--primary);
}

.logout-item {
  color: var(--error) !important;
}

/* 动画效果 */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from { opacity: 0; transform: translateX(-10px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(10px); }

.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter-from { opacity: 0; transform: translateY(8px); }
.page-fade-leave-to { opacity: 0; }

/* 响应式适配 */
@media (max-width: 768px) {
  .header-container, .content-wrapper { padding: 0 16px; }
  .brand-subtitle, .divider, .custom-select { display: none; }
  .action-items { padding: 4px 8px; }
}
</style>