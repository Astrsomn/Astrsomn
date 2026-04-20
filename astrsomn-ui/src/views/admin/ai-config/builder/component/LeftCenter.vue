<template>
  <div class="left-center custom-scrollbar">
    <div class="section-header">
      <div class="section-title">
        <span class="title-indicator"></span>
        <h2 class="title-text">功能增强模组</h2>
      </div>
    </div>

    <div class="card-grid">
      <div class="card-column">
        <PromptCard
          :prompt="currentPrompt"
          @select="handleSelectPrompt"
          @create="handleCreatePrompt"
          @update:promptContent="handlePromptContentUpdate"
        />
        <ImageModelCard />
      </div>
      <div class="card-column">
        <ToolCard />
        <RagCard />
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

const promptDrawerOpen = ref(false)
const promptFormOpen = ref(false)
const currentPrompt = ref<AiPrompt | undefined>(undefined)

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
</script>

<style scoped>
.left-center {
  flex: 1;
  overflow-y: auto;
  padding: 16px 32px 32px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.title-indicator {
  width: 6px;
  height: 16px;
  background: var(--border-default);
  border-radius: 3px;
}

.title-text {
  font-weight: bold;
  font-size: 14px;
  color: var(--text-heading);
  margin: 0;
}

.card-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.card-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
