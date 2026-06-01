<template>
  <div class="left-center custom-scrollbar">
    <div class="section-header">
      <div class="section-title">
        <span class="title-indicator"></span>
        <h2 class="title-text">{{ t.leftCenter.sectionTitle }}</h2>
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

    <PromptSelectorDrawer
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
        :title="t.leftCenter.diffModalTitle"
        width="800px"
    >
      <div class="diff-container">
        <div class="diff-header">
          <div class="diff-title original">{{ t.leftCenter.diffOriginalTitle }}</div>
          <div class="diff-title improved">{{ t.leftCenter.diffImprovedTitle }}</div>
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
          <a-button @click="diffModalVisible = false">{{ t.leftCenter.diffCancelButton }}</a-button>
          <a-button type="primary" @click="handleApplyImproved">{{ t.leftCenter.diffApplyButton }}</a-button>
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
import ToolCard from '../../ai-config-center/component/right/agent-config/ToolCard.vue'
import McpCard from '../../ai-config-center/component/right/agent-config/McpCard.vue'
import RagCard from '../../ai-config-center/component/right/agent-config/RagCard.vue'
import PromptSelectorDrawer from '../../ai-prompt/selector/PromptSelectorDrawer.vue'
import PromptFormModal from '../../ai-prompt/component/PromptFormModal.vue'
import PromptHistoryModal from '../../ai-prompt/component/PromptHistoryModal.vue'
import {type AiPrompt, aiPromptApi} from '@/api/aiPrompt'
import type {AiInstance} from '@/api/aiInstance'
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-builder')

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
    message.warning(t.value.leftCenter.selectPromptFirst)
    return
  }
  historyModalOpen.value = true
}

const handleImprovePrompt = async () => {
  const content = currentPrompt.value?.promptContent
  if (!content?.trim()) {
    message.warning(t.value.leftCenter.enterPromptContentFirst)
    return
  }
  originalContent.value = content
  improveLoading.value = true
  try {
    const improved = await aiPromptApi.beautify(content)
    improvedContent.value = improved
    diffModalVisible.value = true
  } catch {
    message.error(t.value.leftCenter.beautifyFailed)
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
  message.success(t.value.leftCenter.beautifyApplied)
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
  background: var(--border-strong);
  border-radius: 4px;
}

.title-text {
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
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
  border: 1px solid var(--border-default);
  border-radius: 8px;
  overflow: hidden;
}

.diff-header {
  display: flex;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border-default);
}

.diff-title {
  flex: 1;
  padding: 12px 16px;
  font-weight: 600;
  font-size: 14px;
}

.diff-title.original {
  background: color-mix(in srgb, var(--error) 6%, var(--bg-card));
  color: var(--error);
  border-right: 1px solid var(--border-default);
}

.diff-title.improved {
  background: color-mix(in srgb, var(--success) 6%, var(--bg-card));
  color: var(--success);
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
  background: var(--bg-card);
}

.diff-original {
  border-right: 1px solid var(--border-default);
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
  color: var(--error);
  text-decoration: line-through;
  opacity: 0.8;
}

.improved-text {
  color: var(--success);
}

.diff-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-default);
  text-align: right;
}
</style>
