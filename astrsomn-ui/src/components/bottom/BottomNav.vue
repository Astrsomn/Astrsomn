<template>
  <div 
    class="dock-container" 
    :class="{ 'dock-active': shouldShowDock }"
  >
    <div
      class="dock-wake-zone"
      @mouseenter="handlePointerEnter"
      @mouseleave="handlePointerLeave"
    ></div>

    <div class="dock-main" @mouseenter="handlePointerEnter" @mouseleave="handlePointerLeave">
      <div class="nav-group">
        <div class="nav-item" @click="navigateTo('/admin/ai-config-center')">
          <div class="icon-wrapper"><RobotOutlined /></div>
          <span class="nav-text">AI 配置</span>
        </div>

        <div class="nav-item" @click="navigateTo('/admin/vec-center')">
          <div class="icon-wrapper"><DatabaseOutlined /></div>
          <span class="nav-text">向量中心</span>
        </div>
        
        <div class="center-btn" @click="navigateTo('/admin/builder')">
          <div class="pulse-ring"></div>
          <ExperimentOutlined />
          <span class="floating-label">Agent Studio</span>
        </div>
        <div class="nav-item" @click="navigateTo('/admin/system-config-center')">
          <div class="icon-wrapper"><SettingOutlined /></div>
          <span class="nav-text">系统管理</span>
        </div>


        <div class="nav-item" @click="navigateTo('/admin/ai-safety-center')">
          <div class="icon-wrapper"><SafetyOutlined /></div>
          <span class="nav-text">安全治理</span>
        </div>
      </div>
    </div>

    <div class="home-indicator" @mouseenter="handlePointerEnter" @mouseleave="handlePointerLeave"></div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  DatabaseOutlined,
  SettingOutlined,
  ExperimentOutlined,
  SafetyOutlined,
  RobotOutlined
} from '@ant-design/icons-vue';

const router = useRouter();
const isNavVisible = ref(false);
const isAtBottom = ref(false);
const AUTO_HIDE_DELAY = 200;
let hideTimer: ReturnType<typeof setTimeout> | null = null;

const clearHideTimer = () => {
  if (hideTimer) {
    clearTimeout(hideTimer);
    hideTimer = null;
  }
};

const startHideTimer = () => {
  clearHideTimer();
  hideTimer = setTimeout(() => {
    isNavVisible.value = false;
  }, AUTO_HIDE_DELAY);
};

const handlePointerEnter = () => {
  clearHideTimer();
  if (!isAtBottom.value) {
    isNavVisible.value = true;
  }
};

const handlePointerLeave = () => {
  startHideTimer();
};

const shouldShowDock = computed(() => {
  if (isAtBottom.value) return false;
  return isNavVisible.value;
});

const handleScroll = () => {
  const currentScrollY = window.scrollY;
  const documentHeight = Math.max(
    document.documentElement.scrollHeight,
    document.body?.scrollHeight ?? 0
  );
  const maxScrollableDistance = documentHeight - window.innerHeight;
  const viewportBottom = currentScrollY + window.innerHeight;
  const bottomThreshold = 8;

  // 只有页面可滚动时，才判定“滚动到底部”
  isAtBottom.value =
    maxScrollableDistance > bottomThreshold &&
    viewportBottom >= documentHeight - bottomThreshold;

  if (isAtBottom.value) {
    isNavVisible.value = false;
    clearHideTimer();
  }
};

const navigateTo = (path: string) => {
  router.push(path);
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('resize', handleScroll);
  handleScroll();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('resize', handleScroll);
  clearHideTimer();
});
</script>

<style scoped>
.dock-container {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 12px;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* 底部唤醒热区：悬浮到操作栏预期位置即可唤醒 */
.dock-wake-zone {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: min(440px, calc(100vw - 24px));
  height: 56px;
  pointer-events: auto;
}

/* 默认隐藏状态：下移并透明 */
.dock-main {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 32px;
  padding: 8px 16px;
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.05),
    0 20px 40px -10px rgba(0, 0, 0, 0.1);
  transform: translateY(120px) scale(0.9);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  pointer-events: none;
}

/* 激活状态 */
.dock-active .dock-main {
  transform: translateY(0) scale(1);
  opacity: 1;
  pointer-events: auto;
}

.nav-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 导航项基础样式 */
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 16px;
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 72px;
  color: #64748b;
}

.nav-item:hover {
  background: rgba(99, 102, 241, 0.08);
  color: #6366f1;
  transform: translateY(-4px);
}

.icon-wrapper {
  font-size: 20px;
  margin-bottom: 4px;
}

.nav-text {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

/* 中心按钮特别强化 */
.center-btn {
  position: relative;
  width: 56px;
  height: 56px;
  background: #6366f1;
  color: white;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 12px;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.center-btn:hover {
  transform: scale(1.1) translateY(-8px);
  background: #4f46e5;
}

.center-btn .floating-label {
  position: absolute;
  top: -35px;
  background: #1e293b;
  color: white;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 10px;
  white-space: nowrap;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
  pointer-events: none;
}

.center-btn:hover .floating-label {
  opacity: 1;
  transform: translateY(0);
}

/* 底部横条 */
.home-indicator {
  position: relative;
  width: 120px;
  height: 16px;
  margin-top: 14px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.home-indicator::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 112px;
  height: 5px;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.18);
  transition: all 0.3s ease;
}

.dock-active .home-indicator {
  transform: translateY(4px);
}

.dock-active .home-indicator::before {
  opacity: 0.9;
  width: 72px;
}

.home-indicator:hover::before {
  background: #ffffff;
  width: 120px;
}

/* 呼吸灯效果 */
.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 18px;
  background: #6366f1;
  opacity: 0.4;
  z-index: -1;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.4; }
  100% { transform: scale(1.5); opacity: 0; }
}

/* 响应式 */
@media (max-width: 640px) {
  .nav-text { display: none; }
  .nav-item { min-width: 48px; padding: 12px; }
  .dock-main { padding: 6px; border-radius: 24px; }
}

/* 深色模式适配 - 使用CSS变量 */
@media (prefers-color-scheme: dark) {
  .dock-main {
    background: rgba(15, 23, 42, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 
      0 4px 6px -1px rgba(0, 0, 0, 0.3),
      0 20px 40px -10px rgba(0, 0, 0, 0.4);
  }
  
  .nav-item { 
    color: #94a3b8; 
  }
  
  .nav-item:hover {
    background: rgba(99, 102, 241, 0.15);
    color: #818cf8;
  }
  
  .home-indicator::before { 
    background: rgba(255, 255, 255, 0.9);
  }
  
  .home-indicator:hover::before {
    background: rgba(255, 255, 255, 1);
  }
  
  .center-btn {
    background: #6366f1;
    box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
  }
  
  .center-btn:hover {
    background: #4f46e5;
  }
  
  .center-btn .floating-label {
    background: rgba(15, 23, 42, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
}

/* 使用CSS变量的深色模式支持 */
:root.dark .dock-main {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.3),
    0 20px 40px -10px rgba(0, 0, 0, 0.4);
}

:root.dark .nav-item { 
  color: #94a3b8; 
}

:root.dark .nav-item:hover {
  background: rgba(99, 102, 241, 0.15);
  color: #818cf8;
}

:root.dark .home-indicator::before { 
  background: rgba(255, 255, 255, 0.9); 
}

:root.dark .home-indicator:hover::before {
  background: rgba(255, 255, 255, 1);
}

:root.dark .center-btn {
  background: #6366f1;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
}

:root.dark .center-btn:hover {
  background: #4f46e5;
}

:root.dark .center-btn .floating-label {
  background: rgba(15, 23, 42, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 浅色模式支持 */
:root.light .dock-main {
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.05),
    0 20px 40px -10px rgba(0, 0, 0, 0.1);
}

:root.light .nav-item { 
  color: #64748b; 
}

:root.light .nav-item:hover {
  background: rgba(99, 102, 241, 0.08);
  color: #6366f1;
}

:root.light .home-indicator::before { 
  background: rgba(255, 255, 255, 0.95); 
}

:root.light .home-indicator:hover::before {
  background: #ffffff;
}

:root.light .center-btn {
  background: #6366f1;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3);
}

:root.light .center-btn:hover {
  background: #4f46e5;
}

:root.light .center-btn .floating-label {
  background: #1e293b;
}
</style>