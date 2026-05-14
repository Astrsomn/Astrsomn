/**
 * 与后端 `AiModelParamEnum` 内各枚举的 code 一一对应（中文文案）。
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'
import {
    AI_MODEL_CAPABILITY_ORDER,
    AI_MODEL_PROVIDER_ORDER,
    AI_MODEL_SOURCE_TYPE_ORDER,
    AI_MODEL_STATUS_ORDER
} from '@/constants/aiModelEnums.ts'

const providerLabels = {
    openai: 'OpenAI',
    xai: 'xAI',
    anthropic: 'Anthropic',
    google: 'Google',
    alibaba: '通义千问',
    zhipu: '智谱 AI',
    moonshot: '月之暗面',
    baichuan: '百川智能',
    minimax: 'MiniMax',
    yi: '零一万物',
    siliconflow: '硅基流动',
    tencent: '腾讯混元',
    deepseek: 'DeepSeek',
    ollama: 'Ollama',
    qianfan: '百度千帆'
} as const;

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
    // === 聊天模型能力位 (ChatCapabilitiesEnum) ===
    streaming: '流式输出 (StreamingChatLanguageModel)',
    tools: '工具/函数调用 (ToolSpecifications)',
    vision: '视觉理解 (ImageContent)',
    json_mode: 'JSON 模式 (ResponseFormat)',
    deep_reasoning: '深度推理 (如 DeepSeek-R1 / O1)',
    context_caching: '上下文缓存 (Context Caching)',

    // === 聊天模型推理参数 (ChatParamEnum) ===
    temperature: '采样温度',
    top_p: '核采样 (Top-P)',
    top_k: 'Top-K 采样',
    max_tokens: '最大生成 Token 数',
    stop_sequences: '停止词列表',
    seed: '随机种子',
    presence_penalty: '话题存在惩罚',
    frequency_penalty: '频率惩罚',
    logit_bias: 'Token 偏好偏差',

    // === 向量模型能力位 (EmbeddingCapabilityEnum) ===
    text_embedding: '文本向量化',
    image_embedding: '图像向量化',

    // === 向量模型参数 (EmbeddingParamEnum) ===
    dimensions: '向量输出维度',
    model_name: '模型名称',
    user: '终端用户标识',
    max_retries: '最大重试次数',
    timeout_seconds: '超时时间（秒）',
    max_segments_per_batch: '每批最大分段数',
    encoding_format: '嵌入编码格式',

    // === 图像模型能力位 (ImageCapabilitiesEnum) ===
    text_to_image: '文生图',
    image_to_image: '图生图',
    image_editing: '图像编辑/修复',

    // === 图像生成参数 (ImageParamEnum) ===
    size: '图片尺寸 (如 1024x1024)',
    quality: '质量 (standard/hd)',
    style: '风格 (vivid/natural)',
    response_format: '响应格式 (url/b64_json)',
    // user / max_retries / timeout_seconds 与向量/系统配置共用同一 code，这里不重复定义

    // === 系统与 HTTP 级配置 (SystemConfigParam) ===
    base_url: '接口基础地址',
    api_key: '令牌/密钥',
    // timeout_seconds / max_retries 与向量/图像配置共用同一 code，这里不重复定义
    log_requests: '启用请求日志',
    log_responses: '启用响应日志',
    proxy_url: '代理服务器地址'
} as const

export const aiModelCapabilitiesDictionary = createEnumDictionary({
    id: 'ai-model.capabilities',
    labels: capabilityLabels,
    order: AI_MODEL_CAPABILITY_ORDER
})

const sourceTypeLabels = {
    user_custom: '用户自定义模型',
    plugin: '插件模型'
} as const

export const aiModelSourceTypeDictionary = createEnumDictionary({
    id: 'ai-model.sourceType',
    labels: sourceTypeLabels,
    order: AI_MODEL_SOURCE_TYPE_ORDER
})
