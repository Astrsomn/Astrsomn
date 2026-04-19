<template>
  <div class="rag-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <DatabaseOutlined />
        </div>
        <h3 class="card-title">知识库 (RAG)</h3>
      </div>
      <div class="header-actions">
        <button class="action-btn">
          <SearchOutlined />
        </button>
        <button class="action-btn primary">
          <CloudUploadOutlined />
        </button>
      </div>
    </div>
    <div class="upload-area">
      <p class="upload-text">拖拽或点击上传文档数据</p>
      <span class="upload-hint">支持 PDF, Markdown, Notion</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { DatabaseOutlined, SearchOutlined, CloudUploadOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.rag-card {
  background: var(--bg-card);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-xl);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.rag-card:hover {
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
  background: rgba(16, 185, 129, 0.1);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #10b981;
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

.header-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 28px;
  height: 28px;
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: rgba(16, 185, 129, 0.2);
}

.action-btn.primary {
  background: #10b981;
  color: white;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}

.action-btn.primary:hover {
  transform: scale(1.05);
}

.action-btn .anticon {
  font-size: 12px;
}

.upload-area {
  padding: 24px;
  border: 2px dashed var(--border-default);
  border-radius: var(--radius-lg);
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.upload-area:hover {
  border-color: rgba(16, 185, 129, 0.5);
  background: rgba(16, 185, 129, 0.05);
}

.upload-text {
  font-size: 11px;
  color: var(--text-secondary);
  font-weight: 500;
  margin: 0;
}

.upload-hint {
  font-size: 9px;
  color: var(--text-tertiary);
  margin-top: 4px;
  display: block;
}
</style>