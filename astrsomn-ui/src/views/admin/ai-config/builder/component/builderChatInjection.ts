import type {ComputedRef, InjectionKey, Ref} from 'vue'
import type {AiAccount} from '@/api/aiAccount'
import type {AiModel} from '@/api/aiModel'
import type {AiInstance} from '@/api/aiInstance'
import type {AiPrompt} from '@/api/aiPrompt'
import type {AiTool} from '@/api/aiTool'
import type {AiMcp} from '@/api/aiMcp'

export type BuilderChatSnapshot = {
    currentAccount?: AiAccount
    currentModel?: AiModel
    currentInstance?: AiInstance
    currentPrompt?: AiPrompt
    placedTools: AiTool[]
    placedMcps: AiMcp[]
    knowledgeKeys: string[]
}

export type BuilderChatContextValue = {
    snapshot: ComputedRef<BuilderChatSnapshot>
    memoryKey: Ref<string>
}

export const BUILDER_CHAT_CONTEXT: InjectionKey<BuilderChatContextValue> = Symbol('BUILDER_CHAT_CONTEXT')
