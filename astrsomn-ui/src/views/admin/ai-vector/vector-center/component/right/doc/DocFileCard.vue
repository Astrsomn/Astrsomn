<template>
  <a-card :body-style="{ padding: 0 }" :bordered="false" :class="[{ active, cut, selected }, `size-${size}`]" class="custom-file-card"
          @click="$emit('select', file, $event)" @contextmenu.prevent="$emit('contextmenu', $event, file)">
    <div class="square-container">
      <div class="inner-content">

        <div class="action-group">
          <div class="action-btn" title="编辑" @click.stop="$emit('edit', file)">
            <edit-outlined/>
          </div>
          <div v-if="canChunk" class="action-btn chunk" title="切片" @click.stop="$emit('chunk', file)">
            <block-outlined/>
          </div>
          <div v-if="canVectorize" class="action-btn vectorize" title="向量化" @click.stop="$emit('vectorize', file)">
            <experiment-outlined/>
          </div>
          <div v-if="canReChunk" class="action-btn re-chunk" title="重新切片" @click.stop="$emit('re-chunk', file)">
            <block-outlined/>
          </div>
          <div v-if="canReVectorize" class="action-btn re-vectorize" title="重新向量化" @click.stop="$emit('re-vectorize', file)">
            <sync-outlined/>
          </div>
          <div class="action-btn delete" title="删除" @click.stop="$emit('delete', file)">
            <delete-outlined/>
          </div>
        </div>

        <div v-if="vectorizing" class="vectorizing-overlay">
          <div class="progress-ring">
            <a-progress :percent="progress || 0" :size="54" :stroke-color="'#1677ff'" :trail-color="'#f0f0f0'" type="circle"/>
          </div>
          <span class="progress-msg">{{ progressMsg || '处理中...' }}</span>
        </div>

        <div class="main-body">
          <div :class="file.status === '已向量化' ? 'ready' : 'pending'" class="status-dot"></div>
          <div :class="getFileExtension(file.name)" class="icon-box">
            <component :is="getFileIcon(file.name)" class="file-icon-svg"/>
          </div>
          <h3 :title="file.name" class="file-name">{{ file.name }}</h3>
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
import {computed} from 'vue';
import {
  BlockOutlined,
  DeleteOutlined,
  EditOutlined,
  ExperimentOutlined,
  FileMarkdownOutlined,
  FilePdfOutlined,
  FileTextOutlined,
  SyncOutlined
} from '@ant-design/icons-vue';

const props = defineProps<{
  file: {
    id?: number | string;
    name: string;
    segments: number;
    size: string;
    status: string;
    uploadTime?: string;
  };
  active?: boolean;
  cut?: boolean;
  selected?: boolean;
  vectorizing?: boolean;
  progress?: number;
  progressMsg?: string;
  size?: 'small' | 'medium' | 'large';
}>();

defineEmits<{
  select: [file: any, e: MouseEvent]
  edit: [file: any]
  chunk: [file: any]
  vectorize: [file: any]
  're-chunk': [file: any]
  're-vectorize': [file: any]
  delete: [file: any]
  contextmenu: [e: MouseEvent, file: any]
}>()

const canChunk = computed(() => !props.vectorizing && (props.file.status === '待向量化' || props.file.status === '失败'))
const canVectorize = computed(() => !props.vectorizing && props.file.status === '已切片')
const canReChunk = computed(() => !props.vectorizing && (props.file.status === '已切片' || props.file.status === '已向量化' || props.file.status === '失败'))
const canReVectorize = computed(() => !props.vectorizing && (props.file.status === '已向量化' || props.file.status === '失败'))

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

    .action-group {
      opacity: 1;
      transform: translateY(0);
    }

    .icon-box {
      transform: scale(1.1);
    }
  }

  &.active {
    border-color: var(--primary) !important;
    box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  }

  &.selected {
    background: rgba(59, 130, 246, 0.08);
    border-color: var(--primary) !important;
  }

  &.cut {
    opacity: 0.45;
    filter: grayscale(0.6);
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

      &.ready {
        background: var(--success);
      }

      &.pending {
        background: #f59e0b;
      }
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

      &.pdf {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
      }

      &.md {
        background: rgba(59, 130, 246, 0.1);
        color: var(--primary);
      }
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

      &:hover {
        background: var(--primary);
        color: #fff;
      }

      &.delete:hover {
        background: var(--error);
      }

      &.chunk:hover {
        background: #722ed1;
      }

      &.re-chunk:hover {
        background: #722ed1;
      }

      &.re-vectorize:hover {
        background: #faad14;
      }
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

  .vectorizing-overlay {
    position: absolute;
    inset: 0;
    background: rgba(255, 255, 255, 0.88);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    z-index: 20;
    border-radius: var(--radius-lg);

    .progress-msg {
      font-size: 11px;
      color: var(--text-secondary);
      max-width: 80%;
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  // 小图标模式
  &.size-small {
    .square-container {
      padding-top: 80%;
    }
    .main-body {
      .icon-box {
        width: 36px;
        height: 36px;
        font-size: 20px;
        margin-bottom: 8px;
      }
      .file-name {
        font-size: 12px;
      }
    }
    .footer-overlay {
      font-size: 10px;
      bottom: 6px;
    }
    .status-dot {
      top: 8px;
      left: 8px;
      width: 6px;
      height: 6px;
    }
    .action-group {
      top: 6px;
      right: 6px;
      .action-btn {
        width: 22px;
        height: 22px;
      }
    }
  }

  // 大图标模式
  &.size-large {
    .square-container {
      padding-top: 100%;
    }
    .main-body {
      .icon-box {
        width: 72px;
        height: 72px;
        font-size: 40px;
        margin-bottom: 16px;
      }
      .file-name {
        font-size: 16px;
        max-width: 90%;
      }
    }
    .footer-overlay {
      font-size: 12px;
      bottom: 14px;
      padding: 0 16px;
    }
  }
}
</style>
