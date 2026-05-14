<template>
  <AgentConfigSectionShell
      :step="3"
      hint="必选对话模型；图像与语音为可选。默认使用系统自动实例与参数，展开「实例与参数」可手动选择实例并微调。"
      title="模型与推理"
  >
    <div class="inference-panel">
      <div class="tracks-grid">
        <div class="track-cell">
          <div class="track-head">
            <span class="track-title">对话模型</span>
            <a-button v-if="chatModel || currentChatInstance" size="small" type="link" @click="emit('clear-chat')">
              清除
            </a-button>
          </div>
          <div class="model-line">
            <RobotOutlined class="model-inline-icon"/>
            <span class="model-line-text">{{ chatModel?.modelName || chatModel?.modelKey || '未选择' }}</span>
          </div>
          <div class="account-line">
            <CreditCardOutlined class="account-icon"/>
            <span>{{
                currentChatInstance ? (chatAccount?.accountName || chatAccount?.accountKey || '—') : '保存时可自动绑定实例'
              }}</span>
          </div>
          <a-button block class="pick-btn" ghost type="primary" @click="emit('open-model', 'chat')">
            {{ chatModel?.modelName ? '更换对话模型' : '选择对话模型' }}
          </a-button>
        </div>

        <div class="track-cell">
          <div class="track-head">
            <span class="track-title">图像模型</span>
            <a-button v-if="imageModel || currentImageInstance" size="small" type="link" @click="emit('clear-image')">
              清除
            </a-button>
          </div>
          <div class="model-line">
            <RobotOutlined class="model-inline-icon"/>
            <span class="model-line-text">{{ imageModel?.modelName || imageModel?.modelKey || '未选择' }}</span>
          </div>
          <div class="account-line">
            <CreditCardOutlined class="account-icon"/>
            <span>{{
                currentImageInstance ? (imageAccount?.accountName || imageAccount?.accountKey || '—') : '可选'
              }}</span>
          </div>
          <a-button block class="pick-btn pick-btn--muted" type="default" @click="emit('open-model', 'image')">
            {{ imageModel?.modelName ? '更换图像模型' : '选择图像模型' }}
          </a-button>
        </div>

        <div class="track-cell">
          <div class="track-head">
            <span class="track-title">语音模型</span>
            <a-button v-if="voiceModel || currentVoiceInstance" size="small" type="link" @click="emit('clear-voice')">
              清除
            </a-button>
          </div>
          <div class="model-line">
            <AudioOutlined class="model-inline-icon model-inline-icon--voice"/>
            <span class="model-line-text">{{ voiceModel?.modelName || voiceModel?.modelKey || '未选择' }}</span>
          </div>
          <div class="account-line">
            <CreditCardOutlined class="account-icon"/>
            <span>{{
                currentVoiceInstance ? (voiceAccount?.accountName || voiceAccount?.accountKey || '—') : '可选'
              }}</span>
          </div>
          <a-button block class="pick-btn pick-btn--muted" type="default" @click="emit('open-model', 'voice')">
            {{ voiceModel?.modelName ? '更换语音模型' : '选择语音模型' }}
          </a-button>
        </div>
      </div>

      <div class="advanced-bar">
        <a-button class="adv-toggle" type="text" @click="showAdvanced = !showAdvanced">
          <SettingOutlined/>
          <span>{{ showAdvanced ? '收起实例与参数' : '实例与参数' }}</span>
        </a-button>
      </div>

      <div v-show="showAdvanced" class="advanced-block">
        <div class="instances-strip">
          <div class="instance-chip">
            <span class="instance-chip-label">对话实例</span>
            <a-button block class="instance-pick" type="default" @click="emit('open-instance', 'chat')">
              {{ currentChatInstance?.instanceName || currentChatInstance?.instanceKey || '选择已有实例（可选）' }}
            </a-button>
          </div>
          <div class="instance-chip">
            <span class="instance-chip-label">图像实例</span>
            <a-button block class="instance-pick" type="default" @click="emit('open-instance', 'image')">
              {{ currentImageInstance?.instanceName || currentImageInstance?.instanceKey || '选择已有实例（可选）' }}
            </a-button>
          </div>
          <div class="instance-chip">
            <span class="instance-chip-label">语音实例</span>
            <a-button block class="instance-pick" type="default" @click="emit('open-instance', 'voice')">
              {{ currentVoiceInstance?.instanceName || currentVoiceInstance?.instanceKey || '选择已有实例（可选）' }}
            </a-button>
          </div>
        </div>

        <a-segmented
            :options="[
            { label: '对话参数', value: 'chat' },
            { label: '图像参数', value: 'image' },
            { label: '语音参数', value: 'voice' },
          ]"
            :value="paramTab"
            block
            class="param-segmented"
            size="small"
            @update:value="onParamTabChange"
        />

        <div class="param-shared">
          <Center :key="paramTab" v-bind="centerBind"/>
        </div>
      </div>
    </div>
  </AgentConfigSectionShell>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue'
import {AudioOutlined, CreditCardOutlined, RobotOutlined, SettingOutlined,} from '@ant-design/icons-vue'
import type {AiModel} from '@/api/aiModel'
import type {AiInstance} from '@/api/aiInstance'
import type {AiAccount} from '@/api/aiAccount'
import Center from '@/views/admin/ai-config/ai-instance/instance-form/Center.vue'
import type {TempInfo} from '@/views/admin/ai-config/ai-instance/useInstanceParamVisibility'
import AgentConfigSectionShell from './AgentConfigSectionShell.vue'

const props = defineProps<{
  chatModel: AiModel | undefined
  imageModel: AiModel | undefined
  voiceModel: AiModel | undefined
  currentChatInstance: AiInstance | undefined
  currentImageInstance: AiInstance | undefined
  currentVoiceInstance: AiInstance | undefined
  chatAccount: AiAccount | undefined
  imageAccount: AiAccount | undefined
  voiceAccount: AiAccount | undefined
  chatParamForm: AiInstance
  imageParamForm: AiInstance
  voiceParamForm: AiInstance
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
  imageParamSectionTitle: string
  imageCapabilityHint: string
  imageHasParamSchema: boolean
  imageUnsupportedParamCodes: string[]
  imageModelKind: 'chat' | 'embedding' | 'image'
  imageShowChatTemperature: boolean
  imageShowChatMaxTokens: boolean
  imageShowChatTopP: boolean
  imageShowChatTopK: boolean
  imageShowChatSeed: boolean
  imageShowChatStopSequences: boolean
  imageShowChatPenalties: boolean
  imageShowChatFrequencyPenalty: boolean
  imageShowChatPresencePenalty: boolean
  imageShowEmbeddingDimensions: boolean
  imageShowImageSize: boolean
  imageShowImageStyle: boolean
  imageEmbeddingHasAnyControl: boolean
  imageImageHasAnyControl: boolean
  voiceParamSectionTitle: string
  voiceCapabilityHint: string
  voiceHasParamSchema: boolean
  voiceUnsupportedParamCodes: string[]
  voiceModelKind: 'chat' | 'embedding' | 'image'
  voiceShowChatTemperature: boolean
  voiceShowChatMaxTokens: boolean
  voiceShowChatTopP: boolean
  voiceShowChatTopK: boolean
  voiceShowChatSeed: boolean
  voiceShowChatStopSequences: boolean
  voiceShowChatPenalties: boolean
  voiceShowChatFrequencyPenalty: boolean
  voiceShowChatPresencePenalty: boolean
  voiceShowEmbeddingDimensions: boolean
  voiceShowImageSize: boolean
  voiceShowImageStyle: boolean
  voiceEmbeddingHasAnyControl: boolean
  voiceImageHasAnyControl: boolean
  getTempInfo: (v: number) => TempInfo
}>()

const emit = defineEmits<{
  'clear-chat': []
  'clear-image': []
  'clear-voice': []
  'open-model': [kind: 'chat' | 'image' | 'voice']
  'open-instance': [kind: 'chat' | 'image' | 'voice']
}>()

const showAdvanced = ref(false)
const paramTab = ref<'chat' | 'image' | 'voice'>('chat')

function onParamTabChange(v: string) {
  if (v === 'chat' || v === 'image' || v === 'voice') {
    paramTab.value = v
  }
}

const centerBind = computed(() => {
  if (paramTab.value === 'chat') {
    return {
      form: props.chatParamForm,
      paramSectionTitle: props.chatParamSectionTitle,
      capabilityHint: props.chatCapabilityHint,
      hasParamSchema: props.chatHasParamSchema,
      unsupportedParamCodes: props.chatUnsupportedParamCodes,
      modelKind: props.chatModelKind,
      showChatTemperature: props.chatShowChatTemperature,
      showChatMaxTokens: props.chatShowChatMaxTokens,
      showChatTopP: props.chatShowChatTopP,
      showChatTopK: props.chatShowChatTopK,
      showChatSeed: props.chatShowChatSeed,
      showChatStopSequences: props.chatShowChatStopSequences,
      showChatPenalties: props.chatShowChatPenalties,
      showChatFrequencyPenalty: props.chatShowChatFrequencyPenalty,
      showChatPresencePenalty: props.chatShowChatPresencePenalty,
      showEmbeddingDimensions: props.chatShowEmbeddingDimensions,
      showImageSize: props.chatShowImageSize,
      showImageStyle: props.chatShowImageStyle,
      embeddingHasAnyControl: props.chatEmbeddingHasAnyControl,
      imageHasAnyControl: props.chatImageHasAnyControl,
      getTempInfo: props.getTempInfo,
    }
  }
  if (paramTab.value === 'image') {
    return {
      form: props.imageParamForm,
      paramSectionTitle: props.imageParamSectionTitle,
      capabilityHint: props.imageCapabilityHint,
      hasParamSchema: props.imageHasParamSchema,
      unsupportedParamCodes: props.imageUnsupportedParamCodes,
      modelKind: props.imageModelKind,
      showChatTemperature: props.imageShowChatTemperature,
      showChatMaxTokens: props.imageShowChatMaxTokens,
      showChatTopP: props.imageShowChatTopP,
      showChatTopK: props.imageShowChatTopK,
      showChatSeed: props.imageShowChatSeed,
      showChatStopSequences: props.imageShowChatStopSequences,
      showChatPenalties: props.imageShowChatPenalties,
      showChatFrequencyPenalty: props.imageShowChatFrequencyPenalty,
      showChatPresencePenalty: props.imageShowChatPresencePenalty,
      showEmbeddingDimensions: props.imageShowEmbeddingDimensions,
      showImageSize: props.imageShowImageSize,
      showImageStyle: props.imageShowImageStyle,
      embeddingHasAnyControl: props.imageEmbeddingHasAnyControl,
      imageHasAnyControl: props.imageImageHasAnyControl,
      getTempInfo: props.getTempInfo,
    }
  }
  return {
    form: props.voiceParamForm,
    paramSectionTitle: props.voiceParamSectionTitle,
    capabilityHint: props.voiceCapabilityHint,
    hasParamSchema: props.voiceHasParamSchema,
    unsupportedParamCodes: props.voiceUnsupportedParamCodes,
    modelKind: props.voiceModelKind,
    showChatTemperature: props.voiceShowChatTemperature,
    showChatMaxTokens: props.voiceShowChatMaxTokens,
    showChatTopP: props.voiceShowChatTopP,
    showChatTopK: props.voiceShowChatTopK,
    showChatSeed: props.voiceShowChatSeed,
    showChatStopSequences: props.voiceShowChatStopSequences,
    showChatPenalties: props.voiceShowChatPenalties,
    showChatFrequencyPenalty: props.voiceShowChatFrequencyPenalty,
    showChatPresencePenalty: props.voiceShowChatPresencePenalty,
    showEmbeddingDimensions: props.voiceShowEmbeddingDimensions,
    showImageSize: props.voiceShowImageSize,
    showImageStyle: props.voiceShowImageStyle,
    embeddingHasAnyControl: props.voiceEmbeddingHasAnyControl,
    imageHasAnyControl: props.voiceImageHasAnyControl,
    getTempInfo: props.getTempInfo,
  }
})
</script>

<style scoped>
.inference-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 14px 16px;
  box-shadow: none;
}

.tracks-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

@media (max-width: 960px) {
  .tracks-grid {
    grid-template-columns: 1fr;
  }
}

.track-cell {
  border: 1px dashed var(--border-subtle);
  border-radius: 12px;
  padding: 12px 14px;
  background: transparent;
  min-width: 0;
}

.track-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.track-title {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.model-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.model-inline-icon {
  font-size: 15px;
  color: #a78bfa;
  flex-shrink: 0;
}

.model-inline-icon--voice {
  color: #06b6d4;
}

.model-line-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-line {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--text-secondary);
  margin-bottom: 10px;
  min-height: 1.4em;
}

.account-line span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-icon {
  font-size: 12px;
  color: var(--primary);
  flex-shrink: 0;
}

.pick-btn {
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  text-align: left;
  height: auto;
  min-height: 34px;
}

.pick-btn--muted {
  border-color: var(--border-subtle);
}

.advanced-bar {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--border-subtle);
}

.adv-toggle {
  padding: 0;
  height: auto;
  color: var(--text-secondary);
  font-size: 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.adv-toggle:hover {
  color: var(--primary);
}

.advanced-block {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.instances-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

@media (max-width: 960px) {
  .instances-strip {
    grid-template-columns: 1fr;
  }
}

.instance-chip-label {
  display: block;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  margin-bottom: 6px;
}

.instance-pick {
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  text-align: left;
  height: auto;
  min-height: 34px;
  border-color: var(--border-subtle);
}

.param-segmented {
  width: 100%;
}

.param-segmented :deep(.ant-segmented) {
  background: var(--bg-surface, rgba(0, 0, 0, 0.04));
}

.param-shared {
  border: 1px dashed var(--border-subtle);
  border-radius: 12px;
  padding: 12px;
  min-height: 200px;
  background: transparent;
}

.param-shared :deep(.params-pane) {
  min-height: 180px;
}

.param-shared :deep(.pane-card) {
  max-height: 520px;
  box-shadow: none;
  border-color: var(--border-subtle);
}
</style>
