<template>
  <section class="params-pane">
    <div class="pane-card glass-card scroll-y">
      <div class="config-section">
        <div class="section-header-flex">
          <h3 class="section-title"><ControlOutlined /> {{ paramSectionTitle }}</h3>
          <a-tag v-if="form.modelKey" color="blue" class="model-key-tag">{{ form.modelKey }}</a-tag>
        </div>

        <div v-if="!form.modelKey" class="empty-state">
          <div class="empty-icon"><SelectOutlined /></div>
          <p>请在最右侧选择一个接入端点</p>
        </div>

        <div v-else class="params-list">
          <p v-if="capabilityHint" class="cap-hint">{{ capabilityHint }}</p>
          <p v-if="hasParamSchema && unsupportedParamCodes.length > 0" class="cap-hint muted">
            当前模型参数中有 {{ unsupportedParamCodes.length }} 项暂不支持实例侧填写：{{ unsupportedParamCodes.join(', ') }}
          </p>

          <template v-if="modelKind === 'chat'">
            <div v-if="showChatTemperature" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left"><template #title>控制生成内容的随机性。</template><span class="p-label">采样温度 (Temperature) <QuestionCircleOutlined /></span></a-tooltip>
                <a-input-number v-model:value="form.temperature" :min="0" :max="2" :step="0.1" size="small" />
              </div>
              <div class="slider-box"><a-slider v-model:value="form.temperature" :min="0" :max="2" :step="0.1" :marks="{ 0: '严谨', 0.7: '平衡', 1.5: '创意', 2: '随机' }" /></div>
              <div class="p-desc-bar" :class="getTempInfo(form.temperature ?? 0.7).color">{{ getTempInfo(form.temperature ?? 0.7).text }}</div>
            </div>

            <div v-if="showChatMaxTokens" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left"><template #title>设置生成内容的最大长度限制。</template><span class="p-label">响应上限 (Max Tokens) <QuestionCircleOutlined /></span></a-tooltip>
                <a-input-number v-model:value="form.maxTokens" :min="1" :max="128000" size="small" />
              </div>
              <div class="slider-box"><a-slider v-model:value="form.maxTokens" :min="0" :max="8192" :step="256" :marks="{ 0: '短', 2048: '中等', 4096: '长', 8192: '超长' }" /></div>
            </div>

            <div v-if="showChatTopP" class="param-group-card">
              <div class="p-header">
                <a-tooltip placement="left" title="核心采样"><span class="p-label">核采样 (Top P) <QuestionCircleOutlined /></span></a-tooltip>
                <a-input-number v-model:value="form.topP" :min="0" :max="1" :step="0.01" size="small" />
              </div>
              <div class="slider-box"><a-slider v-model:value="form.topP" :min="0" :max="1" :step="0.05" :marks="{ 0: '极窄', 0.5: '标准', 1: '完整' }" /></div>
            </div>

            <div v-if="showChatTopK" class="param-group-card">
              <div class="p-header"><span class="p-label">Top K <QuestionCircleOutlined /></span><a-input-number v-model:value="form.topK" :min="0" :max="100" :step="1" size="small" /></div>
              <p class="p-inline-hint">0 表示不启用</p>
            </div>
            <div v-if="showChatSeed" class="param-group-card"><div class="p-header"><span class="p-label">随机种子 (Seed) <QuestionCircleOutlined /></span><a-input-number v-model:value="form.seed" :min="0" :max="2147483647" :step="1" size="small" /></div></div>
            <div v-if="showChatStopSequences" class="param-group-card"><div class="p-header"><span class="p-label">停止序列 (Stop)</span></div><a-textarea v-model:value="form.stopSequences" placeholder="多个序列用英文逗号分隔" :rows="3" class="stop-seq-input" /></div>
            <div v-if="showChatPenalties" class="penalty-row">
              <div v-if="showChatFrequencyPenalty" class="mini-param-card"><span class="mini-label">重复惩罚 (Frequency)</span><a-slider v-model:value="form.frequencyPenalty" :min="-2" :max="2" :step="0.1" /></div>
              <div v-if="showChatPresencePenalty" class="mini-param-card"><span class="mini-label">新鲜度 (Presence)</span><a-slider v-model:value="form.presencePenalty" :min="-2" :max="2" :step="0.1" /></div>
            </div>
          </template>

          <template v-else-if="modelKind === 'embedding'">
            <div v-if="showEmbeddingDimensions" class="param-group-card">
              <div class="p-header"><span class="p-label">向量维度 (Dimensions) <QuestionCircleOutlined /></span><a-input-number v-model:value="form.dimensions" :min="1" :max="8192" :step="1" size="small" placeholder="如 1536" /></div>
            </div>
            <p v-if="!embeddingHasAnyControl" class="cap-hint muted">当前端点未开放向量可调参数。</p>
          </template>

          <template v-else-if="modelKind === 'image'">
            <div v-if="showImageSize" class="param-group-card"><div class="p-header"><span class="p-label">画幅尺寸 (Size)</span></div><a-input v-model:value="form.size" placeholder="例如 1024x1024" size="large" allow-clear /></div>
            <div v-if="showImageStyle" class="param-group-card"><div class="p-header"><span class="p-label">风格 (Style)</span></div><a-input v-model:value="form.style" placeholder="例如 vivid / natural" size="large" allow-clear /></div>
            <p v-if="!imageHasAnyControl" class="cap-hint muted">当前端点未开放图像可调参数。</p>
          </template>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ControlOutlined, QuestionCircleOutlined, SelectOutlined } from '@ant-design/icons-vue'

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
</script>

<style scoped>
.params-pane { flex: 1; min-width: 0; min-height: 0; display: flex; flex-direction: column; }
.pane-card { height: 100%; display: flex; flex-direction: column; padding: 18px; }
.glass-card { background: #fff; border-radius: 16px; border: 1px solid #e2e8f0; box-shadow: 0 2px 12px rgba(15, 23, 42, 0.04); }
.scroll-y { overflow-y: auto; }
.section-header-flex { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 16px; }
.section-title { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.model-key-tag { flex-shrink: 0; max-width: 180px; overflow: hidden; text-overflow: ellipsis; }
.cap-hint { font-size: 12px; color: #64748b; line-height: 1.5; padding: 10px 12px; background: #f8fafc; border-radius: 10px; border: 1px solid #e2e8f0; margin-bottom: 16px; }
.cap-hint.muted { color: #94a3b8; background: #fafafa; border-style: dashed; }
.param-group-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 14px; margin-bottom: 14px; transition: all 0.2s; }
.p-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.p-label { font-size: 13px; font-weight: 600; color: #334155; cursor: help; display: flex; align-items: center; gap: 4px; }
.slider-box { padding: 0 8px 16px 8px; }
.p-desc-bar { margin-top: 8px; padding: 8px 12px; border-radius: 8px; font-size: 11px; font-weight: 600; border-left: 4px solid transparent; }
.c-blue { background: #eff6ff; color: #1d4ed8; border-left-color: #3b82f6; }
.c-purple { background: #faf5ff; color: #7e22ce; border-left-color: #a855f7; }
.c-orange { background: #fff7ed; color: #c2410c; border-left-color: #f97316; }
.c-red { background: #fef2f2; color: #b91c1c; border-left-color: #ef4444; }
.penalty-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.mini-param-card { background: #f8fafc; padding: 12px; border-radius: 12px; border: 1px solid #f1f5f9; }
.mini-label { font-size: 11px; font-weight: 700; color: #64748b; margin-bottom: 8px; display: block; }
.empty-state { text-align: center; padding: 100px 0; color: #cbd5e1; }
.empty-icon { width: 42px; height: 42px; border-radius: 12px; margin: 0 auto 10px; display: flex; align-items: center; justify-content: center; background: #eef2ff; color: #4f46e5; }
</style>
