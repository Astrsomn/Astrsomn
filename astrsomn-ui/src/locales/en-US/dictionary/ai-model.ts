/**
 * Same codes as `AiModelEnum`; English labels for UI (aligned with backend desc where applicable).
 */
import { createEnumDictionary } from '@/locales/dictionary/core'
import {
  AI_MODEL_CAPABILITY_ORDER,
  AI_MODEL_PROVIDER_ORDER,
  AI_MODEL_STATUS_ORDER
} from '@/locales/dictionary/ai-model-orders'

const providerLabels = {
  openai: 'OpenAI (GPT)',
  xai: 'xAI (Grok)',
  anthropic: 'Anthropic (Claude)',
  google: 'Google (Gemini)',
  alibaba: 'Alibaba (Qwen)',
  zhipu: 'Zhipu (GLM)',
  moonshot: 'Moonshot',
  baidu: 'Baidu',
  baichuan: 'Baichuan',
  minimax: 'MiniMax',
  yi: 'Yi',
  siliconflow: 'SiliconFlow',
  tencent: 'Tencent',
  deepseek: 'DeepSeek',
  ollama: 'Ollama (local)',
  qianfan: 'Baidu Qianfan'
} as const

export const aiModelProviderDictionary = createEnumDictionary({
  id: 'ai-model.provider',
  labels: providerLabels,
  order: AI_MODEL_PROVIDER_ORDER,
  caseInsensitive: true
})

const statusLabels = {
  enabled: 'Enabled',
  disabled: 'Disabled'
} as const

export const aiModelStatusDictionary = createEnumDictionary({
  id: 'ai-model.status',
  labels: statusLabels,
  order: AI_MODEL_STATUS_ORDER,
  caseInsensitive: true
})

const capabilityLabels = {
  text_generation: 'Natural language text generation',
  image_recognition: 'Image understanding (vision)',
  image_generation: 'Text-to-image generation',
  deep_reasoning: 'Multi-step reasoning & chain-of-thought',
  temperature_setting: 'Temperature (randomness / creativity)',
  top_p_setting: 'Top-p (nucleus sampling threshold)',
  top_k_setting: 'Top-k sampling',
  presence_penalty_setting: 'Presence penalty',
  frequency_penalty_setting: 'Frequency penalty',
  max_token_setting: 'Max tokens to generate',
  stop_sequences_setting: 'Stop sequences',
  seed_setting: 'Random seed (reproducible output)',
  network_search: 'Web search'
} as const

export const aiModelCapabilitiesDictionary = createEnumDictionary({
  id: 'ai-model.capabilities',
  labels: capabilityLabels,
  order: AI_MODEL_CAPABILITY_ORDER
})
