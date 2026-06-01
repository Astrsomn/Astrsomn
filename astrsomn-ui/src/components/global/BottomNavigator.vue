<template>
  <div
      :class="{ 'dock-active': shouldShowDock }"
      class="dock-container"
      @mouseenter="isAutoHideEnabled ? handlePointerEnter() : undefined"
      @mouseleave="isAutoHideEnabled ? handlePointerLeave($event) : undefined"
  >
    <div
        v-if="isAutoHideEnabled"
        class="dock-wake-zone"
        @mouseenter="handlePointerEnter"
    ></div>

    <div class="dock-main">
      <div class="nav-group">
        <div :class="{ 'nav-item-active': isActivePath('/admin/ai-config-center') }" class="nav-item"
             @click="navigateTo('/admin/ai-config-center')">
          <div class="icon-wrapper">
            <RobotOutlined/>
          </div>
          <span class="nav-text">{{ t.bottomNav.aiConfig }}</span>
        </div>

        <div v-if="false" :class="{ 'nav-item-active': isActivePath('/admin/system') }" class="nav-item"
             @click="navigateTo('/admin/system')">
          <div class="icon-wrapper">
            <SettingOutlined/>
          </div>
          <span class="nav-text">{{ t.bottomNav.system }}</span>
        </div>
        <div :class="{ 'center-btn-active': isActivePath('/admin/ai-config/builder') }" class="center-btn"
             @click="navigateTo('/admin/ai-config/builder')">
          <div class="pulse-ring"></div>
          <ToolOutlined/>
          <span class="floating-label">Agent Studio</span>
        </div>


        <div v-if="false" :class="{ 'nav-item-active': isActivePath('/admin/ai-safety') }" class="nav-item"
             @click="navigateTo('/admin/ai-safety')">
          <div class="icon-wrapper">
            <SafetyOutlined/>
          </div>
          <span class="nav-text">{{ t.bottomNav.safety }}</span>
        </div>


        <div :class="{ 'nav-item-active': isActivePath('/admin/vec-center') }" class="nav-item"
             @click="navigateTo('/admin/vec-center')">
          <div class="icon-wrapper">
            <DatabaseOutlined/>
          </div>
          <span class="nav-text">{{ t.bottomNav.vectorCenter }}</span>
        </div>
      </div>
    </div>

    <div
        class="home-indicator"
    ></div>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, onUnmounted, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {DatabaseOutlined, RobotOutlined, SafetyOutlined, SettingOutlined, ToolOutlined} from '@ant-design/icons-vue';
import {usePageTranslation} from '@/locales/pages.ts';

const props = defineProps<{
  fixed?: boolean;
  autoHide?: boolean;
}>();

const router = useRouter();
const route = useRoute();
const t = usePageTranslation('common');
const isAutoHideEnabled = computed(() => props.autoHide ?? true);
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
  if (!isAutoHideEnabled.value) return;
  clearHideTimer();
  if (!isAtBottom.value) {
    isNavVisible.value = true;
  }
};

const handlePointerLeave = (event?: MouseEvent) => {
  if (!isAutoHideEnabled.value) return;

  if (event?.currentTarget instanceof HTMLElement && event.relatedTarget instanceof Node) {
    if (event.currentTarget.contains(event.relatedTarget)) return;
  }
  startHideTimer();
};

const shouldShowDock = computed(() => {
  if (!isAutoHideEnabled.value) return true;
  if (props.fixed) return true;
  if (isAtBottom.value) return false;
  return isNavVisible.value;
});

const handleScroll = () => {
  if (!isAutoHideEnabled.value || props.fixed) return;

  const currentScrollY = window.scrollY;
  const documentHeight = Math.max(
      document.documentElement.scrollHeight,
      document.body?.scrollHeight ?? 0
  );
  const maxScrollableDistance = documentHeight - window.innerHeight;
  const viewportBottom = currentScrollY + window.innerHeight;
  const bottomThreshold = 8;


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

const isActivePath = (targetPath: string) => {
  const normalizedTargetPath = router.resolve(targetPath).path.replace(/\/+$/, '');
  const currentPath = route.path.replace(/\/+$/, '');
  return currentPath === normalizedTargetPath || currentPath.startsWith(`${normalizedTargetPath}/`);
};

onMounted(() => {
  if (isAutoHideEnabled.value) {
    window.addEventListener('scroll', handleScroll);
    window.addEventListener('resize', handleScroll);
    handleScroll();
  }
});

onUnmounted(() => {
  if (isAutoHideEnabled.value) {
    window.removeEventListener('scroll', handleScroll);
    window.removeEventListener('resize', handleScroll);
  }
  clearHideTimer();
});
</script>

<style scoped>
.dock-container {
  position: fixed;
  bottom: 3px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;

  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}


.dock-wake-zone {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: min(440px, calc(100vw - 24px));
  height: 56px;
  pointer-events: auto;
}


.dock-main {
  background: color-mix(in srgb, var(--bg-surface) 75%, transparent);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid color-mix(in srgb, var(--border-default) 40%, transparent);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  box-shadow: 0 4px 6px -1px color-mix(in srgb, var(--shadow-color, #000) 5%, transparent),
  0 20px 40px -10px color-mix(in srgb, var(--shadow-color, #000) 10%, transparent);
  transform: translateY(120px) scale(0.9);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  pointer-events: none;
}


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


.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 16px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 72px;
  color: var(--text-secondary);
}

.nav-item:hover {
  background: color-mix(in srgb, var(--primary) 12%, transparent);
  color: var(--primary);
  transform: translateY(-4px);
}

.nav-item-active {
  background: color-mix(in srgb, var(--primary) 12%, transparent);
  color: var(--primary);
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


.center-btn {
  position: relative;
  width: 56px;
  height: 56px;
  background: var(--primary, #1677ff);
  color: white;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin: 0 12px;
  cursor: pointer;
  box-shadow: 0 8px 20px color-mix(in srgb, var(--primary) 35%, transparent);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.center-btn:hover {
  transform: scale(1.1) translateY(-8px);
  background: var(--primary-hover-active, #0958d9);
}

.center-btn-active {
  background: var(--primary);
  box-shadow: 0 10px 24px color-mix(in srgb, var(--primary) 45%, transparent);
}

.center-btn .floating-label {
  position: absolute;
  top: -35px;
  background: var(--bg-inverse, #1e293b);
  color: white;
  padding: 4px 10px;
  border-radius: var(--radius-md);
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


.home-indicator {
  position: relative;
  width: 120px;
  height: 16px;

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
  background: color-mix(in srgb, var(--bg-surface) 95%, transparent);
  border-radius: var(--radius-md);
  box-shadow: 0 2px 10px color-mix(in srgb, var(--shadow-color, #000) 18%, transparent);
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
  background: var(--bg-surface);
  width: 120px;
}


.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: var(--radius-md);
  background: var(--primary, #1677ff);
  opacity: 0.4;
  z-index: -1;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.4;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}


@media (max-width: 640px) {
  .nav-text {
    display: none;
  }

  .nav-item {
    min-width: 48px;
    padding: 12px;
  }

  .dock-main {
    padding: 6px;
    border-radius: var(--radius-md);
  }
}
</style>