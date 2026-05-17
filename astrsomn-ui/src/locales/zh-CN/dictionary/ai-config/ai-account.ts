/**
 * 与后端 `AiAccountEnum` 各枚举的 code 一一对应（中文文案）。
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const statusLabels = {
    enabled: '启用',
    disabled: '禁用'
} as const

export const aiAccountStatusDictionary = createEnumDictionary({
    id: 'ai-account.status',
    labels: statusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})
