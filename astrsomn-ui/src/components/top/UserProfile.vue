<template>
  <a-dropdown :trigger="['click']" placement="bottomRight">
    <div class="user-profile">
      <div class="avatar-box">{{ avatarChar }}</div>
      <div class="user-status-dot"></div>
    </div>
    <template #overlay>
      <a-menu class="custom-dropdown" @click="handleMenuClick">
        <a-menu-item key="home">
          <home-outlined /> <span>立即聊天</span>
        </a-menu-item>
        <a-menu-item key="admin">
          <setting-outlined /> <span>管理后台</span>
        </a-menu-item>
        <a-menu-item key="config">
          <setting-outlined /> <span>配置中心</span>
        </a-menu-item>
        <a-menu-divider />
        <a-menu-item key="logout" class="logout-item">
          <logout-outlined /> <span>退出登录</span>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script lang="ts">
export default {
  name: 'UserProfile',
}
</script>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { 
  HomeOutlined, 
  SettingOutlined, 
  LogoutOutlined 
} from '@ant-design/icons-vue';

const router = useRouter();

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

const handleMenuClick = ({ key }: { key: string }) => {
  switch (key) {
    case 'home': router.push('/'); break;
    case 'admin': router.push('/admin'); break;
    case 'config': router.push('/admin/system/config'); break;
    case 'logout':
      localStorage.clear();
      message.success('已安全退出');
      router.push('/login');
      break;
  }
};
</script>

<style scoped>
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
</style>
