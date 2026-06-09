<template>
  <div class="activity-bar">
    <div class="activity-bar-top">
      <div class="activity-logo" @click="goHome">
        <img alt="Astrsomn" class="logo-img" src="../../assets/Astrsomn-logo.png"/>
      </div>

      <a-tooltip v-for="item in modules" :key="item.key" placement="right">
        <template #title>{{ item.label }}</template>
        <div
            :class="{ 'is-active': isActive(item.route) }"
            class="activity-item"
            @click="navigateTo(item.route)"
        >
          <div class="activity-indicator"></div>
          <component :is="item.icon" :size="20" class="activity-icon" weight="regular"/>
        </div>
      </a-tooltip>
    </div>

    <div class="activity-bar-bottom">
      <a-tooltip placement="right">
        <template #title>{{ t.activityBar.settings }}</template>
        <div class="activity-item" @click="navigateTo('/admin/system')">
          <PhGear :size="20" class="activity-icon" weight="regular"/>
        </div>
      </a-tooltip>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {PhDatabase, PhGear, PhRobot} from '@phosphor-icons/vue'
import {useRoute, useRouter} from 'vue-router'
import {usePageTranslation} from '@/locales/pages.ts'

const route = useRoute()
const router = useRouter()
const t = usePageTranslation('common')

const modules = computed(() => [
  {key: 'ai-config', label: t.value.activityBar.aiConfig, icon: PhRobot, route: '/admin/ai-config-center'},
  {key: 'vector', label: t.value.activityBar.vectorCenter, icon: PhDatabase, route: '/admin/vec-center'},
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
  width: 56px;
  background: var(--bg-default);
  border-right: 1px solid #e5e6eb47;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  padding: 8px 0;
}

.activity-bar-top,
.activity-bar-bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  width: 100%;
}

.activity-logo {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.2s ease;
}

.activity-logo:hover {
  background: var(--primary-hover);
}

.logo-img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.activity-item {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-muted);
}

.activity-item:hover {
  background: var(--primary-hover);
  color: var(--primary);
}

.activity-item.is-active {
  color: var(--primary);
}

.activity-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: var(--primary);
  border-radius: 0 2px 2px 0;
  transition: height 0.2s ease;
}

.activity-item.is-active .activity-indicator {
  height: 20px;
}

.activity-item:hover .activity-indicator {
  height: 14px;
  opacity: 0.5;
}

.activity-item.is-active:hover .activity-indicator {
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
  transform: scale(1.1);
}
</style>
