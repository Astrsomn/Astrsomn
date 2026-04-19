<template>
  <div class="credential-card" @click="navigateTo('/admin/ai-account')">
    <div class="card-icon sky-icon">
      <KeyOutlined />
    </div>
    <h3 class="card-title">凭证管理</h3>
    <p class="card-desc">维护 API_KEY 与账户额度</p>
    
    <div class="account-stack">
      <div class="stack-item i1">
        <div class="account-icon-inner bg-indigo-500">
          <span class="account-initial">O</span>
        </div>
      </div>
      <div class="stack-item i2">
        <div class="account-icon-inner bg-blue-500">
          <span class="account-initial">Q</span>
        </div>
      </div>
      <div class="stack-item i3">
        <div class="account-icon-inner bg-slate-800">
          <span class="count-text">+5</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { KeyOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.credential-card {
  position: relative;
  /* 基础背景：半透明深色，配合毛玻璃 */
  background: rgba(18, 30, 45, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  
  /* 边框建议使用带有透明度的深色，模拟玻璃边缘 */
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 24px;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  cursor: pointer;
  overflow: hidden; /* 确保渐变背景不溢出 */
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
}

/* 核心：底层动态/彩色弥散渐变 */
.credential-card::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  /* 高级感配色：紫罗兰、天空蓝、薄荷绿的弥散 */
  background: radial-gradient(circle at 30% 30%, rgba(224, 195, 252, 0.3) 0%, transparent 40%),
              radial-gradient(circle at 70% 60%, rgba(142, 197, 252, 0.3) 0%, transparent 40%),
              radial-gradient(circle at 40% 80%, rgba(194, 255, 216, 0.3) 0%, transparent 40%);
  z-index: -1;
  opacity: 1;
  transition: transform 0.8s ease;
}

.credential-card:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 12px 40px 0 rgba(0, 0, 0, 0.4);
}

.credential-card:hover::before {
  /* 鼠标悬浮时背景发生位移，产生流动感 */
  transform: translate(10%, 10%) rotate(5deg);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  /* 这里的图标背景也要透亮 */
  background: rgba(255, 255, 255, 0.1);
  color: var(--primary);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  /* 文字颜色改回深色，毛玻璃背景下深色文字更高级 */
  color: var(--text-primary);
  margin: 0 0 8px 0;
  z-index: 1;
}

.card-desc {
  font-size: 13px;
  color: var(--text-muted);
  margin: 0 0 20px 0;
  line-height: 1.6;
  z-index: 1;
}

.account-stack {
  display: flex;
  flex-direction: row-reverse;
  margin-top: auto;
  padding-top: 16px;
  z-index: 1;
}

.stack-item {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  margin-left: -10px;
  transition: all 0.3s ease;
  padding: 2px;
  /* 头像外圈也采用半透明感 */
  background: rgba(255, 255, 255, 0.1);
}

.credential-card:hover .stack-item {
  margin-left: 4px;
}

.account-icon-inner {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.account-initial {
  text-transform: uppercase;
}

.count-text {
  font-size: 11px;
}

/* 修改原有特定颜色，使其更柔和 */
.bg-indigo-500 { background: linear-gradient(135deg, #6366f1, #8b5cf6); }
.bg-blue-500 { background: linear-gradient(135deg, #3b82f6, #2dd4bf); }
.bg-slate-800 { background: #1e293b; }

.i1 { z-index: 3; }
.i2 { z-index: 2; }
.i3 { z-index: 1; }

/* 浅色模式适配 */
:root.light .credential-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.07);
}

:root.light .credential-card::before {
  background: radial-gradient(circle at 30% 30%, rgba(224, 195, 252, 0.6) 0%, transparent 40%),
              radial-gradient(circle at 70% 60%, rgba(142, 197, 252, 0.6) 0%, transparent 40%),
              radial-gradient(circle at 40% 80%, rgba(194, 255, 216, 0.6) 0%, transparent 40%);
}

:root.light .credential-card:hover {
  border-color: var(--primary);
  box-shadow: 0 12px 40px 0 rgba(0, 0, 0, 0.15);
}

:root.light .stack-item {
  background: rgba(255, 255, 255, 0.8);
}

:root.light .card-icon {
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 移除重复的样式定义 */</style>