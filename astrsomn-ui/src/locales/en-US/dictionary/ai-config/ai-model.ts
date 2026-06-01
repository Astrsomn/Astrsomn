import {createEnumDictionary} from '@/locales/dictionary/core.ts'
import {
    AI_MODEL_CAPABILITY_ORDER,
    AI_MODEL_PROVIDER_ORDER,
    AI_MODEL_SOURCE_TYPE_ORDER,
    AI_MODEL_STATUS_ORDER
} from '@/constants/aiModelEnums.ts'

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
    qianfan: 'Baidu Qianfan',
    xiaomi: 'Xiaomi (MiMo)',
    volcengine: 'Volcengine (Doubao)'
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
    // === Chat Model Capabilities (ChatCapabilitiesEnum) ===
    streaming: 'Streaming output (StreamingChatLanguageModel)',
    tools: 'Tool/function calling (ToolSpecifications)',
    vision: 'Vision understanding (ImageContent)',
    json_mode: 'JSON mode (ResponseFormat)',
    deep_reasoning: 'Deep reasoning (e.g. DeepSeek-R1 / O1)',
    context_caching: 'Context caching (Context Caching)',

    // === Embedding Model Capabilities (EmbeddingCapabilityEnum) ===
    text_embedding: 'Text embedding',
    image_embedding: 'Image embedding',

    // === Image Model Capabilities (ImageCapabilitiesEnum) ===
    text_to_image: 'Text to image',
    image_to_image: 'Image to image',
    image_editing: 'Image editing/repair',

    // === Chat Model Parameters (ChatParamEnum) ===
    temperature: 'Temperature',
    top_p: 'Top-p (nucleus sampling)',
    top_k: 'Top-k sampling',
    max_tokens: 'Max output tokens',
    stop_sequences: 'Stop sequences',
    seed: 'Random seed',
    presence_penalty: 'Presence penalty',
    frequency_penalty: 'Frequency penalty',
    logit_bias: 'Token bias',

    // === Embedding Model Parameters (EmbeddingParamEnum) ===
    dimensions: 'Output dimensions',
    model_name: 'Model name',
    user: 'End-user id',
    max_retries: 'Max retries',
    timeout_seconds: 'Timeout (seconds)',
    max_segments_per_batch: 'Max segments per batch',
    encoding_format: 'Encoding format',

    // === Image Model Parameters (ImageParamEnum) ===
    size: 'Image size (e.g. 1024x1024)',
    quality: 'Quality (standard/hd)',
    style: 'Style (vivid/natural)',
    response_format: 'Response format (url/b64_json)',

    // === System & HTTP Configuration (SystemConfigParam) ===
    base_url: 'Base API URL',
    api_key: 'API key/token',
    log_requests: 'Enable request logging',
    log_responses: 'Enable response logging',
    proxy_url: 'Proxy server URL'
} as const

export const aiModelCapabilitiesDictionary = createEnumDictionary({
    id: 'ai-model.capabilities',
    labels: capabilityLabels,
    order: AI_MODEL_CAPABILITY_ORDER
})

const sourceTypeLabels = {
    user_custom: 'User Custom Model',
    plugin: 'Plugin Model'
} as const

export const aiModelSourceTypeDictionary = createEnumDictionary({
    id: 'ai-model.sourceType',
    labels: sourceTypeLabels,
    order: AI_MODEL_SOURCE_TYPE_ORDER
})
