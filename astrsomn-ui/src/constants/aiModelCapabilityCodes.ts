/**
 * 与后端 `org.astrsomn.core.common.constant.AiModelEnum` 各枚举 code 一致，
 * 供模型表单一处维护 capabilities JSON。
 */

/** ChatCapabilitiesEnum */
export const CHAT_FEATURE_CODES = [
  'text_generation',
  'deep_reasoning',
  'function_calling',
  'streaming',
  'json_mode',
  'vision',
  'network_search'
] as const

/** InferenceParamEnum 主 code + 历史别名（后端 containedIn 仍识别） */
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

/** EmbeddingCapabilitiesEnum */
export const EMBEDDING_FEATURE_CODES = ['text_embedding', 'image_embedding', 'semantic_search'] as const

/** EmbeddingInferenceParamEnum */
export const EMBEDDING_INFERENCE_CODES = [
  'embedding_dimensions',
  'embedding_user',
  'embedding_max_retries',
  'embedding_max_segments_per_batch',
  'embedding_encoding_format',
  'embedding_timeout_seconds'
] as const

/** ImageCapabilitiesEnum */
export const IMAGE_FEATURE_CODES = [
  'image_recognition',
  'image_generation',
  'text_to_image',
  'image_to_image',
  'image_editing',
  'image_inpainting'
] as const

/** ImageGenParamEnum（含后端 size_setting / style_setting 别名识别） */
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
