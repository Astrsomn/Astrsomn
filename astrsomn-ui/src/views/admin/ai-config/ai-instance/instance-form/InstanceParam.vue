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
          <p>请在最右侧选择一个接入端点</p>
        </div>

        <div v-else class="params-list">
          <p v-if="capabilityHint" class="cap-hint">{{ capabilityHint }}</p>
          <p v-if="hasParamSchema && unsupportedParamCodes.length > 0" class="cap-hint muted">
            当前模型参数中有 {{ unsupportedParamCodes.length }} 项暂不支持实例侧填写：{{
              unsupportedParamCodes.join(', ')
            }}
          </p>

          <template v-if="modelKind === 'chat'">
            <div v-if="showChatTemperature" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left">
                  <template #title>控制生成内容的随机性。</template>
                  <span class="p-label">采样温度 (Temperature) <QuestionCircleOutlined/></span></a-tooltip>
                <a-input-number v-model:value="form.temperature" :max="2" :min="0" :step="0.1" size="small"/>
              </div>
              <div class="slider-box">
                <a-slider v-model:value="form.temperature" :marks="{ 0: '严谨', 0.7: '平衡', 1.5: '创意', 2: '随机' }" :max="2" :min="0"
                          :step="0.1"/>
              </div>
              <div :class="getTempInfo(form.temperature ?? 0.7).color" class="p-desc-bar">
                {{ getTempInfo(form.temperature ?? 0.7).text }}
              </div>
            </div>

            <div v-if="showChatMaxTokens" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left">
                  <template #title>设置生成内容的最大长度限制。</template>
                  <span class="p-label">响应上限 (Max Tokens) <QuestionCircleOutlined/></span></a-tooltip>
                <a-input-number v-model:value="form.maxTokens" :max="128000" :min="1" size="small"/>
              </div>
              <div class="slider-box">
                <a-slider v-model:value="form.maxTokens" :marks="{ 0: '短', 2048: '中等', 4096: '长', 8192: '超长' }" :max="8192" :min="0"
                          :step="256"/>
              </div>
            </div>

            <div v-if="showChatTopP" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left" title="核心采样"><span class="p-label">核采样 (Top P) <QuestionCircleOutlined/></span>
                </a-tooltip>
                <a-input-number v-model:value="form.topP" :max="1" :min="0" :step="0.01" size="small"/>
              </div>
              <div class="slider-box">
                <a-slider v-model:value="form.topP" :marks="{ 0: '极窄', 0.5: '标准', 1: '完整' }" :max="1" :min="0"
                          :step="0.05"/>
              </div>
            </div>

            <div v-if="showChatTopK" class="param-group-card">
              <div class="p-header"><span class="p-label">Top K <QuestionCircleOutlined/></span>
                <a-input-number v-model:value="form.topK" :max="100" :min="0" :step="1" size="small"/>
              </div>
              <p class="p-inline-hint">0 表示不启用</p>
            </div>
            <div v-if="showChatSeed" class="param-group-card">
              <div class="p-header"><span class="p-label">随机种子 (Seed) <QuestionCircleOutlined/></span>
                <a-input-number v-model:value="form.seed" :max="2147483647" :min="0" :step="1" size="small"/>
              </div>
            </div>
            <div v-if="showChatStopSequences" class="param-group-card">
              <div class="p-header"><span class="p-label">停止序列 (Stop)</span></div>
              <a-textarea v-model:value="form.stopSequences" :rows="3" class="stop-seq-input"
                          placeholder="多个序列用英文逗号分隔"/>
            </div>
            <div v-if="showChatPenalties" class="penalty-row">
              <div v-if="showChatFrequencyPenalty" class="mini-param-card"><span
                  class="mini-label">重复惩罚 (Frequency)</span>
                <a-slider v-model:value="form.frequencyPenalty" :max="2" :min="-2" :step="0.1"/>
              </div>
              <div v-if="showChatPresencePenalty" class="mini-param-card"><span
                  class="mini-label">新鲜度 (Presence)</span>
                <a-slider v-model:value="form.presencePenalty" :max="2" :min="-2" :step="0.1"/>
              </div>
            </div>
          </template>

          <template v-else-if="modelKind === 'embedding'">
            <div v-if="showEmbeddingDimensions" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left">
                  <template #title>控制输出向量的维度大小，维度越高信息越丰富但计算成本越大。</template>
                  <span class="p-label">向量维度 (Dimensions) <QuestionCircleOutlined/></span></a-tooltip>
              </div>
              <a-select
                  v-model:value="form.dimensions"
                  :filter-option="filterDimensionOption"
                  :options="dimensionOptions"
                  allow-clear
                  placeholder="选择或输入维度"
                  show-search
                  size="large"
                  style="width: 100%"
              />
            </div>
            <p v-if="!embeddingHasAnyControl" class="cap-hint muted">当前端点未开放向量可调参数。</p>
          </template>

          <template v-else-if="modelKind === 'image'">
            <div v-if="showImageSize" class="param-group-card">
              <div class="p-header"><span class="p-label">画幅尺寸 (Size)</span></div>
              <a-input v-model:value="form.size" allow-clear placeholder="例如 1024x1024" size="large"/>
            </div>
            <div v-if="showImageStyle" class="param-group-card">
              <div class="p-header"><span class="p-label">风格 (Style)</span></div>
              <a-input v-model:value="form.style" allow-clear placeholder="例如 vivid / natural" size="large"/>
            </div>
            <p v-if="!imageHasAnyControl" class="cap-hint muted">当前端点未开放图像可调参数。</p>
          </template>
        </div>
      </div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import {ControlOutlined, QuestionCircleOutlined, SelectOutlined} from '@ant-design/icons-vue'

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

const dimensionOptions = [
  {value: 256, label: '256 — 轻量级，适合简单检索'},
  {value: 512, label: '512 — 紧凑型，平衡性能与精度'},
  {value: 768, label: '768 — 常用基线（BGE / text-embedding-ada）'},
  {value: 1024, label: '1024 — 中高维度，语义表达更丰富'},
  {value: 1536, label: '1536 — 主流高维（OpenAI text-embedding-3）'},
  {value: 2048, label: '2048 — 高精度场景'},
  {value: 3072, label: '3072 — 超高精度，适合专业语义匹配'},
  {value: 4096, label: '4096 — 最大常用档位'},
  {value: 8192, label: '8192 — 极限维度，计算成本极高'}
]

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
  background: #fff;
  border-radius: var(--radius-md);
  border: 1px solid #e2e8f0;
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
  color: #1e293b;
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
  color: #64748b;
  line-height: 1.5;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  margin-bottom: 16px;
}

.cap-hint.muted {
  color: #94a3b8;
  background: #fafafa;
  border-style: dashed;
}

.param-group-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
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
  color: #334155;
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
  background: #f8fafc;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.mini-label {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  margin-bottom: 8px;
  display: block;
}

.empty-state {
  text-align: center;
  padding: 100px 0;
  color: #cbd5e1;
}

.empty-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  margin: 0 auto 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef2ff;
  color: #4f46e5;
}
</style>
