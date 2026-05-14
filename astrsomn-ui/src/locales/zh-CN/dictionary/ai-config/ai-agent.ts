/**
 * 与后端 `AiAgentEnum` 各枚举的 code 一一对应（中文文案）。
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const statusLabels = {
    enabled: '启用',
    disabled: '禁用'
} as const

export const aiAgentStatusDictionary = createEnumDictionary({
    id: 'ai-agent.status',
    labels: statusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})

const memoryModeLabels = {
    shortTerm: '短期记忆',
    longTerm: '长期记忆',
    hybrid: '混合记忆'
} as const

export const aiAgentMemoryModeDictionary = createEnumDictionary({
    id: 'ai-agent.memoryMode',
    labels: memoryModeLabels,
    order: ['shortTerm', 'longTerm', 'hybrid']
})

const isDefaultLabels = {
    Y: '是',
    N: '否'
} as const

export const aiAgentIsDefaultDictionary = createEnumDictionary({
    id: 'ai-agent.isDefault',
    labels: isDefaultLabels,
    order: ['Y', 'N']
})
