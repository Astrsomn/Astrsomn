<template>
  <div class="left-center custom-scrollbar">
    <div class="section-header">
      <div class="section-title">
        <span class="title-indicator"></span>
        <h2 class="title-text">功能增强模组</h2>
      </div>
    </div>

    <div class="card-grid">
      <div class="cell-prompt">
        <PromptCard
          :prompt="currentPrompt"
          @select="handleSelectPrompt"
          @create="handleCreatePrompt"
          @update:promptContent="handlePromptContentUpdate"
        />
      </div>
      <div class="cell-tools-rag">
        <ToolCard />
        <RagCard />
      </div>
      <div class="cell-multimodal">
        <ImageModelCard :current-image-instance="currentImageInstance" @select:image-instance="handleSelectImageInstance" />
      </div>
    </div>

    <PromptSelectDrawer
      v-model:open="promptDrawerOpen"
      @select="handlePromptSelect"
    />

    <PromptFormModal
      v-model:open="promptFormOpen"
      mode="create"
      :confirm-loading="false"
      :initial="null"
      @submit="handlePromptSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PromptCard from './left-center/PromptCard.vue'
import ImageModelCard from './left-center/ImageModelCard.vue'
import ToolCard from './left-center/ToolCard.vue'
import RagCard from './left-center/RagCard.vue'
import PromptSelectDrawer from '../../ai-prompt/PromptSelectDrawer.vue'
import PromptFormModal from '../../ai-prompt/PromptFormModal.vue'
import type { AiPrompt } from '@/api/aiPrompt'
import type { AiInstance } from '@/api/aiInstance'

const promptDrawerOpen = ref(false)
const promptFormOpen = ref(false)
const currentPrompt = ref<AiPrompt | undefined>(undefined)
const currentImageInstance = ref<AiInstance | undefined>(undefined)

const handleSelectPrompt = () => {
  promptDrawerOpen.value = true
}

const handleCreatePrompt = () => {
  promptFormOpen.value = true
}

const handlePromptSelect = (prompt: AiPrompt) => {
  currentPrompt.value = prompt
  promptDrawerOpen.value = false
}

const handlePromptContentUpdate = (content: string) => {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = content
  }
}

const handlePromptSubmit = async (form: AiPrompt) => {
  promptFormOpen.value = false
  currentPrompt.value = form
}

const handleSelectImageInstance = (instance: AiInstance) => {
  currentImageInstance.value = instance
}
</script>

<style scoped>
.left-center {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 0 8px 8px 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.title-indicator {
  width: 6px;
  height: 16px;
  background: #d1d5db;
  border-radius: 4px;
}

.title-text {
  font-weight: 700;
  font-size: 14px;
  color: #1e293b;
  margin: 0;
}

.card-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 16px;
  align-items: stretch;
  padding-bottom: 24px;
}

.cell-prompt {
  grid-column: 1;
  grid-row: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.cell-tools-rag {
  grid-column: 2;
  grid-row: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-self: stretch;
}

.cell-multimodal {
  grid-column: 1 / -1;
  grid-row: 2;
  min-width: 0;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: 10px;
}
</style>
