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
  temperature: '温度 temperature（LangChain / OpenAI 兼容）',
  top_p: '核采样 top_p',
  top_k: 'Top-K 采样',
  presence_penalty: '存在惩罚 presence_penalty',
  frequency_penalty: '频率惩罚 frequency_penalty',
  max_tokens: '最大生成 token max_tokens',
  seed: '随机种子 seed',
  temperature_setting: '温度：控制输出随机性与创造性',
  top_p_setting: '核采样（Top-p）：累积概率阈值',
  top_k_setting: 'Top-k：仅从概率最高的 k 个 token 中采样',
  presence_penalty_setting: '存在惩罚：抑制已出现主题重复',
  frequency_penalty_setting: '频率惩罚：抑制同一表述重复',
  max_token_setting: '最大生成 token 数',
  stop_sequences_setting: '停止序列：自定义结束标记',
  seed_setting: '随机种子：可复现输出',
  image_size: '文生图：尺寸 image_size（如 1024x1024）',
  image_quality: '文生图：质量 image_quality（standard/hd）',
  image_style: '文生图：风格 image_style（vivid/natural）',
  image_user: '文生图：终端用户标识 image_user',
  image_response_format: '文生图：返回格式 image_response_format（url/b64_json）',
  image_max_retries: '文生图：重试次数 image_max_retries',
  image_timeout_seconds: '文生图：超时秒数 image_timeout_seconds',
  size_setting: '文生图：尺寸（旧别名，等同 image_size）',
  style_setting: '文生图：风格（旧别名，等同 image_style）',
  embedding_dimensions: '嵌入：向量维度 embedding_dimensions',
  embedding_user: '嵌入：终端用户 embedding_user',
  embedding_max_retries: '嵌入：重试次数 embedding_max_retries',
  embedding_max_segments_per_batch: '嵌入：单批最大条数 embedding_max_segments_per_batch',
  embedding_encoding_format: '嵌入：编码格式 embedding_encoding_format',
  embedding_timeout_seconds: '嵌入：超时秒数 embedding_timeout_seconds',
  network_search: '联网搜索'
} as const

export const aiModelCapabilitiesDictionary = createEnumDictionary({
  id: 'ai-model.capabilities',
  labels: capabilityLabels,
  order: AI_MODEL_CAPABILITY_ORDER
})
