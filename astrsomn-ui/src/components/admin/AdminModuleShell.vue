<template>
  <div class="module-shell">
    <aside v-if="showModuleSidebar" class="module-sider" aria-label="模块导航">
      <div class="module-sider-scroll">
        <nav class="module-sider-inner">
          <a-menu
              mode="inline"
              :inline-indent="12"
              :open-keys="openKeys"
              :selected-keys="selectedKeys"
              class="module-sider-menu"
              @openChange="onOpenChange"
          >
            <a-sub-menu v-for="g in groups" :key="g.key" class="module-sider-group">
              <template #title>
                <span class="module-sider-group-title">
                  <component
                      v-if="g.icon"
                      :is="g.icon"
                      class="anticon module-sider-group-icon"
                  />
                  <span>{{ g.label }}</span>
                </span>
              </template>

              <a-menu-item
                  v-for="it in g.children"
                  :key="it.to"
                  @click="() => go(it.to)"
                  class="module-sider-item"
              >
                <span class="module-sider-item-inner">
                  <component
                      v-if="it.icon"
                      :is="it.icon"
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
        <a href="https://www.astrsomn.com/home.html" target="_blank" class="official-btn">
          <GlobalOutlined class="btn-icon" />
          <span>访问官方网站</span>
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
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, provide, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { GlobalOutlined } from '@ant-design/icons-vue'; // 确保安装了 antd-icons
import { appPkgName, appPkgVersion } from '@/config/appMeta';
import type { AdminModuleNavGroup } from './adminModuleTypes';

const props = defineProps<{
  groups: AdminModuleNavGroup[];
}>();

const route = useRoute();
const router = useRouter();

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
  { immediate: true }
);

const appMeta = computed(() => {
  const mode = import.meta.env.MODE;
  return {
    name: appPkgName,
    version: appPkgVersion,
    envLabel: import.meta.env.PROD ? 'PRO' : mode.toUpperCase()
  };
});

const go = (to: string) => { void router.push(to); };
</script>

<style scoped>
/* 1. 基础容器：干净、稳定 */
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
  width: 260px;
  background: var(--bg-card);
  border-right: 1px solid var(--border-default);
  transition: all 0.3s;
}

.module-sider-scroll {
  flex: 1;
  overflow-y: auto;
  scrollbar-gutter: stable;
}

/* 隐藏滚动条但保留功能 */
.module-sider-scroll::-webkit-scrollbar { width: 4px; }
.module-sider-scroll::-webkit-scrollbar-thumb { background: transparent; border-radius: 4px; }
.module-sider-scroll:hover::-webkit-scrollbar-thumb { background: var(--border-default); }

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

/* 2. Menu 样式重塑：扁平化、去叠色 */
.module-sider-menu {
  border: none !important;
  background: transparent !important;
}

/* 一级菜单样式 */
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

/* 3. 二级菜单：取消背景色，改为引导线风格 */
:deep(.ant-menu-sub) {
  background: transparent !important;
  position: relative;
  padding-left: 10px !important; /* 缩进产生层级感 */
}

/* 引导线：模拟飞书/大厂设计 */
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
  padding-left: 32px !important; /* 给图标留空间 */
  color: var(--text-secondary) !important;
}

/* 选中项态 */
:deep(.ant-menu-item-selected) {
  background-color: var(--primary-hover) !important;
  color: var(--primary) !important;
  font-weight: 600;
  position: relative;
}

/* 选中提示条：显示在右侧 */
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

/* 4. 底部重新设计：官网入口 + 极简信息 */
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
  background: #22c55e;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(34, 197, 94, 0.4);
}

.env-label {
  font-size: 10px;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 内容区 */
.module-content {
  flex: 1;
  background: var(--bg-card);
  overflow-y: auto;
}
</style>