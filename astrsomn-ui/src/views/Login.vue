<template>
  <div class="login-page">
    <nav class="glass-header">
      <div class="header-left" @click="goHome">
        <div class="logo-dot">
          <img :src="logoUrl" class="logo-img" alt="Astrsomn" />
        </div>
        <span class="logo-text">Astrsomn <small>星梦</small></span>
      </div>

      <div class="header-right">
        <DocLangTheme :showDoc="true"/>
      </div>
    </nav>

    <section class="login-left">
      <div class="brand-visual-bg"></div>
      <div class="brand-content">
        <div class="tagline">Enterprise AI Framework</div>
        <h1 class="brand-title">
          封装复杂 AI<br />
          <span class="gradient-text">释放 Java 创造力</span>
        </h1>
        <div class="brand-features">
          <div class="feature-item">
            <check-circle-filled class="icon" />
            <span>基于 <strong>LangChain4j</strong> 深度构建</span>
          </div>
          <div class="feature-item">
            <check-circle-filled class="icon" />
            <span>生产级 LLM 应用开发解决方案</span>
          </div>
          <div class="feature-item">
            <check-circle-filled class="icon" />
            <span>为 Java 开发者而生的 AI 基础设施</span>
          </div>
        </div>
      </div>
    </section>

    <section class="login-right">
      <div class="login-card">
        <div class="card-header">
          <h2 class="welcome-title">欢迎回来</h2>
          <p class="welcome-sub">请使用您的内部账号访问系统</p>
        </div>

        <a-form
          :model="formState"
          :rules="rules"
          class="login-form"
          @finish="handleLogin"
          layout="vertical"
        >
          <a-form-item name="username" label="用户名">
            <a-input
              v-model:value="formState.username"
              placeholder="Admin / User"
              size="large"
              class="custom-input"
            >
              <template #prefix><user-outlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item name="password" label="密码">
            <a-input-password
              v-model:value="formState.password"
              placeholder="••••••••"
              size="large"
              class="custom-input"
            >
              <template #prefix><lock-outlined /></template>
            </a-input-password>
          </a-form-item>

          <div class="form-options">
            <a-checkbox>记住我</a-checkbox>
            <a class="forget-pwd">忘记密码？</a>
          </div>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
              class="submit-btn"
            >
              即刻进入系统
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </section>

    <footer class="login-footer">
      <p>© 2026 Astrsomn 星梦科技 · 让 AI 开发回归简单</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { login } from '@/api/auth'
import { 
  UserOutlined, 
  LockOutlined, 
  CheckCircleFilled 
} from '@ant-design/icons-vue'
import logoUrl from '@/assets/Astrsomn.png'
import DocLangTheme from '@/components/top/DocLangTheme.vue'

const router = useRouter()
const loading = ref(false)

const formState = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

const goHome = () => router.push('/')

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login({
      username: formState.username,
      password: formState.password
    })

    localStorage.setItem('token', res.token)
    localStorage.setItem('userInfo', JSON.stringify(res.userInfo))

    message.success('验证成功')
    router.push('/admin')
  } catch (e: any) {
    message.error(e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  display: flex;
  width: 100vw;
  height: 100vh;
  background: var(--bg-base);
  color: var(--text-primary);
  overflow: hidden;
}

/* 顶部玻璃态导航 */
.glass-header {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 72px;
  padding: 0 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-dot {
  width: 32px; height: 32px;
  background: transparent;
  border-radius: 8px;
  display: flex;
  align-items: center; justify-content: center;
  box-shadow: none;
  overflow: hidden;
}

.logo-img {
  width: 100px;
  height: 80px;
  object-fit: cover;
  object-position: 50% 0%;
}

.logo-text {
  font-size: 18px; font-weight: 700;
  color: var(--text-heading);
}

.logo-text small {
  font-weight: 400; font-size: 14px; opacity: 0.6;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--border-subtle);
  padding: 4px 16px;
  border-radius: 20px;
  backdrop-filter: blur(8px);
}


.nav-item {
  color: var(--text-secondary);
  font-size: 13px;
  display: flex; align-items: center; gap: 4px;
}

.v-divider {
  width: 1px; height: 14px; background: var(--border-default);
}

.lang-select { width: 80px; }

.theme-toggle {
  background: none; border: none; cursor: pointer; font-size: 16px;
}

/* 左侧品牌区：光效与文字排版 */
.login-left {
  flex: 1.2;
  position: relative;
  display: flex;
  align-items: center;
  padding-left: 10%;
  background: var(--bg-surface);
  overflow: hidden;
}

.brand-visual-bg {
  position: absolute;
  width: 150%; height: 150%;
  top: -25%; left: -25%;
  background: radial-gradient(circle at 20% 50%, rgba(59, 130, 246, 0.1), transparent 40%),
              radial-gradient(circle at 80% 80%, rgba(16, 185, 129, 0.05), transparent 50%);
  z-index: 1;
}

.brand-content {
  position: relative;
  z-index: 2;
  max-width: 540px;
}

.tagline {
  color: var(--primary);
  font-weight: 600;
  letter-spacing: 2px;
  font-size: 12px;
  text-transform: uppercase;
  margin-bottom: 16px;
}

.brand-title {
  font-size: 52px;
  line-height: 1.1;
  font-weight: 800;
  color: var(--text-heading);
  margin-bottom: 32px;
}

.gradient-text {
  background: linear-gradient(135deg, var(--primary), var(--accent-cyan));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: var(--text-secondary);
}

.feature-item .icon {
  color: var(--success);
  font-size: 18px;
}

/* 右侧表单区：悬浮卡片感 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-base);
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 440px;
  padding: 48px;
  background: var(--bg-card);
  border-radius: 24px;
  border: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-card);
}

.card-header {
  margin-bottom: 32px;
  text-align: center;
}

.welcome-title {
  font-size: 28px; font-weight: 700;
  color: var(--text-heading);
  margin-bottom: 8px;
}

.welcome-sub {
  color: var(--text-muted);
  font-size: 14px;
}

/* 输入框定制 */
:deep(.custom-input) {
  border-radius: 12px !important;
  background: var(--bg-input) !important;
  border-color: var(--border-default) !important;
  transition: all 0.3s;
}
:deep(.ant-input){
  background-color: var(--bg-input) !important;
}

:deep(.custom-input:hover), :deep(.custom-input:focus) {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 3px var(--primary-hover) !important;
}

:deep(.ant-form-item-label label) {
  color: var(--text-secondary) !important;
  font-weight: 500;
}

.form-options {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  font-size: 13px;
}

.forget-pwd { color: var(--primary); }

.submit-btn {
  height: 50px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  background: var(--primary-gradient);
  border: none;
  box-shadow: 0 8px 20px rgba(0, 123, 255, 0.2);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(0, 123, 255, 0.3);
}

/* 页脚 */
.login-footer {
  position: absolute;
  bottom: 24px; left: 0; right: 0;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  pointer-events: none;
}

@media (max-width: 1024px) {
  .login-left { display: none; }
  .login-right { flex: 1; width: 100%; }
}
</style>