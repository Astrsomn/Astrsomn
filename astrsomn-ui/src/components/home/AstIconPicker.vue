<template>
  <div class="avatar-picker-wrapper">
    <div :style="{ width: size + 'px', height: size + 'px' }" class="avatar-trigger" @click="modalOpen = true">
      <div v-if="modelValue && modelValue.startsWith('data:image')" :style="{ width: size + 'px', height: size + 'px' }" class="avatar-img-wrap">
        <img :src="modelValue" alt="avatar" class="avatar-img"/>
      </div>
      <div v-else-if="modelValue && iconMap[modelValue]" :style="{ width: size + 'px', height: size + 'px', fontSize: size * 0.5 + 'px' }" class="avatar-icon-wrap">
        <component :is="iconMap[modelValue]"/>
      </div>
      <div v-else :style="{ width: size + 'px', height: size + 'px', fontSize: size * 0.45 + 'px' }" class="avatar-fallback">
        {{ fallbackChar }}
      </div>
      <div class="avatar-overlay">
        <EditOutlined/>
      </div>
    </div>

    <a-modal
        v-model:open="modalOpen"
        :footer="null"
        title="选择头像"
        width="520px"
        @cancel="onCancel"
    >
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="icon" tab="图标选择">
          <div class="icon-search">
            <a-input
                v-model:value="searchText"
                allow-clear
                placeholder="搜索图标..."
                size="small"
            >
              <template #prefix>
                <SearchOutlined/>
              </template>
            </a-input>
          </div>
          <div class="icon-grid">
            <div
                v-for="icon in filteredIcons"
                :key="icon"
                :class="{ 'icon-item-selected': modelValue === icon }"
                class="icon-item"
                @click="onSelectIcon(icon)"
            >
              <component :is="iconMap[icon]"/>
            </div>
            <div v-if="filteredIcons.length === 0" class="icon-empty">无匹配图标</div>
          </div>
        </a-tab-pane>

        <a-tab-pane key="upload" tab="图片上传">
          <div class="upload-area">
            <a-upload
                :before-upload="onBeforeUpload"
                :max-count="1"
                :show-upload-list="false"
                accept="image
.icon-search {
  margin-bottom: 12px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 6px;
  max-height: 320px;
  overflow-y: auto;
  padding: 2px;
}

.icon-item {
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 1;
  border-radius: 8px;
  cursor: pointer;
  font-size: 20px;
  color: var(--text-secondary);
  transition: all 0.15s;
  border: 2px solid transparent;
  background: var(--bg-input, #fafafa);
}

.icon-item:hover {
  color: var(--primary);
  background: rgba(59, 130, 246, 0.08);
  border-color: var(--primary);
}

.icon-item-selected {
  color: var(--primary);
  border-color: var(--primary);
  background: rgba(59, 130, 246, 0.12);
}

.icon-empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 32px 0;
  color: var(--text-muted);
  font-size: 13px;
}


.upload-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 280px;
  height: 160px;
  border: 2px dashed var(--border-default);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-muted);
}

.upload-trigger:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
}

.upload-hint {
  font-size: 12px;
  opacity: 0.7;
}

.upload-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.preview-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid var(--border-default);
}

.modal-footer-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-subtle, #f0f0f0);
}
</style>
