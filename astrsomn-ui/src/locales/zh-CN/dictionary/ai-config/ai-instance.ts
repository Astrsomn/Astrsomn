/**
 * 与后端 `AiInstanceEnum` 各枚举的 code 一一对应（中文文案）。
 */
import {createEnumDictionary} from '@/locales/dictionary/core.ts'

const statusLabels = {
    enabled: '启用',
    disabled: '禁用'
} as const

export const aiInstanceStatusDictionary = createEnumDictionary({
    id: 'ai-instance.status',
    labels: statusLabels,
    order: ['enabled', 'disabled'],
    caseInsensitive: true
})
