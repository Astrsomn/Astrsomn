<template>
  <a-card :bordered="false" class="custom-file-card" :class="{ active }" :body-style="{ padding: 0 }" @click="$emit('select', file)">
    <div class="square-container">
      <div class="inner-content">
        
        <div class="action-group">
          <div class="action-btn" title="编辑" @click.stop="$emit('edit', file)"><edit-outlined /></div>
          <div class="action-btn vectorize" title="向量化" @click.stop="$emit('vectorize', file)"><experiment-outlined /></div>
          <div class="action-btn delete" title="删除" @click.stop="$emit('delete', file)"><delete-outlined /></div>
        </div>

        <div class="main-body">
          <div class="status-dot" :class="file.status === '已向量化' ? 'ready' : 'pending'"></div>
          <div class="icon-box" :class="getFileExtension(file.name)">
            <component :is="getFileIcon(file.name)" class="file-icon-svg" />
          </div>
          <h3 class="file-name" :title="file.name">{{ file.name }}</h3>
        </div>

        <div class="footer-overlay">
          <span class="file-size">{{ file.size }}</span>
          <span class="upload-time">{{ file.uploadTime }}</span>
        </div>

      </div>
    </div>
  </a-card>
</template>

<script lang="ts" setup>
import {
  FileTextOutlined,
  FilePdfOutlined,
  FileMarkdownOutlined,
  EditOutlined,
  DeleteOutlined,
  ExperimentOutlined
} from '@ant-design/icons-vue';

defineProps<{
  file: {
    id?: number | string;
    name: string;
    segments: number;
    size: string;
    status: string;
    uploadTime?: string;
  };
  active?: boolean;
}>();

defineEmits<{
  select: [file: any]
  edit: [file: any]
  vectorize: [file: any]
  delete: [file: any]
}>()

const getFileIcon = (name: string) => {
  const ext = name.split('.').pop()?.toLowerCase();
  if (ext === 'pdf') return FilePdfOutlined;
  if (ext === 'md') return FileMarkdownOutlined;
  return FileTextOutlined;
};

const getFileExtension = (name: string) => {
  return name.split('.').pop()?.toLowerCase() || 'txt';
};
</script>

<style lang="less" scoped>
.custom-file-card {
  cursor: pointer;
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  border: 1px solid var(--border-default) !important;
  transition: all 0.3s ease;
  overflow: hidden;

  &:hover {
    background: var(--bg-elevated);
    transform: translateY(-4px);
    box-shadow: var(--shadow-card);
    
    .action-group { opacity: 1; transform: translateY(0); }
    .icon-box { transform: scale(1.1); }
  }

  &.active {
    border-color: var(--primary) !important;
    box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  }

  // 核心：强制正方形方案
  .square-container {
    position: relative;
    width: 100%;
    padding-top: 100%; // 关键：利用 padding 实现 1:1 比例
    
    .inner-content {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
  }

  .main-body {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    z-index: 2;

    .status-dot {
      position: absolute;
      top: 12px;
      left: 12px;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      &.ready { background: var(--success); }
      &.pending { background: #f59e0b; }
    }

    .icon-box {
      width: 54px;
      height: 54px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: var(--radius-lg);
      background: var(--bg-input);
      font-size: 30px;
      color: var(--text-secondary);
      transition: transform 0.3s ease;
      
      .file-icon-svg, span {
        display: inline-block !important;
        line-height: 1;
      }

      &.pdf { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
      &.md { background: rgba(59, 130, 246, 0.1); color: var(--primary); }
    }

    .file-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-heading);
      margin: 0;
      max-width: 80%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .action-group {
    position: absolute;
    top: 10px;
    right: 10px;
    display: flex;
    gap: 4px;
    opacity: 0;
    transform: translateY(-5px);
    transition: all 0.3s ease;
    z-index: 10;

    .action-btn {
      width: 26px;
      height: 26px;
      background: var(--bg-input);
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: var(--text-secondary);
      &:hover { background: var(--primary); color: #fff; }
      &.delete:hover { background: var(--error); }
    }
  }

  .footer-overlay {
    position: absolute;
    bottom: 10px;
    left: 0;
    width: 100%;
    padding: 0 12px;
    display: flex;
    justify-content: space-between;
    font-size: 11px;
    color: var(--text-muted);
    opacity: 0.7;
    z-index: 1;
  }
}
</style>