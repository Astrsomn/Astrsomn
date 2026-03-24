<template>
  <div class="login-page" :class="{ dark: isDark }">
    <!-- 左上角 Logo -->
    <div class="logo-header">
      <img src="https://picsum.photos/40/40" alt="Logo" class="logo-img" />
      <span class="logo-text">Astrsomn 星梦</span>
    </div>

    <!-- 顶部工具栏：技术文档 + 语言切换 + 夜间模式 -->
    <div class="top-toolbar">
      <a href="javascript:;" target="_blank" class="doc-link">📖 技术文档</a>
      <a-select
        v-model:value="currentLang"
        size="small"
        style="width: 120px; margin: 0 16px"
        @change="changeLang"
      >
        <a-select-option value="zh-CN">简体中文</a-select-option>
        <a-select-option value="en-US">English</a-select-option>
      </a-select>

      <a-switch
        v-model:checked="isDark"
        checked-children="🌙"
        un-checked-children="☀️"
        @change="toggleTheme"
      />
    </div>

    <!-- 左侧：品牌区域 0.618 黄金比例 -->
    <div class="login-left">
      <div class="brand-content">
        <h1 class="brand-title">🌟 Astrsomn 星梦</h1>
        <div class="brand-slogan">
          <p>封装复杂 AI，释放 Java 创造力</p>
          <p>基于 LangChain4j 构建，为 Java 开发者提供生产级 AI 应用开发解决方案</p>
        </div>
      </div>
    </div>

    <!-- 右侧：登录表单 -->
    <div class="login-right">
      <div class="login-form-wrapper">
        <!-- 欢迎标语 移动到这里 -->
        <div class="form-title-group">
          <h2 class="form-welcome">欢迎登录</h2>
          <p class="form-desc">请输入账号密码登录系统</p>
        </div>

        <a-form
          :model="formState"
          name="login"
          @finish="handleLogin"
          autocomplete="off"
          :rules="rules"
          class="login-form"
        >
          <a-form-item name="username">
            <a-input
              v-model:value="formState.username"
              placeholder="请输入用户名"
              size="large"
              class="login-input"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="password">
            <a-input-password
              v-model:value="formState.password"
              placeholder="请输入密码"
              size="large"
              class="login-input"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
              class="login-btn"
            >
              登录
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>

    <!-- 底部公司版权 绝对居中 -->
    <div class="footer">
      © 2026 Astrsomn 星梦科技 | 内部专用系统
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { login } from '@/api/auth'
import type { LoginRequest } from '@/types'

const router = useRouter()
const loading = ref(false)

const formState = reactive<LoginRequest>({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 语言
const currentLang = ref('zh-CN')
const changeLang = (val: string) => {
  message.success(`已切换：${val === 'zh-CN' ? '简体中文' : 'English'}`)
}

// 暗黑模式
const isDark = ref(true)
const toggleTheme = (val: boolean) => {
  localStorage.setItem('theme', val ? 'dark' : 'light')
  message.info(val ? '夜间模式已开启' : '日间模式已开启')
}

// 登录
const handleLogin = async (values: LoginRequest) => {
  loading.value = true
  try {
    const response = await login(values)
    localStorage.setItem('token', response.token)
    localStorage.setItem('userInfo', JSON.stringify(response.userInfo))
    message.success('登录成功')
    router.push('/')
  } catch (error: any) {
    message.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const theme = localStorage.getItem('theme')
  if (theme) isDark.value = theme === 'dark'
})
</script>

<style scoped>
.login-page {
  position: relative;
  display: flex;
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background: #090e15;
  transition: all 0.3s ease;
}

/* 日间模式 */
.login-page:not(.dark) {
  background: #f5f7fa;
}

/* Logo 区域 */
.logo-header {
  position: absolute;
  top: 22px;
  left: 32px;
  display: flex;
  align-items: center;
  z-index: 999;
}
.logo-img {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  margin-right: 10px;
}
.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}
.login-page:not(.dark) .logo-text {
  color: #1f2937;
}

/* 顶部工具栏 */
.top-toolbar {
  position: absolute;
  top: 24px;
  right: 32px;
  display: flex;
  align-items: center;
  z-index: 999;
}
.doc-link {
  color: #e1e9f5;
  text-decoration: none;
  font-size: 14px;
}
.login-page:not(.dark) .doc-link {
  color: #1f2937;
}

/* 左侧 0.618 黄金比例 */
.login-left {
  flex: 0 0 61.8%;
  background: linear-gradient(135deg, #111a2e 0%, #1a2a4a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.login-page:not(.dark) .login-left {
  background: linear-gradient(135deg, #e6f7ff 0%, #91d5ff 100%);
}
.login-left::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(to bottom, #00e4ff, #007bff);
  box-shadow: 0 0 15px #007bff;
}

.brand-content {
  max-width: 600px;
  padding: 0 40px;
}
.brand-title {
  font-size: 46px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
  text-shadow: 0 0 12px rgba(0, 228, 255, 0.4);
}
.login-page:not(.dark) .brand-title {
  color: #005c9c;
  text-shadow: none;
}
.brand-slogan p {
  font-size: 16px;
  color: #7b93b6;
  line-height: 1.8;
}
.login-page:not(.dark) .brand-slogan p {
  color: #417fb3;
}

/* 右侧登录区 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0c1521;
}
.login-page:not(.dark) .login-right {
  background: #fff;
}

/* 登录卡片 */
.login-form-wrapper {
  width: 420px;
  padding: 44px 42px;
  background: #121e2d;
  border-radius: 14px;
  border: 1px solid #1e2f46;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}
.login-page:not(.dark) .login-form-wrapper {
  background: #ffffff;
  border-color: #e5e6eb;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

/* 登录框顶部标题 */
.form-title-group {
  text-align: center;
  margin-bottom: 30px;
}
.form-welcome {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 8px;
}
.login-page:not(.dark) .form-welcome {
  color: #1f2937;
}
.form-desc {
  font-size: 14px;
  color: #7b93b6;
  margin: 0;
}
.login-page:not(.dark) .form-desc {
  color: #6b7280;
}

/* 输入框样式 */
:deep(.login-input .ant-input) {
  height: 46px;
  background: #182436;
  border-color: #273a57;
  color: #e1e9f5;
  border-radius: 6px;
}
:deep(.login-input .ant-input::placeholder) {
  color: #5c7394;
}

/* 日间模式输入框 */
.login-page:not(.dark) :deep(.login-input .ant-input) {
  background: #ffffff;
  border-color: #dcdfe6;
  color: #333;
}
.login-page:not(.dark) :deep(.login-input .ant-input::placeholder) {
  color: #909399;
}

/* 密码框 */
:deep(.login-input .ant-input-password) {
  height: 46px;
  background: #182436;
  border-color: #273a57;
  color: #e1e9f5;
  border-radius: 6px;
}
.login-page:not(.dark) :deep(.login-input .ant-input-password) {
  background: #ffffff;
  border-color: #dcdfe6;
  color: #333;
}
.ant-input-affix-wrapper :deep{
    background: none;
}


/* 聚焦 */
:deep(.login-input .ant-input:focus),
:deep(.login-input .ant-input-password:focus) {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.15);
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}

/* 按钮 */
.login-btn {
  height: 48px;
  font-size: 16px;
  background: linear-gradient(90deg, #007bff, #00a3ff);
  border: none;
  border-radius: 6px;
}
.login-btn:hover {
  opacity: 0.9;
}

/* 底部版权 */
.footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 20px;
  width: 100%;
  text-align: center;
  font-size: 13px;
  color: #5c7394;
  z-index: 99;
}
.login-page:not(.dark) .footer {
  color: #666;
}
</style>