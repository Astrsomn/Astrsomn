<template>
  <a-card :bordered="false" class="custom-file-card" :body-style="{ padding: 0 }">
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
}>();

defineEmits<{
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
@text-main: #262626;
@text-secondary: #8c8c8c;
@blue-base: #1677ff;

.custom-file-card {
  cursor: pointer;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.05) !important;
  transition: all 0.3s ease;
  overflow: hidden;

  &:hover {
    background: #fff;
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    
    .action-group { opacity: 1; transform: translateY(0); }
    .icon-box { transform: scale(1.1); }
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
      &.ready { background: #52c41a; }
      &.pending { background: #faad14; }
    }

    .icon-box {
      width: 54px;
      height: 54px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 12px;
      background: #f5f5f5;
      font-size: 30px; // 显式设置图标字体大小
      transition: transform 0.3s ease;
      
      // 这里的 .file-icon-svg 是针对 component 渲染出来的 svg 进行强制显示
      .file-icon-svg, span {
        display: inline-block !important;
        line-height: 1;
      }

      &.pdf { background: #fff1f0; color: #f5222d; }
      &.md { background: #e6f7ff; color: #096dd9; }
    }

    .file-name {
      font-size: 14px;
      font-weight: 500;
      color: @text-main;
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
      background: #eee;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      &:hover { background: @blue-base; color: #fff; }
      &.delete:hover { background: #ff4d4f; }
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
    color: @text-secondary;
    opacity: 0.6;
    z-index: 1;
  }
}
</style>