<template>
  <a-card :body-style="{ padding: 0 }" :bordered="false" :class="[{ selected }, `size-${size}`]" class="folder-card"
          @click="!editing && $emit('enter', folder, $event)" @contextmenu.prevent="$emit('contextmenu', $event, folder)">
    <div class="square-container">
      <div class="inner-content">

        <div class="action-group" v-if="!editing">
          <div class="action-btn" :title="t.vectorCenter.docFolderCard.rename" @click.stop="$emit('rename', folder)">
            <edit-outlined/>
          </div>
          <div class="action-btn delete" :title="t.vectorCenter.docFolderCard.delete" @click.stop="$emit('delete', folder)">
            <delete-outlined/>
          </div>
        </div>

        <div class="main-body">
          <div class="folder-icon-box">
            <folder-outlined class="folder-icon-svg"/>
          </div>
          <input
              v-if="editing"
              ref="editInputRef"
              :value="folder.folderName"
              :placeholder="placeholder || ''"
              class="folder-name-input"
              @blur="$emit('rename-cancel', folder)"
              @keydown.enter="onConfirmRename($event)"
              @keydown.escape="$emit('rename-cancel', folder)"
              @click.stop
          />
          <h3 v-else :title="folder.folderName" class="folder-name">{{ folder.folderName }}</h3>
        </div>

        <div class="footer-overlay">
          <span class="doc-count">{{ t.vectorCenter.docFolderCard.docCount.replace('{count}', String(folder.docCount ?? 0)) }}</span>
        </div>

      </div>
    </div>
  </a-card>
</template>

<script lang="ts" setup>
import {nextTick, ref, watch} from 'vue';
import {DeleteOutlined, EditOutlined, FolderOutlined} from '@ant-design/icons-vue';
import type {AiVecFolder} from '@/api/aiVecFolder';
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-vector')

const props = defineProps<{
  folder: AiVecFolder;
  size?: 'small' | 'medium' | 'large';
  editing?: boolean;
  selected?: boolean;
  placeholder?: string;
}>();

const emit = defineEmits<{
  enter: [folder: AiVecFolder, e: MouseEvent]
  rename: [folder: AiVecFolder]
  delete: [folder: AiVecFolder]
  contextmenu: [e: MouseEvent, folder: AiVecFolder]
  'rename-confirm': [folder: AiVecFolder, newName: string]
  'rename-cancel': [folder: AiVecFolder]
}>();

const editInputRef = ref<HTMLInputElement | null>(null);

watch(() => props.editing, (val) => {
  if (val) {
    nextTick(() => {
      if (editInputRef.value) {
        editInputRef.value.focus();
        editInputRef.value.select();
      }
    });
  }
});

const onConfirmRename = (e: KeyboardEvent) => {
  const input = e.target as HTMLInputElement;
  const newName = input.value.trim();
  if (newName && newName !== props.folder.folderName) {
    emit('rename-confirm', props.folder, newName);
  } else {
    emit('rename-cancel', props.folder);
  }
};
</script>

<style lang="less" scoped>
.folder-card {
  cursor: pointer;
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  border: 1px solid var(--border-default) !important;
  transition: all 0.3s ease;
  overflow: hidden;

  &:hover {
    background: var(--bg-elevated);
    border-color: var(--primary) !important;

    .action-group {
      opacity: 1;
      transform: translateY(0);
    }
  }

  &.selected {
    background: color-mix(in srgb, var(--primary) 8%, transparent);
    border-color: var(--primary) !important;
  }

  .square-container {
    position: relative;
    width: 100%;
    padding-top: 100%;

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

    .folder-icon-box {
      width: 54px;
      height: 54px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: var(--radius-lg);
      background: color-mix(in srgb, #fbbf24 15%, transparent);
      font-size: 30px;
      color: #fbbf24;
      transition: transform 0.3s ease;

      .folder-icon-svg, span {
        display: inline-block !important;
        line-height: 1;
      }
    }

    .folder-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-heading);
      margin: 0;
      max-width: 80%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .folder-name-input {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-heading);
      margin: 0;
      max-width: 80%;
      text-align: center;
      border: 1px solid var(--primary);
      border-radius: var(--radius-sm);
      padding: 2px 6px;
      outline: none;
      background: var(--bg-input);
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
    }
  }

  .footer-overlay {
    position: absolute;
    bottom: 10px;
    left: 0;
    width: 100%;
    padding: 0 12px;
    display: flex;
    justify-content: center;
    font-size: 11px;
    color: var(--text-muted);
    opacity: 0.7;
    z-index: 1;
  }


  &.size-small {
    .square-container {
      padding-top: 80%;
    }
    .main-body {
      .folder-icon-box {
        width: 36px;
        height: 36px;
        font-size: 20px;
        margin-bottom: 8px;
      }
      .folder-name {
        font-size: 12px;
      }
      .folder-name-input {
        font-size: 12px;
      }
    }
    .footer-overlay {
      font-size: 10px;
      bottom: 6px;
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

  &.size-large {
    .main-body {
      .folder-icon-box {
        width: 72px;
        height: 72px;
        font-size: 40px;
        margin-bottom: 16px;
      }
      .folder-name {
        font-size: 16px;
        max-width: 90%;
      }
      .folder-name-input {
        font-size: 16px;
        max-width: 90%;
      }
    }
    .footer-overlay {
      font-size: 12px;
      bottom: 14px;
    }
  }
}
</style>
