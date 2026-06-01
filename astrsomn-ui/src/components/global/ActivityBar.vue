<template>
  <div class="activity-bar">
    <div class="activity-bar-top">
      <a-tooltip v-for="item in modules" :key="item.key" placement="right">
        <template #title>{{ item.label }}</template>
        <div
            :class="{ 'is-active': isActive(item.route) }"
            class="activity-item"
            @click="navigateTo(item.route)"
        >
          <div class="activity-indicator"></div>
          <component :is="item.icon" class="activity-icon"/>
        </div>
      </a-tooltip>
    </div>

    <div class="activity-bar-bottom">
      <a-tooltip placement="right">
        <template #title>{{ t.activityBar.settings }}</template>
        <div class="activity-item" @click="navigateTo('/admin/system')">
          <SettingOutlined class="activity-icon"/>
        </div>
      </a-tooltip>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {DatabaseOutlined, RobotOutlined, SettingOutlined} from '@ant-design/icons-vue'
import {useRoute, useRouter} from 'vue-router'
import {usePageTranslation} from '@/locales/pages.ts'

const route = useRoute()
const router = useRouter()
const t = usePageTranslation('common')

const modules = computed(() => [
  {key: 'ai-config', label: t.value.activityBar.aiConfig, icon: RobotOutlined, route: '/admin/ai-config-center'},
  {key: 'vector', label: t.value.activityBar.vectorCenter, icon: DatabaseOutlined, route: '/admin/vec-center'},
])

const isActive = (targetPath: string) => {
  const normalized = targetPath.replace(/\/+$/, '')
  const current = route.path.replace(/\/+$/, '')
  return current === normalized || current.startsWith(`${normalized}/`)
}

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.activity-bar {
  position: fixed;
  left: 0;
  top: 60px;
  bottom: 0;
  width: 56px;
  background: var(--bg-card);
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
}
</style>
