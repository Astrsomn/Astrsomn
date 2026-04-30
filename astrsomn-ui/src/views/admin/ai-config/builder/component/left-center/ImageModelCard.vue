<template>
  <div class="image-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <PictureOutlined />
        </div>
        <h3 class="card-title">多模态增强能力</h3>
      </div>
      <button class="add-btn" @click="handleAddImageInstance">
        <PlusCircleOutlined />
      </button>
    </div>
    <div class="model-grid">
      <div class="model-item" @click="handleSelectImageModel">
        <div class="model-info">
          <PictureOutlined class="model-icon" />
          <span class="model-name">{{ currentImageInstance ? currentImageInstance.instanceName : 'DALL-E 3 图像生成' }}</span>
        </div>
        <RightOutlined class="model-arrow" />
      </div>
      <div class="model-item" @click="handleSelectAudioModel">
        <div class="model-info">
          <AudioOutlined class="model-icon audio" />
          <span class="model-name">OpenAI TTS 语音合成</span>
        </div>
        <RightOutlined class="model-arrow" />
      </div>
    </div>

    <InstanceSelector
      v-model:open="selectDrawerOpen"
      :disable-ttl-edit="true"
      @select="handleInstanceSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { PictureOutlined, PlusCircleOutlined, RightOutlined, AudioOutlined } from '@ant-design/icons-vue'
import InstanceSelector from '../../../ai-instance/selector/InstanceSelector.vue'
import type { AiInstance } from '@/api/aiInstance'

defineProps<{
  currentImageInstance?: AiInstance
}>()

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
  /* 音频模型选择待接 */
}

const handleInstanceSelect = (instance: AiInstance) => {
  if (instance.modelType === 'image') {
    emit('select:image-instance', instance)
  }
  selectDrawerOpen.value = false
}
</script>

<style scoped>
.image-card {
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

.image-card:hover {
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
  gap: 12px;
}

.icon-badge {
  width: 32px;
  height: 32px;
  background: #fce7f3;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #db2777;
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
  color: #f472b6;
  padding: 2px;
  border-radius: 6px;
  background: none;
  border: none;
  cursor: pointer;
  transition: transform 0.2s;
  line-height: 1;
}

.add-btn:hover {
  transform: scale(1.1);
  color: #ec4899;
}

.add-btn .anticon {
  font-size: 18px;
}

.model-grid {
  display: flex;
  gap: 16px;
}

.model-item {
  flex: 1;
  min-width: 0;
  padding: 8px 16px;
  background: rgba(239, 246, 255, 0.5);
  border: 1px solid #bfdbfe;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition:
    background 0.2s,
    border-color 0.2s;
}

.model-item:first-child:hover {
  background: #eff6ff;
}

.model-item:nth-child(2) {
  background: rgba(255, 247, 237, 0.5);
  border-color: #fed7aa;
}

.model-item:nth-child(2):hover {
  background: #fff7ed;
}

.model-info {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.model-icon {
  color: #60a5fa;
  font-size: 16px;
  flex-shrink: 0;
}

.model-item:nth-child(2) .model-icon.audio {
  color: #fb923c;
}

.model-name {
  font-size: 12px;
  font-weight: 700;
  color: #475569;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-arrow {
  font-size: 10px;
  color: #cbd5e1;
  flex-shrink: 0;
}
</style>