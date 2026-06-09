<template>
  <div class="module-shell">
    <aside v-if="showModuleSidebar" :aria-label="t.layout.moduleNav" class="module-sider">
      <div class="module-sider-scroll">
        <nav class="module-sider-inner">
          <a-menu
              :inline-indent="12"
              :open-keys="openKeys"
              :selected-keys="selectedKeys"
              class="module-sider-menu"
              mode="inline"
              @openChange="onOpenChange"
          >
            <a-sub-menu v-for="g in groups" :key="g.key" class="module-sider-group">
              <template #title>
                <span class="module-sider-group-title">
                  <component
                      :is="g.icon"
                      v-if="g.icon"
                      class="anticon module-sider-group-icon"
                  />
                  <span>{{ g.label }}</span>
                </span>
              </template>

              <a-menu-item
                  v-for="it in g.children"
                  :key="it.to"
                  class="module-sider-item"
                  @click="() => go(it.to)"
              >
                <span class="module-sider-item-inner">
                  <component
                      :is="it.icon"
                      v-if="it.icon"
                      class="anticon module-sider-item-icon"
                  />
                  <span>{{ it.label }}</span>
                </span>
              </a-menu-item>
            </a-sub-menu>
          </a-menu>
        </nav>
      </div>

      <div class="module-sider-foot">
        <a class="official-btn" href="https://www.astrsomn.com/home.html" target="_blank">
          <GlobalOutlined class="btn-icon"/>
          <span>{{ t.layout.visitOfficialSite }}</span>
        </a>

        <div class="version-info">
          <div class="version-row">
            <span class="app-name">{{ appMeta.name }}</span>
            <span class="version-tag">v{{ appMeta.version }}</span>
          </div>
          <div class="status-row">
            <div class="status-dot"></div>
            <span class="env-label">{{ appMeta.envLabel }}</span>
          </div>
        </div>
      </div>
    </aside>

    <div class="module-content">
      <slot/>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, provide, ref, watch} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {GlobalOutlined} from '@ant-design/icons-vue'; // 确保安装了 antd-icons
import {appConfig} from '@/config/config.ts';
import {usePageTranslation} from '@/locales/pages.ts';
import type {AdminModuleNavGroup} from './utils/adminModuleTypes.ts';

const props = defineProps<{
  groups: AdminModuleNavGroup[];
}>();

const route = useRoute();
const router = useRouter();
const t = usePageTranslation('common');

provide('adminInModuleLayout', true);

const normalizePath = (path: string) => path.replace(/\/+$/, '') || '/';

const findMatchedGroupKey = (path: string) => {
  const currentPath = normalizePath(path);
  for (const g of props.groups) {
    for (const it of g.children) {
      const targetPath = normalizePath(it.to);
      if (currentPath === targetPath || currentPath.startsWith(`${targetPath}/`)) {
        return g.key;
      }
    }
  }
  return null;
};

const openKeys = ref<string[]>([]);

const onOpenChange = (next: (string | number)[]) => {
  const keys = (next as string[]).filter(Boolean);
  openKeys.value = keys.length ? [keys[keys.length - 1]] : [];
};

const showModuleSidebar = computed(() => {
  const m = route.matched[route.matched.length - 1];
  return m?.meta?.showModuleSidebar === true;
});

const selectedKeys = computed(() => {
  const p = normalizePath(route.path);
  for (const g of props.groups) {
    for (const it of g.children) {
      const t = normalizePath(it.to);
      if (p === t || p.startsWith(`${t}/`)) return [it.to];
    }
  }
  return [p];
});

watch(
    () => route.path,
    (path) => {
      const groupKey = findMatchedGroupKey(path);
      if (groupKey) openKeys.value = [groupKey];
    },
    {immediate: true}
);

const appMeta = computed(() => {
  const mode = import.meta.env.MODE;
  return {
    name: appConfig.name,
    version: appConfig.version,
    envLabel: import.meta.env.PROD ? 'PRO' : mode.toUpperCase()
  };
});

const go = (to: string) => {
  void router.push(to);
};
</script>

<style scoped>

.module-shell {
  display: flex;
  width: 100%;
  height: calc(100vh - 60px);
  background-color: var(--bg-surface);
  overflow: hidden;
}

.module-sider {
  display: flex;
  flex-direction: column;
  width: 288px;
  background: var(--bg-card);
  border-right: 1px solid var(--border-default);
  transition: width 0.3s;
}

.module-sider-scroll {
  flex: 1;
  overflow-y: auto;
  scrollbar-gutter: stable;
}


.module-sider-scroll::-webkit-scrollbar {
  width: 4px;
}

.module-sider-scroll::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 4px;
}

.module-sider-scroll:hover::-webkit-scrollbar-thumb {
  background: var(--border-default);
}

.module-sider-inner {
  padding: 0px 12px;
}

.module-sider-title {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-muted);
  padding: 0 16px 12px;
  letter-spacing: 1px;
  text-transform: uppercase;
}


.module-sider-menu {
  border: none !important;
  background: transparent !important;
}


:deep(.ant-menu-submenu-title) {
  margin: 2px 0 !important;
  height: 42px !important;
  line-height: 42px !important;
  border-radius: var(--radius-md) !important;
  color: var(--text-secondary) !important;
  transition: all 0.2s;
}

:deep(.ant-menu-submenu-title:hover) {
  background-color: var(--primary-hover) !important;
  color: var(--primary) !important;
}

.module-sider-group-title {
  display: flex;
  align-items: center;
  font-weight: 600;
}

.module-sider-group-icon {
  font-size: 16px;
  margin-right: 10px;
}


:deep(.ant-menu-sub) {
  background: transparent !important;
  position: relative;
  padding-left: 10px !important;
}


:deep(.ant-menu-sub)::before {
  content: "";
  position: absolute;
  left: 24px;
  top: 0;
  bottom: 12px;
  width: 1px;
  background: var(--border-default);
}

:deep(.ant-menu-item) {
  height: 38px !important;
  line-height: 38px !important;
  margin: 2px 0 !important;
  border-radius: var(--radius-md) !important;
  width: 100% !important;
  padding-left: 32px !important;
  color: var(--text-secondary) !important;
}


:deep(.ant-menu-item-selected) {
  background-color: var(--primary-hover) !important;
  color: var(--primary) !important;
  font-weight: 600;
  position: relative;
}


:deep(.ant-menu-item-selected)::after {
  content: "";
  position: absolute;
  right: 8px;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 999px;
  background-color: var(--primary);
}

.module-sider-item-inner {
  display: flex;
  align-items: center;
}

.module-sider-item-icon {
  font-size: 14px;
  margin-right: 8px;
  opacity: 0.7;
}


.module-sider-foot {
  padding: 16px;
  border-top: 1px solid var(--border-default);
}

.official-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  background: var(--primary-hover);
  color: var(--primary);
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 16px;
  transition: all 0.2s;
  border-radius: var(--radius-md);
}

.official-btn:hover {
  background: var(--primary);
  color: #fff;
  transform: translateY(-1px);
}

.version-info {
  padding: 0 4px;
}

.version-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.app-name {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-heading);
}

.version-tag {
  font-size: 10px;
  color: var(--text-muted);
  background: var(--bg-input);
  padding: 1px 6px;
  border-radius: 4px;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: var(--success);
  border-radius: 50%;
  box-shadow: 0 0 6px color-mix(in srgb, var(--success) 40%, transparent);
}

.env-label {
  font-size: 10px;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}


.module-content {
  flex: 1;
  background: var(--bg-card);
  overflow-y: auto;
}
</style>