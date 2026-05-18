/**
 * 聊天相关枚举 - 与后端 ChatStreamEnum 对齐（英文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const chatRoleLabels = {
    user: 'User',
    assistant: 'AI Assistant',
    system: 'System',
    thought: 'Thought'
} as const

export const astroChatRoleDictionary = createEnumDictionary({
    id: 'astro.chat.role',
    labels: chatRoleLabels,
    order: ['user', 'assistant', 'system', 'thought'],
    caseInsensitive: true
})

const chatEventTypeLabels = {
    text: 'Text',
    thought: 'Thought',
    tool: 'Tool',
    html: 'HTML Code',
    error: 'Error',
    image: 'Image',
    done: 'Done'
} as const

export const astroChatEventTypeDictionary = createEnumDictionary({
    id: 'astro.chat.eventType',
    labels: chatEventTypeLabels,
    order: ['text', 'thought', 'tool', 'html', 'error', 'image', 'done'],
    caseInsensitive: true
})
