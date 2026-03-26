/**
 * 与后端 `AiModelEnum` 各枚举的 code 顺序一致；文案按语言分文件，顺序共用。
 */
export const AI_MODEL_PROVIDER_ORDER = [
  'openai',
  'xai',
  'anthropic',
  'google',
  'alibaba',
  'zhipu',
  'moonshot',
  'baidu',
  'baichuan',
  'minimax',
  'yi',
  'siliconflow',
  'tencent',
  'deepseek',
  'ollama',
  'qianfan'
] as const

export type AiModelProviderCode = (typeof AI_MODEL_PROVIDER_ORDER)[number]

export const AI_MODEL_STATUS_ORDER = ['enabled', 'disabled'] as const

export type AiModelStatusCode = (typeof AI_MODEL_STATUS_ORDER)[number]

export const AI_MODEL_CAPABILITY_ORDER = [
  'text_generation',
  'image_recognition',
  'image_generation',
  'deep_reasoning',
  'temperature_setting',
  'top_p_setting',
  'top_k_setting',
  'presence_penalty_setting',
  'frequency_penalty_setting',
  'max_token_setting',
  'stop_sequences_setting',
  'seed_setting',
  'network_search'
] as const

export type AiModelCapabilityCode = (typeof AI_MODEL_CAPABILITY_ORDER)[number]
