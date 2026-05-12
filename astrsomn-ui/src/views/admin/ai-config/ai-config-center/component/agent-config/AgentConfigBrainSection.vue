<template>
  <AgentConfigSectionShell :step="2" :title="'选择\u201C大脑\u201D'">
    <div class="brain-panel">
      <div class="brain-card" :class="{ 'has-model': !!chatModel }">
        <div class="brain-main">
          <div class="model-avatar">
            <span v-if="chatModel?.modelName" class="avatar-text">{{ avatarInitial }}</span>
            <RobotOutlined v-else class="avatar-icon" />
          </div>
          <div class="model-info">
            <h4 class="model-name">{{ chatModel?.modelName || chatModel?.modelKey || '未选择模型' }}</h4>
            <p class="model-desc">
              {{ chatModel ? '当前选中的对话推理模型' : '决定 Agent 的理解与推理上限' }}
            </p>
          </div>
        </div>
        <div class="brain-actions">
          <a-button
            class="brain-btn brain-btn--outline"
            :disabled="!chatModel && !currentChatInstance"
            @click="showAdvanced = !showAdvanced"
          >
            <SettingOutlined />
            <span>配置参数</span>
          </a-button>
          <a-button class="brain-btn brain-btn--primary" @click="emit('open-model', 'chat')">
            <SwapOutlined />
            <span>{{ chatModel ? '切换模型' : '选择模型' }}</span>
          </a-button>
        </div>
      </div>

      <div v-show="showAdvanced" class="advanced-panel">
        <div class="advanced-header">
          <label class="card-label">推理实例</label>
          <a-button type="link" size="small" class="instance-pick-btn" @click="emit('open-instance', 'chat')">
            {{ currentChatInstance?.instanceName || currentChatInstance?.instanceKey || '选择实例' }}
            <RightOutlined />
          </a-button>
        </div>

        <div class="params-wrap">
          <Center
            :form="chatParamForm"
            :param-section-title="chatParamSectionTitle"
            :capability-hint="chatCapabilityHint"
            :has-param-schema="chatHasParamSchema"
            :unsupported-param-codes="chatUnsupportedParamCodes"
            :model-kind="chatModelKind"
            :show-chat-temperature="chatShowChatTemperature"
            :show-chat-max-tokens="chatShowChatMaxTokens"
            :show-chat-top-p="chatShowChatTopP"
            :show-chat-top-k="chatShowChatTopK"
            :show-chat-seed="chatShowChatSeed"
            :show-chat-stop-sequences="chatShowChatStopSequences"
            :show-chat-penalties="chatShowChatPenalties"
            :show-chat-frequency-penalty="chatShowChatFrequencyPenalty"
            :show-chat-presence-penalty="chatShowChatPresencePenalty"
            :show-embedding-dimensions="chatShowEmbeddingDimensions"
            :show-image-size="chatShowImageSize"
            :show-image-style="chatShowImageStyle"
            :embedding-has-any-control="chatEmbeddingHasAnyControl"
            :image-has-any-control="chatImageHasAnyControl"
            :get-temp-info="getTempInfo"
          />
        </div>
      </div>
    </div>
  </AgentConfigSectionShell>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { RobotOutlined, SettingOutlined, SwapOutlined, RightOutlined } from '@ant-design/icons-vue'
import type { AiModel } from '@/api/aiModel'
import type { AiInstance } from '@/api/aiInstance'
import type { AiAccount } from '@/api/aiAccount'
import type { TempInfo } from '@/views/admin/ai-config/ai-instance/useInstanceParamVisibility'
import Center from '@/views/admin/ai-config/ai-instance/instance-form/Center.vue'
import AgentConfigSectionShell from './AgentConfigSectionShell.vue'

const props = defineProps<{
  chatModel: AiModel | undefined
  currentChatInstance: AiInstance | undefined
  chatAccount: AiAccount | undefined
  chatParamForm: AiInstance
  chatParamSectionTitle: string
  chatCapabilityHint: string
  chatHasParamSchema: boolean
  chatUnsupportedParamCodes: string[]
  chatModelKind: 'chat' | 'embedding' | 'image'
  chatShowChatTemperature: boolean
  chatShowChatMaxTokens: boolean
  chatShowChatTopP: boolean
  chatShowChatTopK: boolean
  chatShowChatSeed: boolean
  chatShowChatStopSequences: boolean
  chatShowChatPenalties: boolean
  chatShowChatFrequencyPenalty: boolean
  chatShowChatPresencePenalty: boolean
  chatShowEmbeddingDimensions: boolean
  chatShowImageSize: boolean
  chatShowImageStyle: boolean
  chatEmbeddingHasAnyControl: boolean
  chatImageHasAnyControl: boolean
  getTempInfo: (v: number) => TempInfo
}>()

const emit = defineEmits<{
  'open-model': [kind: 'chat' | 'image' | 'voice']
  'open-instance': [kind: 'chat' | 'image' | 'voice']
  'clear-chat': []
}>()

const showAdvanced = ref(false)

const avatarInitial = computed(() => {
  const name = props.chatModel?.modelName || props.chatModel?.modelKey || ''
  return name.slice(0, 2).toUpperCase()
})
</script>

<style scoped>
.brain-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.brain-card {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-left: 4px solid var(--primary);
  border-radius: var(--radius-lg);
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  transition: border-color 0.2s;
}

.brain-card.has-model {
  border-left-color: var(--primary);
}

.brain-main {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.model-avatar {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.avatar-icon {
  font-size: 22px;
  color: var(--text-muted);
}

.model-info {
  flex: 1;
  min-width: 0;
}

.model-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-desc {
  font-size: 11px;
  color: var(--text-muted);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.brain-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.brain-btn {
  font-size: 12px;
  font-weight: 600;
  border-radius: 8px;
  height: auto;
  padding: 7px 14px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.brain-btn--outline {
  border-color: var(--border-default);
  color: var(--text-secondary);
  background: transparent;
}

.brain-btn--outline:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-hover);
}

.brain-btn--outline:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.brain-btn--primary {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.brain-btn--primary:hover {
  background: var(--primary-light, var(--primary));
  opacity: 0.9;
}

.advanced-panel {
  background: var(--bg-card);
  border: 1px dashed var(--border-default);
  border-radius: var(--radius-lg);
  padding: 14px 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.advanced-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.instance-pick-btn {
  font-size: 12px;
  font-weight: 600;
  padding: 0;
  height: auto;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.params-wrap {
  border: 1px dashed var(--border-subtle);
  border-radius: 12px;
  padding: 12px;
  min-height: 160px;
  background: transparent;
}

.params-wrap :deep(.params-pane) {
  min-height: 140px;
}

.params-wrap :deep(.pane-card) {
  max-height: 480px;
  box-shadow: none;
  border-color: var(--border-subtle);
}

.card-label {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}
</style>
