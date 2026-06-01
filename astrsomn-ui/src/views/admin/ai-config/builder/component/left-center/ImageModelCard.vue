<template>
  <div class="image-card">
    <div class="card-header">
      <div class="header-left">
        <div class="icon-badge">
          <PictureOutlined/>
        </div>
        <h3 class="card-title">{{ t.imageModelCard.title }}</h3>
      </div>
      <button class="add-btn" @click="handleAddImageInstance">
        <PlusCircleOutlined/>
      </button>
    </div>
    <div class="model-grid">
      <div class="model-item" @click="handleSelectImageModel">
        <div class="model-info">
          <PictureOutlined class="model-icon"/>
          <span class="model-name">{{
              currentImageInstance ? currentImageInstance.instanceName : t.imageModelCard.dalleGeneration
            }}</span>
        </div>
        <RightOutlined class="model-arrow"/>
      </div>
      <div class="model-item" @click="handleSelectAudioModel">
        <div class="model-info">
          <AudioOutlined class="model-icon audio"/>
          <span class="model-name">{{ t.imageModelCard.ttsSynthesis }}</span>
        </div>
        <RightOutlined class="model-arrow"/>
      </div>
    </div>

    <InstanceSelectorDrawer
        v-model:open="selectDrawerOpen"
        :disable-ttl-edit="true"
        default-model-type="image"
        @edit="handleInstanceEdit"
        @select="handleInstanceSelect"
    />

    <InstanceForm
        v-model:visible="instanceFormVisible"
        :record="editInstance"
        @success="handleInstanceFormSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {AudioOutlined, PictureOutlined, PlusCircleOutlined, RightOutlined} from '@ant-design/icons-vue'
import InstanceSelectorDrawer from '../../../ai-instance/selector/InstanceSelectorDrawer.vue'
import InstanceForm from '../../../ai-instance/InstanceForm.vue'
import type {AiInstance} from '@/api/aiInstance'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-builder')

defineProps<{
  currentImageInstance?: AiInstance
}>()

const selectDrawerOpen = ref(false)
const instanceFormVisible = ref(false)
const editInstance = ref<AiInstance | undefined>(undefined)

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

}

const handleInstanceSelect = (instance: AiInstance) => {
  if (instance.modelType === 'image') {
    emit('select:image-instance', instance)
  }
  selectDrawerOpen.value = false
}

const handleInstanceEdit = (instance: AiInstance) => {
  selectDrawerOpen.value = false
  editInstance.value = instance
  instanceFormVisible.value = true
}

const handleInstanceFormSuccess = () => {
  instanceFormVisible.value = false
}
</script>

<style scoped>
.image-card {
  background: var(--ab-glass-bg);
  backdrop-filter: blur(var(--ab-glass-haze, 10px));
  -webkit-backdrop-filter: blur(var(--ab-glass-haze, 10px));
  border: 1px solid var(--ab-glass-border);
  border-radius: var(--ab-glass-radius, 16px);
  box-shadow: var(--ab-glass-shadow);
  padding: 20px;
  transition: border-color 0.2s,
  box-shadow 0.2s;
}

.image-card:hover {
  border-color: var(--ab-hover-line);
  box-shadow: var(--ab-hover-shadow, 0 0 15px color-mix(in srgb, var(--primary) 15%, transparent));
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
  color: var(--text-primary);
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
  background: color-mix(in srgb, var(--bg-container) 50%, transparent);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: background 0.2s,
  border-color 0.2s;
}

.model-item:first-child:hover {
  background: #eff6ff;
}

.model-item:nth-child(2) {
  background: color-mix(in srgb, var(--bg-elevated) 50%, transparent);
  border-color: var(--border-default);
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
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-arrow {
  font-size: 10px;
  color: var(--text-hint);
  flex-shrink: 0;
}
</style>