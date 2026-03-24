<template>
  <div class="home-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <h1>Astrsomn</h1>
          <a-dropdown>
            <a class="ant-dropdown-link" @click.prevent>
              {{ userInfo?.username || '用户' }}
              <DownOutlined />
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout" @click="handleLogout">
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <div class="welcome-card">
          <h2>欢迎回来，{{ userInfo?.username }}</h2>
          <p>您已成功登录系统</p>
        </div>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { logout } from '@/api/auth'

const router = useRouter()
const userInfo = ref<any>(null)

onMounted(() => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    userInfo.value = JSON.parse(userInfoStr)
  }
})

const handleLogout = async () => {
  try {
    await logout()
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    message.success('退出登录成功')
    router.push('/login')
  } catch (error: any) {
    message.error(error.message || '退出登录失败')
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.content {
  padding: 24px;
  background: #f0f2f5;
}

.welcome-card {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.welcome-card h2 {
  margin-bottom: 16px;
  color: #333;
}

.welcome-card p {
  color: #666;
  font-size: 16px;
}
</style>
