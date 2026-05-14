import type {BuilderChatSnapshot} from './builderChatInjection'

export type BuilderStreamOptions = {
    userMessage: string
    memoryKey: string
    enableDeepThinking: boolean
    enableNetwork: boolean
}

/**
 * Maps builder UI state to {@code AstroBuilderChatRequest} JSON for {@code POST /v1/astro/chat/builder/stream}.
 */
export function buildAstroBuilderChatRequest(
    snap: BuilderChatSnapshot,
    opts: BuilderStreamOptions
): Record<string, unknown> {
    const ext = snap.currentModel?.extensionCode?.trim()
    if (!ext) {
        throw new Error('请先在左侧选择模型（需要 extensionCode）与对话实例')
    }

    const modelSetting: Record<string, unknown> = {
        extensionCode: ext,
        modelName: snap.currentModel?.modelName?.trim() || undefined,
        accountKey: snap.currentAccount?.accountKey?.trim() || snap.currentInstance?.accountKey?.trim() || undefined,
        apiUrl: snap.currentAccount?.apiUrl?.trim() || undefined,
        apiKey: snap.currentAccount?.apiKey?.trim() || undefined,
        apiSecret: snap.currentAccount?.apiSecret?.trim() || undefined
    }

    const inst = snap.currentInstance
    const chatSetting: Record<string, unknown> = {}
    if (inst?.temperature != null) chatSetting.temperature = Number(inst.temperature)
    if (inst?.topP != null) chatSetting.topP = Number(inst.topP)
    if (inst?.topK != null) chatSetting.topK = Number(inst.topK)
    if (inst?.maxTokens != null) chatSetting.maxTokens = Number(inst.maxTokens)
    if (inst?.seed != null) chatSetting.seed = Number(inst.seed)
    if (inst?.presencePenalty != null) chatSetting.presencePenalty = Number(inst.presencePenalty)
    if (inst?.frequencyPenalty != null) chatSetting.frequencyPenalty = Number(inst.frequencyPenalty)

    const promptSetting: Record<string, unknown> = {
        promptKey: snap.currentPrompt?.promptKey?.trim() || undefined
    }

    const toolKeys = snap.placedTools.map((t) => t.toolKey).filter((k): k is string => !!k?.trim())
    const mcpKeys = snap.placedMcps.map((m) => m.mcpKey).filter((k): k is string => !!k?.trim())
    const toolSetting: Record<string, unknown> = {
        toolKeys: toolKeys.length ? toolKeys : undefined,
        mcpKeys: mcpKeys.length ? mcpKeys : undefined,
        ragKeys: undefined
    }

    const kb = snap.knowledgeKeys.filter(Boolean)
    const ragSetting: Record<string, unknown> = {
        enabled: kb.length > 0,
        knowledgeKeys: kb.length ? kb : undefined,
        maxResults: 5,
        minScore: 0.35
    }

    const conversationSetting: Record<string, unknown> = {
        enableNetwork: opts.enableNetwork,
        enableDeepThinking: opts.enableDeepThinking,
        enableStream: true
    }

    return {
        userMessage: opts.userMessage,
        memoryKey: opts.memoryKey,
        modelSetting,
        chatSetting,
        promptSetting,
        conversationSetting,
        toolSetting,
        ragSetting,
        maxHistoryMessages: 10,
        enableHistorySave: false
    }
}
