/**
 * 与后端 `org.astrsomn.core.common.constant.AiModelEnum` 各枚举 code 一致，
 * 供模型表单一处维护 capabilities JSON。
 */

/** ChatCapabilitiesEnum - 聊天模型核心能力 */
export const CHAT_FEATURE_CODES = [
  'text_generation',
  'deep_reasoning',
  'function_calling',
  'streaming',
  'json_mode',
  'vision',
  'network_search'
] as const

/** InferenceParamEnum - 聊天模型推理参数（含历史别名） */
export const CHAT_INFERENCE_CODES = [
  'temperature',
  'top_p',
  'top_k',
  'presence_penalty',
  'frequency_penalty',
  'max_tokens',
  'seed',
  'temperature_setting',
  'top_p_setting',
  'top_k_setting',
  'presence_penalty_setting',
  'frequency_penalty_setting',
  'max_token_setting',
  'stop_sequences_setting',
  'seed_setting'
] as const

/** EmbeddingCapabilitiesEnum - 嵌入模型核心能力 */
export const EMBEDDING_FEATURE_CODES = [
  'text_embedding',
  'image_embedding',
  'semantic_search'
] as const

/** EmbeddingInferenceParamEnum - 嵌入模型推理参数 */
export const EMBEDDING_INFERENCE_CODES = [
  'embedding_dimensions',
  'embedding_user',
  'embedding_max_retries',
  'embedding_max_segments_per_batch',
  'embedding_encoding_format',
  'embedding_timeout_seconds'
] as const

/** ImageCapabilitiesEnum - 图像模型核心能力 */
export const IMAGE_FEATURE_CODES = [
  'image_recognition',
  'image_generation',
  'text_to_image',
  'image_to_image',
  'image_editing',
  'image_inpainting'
] as const

/** ImageGenParamEnum - 图像模型生成参数（含历史别名） */
export const IMAGE_GEN_CODES = [
  'image_size',
  'image_quality',
  'image_style',
  'image_user',
  'image_response_format',
  'image_max_retries',
  'image_timeout_seconds',
  'size_setting',
  'style_setting'
] as const

// 集合形式，方便快速查找
export const CHAT_FEATURE_SET = new Set<string>(CHAT_FEATURE_CODES)
export const CHAT_INFERENCE_SET = new Set<string>(CHAT_INFERENCE_CODES)
export const CHAT_ALL_KNOWN_SET = new Set<string>([...CHAT_FEATURE_CODES, ...CHAT_INFERENCE_CODES])

export const EMBEDDING_FEATURE_SET = new Set<string>(EMBEDDING_FEATURE_CODES)
export const EMBEDDING_INFERENCE_SET = new Set<string>(EMBEDDING_INFERENCE_CODES)
export const EMBEDDING_ALL_KNOWN_SET = new Set<string>([
  ...EMBEDDING_FEATURE_CODES,
  ...EMBEDDING_INFERENCE_CODES
])

export const IMAGE_FEATURE_SET = new Set<string>(IMAGE_FEATURE_CODES)
export const IMAGE_GEN_SET = new Set<string>(IMAGE_GEN_CODES)
export const IMAGE_ALL_KNOWN_SET = new Set<string>([...IMAGE_FEATURE_CODES, ...IMAGE_GEN_CODES])

// 模型类型常量
export const MODEL_TYPE_CODES = {
  CHAT: 'chat',
  EMBEDDING: 'embedding',
  IMAGE: 'image'
} as const

// 能力类型映射，方便根据模型类型获取对应能力
export const MODEL_CAPABILITIES_MAP = {
  [MODEL_TYPE_CODES.CHAT]: {
    features: CHAT_FEATURE_CODES,
    inference: CHAT_INFERENCE_CODES,
    allKnown: [...CHAT_FEATURE_CODES, ...CHAT_INFERENCE_CODES]
  },
  [MODEL_TYPE_CODES.EMBEDDING]: {
    features: EMBEDDING_FEATURE_CODES,
    inference: EMBEDDING_INFERENCE_CODES,
    allKnown: [...EMBEDDING_FEATURE_CODES, ...EMBEDDING_INFERENCE_CODES]
  },
  [MODEL_TYPE_CODES.IMAGE]: {
    features: IMAGE_FEATURE_CODES,
    inference: IMAGE_GEN_CODES,
    allKnown: [...IMAGE_FEATURE_CODES, ...IMAGE_GEN_CODES]
  }
} as const
