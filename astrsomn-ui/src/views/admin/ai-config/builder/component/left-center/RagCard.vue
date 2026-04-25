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
        <button class="action-btn primary" type="button" aria-label="上传">
          <CloudUploadOutlined />
        </button>
      </div>
    </div>
    <div class="upload-area">
      <CloudUploadOutlined class="upload-ico" />
      <p class="upload-text">拖拽或点击上传本地文档</p>
      <span class="upload-hint">PDF, Markdown, Docx…</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { DatabaseOutlined, SearchOutlined, CloudUploadOutlined } from '@ant-design/icons-vue';
</script>

<style scoped>
.rag-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
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

.rag-card:hover {
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
  background: #d1fae5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #059669;
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

.header-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 24px;
  height: 24px;
  background: transparent;
  color: #94a3b8;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #2563eb;
}

.action-btn.primary {
  background: #10b981;
  color: #fff;
  box-shadow: 0 1px 3px rgba(16, 185, 129, 0.35);
}

.action-btn.primary:hover {
  color: #fff;
  background: #059669;
}

.action-btn .anticon {
  font-size: 12px;
}

.upload-area {
  flex: 1;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px 16px;
  border: 1px dashed #e2e8f0;
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.2);
  text-align: center;
  transition:
    background 0.2s,
    border-color 0.2s;
  cursor: pointer;
}

.upload-ico {
  font-size: 22px;
  color: #cbd5e1;
}

.upload-area:hover {
  border-color: #cbd5e1;
  background: rgba(248, 250, 252, 0.5);
}

.upload-text {
  font-size: 10px;
  color: #94a3b8;
  font-weight: 500;
  margin: 0;
}

.upload-hint {
  font-size: 8px;
  color: #cbd5e1;
  display: block;
}
</style>