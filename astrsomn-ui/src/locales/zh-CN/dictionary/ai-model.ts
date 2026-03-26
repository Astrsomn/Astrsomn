/**
 * 与后端 `AiModelEnum` 内各枚举的 code 一一对应（中文文案）。
 */
import { createEnumDictionary } from '@/locales/dictionary/core'
import {
  AI_MODEL_CAPABILITY_ORDER,
  AI_MODEL_PROVIDER_ORDER,
  AI_MODEL_STATUS_ORDER
} from '@/locales/dictionary/ai-model-orders'

export type {
  AiModelCapabilityCode,
  AiModelProviderCode,
  AiModelStatusCode
} from '@/locales/dictionary/ai-model-orders'

const providerLabels = {
  openai: 'OpenAI（GPT）',
  xai: 'xAI（Grok）',
  anthropic: 'Anthropic（Claude）',
  google: 'Google（Gemini）',
  alibaba: '阿里巴巴（通义千问）',
  zhipu: '智谱 AI（GLM）',
  moonshot: 'Moonshot（月之暗面）',
  baidu: '百度文心',
  baichuan: '百川智能',
  minimax: 'MiniMax',
  yi: '零一万物（Yi）',
  siliconflow: '硅基流动（SiliconFlow）',
  tencent: '腾讯混元',
  deepseek: 'DeepSeek',
  ollama: 'Ollama（本地）',
  qianfan: '百度千帆'
} as const

export const aiModelProviderDictionary = createEnumDictionary({
  id: 'ai-model.provider',
  labels: providerLabels,
  order: AI_MODEL_PROVIDER_ORDER,
  caseInsensitive: true
})

const statusLabels = {
  enabled: '启用',
  disabled: '禁用'
} as const

export const aiModelStatusDictionary = createEnumDictionary({
  id: 'ai-model.status',
  labels: statusLabels,
  order: AI_MODEL_STATUS_ORDER,
  caseInsensitive: true
})

const capabilityLabels = {
  text_generation: '自然语言文本生成',
  image_recognition: '图像理解与识别（视觉）',
  image_generation: '文生图',
  deep_reasoning: '多步逻辑推理与思维链',
  temperature_setting: '温度：控制输出随机性与创造性',
  top_p_setting: '核采样（Top-p）：累积概率阈值',
  top_k_setting: 'Top-k：仅从概率最高的 k 个 token 中采样',
  presence_penalty_setting: '存在惩罚：抑制已出现主题重复',
  frequency_penalty_setting: '频率惩罚：抑制同一表述重复',
  max_token_setting: '最大生成 token 数',
  stop_sequences_setting: '停止序列：自定义结束标记',
  seed_setting: '随机种子：可复现输出',
  network_search: '联网搜索'
} as const

export const aiModelCapabilitiesDictionary = createEnumDictionary({
  id: 'ai-model.capabilities',
  labels: capabilityLabels,
  order: AI_MODEL_CAPABILITY_ORDER
})
