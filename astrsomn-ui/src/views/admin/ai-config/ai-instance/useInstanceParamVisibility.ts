import {computed, type Ref} from 'vue'
import type {AiModel} from '@/api/aiModel'

export function parseCapabilitiesRaw(raw?: string): string[] {
    if (!raw) return []
    try {
        const p = JSON.parse(raw)
        return Array.isArray(p) ? p.map(String) : []
    } catch {
        return []
    }
}

export type ModelParamDef = {
    id?: string
    mapping?: string
    active?: boolean
}

export function parseModelParamsRaw(raw?: string): ModelParamDef[] {
    if (!raw) return []
    try {
        const parsed = JSON.parse(raw)
        if (!Array.isArray(parsed)) return []
        return parsed
            .filter((item) => item && typeof item === 'object')
            .map((item) => item as ModelParamDef)
    } catch {
        return []
    }
}

const SUPPORTED_PARAM_CODES = new Set<string>([
    'temperature',
    'max_tokens',
    'top_p',
    'top_k',
    'seed',
    'stop_sequences',
    'frequency_penalty',
    'presence_penalty',
    'dimensions',
    'size',
    'style',
])

export type TempInfo = { text: string; color: string }

export function getTempInfo(v: number): TempInfo {
    if (v <= 0.3) return {text: '🎯 适合：代码编写、数学逻辑、事实问答', color: 'c-blue'}
    if (v <= 0.8) return {text: '⚖️ 适合：通用对话、周报草拟、翻译', color: 'c-purple'}
    if (v <= 1.4) return {text: '🎨 适合：创意写作、角色扮演、头脑风暴', color: 'c-orange'}
    return {text: '🎲 适合：极高随机性的发散性内容', color: 'c-red'}
}

/**
 * Drives InstanceForm/InstanceParam param visibility from the currently selected AI_MODEL row.
 */
export function useInstanceParamVisibility(selectedModel: Ref<AiModel | undefined>) {
    const selectedCaps = computed(() => parseCapabilitiesRaw(selectedModel.value?.capabilities))

    const selectedModelParams = computed(() => {
        const model = selectedModel.value
        const raw = model?.params || model?.param
        return parseModelParamsRaw(raw)
    })

    const hasParamSchema = computed(() => selectedModelParams.value.length > 0)

    const activeParamCodes = computed(() => {
        if (!hasParamSchema.value) return new Set<string>()
        const codes = selectedModelParams.value
            .filter((item) => item.active !== false)
            .map((item) => String(item.mapping || item.id || '').trim().toLowerCase())
            .filter(Boolean)
        return new Set(codes)
    })

    const unsupportedParamCodes = computed(() => {
        if (!hasParamSchema.value) return []
        return Array.from(activeParamCodes.value).filter((code) => !SUPPORTED_PARAM_CODES.has(code))
    })

    const modelKind = computed<'chat' | 'embedding' | 'image'>(() => {
        const t = selectedModel.value?.modelType
        if (t === 'embedding' || t === 'image') return t
        return 'chat'
    })

    const legacyChatFullPanel = computed(() => modelKind.value === 'chat' && selectedCaps.value.length === 0)
    const legacyEmbeddingPanel = computed(() => modelKind.value === 'embedding' && selectedCaps.value.length === 0)
    const legacyImagePanel = computed(() => modelKind.value === 'image' && selectedCaps.value.length === 0)

    const showChatTemperature = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('temperature')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return true
        return c.includes('temperature') || c.includes('temperature_setting') || c.includes('text_generation')
    })

    const showChatMaxTokens = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('max_tokens')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return true
        return c.includes('max_tokens') || c.includes('max_token_setting')
    })

    const showChatTopP = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('top_p')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return true
        return c.includes('top_p') || c.includes('top_p_setting')
    })

    const showChatTopK = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('top_k')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return false
        return c.includes('top_k') || c.includes('top_k_setting')
    })

    const showChatSeed = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('seed')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return false
        return c.includes('seed') || c.includes('seed_setting')
    })

    const showChatStopSequences = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('stop_sequences')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return false
        return c.includes('stop_sequences_setting')
    })

    const showChatFrequencyPenalty = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('frequency_penalty')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return true
        return c.includes('frequency_penalty') || c.includes('frequency_penalty_setting')
    })

    const showChatPresencePenalty = computed(() => {
        if (modelKind.value !== 'chat') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('presence_penalty')
        const c = selectedCaps.value
        if (legacyChatFullPanel.value) return true
        return c.includes('presence_penalty') || c.includes('presence_penalty_setting')
    })

    const showChatPenalties = computed(
        () => showChatFrequencyPenalty.value || showChatPresencePenalty.value
    )

    const showEmbeddingDimensions = computed(() => {
        if (modelKind.value !== 'embedding') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('dimensions')
        const c = selectedCaps.value
        if (legacyEmbeddingPanel.value) return true
        return c.includes('embedding_dimensions')
    })

    const showImageSize = computed(() => {
        if (modelKind.value !== 'image') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('size')
        const c = selectedCaps.value
        if (legacyImagePanel.value) return true
        return c.includes('image_size') || c.includes('size_setting')
    })

    const showImageStyle = computed(() => {
        if (modelKind.value !== 'image') return false
        if (hasParamSchema.value) return activeParamCodes.value.has('style')
        const c = selectedCaps.value
        if (legacyImagePanel.value) return true
        return c.includes('image_style') || c.includes('style_setting')
    })

    const chatHasAnyControl = computed(
        () =>
            showChatTemperature.value ||
            showChatMaxTokens.value ||
            showChatTopP.value ||
            showChatTopK.value ||
            showChatSeed.value ||
            showChatStopSequences.value ||
            showChatFrequencyPenalty.value ||
            showChatPresencePenalty.value
    )

    const embeddingHasAnyControl = computed(() => showEmbeddingDimensions.value)
    const imageHasAnyControl = computed(() => showImageSize.value || showImageStyle.value)

    const paramSectionTitle = computed(() => {
        switch (modelKind.value) {
            case 'embedding':
                return '向量参数'
            case 'image':
                return '图像生成参数'
            default:
                return '对话推理参数'
        }
    })

    const capabilityHint = computed(() => {
        if (modelKind.value !== 'chat' || legacyChatFullPanel.value) return ''
        if (!chatHasAnyControl.value) {
            return '当前端点 capabilities 中未包含可调推理超参，请先在「模型管理」中为该端点勾选温度、Max Tokens 等推理能力。'
        }
        return ''
    })

    return {
        selectedCaps,
        selectedModelParams,
        hasParamSchema,
        activeParamCodes,
        unsupportedParamCodes,
        modelKind,
        showChatTemperature,
        showChatMaxTokens,
        showChatTopP,
        showChatTopK,
        showChatSeed,
        showChatStopSequences,
        showChatPenalties,
        showChatFrequencyPenalty,
        showChatPresencePenalty,
        showEmbeddingDimensions,
        showImageSize,
        showImageStyle,
        chatHasAnyControl,
        embeddingHasAnyControl,
        imageHasAnyControl,
        paramSectionTitle,
        capabilityHint,
    }
}
