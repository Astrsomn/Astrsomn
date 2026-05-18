/**
 * 聊天相关枚举 - 与后端 ChatStreamEnum 对齐（中文文案）
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const chatRoleLabels = {
    user: '用户',
    assistant: 'AI回答',
    system: '系统',
    thought: '思考'
} as const

export const astroChatRoleDictionary = createEnumDictionary({
    id: 'astro.chat.role',
    labels: chatRoleLabels,
    order: ['user', 'assistant', 'system', 'thought'],
    caseInsensitive: true
})

const chatEventTypeLabels = {
    text: '文本',
    thought: '思考',
    tool: '工具',
    html: 'HTML代码',
    error: '错误',
    image: '图像',
    done: '完成'
} as const

export const astroChatEventTypeDictionary = createEnumDictionary({
    id: 'astro.chat.eventType',
    labels: chatEventTypeLabels,
    order: ['text', 'thought', 'tool', 'html', 'error', 'image', 'done'],
    caseInsensitive: true
})
