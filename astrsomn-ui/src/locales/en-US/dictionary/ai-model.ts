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
  temperature: 'Temperature (LangChain / OpenAI-compatible)',
  top_p: 'Top-p nucleus sampling',
  top_k: 'Top-k sampling',
  presence_penalty: 'Presence penalty',
  frequency_penalty: 'Frequency penalty',
  max_tokens: 'Max output tokens (max_tokens)',
  seed: 'Random seed',
  temperature_setting: 'Temperature (randomness / creativity)',
  top_p_setting: 'Top-p (nucleus sampling threshold)',
  top_k_setting: 'Top-k sampling',
  presence_penalty_setting: 'Presence penalty',
  frequency_penalty_setting: 'Frequency penalty',
  max_token_setting: 'Max tokens to generate',
  stop_sequences_setting: 'Stop sequences',
  seed_setting: 'Random seed (reproducible output)',
  image_size: 'Image: size (e.g. 1024x1024)',
  image_quality: 'Image: quality (standard / hd)',
  image_style: 'Image: style (vivid / natural)',
  image_user: 'Image: end-user id',
  image_response_format: 'Image: response format (url / b64_json)',
  image_max_retries: 'Image: max retries',
  image_timeout_seconds: 'Image: HTTP timeout (seconds)',
  size_setting: 'Image: size (legacy alias)',
  style_setting: 'Image: style (legacy alias)',
  embedding_dimensions: 'Embedding: output dimensions',
  embedding_user: 'Embedding: end-user id',
  embedding_max_retries: 'Embedding: max retries',
  embedding_max_segments_per_batch: 'Embedding: max segments per batch',
  embedding_encoding_format: 'Embedding: encoding format',
  embedding_timeout_seconds: 'Embedding: HTTP timeout (seconds)',
  network_search: 'Web search'
} as const

export const aiModelCapabilitiesDictionary = createEnumDictionary({
  id: 'ai-model.capabilities',
  labels: capabilityLabels,
  order: AI_MODEL_CAPABILITY_ORDER
})
