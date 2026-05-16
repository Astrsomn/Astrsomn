<template>
  <aside aria-label="扩展中心导航" class="extension-sider">
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
        <a-menu :selected-keys="selectedMenuKeys" class="custom-menu" mode="inline" @click="onMenuClick">
          <a-menu-item key="installed">
            <template #icon>
              <inbox-outlined/>
            </template>
            已安装插件
          </a-menu-item>
          <a-menu-item key="marketplace">
            <template #icon>
              <appstore-outlined/>
            </template>
            云端仓库
          </a-menu-item>
        </a-menu>
      </div>

      <div class="sider-footer">
        <div class="promo-card" @click="openMarketplace">
          <div class="promo-content">
            <div class="promo-icon">
              <rocket-filled/>
            </div>
            <div class="promo-text">
              <div class="promo-title">官方插件市场</div>
              <div class="promo-slogan">探索更多无限可能</div>
            </div>
          </div>
          <div class="promo-action">
            <span>立即访问</span>
            <right-outlined/>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script lang="ts" setup>
import {AppstoreOutlined, InboxOutlined, RightOutlined, RocketFilled} from '@ant-design/icons-vue'
import {appConfig} from '@/config/config.ts'

defineProps<{
  selectedMenuKeys: string[]
}>()

const emit = defineEmits<{
  (e: 'menu-click', key: string): void
}>()

function onMenuClick({key}: { key: string }) {
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
  background: var(--bg-card);
  border-right: 1px solid var(--border-default);
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
  background: var(--primary);
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
  color: var(--text-heading);
  line-height: 1.2;
}

.sider-version {
  font-size: 10px;
  color: var(--text-muted);
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
  color: var(--text-secondary) !important;
  transition: all 0.2s;
}

:deep(.ant-menu-item:hover) {
  background: var(--primary-hover) !important;
  color: var(--primary) !important;
}

:deep(.ant-menu-item-selected) {
  background: var(--primary-hover) !important;
  color: var(--primary) !important;
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
  background: var(--primary);
}

/* 底部区域 */
.sider-footer {
  padding: 14px 12px;
  border-top: 1px solid var(--border-default);
}

.promo-card {
  background: var(--primary-hover);
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.promo-card:hover {
  background: var(--primary);
  color: #fff;
}

.promo-card:hover .promo-icon,
.promo-card:hover .promo-title,
.promo-card:hover .promo-slogan,
.promo-card:hover .promo-action {
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
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
  color: var(--primary);
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
}

.promo-title {
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 600;
}

.promo-slogan {
  color: var(--text-secondary);
  font-size: 10px;
}

.promo-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--primary);
  background: var(--bg-card);
  font-size: 11px;
  font-weight: 500;
  padding: 6px 8px;
  border-radius: 6px;
}
</style>