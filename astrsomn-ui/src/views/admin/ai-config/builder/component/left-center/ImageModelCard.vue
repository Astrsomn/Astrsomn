<template>
  <div class="image-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <PictureOutlined />
        </div>
        <h3 class="card-title">多模态增强</h3>
      </div>
      <button class="add-btn" @click="handleAddImageInstance">
        <PlusCircleOutlined />
      </button>
    </div>
    <div class="model-grid">
      <div class="model-item" @click="handleSelectImageModel">
        <div class="model-info">
          <PictureOutlined class="model-icon" />
          <span class="model-name">{{ currentImageInstance ? currentImageInstance.instanceName : 'DALL-E 3' }}</span>
        </div>
        <RightOutlined class="model-arrow" />
      </div>
      <div class="model-item" @click="handleSelectAudioModel">
        <div class="model-info">
          <AudioOutlined class="model-icon audio" />
          <span class="model-name">TTS-1</span>
        </div>
        <RightOutlined class="model-arrow" />
      </div>
    </div>

    <InstanceSelectDrawer
      v-model:open="selectDrawerOpen"
      :disable-ttl-edit="true"
      @select="handleInstanceSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { PictureOutlined, PlusCircleOutlined, RightOutlined, AudioOutlined } from '@ant-design/icons-vue';
import InstanceSelectDrawer from '../../../ai-instance/InstanceSelectDrawer.vue'
import type { AiInstance } from '@/api/aiInstance'

const props = defineProps<{
  currentImageInstance?: AiInstance
}>()

// 调试日志
console.log('ImageModelCard props:', props)

// 监听 currentImageInstance 的变化
watch(() => props.currentImageInstance, (newValue) => {
  console.log('currentImageInstance changed:', newValue)
})

const selectDrawerOpen = ref(false)

const emit = defineEmits<{
  (e: 'select:image-instance', instance: AiInstance): void
}>()

const handleAddImageInstance = () => {
  selectDrawerOpen.value = true
}

const handleSelectImageModel = () => {
  selectDrawerOpen.value = true
}

const handleSelectAudioModel = () => {
  // 暂时不处理音频模型选择
  console.log('Select audio model')
}

const handleInstanceSelect = (instance: AiInstance) => {
  console.log('Received instance in ImageModelCard:', instance)
  // 确保选择的是图像类型的实例
  if (instance.modelType === 'image') {
    console.log('Selected image instance:', instance)
    // 将选择的实例信息传递给父组件
    emit('select:image-instance', instance)
  } else {
    console.log('Selected instance is not image type:', instance.modelType)
  }
  selectDrawerOpen.value = false
}
</script>

<style scoped>
.image-card {
  background: white;
  border: 1px dashed var(--border-default);
  border-radius: var(--radius-xl);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.image-card:hover {
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
  background: rgba(236, 72, 153, 0.1);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ec4899;
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
  color: #ec4899;
  padding: 4px;
  border-radius: var(--radius-md);
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: rgba(236, 72, 153, 0.1);
}

.add-btn .anticon {
  font-size: 16px;
}

.model-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.model-item {
  padding: 12px;
  background: var(--bg-elevated);
  border: 1px dashed var(--border-default);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s;
}

.model-item:hover {
  border-color: rgba(236, 72, 153, 0.5);
}

.model-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-icon {
  color: #ec4899;
  font-size: 14px;
}

.model-icon.audio {
  color: #f97316;
}

.model-name {
  font-size: 11px;
  font-weight: bold;
  color: var(--text-primary);
}

.model-arrow {
  font-size: 10px;
  color: var(--text-tertiary);
}
</style>