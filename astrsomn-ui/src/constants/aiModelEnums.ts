/**
 * AI 模型相关枚举定义，与后端 `AiModelParamEnum` 和 `AiModelEnum` 各枚举 code 一致。
 * 整合了原来的 ai-model-orders.ts 和 aiModelCapabilityCodes.ts 文件。
 */

// === 模型提供商顺序，与后端 AiModelEnum.ProviderEnum 对齐 ===
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
    'qianfan',
    'xiaomi',
    'volcengine'
] as const

export type AiModelExtensionCode = (typeof AI_MODEL_PROVIDER_ORDER)[number]

// === 模型状态顺序 ===
export const AI_MODEL_STATUS_ORDER = ['enabled', 'disabled'] as const

export type AiModelStatusCode = (typeof AI_MODEL_STATUS_ORDER)[number]

// === 模型源类型顺序 ===
export const AI_MODEL_SOURCE_TYPE_ORDER = ['user_custom', 'plugin'] as const

export type AiModelSourceTypeCode = (typeof AI_MODEL_SOURCE_TYPE_ORDER)[number]

// === 模型能力和参数顺序 ===
export const AI_MODEL_CAPABILITY_ORDER = [
    // === 聊天模型能力位 (ChatCapabilitiesEnum) ===
    'streaming',
    'tools',
    'vision',
    'json_mode',
    'deep_reasoning',
    'context_caching',

    // === 向量模型能力位 (EmbeddingCapabilityEnum) ===
    'text_embedding',
    'image_embedding',

    // === 图像模型能力位 (ImageCapabilitiesEnum) ===
    'text_to_image',
    'image_to_image',
    'image_editing',

    // === 聊天模型推理参数 (ChatParamEnum) ===
    'temperature',
    'top_p',
    'top_k',
    'max_tokens',
    'stop_sequences',
    'seed',
    'presence_penalty',
    'frequency_penalty',
    'logit_bias',

    // === 向量模型参数 (EmbeddingParamEnum) ===
    'dimensions',
    'model_name',
    'user',
    'max_retries',
    'timeout_seconds',
    'max_segments_per_batch',
    'encoding_format',

    // === 图像生成参数 (ImageParamEnum) ===
    'size',
    'quality',
    'style',
    'response_format',
    'user',
    'max_retries',
    'timeout_seconds',

    // === 系统与 HTTP 级配置 (SystemConfigParam) ===
    'base_url',
    'api_key',
    'timeout_seconds',
    'max_retries',
    'log_requests',
    'log_responses',
    'proxy_url'
] as const

export type AiModelCapabilityCode = (typeof AI_MODEL_CAPABILITY_ORDER)[number]

// === 聊天模型能力位 ===
export const CHAT_CAPABILITIES_CODES = [
    'streaming',
    'tools',
    'vision',
    'json_mode',
    'deep_reasoning',
    'context_caching'
] as const

// === 聊天模型推理参数 ===
export const CHAT_PARAM_CODES = [
    'temperature',
    'top_p',
    'top_k',
    'max_tokens',
    'stop_sequences',
    'seed',
    'presence_penalty',
    'frequency_penalty',
    'logit_bias'
] as const

// === 向量模型能力位 ===
export const EMBEDDING_CAPABILITIES_CODES = [
    'text_embedding',
    'image_embedding'
] as const

// === 向量模型参数 ===
export const EMBEDDING_PARAM_CODES = [
    'dimensions',
    'model_name',
    'user',
    'max_retries',
    'timeout_seconds',
    'max_segments_per_batch',
    'encoding_format'
] as const

// === 图像模型能力位 ===
export const IMAGE_CAPABILITIES_CODES = [
    'text_to_image',
    'image_to_image',
    'image_editing'
] as const

// === 图像生成参数 ===
export const IMAGE_PARAM_CODES = [
    'size',
    'quality',
    'style',
    'response_format',
    'user',
    'max_retries',
    'timeout_seconds'
] as const

// === 系统与 HTTP 级配置 ===
export const SYSTEM_CONFIG_CODES = [
    'base_url',
    'api_key',
    'timeout_seconds',
    'max_retries',
    'log_requests',
    'log_responses',
    'proxy_url'
] as const

// 集合形式，方便快速查找
export const CHAT_CAPABILITIES_SET = new Set<string>(CHAT_CAPABILITIES_CODES)
export const CHAT_PARAM_SET = new Set<string>(CHAT_PARAM_CODES)
export const CHAT_ALL_KNOWN_SET = new Set<string>([...CHAT_CAPABILITIES_CODES, ...CHAT_PARAM_CODES])

export const EMBEDDING_CAPABILITIES_SET = new Set<string>(EMBEDDING_CAPABILITIES_CODES)
export const EMBEDDING_PARAM_SET = new Set<string>(EMBEDDING_PARAM_CODES)
export const EMBEDDING_ALL_KNOWN_SET = new Set<string>([
    ...EMBEDDING_CAPABILITIES_CODES,
    ...EMBEDDING_PARAM_CODES
])

export const IMAGE_CAPABILITIES_SET = new Set<string>(IMAGE_CAPABILITIES_CODES)
export const IMAGE_PARAM_SET = new Set<string>(IMAGE_PARAM_CODES)
export const IMAGE_ALL_KNOWN_SET = new Set<string>([...IMAGE_CAPABILITIES_CODES, ...IMAGE_PARAM_CODES])

export const SYSTEM_CONFIG_SET = new Set<string>(SYSTEM_CONFIG_CODES)

// 模型类型常量
export const MODEL_TYPE_CODES = {
    CHAT: 'chat',
    EMBEDDING: 'embedding',
    IMAGE: 'image'
} as const

// 能力与参数映射，方便根据模型类型获取对应配置
export const MODEL_CONFIG_MAP = {
    [MODEL_TYPE_CODES.CHAT]: {
        capabilities: CHAT_CAPABILITIES_CODES,
        params: CHAT_PARAM_CODES,
        allKnown: [...CHAT_CAPABILITIES_CODES, ...CHAT_PARAM_CODES]
    },
    [MODEL_TYPE_CODES.EMBEDDING]: {
        capabilities: EMBEDDING_CAPABILITIES_CODES,
        params: EMBEDDING_PARAM_CODES,
        allKnown: [...EMBEDDING_CAPABILITIES_CODES, ...EMBEDDING_PARAM_CODES]
    },
    [MODEL_TYPE_CODES.IMAGE]: {
        capabilities: IMAGE_CAPABILITIES_CODES,
        params: IMAGE_PARAM_CODES,
        allKnown: [...IMAGE_CAPABILITIES_CODES, ...IMAGE_PARAM_CODES]
    }
} as const
