<template>
  <div class="builder-container">
    <header class="builder-header">
      <div class="header-left">
        <div class="logo-badge">
          <LoadingOutlined />
        </div>
        <h1 class="header-title">Agent Studio</h1>
        <div class="header-divider"></div>
        <div class="header-status">
          正在编辑: <span class="status-value">未命名的智能体</span>
        </div>
      </div>
      <div class="header-right">
        <button class="btn-ghost">预览</button>
        <button class="btn-primary">完成创建</button>
      </div>
    </header>

    <main class="builder-main">
      <section class="builder-left">
        <LeftTop />
        <LeftCenter />
      </section>
      <Right />
    </main>

    <div class="float-toolbar">
      <div class="toolbar-group">
        <button class="toolbar-btn" title="配置导出">
          <CodeOutlined />
        </button>
        <button class="toolbar-btn" title="版本管理">
          <HistoryOutlined />
        </button>
        <div class="toolbar-divider"></div>
        <button class="toolbar-btn" title="帮助文档">
          <QuestionCircleOutlined />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  LoadingOutlined,
  CodeOutlined,
  HistoryOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons-vue';
import LeftTop from './component/LeftTop.vue';
import LeftCenter from './component/LeftCenter.vue';
import Right from './component/Right.vue';
</script>

<style scoped>
.builder-container {
  height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-size: 13px;
  color: var(--text-primary);
}

.builder-header {
  height: 56px;
  flex-shrink: 0;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  z-index: 40;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo-badge {
  width: 32px;
  height: 32px;
  background: var(--primary-gradient);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: var(--shadow-overview);
  font-size: 16px;
}

.header-title {
  font-weight: bold;
  font-size: 18px;
  color: var(--text-heading);
  margin: 0;
}

.header-divider {
  width: 1px;
  height: 16px;
  background: var(--border-default);
}

.header-status {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-secondary);
}

.status-value {
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-ghost {
  color: var(--text-secondary);
  font-weight: 600;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.3s;
}

.btn-ghost:hover {
  color: var(--text-primary);
}

.btn-primary {
  background: var(--primary-gradient);
  color: white;
  padding: 8px 20px;
  border-radius: var(--radius-lg);
  font-weight: bold;
  border: none;
  cursor: pointer;
  box-shadow: var(--shadow-overview);
  transition: all 0.3s;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-primary:active {
  transform: scale(0.95);
}

.builder-main {
  flex: 1;
  display: flex;
  overflow: hidden;
  min-height: 0;
}

.builder-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--bg-base);
}

.float-toolbar {
  position: fixed;
  bottom: 24px;
  left: 32px;
  display: flex;
  gap: 12px;
  z-index: 50;
}

.toolbar-group {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: 20px;
  padding: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: var(--shadow-card);
}

.toolbar-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
}

.toolbar-btn:hover {
  color: var(--primary);
  background: var(--primary-hover);
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background: var(--border-default);
  margin: 0 4px;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}
</style>