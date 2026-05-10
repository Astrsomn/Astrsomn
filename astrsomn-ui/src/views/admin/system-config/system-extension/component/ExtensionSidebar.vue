<template>
  <aside class="extension-sider" aria-label="扩展中心导航">
    <div class="sider-wrapper">
      <header class="sider-header">
        <div class="brand-info">
          <div class="brand-logo">A</div>
          <div class="brand-text">
            <h2 class="sider-title">扩展中心</h2>
            <span class="sider-version">v{{ appConfig.version }}</span>
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
  </aside>
</template>

<script setup lang="ts">
import { InboxOutlined, AppstoreOutlined, RocketFilled, RightOutlined } from '@ant-design/icons-vue'
import { appConfig } from '@/config'

defineProps<{
  selectedMenuKeys: string[]
}>()

const emit = defineEmits<{
  (e: 'menu-click', key: string): void
}>()

function onMenuClick({ key }: { key: string }) {
  emit('menu-click', key)
}

function openMarketplace() {
  window.open('https://www.astrsomn.com/market', '_blank')
}
</script>

<style scoped>
/* 侧边栏主体 - 与 AdminModuleShell 保持一致的宽度 */
.extension-sider {
  width: 260px;
  background: #ffffff;
  border-right: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  transition: all 0.3s;
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
  height: 38px !important;
  line-height: 38px !important;
  margin: 3px 0 !important;
  border-radius: 10px !important;
  color: #64748b !important;
  transition: all 0.2s;
}

:deep(.ant-menu-item:hover) {
  background: #f8fafc !important;
  color: #1a73e8 !important;
}

:deep(.ant-menu-item-selected) {
  background: #e8f0fe !important;
  color: #1a73e8 !important;
  font-weight: 600;
  position: relative;
}

:deep(.ant-menu-item-selected)::after {
  content: '';
  position: absolute;
  right: 8px;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 999px;
  background: #1a73e8;
}

/* 底部区域 */
.sider-footer {
  padding: 14px 12px;
  border-top: 1px solid #f1f5f9;
}

.promo-card {
  background: #e8f0fe;
  border: 1px solid #dbeafe;
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.promo-card:hover {
  background: #dbeafe;
}

.promo-content {
  display: flex;
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.promo-title {
  color: #1e293b;
  font-size: 13px;
  font-weight: 600;
}

.promo-slogan {
  color: #64748b;
  font-size: 10px;
}

.promo-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #1a73e8;
  background: #ffffff;
  font-size: 11px;
  font-weight: 500;
  padding: 6px 8px;
  border-radius: 6px;
}
</style>