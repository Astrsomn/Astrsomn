<template>
  <div class="tool-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <ToolOutlined />
        </div>
        <h3 class="card-title">扩展插件 (Tools/MCP)</h3>
      </div>
      <button class="add-btn">
        <PlusOutlined />
      </button>
    </div>
    <div class="tool-list">
      <div class="tool-tag">
        <span class="tool-letter">G</span>
        <span class="tool-name">联网搜索</span>
        <CloseOutlined class="tool-close" />
      </div>
      <div class="tool-tag dashed">
        <PlusOutlined class="tool-add-icon" />
        <span class="tool-name">绑定 MCP</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ToolOutlined, PlusOutlined, CloseOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.tool-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tool-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
  border-color: var(--primary);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
  box-shadow: var(--shadow-sm);
}

.icon-badge .anticon {
  font-size: 14px;
}

.card-title {
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.add-btn {
  width: 32px;
  height: 32px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}

.add-btn:hover {
  background: #3b82f6;
  color: white;
}

.add-btn .anticon {
  font-size: 14px;
}

.tool-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tool-tag {
  padding: 6px 12px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-tag.dashed {
  background: var(--bg-elevated);
  color: var(--text-secondary);
  border-style: dashed;
  cursor: pointer;
}

.tool-tag.dashed:hover {
  background: var(--bg-secondary);
}

.tool-letter {
  font-size: 10px;
}

.tool-name {
  font-size: 11px;
  font-weight: bold;
}

.tool-close {
  font-size: 10px;
  cursor: pointer;
  transition: color 0.2s;
}

.tool-close:hover {
  color: #ef4444;
}

.tool-add-icon {
  font-size: 10px;
}
</style>