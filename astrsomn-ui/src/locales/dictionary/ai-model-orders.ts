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
  /** 与后端 `AiModelEnum.InferenceParamEnum` / LangChain4j Builder 对齐（推荐写入 capabilities） */
  'temperature',
  'top_p',
  'top_k',
  'presence_penalty',
  'frequency_penalty',
  'max_tokens',
  'seed',
  /** 历史别名，后端 `InferenceParamEnum#containedIn` 仍识别 */
  'temperature_setting',
  'top_p_setting',
  'top_k_setting',
  'presence_penalty_setting',
  'frequency_penalty_setting',
  'max_token_setting',
  'stop_sequences_setting',
  'seed_setting',
  /** `AiModelEnum.ImageGenParamEnum` / OpenAiImageModel Builder */
  'image_size',
  'image_quality',
  'image_style',
  'image_user',
  'image_response_format',
  'image_max_retries',
  'image_timeout_seconds',
  'size_setting',
  'style_setting',
  /** `AiModelEnum.EmbeddingInferenceParamEnum` / OpenAiEmbeddingModel Builder */
  'embedding_dimensions',
  'embedding_user',
  'embedding_max_retries',
  'embedding_max_segments_per_batch',
  'embedding_encoding_format',
  'embedding_timeout_seconds',
  'network_search'
] as const

export type AiModelCapabilityCode = (typeof AI_MODEL_CAPABILITY_ORDER)[number]

export const AI_MODEL_SOURCE_TYPE_ORDER = ['user_custom', 'plugin'] as const

export type AiModelSourceTypeCode = (typeof AI_MODEL_SOURCE_TYPE_ORDER)[number]
