<template>
  <AdminPageShell
    title="系统扩展"
    description="管理系统扩展包"
    empty-text="暂无系统扩展。"
  >
    <div class="extension-page">
      <a-layout class="extension-layout">
        <a-layout-sider width="252" theme="light" class="extension-sider">
          <div class="sider-shell">
            <header class="sider-brand">
           
              <h2 class="sider-title">Astrsomn系统扩展</h2>
              <p class="sider-desc">
                浏览市场插件并安装到当前环境，在已安装列表中应用、同步模型与卸载维护。
              </p>
            </header>

            <div class="sider-menu-wrap">
              <a-menu mode="inline" :selected-keys="selectedMenuKeys" @click="onMenuClick">
                <a-menu-item key="marketplace">
                  <template #icon><shop-outlined /></template>
                  插件市场
                </a-menu-item>
                <a-menu-item key="installed">
                  <template #icon><inbox-outlined /></template>
                  已安装插件
                </a-menu-item>
              </a-menu>
            </div>

            <footer class="sider-foot">
              <p class="foot-line">扩展包对接 </p>
              <p class="foot-muted">安装前请确认环境与依赖兼容</p>
            </footer>
          </div>
        </a-layout-sider>
        <a-layout-content class="extension-main">
          <MarketplacePage v-if="activePanel === 'marketplace'" />
          <InstalledPage v-else />
        </a-layout-content>
      </a-layout>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ShopOutlined, InboxOutlined } from '@ant-design/icons-vue'
import AdminPageShell from '@/components/home/AdminPageShell.vue'
import MarketplacePage from './marketplace/MarketplacePage.vue'
import InstalledPage from './installed/InstalledPage.vue'

type ExtensionPanel = 'marketplace' | 'installed'

const activePanel = ref<ExtensionPanel>('installed')

const selectedMenuKeys = computed(() => [activePanel.value])

function onMenuClick({ key }: { key: string }) {
  if (key === 'marketplace' || key === 'installed') {
    activePanel.value = key
  }
}
</script>

<style scoped>
.extension-page {
  padding: 0 4px;
}

.extension-layout {
  background: transparent;
  align-items: stretch;
  height: calc(100vh - 70px);
}

.extension-sider {
  border-radius: var(--radius-lg);
  border: none;
  background: linear-gradient(
      165deg,
      color-mix(in srgb, var(--primary) 7%, var(--bg-surface)) 0%,
      var(--bg-surface) 42%,
      color-mix(in srgb, var(--bg-card) 88%, var(--bg-surface)) 100%
    )
    !important;
  box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--border-default) 45%, transparent),
    0 8px 28px -18px color-mix(in srgb, var(--primary) 22%, transparent);
  overflow: hidden;
}

.extension-sider :deep(.ant-layout-sider-children) {
  height: 100%;
  padding: 0;
}

.sider-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.sider-brand {
  padding: 18px 16px 14px;
  border-bottom: 1px solid color-mix(in srgb, var(--border-default) 35%, transparent);
}

.brand-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.brand-name {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--primary-light);
}

.brand-pill {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.04em;
  padding: 2px 8px;
  border-radius: 999px;
  color: var(--text-muted);
  background: color-mix(in srgb, var(--primary) 9%, transparent);
  border: 1px solid color-mix(in srgb, var(--primary) 14%, transparent);
}

.sider-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 700;
  line-height: 1.25;
  color: var(--text-primary);
}

.sider-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.55;
  color: var(--text-muted);
}

.sider-menu-wrap {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 12px 8px 8px;
}

.sider-foot {
  padding: 12px 16px 16px;
  margin-top: auto;
  border-top: 1px solid color-mix(in srgb, var(--border-default) 30%, transparent);
}

.foot-line {
  margin: 0 0 4px;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
  line-height: 1.4;
}

.foot-muted {
  margin: 0;
  font-size: 11px;
  line-height: 1.45;
  color: var(--text-muted);
}

.extension-sider :deep(.ant-menu) {
  background: transparent;
  color: var(--text-primary);
  border-inline-end: none !important;
}

.extension-sider :deep(.ant-menu-item) {
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  margin: 4px 4px;
  width: auto;
  height: auto !important;
  line-height: 1.35 !important;
  padding: 10px 12px !important;
}

.extension-sider :deep(.ant-menu-item .anticon) {
  font-size: 16px;
}

.extension-sider :deep(.ant-menu-item-selected) {
  background: color-mix(in srgb, var(--primary) 14%, transparent) !important;
  color: var(--primary-light) !important;
}

.extension-sider :deep(.ant-menu-item:hover) {
  color: var(--text-hover);
}

.extension-main {
  padding-left: 16px;
  min-width: 0;
}

@media (max-width: 720px) {
  .extension-layout {
    flex-direction: column;
  }

  .extension-sider {
    width: 100% !important;
    max-width: 100% !important;
    margin-bottom: 12px;
  }

  .extension-main {
    padding-left: 0;
  }
}
</style>
