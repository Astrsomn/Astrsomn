<template>
  <div class="module-shell">
    <aside v-if="showModuleSidebar" class="module-sider" aria-label="模块导航">
      <div class="module-sider-scroll">
        <nav class="module-sider-inner">
          <div class="module-sider-title">管理导航</div>
          <a-menu
            mode="inline"
            :inline-indent="20"
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
      <div class="module-sider-foot" role="contentinfo" aria-label="环境信息">
        <div class="module-sider-foot-line app-name">{{ appMeta.name }}</div>
        <div class="module-sider-foot-line">v{{ appMeta.version }}</div>
        <div class="module-sider-foot-line text-muted">
          {{ appMeta.envLabel }}
        </div>
      </div>
    </aside>
    <div class="module-content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, provide, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { appPkgName, appPkgVersion } from '@/config/appMeta';
import type { AdminModuleNavGroup } from './adminModuleTypes';

const props = defineProps<{
  groups: AdminModuleNavGroup[];
}>();

const route = useRoute();
const router = useRouter();

/** 子页面可注入以收紧 min-height，适配侧栏布局 */
provide('adminInModuleLayout', true);

const openKeys = ref<string[]>([...props.groups.map((g) => g.key)]);

/** 默认全部分组展开 */
const onOpenChange = (next: (string | number)[]) => {
  openKeys.value = next as string[];
};

const showModuleSidebar = computed(() => {
  const m = route.matched[route.matched.length - 1];
  return m?.meta?.showModuleSidebar === true;
});

const selectedKeys = computed(() => {
  const p = route.path.replace(/\/+$/, '') || '/';
  for (const g of props.groups) {
    for (const it of g.children) {
      const t = it.to.replace(/\/+$/, '') || '/';
      if (p === t || p.startsWith(`${t}/`)) {
        return [it.to];
      }
    }
  }
  return [p];
});

const appMeta = computed(() => {
  const mode = import.meta.env.MODE;
  const isProd = import.meta.env.PROD;
  return {
    name: appPkgName,
    version: appPkgVersion,
    envLabel: isProd ? 'production' : mode
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
  max-height: calc(100vh - 60px);
  min-height: 0;
  overflow: hidden;
  box-sizing: border-box;
}

.module-sider {
  display: flex;
  flex-direction: column;
  flex: 0 0 240px;
  max-width: 240px;
  width: 240px;
  min-height: 0;
  align-self: stretch;
  border-right: 1px solid color-mix(in srgb, var(--text-muted) 20%, transparent);
  background: color-mix(in srgb, var(--bg-base) 86%, #fff 14%);
  overflow: hidden;
  z-index: 1;
}

.module-sider-scroll {
  flex: 1 1 auto;
  min-height: 0;
  overflow: hidden;
}

.module-sider-inner {
  padding: 16px 6px 12px;
}

.module-sider-title {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted);
  padding: 0 12px 10px;
}

.module-sider-group-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.module-sider-group-icon {
  flex-shrink: 0;
  font-size: 15px;
  opacity: 0.9;
}

.module-sider-item-inner {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-width: 0;
}

.module-sider-item-icon {
  flex-shrink: 0;
  font-size: 14px;
  opacity: 0.85;
}

.module-sider-menu {
  border-inline-end: none !important;
  background: transparent !important;
}

.module-sider :deep(.ant-menu-submenu) {
  margin: 0 0 2px;
}

.module-sider :deep(.ant-menu-submenu-title) {
  width: 100% !important;
  margin: 0 !important;
  padding-inline: 10px 12px !important;
  border-radius: 10px;
  height: 38px;
  line-height: 38px;
}

.module-sider :deep(.ant-menu-item) {
  border-radius: 8px;
  margin: 2px 0 2px 2px;
  width: auto;
  height: 36px;
  line-height: 36px;
  padding-inline: 8px 10px !important;
}

.module-sider :deep(.ant-menu-item-only-child) {
  padding-inline: 8px 10px !important;
}

.module-sider :deep(.ant-menu-submenu .ant-menu-sub) {
  background: transparent !important;
  padding: 0 0 2px 4px;
}

.module-sider-foot {
  flex-shrink: 0;
  padding: 10px 14px 14px;
  border-top: 1px solid color-mix(in srgb, var(--text-muted) 14%, transparent);
  font-size: 10px;
  line-height: 1.4;
  letter-spacing: 0.02em;
  color: var(--text-muted);
  font-feature-settings: 'tnum' 1;
}

.module-sider-foot-line {
  font-variant-numeric: tabular-nums;
}

.app-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 10.5px;
  margin-bottom: 1px;
}

.text-muted {
  color: var(--text-muted);
  font-size: 9.5px;
  margin-top: 1px;
}

.module-content {
  flex: 1 1 0;
  min-width: 0;
  min-height: 0;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
