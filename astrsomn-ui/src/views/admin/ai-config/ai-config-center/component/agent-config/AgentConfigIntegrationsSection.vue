<template>
  <AgentConfigSectionShell :step="3" title="功能组件">
    <div class="integrations-layout">

      <div class="subsection">
        <h3 class="subsection-title">
          <span class="title-line"></span>
          交互能力 (Multimodal)
          <span class="title-line"></span>
        </h3>
        <div class="multimodal-grid">
          <div
              :class="{ 'is-active': !!imageModel }"
              class="modal-card"
              @click="emit('open-model', 'image')"
          >
            <div class="modal-card-left">
              <div class="modal-icon modal-icon--image">
                <PictureOutlined/>
              </div>
              <div class="modal-info">
                <p class="modal-name">图像理解与生成</p>
                <p class="modal-desc">{{ imageModel?.modelName || '支持读取图片或绘制图表' }}</p>
              </div>
            </div>
            <div class="modal-card-right">
              <a-button
                  v-if="imageModel || currentImageInstance"
                  class="clear-btn"
                  danger
                  size="small"
                  type="link"
                  @click.stop="emit('clear-image')"
              >
                清除
              </a-button>
              <PlusOutlined v-if="!imageModel" class="modal-add"/>
              <RightOutlined v-else class="modal-add modal-add--active"/>
            </div>
          </div>

          <div
              :class="{ 'is-active': !!voiceModel }"
              class="modal-card"
              @click="emit('open-model', 'voice')"
          >
            <div class="modal-card-left">
              <div class="modal-icon modal-icon--voice">
                <AudioOutlined/>
              </div>
              <div class="modal-info">
                <p class="modal-name">语音对话能力</p>
                <p class="modal-desc">{{ voiceModel?.modelName || '开启实时语音通话支持' }}</p>
              </div>
            </div>
            <div class="modal-card-right">
              <a-button
                  v-if="voiceModel || currentVoiceInstance"
                  class="clear-btn"
                  danger
                  size="small"
                  type="link"
                  @click.stop="emit('clear-voice')"
              >
                清除
              </a-button>
              <PlusOutlined v-if="!voiceModel" class="modal-add"/>
              <RightOutlined v-else class="modal-add modal-add--active"/>
            </div>
          </div>
        </div>
      </div>

      <div class="subsection">
        <h3 class="subsection-title">
          <span class="title-line"></span>
          外部集成 (Integrations)
          <span class="title-line"></span>
        </h3>
        <div class="integrations-grid builder-tool-mcp-skin">
          <ToolCard :tools="tools" @add="emit('tool-add', $event)" @remove="emit('tool-remove', $event)"/>
          <McpCard :mcps="mcps" @add="emit('mcp-add', $event)" @remove="emit('mcp-remove', $event)"/>
          <RagCard
              :knowledge-keys="knowledgeKeys"
              @add="emit('knowledge-add', $event)"
              @remove="emit('knowledge-remove', $event)"
          />
        </div>
      </div>

    </div>
  </AgentConfigSectionShell>
</template>

<script lang="ts" setup>
import {AudioOutlined, PictureOutlined, PlusOutlined, RightOutlined} from '@ant-design/icons-vue'
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'
import type {AiModel} from '@/api/aiModel'
import type {AiInstance} from '@/api/aiInstance'
import ToolCard from '@/views/admin/ai-config/builder/component/left-center/ToolCard.vue'
import McpCard from '@/views/admin/ai-config/builder/component/left-center/McpCard.vue'
import RagCard from '@/views/admin/ai-config/builder/component/left-center/RagCard.vue'
import AgentConfigSectionShell from './AgentConfigSectionShell.vue'

defineProps<{
  imageModel: AiModel | undefined
  voiceModel: AiModel | undefined
  currentImageInstance: AiInstance | undefined
  currentVoiceInstance: AiInstance | undefined
  tools: AiTool[]
  mcps: AiMcp[]
  knowledgeKeys: string[]
}>()

const emit = defineEmits<{
  'open-model': [kind: 'chat' | 'image' | 'voice']
  'clear-image': []
  'clear-voice': []
  'tool-add': [tool: AiTool]
  'tool-remove': [toolKey: string]
  'mcp-add': [mcp: AiMcp]
  'mcp-remove': [mcpKey: string]
  'knowledge-add': [key: string]
  'knowledge-remove': [key: string]
}>()
</script>

<style scoped>
.integrations-layout {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.subsection {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.subsection-title {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-line {
  flex: 1;
  height: 1px;
  background: var(--border-subtle);
}

.multimodal-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

@media (max-width: 768px) {
  .multimodal-grid {
    grid-template-columns: 1fr;
  }
}

.modal-card {
  background: var(--bg-card);
  border: 2px dashed var(--border-default);
  border-radius: 14px;
  padding: 16px 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.modal-card:hover {
  border-color: var(--primary);
  background: var(--primary-hover);
}

.modal-card.is-active {
  border-style: solid;
  border-color: var(--primary);
  background: var(--primary-hover);
}

.modal-card-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.modal-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.modal-icon--image {
  background: rgba(59, 130, 246, 0.12);
  color: #3b82f6;
}

.modal-icon--voice {
  background: rgba(139, 92, 246, 0.12);
  color: #a78bfa;
}

.modal-info {
  flex: 1;
  min-width: 0;
}

.modal-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 3px;
}

.modal-desc {
  font-size: 11px;
  color: var(--text-muted);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.modal-card-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.modal-add {
  font-size: 14px;
  color: var(--text-muted);
  transition: color 0.2s;
}

.modal-add--active {
  color: var(--primary);
}

.modal-card:hover .modal-add {
  color: var(--primary);
}

.clear-btn {
  font-size: 11px;
  padding: 0;
  height: auto;
}

.integrations-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  min-height: 0;
}

@media (max-width: 960px) {
  .integrations-grid {
    grid-template-columns: 1fr;
  }
}

.integrations-grid > * {
  min-width: 0;
}

.integrations-grid :deep(.tool-card),
.integrations-grid :deep(.mcp-card),
.integrations-grid :deep(.rag-card) {
  box-shadow: none;
}

.integrations-grid :deep(.tool-card:hover),
.integrations-grid :deep(.mcp-card:hover),
.integrations-grid :deep(.rag-card:hover) {
  border-color: var(--border-subtle);
  box-shadow: none;
}

.builder-tool-mcp-skin {
  --ab-glass-bg: var(--bg-card);
  --ab-glass-border: var(--border-subtle);
  --ab-glass-shadow: none;
  --ab-glass-radius: var(--radius-lg);
  --ab-glass-haze: 8px;
  --ab-hover-line: var(--border-subtle);
  --ab-hover-shadow: none;
}
</style>
