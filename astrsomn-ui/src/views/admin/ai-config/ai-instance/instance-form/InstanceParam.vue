<template>
  <section class="params-pane">
    <div class="pane-card glass-card scroll-y">
      <div class="config-section">
        <div class="section-header-flex">
          <h3 class="section-title">
            <ControlOutlined/>
            {{ paramSectionTitle }}
          </h3>
          <a-tag v-if="form.modelKey" class="model-key-tag" color="blue">{{ form.modelKey }}</a-tag>
        </div>

        <div v-if="!form.modelKey" class="empty-state">
          <div class="empty-icon">
            <SelectOutlined/>
          </div>
          <p>{{ t.param.emptyHint }}</p>
        </div>

        <div v-else class="params-list">
          <p v-if="capabilityHint" class="cap-hint">{{ capabilityHint }}</p>
          <p v-if="hasParamSchema && unsupportedParamCodes.length > 0" class="cap-hint muted">
            {{ t.param.unsupportedHint.replace('{n}', String(unsupportedParamCodes.length)) }}：{{
              unsupportedParamCodes.join(', ')
            }}
          </p>

          <template v-if="modelKind === 'chat'">
            <div v-if="showChatTemperature" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left">
                  <template #title>{{ t.param.chat.temperature.tooltip }}</template>
                  <span class="p-label">{{ t.param.chat.temperature.label }} <QuestionCircleOutlined/></span></a-tooltip>
                <a-input-number v-model:value="form.temperature" :max="2" :min="0" :step="0.1" size="small"/>
              </div>
              <div class="slider-box">
                <a-slider v-model:value="form.temperature" :marks="{ 0: t.param.chat.temperature.marks.strict, 0.7: t.param.chat.temperature.marks.balanced, 1.5: t.param.chat.temperature.marks.creative, 2: t.param.chat.temperature.marks.random }" :max="2" :min="0"
                          :step="0.1"/>
              </div>
              <div :class="getTempInfo(form.temperature ?? 0.7).color" class="p-desc-bar">
                {{ getTempInfo(form.temperature ?? 0.7).text }}
              </div>
            </div>

            <div v-if="showChatMaxTokens" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left">
                  <template #title>{{ t.param.chat.maxTokens.tooltip }}</template>
                  <span class="p-label">{{ t.param.chat.maxTokens.label }} <QuestionCircleOutlined/></span></a-tooltip>
                <a-input-number v-model:value="form.maxTokens" :max="128000" :min="1" size="small"/>
              </div>
              <div class="slider-box">
                <a-slider v-model:value="form.maxTokens" :marks="{ 0: t.param.chat.maxTokens.marks.short, 2048: t.param.chat.maxTokens.marks.medium, 4096: t.param.chat.maxTokens.marks.long, 8192: t.param.chat.maxTokens.marks.extraLong }" :max="8192" :min="0"
                          :step="256"/>
              </div>
            </div>

            <div v-if="showChatTopP" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left" :title="t.param.chat.topP.tooltip"><span class="p-label">{{ t.param.chat.topP.label }} <QuestionCircleOutlined/></span>
                </a-tooltip>
                <a-input-number v-model:value="form.topP" :max="1" :min="0" :step="0.01" size="small"/>
              </div>
              <div class="slider-box">
                <a-slider v-model:value="form.topP" :marks="{ 0: t.param.chat.topP.marks.narrow, 0.5: t.param.chat.topP.marks.standard, 1: t.param.chat.topP.marks.full }" :max="1" :min="0"
                          :step="0.05"/>
              </div>
            </div>

            <div v-if="showChatTopK" class="param-group-card">
              <div class="p-header"><span class="p-label">{{ t.param.chat.topK.label }} <QuestionCircleOutlined/></span>
                <a-input-number v-model:value="form.topK" :max="100" :min="0" :step="1" size="small"/>
              </div>
              <p class="p-inline-hint">{{ t.param.chat.topK.hint }}</p>
            </div>
            <div v-if="showChatSeed" class="param-group-card">
              <div class="p-header"><span class="p-label">{{ t.param.chat.seed.label }} <QuestionCircleOutlined/></span>
                <a-input-number v-model:value="form.seed" :max="2147483647" :min="0" :step="1" size="small"/>
              </div>
            </div>
            <div v-if="showChatStopSequences" class="param-group-card">
              <div class="p-header"><span class="p-label">{{ t.param.chat.stopSequences.label }}</span></div>
              <a-textarea v-model:value="form.stopSequences" :rows="3" class="stop-seq-input"
                          :placeholder="t.param.chat.stopSequences.placeholder"/>
            </div>
            <div v-if="showChatPenalties" class="penalty-row">
              <div v-if="showChatFrequencyPenalty" class="mini-param-card"><span
                  class="mini-label">{{ t.param.chat.frequencyPenalty.label }}</span>
                <a-slider v-model:value="form.frequencyPenalty" :max="2" :min="-2" :step="0.1"/>
              </div>
              <div v-if="showChatPresencePenalty" class="mini-param-card"><span
                  class="mini-label">{{ t.param.chat.presencePenalty.label }}</span>
                <a-slider v-model:value="form.presencePenalty" :max="2" :min="-2" :step="0.1"/>
              </div>
            </div>
          </template>

          <template v-else-if="modelKind === 'embedding'">
            <div v-if="showEmbeddingDimensions" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left">
                  <template #title>{{ t.param.embedding.dimensions.tooltip }}</template>
                  <span class="p-label">{{ t.param.embedding.dimensions.label }} <QuestionCircleOutlined/></span></a-tooltip>
              </div>
              <a-select
                  v-model:value="form.dimensions"
                  :filter-option="filterDimensionOption"
                  :options="dimensionOptions"
                  allow-clear
                  :placeholder="t.param.embedding.dimensions.placeholder"
                  show-search
                  size="large"
                  style="width: 100%"
              />
            </div>
            <p v-if="!embeddingHasAnyControl" class="cap-hint muted">{{ t.param.embedding.noControlHint }}</p>
          </template>

          <template v-else-if="modelKind === 'image'">
            <div v-if="showImageSize" class="param-group-card">
              <div class="p-header"><span class="p-label">{{ t.param.image.size.label }}</span></div>
              <a-input v-model:value="form.size" allow-clear :placeholder="t.param.image.size.placeholder" size="large"/>
            </div>
            <div v-if="showImageStyle" class="param-group-card">
              <div class="p-header"><span class="p-label">{{ t.param.image.style.label }}</span></div>
              <a-input v-model:value="form.style" allow-clear :placeholder="t.param.image.style.placeholder" size="large"/>
            </div>
            <p v-if="!imageHasAnyControl" class="cap-hint muted">{{ t.param.image.noControlHint }}</p>
          </template>
        </div>
      </div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {ControlOutlined, QuestionCircleOutlined, SelectOutlined} from '@ant-design/icons-vue'
import {usePageTranslation} from '@/locales/pages.ts'

const t = usePageTranslation('ai-instance')

type InstanceFormModel = {
  modelKey?: string
  temperature?: number
  maxTokens?: number
  topP?: number
  topK?: number
  seed?: number
  stopSequences?: string
  frequencyPenalty?: number
  presencePenalty?: number
  dimensions?: number
  size?: string
  style?: string
}

type TempInfo = {
  text: string
  color: string
}

defineProps<{
  form: InstanceFormModel
  paramSectionTitle: string
  capabilityHint: string
  hasParamSchema: boolean
  unsupportedParamCodes: string[]
  modelKind: 'chat' | 'embedding' | 'image'
  showChatTemperature: boolean
  showChatMaxTokens: boolean
  showChatTopP: boolean
  showChatTopK: boolean
  showChatSeed: boolean
  showChatStopSequences: boolean
  showChatPenalties: boolean
  showChatFrequencyPenalty: boolean
  showChatPresencePenalty: boolean
  showEmbeddingDimensions: boolean
  showImageSize: boolean
  showImageStyle: boolean
  embeddingHasAnyControl: boolean
  imageHasAnyControl: boolean
  getTempInfo: (v: number) => TempInfo
}>()

const dimensionOptions = computed(() => [
  {value: 256, label: t.value.param.embedding.dimensions.options.d256},
  {value: 512, label: t.value.param.embedding.dimensions.options.d512},
  {value: 768, label: t.value.param.embedding.dimensions.options.d768},
  {value: 1024, label: t.value.param.embedding.dimensions.options.d1024},
  {value: 1536, label: t.value.param.embedding.dimensions.options.d1536},
  {value: 2048, label: t.value.param.embedding.dimensions.options.d2048},
  {value: 3072, label: t.value.param.embedding.dimensions.options.d3072},
  {value: 4096, label: t.value.param.embedding.dimensions.options.d4096},
  {value: 8192, label: t.value.param.embedding.dimensions.options.d8192}
])

function filterDimensionOption(input: string, option: { value: number; label: string }) {
  return String(option.value).includes(input) || option.label.toLowerCase().includes(input.toLowerCase())
}
</script>

<style scoped>
.params-pane {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pane-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 18px;
  overflow-y: auto;
}

.glass-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
}

.section-header-flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-key-tag {
  flex-shrink: 0;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cap-hint {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
  padding: 10px 12px;
  background: var(--bg-secondary);
  border-radius: 10px;
  border: 1px solid var(--border-default);
  margin-bottom: 16px;
}

.cap-hint.muted {
  color: var(--text-tertiary);
  background: var(--bg-secondary);
  border-style: dashed;
}

.param-group-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-default);
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 14px;
  transition: all 0.2s;
}

.p-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.p-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  cursor: help;
  display: flex;
  align-items: center;
  gap: 4px;
}

.slider-box {
  padding: 0 8px 16px 8px;
}

.p-desc-bar {
  margin-top: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  border-left: 4px solid transparent;
}

.penalty-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.mini-param-card {
  background: var(--bg-secondary);
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--border-default);
}

.mini-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-secondary);
  margin-bottom: 8px;
  display: block;
}

.empty-state {
  text-align: center;
  padding: 100px 0;
  color: var(--text-tertiary);
}

.empty-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  margin: 0 auto 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  color: var(--primary);
}
</style>
