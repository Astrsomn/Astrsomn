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
  background: var(--ab-glass-bg, rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border, rgba(255, 255, 255, 0.6));
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow, 0 4px 20px rgba(0, 0, 0, 0.03));
  padding: 20px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

.tool-card:hover {
  border-color: var(--ab-hover-line, #3b82f6);
  box-shadow: var(--ab-hover-shadow, 0 0 15px rgba(59, 130, 246, 0.15));
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
  background: #dbeafe;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  box-shadow: none;
}

.icon-badge .anticon {
  font-size: 16px;
}

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: #334155;
  margin: 0;
}

.add-btn {
  width: 28px;
  height: 28px;
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: none;
}

.add-btn:hover {
  background: #dbeafe;
  color: #1d4ed8;
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
  padding: 4px 8px;
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  font-weight: 600;
}

.tool-tag.dashed {
  background: #f8fafc;
  color: #94a3b8;
  border: 1px solid #e2e8f0;
  border-style: solid;
  cursor: pointer;
}

.tool-tag.dashed:hover {
  background: #fff;
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