<template>
  <AdminPageShell
    title="系统扩展"
    description="管理系统扩展包"
    empty-text="暂无系统扩展。"
  >
    <div class="extension-page">
      <a-layout class="extension-layout">
        <a-layout-sider width="240" theme="light" class="extension-sider">
          <div class="sider-wrapper">
            <header class="sider-header">
              <div class="brand-info">
                <div class="brand-logo">A</div>
                <div class="brand-text">
                  <h2 class="sider-title">扩展中心</h2>
                  <span class="sider-version">v2.4.0</span>
                </div>
              </div>
            </header>

            <div class="sider-menu-container">
              <a-menu mode="inline" :selected-keys="selectedMenuKeys" @click="onMenuClick" class="custom-menu">
                <a-menu-item key="installed">
                  <template #icon><inbox-outlined /></template>
                  已安装插件
                </a-menu-item>
                <a-menu-item key="marketplace">
                  <template #icon><appstore-outlined /></template>
                  云端仓库
                </a-menu-item>
              </a-menu>
            </div>

            <div class="sider-footer">
              <div class="promo-card" @click="openMarketplace">
                <div class="promo-content">
                  <div class="promo-icon">
                    <rocket-filled />
                  </div>
                  <div class="promo-text">
                    <div class="promo-title">官方插件市场</div>
                    <div class="promo-slogan">探索更多无限可能</div>
                  </div>
                </div>
                <div class="promo-action">
                  <span>立即访问</span>
                  <right-outlined />
                </div>
              </div>
            </div>
          </div>
        </a-layout-sider>

        <a-layout-content class="extension-main">
          <div class="content-card">
            <MarketplacePage v-if="activePanel === 'marketplace'" />
            <InstalledPage v-else />
          </div>
        </a-layout-content>
      </a-layout>
    </div>
  </AdminPageShell>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { 
  InboxOutlined, 
  AppstoreOutlined, 
  RocketFilled, 
  RightOutlined 
} from '@ant-design/icons-vue'
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

function openMarketplace() {
  window.open('https://www.astrsomn.com/market', '_blank')
}
</script>

<style scoped>
.extension-page {
  padding: 0 12px;
  height: calc(100vh - 70px);
  overflow: hidden;
  background: #f8fafc;
}

.extension-layout {
  background: transparent;
  min-height: 100%;
  height: 100%;
  gap: 16px;
}

/* 侧边栏主体 */
.extension-sider {
  background: #ffffff !important;
  border: 1px solid #f1f5f9;
  border-radius: 14px;
  overflow: hidden;
  height: 100%;
}

.sider-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 顶部品牌区 */
.sider-header {
  padding: 20px 16px 12px;
}

.brand-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-logo {
  width: 30px;
  height: 30px;
  background: #1a73e8;
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
}

.sider-title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.2;
}

.sider-version {
  font-size: 10px;
  color: #94a3b8;
}

/* 菜单区 */
.sider-menu-container {
  flex: 1;
  padding: 0 10px 8px;
  overflow-y: auto;
}

.custom-menu {
  border: none !important;
  background: transparent !important;
}

:deep(.ant-menu-item) {
  height: 44px !important;
  line-height: 44px !important;
  margin: 4px 0 !important;
  border-radius: 10px !important;
  color: #666 !important;
}

:deep(.ant-menu-item-selected) {
  background: color-mix(in srgb, var(--primary) 8%, transparent) !important;
  color: var(--primary) !important;
}

/* 底部大图标卡片优化 */
.sider-footer {
  padding: 20px 12px;
}

.promo-card {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%); /* 使用更高级的紫色渐变 */
  border-radius: 14px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.promo-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
}

.promo-card::before {
  content: "";
  position: absolute;
  top: -20px;
  right: -20px;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.promo-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.promo-icon {
  font-size: 24px;
  color: #fff;
  background: rgba(255, 255, 255, 0.2);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  flex-shrink: 0;
}

.promo-title {
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}

.promo-slogan {
  color: rgba(255, 255, 255, 0.8);
  font-size: 11px;
  margin-top: 2px;
}

.promo-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 10px;
  border-radius: 6px;
}

/* 主内容区 */
.extension-main {
  overflow: hidden;
}

.content-card {
  background: #fff;
  border-radius: 16px;
  height: 100%;
  padding: 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  overflow-y: auto;
}

/* 滚动条样式 */
.content-card::-webkit-scrollbar {
  width: 6px;
}

.content-card::-webkit-scrollbar-track {
  background: var(--bg-surface);
  border-radius: 3px;
}

.content-card::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 3px;
}

.content-card::-webkit-scrollbar-thumb:hover {
  background: var(--text-tertiary);
}

/* Unified style: align with AdminModuleShell */
.sider-menu-container {
  padding: 0 10px 8px;
  overflow-y: auto;
}

:deep(.custom-menu .ant-menu-item) {
  height: 38px !important;
  line-height: 38px !important;
  margin: 3px 0 !important;
  border-radius: 10px !important;
  color: #64748b !important;
  transition: all 0.2s;
}

:deep(.custom-menu .ant-menu-item:hover) {
  background: #f8fafc !important;
  color: #1a73e8 !important;
}

:deep(.custom-menu .ant-menu-item-selected) {
  background: #e8f0fe !important;
  color: #1a73e8 !important;
  font-weight: 600;
  position: relative;
}

:deep(.custom-menu .ant-menu-item-selected)::after {
  content: '';
  position: absolute;
  right: 8px;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 999px;
  background: #1a73e8;
}

.sider-footer {
  padding: 14px 12px;
  border-top: 1px solid #f1f5f9;
}

.promo-card {
  background: #e8f0fe;
  border: 1px solid #dbeafe;
  border-radius: 12px;
  padding: 12px;
  box-shadow: none;
}

.promo-card:hover {
  background: #dbeafe;
  box-shadow: none;
}

.promo-card::before {
  display: none;
}

.promo-content {
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.promo-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  font-size: 16px;
  color: #1a73e8;
  background: #ffffff;
}

.promo-title {
  color: #1e293b;
  font-size: 13px;
}

.promo-slogan {
  color: #64748b;
  font-size: 10px;
}

.promo-action {
  color: #1a73e8;
  background: #ffffff;
  font-size: 11px;
  padding: 6px 8px;
}
</style>