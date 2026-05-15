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
            :improve-loading="improveLoading"
            :prompt="currentPrompt"
            @create="handleCreatePrompt"
            @history="handleHistoryPrompt"
            @improve="handleImprovePrompt"
            @select="handleSelectPrompt"
            @update:promptContent="handlePromptContentUpdate"
        />
      </div>
      <div class="cell-tools-rag">
        <ToolCard
            :tools="tools"
            @add="handleToolAdd"
            @remove="handleToolRemove"
        />
        <McpCard
            :mcps="mcps"
            @add="handleMcpAdd"
            @remove="handleMcpRemove"
        />
        <RagCard
            :knowledge-keys="knowledgeKeys"
            @add="handleKbAdd"
            @remove="handleKbRemove"
        />
      </div>
      <div class="cell-multimodal">
        <ImageModelCard :current-image-instance="currentImageInstance"
                        @select:image-instance="handleSelectImageInstance"/>
      </div>
    </div>

    <PromptSelectDrawer
        v-model:open="promptDrawerOpen"
        @select="handlePromptSelect"
    />

    <PromptFormModal
        v-model:open="promptFormOpen"
        :confirm-loading="false"
        :initial="null"
        mode="create"
        @submit="handlePromptSubmit"
    />

    <PromptHistoryModal
        v-model:open="historyModalOpen"
        :env-code="currentPrompt?.envCode"
        :prompt-key="currentPrompt?.promptKey"
    />

    <a-modal
        v-model:open="diffModalVisible"
        :footer="null"
        destroy-on-close
        title="提示词美化对比"
        width="800px"
    >
      <div class="diff-container">
        <div class="diff-header">
          <div class="diff-title original">原内容（已删除）</div>
          <div class="diff-title improved">美化后</div>
        </div>
        <div class="diff-content">
          <div class="diff-original">
            <pre class="diff-text original-text">{{ originalContent }}</pre>
          </div>
          <div class="diff-improved">
            <pre class="diff-text improved-text">{{ improvedContent }}</pre>
          </div>
        </div>
      </div>
      <div class="diff-footer">
        <a-space>
          <a-button @click="diffModalVisible = false">取消</a-button>
          <a-button type="primary" @click="handleApplyImproved">使用美化后内容</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {message} from 'ant-design-vue'
import PromptCard from './left-center/PromptCard.vue'
import ImageModelCard from './left-center/ImageModelCard.vue'
import ToolCard from './left-center/ToolCard.vue'
import McpCard from './left-center/McpCard.vue'
import RagCard from './left-center/RagCard.vue'
import PromptSelectDrawer from '../../ai-prompt/PromptSelectDrawer.vue'
import PromptFormModal from '../../ai-prompt/PromptFormModal.vue'
import PromptHistoryModal from '../../ai-prompt/PromptHistoryModal.vue'
import {type AiPrompt, aiPromptApi} from '@/api/aiPrompt'
import type {AiInstance} from '@/api/aiInstance'
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'

defineProps<{
  tools: AiTool[]
  mcps: AiMcp[]
  knowledgeKeys: string[]
}>()

const emit = defineEmits<{
  (e: 'update:prompt', value: AiPrompt | undefined): void
  (e: 'update:imageInstance', value: AiInstance | undefined): void
  (e: 'add:tool', tool: AiTool): void
  (e: 'remove:tool', toolKey: string): void
  (e: 'add:mcp', mcp: AiMcp): void
  (e: 'remove:mcp', mcpKey: string): void
  (e: 'add:kb', kbKey: string, title?: string): void
  (e: 'remove:kb', kbKey: string): void
}>()

const promptDrawerOpen = ref(false)
const promptFormOpen = ref(false)
const historyModalOpen = ref(false)
const improveLoading = ref(false)
const diffModalVisible = ref(false)
const originalContent = ref('')
const improvedContent = ref('')
const currentPrompt = ref<AiPrompt | undefined>(undefined)
const currentImageInstance = ref<AiInstance | undefined>(undefined)

function setPrompt(prompt: AiPrompt | undefined) {
  currentPrompt.value = prompt
}

function setImageInstance(instance: AiInstance | undefined) {
  currentImageInstance.value = instance
}

defineExpose({setPrompt, setImageInstance})

const handleSelectPrompt = () => {
  promptDrawerOpen.value = true
}

const handleCreatePrompt = () => {
  promptFormOpen.value = true
}

const handleHistoryPrompt = () => {
  if (!currentPrompt.value?.promptKey) {
    message.warning('请先选择一个提示词')
    return
  }
  historyModalOpen.value = true
}

const handleImprovePrompt = async () => {
  const content = currentPrompt.value?.promptContent
  if (!content?.trim()) {
    message.warning('请先输入提示词内容')
    return
  }
  originalContent.value = content
  improveLoading.value = true
  try {
    const improved = await aiPromptApi.beautify(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch {
    message.error('美化失败，请重试')
  } finally {
    improveLoading.value = false
  }
}

const handleApplyImproved = () => {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = improvedContent.value
    emit('update:prompt', currentPrompt.value)
  }
  diffModalVisible.value = false
  message.success('已应用美化后的提示词')
}

const handlePromptSelect = (prompt: AiPrompt) => {
  currentPrompt.value = prompt
  promptDrawerOpen.value = false
  emit('update:prompt', prompt)
}

const handlePromptContentUpdate = (content: string) => {
  if (currentPrompt.value) {
    currentPrompt.value.promptContent = content
  }
}

const handlePromptSubmit = async (form: AiPrompt) => {
  promptFormOpen.value = false
  currentPrompt.value = form
  emit('update:prompt', form)
}

const handleSelectImageInstance = (instance: AiInstance) => {
  currentImageInstance.value = instance
  emit('update:imageInstance', instance)
}

const handleToolAdd = (tool: AiTool) => {
  emit('add:tool', tool)
}

const handleToolRemove = (toolKey: string) => {
  emit('remove:tool', toolKey)
}

const handleMcpAdd = (mcp: AiMcp) => {
  emit('add:mcp', mcp)
}

const handleMcpRemove = (mcpKey: string) => {
  emit('remove:mcp', mcpKey)
}

const handleKbAdd = (kbKey: string, title?: string) => {
  emit('add:kb', kbKey, title)
}

const handleKbRemove = (kbKey: string) => {
  emit('remove:kb', kbKey)
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

.diff-container {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.diff-header {
  display: flex;
  background: #f5f5f5;
  border-bottom: 1px solid #e8e8e8;
}

.diff-title {
  flex: 1;
  padding: 12px 16px;
  font-weight: 600;
  font-size: 14px;
}

.diff-title.original {
  background: #fff1f0;
  color: #ff4d4f;
  border-right: 1px solid #e8e8e8;
}

.diff-title.improved {
  background: #f6ffed;
  color: #52c41a;
}

.diff-content {
  display: flex;
  min-height: 300px;
  max-height: 500px;
}

.diff-original,
.diff-improved {
  flex: 1;
  padding: 16px;
  overflow: auto;
  background: #fff;
}

.diff-original {
  background: #fffafafa;
  border-right: 1px solid #e8e8e8;
}

.diff-text {
  margin: 0;
  font-family: 'Fira Code', ui-monospace, monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

.original-text {
  color: #ff4d4f;
  text-decoration: line-through;
  opacity: 0.8;
}

.improved-text {
  color: #52c41a;
}

.diff-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}
</style>
