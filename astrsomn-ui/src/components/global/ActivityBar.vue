<template>
  <div class="activity-bar">
    <div class="activity-bar-top">
      <div class="activity-logo" @click="goHome">
        <div class="logo-glow"></div>
        <div class="logo-inner">
          <img
            v-if="logoSrc"
            alt="Astrsomn"
            class="logo-img"
            :src="logoSrc"
          />
          <span v-else class="logo-text">A</span>
        </div>
      </div>

      <a-tooltip v-for="item in modules" :key="item.key" placement="right">
        <template #title>{{ item.label }}</template>
        <div
            :class="{ 'is-active': isActive(item.route) }"
            class="activity-item"
            @click="navigateTo(item.route)"
        >
          <component
            :is="item.icon"
            :size="20"
            :weight="isActive(item.route) ? 'fill' : 'regular'"
            class="activity-icon"
          />
        </div>
      </a-tooltip>
    </div>

    <div class="activity-bar-bottom">
      <a-tooltip placement="right">
        <template #title>{{ t.activityBar.settings }}</template>
        <div
            :class="{ 'is-active': isActive('/admin/system') }"
            class="activity-item"
            @click="navigateTo('/admin/system')"
        >
          <component
            :is="PhGear"
            :size="20"
            :weight="isActive('/admin/system') ? 'fill' : 'regular'"
            class="activity-icon"
          />
        </div>
      </a-tooltip>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {PhGear, PhRobot, PhSliders} from '@phosphor-icons/vue'
import {useRoute, useRouter} from 'vue-router'
import {usePageTranslation} from '@/locales/pages.ts'

const route = useRoute()
const router = useRouter()
const t = usePageTranslation('common')

// 优先使用本地 logo 资源；不可用时回退到设计稿的渐变 "A"
const logoSrc = new URL('../../assets/Astrsomn-logo.png', import.meta.url).href

const modules = computed(() => [
  {key: 'ai-config', label: t.value.activityBar.aiConfig, icon: PhRobot, route: '/admin/ai-config-center'},
  {key: 'vector', label: t.value.activityBar.vectorCenter, icon: PhSliders, route: '/admin/vec-center'},
])

const isActive = (targetPath: string) => {
  const normalized = targetPath.replace(/\/+$/, '')
  const current = route.path.replace(/\/+$/, '')
  return current === normalized || current.startsWith(`${normalized}/`)
}

const navigateTo = (path: string) => {
  router.push(path)
}

const goHome = () => {
  // 始终切换到 chat 主页（统一入口）
  if (route.path !== '/') {
    router.push('/')
  }
}
</script>

<style scoped>
.activity-bar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 76px;
  background: var(--bg-default);
  border-right: 1px solid var(--border-default);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  padding: 20px 0 20px 4px;
}

.activity-bar-top,
.activity-bar-bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
}

/* ── Logo ── */
.activity-logo {
  position: relative;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  cursor: pointer;
  margin-bottom: 4px;
  background: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
  overflow: visible;
}

.activity-logo:hover .logo-glow {
  opacity: 0.6;
}

.logo-glow {
  position: absolute;
  inset: -4px;
  border-radius: 16px;
  background: linear-gradient(135deg, #3b82f6, #38bdf8);
  opacity: 0.2;
  filter: blur(8px);
  transition: opacity 0.3s ease;
  z-index: -1;
}

.logo-inner {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: #ffffff;
  color: #0f172a;
  font-weight: 700;
  font-size: 20px;
  overflow: hidden;
}

.logo-img {
  width: 26px;
  height: 26px;
  object-fit: contain;
}

.logo-text {
  background: linear-gradient(135deg, #3b82f6, #38bdf8);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: 800;
  font-size: 22px;
  line-height: 1;
}

/* ── Item 容器 ── */
.activity-item {
  position: relative;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-muted);
  background: transparent;
}

.activity-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--primary);
}

/* 激活：渐变背景 + 白色 icon + 投影 */
.activity-item.is-active {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: #ffffff;
  box-shadow:
    0 8px 16px -4px rgba(59, 130, 246, 0.4),
    0 4px 8px -2px rgba(59, 130, 246, 0.2);
}

/* 激活态左侧霓虹指示条 */
.activity-item.is-active::before {
  content: '';
  position: absolute;
  left: -4px;
  top: 14px;
  width: 3px;
  height: 20px;
  background: var(--primary);
  border-radius: 0 2px 2px 0;
  box-shadow: 0 0 8px 2px rgba(59, 130, 246, 0.6);
}

/* 悬停时也显示一个微弱的指示条 */
.activity-item:hover::before {
  content: '';
  position: absolute;
  left: -4px;
  top: 14px;
  width: 3px;
  height: 14px;
  background: var(--primary);
  border-radius: 0 2px 2px 0;
  opacity: 0.5;
}

.activity-item.is-active:hover::before {
  height: 20px;
  opacity: 1;
}

.activity-icon {
  font-size: 20px;
  color: currentColor;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}

.activity-icon :deep(svg) {
  display: block;
}

.activity-item:hover .activity-icon {
  transform: scale(1.08);
}
</style>
